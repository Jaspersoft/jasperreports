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
package net.sf.jasperreports.engine.export.ooxml;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintElementIndex;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.export.ExcelAbstractExporter;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter.SheetInfo;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.ResetableExporterFilter;
import net.sf.jasperreports.engine.export.XlsRowLevelInfo;
import net.sf.jasperreports.engine.export.data.BooleanTextValue;
import net.sf.jasperreports.engine.export.data.DateTextValue;
import net.sf.jasperreports.engine.export.data.NumberTextValue;
import net.sf.jasperreports.engine.export.data.StringTextValue;
import net.sf.jasperreports.engine.export.data.TextValue;
import net.sf.jasperreports.engine.export.data.TextValueHandler;
import net.sf.jasperreports.engine.export.type.CellEdgeEnum;
import net.sf.jasperreports.engine.export.type.ImageAnchorTypeEnum;
import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZipEntry;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.FileBufferedOutputStream;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.XlsReportConfiguration;
import net.sf.jasperreports.export.XlsxMetadataExporterConfiguration;
import net.sf.jasperreports.export.XlsxMetadataReportConfiguration;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;


/**
 * Exports a JasperReports document to XLSX format based on the metadata provided.
 * <p/>
 * The exporter allows users to specify which columns should be included in the XLSX export, what other value than the default
 * should they contain and whether the values for some columns should be auto filled when they are empty or missing (e.g. value 
 * for group columns)
 * 
 * @see net.sf.jasperreports.engine.export.ExcelAbstractExporter
 * @see net.sf.jasperreports.export.XlsExporterConfiguration
 * @see net.sf.jasperreports.export.XlsReportConfiguration
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class XlsxMetadataExporter extends ExcelAbstractExporter<XlsxMetadataReportConfiguration, XlsxMetadataExporterConfiguration, JRXlsxExporterContext> 
{
	private static final Log log = LogFactory.getLog(XlsxMetadataExporter.class);
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String XLSX_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "xlsx";

	public static final String XLSX_METADATA_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "metadata.xlsx";
	
	protected static final String XLSX_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xlsx.";
	
	protected static final String ONE_CELL = "oneCell";
	
	protected static final String TWO_CELL = "twoCell";
	
	protected static final String ABSOLUTE = "absolute";

	protected static final String PARENT_STYLE = "__JR_PARENT_STYLE";
	
	protected static final String CURRENT_DATA = "__JR_CURRENT_DATA";
	
	/**
	 *
	 */
	protected static final String JR_PAGE_ANCHOR_PREFIX = "JR_PAGE_ANCHOR_";

	/**
	 *
	 */
	public static final String IMAGE_NAME_PREFIX = "img_";
	protected static final int IMAGE_NAME_PREFIX_LEGTH = IMAGE_NAME_PREFIX.length();

	/**
	 *
	 */
	protected XlsxZip xlsxZip;
	protected XlsxWorkbookHelper wbHelper;
	protected XlsxRelsHelper relsHelper;
	protected XlsxContentTypesHelper ctHelper;
	protected PropsAppHelper appHelper;
	protected PropsCoreHelper coreHelper;
	protected XlsxSheetHelper sheetHelper;
	protected XlsxSheetRelsHelper sheetRelsHelper;
	protected XlsxDrawingHelper drawingHelper;
	protected XlsxDrawingRelsHelper drawingRelsHelper;
	protected XlsxStyleHelper styleHelper;
	protected XlsxSharedStringsHelper sharedStringsHelper;
	protected XlsxCellHelper cellHelper;
	protected StringBuilder definedNames;
	protected String firstSheetName;
	protected String currentSheetName;

	protected Map<String, String> rendererToImagePathMap;

	protected int tableIndex;
	protected boolean startPage;

	protected LinkedList<Color> backcolorStack = new LinkedList<>();
	protected Color backcolor;

	protected String sheetAutoFilter;		
	
	protected String macroTemplate;
	
	protected PrintPageFormat oldPageFormat;
	
	protected Integer currentSheetPageScale;	
	
	protected Integer currentSheetFirstPageNumber;		

	protected Map<String, Integer> sheetMapping;

	/**
	 * 
	 */
	protected List<String> columnNames;
	protected Map<String, Integer> columnNamesMap;
	protected Map<String, Integer> rowSpanStartIndexesMap;
	protected int rowIndex;
	boolean hasDefinedColumns;
	protected Map<String, Object> currentRow;	
	protected Map<String, Object> repeatedValues;	
	protected Map<String, Object> columnHeadersRow;
	private JRBasePrintText currentDataElement;

	
	protected class ExporterContext extends BaseExporterContext implements JRXlsxExporterContext
	{
	}

	
	/**
	 * @see #XlsxMetadataExporter(JasperReportsContext)
	 */
	public XlsxMetadataExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public XlsxMetadataExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
		
		maxColumnIndex = 16383;
	}


	@Override
	protected Class<XlsxMetadataExporterConfiguration> getConfigurationInterface()
	{
		return XlsxMetadataExporterConfiguration.class;
	}


	@Override
	protected Class<XlsxMetadataReportConfiguration> getItemConfigurationInterface()
	{
		return XlsxMetadataReportConfiguration.class;
	}
	

	@Override
	protected void initExport()
	{
		super.initExport();
		sheetInfo = null;
		currentRow = new HashMap<>();
		repeatedValues = new HashMap<>();
		columnHeadersRow = new HashMap<>();
		onePagePerSheetMap.clear();
		sheetsBeforeCurrentReport = 0;
		sheetsBeforeCurrentReportMap.clear();
		
	}
	

	@Override
	protected void initReport()
	{
		super.initReport();
		
		XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();

		styleHelper.setConfiguration(configuration); 

		nature = 
			new JRXlsxExporterNature(
				jasperReportsContext, 
				filter, 
				configuration.isIgnoreGraphics(), 
				configuration.isIgnorePageMargins()
				);
		setColumnNames();
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

			XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
			configureDefinedNames(configuration.getDefinedNames());

			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && !pages.isEmpty())
			{
				PageRange pageRange = getPageRange();
				int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
				int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

				if (Boolean.TRUE.equals(configuration.isOnePagePerSheet()))
				{

					for(pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
					{
						checkInterrupted();
						if(pageExported)
						{
							closeSheet();
						}
						JRPrintPage page = pages.get(pageIndex);
						pageFormat = jasperPrint.getPageFormat(pageIndex);

						sheetInfo = getSheetInfo(configuration, null);
						createSheet(sheetInfo);

						// we need to count all sheets generated for all exported documents
						sheetIndex++;
						sheetNamesIndex++;
						rowIndex = 0;
						for(String key : rowSpanStartIndexesMap.keySet())
						{
							rowSpanStartIndexesMap.put(key, 0);
						}
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
					sheetInfo = getSheetInfo(configuration, jasperPrint.getName());
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
			else if(reportIndex == items.size() -1 && !pageExported)
			{
				exportEmptyReport();
			}
			
			sheetsBeforeCurrentReport = Boolean.TRUE.equals(configuration.isOnePagePerSheet()) ? sheetIndex : sheetsBeforeCurrentReport + 1;
		}
		closeSheet();
		closeWorkbook(os);
	}
	
	@Override
	protected void exportEmptyReport() throws JRException, IOException 
	{
		pageFormat = jasperPrint.getPageFormat();
		sheetInfo = getSheetInfo(null, jasperPrint.getName());
		createSheet(sheetInfo);
		sheetIndex++;
		sheetNamesIndex++;
		rowIndex = 0;
		resetAutoFilters();
		if (filter instanceof ResetableExporterFilter)
		{
			((ResetableExporterFilter)filter).reset();
		}
		exportPage(new JRBasePrintPage());
	}

	
	/**
	 * 
	 */
	protected int exportPage(JRPrintPage page) throws JRException
	{
		if (oldPageFormat != pageFormat)
		{
			oldPageFormat = pageFormat;
		}
		
		XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
		if(currentRow == null)
		{
			currentRow = new HashMap<>();
		}
		else if(!currentRow.isEmpty()) 
		{
			writeCurrentRow(currentRow, repeatedValues);
		}
		exportElements(page.getElements(), null);
		
		if(columnNames.size() > maxColumnIndex+1)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_COLUMN_INDEX_BEYOND_LIMIT, 
					columnNames.size(), 
					maxColumnIndex+1);
			
		}
		// write last row
		if (!columnNames.isEmpty())
		{
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
	
	protected void exportElements(List<JRPrintElement> elements, JRStyle style) throws JRException
	{
		if (elements != null) 
		{
			for (int i = 0; i < elements.size(); ++i) 
			{
				JRPrintElement element = elements.get(i);
				if (element instanceof JRPrintFrame) 
				{
					exportElements(((JRPrintFrame) element).getElements(), element.getStyle());
				} 
				else 
				{
					String sheetName = element.getPropertiesMap().getProperty(PROPERTY_SHEET_NAME);
					if (sheetName != null) 
					{
						setSheetName(sheetName);
					}
					exportElement(element, style);

					String currentColumnName = element.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);

					String rowFreeze = getPropertiesUtil().getProperty(element, PROPERTY_FREEZE_ROW_EDGE);

					int rowFreezeIndex = rowFreeze == null 
							? -1
							: (CellEdgeEnum.BOTTOM.getName().equals(rowFreeze) ? rowIndex + 1 : rowIndex);

					String columnFreeze = getPropertiesUtil().getProperty(element, PROPERTY_FREEZE_COLUMN_EDGE);

					int columnFreezeIndex = columnFreeze == null 
							? -1
							: (CellEdgeEnum.RIGHT.getName().equals(columnFreeze) 
									? columnNamesMap.get(currentColumnName) + 1
									: columnNamesMap.get(currentColumnName));

					if (rowFreezeIndex > 0 || columnFreezeIndex > 0) 
					{
						setFreezePane(rowFreezeIndex, columnFreezeIndex);
					}
				}
			} 
		}
	}
	
	public JRPrintImage getImage(ExporterInput exporterInput, JRPrintElementIndex imageIndex) throws JRException//FIXMECONTEXT move these to an abstract up?
	{
		List<ExporterInputItem> items = exporterInput.getItems();
		ExporterInputItem item = items.get(imageIndex.getReportIndex());
		JasperPrint report = item.getJasperPrint();
		JRPrintPage page = report.getPages().get(imageIndex.getPageIndex());

		Integer[] elementIndexes = imageIndex.getAddressArray();
		Object element = page.getElements().get(elementIndexes[0]);

		for (int i = 1; i < elementIndexes.length; ++i)
		{
			JRPrintFrame frame = (JRPrintFrame) element;
			element = frame.getElements().get(elementIndexes[i]);
		}

		if(element instanceof JRGenericPrintElement)
		{
			JRGenericPrintElement genericPrintElement = (JRGenericPrintElement)element;
			return ((GenericElementXlsxMetadataHandler)GenericElementHandlerEnviroment.getInstance(jasperReportsContext).getElementHandler(
					genericPrintElement.getGenericType(), 
					XLSX_METADATA_EXPORTER_KEY
					)).getImage(exporterContext, genericPrintElement);
		}
		
		return (JRPrintImage) element;
	}


	/**
	 *
	 */
	protected void exportStyledText(JRStyle style, JRStyledText styledText, Locale locale, boolean isStyledText, String currentData)
	{
		XlsxRunHelper runHelper = new XlsxRunHelper(jasperReportsContext, getExporterKey());

		if (currentData == null)
		{
			String text = styledText.getText();
			
			int runLimit = 0;
			
			AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();
			
			while(runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length())
			{
				runHelper.export(
						style, iterator.getAttributes(), 
						text.substring(iterator.getIndex(), runLimit),
						locale,
						invalidCharReplacement,
						isStyledText
						);
				
				iterator.setIndex(runLimit);
			}
		}
		else
		{
			runHelper.export(
					style, new HashMap<>(), 
					currentData,
					locale,
					invalidCharReplacement,
					isStyledText
					);
			
		}

		String sharedString = runHelper.getSharedString();
		int index = sharedStringsHelper.export(sharedString);
		sheetHelper.write(String.valueOf(index));
	}


	protected JRPrintElementIndex getElementIndex(int colIndex)
	{
		return new JRPrintElementIndex(
					reportIndex,
					pageIndex,
					String.valueOf(colIndex + 1 + rowIndex * (columnNames == null ? 0 : columnNames.size()))
					);
	}


	/**
	 *
	 */
	public static JRPrintElementIndex getPrintElementIndex(String imageName)
	{
		if (!imageName.startsWith(IMAGE_NAME_PREFIX))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INVALID_IMAGE_NAME,
					imageName);
		}

		return JRPrintElementIndex.parsePrintElementIndex(imageName.substring(IMAGE_NAME_PREFIX_LEGTH));
	}


	/**
	 *
	 */
	protected void setBackcolor(Color color)
	{
		backcolorStack.addLast(backcolor);

		backcolor = color;
	}


	protected void restoreBackcolor()
	{
		backcolor = backcolorStack.removeLast();
	}


	protected String getHyperlinkTarget(JRPrintHyperlink link)
	{
		String target = null;
		switch(link.getHyperlinkTargetValue())
		{
			case SELF :
			{
				target = "_self";
				break;
			}
			case BLANK :
			default :
			{
				target = "_blank";
				break;
			}
		}
		return target;
	}


	protected String getHyperlinkURL(JRPrintHyperlink link)
	{
		String href = null;

		XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
		
		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(XlsReportConfiguration.PROPERTY_IGNORE_HYPERLINK, link);
		if (ignoreHyperlink == null)
		{
			ignoreHyperlink = configuration.isIgnoreHyperlink();
		}
		
		//test for ignore hyperlinks done here
		if (!ignoreHyperlink)
		{
			JRHyperlinkProducer customHandler = getHyperlinkProducer(link);
			if (customHandler == null)
			{
				boolean includeAnchors = !Boolean.TRUE.equals(configuration.isIgnoreAnchors());
				boolean onePagePerSheet = Boolean.TRUE.equals(configuration.isOnePagePerSheet());
				switch(link.getHyperlinkTypeValue())
				{
					case REFERENCE :
					{
						if(link.getHyperlinkReference() != null) 
						{
							try
							{
								href = link.getHyperlinkReference().replaceAll("\\s", URLEncoder.encode(" ","UTF-8"));
							}
							catch (UnsupportedEncodingException e) 
							{
								href = link.getHyperlinkReference();
							}
						}
						
						break;
					}
					case LOCAL_ANCHOR :
					{
						if (includeAnchors && link.getHyperlinkAnchor() != null)		//test for ignore anchors done here
						{
							href = link.getHyperlinkAnchor();
						}
						break;
					}
					case LOCAL_PAGE :
					{
						if (includeAnchors && link.getHyperlinkPage() != null)		//test for ignore anchors done here
						{
							href = JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (onePagePerSheet ? link.getHyperlinkPage().toString() : "1");
						}
						break;
					}
					case REMOTE_ANCHOR :
					{
						if (
							link.getHyperlinkReference() != null &&
							link.getHyperlinkAnchor() != null
							)
						{
							try 
							{
								href = link.getHyperlinkReference().replaceAll("\\s", URLEncoder.encode(" ","UTF-8"));
							} 
							catch (UnsupportedEncodingException e) 
							{
								href = link.getHyperlinkReference();
							}
							href = href + "#" + link.getHyperlinkAnchor();
						}
						break;
					}
					case REMOTE_PAGE :
					case NONE :
					default :
					{
						break;
					}
				}
			}
			else
			{
				href = customHandler.getHyperlink(link);
			}
		}

		return href;
	}

	protected void insertPageAnchor()
	{
		if(!Boolean.TRUE.equals(getCurrentItemConfiguration().isIgnoreAnchors()) && startPage)
		{
			String anchorPage = JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (sheetIndex - sheetsBeforeCurrentReport);
			String ref = "'" + JRStringUtil.xmlEncode(currentSheetName) + "'!$A$1";	
			definedNames.append("<definedName name=\"" + getDefinedName(anchorPage) +"\">"+ ref +"</definedName>\n");
			startPage = false;
		}
	}
	

	@Override
	protected void closeWorkbook(OutputStream os) throws JRException //FIXMEXLSX could throw IOException here, as other implementations do
	{
		if(sheetMapping != null && definedNamesMap != null && !definedNamesMap.isEmpty())
		{
			for(Map.Entry<NameScope, String> entry : definedNamesMap.entrySet())
			{
				String name = entry.getKey().getName();
				String localSheetId = "";
				if(name != null && entry.getValue() != null) 
				{
					String scope = entry.getKey().getScope();
					// name and name scope are ignoring case in Excel
					if(scope != null && !scope.equalsIgnoreCase(DEFAULT_DEFINED_NAME_SCOPE) && sheetMapping.containsKey(scope))
					{
						localSheetId = " localSheetId=\"" + sheetMapping.get(scope) + "\"";
					}
					definedNames.append("<definedName name=\"" + name + "\"" + localSheetId + ">" + entry.getValue() + "</definedName>\n");
				}
			}
		}
		
		styleHelper.export();
		styleHelper.close();

		sharedStringsHelper.exportFooter();
		sharedStringsHelper.close();

		try
		{
			wbHelper.exportFooter();
			wbHelper.close();

			relsHelper.exportFooter();
			relsHelper.close();
			
			ctHelper.exportFooter();
			ctHelper.close();

			appHelper.exportFooter();
			appHelper.close();

			coreHelper.exportFooter();
			coreHelper.close();

			String password = getCurrentConfiguration().getEncryptionPassword();
			if (password == null || password.trim().length() == 0)
			{
				xlsxZip.zipEntries(os);
			}
			else
			{
				// isolate POI encryption code into separate class to avoid POI dependency when not needed
				OoxmlEncryptUtil.zipEntries(xlsxZip, os, password);
			}

			xlsxZip.dispose();			
		}
		catch (IOException e)
		{
			throw new JRException(e);
		}
	}

	protected void createSheet(SheetInfo sheetInfo)
	{
		startPage = true;
		currentSheetPageScale = sheetInfo.sheetPageScale;
		currentSheetFirstPageNumber = sheetInfo.sheetFirstPageNumber;
		currentSheetName = sheetInfo.sheetName;
		firstSheetName = firstSheetName == null ? currentSheetName : firstSheetName;
		wbHelper.exportSheet(sheetIndex + 1, currentSheetName, sheetMapping);
		ctHelper.exportSheet(sheetIndex + 1);
		relsHelper.exportSheet(sheetIndex + 1);
		XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
		ExportZipEntry sheetRelsEntry = xlsxZip.addSheetRels(sheetIndex + 1);
		Writer sheetRelsWriter = sheetRelsEntry.getWriter();
		sheetRelsHelper = new XlsxSheetRelsHelper(jasperReportsContext, sheetRelsWriter);

		ExportZipEntry sheetEntry = xlsxZip.addSheet(sheetIndex + 1);
		Writer sheetWriter = sheetEntry.getWriter();
		sheetHelper = 
			new XlsxSheetHelper(
				jasperReportsContext,
				sheetWriter, 
				sheetRelsHelper,
				configuration
				);
		
		ExportZipEntry drawingRelsEntry = xlsxZip.addDrawingRels(sheetIndex + 1);
		Writer drawingRelsWriter = drawingRelsEntry.getWriter();
		drawingRelsHelper = new XlsxDrawingRelsHelper(jasperReportsContext, drawingRelsWriter);
		
		ExportZipEntry drawingEntry = xlsxZip.addDrawing(sheetIndex + 1);
		Writer drawingWriter = drawingEntry.getWriter();
		drawingHelper = new XlsxDrawingHelper(jasperReportsContext, drawingWriter, drawingRelsHelper);
		
		cellHelper = new XlsxCellHelper(jasperReportsContext, sheetWriter, styleHelper);
		
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
		sheetHelper.exportHeader(
				showGridlines, 
				(sheetInfo.sheetPageScale == null ? 0 : sheetInfo.sheetPageScale), 
				sheetInfo.rowFreezeIndex, 
				sheetInfo.columnFreezeIndex, 
				maxColumnIndex,
				jasperPrint, 
				sheetInfo.tabColor);
		sheetRelsHelper.exportHeader(sheetIndex + 1);
		drawingHelper.exportHeader();
		drawingRelsHelper.exportHeader();
	}


	@Override
	protected void closeSheet()
	{			
		if (sheetHelper != null)
		{
			// merged cells generate accessibility warnings in Excel documents:
			
//			for(String columnName: columnNames)
//			{
//				int rowSpanStartIndex = rowSpanStartIndexesMap.get(columnName);
//				sheetHelper.exportMergedCells(rowSpanStartIndex, columnNamesMap.get(columnName), maxColumnIndex, rowIndex - rowSpanStartIndex, 1);
//				rowSpanStartIndexesMap.put(columnName, 0);
//			}

			XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
			
			boolean isIgnorePageMargins = configuration.isIgnorePageMargins();
			String password = configuration.getPassword();
			
			if(currentSheetFirstPageNumber != null && currentSheetFirstPageNumber > 0)
			{
				sheetHelper.exportFooter(
						sheetIndex, 
						oldPageFormat == null ? pageFormat : oldPageFormat, 
						isIgnorePageMargins, 
						sheetAutoFilter,
						currentSheetPageScale, 
						currentSheetFirstPageNumber,
						false,
						pageIndex - sheetInfo.sheetFirstPageIndex,
						sheetInfo.printSettings,
						password
						);
					firstPageNotSet = false;
			}
			else
			{
				Integer documentFirstPageNumber = configuration.getFirstPageNumber();
				if(documentFirstPageNumber != null && documentFirstPageNumber > 0 && firstPageNotSet)
				{
					sheetHelper.exportFooter(
						sheetIndex, 
						oldPageFormat == null ? pageFormat : oldPageFormat, 
						isIgnorePageMargins, 
						sheetAutoFilter,
						currentSheetPageScale, 
						documentFirstPageNumber,
						false,
						pageIndex - sheetInfo.sheetFirstPageIndex,
						sheetInfo.printSettings,
						password
						);
					firstPageNotSet = false;
				}
				else
				{
					sheetHelper.exportFooter(
						sheetIndex, 
						oldPageFormat == null ? pageFormat : oldPageFormat, 
						isIgnorePageMargins, 
						sheetAutoFilter,
						currentSheetPageScale, 
						null,
						firstPageNotSet,
						pageIndex - sheetInfo.sheetFirstPageIndex,
						sheetInfo.printSettings,
						password
						);
				}
			}
			if(sheetAutoFilter != null)
			{
				int index = Math.max(0, sheetIndex-1);
				definedNames.append("<definedName name=\"_xlnm._FilterDatabase\" localSheetId=\"" + index + "\">'" + JRStringUtil.xmlEncode(currentSheetName) +"'!"+sheetAutoFilter+"</definedName>\n");
			}
			sheetHelper.close();

			sheetRelsHelper.exportFooter();
			sheetRelsHelper.close();
			
			drawingHelper.exportFooter();
			drawingHelper.close();

			drawingRelsHelper.exportFooter();
			drawingRelsHelper.close();
		}
	}

	
	public void exportImage(final JRPrintImage image, int colIndex, final JRStyle parentStyle) throws JRException 
	{
		int topPadding =
			Math.max(image.getLineBox().getTopPadding(), getImageBorderCorrection(image.getLineBox().getTopPen()));
		int leftPadding =
			Math.max(image.getLineBox().getLeftPadding(), getImageBorderCorrection(image.getLineBox().getLeftPen()));
		int bottomPadding =
			Math.max(image.getLineBox().getBottomPadding(), getImageBorderCorrection(image.getLineBox().getBottomPen()));
		int rightPadding =
			Math.max(image.getLineBox().getRightPadding(), getImageBorderCorrection(image.getLineBox().getRightPen()));

		int availableImageWidth = image.getWidth() - leftPadding - rightPadding;
		availableImageWidth = availableImageWidth < 0 ? 0 : availableImageWidth;

		int availableImageHeight = image.getHeight() - topPadding - bottomPadding;
		availableImageHeight = availableImageHeight < 0 ? 0 : availableImageHeight;

		cellHelper.exportHeader(image,
								rowIndex, 
								colIndex, 
								maxColumnIndex, 
								null, 
								null, 
								null, 
								true, 
								false, 
								false, 
								false, 
								false, 
								RotationEnum.NONE, 
								sheetInfo,
								null,
								parentStyle);
		Renderable renderer = image.getRenderer();

		if (
			renderer != null
			&& availableImageWidth > 0 
			&& availableImageHeight > 0
			)
		{
			InternalImageProcessor imageProcessor = 
				new InternalImageProcessor(
					image, 
					colIndex,
					availableImageWidth,
					availableImageHeight
					);
				
			InternalImageProcessorResult imageProcessorResult = null;
			
			try
			{
				imageProcessorResult = imageProcessor.process(renderer);
			}
			catch (Exception e)
			{
				Renderable onErrorRenderer = getRendererUtil().handleImageError(e, image.getOnErrorTypeValue());
				if (onErrorRenderer != null)
				{
					imageProcessorResult = imageProcessor.process(onErrorRenderer);
				}
			}
			
			if (imageProcessorResult != null)
			{
				double cropTop = 0;
				double cropLeft = 0;
				double cropBottom = 0;
				double cropRight = 0;
				
				int angle = 0;

				switch (image.getScaleImageValue())
				{
					case FILL_FRAME :
					{
						switch (image.getRotation())
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
	 					break;
					}
					case CLIP :
					{
						double normalWidth = availableImageWidth;
						double normalHeight = availableImageHeight;

						Dimension2D dimension = imageProcessorResult.dimension;
						if (dimension != null)
						{
							normalWidth = dimension.getWidth();
							normalHeight = dimension.getHeight();
						}

						switch (image.getRotation())
						{
							case LEFT:
								if (dimension == null)
								{
									normalWidth = availableImageHeight;
									normalHeight = availableImageWidth;
								}
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageHeight - normalWidth) / availableImageHeight;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageHeight - normalWidth) / availableImageHeight / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageHeight - normalWidth) / availableImageHeight;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageWidth - normalHeight) / availableImageWidth;
										break;
									case MIDDLE :
										cropTop = (availableImageWidth - normalHeight) / availableImageWidth / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageWidth - normalHeight) / availableImageWidth;
										cropBottom = 0;
										break;
								}
								angle = -90;
								break;
							case RIGHT:
								if (dimension == null)
								{
									normalWidth = availableImageHeight;
									normalHeight = availableImageWidth;
								}
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageHeight - normalWidth) / availableImageHeight;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageHeight - normalWidth) / availableImageHeight / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageHeight - normalWidth) / availableImageHeight;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageWidth - normalHeight) / availableImageWidth;
										break;
									case MIDDLE :
										cropTop = (availableImageWidth - normalHeight) / availableImageWidth / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageWidth - normalHeight) / availableImageWidth;
										cropBottom = 0;
										break;
								}
								angle = 90;
								break;
							case UPSIDE_DOWN:
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageWidth - normalWidth) / availableImageWidth;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageWidth - normalWidth) / availableImageWidth / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageWidth - normalWidth) / availableImageWidth;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageHeight - normalHeight) / availableImageHeight;
										break;
									case MIDDLE :
										cropTop = (availableImageHeight - normalHeight) / availableImageHeight / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageHeight - normalHeight) / availableImageHeight;
										cropBottom = 0;
										break;
								}
								angle = 180;
								break;
							case NONE:
							default:
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageWidth - normalWidth) / availableImageWidth;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageWidth - normalWidth) / availableImageWidth / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageWidth - normalWidth) / availableImageWidth;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageHeight - normalHeight) / availableImageHeight;
										break;
									case MIDDLE :
										cropTop = (availableImageHeight - normalHeight) / availableImageHeight / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageHeight - normalHeight) / availableImageHeight;
										cropBottom = 0;
										break;
								}
								angle = 0;
								break;
						}

						break;
					}
					case RETAIN_SHAPE :
					default :
					{
						double normalWidth = availableImageWidth;
						double normalHeight = availableImageHeight;

						Dimension2D dimension = imageProcessorResult.dimension;
						if (dimension != null)
						{
							normalWidth = dimension.getWidth();
							normalHeight = dimension.getHeight();
						}

						double ratioX;
						double ratioY;
						double imageWidth;
						double imageHeight;

						switch (image.getRotation())
						{
							case LEFT:
								if (dimension == null)
								{
									normalWidth = availableImageHeight;
									normalHeight = availableImageWidth;
								}
								ratioX = availableImageWidth / normalHeight;
								ratioY = availableImageHeight / normalWidth;
								ratioX = ratioX < ratioY ? ratioX : ratioY;
								ratioY = ratioX;
								imageWidth = (int)(normalHeight * ratioX);
								imageHeight = (int)(normalWidth * ratioY);
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageHeight - imageHeight) / availableImageHeight;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageHeight - imageHeight) / availableImageHeight / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageHeight - imageHeight) / availableImageHeight;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageWidth - imageWidth) / availableImageWidth;
										break;
									case MIDDLE :
										cropTop = (availableImageWidth - imageWidth) / availableImageWidth / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageWidth - imageWidth) / availableImageWidth;
										cropBottom = 0;
										break;
								}
								angle = -90;
								break;
							case RIGHT:
								if (dimension == null)
								{
									normalWidth = availableImageHeight;
									normalHeight = availableImageWidth;
								}
								ratioX = availableImageWidth / normalHeight;
								ratioY = availableImageHeight / normalWidth;
								ratioX = ratioX < ratioY ? ratioX : ratioY;
								ratioY = ratioX;
								imageWidth = (int)(normalHeight * ratioX);
								imageHeight = (int)(normalWidth * ratioY);
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageHeight - imageHeight) / availableImageHeight;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageHeight - imageHeight) / availableImageHeight / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageHeight - imageHeight) / availableImageHeight;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageWidth - imageWidth) / availableImageWidth;
										break;
									case MIDDLE :
										cropTop = (availableImageWidth - imageWidth) / availableImageWidth / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageWidth - imageWidth) / availableImageWidth;
										cropBottom = 0;
										break;
								}
								angle = 90;
								break;
							case UPSIDE_DOWN:
								ratioX = availableImageWidth / normalWidth;
								ratioY = availableImageHeight / normalHeight;
								ratioX = ratioX < ratioY ? ratioX : ratioY;
								ratioY = ratioX;
								imageWidth = (int)(normalWidth * ratioX);
								imageHeight = (int)(normalHeight * ratioY);
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageWidth - imageWidth) / availableImageWidth;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageWidth - imageWidth) / availableImageWidth / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageWidth - imageWidth) / availableImageWidth;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageHeight - imageHeight) / availableImageHeight;
										break;
									case MIDDLE :
										cropTop = (availableImageHeight - imageHeight) / availableImageHeight / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageHeight - imageHeight) / availableImageHeight;
										cropBottom = 0;
										break;
								}
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
								switch (image.getHorizontalImageAlign())
								{
									case RIGHT :
										cropLeft = (availableImageWidth - imageWidth) / availableImageWidth;
										cropRight = 0;
										break;
									case CENTER :
										cropLeft = (availableImageWidth - imageWidth) / availableImageWidth / 2;
										cropRight = cropLeft;
										break;
									case LEFT :
									default :
										cropLeft = 0;
										cropRight = (availableImageWidth - imageWidth) / availableImageWidth;
										break;
								}
								switch (image.getVerticalImageAlign())
								{
									case TOP :
										cropTop = 0;
										cropBottom = (availableImageHeight - imageHeight) / availableImageHeight;
										break;
									case MIDDLE :
										cropTop = (availableImageHeight - imageHeight) / availableImageHeight / 2;
										cropBottom = cropTop;
										break;
									case BOTTOM :
									default :
										cropTop = (availableImageHeight - imageHeight) / availableImageHeight;
										cropBottom = 0;
										break;
								}
								angle = 0;
								break;
						}
					}
				}

				XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
				
				if (!Boolean.TRUE.equals(configuration.isIgnoreAnchors()))
				{
					insertPageAnchor();
					if (image.getAnchorName() != null)
					{
						String ref = "'" + JRStringUtil.xmlEncode(currentSheetName) + "'!$" + ExcelAbstractExporter.getColumIndexName(colIndex, maxColumnIndex) + "$" + (rowIndex + 1);
						definedNames.append("<definedName name=\"" + getDefinedName(image.getAnchorName()) +"\">"+ ref +"</definedName>\n");
					}
				}

				drawingRelsHelper.exportImage(imageProcessorResult.imagePath);

				ImageAnchorTypeEnum imageAnchorType = 
					ImageAnchorTypeEnum.getByName(
						JRPropertiesUtil.getOwnProperty(image, XlsReportConfiguration.PROPERTY_IMAGE_ANCHOR_TYPE)
						);
				if (imageAnchorType == null)
				{
					imageAnchorType = configuration.getImageAnchorType();
					if (imageAnchorType == null)
					{
						imageAnchorType = ImageAnchorTypeEnum.MOVE_NO_SIZE;
					}
				}
				drawingHelper.write("<xdr:twoCellAnchor editAs=\"" + getAnchorType(imageAnchorType) + "\">\n");
				drawingHelper.write("<xdr:from><xdr:col>" +
					colIndex +
					"</xdr:col><xdr:colOff>" +
					LengthUtil.emu(leftPadding) +
					"</xdr:colOff><xdr:row>" +
					rowIndex +
					"</xdr:row><xdr:rowOff>" +
					LengthUtil.emu(topPadding) +
					"</xdr:rowOff></xdr:from>\n");
				drawingHelper.write("<xdr:to><xdr:col>" +
					(colIndex + 1) +
					"</xdr:col><xdr:colOff>" +
					LengthUtil.emu(-rightPadding) +
					"</xdr:colOff><xdr:row>" +
					(rowIndex + 1) +
					"</xdr:row><xdr:rowOff>" +
					LengthUtil.emu(-bottomPadding) +
					"</xdr:rowOff></xdr:to>\n");
				
				drawingHelper.write("<xdr:pic>\n");
				String altText = image.getHyperlinkTooltip() == null ? "" : image.getHyperlinkTooltip();
				if(!altText.isEmpty())
				{
					altText = " descr=\"" + altText +"\"";
				}
				int hashCode = image.hashCode();
				drawingHelper.write("<xdr:nvPicPr><xdr:cNvPr id=\"" + (hashCode > 0 ? hashCode : -hashCode) + "\" name=\"Picture\"" + altText + ">\n");

				String href = HyperlinkTypeEnum.LOCAL_ANCHOR.equals(image.getHyperlinkTypeValue()) || HyperlinkTypeEnum.LOCAL_PAGE.equals(image.getHyperlinkTypeValue()) ? "#" + getHyperlinkURL(image) : getHyperlinkURL(image);
				if (href != null)
				{
					drawingHelper.exportHyperlink(href);
				}
				
				drawingHelper.write("</xdr:cNvPr><xdr:cNvPicPr/></xdr:nvPicPr>\n");
				drawingHelper.write("<xdr:blipFill>\n");
				drawingHelper.write("<a:blip r:embed=\"" + imageProcessorResult.imagePath + "\"/>");
				drawingHelper.write("<a:srcRect/>");
				drawingHelper.write("<a:stretch><a:fillRect");
				drawingHelper.write(" l=\"" + (int)(100000 * cropLeft) + "\"");
				drawingHelper.write(" t=\"" + (int)(100000 * cropTop) + "\"");
				drawingHelper.write(" r=\"" + (int)(100000 * cropRight) + "\"");
				drawingHelper.write(" b=\"" + (int)(100000 * cropBottom) + "\"");
				drawingHelper.write("/></a:stretch>\n");
				drawingHelper.write("</xdr:blipFill>\n");
				drawingHelper.write("<xdr:spPr>\n");
				drawingHelper.write("  <a:xfrm rot=\"" + (60000 * angle) + "\">\n");
				drawingHelper.write("    <a:off x=\"0\" y=\"0\"/>\n");
				drawingHelper.write("    <a:ext cx=\"" + LengthUtil.emu(0) + "\" cy=\"" + LengthUtil.emu(0) + "\"/>");
				drawingHelper.write("  </a:xfrm>\n");
				drawingHelper.write("<a:prstGeom prst=\"rect\"></a:prstGeom>\n");
				drawingHelper.write("</xdr:spPr>\n");
				drawingHelper.write("</xdr:pic>\n");
				drawingHelper.write("<xdr:clientData/>\n");
				drawingHelper.write("</xdr:twoCellAnchor>\n");

			}
		}

		cellHelper.exportFooter();
	}


	private class InternalImageProcessor
	{
		private final JRPrintElement imageElement;
		private final RenderersCache imageRenderersCache;
		private final boolean needDimension; 
		private final int colIndex;
		private final int availableImageWidth;
		private final int availableImageHeight;

		protected InternalImageProcessor(
			JRPrintImage imageElement,
			int colIndex,
			int availableImageWidth,
			int availableImageHeight
			)
		{
			this.imageElement = imageElement;
			this.colIndex = colIndex;
			this.imageRenderersCache = imageElement.isUsingCache() ? renderersCache : new RenderersCache(getJasperReportsContext());
			this.needDimension = imageElement.getScaleImageValue() != ScaleImageEnum.FILL_FRAME; 
			if (
				imageElement.getRotation() == RotationEnum.LEFT
				|| imageElement.getRotation() == RotationEnum.RIGHT
				)
			{
				this.availableImageWidth = availableImageHeight;
				this.availableImageHeight = availableImageWidth;
			}
			else
			{
				this.availableImageWidth = availableImageWidth;
				this.availableImageHeight = availableImageHeight;
			}
		}
		
		private InternalImageProcessorResult process(Renderable renderer) throws JRException
		{
			if (renderer instanceof ResourceRenderer)
			{
				renderer = imageRenderersCache.getLoadedRenderer((ResourceRenderer)renderer);
			}
			
			// check dimension first, to avoid caching renderers that might not be used eventually, due to their dimension errors 
			Dimension2D dimension = null;
			if (needDimension)
			{
				DimensionRenderable dimensionRenderer = imageRenderersCache.getDimensionRenderable(renderer);
				dimension = dimensionRenderer == null ? null :  dimensionRenderer.getDimension(jasperReportsContext);
			}
			
			
			String imagePath = null;

			if (
				renderer instanceof DataRenderable //we do not cache imagePath for non-data renderers because they render width different width/height each time
				&& rendererToImagePathMap.containsKey(renderer.getId())
				)
			{
				imagePath = rendererToImagePathMap.get(renderer.getId());
			}
			else
			{
				JRPrintElementIndex imageIndex = getElementIndex(colIndex);

				DataRenderable imageRenderer = 
					getRendererUtil().getImageDataRenderable(
						imageRenderersCache,
						renderer,
						new Dimension(availableImageWidth, availableImageHeight),
						ModeEnum.OPAQUE == imageElement.getModeValue() ? imageElement.getBackcolor() : null
						);

				byte[] imageData = imageRenderer.getData(jasperReportsContext);
				String fileExtension = JRTypeSniffer.getImageTypeValue(imageData).getFileExtension();
				String imageName = IMAGE_NAME_PREFIX + imageIndex.toString() + (fileExtension == null ? "" : ("." + fileExtension));

				xlsxZip.addEntry(
					new FileBufferedZipEntry(
						"xl/media/" + imageName,
						imageData
						)
					);
				

				imagePath = imageName;
				//imagePath = "Pictures/" + imageName;

				if (imageRenderer == renderer)
				{
					//cache imagePath only for true ImageRenderable instances because the wrapping ones render with different width/height each time
					rendererToImagePathMap.put(renderer.getId(), imagePath);
				}
			}
				
			return new InternalImageProcessorResult(imagePath, dimension);
		}
	}

	private class InternalImageProcessorResult
	{
		protected final String imagePath;
		protected final Dimension2D dimension;
		
		protected InternalImageProcessorResult(String imagePath, Dimension2D dimension)
		{
			this.imagePath = imagePath;
			this.dimension = dimension;
		}
	}

	
	protected void exportLine(JRPrintLine line, int colIndex, final JRStyle parentStyle) 
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = null;
		LineDirectionEnum direction = null;
		float ratio = line.getWidth() / (float)line.getHeight();
		if (ratio > 1)
		{
			if(line.getHeight() > 1)
			{
				direction = line.getDirectionValue();
				pen = box.getPen();
			}
			else if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN)
			{
				pen = box.getTopPen();
			}
			else
			{
				pen = box.getBottomPen();
			}
		}
		else
		{
			if(line.getWidth() > 1)
			{
				direction = line.getDirectionValue();
				pen = box.getPen();
			}
			else if (line.getDirectionValue() == LineDirectionEnum.TOP_DOWN)
			{
				pen = box.getLeftPen();
			}
			else
			{
				pen = box.getRightPen();
			}
		}
		pen.setLineColor(line.getLinePen().getLineColor());
		pen.setLineStyle(line.getLinePen().getLineStyleValue());
		pen.setLineWidth(line.getLinePen().getLineWidth());

		cellHelper.exportHeader(box,
								rowIndex, 
								colIndex, 
								maxColumnIndex, 
								null, 
								null, 
								null, 
								true, 
								false, 
								false, 
								false, 
								false, 
								RotationEnum.NONE, 
								sheetInfo,
								direction,
								parentStyle);
		cellHelper.exportFooter();
	}
	
	protected void exportRectangle(JRPrintGraphicElement rectangle, int colIndex, final JRStyle parentStyle)
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = box.getPen();
		pen.setLineColor(rectangle.getLinePen().getLineColor());
		pen.setLineStyle(rectangle.getLinePen().getLineStyleValue());
		pen.setLineWidth(rectangle.getLinePen().getLineWidth());

		cellHelper.exportHeader(box,
				rowIndex, 
				colIndex, 
				maxColumnIndex, 
				null, 
				null, 
				null, 
				true, 
				false, 
				false, 
				false, 
				false, 
				RotationEnum.NONE, 
				sheetInfo,
				null, 
				parentStyle);
		cellHelper.exportFooter();
	}
	
	public void exportText(final JRPrintText text, int colIndex, final JRStyle parentStyle, String currentData) throws JRException
	{
		exportText(text, colIndex, rowIndex, parentStyle, currentData);
	}
	
	public void exportText(final JRPrintText text, int colIndex, int rowIndex, final JRStyle parentStyle, String currentData) throws JRException
	{
		final JRStyledText styledText = getStyledText(text);

		boolean useCurrentData = currentData != null;
		final String textStr = useCurrentData ? currentData : styledText.getText();
		TextValue textValue = null;
		String pattern = null;
		
		XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
		Boolean isDetectCellType = Boolean.TRUE.equals(configuration.isDetectCellType());
		
		if (isDetectCellType)
		{
			textValue = getTextValue(text, textStr, useCurrentData);
			if (textValue instanceof NumberTextValue)
			{
				pattern = ((NumberTextValue)textValue).getPattern();
			}
			else if (textValue instanceof DateTextValue)
			{
				pattern = ((DateTextValue)textValue).getPattern();
			}
		}
		
		final String convertedPattern = isDetectCellType ? getConvertedPattern(text, pattern) : null;
	
		cellHelper.exportHeader(
			text, 
			rowIndex, 
			colIndex, 
			maxColumnIndex, 
			textValue, 
			convertedPattern, 
			getTextLocale(text), 
			isWrapText(text) || Boolean.TRUE.equals(((JRXlsxExporterNature)nature).getColumnAutoFit(text)), 
			isCellHidden(text), 
			isCellLocked(text),
			isShrinkToFit(text), 
			isIgnoreTextFormatting(text),
			text.getRotationValue(),
			sheetInfo,
			null,
			parentStyle);
		
		String textFormula = useCurrentData ? null : getFormula(text);
		if (textFormula != null)
		{
			sheetHelper.write("<f>" + JRStringUtil.xmlEncode(textFormula) + "</f>\n");
		}

		if(!Boolean.TRUE.equals(configuration.isIgnoreAnchors()))
		{
			insertPageAnchor();
			if (text.getAnchorName() != null)
			{
				String ref = "'" + JRStringUtil.xmlEncode(currentSheetName) + "'!$" + ExcelAbstractExporter.getColumIndexName(colIndex, maxColumnIndex) + "$" + (rowIndex + 1);
				definedNames.append("<definedName name=\"" + getDefinedName(text.getAnchorName()) +"\">"+ ref +"</definedName>\n");
			}
		}

		String href = getHyperlinkURL(text);
		if (href != null)
		{
			sheetHelper.exportHyperlink(
					rowIndex, 
					colIndex,
					maxColumnIndex,
					href, 
					HyperlinkTypeEnum.LOCAL_ANCHOR.equals(text.getHyperlinkTypeValue()) || HyperlinkTypeEnum.LOCAL_PAGE.equals(text.getHyperlinkTypeValue()));
		}

		
		TextValueHandler handler = getTextValueHandler(text, convertedPattern, currentData);
		
		if (textValue != null)
		{
			//detect cell type
			textValue.handle(handler);
		}
		else
		{
			handler.handle((StringTextValue)null);
		}

		cellHelper.exportFooter();
	}

    protected TextValueHandler getTextValueHandler(final JRPrintText text, final String convertedPattern, String currentData)
    {
		final JRStyledText styledText = getStyledText(text);
		final String textStr = currentData == null ? styledText.getText() : currentData;
		
    	return new TextValueHandler() 
		{
			@Override
			public void handle(BooleanTextValue textValue) throws JRException 
			{
				if(textValue.getValue() != null)
				{
					sheetHelper.write("<v>" + textValue.getValue() + "</v>");
				}
			}
			
			@Override
			public void handle(DateTextValue textValue) throws JRException 
			{
				Date date = textValue.getValue();
				if(date != null)
				{
					sheetHelper.write(
						"<v>" 
						+ JRDataUtils.getExcelSerialDayNumber(
							date, 
							getTextLocale(text), 
							getTextTimeZone(text)
							)
						+ "</v>"
						);
				}
			}
			
			@Override
			public void handle(NumberTextValue textValue) throws JRException 
			{
				
				if (textValue.getValue() != null)
				{
					sheetHelper.write("<v>"); 
					double doubleValue = textValue.getValue().doubleValue();
					if (DefaultFormatFactory.STANDARD_NUMBER_FORMAT_DURATION.equals(convertedPattern))
					{
						doubleValue = doubleValue / 86400;
					}
					sheetHelper.write(String.valueOf(doubleValue));
					sheetHelper.write("</v>");
				}
			}
			
			@Override
			public void handle(StringTextValue textValue) throws JRException 
			{
				if (textStr != null && textStr.length() > 0)
				{
					sheetHelper.write("<v>");
					String markup = text.getMarkup();
					boolean isStyledText = markup != null && !JRCommonText.MARKUP_NONE.equals(markup) && !isIgnoreTextFormatting(text);
					exportStyledText(text.getStyle(), styledText, getTextLocale(text), isStyledText, currentData);
					sheetHelper.write("</v>");
				}
			}
		};
    }
    
    protected TextValue getTextValue(JRPrintText text, String textStr, boolean useCurrentData)
    {
    	boolean isDetectCellType = Boolean.TRUE.equals(getCurrentItemConfiguration().isDetectCellType());
    	if(!useCurrentData)
    	{
    		if(isDetectCellType)
    		{
    			return super.getTextValue(text, textStr);
    		}
    		else
    		{
    			return super.getTextValueString(text, textStr);
    		}
    	}
    	if (currentDataElement == null)
    	{
    		currentDataElement = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
    	}
    	currentDataElement.setValueClassName(isDetectCellType ? text.getValueClassName() : null);
    	currentDataElement.setPattern(text.getPattern());
    	currentDataElement.setLocaleCode(text.getLocaleCode());
    	currentDataElement.setTimeZoneId(text.getTimeZoneId());
    	return super.getTextValue(currentDataElement, textStr);
    }
    
    protected void exportElement(final JRPrintElement element, final JRStyle parentStyle) throws JRException 
	{
		String currentColumnName = element.getPropertiesMap().getProperty(PROPERTY_COLUMN_NAME);
		if (currentColumnName != null && currentColumnName.length() > 0) 
		{
			String currentData = (element instanceof JRPrintText && element.getPropertiesMap().containsProperty(PROPERTY_DATA))
					? element.getPropertiesMap().getProperty(PROPERTY_DATA)
					: null;
			
			Boolean ignoreRowHeight = ((JRXlsxExporterNature)nature).getIgnoreRowHeight(element);
			adjustRowHeight(
				Boolean.TRUE.equals(ignoreRowHeight) ? 0 : element.getHeight(), 
				((JRXlsxExporterNature)nature).getRowAutoFit(element)
				);
			setColumnName(currentColumnName);
			addElement(
					element, 
					getPropertiesUtil().getBooleanProperty(element, PROPERTY_REPEAT_VALUE, false), 
					currentColumnName,
					parentStyle,
					currentData);
		}
	}

	protected void adjustRowHeight(int rowHeight, Boolean isAutofit) 
	{
		if(isAutofit != null || !Boolean.TRUE.equals(currentRow.get(CURRENT_ROW_AUTOFIT)))
		{
			currentRow.put(CURRENT_ROW_AUTOFIT, isAutofit);
		} 
		if(!currentRow.containsKey(CURRENT_ROW_HEIGHT) || (Integer)currentRow.get(CURRENT_ROW_HEIGHT) < rowHeight) 
		{
			currentRow.put(CURRENT_ROW_HEIGHT, rowHeight);
		}
	}
	
	protected void addElement(final JRPrintElement element, boolean repeatValue, String currentColumnName, final JRStyle parentStyle, String currentData) throws JRException 
	{
		if (!columnNames.isEmpty()) 
		{
			if (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && isColumnReadOnTime(currentRow, currentColumnName)) 
			{	// the column is for export but was not read yet and comes in the expected order
				addElement(currentRow, element, currentColumnName, parentStyle, currentData);
				
			} else if ( (columnNames.contains(currentColumnName) && !currentRow.containsKey(currentColumnName) && !isColumnReadOnTime(currentRow, currentColumnName)) // the column is for export, was not read yet, but it is read after it should be
					|| (columnNames.contains(currentColumnName) && currentRow.containsKey(currentColumnName)) ) 
			{	// the column is for export and was already read

				writeCurrentRow(currentRow, repeatedValues);
				addElement(currentRow, element, currentColumnName, parentStyle, currentData);
			}
			// set auto fill columns
			if(repeatValue) {
				if (currentColumnName != null && !currentColumnName.isEmpty()) {
					addElement(repeatedValues, element, currentColumnName, parentStyle, currentData);
				}
			} else {
				repeatedValues.remove(currentColumnName);
				repeatedValues.remove(currentColumnName + PARENT_STYLE);
				repeatedValues.remove(currentColumnName + CURRENT_DATA);
			}
		}
	}
	
	protected void addElement(Map<String, Object> currentMap, final JRPrintElement element, String currentColumnName, final JRStyle parentStyle, String currentData) 
	{
		currentMap.put(currentColumnName, element);
		if(currentData != null)
		{
			currentMap.put(currentColumnName + CURRENT_DATA, currentData);
		}
		XlsxMetadataReportConfiguration configuration = getCurrentItemConfiguration();
		if(parentStyle != null 
				&& !(Boolean.TRUE.equals(configuration.isIgnoreTextFormatting()) 
						&& (Boolean.TRUE.equals(configuration.isIgnoreCellBorder()) 
						|| Boolean.TRUE.equals(configuration.isIgnoreGraphics()))))
		{
			currentMap.put(currentColumnName + PARENT_STYLE, parentStyle);
		}
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
	
	protected void writeCurrentRow(Map<String, Object> currentRow, Map<String, Object> repeatedValues)  throws JRException 
	{
		int columnsCount = columnNames == null ? 0 : columnNames.size();
		exportHeaderRow(columnsCount, currentRow, repeatedValues);
		
		int rowHeight = currentRow.get(CURRENT_ROW_HEIGHT) == null ? 0 : (Integer)currentRow.get(CURRENT_ROW_HEIGHT);
		boolean isAutofit = currentRow.get(CURRENT_ROW_AUTOFIT) == null ? Boolean.FALSE : (Boolean)currentRow.get(CURRENT_ROW_AUTOFIT);
		sheetHelper.exportRow(rowHeight, isAutofit, null);
		for (int i = 0; i < columnNames.size(); i++) 
		{
			String columnName = columnNames.get(i);
			
			JRPrintElement element = currentRow.containsKey(columnName) 
					? (JRPrintElement)currentRow.get(columnName) 
					: (JRPrintElement)repeatedValues.get(columnName);
			
			// merged cells generate accessibility warnings in Excel documents:
			
//			JRPrintElement element = (JRPrintElement)currentRow.get(columnName);
//			if(element != null)
//			{
//				int rowSpanStartIndex = getPropertiesUtil().getBooleanProperty(element, PROPERTY_REPEAT_VALUE, false) ? rowIndex : rowSpanStartIndexesMap.get(columnName);
//				sheetHelper.exportMergedCells(rowSpanStartIndex, columnNamesMap.get(columnName), maxColumnIndex, rowIndex - rowSpanStartIndex, 1);
//				rowSpanStartIndexesMap.put(columnName, rowIndex);
//			}
//			else
//			{
//				element = (JRPrintElement)repeatedValues.get(columnName);
//			}
			if (element != null) 
			{
				String autofilter = getPropertiesUtil().getProperty(element, PROPERTY_AUTO_FILTER);
				if ("Start".equals(autofilter)) 
				{
					autoFilterStart = "$" + JRStringUtil.getLetterNumeral(i + 1, true) + "$" + (rowIndex + 1);
				} 
				else if ("End".equals(autofilter)) 
				{
					autoFilterEnd = "$" + JRStringUtil.getLetterNumeral(i + 1, true) + "$" + (rowIndex + 1);
				}

				configureDefinedNames(getNature(), element);

				JRStyle parentStyle = (JRStyle)currentRow.get(columnName + PARENT_STYLE) == null
						? (JRStyle) repeatedValues.get(columnName + PARENT_STYLE)
						: (JRStyle) currentRow.get(columnName + PARENT_STYLE);
				if (element instanceof JRPrintLine) 
				{
					exportLine((JRPrintLine) element, i, parentStyle);
				} 
				else if (element instanceof JRPrintRectangle) 
				{
					exportRectangle((JRPrintRectangle) element, i, parentStyle);
				} 
				else if (element instanceof JRPrintEllipse) 
				{
					exportRectangle((JRPrintEllipse) element, i, parentStyle);
				} 
				else if (element instanceof JRPrintImage) 
				{
					exportImage((JRPrintImage) element, i, parentStyle);
				} 
				else if (element instanceof JRPrintText) 
				{
					String currentData = (String)currentRow.get(columnName + CURRENT_DATA) == null
						? (String) repeatedValues.get(columnName + CURRENT_DATA)
						: (String) currentRow.get(columnName + CURRENT_DATA);
					
					exportText((JRPrintText) element, i, parentStyle, currentData);
				} 
				else if (element instanceof JRPrintFrame) 
				{
					exportFrame((JRPrintFrame) element, i, parentStyle);
				} 
				else if (element instanceof JRGenericPrintElement) 
				{
					exportGenericElement((JRGenericPrintElement) element, i, parentStyle);
				}
			}
		}
		++rowIndex;
		currentRow.clear();
	}
		
	private void exportHeaderRow(int columnsCount, Map<String, Object> currentRow, Map<String, Object> repeatedValues) throws JRException
	{
		if (currentRow.get(CURRENT_ROW_HEIGHT) != null)
		{
			for (int i = 0; i < columnsCount; i++) 
			{
				String columnName = columnNames.get(i);
				JRPrintElement element = currentRow.get(columnName) == null
						? (JRPrintElement) repeatedValues.get(columnName)
						: (JRPrintElement) currentRow.get(columnName);
				if (element != null) 
				{
					if (rowIndex == 0) 
					{
						String width = element.getPropertiesMap().getProperty(PROPERTY_COLUMN_WIDTH_METADATA);
						width = width == null || width.isEmpty()
								? element.getPropertiesMap().getProperty(PROPERTY_COLUMN_WIDTH)
								: width;
						Integer columnWidth = width == null || width.isEmpty() 
								? element.getWidth()
								: Integer.valueOf(width);
						setColumnWidth(i, columnWidth, false); // FIXMENOW should we take care of autofit column here as well?
					}
					if (!columnHeadersRow.containsKey(columnName)) 
					{
						JRPrintText headerElement = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
						headerElement.setText(columnName);
						headerElement.setMode(ModeEnum.TRANSPARENT);
						headerElement.setWidth(element.getWidth());
						headerElement.setHeight(element.getHeight());
						headerElement.setX(element.getX());
						headerElement.setY(0);
						columnHeadersRow.put(columnName, headerElement);
					}
				}
			}
			
			if ((rowIndex == 0 && getCurrentItemConfiguration().isWriteHeader())) 
			{
				sheetHelper.exportRow((Integer) currentRow.get(CURRENT_ROW_HEIGHT), false, null);
				for (int i = 0; i < columnsCount; i++) {
					String columnName = columnNames.get(i);
					JRPrintText element = columnHeadersRow.get(columnName) == null
							? new JRBasePrintText(jasperPrint.getDefaultStyleProvider())
							: (JRPrintText) columnHeadersRow.get(columnName);
					exportText(element, i, 0, null, null);
					rowSpanStartIndexesMap.put(columnName, rowSpanStartIndexesMap.get(columnName)+1);
				}
				++rowIndex;
			}
		}
	}
	
	
	protected void exportGenericElement(final JRGenericPrintElement element, int colIndex, final JRStyle parentStyle) throws JRException
	{
		GenericElementXlsxMetadataHandler handler = (GenericElementXlsxMetadataHandler) 
		GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
			element.getGenericType(), XLSX_METADATA_EXPORTER_KEY);

		if (handler != null)
		{
			handler.exportElement(exporterContext, element, colIndex, rowIndex, parentStyle);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No XLSX generic element handler for " 
						+ element.getGenericType());
			}
		}
	}
	

	protected void exportFrame(JRPrintFrame frame, int colIndex, JRStyle parentStyle)
		{
			cellHelper.exportHeader(frame,
					rowIndex, 
					colIndex, 
					maxColumnIndex, 
					null, 
					null, 
					null, 
					true, 
					false, 
					false, 
					false, 
					false, 
					RotationEnum.NONE, 
					sheetInfo,
					null,
					parentStyle);

			cellHelper.exportFooter();
		}



	@Override
	protected void openWorkbook(OutputStream os) throws JRException 
	{
		rendererToImagePathMap = new HashMap<>();
		definedNames = new StringBuilder();
		sheetMapping = new HashMap<>();
		try
		{
			String memoryThreshold = jasperPrint.getPropertiesMap().getProperty(FileBufferedOutputStream.PROPERTY_MEMORY_THRESHOLD);
			xlsxZip = new XlsxZip(jasperReportsContext, getRepository(), 
					memoryThreshold == null ? null : JRPropertiesUtil.asInteger(memoryThreshold));

			wbHelper = new XlsxWorkbookHelper(jasperReportsContext, xlsxZip.getWorkbookEntry().getWriter(), definedNames);
			wbHelper.exportHeader();

			relsHelper = new XlsxRelsHelper(jasperReportsContext, xlsxZip.getRelsEntry().getWriter());
			ctHelper = new XlsxContentTypesHelper(jasperReportsContext, xlsxZip.getContentTypesEntry().getWriter());

			appHelper = new PropsAppHelper(jasperReportsContext, xlsxZip.getAppEntry().getWriter());
			coreHelper = new PropsCoreHelper(jasperReportsContext, xlsxZip.getCoreEntry().getWriter());
			
			XlsxMetadataExporterConfiguration configuration = getCurrentConfiguration();
			
			String macro = macroTemplate == null ? configuration.getMacroTemplate() : macroTemplate;
			if(macro != null)
			{
				xlsxZip.addMacro(macro);
				relsHelper.setContainsMacro(true);
				ctHelper.setContainsMacro(true);
			}
			relsHelper.exportHeader();
			ctHelper.exportHeader();

			appHelper.exportHeader();
			
			String application = configuration.getMetadataApplication();
			if( application == null )
			{
				@SuppressWarnings("deprecation") //this can be replaced only after abandoning Java 8 support 
				String depApplication = "JasperReports Library version " + Package.getPackage("net.sf.jasperreports.engine").getImplementationVersion();
				application = depApplication;
			}
			appHelper.exportProperty(PropsAppHelper.PROPERTY_APPLICATION, application);
			
			coreHelper.exportHeader();
			
			String title = configuration.getMetadataTitle();
			if (title != null)
			{
				coreHelper.exportProperty(PropsCoreHelper.PROPERTY_TITLE, title);
			}
			String subject = configuration.getMetadataSubject();
			if (subject != null)
			{
				coreHelper.exportProperty(PropsCoreHelper.PROPERTY_SUBJECT, subject);
			}
			String author = configuration.getMetadataAuthor();
			if (author != null)
			{
				coreHelper.exportProperty(PropsCoreHelper.PROPERTY_CREATOR, author);
			}
			String keywords = configuration.getMetadataKeywords();
			if (keywords != null)
			{
				coreHelper.exportProperty(PropsCoreHelper.PROPERTY_KEYWORDS, keywords);
			}

			styleHelper = 
				new XlsxStyleHelper(
					jasperReportsContext,
					xlsxZip.getStylesEntry().getWriter(), 
					getExporterKey()
					);
			
			sharedStringsHelper = 
				new XlsxSharedStringsHelper(
					jasperReportsContext,
					xlsxZip.getSharedStringsEntry().getWriter(), 
					getExporterKey()
					);
			sharedStringsHelper.exportHeader();
				
			firstPageNotSet = true;
			firstSheetName = null;
		}
		catch (IOException e)
		{
			throw new JRException(e);
		}
	}

	protected void setBackground() {
		// do nothing here
	}


	@Override
	protected void setColumnWidth(int col, int width, boolean autoFit) 
	{
		sheetHelper.exportColumn(col, width, autoFit);
	}


	@Override
	protected void addRowBreak(int rowIndex) 
	{
		sheetHelper.addRowBreak(rowIndex);
	}

	@Override
	public String getExporterKey()
	{
		return XLSX_EXPORTER_KEY;
	}

	@Override
	public String getExporterPropertiesPrefix()
	{
		return XLSX_EXPORTER_PROPERTIES_PREFIX;
	}
	
	@Override
	protected void setFreezePane(int rowIndex, int colIndex)
	{
		//nothing to do here
	}

	@Override
	protected void setSheetName(String sheetName)
	{
		/* nothing to do here; it's done in createSheet() */
	}

	@Override
	protected void setAutoFilter(String autoFilterRange)
	{
		sheetAutoFilter = autoFilterRange;
	}
	
	@Override
	protected void resetAutoFilters()
	{
		super.resetAutoFilters();
		sheetAutoFilter = null;
	}


	@Override
	protected void setRowLevels(XlsRowLevelInfo levelInfo, String level) 
	{
		/* nothing to do here; it's done in setRowHeight */
	}
	
	protected void setScale(Integer scale)
	{
		/* nothing to do here; it's already done in the abstract exporter */
	}
	
	protected String getAnchorType(ImageAnchorTypeEnum anchorType)
	{
		switch (anchorType)
		{
			case MOVE_SIZE: 
				return TWO_CELL;
			case NO_MOVE_NO_SIZE:
				return ABSOLUTE;
			case MOVE_NO_SIZE:
			default:
				return ONE_CELL;
		}
	}
	
	protected String getDefinedName(String name)
	{
		if (name != null)
		{
			return name.replaceAll("\\W", "");
		}
		return null;
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
		rowSpanStartIndexesMap = new HashMap<>();

		List<String> columnNamesList = JRStringUtil.split(columnNamesArray, ",");
		if (columnNamesList != null)
		{
			for (String columnName : columnNamesList)
			{
				if (!columnNamesMap.containsKey(columnName))
				{
					columnNames.add(columnName);
					columnNamesMap.put(columnName, columnNames.size());
					rowSpanStartIndexesMap.put(columnName, 0);
				}
			}
		}
	}
	

	private SheetInfo getSheetInfo(XlsxMetadataReportConfiguration configuration, String name)
	{
		SheetInfo sheetInfo = new SheetInfo();
		sheetInfo.sheetName = getSheetName(name);
		sheetInfo.sheetFirstPageIndex = pageIndex;
		sheetInfo.printSettings = new JRXlsAbstractExporter.SheetInfo().new SheetPrintSettings();	
		if(configuration == null)
		{
			sheetInfo.printSettings.setPageHeight(0);
			sheetInfo.printSettings.setPageWidth(0);
			sheetInfo.printSettings.setLeftMargin(0);
			sheetInfo.printSettings.setRightMargin(0);
			sheetInfo.printSettings.setTopMargin(0);
			sheetInfo.printSettings.setBottomMargin(0);
			sheetInfo.printSettings.setHeaderMargin(0);
			sheetInfo.printSettings.setFooterMargin(0);
			sheetInfo.rowFreezeIndex = -1;
			sheetInfo.columnFreezeIndex = -1;
		}
		else
		{
			sheetInfo.rowFreezeIndex = configuration.getFreezeRow() == null ? -1 : configuration.getFreezeRow(); 
			sheetInfo.columnFreezeIndex = configuration.getFreezeColumn() == null ? -1 : getColumnIndex(configuration.getFreezeColumn()); 
			sheetInfo.ignoreCellBackground = configuration.isIgnoreCellBackground();
			sheetInfo.ignoreCellBorder = configuration.isIgnoreCellBorder();
			sheetInfo.whitePageBackground = configuration.isWhitePageBackground();
			sheetInfo.sheetFirstPageNumber = configuration.getFirstPageNumber();		
			sheetInfo.sheetPageScale = configuration.getPageScale();		
			sheetInfo.sheetShowGridlines = configuration.isShowGridLines() ;
			sheetInfo.tabColor = configuration.getSheetTabColor();
			sheetInfo.columnWidthRatio = configuration.getColumnWidthRatio();
			updatePrintSettings(sheetInfo.printSettings, configuration);
		}
		return sheetInfo;
	}

	protected void setColumnName(String currentColumnName) {
		// when no columns are provided, build the column names list as they are retrieved from the report element property
		if (!hasDefinedColumns 
				&& currentColumnName != null 
				&& currentColumnName.length() > 0 
			&& !columnNames.contains(currentColumnName))
		{
			columnNamesMap.put( currentColumnName, columnNames.size());
			rowSpanStartIndexesMap.put( currentColumnName, 0);
			columnNames.add(currentColumnName);
		}
	}

}

