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
 * Special thanks to Google 'Summer of Code 2005' program for supporting this development
 *
 * Contributors:
 * Majid Ali Khan - majidkk@users.sourceforge.net
 * Frank Schönheit - Frank.Schoenheit@Sun.COM
 */
package net.sf.jasperreports.engine.export.oasis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.export.Cut;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.ElementGridCell;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.OccupiedGridCell;
import net.sf.jasperreports.engine.export.XlsRowLevelInfo;
import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZipEntry;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.ExifOrientationEnum;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.ImageUtil.Insets;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.export.OdsExporterConfiguration;
import net.sf.jasperreports.export.OdsReportConfiguration;
import net.sf.jasperreports.export.XlsReportConfiguration;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;


/**
 * Exports a JasperReports document to Open Document Spreadsheet format. It has character output type
 * and exports the document to a grid-based layout.
 * <p/>
 * The {@link net.sf.jasperreports.engine.export.oasis.JROdsExporter} exporter
 * implementation produces documents that comply with the Open Document Format for
 * Office Applications specifications for spreadsheets. These documents use the 
 * <code>.ods</code> file extension.
 * <p/>
 * Because spreadsheet documents are made of sheets containing cells, this exporter is a
 * grid exporter, as well, therefore having the known limitations of grid exporters. 
 * <p/>
 * Special exporter configuration settings, that can be applied to a 
 * {@link net.sf.jasperreports.engine.export.oasis.JROdsExporter} instance
 * to control its behavior, can be found in {@link net.sf.jasperreports.export.OdsReportConfiguration} 
 * and in its {@link net.sf.jasperreports.export.XlsReportConfiguration} superclass.
 * 
 * @see net.sf.jasperreports.export.OdsReportConfiguration
 * @see net.sf.jasperreports.export.XlsReportConfiguration
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JROdsExporter extends JRXlsAbstractExporter<OdsReportConfiguration, OdsExporterConfiguration, JROdsExporterContext>
{
	/**
	 *
	 */
	protected static final String JR_PAGE_ANCHOR_PREFIX = "JR_PAGE_ANCHOR_";
	protected static final String DEFAULT_COLUMN = "A";
	protected static final String DEFAULT_ADDRESS = "$A$1";

	/**
	 * 
	 */
	protected OasisZip oasisZip;
	protected ExportZipEntry tempBodyEntry;
	protected ExportZipEntry tempStyleEntry;
	protected WriterHelper tempBodyWriter;
	protected WriterHelper tempStyleWriter;
	protected WriterHelper stylesWriter;

	protected StyleCache styleCache;

	protected int reportDpi;

	protected DocumentBuilder documentBuilder;
	protected TableBuilder tableBuilder;
	protected StyleBuilder styleBuilder;

	protected boolean startPage;
	protected PrintPageFormat oldPageFormat;
	protected int pageFormatIndex;
	
	protected StringBuilder namedExpressions;

	protected Map<Integer, String> rowStyles = new HashMap<>();
	protected Map<Integer, String> columnStyles = new HashMap<>();

	@Override
	protected void openWorkbook(OutputStream os) throws JRException, IOException
	{
		reportDpi = jasperPrint.getDpi();

		oasisZip = new OdsZip();

		tempBodyEntry = new FileBufferedZipEntry(null);
		tempStyleEntry = new FileBufferedZipEntry(null);

		tempBodyWriter = new WriterHelper(jasperReportsContext, tempBodyEntry.getWriter());
		tempStyleWriter = new WriterHelper(jasperReportsContext, tempStyleEntry.getWriter());

		rowStyles.clear();
		columnStyles.clear();
		documentBuilder = new OdsDocumentBuilder(oasisZip);
		
		styleCache = new StyleCache(jasperReportsContext, tempStyleWriter, getExporterKey(), reportDpi);

		stylesWriter = new WriterHelper(jasperReportsContext, oasisZip.getStylesEntry().getWriter());

		styleBuilder = new StyleBuilder(stylesWriter, reportDpi);
		styleBuilder.buildBeforeAutomaticStyles(jasperPrint);

		namedExpressions = new StringBuilder("<table:named-expressions>\n");
		
		pageFormatIndex = -1;
		
		maxColumnIndex = 1023;
	}

	@Override
	protected int exportPage(JRPrintPage page, CutsInfo xCuts, int startRow, String defaultSheetName) throws JRException
	{
		if (oldPageFormat != pageFormat)
		{
			styleBuilder.buildPageLayout(++pageFormatIndex, pageFormat);
			oldPageFormat = pageFormat;
		}

		return super.exportPage(page, xCuts, startRow, defaultSheetName);
	}
	
	@Override
	protected void createSheet(CutsInfo xCuts, SheetInfo sheetInfo)
	{
		startPage = true;
		
//		CutsInfo xCuts = gridLayout.getXCuts();
//		JRExporterGridCell[][] grid = gridLayout.getGrid();

//		TableBuilder tableBuilder = frameIndex == null
//			? new TableBuilder(reportIndex, pageIndex, tempBodyWriter, tempStyleWriter)
//			: new TableBuilder(frameIndex.toString(), tempBodyWriter, tempStyleWriter);
		tableBuilder = new OdsTableBuilder(
							documentBuilder, 
							jasperPrint, 
							pageFormatIndex, 
							pageIndex, 
							tempBodyWriter, 
							tempStyleWriter, 
							styleCache, 
							rowStyles, 
							columnStyles, 
							sheetInfo.sheetName,
							sheetInfo.tabColor);

//		tableBuilder.buildTableStyle(gridLayout.getWidth());
		tableBuilder.buildTableStyle(xCuts.getLastCutOffset());//FIXMEODS
		tableBuilder.buildTableHeader();

//		for (int col = 1; col < xCuts.size(); col++)
//		{
//			tableBuilder.buildColumnStyle(
//					col - 1,
//					xCuts.getCutOffset(col) - xCuts.getCutOffset(col - 1)
//					);
//			tableBuilder.buildColumnHeader(col - 1);
//			tableBuilder.buildColumnFooter();
//		}
	}


	@Override
	protected void closeSheet()
	{
		if (tableBuilder != null)
		{
			tableBuilder.buildRowFooter();
			tableBuilder.buildTableFooter();
		}
	}

	@Override
	protected void closeWorkbook(OutputStream os) throws JRException, IOException
	{
		styleBuilder.buildMasterPages(pageFormatIndex);
		
		stylesWriter.flush();
		tempBodyWriter.flush();
		tempStyleWriter.flush();


		stylesWriter.close();
		tempBodyWriter.close();
		tempStyleWriter.close();
		namedExpressions.append("</table:named-expressions>\n");

		/*   */
		ContentBuilder contentBuilder =
			new ContentBuilder(
				oasisZip.getContentEntry(),
				tempStyleEntry,
				tempBodyEntry,
				styleCache.getFontFaces(),
				OasisZip.MIME_TYPE_ODS,
				namedExpressions
				);
		contentBuilder.build();

		tempStyleEntry.dispose();
		tempBodyEntry.dispose();

		oasisZip.zipEntries(os);

		oasisZip.dispose();
	}

	@Override
	protected void setColumnWidth(int col, int width, boolean autoFit)
	{
		tableBuilder.buildColumnStyle(col - 1, width);
		tableBuilder.buildColumnHeader(width);
		tableBuilder.buildColumnFooter();
	}

	@Override
	protected void setRowHeight(
		int rowIndex, 
		int lastRowHeight, 
		Cut yCut,
		XlsRowLevelInfo levelInfo
		) throws JRException 
	{
		boolean isFlexibleRowHeight = getCurrentItemConfiguration().isFlexibleRowHeight();
		tableBuilder.buildRowStyle(rowIndex, isFlexibleRowHeight ? -1 : lastRowHeight);
		tableBuilder.buildRow(rowIndex, isFlexibleRowHeight ? -1 : lastRowHeight);
	}

	@Override
	protected void addRowBreak(int rowIndex)
	{
		//FIXMEODS sheet.setRowBreak(rowIndex);
	}

