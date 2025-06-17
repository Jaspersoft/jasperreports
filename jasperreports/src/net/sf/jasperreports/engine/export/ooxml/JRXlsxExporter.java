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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.export.Cut;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
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
import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZipEntry;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import net.sf.jasperreports.engine.util.ExifOrientationEnum;
import net.sf.jasperreports.engine.util.FileBufferedOutputStream;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.ImageUtil.Insets;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.engine.util.StyledTextWriteContext;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.XlsReportConfiguration;
import net.sf.jasperreports.export.XlsxExporterConfiguration;
import net.sf.jasperreports.export.XlsxReportConfiguration;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;


/**
 * Exports a JasperReports document to XLSX format. It has character output type and exports the document to a
 * grid-based layout.
 * 
 * @see net.sf.jasperreports.engine.export.JRXlsAbstractExporter
 * @see net.sf.jasperreports.export.XlsExporterConfiguration
 * @see net.sf.jasperreports.export.XlsReportConfiguration
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRXlsxExporter extends JRXlsAbstractExporter<XlsxReportConfiguration, XlsxExporterConfiguration, JRXlsxExporterContext>
{
	private static final Log log = LogFactory.getLog(JRXlsxExporter.class);
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String XLSX_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "xlsx";

	protected static final String XLSX_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xlsx.";
	
	protected static final String ONE_CELL = "oneCell";
	
	protected static final String TWO_CELL = "twoCell";
	
	protected static final String ABSOLUTE = "absolute";

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
	protected XlsxCellHelper cellHelper;//FIXMEXLSX maybe cell helper should be part of sheet helper, just like in table helper
	protected StringBuilder definedNames;
	protected String firstSheetName;
	protected String currentSheetName;

	protected Map<String, Pair<String, ExifOrientationEnum>> rendererToImagePathMap;
//	protected Map imageMaps;
//	protected Map hyperlinksMap;

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

	
	protected class ExporterContext extends BaseExporterContext implements JRXlsxExporterContext
	{
	}

	
	/**
	 * @see #JRXlsxExporter(JasperReportsContext)
	 */
	public JRXlsxExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JRXlsxExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
		
		maxColumnIndex = 16383;
	}


	@Override
	protected Class<XlsxExporterConfiguration> getConfigurationInterface()
	{
		return XlsxExporterConfiguration.class;
	}


	@Override
	protected Class<XlsxReportConfiguration> getItemConfigurationInterface()
	{
		return XlsxReportConfiguration.class;
	}
	

	@Override
	protected void initExport()
	{
		super.initExport();
	}
	

	@Override
	protected void initReport()
	{
		super.initReport();
		
		XlsReportConfiguration configuration = getCurrentItemConfiguration();

		styleHelper.setConfiguration(configuration); 

		nature = 
			new JRXlsxExporterNature(
				jasperReportsContext, 
				filter, 
				configuration.isIgnoreGraphics(), 
				configuration.isIgnorePageMargins()
				);
	}

	@Override
	protected int exportPage(JRPrintPage page, CutsInfo xCuts, int startRow, String defaultSheetName) throws JRException
	{
		if (oldPageFormat != pageFormat)
		{
			oldPageFormat = pageFormat;
		}

		return super.exportPage(page, xCuts, startRow, defaultSheetName);
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
			return ((GenericElementXlsxHandler)GenericElementHandlerEnviroment.getInstance(jasperReportsContext).getElementHandler(
					genericPrintElement.getGenericType(), 
					XLSX_EXPORTER_KEY
					)).getImage(exporterContext, genericPrintElement);
		}
		
		return (JRPrintImage) element;
	}


	/**
	 *
	 */
	protected void exportStyledText(JRStyle style, JRStyledText styledText, Locale locale, boolean isStyledText)
	{
		StyledTextWriteContext context = new StyledTextWriteContext();

		XlsxRunHelper runHelper = new XlsxRunHelper(jasperReportsContext, getExporterKey());

		styledText.consumeText((startIndex, endIndex, attributes, runText) ->
		{
			context.next(attributes, runText);
			
			//if (context.listItemStartsWithNewLine() && !context.isListItemStart() && (context.isListItemEnd() || context.isListStart() || context.isListEnd()))
			//{
			//	runText = runText.substring(1);
			//}

			if (runText.length() > 0)
			{
				String bulletText = JRStyledTextUtil.getIndentedBulletText(context);
				
				runHelper.export(
					style, 
					attributes, 
					(bulletText == null ? "" : bulletText) + runText,
					locale,
					invalidCharReplacement,
					isStyledText
					);
			}
		});
		
		String sharedString = runHelper.getSharedString();
		int index = sharedStringsHelper.export(sharedString);
		sheetHelper.write(String.valueOf(index));
	}


	protected JRPrintElementIndex getElementIndex(JRExporterGridCell gridCell)
	{
		JRPrintElementIndex imageIndex =
			new JRPrintElementIndex(
					reportIndex,
					pageIndex,
					gridCell.getElementAddress()
					);
		return imageIndex;
	}


	/*
	 *
	 *
	protected void writeImageMap(String imageMapName, JRPrintHyperlink mainHyperlink, List imageMapAreas) throws IOException
	{
		writer.write("<map name=\"" + imageMapName + "\">\n");

		for (Iterator it = imageMapAreas.iterator(); it.hasNext();)
		{
			JRPrintImageAreaHyperlink areaHyperlink = (JRPrintImageAreaHyperlink) it.next();
			JRPrintImageArea area = areaHyperlink.getArea();

			writer.write("  <area shape=\"" + JRPrintImageArea.getHtmlShape(area.getShape()) + "\"");
			writeImageAreaCoordinates(area);
			writeImageAreaHyperlink(areaHyperlink.getHyperlink());
			writer.write("/>\n");
		}

		if (mainHyperlink.getHyperlinkTypeValue() != NONE)
		{
			writer.write("  <area shape=\"default\"");
			writeImageAreaHyperlink(mainHyperlink);
			writer.write("/>\n");
		}

		writer.write("</map>\n");
	}


	protected void writeImageAreaCoordinates(JRPrintImageArea area) throws IOException
	{
		int[] coords = area.getCoordinates();
		if (coords != null && coords.length > 0)
		{
			StringBuilder coordsEnum = new StringBuilder(coords.length * 4);
			coordsEnum.append(coords[0]);
			for (int i = 1; i < coords.length; i++)
			{
				coordsEnum.append(',');
				coordsEnum.append(coords[i]);
			}

			writer.write(" coords=\"" + coordsEnum + "\"");
		}
	}


	protected void writeImageAreaHyperlink(JRPrintHyperlink hyperlink) throws IOException
	{
		String href = getHyperlinkURL(hyperlink);
		if (href == null)
		{
			writer.write(" nohref=\"nohref\"");
		}
		else
		{
			writer.write(" href=\"" + href + "\"");

			String target = getHyperlinkTarget(hyperlink);
			if (target != null)
			{
				writer.write(" target=\"");
				writer.write(target);
				writer.write("\"");
			}
		}

		if (hyperlink.getHyperlinkTooltip() != null)
		{
			writer.write(" title=\"");
			writer.write(JRStringUtil.xmlEncode(hyperlink.getHyperlinkTooltip()));
			writer.write("\"");
		}
	}
	*/


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
					new Object[]{imageName});
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


