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
 * Contributor: Manuel Paul <mpaul@ratundtat.com>,
 *				Rat & Tat Beratungsgesellschaft mbH,
 *				Muehlenkamp 6c,
 *				22303 Hamburg,
 *				Germany.
 */
package net.sf.jasperreports.poi.export;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellReference;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRCommonGraphicElement;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.export.Cut;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.ExporterNature;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXlsAbstractMetadataExporter;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.XlsRowLevelInfo;
import net.sf.jasperreports.engine.export.data.BooleanTextValue;
import net.sf.jasperreports.engine.export.data.DateTextValue;
import net.sf.jasperreports.engine.export.data.NumberTextValue;
import net.sf.jasperreports.engine.export.data.StringTextValue;
import net.sf.jasperreports.engine.export.data.TextValue;
import net.sf.jasperreports.engine.export.data.TextValueHandler;
import net.sf.jasperreports.engine.export.type.ImageAnchorTypeEnum;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.export.XlsExporterConfiguration;
import net.sf.jasperreports.export.XlsMetadataExporterConfiguration;
import net.sf.jasperreports.export.XlsMetadataReportConfiguration;
import net.sf.jasperreports.export.XlsReportConfiguration;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class JRXlsMetadataExporter extends JRXlsAbstractMetadataExporter<XlsMetadataReportConfiguration, XlsMetadataExporterConfiguration, JRXlsExporterContext> 
{

	private static final Log log = LogFactory.getLog(JRXlsMetadataExporter.class);
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String XLS_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "xls";
	public static short MAX_COLOR_INDEX = 56;
	public static short MIN_COLOR_INDEX = 10;	/* Indexes from 0 to 9 are reserved */
	
	private static Map<Color,HSSFColor> hssfColorsCache = new ReferenceMap<>();

	protected final DateFormat isoDateFormat = JRDataUtils.getIsoDateFormat();
	
	protected Map<StyleInfo,HSSFCellStyle> loadedCellStyles = new HashMap<>();
	protected Map<String,List<Hyperlink>> anchorLinks = new HashMap<>();
	protected Map<Integer,List<Hyperlink>> pageLinks = new HashMap<>();
	protected Map<String,HSSFName> anchorNames = new HashMap<>();

	protected HSSFWorkbook workbook;
	protected HSSFSheet sheet;
	protected HSSFRow row;
	protected HSSFCell cell;
	protected HSSFCellStyle emptyCellStyle;
	protected CreationHelper createHelper;
	private HSSFPalette palette = null;
	protected Map<String, HSSFCellStyle> columnStylesMap;
	protected Map<String, Integer> columnWidths;
	protected Map<String, Float> columnWidthRatios;
	protected Map<HSSFCell, String> formulaCellsMap;


	/**
	 *
	 */
	protected short whiteIndex = HSSFColor.HSSFColorPredefined.WHITE.getIndex();
	protected short blackIndex = HSSFColor.HSSFColorPredefined.BLACK.getIndex();
	protected short customColorIndex = MIN_COLOR_INDEX;

	protected FillPatternType backgroundMode = FillPatternType.SOLID_FOREGROUND;

	protected HSSFDataFormat dataFormat;

	protected HSSFPatriarch patriarch;
	
	protected static final String EMPTY_SHEET_NAME = "Sheet1";

	protected class ExporterContext extends BaseExporterContext implements JRXlsExporterContext	
	{
	}

	
	/**
	 * @see #JRXlsMetadataExporter(JasperReportsContext)
	 */
	public JRXlsMetadataExporter(){
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JRXlsMetadataExporter(JasperReportsContext jasperReportsContext)	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
	}


	@Override
	protected Class<XlsMetadataExporterConfiguration> getConfigurationInterface()
	{
		return XlsMetadataExporterConfiguration.class;
	}


	@Override
	protected Class<XlsMetadataReportConfiguration> getItemConfigurationInterface()
	{
		return XlsMetadataReportConfiguration.class;
	}
	

	@Override
	protected void initExport() 
	{
		super.initExport();
		
		sheet = null;
	}
	

	@Override
	protected void initReport() 
	{
		super.initReport();

		XlsReportConfiguration configuration = getCurrentItemConfiguration();
		
		if (!configuration.isWhitePageBackground())
		{
			backgroundMode = FillPatternType.NO_FILL;
		}

		nature = 
			new JRXlsMetadataExporterNature(
				jasperReportsContext, 
				filter, 
				configuration.isIgnoreGraphics(), 
				configuration.isIgnorePageMargins()
				);
	}

	@Override
	protected void openWorkbook(OutputStream os) throws JRException	{
		XlsMetadataExporterConfiguration configuration = getCurrentConfiguration();
		String lcWorkbookTemplate = workbookTemplate == null ? configuration.getWorkbookTemplate() : workbookTemplate;
		if (lcWorkbookTemplate == null) {
			workbook = new HSSFWorkbook();
		} else {
			InputStream templateIs = null;
			try {
				templateIs = getRepository().getInputStreamFromLocation(lcWorkbookTemplate);
				if (templateIs == null)	{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_TEMPLATE_NOT_FOUND,  
							new Object[]{lcWorkbookTemplate} 
							);
				} else {
					workbook = new HSSFWorkbook(new POIFSFileSystem(templateIs));
					boolean keepSheets = keepTemplateSheets == null ? configuration.isKeepWorkbookTemplateSheets() : keepTemplateSheets;
					if (keepSheets) {
						sheetIndex += workbook.getNumberOfSheets();
					} else {
						for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
							workbook.removeSheetAt(i);
						}
					}
				}
			} catch (JRException | IOException e) {
				throw new JRRuntimeException(e);
			} finally {
				if (templateIs != null)	{
					try	{
						templateIs.close();
					} catch (IOException e)	{
					}
				}
			}
		}
		emptyCellStyle = workbook.createCellStyle();
		emptyCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		emptyCellStyle.setFillPattern(backgroundMode);
		dataFormat = workbook.createDataFormat();
		createHelper = workbook.getCreationHelper();
		firstPageNotSet = true;
		palette =  workbook.getCustomPalette();
		customColorIndex = MIN_COLOR_INDEX; 
		columnWidths = new HashMap<>();
		columnWidthRatios = new HashMap<>();
		formulaCellsMap = new HashMap<>();
	}

	@Override
	protected void createSheet(SheetInfo sheetInfo)	{
		this.sheetInfo = sheetInfo;
		sheet = workbook.createSheet(sheetInfo.sheetName);
		patriarch = sheet.createDrawingPatriarch();
		HSSFPrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(pageFormat.getOrientation() == OrientationEnum.LANDSCAPE);
		short paperSize = getSuitablePaperSize();
		
		XlsReportConfiguration configuration = getCurrentItemConfiguration();

		if(paperSize != -1)	{
			printSetup.setPaperSize(paperSize);
		}

		String password = configuration.getPassword();
		if(password != null) {
			sheet.protectSheet(password);
		}
		
		sheet.setMargin(PageMargin.LEFT, LengthUtil.inch(configuration.getPrintPageLeftMargin()));
		sheet.setMargin(PageMargin.RIGHT, LengthUtil.inch(configuration.getPrintPageRightMargin()));
		sheet.setMargin(PageMargin.TOP, LengthUtil.inch(configuration.getPrintPageTopMargin()));
		sheet.setMargin(PageMargin.BOTTOM, LengthUtil.inch(configuration.getPrintPageBottomMargin()));

		if(configuration.getSheetHeaderLeft() != null)
		{
			sheet.getHeader().setLeft(configuration.getSheetHeaderLeft());
		}
		
		if(configuration.getSheetHeaderCenter() != null)
		{
			sheet.getHeader().setCenter(configuration.getSheetHeaderCenter());
		}
		
		if(configuration.getSheetHeaderRight() != null)
		{
			sheet.getHeader().setRight(configuration.getSheetHeaderRight());
		}
		
		if(configuration.getSheetFooterLeft() != null)
		{
			sheet.getFooter().setLeft(configuration.getSheetFooterLeft());
		}
		
		if(configuration.getSheetFooterCenter() != null)
		{
			sheet.getFooter().setCenter(configuration.getSheetFooterCenter());
		}
		
		if(configuration.getSheetFooterRight() != null)
		{
			sheet.getFooter().setRight(configuration.getSheetFooterRight());
		}
		
		printSetup.setHeaderMargin(LengthUtil.inch(configuration.getPrintHeaderMargin()));	
		printSetup.setFooterMargin(LengthUtil.inch(configuration.getPrintFooterMargin()));	
		
		RunDirectionEnum sheetDirection = configuration.getSheetDirection();
		if(sheetDirection != null) {
			printSetup.setLeftToRight(sheetDirection == RunDirectionEnum.LTR);
			sheet.setRightToLeft(sheetDirection == RunDirectionEnum.RTL);
		}
		
		if(sheetInfo.sheetFirstPageNumber != null && sheetInfo.sheetFirstPageNumber > 0) 
		{
			printSetup.setPageStart((short)sheetInfo.sheetFirstPageNumber.intValue());
			printSetup.setUsePage(true);
			firstPageNotSet = false;
		}
		else 
		{
			Integer documentFirstPageNumber = configuration.getFirstPageNumber();
			if(documentFirstPageNumber != null && documentFirstPageNumber > 0 && firstPageNotSet) 
			{
				printSetup.setPageStart((short)documentFirstPageNumber.intValue());
				printSetup.setUsePage(true);
				firstPageNotSet = false;
			}
		}
		if(!firstPageNotSet && (sheet.getFooter().getCenter() == null || sheet.getFooter().getCenter().length() == 0)) {
			sheet.getFooter().setCenter("Page " + HeaderFooter.page());
		}
		
		boolean showGridlines = true;
		if (sheetInfo.sheetShowGridlines == null)
		{
			Boolean documentShowGridlines = configuration.isShowGridLines();
			if (documentShowGridlines != null)
			{
				showGridlines = documentShowGridlines;
			}
		}
		else
		{
			showGridlines = sheetInfo.sheetShowGridlines;
		}
		sheet.setDisplayGridlines(showGridlines);
		
		backgroundMode = Boolean.TRUE.equals(sheetInfo.whitePageBackground) 
				? FillPatternType.SOLID_FOREGROUND 
				: FillPatternType.NO_FILL;
		
		
//		maxRowFreezeIndex = 0;
//		maxColumnFreezeIndex = 0;

		onePagePerSheetMap.put(sheetIndex, onePagePerSheet);
		sheetsBeforeCurrentReportMap.put(sheetIndex, sheetsBeforeCurrentReport);
	}

	@Override
	protected void closeSheet()	
	{
		if (sheet == null)
		{
			return;
		}
		
		HSSFPrintSetup printSetup = sheet.getPrintSetup();
		
		if (isValidScale(sheetInfo.sheetPageScale))
		{
			printSetup.setScale((short)sheetInfo.sheetPageScale.intValue());
		}
		else
		{
			XlsReportConfiguration configuration = getCurrentItemConfiguration();

			Integer fitWidth = configuration.getFitWidth();
			if (fitWidth != null) 
			{
				printSetup.setFitWidth(fitWidth.shortValue());
				sheet.setAutobreaks(true);
			}

			Integer fitHeight = configuration.getFitHeight();
			fitHeight = 
				fitHeight == null
				? (Boolean.TRUE == configuration.isAutoFitPageHeight() 
					? (pageIndex - sheetInfo.sheetFirstPageIndex)
					: null)
				: fitHeight;
			if (fitHeight != null)
			{
				printSetup.setFitHeight(fitHeight.shortValue());
				sheet.setAutobreaks(true);
			}
		}
	}

	@Override
	protected void closeWorkbook(OutputStream os) throws JRException {
		try	{
			for (Entry<String, HSSFName> entry : anchorNames.entrySet()) {
				String anchorName = entry.getKey();
				HSSFName anchor = entry.getValue();
				List<Hyperlink> linkList = anchorLinks.get(anchorName);
				anchor.setRefersToFormula("'" + workbook.getSheetName(anchor.getSheetIndex()) + "'!"+ anchor.getRefersToFormula());
				
				if(linkList != null && !linkList.isEmpty())	{
					for(Hyperlink link : linkList) {
						link.setAddress(anchor.getRefersToFormula());
					}
				}
			}
			
			if(!definedNamesMap.isEmpty()) 
			{
				for(Map.Entry<NameScope, String> entry : definedNamesMap.entrySet())
				{
					HSSFName name = workbook.createName();
					NameScope nameScope = entry.getKey();
					name.setNameName(nameScope.getName());
					name.setRefersToFormula(entry.getValue());
					int scopeIndex = workbook.getSheetIndex(nameScope.getScope());
					// name and name scope are ignoring case in Excel
					if(nameScope.getScope() != null 
							&& !DEFAULT_DEFINED_NAME_SCOPE.equalsIgnoreCase(nameScope.getScope())
							&& scopeIndex >= 0)
					{
						name.setSheetIndex(scopeIndex);
					}
				}
			}
			
			// applying formulas
			if(formulaCellsMap != null && !formulaCellsMap.isEmpty())
			{
				for(Map.Entry<HSSFCell, String> formulaCell: formulaCellsMap.entrySet())
				{
					try
					{
						formulaCell.getKey().setCellFormula(formulaCell.getValue());
					}
					catch(Exception e)
					{
						// usually an org.apache.poi.ss.formula.FormulaParseException 
						// or a java.lang.IllegalArgumentException
						// or a java.lang.IllegalStateException
						if(log.isWarnEnabled())
						{
							log.warn(e.getMessage());
						}
						throw new JRException(e);
					}
				}
			}
			
			int index = 0;
			for (Entry<Integer, List<Hyperlink>> entry : pageLinks.entrySet()) {
				Integer linkPage = entry.getKey();
				List<Hyperlink> linkList = entry.getValue();
				if(linkList != null && !linkList.isEmpty()) {
					for(Hyperlink link : linkList) {
						index = onePagePerSheetMap.get(linkPage-1)!= null 
							? (onePagePerSheetMap.get(linkPage-1)
								? Math.max(0, linkPage - 1)
								: Math.max(0, sheetsBeforeCurrentReportMap.get(linkPage)))
							: 0;
						link.setAddress("'" + workbook.getSheetName(index)+ "'!$A$1");
					}
				}
			}
			
			for(int i=0; i < workbook.getNumberOfSheets(); i++)	{
				HSSFSheet currentSheet = workbook.getSheetAt(i);
				currentSheet.setForceFormulaRecalculation(true);
				for(String columnName : columnNames) {
					Integer columnWidth = columnWidths.get(columnName);
					Float columnWidthRatio = columnWidthRatios.get(columnName);
					if (columnWidth != null && columnWidth < Integer.MAX_VALUE) {
						if(columnWidthRatio != null && columnWidthRatio > 1f)
						{
							columnWidth =  Math.round(43 * columnWidth * columnWidthRatio);
						}
						else
						{
							columnWidth =  43 * columnWidth;
						}
						currentSheet.setColumnWidth(columnNamesMap.get(columnName), Math.min(columnWidth, 256*255));
					} else {
						currentSheet.autoSizeColumn(columnNamesMap.get(columnName), false);
					}
				}
			}

			String encryptionPassword = getCurrentConfiguration().getEncryptionPassword();
			if (encryptionPassword == null || encryptionPassword.trim().length() == 0)
			{
				Biff8EncryptionKey.setCurrentUserPassword(null);
			}
			else
			{
				Biff8EncryptionKey.setCurrentUserPassword(encryptionPassword);
			}
			workbook.write(os);
			Biff8EncryptionKey.setCurrentUserPassword(null);
			
		} catch (IOException e) {
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_REPORT_GENERATION_ERROR,
					new Object[]{jasperPrint.getName()}, 
					e);
		}
	}

	@Override
	protected void setColumnWidth(int col, int width) {
	}

	protected void setRowHeight(HSSFRow row) {
		Integer rowHeight = (Integer)currentRow.get(CURRENT_ROW_HEIGHT);
		if (row != null && rowHeight != null && rowHeight < Integer.MAX_VALUE) {
			row.setHeightInPoints((Integer)currentRow.get(CURRENT_ROW_HEIGHT));
		}
	}
	
	protected void adjustRowHeight(int rowHeight, Boolean isAutofit) {
		if(isAutofit != null && isAutofit) {
			currentRow.put(CURRENT_ROW_HEIGHT, Integer.MAX_VALUE);
		} else if(!currentRow.containsKey(CURRENT_ROW_HEIGHT) || (Integer)currentRow.get(CURRENT_ROW_HEIGHT) < rowHeight) {
			currentRow.put(CURRENT_ROW_HEIGHT, rowHeight);
		}
	}
	
	protected void adjustColumnWidth(String columnName, int columnWidth, Boolean columnAutofit) 
	{
		if (Boolean.TRUE.equals(columnAutofit))
		{
			columnWidths.put(columnName, Integer.MAX_VALUE);
		} 
		else 
		{
			if(!columnWidths.containsKey(columnName) || columnWidths.get(columnName) < columnWidth) 
			{
				columnWidths.put(columnName, columnWidth);
			}
			if(!columnWidthRatios.containsKey(columnName) && sheetInfo.columnWidthRatio != null) 
			{
				columnWidthRatios.put(columnName, sheetInfo.columnWidthRatio);
			}
		}
	}

	protected void addBlankCell(HSSFCellStyle cellStyle, Map<String, Object> cellValueMap, String currentColumnName) throws JRException	{
		HSSFCellStyle currentStyle = cellStyle != null 
			? cellStyle 
			: (columnStylesMap.get(currentColumnName) != null ? columnStylesMap.get(currentColumnName) : emptyCellStyle);
		cellValueMap.put(currentColumnName, new CellSettings(currentStyle));
	}
	
	@Override
	protected void writeCurrentRow(Map<String, Object> currentRow, Map<String, Object> repeatedValues)  throws JRException {
		row = sheet.createRow(sheet.getPhysicalNumberOfRows());
		setRowHeight(row);
		
		for(int i = 0; i< columnNames.size(); i++) {
			String columnName = columnNames.get(i);
			CellSettings cellSettings = (CellSettings)currentRow.get(columnName) == null 
				? (repeatedValues.get(columnName) != null 
				? (CellSettings)repeatedValues.get(columnName)
				: null)
				: (CellSettings)currentRow.get(columnName);
			cell = row.createCell(i);
			if(cellSettings != null) {
				CellType type = cellSettings.getCellType() == null ? CellType.BLANK : cellSettings.getCellType();
				cell.setCellType(type);
				Object cellValue = cellSettings.getCellValue();
				if(cellValue != null) {
					if(cellValue instanceof RichTextString) {
						cell.setCellValue((RichTextString)cellSettings.getCellValue());
					} else if (cellValue instanceof Number) {
						cell.setCellValue(((Number)cellSettings.getCellValue()).doubleValue());
					} else if (cellValue instanceof Date) {
						cell.setCellValue((Date)cellSettings.getCellValue());
					} else if(cellValue instanceof Boolean) {
						cell.setCellValue((Boolean)cellSettings.getCellValue());
					}else if(cellValue instanceof ImageSettings){
						ImageSettings imageSettings = (ImageSettings)cellValue;
						try {
							HSSFClientAnchor anchor = 
									new HSSFClientAnchor(
										0, 
										0, 
										0, 
										0, 
										(short)(i), 
										rowIndex, 
										(short)(i + 1), 
										rowIndex+1
										);
							anchor.setAnchorType(imageSettings.getAnchorType());
							patriarch.createPicture(anchor, imageSettings.getIndex());
						} catch (Exception ex) {
							throw 
								new JRException(
									EXCEPTION_MESSAGE_KEY_CANNOT_ADD_CELL, 
									null,
									ex);
						} catch (Error err) {
							throw 
								new JRException(
									EXCEPTION_MESSAGE_KEY_CANNOT_ADD_CELL, 
									null,
									err);
						}
					}
				}
				
				if(cellSettings.getCellStyle() != null) {
					cell.setCellStyle(cellSettings.getCellStyle());
				}
				if(cellSettings.getFormula() != null) {
					// the formula text will be stored in formulaCellsMap in order to be applied only after 
					// all defined names are created and available in the workbook (see #closeWorkbook())
					formulaCellsMap.put(cell, cellSettings.getFormula());
				}
				if(cellSettings.getLink() != null) {
					cell.setHyperlink(cellSettings.getLink());
				}
			}
		}
		++rowIndex;
	}
	

	@Override
	protected void exportLine(JRPrintLine line) throws JRException {
		String currentColumnName = line.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
		
		if (currentColumnName != null && currentColumnName.length() > 0) {
			boolean repeatValue = getPropertiesUtil().getBooleanProperty(line, PROPERTY_REPEAT_VALUE, false);
			
			setColumnName(currentColumnName);
			adjustColumnWidth(currentColumnName, line.getWidth(), ((JRXlsExporterNature)nature).getColumnAutoFit(line));
			adjustRowHeight(line.getHeight(), ((JRXlsExporterNature)nature).getRowAutoFit(line));
			
			short forecolor = getWorkbookColor(line.getLinePen().getLineColor()).getIndex();

			int side = BoxStyle.TOP;
			float ratio = line.getWidth() / line.getHeight();
			if (ratio > 1) {
				if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)	{
					side = BoxStyle.TOP;
				} else {
					side = BoxStyle.BOTTOM;
				}
			} else {
				if (line.getDirection() != LineDirectionEnum.BOTTOM_UP) {
					side = BoxStyle.LEFT;
				} else {
					side = BoxStyle.RIGHT;
				}
			}
			BoxStyle boxStyle = new BoxStyle(side, line.getLinePen());

			FillPatternType mode = backgroundMode;
			short backcolor = whiteIndex;
			if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && line.getBackcolor() != null) {
				mode = FillPatternType.SOLID_FOREGROUND;
				backcolor = getWorkbookColor(line.getBackcolor()).getIndex();
			}

			HSSFCellStyle cellStyle =
				getLoadedCellStyle(
					mode,
					backcolor,
					HorizontalAlignment.LEFT,
					VerticalAlignment.TOP,
					(short)0,
					getLoadedFont(getDefaultFont(), forecolor, null, getLocale()),
					boxStyle,
					isCellLocked(line),
					isCellHidden(line),
					isShrinkToFit(line)
					);
			addBlankElement(cellStyle, repeatValue, currentColumnName);
		}
	}

	@Override
	protected void exportRectangle(JRPrintGraphicElement element) throws JRException {
		String currentColumnName = element.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
		
		if (currentColumnName != null && currentColumnName.length() > 0) {
			boolean repeatValue = getPropertiesUtil().getBooleanProperty(element,PROPERTY_REPEAT_VALUE, false);
			
			setColumnName(currentColumnName);
			adjustColumnWidth(currentColumnName, element.getWidth(), ((JRXlsExporterNature)nature).getColumnAutoFit(element));
			adjustRowHeight(element.getHeight(), ((JRXlsExporterNature)nature).getRowAutoFit(element));
			
			short forecolor = getWorkbookColor(element.getLinePen().getLineColor()).getIndex();

			FillPatternType mode = backgroundMode;
			short backcolor = whiteIndex;
			if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && element.getBackcolor() != null) {
				mode = FillPatternType.SOLID_FOREGROUND;
				backcolor = getWorkbookColor(element.getBackcolor()).getIndex();
			}

			HSSFCellStyle cellStyle =
				getLoadedCellStyle(
					mode,
					backcolor,
					HorizontalAlignment.LEFT,
					VerticalAlignment.TOP,
					(short)0,
					getLoadedFont(getDefaultFont(), forecolor, null, getLocale()),
					new BoxStyle(element),
					isCellLocked(element),
					isCellHidden(element),
					isShrinkToFit(element)
					);
			addBlankElement(cellStyle, repeatValue, currentColumnName);
		}
	}


	@Override
	protected void exportText(final JRPrintText textElement) throws JRException {
		String currentColumnName = textElement.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
		if (currentColumnName != null && currentColumnName.length() > 0) {
			final boolean hasCurrentColumnData = textElement.getPropertiesMap().containsProperty(PROPERTY_DATA);
			String currentColumnData = textElement.getPropertiesMap().getProperty(PROPERTY_DATA);
			boolean repeatValue = getPropertiesUtil().getBooleanProperty(textElement, PROPERTY_REPEAT_VALUE, false);
			
			setColumnName(currentColumnName);
			boolean columnAutoFit = Boolean.TRUE.equals(((JRXlsExporterNature)nature).getColumnAutoFit(textElement));
			adjustColumnWidth(currentColumnName, textElement.getWidth(), columnAutoFit);
			adjustRowHeight(textElement.getHeight(), isWrapText(textElement) || Boolean.TRUE.equals(((JRXlsExporterNature)nature).getRowAutoFit(textElement)));
			
			final short forecolor = getWorkbookColor(textElement.getForecolor()).getIndex();

			TextAlignHolder textAlignHolder = getTextAlignHolder(textElement);
			HorizontalAlignment horizontalAlignment = getHorizontalAlignment(textAlignHolder);
			VerticalAlignment verticalAlignment = getVerticalAlignment(textAlignHolder);
			short rotation = getRotation(textAlignHolder);
			
			FillPatternType mode = backgroundMode;
			short backcolor = whiteIndex;
			if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && textElement.getBackcolor() != null) {
				mode = FillPatternType.SOLID_FOREGROUND;
				backcolor = getWorkbookColor(textElement.getBackcolor()).getIndex();
			}

			final StyleInfo baseStyle = isIgnoreTextFormatting(textElement) 
				? new StyleInfo(
					mode,
					whiteIndex,
					horizontalAlignment,
					verticalAlignment,
					(short)0,
					null,
					(BoxStyle)null, 
					isWrapText(textElement) || columnAutoFit,
					isCellLocked(textElement),
					isCellHidden(textElement),
					isShrinkToFit(textElement)
					)
				: new StyleInfo(
					mode,
					backcolor,
					horizontalAlignment,
					verticalAlignment,
					rotation,
					getLoadedFont(textElement, forecolor, null, getTextLocale(textElement)),
					new BoxStyle(textElement), 
					isWrapText(textElement) || columnAutoFit,
					isCellLocked(textElement),
					isCellHidden(textElement),
					isShrinkToFit(textElement)
					);
			
			final JRStyledText styledText;
			final String textStr;
			final String formula;
			final CellSettings cellSettings = new CellSettings();
			if (hasCurrentColumnData)
			{
				styledText = new JRStyledText();
				if (currentColumnData != null) {
					styledText.append(currentColumnData);
				}
				textStr = currentColumnData;
				formula = null;
			} else {
				styledText = getStyledText(textElement);
				if (styledText != null) {
					textStr = styledText.getText();
				} else {
					textStr = null;
				}
				formula = getFormula(textElement);
			}
			
			TextValue value = null;
			String pattern = null;

			if (
				formula != null
				|| detectCellType
				) 
			{
				value = getTextValue(textElement, textStr);

				if (value instanceof NumberTextValue) {
					pattern = ((NumberTextValue)value).getPattern();
				} else if (value instanceof DateTextValue) {
					pattern = ((DateTextValue)value).getPattern();
				}
			}

			String convertedPattern = getConvertedPattern(textElement, pattern);
			if (convertedPattern != null) {
				baseStyle.setDataFormat(dataFormat.getFormat(convertedPattern));
			}
			
			if (formula != null) {	
				cellSettings.importValues(CellType.FORMULA, getLoadedCellStyle(baseStyle), null, formula);
				
			} else if (detectCellType) {
				value.handle(new TextValueHandler() {
					@Override
					public void handle(StringTextValue textValue) {
						if (JRCommonText.MARKUP_NONE.equals(textElement.getMarkup()) || isIgnoreTextFormatting(textElement)) {
							cellSettings.importValues(CellType.STRING, getLoadedCellStyle(baseStyle), new HSSFRichTextString(textValue.getText()));
						} else {
							cellSettings.importValues(CellType.STRING, getLoadedCellStyle(baseStyle), getRichTextString(styledText, forecolor, textElement, getTextLocale(textElement)));
						}
					}

					@Override
					public void handle(NumberTextValue textValue) {
						Number value = null;
						if (hasCurrentColumnData) {
							if (textStr != null) {
								try {
									value = Double.parseDouble(textStr);
								} catch (NumberFormatException nfe) {
									throw new JRRuntimeException(nfe);
								}
							}
						} else {
							value = textValue.getValue();
						}
						
						if (
							value != null
							&& DefaultFormatFactory.STANDARD_NUMBER_FORMAT_DURATION.equals(convertedPattern) 
							)
						{
							value = value.doubleValue() / 86400;
						}
						
						cellSettings.importValues(CellType.NUMERIC, getLoadedCellStyle(baseStyle), value);
					}

					@Override
					public void handle(DateTextValue textValue) {
						Date value = null;
						if (hasCurrentColumnData) {
							if (textStr != null) {
								try {
									value = new Date(Long.parseLong(textStr));
								} catch (NumberFormatException nfe) {
									try {
										value = isoDateFormat.parse(textStr);
									} catch (ParseException pe) {
										throw new JRRuntimeException(pe);
									}
								}
							}
						} else {
							value = textValue.getValue() == null ? null : translateDateValue(textElement, textValue.getValue());
						}
						cellSettings.importValues(CellType.NUMERIC, getLoadedCellStyle(baseStyle), value);
					}

					@Override
					public void handle(BooleanTextValue textValue) {
						Boolean value = hasCurrentColumnData ? Boolean.valueOf(textStr) : textValue.getValue();
						cellSettings.importValues(CellType.BOOLEAN, getLoadedCellStyle(baseStyle), value);
					}
				});
			} else {
				if (JRCommonText.MARKUP_NONE.equals(textElement.getMarkup()) || isIgnoreTextFormatting(textElement)) {
					cellSettings.importValues(CellType.STRING, getLoadedCellStyle(baseStyle), new HSSFRichTextString(textStr));
				} else {
					cellSettings.importValues(CellType.STRING, getLoadedCellStyle(baseStyle), getRichTextString(styledText, forecolor, textElement, getTextLocale(textElement)));
				}
			}
			
			if(!ignoreAnchors) {
				String anchorName = textElement.getAnchorName();
				if(anchorName != null) {
					HSSFName aName = workbook.createName();
					aName.setNameName(toExcelName(anchorName));
					aName.setSheetIndex(workbook.getSheetIndex(sheet));
					CellReference cRef = new CellReference(rowIndex, columnNamesMap.get(currentColumnName), true, true);
					aName.setRefersToFormula(cRef.formatAsString());
					anchorNames.put(anchorName, aName);
				}
			}

			setHyperlinkCell(textElement, cellSettings);
			addTextElement(cellSettings, textStr, repeatValue, currentColumnName);
		}
	}

	protected void setHyperlinkCell(JRPrintHyperlink hyperlink, CellSettings cellSettings)
	{
		Hyperlink link = null;

		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(XlsReportConfiguration.PROPERTY_IGNORE_HYPERLINK, hyperlink);
		if (ignoreHyperlink == null) {
			ignoreHyperlink = defaultIgnoreHyperlink;
		}

		if (!ignoreHyperlink) {
			JRHyperlinkProducer customHandler = getHyperlinkProducer(hyperlink);
			if (customHandler == null) {
				switch (hyperlink.getHyperlinkType()) {
					case REFERENCE: {
						String href = hyperlink.getHyperlinkReference();
						if (href != null) {
							link = createHelper.createHyperlink(HyperlinkType.URL);
							link.setAddress(href);
						}
						break;
					}
					case LOCAL_ANCHOR : {
						if(!ignoreAnchors) {
							String href = hyperlink.getHyperlinkAnchor();
							if (href != null) {
								link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
								if(anchorLinks.containsKey(href)) {
									(anchorLinks.get(href)).add(link);
								} else {
									List<Hyperlink> hrefList = new ArrayList<>();
									hrefList.add(link);
									anchorLinks.put(href, hrefList);
								}
							}
						}
						break;
					}
					case LOCAL_PAGE :
					{
						Integer hrefPage = (onePagePerSheet ? hyperlink.getHyperlinkPage() : 0);
						if (hrefPage != null) {
							link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
							if(pageLinks.containsKey(sheetsBeforeCurrentReport+hrefPage)) {
								pageLinks.get(sheetsBeforeCurrentReport + hrefPage).add(link);
							} else {
								List<Hyperlink> hrefList = new ArrayList<>();
								hrefList.add(link);
								pageLinks.put(sheetsBeforeCurrentReport + hrefPage, hrefList);
							}
						}
						break;
					}
					case REMOTE_ANCHOR : {
						String href = hyperlink.getHyperlinkReference();
						if (href != null && hyperlink.getHyperlinkAnchor() != null) {
							href = href + "#" + hyperlink.getHyperlinkAnchor();
							link = createHelper.createHyperlink(HyperlinkType.FILE);
							link.setAddress(href);
							
						}
						break;
					}
					case REMOTE_PAGE : {
						String href = hyperlink.getHyperlinkReference();
						if (href != null && hyperlink.getHyperlinkPage() != null) {
							href = href + "#JR_PAGE_ANCHOR_0_" + hyperlink.getHyperlinkPage().toString();
							link = createHelper.createHyperlink(HyperlinkType.FILE);
							link.setAddress(href);
							
						}
						break;
					}
					case NONE:
					default: {
					}
				}
			} else {
				String href = customHandler.getHyperlink(hyperlink);
				if (href != null) {
					link = createHelper.createHyperlink(HyperlinkType.URL);
					link.setAddress(href);
				}
			}
			
			if(link != null) {
				//TODO: make tooltips functional
//				if(hyperlink.getHyperlinkTooltip() != null)
//				{
//					link.setLabel(hyperlink.getHyperlinkTooltip());
//				}
				cellSettings.setLink(link);
			}
		}
	}
	
	protected void addTextElement(CellSettings cellSettings, String textStr, boolean repeatValue, String currentColumnName) throws JRException {
		if (columnNames.size() > 0) {
			if (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && isColumnReadOnTime(currentRow, currentColumnName)) {	// the column is for export but was not read yet and comes in the expected order
				addCell(cellSettings, currentRow, currentColumnName);
			} else if ( (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && !isColumnReadOnTime(currentRow, currentColumnName)) // the column is for export, was not read yet, but it is read after it should be
					|| (columnNames.contains(currentColumnName) && currentRow.containsKey(currentColumnName)) ) {	// the column is for export and was already read

				if(rowIndex == 1 && getCurrentItemConfiguration().isWriteHeader()) {
					writeReportHeader();
				}
				writeCurrentRow(currentRow, repeatedValues);
				currentRow.clear();
				addCell(cellSettings, currentRow, currentColumnName);
			}
			// set auto fill columns
			if(repeatValue) {
				if ( currentColumnName != null && currentColumnName.length() > 0 && textStr.length() > 0) {
					addCell(cellSettings, repeatedValues, currentColumnName);
				}
			} else {
				repeatedValues.remove(currentColumnName);
			}
		}
	}
	
	protected void addBlankElement(HSSFCellStyle cellStyle, boolean repeatValue, String currentColumnName) throws JRException {
		if (columnNames.size() > 0) {
			if (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && isColumnReadOnTime(currentRow, currentColumnName)) {	// the column is for export but was not read yet and comes in the expected order
				addBlankCell(cellStyle, currentRow, currentColumnName); 
			} else if ( (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && !isColumnReadOnTime(currentRow, currentColumnName)) 	// the column is for export, was not read yet, but it is read after it should be
					|| (columnNames.contains(currentColumnName) && currentRow.containsKey(currentColumnName)) ) {	// the column is for export and was already read
				
				if(rowIndex == 1 && getCurrentItemConfiguration().isWriteHeader()) {
					writeReportHeader();
				}
				
				writeCurrentRow(currentRow, repeatedValues);
				currentRow.clear();
				addBlankCell(cellStyle, currentRow, currentColumnName);
			}
			
			// set auto fill columns
			if(repeatValue) {
				if (repeatValue && currentColumnName != null && currentColumnName.length() > 0 && cellStyle != null) {
					addBlankCell(cellStyle, repeatedValues, currentColumnName);
				}
			} else {
				repeatedValues.remove(currentColumnName);
			}
		}
	}

	protected void addCell(CellSettings cellSettings, Map<String, Object> cellValueMap, String currentColumnName) throws JRException {
		cellValueMap.put(currentColumnName, cellSettings);
	}

	protected HSSFRichTextString getRichTextString(JRStyledText styledText, short forecolor, JRFont defaultFont, Locale locale) 
	{
		styledText = JRStyledTextUtil.getBulletedStyledText(styledText);
		
		String text = styledText.getText();
		HSSFRichTextString richTextStr = new HSSFRichTextString(text);
		int runLimit = 0;
		AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();

		while (runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length()) 
		{
			Map<Attribute,Object> attributes = iterator.getAttributes();
			JRFont runFont = attributes.isEmpty()? defaultFont : new JRBaseFont(attributes);
			short runForecolor = 
				attributes.get(TextAttribute.FOREGROUND) != null  
				? getWorkbookColor((Color)attributes.get(TextAttribute.FOREGROUND)).getIndex() 
				: forecolor;
			HSSFFont font = getLoadedFont(runFont, runForecolor, attributes, locale);
			richTextStr.applyFont(iterator.getIndex(), runLimit, font);
			iterator.setIndex(runLimit);
		}
		return richTextStr;
	}

	@Override
	public void exportImage(JRPrintImage element) throws JRException {

		String currentColumnName = element.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
		if (currentColumnName != null && currentColumnName.length() > 0) {
			boolean repeatValue = getPropertiesUtil().getBooleanProperty(element, PROPERTY_REPEAT_VALUE, false);
			
			setColumnName(currentColumnName);
			adjustColumnWidth(currentColumnName, element.getWidth(), ((JRXlsExporterNature)nature).getColumnAutoFit(element));
			adjustRowHeight(element.getHeight(), Boolean.TRUE.equals(((JRXlsExporterNature)nature).getRowAutoFit(element)));
			
			int topPadding =
				Math.max(element.getLineBox().getTopPadding(), getImageBorderCorrection(element.getLineBox().getTopPen()));
			int leftPadding =
				Math.max(element.getLineBox().getLeftPadding(), getImageBorderCorrection(element.getLineBox().getLeftPen()));
			int bottomPadding =
				Math.max(element.getLineBox().getBottomPadding(), getImageBorderCorrection(element.getLineBox().getBottomPen()));
			int rightPadding =
				Math.max(element.getLineBox().getRightPadding(), getImageBorderCorrection(element.getLineBox().getRightPen()));

			int tmpAvailableImageWidth = element.getWidth() - leftPadding - rightPadding;
			int availableImageWidth = tmpAvailableImageWidth < 0 ? 0 : tmpAvailableImageWidth;

			int tmpAvailableImageHeight = element.getHeight() - topPadding - bottomPadding;
			int availableImageHeight = tmpAvailableImageHeight < 0 ? 0 : tmpAvailableImageHeight;

			InternalImageProcessor imageProcessor = 
				new InternalImageProcessor(
					element,
					availableImageWidth,
					availableImageHeight
					);
						
			Renderable renderer = element.getRenderer();

			if (
				renderer != null 
				&& imageProcessor.availableImageWidth > 0 
				&& imageProcessor.availableImageHeight > 0
				) 
			{
				InternalImageProcessorResult imageProcessorResult = null;
				
				try
				{
					imageProcessorResult = imageProcessor.process(renderer);
				}
				catch (Exception e)
				{
					Renderable onErrorRenderer = getRendererUtil().handleImageError(e, element.getOnErrorType());
					if (onErrorRenderer != null)
					{
						imageProcessorResult = imageProcessor.process(onErrorRenderer);
					}
				}
				
				if (imageProcessorResult != null)//FIXMEXLS background for null images like the other exporters
				{
					FillPatternType mode = backgroundMode;
					short backcolor = whiteIndex;
					if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && element.getBackcolor() != null) {
						mode = FillPatternType.SOLID_FOREGROUND;
						backcolor = getWorkbookColor(element.getBackcolor()).getIndex();
					}

					short forecolor = getWorkbookColor(element.getLineBox().getPen().getLineColor()).getIndex();

					if(element.getMode() == ModeEnum.OPAQUE ) {
						backcolor = getWorkbookColor(element.getBackcolor()).getIndex();
					}

					HSSFCellStyle cellStyle =
						getLoadedCellStyle(
							mode,
							backcolor,
							HorizontalAlignment.LEFT,
							VerticalAlignment.TOP,
							(short)0,
							getLoadedFont(getDefaultFont(), forecolor, null, getLocale()),
							new BoxStyle(element),
							isCellLocked(element),
							isCellHidden(element),
							isShrinkToFit(element)
							);

					addBlankElement(cellStyle, false, currentColumnName);
					
					int colIndex = columnNamesMap.get(currentColumnName);
					try {
						HSSFClientAnchor anchor = 
								new HSSFClientAnchor(
									0, //paddings and offsets, although calculated, are deliberately ignored
									0, 
									0, 
									0, 
									(short)(colIndex), 
									rowIndex, 
									(short)(colIndex + 1), 
									rowIndex+1
									);
						ImageAnchorTypeEnum imageAnchorType = 
							ImageAnchorTypeEnum.getByName(
								JRPropertiesUtil.getOwnProperty(element, XlsReportConfiguration.PROPERTY_IMAGE_ANCHOR_TYPE)
								);
						if (imageAnchorType == null)
						{
							imageAnchorType = defaultImageAnchorType;
							if (imageAnchorType == null)
							{
								imageAnchorType = ImageAnchorTypeEnum.MOVE_NO_SIZE;
							}
						}
						anchor.setAnchorType(JRXlsExporter.getAnchorType(imageAnchorType));
						//pngEncoder.setImage(bi);
						//int imgIndex = workbook.addPicture(pngEncoder.pngEncode(), HSSFWorkbook.PICTURE_TYPE_PNG);
						int imgIndex = workbook.addPicture(imageProcessorResult.imageData, HSSFWorkbook.PICTURE_TYPE_PNG);
						patriarch.createPicture(anchor, imgIndex).setRotationDegree(imageProcessorResult.angle);
						
						// set auto fill columns
						if(repeatValue) {
							CellSettings cellSettings = new CellSettings(cellStyle);
							cellSettings.setCellValue(new ImageSettings(imgIndex, anchor.getAnchorType()));
							addCell(cellSettings, repeatedValues, currentColumnName);
						} else {
							repeatedValues.remove(currentColumnName);
						}
						
//						setHyperlinkCell(element);
					} catch (Exception ex) {
						throw 
							new JRException(
								EXCEPTION_MESSAGE_KEY_CANNOT_ADD_CELL, 
								null,
								ex);
					} catch (Error err) {
						throw 
							new JRException(
								EXCEPTION_MESSAGE_KEY_CANNOT_ADD_CELL, 
								null,
								err);
					}
				}
			}
		}
	}

	private class InternalImageProcessor
	{
		private final JRPrintImage imageElement;
		private final RenderersCache imageRenderersCache;

		private final int availableImageWidth;
		private final int availableImageHeight;
		
		protected InternalImageProcessor(
			JRPrintImage imageElement,
			int availableImageWidth,
			int availableImageHeight
			)
		{
			this.imageElement = imageElement;
			this.imageRenderersCache = imageElement.isUsingCache() ? renderersCache : new RenderersCache(getJasperReportsContext());
			
			this.availableImageWidth = availableImageWidth;
			this.availableImageHeight = availableImageHeight;
		}
		
		private InternalImageProcessorResult process(Renderable renderer) throws JRException
		{
			InternalImageProcessorResult imageProcessorResult = null;

			if (renderer instanceof ResourceRenderer)
			{
				renderer = imageRenderersCache.getLoadedRenderer((ResourceRenderer)renderer);
			}
			
			switch (imageElement.getScaleImage())
			{
				case CLIP:
				{
					imageProcessorResult = 
						processImageClip(
							imageRenderersCache.getGraphics2DRenderable(renderer)
							);

					break;
				}
				case FILL_FRAME:
				{
					Dimension dimension = null;
					if (
						imageElement.getRotation() == RotationEnum.LEFT
						|| imageElement.getRotation() == RotationEnum.RIGHT
						)
					{
						dimension = new Dimension(availableImageHeight, availableImageWidth);
					}
					else
					{
						dimension = new Dimension(availableImageWidth, availableImageHeight);
					}
					imageProcessorResult = 
						processImageFillFrame(
							getRendererUtil().getImageDataRenderable(
								imageRenderersCache,
								renderer,
								dimension,
								ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null
								)
							);

					break;
				}
				case RETAIN_SHAPE:
				default:
				{
					Dimension dimension = null;
					if (
						imageElement.getRotation() == RotationEnum.LEFT
						|| imageElement.getRotation() == RotationEnum.RIGHT
						)
					{
						dimension = new Dimension(availableImageHeight, availableImageWidth);
					}
					else
					{
						dimension = new Dimension(availableImageWidth, availableImageHeight);
					}
					imageProcessorResult = 
						processImageRetainShape(
							getRendererUtil().getImageDataRenderable(
								imageRenderersCache,
								renderer,
								dimension,
								ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null
								)
							);

					break;
				}
			}
			
			return imageProcessorResult;
		}
		
		private InternalImageProcessorResult processImageClip(Graphics2DRenderable renderer) throws JRException
		{
			int normalWidth = availableImageWidth;
			int normalHeight = availableImageHeight;

			DimensionRenderable dimensionRenderer = imageRenderersCache.getDimensionRenderable((Renderable)renderer);
			Dimension2D dimension = dimensionRenderer == null ? null :  dimensionRenderer.getDimension(jasperReportsContext);
			if (dimension != null) {
				normalWidth = (int) dimension.getWidth();
				normalHeight = (int) dimension.getHeight();
			}

			int minWidth = 0;
			int minHeight = 0;
			int topOffset = 0;
			int leftOffset = 0;
			int bottomOffset = 0;
			int rightOffset = 0;
			int translateX = 0;
			int translateY = 0;
			short angle = 0;
			
			switch (imageElement.getRotation())
			{
				case LEFT :
					if (dimension == null)
					{
						normalWidth = availableImageHeight;
						normalHeight = availableImageWidth;
					}
					minWidth = Math.min(normalWidth, availableImageHeight);
					minHeight = Math.min(normalHeight, availableImageWidth);
					topOffset = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageHeight - normalWidth));
					leftOffset = (int)(ImageUtil.getYAlignFactor(imageElement) * (availableImageWidth - normalHeight));
					bottomOffset = (int)(ImageUtil.getXAlignFactor(imageElement) * (availableImageHeight - normalWidth));
					rightOffset = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageWidth - normalHeight));
					translateX = bottomOffset;
					translateY = leftOffset;
					angle = -90;
					break;
				case RIGHT :
					if (dimension == null)
					{
						normalWidth = availableImageHeight;
						normalHeight = availableImageWidth;
					}
					minWidth = Math.min(normalWidth, availableImageHeight);
					minHeight = Math.min(normalHeight, availableImageWidth);
					topOffset = (int)(ImageUtil.getXAlignFactor(imageElement) * (availableImageHeight - normalWidth));
					leftOffset = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageWidth - normalHeight));
					bottomOffset = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageHeight - normalWidth));
					rightOffset = (int)(ImageUtil.getYAlignFactor(imageElement) * (availableImageWidth - normalHeight));
					translateX = topOffset;
					translateY = rightOffset;
					angle = 90;
					break;
				case UPSIDE_DOWN :
					minWidth = Math.min(normalWidth, availableImageWidth);
					minHeight = Math.min(normalHeight, availableImageHeight);
					topOffset = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageHeight - normalHeight));
					leftOffset = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageWidth - normalWidth));
					bottomOffset = (int)(ImageUtil.getYAlignFactor(imageElement) * (availableImageHeight - normalHeight));
					rightOffset = (int)(ImageUtil.getXAlignFactor(imageElement) * (availableImageWidth - normalWidth));
					translateX = rightOffset;
					translateY = bottomOffset;
					angle = 180;
					break;
				case NONE :
				default :
					minWidth = Math.min(normalWidth, availableImageWidth);
					minHeight = Math.min(normalHeight, availableImageHeight);
					topOffset = (int)(ImageUtil.getYAlignFactor(imageElement) * (availableImageHeight - normalHeight));
					leftOffset = (int)(ImageUtil.getXAlignFactor(imageElement) * (availableImageWidth - normalWidth));
					bottomOffset = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageHeight - normalHeight));
					rightOffset = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageWidth - normalWidth));
					translateX = leftOffset;
					translateY = topOffset;
					angle = 0;
					break;
			}

			int dpi = getPropertiesUtil().getIntegerProperty(Renderable.PROPERTY_IMAGE_DPI, 72);
			double scale = dpi/72d;
			
			BufferedImage bi = 
				new BufferedImage(
					(int)(scale * minWidth), 
					(int)(scale * minHeight), 
					BufferedImage.TYPE_INT_ARGB
					);
			
			Graphics2D grx = bi.createGraphics();
			try
			{
				grx.scale(scale, scale);
				grx.clip(
					new Rectangle(
						0,
						0,
						minWidth,
						minHeight
						)
					);

				renderer.render(
					jasperReportsContext,
					grx,
					new Rectangle(
						translateX > 0 ? 0 : translateX,
						translateY > 0 ? 0 : translateY,
						normalWidth,
						normalHeight
						)
					);
			}
			finally
			{
				grx.dispose();
			}

			return 
				new InternalImageProcessorResult(
					JRImageLoader.getInstance(jasperReportsContext).loadBytesFromAwtImage(bi, ImageTypeEnum.PNG),
					topOffset < 0 ? 0 : topOffset, 
					leftOffset < 0 ? 0 : leftOffset, 
					bottomOffset < 0 ? 0 : bottomOffset, 
					rightOffset < 0 ? 0 : rightOffset,
					angle
					);
		}
		
		private InternalImageProcessorResult processImageFillFrame(DataRenderable renderer) throws JRException
		{
			short angle = 0;
			
			switch (imageElement.getRotation())
			{
				case LEFT:
					angle = -90;
					break;
				case RIGHT:
					angle = 90;
					break;
				case UPSIDE_DOWN:
					angle = 180;
					break;
				case NONE:
				default:
					angle = 0;
					break;
			}
			
			return 
				new InternalImageProcessorResult(
					renderer.getData(jasperReportsContext), 
					0, 
					0, 
					0, 
					0,
					angle
					);
		}
		
		private InternalImageProcessorResult processImageRetainShape(DataRenderable renderer) throws JRException
		{
			float normalWidth = availableImageWidth;
			float normalHeight = availableImageHeight;

			DimensionRenderable dimensionRenderer = imageRenderersCache.getDimensionRenderable((Renderable)renderer);
			Dimension2D dimension = dimensionRenderer == null ? null :  dimensionRenderer.getDimension(jasperReportsContext);
			if (dimension != null) {
				normalWidth = (int) dimension.getWidth();
				normalHeight = (int) dimension.getHeight();
			}

			float ratioX = 1f;
			float ratioY = 1f;

			int imageWidth = 0;
			int imageHeight = 0;
			
			int topOffset = 0;
			int leftOffset = 0;
			int bottomOffset = 0;
			int rightOffset = 0;

			short angle = 0;
	
			switch (imageElement.getRotation())
			{
				case LEFT:
					ratioX = availableImageWidth / normalHeight;
					ratioY = availableImageHeight / normalWidth;
					ratioX = ratioX < ratioY ? ratioX : ratioY;
					ratioY = ratioX;
					imageWidth = (int)(normalHeight * ratioX);
					imageHeight = (int)(normalWidth * ratioY);
					topOffset = (int) ((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageHeight - imageHeight));
					leftOffset = (int) (ImageUtil.getYAlignFactor(imageElement) * (availableImageWidth - imageWidth));
					bottomOffset = (int) (ImageUtil.getXAlignFactor(imageElement) * (availableImageHeight - imageHeight));
					rightOffset = (int) ((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageWidth - imageWidth));
					angle = -90;
					break;
				case RIGHT:
					ratioX = availableImageWidth / normalHeight;
					ratioY = availableImageHeight / normalWidth;
					ratioX = ratioX < ratioY ? ratioX : ratioY;
					ratioY = ratioX;
					imageWidth = (int)(normalHeight * ratioX);
					imageHeight = (int)(normalWidth * ratioY);
					topOffset = (int) (ImageUtil.getXAlignFactor(imageElement) * (availableImageHeight - imageHeight));
					leftOffset = (int) ((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageWidth - imageWidth));
					bottomOffset = (int) ((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageHeight - imageHeight));
					rightOffset = (int) (ImageUtil.getYAlignFactor(imageElement) * (availableImageWidth - imageWidth));
					angle = 90;
					break;
				case UPSIDE_DOWN:
					ratioX = availableImageWidth / normalWidth;
					ratioY = availableImageHeight / normalHeight;
					ratioX = ratioX < ratioY ? ratioX : ratioY;
					ratioY = ratioX;
					imageWidth = (int)(normalWidth * ratioX);
					imageHeight = (int)(normalHeight * ratioY);
					topOffset = (int) ((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageHeight - imageHeight));
					leftOffset = (int) ((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageWidth - imageWidth));
					bottomOffset = (int) (ImageUtil.getYAlignFactor(imageElement) * (availableImageHeight - imageHeight));
					rightOffset = (int) (ImageUtil.getXAlignFactor(imageElement) * (availableImageWidth - imageWidth));
					angle = 180;
					break;
				case NONE:
				default:
					ratioX = availableImageWidth / normalWidth;
					ratioY = availableImageHeight / normalHeight;
					ratioX = ratioX < ratioY ? ratioX : ratioY;
					ratioY = ratioX;
					imageWidth = (int)(normalWidth * ratioX);
					imageHeight = (int)(normalHeight * ratioY);
					topOffset = (int) (ImageUtil.getYAlignFactor(imageElement) * (availableImageHeight - imageHeight));
					leftOffset = (int) (ImageUtil.getXAlignFactor(imageElement) * (availableImageWidth - imageWidth));
					bottomOffset = (int) ((1f - ImageUtil.getYAlignFactor(imageElement)) * (availableImageHeight - imageHeight));
					rightOffset = (int) ((1f - ImageUtil.getXAlignFactor(imageElement)) * (availableImageWidth - imageWidth));
					angle = 0;
					break;
			}
			
			return 
				new InternalImageProcessorResult(
					renderer.getData(jasperReportsContext), 
					topOffset, 
					leftOffset, 
					bottomOffset, 
					rightOffset,
					angle
					);
		}
	}

	private class InternalImageProcessorResult
	{
		private final byte[] imageData;
//		private final int topOffset;
//		private final int leftOffset;
//		private final int bottomOffset;
//		private final int rightOffset;
		private final short angle;
		
		protected InternalImageProcessorResult(
			byte[] imageData,
			int topOffset,
			int leftOffset,
			int bottomOffset,
			int rightOffset,
			short angle
			)
		{
			this.imageData = imageData;
//			this.topOffset = topOffset;
//			this.leftOffset = leftOffset;
//			this.bottomOffset = bottomOffset;
//			this.rightOffset = rightOffset;
			this.angle = angle;
		}
	}


	protected HSSFCellStyle getLoadedCellStyle(StyleInfo style) {
		HSSFCellStyle cellStyle = loadedCellStyles.get(style);
		if (cellStyle == null) {
			
			cellStyle = workbook.createCellStyle();
			cellStyle.setFillForegroundColor(style.backcolor);
			cellStyle.setFillPattern(style.mode);
			cellStyle.setAlignment(style.horizontalAlignment);
			cellStyle.setVerticalAlignment(style.verticalAlignment);
			cellStyle.setRotation(style.rotation);
			if(style.font != null)
			{
				cellStyle.setFont(style.font);
			}
			cellStyle.setWrapText(style.lcWrapText);
			cellStyle.setLocked(style.lcCellLocked);
			cellStyle.setHidden(style.lcCellHidden);
			cellStyle.setShrinkToFit(style.lcShrinkToFit);

			if (style.hasDataFormat()) {
				cellStyle.setDataFormat(style.getDataFormat());
			}

			if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBorder) && style.box != null) {
				BoxStyle box = style.box;
				if (box.borderStyle[BoxStyle.TOP] != null)
					cellStyle.setBorderTop(box.borderStyle[BoxStyle.TOP]);
				cellStyle.setTopBorderColor(box.borderColour[BoxStyle.TOP]);
				if (box.borderStyle[BoxStyle.LEFT] != null)
					cellStyle.setBorderLeft(box.borderStyle[BoxStyle.LEFT]);
				cellStyle.setLeftBorderColor(box.borderColour[BoxStyle.LEFT]);
				if (box.borderStyle[BoxStyle.BOTTOM] != null)
					cellStyle.setBorderBottom(box.borderStyle[BoxStyle.BOTTOM]);
				cellStyle.setBottomBorderColor(box.borderColour[BoxStyle.BOTTOM]);
				if (box.borderStyle[BoxStyle.RIGHT] != null)
					cellStyle.setBorderRight(box.borderStyle[BoxStyle.RIGHT]);
				cellStyle.setRightBorderColor(box.borderColour[BoxStyle.RIGHT]);
			}
			loadedCellStyles.put(style, cellStyle);
		}
		return cellStyle;
	}

	protected HSSFCellStyle getLoadedCellStyle(
		FillPatternType mode,
		short backcolor,
		HorizontalAlignment horizontalAlignment,
		VerticalAlignment verticalAlignment,
		short rotation,
		HSSFFont font,
		BoxStyle box,
		boolean isCellLocked,
		boolean isCellHidden,
		boolean isShrinkToFit
		) 
	{
		return getLoadedCellStyle(new StyleInfo(mode, backcolor, horizontalAlignment, verticalAlignment, rotation, font, box, true, isCellLocked, isCellHidden, isShrinkToFit));
	}

	/**
	 *
	 */
	protected static BorderStyle getBorderStyle(JRPen pen) {
		float lineWidth = pen.getLineWidth();

		if (lineWidth > 0f) {
			switch (pen.getLineStyle()) {
				case DOUBLE : {
					return BorderStyle.DOUBLE;
				}
				case DOTTED : {
					return BorderStyle.DOTTED;
				}
				case DASHED : {
					if (lineWidth >= 1f) {
						return BorderStyle.MEDIUM_DASHED;
					}
					return BorderStyle.DASHED;
				}
				case SOLID :
				default : {
					if (lineWidth >= 2f) {
						return BorderStyle.THICK;
					}
					else if (lineWidth >= 1f) {
						return BorderStyle.MEDIUM;
					} else if (lineWidth >= 0.5f) {
						return BorderStyle.THIN;
					}
					return BorderStyle.HAIR;
				}
			}
		}
		return BorderStyle.NONE;
	}
	
	@Override
	protected void exportFrame(JRPrintFrame frame) throws JRException {
		
		for (Object element : frame.getElements()) {
			if (element instanceof JRPrintLine) {
				exportLine((JRPrintLine)element);
			} else if (element instanceof JRPrintRectangle) {
				exportRectangle((JRPrintRectangle)element);
			} else if (element instanceof JRPrintEllipse) {
				exportRectangle((JRPrintEllipse)element);
			} else if (element instanceof JRPrintImage) {
				exportImage((JRPrintImage) element);
			} else if (element instanceof JRPrintText) {
				exportText((JRPrintText)element);
			} else if (element instanceof JRPrintFrame) {
				exportFrame((JRPrintFrame) element);
			} else if (element instanceof JRGenericPrintElement) {
				exportGenericElement((JRGenericPrintElement) element);
			}
		}
	}


	@Override
	protected void exportGenericElement(JRGenericPrintElement element) throws JRException {
		String currentColumnName = element.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
		if(currentColumnName != null && currentColumnName.length() > 0) {
			boolean repeatValue = getPropertiesUtil().getBooleanProperty(element, PROPERTY_REPEAT_VALUE, false);
			int colIndex = columnNamesMap.get(currentColumnName);
			
			setColumnName(currentColumnName);
			adjustColumnWidth(currentColumnName, element.getWidth(), ((JRXlsExporterNature)nature).getColumnAutoFit(element));
			adjustRowHeight(element.getHeight(), Boolean.TRUE.equals(((JRXlsExporterNature)nature).getRowAutoFit(element)));
			
	
			GenericElementXlsMetadataHandler handler = 
				(GenericElementXlsMetadataHandler)GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
					element.getGenericType(), 
					XLS_EXPORTER_KEY);
	
			if (handler != null) {
				rowIndex = handler.exportElement(
						exporterContext, 
						element, 
						currentRow, 
						repeatedValues, 
						columnNames,
						columnNamesMap,
						currentColumnName, 
						colIndex, 
						rowIndex, 
						repeatValue
						);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("No XLS Metadata generic element handler for " 
							+ element.getGenericType());
				}
			}
		}
	}


	@Override
	protected ExporterNature getNature() {
		return nature;
	}


	@Override
	public String getExporterKey() {
		return XLS_EXPORTER_KEY;
	}

	@Override
	public String getExporterPropertiesPrefix() {
		return XLS_EXPORTER_PROPERTIES_PREFIX;
	}

	protected void setColumnName(String currentColumnName) {
		// when no columns are provided, build the column names list as they are retrieved from the report element property
		if (!hasDefinedColumns)
		{
			if (currentColumnName != null && currentColumnName.length() > 0 && !columnNames.contains(currentColumnName))
			{
				columnNamesMap.put( currentColumnName, columnNames.size());
				columnNames.add(currentColumnName);
			}
		}
	}
	
	/**
	 * Writes the header column names
	 */
	@Override
	protected void writeReportHeader() throws JRException {
		row = sheet.createRow(0);
		for(int i = 0; i< columnNames.size(); i++) {
			String columnName = columnNames.get(i);
			cell = row.createCell(i);
			cell.setCellType(CellType.STRING);
			cell.setCellValue(new HSSFRichTextString(columnName));
		}
	}
	
	@Override
	protected void setSheetName(String sheetName) {
		workbook.setSheetName(workbook.getSheetIndex(sheet) , sheetName);
	}
	
	@Override
	protected void setFreezePane(int rowIndex, int colIndex) {
		if(rowIndex > 0 || colIndex > 0)
		{
			sheet.createFreezePane(Math.max(0, colIndex), Math.max(0, rowIndex));
		}
	}
	
	@Override
	protected void setAutoFilter(String autoFilterRange) {
		//TODO: recalculate autoFilterRange depending on exported columns
//		sheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
	}


	@Override
	protected void setRowLevels(XlsRowLevelInfo levelInfo, String level) {
		//TODO: recalculate row levels depending on exported columns
//		if(levelInfo != null) {
//			Map<String, Integer> levelMap = levelInfo.getLevelMap();
//			if(levelMap != null && levelMap.size() > 0) {
//				for(String l : levelMap.keySet()) {
//					if (level == null || l.compareTo(level) >= 0) {
//						Integer startIndex = levelMap.get(l);
//						if(levelInfo.getEndIndex() >= startIndex) {
//							sheet.groupRow(startIndex, levelInfo.getEndIndex());
//						}
//					}
//				}
//				sheet.setRowSumsBelow(false);
//			}
//		}
	}


	private final short getSuitablePaperSize() {

		if (pageFormat == null) {
			return -1;
		}
		long width = 0;
		long height = 0;
		short ps = -1;

		if ((pageFormat.getPageWidth() != 0) && (pageFormat.getPageHeight() != 0)) {

			double dWidth = (pageFormat.getPageWidth() / 72.0);
			double dHeight = (pageFormat.getPageHeight() / 72.0);

			height = Math.round(dHeight * 25.4);
			width = Math.round(dWidth * 25.4);

			// Compare to ISO 216 A-Series (A3-A5). All other ISO 216 formats
			// not supported by POI Api yet.
			// A3 papersize also not supported by POI Api yet.
			for (int i = 4; i < 6; i++) {
				int w = calculateWidthForDinAN(i);
				int h = calculateHeightForDinAN(i);

				if (((w == width) && (h == height)) || ((h == width) && (w == height))) {
					if (i == 4) {
						ps = HSSFPrintSetup.A4_PAPERSIZE;
					} else if (i == 5) {
						ps = HSSFPrintSetup.A5_PAPERSIZE;
					}
					break;
				}
			}
			
			//envelope sizes
			if (ps == -1) {
				// ISO 269 sizes - "Envelope DL" (110 x 220 mm)
				if (((width == 110) && (height == 220)) || ((width == 220) && (height == 110))) {
					ps = HSSFPrintSetup.ENVELOPE_DL_PAPERSIZE;
				}
			}

			// Compare to common North American Paper Sizes (ANSI X3.151-1987).
			if (ps == -1) {
				// ANSI X3.151-1987 - "Letter" (216 x 279 mm)
				if (((width == 216) && (height == 279)) || ((width == 279) && (height == 216))) {
					ps = HSSFPrintSetup.LETTER_PAPERSIZE;
				}
				// ANSI X3.151-1987 - "Legal" (216 x 356 mm)
				if (((width == 216) && (height == 356)) || ((width == 356) && (height == 216))) {
					ps = HSSFPrintSetup.LEGAL_PAPERSIZE;
				}
				// ANSI X3.151-1987 - "Executive" (190 x 254 mm)
				else if (((width == 190) && (height == 254)) || ((width == 254) && (height == 190))) {
					ps = HSSFPrintSetup.EXECUTIVE_PAPERSIZE;
				}
				// ANSI X3.151-1987 - "Ledger/Tabloid" (279 x 432 mm)
				// Not supported by POI Api yet.
			}
		}
		return ps;
	}
	
	private HorizontalAlignment getHorizontalAlignment(TextAlignHolder alignment) {
		switch (alignment.horizontalAlignment) 
		{
			case RIGHT:
				return HorizontalAlignment.RIGHT;
			case CENTER:
				return HorizontalAlignment.CENTER;
			case JUSTIFIED:
				return HorizontalAlignment.JUSTIFY;
			case LEFT:
			default:
				return HorizontalAlignment.LEFT;
		}
	}

	private VerticalAlignment getVerticalAlignment(TextAlignHolder alignment) {
		switch (alignment.verticalAlignment) {
			case BOTTOM:
				return VerticalAlignment.BOTTOM;
			case MIDDLE:
				return VerticalAlignment.CENTER;
			case JUSTIFIED:
				return VerticalAlignment.JUSTIFY;
			case TOP:
			default:
				return VerticalAlignment.TOP;
		}
	}

	private short getRotation(TextAlignHolder alignment) {
		switch (alignment.rotation) {
			case LEFT:
				return 90;
			case RIGHT:
				return -90;
			case UPSIDE_DOWN:
			case NONE:
			default:
				return 0;
		}
	}

	/**
	 *
	 */
	protected HSSFColor getWorkbookColor(Color awtColor) {
		HSSFColor color = null;
		if(awtColor != null) {
			byte red = (byte)awtColor.getRed();
			byte green = (byte)awtColor.getGreen();
			byte blue = (byte)awtColor.getBlue();

			XlsExporterConfiguration configuration = getCurrentConfiguration();
			
			if (configuration.isCreateCustomPalette()) {
				try {
					color = palette.findColor(red,green, blue) != null
						? palette.findColor(red,green, blue)
						: palette.addColor(red,green, blue);
				} catch(Exception e) {
					if(customColorIndex < MAX_COLOR_INDEX) {
						palette.setColorAtIndex(customColorIndex, red, green, blue);
						color = palette.getColor(customColorIndex++);
					} else {
						color = palette.findSimilarColor(red, green, blue);
					}
				}
			}
		}
		return color == null ? getNearestColor(awtColor) : color;
	}

	/**
	 *
	 */
	protected static HSSFColor getNearestColor(Color awtColor)
	{
		HSSFColor color = hssfColorsCache.get(awtColor);		
		if (color == null) {
			Map<?,?> triplets = HSSFColor.getTripletHash();
			if (triplets != null) {
				Collection<?> keys = triplets.keySet();
				if (keys != null && keys.size() > 0) {
					Object key = null;
					HSSFColor crtColor = null;
					short[] rgb = null;
					int diff = 0;
					int minDiff = 999;
					for (Iterator<?> it = keys.iterator(); it.hasNext();) {
						key = it.next();
						crtColor = (HSSFColor) triplets.get(key);
						rgb = crtColor.getTriplet();
						diff = Math.abs(rgb[0] - awtColor.getRed()) + Math.abs(rgb[1] - awtColor.getGreen()) + Math.abs(rgb[2] - awtColor.getBlue());
						if (diff < minDiff) {
							minDiff = diff;
							color = crtColor;
						}
					}
				}
			}
			hssfColorsCache.put(awtColor, color);
		}
		return color;
	}

	/**
	 *
	 */
	protected HSSFFont getLoadedFont(JRFont font, short forecolor, Map<Attribute,Object> attributes, Locale locale) {

		HSSFFont cellFont = null;
		String fontName = fontUtil.getExportFontFamily(font.getFontName(), locale, getExporterKey());

		boolean isFontSizeFixEnabled = getCurrentItemConfiguration().isFontSizeFixEnabled();
		
		short superscriptType = HSSFFont.SS_NONE;
		
		if( attributes != null && attributes.get(TextAttribute.SUPERSCRIPT) != null) {
			Object value = attributes.get(TextAttribute.SUPERSCRIPT);
			if(TextAttribute.SUPERSCRIPT_SUPER.equals(value)) {
				superscriptType = HSSFFont.SS_SUPER;
			} else if(TextAttribute.SUPERSCRIPT_SUB.equals(value)) {
				superscriptType = HSSFFont.SS_SUB;
			}
		}
		for (int i = 0; i < loadedFonts.size(); i++) {
			HSSFFont cf = (HSSFFont)loadedFonts.get(i);
			short fontSize = (short)font.getFontSize();
			if (isFontSizeFixEnabled) {
				fontSize -= 1;
			}
			if (
				cf.getFontName().equals(fontName) &&
				(cf.getColor() == forecolor) &&
				(cf.getFontHeightInPoints() == fontSize) &&
				((cf.getUnderline() == HSSFFont.U_SINGLE)?(font.isUnderline()):(!font.isUnderline())) &&
				(cf.getStrikeout() == font.isStrikeThrough()) &&
				(cf.getBold() == font.isBold()) &&
				(cf.getItalic() == font.isItalic()) &&
				(cf.getTypeOffset() == superscriptType)
				) {
				cellFont = cf;
				break;
			}
		}

		if (cellFont == null) {
			
			cellFont = workbook.createFont();
			cellFont.setFontName(fontName);
			cellFont.setColor(forecolor);
			short fontSize = (short)font.getFontSize();
			if (isFontSizeFixEnabled) {
				fontSize -= 1;
			}
			cellFont.setFontHeightInPoints(fontSize);

			if (font.isUnderline()) {
				cellFont.setUnderline(HSSFFont.U_SINGLE);
			}
			if (font.isStrikeThrough()) {
				cellFont.setStrikeout(true);
			}
			if (font.isBold()) {
				cellFont.setBold(true);
			}
			if (font.isItalic()) {
				cellFont.setItalic(true);
			}

			cellFont.setTypeOffset(superscriptType);
			loadedFonts.add(cellFont);
		}
		return cellFont;
	}
	
	@Override
	protected void createSheet(CutsInfo xCuts, SheetInfo sheetInfo) {
	}

	@Override
	protected void setRowHeight(int rowIndex, int lastRowHeight, Cut yCut, XlsRowLevelInfo levelInfo) throws JRException {
	}

	@Override
	protected void addRowBreak(int rowIndex)
	{
		sheet.setRowBreak(rowIndex);
	}

	@Override
	protected void setColumnWidth(int col, int width, boolean autoFit) {
	}
	
	@Override
	protected void updateSheet(JRPrintElement element)
	{
		JRXlsMetadataExporterNature xlsNature = (JRXlsMetadataExporterNature)nature;
		configureDefinedNames(xlsNature, element);
		updatePageMargin(xlsNature.getPrintPageTopMargin(element), PageMargin.TOP);
		updatePageMargin(xlsNature.getPrintPageLeftMargin(element), PageMargin.LEFT);
		updatePageMargin(xlsNature.getPrintPageBottomMargin(element), PageMargin.BOTTOM);
		updatePageMargin(xlsNature.getPrintPageRightMargin(element), PageMargin.RIGHT);
		updateHeaderFooterMargin(xlsNature.getPrintHeaderMargin(element), true);
		updateHeaderFooterMargin(xlsNature.getPrintFooterMargin(element), false);

		HSSFHeader header = sheet.getHeader();
		String sheetHeaderLeft = xlsNature.getSheetHeaderLeft(element);
		if(sheetHeaderLeft != null)
		{
			header.setLeft(sheetHeaderLeft);
		}
		
		String sheetHeaderCenter = xlsNature.getSheetHeaderCenter(element);
		if(sheetHeaderCenter != null)
		{
			header.setCenter(sheetHeaderCenter);
		}
		
		String sheetHeaderRight = xlsNature.getSheetHeaderRight(element);
		if(sheetHeaderRight != null)
		{
			header.setRight(sheetHeaderRight);
		}
		
		HSSFFooter footer = sheet.getFooter();
		String sheetFooterLeft = xlsNature.getSheetFooterLeft(element);
		if(sheetFooterLeft != null)
		{
			footer.setLeft(sheetFooterLeft);
		}
		
		String sheetFooterCenter = xlsNature.getSheetFooterCenter(element);
		if(sheetFooterCenter != null)
		{
			footer.setCenter(sheetFooterCenter);
		}
		
		String sheetFooterRight = xlsNature.getSheetFooterRight(element);
		if(sheetFooterRight != null)
		{
			footer.setRight(sheetFooterRight);
		}
	}

	private void updatePageMargin(Integer marginValue, PageMargin marginType)
	{
		if(marginValue != null)
		{
			double margin = LengthUtil.inch(marginValue);
			if(margin > sheet.getMargin(marginType))
			{
				sheet.setMargin(marginType, margin);
			}
		}
	}
	
	private void updateHeaderFooterMargin(Integer marginValue, boolean isHeaderMargin)
	{
		if(marginValue != null)
		{
			HSSFPrintSetup printSetup = sheet.getPrintSetup();
			double margin = LengthUtil.inch(marginValue);
			if(isHeaderMargin)
			{
				if(margin > printSetup.getHeaderMargin())
				{
					printSetup.setHeaderMargin(margin);
				}
			}
			else
			{
				if(margin > printSetup.getFooterMargin())
				{
					printSetup.setFooterMargin(margin);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	protected class BoxStyle {
		protected static final int TOP = 0;
		protected static final int LEFT = 1;
		protected static final int BOTTOM = 2;
		protected static final int RIGHT = 3;

		protected BorderStyle[] borderStyle = new BorderStyle[] {BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE};
		protected short[] borderColour = new short[4];
		private int hash;

		public BoxStyle(int side, JRPen pen) {
			borderStyle[side] = JRXlsMetadataExporter.getBorderStyle(pen);
			borderColour[side] = JRXlsMetadataExporter.this.getWorkbookColor(pen.getLineColor()).getIndex();
			hash = computeHash();
		}

		public BoxStyle(JRPrintElement element) {
			if(element != null)
			{
				if (element instanceof JRBoxContainer) {
					setBox(((JRBoxContainer)element).getLineBox());
				}
				if (element instanceof JRCommonGraphicElement) {
					setPen(((JRCommonGraphicElement)element).getLinePen());
				}
			}
		}

		public void setBox(JRLineBox box) {
			borderStyle[TOP] = JRXlsMetadataExporter.getBorderStyle(box.getTopPen());
			borderColour[TOP] = JRXlsMetadataExporter.this.getWorkbookColor(box.getTopPen().getLineColor()).getIndex();

			borderStyle[BOTTOM] = JRXlsMetadataExporter.getBorderStyle(box.getBottomPen());
			borderColour[BOTTOM] = JRXlsMetadataExporter.this.getWorkbookColor(box.getBottomPen().getLineColor()).getIndex();

			borderStyle[LEFT] = JRXlsMetadataExporter.getBorderStyle(box.getLeftPen());
			borderColour[LEFT] = JRXlsMetadataExporter.this.getWorkbookColor(box.getLeftPen().getLineColor()).getIndex();

			borderStyle[RIGHT] = JRXlsMetadataExporter.getBorderStyle(box.getRightPen());
			borderColour[RIGHT] = JRXlsMetadataExporter.this.getWorkbookColor(box.getRightPen().getLineColor()).getIndex();

			hash = computeHash();
		}

		public void setPen(JRPen pen) {
			if (
				borderStyle[TOP] == BorderStyle.NONE
				&& borderStyle[LEFT] == BorderStyle.NONE
				&& borderStyle[BOTTOM] == BorderStyle.NONE
				&& borderStyle[RIGHT] == BorderStyle.NONE
				) {
				BorderStyle style = JRXlsMetadataExporter.getBorderStyle(pen);
				short colour = JRXlsMetadataExporter.this.getWorkbookColor(pen.getLineColor()).getIndex();

				borderStyle[TOP] = style;
				borderStyle[BOTTOM] = style;
				borderStyle[LEFT] = style;
				borderStyle[RIGHT] = style;

				borderColour[TOP] = colour;
				borderColour[BOTTOM] = colour;
				borderColour[LEFT] = colour;
				borderColour[RIGHT] = colour;
			}

			hash = computeHash();
		}

		private int computeHash() {
			int hashCode = (borderStyle[TOP] == null ? 0 : borderStyle[TOP].hashCode());
			hashCode = 31*hashCode + borderColour[TOP];
			hashCode = 31*hashCode + (borderStyle[BOTTOM] == null ? 0 : borderStyle[BOTTOM].hashCode());
			hashCode = 31*hashCode + borderColour[BOTTOM];
			hashCode = 31*hashCode + (borderStyle[LEFT] == null ? 0 : borderStyle[LEFT].hashCode());
			hashCode = 31*hashCode + borderColour[LEFT];
			hashCode = 31*hashCode + (borderStyle[RIGHT] == null ? 0 : borderStyle[RIGHT].hashCode());
			hashCode = 31*hashCode + borderColour[RIGHT];
			return hashCode;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object o) {
			BoxStyle b = (BoxStyle) o;

			return
				b.borderStyle[TOP] == borderStyle[TOP] &&
				b.borderColour[TOP] == borderColour[TOP] &&
				b.borderStyle[BOTTOM] == borderStyle[BOTTOM] &&
				b.borderColour[BOTTOM] == borderColour[BOTTOM] &&
				b.borderStyle[LEFT] == borderStyle[LEFT] &&
				b.borderColour[LEFT] == borderColour[LEFT] &&
				b.borderStyle[RIGHT] == borderStyle[RIGHT] &&
				b.borderColour[RIGHT] == borderColour[RIGHT];
		}

		@Override
		public String toString() {
			return "(" +
				borderStyle[TOP] + "/" + borderColour[TOP] + "," +
				borderStyle[BOTTOM] + "/" + borderColour[BOTTOM] + "," +
				borderStyle[LEFT] + "/" + borderColour[LEFT] + "," +
				borderStyle[RIGHT] + "/" + borderColour[RIGHT] + ")";
		}
	}
	

	/**
	 * 
	 */
	protected class StyleInfo {
		protected final FillPatternType mode;
		protected final short backcolor;
		protected final HorizontalAlignment horizontalAlignment;
		protected final VerticalAlignment verticalAlignment;
		protected final short rotation;
		protected final HSSFFont font;
		protected final BoxStyle box;
		protected final boolean lcWrapText;
		protected final boolean lcCellLocked;
		protected final boolean lcCellHidden;
		protected final boolean lcShrinkToFit;
		private short lcDataFormat = -1;
		private int hashCode;
	
		public StyleInfo(
			FillPatternType mode,
			short backcolor,
			HorizontalAlignment horizontalAlignment,
			VerticalAlignment verticalAlignment,
			short rotation,
			HSSFFont font,
			BoxStyle box,
			boolean wrapText,
			boolean cellLocked,
			boolean cellHidden,
			boolean shrinkToFit
			) 
		{
			this.mode = mode;
			this.backcolor = backcolor;
			this.horizontalAlignment = horizontalAlignment;
			this.verticalAlignment = verticalAlignment;
			this.rotation = rotation;
			this.font = font;
	
			this.box = box;
			this.lcWrapText = shrinkToFit ? false : wrapText;
			this.lcCellLocked = cellLocked;
			this.lcCellHidden = cellHidden;
			this.lcShrinkToFit = shrinkToFit;
			hashCode = computeHash();
		}
	
		@SuppressWarnings("deprecation")
		protected int computeHash() {
			int hash = mode.hashCode();
			hash = 31*hash + backcolor;
			hash = 31*hash + horizontalAlignment.hashCode();
			hash = 31*hash + verticalAlignment.hashCode();
			hash = 31*hash + rotation;
			hash = 31*hash + (font == null ? 0 : font.getIndexAsInt());
			hash = 31*hash + (box == null ? 0 : box.hashCode());
			hash = 31*hash + lcDataFormat;
			hash = 31*hash + (lcWrapText ? 0 : 1);
			hash = 31*hash + (lcCellLocked ? 0 : 1);
			hash = 31*hash + (lcCellHidden ? 0 : 1);
			hash = 31*hash + (lcShrinkToFit ? 0 : 1);
			return hash;
		}
	
		public void setDataFormat(short dataFormat) {
			this.lcDataFormat = dataFormat;
			hashCode = computeHash();
		}
	
		public boolean hasDataFormat() {
			return lcDataFormat != -1;
		}
	
		public short getDataFormat() {
			return lcDataFormat;
		}
	
		@Override
		public int hashCode() {
			return hashCode;
		}
	
		@SuppressWarnings("deprecation")
		@Override
		public boolean equals(Object o) {
			StyleInfo s = (StyleInfo) o;
	
			return s.mode == mode
					&& s.backcolor == backcolor
					&& s.horizontalAlignment == horizontalAlignment
					&& s.verticalAlignment == verticalAlignment
					&& s.rotation == rotation
					&& (s.font == null ? font == null : (font != null && s.font.getIndexAsInt() == font.getIndexAsInt()))
					&& (s.box == null ? box == null : (box != null && s.box.equals(box)))
					&& s.rotation == rotation && s.lcWrapText == lcWrapText 
					&& s.lcCellLocked == lcCellLocked && s.lcCellHidden == lcCellHidden
					&& s.lcShrinkToFit == lcShrinkToFit;	//FIXME should dataformat be part of equals? it is part of toString()...
		}
	
		@Override
		public String toString() {
			return "(" +
				mode + "," + backcolor + "," +
				horizontalAlignment + "," + verticalAlignment + "," +
				rotation + "," + font + "," +
				box + "," + lcDataFormat + "," + lcWrapText + "," + lcCellLocked + "," + lcCellHidden + "," + lcShrinkToFit + ")";
		}
	}
	
	
	protected class CellSettings 
	{
		private CellType cellType;
		private HSSFCellStyle cellStyle;
		private Object cellValue;
		private String formula;
		private Hyperlink link;
		
		public CellSettings() 
		{
		}
		
		public CellSettings(HSSFCellStyle cellStyle) 
		{
			this(CellType.BLANK, cellStyle, null);
		}
		
		public CellSettings(
			CellType cellType,
			HSSFCellStyle cellStyle,
			Object cellValue
			) 
		{
			this(cellType, cellStyle, cellValue, null);
		}
		
		public CellSettings(
			CellType cellType,
			HSSFCellStyle cellStyle,
			Object cellValue,
			String formula
			) 
		{
			this(cellType, cellStyle, cellValue, formula, null);
		}
		
		public CellSettings(
			CellType cellType,
			HSSFCellStyle cellStyle,
			Object cellValue,
			String formula,
			Hyperlink link
			) 
		{
			this.cellType = cellType;
			this.cellStyle = cellStyle;
			this.cellValue = cellValue;
			this.formula = formula;
			this.link = link;
		}
		
		public HSSFCellStyle getCellStyle() {
			return cellStyle;
		}
		
		public void setCellStyle(HSSFCellStyle cellStyle) {
			this.cellStyle = cellStyle;
		}
		
		public CellType getCellType() {
			return cellType;
		}
		
		public void setCellType(CellType cellType) {
			this.cellType = cellType;
		}
		
		public Object getCellValue() {
			return cellValue;
		}
		
		public void setCellValue(Object cellValue) {
			this.cellValue = cellValue;
		}
		
		public String getFormula() {
			return formula;
		}
		
		public void setFormula(String formula) {
			this.formula = formula;
		}
		
		public Hyperlink getLink() {
			return link;
		}
		
		public void setLink(Hyperlink link) {
			this.link = link;
		}
		
		public void importValues(				
			CellType cellType,
			HSSFCellStyle cellStyle,
			Object cellValue
			) 
		{
			this.importValues(cellType, cellStyle, cellValue, null);
		}
		
		public void importValues(				
			CellType cellType,
			HSSFCellStyle cellStyle,
			Object cellValue,
			String formula
			) 
		{
			this.importValues(cellType, cellStyle, cellValue, formula, null);
		}
		
		public void importValues(				
			CellType cellType,
			HSSFCellStyle cellStyle,
			Object cellValue,
			String formula,
			Hyperlink link
			) 
		{
			if(!CellType.FORMULA.equals(cellType))
			{
				this.cellType = cellType;
			}
			this.cellStyle = cellStyle;
			this.cellValue = cellValue;
			this.formula = formula;
			this.link = link;
		}
	}
}

class ImageSettings {

	private int index;
	private ClientAnchor.AnchorType anchorType;
	
	public ImageSettings() {
	}
	
	public ImageSettings(int index, ClientAnchor.AnchorType anchorType) {
		this.index = index;
		this.anchorType = anchorType;
	}

	public int getIndex() {
		return index;
	}
	
	public ClientAnchor.AnchorType getAnchorType() {
		return anchorType;
	}
}