//	@Override
//	protected void setCell(
//		JRExporterGridCell gridCell, 
//		int colIndex,
//		int rowIndex) 
//	{
//		//nothing to do
//	}

	@Override
	protected void addBlankCell(
		JRExporterGridCell gridCell, 
		int colIndex,
		int rowIndex
		) 
	{
		tempBodyWriter.write("<table:table-cell");
		//tempBodyWriter.write(" office:value-type=\"string\"");
		if (gridCell == null)
		{
			tempBodyWriter.write(" table:style-name=\"empty-cell\"");
		}
		else
		{
			tempBodyWriter.write(" table:style-name=\"" + styleCache.getCellStyle(gridCell) + "\"");
		}
//		if (emptyCellColSpan > 1)
//		{
//			tempBodyWriter.write(" table:number-columns-spanned=\"" + emptyCellColSpan + "\"");
//		}
		tempBodyWriter.write("/>\n");
//
//		exportOccupiedCells(emptyCellColSpan - 1);
	}

	@Override
	protected void addOccupiedCell(
		OccupiedGridCell occupiedGridCell,
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		ElementGridCell elementGridCell = (ElementGridCell)occupiedGridCell.getOccupier();
		addBlankCell(elementGridCell, colIndex, rowIndex);
	}

	@Override
	public void exportText(
		JRPrintText text, 
		JRExporterGridCell gridCell,
		int colIndex, 
		int rowIndex
		) throws JRException
	{
		tableBuilder.exportText(text, gridCell, isShrinkToFit(text), isWrapText(text), isIgnoreTextFormatting(text));
		if (!ignoreAnchors && text.getAnchorName() != null)
		{
			String cellAddress = "$&apos;" + tableBuilder.getTableName() + "&apos;." + getCellAddress(rowIndex, colIndex);
			int lastCol = Math.max(0, colIndex + gridCell.getColSpan() -1);
			String cellRangeAddress = getCellAddress(rowIndex + gridCell.getRowSpan() - 1, lastCol);
			namedExpressions.append("<table:named-range table:name=\""+ JRStringUtil.xmlEncode(text.getAnchorName()) +"\" table:base-cell-address=\"" + cellAddress +"\" table:cell-range-address=\"" + cellAddress +":" +cellRangeAddress +"\"/>\n");
		}
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
			Math.max(image.getLineBox().getTopPadding(), image.getLineBox().getTopPen().getLineWidth() == null ? 0 : Math.round(image.getLineBox().getTopPen().getLineWidth()));
		int leftPadding = 
			Math.max(image.getLineBox().getLeftPadding(), image.getLineBox().getLeftPen().getLineWidth() == null ? 0 : Math.round(image.getLineBox().getLeftPen().getLineWidth()));
		int bottomPadding = 
			Math.max(image.getLineBox().getBottomPadding(), image.getLineBox().getBottomPen().getLineWidth() == null ? 0 : Math.round(image.getLineBox().getBottomPen().getLineWidth()));
		int rightPadding = 
			Math.max(image.getLineBox().getRightPadding(), image.getLineBox().getRightPen().getLineWidth() == null ? 0 : Math.round(image.getLineBox().getRightPen().getLineWidth()));

		int availableImageWidth = image.getWidth() - leftPadding - rightPadding;
		availableImageWidth = availableImageWidth < 0 ? 0 : availableImageWidth;

		int availableImageHeight = image.getHeight() - topPadding - bottomPadding;
		availableImageHeight = availableImageHeight < 0 ? 0 : availableImageHeight;

		tableBuilder.buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());

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
					availableImageHeight//,
