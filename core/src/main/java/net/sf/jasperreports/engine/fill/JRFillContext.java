/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.engine.fill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.data.cache.DataCacheHandler;
import net.sf.jasperreports.data.cache.DataRecorder;
import net.sf.jasperreports.data.cache.DataSnapshot;
import net.sf.jasperreports.engine.Deduplicable;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.fill.events.FillEvents;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.query.JRQueryExecuter;
import net.sf.jasperreports.engine.util.DeduplicableRegistry;
import net.sf.jasperreports.engine.util.FormatFactory;
import net.sf.jasperreports.engine.util.JRSingletonCache;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.engine.util.MarkupProcessor;
import net.sf.jasperreports.engine.util.MarkupProcessorFactory;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.repo.JasperDesignCache;

/**
 * Context class shared by all the fillers involved in a report (master and subfillers).
 * <p>
 * The context is created by the master filler and inherited by the subfillers.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see net.sf.jasperreports.engine.fill.JRBaseFiller
 */
public class JRFillContext
{
	private static final Log log = LogFactory.getLog(JRFillContext.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_MISSING_MARKUP_PROCESSOR_FACTORY = "fill.text.element.missing.markup.processor.factory";
	
	private static final JRSingletonCache<MarkupProcessorFactory> markupProcessorFactoryCache = 
			new JRSingletonCache<>(MarkupProcessorFactory.class);
	private final Map<String, MarkupProcessorFactory> markupProcessorFactories = new HashMap<>();

	private final BaseReportFiller masterFiller;
	
	private Map<Object,Renderable> loadedImageRenderers;
	private RenderersCache renderersCache;
	private Map<Object,JasperReportSource> loadedSubreports;
	private Map<Object,ReportTemplateSource> loadedTemplates;
	private DeduplicableRegistry deduplicableRegistry;
	private boolean usingVirtualizer;
	private JRPrintPage printPage;//FIXME used?
	private Map<JRQueryExecuter, Boolean> queryExecuters;
	
	private JasperReportsContext jasperReportsContext;
	private JRStyledTextUtil styledTextUtil;
	private ReportContext reportContext;
	private DataCacheHandler cacheHandler;
	private DataSnapshot dataSnapshot;
	private DataRecorder dataRecorder;
	private List<Pair<FillDatasetPosition, Object>> recordedData;

	private JRVirtualizationContext virtualizationContext;
	
	private FormatFactory masterFormatFactory;
	private Locale masterLocale;
	private TimeZone masterTimeZone;
	
	private volatile boolean canceled;
	
	private final AtomicInteger fillerIdSeq = new AtomicInteger();
	private final AtomicInteger fillElementSeq = new AtomicInteger();
	
	private Map<String, Object> fillCaches = new HashMap<>();
	
	private boolean detectParts;

	private FillEvents fillEvents;
	
	/**
	 * Constructs a fill context.
	 */
	public JRFillContext(BaseReportFiller masterFiller)
	{
		this.masterFiller = masterFiller;
		this.jasperReportsContext = masterFiller.getJasperReportsContext();
		this.styledTextUtil = JRStyledTextUtil.getInstance(jasperReportsContext);
		
		loadedImageRenderers = new HashMap<>();
		renderersCache = new RenderersCache(jasperReportsContext);
		loadedSubreports = new HashMap<>();
		loadedTemplates = new HashMap<>();
		deduplicableRegistry = new DeduplicableRegistry();
		
		FontUtil.getInstance(jasperReportsContext).resetThreadMissingFontsCache();
		
		this.queryExecuters = new HashMap<>();
	}

	public BaseReportFiller getMasterFiller()
	{
		return masterFiller;
	}
	
	protected JRStyledTextUtil getStyledTextUtil()
	{
		return styledTextUtil;
	}

	protected FontUtil getFontUtil()
	{
		return styledTextUtil.getFontUtil();
	}
	
	public void init()
	{
		fillEvents = new FillEvents(this);
	}

	public FillEvents getFillEvents()
	{
		return fillEvents;
	}

	/**
	 * Checks whether an image renderer given by source has already been loaded and cached.
	 * 
	 * @param source the source of the image renderer
	 * @return whether the image renderer has been cached
	 * @see #getLoadedRenderer(Object)
	 * @see #registerLoadedRenderer(Object, Renderable)
	 */
	public synchronized boolean hasLoadedRenderer(Object source)
	{
		return loadedImageRenderers.containsKey(source); 
	}
	
	
	/**
	 * Gets a cached image renderer.
	 * 
	 * @param source the source renderer of the image
	 * @return the cached image renderer
	 * @see #registerLoadedRenderer(Object, Renderable)
	 */
	public synchronized Renderable getLoadedRenderer(Object source)
	{
		return loadedImageRenderers.get(source); 
	}
	
	
	/**
	 * Registers an image renderer loaded from a source.
	 * <p>
	 * The image renderer is cached for further use.
	 * 
	 * @param source the source that was used to load the image renderer
	 * @param renderer the loaded image renderer
	 * @see #getLoadedRenderer(Object)
	 */
	public synchronized Renderable registerLoadedRenderer(Object source, Renderable renderer)
	{
		Renderable existingRenderer = loadedImageRenderers.get(source);
		if (existingRenderer != null)
		{
			return existingRenderer;
		}
		
		loadedImageRenderers.put(source, renderer);
		if (usingVirtualizer)
		{
			virtualizationContext.cacheRenderer(renderer);
		}
		return renderer;
	}

	
	/**
	 * 
	 */
	public RenderersCache getRenderersCache()
	{
		return renderersCache;
	}


	/**
	 * Checks whether a subreport given by source has already been loaded and cached.
	 * 
	 * @param source the source of the subreport
	 * @return whether the subreport has been cached
	 * @see #getLoadedSubreport(Object)
	 * @see #registerLoadedSubreport(Object, JasperReportSource)
	 */
	public synchronized boolean hasLoadedSubreport(Object source)
	{
		return loadedSubreports.containsKey(source); 
	}
	
	
	/**
	 * Gets a cached subreport.
	 * 
	 * @param source the source of the subreport
	 * @return the cached subreport
	 * @see #registerLoadedSubreport(Object, JasperReportSource)
	 */
	public synchronized JasperReportSource getLoadedSubreport(Object source)
	{
		return loadedSubreports.get(source); 
	}
	
	
	/**
	 * Registers a subreport loaded from a source.
	 * <p>
	 * The subreport is cached for further use.
	 * 
	 * @param source the source that was used to load the subreport
	 * @param subreport the loaded subreport
	 * @see #getLoadedSubreport(Object)
	 */
	public synchronized JasperReportSource registerLoadedSubreport(Object source, JasperReportSource subreport)
	{
		JasperReportSource existingSubreport = loadedSubreports.get(source);
		if (existingSubreport != null)
		{
			return existingSubreport;
		}
		
		loadedSubreports.put(source, subreport);
		return subreport;
	}

	
	/**
	 * Sets the flag indicating whether a virtualizer is used by the filling process.
	 * 
	 * @param usingVirtualizer whether virtualization is used
	 * @see #isUsingVirtualizer()
	 */
	public void setUsingVirtualizer(boolean usingVirtualizer)
	{
		this.usingVirtualizer = usingVirtualizer;
		if (usingVirtualizer && virtualizationContext == null)
		{
			virtualizationContext = new JRVirtualizationContext(jasperReportsContext);
		}
	}
	
	
	/**
	 * Decides whether virtualization is used by the filling process.
	 * 
	 * @return <code>true</code> if and only if a virtualizer is used
	 * @see #setUsingVirtualizer(boolean)
	 * @see net.sf.jasperreports.engine.JRParameter#REPORT_VIRTUALIZER
	 */
	public boolean isUsingVirtualizer()
	{
		return usingVirtualizer;
	}
	
	
	/**
	 * Sets the current master print page.
	 * 
	 * @param page the master print page
	 * @see #getPrintPage()
	 */
	public void setPrintPage(JRPrintPage page)
	{
		printPage  = page;
	}
	
	
	/**
	 * Returns the current master print page.
	 *  
	 * @return the current master print page
	 * @see #setPrintPage(JRPrintPage)
	 */
	public JRPrintPage getPrintPage()
	{
		return printPage;
	}
	
	
	/**
	 * Decides whether the filling should ignore pagination.
	 *  
	 * @return whether the filling should ignore pagination
	 * @see net.sf.jasperreports.engine.JRParameter#IS_IGNORE_PAGINATION
	 * @see JRBaseFiller#isIgnorePagination()
	 */
	public boolean isIgnorePagination()
	{
		return masterFiller.isIgnorePagination();
	}
	
	
	/**
	 * Sets the running query executer.
	 * <p>
	 * This method is called before firing the query.
	 * 
	 * @param queryExecuter the running query executer
	 */
	public synchronized void setRunningQueryExecuter(JRQueryExecuter queryExecuter)
	{
		this.queryExecuters.put(queryExecuter, null);
	}
	
	
	/**
	 * Clears the running query executer.
	 * <p>
	 * This method is called after the query has ended.
	 * @param queryExecuter the query executer
	 *
	 */
	public synchronized void clearRunningQueryExecuter(JRQueryExecuter queryExecuter)
	{
		this.queryExecuters.remove(queryExecuter);
	}
	
	
	/**
	 * Cancels the running query.
	 * 
	 * @return <code>true</code> if and only if there is a running query and it has been canceled.
	 * @throws JRException
	 */
	public synchronized boolean cancelRunningQuery() throws JRException
	{
		boolean canceled = false;
		for (JRQueryExecuter queryExecuter : queryExecuters.keySet())
		{
			boolean queryCanceled = queryExecuter.cancelQuery();
			canceled = canceled || queryCanceled;
		}
		return canceled;
	}
	
	
	/**
	 * Returns the virtualization context.
	 * 
	 * @return the virtualization context
	 */
	public JRVirtualizationContext getVirtualizationContext()
	{
		return virtualizationContext;
	}
	
	public void lockVirtualizationContext()
	{
		if (virtualizationContext != null)
		{
			virtualizationContext.lock();
		}
	}
	
	public void unlockVirtualizationContext()
	{
		if (virtualizationContext != null)
		{
			virtualizationContext.unlock();
		}
	}

	
	public FormatFactory getMasterFormatFactory()
	{
		return masterFormatFactory;
	}

	
	public void setMasterFormatFactory(FormatFactory masterFormatFactory)
	{
		this.masterFormatFactory = masterFormatFactory;
	}

	
	public Locale getMasterLocale()
	{
		return masterLocale;
	}

	
	public void setMasterLocale(Locale masterLocale)
	{
		this.masterLocale = masterLocale;
	}

	
	public TimeZone getMasterTimeZone()
	{
		return masterTimeZone;
	}

	
	public void setMasterTimeZone(TimeZone masterTimeZone)
	{
		this.masterTimeZone = masterTimeZone;
	}

	
	/**
	 * Checks whether a template given by source has already been loaded and cached.
	 * 
	 * @param source the source of the template
	 * @return whether the template has been cached
	 * @see #getLoadedTemplate(Object)
	 * @see #registerLoadedTemplate(Object, ReportTemplateSource)
	 */
	public synchronized boolean hasLoadedTemplate(Object source)
	{
		return loadedTemplates.containsKey(source); 
	}
	
	
	/**
	 * Gets a cached template.
	 * 
	 * @param source the source of the template
	 * @return the cached template
	 * @see #registerLoadedTemplate(Object, ReportTemplateSource)
	 */
	public synchronized ReportTemplateSource getLoadedTemplate(Object source)
	{
		return loadedTemplates.get(source); 
	}
	
	
	/**
	 * Registers a template loaded from a source.
	 * <p>
	 * The template is cached for further use.
	 * 
	 * @param source the source that was used to load the template
	 * @param templateSource the loaded template
	 * @see #getLoadedTemplate(Object)
	 */
	public synchronized ReportTemplateSource registerLoadedTemplate(Object source, ReportTemplateSource templateSource)
	{
		ReportTemplateSource existingTemplate = loadedTemplates.get(source);
		if (existingTemplate != null)
		{
			return existingTemplate;
		}
		
		loadedTemplates.put(source, templateSource);
		return templateSource;
	}
	

	/**
	 * 
	 */
	protected MarkupProcessorFactory getMarkupProcessorFactory(String markup)
	{
		MarkupProcessorFactory factory = markupProcessorFactories.get(markup);
		
		if (factory == null)
		{
			String factoryClass = masterFiller.getPropertiesUtil().getProperty(MarkupProcessorFactory.PROPERTY_MARKUP_PROCESSOR_FACTORY_PREFIX + markup);
			if (factoryClass == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_MISSING_MARKUP_PROCESSOR_FACTORY,  
						new Object[]{markup} 
						);
			}

			try
			{
				factory = markupProcessorFactoryCache.getCachedInstance(factoryClass);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			
			markupProcessorFactories.put(markup, factory);
		}
		return factory;
	}

	protected synchronized MarkupProcessor getMarkupProcessor(String markup)
	{
		MarkupProcessorFactory factory = getMarkupProcessorFactory(markup);
		MarkupProcessor markupProcessor = factory.createMarkupProcessor();
		return markupProcessor;
	}

	
	/**
	 * Search for a duplicate of a given object in the fill context, and add the object
	 * to the context if no duplicate found.
	 * 
	 * @param object the object to be searched or added
	 * @return a duplicate of the object if found, or the passed object if not
	 */
	public synchronized <T extends Deduplicable> T deduplicate(T object)
	{
		return deduplicableRegistry.deduplicate(object);
	}

	/**
	 * Generates a fresh fill element Id.
	 * 
	 * This method is called once by each fill element, and the returned Id is used
	 * for the generated print elements.
	 * 
	 * @return a generated Id for a fill element
	 * @see JRPrintElement#getSourceElementId()
	 */
	public int generateFillElementId() 
	{
		return fillElementSeq.incrementAndGet();
	}
	
	protected int generatedFillerId()
	{
		return fillerIdSeq.incrementAndGet();
	}

	public ReportContext getReportContext()
	{
		return reportContext;
	}

	public void setReportContext(ReportContext reportContext)
	{
		this.reportContext = reportContext;
		
		this.cacheHandler = (DataCacheHandler) getContextParameterValue(
				DataCacheHandler.PARAMETER_DATA_CACHE_HANDLER);
		if (cacheHandler != null)
		{
			if (cacheHandler.isSnapshotPopulated())
			{
				dataSnapshot = cacheHandler.getDataSnapshot();
			}
			else if (cacheHandler.isRecordingEnabled())
			{
				dataRecorder = cacheHandler.createDataRecorder();
				recordedData = new ArrayList<>();
			}
		}
	}

	protected Object getContextParameterValue(String parameterName)
	{
		if (reportContext == null)
		{
			return null;
		}

		Object value = reportContext.getParameterValue(parameterName);
		return value;
	}

	public DataCacheHandler getCacheHandler()
	{
		return cacheHandler;
	}

	public DataSnapshot getDataSnapshot()
	{
		return dataSnapshot;
	}

	public boolean hasDataSnapshot()
	{
		return dataSnapshot != null;
	}

	public DataRecorder getDataRecorder()
	{
		return dataRecorder;
	}

	public synchronized void addDataRecordResult(FillDatasetPosition fillPosition, Object recorded)
	{
		recordedData.add(new Pair<>(fillPosition, recorded));
	}
	
	public void cacheDone()
	{
		if (dataRecorder != null && dataRecorder.isEnabled())
		{
			// add all recorded data
			for (Pair<FillDatasetPosition, Object> recorededItem : recordedData)
			{
				dataRecorder.addRecordResult(recorededItem.first(), recorededItem.second());
			}
			
			dataRecorder.setSnapshotPopulated();
		}
	}
	
	public void markCanceled()
	{
		canceled = true;
	}
	
	public boolean isCanceled()
	{
		return canceled;
	}
	
	public synchronized <T> T getFillCache(String key, Supplier<T> cacheCreator)
	{
		@SuppressWarnings("unchecked")
		T cachedValue = (T) fillCaches.computeIfAbsent(key, k -> cacheCreator.get());
		return cachedValue;
	}

	public void dispose()
	{
		for (Object cacheObject : fillCaches.values())
		{
			if (cacheObject instanceof FillCacheDisposable)
			{
				((FillCacheDisposable) cacheObject).dispose();
			}
		}
	}
	
	public static interface FillCacheDisposable
	{
		void dispose();
	}

	public boolean isCollectingBookmarks()
	{
		return getMasterFiller().bookmarkHelper != null;
	}
	
	public void registerReportStyles(JasperReport jasperReport, UUID id, List<JRStyle> styles)
	{
		JasperDesignCache designCache = JasperDesignCache.getExistingInstance(reportContext);
		if (designCache != null)
		{
			String reportURI = designCache.locateReport(jasperReport);
			if (reportURI == null)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Did not find report " + jasperReport.getName() + " " + jasperReport.getUUID());
				}
				return;
			}
			
			designCache.setStyles(reportURI, id, styles);
		}
	}

	public void registerReportStyles(String reportLocation, UUID id, List<JRStyle> styles)
	{
		JasperDesignCache designCache = JasperDesignCache.getExistingInstance(reportContext);
		if (designCache != null)
		{
			designCache.setStyles(reportLocation, id, styles);
		}
	}

	public boolean toDetectParts()
	{
		return detectParts;
	}

	public void setDetectParts(boolean detectParts)
	{
		this.detectParts = detectParts;
	}
}
