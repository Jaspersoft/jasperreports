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

/*
 * Contributors:
 * Greg Hilton 
 */

package net.sf.jasperreports.engine.export;

import static java.lang.Math.max;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_COLUMN_WIDTH_RATIO;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_DEFINED_NAMES_PREFIX;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_FIRST_PAGE_NUMBER;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_IGNORE_CELL_BACKGROUND;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_IGNORE_CELL_BORDER;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PAGE_SCALE;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_FOOTER_MARGIN;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_HEADER_MARGIN;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_PAGE_BOTTOM_MARGIN;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_PAGE_HEIGHT;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_PAGE_LEFT_MARGIN;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_PAGE_RIGHT_MARGIN;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_PAGE_TOP_MARGIN;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_PRINT_PAGE_WIDTH;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHEET_FOOTER_CENTER;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHEET_FOOTER_LEFT;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHEET_FOOTER_RIGHT;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHEET_HEADER_CENTER;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHEET_HEADER_LEFT;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHEET_HEADER_RIGHT;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHEET_TAB_COLOR;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_SHOW_GRIDLINES;
import static net.sf.jasperreports.export.XlsReportConfiguration.PROPERTY_WHITE_PAGE_BACKGROUND;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.type.CellEdgeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRXlsAbstractExporterNature extends AbstractExporterNature
{
	protected boolean isIgnoreGraphics;
	protected boolean isIgnorePageMargins;

	private final float averageCharWidthFactor;

	/**
	 * 
	 */
	protected JRXlsAbstractExporterNature(
		JasperReportsContext jasperReportsContext,
		ExporterFilter filter, 
		boolean isIgnoreGraphics,
		boolean isIgnorePageMargins
		)
	{
		super(jasperReportsContext, filter);
		this.isIgnoreGraphics = isIgnoreGraphics;
		this.isIgnorePageMargins = isIgnorePageMargins;
		
		this.averageCharWidthFactor = getPropertiesUtil().getFloatProperty(ExcelAbstractExporter.PROPERTY_AVERAGE_CHAR_WIDTH_FACTOR);
	}
	
	@Override
	public boolean isToExport(JRPrintElement element)
	{
		return 
			(!isIgnoreGraphics || (element instanceof JRPrintText) || (element instanceof JRPrintFrame) || (element instanceof JRGenericPrintElement))
			&& (filter == null || filter.isToExport(element));
	}
	
	@Override
	public boolean isDeep(JRPrintFrame frame)
	{
		return true;
	}

	@Override
	public boolean isSpanCells()
	{
		return true;
	}
	
	@Override
	public boolean isIgnoreLastRow()
	{
		return false;
	}
	
	@Override
	public boolean isHorizontallyMergeEmptyCells()
	{
		return false;
	}

	/**
	 * Specifies whether empty page margins should be ignored
	 */
	@Override
	public boolean isIgnorePageMargins()
	{
		return isIgnorePageMargins;
	}
	
	@Override
	public boolean isBreakBeforeRow(JRPrintElement element)
	{
		return element.hasProperties() 
				&& JRPropertiesUtil.asBoolean(element.getPropertiesMap().getProperty(ExcelAbstractExporter.PROPERTY_BREAK_BEFORE_ROW));
	}
	
	@Override
	public boolean isBreakAfterRow(JRPrintElement element)
	{
		return element.hasProperties()
				&& JRPropertiesUtil.asBoolean(element.getPropertiesMap().getProperty(ExcelAbstractExporter.PROPERTY_BREAK_AFTER_ROW));
	}
	
	/**
	 *
	 */
	public Boolean getRowAutoFit(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW, false);
		}
		return null;
	}
	
	/**
	 *
	 */
	public Boolean getIgnoreRowHeight(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(ExcelAbstractExporter.PROPERTY_IGNORE_ROW_HEIGHT)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, ExcelAbstractExporter.PROPERTY_IGNORE_ROW_HEIGHT, false);
		}
		return null;
	}
	
	/**
	 *
	 */
	public Boolean getColumnAutoFit(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_COLUMN)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, ExcelAbstractExporter.PROPERTY_AUTO_FIT_COLUMN, false);
		}
		return null;
	}
	
	/**
	 *
	 */
	public Boolean getShowGridlines(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_SHOW_GRIDLINES)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, PROPERTY_SHOW_GRIDLINES, true);
		}
		return null;
	}
	
	public Boolean getIgnoreCellBackground(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_IGNORE_CELL_BACKGROUND)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, PROPERTY_IGNORE_CELL_BACKGROUND, false);
		}
		return null;
	}
	
	public Boolean getIgnoreCellBorder(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_IGNORE_CELL_BORDER)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, PROPERTY_IGNORE_CELL_BORDER, false);
		}
		return null;
	}

	public Boolean getWhitePageBackground(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_WHITE_PAGE_BACKGROUND)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getBooleanProperty(element, PROPERTY_WHITE_PAGE_BACKGROUND, false);
		}
		return null;
	}
	
	public Integer getColumnWidth(JRPrintElement element, boolean columnAutoFit) 
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(ExcelAbstractExporter.PROPERTY_COLUMN_WIDTH)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, ExcelAbstractExporter.PROPERTY_COLUMN_WIDTH, 0);
		}
		else if (columnAutoFit)
		{
			if (element instanceof JRPrintText)
			{
				JRPrintText textElement = (JRPrintText)element;
				//Integer truncateIndex = textElement.getTextTruncateIndex();
				//if (truncateIndex != null) // commented this out this test because the old POI exporter was performing autofit for all cases anyway;
				{
					Float averageCharWidth = textElement.getAverageCharWidth();
					if (averageCharWidth != null && averageCharWidth > 0)
					{
						int maxLineLength = 0;
						StringTokenizer tkzer =  new StringTokenizer(textElement.getFullText(), "\n");
						while (tkzer.hasMoreTokens())
						{
							String token = tkzer.nextToken();
							maxLineLength = Math.max(maxLineLength, token.length());
						}
						return (int)(maxLineLength * averageCharWidth * averageCharWidthFactor);
					}
				}
			}
		}
		return null;
	}
	
	public Float getColumnWidthRatio(JRPrintElement element) 
	{
		if (element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_COLUMN_WIDTH_RATIO)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getFloatProperty(element, PROPERTY_COLUMN_WIDTH_RATIO, 0f);
		}
		return null;
	}

	public List<PropertySuffix> getRowLevelSuffixes(JRPrintElement element)
	{
		if (element.hasProperties())
		{
			return JRPropertiesUtil.getProperties(element,ExcelAbstractExporter.PROPERTY_ROW_OUTLINE_LEVEL_PREFIX);
		}
		return null;
		
	}
	
	public String getSheetName(JRPrintElement element)
	{
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(ExcelAbstractExporter.PROPERTY_SHEET_NAME)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getProperty(element, ExcelAbstractExporter.PROPERTY_SHEET_NAME);
		}
		return null;
	}
	
	public CellEdgeEnum getFreezeRowEdge(JRPrintElement element)
	{
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(ExcelAbstractExporter.PROPERTY_FREEZE_ROW_EDGE)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return CellEdgeEnum.getByName(getPropertiesUtil().getProperty(element, ExcelAbstractExporter.PROPERTY_FREEZE_ROW_EDGE));
		}
		return null;
	}
	
	public CellEdgeEnum getFreezeColumnEdge(JRPrintElement element)
	{
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(ExcelAbstractExporter.PROPERTY_FREEZE_COLUMN_EDGE)
				)
			{
				// we make this test to avoid reaching the global default value of the property directly
				// and thus skipping the report level one, if present
				return CellEdgeEnum.getByName(getPropertiesUtil().getProperty(element, ExcelAbstractExporter.PROPERTY_FREEZE_COLUMN_EDGE));
			}
			return null;
	}
	
	public String getSheetTabColor(JRPrintElement element)
	{
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_SHEET_TAB_COLOR)
				)
			{
				// we make this test to avoid reaching the global default value of the property directly
				// and thus skipping the report level one, if present
				return getPropertiesUtil().getProperty(element, PROPERTY_SHEET_TAB_COLOR);
			}
			return null;
	}
	
	public Integer getPageScale(JRPrintElement element)
	{
		if (
			element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_PAGE_SCALE)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PAGE_SCALE, 0);
		}
		return null;
	}

	public Integer getFirstPageNumber(JRPrintElement element)
	{
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_FIRST_PAGE_NUMBER)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_FIRST_PAGE_NUMBER, 0);
		}
		return null;
	}
	
	public PropertySuffix[] getDefinedNames(JRPrintElement element)
	{
		if (element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_DEFINED_NAMES_PREFIX)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			List<PropertySuffix> propertySuffixes = getPropertiesUtil().getProperties(PROPERTY_DEFINED_NAMES_PREFIX);
			if (propertySuffixes != null && !propertySuffixes.isEmpty())
			{
				return propertySuffixes.toArray(new PropertySuffix[propertySuffixes.size()]);
			}
		}
		return null;
	}

	@Override
	public void setXProperties(CutsInfo xCuts, JRPrintElement element, int row1, int col1, int row2, int col2)
	{
		Map<String,Object> xCutsProperties = xCuts.getPropertiesMap();
		setXProperties(xCutsProperties, element);
		
		if (!element.hasProperties())
		{
			//quick exit for performance
			return;
		}
		
		Cut cut = xCuts.getCut(col1);
		
		Boolean columnAutoFit = getColumnAutoFit(element);
		if (columnAutoFit != null)
		{
			if(!cut.hasProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_COLUMN))
			{
				cut.setProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_COLUMN, columnAutoFit);
			}
			else
			{
				cut.setProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_COLUMN, (Boolean)cut.getProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_COLUMN) && columnAutoFit);
			}
		}

		Integer columnWidth = getColumnWidth(element, Boolean.TRUE.equals(columnAutoFit));
		if (columnWidth != null)
		{
			int existingColSpanWidth = 0;
			for (int colIndex = col1; colIndex < col2; colIndex++)
			{
				Cut lcCut = xCuts.getCut(colIndex);
				Integer cutColumnWidth = (Integer)lcCut.getProperty(ExcelAbstractExporter.PROPERTY_COLUMN_WIDTH);
				cutColumnWidth =
					cutColumnWidth == null 
					? xCuts.getCutOffset(colIndex + 1) - xCuts.getCutOffset(colIndex)  
					: cutColumnWidth;
				existingColSpanWidth += cutColumnWidth; 
			}
			
			if (columnWidth > existingColSpanWidth)
			{
				int columnWidthIncrease = (columnWidth - existingColSpanWidth) / (col2 - col1);
				for (int colIndex = col1; colIndex < col2; colIndex++)
				{
					Cut lcCut = xCuts.getCut(colIndex);
					Integer cutColumnWidth = (Integer)lcCut.getProperty(ExcelAbstractExporter.PROPERTY_COLUMN_WIDTH);
					cutColumnWidth =
						cutColumnWidth == null 
						? xCuts.getCutOffset(colIndex + 1) - xCuts.getCutOffset(colIndex)  
						: cutColumnWidth;
					lcCut.setProperty(ExcelAbstractExporter.PROPERTY_COLUMN_WIDTH, cutColumnWidth + columnWidthIncrease);
				}
			}
		}
	}
	
	@Override
	public void setXProperties(Map<String,Object> xCutsProperties, JRPrintElement element)
	{
	}
	
	private void setMargin(Integer marginValue, Cut cut, String marginName)
	{
		if(
			marginValue != null && (!cut.hasProperty(marginName) || (Integer)cut.getProperty(marginName) < marginValue)
			)
		{
			// a margin value cannot be negative
			cut.setProperty(marginName, max(marginValue,0));
		}
	}
	
	private void setHeaderFooter(String headerFooterValue, Cut cut, String headerFooterName)
	{
		if(headerFooterValue != null && headerFooterValue.trim().length() > 0)
		{
			cut.setProperty(headerFooterName, headerFooterValue);
		}
	}
	
	@Override
	public void setYProperties(CutsInfo yCuts, JRPrintElement element, int row1, int col1, int row2, int col2)
	{
		Map<String,Object> yCutsProperties = yCuts.getPropertiesMap();
		setYProperties(yCutsProperties, element);
		
		if (!element.hasProperties())
		{
			//quick exit for performance
			return;
		}
		
		Cut cut = yCuts.getCut(row1);
		
		Boolean rowAutoFit = getRowAutoFit(element);
		if (rowAutoFit != null)
		{
			if(!cut.hasProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW))
			{
				cut.setProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW, rowAutoFit);
			}
			else
			{
				cut.setProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW, (Boolean)cut.getProperty(ExcelAbstractExporter.PROPERTY_AUTO_FIT_ROW) && rowAutoFit);
			}
		}

		List<PropertySuffix> rowLevelSuffixes = getRowLevelSuffixes(element);
		if(rowLevelSuffixes != null && !rowLevelSuffixes.isEmpty())
		{
			SortedMap<String, Boolean> levelMap = new TreeMap<>();
			for(PropertySuffix suffix : rowLevelSuffixes)
			{
				String level = suffix.getSuffix();
				String marker = suffix.getValue();
				
				levelMap.put(level, "end".equalsIgnoreCase(marker));
			}
			
// FIXMEXLS we should preserve existing outline level information in the current y cut
//			SortedMap<String, Boolean> oldLevelMap = (SortedMap<String, Boolean>)cut.getProperty(ExcelAbstractExporter.PROPERTY_ROW_OUTLINE_LEVEL_PREFIX);
//			if (oldLevelMap != null)
//			{
//				oldLevelMap.putAll(levelMap);
//				levelMap = oldLevelMap;
//			}
			cut.setProperty(ExcelAbstractExporter.PROPERTY_ROW_OUTLINE_LEVEL_PREFIX, levelMap);
		}
		
		String sheetName = getSheetName(element);
		if(sheetName != null)
		{
			cut.setProperty(ExcelAbstractExporter.PROPERTY_SHEET_NAME, sheetName);
		}

		String tabColor = getSheetTabColor(element);
		if(tabColor != null)
		{
			cut.setProperty(PROPERTY_SHEET_TAB_COLOR, tabColor);
		}

		Integer pageScale = getPageScale(element);
		if(pageScale != null && pageScale > 9 && pageScale < 401)
		{
			cut.setProperty(PROPERTY_PAGE_SCALE, pageScale);
		}
		
		Integer firstPageNumber = getFirstPageNumber(element);
		if(firstPageNumber != null)
		{
			cut.setProperty(PROPERTY_FIRST_PAGE_NUMBER, firstPageNumber);
		}
		
		Boolean showGridlines = getShowGridlines(element);
		if(showGridlines != null)
		{
			cut.setProperty(PROPERTY_SHOW_GRIDLINES, showGridlines);
		}
		
		Boolean ignoreCellBackground = getIgnoreCellBackground(element);
		if(ignoreCellBackground != null)
		{
			cut.setProperty(PROPERTY_IGNORE_CELL_BACKGROUND, ignoreCellBackground);
		}
		
		Boolean ignoreCellBorder = getIgnoreCellBorder(element);
		if(ignoreCellBorder != null)
		{
			cut.setProperty(PROPERTY_IGNORE_CELL_BORDER, ignoreCellBorder);
		}

		Boolean whitePageBackground = getWhitePageBackground(element);
		if(whitePageBackground != null)
		{
			cut.setProperty(PROPERTY_WHITE_PAGE_BACKGROUND, whitePageBackground);
		}

		CellEdgeEnum freezeColumnEdge = getFreezeColumnEdge(element);
		int columnFreezeIndex = freezeColumnEdge == null 
				? 0
				: (CellEdgeEnum.RIGHT == freezeColumnEdge 
					? col2
					: col1
					);
		if(columnFreezeIndex > 0)
		{
			cut.setProperty(ExcelAbstractExporter.PROPERTY_FREEZE_COLUMN_EDGE, columnFreezeIndex);
		}
		
		CellEdgeEnum freezeRowEdge = getFreezeRowEdge(element);
		int rowFreezeIndex = freezeRowEdge == null 
			? 0
			: (CellEdgeEnum.BOTTOM == freezeRowEdge 
					? row2
					: row1
					);
		if(rowFreezeIndex > 0)
		{
			cut.setProperty(ExcelAbstractExporter.PROPERTY_FREEZE_ROW_EDGE, rowFreezeIndex);
		}
		
		Float columnWidthRatio = getColumnWidthRatio(element);
		// only positive  values are allowed
		if(columnWidthRatio != null && columnWidthRatio > 0f)
		{
			cut.setProperty(PROPERTY_COLUMN_WIDTH_RATIO, columnWidthRatio);
		}
		
		Integer printPageHeight = getPrintPageHeight(element);
		// only positive  values are allowed
		if(printPageHeight != null && printPageHeight > 0 
			&& (!cut.hasProperty(PROPERTY_PRINT_PAGE_HEIGHT) || (Integer)cut.getProperty(PROPERTY_PRINT_PAGE_HEIGHT) < printPageHeight))
		{
			cut.setProperty(PROPERTY_PRINT_PAGE_HEIGHT, printPageHeight);
		}
		
		Integer printPageWidth = getPrintPageWidth(element);
		// only positive  values are allowed
		if(printPageWidth != null && printPageWidth > 0 
				&& (!cut.hasProperty(PROPERTY_PRINT_PAGE_WIDTH) || (Integer)cut.getProperty(PROPERTY_PRINT_PAGE_WIDTH) < printPageWidth))
		{
			cut.setProperty(PROPERTY_PRINT_PAGE_WIDTH, printPageWidth);
		}

		setMargin(getPrintPageTopMargin(element), cut, PROPERTY_PRINT_PAGE_TOP_MARGIN);
		setMargin(getPrintPageLeftMargin(element), cut, PROPERTY_PRINT_PAGE_LEFT_MARGIN);
		setMargin(getPrintPageBottomMargin(element), cut, PROPERTY_PRINT_PAGE_BOTTOM_MARGIN);
		setMargin(getPrintPageRightMargin(element), cut, PROPERTY_PRINT_PAGE_RIGHT_MARGIN);
		setMargin(getPrintHeaderMargin(element), cut, PROPERTY_PRINT_HEADER_MARGIN);
		setMargin(getPrintFooterMargin(element), cut, PROPERTY_PRINT_FOOTER_MARGIN);
		
		setHeaderFooter(getSheetHeaderLeft(element), cut, PROPERTY_SHEET_HEADER_LEFT);
		setHeaderFooter(getSheetHeaderCenter(element), cut, PROPERTY_SHEET_HEADER_CENTER);
		setHeaderFooter(getSheetHeaderRight(element), cut, PROPERTY_SHEET_HEADER_RIGHT);
		setHeaderFooter(getSheetFooterLeft(element), cut, PROPERTY_SHEET_FOOTER_LEFT);
		setHeaderFooter(getSheetFooterCenter(element), cut, PROPERTY_SHEET_FOOTER_CENTER);
		setHeaderFooter(getSheetFooterRight(element), cut, PROPERTY_SHEET_FOOTER_RIGHT);	
	}
	
	@Override
	public void setYProperties(Map<String,Object> yCutsProperties, JRPrintElement element)
	{
		// nothing to do here
	}
	
	public Integer getPrintPageTopMargin(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_PAGE_TOP_MARGIN)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_PAGE_TOP_MARGIN, 0);
		}
		return null;
	}
	
	public Integer getPrintPageLeftMargin(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_PAGE_LEFT_MARGIN)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_PAGE_LEFT_MARGIN, 0);
		}
		return null;
	}
	
	public Integer getPrintPageBottomMargin(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_PAGE_BOTTOM_MARGIN)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_PAGE_BOTTOM_MARGIN, 0);
		}
		return null;
	}
	
	public Integer getPrintPageRightMargin(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_PAGE_RIGHT_MARGIN)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_PAGE_RIGHT_MARGIN, 0);
		}
		return null;
	}
	
	public Integer getPrintPageHeight(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_PAGE_HEIGHT)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_PAGE_HEIGHT);
		}
		return null;
	}
	
	public Integer getPrintPageWidth(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_PAGE_WIDTH)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_PAGE_WIDTH);
		}
		return null;
	}
	
	public Integer getPrintHeaderMargin(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_HEADER_MARGIN)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_HEADER_MARGIN, 0);
		}
		return null;
	}
	
	public Integer getPrintFooterMargin(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_PRINT_FOOTER_MARGIN)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getIntegerProperty(element, PROPERTY_PRINT_FOOTER_MARGIN, 0);
		}
		return null;
	}
	
	public String getSheetHeaderLeft(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_SHEET_HEADER_LEFT)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getProperty(element, PROPERTY_SHEET_HEADER_LEFT);
		}
		return null;
	}
	
	public String getSheetHeaderCenter(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_SHEET_HEADER_CENTER)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getProperty(element, PROPERTY_SHEET_HEADER_CENTER);
		}
		return null;
	}
	
	public String getSheetHeaderRight(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_SHEET_HEADER_RIGHT)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getProperty(element, PROPERTY_SHEET_HEADER_RIGHT);
		}
		return null;
	}
	
	public String getSheetFooterLeft(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_SHEET_FOOTER_LEFT)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getProperty(element, PROPERTY_SHEET_FOOTER_LEFT);
		}
		return null;
	}
	
	public String getSheetFooterCenter(JRPrintElement element) {
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(PROPERTY_SHEET_FOOTER_CENTER)
				)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getProperty(element, PROPERTY_SHEET_FOOTER_CENTER);
		}
		return null;
	}
	
	public String getSheetFooterRight(JRPrintElement element) {
		if (element.hasProperties()
			&& element.getPropertiesMap().containsProperty(PROPERTY_SHEET_FOOTER_RIGHT)
			)
		{
			// we make this test to avoid reaching the global default value of the property directly
			// and thus skipping the report level one, if present
			return getPropertiesUtil().getProperty(element, PROPERTY_SHEET_FOOTER_RIGHT);
		}
		return null;
	}

}