//					documentBuilder,
//					jasperReportsContext
					);
				
			InternalImageProcessorResult imageProcessorResult = null;
			
			try
			{
				imageProcessorResult = imageProcessor.process(renderer);
			}
			catch (Exception e)
			{
				Renderable onErrorRenderer = getRendererUtil().handleImageError(e, image.getOnErrorType());
				if (onErrorRenderer != null)
				{
					imageProcessorResult = imageProcessor.process(onErrorRenderer);
				}
			}
			
			if (imageProcessorResult != null)
			{
//					tempBodyWriter.write("<text:p>");
				documentBuilder.insertPageAnchor(tableBuilder);
				if (!ignoreAnchors && image.getAnchorName() != null)
				{
					tableBuilder.exportAnchor(JRStringUtil.xmlEncode(image.getAnchorName()));
					String cellAddress = "$&apos;" + tableBuilder.getTableName() + "&apos;." + getCellAddress(rowIndex, colIndex);
					int lastCol = Math.max(0, colIndex + gridCell.getColSpan() - 1);
					String cellRangeAddress = getCellAddress(rowIndex + gridCell.getRowSpan() - 1, lastCol);
					namedExpressions.append("<table:named-range table:name=\""+ image.getAnchorName() +"\" table:base-cell-address=\"" + cellAddress +"\" table:cell-range-address=\"" + cellAddress +":" + cellRangeAddress +"\"/>\n");
				}

				boolean startedHyperlink = tableBuilder.startHyperlink(image,false, onePagePerSheet);

				//String cellAddress = getCellAddress(rowIndex + gridCell.getRowSpan(), colIndex + gridCell.getColSpan() - 1);
				String cellAddress = getCellAddress(rowIndex + gridCell.getRowSpan(), colIndex + gridCell.getColSpan());
				cellAddress = cellAddress == null ? "" : "table:end-cell-address=\"" + cellAddress + "\" ";
				
//				tempBodyWriter.write(
//					"<draw:frame text:anchor-type=\"frame\" "
//					+ "draw:style-name=\"" + styleCache.getGraphicStyle(image) + "\" "
//					+ cellAddress
////					+ "table:end-x=\"" + LengthUtil.inchRound(image.getWidth()) + "in\" "
////					+ "table:end-y=\"" + LengthUtil.inchRound(image.getHeight()) + "in\" "
//					+ "table:end-x=\"0in\" "
//					+ "table:end-y=\"0in\" "
////					+ "svg:x=\"" + LengthUtil.inch(image.getX() + leftPadding + imageProcessorResult.xoffset) + "in\" "
////					+ "svg:y=\"" + LengthUtil.inch(image.getY() + topPadding + imageProcessorResult.yoffset) + "in\" "
//					+ "svg:x=\"0in\" "
//					+ "svg:y=\"0in\" "
//					+ "svg:width=\"" + LengthUtil.inchRound(image.getWidth()) + "in\" "
//					+ "svg:height=\"" + LengthUtil.inchRound(image.getHeight()) + "in\"" 
//					+ ">"
//					);
				
				
				tempBodyWriter.write(
					"<draw:frame text:anchor-type=\"paragraph\" "
					+ "draw:style-name=\"" + styleCache.getGraphicStyle(
							image, 
							imageProcessorResult.cropTop, 
							imageProcessorResult.cropLeft,
							imageProcessorResult.cropBottom,
							imageProcessorResult.cropRight
							) + "\" "
					// x and y offset of the svg do not seem to have any effect and it works the same regardless of their value; 
					// probably because the image is anchored to the paragraph
					+ "svg:x=\"0in\" "
					+ "svg:y=\"0in\" "
//					+ "svg:x=\"" + LengthUtil.inchFloor4Dec(leftPadding + imageProcessorResult.xoffset, reportDpi) + "in\" "
//					+ "svg:y=\"" + LengthUtil.inchFloor4Dec(topPadding + imageProcessorResult.yoffset, reportDpi) + "in\" "
					+ "svg:width=\"" + LengthUtil.inchFloor4Dec(imageProcessorResult.width, reportDpi) + "in\" "
					+ "svg:height=\"" + LengthUtil.inchFloor4Dec(imageProcessorResult.height, reportDpi) + "in\" "
					+ "draw:transform=\"rotate (" + imageProcessorResult.angle + ") "
					+ "translate (" + LengthUtil.inchFloor4Dec(leftPadding + imageProcessorResult.xoffset, reportDpi)
					+ "in," + LengthUtil.inchFloor4Dec(topPadding + imageProcessorResult.yoffset, reportDpi) + "in)\">"
					);				
				tempBodyWriter.write("<draw:image ");
				tempBodyWriter.write(" xlink:href=\"" + JRStringUtil.xmlEncode(imageProcessorResult.imagePath) + "\"");
				tempBodyWriter.write(" xlink:type=\"simple\"");
				tempBodyWriter.write(" xlink:show=\"embed\"");
				tempBodyWriter.write(" xlink:actuate=\"onLoad\"");
				tempBodyWriter.write("/>\n");

				tempBodyWriter.write("</draw:frame>");
				if (startedHyperlink)
				{
					tableBuilder.endHyperlink(false);
				}
//					tempBodyWriter.write("</text:p>");
			}
		}

		tableBuilder.buildCellFooter();
	}
	
	private class InternalImageProcessor 
	{
		private final JRPrintImage imageElement;
		private final RenderersCache imageRenderersCache;
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
			this.imageRenderersCache = imageElement.isUsingCache() ? documentBuilder.getRenderersCache() 
					: new RenderersCache(getJasperReportsContext());
			this.cell = cell;
			this.availableImageWidth = availableImageWidth;
			this.availableImageHeight = availableImageHeight;
		}
		
		private InternalImageProcessorResult process(Renderable renderer) throws JRException
		{
//			boolean isLazy = RendererUtil.isLazy(renderer);
//
//			if (!isLazy)
//			{
				if (renderer instanceof ResourceRenderer)
				{
					renderer = imageRenderersCache.getLoadedRenderer((ResourceRenderer)renderer);
				}
//			}

			// check dimension first, to avoid caching renderers that might not be used eventually, due to their dimension errors 

			ExifOrientationEnum exifOrientation = ExifOrientationEnum.NORMAL;
			if (renderer instanceof DataRenderable)
			{
				byte[] imageData = ((DataRenderable)renderer).getData(jasperReportsContext);
				exifOrientation = ImageUtil.getExifOrientation(imageData);
			}

			int imageWidth = availableImageWidth;
			int imageHeight = availableImageHeight;

			int xoffset = 0;
			int yoffset = 0;
			
			double cropTop = 0;
			double cropLeft = 0;
			double cropBottom = 0;
			double cropRight = 0;
			
			double angle = 0;

			switch (imageElement.getScaleImage())
			{
				case FILL_FRAME :
				{
					switch (ImageUtil.getRotation(imageElement.getRotation(), exifOrientation))
					{
						case LEFT:
							imageWidth = availableImageHeight;
							imageHeight = availableImageWidth;
							xoffset = 0;
							yoffset = availableImageHeight;
							angle = Math.PI / 2;
							break;
						case RIGHT:
							imageWidth = availableImageHeight;
							imageHeight = availableImageWidth;
							xoffset = availableImageWidth;
							yoffset = 0;
							angle = - Math.PI / 2;
							break;
						case UPSIDE_DOWN:
							imageWidth = availableImageWidth;
							imageHeight = availableImageHeight;
							xoffset = availableImageWidth;
							yoffset = availableImageHeight;
							angle = Math.PI;
							break;
						case NONE:
						default:
							imageWidth = availableImageWidth;
							imageHeight = availableImageHeight;
							xoffset = 0;
							yoffset = 0;
							angle = 0;
							break;
					}
					
					break;
				}
				case CLIP :
				{
					double normalWidth = availableImageWidth;
					double normalHeight = availableImageHeight;

					DimensionRenderable dimensionRenderer = imageRenderersCache.getDimensionRenderable(renderer);
					Dimension2D dimension = dimensionRenderer == null ? null :  dimensionRenderer.getDimension(getJasperReportsContext());
					if (dimension != null)
					{
						normalWidth = dimension.getWidth();
						normalHeight = dimension.getHeight();
					}

					switch (ImageUtil.getRotation(imageElement.getRotation(), exifOrientation))
					{
						case LEFT:
							if (dimension == null)
							{
								normalWidth = availableImageHeight;
								normalHeight = availableImageWidth;
							}
							imageWidth = availableImageHeight;
							imageHeight = availableImageWidth;
							switch (imageElement.getHorizontalImageAlign())
							{
								case RIGHT :
									cropLeft = normalWidth - availableImageHeight;
									cropRight = 0;
									yoffset = imageWidth;
									break;
								case CENTER :
									cropLeft = (normalWidth - availableImageHeight) / 2;
									cropRight = cropLeft;
									yoffset = availableImageHeight - (availableImageHeight - imageWidth) / 2;
									break;
								case LEFT :
								default :
									cropLeft = 0;
									cropRight = normalWidth - availableImageHeight;
									yoffset = availableImageHeight;
									break;
							}
							switch (imageElement.getVerticalImageAlign())
							{
								case TOP :
									cropTop = 0;
									cropBottom = normalHeight - availableImageWidth;
									xoffset = 0;
									break;
								case MIDDLE :
									cropTop = (normalHeight - availableImageWidth) / 2;
									cropBottom = cropTop;
									xoffset = (availableImageWidth - imageHeight) / 2;
									break;
								case BOTTOM :
								default :
									cropTop = normalHeight - availableImageWidth;
									cropBottom = 0;
									xoffset = availableImageWidth - imageHeight;
									break;
							}
							angle = Math.PI / 2;
							break;
						case RIGHT:
							if (dimension == null)
							{
								normalWidth = availableImageHeight;
								normalHeight = availableImageWidth;
							}
							imageWidth = availableImageHeight;
							imageHeight = availableImageWidth;
							switch (imageElement.getHorizontalImageAlign())
							{
								case RIGHT :
									cropLeft = normalWidth - availableImageHeight;
									cropRight = 0;
									yoffset = availableImageHeight - imageWidth;
									break;
								case CENTER :
									cropLeft = (normalWidth - availableImageHeight) / 2;
									cropRight = cropLeft;
									yoffset = (availableImageHeight - imageWidth) / 2;
									break;
								case LEFT :
								default :
									cropLeft = 0;
									cropRight = normalWidth - availableImageHeight;
									yoffset = 0;
									break;
							}
							switch (imageElement.getVerticalImageAlign())
							{
								case TOP :
									cropTop = 0;
									cropBottom = normalHeight - availableImageWidth;
									xoffset = availableImageWidth;
									break;
								case MIDDLE :
									cropTop = (normalHeight - availableImageWidth) / 2;
									cropBottom = cropTop;
									xoffset = availableImageWidth - (availableImageWidth - imageHeight) / 2;
									break;
								case BOTTOM :
								default :
									cropTop = normalHeight - availableImageWidth;
									cropBottom = 0;
									xoffset = imageHeight;
									break;
							}
							angle = - Math.PI / 2;
							break;
						case UPSIDE_DOWN:
							imageWidth = availableImageWidth;
							imageHeight = availableImageHeight;
							switch (imageElement.getHorizontalImageAlign())
							{
								case RIGHT :
									cropLeft = normalWidth - availableImageWidth;
									cropRight = 0;
									xoffset = imageWidth;
									break;
								case CENTER :
									cropLeft = (normalWidth - availableImageWidth) / 2;
									cropRight = cropLeft;
									xoffset = availableImageWidth - (availableImageWidth - imageWidth) / 2;
									break;
								case LEFT :
								default :
									cropLeft = 0;
									cropRight = normalWidth - availableImageWidth;
									xoffset = availableImageWidth;
									break;
							}
							switch (imageElement.getVerticalImageAlign())
							{
								case TOP :
									cropTop = 0;
									cropBottom = normalHeight - availableImageHeight;
									yoffset = availableImageHeight;
									break;
								case MIDDLE :
									cropTop = (normalHeight - availableImageHeight) / 2;
									cropBottom = cropTop;
									yoffset = availableImageHeight - (availableImageHeight - imageHeight) / 2;
									break;
								case BOTTOM :
								default :
									cropTop = normalHeight - availableImageHeight;
									cropBottom = 0;
									yoffset = imageHeight;
									break;
							}
							angle = Math.PI;
							break;
						case NONE:
						default:
							imageWidth = availableImageWidth;
							imageHeight = availableImageHeight;
							switch (imageElement.getHorizontalImageAlign())
							{
								case RIGHT :
									cropLeft = normalWidth - availableImageWidth;
									cropRight = 0;
									xoffset = availableImageWidth - imageWidth;
									break;
								case CENTER :
									cropLeft = (normalWidth - availableImageWidth) / 2;
									cropRight = cropLeft;
									xoffset = (availableImageWidth - imageWidth) / 2;
									break;
								case LEFT :
								default :
									cropLeft = 0;
									cropRight = normalWidth - availableImageWidth;
									xoffset = 0;
									break;
							}
							switch (imageElement.getVerticalImageAlign())
							{
								case TOP :
									cropTop = 0;
									cropBottom = normalHeight - availableImageHeight;
									yoffset = 0;
									break;
								case MIDDLE :
									cropTop = (normalHeight - availableImageHeight) / 2;
									cropBottom = cropTop;
									yoffset = (availableImageHeight - imageHeight) / 2;
									break;
								case BOTTOM :
								default :
									cropTop = normalHeight - availableImageHeight;
									cropBottom = 0;
									yoffset = availableImageHeight - imageHeight;
									break;
							}
							angle = 0;
							break;
					}
									
					Insets exifCrop = ImageUtil.getExifCrop(imageElement, exifOrientation, cropTop, cropLeft, cropBottom, cropRight);
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

					Dimension2D dimension = null;
//					if (!isLazy)
//					{
						DimensionRenderable dimensionRenderer = imageRenderersCache.getDimensionRenderable(renderer);
						dimension = dimensionRenderer == null ? null :  dimensionRenderer.getDimension(getJasperReportsContext());
						if (dimension != null)
						{
							normalWidth = dimension.getWidth();
							normalHeight = dimension.getHeight();
						}
//					}

					double ratioX = 1f;
					double ratioY = 1f;

					switch (ImageUtil.getRotation(imageElement.getRotation(), exifOrientation))
					{
						case LEFT:
							if (dimension == null)
							{
								normalWidth = availableImageHeight;
								normalHeight = availableImageWidth;
							}
							imageWidth = availableImageHeight;
							imageHeight = availableImageWidth;
							ratioX = availableImageWidth / normalHeight;
							ratioY = availableImageHeight / normalWidth;
							ratioX = ratioX < ratioY ? ratioX : ratioY;
							ratioY = ratioX;
							xoffset = 0;
							yoffset = availableImageHeight;
							cropLeft = (int)(ImageUtil.getXAlignFactor(imageElement) * (normalWidth - availableImageHeight / ratioX));
							cropRight = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (normalWidth - availableImageHeight / ratioX));
							cropTop = (int)(ImageUtil.getYAlignFactor(imageElement) * (normalHeight - availableImageWidth / ratioY));
							cropBottom = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (normalHeight - availableImageWidth / ratioY));
							angle = Math.PI / 2;
							break;
						case RIGHT:
							if (dimension == null)
							{
								normalWidth = availableImageHeight;
								normalHeight = availableImageWidth;
							}
							imageWidth = availableImageHeight;
							imageHeight = availableImageWidth;
							ratioX = availableImageWidth / normalHeight;
							ratioY = availableImageHeight / normalWidth;
							ratioX = ratioX < ratioY ? ratioX : ratioY;
							ratioY = ratioX;
							xoffset = availableImageWidth;
							yoffset = 0;
							cropLeft = (int)(ImageUtil.getXAlignFactor(imageElement) * (normalWidth - availableImageHeight / ratioX));
							cropRight = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (normalWidth - availableImageHeight / ratioX));
							cropTop = (int)(ImageUtil.getYAlignFactor(imageElement) * (normalHeight - availableImageWidth / ratioY));
							cropBottom = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (normalHeight - availableImageWidth / ratioY));
							angle = - Math.PI / 2;
							break;
						case UPSIDE_DOWN:
							imageWidth = availableImageWidth;
							imageHeight = availableImageHeight;
							ratioX = availableImageWidth / normalWidth;
							ratioY = availableImageHeight / normalHeight;
							ratioX = ratioX < ratioY ? ratioX : ratioY;
							ratioY = ratioX;
							xoffset = availableImageWidth;
							yoffset = availableImageHeight;
							cropLeft = (int)(ImageUtil.getXAlignFactor(imageElement) * (normalWidth - availableImageWidth / ratioX));
							cropRight = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (normalWidth - availableImageWidth / ratioX));
							cropTop = (int)(ImageUtil.getYAlignFactor(imageElement) * (normalHeight - availableImageHeight / ratioY));
							cropBottom = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (normalHeight - availableImageHeight / ratioY));
							angle = Math.PI;
							break;
						case NONE:
						default:
							imageWidth = availableImageWidth;
							imageHeight = availableImageHeight;
							ratioX = availableImageWidth / normalWidth;
							ratioY = availableImageHeight / normalHeight;
							ratioX = ratioX < ratioY ? ratioX : ratioY;
							ratioY = ratioX;
							xoffset = 0;
							yoffset = 0;
							cropLeft = (int)(ImageUtil.getXAlignFactor(imageElement) * (normalWidth - availableImageWidth / ratioX));
							cropRight = (int)((1f - ImageUtil.getXAlignFactor(imageElement)) * (normalWidth - availableImageWidth / ratioX));
							cropTop = (int)(ImageUtil.getYAlignFactor(imageElement) * (normalHeight - availableImageHeight / ratioY));
							cropBottom = (int)((1f - ImageUtil.getYAlignFactor(imageElement)) * (normalHeight - availableImageHeight / ratioY));
							angle = 0;
							break;
					}
					
					Insets exifCrop = ImageUtil.getExifCrop(imageElement, exifOrientation, cropTop, cropLeft, cropBottom, cropRight);
					cropLeft = exifCrop.left;
					cropRight = exifCrop.right;
					cropTop = exifCrop.top;
					cropBottom = exifCrop.bottom;
				}
			}


			String imagePath = 
				documentBuilder.getImagePath(
					renderer, 
					new Dimension(imageWidth, imageHeight),
					ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null,
					cell,
//					isLazy,
					imageRenderersCache
					);

			return 
				new InternalImageProcessorResult(
					imagePath, 
					imageWidth,
					imageHeight,
					xoffset,
					yoffset,
					cropTop,
					cropLeft,
					cropBottom,
					cropRight,
					angle
					);
		}
	}

	private class InternalImageProcessorResult 
	{
		protected final String imagePath;
		protected final int width;
		protected final int height;
		protected final int xoffset;
		protected final int yoffset;
		protected final double cropTop;
		protected final double cropLeft;
		protected final double cropBottom;
		protected final double cropRight;
		protected final double angle;

		protected InternalImageProcessorResult(
			String imagePath, 
			int width,
			int height,
			int xoffset,
			int yoffset,
			double cropTop,
			double cropLeft,
			double cropBottom,
			double cropRight,
			double angle
			
			)
		{
			this.imagePath = imagePath;
			this.width = width;
			this.height = height;
			this.xoffset = xoffset;
			this.yoffset = yoffset;
			this.cropTop = cropTop;
			this.cropLeft = cropLeft;
			this.cropBottom = cropBottom;
			this.cropRight = cropRight;
			this.angle = angle;
		}
	}

	protected String getCellAddress(int row, int col)
	{
		String address = null;
		if (row > -1 && row < 1048577 && col > -1 && col < maxColumnIndex)
		{
			address = "$" + getColumIndexName(col, maxColumnIndex) + "$" + (row + 1);
		}
		return address == null ? DEFAULT_ADDRESS : address;
	}
	
	@Override
	protected void exportRectangle(
		JRPrintGraphicElement rectangle,
		JRExporterGridCell gridCell, 
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		tableBuilder.exportRectangle(rectangle, gridCell);
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
		float ratio = line.getWidth() / line.getHeight();
		if (ratio > 1)
		{
			if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)
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
			if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)
			{
				pen = box.getLeftPen();
			}
			else
			{
				pen = box.getRightPen();
			}
		}
		pen.setLineColor(line.getLinePen().getLineColor());
		pen.setLineStyle(line.getLinePen().getLineStyle());
		pen.setLineWidth(line.getLinePen().getLineWidth());

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box

		tableBuilder.buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());