//	protected boolean startHyperlink(JRPrintHyperlink link, boolean isText)
//	{
//		String href = getHyperlinkURL(link);
//
//		if (href != null)
//		{
//			String id = (String)hyperlinksMap.get(href);
//			if (id == null)
//			{
//				id = "rIdLnk" + hyperlinksMap.size();
//				hyperlinksMap.put(href, id);
//			}
////			
////			wbHelper.write("<w:hyperlink r:id=\"" + id + "\"");
////
////			String target = getHyperlinkTarget(link);//FIXMETARGET
////			if (target != null)
////			{
////				wbHelper.write(" tgtFrame=\"" + target + "\"");
////			}
////
////			wbHelper.write(">\n");
//
//			sheetRelsHelper.exportHyperlink(id, href);
//
////			String tooltip = link.getHyperlinkTooltip(); 
////			if (tooltip != null)
////			{
////				wbHelper.write(" \\o \"" + JRStringUtil.xmlEncode(tooltip) + "\"");
////			}
////
////			wbHelper.write(" </w:instrText></w:r>\n");
////			wbHelper.write("<w:r><w:fldChar w:fldCharType=\"separate\"/></w:r>\n");
//		}
//
//		return href != null;
//	}


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

		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(XlsReportConfiguration.PROPERTY_IGNORE_HYPERLINK, link);
		if (ignoreHyperlink == null)
		{
			ignoreHyperlink = defaultIgnoreHyperlink;
		}
		
		//test for ignore hyperlinks done here
		if (!ignoreHyperlink)
		{
			JRHyperlinkProducer customHandler = getHyperlinkProducer(link);
			if (customHandler == null)
			{
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
						if (!ignoreAnchors && link.getHyperlinkAnchor() != null)		//test for ignore anchors done here
						{
							href = link.getHyperlinkAnchor();
						}
						break;
					}
					case LOCAL_PAGE :
					{
						if (!ignoreAnchors && link.getHyperlinkPage() != null)		//test for ignore anchors done here
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
					{
//						if (
//							link.getHyperlinkReference() != null &&
//							link.getHyperlinkPage() != null
//							)
//						{
//							href = link.getHyperlinkReference() + "#" + JR_PAGE_ANCHOR_PREFIX + "0_" + link.getHyperlinkPage().toString();
//						}
						break;
					}
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


//	protected void endHyperlink(boolean isText)
//	{
////		wbHelper.write("</w:hyperlink>\n");
//		wbHelper.write("<w:r><w:fldChar w:fldCharType=\"end\"/></w:r>\n");
//	}

	protected void insertPageAnchor(int colIndex, int rowIndex)
	{
		if(!ignoreAnchors && startPage)
		{
			String anchorPage = JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (sheetIndex - sheetsBeforeCurrentReport);
			String ref = "'" + JRStringUtil.xmlEncode(currentSheetName) + "'!$A$1";		// + XlsxCellHelper.getColumIndexLetter(colIndex) + "$" + (rowIndex + 1);
			definedNames.append("<definedName name=\"" + getDefinedName(anchorPage) +"\">"+ ref +"</definedName>\n");
			startPage = false;
		}
	}
	

	@Override
	protected void addBlankCell(
		JRExporterGridCell gridCell, 
		int colIndex,
		int rowIndex
		) throws JRException 
	{
		cellHelper.exportHeader(gridCell, null, rowIndex, colIndex, maxColumnIndex, sheetInfo);
		cellHelper.exportFooter();
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

//			if ((hyperlinksMap != null && hyperlinksMap.size() > 0))
//			{
//				for(Iterator it = hyperlinksMap.keySet().iterator(); it.hasNext();)
//				{
//					String href = (String)it.next();
//					String id = (String)hyperlinksMap.get(href);
	//
//					relsHelper.exportHyperlink(id, href);
//				}
//			}

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


	@Override
	protected void createSheet(CutsInfo xCuts, SheetInfo sheetInfo)
	{
		startPage = true;
		currentSheetPageScale = sheetInfo.sheetPageScale;
		currentSheetFirstPageNumber = sheetInfo.sheetFirstPageNumber;
		currentSheetName = sheetInfo.sheetName;
		firstSheetName = firstSheetName == null ? currentSheetName : firstSheetName;
		wbHelper.exportSheet(sheetIndex + 1, currentSheetName, sheetMapping);
		ctHelper.exportSheet(sheetIndex + 1);
		relsHelper.exportSheet(sheetIndex + 1);
		XlsxReportConfiguration configuration = getCurrentItemConfiguration();
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
			XlsReportConfiguration configuration = getCurrentItemConfiguration();
			
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


	@Override
	protected void exportFrame(
		JRPrintFrame frame, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		cellHelper.exportHeader(gridCell, frame, rowIndex, colIndex, maxColumnIndex, sheetInfo);
		sheetHelper.exportMergedCells(rowIndex, colIndex, maxColumnIndex, gridCell.getRowSpan(), gridCell.getColSpan());

//		boolean appendBackcolor =
//			frame.getModeValue() == ModeEnum.OPAQUE
//			&& (backcolor == null || frame.getBackcolor().getRGB() != backcolor.getRGB());
//
//		if (appendBackcolor)
//		{
//			setBackcolor(frame.getBackcolor());
//		}
//
//		try
//		{
//			JRGridLayout layout = gridCell.getLayout();
//			JRPrintElementIndex frameIndex =
//				new JRPrintElementIndex(
//						reportIndex,
//						pageIndex,
//						gridCell.getWrapper().getAddress()
//						);
//			exportGrid(layout, frameIndex);
//		}
//		finally
//		{
//			if (appendBackcolor)
//			{
//				restoreBackcolor();
//			}
//		}
		
		cellHelper.exportFooter();
	}


	@Override
	public void exportImage(
		JRPrintImage image, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex, 
		int emptyCols,
		int yCutsRow,
		JRGridLayout layout
		) throws JRException 
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

		cellHelper.exportHeader(gridCell, image, rowIndex, colIndex, maxColumnIndex, sheetInfo);

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
					gridCell,
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
						switch (ImageUtil.getRotation(image.getRotation(), imageProcessorResult.exifOrientation))
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

						switch (ImageUtil.getRotation(image.getRotation(), imageProcessorResult.exifOrientation))
						{
							case LEFT:
								if (dimension == null)
								{
									normalWidth = availableImageHeight;
									normalHeight = availableImageWidth;
								}
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageHeight - normalWidth) / availableImageHeight;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageHeight - normalWidth) / availableImageHeight;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageWidth - normalHeight) / availableImageWidth;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageWidth - normalHeight) / availableImageWidth;
								angle = -90;
								break;
							case RIGHT:
								if (dimension == null)
								{
									normalWidth = availableImageHeight;
									normalHeight = availableImageWidth;
								}
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageHeight - normalWidth) / availableImageHeight;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageHeight - normalWidth) / availableImageHeight;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageWidth - normalHeight) / availableImageWidth;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageWidth - normalHeight) / availableImageWidth;
								angle = 90;
								break;
							case UPSIDE_DOWN:
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageWidth - normalWidth) / availableImageWidth;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageWidth - normalWidth) / availableImageWidth;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageHeight - normalHeight) / availableImageHeight;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageHeight - normalHeight) / availableImageHeight;
								angle = 180;
								break;
							case NONE:
							default:
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageWidth - normalWidth) / availableImageWidth;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageWidth - normalWidth) / availableImageWidth;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageHeight - normalHeight) / availableImageHeight;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageHeight - normalHeight) / availableImageHeight;
								angle = 0;
								break;
						}

						Insets exifCrop = ImageUtil.getExifCrop(image, imageProcessorResult.exifOrientation, cropTop, cropLeft, cropBottom, cropRight);
						cropLeft = exifCrop.left;
						cropRight = exifCrop.right;
						cropTop = exifCrop.top;
						cropBottom = exifCrop.bottom;

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

						double ratioX = 1d;
						double ratioY = 1d;

						double imageWidth = availableImageWidth;
						double imageHeight = availableImageHeight;

						switch (ImageUtil.getRotation(image.getRotation(), imageProcessorResult.exifOrientation))
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
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageWidth - imageWidth) / availableImageWidth;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageWidth - imageWidth) / availableImageWidth;
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
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageWidth - imageWidth) / availableImageWidth;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageWidth - imageWidth) / availableImageWidth;
								angle = 90;
								break;
							case UPSIDE_DOWN:
								ratioX = availableImageWidth / normalWidth;
								ratioY = availableImageHeight / normalHeight;
								ratioX = ratioX < ratioY ? ratioX : ratioY;
								ratioY = ratioX;
								imageWidth = (int)(normalWidth * ratioX);
								imageHeight = (int)(normalHeight * ratioY);
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageWidth - imageWidth) / availableImageWidth;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageWidth - imageWidth) / availableImageWidth;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageHeight - imageHeight) / availableImageHeight;
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
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageWidth - imageWidth) / availableImageWidth;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageWidth - imageWidth) / availableImageWidth;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageHeight - imageHeight) / availableImageHeight;
								angle = 0;
								break;
						}
						
						Insets exifCrop = ImageUtil.getExifCrop(image, imageProcessorResult.exifOrientation, cropTop, cropLeft, cropBottom, cropRight);
						cropLeft = exifCrop.left;
						cropRight = exifCrop.right;
						cropTop = exifCrop.top;
						cropBottom = exifCrop.bottom;
					}
				}

				if (!ignoreAnchors)
				{
					insertPageAnchor(colIndex,rowIndex);
					if (image.getAnchorName() != null)
					{
						String ref = "'" + JRStringUtil.xmlEncode(currentSheetName) + "'!$" + JRXlsAbstractExporter.getColumIndexName(colIndex, maxColumnIndex) + "$" + (rowIndex + 1);
						definedNames.append("<definedName name=\"" + getDefinedName(image.getAnchorName()) +"\">"+ ref +"</definedName>\n");
					}
				}

