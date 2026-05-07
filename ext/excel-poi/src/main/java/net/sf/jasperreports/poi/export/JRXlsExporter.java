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
 * Wolfgang - javabreak@users.sourceforge.net
 * Mario Daepp - mdaepp@users.sourceforge.net
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
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
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRCommonGraphicElement;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.export.Cut;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter.SheetInfo.SheetPrintSettings;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.OccupiedGridCell;
import net.sf.jasperreports.engine.export.XlsRowLevelInfo;
import net.sf.jasperreports.engine.export.data.BooleanTextValue;
import net.sf.jasperreports.engine.export.data.DateTextValue;
import net.sf.jasperreports.engine.export.data.NumberTextValue;
import net.sf.jasperreports.engine.export.data.StringTextValue;
import net.sf.jasperreports.engine.export.data.TextValue;
import net.sf.jasperreports.engine.export.data.TextValueHandler;
import net.sf.jasperreports.engine.export.type.ImageAnchorTypeEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.ExifOrientationEnum;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.export.XlsExporterConfiguration;
import net.sf.jasperreports.export.XlsReportConfiguration;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;


/**
 * Exports a JasperReports document to XLS format. It has binary output type and exports the document to
 * a grid-based layout.
 * 
 * @see net.sf.jasperreports.engine.export.JRXlsAbstractExporter
 * @see net.sf.jasperreports.export.XlsExporterConfiguration
 * @see net.sf.jasperreports.export.XlsReportConfiguration
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRXlsExporter extends JRXlsAbstractExporter<XlsReportConfiguration, XlsExporterConfiguration, JRXlsExporterContext>
{

	private static final Log log = LogFactory.getLog(JRXlsExporter.class);
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String XLS_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "xls";
	public static short MAX_COLOR_INDEX = 56;
	public static short MIN_COLOR_INDEX = 10;	/* Indexes from 0 to 9 are reserved */
	private static short A2_PAPERSIZE = (short)66; 	/* A2_PAPERSIZE defined locally since it is not declared in HSSFPrintSetup */
	
	private static Map<HSSFColor, short[]> hssfColorsRgbs;
	
	static
	{
		Map<String, HSSFColor> hssfColors = HSSFColor.getTripletHash();
		hssfColorsRgbs = new LinkedHashMap<>();
		for (HSSFColor color : hssfColors.values())
		{
			hssfColorsRgbs.put(color, color.getTriplet());
		}
	}

	protected Map<StyleInfo,HSSFCellStyle> loadedCellStyles = new HashMap<>();
	protected Map<String,List<Hyperlink>> anchorLinks = new HashMap<>();
	protected Map<Integer,List<Hyperlink>> pageLinks = new HashMap<>();
	protected Map<String,HSSFName> anchorNames = new HashMap<>();
	protected Map<HSSFSheet,List<Integer>> autofitColumns = new HashMap<>();

	/**
	 *
	 */
	protected HSSFWorkbook workbook;
	protected HSSFSheet sheet;
	protected HSSFRow row;
	protected HSSFCell cell;
	protected HSSFCellStyle emptyCellStyle;
	protected CreationHelper createHelper;
	private HSSFPalette palette = null;
	private Map<Color,HSSFColor> hssfColorsCache = new HashMap<>();

	/**
	 *
	 */
	protected short whiteIndex = HSSFColor.HSSFColorPredefined.WHITE.getIndex();
	protected short blackIndex = HSSFColor.HSSFColorPredefined.BLACK.getIndex();
	protected short customColorIndex = MIN_COLOR_INDEX;

	protected FillPatternType backgroundMode = FillPatternType.SOLID_FOREGROUND;

	protected HSSFDataFormat dataFormat;

	protected HSSFPatriarch patriarch;
	
	protected Map<HSSFCell, String> formulaCellsMap;
	
	protected class ExporterContext extends BaseExporterContext implements JRXlsExporterContext
	{
	}

	
	/**
	 * @see #JRXlsExporter(JasperReportsContext)
	 */
	public JRXlsExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JRXlsExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
		
		maxColumnIndex = 255;
	}


	@Override
	protected Class<XlsExporterConfiguration> getConfigurationInterface()
	{
		return XlsExporterConfiguration.class;
	}


	@Override
	protected Class<XlsReportConfiguration> getItemConfigurationInterface()
	{
		return XlsReportConfiguration.class;
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
			new JRXlsExporterNature(
				jasperReportsContext, 
				filter, 
				configuration.isIgnoreGraphics(), 
				configuration.isIgnorePageMargins()
				);
	}


	@Override
	protected void openWorkbook(OutputStream os)
	{
		XlsExporterConfiguration configuration = getCurrentConfiguration();
		String lcWorkbookTemplate = workbookTemplate == null ? configuration.getWorkbookTemplate() : workbookTemplate;
		if (lcWorkbookTemplate == null)
		{
			workbook = new HSSFWorkbook();
		}
		else
		{
			InputStream templateIs = null;
			try 
			{
				templateIs = getRepository().getInputStreamFromLocation(lcWorkbookTemplate);
				if (templateIs == null)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_TEMPLATE_NOT_FOUND,  
							new Object[]{lcWorkbookTemplate} 
							);
				}
				else
				{
					workbook = new HSSFWorkbook(new POIFSFileSystem(templateIs));
					boolean keepSheets = keepTemplateSheets == null ? configuration.isKeepWorkbookTemplateSheets() : keepTemplateSheets;
					if (keepSheets)
					{
						sheetIndex += workbook.getNumberOfSheets();
					}
					else
					{
						for(int i = 0; i < workbook.getNumberOfSheets(); i++)
						{
							workbook.removeSheetAt(i);
						}
					}
				}
			} 
			catch (JRException | IOException e) 
			{
				throw new JRRuntimeException(e);
			}
			finally
			{
				if (templateIs != null)
				{
					try
					{
						templateIs.close();
					}
					catch (IOException e)
					{
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
		autofitColumns = new HashMap<>();
		formulaCellsMap = new HashMap<>();
		
		SummaryInformation summaryInformation = workbook.getSummaryInformation();
		if (summaryInformation == null)
		{
			workbook.createInformationProperties();
			summaryInformation = workbook.getSummaryInformation();
		}
		
		String application = configuration.getMetadataApplication();
		if( application == null )
		{
			application = "JasperReports Library version " + DefaultJasperReportsContext.class.getPackage().getImplementationVersion();
		}
		summaryInformation.setApplicationName(application);
		
		String title = configuration.getMetadataTitle();
		if (title != null)
		{
			summaryInformation.setTitle(title);
		}
		String subject = configuration.getMetadataSubject();
		if (subject != null)
		{
			summaryInformation.setSubject(subject);
		}
		String author = configuration.getMetadataAuthor();
		if (author != null)
		{
			summaryInformation.setAuthor(author);
		}
		String keywords = configuration.getMetadataKeywords();
		if (keywords != null)
		{
			summaryInformation.setKeywords(keywords);
		}
	}


	@Override
	protected void createSheet(CutsInfo xCuts, SheetInfo sheetInfo)
	{
		sheet = workbook.createSheet(sheetInfo.sheetName);
		patriarch = sheet.createDrawingPatriarch();
		HSSFPrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(pageFormat.getOrientation() == OrientationEnum.LANDSCAPE);
		short paperSize = getSuitablePaperSize(sheetInfo.printSettings);

		if(paperSize != -1)
		{
			printSetup.setPaperSize(paperSize);
		}
		
		XlsReportConfiguration configuration = getCurrentItemConfiguration();
		
		String password = configuration.getPassword();
		if(password != null)
		{
			sheet.protectSheet(password);
		}
		
		JRXlsAbstractExporter.SheetInfo.SheetPrintSettings printSettings = sheetInfo.printSettings;
		sheet.setMargin(PageMargin.LEFT, LengthUtil.inch(printSettings.getLeftMargin()));
		sheet.setMargin(PageMargin.RIGHT, LengthUtil.inch(printSettings.getRightMargin()));
		sheet.setMargin(PageMargin.TOP, LengthUtil.inch(printSettings.getTopMargin()));
		sheet.setMargin(PageMargin.BOTTOM, LengthUtil.inch(printSettings.getBottomMargin()));

		String sheetHeaderLeft = printSettings.getHeaderLeft();
		if(sheetHeaderLeft != null)
		{
			sheet.getHeader().setLeft(sheetHeaderLeft);
		}
		
		String sheetHeaderCenter = printSettings.getHeaderCenter();
		if(sheetHeaderCenter != null)
		{
			sheet.getHeader().setCenter(sheetHeaderCenter);
		}
		
		String sheetHeaderRight = printSettings.getHeaderRight();
		if(sheetHeaderRight != null)
		{
			sheet.getHeader().setRight(sheetHeaderRight);
		}
		
		String sheetFooterLeft = printSettings.getFooterLeft();
		if(sheetFooterLeft != null)
		{
			sheet.getFooter().setLeft(sheetFooterLeft);
		}
		
		String sheetFooterCenter = printSettings.getFooterCenter();
		if(sheetFooterCenter != null)
		{
			sheet.getFooter().setCenter(sheetFooterCenter);
		}
		
		String sheetFooterRight = printSettings.getFooterRight();
		if(sheetFooterRight != null)
		{
			sheet.getFooter().setRight(sheetFooterRight);
		}

		printSetup.setHeaderMargin(LengthUtil.inch(printSettings.getHeaderMargin()));	
		printSetup.setFooterMargin(LengthUtil.inch(printSettings.getFooterMargin()));	
		
		RunDirectionEnum sheetDirection = configuration.getSheetDirection();
		if(sheetDirection != null)
		{
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
		if(!firstPageNotSet && (sheet.getFooter().getCenter() == null || sheet.getFooter().getCenter().length() == 0))
		{
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
//		
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
	protected void closeWorkbook(OutputStream os) throws JRException
	{
		try
		{
			for (Entry<String, HSSFName> entry : anchorNames.entrySet())		// the anchorNames map contains no entries for reports with ignore anchors == true;
			{
				String anchorName = entry.getKey();
				HSSFName anchor = entry.getValue();
				List<Hyperlink> linkList = anchorLinks.get(anchorName);
				anchor.setRefersToFormula("'" + workbook.getSheetName(anchor.getSheetIndex()) + "'!"+ anchor.getRefersToFormula());
				
				if(linkList != null && !linkList.isEmpty())
				{
					for(Hyperlink link : linkList)
					{
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
			for (Entry<Integer, List<Hyperlink>> entry : pageLinks.entrySet()) {		// the pageLinks map contains no entries for reports with ignore hyperlinks == true
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
			
			for(int i=0; i < workbook.getNumberOfSheets(); i++)
			{
				HSSFSheet currentSheet = workbook.getSheetAt(i);
				currentSheet.setForceFormulaRecalculation(true);
				List<Integer> autofitList= autofitColumns.get(currentSheet);
				if(autofitList != null)
				{
					for(Integer j : autofitList) 
					{
						currentSheet.autoSizeColumn(j, false);
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
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_REPORT_GENERATION_ERROR,
					new Object[]{jasperPrint.getName()}, 
					e);
		}
	}

	@Override
	protected void setColumnWidth(int col, int width, boolean autoFit)
	{
		if (autoFit)
		{
			//the autofit will be applied before closing workbook, after the sheet completion
			List<Integer> autofitList= autofitColumns.get(sheet) != null ? autofitColumns.get(sheet) : new ArrayList<>();
			autofitList.add(col);
			autofitColumns.put(sheet, autofitList);
		}
		else
		{
			sheet.setColumnWidth(col, Math.min(43 * width, 256*255));
		}
	}

	@Override
	protected void setRowHeight(int rowIndex, int lastRowHeight, Cut yCut, XlsRowLevelInfo levelInfo)
	{
		row = sheet.getRow(rowIndex);
		
		if (row == null)
		{
			row = sheet.createRow(rowIndex);
		}

		boolean isAutoFit = yCut.hasProperty(PROPERTY_AUTO_FIT_ROW) 
				? (Boolean)yCut.getProperty(PROPERTY_AUTO_FIT_ROW)
				: getCurrentItemConfiguration().isAutoFitRow();
		if (!isAutoFit)
		{
			row.setHeightInPoints(lastRowHeight);
		}
	}

	@Override
	protected void addRowBreak(int rowIndex)
	{
		if(rowIndex >= 0 && rowIndex <= SpreadsheetVersion.EXCEL97.getLastRowIndex())
		{
			sheet.setRowBreak(rowIndex);
		}
	}

//	protected void setCell(JRExporterGridCell gridCell, int colIndex, int rowIndex)
//	{
//		HSSFCell emptyCell = row.getCell(colIndex);
//		if (emptyCell == null)
//		{
//			emptyCell = row.createCell(colIndex);
//			emptyCell.setCellStyle(emptyCellStyle);
//		}
//	}

	@Override
	protected void addBlankCell(JRExporterGridCell gridCell, int colIndex, int rowIndex)
	{
		cell = row.createCell(colIndex);

		FillPatternType mode = backgroundMode;
		short backcolor = whiteIndex;
		
		if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && gridCell.getCellBackcolor() != null)
		{
			mode = FillPatternType.SOLID_FOREGROUND;
			backcolor = getWorkbookColor(gridCell.getCellBackcolor()).getIndex();
		}

		short forecolor = blackIndex;
		if (gridCell.getForecolor() != null)
		{
			forecolor = getWorkbookColor(gridCell.getForecolor()).getIndex();
		}

		HSSFCellStyle cellStyle =
			getLoadedCellStyle(
				mode,
				backcolor,
				HorizontalAlignment.LEFT,
				VerticalAlignment.TOP,
				(short)0,
				getLoadedFont(getDefaultFont(), forecolor, null, getLocale()),
				gridCell,
				true, 
				true, 
				false, 
				false
				);

		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void addOccupiedCell(OccupiedGridCell occupiedGridCell, int colIndex, int rowIndex)
	{
	}
	
	@Override
	protected void exportLine(JRPrintLine line, JRExporterGridCell gridCell, int colIndex, int rowIndex)
	{
		short forecolor = getWorkbookColor(line.getLinePen().getLineColor()).getIndex();

		int side = BoxStyle.TOP;
		float ratio = line.getWidth() / line.getHeight();
		if (ratio > 1)
		{
			if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)
			{
				side = BoxStyle.TOP;
			}
			else
			{
				side = BoxStyle.BOTTOM;
			}
		}
		else
		{
			if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)
			{
				side = BoxStyle.LEFT;
			}
			else
			{
				side = BoxStyle.RIGHT;
			}
		}
		BoxStyle boxStyle = new BoxStyle(side, line.getLinePen());

		FillPatternType mode = backgroundMode;
		short backcolor = whiteIndex;
		if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && gridCell.getCellBackcolor() != null)
		{
			mode = FillPatternType.SOLID_FOREGROUND;
			backcolor = getWorkbookColor(gridCell.getCellBackcolor()).getIndex();
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
				false,
				isCellLocked(line),
				isCellHidden(line),
				isShrinkToFit(line)
				);

		createMergeRegion(gridCell, colIndex, rowIndex, cellStyle);

		cell = row.createCell(colIndex);
		cell.setCellStyle(cellStyle);
	}


	@Override
	protected void exportRectangle(JRPrintGraphicElement element, JRExporterGridCell gridCell, int colIndex, int rowIndex)
	{
		short forecolor = getWorkbookColor(element.getLinePen().getLineColor()).getIndex();

		FillPatternType mode = backgroundMode;
		short backcolor = whiteIndex;
		if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && gridCell.getCellBackcolor() != null)
		{
			mode = FillPatternType.SOLID_FOREGROUND;
			backcolor = getWorkbookColor(gridCell.getCellBackcolor()).getIndex();
		}

		HSSFCellStyle cellStyle =
			getLoadedCellStyle(
				mode,
				backcolor,
				HorizontalAlignment.LEFT,
				VerticalAlignment.TOP,
				(short)0,
				getLoadedFont(getDefaultFont(), forecolor, null, getLocale()),
				gridCell,
				isWrapText(element),
				isCellLocked(element),
				isCellHidden(element),
				isShrinkToFit(element)
				);

		createMergeRegion(gridCell, colIndex, rowIndex, cellStyle);

		cell = row.createCell(colIndex);
		cell.setCellStyle(cellStyle);
	}


	@Override
	public void exportText(JRPrintText textElement, JRExporterGridCell gridCell, int colIndex, int rowIndex) throws JRException
	{
		JRStyledText styledText = getStyledText(textElement);

		if (styledText == null)
		{
			return;
		}

		short forecolor = getWorkbookColor(textElement.getForecolor()).getIndex();

		TextAlignHolder textAlignHolder = getTextAlignHolder(textElement);
		HorizontalAlignment horizontalAlignment = getHorizontalAlignment(textAlignHolder);
		VerticalAlignment verticalAlignment = getVerticalAlignment(textAlignHolder);
		short rotation = getRotation(textAlignHolder);

		FillPatternType mode = backgroundMode;
		short backcolor = whiteIndex;
		if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && gridCell.getCellBackcolor() != null)
		{
			mode = FillPatternType.SOLID_FOREGROUND;
			backcolor = getWorkbookColor(gridCell.getCellBackcolor()).getIndex();
		}

		StyleInfo baseStyle = 
			isIgnoreTextFormatting(textElement) 
			? new StyleInfo(
				mode,
				whiteIndex,
				horizontalAlignment,
				verticalAlignment,
				(short)0,
				null,
				(JRExporterGridCell)null, 
				isWrapText(textElement) || Boolean.TRUE.equals(((JRXlsExporterNature)nature).getColumnAutoFit(textElement)),
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
				gridCell, 
				isWrapText(textElement) || Boolean.TRUE.equals(((JRXlsExporterNature)nature).getColumnAutoFit(textElement)),
				isCellLocked(textElement),
				isCellHidden(textElement),
				isShrinkToFit(textElement)
				);
		createTextCell(textElement, gridCell, colIndex, rowIndex, styledText, baseStyle, forecolor);
	}


	protected void createTextCell(final JRPrintText textElement, final JRExporterGridCell gridCell, final int colIndex, final int rowIndex, final JRStyledText styledText, final StyleInfo baseStyle, final short forecolor) throws JRException
	{
		String formula = getFormula(textElement);
		String textStr = styledText.getText();

		TextValue value = null;
		String pattern = null;
		
		if (
			formula != null
			|| detectCellType
			)
		{
			value = getTextValue(textElement, textStr);

			if (value instanceof NumberTextValue)
			{
				pattern = ((NumberTextValue)value).getPattern();
			}
			else if (value instanceof DateTextValue)
			{
				pattern = ((DateTextValue)value).getPattern();
			}
		}

		String convertedPattern = getConvertedPattern(textElement, pattern);
		if (convertedPattern != null)
		{
			//FIXME: use localized Excel pattern
			baseStyle.setDataFormat(
				dataFormat.getFormat(convertedPattern)
				);
		}
		
		if (formula != null)
		{
			try
			{
				HSSFCellStyle cellStyle = initCreateCell(gridCell, colIndex, rowIndex, baseStyle);
				
				// the formula text will be stored in formulaCellsMap in order to be applied only after 
				// all defined names are created and available in the workbook (see #closeWorkbook())
				formulaCellsMap.put(cell, formula);
				endCreateCell(cellStyle);
				return;
			}
			catch(Exception e)//FIXMENOW what exceptions could we get here?
			{
				if(log.isWarnEnabled())
				{
					log.warn(e.getMessage());
				}
			}
		}

		if (detectCellType)
		{
			value.handle(new TextValueHandler()
			{
				@Override
				public void handle(StringTextValue textValue)
				{
					HSSFCellStyle cellStyle = initCreateCell(gridCell, colIndex, rowIndex, baseStyle);
					if (textValue.getText() == null || textValue.getText().length() == 0)
					{
						cell.setCellType(CellType.BLANK);
					}
					else
					{
						if (JRCommonText.MARKUP_NONE.equals(textElement.getMarkup()) || isIgnoreTextFormatting(textElement))
						{
							setStringCellValue(textValue.getText());
						}
						else
						{
							setRichTextStringCellValue(styledText, forecolor, textElement, getTextLocale(textElement));
						}
					}
					endCreateCell(cellStyle);
				}

				@Override
				public void handle(NumberTextValue textValue)
				{
					HSSFCellStyle cellStyle = initCreateCell(gridCell, colIndex, rowIndex, baseStyle);
					if (textValue.getValue() == null)
					{
						cell.setCellType(CellType.BLANK);
					}
					else
					{
						double doubleValue = textValue.getValue().doubleValue();
						if (DefaultFormatFactory.STANDARD_NUMBER_FORMAT_DURATION.equals(convertedPattern))
						{
							doubleValue = doubleValue / 86400;
						}
						cell.setCellValue(doubleValue);
					}
					endCreateCell(cellStyle);
				}

				@Override
				public void handle(DateTextValue textValue)
				{
					HSSFCellStyle cellStyle = initCreateCell(gridCell, colIndex, rowIndex, baseStyle);
					Date date = textValue.getValue();
					if (date == null)
					{
						cell.setCellType(CellType.BLANK);
					}
					else
					{
						date = translateDateValue(textElement, date);
						cell.setCellValue(date);
					}
					endCreateCell(cellStyle);
				}

				@Override
				public void handle(BooleanTextValue textValue)
				{
					HSSFCellStyle cellStyle = initCreateCell(gridCell, colIndex, rowIndex, baseStyle);
					if (textValue.getValue() == null)
					{
						cell.setCellType(CellType.BLANK);
					}
					else
					{
						cell.setCellValue(textValue.getValue());
					}
					endCreateCell(cellStyle);
				}
			});
		}
		else
		{
			HSSFCellStyle cellStyle = initCreateCell(gridCell, colIndex, rowIndex, baseStyle);
			if (JRCommonText.MARKUP_NONE.equals(textElement.getMarkup()) || isIgnoreTextFormatting(textElement))
			{
				setStringCellValue(textStr);
			}
			else
			{
				setRichTextStringCellValue(styledText, forecolor, textElement, getTextLocale(textElement));
			}
			endCreateCell(cellStyle);
		}
		
		if(!ignoreAnchors)
		{
			String anchorName = textElement.getAnchorName();
			if(anchorName != null)
			{
				HSSFName aName = workbook.createName();
				aName.setNameName(toExcelName(anchorName));
				aName.setSheetIndex(workbook.getSheetIndex(sheet));
				CellReference cRef = new CellReference(rowIndex, colIndex, true, true);
				aName.setRefersToFormula(cRef.formatAsString());
				anchorNames.put(anchorName, aName);
			}
		}

		setHyperlinkCell(textElement);
	}
	
	protected HSSFCellStyle initCreateCell(JRExporterGridCell gridCell, int colIndex, int rowIndex, StyleInfo baseStyle)
	{
		HSSFCellStyle cellStyle = getLoadedCellStyle(baseStyle);
		createMergeRegion(gridCell, colIndex, rowIndex, cellStyle);
		cell = row.createCell(colIndex);
		return cellStyle;
	}

	protected void endCreateCell(HSSFCellStyle cellStyle)
	{
		cell.setCellStyle(cellStyle);
	}
	
	protected final void setStringCellValue(String textStr)
	{
		//cell.setCellValue(JRStringUtil.replaceDosEOL(textStr));
		//cell.setCellValue(textStr);
		cell.setCellValue(new HSSFRichTextString(textStr));
	}
	
	protected final void setRichTextStringCellValue(JRStyledText styledText, short forecolor, JRFont defaultFont, Locale locale)
	{	
		if(styledText != null)
		{
			cell.setCellValue(getRichTextString(styledText, forecolor, defaultFont, locale));
		}
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

	protected void createMergeRegion(JRExporterGridCell gridCell, int colIndex, int rowIndex, HSSFCellStyle cellStyle)
	{
		int rowSpan = collapseRowSpan ? 1 : gridCell.getRowSpan();
		if (gridCell.getColSpan() > 1 || rowSpan > 1)
		{
			sheet.addMergedRegion(new CellRangeAddress(rowIndex, (rowIndex + rowSpan - 1), 
					colIndex, (colIndex + gridCell.getColSpan() - 1)));

			for(int i = 0; i < rowSpan; i++)
			{
				HSSFRow spanRow = sheet.getRow(rowIndex + i);
				if (spanRow == null)
				{
					spanRow = sheet.createRow(rowIndex + i);
				}
				for(int j = 0; j < gridCell.getColSpan(); j++)
				{
					HSSFCell spanCell = spanRow.getCell((colIndex + j));
					if (spanCell == null)
					{
						spanCell = spanRow.createCell((colIndex + j));
					}
					spanCell.setCellStyle(cellStyle);
				}
			}
		}
	}

	private HorizontalAlignment getHorizontalAlignment(TextAlignHolder alignment)
	{
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

	private VerticalAlignment getVerticalAlignment(TextAlignHolder alignment)
	{
		switch (alignment.verticalAlignment)
		{
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

	private short getRotation(TextAlignHolder alignment)
	{
		switch (alignment.rotation)
		{
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
	protected HSSFColor getWorkbookColor(Color awtColor)
	{
		byte red = (byte)awtColor.getRed();
		byte green = (byte)awtColor.getGreen();
		byte blue = (byte)awtColor.getBlue();
		HSSFColor color = null;

		if (getCurrentConfiguration().isCreateCustomPalette())
		{
			try
			{
				color = palette.findColor(red,green, blue) != null
					? palette.findColor(red,green, blue)
					: palette.addColor(red,green, blue);
			}
			catch(Exception e)
			{
				if(customColorIndex < MAX_COLOR_INDEX)
				{
					palette.setColorAtIndex(customColorIndex, red, green, blue);
					color = palette.getColor(customColorIndex++);
				}
				else
				{
					color = palette.findSimilarColor(red, green, blue);
				}
			}
		}
		
		return color == null ? getNearestColor(awtColor) : color;
	}

	/**
	 *
	 */
	protected HSSFColor getNearestColor(Color awtColor)
	{
		HSSFColor color = hssfColorsCache.get(awtColor);		
		if (color == null)
		{
			int minDiff = Integer.MAX_VALUE;
			for (Map.Entry<HSSFColor, short[]> hssfColorEntry : hssfColorsRgbs.entrySet())
			{
				HSSFColor crtColor = hssfColorEntry.getKey();
				short[] rgb = hssfColorEntry.getValue();
				
				int diff = Math.abs(rgb[0] - awtColor.getRed()) + Math.abs(rgb[1] - awtColor.getGreen()) + Math.abs(rgb[2] - awtColor.getBlue());

				if (diff < minDiff)
				{
					minDiff = diff;
					color = crtColor;
				}
			}

			hssfColorsCache.put(awtColor, color);
		}
		return color;
	}

	/**
	 *
	 */
	protected HSSFFont getLoadedFont(JRFont font, short forecolor, Map<Attribute,Object> attributes, Locale locale)
	{
		HSSFFont cellFont = null;

		String fontName = fontUtil.getExportFontFamily(font.getFontName(), locale, getExporterKey());
		
		short superscriptType = HSSFFont.SS_NONE;
		
		if( attributes != null && attributes.get(TextAttribute.SUPERSCRIPT) != null)
		{
			Object value = attributes.get(TextAttribute.SUPERSCRIPT);
			if(TextAttribute.SUPERSCRIPT_SUPER.equals(value))
			{
				superscriptType = HSSFFont.SS_SUPER;
			}
			else if(TextAttribute.SUPERSCRIPT_SUB.equals(value))
			{
				superscriptType = HSSFFont.SS_SUB;
			}
			
		}
		boolean isFontSizeFixEnabled = getCurrentItemConfiguration().isFontSizeFixEnabled();
		for (int i = 0; i < loadedFonts.size(); i++)
		{
			HSSFFont cf = (HSSFFont)loadedFonts.get(i);

			short fontSize = (short)font.getFontSize();
			if (isFontSizeFixEnabled)
			{
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
				)
			{
				cellFont = cf;
				break;
			}
		}

		if (cellFont == null)
		{
			cellFont = workbook.createFont();

			cellFont.setFontName(fontName);
			cellFont.setColor(forecolor);

			short fontSize = (short)font.getFontSize();
			if (isFontSizeFixEnabled)
			{
				fontSize -= 1;
			}
			cellFont.setFontHeightInPoints(fontSize);

			if (font.isUnderline())
			{
				cellFont.setUnderline(HSSFFont.U_SINGLE);
			}
			if (font.isStrikeThrough())
			{
				cellFont.setStrikeout(true);
			}
			if (font.isBold())
			{
				cellFont.setBold(true);
			}
			if (font.isItalic())
			{
				cellFont.setItalic(true);
			}

			cellFont.setTypeOffset(superscriptType);
			loadedFonts.add(cellFont);
		}

		return cellFont;
	}


	protected HSSFCellStyle getLoadedCellStyle(StyleInfo style)
	{
		HSSFCellStyle cellStyle = loadedCellStyles.get(style);
		if (cellStyle == null)
		{
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
			
			if (style.hasDataFormat())
			{
				cellStyle.setDataFormat(style.getDataFormat());
			}
			
			boolean isIgnoreCellBorder = Boolean.TRUE.equals(sheetInfo.ignoreCellBorder) || style.box ==null;
			if (!isIgnoreCellBorder)
			{
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
		JRExporterGridCell gridCell,
		boolean isWrapText,
		boolean isCellLocked,
		boolean isCellHidden,
		boolean isShrinkToFit
		)
	{
		return getLoadedCellStyle(
			new StyleInfo(
				mode, 
				backcolor, 
				horizontalAlignment, 
				verticalAlignment, 
				rotation, 
				font, 
				gridCell, 
				isWrapText, 
				isCellLocked, 
				isCellHidden, 
				isShrinkToFit));
	}

	protected HSSFCellStyle getLoadedCellStyle(
		FillPatternType mode,
		short backcolor,
		HorizontalAlignment horizontalAlignment,
		VerticalAlignment verticalAlignment,
		short rotation,
		HSSFFont font,
		BoxStyle box,
		boolean isWrapText,
		boolean isCellLocked,
		boolean isCellHidden,
		boolean isShrinkToFit
		)
	{
		StyleInfo style = new StyleInfo(mode, backcolor, horizontalAlignment, verticalAlignment, rotation, font, box, isWrapText, isCellLocked, isCellHidden, isShrinkToFit);
		return getLoadedCellStyle(style);
	}

	/**
	 *
	 */
	protected static BorderStyle getBorderStyle(JRPen pen)
	{
		float lineWidth = pen.getLineWidth();

		if (lineWidth > 0f)
		{
			switch (pen.getLineStyle())
			{
				case DOUBLE :
				{
					return BorderStyle.DOUBLE;
				}
				case DOTTED :
				{
					return BorderStyle.DOTTED;
				}
				case DASHED :
				{
					if (lineWidth >= 1f)
					{
						return BorderStyle.MEDIUM_DASHED;
					}

					return BorderStyle.DASHED;
				}
				case SOLID :
				default :
				{
					if (lineWidth >= 2f)
					{
						return BorderStyle.THICK;
					}
					else if (lineWidth >= 1f)
					{
						return BorderStyle.MEDIUM;
					}
					else if (lineWidth >= 0.5f)
					{
						return BorderStyle.THIN;
					}

					return BorderStyle.HAIR;
				}
			}
		}

		return BorderStyle.NONE;
	}

	@Override
	public void exportImage(
		JRPrintImage element, 
		JRExporterGridCell gridCell, 
		int colIndex, 
		int rowIndex, 
		int emptyCols,
		int yCutsRow,
		JRGridLayout layout
		) throws JRException
	{
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
				
		try
		{
			Renderable renderer = element.getRenderer();

			if (
				renderer != null 
				&& availableImageWidth > 0 
				&& availableImageHeight > 0
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
					if (!Boolean.TRUE.equals(sheetInfo.ignoreCellBackground) && gridCell.getCellBackcolor() != null)
					{
						mode = FillPatternType.SOLID_FOREGROUND;
						backcolor = getWorkbookColor(gridCell.getCellBackcolor()).getIndex();
					}

					short forecolor = getWorkbookColor(element.getLineBox().getPen().getLineColor()).getIndex();

					if(element.getMode() == ModeEnum.OPAQUE )
					{
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
							gridCell,
							isWrapText(element),
							isCellLocked(element),
							isCellHidden(element),
							isShrinkToFit(element)
							);

					createMergeRegion(gridCell, colIndex, rowIndex, cellStyle);

					cell = row.createCell(colIndex);
					cell.setCellStyle(cellStyle);

					double topPos = getRowRelativePosition(layout, yCutsRow, topPadding + imageProcessorResult.topOffset);
					double leftPos = getColumnRelativePosition(layout, colIndex, leftPadding + imageProcessorResult.leftOffset);
					double bottomPos = getRowRelativePosition(layout, yCutsRow, element.getHeight() - bottomPadding - imageProcessorResult.bottomOffset);
					double rightPos = getColumnRelativePosition(layout, colIndex, element.getWidth() - rightPadding - imageProcessorResult.rightOffset);
					HSSFClientAnchor anchor = 
						new HSSFClientAnchor(
							(int)((leftPos - (int)leftPos) * 1023), //numbers taken from POI source code
							(int)((topPos - (int)topPos) * 255), 
							(int)((rightPos - (int)rightPos) * 1023), 
							(int)((bottomPos - (int)bottomPos) * 255), 
							(short)(colIndex + (int)leftPos), 
							(rowIndex + (int)topPos), 
							//(short) (colIndex + gridCell.getColSpan()), 
							(short)(colIndex + (int)rightPos), 
							//rowIndex + (isCollapseRowSpan ? 1 : gridCell.getRowSpan())
							(rowIndex + (int)bottomPos)
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
					anchor.setAnchorType(getAnchorType(imageAnchorType));
					//pngEncoder.setImage(bi);
					//int imgIndex = workbook.addPicture(pngEncoder.pngEncode(), HSSFWorkbook.PICTURE_TYPE_PNG);
					int imgIndex = workbook.addPicture(imageProcessorResult.imageData, HSSFWorkbook.PICTURE_TYPE_PNG);
					patriarch.createPicture(anchor, imgIndex).setRotationDegree(imageProcessorResult.angle);
					
//					setHyperlinkCell(element);
				}
			}
		}
		catch (Exception ex)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_CANNOT_ADD_CELL, 
					null,
					ex);
		}
		catch (Error err)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_CANNOT_ADD_CELL, 
					null,
					err);
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
			if (dimension != null)
			{
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
			
			// exif orientation is taken care inside the wrapping renderer 
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
			byte[] imageData = renderer.getData(jasperReportsContext); 
			ExifOrientationEnum exifOrientation = ImageUtil.getExifOrientation(imageData);

			short angle = 0;
			
			switch (ImageUtil.getRotation(imageElement.getRotation(), exifOrientation))
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
					imageData, 
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
			if (dimension != null)
			{
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
	
			byte[] imageData = renderer.getData(jasperReportsContext); 
			ExifOrientationEnum exifOrientation = ImageUtil.getExifOrientation(imageData);

			switch (ImageUtil.getRotation(imageElement.getRotation(), exifOrientation))
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

			/*
			 * The code below the result of tests alone and not the result of prior design or understanding of how image cropping works in the Microsoft document formats.
			 * Trial and error during tests were the only way to achieve desired output and this code is the result of this trial and error technique alone, 
			 * without actually understanding how image cropping works in these document formats.
			 */
			switch (imageElement.getRotation())
			{
				case LEFT :
					switch (exifOrientation)
					{
						case UPSIDE_DOWN :
						{
							int t = leftOffset;
							leftOffset = rightOffset;
							rightOffset = t;
							t = topOffset;
							topOffset = bottomOffset;
							bottomOffset = t;
							break;
						}
						case RIGHT :
						{
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							break;
						}
						case LEFT :
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							break;
						case NORMAL :
						default :
							break;
					}
					break;
				case RIGHT :
					switch (exifOrientation)
					{
						case UPSIDE_DOWN :
						{
							int t = leftOffset;
							leftOffset = rightOffset;
							rightOffset = t;
							t = topOffset;
							topOffset = bottomOffset;
							bottomOffset = t;
							break;
						}
						case RIGHT :
						{
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							break;
						}
						case LEFT :
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							break;
						case NORMAL :
						default :
							break;
					}
					break;
				case UPSIDE_DOWN :
					switch (exifOrientation)
					{
						case UPSIDE_DOWN :
						{
							int t = leftOffset;
							leftOffset = rightOffset;
							rightOffset = t;
							t = topOffset;
							topOffset = bottomOffset;
							bottomOffset = t;
							break;
						}
						case RIGHT :
						{
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							break;
						}
						case LEFT :
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							break;
						case NORMAL :
						default :
							break;
					}
					break;
				case NONE :
				default :
					switch (exifOrientation)
					{
						case UPSIDE_DOWN :
						{
							int t = leftOffset;
							leftOffset = rightOffset;
							rightOffset = t;
							t = topOffset;
							topOffset = bottomOffset;
							bottomOffset = t;
							break;
						}
						case RIGHT :
						{
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							break;
						}
						case LEFT :
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								)
							{
								int t = topOffset;
								topOffset = bottomOffset;
								bottomOffset = t;
							}
							if (
								(imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.BOTTOM)
								|| (imageElement.getHorizontalImageAlign() == HorizontalImageAlignEnum.RIGHT && imageElement.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)
								)
							{
								int t = leftOffset;
								leftOffset = rightOffset;
								rightOffset = t;
							}
							break;
						case NORMAL :
						default :
							break;
					}
					break;
			}
			
			return 
				new InternalImageProcessorResult(
					imageData, 
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
		private final int topOffset;
		private final int leftOffset;
		private final int bottomOffset;
		private final int rightOffset;
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
			this.topOffset = topOffset;
			this.leftOffset = leftOffset;
			this.bottomOffset = bottomOffset;
			this.rightOffset = rightOffset;
			this.angle = angle;
		}
	}

	/**
	 *
	 */
	protected double getColumnRelativePosition(JRGridLayout layout, int col, int offset)
	{
		double colRelPos = 0;
		
		int cumulativeColWidth = 0;
		int colIndex = 0;
		while(cumulativeColWidth < offset)
		{
			int colWidth = layout.getColumnWidth(col + colIndex);
			if (cumulativeColWidth + colWidth < offset)
			{
				colIndex++;
			}
			else
			{
				colRelPos += colIndex + ((offset - cumulativeColWidth) / (double) colWidth);
			}
			cumulativeColWidth += colWidth;
		}
		
		return colRelPos;
	}	
	
	/**
	 *
	 */
	protected double getRowRelativePosition(JRGridLayout layout, int row, int offset)
	{
		double rowRelPos = 0;
		
		//isCollapseRowSpan
		int cumulativeRowHeight = 0;
		int rowIndex = 0;
		while(cumulativeRowHeight < offset)
		{
			int rowHeight = collapseRowSpan ? layout.getMaxRowHeight(row + rowIndex) : layout.getRowHeight(row + rowIndex);
			if (cumulativeRowHeight + rowHeight < offset)
			{
				rowIndex++;
			}
			else
			{
				rowRelPos += rowIndex + ((offset - cumulativeRowHeight) / (double) rowHeight);
			}
			cumulativeRowHeight += rowHeight;
		}
		
		return rowRelPos;
	}	

	@Override
	protected void exportFrame(JRPrintFrame frame, JRExporterGridCell gridCell, int x, int y)
	{
		FillPatternType mode = backgroundMode;
		short backcolor = whiteIndex;
		if (frame.getMode() == ModeEnum.OPAQUE)
		{
			mode = FillPatternType.SOLID_FOREGROUND;
			backcolor = getWorkbookColor(frame.getBackcolor()).getIndex();
		}

		short forecolor = getWorkbookColor(frame.getForecolor()).getIndex();

		HSSFCellStyle cellStyle =
			getLoadedCellStyle(
				mode,
				backcolor,
				HorizontalAlignment.LEFT,
				VerticalAlignment.TOP,
				(short)0,
				getLoadedFont(getDefaultFont(), forecolor, null, getLocale()),
				gridCell,
				isWrapText(frame),
				isCellLocked(frame),
				isCellHidden(frame),
				isShrinkToFit(frame)
				);

		createMergeRegion(gridCell, x, y, cellStyle);

		cell = row.createCell(x);
		cell.setCellStyle(cellStyle);
	}

	@Override
	protected void exportGenericElement(JRGenericPrintElement element, JRExporterGridCell gridCell, int colIndex, int rowIndex, int emptyCols, int yCutsRow, JRGridLayout layout) throws JRException
	{
		GenericElementXlsHandler handler = (GenericElementXlsHandler) 
			GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
				element.getGenericType(), XLS_EXPORTER_KEY);

		if (handler != null)
		{
			handler.exportElement(exporterContext, element, gridCell, colIndex, rowIndex, emptyCols, yCutsRow, layout);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No XLS generic element handler for " 
						+ element.getGenericType());
			}
		}
	}


	private final short getSuitablePaperSize(SheetPrintSettings printSettings)
	{

		if (printSettings == null)
		{
			return -1;
		}
		long width = 0;
		long height = 0;
		short ps = -1;

		if ((printSettings.getPageWidth() != 0) && (printSettings.getPageHeight() != 0))
		{

			double dWidth = (printSettings.getPageWidth() / 72.0);
			double dHeight = (printSettings.getPageHeight() / 72.0);

			height = Math.round(dHeight * 25.4);
			width = Math.round(dWidth * 25.4);

			// Compare to ISO 216 A-Series (A3-A5). All other ISO 216 formats
			// not supported by POI Api yet.
			for (int i = 2; i < 6; i++)
			{
				int w = calculateWidthForDinAN(i);
				int h = calculateHeightForDinAN(i);

				if (((w == width) && (h == height)) || ((h == width) && (w == height)))
				{
					if (i == 2)
					{
						// local A2_PAPERSIZE constant
						ps = A2_PAPERSIZE;
					}
					if (i == 3)
					{
						ps = HSSFPrintSetup.A3_PAPERSIZE;
					}
					else if (i == 4)
					{
						ps = HSSFPrintSetup.A4_PAPERSIZE;
					}
					else if (i == 5)
					{
						ps = HSSFPrintSetup.A5_PAPERSIZE;
					}
					break;
				}
			}
			
			//envelope sizes
			if (ps == -1)
			{
				// ISO 269 sizes - "Envelope DL" (110 x 220 mm)
				if (((width == 110) && (height == 220)) || ((width == 220) && (height == 110)))
				{
					ps = HSSFPrintSetup.ENVELOPE_DL_PAPERSIZE;
				}
			}

			// Compare to common North American Paper Sizes (ANSI X3.151-1987).
			if (ps == -1)
			{
				// ANSI X3.151-1987 - "Letter" (216 x 279 mm)
				if (((width == 216) && (height == 279)) || ((width == 279) && (height == 216)))
				{
					ps = HSSFPrintSetup.LETTER_PAPERSIZE;
				}
				// ANSI X3.151-1987 - "Legal" (216 x 356 mm)
				if (((width == 216) && (height == 356)) || ((width == 356) && (height == 216)))
				{
					ps = HSSFPrintSetup.LEGAL_PAPERSIZE;
				}
				// ANSI X3.151-1987 - "Executive" (190 x 254 mm)
				else if (((width == 190) && (height == 254)) || ((width == 254) && (height == 190)))
				{
					ps = HSSFPrintSetup.EXECUTIVE_PAPERSIZE;
				}
				// ANSI X3.151-1987 - "Ledger/Tabloid" (279 x 432 mm)
				// Not supported by POI Api yet.
				
			}
		}
		return ps;
	}
	
	protected void setHyperlinkCell(JRPrintHyperlink hyperlink)
	{
		Hyperlink link = null;

		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(XlsReportConfiguration.PROPERTY_IGNORE_HYPERLINK, hyperlink);
		if (ignoreHyperlink == null)
		{
			ignoreHyperlink = defaultIgnoreHyperlink;
		}

		//test for ignore hyperlinks done here
		if (!ignoreHyperlink)
		{
			JRHyperlinkProducer customHandler = getHyperlinkProducer(hyperlink);
			if (customHandler == null)
			{
				switch (hyperlink.getHyperlinkType())
				{
					case REFERENCE:
					{
						String href = hyperlink.getHyperlinkReference();
						if (href != null)
						{
							link = createHelper.createHyperlink(HyperlinkType.URL);
							link.setAddress(href);
						}
						break;
					}
					case LOCAL_ANCHOR :
					{
						//test for ignore anchors done here
						if(!ignoreAnchors)
						{
							String href = hyperlink.getHyperlinkAnchor();
							if (href != null)
							{
								link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
								if(anchorLinks.containsKey(href))
								{
									(anchorLinks.get(href)).add(link);
								}
								else
								{
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
						if (hrefPage != null)
						{
							link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
							if(pageLinks.containsKey(sheetsBeforeCurrentReport+hrefPage))
							{
								pageLinks.get(sheetsBeforeCurrentReport + hrefPage).add(link);
							}
							else
							{
								List<Hyperlink> hrefList = new ArrayList<>();
								hrefList.add(link);
								pageLinks.put(sheetsBeforeCurrentReport + hrefPage, hrefList);
							}
						}
						break;
					}
					case REMOTE_ANCHOR :
					{
						String href = hyperlink.getHyperlinkReference();
						if (href != null && hyperlink.getHyperlinkAnchor() != null)
						{
							href = href + "#" + hyperlink.getHyperlinkAnchor();
							link = createHelper.createHyperlink(HyperlinkType.FILE);
							link.setAddress(href);
							
						}
						break;
						
					}
					case REMOTE_PAGE :
					{
						String href = hyperlink.getHyperlinkReference();
						if (href != null && hyperlink.getHyperlinkPage() != null)
						{
							href = href + "#JR_PAGE_ANCHOR_0_" + hyperlink.getHyperlinkPage().toString();
							link = createHelper.createHyperlink(HyperlinkType.FILE);
							link.setAddress(href);
							
						}
						break;
						
					}
					case NONE:
					default:
					{
					}
				}
			}
			else
			{
				String href = customHandler.getHyperlink(hyperlink);
				if (href != null)
				{
					link = createHelper.createHyperlink(HyperlinkType.URL);
					link.setAddress(href);
				}
			}
			if(link != null)
			{
				//TODO: make tooltips functional
//				if(hyperlink.getHyperlinkTooltip() != null)
//				{
//					link.setLabel(hyperlink.getHyperlinkTooltip());
//				}
				cell.setHyperlink(link);
			}
		}
	}

	@Override
	protected Integer getMaxRowsPerSheet()
	{
		Integer maxRowsPerSheet = super.getMaxRowsPerSheet();
		return maxRowsPerSheet == null || maxRowsPerSheet == 0  || maxRowsPerSheet > 65536 ? 65536 : maxRowsPerSheet;
	}


	@Override
	public String getExporterKey()
	{
		return XLS_EXPORTER_KEY;
	}
	
	
	@Override
	public String getExporterPropertiesPrefix()
	{
		return XLS_EXPORTER_PROPERTIES_PREFIX;
	}

	
	/**
	 * Creates a freeze pane for the current sheet. Freeze pane row and column indexes defined at element level override indexes defined at report level. 
	 * If multiple row freeze indexes are found in the same sheet, their maximum 
	 * value is considered. 
	 * 
	 * @param rowIndex the freeze 0-based row index
	 * @param colIndex the freeze 0-based column index
	 */
	@Override
	protected void setFreezePane(int rowIndex, int colIndex)
	{
		if(rowIndex > 0 || colIndex > 0)
		{
			sheet.createFreezePane(Math.max(0, colIndex), Math.max(0, rowIndex));
		}
	}
	
	@Override
	protected void setSheetName(String sheetName)
	{
		workbook.setSheetName(workbook.getSheetIndex(sheet) , sheetName);
	}

	
	@Override
	protected void setAutoFilter(String autoFilterRange)
	{
		sheet.setAutoFilter(CellRangeAddress.valueOf(autoFilterRange));
	}


	@Override
	protected void setRowLevels(XlsRowLevelInfo levelInfo, String level) 
	{
		Map<String, Integer> levelMap = levelInfo.getLevelMap();
		if(levelMap != null && levelMap.size() > 0)
		{
			for (Entry<String, Integer> entry : levelMap.entrySet())
			{
				String l = entry.getKey(); 
				if (level == null || l.compareTo(level) >= 0)
				{
					Integer startIndex = entry.getValue();
					if(levelInfo.getEndIndex() >= startIndex)
					{
						sheet.groupRow(startIndex, levelInfo.getEndIndex());
					}
				}
			}
			sheet.setRowSumsBelow(false);
		}
	}
	

	public static ClientAnchor.AnchorType getAnchorType(ImageAnchorTypeEnum anchorType)
	{
		switch (anchorType)
		{
			case MOVE_SIZE: 
				return ClientAnchor.AnchorType.MOVE_AND_RESIZE;
			case NO_MOVE_NO_SIZE:
				return ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE;
			case MOVE_NO_SIZE:
			default:
				return ClientAnchor.AnchorType.MOVE_DONT_RESIZE;
		}
	}

	
	/**
	 * 
	 */
	class BoxStyle
	{
		protected static final int TOP = 0;
		protected static final int LEFT = 1;
		protected static final int BOTTOM = 2;
		protected static final int RIGHT = 3;

		protected BorderStyle[] borderStyle = new BorderStyle[] {BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE, BorderStyle.NONE};
		protected short[] borderColour = new short[4];
		private int hash;

		public BoxStyle(int side, JRPen pen)
		{
			borderStyle[side] = JRXlsExporter.getBorderStyle(pen);
			borderColour[side] = JRXlsExporter.this.getWorkbookColor(pen.getLineColor()).getIndex();

			hash = computeHash();
		}

		public BoxStyle(JRExporterGridCell gridCell)
		{
			if(gridCell != null)
			{
				JRLineBox lineBox = gridCell.getBox();
				if (lineBox != null)
				{
					setBox(lineBox);
				}
				JRPrintElement element = gridCell.getElement();
				if (element instanceof JRCommonGraphicElement)
				{
					setPen(((JRCommonGraphicElement)element).getLinePen());
				}
	
				hash = computeHash();
			}
		}

		public void setBox(JRLineBox box)
		{
			borderStyle[TOP] = JRXlsExporter.getBorderStyle(box.getTopPen());
			borderColour[TOP] = JRXlsExporter.this.getWorkbookColor(box.getTopPen().getLineColor()).getIndex();

			borderStyle[BOTTOM] = JRXlsExporter.getBorderStyle(box.getBottomPen());
			borderColour[BOTTOM] = JRXlsExporter.this.getWorkbookColor(box.getBottomPen().getLineColor()).getIndex();

			borderStyle[LEFT] = JRXlsExporter.getBorderStyle(box.getLeftPen());
			borderColour[LEFT] = JRXlsExporter.this.getWorkbookColor(box.getLeftPen().getLineColor()).getIndex();

			borderStyle[RIGHT] = JRXlsExporter.getBorderStyle(box.getRightPen());
			borderColour[RIGHT] = JRXlsExporter.this.getWorkbookColor(box.getRightPen().getLineColor()).getIndex();

			hash = computeHash();
		}

		public void setPen(JRPen pen)
		{
			if (
				borderStyle[TOP] == BorderStyle.NONE
				&& borderStyle[LEFT] == BorderStyle.NONE
				&& borderStyle[BOTTOM] == BorderStyle.NONE
				&& borderStyle[RIGHT] == BorderStyle.NONE
				)
			{
				BorderStyle style = JRXlsExporter.getBorderStyle(pen);
				short colour = JRXlsExporter.this.getWorkbookColor(pen.getLineColor()).getIndex();

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

		private int computeHash()
		{
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
		public int hashCode()
		{
			return hash;
		}

		@Override
		public boolean equals(Object o)
		{
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
		public String toString()
		{
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
	protected class StyleInfo
	{
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
			JRExporterGridCell gridCell,
			boolean wrapText,
			boolean cellLocked,
			boolean cellHidden,
			boolean shrinkToFit
			)
		{
			this(mode, 
				backcolor, 
				horizontalAlignment, 
				verticalAlignment, 
				rotation, 
				font, 
				(gridCell == null ? null : new BoxStyle(gridCell)), 
				wrapText, 
				cellLocked, 
				cellHidden, 
				shrinkToFit);
		}
		
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
		protected int computeHash()
		{
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
	
		public void setDataFormat(short dataFormat)
		{
			this.lcDataFormat = dataFormat;
			hashCode = computeHash();
		}
	
		public boolean hasDataFormat()
		{
			return lcDataFormat != -1;
		}
	
		public short getDataFormat()
		{
			return lcDataFormat;
		}
	
		@Override
		public int hashCode()
		{
			return hashCode;
		}
	
		@SuppressWarnings("deprecation")
		@Override
		public boolean equals(Object o)
		{
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
		public String toString()
		{
			return "(" +
				mode + "," + backcolor + "," +
				horizontalAlignment + "," + verticalAlignment + "," +
				rotation + "," + font + "," +
				box + "," + lcDataFormat + "," + lcWrapText + "," + lcCellLocked + "," + lcCellHidden + "," + lcShrinkToFit + ")";
		}
	}

}
