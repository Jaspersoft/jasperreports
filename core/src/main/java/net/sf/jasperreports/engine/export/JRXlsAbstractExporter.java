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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.XlsExporterConfiguration;
import net.sf.jasperreports.export.XlsReportConfiguration;


/**
 * Superclass for the Excel exporters.
 * <h3>Excel Exporters</h3>
 * For generating Excel files, there are currently two different exporter implementations
 * available in JasperReports. The first to appear was the
 * {@link net.sf.jasperreports.poi.export.JRXlsExporter} implementation, which
 * uses the Apache POI library to export documents to the Microsoft Excel 2003 file format (XLS). 
 * <p/>
 * Later on, with the introduction of the Microsoft Excel 2007 file format (XLSX), a new
 * exporter was added to JasperReports to support it. This exporter implementation is the
 * {@link net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter}; it does not rely
 * on any third party library to produce XLSX files.
 * <p/>
 * Because in XLS and XLSX all document content is placed inside cells, the Excel exporters
 * are considered typical grid exporters, and having their known limitations.
 * <h3>Configuring Sheets</h3>
 * An Excel file is structured in multiple sheets, and both exporters can be configured either
 * to put all pages inside the source {@link net.sf.jasperreports.engine.JasperPrint} document 
 * on one sheet (one after the another), or to put each page on a separate sheet in the resulting 
 * Excel file. The choice is made by setting the 
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#isOnePagePerSheet() isOnePagePerSheet()}
 * exporter configuration setting, which is set to <code>Boolean.FALSE</code> by default.
 * <p/>
 * When {@link net.sf.jasperreports.export.XlsReportConfiguration#isOnePagePerSheet() isOnePagePerSheet()} 
 * is set to true, or when you have to execute a batch export to XLS, multiple sheets are created in the worksheet.
 * You can also combine two exporter settings to customize the maximum number of rows per
 * page, and display each page of the report in a separate sheet. To do this, set the number
 * of rows per sheet for {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_MAXIMUM_ROWS_PER_SHEET net.sf.jasperreports.export.xls.max.rows.per.sheet} property and
 * set true for {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_ONE_PAGE_PER_SHEET net.sf.jasperreports.export.xls.one.page.per.sheet} property.
 * <p/>
 * The JasperReports Excel exporters provide a simple but efficient sheet-naming
 * mechanism. They use the {@link net.sf.jasperreports.export.XlsReportConfiguration#getSheetNames() getSheetNames()} 
 * exporter configuration setting to read custom sheet names
 * from the String array passed as value. This exporter setting can hold an array of
 * strings, which are passed as sheet names in order. If no value is supplied for this
 * setting or if the value contains fewer sheet names than actually needed
 * by the final document, then the sheets are named by default <code>Page i</code> (where i represents
 * the one-based sheet index).
 * <p/>
 * Taking into account the sheet name's length limitation in Excel (31 characters), if a sheet
 * name contains more than 31 characters it will be truncated as follows: the name of the
 * sheet will be given by the first 31 characters minus the sheet index length characters of
 * the document's name, followed by the sheet index, so that the entire name has exactly 31
 * characters.
 * <p/>
 * For example, if the second sheet name is TheQuickBrownFoxJumpsOverTheLazyDog
 * (35 chars), it will become TheQuickBrownFoxJumpsOverTheLa2 (the final zyDog gets
 * truncated, and the second sheet index 2 will end the name).
 * The name of the 12345-th sheet will become TheQuickBrownFoxJumpsOverT12345
 * (the final heLazyDog gets truncated, in order to make the exact room for 12345).
 * <p/>
 * Multiple sheet names can be specified in the JRXML file as well, using the
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_SHEET_NAMES_PREFIX net.sf.jasperreports.export.xls.sheet.names.&lt;arbitrary_name&gt;} property 
 * at report level. Add the following to the JRXML file:
 * <pre>
 * &lt;property name="net.sf.jasperreports.export.xls.sheet.names.all" value="Sheet A/Sheet B/Sheet C" /&gt;
 * </pre>
 * Keep in mind the naming order is important, sheets will be named in the same order the names are 
 * provided in this property. 
 * <p/>
 * Sheets can be also named at element level, using the {@link #PROPERTY_SHEET_NAME net.sf.jasperreports.export.xls.sheet.name} 
 * element property. This name will be provided for the sheet the element belongs to.
 * <h3>Flow-Oriented Output</h3>
 * The JasperPrint documents are page-oriented. When they are exported to a single sheet
 * Excel document, all the pages are rendered consecutively. Because all exporters try
 * to adhere as closely as possible to the quality and layout of the source document's
 * <code>Graphics2D</code> or PDF format, the page breaks are visible in Excel format. Sometimes this
 * is not desirable. One way to make page breaks less obvious and the layout more flow-based
 * is to suppress all the remaining blank space between cells on the vertical axis.
 * <p/>
 * When set to <code>Boolean.TRUE</code>, the 
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#isRemoveEmptySpaceBetweenRows() isRemoveEmptySpaceBetweenRows()} 
 * exporter configuration flag ensures that all empty rows on the resulting sheet are collapsed. By
 * default, the exporter preserves all the white space for a precise page layout.
 * The provided /demo/samples/nopagebreak sample shows you how to use this
 * setting when exporting to XLS to produce a more flow-based document layout.
 * <p/>
 * To completely ignore pagination, use the built-in fill-time parameter
 * {@link net.sf.jasperreports.engine.JRParameter#IS_IGNORE_PAGINATION IS_IGNORE_PAGINATION}.
 * You can modify the API to remove the empty space between rows and columns as well.
 * You need to set {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_ROWS net.sf.jasperreports.export.xls.remove.empty.space.between.rows}
 * property and/or {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS net.sf.jasperreports.export.xls.remove.empty.space.between.columns}
 * property to true.
 * <p/>
 * Keep in mind these settings are limited by your report layout. If it is too far away
 * from a grid layout, these options cannot work. On a good grid layout, once you set
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_ROWS net.sf.jasperreports.export.xls.remove.empty.space.between.rows} property and/or
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS net.sf.jasperreports.export.xls.remove.empty.space.between.columns} property to
 * true, the empty spaces are removed.
 * <h3>Cell Types</h3>
 * Inside the proprietary document format that JasperReports uses (represented by the
 * {@link net.sf.jasperreports.engine.JasperPrint} object), all text elements are 
 * considered alphanumeric values. This means that if a numeric text field of type 
 * <code>java.lang.Double</code> is placed in the report template at design time, all 
 * the text elements inside the {@link net.sf.jasperreports.engine.JasperPrint} object 
 * resulting from it will hold <code>java.lang.String</code> values, even though 
 * they are actually numbers. Therefore, in a sense, data type information is lost 
 * during report filling. This is because the main goal of JasperReports is to create 
 * documents for viewing and printing, not necessarily for further data manipulation inside 
 * tools like Excel, where formulas could be added to numeric cells.
 * <p/>
 * However, these resulting text elements found in the generated documents nowadays hold
 * enough data type information (in addition to the alphanumeric content) for the original
 * value of the text element to be re-created, if needed.
 * <p/>
 * Both Excel exporters support the 
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#isDetectCellType() isDetectCellType()} 
 * configuration flag, which forces the recreation
 * of the original cell value in accordance with its declared data type, as specified
 * in the report template. 
 * <p/>
 * Cell type detection is turned off by default.
 * You can have JasperReports automatically detect the cell type by modifying the API. Set the
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_DETECT_CELL_TYPE net.sf.jasperreports.export.xls.detect.cell.type} property to true. When you do this, instead
 * of being prompted by Excel to convert the value manually, the value is automatically
 * converted.
 * <h3>Format Pattern Conversions</h3>
 * It is important to keep in mind that standard Java format patterns are not 
 * completely supported by Microsoft Excel. There are rather few data patterns 
 * that make a perfect match between Java and Excel.
 * <p/>
 * In the case that the Java pattern stored in the generated report does not match any of the
 * supported Excel cell patterns, there is still a way to choose an appropriate Excel format
 * pattern. The solution is to use the 
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#getFormatPatternsMap() getFormatPatternsMap()} 
 * export configuration setting and supply
 * a <code>java.util.Map</code> as value. This map should contain Java format patterns as keys and
 * corresponding Excel format patterns as values.
 * <p/>
 * Another way to adjust the format pattern to Excel-compatible values is to set the 
 * {@link #PROPERTY_CELL_PATTERN net.sf.jasperreports.export.xls.pattern} property at element level.
 * <h3>Font Size Correction</h3>
 * Currently, there is no way to control the line spacing in a spreadsheet cell, which results
 * in the cell text not fitting exactly within the cell boundaries. As a workaround, in order to
 * force the cell text to fit, one can use the 
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#isFontSizeFixEnabled() isFontSizeFixEnabled()} 
 * exporter configuration flag to decrease the font size by one point when generating the cell format.
 * Alternatively, one can use the {@link net.sf.jasperreports.export.XlsReportConfiguration#PROPERTY_FONT_SIZE_FIX_ENABLED net.sf.jasperreports.export.xls.font.size.fix.enabled}
 * property at report level.
 * <h3>Background Color</h3>
 * Empty space found on each page in the source {@link net.sf.jasperreports.engine.JasperPrint} 
 * document normally results in empty cells on the corresponding sheet inside the Excel file. 
 * The background color of these empty cells is specified by the configuration of the Excel 
 * viewer itself. This makes the cells appear transparent. To force the document's background 
 * to be white, set the 
 * {@link net.sf.jasperreports.export.XlsReportConfiguration#isWhitePageBackground() isWhitePageBackground()} 
 * exporter configuration flag to <code>Boolean.TRUE</code>.
 * <h3>Excel Color Palette</h3>
 * In JasperReports, any color can be used for the background or the foreground of a report
 * element. However, when exporting to Excel format, only a limited set of colors is
 * supported, through what is called a <i>color palette</i>.
 * <p/>
 * If the colors used in a report template do not match the colors in the color palette, then
 * the Excel exporter will use a special algorithm to determine the closest matches by
 * comparing the RGB levels. However, the results might not always be as expected. A 
 * possibility to optimize the use of supported colors is to create a custom color palette. This 
 * can be achieved by setting to true the 
 * {@link net.sf.jasperreports.export.XlsExporterConfiguration#isCreateCustomPalette() isCreateCustomPalette()} 
 * export configuration flag. If the flag is set, the nearest not yet modified color from the palette is chosen
 * and modified to exactly match the report color.  If all the colors from the palette
 * are modified (the palette has a fixed size), the nearest color from the palette is
 * chosen for further report colors.
 * <p/>
 * To see other various exporter configuration settings, please consult the 
 * {@link net.sf.jasperreports.export.XlsReportConfiguration} and 
 * {@link net.sf.jasperreports.export.XlsExporterConfiguration} classes.
 * 
 * @see net.sf.jasperreports.export.XlsExporterConfiguration
 * @see net.sf.jasperreports.export.XlsReportConfiguration
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRXlsAbstractExporter<RC extends XlsReportConfiguration, C extends XlsExporterConfiguration, E extends JRExporterContext> 
	extends ExcelAbstractExporter<RC, C, E>
{	

	public static class SheetInfo
	{
		public Integer sheetFirstPageIndex;
		public String sheetName;
		public Integer sheetFirstPageNumber;		
		public Integer sheetPageScale;		
		public Boolean sheetShowGridlines;
		public Color tabColor;
		public Boolean ignoreCellBorder;
		public Boolean ignoreCellBackground;
		public Boolean whitePageBackground;
		public Integer columnFreezeIndex;
		public Integer rowFreezeIndex;
		public Float columnWidthRatio;
		public SheetPrintSettings printSettings;
		
		public class SheetPrintSettings implements PrintPageFormat
		{

			private Integer pageHeight;
			private Integer pageWidth;
			private Integer topMargin;
			private Integer leftMargin;
			private Integer bottomMargin;
			private Integer rightMargin;
			private Integer headerMargin;
			private Integer footerMargin;
			
			private String headerLeft;
			private String headerCenter;
			private String headerRight;
			private String footerLeft;
			private String footerCenter;
			private String footerRight;
			
			private OrientationEnum orientation;
			
			private int dpi = JasperPrint.DEFAULT_REPORT_DPI;
			
			public SheetPrintSettings() 
			{
			}
			
			@Override
			public Integer getPageHeight() 
			{
				return pageHeight;
			}
			
			public void setPageHeight(Integer pageHeight) 
			{
				this.pageHeight = pageHeight;
			}
			
			@Override
			public Integer getPageWidth() 
			{
				return pageWidth;
			}
			
			public void setPageWidth(Integer pageWidth) 
			{
				this.pageWidth = pageWidth;
			}
			
			@Override
			public Integer getTopMargin() 
			{
				return topMargin;
			}

			public void setTopMargin(Integer topMargin) 
			{
				this.topMargin = topMargin;
			}

			@Override
			public Integer getLeftMargin() 
			{
				return leftMargin;
			}

			public void setLeftMargin(Integer leftMargin) 
			{
				this.leftMargin = leftMargin;
			}

			@Override
			public Integer getBottomMargin() 
			{
				return bottomMargin;
			}

			public void setBottomMargin(Integer bottomMargin) 
			{
				this.bottomMargin = bottomMargin;
			}

			@Override
			public Integer getRightMargin() 
			{
				return rightMargin;
			}

			public void setRightMargin(Integer rightMargin) 
			{
				this.rightMargin = rightMargin;
			}

			public Integer getHeaderMargin() 
			{
				return headerMargin;
			}

			public void setHeaderMargin(Integer headerMargin) 
			{
				this.headerMargin = headerMargin;
			}

			public Integer getFooterMargin() 
			{
				return footerMargin;
			}

			public void setFooterMargin(Integer footerMargin) 
			{
				this.footerMargin = footerMargin;
			}

			public String getHeaderLeft() 
			{
				return headerLeft;
			}

			public void setHeaderLeft(String headerLeft) 
			{
				this.headerLeft = headerLeft;
			}

			public String getHeaderCenter() 
			{
				return headerCenter;
			}

			public void setHeaderCenter(String headerCenter) 
			{
				this.headerCenter = headerCenter;
			}

			public String getHeaderRight() 
			{
				return headerRight;
			}

			public void setHeaderRight(String headerRight) 
			{
				this.headerRight = headerRight;
			}

			public String getFooterLeft() 
			{
				return footerLeft;
			}

			public void setFooterLeft(String footerLeft) 
			{
				this.footerLeft = footerLeft;
			}

			public String getFooterCenter() 
			{
				return footerCenter;
			}

			public void setFooterCenter(String footerCenter) 
			{
				this.footerCenter = footerCenter;
			}

			public String getFooterRight() 
			{
				return footerRight;
			}

			public void setFooterRight(String footerRight) 
			{
				this.footerRight = footerRight;
			}

			@Override
			public int getDpi() {
				return dpi;
			}

			public void setDpi(int dpi) {
				this.dpi = dpi;
			}

			@Override
			public OrientationEnum getOrientation() {
				return orientation;
			}

			public void setOrientation(OrientationEnum orientation) {
				this.orientation = orientation;
			}
		}
	}
	
	/**
	 * @see #JRXlsAbstractExporter(JasperReportsContext)
	 */
	public JRXlsAbstractExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	
	/**
	 *
	 */
	public JRXlsAbstractExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}
	
	@Override
	protected void exportReportToStream(OutputStream os) throws JRException, IOException
	{
		openWorkbook(os);
		sheetNamesMap = new HashMap<>();
		definedNamesMap = new HashMap<>();
		pageFormat = null;
		boolean pageExported = false;
		List<ExporterInputItem> items = exporterInput.getItems();

		for (reportIndex = 0; reportIndex < items.size(); reportIndex++)
		{
			ExporterInputItem item = items.get(reportIndex);

			setCurrentExporterInputItem(item);
			
			defaultFont = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
			
			if (!hasGlobalSheetNames())
			{
				sheetNamesIndex = 0;
			}

			XlsReportConfiguration configuration = getCurrentItemConfiguration();
			configureDefinedNames(configuration.getDefinedNames());

			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				PageRange pageRange = getPageRange();
				int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
				int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

				if (onePagePerSheet)
				{
					for (pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
					{
						checkInterrupted();

						JRPrintPage page = pages.get(pageIndex);
						
						pageFormat = jasperPrint.getPageFormat(pageIndex);
						
						/*   */
						exportPage(page, /*xCuts*/null, /*startRow*/0, /*defaultSheetName*/null);
						pageExported = true;
					}
				}
				else
				{
					/*
					 * Make a pass and calculate the X cuts for all pages on this sheet.
					 * The Y cuts can be calculated as each page is exported.
					 */
					CutsInfo xCuts = 
						JRGridLayout.calculateXCuts(
							getNature(), jasperPrint, startPageIndex, endPageIndex,
							configuration.getOffsetX() == null ? 0 : configuration.getOffsetX() 
							);
					
					//clear the filter's internal cache that might have built up
					if (filter instanceof ResetableExporterFilter)
					{
						((ResetableExporterFilter)filter).reset();
					}
					int startRow = 0;

					for (pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
					{
						checkInterrupted();
						JRPrintPage page = pages.get(pageIndex);
						pageFormat = jasperPrint.getPageFormat(pageIndex);
						String singleSheetName = configuration.getSingleSheetName();
						startRow = exportPage(page, xCuts, startRow, singleSheetName == null ? jasperPrint.getName() : singleSheetName);//FIXMEPART
 						pageExported = true;
					}
					//updateColumns(xCuts);
				}
			} 
			
			if (reportIndex == items.size() -1 && !pageExported)
			{
				exportEmptyReport();
			}
			sheetsBeforeCurrentReport = onePagePerSheet ? sheetIndex : sheetsBeforeCurrentReport + 1;
		}
		closeSheet();
		closeWorkbook(os);
	}

	/**
	 *
	 * @return the number of rows added.
	 */
	protected int exportPage(JRPrintPage page, CutsInfo xCuts, int startRow, String defaultSheetName) throws JRException
	{
		XlsReportConfiguration configuration = getCurrentItemConfiguration();
		
		int maxRowsPerSheet = getMaxRowsPerSheet();
		boolean isRemoveEmptySpaceBetweenRows = configuration.isRemoveEmptySpaceBetweenRows();
		boolean isRemoveEmptySpaceBetweenColumns = configuration.isRemoveEmptySpaceBetweenColumns();
		
		JRGridLayout layout =
			new JRGridLayout(
				getNature(),
				page.getElements(),
				pageFormat.getPageWidth(),
				pageFormat.getPageHeight(),
				configuration.getOffsetX() == null ? 0 : configuration.getOffsetX(), 
				configuration.getOffsetY() == null ? 0 : configuration.getOffsetY(),
				xCuts
				);

		Grid grid = layout.getGrid();

		boolean createXCuts = (xCuts == null); 
		
		if (createXCuts) 
		{
			xCuts = layout.getXCuts();
		}

		CutsInfo yCuts = layout.getYCuts();
		
		if (createXCuts || startRow == 0)
		{
			exportSheet(xCuts, yCuts, 0, defaultSheetName);
		}

		XlsRowLevelInfo levelInfo = new XlsRowLevelInfo(); 

		int skippedRows = 0;
		int rowIndex = startRow;
		int rowCount = grid.getRowCount();
		for (int y = 0; y < rowCount; y++)
		{
			Cut yCut = yCuts.getCut(y);

			rowIndex = y - skippedRows + startRow;

			//if number of rows is too large a new sheet is created and populated with remaining rows
			if (
				(maxRowsPerSheet > 0 && rowIndex >= maxRowsPerSheet)
				|| yCut.isBreak() 
				)
			{
				//updateColumns(xCuts);
				
				setRowLevels(levelInfo, null);
				
				exportSheet(xCuts, yCuts, y, defaultSheetName);
				
				startRow = 0;
				rowIndex = 0;
				skippedRows = y;
			}
			
			if (
				yCut.isCutNotEmpty()
				|| ((!isRemoveEmptySpaceBetweenRows || yCut.isCutSpanned())
				&& !collapseRowSpan)
				)
			{
				GridRow gridRow = grid.getRow(y);

				int emptyCellColSpan = 0;
				//int emptyCellWidth = 0;

				mergeAndSetRowLevels(levelInfo, (SortedMap<String, Boolean>)yCut.getProperty(PROPERTY_ROW_OUTLINE_LEVEL_PREFIX), rowIndex);

				setRowHeight(
					rowIndex,
					collapseRowSpan
						?  layout.getMaxRowHeight(y)//FIXME consider putting these in cuts
						: JRGridLayout.getRowHeight(gridRow),
					yCuts.getCut(y),
					levelInfo
					);

				int emptyCols = 0;
				int rowSize = gridRow.size();
				for (int xCutIndex = 0; xCutIndex < rowSize; xCutIndex++)
				{
					Cut xCut = xCuts.getCut(xCutIndex);

					boolean isEmptyCol = !(xCut.isCutNotEmpty() || xCut.isCutSpanned());//FIXMEXLS we could do this only once
					emptyCols += isRemoveEmptySpaceBetweenColumns && isEmptyCol ? 1 : 0;
					
					int colIndex = xCutIndex - emptyCols;
					if (colIndex > maxColumnIndex)
					{
						throw 
							new JRException(
								EXCEPTION_MESSAGE_KEY_COLUMN_INDEX_BEYOND_LIMIT, 
								new Object[]{colIndex, maxColumnIndex});
					}
					
					JRExporterGridCell gridCell = gridRow.get(xCutIndex);

					//setCell(gridCell, colIndex, rowIndex);
					
					if (gridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
					{
						if (emptyCellColSpan > 0)
						{
							//tableHelper.exportEmptyCell(gridCell, emptyCellColSpan);
							emptyCellColSpan = 0;
							//emptyCellWidth = 0;
						}

						addOccupiedCell((OccupiedGridCell)gridCell, colIndex, rowIndex);
					}
					else if (gridCell.getType() == JRExporterGridCell.TYPE_ELEMENT_CELL)
					{
						if (emptyCellColSpan > 0)
						{
//							if (emptyCellColSpan > 1)
//							{
//								//sbuffer.append(" colspan=" + emptyCellColSpan);
//								//sheet.addMergedRegion(new Region(y, (short)(x - emptyCellColSpan - 1), y, (short)(x - 1)));
//							}
							emptyCellColSpan = 0;
							//emptyCellWidth = 0;
						}

						JRPrintElement element = gridCell.getElement();
						
						/* the following code is no more necessary since these properties are stored in SheetInfo via CutsInfo */
						
//						String rowFreeze = getPropertiesUtil().getProperty(element, PROPERTY_FREEZE_ROW_EDGE);
//						
//						int rowFreezeIndex = rowFreeze == null 
//								? 0 
//								: (EdgeEnum.BOTTOM.getName().equals(rowFreeze) 
//										? rowIndex + gridCell.getRowSpan()
//										: rowIndex
//										);
//							
//						String columnFreeze = getPropertiesUtil().getProperty(element, PROPERTY_FREEZE_COLUMN_EDGE);
//							
//						int columnFreezeIndex = columnFreeze == null 
//							? 0
//							: (EdgeEnum.RIGHT.getName().equals(columnFreeze) 
//									? colIndex + gridCell.getColSpan()
//									: colIndex
//									);
//
//						if (rowFreezeIndex > 0 || columnFreezeIndex > 0)
//						{
//							setFreezePane(rowFreezeIndex, columnFreezeIndex, rowFreezeIndex > 0, columnFreezeIndex > 0);
//						}
						
//						String sheetName = element.getPropertiesMap().getProperty(PROPERTY_SHEET_NAME);
//						if (sheetName != null)
//						{
//							setSheetName(sheetName);
//						}


//						boolean start = getPropertiesUtil().getBooleanProperty(element, PROPERTY_AUTO_FILTER_START, false);
//						if (start && rowIndex < MAX_ROW_INDEX)
//						{
//							autoFilterStart = getColumnName(colIndex) + (rowIndex + 1);
//						}
//						
//						boolean end = getPropertiesUtil().getBooleanProperty(element, PROPERTY_AUTO_FILTER_END, false);
//						if (end && rowIndex < MAX_ROW_INDEX)
//						{
//							autoFilterEnd = getColumnName(colIndex) + (rowIndex + 1);
//						}
//						
						String autofilter = getPropertiesUtil().getProperty(element, PROPERTY_AUTO_FILTER);
						if ("Start".equals(autofilter))
						{
							autoFilterStart = "$" + JRStringUtil.getLetterNumeral(colIndex + 1, true) + "$" + (rowIndex + 1);
						}
						else if ("End".equals(autofilter))
						{
							autoFilterEnd = "$" + JRStringUtil.getLetterNumeral(colIndex + 1, true) + "$" + (rowIndex + 1);
						}
						
						configureDefinedNames(getNature(), element);
						
						if (element instanceof JRPrintLine)
						{
							exportLine((JRPrintLine)element, gridCell, colIndex, rowIndex);
						}
						else if (element instanceof JRPrintRectangle)
						{
							exportRectangle((JRPrintRectangle)element, gridCell, colIndex, rowIndex);
						}
						else if (element instanceof JRPrintEllipse)
						{
							exportRectangle((JRPrintEllipse)element, gridCell, colIndex, rowIndex);
						}
						else if (element instanceof JRPrintImage)
						{
							exportImage((JRPrintImage) element, gridCell, colIndex, rowIndex, emptyCols, y, layout);
						}
						else if (element instanceof JRPrintText)
						{
							exportText((JRPrintText)element, gridCell, colIndex, rowIndex);
						}
						else if (element instanceof JRPrintFrame)
						{
							exportFrame((JRPrintFrame) element, gridCell, colIndex, y);//FIXME rowIndex?
						}
						else if (element instanceof JRGenericPrintElement)
						{
							exportGenericElement((JRGenericPrintElement) element, gridCell, colIndex, rowIndex, emptyCols, y, layout);
						}

						//colIndex += gridCell.getColSpan() - 1;
					}
					else
					{
						emptyCellColSpan++;
						//emptyCellWidth += gridCell.getWidth();
						if (!isRemoveEmptySpaceBetweenColumns || !isEmptyCol)
						{
							addBlankCell(gridCell, colIndex, rowIndex);
						}
					}
				}

//				if (emptyCellColSpan > 0)
//				{
//					if (emptyCellColSpan > 1)
//					{
//						//sbuffer.append(" colspan=" + emptyCellColSpan);
//						//sheet.addMergedRegion(new Region(y, (short)x, y, (short)(x + emptyCellColSpan - 1)));
//					}
//				}
				
				//increment row index to return proper value
				++rowIndex;
			}
			else
			{
				skippedRows++;
//				setRowHeight(y, 0);
//
//				for (int x = 0; x < grid[y].length; x++)
//				{
//					addBlankCell(x, y);
//					setCell(x, y);
//				}
			}
		}

		if (configuration.isForcePageBreaks())
		{
			addRowBreak(rowCount - skippedRows + startRow - 1);
		}
		
		if (autoFilterStart != null)
		{
			setAutoFilter(autoFilterStart + ":" + (autoFilterEnd != null ? autoFilterEnd : autoFilterStart));
		}
		else if (autoFilterEnd != null)
		{
			setAutoFilter(autoFilterEnd + ":" + autoFilterEnd);
		}
		
//		if (createXCuts)
//		{
//			updateColumns(xCuts);
//		}
		
		setRowLevels(levelInfo, null);
		
		JRExportProgressMonitor progressMonitor = configuration.getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
		
		// Return the number of rows added
		return rowIndex;
	}
	

	protected JRXlsAbstractExporter.SheetInfo getSheetProps(CutsInfo xCuts, CutsInfo yCuts, int startCutIndex)
	{
		SheetInfo sheetInfo = new SheetInfo();
		
		XlsReportConfiguration configuration = getCurrentItemConfiguration();
		int maxRowsPerSheet = getMaxRowsPerSheet();
		boolean isRemoveEmptySpaceBetweenRows = configuration.isRemoveEmptySpaceBetweenRows();
		
		sheetInfo.tabColor = configuration.getSheetTabColor();
		sheetInfo.ignoreCellBackground = configuration.isIgnoreCellBackground();
		sheetInfo.whitePageBackground = configuration.isWhitePageBackground();
		sheetInfo.ignoreCellBorder = configuration.isIgnoreCellBorder();
		boolean elementLevelRowFreeze = false, elementLevelColumnFreeze = false;
		sheetInfo.rowFreezeIndex = configuration.getFreezeRow() == null
			? -1
			: Math.max(0, configuration.getFreezeRow() - 1);
		sheetInfo.columnFreezeIndex = configuration.getFreezeColumn() == null
			? -1
			: Math.max(0, getColumnIndex(configuration.getFreezeColumn()));
		sheetInfo.printSettings = new JRXlsAbstractExporter.SheetInfo().new SheetPrintSettings();
		
		int skippedRows = 0;
		int rowIndex = 0;
		int rowCount = yCuts.size() - 1;
		for (int y = startCutIndex; y < rowCount; y++)
		{
			Cut yCut = yCuts.getCut(y);
			
			rowIndex = y - skippedRows;

			if (
				y > startCutIndex &&
				((maxRowsPerSheet > 0 && rowIndex >= maxRowsPerSheet)
				|| yCut.isBreak()) 
				)
			{
				break;
			}
			
			if (
				yCut.isCutNotEmpty()
				|| ((!isRemoveEmptySpaceBetweenRows || yCut.isCutSpanned())
				&& !collapseRowSpan)
				)
			{
				String sheetName = (String)yCut.getProperty(PROPERTY_SHEET_NAME);
				if (sheetName != null)
				{
					sheetInfo.sheetName = sheetName;
				}

				Float sheetRatio = (Float)yCut.getProperty(XlsReportConfiguration.PROPERTY_COLUMN_WIDTH_RATIO);
				// keep the maximum value for sheet ratio
				if (sheetRatio != null && (sheetInfo.columnWidthRatio == null || sheetInfo.columnWidthRatio < sheetRatio))
				{
					sheetInfo.columnWidthRatio = sheetRatio;
				}
				
				String color = (String)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHEET_TAB_COLOR);
				Color tabColor = JRColorUtil.getColor(color, null);
				if (tabColor != null)
				{
					sheetInfo.tabColor = tabColor;
				}

				Integer firstPageNumber = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_FIRST_PAGE_NUMBER);
				if (firstPageNumber != null)
				{
					sheetInfo.sheetFirstPageNumber = firstPageNumber;
				}
				Boolean showGridlines = (Boolean)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHOW_GRIDLINES);
				if (showGridlines != null)
				{
					sheetInfo.sheetShowGridlines = showGridlines;
				}
				
				Boolean ignoreCellBackground = (Boolean)yCut.getProperty(XlsReportConfiguration.PROPERTY_IGNORE_CELL_BACKGROUND);
				if (ignoreCellBackground != null)
				{
					sheetInfo.ignoreCellBackground = ignoreCellBackground;
				}
				
				Boolean ignoreCellBorder = (Boolean)yCut.getProperty(XlsReportConfiguration.PROPERTY_IGNORE_CELL_BORDER);
				if (ignoreCellBorder != null)
				{
					sheetInfo.ignoreCellBorder = ignoreCellBorder;
				}
				
				Boolean whitePageBackground = (Boolean)yCut.getProperty(XlsReportConfiguration.PROPERTY_WHITE_PAGE_BACKGROUND);
				if (whitePageBackground != null)
				{
					sheetInfo.whitePageBackground = whitePageBackground;
				}

				Integer pageScale = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PAGE_SCALE);
				sheetInfo.sheetPageScale = (isValidScale(pageScale))
								? pageScale 
								: configuration.getPageScale();
				Integer freezeColumn = (Integer)yCut.getProperty(PROPERTY_FREEZE_COLUMN_EDGE);
				if (freezeColumn != null && (!elementLevelColumnFreeze || freezeColumn > sheetInfo.columnFreezeIndex))
				{
					sheetInfo.columnFreezeIndex = Math.max(0, freezeColumn);
					elementLevelColumnFreeze = true;
				}
				Integer freezeRow = (Integer)yCut.getProperty(PROPERTY_FREEZE_ROW_EDGE);
				if (freezeRow != null && (!elementLevelRowFreeze || freezeRow > sheetInfo.rowFreezeIndex))
				{
					sheetInfo.rowFreezeIndex = Math.max(0, freezeRow);
					elementLevelRowFreeze = true;
				}
				sheetInfo.sheetPageScale = (isValidScale(pageScale))
								? pageScale 
								: configuration.getPageScale();
				configurePrintSettings(sheetInfo.printSettings, yCut);
				configureHeaderFooter(sheetInfo.printSettings, yCut);
				
				++rowIndex;
			}
			else
			{
				skippedRows++;
			}
		}
		
		// in case these properties are set per configuration
		updatePrintSettings(sheetInfo.printSettings, configuration);
		updateHeaderFooter(sheetInfo.printSettings, configuration);
		
		return sheetInfo;
	}
	
	protected void configurePrintSettings(SheetInfo.SheetPrintSettings printSettings, Cut yCut) 
	{
		Integer value = null;
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_HEIGHT)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_HEIGHT);
			// The maximum value will be considered as page height
			if (printSettings.getPageHeight() == null || printSettings.getPageHeight() < value)
			{
				printSettings.setPageHeight(value);
			}
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_WIDTH)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_WIDTH);
			// The maximum value will be considered as page height
			if (printSettings.getPageWidth() == null || printSettings.getPageWidth() < value)
			{
				printSettings.setPageWidth(value);
			}
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_TOP_MARGIN)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_TOP_MARGIN);
			// The maximum value will be considered as page margin
			if (printSettings.getTopMargin() == null || printSettings.getTopMargin() < value)
			{
				printSettings.setTopMargin(value);
			}
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_LEFT_MARGIN)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_LEFT_MARGIN);
			if (printSettings.getLeftMargin() == null || printSettings.getLeftMargin() < value)
			{
				printSettings.setLeftMargin(value);
			}
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_BOTTOM_MARGIN)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_BOTTOM_MARGIN);
			if (printSettings.getBottomMargin() == null || printSettings.getBottomMargin() < value)
			{
				printSettings.setBottomMargin((Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_BOTTOM_MARGIN));
			}
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_RIGHT_MARGIN)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_RIGHT_MARGIN);
			if (printSettings.getRightMargin() == null || printSettings.getRightMargin() < value)
			{
				printSettings.setRightMargin((Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_PAGE_RIGHT_MARGIN));
			}
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_HEADER_MARGIN)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_HEADER_MARGIN);
			if (printSettings.getHeaderMargin() == null || printSettings.getHeaderMargin() < value)
			{
				printSettings.setHeaderMargin((Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_HEADER_MARGIN));
			}
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_PRINT_FOOTER_MARGIN)) 
		{
			value = (Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_FOOTER_MARGIN);
			if (printSettings.getFooterMargin() == null || printSettings.getFooterMargin() < value)
			{
				printSettings.setFooterMargin((Integer)yCut.getProperty(XlsReportConfiguration.PROPERTY_PRINT_FOOTER_MARGIN));
			}
		}
	}
	
	protected void configureHeaderFooter(SheetInfo.SheetPrintSettings printSettings, Cut yCut) 
	{
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_SHEET_HEADER_LEFT)) 
		{
			printSettings.setHeaderLeft((String)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHEET_HEADER_LEFT));
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_SHEET_HEADER_CENTER)) 
		{
			printSettings.setHeaderCenter((String)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHEET_HEADER_CENTER));
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_SHEET_HEADER_RIGHT)) 
		{
			printSettings.setHeaderRight((String)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHEET_HEADER_RIGHT));
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_SHEET_FOOTER_LEFT)) 
		{
			printSettings.setFooterLeft((String)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHEET_FOOTER_LEFT));
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_SHEET_FOOTER_CENTER)) 
		{
			printSettings.setFooterCenter((String)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHEET_FOOTER_CENTER));
		}
		if (yCut.hasProperty(XlsReportConfiguration.PROPERTY_SHEET_FOOTER_RIGHT)) 
		{
			printSettings.setFooterRight((String)yCut.getProperty(XlsReportConfiguration.PROPERTY_SHEET_FOOTER_RIGHT));
		}
	}
		
	protected void exportSheet(CutsInfo xCuts, CutsInfo yCuts, int startCutIndex, String defaultSheetName)
	{
		if (sheetInfo != null)
		{
			closeSheet();
		}

		sheetInfo = getSheetProps(xCuts, yCuts, startCutIndex);
		
		sheetInfo.sheetName = getSheetName(sheetInfo.sheetName, defaultSheetName);
		
		sheetInfo.sheetFirstPageIndex = pageIndex;
		
		createSheet(xCuts, sheetInfo);

		// we need to count all sheets generated for all exported documents
		sheetIndex++;
		sheetNamesIndex++;
		resetAutoFilters();

		setFreezePane(sheetInfo.rowFreezeIndex, sheetInfo.columnFreezeIndex);
		setColumnWidths(xCuts);
	}
	

	protected void setColumnWidths(CutsInfo xCuts)
	{
		XlsReportConfiguration configuration = getCurrentItemConfiguration();
		
		boolean isRemoveEmptySpaceBetweenColumns = configuration.isRemoveEmptySpaceBetweenColumns();

		float sheetRatio = sheetInfo.columnWidthRatio == null 
				? (configuration.getColumnWidthRatio() == null ? 1f : configuration.getColumnWidthRatio())
				: sheetInfo.columnWidthRatio; 
		sheetRatio = Math.max(1f, sheetRatio);
		
		int emptyCols = 0;
		for (int xCutIndex = 0; xCutIndex < xCuts.size() - 1; xCutIndex++)
		{
			Cut xCut = xCuts.getCut(xCutIndex);

			if (!isRemoveEmptySpaceBetweenColumns || (xCut.isCutNotEmpty() || xCut.isCutSpanned()))
			{
				Integer width = (Integer)xCut.getProperty(PROPERTY_COLUMN_WIDTH);
				width = 
					width == null 
					? (int)((xCuts.getCutOffset(xCutIndex + 1) - xCuts.getCutOffset(xCutIndex)) * sheetRatio) 
					: width;  
				
				boolean isAutoFit = xCut.hasProperty(PROPERTY_AUTO_FIT_COLUMN) 
						&& (Boolean)xCut.getProperty(PROPERTY_AUTO_FIT_COLUMN);
				
				setColumnWidth(xCutIndex - emptyCols, width, isAutoFit);
			}
			else
			{
				emptyCols ++;
			}
		}
	}
	
	@Override
	protected void exportEmptyReport() throws JRException, IOException 
	{
		pageFormat = jasperPrint.getPageFormat();
		String singleSheetName = getCurrentItemConfiguration().getSingleSheetName();
		exportPage(new JRBasePrintPage(), null, 0, singleSheetName == null ? jasperPrint.getName() : singleSheetName);
	}
	
	//abstract methods


	protected abstract void createSheet(CutsInfo xCuts, SheetInfo sheetInfo);
	
	protected abstract void setRowHeight(int rowIndex, int lastRowHeight, Cut yCut, XlsRowLevelInfo levelInfo) throws JRException;

	//	protected abstract void setCell(JRExporterGridCell gridCell, int colIndex, int rowIndex);

	protected abstract void addBlankCell(JRExporterGridCell gridCell, int colIndex, int rowIndex) throws JRException;

	protected abstract void addOccupiedCell(OccupiedGridCell occupiedGridCell, int colIndex, int rowIndex) throws JRException;

	protected abstract void exportText(JRPrintText text, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException;

	protected abstract void exportImage(JRPrintImage image, JRExporterGridCell cell, int colIndex, int rowIndex, int emptyCols, int yCutsRow, JRGridLayout layout) throws JRException;

	protected abstract void exportRectangle(JRPrintGraphicElement element, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException;

	protected abstract void exportLine(JRPrintLine line, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException;

	protected abstract void exportFrame(JRPrintFrame frame, JRExporterGridCell cell, int colIndex, int rowIndex) throws JRException;

	protected abstract void exportGenericElement(JRGenericPrintElement element, JRExporterGridCell cell, int colIndex, int rowIndex, int emptyCols, int yCutsRow, JRGridLayout layout) throws JRException;

}