//				boolean startedHyperlink = startHyperlink(image,false);

				drawingRelsHelper.exportImage(imageProcessorResult.imagePath);

				sheetHelper.exportMergedCells(rowIndex, colIndex, maxColumnIndex, gridCell.getRowSpan(), gridCell.getColSpan());

				ImageAnchorTypeEnum imageAnchorType = 
					ImageAnchorTypeEnum.getByName(
						JRPropertiesUtil.getOwnProperty(image, XlsReportConfiguration.PROPERTY_IMAGE_ANCHOR_TYPE)
						);
				if (imageAnchorType == null)
				{
					imageAnchorType = defaultImageAnchorType;
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
					(colIndex + gridCell.getColSpan()) +
					"</xdr:col><xdr:colOff>" +
					LengthUtil.emu(-rightPadding) +
					"</xdr:colOff><xdr:row>" +
					(rowIndex + (collapseRowSpan ? 1 : gridCell.getRowSpan())) +
					"</xdr:row><xdr:rowOff>" +
					LengthUtil.emu(-bottomPadding) +
					"</xdr:rowOff></xdr:to>\n");
				
				drawingHelper.write("<xdr:pic>\n");
				String altText = image.getHyperlinkTooltip() == null ? "" : image.getHyperlinkTooltip();
				if(!altText.isEmpty())
				{
					altText = " descr=\"" + altText +"\"";
				}
				
				drawingHelper.write("<xdr:nvPicPr><xdr:cNvPr id=\"" + (image.hashCode() > 0 ? image.hashCode() : -image.hashCode()) + "\" name=\"Picture\"" + altText + ">\n");

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
//				if (image.getModeValue() == ModeEnum.OPAQUE && image.getBackcolor() != null)
//				{
//					drawingHelper.write("<a:solidFill><a:srgbClr val=\"" + JRColorUtil.getColorHexa(image.getBackcolor()) + "\"/></a:solidFill>\n");
//				}
				drawingHelper.write("</xdr:spPr>\n");
				drawingHelper.write("</xdr:pic>\n");
				drawingHelper.write("<xdr:clientData/>\n");
				drawingHelper.write("</xdr:twoCellAnchor>\n");

