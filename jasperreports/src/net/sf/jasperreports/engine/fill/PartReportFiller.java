/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.BookmarkHelper;
import net.sf.jasperreports.engine.BookmarkIterator;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.part.DelayedPrintPart;
import net.sf.jasperreports.engine.part.FillPart;
import net.sf.jasperreports.engine.part.FillPartPrintOutput;
import net.sf.jasperreports.engine.part.FillParts;
import net.sf.jasperreports.engine.part.FillPrintPart;
import net.sf.jasperreports.engine.part.FillPrintPartQueue;
import net.sf.jasperreports.engine.part.FillingPrintPart;
import net.sf.jasperreports.engine.part.FinalFillingPrintPart;
import net.sf.jasperreports.engine.part.GroupFillParts;
import net.sf.jasperreports.engine.part.PartEvaluationTime;
import net.sf.jasperreports.engine.part.PartPrintOutput;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.parts.PartFillerParent;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PartReportFiller extends BaseReportFiller
{
	private static final Log log = LogFactory.getLog(PartReportFiller.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_EVALUATION_GROUP_NOT_FOUND = "fill.part.filler.evaluation.group.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_EVALUATION_TIME_TYPE = "fill.part.filler.unknown.evaluation.time.type";
	public static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_SECTION_TYPE = "fill.part.filler.unsupported.section.type";
	
	private FillParts detailParts;
	private List<GroupFillParts> groupParts;
	private Map<String, GroupFillParts> groupPartsByName;
	
	private FillPrintPartQueue partQueue;
	
	private List<DelayedPrintPart> reportEvaluatedParts;
	
	public PartReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport) throws JRException
	{
		this(jasperReportsContext, SimpleJasperReportSource.from(jasperReport), null);
	}
	
	public PartReportFiller(JasperReportsContext jasperReportsContext, JasperReport jasperReport, 
			PartFillerParent parent) throws JRException
	{
		this(jasperReportsContext, SimpleJasperReportSource.from(jasperReport), parent);
	}
	
	public PartReportFiller(JasperReportsContext jasperReportsContext, JasperReportSource reportSource, 
			PartFillerParent parent) throws JRException
	{
		super(jasperReportsContext, reportSource, parent);
		
		detailParts = new FillParts(jasperReport.getDetailSection(), factory);
		
		JRGroup[] reportGroups = jasperReport.getGroups();
		if (reportGroups == null || reportGroups.length == 0)
		{
			groupParts = Collections.emptyList();
			groupPartsByName = Collections.emptyMap();
		}
		else
		{
			groupParts = new ArrayList<GroupFillParts>(reportGroups.length);
			groupPartsByName = new HashMap<String, GroupFillParts>();
			for (JRGroup reportGroup : reportGroups)
			{
				GroupFillParts groupFillParts = new GroupFillParts(reportGroup, factory);
				groupParts.add(groupFillParts);
				groupPartsByName.put(reportGroup.getName(), groupFillParts);
			}
		}
		
		initDatasets();
		
		reportEvaluatedParts = new ArrayList<DelayedPrintPart>();
		
		if (parent == null)
		{
			JasperPrintPartOutput jasperPrintOutput = new JasperPrintPartOutput();
			partQueue = new FillPrintPartQueue(jasperPrintOutput);
		}
		else
		{
			partQueue = parent.getFiller().partQueue;
		}
	}

	@Override
	protected void jasperReportSet()
	{
		if (jasperReport.getSectionType() != SectionTypeEnum.PART)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNSUPPORTED_SECTION_TYPE,
					new Object[]{jasperReport.getSectionType()});
		}
	}

	@Override
	protected JRFillObjectFactory initFillFactory()
	{
		return new JRFillObjectFactory(this);
	}

	@Override
	public JasperPrint fill(Map<String, Object> parameterValues) throws JRException
	{
		return fill(parameterValues, 0);
	}

	@Override
	public JasperPrint fill(Map<String, Object> parameterValues, int rowsToFill) throws JRException
	{
		//FIXMEBOOK copied from JRBaseFiller
		if (parameterValues == null)
		{
			parameterValues = new HashMap<String,Object>();
		}

		if (log.isDebugEnabled())
		{
			log.debug("Fill " + fillerId + ": filling report");
		}

		setParametersToContext(parameterValues);

		fillingThread = Thread.currentThread();
		
		JRResourcesFillUtil.ResourcesFillContext resourcesContext = 
			JRResourcesFillUtil.setResourcesFillContext(parameterValues);
		
		boolean success = false;
		try
		{
			createBoundElemementMaps();

/*			if (parent != null)
			{
				parent.registerSubfiller(this);
			}
*/
			setParameters(parameterValues);

			setBookmarkHelper();
			
			//loadStyles();

			jasperPrint.setName(jasperReport.getName());
			jasperPrint.setPageWidth(jasperReport.getPageWidth());
			jasperPrint.setPageHeight(jasperReport.getPageHeight());
			jasperPrint.setTopMargin(jasperReport.getTopMargin());
			jasperPrint.setLeftMargin(jasperReport.getLeftMargin());
			jasperPrint.setBottomMargin(jasperReport.getBottomMargin());
			jasperPrint.setRightMargin(jasperReport.getRightMargin());
			jasperPrint.setOrientation(jasperReport.getOrientationValue());

			jasperPrint.setFormatFactoryClass(jasperReport.getFormatFactoryClass());
			jasperPrint.setLocaleCode(JRDataUtils.getLocaleCode(getLocale()));
			jasperPrint.setTimeZoneId(JRDataUtils.getTimeZoneId(getTimeZone()));

			propertiesUtil.transferProperties(mainDataset, jasperPrint, 
				JasperPrint.PROPERTIES_PRINT_TRANSFER_PREFIX);

/*			jasperPrint.setDefaultStyle(defaultStyle);

			if (styles != null && styles.length > 0)
			{
				for (int i = 0; i < styles.length; i++)
				{
					addPrintStyle(styles[i]);
				}
			}
*/
			/*   */
			mainDataset.start();

			/*   */
			fillReport();
			
			if (bookmarkHelper != null)
			{
				jasperPrint.setBookmarks(bookmarkHelper.getRootBookmarks());
			}

			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": ended");
			}

			success = true;
			return jasperPrint;
		}
		finally
		{
			mainDataset.closeDatasource();
			mainDataset.disposeParameterContributors();
			
			if (success && parent == null)
			{
				// commit the cached data
				fillContext.cacheDone();
			}

/*			if (parent != null)
			{
				parent.unregisterSubfiller(this);
			}
*/			
			delayedActions.dispose();

			fillingThread = null;

			//kill the subreport filler threads
			//killSubfillerThreads();
			
			if (parent == null)
			{
				fillContext.dispose();
			}

			JRResourcesFillUtil.revertResourcesFillContext(resourcesContext);
		}
	}

	private void createBoundElemementMaps()
	{
		createBoundElementMaps(JREvaluationTime.EVALUATION_TIME_MASTER);
	}

	@Override
	protected void ignorePaginationSet(Map<String, Object> parameterValues)
	{
		//NOP
	}
	
	protected void fillReport() throws JRException
	{
		if (mainDataset.next())
		{
			startReport();
			
			fillFirstGroupHeaders();
			calculateDetail();
			fillDetail();

			while (mainDataset.next())
			{
				checkInterrupted();
				estimateGroups();
				fillChangedGroupFooters();
				fillChangedGroupEvaluatedParts();
				
				calculateGroups();
				fillChangedGroupHeaders();
				
				calculateDetail();
				fillDetail();
			}
			
			fillLastGroupFooters();
			fillLastGroupEvaluatedParts();
		}
		else
		{
			//no records
			//might not be required, but it's safer to do it because it used to happen before mainDataset.next() 
			startReport();
		}
		
		fillReportEvaluatedParts();
		assert partQueue.isCollapsed();
		
		if (isMasterReport())
		{
			resolveMasterBoundElements();
		}
	}

	protected void startReport() throws JRScriptletException, JRException
	{
		scriptlet.callBeforeReportInit();
		calculator.initializeVariables(ResetTypeEnum.REPORT, IncrementTypeEnum.REPORT);
		scriptlet.callAfterReportInit();
	}

	protected void calculateDetail() throws JRScriptletException, JRException
	{
		scriptlet.callBeforeDetailEval();
		calculator.calculateVariables(true);
		scriptlet.callAfterDetailEval();
	}

	protected void estimateGroups() throws JRException
	{
		calculator.estimateGroupRuptures();
	}
	
	protected void calculateGroups() throws JRException
	{
		scriptlet.callBeforeGroupInit();
		calculator.initializeVariables(ResetTypeEnum.GROUP, IncrementTypeEnum.GROUP);
		scriptlet.callAfterGroupInit();
	}

	protected void fillDetail() throws JRException
	{
		fillParts(detailParts, JRExpression.EVALUATION_DEFAULT);
	}

	protected void fillFirstGroupHeaders() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			fillParts(group.getHeaderParts(), JRExpression.EVALUATION_DEFAULT);
		}
	}

	protected void fillChangedGroupHeaders() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			if (group.hasChanged())
			{
				fillParts(group.getHeaderParts(), JRExpression.EVALUATION_DEFAULT);
			}
		}
	}

	private void fillChangedGroupFooters() throws JRException
	{
		for (ListIterator<GroupFillParts> iterator = groupParts.listIterator(groupParts.size()); iterator.hasPrevious();)
		{
			GroupFillParts group = iterator.previous();
			if (group.hasChanged())
			{
				fillParts(group.getFooterParts(), JRExpression.EVALUATION_OLD);
			}
		}
	}

	private void fillLastGroupFooters() throws JRException
	{
		for (ListIterator<GroupFillParts> iterator = groupParts.listIterator(groupParts.size()); iterator.hasPrevious();)
		{
			GroupFillParts group = iterator.previous();
			fillParts(group.getFooterParts(), JRExpression.EVALUATION_DEFAULT);
		}
	}

	protected void fillParts(FillParts parts, byte evaluation) throws JRException
	{
		for (FillPart part : parts.getParts())
		{
			checkInterrupted();
			fillPart(part, evaluation);
		}
	}

	protected void fillPart(FillPart part, byte evaluation) throws JRException
	{
		PartEvaluationTime evaluationTime = part.getEvaluationTime();
		switch (evaluationTime.getEvaluationTimeType())
		{
		case NOW:
		{
			PartPrintOutput appendOutput = partQueue.tail().getOutput();
			if (appendOutput != null)
			{
				// can write directly to the previous output
				part.fill(evaluation, appendOutput);
			}
			else
			{
				// previous part is delayed, creating a new part with local output
				FillPartPrintOutput localOutput = new FillPartPrintOutput(this);
				part.fill(evaluation, localOutput);
				
				// adding to the queue
				partQueue.appendOutput(localOutput);
			}
			break;
		}
		case REPORT:
		{
			DelayedPrintPart delayedPart = partQueue.appendDelayed(part);
			reportEvaluatedParts.add(delayedPart);
			break;
		}
		case GROUP:
		{
			GroupFillParts groupFillParts = groupPartsByName.get(evaluationTime.getEvaluationGroup());
			if (groupFillParts == null)//verified at compile time, checking twice nonetheless
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_EVALUATION_GROUP_NOT_FOUND,
						new Object[]{evaluationTime.getEvaluationGroup()});
			}
			
			DelayedPrintPart delayedPart = partQueue.appendDelayed(part);
			groupFillParts.addGroupEvaluatedPart(delayedPart);
			break;
		}
		default:
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNKNOWN_EVALUATION_TIME_TYPE,
					new Object[]{evaluationTime.getEvaluationTimeType()});
		}
	}

	@Override
	public boolean isPageFinal(int pageIndex)
	{
		if (isSubreport())
		{
			// shouldn't be called
			return false;
		}
		
		return ((JasperPrintPartOutput) partQueue.head().getOutput()).isPageFinal(pageIndex);
	}

	protected void partPageUpdated(int pageIndex)
	{
		if (fillListener != null)
		{
			fillListener.pageUpdated(jasperPrint, pageIndex);
		}
	}

	protected void fillReportEvaluatedParts() throws JRException
	{
		fillDelayedEvaluatedParts(reportEvaluatedParts, JRExpression.EVALUATION_DEFAULT);
	}

	protected void fillChangedGroupEvaluatedParts() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			if (group.hasChanged())
			{
				fillDelayedEvaluatedParts(group.getGroupEvaluatedParts(), JRExpression.EVALUATION_OLD);
			}
		}
	}

	protected void fillLastGroupEvaluatedParts() throws JRException
	{
		for (GroupFillParts group : groupParts)
		{
			fillDelayedEvaluatedParts(group.getGroupEvaluatedParts(), JRExpression.EVALUATION_DEFAULT);
		}
	}
	
	protected void fillDelayedEvaluatedParts(List<DelayedPrintPart> parts, byte evaluation) throws JRException
	{
		for (ListIterator<DelayedPrintPart> it = parts.listIterator(); it.hasNext();)
		{
			DelayedPrintPart part = it.next();
			it.remove();
			
			fillDelayedPart(evaluation, part);
		}
	}

	protected void fillDelayedPart(byte evaluation, DelayedPrintPart part) throws JRException
	{
		partQueue.fillDelayed(part, this, evaluation);
	}

	public BookmarkHelper getFirstBookmarkHelper()
	{
		for(FillPrintPart part = partQueue.head(); part != null; part = part.nextPart())
		{
			PartPrintOutput output = part.getOutput();
			if (output != null)
			{
				BookmarkHelper bookmarks = output.getBookmarkHelper();
				if (bookmarks.hasBookmarks())
				{
					return bookmarks;
				}
			}
		}
		return null;
	}
	
	protected class JasperPrintPartOutput implements PartPrintOutput
	{
		private final ReadWriteLock currentFillPartLock = new ReentrantReadWriteLock();
		private transient int currentPartStartIndex;
		private FillingPrintPart currentFillingPart;
		
		@Override
		public void startPart(PrintPart part, FillingPrintPart fillingPart)
		{
			int startIndex = jasperPrint.getPages().size();
			jasperPrint.addPart(startIndex, part);

			currentFillPartLock.writeLock().lock();
			try
			{
				currentPartStartIndex = startIndex;
				currentFillingPart = fillingPart;
			}
			finally
			{
				currentFillPartLock.writeLock().unlock();
			}
			
			if (log.isDebugEnabled())
			{
				log.debug("added part " + part.getName() + " at index " + startIndex);
			}
		}
		
		public boolean isPageFinal(int pageIndex)
		{
			currentFillPartLock.readLock().lock();
			try
			{
				JRPrintPage page = getPage(pageIndex);
				boolean hasMasterActions = delayedActions.hasDelayedActions(page);
				if (hasMasterActions)
				{
					return false;
				}
				
				boolean isFinal;
				if (pageIndex < currentPartStartIndex)
				{
					isFinal = true;
				}
				else
				{
					isFinal = currentFillingPart.isPageFinal(page);
				}
				return isFinal;
			}
			finally
			{
				currentFillPartLock.readLock().unlock();
			}
		}

		@Override
		public void addPage(JRPrintPage page, DelayedFillActions delayedActionsSource)
		{
			int pageIndex = jasperPrint.getPages().size();
			if (log.isDebugEnabled())
			{
				log.debug("adding part page at index " + pageIndex);
			}
			
			jasperPrint.addPage(page);
			addLastPageBookmarks();
			
			delayedActions.moveMasterEvaluations(delayedActionsSource, page, pageIndex);
			
			if (fillListener != null)
			{
				fillListener.pageGenerated(jasperPrint, pageIndex);
			}
		}
		
		@Override
		public JRPrintPage getPage(int pageIndex)
		{
			return jasperPrint.getPages().get(pageIndex);
		}

		@Override
		public void pageUpdated(int partPageIndex)
		{
			partPageUpdated(currentPartStartIndex + partPageIndex);
		}

		@Override
		public void append(FillPartPrintOutput output)
		{
			addStyles(output.getStyles());
			addOrigins(output.getOrigins());
			
			int pageOffset = jasperPrint.getPages().size();
			BookmarkHelper outputBookmarks = output.getBookmarkHelper();
			BookmarkIterator bookmarkIterator = null;
			if (bookmarkHelper != null && outputBookmarks != null)
			{
				bookmarkIterator = outputBookmarks.bookmarkIterator();
			}
			
			DelayedFillActions sourceActions = output.getDelayedActions();
			
			// adding in an organic order: for each part the pages that belong to the part,
			// and for each page the bookmarks that belong to the page
			ListIterator<JRPrintPage> pagesIterator = output.getPages().listIterator();
			int prevPartStart = 0;
			for (Map.Entry<Integer, PrintPart> partEntry : output.getParts().entrySet())
			{
				int partStart = partEntry.getKey();
				// add the pages that belong to the previous part
				for (int i = prevPartStart; i < partStart; i++)
				{
					JRPrintPage page = pagesIterator.next();
					addPage(page, pageOffset, sourceActions, bookmarkIterator);
				}
				prevPartStart = partStart;
				
				PrintPart part = partEntry.getValue();
				startPart(part, FinalFillingPrintPart.instance());
			}

			// add the pages that belong to the last part
			while (pagesIterator.hasNext())
			{
				JRPrintPage page = pagesIterator.next();
				addPage(page, pageOffset, sourceActions, bookmarkIterator);
			}
		}
		
		protected void addPage(JRPrintPage page, int pageOffset, 
				DelayedFillActions sourceActions, BookmarkIterator sourceBookmarkIterator)
		{
			int pageIndex = jasperPrint.getPages().size();
			if (log.isDebugEnabled())
			{
				log.debug("adding part page at index " + pageIndex);
			}
			
			jasperPrint.addPage(page);
			
			if (sourceBookmarkIterator != null)
			{
				int sourcePageIndex = pageIndex - pageOffset;
				while (sourceBookmarkIterator.hasBookmark() 
						&& sourceBookmarkIterator.bookmark().getPageIndex() == sourcePageIndex)
				{
					bookmarkHelper.addBookmark(sourceBookmarkIterator.bookmark(), pageOffset);
					sourceBookmarkIterator.next();
				}
			}
			
			delayedActions.moveMasterEvaluations(sourceActions, page, pageIndex);
			
			if (fillListener != null)
			{
				fillListener.pageGenerated(jasperPrint, pageIndex);
			}
		}

		@Override
		public BookmarkHelper getBookmarkHelper()
		{
			return bookmarkHelper;
		}

		@Override
		public void addStyles(Collection<JRStyle> stylesList)
		{
			for (JRStyle style : stylesList)
			{
				try
				{
					jasperPrint.addStyle(style, true);
				}
				catch (JRException e)
				{
					// should not happen
					throw new JRRuntimeException(e);
				}
			}
		}

		@Override
		public void addOrigins(Collection<JROrigin> origins)
		{
			for (JROrigin origin : origins)
			{
				jasperPrint.addOrigin(origin);
			}
		}
	}

}