//		double x1, y1, x2, y2;
//
//		if (line.getDirection() == JRLine.DIRECTION_TOP_DOWN)
//		{
//			x1 = Utility.translatePixelsToInches(0);
//			y1 = Utility.translatePixelsToInches(0);
//			x2 = Utility.translatePixelsToInches(line.getWidth() - 1);
//			y2 = Utility.translatePixelsToInches(line.getHeight() - 1);
//		}
//		else
//		{
//			x1 = Utility.translatePixelsToInches(0);
//			y1 = Utility.translatePixelsToInches(line.getHeight() - 1);
//			x2 = Utility.translatePixelsToInches(line.getWidth() - 1);
//			y2 = Utility.translatePixelsToInches(0);
//		}

		tempBodyWriter.write("<text:p>");
//FIXMEODS		insertPageAnchor();
//		tempBodyWriter.write(
//				"<draw:line text:anchor-type=\"paragraph\" "
//				+ "draw:style-name=\"" + styleCache.getGraphicStyle(line) + "\" "
//				+ "svg:x1=\"" + x1 + "in\" "
//				+ "svg:y1=\"" + y1 + "in\" "
//				+ "svg:x2=\"" + x2 + "in\" "
//				+ "svg:y2=\"" + y2 + "in\">"
//				//+ "</draw:line>"
//				+ "<text:p/></draw:line>"
//				);
		tempBodyWriter.write("</text:p>");
		tableBuilder.buildCellFooter();
	}

	@Override
	protected void exportFrame(
		JRPrintFrame frame, 
		JRExporterGridCell cell,
		int colIndex, 
		int rowIndex
		) throws JRException 
	{
		addBlankCell(cell, colIndex, rowIndex);
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
		GenericElementOdsHandler handler = (GenericElementOdsHandler) 
		GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
				element.getGenericType(), ODS_EXPORTER_KEY);

		if (handler != null)
		{
			JROdsExporterContext exporterContext = new ExporterContext(tableBuilder);

			handler.exportElement(exporterContext, element, gridCell, colIndex, rowIndex, emptyCols, yCutsRow, layout);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No ODS generic element handler for " 
						+ element.getGenericType());
			}
		}
	}

	@Override
	protected void setFreezePane(int rowIndex, int colIndex) {
		// nothing to do here
	}
	
	@Override
	protected void setSheetName(String sheetName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setAutoFilter(String autoFilterRange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void setRowLevels(XlsRowLevelInfo levelInfo, String level) {
		// TODO Auto-generated method stub
		
	}

	
	private static final Log log = LogFactory.getLog(JROdsExporter.class);
	
	protected static final String ODS_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.ods.";
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String ODS_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "ods";
	
	protected class ExporterContext extends BaseExporterContext implements JROdsExporterContext
	{
		TableBuilder tableBuilder = null;
		
		public ExporterContext(TableBuilder tableBuidler)
		{
			this.tableBuilder = tableBuidler;
		}
		
		@Override
		public TableBuilder getTableBuilder()
		{
			return tableBuilder;
		}
	}
	
	protected class OdsDocumentBuilder extends DocumentBuilder
	{
		public OdsDocumentBuilder(OasisZip oasisZip) 
		{
			super(oasisZip);
		}

		@Override
		public JRStyledText getStyledText(JRPrintText text) 
		{
			return JROdsExporter.this.getStyledText(text);
		}

		@Override
		public Locale getTextLocale(JRPrintText text) 
		{
			return JROdsExporter.this.getTextLocale(text);
		}

		@Override
		public String getInvalidCharReplacement() 
		{
			return JROdsExporter.this.invalidCharReplacement;
		}

		@Override
		protected void insertPageAnchor(TableBuilder tableBuilder) 
		{
			JROdsExporter.this.insertPageAnchor(tableBuilder);
		}

		@Override
		protected JRHyperlinkProducer getHyperlinkProducer(JRPrintHyperlink link) 
		{
			return JROdsExporter.this.getHyperlinkProducer(link);
		}

		@Override
		protected JasperReportsContext getJasperReportsContext() 
		{
			return JROdsExporter.this.getJasperReportsContext();
		}

		@Override
		protected int getReportIndex() 
		{
			return JROdsExporter.this.reportIndex;
		}

		@Override
		protected int getPageIndex() 
		{
			return JROdsExporter.this.pageIndex;
		}
	}

	
	protected class OdsTableBuilder extends TableBuilder
	{
		protected OdsTableBuilder(
				DocumentBuilder documentBuilder, 
				JasperPrint jasperPrint,
				int pageFormatIndex, 
				int pageIndex, 
				WriterHelper bodyWriter,
				WriterHelper styleWriter, 
				StyleCache styleCache, 
				Map<Integer, String> rowStyles, 
				Map<Integer, String> columnStyles,
				Color tabColor) 
		{
			super(
				documentBuilder, 
				jasperPrint, 
				pageFormatIndex, 
				pageIndex, 
				bodyWriter, 
				styleWriter, 
				styleCache, 
				rowStyles, 
				columnStyles, 
				tabColor);
		}
		
		protected OdsTableBuilder(
				DocumentBuilder documentBuilder, 
				JasperPrint jasperPrint,
				int pageFormatIndex, 
				int pageIndex, 
				WriterHelper bodyWriter,
				WriterHelper styleWriter, 
				StyleCache styleCache, 
				Map<Integer, String> rowStyles, 
				Map<Integer, String> columnStyles, 
				String sheetName,
				Color tabColor) 
		{
			super(
				documentBuilder, 
				jasperPrint, 
				pageFormatIndex, 
				pageIndex, 
				bodyWriter, 
				styleWriter, 
				styleCache, 
				rowStyles, 
				columnStyles,
				tabColor);
			this.tableName = sheetName;
		}

		@Override
		protected String getIgnoreHyperlinkProperty()
		{
			return XlsReportConfiguration.PROPERTY_IGNORE_HYPERLINK;
		}
		
		@Override
		protected void exportTextContents(JRPrintText textElement)
		{
			String href = null;
			
			String ignLnkPropName = getIgnoreHyperlinkProperty();
			Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(ignLnkPropName, textElement);
			boolean isIgnoreTextFormatting = isIgnoreTextFormatting(textElement);
			if (ignoreHyperlink == null)
			{
				ignoreHyperlink = getPropertiesUtil().getBooleanProperty(jasperPrint, ignLnkPropName, false);
			}

			if (!ignoreHyperlink)
			{
				href = documentBuilder.getHyperlinkURL(textElement, onePagePerSheet);
			}

			if (href == null)
			{
				exportStyledText(textElement, false, isIgnoreTextFormatting);
			}
			else
			{
				JRStyledText styledText = getStyledText(textElement);
				if (styledText != null && styledText.length() > 0)
				{
					String text = styledText.getText();
					Locale locale = getTextLocale(textElement);
					
					int runLimit = 0;
					AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();
					while (runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length())
					{
						// ODS does not like text:span inside text:a
						// writing one text:a inside text:span for each style run
						String runText = text.substring(iterator.getIndex(), runLimit);
						startTextSpan(
								iterator.getAttributes(), 
								runText, 
								locale,
								isIgnoreTextFormatting);
						writeHyperlink(textElement, href, true);
						writeText(runText);
						endHyperlink(true);
						endTextSpan();
						iterator.setIndex(runLimit);
					}
				}
			}
		}
	}
	
	
	/**
	 * @see #JROdsExporter(JasperReportsContext)
	 */
	public JROdsExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JROdsExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext(null);
	}


	@Override
	protected Class<OdsExporterConfiguration> getConfigurationInterface()
	{
		return OdsExporterConfiguration.class;
	}


	@Override
	protected Class<OdsReportConfiguration> getItemConfigurationInterface()
	{
		return OdsReportConfiguration.class;
	}
	

	@Override
	protected void initExport()
	{
		super.initExport();

//		macroTemplate =  macroTemplate == null ? getPropertiesUtil().getProperty(jasperPrint, PROPERTY_MACRO_TEMPLATE) : macroTemplate;
//		
//		password = 
//			getStringParameter(
//				JRXlsAbstractExporterParameter.PASSWORD,
//				JRXlsAbstractExporterParameter.PROPERTY_PASSWORD
//				);
	}
	

	@Override
	protected void initReport()
	{
		super.initReport();

		reportDpi = jasperPrint.getDpi();

		XlsReportConfiguration configuration = getCurrentItemConfiguration();
		
		//FIXMEODS setBackground()

		nature = 
			new JROdsExporterNature(
				jasperReportsContext, 
				filter, 
				configuration.isIgnoreGraphics(),
				configuration.isIgnorePageMargins()
				);
	}


//	/**
//	 *
//	 */
//	protected void exportEllipse(TableBuilder tableBuilder, JRPrintEllipse ellipse, JRExporterGridCell gridCell) throws IOException
//	{
//		JRLineBox box = new JRBaseLineBox(null);
//		JRPen pen = box.getPen();
//		pen.setLineColor(ellipse.getLinePen().getLineColor());
//		pen.setLineStyle(ellipse.getLinePen().getLineStyle());
//		pen.setLineWidth(ellipse.getLinePen().getLineWidth());
//
//		gridCell.setBox(box);//CAUTION: only some exporters set the cell box
//		
//		tableBuilder.buildCellHeader(styleCache.getCellStyle(gridCell), gridCell.getColSpan(), gridCell.getRowSpan());
//		tempBodyWriter.write("<text:p>");
//		insertPageAnchor();
////		tempBodyWriter.write(
////			"<draw:ellipse text:anchor-type=\"paragraph\" "
////			+ "draw:style-name=\"" + styleCache.getGraphicStyle(ellipse) + "\" "
////			+ "svg:width=\"" + Utility.translatePixelsToInches(ellipse.getWidth()) + "in\" "
////			+ "svg:height=\"" + Utility.translatePixelsToInches(ellipse.getHeight()) + "in\" "
////			+ "svg:x=\"0in\" "
////			+ "svg:y=\"0in\">"
////			+ "<text:p/></draw:ellipse>"
////			);
//		tempBodyWriter.write("</text:p>");
//		tableBuilder.buildCellFooter();
//	}


	@Override
	public String getExporterKey()
	{
		return ODS_EXPORTER_KEY;
	}

	
	@Override
	public String getExporterPropertiesPrefix()
	{
		return ODS_EXPORTER_PROPERTIES_PREFIX;
	}

	
	/**
	 * 
	 */
	protected void insertPageAnchor(TableBuilder tableBuilder)
	{
		if (startPage)
		{
			String pageName = DocumentBuilder.JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (sheetIndex - sheetsBeforeCurrentReport);
			String cellAddress = "$&apos;" + tableBuilder.getTableName() + "&apos;.$A$1";
			tableBuilder.exportAnchor(pageName);
			namedExpressions.append("<table:named-range table:name=\""+ pageName +"\" table:base-cell-address=\"" + cellAddress +"\" table:cell-range-address=\"" +cellAddress +"\"/>\n");
			startPage = false;
		}
	}
	
	@Override
	protected void exportEmptyReport() throws JRException, IOException 
	{
		// does nothing in ODS export
	}
	
}