//				if(startedHyperlink)
//				{
//					endHyperlink(false);
//				}
			}
		}

//		drawingHelper.write("</w:p>");

		cellHelper.exportFooter();
	}


	private class InternalImageProcessor
	{
		private final JRPrintElement imageElement;
		private final RenderersCache imageRenderersCache;
		private final boolean needDimension; 
		private final JRExporterGridCell cell;
		private final int availableImageWidth;
		private final int availableImageHeight;

		protected InternalImageProcessor(
			JRPrintImage imageElement,
			JRExporterGridCell cell,
			int availableImageWidth,
			int availableImageHeight
			)
		{
			this.imageElement = imageElement;
			this.cell = cell;
			this.imageRenderersCache = imageElement.isUsingCache() ? renderersCache : new RenderersCache(getJasperReportsContext());
			this.needDimension = imageElement.getScaleImageValue() != ScaleImageEnum.FILL_FRAME; 
			// at this point, we do not yet have the exifOrientation, but we do not need it because the available width and height
			// are used only for non data renderers, which need to produce their data for the image and have nothing to do with exif metadata anyway
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
			
			ExifOrientationEnum exifOrientation = ExifOrientationEnum.NORMAL;
			
			String imagePath = null;

//			if (image.isLazy()) //FIXMEXLSX learn how to link images
//			{
//				
//			}
//			else
//			{
				if (
					renderer instanceof DataRenderable //we do not cache imagePath for non-data renderers because they render width different width/height each time
					&& rendererToImagePathMap.containsKey(renderer.getId())
					)
				{
					Pair<String, ExifOrientationEnum> imagePair = rendererToImagePathMap.get(renderer.getId());
					imagePath = imagePair.first();
					exifOrientation = imagePair.second();
				}
				else
				{
					JRPrintElementIndex imageIndex = getElementIndex(cell);

					DataRenderable imageRenderer = 
						getRendererUtil().getImageDataRenderable(
							imageRenderersCache,
							renderer,
							new Dimension(availableImageWidth, availableImageHeight),
							ModeEnum.OPAQUE == imageElement.getModeValue() ? imageElement.getBackcolor() : null
							);

					byte[] imageData = imageRenderer.getData(jasperReportsContext);
					exifOrientation = ImageUtil.getExifOrientation(imageData);
					String fileExtension = JRTypeSniffer.getImageTypeValue(imageData).getFileExtension();
					String imageName = IMAGE_NAME_PREFIX + imageIndex.toString() + (fileExtension == null ? "" : ("." + fileExtension));

					xlsxZip.addEntry(//FIXMEDOCX optimize with a different implementation of entry
						new FileBufferedZipEntry(
							"xl/media/" + imageName,
							imageData
							)
						);
					
//					drawingRelsHelper.exportImage(imageName);

					imagePath = imageName;
					//imagePath = "Pictures/" + imageName;
	
					if (imageRenderer == renderer)
					{
						//cache imagePath only for true ImageRenderable instances because the wrapping ones render with different width/height each time
						rendererToImagePathMap.put(renderer.getId(), new Pair<>(imagePath, exifOrientation));
					}
				}
//			}
			
			return new InternalImageProcessorResult(imagePath, dimension, exifOrientation);
		}
	}

	private class InternalImageProcessorResult
	{
		protected final String imagePath;
		protected final Dimension2D dimension;
		protected final ExifOrientationEnum exifOrientation;
		
		protected InternalImageProcessorResult(String imagePath, Dimension2D dimension, ExifOrientationEnum exifOrientation)
		{
			this.imagePath = imagePath;
			this.dimension = dimension;
			this.exifOrientation = exifOrientation;
		}
	}


	@Override
	protected void exportLine(
		JRPrintLine line, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex
		) throws JRException 
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

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box
		
		cellHelper.exportHeader(gridCell, line, rowIndex, colIndex, maxColumnIndex, sheetInfo, direction);
		sheetHelper.exportMergedCells(rowIndex, colIndex, maxColumnIndex, gridCell.getRowSpan(), gridCell.getColSpan());
		cellHelper.exportFooter();
	}


	@Override
	protected void exportRectangle(
		JRPrintGraphicElement rectangle,
		JRExporterGridCell gridCell, 
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = box.getPen();
		pen.setLineColor(rectangle.getLinePen().getLineColor());
		pen.setLineStyle(rectangle.getLinePen().getLineStyleValue());
		pen.setLineWidth(rectangle.getLinePen().getLineWidth());

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box
		
		cellHelper.exportHeader(gridCell, rectangle, rowIndex, colIndex, maxColumnIndex, sheetInfo);
		sheetHelper.exportMergedCells(rowIndex, colIndex, maxColumnIndex, gridCell.getRowSpan(), gridCell.getColSpan());
		cellHelper.exportFooter();
	}


	@Override
	public void exportText(
		final JRPrintText text, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex
		) throws JRException
	{
		final JRStyledText styledText = getStyledText(text);

//		final int textLength = styledText == null ? 0 : styledText.length();

		final String textStr = styledText.getText();

		TextValue textValue = null;
		String pattern = null;
		
		if (detectCellType)
		{
			textValue = getTextValue(text, textStr);
			if (textValue instanceof NumberTextValue)
			{
				pattern = ((NumberTextValue)textValue).getPattern();
			}
			else if (textValue instanceof DateTextValue)
			{
				pattern = ((DateTextValue)textValue).getPattern();
			}
		}
		
		//FIXME: use localized pattern symbols similar to XLS export (via DateFormatConverter class)
		final String convertedPattern = getConvertedPattern(text, pattern);
				
		cellHelper.exportHeader(
			gridCell, text, rowIndex, colIndex, maxColumnIndex, textValue, 
			convertedPattern, 
			getTextLocale(text), 
			isWrapText(text) || Boolean.TRUE.equals(((JRXlsxExporterNature)nature).getColumnAutoFit(text)), 
			isCellHidden(text), 
			isCellLocked(text),
			isShrinkToFit(text), 
			isIgnoreTextFormatting(text),
			text.getRotationValue(),
			sheetInfo
			);
		sheetHelper.exportMergedCells(rowIndex, colIndex, maxColumnIndex, gridCell.getRowSpan(), gridCell.getColSpan());

		String textFormula = getFormula(text);
		if (textFormula != null)
		{
			sheetHelper.write("<f>" + JRStringUtil.xmlEncode(textFormula) + "</f>\n");
		}

//		if (text.getLineSpacing() != JRTextElement.LINE_SPACING_SINGLE)
//		{
//			styleBuffer.append("line-height: " + text.getLineSpacingFactor() + "; ");
//		}

//		if (styleBuffer.length() > 0)
//		{
//			writer.write(" style=\"");
//			writer.write(styleBuffer.toString());
//			writer.write("\"");
//		}
//
//		writer.write(">");
		
//		tableHelper.getParagraphHelper().exportProps(text);
		if(!ignoreAnchors)
		{
			insertPageAnchor(colIndex,rowIndex);
			if (text.getAnchorName() != null)
			{
				String ref = "'" + JRStringUtil.xmlEncode(currentSheetName) + "'!$" + JRXlsAbstractExporter.getColumIndexName(colIndex, maxColumnIndex) + "$" + (rowIndex + 1);
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

		
		TextValueHandler handler = 
			new TextValueHandler() 
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
							+ (date == null ? "" : JRDataUtils.getExcelSerialDayNumber(
								date, 
								getTextLocale(text), 
								getTextTimeZone(text)
								)) 
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
					writeText();
				}
				
				private void writeText() throws JRException 
				{	
					if (textStr != null && textStr.length() > 0)
					{
						sheetHelper.write("<v>"); //FIXMENOW make writer util; check everywhere
						String markup = text.getMarkup();
						boolean isStyledText = markup != null && !JRCommonText.MARKUP_NONE.equals(markup) && !isIgnoreTextFormatting(text);
						exportStyledText(text.getStyle(), styledText, getTextLocale(text), isStyledText);
						sheetHelper.write("</v>");
					}
				}
			};
		
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


	@Override
	protected void exportGenericElement(
		JRGenericPrintElement element, 
		JRExporterGridCell gridCell, 
		int colIndex, 
		int rowIndex, 
		int emptyCols,
		int yCutsRow, 
		JRGridLayout layout
		) throws JRException
	{
		GenericElementXlsxHandler handler = (GenericElementXlsxHandler) 
			GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
				element.getGenericType(), XLSX_EXPORTER_KEY);

		if (handler != null)
		{
			handler.exportElement(exporterContext, element, gridCell, colIndex, rowIndex);
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


	@Override
	protected void openWorkbook(OutputStream os) throws JRException 
	{
		rendererToImagePathMap = new HashMap<>();
//		imageMaps = new HashMap();
//		hyperlinksMap = new HashMap();
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
			
			XlsxExporterConfiguration configuration = getCurrentConfiguration();
			
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
		// TODO Auto-generated method stub
		
	}


//	protected void setCell(JRExporterGridCell gridCell, int colIndex, int rowIndex) 
//	{
//	}


	@Override
	protected void addOccupiedCell(OccupiedGridCell occupiedGridCell, int colIndex, int rowIndex) 
	{
		//ElementGridCell elementGridCell = (ElementGridCell)occupiedGridCell.getOccupier();
		cellHelper.exportHeader(occupiedGridCell, null, rowIndex, colIndex, maxColumnIndex, sheetInfo);
		cellHelper.exportFooter();
	}


	@Override
	protected void setColumnWidth(int col, int width, boolean autoFit) 
	{
		sheetHelper.exportColumn(col, width, autoFit);
	}


	@Override
	protected void setRowHeight(
		int rowIndex, 
		int rowHeight,
		Cut yCut,
		XlsRowLevelInfo levelInfo
		) throws JRException 
	{
		sheetHelper.exportRow(rowHeight, yCut, levelInfo);
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
		return sheetHelper.getDefinedName(name);
	}
	
}

