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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.export.type.CellEdgeEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.XlsMetadataExporterConfiguration;
import net.sf.jasperreports.export.XlsMetadataReportConfiguration;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public abstract class JRXlsAbstractMetadataExporter<RC extends XlsMetadataReportConfiguration, C extends XlsMetadataExporterConfiguration, E extends JRExporterContext> 
	extends JRXlsAbstractExporter<RC, C, E>
{
	/**
	 * 
	 */
	protected List<String> columnNames;
	protected Map<String, Integer> columnNamesMap;
	protected int rowIndex;
	protected boolean hasDefinedColumns;
	protected Map<String, Object> currentRow;	
	protected Map<String, Object> repeatedValues;	

	/**
	 * @see #JRXlsAbstractMetadataExporter(JasperReportsContext)
	 */
	public JRXlsAbstractMetadataExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JRXlsAbstractMetadataExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		maxColumnIndex = 255;
	}


	@Override
	protected void initExport() 
	{
		super.initExport();
		
		currentRow = new HashMap<>();//FIXMEEXPORT check these two
		repeatedValues = new HashMap<>();
		onePagePerSheetMap.clear();
		sheetsBeforeCurrentReport = 0;
		sheetsBeforeCurrentReportMap.clear();
	}


	@Override
	protected void initReport() 
	{
		super.initReport();

		setColumnNames();
	}

	
	/**
	 * 
	 */
	protected void setColumnNames()
	{
		String[] columnNamesArray = getCurrentItemConfiguration().getColumnNames();
		
		hasDefinedColumns = (columnNamesArray != null && columnNamesArray.length > 0);

		columnNames = new ArrayList<>();
		columnNamesMap = new HashMap<>();

		List<String> columnNamesList = JRStringUtil.split(columnNamesArray, ",");
		if (columnNamesList != null)
		{
			for (String columnName : columnNamesList)
			{
				if (!columnNamesMap.containsKey(columnName))
				{
					columnNames.add(columnName);
					columnNamesMap.put(columnName, columnNames.size());
				}
			}
		}
	}
	
	@Override
	protected void exportReportToStream(OutputStream os) throws JRException, IOException
	{
		openWorkbook(os);
		sheetNamesMap = new HashMap<String,Integer>();
		definedNamesMap = new HashMap<NameScope, String>();
		boolean pageExported = false;

		List<ExporterInputItem> items = exporterInput.getItems();

		for(reportIndex = 0; reportIndex < items.size(); reportIndex++)
		{
			ExporterInputItem item = items.get(reportIndex);

			setCurrentExporterInputItem(item);
			
			defaultFont = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());

			if(!hasGlobalSheetNames())
			{
				sheetNamesIndex = 0;
			}

			XlsMetadataReportConfiguration configuration = getCurrentItemConfiguration();
			configureDefinedNames(configuration.getDefinedNames());

			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				PageRange pageRange = getPageRange();
				int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
				int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

				if (onePagePerSheet)
				{

					for(pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
					{
						checkInterrupted();

						JRPrintPage page = pages.get(pageIndex);

						pageFormat = jasperPrint.getPageFormat(pageIndex);

						SheetInfo sheetInfo = getSheetInfo(configuration, null);
						createSheet(sheetInfo);

						// we need to count all sheets generated for all exported documents
						sheetIndex++;
						sheetNamesIndex++;
						rowIndex = 0;
						resetAutoFilters();
						
						setFreezePane(sheetInfo.rowFreezeIndex, sheetInfo.columnFreezeIndex);
						
						/*   */
						exportPage(page);
						pageExported = true;
					}
				}
				else
				{
					pageFormat = jasperPrint.getPageFormat(startPageIndex);
					
					// Create the sheet before looping.
					SheetInfo sheetInfo = getSheetInfo(configuration, jasperPrint.getName());
					createSheet(sheetInfo);

					// we need to count all sheets generated for all exported documents
					sheetIndex++;
					sheetNamesIndex++;
					resetAutoFilters();
					
					setFreezePane(sheetInfo.rowFreezeIndex, sheetInfo.columnFreezeIndex);
					
					if (filter instanceof ResetableExporterFilter)
					{
						((ResetableExporterFilter)filter).reset();
					}
					for(pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
					{
						checkInterrupted();
						JRPrintPage page = pages.get(pageIndex);
						pageFormat = jasperPrint.getPageFormat(pageIndex);
						exportPage(page);
						pageExported = true;
					}
					
				}
			}

			if(reportIndex == items.size() -1 && !pageExported)
			{
				exportEmptyReport();
			}
			
			sheetsBeforeCurrentReport = onePagePerSheet ? sheetIndex : sheetsBeforeCurrentReport + 1;
		}

		closeWorkbook(os);
	}

	/**
	 * 
	 */
	protected int exportPage(JRPrintPage page) throws JRException
	{
		XlsMetadataReportConfiguration configuration = getCurrentItemConfiguration();
		
		List<JRPrintElement> elements = page.getElements();
		currentRow = new HashMap<>();
		rowIndex += configuration.isWriteHeader() ? 1 : 0;
		
		for (int i = 0; i < elements.size(); ++i) 
		{
			JRPrintElement element = elements.get(i);
			updateSheet(element);
			
			String sheetName = element.getPropertiesMap().getProperty(PROPERTY_SHEET_NAME);
			if(sheetName != null)
			{
				setSheetName(sheetName);
			}
			
			if (element instanceof JRPrintLine)
			{
				exportLine((JRPrintLine)element);
			}
			else if (element instanceof JRPrintRectangle)
			{
				exportRectangle((JRPrintRectangle)element);
			}
			else if (element instanceof JRPrintEllipse)
			{
				exportRectangle((JRPrintEllipse)element);
			}
			else if (element instanceof JRPrintImage)
			{
				exportImage((JRPrintImage) element);
			}
			else if (element instanceof JRPrintText)
			{
				exportText((JRPrintText)element);
			}
			else if (element instanceof JRPrintFrame)
			{
				exportFrame((JRPrintFrame) element);
			}
			else if (element instanceof JRGenericPrintElement)
			{
				exportGenericElement((JRGenericPrintElement) element);
			}
			
			String currentColumnName = element.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
			
			String rowFreeze = getPropertiesUtil().getProperty(element, PROPERTY_FREEZE_ROW_EDGE);
			
			int rowFreezeIndex = rowFreeze == null 
				? -1 
				: (CellEdgeEnum.BOTTOM.getName().equals(rowFreeze) 
						? rowIndex + 1
						: rowIndex
						);
			
			String columnFreeze = getPropertiesUtil().getProperty(element, PROPERTY_FREEZE_COLUMN_EDGE);
				
			int columnFreezeIndex = columnFreeze == null 
				? -1 
				: (CellEdgeEnum.RIGHT.getName().equals(columnFreeze) 
						? columnNamesMap.get(currentColumnName) + 1
						: columnNamesMap.get(currentColumnName)
						);

			if(rowFreezeIndex > 0 || columnFreezeIndex > 0)
			{
				setFreezePane(rowFreezeIndex, columnFreezeIndex);
			}
			
		}
		if(columnNames.size() > maxColumnIndex+1)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_COLUMN_INDEX_BEYOND_LIMIT, 
					new Object[]{columnNames.size(), maxColumnIndex+1});
			
		}
		// write last row
		if (columnNames.size() > 0)
		{
			if(rowIndex == 1 && getCurrentItemConfiguration().isWriteHeader()) {
				writeReportHeader();
			}
			writeCurrentRow(currentRow, repeatedValues);
		}

		if(autoFilterStart != null)
		{
			setAutoFilter(autoFilterStart + ":" + (autoFilterEnd != null ? autoFilterEnd : autoFilterStart));
		}
		else if(autoFilterEnd != null)
		{
			setAutoFilter(autoFilterEnd + ":" + autoFilterEnd);
		}

		setRowLevels(null, null);
		
		JRExportProgressMonitor progressMonitor = configuration.getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
		
		return 0;
	}
	
	
	@Override
	protected JRStyledText getStyledText(JRPrintText textElement)
	{
		return textElement.getFullStyledText(noneSelector);
	}

	@Override
	protected int getImageBorderCorrection(JRPen pen)
	{
		float lineWidth = pen.getLineWidth() == null ? 0 : pen.getLineWidth();
		
		if (lineWidth > 0f)
		{
			if (lineWidth >= 2f)
			{
				return 2;
			}

			return 1;
		}
		
		return imageBorderFixEnabled ? 1 : 0;
	}

	
	/**
	 *
	 */
	@Override
	protected String getSheetName(String sheetName)
	{
		if (sheetNames != null && sheetNamesIndex < sheetNames.length)
		{
			sheetName = sheetNames[sheetNamesIndex];
		}
		
		if (sheetName == null)
		{
			// no sheet name was specified or if it was null
			return "Page " + (sheetIndex + 1);
		}

		// sheet name specified; assuming it is first occurrence
		int crtIndex = 1;
		String txtIndex = "";

		if(sheetNamesMap.containsKey(sheetName))
		{
			// sheet names must be unique; altering sheet name using number of occurrences
			crtIndex = sheetNamesMap.get(sheetName) + 1;
			txtIndex = String.valueOf(crtIndex);
		}

		sheetNamesMap.put(sheetName, crtIndex);

		String name = sheetName;
		if(txtIndex.length() > 0)
		{
			name += " " + txtIndex;
		}
		
		if (name.length() > 31)
		{
			name = (sheetName + " ").substring(0, 31 - txtIndex.length()) + txtIndex;
		}
		
		return name;
	}

	private SheetInfo getSheetInfo(XlsMetadataReportConfiguration configuration, String name)
	{
		SheetInfo sheetInfo = new SheetInfo();
		sheetInfo.sheetName = getSheetName(name);
		sheetInfo.rowFreezeIndex = configuration.getFreezeRow() == null ? -1 : configuration.getFreezeRow(); 
		sheetInfo.columnFreezeIndex = configuration.getFreezeColumn() == null ? -1 : getColumnIndex(configuration.getFreezeColumn()); 
		sheetInfo.ignoreCellBackground = configuration.isIgnoreCellBackground();
		sheetInfo.ignoreCellBorder = configuration.isIgnoreCellBorder();
		sheetInfo.whitePageBackground = configuration.isWhitePageBackground();
		sheetInfo.sheetFirstPageIndex = pageIndex;
		sheetInfo.sheetFirstPageNumber = configuration.getFirstPageNumber();		
		sheetInfo.sheetPageScale = configuration.getPageScale();		
		sheetInfo.sheetShowGridlines = configuration.isShowGridLines() ;
		sheetInfo.tabColor = configuration.getSheetTabColor();
		sheetInfo.columnWidthRatio = configuration.getColumnWidthRatio();
		return sheetInfo;
	}

	@Override
	protected String getFormula(JRPrintText text)
	{
		String formula = text.getPropertiesMap().getProperty(PROPERTY_CELL_FORMULA);
		if( formula != null)
		{
			formula = formula.trim();
			if(formula.startsWith("="))
			{
				formula = formula.substring(1);
			}
		}
		return formula;
	}
	
	/**
	 * Compares the highest index of the currentRow's columns with the index of the column to be inserted
	 * to determine if the current column is read in the proper order
	 * </p>
	 * @param currentRow
	 * @param currentColumnName
	 */
	protected boolean isColumnReadOnTime(Map<String, Object> currentRow, String currentColumnName)
	{
		int indexOfLastFilledColumn = -1;
		Set<String> currentlyFilledColumns = currentRow.keySet();
		
		for (String column: currentlyFilledColumns)
		{
			indexOfLastFilledColumn = Math.max(indexOfLastFilledColumn, columnNames.indexOf(column));
		}
		
		return indexOfLastFilledColumn < columnNames.indexOf(currentColumnName);
	}
	
	@Override
	protected void exportText(JRPrintText text, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException
	{
	}

	@Override
	public void exportImage(JRPrintImage image, JRExporterGridCell cell, int colIndex, int rowIndex, int emptyCols, int yCutsRow, JRGridLayout layout) throws JRException
	{
	}

	@Override
	protected void exportRectangle(JRPrintGraphicElement element, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException
	{
	}

	@Override
	protected void exportLine(JRPrintLine line, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException
	{
	}

	@Override
	protected void exportFrame(JRPrintFrame frame, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException
	{
	}

	@Override
	protected void exportGenericElement(JRGenericPrintElement element, JRExporterGridCell cell, int colIndex, int rowIndex, int emptyCols, int yCutsRow, JRGridLayout layout) throws JRException
	{
	}
	
	@Override
	protected void addBlankCell(JRExporterGridCell gridCell, int colIndex, int rowIndex) throws JRException
	{
	}

	@Override
	protected void addOccupiedCell(OccupiedGridCell occupiedGridCell, int colIndex, int rowIndex) throws JRException
	{
	}

//	protected void setCell(JRExporterGridCell gridCell, int x, int y)
//	{
//	}
	
	@Override
	protected abstract ExporterNature getNature();

	@Override
	protected abstract void openWorkbook(OutputStream os) throws JRException;

	@Override
	protected void createSheet(CutsInfo xCuts, SheetInfo sheetInfo)
	{
		createSheet(sheetInfo);
	}

	protected abstract void createSheet(SheetInfo sheetInfo);

	@Override
	protected abstract void closeWorkbook(OutputStream os) throws JRException;

	protected void setColumnWidth(int col, int width)
	{
		setColumnWidth(col, width, false);
	}

	@Override
	protected abstract void setColumnWidth(int col, int width, boolean autoFit);

	protected void setRowHeight(int rowIndex, int lastRowHeight) throws JRException
	{
		setRowHeight(rowIndex, lastRowHeight, null, null);
	}

	@Override
	protected abstract void setRowHeight(int rowIndex, int lastRowHeight, Cut yCut, XlsRowLevelInfo levelInfo) throws JRException;

	protected abstract void exportText(JRPrintText textElement) throws JRException;

	public abstract void exportImage(JRPrintImage image) throws JRException;

	protected abstract void exportRectangle(JRPrintGraphicElement element) throws JRException;

	protected abstract void exportLine(JRPrintLine line) throws JRException;

	protected abstract void exportFrame(JRPrintFrame frame) throws JRException;

	protected abstract void exportGenericElement(JRGenericPrintElement element) throws JRException;
	
	protected abstract void writeCurrentRow(Map<String, Object> currentRow, Map<String, Object> repeatedValues)  throws JRException;
	
	protected abstract void writeReportHeader() throws JRException;
	
	protected abstract void updateSheet(JRPrintElement element);

}
