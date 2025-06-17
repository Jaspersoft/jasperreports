/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
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

import org.apache.commons.javaflow.api.continuable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.FooterPositionEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRVerticalFiller extends JRBaseFiller
{
	
	private static final Log log = LogFactory.getLog(JRVerticalFiller.class);
	
	protected boolean hasDetailOnPage;
	

	/**
	 *
	 */
	protected JRVerticalFiller(
		JasperReportsContext jasperReportsContext, 
		JasperReport jasperReport
		) throws JRException
	{
		this(jasperReportsContext, jasperReport, null);
	}

	/**
	 *
	 */
	public JRVerticalFiller(
		JasperReportsContext jasperReportsContext,
		JasperReport jasperReport, 
		BandReportFillerParent parent 
		) throws JRException
	{
		this(jasperReportsContext, SimpleJasperReportSource.from(jasperReport), parent);
	}

	public JRVerticalFiller(
		JasperReportsContext jasperReportsContext,
		JasperReportSource reportSource, 
		BandReportFillerParent parent 
		) throws JRException
	{
		super(jasperReportsContext, reportSource, parent);

		setPageHeight(pageHeight);
	}
	
	
	public boolean hasDetailOnPage()
	{
		return hasDetailOnPage;
	}


	@Override
	protected void setPageHeight(int pageHeight)
	{
		this.pageHeight = pageHeight;

		columnFooterOffsetY = pageHeight - bottomMargin - pageFooter.getHeight() - columnFooter.getHeight();
		lastPageColumnFooterOffsetY = pageHeight - bottomMargin - lastPageFooter.getHeight() - columnFooter.getHeight();
		
		if (log.isDebugEnabled())
		{
			log.debug("Filler " + fillerId + " - pageHeight: " + pageHeight
					+ ", columnFooterOffsetY: " + columnFooterOffsetY
					+ ", lastPageColumnFooterOffsetY: " + lastPageColumnFooterOffsetY);
		}
	}


	@Override
	@continuable
	protected synchronized void fillReport() throws JRException
	{
		setLastPageFooter(false);

		if (next())
		{
			fillReportStart();

			while (next())
			{
				fillReportContent();
			}

			fillReportEnd();
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("Fill " + fillerId + ": no data");
			}

			switch (getWhenNoDataType())
			{
				case ALL_SECTIONS_NO_DETAIL :
				{
					if (log.isDebugEnabled())
					{
						log.debug("Fill " + fillerId + ": all sections");
					}

					scriptlet.callBeforeReportInit();
					calculator.initializeVariables(ResetTypeEnum.REPORT, IncrementTypeEnum.REPORT);
					scriptlet.callAfterReportInit();

					printPage = newPage();
					printPageContentsWidth = 0;
					addPage(printPage);
					setFirstColumn();
					offsetY = topMargin;
					isFirstPageBand = true;
					isFirstColumnBand = true;
					isCrtRecordOnPage = false;
					isCrtRecordOnColumn = false;

					fillBackground();

					fillTitle();

					fillPageHeader(JRExpression.EVALUATION_DEFAULT);

					fillColumnHeader(JRExpression.EVALUATION_DEFAULT);

					fillGroupHeaders(true);

					fillGroupFooters(true);

					fillSummary();

					break;
				}
				case BLANK_PAGE :
				{
					if (log.isDebugEnabled())
					{
						log.debug("Fill " + fillerId + ": blank page");
					}

					printPage = newPage();
					addPage(printPage);
					break;
				}
				case NO_DATA_SECTION:
				{
					if (log.isDebugEnabled())
					{
						log.debug("Fill " + fillerId + ": all sections");
					}

					scriptlet.callBeforeReportInit();
					calculator.initializeVariables(ResetTypeEnum.REPORT, IncrementTypeEnum.REPORT);
					scriptlet.callAfterReportInit();

					printPage = newPage();
					addPage(printPage);
					setFirstColumn();
					offsetY = topMargin;
					isFirstPageBand = true;
					isFirstColumnBand = true;
					isCrtRecordOnPage = false;
					isCrtRecordOnColumn = false;

					fillBackground();

					fillNoData();
					
					break;

				}
				case NO_PAGES :
				default :
				{
					if (log.isDebugEnabled())
					{
						log.debug("Fill " + fillerId + ": no pages");
					}
				}
			}
		}

		recordUsedPageHeight(offsetY + bottomMargin);
		if (ignorePagination)
		{
			jasperPrint.setPageHeight(usedPageHeight);
		}

		if (isSubreport())
		{
			addPageToParent(true);
		}
		else
		{
			addLastPageBookmarks();
			detectPart();
		}
		
		if (bookmarkHelper != null)
		{
			jasperPrint.setBookmarks(bookmarkHelper.getRootBookmarks());
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillReportStart() throws JRException
	{
		scriptlet.callBeforeReportInit();
		calculator.initializeVariables(ResetTypeEnum.REPORT, IncrementTypeEnum.REPORT);
		scriptlet.callAfterReportInit();

		printPage = newPage();
		printPageContentsWidth = 0;
		addPage(printPage);
		setFirstColumn();
		offsetY = topMargin;
		isFirstPageBand = true;
		isFirstColumnBand = true;
		isCrtRecordOnPage = false;
		isCrtRecordOnColumn = false;

		fillBackground();

		fillTitle();

		fillPageHeader(JRExpression.EVALUATION_DEFAULT);

		fillColumnHeader(JRExpression.EVALUATION_DEFAULT);

		fillGroupHeaders(true);

		fillDetail();
	}


	/**
	 *
	 */
	@continuable
	private void fillReportContent() throws JRException
	{
		calculator.estimateGroupRuptures();

		fillGroupFooters(false);

		resolveGroupBoundElements(JRExpression.EVALUATION_OLD, false);
		scriptlet.callBeforeGroupInit();
		calculator.initializeVariables(ResetTypeEnum.GROUP, IncrementTypeEnum.GROUP);
		scriptlet.callAfterGroupInit();

		fillGroupHeaders(false);

		fillDetail();
	}


	/**
	 *
	 */
	@continuable
	private void fillReportEnd() throws JRException
	{
		fillGroupFooters(true);

		fillSummary();
	}


	/**
	 *
	 */
	@continuable
	 private void fillTitle() throws JRException
	 {
		if (log.isDebugEnabled() && !title.isEmpty())
		{
			log.debug("Fill " + fillerId + ": title at " + offsetY);
		}

		title.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

		if (title.isToPrint())
		{
			while (
				title.getBreakHeight() > pageHeight - bottomMargin - offsetY
				)
			{
				addPage(false);
			}

			title.evaluate(JRExpression.EVALUATION_DEFAULT);

			JRPrintBand printBand = title.fill(pageHeight - bottomMargin - offsetY);

			if (title.willOverflow() && title.isSplitPrevented() && !title.isSplitTypePreventInhibited())
			{
				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, false);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);

				printBand = 
					title.refill(
						JRExpression.EVALUATION_DEFAULT, 
						pageHeight - bottomMargin - offsetY
						);
			}

			fillBand(printBand);
			offsetY += printBand.getHeight();
			isCrtRecordOnPage = true;
			isCrtRecordOnColumn = true;

			while (title.willOverflow())
			{
				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, false);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);

				printBand = title.fill(pageHeight - bottomMargin - offsetY);

				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;
			}

			resolveBandBoundElements(title, JRExpression.EVALUATION_DEFAULT);

			if (isTitleNewPage)
			{
				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, false);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);
			}
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillPageHeader(byte evaluation) throws JRException
	{
		if (log.isDebugEnabled() && !pageHeader.isEmpty())
		{
			log.debug("Fill " + fillerId + ": page header at " + offsetY);
		}

		setNewPageColumnInBands();

		pageHeader.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

		if (pageHeader.isToPrint())
		{
			int reattempts = getMasterColumnCount();
			if (isCreatingNewPage)
			{
				--reattempts;
			}

			boolean filled = fillBandNoOverflow(pageHeader, evaluation);

			for (int i = 0; !filled && i < reattempts; ++i)
			{
				resolveGroupBoundElements(evaluation, false);
				resolveColumnBoundElements(evaluation);
				resolvePageBoundElements(evaluation);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);

				filled = fillBandNoOverflow(pageHeader, evaluation);
			}

			if (!filled)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_PAGE_HEADER_OVERFLOW_INFINITE_LOOP,
						(Object[])null);
			}
		}

		columnHeaderOffsetY = offsetY;

		hasDetailOnPage = false;
		
		isNewPage = true;
	}


	private boolean fillBandNoOverflow(JRFillBand band, byte evaluation) throws JRException
	{
		int availableHeight = columnFooterOffsetY - offsetY;
		boolean overflow = availableHeight < band.getHeight();

		if (!overflow)
		{
			band.evaluate(evaluation);
			JRPrintBand printBand = band.fill(availableHeight);

			overflow = band.willOverflow();
			if (overflow)
			{
				band.rewind();
			}
			else
			{
				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = evaluation == JRExpression.EVALUATION_DEFAULT;
				isCrtRecordOnColumn = isCrtRecordOnPage;

				resolveBandBoundElements(band, evaluation);
			}
		}

		return !overflow;
	}


	/**
	 *
	 */
	@continuable
	private void fillColumnHeader(byte evaluation) throws JRException
	{
		if (log.isDebugEnabled() && !columnHeader.isEmpty())
		{
			log.debug("Fill " + fillerId + ": column header at " + offsetY);
		}

		setNewPageColumnInBands();
		isFirstColumnBand = true;

		columnHeader.evaluatePrintWhenExpression(evaluation);

		if (columnHeader.isToPrint())
		{
			int reattempts = getMasterColumnCount();
			if (isCreatingNewPage)
			{
				--reattempts;
			}

			setOffsetX();

			boolean filled = fillBandNoOverflow(columnHeader, evaluation);

			for (int i = 0; !filled && i < reattempts; ++i)
			{
				while (columnIndex < columnCount - 1)
				{
					resolveGroupBoundElements(evaluation, false);
					resolveColumnBoundElements(evaluation);
					scriptlet.callBeforeColumnInit();
					calculator.initializeVariables(ResetTypeEnum.COLUMN, IncrementTypeEnum.COLUMN);
					scriptlet.callAfterColumnInit();

					columnIndex += 1;
					setOffsetX();
					offsetY = columnHeaderOffsetY;
					isCrtRecordOnColumn = false;

					setColumnNumberVar();
				}

				fillPageFooter(evaluation);

				resolveGroupBoundElements(evaluation, false);
				resolveColumnBoundElements(evaluation);
				resolvePageBoundElements(evaluation);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);

				fillPageHeader(evaluation);

				filled = fillBandNoOverflow(columnHeader, evaluation);
			}

			if (!filled)
			{
				throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_COLUMN_HEADER_OVERFLOW_INFINITE_LOOP, (Object[]) null);
			}
		}

		isNewColumn = true;
	}


	/**
	 *
	 */
	@continuable
	private void fillGroupHeaders(boolean isFillAll) throws JRException
	{
		if (groups != null && groups.length > 0)
		{
			for (int i = 0; i < groups.length; i++)
			{
				JRFillGroup group = groups[i];

				if (isFillAll || group.hasChanged())
				{
					fillGroupHeader(group);
				}
			}
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillGroupHeader(JRFillGroup group) throws JRException
	{
		JRFillSection groupHeaderSection = (JRFillSection)group.getGroupHeaderSection();

		if (log.isDebugEnabled() && !groupHeaderSection.isEmpty())
		{
			log.debug("Fill " + fillerId + ": " + group.getName() + " header at " + offsetY);
		}

		//byte evalPrevPage = (group.isTopLevelChange()?JRExpression.EVALUATION_OLD:JRExpression.EVALUATION_DEFAULT);

		if ( (group.isStartNewPage() || group.isResetPageNumber()) && !isNewPage )
		{
			fillPageBreak(
				group.isResetPageNumber(),
				isCrtRecordOnPage ? JRExpression.EVALUATION_DEFAULT : JRExpression.EVALUATION_OLD, //evalPrevPage,
				JRExpression.EVALUATION_DEFAULT,
				true
				);
		}
		else if ( group.isStartNewColumn() && !isNewColumn )
		{
			fillColumnBreak(
				isCrtRecordOnColumn ? JRExpression.EVALUATION_DEFAULT : JRExpression.EVALUATION_OLD, //evalPrevPage,
				JRExpression.EVALUATION_DEFAULT
				);
		}

		boolean isFirstHeaderBandToPrint = true;
		boolean isGroupReset = false;
		
		JRFillBand[] groupHeaderBands = groupHeaderSection.getFillBands();
		for (int i = 0; i < groupHeaderBands.length; i++)
		{
			JRFillBand groupHeaderBand = groupHeaderBands[i];

			groupHeaderBand.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (groupHeaderBand.isToPrint())
			{
				while (
					groupHeaderBand.getBreakHeight() > columnFooterOffsetY - offsetY ||
					(isFirstHeaderBandToPrint && group.getMinHeightToStartNewPage() > columnFooterOffsetY - offsetY) 
					)
				{
					fillColumnBreak(
						isCrtRecordOnColumn ? JRExpression.EVALUATION_DEFAULT : JRExpression.EVALUATION_OLD, //evalPrevPage,
						JRExpression.EVALUATION_DEFAULT
						);
				}
			}

			if (!isGroupReset && (isFirstHeaderBandToPrint || i == groupHeaderBands.length - 1))
			{
				// perform this group reset before the first header band prints, 
				// or before the last header band, regardless if it prints or not 
				setNewGroupInBands(group);

				group.setFooterPrinted(false);
				group.resetDetailsCount();
				
				isGroupReset = true;
			}

			ElementRange elementRange = null;
			
			if (
				(group.isKeepTogether() && !isNewColumn)
				|| group.getMinDetailsToStartFromTop() > 0
				)
			{
				elementRange = group.getKeepTogetherElementRange();
				
				if (elementRange == null)
				{
					// we need to set a keep together element range for the group
					// even if its header does not print,
					// but only if the column is not already new
					elementRange = new SimpleElementRange(getCurrentPage(), columnIndex, offsetY);
					
					group.setKeepTogetherElementRange(elementRange);
					// setting a non-null element range here would cause the group header band to be
					// refilled below and thus kept together, in case a split occurs in it;
					// the non-null element range will be also moved onto the new page/column in the process,
					// but it will contain no elements as the already mentioned non-splitting behavior of the group header band
					// would not add any element to it;
					// so the keep together element range set here is more like flag to signal the group header itself
					// should be prevented from splitting in the fillColumnBand call below
				}
			}

			if (groupHeaderBand.isToPrint())
			{
				fillColumnBand(groupHeaderBand, JRExpression.EVALUATION_DEFAULT, false);
				
				ElementRange newElementRange = new SimpleElementRange(getCurrentPage(), columnIndex, offsetY);
					
				// in case a column/page break occurred during the filling of the band above,
				// the provided element range is discarded/ignored,
				// but that should not be a problem because the discarded element range was already dealt with during the break, 
				// because it was a keep together element range
				ElementRangeUtil.expandOrIgnore(elementRange, newElementRange);

				isFirstPageBand = false;
				isFirstColumnBand = false;
				
				isFirstHeaderBandToPrint = false;
			}
		}

		group.setHeaderPrinted(true);
	}


	/**
	 *
	 */
	@continuable
	private void fillGroupHeadersReprint(byte evaluation, boolean isPageBreak) throws JRException
	{
		if (groups != null && groups.length > 0)
		{
			for (int i = 0; i < groups.length; i++)
			{
				JRFillGroup group = groups[i];
				
				if (
					group.getKeepTogetherElementRange() != null
					&& (group.isKeepTogether() || !group.hasMinDetails())
					)
				{
					// we reprint headers only for groups that are "outer" to the one which 
					// triggered a potential "keep together" move 
					break;
				}

				if (
					(group.isReprintHeaderOnEachColumn() || (group.isReprintHeaderOnEachPage() && isPageBreak)) 
					&& (!group.hasChanged() || (group.hasChanged() && group.isHeaderPrinted()))
					)
				{
					fillGroupHeaderReprint(groups[i], evaluation);
				}
			}
		}
	}


	/**
	 *
	 */
	@continuable
	 private void fillGroupHeaderReprint(JRFillGroup group, byte evaluation) throws JRException
	 {
		JRFillSection groupHeaderSection = (JRFillSection)group.getGroupHeaderSection();

		JRFillBand[] groupHeaderBands = groupHeaderSection.getFillBands();
		for (int i = 0; i < groupHeaderBands.length; i++)
		{
			JRFillBand groupHeaderBand = groupHeaderBands[i];

			groupHeaderBand.evaluatePrintWhenExpression(evaluation);

			if (groupHeaderBand.isToPrint())
			{
				while (groupHeaderBand.getBreakHeight() > columnFooterOffsetY - offsetY)
				{
					fillColumnBreak(evaluation, evaluation); // using same evaluation for both side of the break is ok here
				}

				fillColumnBand(groupHeaderBand, evaluation, false);

				//isFirstPageBand = false;
				if (columnCount > 1)
				{
					isFirstColumnBand = false;
				}
			}
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillDetail() throws JRException
	{
		if (log.isDebugEnabled() && !detailSection.isEmpty())
		{
			log.debug("Fill " + fillerId + ": detail at " + offsetY);
		}

		if (!detailSection.areAllPrintWhenExpressionsNull())
		{
			calculator.estimateVariables();
		}

		JRFillBand[] detailBands = detailSection.getFillBands();
		for (int i = 0; i < detailBands.length; i++)
		{
			JRFillBand detailBand = detailBands[i];
			
			detailBand.evaluatePrintWhenExpression(JRExpression.EVALUATION_ESTIMATED);

			if (detailBand.isToPrint())
			{
				while (
					detailBand.getBreakHeight() > columnFooterOffsetY - offsetY
					)
				{
					fillColumnBreak(
						isCrtRecordOnColumn ? JRExpression.EVALUATION_DEFAULT : JRExpression.EVALUATION_OLD,
						JRExpression.EVALUATION_DEFAULT
						);
				}
				
				break;
			}
		}

		scriptlet.callBeforeDetailEval();
		calculator.calculateVariables(true);
		scriptlet.callAfterDetailEval();

		detailElementRange = null;

		boolean keepDetailElementRangeForOrphanFooter = true;
		boolean atLeastOneDetailBandPrinted = false;
		
		for (int i = 0; i < detailBands.length; i++)
		{
			JRFillBand detailBand = detailBands[i];
					
			detailBand.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (detailBand.isToPrint())
			{
				while (
					detailBand.getBreakHeight() > columnFooterOffsetY - offsetY
					)
				{
					fillColumnBreak(
						isCrtRecordOnColumn ? JRExpression.EVALUATION_DEFAULT : JRExpression.EVALUATION_OLD,
						JRExpression.EVALUATION_DEFAULT
						);
				}

				if (
					keepDetailElementRangeForOrphanFooter
					&& detailElementRange == null
					)
				{
					detailElementRange = new SimpleElementRange(getCurrentPage(), columnIndex, offsetY);
				}

				fillColumnBand(detailBand, JRExpression.EVALUATION_DEFAULT, !atLeastOneDetailBandPrinted); //recalculate variables on refill if this is the first detail band
				
				if (detailElementRange == null)
				{
					// page/column break occurred so we give up keeping the detail element range altogether;
					// even if the detail moved onto new page/column completely (did not start at the bottom as it was
					// non-splitting or did not fit within break height), we still need to give up on keeping
					// the current detail element range because only the next detail on the page/column would make
					// sense to move for orphan footers;
					// if there will be no second detail there, we simply can't move the only detail, so we still have to give up here
					keepDetailElementRangeForOrphanFooter = false;
				}
				else
				{
					// there was no overflow, otherwise this range would have been reset to null during page/column break
					
					ElementRange newElementRange = new SimpleElementRange(getCurrentPage(), columnIndex, offsetY);

					ElementRangeUtil.expandOrIgnore(detailElementRange, newElementRange);
				}

				atLeastOneDetailBandPrinted = true;

				isFirstPageBand = false;
				isFirstColumnBand = false;
			}
		}
		
		if (atLeastOneDetailBandPrinted)
		{
			if (groups != null)
			{
				for (JRFillGroup group : groups)
				{
					group.incrementDetailsCount();
				}
			}
		}
		
		hasDetailOnPage = atLeastOneDetailBandPrinted;

		isNewPage = false;
		isNewColumn = false;
	}


	/**
	 *
	 */
	@continuable
	private void fillGroupFooters(boolean isFillAll) throws JRException
	{
		if (groups != null && groups.length > 0)
		{
			byte evaluation = (isFillAll)?JRExpression.EVALUATION_DEFAULT:JRExpression.EVALUATION_OLD;

			preventOrphanFootersMinLevel = null;
			for (int i = groups.length - 1; i >= 0; i--)
			{
				JRFillGroup group = groups[i];
				
				if (
					(isFillAll || group.hasChanged())
					&& group.isPreventOrphanFooter()
					)
				{
					// we need to decide up-front if during the current group footers printing,
					// there are any potential orphans to take care of
					preventOrphanFootersMinLevel = i;
					break;
				}
			}
			
			for (int i = groups.length - 1; i >= 0; i--)
			{
				JRFillGroup group = groups[i];
				
				crtGroupFootersLevel = i;
				if (
					preventOrphanFootersMinLevel != null
					&& crtGroupFootersLevel < preventOrphanFootersMinLevel
					)
				{
					// reset the element ranges when we get to the group footers
					// that are outer to the ones for which we need to prevent orphans;
					// these ranges act like flags to signal we need to deal with orphans
					orphanGroupFooterDetailElementRange = null;
					orphanGroupFooterElementRange = null;
					detailElementRange = null; // put this line here just to have the same trio everywhere; could not find a case when this line really matters
				}
				
				if (isFillAll || group.hasChanged())
				{
					fillGroupFooter(group, evaluation);
					
					// regardless of whether the fillGroupFooter was printed or not, 
					// we just need to mark the end of the group 
					group.setKeepTogetherElementRange(null);
				}
			}
			
			// resetting orphan footer element ranges because all group footers have been rendered
			orphanGroupFooterDetailElementRange = null;
			orphanGroupFooterElementRange = null;
			detailElementRange = null; // put this line here just to have the same trio everywhere; could not find a case when this line really matters
			
			// we need to take care of groupFooterPositionElementRange here because all groups footers have been 
			// rendered and we need to consume remaining space before next groups start;
			//
			// but we don't process the last groupFooterPositionElementRange when the report ends (isFillAll true),
			// because it will be dealt with during summary rendering, depending on whether a last page footer exists or not
			if (
				!isFillAll
				&& groupFooterPositionElementRange != null
				)
			{
				ElementRangeUtil.moveContent(groupFooterPositionElementRange, columnFooterOffsetY);
				groupFooterPositionElementRange = null;
				// update the offsetY to signal there is no more space left at the bottom after forcing the footer
				offsetY = columnFooterOffsetY;
			}
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillGroupFooter(JRFillGroup group, byte evaluation) throws JRException
	{
		JRFillSection groupFooterSection = (JRFillSection)group.getGroupFooterSection();

		if (log.isDebugEnabled() && !groupFooterSection.isEmpty())
		{
			log.debug("Fill " + fillerId + ": " + group.getName() + " footer at " + offsetY);
		}

		JRFillBand[] groupFooterBands = groupFooterSection.getFillBands();
		for (int i = 0; i < groupFooterBands.length; i++)
		{
			JRFillBand groupFooterBand = groupFooterBands[i];
			
			groupFooterBand.evaluatePrintWhenExpression(evaluation);

			if (groupFooterBand.isToPrint())
			{
				if (
					preventOrphanFootersMinLevel != null
					&& crtGroupFootersLevel >= preventOrphanFootersMinLevel 
					&& orphanGroupFooterDetailElementRange == null
					)
				{
					// the detail element range can't be null here, unless there is no detail printing;
					// keeping the detail element range in this separate variable signals we are currently
					// dealing with orphan group footers
					orphanGroupFooterDetailElementRange = detailElementRange;
				}
				
				if (
					groupFooterBand.getBreakHeight() > columnFooterOffsetY - offsetY
					)
				{
					fillColumnBreak(evaluation, evaluation); // using same evaluation for both side of the break is ok here
				}

				if (
					groupFooterPositionElementRange == null 
					&& group.getFooterPositionValue() != FooterPositionEnum.NORMAL
					)
				{
					groupFooterPositionElementRange = 
						new SimpleGroupFooterElementRange(
							new SimpleElementRange(getCurrentPage(), columnIndex, offsetY), 
							group.getFooterPositionValue()
							);
				}

				if (groupFooterPositionElementRange != null)
				{
					// keep the current group footer position because it will be needed
					// in case the band breaks and the group footer element range needs to
					// be recreated on the new page
					groupFooterPositionElementRange.setCurrentFooterPosition(group.getFooterPositionValue());
				}
				
				if (orphanGroupFooterDetailElementRange != null)
				{
					ElementRange newElementRange = new SimpleElementRange(getCurrentPage(), columnIndex, offsetY);
					if (orphanGroupFooterElementRange == null)
					{
						orphanGroupFooterElementRange = newElementRange;
					}
					else
					{
						ElementRangeUtil.expandOrIgnore(orphanGroupFooterElementRange, newElementRange);
					}
				}
				
				fillColumnBand(groupFooterBand, evaluation, false);
				
				ElementRange newElementRange = new SimpleElementRange(getCurrentPage(), columnIndex, offsetY);
				
				if (groupFooterPositionElementRange != null)
				{
					ElementRangeUtil.expandOrIgnore(groupFooterPositionElementRange.getElementRange(), newElementRange);

					switch (group.getFooterPositionValue())
					{
						case STACK_AT_BOTTOM :
						{
							groupFooterPositionElementRange.setMasterFooterPosition(FooterPositionEnum.STACK_AT_BOTTOM);
							break;
						}
						case FORCE_AT_BOTTOM :
						{
							groupFooterPositionElementRange.setMasterFooterPosition(FooterPositionEnum.FORCE_AT_BOTTOM);
							break;
						}
						case COLLATE_AT_BOTTOM :
						{
							break;
						}
						case NORMAL :
						default :
						{
							// only StackAtBottom and CollateAtBottom can get here
							if (groupFooterPositionElementRange.getMasterFooterPosition() == FooterPositionEnum.COLLATE_AT_BOTTOM)
							{
								groupFooterPositionElementRange = null;
							}
							break;
						}
					}
				}
				
				isFirstPageBand = false;
				isFirstColumnBand = false;
			}
		}

		// we need to perform ForceAtBottom here because only the group footer as a whole should be forced to bottom, 
		// not the individual bands in this footer section;
		// also, when forcing a group footer to bottom, we consider the normal/current columnFooterOffsetY, because it is impossible
		// to tell at this point if this would be the last page or not (last page footer)
		if (
			groupFooterPositionElementRange != null
			&& groupFooterPositionElementRange.getMasterFooterPosition() == FooterPositionEnum.FORCE_AT_BOTTOM
			)
		{
			ElementRangeUtil.moveContent(groupFooterPositionElementRange, columnFooterOffsetY);
			groupFooterPositionElementRange = null;
			// update the offsetY to signal there is no more space left at the bottom after forcing the footer
			offsetY = columnFooterOffsetY;
		}

		isNewPage = false;
		isNewColumn = false;

		group.setHeaderPrinted(false);
		group.setFooterPrinted(true);
	}


	/**
	 *
	 */
	 private void fillColumnFooter(byte evaluation) throws JRException
	 {
		if (log.isDebugEnabled() && !columnFooter.isEmpty())
		{
			log.debug("Fill " + fillerId + ": column footer at " + offsetY);
		}

		setOffsetX();
		
		/*
		if (!isSubreport)
		{
			offsetY = columnFooterOffsetY;
		}
		*/

		if (isSubreport() && !isSubreportRunToBottom() && columnIndex == 0)
		{
			columnFooterOffsetY = offsetY;
		}

		int oldOffsetY = offsetY;
		if (!isFloatColumnFooter && !ignorePagination)
		{
			offsetY = columnFooterOffsetY;
		}

		// we first let the column footer Y offset calculations to occur normally above, 
		// before attempting to deal with existing groupFooterPositionElementRange
		if (groupFooterPositionElementRange != null)
		{
			// all types of footer position can get here (StackAtBottom, CollapseAtBottom and ForceAtBottom);
			// ForceAtBottom group footer element ranges could reach this point in case multi-band group footer gets
			// split across a column/page break; remaining bands in such group footer would be dealt at the end 
			// of the group footer filling method (see fillGroupFooter() method above)
			ElementRangeUtil.moveContent(groupFooterPositionElementRange, columnFooterOffsetY);
			groupFooterPositionElementRange = null;
			// we do not need to set the offsetY because it has already been set properly earlier in this method;
		}
		
		columnFooter.evaluatePrintWhenExpression(evaluation);

		if (columnFooter.isToPrint())
		{
			if (isFloatColumnFooter && !ignorePagination)
			{
				floatColumnFooterElementRange = new SimpleElementRange(getCurrentPage(), columnIndex, offsetY);
			}
			
			fillFixedBand(columnFooter, evaluation);
			
			if (floatColumnFooterElementRange != null)
			{
				floatColumnFooterElementRange.expand(offsetY);
			}
		}

		if (isFloatColumnFooter && !ignorePagination)
		{
			offsetY += columnFooterOffsetY - oldOffsetY;
		}
	}


	/**
	 *
	 */
	private void fillPageFooter(byte evaluation) throws JRException
	{
		JRFillBand crtPageFooter = getCurrentPageFooter();

		if (log.isDebugEnabled() && !crtPageFooter.isEmpty())
		{
			log.debug("Fill " + fillerId + ": " + (isLastPageFooter ? "last " : "") + "page footer at " + offsetY);
		}

		offsetX = leftMargin;

		if ((!isSubreport() || isSubreportRunToBottom()) && !ignorePagination)
		{
			offsetY = pageHeight - crtPageFooter.getHeight() - bottomMargin;
		}

		crtPageFooter.evaluatePrintWhenExpression(evaluation);

		if (crtPageFooter.isToPrint())
		{
			fillFixedBand(crtPageFooter, evaluation);
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillSummary() throws JRException
	{
		if (log.isDebugEnabled() && !summary.isEmpty())
		{
			log.debug("Fill " + fillerId + ": summary at " + offsetY);
		}

		offsetX = leftMargin;
		
		if (lastPageFooter == missingFillBand)
		{
			if (
				!isSummaryNewPage
				&& columnIndex == 0
				&& summary.getBreakHeight() <= columnFooterOffsetY - offsetY
				)
			{
				fillSummaryNoLastFooterSamePage();
			}
			else
			{
				fillSummaryNoLastFooterNewPage();
			}
		}
		else
		{
			if (isSummaryWithPageHeaderAndFooter)
			{
				fillSummaryWithLastFooterAndPageBands();
			}
			else
			{
				fillSummaryWithLastFooterNoPageBands();
			}
		}

		resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
		resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
		resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
		resolveReportBoundElements();
		if (isMasterReport())
		{
			resolveMasterBoundElements();
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillSummaryNoLastFooterSamePage() throws JRException
	{
		summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

		if (summary.isToPrint())
		{
			// deal with groupFooterPositionElementRange here because summary will attempt to use remaining space
			if (groupFooterPositionElementRange != null)
			{
				ElementRangeUtil.moveContent(groupFooterPositionElementRange, columnFooterOffsetY);
				offsetY = columnFooterOffsetY;
				// reset the element range here although it will not be checked anymore as the report ends
				groupFooterPositionElementRange = null;
			}
			
			summary.evaluate(JRExpression.EVALUATION_DEFAULT);

			JRPrintBand printBand = summary.fill(columnFooterOffsetY - offsetY);

			if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
			{
				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);

				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);
				
				if (isSummaryWithPageHeaderAndFooter)
				{
					fillPageHeader(JRExpression.EVALUATION_DEFAULT);
				}

				printBand = 
					summary.refill(
						JRExpression.EVALUATION_DEFAULT, 
						pageHeight - bottomMargin - offsetY - (isSummaryWithPageHeaderAndFooter?pageFooter.getHeight():0)
						);

				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;

				/*   */
				fillSummaryOverflow();
				
				//DONE
			}
			else
			{
				//SummaryReport.14 test
				
				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;

				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				
				if (summary.willOverflow())
				{
					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);
					
					if (isSummaryWithPageHeaderAndFooter)
					{
						fillPageHeader(JRExpression.EVALUATION_DEFAULT);
					}

					printBand = summary.fill(pageHeight - bottomMargin - offsetY - (isSummaryWithPageHeaderAndFooter?pageFooter.getHeight():0));

					fillBand(printBand);
					offsetY += printBand.getHeight();
					isCrtRecordOnPage = true;
					isCrtRecordOnColumn = true;

					/*   */
					fillSummaryOverflow();
					
					//DONE
				}
				else
				{
					resolveBandBoundElements(summary, JRExpression.EVALUATION_DEFAULT);

					//DONE
				}
			}
		}
		else
		{
			//SummaryReport.15 test
			
			// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do
			
			fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

			fillPageFooter(JRExpression.EVALUATION_DEFAULT);
			
			//DONE
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillSummaryNoLastFooterNewPage() throws JRException
	{
		//SummaryReport.13 test

		// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do
		
		fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

		fillPageFooter(JRExpression.EVALUATION_DEFAULT);

		summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

		if (summary.isToPrint())
		{
			resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
			resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
			resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
			scriptlet.callBeforePageInit();
			calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
			scriptlet.callAfterPageInit();

			addPage(false);

			if (isSummaryWithPageHeaderAndFooter)
			{
				fillPageHeader(JRExpression.EVALUATION_DEFAULT);
			}

			summary.evaluate(JRExpression.EVALUATION_DEFAULT);

			JRPrintBand printBand = summary.fill(pageHeight - bottomMargin - offsetY - (isSummaryWithPageHeaderAndFooter?pageFooter.getHeight():0));

			if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
			{
				if (isSummaryWithPageHeaderAndFooter)
				{
					fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				}

				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);

				if (isSummaryWithPageHeaderAndFooter)
				{
					fillPageHeader(JRExpression.EVALUATION_DEFAULT);
				}

				printBand = 
					summary.refill(
						JRExpression.EVALUATION_DEFAULT, 
						pageHeight - bottomMargin - offsetY - (isSummaryWithPageHeaderAndFooter?pageFooter.getHeight():0)
						);
			}

			fillBand(printBand);
			offsetY += printBand.getHeight();
			isCrtRecordOnPage = true;
			isCrtRecordOnColumn = true;

			/*   */
			fillSummaryOverflow();
		}
		
		//DONE
	}


	/**
	 *
	 */
	@continuable
	private void fillSummaryWithLastFooterAndPageBands() throws JRException
	{
		if (
			!isSummaryNewPage
			&& columnIndex == 0
			&& summary.getBreakHeight() <= columnFooterOffsetY - offsetY
			)
		{
			summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (summary.isToPrint())
			{
				// deal with groupFooterPositionElementRange here because summary will attempt to use remaining space
				if (groupFooterPositionElementRange != null)
				{
					ElementRangeUtil.moveContent(groupFooterPositionElementRange, columnFooterOffsetY);
					offsetY = columnFooterOffsetY;
					// reset the element range here although it will not be checked anymore as the report ends
					groupFooterPositionElementRange = null;
				}
				
				summary.evaluate(JRExpression.EVALUATION_DEFAULT);

				JRPrintBand printBand = summary.fill(columnFooterOffsetY - offsetY);

				if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
				{
					fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

					fillPageFooter(JRExpression.EVALUATION_DEFAULT);

					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);
					
					fillPageHeader(JRExpression.EVALUATION_DEFAULT);
					
					printBand = 
						summary.refill(
							JRExpression.EVALUATION_DEFAULT,
							pageHeight - bottomMargin - offsetY - pageFooter.getHeight()
							);

					fillBand(printBand);
					offsetY += printBand.getHeight();
					isCrtRecordOnPage = true;
					isCrtRecordOnColumn = true;
				}
				else
				{
					//SummaryReport.8 test

					fillBand(printBand);
					offsetY += printBand.getHeight();
					isCrtRecordOnPage = true;
					isCrtRecordOnColumn = true;

					if (
						!summary.willOverflow()
						&& offsetY <= lastPageColumnFooterOffsetY
						)
					{
						setLastPageFooter(true);
					}
					
					fillColumnFooter(JRExpression.EVALUATION_DEFAULT);
				}
				
				/*   */
				fillSummaryOverflow();

				//DONE
			}
			else
			{
				// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do
				// it will be either the one in fillPageBreak or the following
				
				if (offsetY > lastPageColumnFooterOffsetY)
				{
					//SummaryReport.22 test
					fillPageBreak(false, JRExpression.EVALUATION_DEFAULT, JRExpression.EVALUATION_DEFAULT, false);
				}
				else
				{
					//SummaryReport.9 test
				}
				
				setLastPageFooter(true);

				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				
				//DONE
			}
		}
		else if (columnIndex == 0 && offsetY <= lastPageColumnFooterOffsetY)
		{
			summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (summary.isToPrint())
			{
				//SummaryReport.10 test

				// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do
				
				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);

				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);
				
				fillPageHeader(JRExpression.EVALUATION_DEFAULT);

				summary.evaluate(JRExpression.EVALUATION_DEFAULT);

				JRPrintBand printBand = summary.fill(pageHeight - bottomMargin - offsetY - pageFooter.getHeight());

				if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
				{
					fillPageFooter(JRExpression.EVALUATION_DEFAULT);

					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);
					
					fillPageHeader(JRExpression.EVALUATION_DEFAULT);

					printBand = 
						summary.refill(
							JRExpression.EVALUATION_DEFAULT,
							pageHeight - bottomMargin - offsetY - pageFooter.getHeight()
							);
				}

				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;

				/*   */
				fillSummaryOverflow();
				
				//DONE
			}
			else
			{
				//SummaryReport.11 test

				// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do
				
				setLastPageFooter(true);

				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				
				//DONE
			}
		}
		else
		{
			// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do
			
			fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

			fillPageFooter(JRExpression.EVALUATION_DEFAULT);

			resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, false);
			resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
			resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
			scriptlet.callBeforePageInit();
			calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
			scriptlet.callAfterPageInit();

			addPage(false);

			fillPageHeader(JRExpression.EVALUATION_DEFAULT);

			summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (summary.isToPrint())
			{
				//SummaryReport.12 test

				summary.evaluate(JRExpression.EVALUATION_DEFAULT);

				JRPrintBand printBand = summary.fill(pageHeight - bottomMargin - offsetY - pageFooter.getHeight());

				if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
				{
					fillPageFooter(JRExpression.EVALUATION_DEFAULT);

					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);
					
					fillPageHeader(JRExpression.EVALUATION_DEFAULT);

					printBand = 
						summary.refill(
							JRExpression.EVALUATION_DEFAULT,
							pageHeight - bottomMargin - offsetY - pageFooter.getHeight()
							);
				}

				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;
			}
			else
			{
				//SummaryReport.16 test
			}

			/*   */
			fillSummaryOverflow();
			
			//DONE
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillSummaryWithLastFooterNoPageBands() throws JRException
	{
		if (
			!isSummaryNewPage
			&& columnIndex == 0
			&& summary.getBreakHeight() <= lastPageColumnFooterOffsetY - offsetY
			)
		{
			setLastPageFooter(true);

			summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (summary.isToPrint())
			{
				// deal with groupFooterPositionElementRange here because summary will attempt to use remaining space
				if (groupFooterPositionElementRange != null)
				{
					ElementRangeUtil.moveContent(groupFooterPositionElementRange, columnFooterOffsetY);
					offsetY = columnFooterOffsetY;
					// reset the element range here although it will not be checked anymore as the report ends
					groupFooterPositionElementRange = null;
				}
				
				summary.evaluate(JRExpression.EVALUATION_DEFAULT);

				JRPrintBand printBand = summary.fill(columnFooterOffsetY - offsetY);

				if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
				{
					fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

					fillPageFooter(JRExpression.EVALUATION_DEFAULT);

					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);

					printBand = 
						summary.refill(
							JRExpression.EVALUATION_DEFAULT, 
							pageHeight - bottomMargin - offsetY
							);

					fillBand(printBand);
					offsetY += printBand.getHeight();
					isCrtRecordOnPage = true;
					isCrtRecordOnColumn = true;
				}
				else
				{
					//SummaryReport.1 test
					
					fillBand(printBand);
					offsetY += printBand.getHeight();
					isCrtRecordOnPage = true;
					isCrtRecordOnColumn = true;

					fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

					fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				}

				/*   */
				fillSummaryOverflow();
				
				//DONE
			}
			else
			{
				//SummaryReport.2 test
				
				// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do;

				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				
				//DONE
			}
		}
		else if (
			!isSummaryNewPage
			&& columnIndex == 0
			&& summary.getBreakHeight() <= columnFooterOffsetY - offsetY
			)
		{
			summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (summary.isToPrint())
			{
				// deal with groupFooterPositionElementRange here because summary will attempt to use remaining space
				if (groupFooterPositionElementRange != null)
				{
					ElementRangeUtil.moveContent(groupFooterPositionElementRange, columnFooterOffsetY);
					offsetY = columnFooterOffsetY;
					// reset the element range here although it will not be checked anymore as the report ends
					groupFooterPositionElementRange = null;
				}
				
				summary.evaluate(JRExpression.EVALUATION_DEFAULT);

				JRPrintBand printBand = summary.fill(columnFooterOffsetY - offsetY);

				if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
				{
					if (offsetY <= lastPageColumnFooterOffsetY)
					{
						setLastPageFooter(true);

						fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

						fillPageFooter(JRExpression.EVALUATION_DEFAULT);

						resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
						resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
						resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
						scriptlet.callBeforePageInit();
						calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
						scriptlet.callAfterPageInit();

						addPage(false);

						printBand = 
							summary.refill(
								JRExpression.EVALUATION_DEFAULT,
								pageHeight - bottomMargin - offsetY
								);

						fillBand(printBand);
						offsetY += printBand.getHeight();
						isCrtRecordOnPage = true;
						isCrtRecordOnColumn = true;
					}
					else
					{
						fillPageBreak(false, JRExpression.EVALUATION_DEFAULT, JRExpression.EVALUATION_DEFAULT, false);

						setLastPageFooter(true);

						printBand = 
							summary.refill(
								JRExpression.EVALUATION_DEFAULT,
								lastPageColumnFooterOffsetY - offsetY
								);

						fillBand(printBand);
						offsetY += printBand.getHeight();
						isCrtRecordOnPage = true;
						isCrtRecordOnColumn = true;

						fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

						fillPageFooter(JRExpression.EVALUATION_DEFAULT);
					}
				}
				else
				{
					//SummaryReport.3 test
					
					fillBand(printBand);
					offsetY += printBand.getHeight();
					isCrtRecordOnPage = true;
					isCrtRecordOnColumn = true;

					fillPageBreak(false, JRExpression.EVALUATION_DEFAULT, JRExpression.EVALUATION_DEFAULT, false);

					setLastPageFooter(true);

					if (summary.willOverflow())
					{
						printBand = summary.fill(lastPageColumnFooterOffsetY - offsetY);

						fillBand(printBand);
						offsetY += printBand.getHeight();
						isCrtRecordOnPage = true;
						isCrtRecordOnColumn = true;
					}

					fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

					fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				}

				/*   */
				fillSummaryOverflow();
				
				//DONE
			}
			else
			{
				// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do;
				// it will be either the one in fillPageBreak or the following
				
				if (offsetY > lastPageColumnFooterOffsetY)
				{
					//SummaryReport.5 test
					fillPageBreak(false, JRExpression.EVALUATION_DEFAULT, JRExpression.EVALUATION_DEFAULT, false);
				}
				else
				{
					//SummaryReport.4 test
				}
				
				setLastPageFooter(true);

				fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				
				//DONE
			}
		}
		else if (columnIndex == 0 && offsetY <= lastPageColumnFooterOffsetY)
		{
			//SummaryReport.6 test
			
			// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do

			setLastPageFooter(true);

			fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

			fillPageFooter(JRExpression.EVALUATION_DEFAULT);

			summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (summary.isToPrint())
			{
				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);

				summary.evaluate(JRExpression.EVALUATION_DEFAULT);

				JRPrintBand printBand = summary.fill(pageHeight - bottomMargin - offsetY);

				if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
				{
					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);

					printBand = 
						summary.refill(
							JRExpression.EVALUATION_DEFAULT,
							pageHeight - bottomMargin - offsetY
							);
				}

				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;

				/*   */
				fillSummaryOverflow();
			}
			
			//DONE
		}
		else
		{
			//SummaryReport.7 test
			
			// do nothing about groupFooterPositionElementRange because the following fillColumnFooter will do;

			fillColumnFooter(JRExpression.EVALUATION_DEFAULT);

			fillPageFooter(JRExpression.EVALUATION_DEFAULT);

			resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, false);
			resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
			resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
			scriptlet.callBeforePageInit();
			calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
			scriptlet.callAfterPageInit();

			addPage(false);

			fillPageHeader(JRExpression.EVALUATION_DEFAULT);

			//fillColumnHeader(JRExpression.EVALUATION_DEFAULT);

			setLastPageFooter(true);

			if (isSummaryNewPage)
			{
				fillPageFooter(JRExpression.EVALUATION_DEFAULT);

				summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

				if (summary.isToPrint())
				{
					resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
					resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
					resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
					scriptlet.callBeforePageInit();
					calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
					scriptlet.callAfterPageInit();

					addPage(false);

					summary.evaluate(JRExpression.EVALUATION_DEFAULT);

					JRPrintBand printBand = summary.fill(pageHeight - bottomMargin - offsetY);

					if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
					{
						resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
						resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
						resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
						scriptlet.callBeforePageInit();
						calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
						scriptlet.callAfterPageInit();

						addPage(false);

						printBand = 
							summary.refill(
								JRExpression.EVALUATION_DEFAULT,
								pageHeight - bottomMargin - offsetY
								);
					}

					fillBand(printBand);
					offsetY += printBand.getHeight();
					isCrtRecordOnPage = true;
					isCrtRecordOnColumn = true;

					/*   */
					fillSummaryOverflow();
				}
				
				//DONE
			}
			else
			{
				summary.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

				if (summary.isToPrint())
				{
					summary.evaluate(JRExpression.EVALUATION_DEFAULT);

					JRPrintBand printBand = summary.fill(columnFooterOffsetY - offsetY);

					if (summary.willOverflow() && summary.isSplitPrevented() && !summary.isSplitTypePreventInhibited())
					{
						fillPageFooter(JRExpression.EVALUATION_DEFAULT);

						resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
						resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
						resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
						scriptlet.callBeforePageInit();
						calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
						scriptlet.callAfterPageInit();

						addPage(false);

						printBand = 
							summary.refill(
								JRExpression.EVALUATION_DEFAULT,
								pageHeight - bottomMargin - offsetY
								);

						fillBand(printBand);
						offsetY += printBand.getHeight();
						isCrtRecordOnPage = true;
						isCrtRecordOnColumn = true;
					}
					else
					{
						fillBand(printBand);
						offsetY += printBand.getHeight();
						isCrtRecordOnPage = true;
						isCrtRecordOnColumn = true;

						fillPageFooter(JRExpression.EVALUATION_DEFAULT);
					}

					/*   */
					fillSummaryOverflow();
				}
				else
				{
					fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				}
				
				//DONE
			}
		}
	}


	/**
	 *
	 */
	@continuable
	private void fillSummaryOverflow() throws JRException
	{
		while (summary.willOverflow())
		{
			if (isSummaryWithPageHeaderAndFooter)
			{
				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
			}
			
			resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
			resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
			resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
			scriptlet.callBeforePageInit();
			calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
			scriptlet.callAfterPageInit();

			addPage(false);

			if (isSummaryWithPageHeaderAndFooter)
			{
				fillPageHeader(JRExpression.EVALUATION_DEFAULT);
			}
			
			JRPrintBand printBand = summary.fill(pageHeight - bottomMargin - offsetY - (isSummaryWithPageHeaderAndFooter?pageFooter.getHeight():0));

			fillBand(printBand);
			offsetY += printBand.getHeight();
			isCrtRecordOnPage = true;
			isCrtRecordOnColumn = true;
		}

		resolveBandBoundElements(summary, JRExpression.EVALUATION_DEFAULT);

		if (isSummaryWithPageHeaderAndFooter)
		{
			if (offsetY > pageHeight - bottomMargin - lastPageFooter.getHeight())
			{
				fillPageFooter(JRExpression.EVALUATION_DEFAULT);
				
				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, true);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();

				addPage(false);

				fillPageHeader(JRExpression.EVALUATION_DEFAULT);
			}
			
			if (lastPageFooter != missingFillBand)
			{
				setLastPageFooter(true);
			}
			
			fillPageFooter(JRExpression.EVALUATION_DEFAULT);
		}
	}


	/**
	 *
	 */
	private void fillBackground() throws JRException
	{
		if (log.isDebugEnabled() && !background.isEmpty())
		{
			log.debug("Fill " + fillerId + ": background at " + offsetY);
		}

		//offsetX = leftMargin;

		//if (!isSubreport)
		//{
		//	offsetY = pageHeight - pageFooter.getHeight() - bottomMargin;
		//}

		if (background.getHeight() <= pageHeight - bottomMargin - offsetY)
		{
			background.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

			if (background.isToPrint())
			{
				background.evaluate(JRExpression.EVALUATION_DEFAULT);

				JRPrintBand printBand = background.fill(pageHeight - bottomMargin - offsetY);

				fillBand(printBand);
				//offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;
				
				//FIXMENOW does not have resolveBandBoundElements
			}
		}
	}


	/**
	 *
	 */
	@continuable
	private void addPage(boolean isResetPageNumber) throws JRException
	{
		if (isSubreport())
		{
			addPageToParent(false);
		}
		
		if (printPage != null)
		{
			recordUsedPageHeight(offsetY + bottomMargin);
		}

		printPage = newPage();
		printPageContentsWidth = 0;

		JRFillVariable pageNumberVar = calculator.getPageNumber();
		if (isResetPageNumber)
		{
			pageNumberVar.setValue(1);
		}
		else
		{
			pageNumberVar.setValue(
				((Number)pageNumberVar.getValue()).intValue() + 1
				);
		}
		pageNumberVar.setOldValue(pageNumberVar.getValue());

		addPage(printPage);

		setFirstColumn();
		offsetY = topMargin;
		isFirstPageBand = true;
		isFirstColumnBand = true;
		isCrtRecordOnPage = false;
		isCrtRecordOnColumn = false;

		fillBackground();
	}

	private void setFirstColumn()
	{
		columnIndex = 0;
		offsetX = leftMargin;
		setColumnNumberVar();
	}

	private void setColumnNumberVar()
	{
		JRFillVariable columnNumber = calculator.getColumnNumber();
		columnNumber.setValue(columnIndex + 1);
		columnNumber.setOldValue(columnNumber.getValue());
	}

	/**
	 *
	 */
	@continuable
	private void fillPageBreak(
		boolean isResetPageNumber,
		byte evalPrevPage,
		byte evalNextPage,
		boolean isReprintGroupHeaders
		) throws JRException
	{
		if (isCreatingNewPage)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_INFINITE_LOOP_CREATING_NEW_PAGE,  
					(Object[])null 
					);
		}

		if (groups != null)
		{
			for (JRFillGroup group : groups)
			{
				if (group.getKeepTogetherElementRange() != null)
				{
					group.getKeepTogetherElementRange().expand(offsetY);
				}
			}
		}
		
		FooterPositionEnum groupFooterPositionForOverflow = null;
		if (groupFooterPositionElementRange != null)
		{
			groupFooterPositionForOverflow = groupFooterPositionElementRange.getCurrentFooterPosition();
			// we are during group footers filling, otherwise this element range would have been null;
			// adding the content of the group footer band that is currently breaking
			groupFooterPositionElementRange.getElementRange().expand(offsetY);
		}

		if (orphanGroupFooterElementRange != null)
		{
			// we are during a group footer filling and footers already started to print,
			// so the current expansion applies to the group footer element range, not the detail element range
			orphanGroupFooterElementRange.expand(offsetY);
		}
		else if (orphanGroupFooterDetailElementRange != null)
		{
			// we are during a group footer filling, but footers did not yet start to print,
			// so the current expansion applies to the detail element range
			orphanGroupFooterDetailElementRange.expand(offsetY);
		}
		
		isCreatingNewPage = true;

		fillColumnFooter(evalPrevPage);

		fillPageFooter(evalPrevPage);

		resolveGroupBoundElements(evalPrevPage, false);
		resolveColumnBoundElements(evalPrevPage);
		resolvePageBoundElements(evalPrevPage);
		scriptlet.callBeforePageInit();
		calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
		scriptlet.callAfterPageInit();
		
		JRFillGroup keepTogetherGroup = getKeepTogetherGroup();
		
		ElementRange elementRangeToMove = null;
		ElementRange elementRangeToMove2 = null; // we don't have more than two possible element ranges to move; at least for now
		if (keepTogetherGroup != null)
		{
			elementRangeToMove = keepTogetherGroup.getKeepTogetherElementRange();
		}
		else if (orphanGroupFooterDetailElementRange != null)
		{
			elementRangeToMove = orphanGroupFooterDetailElementRange;
			elementRangeToMove2 = orphanGroupFooterElementRange;
		}

		if (floatColumnFooterElementRange != null && elementRangeToMove != null)
		{
			ElementRangeUtil.moveContent(floatColumnFooterElementRange, elementRangeToMove.getTopY());
		}

		// remove second range first, otherwise element indexes would become out of range
		ElementRangeContents elementsToMove2 = null;
		if (elementRangeToMove2 != null)
		{
			elementsToMove2 = ElementRangeUtil.removeContent(elementRangeToMove2, delayedActions);
		}
		ElementRangeContents elementsToMove = null;
		if (elementRangeToMove != null)
		{
			elementsToMove = ElementRangeUtil.removeContent(elementRangeToMove, delayedActions);
		}

		addPage(isResetPageNumber);

		fillPageHeader(evalNextPage);

		fillColumnHeader(evalNextPage);

		if (isReprintGroupHeaders)
		{
			fillGroupHeadersReprint(evalNextPage, true);

			ElementRange keepTogetherElementRange = keepTogetherGroup == null ? null : keepTogetherGroup.getKeepTogetherElementRange();
			
			if (
				keepTogetherElementRange != null
				&& offsetY > keepTogetherElementRange.getTopY()
				)
			{
				throw new JRException(EXCEPTION_MESSAGE_KEY_KEEP_TOGETHER_CONTENT_DOES_NOT_FIT, (Object[]) null);
			}
		}

		// reseting all movable element ranges
		orphanGroupFooterDetailElementRange = null;
		orphanGroupFooterElementRange = null;
		detailElementRange = null;
		if (keepTogetherGroup != null)
		{
			keepTogetherGroup.setKeepTogetherElementRange(null);
		}

		if (elementRangeToMove != null)
		{
			ElementRangeUtil.addContent(
				printPage, 
				currentPageIndex(),
				elementsToMove,
				//regardless whether there was page break or column  break, the X offset needs to account for columnIndex difference
				(columnIndex - elementRangeToMove.getColumnIndex()) * (columnSpacing + columnWidth),
				offsetY - elementRangeToMove.getTopY(),
				delayedActions
				);

			offsetY = offsetY + elementRangeToMove.getBottomY() - elementRangeToMove.getTopY();
			
			if (elementRangeToMove2 != null)
			{
				ElementRangeUtil.addContent( 
					printPage, 
					currentPageIndex(),
					elementsToMove2,
					//regardless whether there was page break or column  break, the X offset needs to account for columnIndex difference
					(columnIndex - elementRangeToMove2.getColumnIndex()) * (columnSpacing + columnWidth),
					offsetY - elementRangeToMove2.getTopY(),
					delayedActions
					);

				offsetY = offsetY + elementRangeToMove2.getBottomY() - elementRangeToMove2.getTopY();
			}
			
			isFirstPageBand = false;
			isFirstColumnBand = false;
		} 
		else if (
			groupFooterPositionForOverflow != null
			&& groupFooterPositionForOverflow != FooterPositionEnum.NORMAL
			)
		{
			// here we are during a group footer filling that broke over onto a new page;
			// recreating the group footer element range for the overflow content of the band
			groupFooterPositionElementRange = 
				new SimpleGroupFooterElementRange(
					new SimpleElementRange(getCurrentPage(), columnIndex, offsetY), 
					groupFooterPositionForOverflow
					);
		}

		isCreatingNewPage = false;
	}


	/**
	 *
	 */
	@continuable
	private void fillColumnBreak(
		byte evalPrevPage,
		byte evalNextPage
		) throws JRException
	{
		if (columnIndex == columnCount - 1)
		{
			fillPageBreak(false, evalPrevPage, evalNextPage, true);
		}
		else
		{
			if (groups != null)
			{
				for (JRFillGroup group : groups)
				{
					if (group.getKeepTogetherElementRange() != null)
					{
						group.getKeepTogetherElementRange().expand(offsetY);
					}
				}
			}
			
			FooterPositionEnum groupFooterPositionForOverflow = null;
			if (groupFooterPositionElementRange != null)
			{
				groupFooterPositionForOverflow = groupFooterPositionElementRange.getCurrentFooterPosition();
				// we are during group footers filling, otherwise this element range would have been null;
				// adding the content of the group footer band that is currently breaking
				groupFooterPositionElementRange.getElementRange().expand(offsetY);
			}
			
			if (orphanGroupFooterElementRange != null)
			{
				// we are during a group footer filling and footers already started to print,
				// so the current expansion applies to the group footer element range, not the detail element range
				orphanGroupFooterElementRange.expand(offsetY);
			}
			else if (orphanGroupFooterDetailElementRange != null)
			{
				// we are during a group footer filling, but footers did not yet start to print,
				// so the current expansion applies to the detail element range
				orphanGroupFooterDetailElementRange.expand(offsetY);
			}
			
			fillColumnFooter(evalPrevPage);

			resolveGroupBoundElements(evalPrevPage, false);
			resolveColumnBoundElements(evalPrevPage);
			scriptlet.callBeforeColumnInit();
			calculator.initializeVariables(ResetTypeEnum.COLUMN, IncrementTypeEnum.COLUMN);
			scriptlet.callAfterColumnInit();

			JRFillGroup keepTogetherGroup = getKeepTogetherGroup();

			ElementRange elementRangeToMove = null;
			ElementRange elementRangeToMove2 = null; // we don't have more than two possible element ranges to move; at least for now
			if (keepTogetherGroup != null)
			{
				elementRangeToMove = keepTogetherGroup.getKeepTogetherElementRange();
			}
			else if (orphanGroupFooterDetailElementRange != null)
			{
				elementRangeToMove = orphanGroupFooterDetailElementRange;
				elementRangeToMove2 = orphanGroupFooterElementRange;
			}
			
			if (floatColumnFooterElementRange != null && elementRangeToMove != null)
			{
				ElementRangeUtil.moveContent(floatColumnFooterElementRange, elementRangeToMove.getTopY());
			}
			
			// remove second range first, otherwise element indexes would become out of range
			ElementRangeContents elementsToMove2 = null;
			if (elementRangeToMove2 != null)
			{
				elementsToMove2 = ElementRangeUtil.removeContent(elementRangeToMove2, delayedActions);
			}
			ElementRangeContents elementsToMove = null;
			if (elementRangeToMove != null)
			{
				elementsToMove = ElementRangeUtil.removeContent(elementRangeToMove, null);
			}

			columnIndex += 1;
			setOffsetX();
			offsetY = columnHeaderOffsetY;
			isCrtRecordOnColumn = false;

			setColumnNumberVar();

			fillColumnHeader(evalNextPage);

			//if (isReprintGroupHeaders)
			{
				fillGroupHeadersReprint(evalNextPage, false);

				ElementRange keepTogetherElementRange = keepTogetherGroup == null ? null : keepTogetherGroup.getKeepTogetherElementRange();
				
				if (
					keepTogetherElementRange != null
					&& offsetY > keepTogetherElementRange.getTopY()
					)
				{
					throw new JRException(EXCEPTION_MESSAGE_KEY_KEEP_TOGETHER_CONTENT_DOES_NOT_FIT, (Object[]) null);
				}
			}

			// reseting all movable element ranges
			orphanGroupFooterDetailElementRange = null;
			orphanGroupFooterElementRange = null;
			detailElementRange = null;
			if (keepTogetherGroup != null)
			{
				keepTogetherGroup.setKeepTogetherElementRange(null);
			}

			if (elementRangeToMove != null)
			{
				ElementRangeUtil.addContent(
					printPage, 
					currentPageIndex(),
					elementsToMove,
					//regardless whether there was page break or column  break, the X offset needs to account for columnIndex difference
					(columnIndex - elementRangeToMove.getColumnIndex()) * (columnSpacing + columnWidth),
					offsetY - elementRangeToMove.getTopY(),
					delayedActions
					);

				offsetY = offsetY + elementRangeToMove.getBottomY() - elementRangeToMove.getTopY();
				
				if (elementRangeToMove2 != null)
				{
					ElementRangeUtil.addContent( 
						printPage, 
						currentPageIndex(),
						elementsToMove2,
						//regardless whether there was page break or column  break, the X offset needs to account for columnIndex difference
						(columnIndex - elementRangeToMove2.getColumnIndex()) * (columnSpacing + columnWidth),
						offsetY - elementRangeToMove2.getTopY(),
						delayedActions
						);

					offsetY = offsetY + elementRangeToMove2.getBottomY() - elementRangeToMove2.getTopY();
				}
				
				isFirstPageBand = false;
				isFirstColumnBand = false;
			}
			else if (
				groupFooterPositionForOverflow != null
				&& groupFooterPositionForOverflow != FooterPositionEnum.NORMAL
				)
			{
				// here we are during a group footer filling that broke over onto a new column;
				// recreating the group footer element range for the overflow content of the band
				groupFooterPositionElementRange = 
					new SimpleGroupFooterElementRange(
						new SimpleElementRange(getCurrentPage(), columnIndex, offsetY), 
						groupFooterPositionForOverflow
						);
			}
		}
	}


	/**
	 *
	 */
	@continuable
	protected void fillColumnBand(JRFillBand band, byte evaluation, boolean recalculateVariables) throws JRException
	{
		band.evaluate(evaluation);

		JRPrintBand printBand = band.fill(columnFooterOffsetY - offsetY);

		if (band.willOverflow())
		{
			boolean toRefill = band.isSplitPrevented() && !band.isSplitTypePreventInhibited();
			
			if (!toRefill)
			{
				if (groups != null)
				{
					// this works even for group headers and footers, not only detail,
					// because outer groups keep together is honored, while for the
					// inner keep together groups, the element range would be null 
					// in-between parent group breaks
					for (JRFillGroup group : groups)
					{
						if (
							group.getKeepTogetherElementRange() != null
							&& (group.isKeepTogether() || !group.hasMinDetails())
							)
						{
							toRefill = true;
							break;
						}
					}
				}
			}
			
			if (!toRefill)
			{
				if (orphanGroupFooterDetailElementRange != null)
				{
					toRefill = true;
				}
			}
			
			if (toRefill)
			{
				fillColumnBreak(
					evaluation == JRExpression.EVALUATION_DEFAULT 
						? (isCrtRecordOnColumn ? JRExpression.EVALUATION_DEFAULT : JRExpression.EVALUATION_OLD) 
						: evaluation, 
					evaluation
					);

				if (recalculateVariables)
				{
					@SuppressWarnings("deprecation")
					boolean isLegacyBandEvaluationEnabled = getFillContext().isLegacyBandEvaluationEnabled(); 
					if (!isLegacyBandEvaluationEnabled)
					{
						mainDataset.revertVariablesToOldValues();
						
						calculator.recalculateVariables();
					}
				}
				
				printBand = band.refill(evaluation, columnFooterOffsetY - offsetY);
			}
		}

		fillBand(printBand);
		offsetY += printBand.getHeight();
		isCrtRecordOnPage = evaluation == JRExpression.EVALUATION_DEFAULT;
		isCrtRecordOnColumn = isCrtRecordOnPage;
		
		while (band.willOverflow())
		{
			// this overflow here is special in the sense that it is the overflow of a detail band or group header or footer,
			// which are the only bands that are involved with movable element ranges such as keep together, footer position or orphan footer;
			// it is also special in the sense that it is an overflow after the band actually generated some content on the current page/column
			// and is not an early overflow like the one occurring when the band does not fit with its declared height or is non-splitting band;
			// having said that, it is OK to be more specific about the type of overflow here and only deal with non-white-space overflows of the band,
			// as they are the only ones which actually need to introduce a page/column break and continue rendering their remaining elements;
			// white space band overflows do not render anything on the next page/column and don't even preserve their remaining white space (historical behavior);
			// avoiding a page/column break here in case of white space overflows helps with preserving the detail element range, which would
			// thus be moved onto the new page/column as a non-breaking detail, if orphan footers follow; 
			// a page/column break here would cause the existing detail element range to be discarded (lost on subsequent element range expand),
			// and thus it would not be moved in case orphan footer follows, 
			// even if nothing gets rendered by this detail on the next page/column 
			if (band.willOverflowWithElements())
			{
				fillColumnBreak(
					evaluation == JRExpression.EVALUATION_DEFAULT 
						? (isCrtRecordOnColumn ? JRExpression.EVALUATION_DEFAULT : JRExpression.EVALUATION_OLD) 
						: evaluation, 
					evaluation
					);
			}

			// we continue filling band overflow normally, because even in case of white space band overflow, nothing gets actually rendered
			// and the offsetY remains unchanged;
			// but we need to do this because the isOverflow flag would eventually be set to false and thus the current band rendering would end,
			// bringing the band into a state ready for the next filling
			printBand = band.fill(columnFooterOffsetY - offsetY);

			fillBand(printBand);
			offsetY += printBand.getHeight();
			isCrtRecordOnPage = evaluation == JRExpression.EVALUATION_DEFAULT;
			isCrtRecordOnColumn = isCrtRecordOnPage;
		}

		resolveBandBoundElements(band, evaluation);
	}


	/**
	 *
	 */
	protected void fillFixedBand(JRFillBand band, byte evaluation) throws JRException
	{
		band.evaluate(evaluation);

		JRPrintBand printBand = band.fill();

		fillBand(printBand);
		offsetY += printBand.getHeight();
		isCrtRecordOnPage = evaluation == JRExpression.EVALUATION_DEFAULT;
		isCrtRecordOnColumn = isCrtRecordOnPage;

		resolveBandBoundElements(band, evaluation);
	}


	/**
	 *
	 */
	private void setNewPageColumnInBands()
	{
		title.setNewPageColumn(true);
		pageHeader.setNewPageColumn(true);
		columnHeader.setNewPageColumn(true);
		detailSection.setNewPageColumn(true);
		columnFooter.setNewPageColumn(true);
		pageFooter.setNewPageColumn(true);
		lastPageFooter.setNewPageColumn(true);
		summary.setNewPageColumn(true);
		noData.setNewPageColumn(true);

		if (groups != null && groups.length > 0)
		{
			for(int i = 0; i < groups.length; i++)
			{
				((JRFillSection)groups[i].getGroupHeaderSection()).setNewPageColumn(true);
				((JRFillSection)groups[i].getGroupFooterSection()).setNewPageColumn(true);
			}
		}
	}


	/**
	 *
	 */
	private void setNewGroupInBands(JRGroup group)
	{
		title.setNewGroup(group, true);
		pageHeader.setNewGroup(group, true);
		columnHeader.setNewGroup(group, true);
		detailSection.setNewGroup(group, true);
		columnFooter.setNewGroup(group, true);
		pageFooter.setNewGroup(group, true);
		lastPageFooter.setNewGroup(group, true);
		summary.setNewGroup(group, true);
		noData.setNewGroup(group, true);

		if (groups != null && groups.length > 0)
		{
			for(int i = 0; i < groups.length; i++)
			{
				((JRFillSection)groups[i].getGroupHeaderSection()).setNewGroup(group, true);
				((JRFillSection)groups[i].getGroupFooterSection()).setNewGroup(group, true);
			}
		}
	}


	/**
	 *
	 */
	private JRFillBand getCurrentPageFooter()
	{
		return isLastPageFooter ? lastPageFooter : pageFooter;
	}


	/**
	 *
	 */
	private void setLastPageFooter(boolean isLastPageFooter)
	{
		this.isLastPageFooter = isLastPageFooter;

		if (isLastPageFooter)
		{
			columnFooterOffsetY = lastPageColumnFooterOffsetY;
		}
	}

	
	/**
	 *
	 */
	@continuable
	private void fillNoData() throws JRException
	{
		if (log.isDebugEnabled() && !noData.isEmpty())
		{
			log.debug("Fill " + fillerId + ": noData at " + offsetY);
		}

		noData.evaluatePrintWhenExpression(JRExpression.EVALUATION_DEFAULT);

		if (noData.isToPrint())
		{
			while (noData.getBreakHeight() > pageHeight - bottomMargin - offsetY)
			{
				addPage(false);
			}

			noData.evaluate(JRExpression.EVALUATION_DEFAULT);

			JRPrintBand printBand = noData.fill(pageHeight - bottomMargin - offsetY);

			if (noData.willOverflow() && noData.isSplitPrevented() && !noData.isSplitTypePreventInhibited())
			{
				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, false);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();
				
				addPage(false);
				
				printBand = 
					noData.refill(
						JRExpression.EVALUATION_DEFAULT,
						pageHeight - bottomMargin - offsetY
						);
			}

			fillBand(printBand);
			offsetY += printBand.getHeight();
			isCrtRecordOnPage = true;
			isCrtRecordOnColumn = true;

			while (noData.willOverflow())
			{
				resolveGroupBoundElements(JRExpression.EVALUATION_DEFAULT, false);
				resolveColumnBoundElements(JRExpression.EVALUATION_DEFAULT);
				resolvePageBoundElements(JRExpression.EVALUATION_DEFAULT);
				scriptlet.callBeforePageInit();
				calculator.initializeVariables(ResetTypeEnum.PAGE, IncrementTypeEnum.PAGE);
				scriptlet.callAfterPageInit();
				
				addPage(false);
				
				printBand = noData.fill(pageHeight - bottomMargin - offsetY);
				
				fillBand(printBand);
				offsetY += printBand.getHeight();
				isCrtRecordOnPage = true;
				isCrtRecordOnColumn = true;
			}

			resolveBandBoundElements(noData, JRExpression.EVALUATION_DEFAULT);
		}
	}

	
	/**
	 *
	 */
	private void setOffsetX()
	{
		if (columnDirection == RunDirectionEnum.RTL)
		{
			offsetX = pageWidth - rightMargin - columnWidth - columnIndex * (columnSpacing + columnWidth);
		}
		else
		{
			offsetX = leftMargin + columnIndex * (columnSpacing + columnWidth);
		}
	}

	
}
