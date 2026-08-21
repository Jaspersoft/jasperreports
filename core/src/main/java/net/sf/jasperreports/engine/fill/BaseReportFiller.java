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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.BookmarkHelper;
import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.SimplePrintPart;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.FormatFactory;
import net.sf.jasperreports.engine.util.JRGraphEnvInitializer;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class BaseReportFiller implements ReportFiller
{
	private static final Log log = LogFactory.getLog(BaseReportFiller.class);
	
	protected JasperReportsContext jasperReportsContext;
	protected JRPropertiesUtil propertiesUtil;

	protected JRFillContext fillContext;
	
	protected FillerParent parent;
	
	protected final int fillerId;

	protected List<String> printTransferPropertyPrefixes;

	protected JasperReportSource reportSource;
	
	/**
	 * The report.
	 */
	protected JasperReport jasperReport;
	
	protected RepositoryContext repositoryContext;

	protected JRCalculator calculator;

	protected final JRFillObjectFactory factory;

	/**
	 * Main report dataset.
	 */
	protected JRFillDataset mainDataset;

	/**
	 * Map of datasets ({@link JRFillDataset JRFillDataset} objects} indexed by name.
	 */
	protected Map<String,JRFillDataset> datasetMap;

	protected DelayedFillActions delayedActions;

	protected JRAbstractScriptlet scriptlet;

	protected FormatFactory formatFactory;
	
	protected boolean ignorePagination;
	
	protected BookmarkHelper bookmarkHelper;
	
	protected JRVirtualizationContext virtualizationContext;
	
	protected JasperPrint jasperPrint;
	
	protected Thread fillingThread;
	private volatile boolean fillingThreadInterrupted;
	
	private boolean isInterrupted;
	private boolean threadInterrupted;

	protected FillListener fillListener;
	
	protected int usedPageWidth = 0;

	public BaseReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport, 
			FillerParent parent) throws JRException
	{
		this(jasperReportsContext, SimpleJasperReportSource.from(jasperReport), parent);
	}

	public BaseReportFiller(JasperReportsContext jasperReportsContext, JasperReportSource reportSource, 
			FillerParent parent) throws JRException
	{
		JRGraphEnvInitializer.initializeGraphEnv();
		
		setJasperReportsContext(jasperReportsContext);
		
		this.reportSource = reportSource;
		this.jasperReport = reportSource.getReport();
		this.repositoryContext = SimpleRepositoryContext.of(jasperReportsContext, reportSource.getRepositoryReportContext());
		jasperReportSet();
		
		this.parent = parent;

		DatasetExpressionEvaluator initEvaluator = null;
		if (parent == null)
		{
			fillContext = new JRFillContext(this);
			printTransferPropertyPrefixes = readPrintTransferPropertyPrefixes();
		}
		else
		{
			fillContext = parent.getFiller().fillContext;
			printTransferPropertyPrefixes = parent.getFiller().printTransferPropertyPrefixes;
			initEvaluator = parent.getCachedEvaluator();
		}
		
		this.fillerId = fillContext.generatedFillerId();
		if (log.isDebugEnabled())
		{
			log.debug("Fill " + fillerId + ": created for " + jasperReport.getName());
		}
		
		if (initEvaluator == null)
		{
			calculator = JRFillDataset.createCalculator(jasperReportsContext, jasperReport, jasperReport.getMainDataset());
		}
		else
		{
			calculator = new JRCalculator(initEvaluator);
		}
		
		jasperPrint = new JasperPrint();
		
		factory = initFillFactory();

		createDatasets();
		mainDataset = factory.getDataset(jasperReport.getMainDataset());
		
		if (parent == null)
		{
			FillDatasetPosition masterFillPosition = new FillDatasetPosition(null);
			mainDataset.setFillPosition(masterFillPosition);
		}

		delayedActions = new DelayedFillActions(this);
		if (log.isDebugEnabled())
		{
			log.debug("created delayed actions " + delayedActions.getId() + " for filler " + fillerId);
		}
	}
	
	protected abstract void jasperReportSet();
	
	private List<String> readPrintTransferPropertyPrefixes()
	{
		List<JRPropertiesUtil.PropertySuffix> transferProperties = propertiesUtil.getProperties(
				JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);
		List<String> prefixes = new ArrayList<>(transferProperties.size());
		for (JRPropertiesUtil.PropertySuffix property : transferProperties)
		{
			String transferPrefix = property.getValue();
			if (transferPrefix != null && transferPrefix.length() > 0)
			{
				prefixes.add(transferPrefix);
			}
		}
		return prefixes;
	}

	protected abstract JRFillObjectFactory initFillFactory();

	private void createDatasets() throws JRException
	{
		datasetMap = new HashMap<>();

		JRDataset[] datasets = jasperReport.getDatasets();
		if (datasets != null && datasets.length > 0)
		{
			for (int i = 0; i < datasets.length; i++)
			{
				JRFillDataset fillDataset = factory.getDataset(datasets[i]);
				fillDataset.createCalculator(jasperReport);

				datasetMap.put(datasets[i].getName(), fillDataset);
			}
		}
	}

	protected final void initDatasets() throws JRException
	{
		mainDataset.initElementDatasets(factory);
		initDatasets(factory);

		mainDataset.checkVariableCalculationReqs(factory);

		mainDataset.setCalculator(calculator);
		mainDataset.initCalculator();
	}

	private void initDatasets(JRFillObjectFactory factory)
	{
		for (Iterator<JRFillDataset> it = datasetMap.values().iterator(); it.hasNext();)
		{
			JRFillDataset dataset = it.next();
			dataset.inheritFromMain();
			dataset.initElementDatasets(factory);
		}
	}

	protected final void createBoundElementMaps(JREvaluationTime evaluationTime)
	{
		delayedActions.createDelayedEvaluationTime(evaluationTime);
	}
	
	/**
	 * Adds a fill lister to be notified by events that occur during the fill.
	 * 
	 * @param fillListener the listener to add
	 */
	@Override
	public void addFillListener(FillListener fillListener)
	{
		this.fillListener = CompositeFillListener.addListener(this.fillListener, fillListener);
	}

	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}
	
	public RepositoryContext getRepositoryContext()
	{
		return repositoryContext;
	}

	public JRPropertiesUtil getPropertiesUtil()
	{
		return propertiesUtil;
	}

	public JasperReportSource getReportSource()
	{
		return reportSource;
	}

	/**
	 * Returns the report.
	 *
	 * @return the report
	 */
	public JasperReport getJasperReport()
	{
		return jasperReport;
	}

	public JasperPrint getJasperPrint()
	{
		return jasperPrint;
	}

	protected void setJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.propertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
	}

	protected final void setParametersToContext(Map<String,Object> parameterValues)
	{
		@SuppressWarnings("deprecation")
		JasperReportsContext localContext = 
			net.sf.jasperreports.engine.util.LocalJasperReportsContext.getLocalContext(jasperReportsContext, parameterValues);
		if (localContext != jasperReportsContext)
		{
			setJasperReportsContext(localContext);
		}
	}

	protected void initVirtualizationContext(Map<String, Object> parameterValues)
	{
		if (isSubreport())
		{
			if (fillContext.isUsingVirtualizer())
			{
				virtualizationContext = parent.getChildVirtualizationContext(jasperReport, parameterValues);
			}
		}
		else
		{
			/* Virtualizer */
			JRVirtualizer virtualizer = (JRVirtualizer) parameterValues.get(JRParameter.REPORT_VIRTUALIZER);
			if (virtualizer == null)
			{
				return;
			}
			
			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": using virtualizer " + virtualizer);
			}

			fillContext.setUsingVirtualizer(true);
			
			virtualizationContext = fillContext.getVirtualizationContext();
			virtualizationContext.setVirtualizer(virtualizer);
			
			virtualizationContext.setVirtualPageSize(jasperReport, parameterValues);
			
			JRVirtualizationContext.register(virtualizationContext, jasperPrint);
		}
		
		if (virtualizationContext != null && log.isDebugEnabled())
		{
			log.debug("filler " + fillerId + " created virtualization context " + virtualizationContext);
		}
	}

	@Override
	@continuable
	public JasperPrint fill(Map<String,Object> parameterValues, Connection conn) throws JRException
	{
		if (parameterValues == null)
		{
			parameterValues = new HashMap<>();
		}

		setConnectionParameterValue(parameterValues, conn);

		return fill(parameterValues);
	}

	protected void setConnectionParameterValue(Map<String,Object> parameterValues, Connection conn)
	{
		mainDataset.setConnectionParameterValue(parameterValues, conn);
	}

	@Override
	@continuable
	public JasperPrint fill(Map<String,Object> parameterValues, JRDataSource ds) throws JRException
	{
		if (parameterValues == null)
		{
			parameterValues = new HashMap<>();
		}

		setDatasourceParameterValue(parameterValues, ds);

		return fill(parameterValues);
	}

	protected void setDatasourceParameterValue(Map<String,Object> parameterValues, JRDataSource ds)
	{
		mainDataset.setDatasourceParameterValue(parameterValues, ds);
	}
	
	protected void setParameters(Map<String,Object> parameterValues) throws JRException
	{
		initVirtualizationContext(parameterValues);

		setFormatFactory(parameterValues);

		setIgnorePagination(parameterValues);

		if (parent == null)
		{
			ReportContext reportContext = (ReportContext) parameterValues.get(JRParameter.REPORT_CONTEXT);
			fillContext.setReportContext(reportContext);
		}

		mainDataset.setParameterValues(parameterValues);

		ignorePagination = (Boolean)parameterValues.get(JRParameter.IS_IGNORE_PAGINATION);

		ignorePaginationSet(parameterValues);

		mainDataset.evaluateFieldProperties();
		
		mainDataset.initDatasource();

		this.scriptlet = mainDataset.delegateScriptlet;

		if (!isSubreport())
		{
			fillContext.setMasterFormatFactory(getFormatFactory());
			fillContext.setMasterLocale(getLocale());
			fillContext.setMasterTimeZone(getTimeZone());
			fillContext.init();
		}
	}
	
	protected void setBookmarkHelper()
	{
		boolean isCreateBookmarks = propertiesUtil.getBooleanProperty(mainDataset, 
				JasperPrint.PROPERTY_CREATE_BOOKMARKS, false);
		if (isCreateBookmarks)
		{
			boolean collapseMissingLevels = propertiesUtil.getBooleanProperty(mainDataset, 
				JasperPrint.PROPERTY_COLLAPSE_MISSING_BOOKMARK_LEVELS, false);
			bookmarkHelper = new BookmarkHelper(collapseMissingLevels);
		}
	}

	public void setIgnorePagination(Map<String,Object> parameterValues)
	{
		boolean ignore;
		if (parent == null)
		{
			ignore = getOwnIgnorePagination(parameterValues, false);
		}
		else
		{
			if (parent.isParentPagination())
			{
				// the parent drives pagination, not looking at parameter and flag
				ignore = parent.getFiller().ignorePagination;
			}
			else
			{
				// subreport parts are allowed to ignore pagination even if the parent does not have the flag
				// the report attribute overrides the parent only when set to true
				Boolean ownIgnorePagination = getOwnIgnorePagination(parameterValues, true);
				if (ownIgnorePagination != null)
				{
					ignore = ownIgnorePagination;
				}
				else
				{
					// default to the parent
					ignore = parent.getFiller().ignorePagination;
				}
			}
		}
		
		ignorePagination = ignore; // we can keep this here although most likely this field is not read until set again by ignorePaginationSet method
		parameterValues.put(JRParameter.IS_IGNORE_PAGINATION, ignorePagination);
	}
	
	protected Boolean getOwnIgnorePagination(Map<String,Object> parameterValues, boolean onlySetAttribute)
	{
		Boolean isIgnorePaginationParam = (Boolean) parameterValues.get(JRParameter.IS_IGNORE_PAGINATION);
		if (isIgnorePaginationParam != null)
		{
			return isIgnorePaginationParam;
		}
		
		boolean ignorePaginationAttribute = jasperReport.isIgnorePagination();
		if (ignorePaginationAttribute)
		{
			return ignorePaginationAttribute;
		}
		
		return onlySetAttribute ? null : false;
	}
	
	protected abstract void ignorePaginationSet(Map<String, Object> parameterValues);
	
	public boolean isIgnorePagination()
	{
		return ignorePagination;
	}
	
	protected boolean isInterrupted()
	{
		return (isInterrupted || threadInterrupted || (parent != null && parent.getFiller().isInterrupted()));
	}
	
	protected boolean isDeliberatelyInterrupted()
	{
		return (isInterrupted || (parent != null && parent.getFiller().isDeliberatelyInterrupted()));
	}

	protected void setInterrupted(boolean isInterrupted)
	{
		this.isInterrupted = isInterrupted;
	}

	protected void checkInterrupted()
	{
		if (Thread.interrupted())
		{
			threadInterrupted = true;
		}
		
		if (isInterrupted())
		{
			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": interrupting");
			}

			throw new JRFillInterruptedException();
		}
	}

	public abstract boolean isPageBreakInhibited();

	@Override
	public JRFillContext getFillContext()
	{
		return fillContext;
	}

	public JRVirtualizationContext getVirtualizationContext()
	{
		return virtualizationContext;
	}

	public JRFillDataset getMainDataset()
	{
		return mainDataset;
	}
	
	/**
	 * Returns the map of parameter values.
	 * 
	 * @return the map of parameter values
	 */
	public Map<String,Object> getParameterValuesMap()
	{
		return mainDataset.getParameterValuesMap();
	}

	/**
	 * Returns the report parameters indexed by name.
	 *
	 * @return the report parameters map
	 */
	protected Map<String,JRFillParameter> getParametersMap()
	{
		return mainDataset.parametersMap;
	}
	
	/**
	 * Returns the value of a parameter.
	 * 
	 * @param parameterName the parameter name
	 * @return the parameter value
	 */
	public Object getParameterValue(String parameterName)
	{
		return mainDataset.getParameterValue(parameterName);
	}

	/**
	 * Returns the report locale.
	 *
	 * @return the report locale
	 */
	public Locale getLocale()
	{
		return mainDataset.getLocale();
	}

	/**
	 * Returns the report time zone.
	 *
	 * @return the report time zone
	 */
	protected TimeZone getTimeZone()
	{
		return mainDataset.timeZone;
	}

	/**
	 * Adds a variable calculation request.
	 *
	 * @param variableName
	 *            the variable name
	 * @param calculation
	 *            the calculation type
	 */
	protected void addVariableCalculationReq(String variableName, CalculationEnum calculation)
	{
		mainDataset.addVariableCalculationReq(variableName, calculation);
	}

	/**
	 * Returns a report variable.
	 *
	 * @param variableName the variable name
	 * @return the variable
	 */
	public JRFillVariable getVariable(String variableName)
	{
		return mainDataset.getVariable(variableName);
	}

	/**
	 * Returns the value of a variable.
	 *
	 * @param variableName
	 *            the variable name
	 *
	 * @return the variable value
	 *
	 * @throws JRRuntimeException when the variable does not exist
	 */
	public Object getVariableValue(String variableName)
	{
		return mainDataset.getVariableValue(variableName);
	}

	protected JRFillExpressionEvaluator getExpressionEvaluator()
	{
		return calculator;
	}

	protected boolean isSubreport()
	{
		return parent != null;
	}

	public boolean isMasterReport()
	{
		return parent == null;
	}

	/**
	 * Evaluates an expression
	 * @param expression the expression
	 * @param evaluation the evaluation type
	 * @return the evaluation result
	 * @throws JRException
	 */
	public Object evaluateExpression(JRExpression expression, byte evaluation) throws JRException
	{
		return mainDataset.evaluateExpression(expression, evaluation);
	}

	protected final void setFormatFactory(Map<String,Object> parameterValues)
	{
		formatFactory = (FormatFactory)parameterValues.get(JRParameter.REPORT_FORMAT_FACTORY);
		if (formatFactory == null)
		{
			formatFactory = DefaultFormatFactory.createFormatFactory(jasperReport.getFormatFactoryClass());
			parameterValues.put(JRParameter.REPORT_FORMAT_FACTORY, formatFactory);
		}
	}

	/**
	 * Returns the report format factory.
	 *
	 * @return the report format factory
	 */
	protected FormatFactory getFormatFactory()
	{
		return formatFactory;
	}

	protected void addLastPageBookmarks()
	{
		if (bookmarkHelper != null)
		{
			int pageIndex = jasperPrint.getPages() == null ? -1 : (jasperPrint.getPages().size() - 1);
			if (pageIndex >= 0)
			{
				JRPrintPage page = jasperPrint.getPages().get(pageIndex);
				bookmarkHelper.addBookmarks(page, pageIndex);
			}
		}
	}

	public void updateBookmark(JRPrintElement element)
	{
		if (isMasterReport())
		{
			if (bookmarkHelper != null)
			{
				bookmarkHelper.updateBookmark(element);
			}
		}
		else
		{
			parent.updateBookmark(element);
		}
	}

	protected void detectPart()
	{
		//we have a flag for performance reasons
		if (!fillContext.toDetectParts())
		{
			return;
		}

		List<JRPrintPage> pages = jasperPrint.getPages();
		if (pages != null && !pages.isEmpty())
		{
			JRPrintPage page = pages.get(pages.size() - 1);
			PartPropertiesDetector detector = new PartPropertiesDetector(propertiesUtil, (partName, partProperties) ->
			{
				SimplePrintPart part = SimplePrintPart.fromJasperPrint(jasperPrint, partName, partProperties);
				jasperPrint.addPart(pages.size() - 1, part);
			});
			detector.detect(page.getElements());
		}
	}

	/**
	 * Cancels the fill process.
	 *
	 * @throws JRException
	 */
	@Override
	public void cancelFill() throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug("Fill " + fillerId + ": cancelling");
		}

		fillContext.markCanceled();
		
		if (fillContext.cancelRunningQuery())
		{
			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": query cancelled");
			}
		}
		else
		{
			Thread t = fillingThread;
			if (t != null)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Fill " + fillerId + ": Interrupting thread " + t);
				}

				fillingThreadInterrupted = true;
				t.interrupt();
			}
		}
	}

	protected void clearFillingThread()
	{
		if (fillingThreadInterrupted)
		{
			//we have previously interrupted the filling thread
			//checking and clearing the interrupted status of the thread to make sure it doesn't stay on
			boolean interrupted = Thread.interrupted();
			if (interrupted && log.isDebugEnabled())
			{
				log.debug("filling thread interrupted status was on");
			}
		}

		fillingThread = null;
	}

	protected void addBoundElement(JRFillElement element, JRPrintElement printElement, JREvaluationTime evaluationTime,
			FillPageKey pageKey)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Adding evaluation of " + printElement + " by " + element 
					+ " for evaluation " + evaluationTime);
		}
		
		delayedActions.addDelayedAction(element, printElement, evaluationTime, pageKey);
	}

	protected void resolveBoundElements(JREvaluationTime evaluationTime, byte evaluation) throws JRException
	{
		delayedActions.runActions(evaluationTime, evaluation);
	}
	
	protected void resolveMasterBoundElements() throws JRException
	{
		resolveBoundElements(JREvaluationTime.EVALUATION_TIME_MASTER, JRExpression.EVALUATION_DEFAULT);
	}
	
	public void recordUsedPageWidth(int width)
	{
		if (width > usedPageWidth)
		{
			usedPageWidth = width;
		}
	}
	
	public int getUsedPageWidth()
	{
		return usedPageWidth;
	}

}