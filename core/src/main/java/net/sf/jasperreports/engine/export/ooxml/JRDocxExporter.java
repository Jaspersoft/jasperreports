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
package net.sf.jasperreports.engine.export.ooxml;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.font.TextAttribute;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintElementIndex;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.ElementGridCell;
import net.sf.jasperreports.engine.export.ExporterFilter;
import net.sf.jasperreports.engine.export.ExporterNature;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.Grid;
import net.sf.jasperreports.engine.export.GridRow;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.LengthUtil;
import net.sf.jasperreports.engine.export.OccupiedGridCell;
import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZipEntry;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.ExifOrientationEnum;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.ImageUtil.Insets;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.engine.util.StyledTextWriteContext;
import net.sf.jasperreports.export.DocxExporterConfiguration;
import net.sf.jasperreports.export.DocxReportConfiguration;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.PrintPartUnrollExporterInput;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;


/**
 * Exports a JasperReports document to DOCX format. It has binary output type and exports the document to a
 * grid-based layout, therefore having the known limitations of grid exporters.
 * <p/>
 * It can work in batch mode and supports all types of
 * exporter input and output, content filtering, and font mappings.
 * <p/>
 * Currently, there are the following special configurations that can be made to a DOCX
 * exporter instance (see {@link net.sf.jasperreports.export.DocxReportConfiguration}):
 * <ul>
 * <li>Forcing the use of nested tables to render the content of frame elements using either
 * the {@link net.sf.jasperreports.export.DocxReportConfiguration#isFramesAsNestedTables() isFramesAsNestedTables()} 
 * exporter configuration flag or its corresponding exporter hint called
 * {@link net.sf.jasperreports.export.DocxReportConfiguration#PROPERTY_FRAMES_AS_NESTED_TABLES net.sf.jasperreports.export.docx.frames.as.nested.tables}.</li>
 * <li>Allowing table rows to adjust their height if more text is typed into their cells using
 * the Word editor. This is controlled using either the
 * {@link net.sf.jasperreports.export.DocxReportConfiguration#isFlexibleRowHeight() isFlexibleRowHeight()} 
 * exporter configuration flag, or its corresponding exporter hint called
 * {@link net.sf.jasperreports.export.DocxReportConfiguration#PROPERTY_FLEXIBLE_ROW_HEIGHT net.sf.jasperreports.export.docx.flexible.row.height}.</li>
 * <li>Ignoring hyperlinks in generated documents if they are not intended for the DOCX output format. This can be 
 * customized using either the 
 * {@link net.sf.jasperreports.export.DocxReportConfiguration#isIgnoreHyperlink() isIgnoreHyperlink()} 
 * exporter configuration flag, or its corresponding exporter hint called
 * {@link net.sf.jasperreports.export.DocxReportConfiguration#PROPERTY_IGNORE_HYPERLINK net.sf.jasperreports.export.docx.ignore.hyperlink}</li>
 * </ul>
 * 
 * @see net.sf.jasperreports.export.DocxReportConfiguration
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class JRDocxExporter extends JRAbstractExporter<DocxReportConfiguration, DocxExporterConfiguration, OutputStreamExporterOutput, JRDocxExporterContext>
{
	private static final Log log = LogFactory.getLog(JRDocxExporter.class);
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String DOCX_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "docx";
	
	public static final String EXCEPTION_MESSAGE_KEY_COLUMN_COUNT_OUT_OF_RANGE = "export.docx.column.count.out.of.range";
	
	public static final String DOCX_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.docx.";

	/**
	 * This property is used to mark text elements as being hidden either for printing or on-screen display.
	 * @see JRPropertiesUtil
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_6,
			valueType = Boolean.class
			)
	public static final String PROPERTY_HIDDEN_TEXT = DOCX_EXPORTER_PROPERTIES_PREFIX + "hidden.text";

	/**
	 *
	 */
	public static final String JR_PAGE_ANCHOR_PREFIX = "JR_PAGE_ANCHOR_";

	/**
	 *
	 */
	public static final String IMAGE_NAME_PREFIX = "img_";
	protected static final int IMAGE_NAME_PREFIX_LEGTH = IMAGE_NAME_PREFIX.length();
	public static final String IMAGE_LINK_PREFIX = "link_" + IMAGE_NAME_PREFIX;
	
	/**
	 *
	 */
	protected DocxZip docxZip;
	protected DocxDocumentHelper docHelper;
	protected Writer docWriter;
	protected DocxHeaderHelper headerHelper;
	protected Writer headerWriter;
	protected DocxDocumentHelper crtDocHelper;
	protected Writer crtDocWriter;

	protected Map<String, Pair<String, ExifOrientationEnum>> rendererToImagePathMap;
	protected RenderersCache renderersCache;
//	protected Map imageMaps;

	protected int reportIndex;
	protected int pageIndex;
	protected int startPageIndex;
	protected int endPageIndex;
	protected int tableIndex;
	protected int headerIndex;
	protected boolean startPage;
	protected String invalidCharReplacement;
	protected PrintPageFormat pageFormat;
	protected JRGridLayout pageGridLayout;

	protected LinkedList<Color> backcolorStack = new LinkedList<>();
	protected Color backcolor;

	protected DocxRunHelper runHelper;
	protected DocxRunHelper headerRunHelper;
	protected DocxRunHelper crtRunHelper;

	protected ExporterNature backgroundNature;
	protected ExporterNature pageNature;

	protected long bookmarkIndex;
	
	protected String pageAnchor;
	
	protected DocxRelsHelper relsHelper;
	protected DocxHeaderRelsHelper headerRelsHelper;
	protected DocxRelsHelper crtRelsHelper;
	protected DocxContentTypesHelper ctHelper;
	protected PropsAppHelper appHelper;
	protected PropsCoreHelper coreHelper;
	protected DocxFontHelper docxFontHelper;
	protected DocxFontTableHelper docxFontTableHelper;
	protected DocxFontTableRelsHelper docxFontTableRelsHelper;
	
	boolean emptyPageState;
	

	protected class ExporterContext extends BaseExporterContext implements JRDocxExporterContext
	{
		DocxTableHelper tableHelper = null;
		
		public ExporterContext(DocxTableHelper tableHelper)
		{
			this.tableHelper = tableHelper;
		}
		
		@Override
		public DocxTableHelper getTableHelper()
		{
			return tableHelper;
		}
	}
	
	
	/**
	 * @see #JRDocxExporter(JasperReportsContext)
	 */
	public JRDocxExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}


	/**
	 *
	 */
	public JRDocxExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext(null);
	}


	@Override
	protected Class<DocxExporterConfiguration> getConfigurationInterface()
	{
		return DocxExporterConfiguration.class;
	}


	@Override
	protected Class<DocxReportConfiguration> getItemConfigurationInterface()
	{
		return DocxReportConfiguration.class;
	}
	

	@Override
	public void exportReport() throws JRException
	{
		/*   */
		ensureJasperReportsContext();
		ensureInput();

		initExport();
		
		ensureOutput();
		
		OutputStream outputStream = getExporterOutput().getOutputStream();

		try
		{
			exportReportToStream(outputStream);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			getExporterOutput().close();
			resetExportContext();
		}
	}

	
	@Override
	protected void initExport()
	{
		super.initExport();

		rendererToImagePathMap = new HashMap<>();//FIXMEIMAGE why this is reset at export and not report; are there any others?
//		imageMaps = new HashMap();
//		hyperlinksMap = new HashMap();
	}


	@Override
	protected void initReport()
	{
		super.initReport();
		
		if (jasperPrint.hasProperties() && jasperPrint.getPropertiesMap().containsProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS))
		{
			// allows null values for the property
			invalidCharReplacement = jasperPrint.getProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS);
		}
		else
		{
			invalidCharReplacement = getPropertiesUtil().getProperty(JRXmlExporter.PROPERTY_REPLACE_INVALID_CHARS, jasperPrint);
		}

		DocxReportConfiguration configuration = getCurrentItemConfiguration();
		
		backgroundNature = 
			new JRDocxExporterNature(
				jasperReportsContext, 
				new BackgroundExporterFilter(filter, true),  
				!configuration.isFramesAsNestedTables()
				);

		pageNature = 
			new JRDocxExporterNature(
				jasperReportsContext, 
				configuration.isBackgroundAsHeader() ? new BackgroundExporterFilter(filter, false) : filter,  
				!configuration.isFramesAsNestedTables()
				);

		renderersCache = new RenderersCache(getJasperReportsContext());
	}

	
	/**
	 *
	 */
	protected void exportReportToStream(OutputStream os) throws JRException, IOException
	{
		docxZip = new DocxZip();

		docWriter = docxZip.getDocumentEntry().getWriter();
		
		docHelper = new DocxDocumentHelper(jasperReportsContext, docWriter);
		docHelper.exportHeader(pageFormat);
		
		relsHelper = new DocxRelsHelper(jasperReportsContext, docxZip.getRelsEntry().getWriter());
		relsHelper.exportHeader();

		ctHelper = new DocxContentTypesHelper(jasperReportsContext, docxZip.getContentTypesEntry().getWriter());
		ctHelper.exportHeader();
		
		appHelper = new PropsAppHelper(jasperReportsContext, docxZip.getAppEntry().getWriter());
		coreHelper = new PropsCoreHelper(jasperReportsContext, docxZip.getCoreEntry().getWriter());

		appHelper.exportHeader();
			
		DocxExporterConfiguration configuration = getCurrentConfiguration();

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

		List<ExporterInputItem> items = exporterInput.getItems();

		boolean isEmbedFonts = Boolean.TRUE.equals(configuration.isEmbedFonts());
		
		docxFontHelper = 
			new DocxFontHelper(
				jasperReportsContext, 
				docxZip,
				isEmbedFonts
				);

		DocxStyleHelper styleHelper = 
			new DocxStyleHelper(
				this,
				docxZip.getStylesEntry().getWriter(),
				docxFontHelper
				);
		styleHelper.export(exporterInput);
		styleHelper.close();

		DocxSettingsHelper settingsHelper = 
			new DocxSettingsHelper(
				jasperReportsContext,
				docxZip.getSettingsEntry().getWriter()
				);
		settingsHelper.export(jasperPrint, isEmbedFonts);
		settingsHelper.close();

		docxFontTableHelper = new DocxFontTableHelper(jasperReportsContext, docxZip.getFontTableEntry().getWriter());
		docxFontTableHelper.exportHeader();
		
		docxFontTableRelsHelper = new DocxFontTableRelsHelper(jasperReportsContext, docxZip.getFontTableRelsEntry().getWriter());
		docxFontTableRelsHelper.exportHeader();
		
		runHelper = new DocxRunHelper(jasperReportsContext, docWriter, docxFontHelper);
		
		pageFormat = null;

		for (reportIndex = 0; reportIndex < items.size(); reportIndex++) // remember this uses PrintPartUnrollExporterInput
		{
			ExporterInputItem item = items.get(reportIndex);

			setCurrentExporterInputItem(item);
			
			boolean isSizePageToContent = getCurrentItemConfiguration().isSizePageToContent();

			bookmarkIndex = 0;
			emptyPageState = false;
			
			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				PageRange pageRange = getPageRange();
				startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
				endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

				if (startPageIndex <= endPageIndex)
				{
					pageFormat = jasperPrint.getPageFormat(startPageIndex);
					
					exportHeader(pages.get(startPageIndex));

					JRPrintPage page = null;
					for (pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
					{
						checkInterrupted();

						page = pages.get(pageIndex);

						exportPage(page);
						
						if (isSizePageToContent)
						{
							docHelper.exportSection(
								pageFormat, 
								pageGridLayout,
								true, //isSizePageToContent
								headerIndex, 
								reportIndex == items.size() - 1 && pageIndex == endPageIndex
								);
						}
					}

					if (!isSizePageToContent)
					{
						docHelper.exportSection(
							pageFormat, 
							pageGridLayout,
							false, //!isSizePageToContent
							headerIndex, 
							reportIndex == items.size() - 1
							);
					}
				}
			}
		}

		docHelper.exportFooter();
		docHelper.close();

//		if ((hyperlinksMap != null && hyperlinksMap.size() > 0))
//		{
//			for(Iterator it = hyperlinksMap.keySet().iterator(); it.hasNext();)
//			{
//				String href = (String)it.next();
//				String id = (String)hyperlinksMap.get(href);
//
//				relsHelper.exportHyperlink(id, href);
//			}
//		}

		relsHelper.exportFooter();
		relsHelper.close();

		ctHelper.exportFooter();
		ctHelper.close();

		appHelper.exportFooter();
		appHelper.close();

		coreHelper.exportFooter();
		coreHelper.close();
		
		docxFontHelper.exportFonts();

		docxFontTableHelper.exportFooter();
		docxFontTableHelper.close();

		docxFontTableRelsHelper.exportFooter();
		docxFontTableRelsHelper.close();

		String password = getCurrentConfiguration().getEncryptionPassword();
		if (password == null || password.trim().length() == 0)
		{
			docxZip.zipEntries(os);
		}
		else
		{
			// isolate POI encryption code into separate class to avoid POI dependency when not needed
			OoxmlEncryptUtil.zipEntries(docxZip, os, password);
		}

		docxZip.dispose();
	}


	/**
	 *
	 */
	protected void exportHeader(JRPrintPage page) throws JRException
	{
		DocxReportConfiguration configuration = getCurrentItemConfiguration();
		
		if (configuration.isBackgroundAsHeader())
		{
			headerIndex++;
			ExportZipEntry headerEntry = docxZip.addHeader(headerIndex);
			headerWriter = headerEntry.getWriter();

			headerHelper = new DocxHeaderHelper(jasperReportsContext, headerWriter);
			headerHelper.exportHeader(pageFormat);

			ExportZipEntry headerRelsEntry = docxZip.addHeaderRels(headerIndex);
			headerRelsHelper = new DocxHeaderRelsHelper(jasperReportsContext, headerRelsEntry.getWriter());
			headerRelsHelper.exportHeader();
			
			headerRunHelper = new DocxRunHelper(jasperReportsContext, headerWriter, docxFontHelper);
			
			pageAnchor = null;

			crtDocHelper = headerHelper;
			crtDocWriter = headerWriter;
			crtRelsHelper = headerRelsHelper;
			crtRunHelper = headerRunHelper;
			
			JRGridLayout backgrounGridLayout =
				new JRGridLayout(
					backgroundNature,
					page.getElements(),
					pageFormat.getPageWidth(),
					pageFormat.getPageHeight(),
					configuration.getOffsetX() == null ? 0 : configuration.getOffsetX(), 
					configuration.getOffsetY() == null ? 0 : configuration.getOffsetY(),
					null //address
					);

			exportGrid(backgrounGridLayout, null);
			
			relsHelper.exportHeader(headerIndex);
			ctHelper.exportHeader(headerIndex);

			headerHelper.exportFooter();
			headerHelper.close();

			headerRelsHelper.exportFooter();
			headerRelsHelper.close();
		}
	}


	/**
	 *
	 */
	protected void exportPage(JRPrintPage page) throws JRException
	{
		startPage = true;
		pageAnchor = JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (pageIndex + 1);
		
		ReportExportConfiguration configuration = getCurrentItemConfiguration();

		crtDocHelper = docHelper;
		crtDocWriter = docWriter;
		crtRelsHelper = relsHelper;
		crtRunHelper = runHelper;

		pageGridLayout =
			new JRGridLayout(
				pageNature,
				page.getElements(),
				pageFormat.getPageWidth(),
				pageFormat.getPageHeight(),
				configuration.getOffsetX() == null ? 0 : configuration.getOffsetX(), 
				configuration.getOffsetY() == null ? 0 : configuration.getOffsetY(),
				null //address
				);

		exportGrid(pageGridLayout, null);
		
		JRExportProgressMonitor progressMonitor = configuration.getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}


	/**
	 *
	 */
	protected void exportGrid(JRGridLayout gridLayout, JRPrintElementIndex frameIndex) throws JRException
	{
		CutsInfo xCuts = gridLayout.getXCuts();
		Grid grid = gridLayout.getGrid();
		DocxTableHelper tableHelper = null;

		int rowCount = grid.getRowCount();
		if (rowCount > 0 && grid.getColumnCount() > 63)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_COLUMN_COUNT_OUT_OF_RANGE,  
					new Object[]{grid.getColumnCount()} 
					);
		}
		
		// an empty page is encountered; 
		// if it's the first one in a series of consecutive empty pages, emptyPageState == false, otherwise emptyPageState == true
		if (rowCount == 0 && (pageIndex < endPageIndex || !emptyPageState))
		{
			tableHelper = 
					new DocxTableHelper(
						jasperReportsContext,
						crtDocWriter, 
						xCuts,
						false,
						pageFormat,
						frameIndex
						);
			int maxReportIndex = exporterInput.getItems().size() - 1;
			
			// while the first and last page in the JasperPrint list need single breaks, all the others require double-breaking 
			boolean twice = 
					(pageIndex > startPageIndex && pageIndex < endPageIndex && !emptyPageState)
					||(reportIndex < maxReportIndex && pageIndex == endPageIndex);
			tableHelper.getParagraphHelper().exportEmptyPage(pageAnchor, bookmarkIndex, twice);
			bookmarkIndex++;
			emptyPageState = true;
			return;
		}
		
		tableHelper = 
				new DocxTableHelper(
					jasperReportsContext,
					crtDocWriter, 
					xCuts,
					frameIndex == null && (reportIndex != 0 || pageIndex != startPageIndex),
					pageFormat,
					frameIndex
					);

		tableHelper.exportHeader();
		
		boolean isFlexibleRowHeight = getCurrentItemConfiguration().isFlexibleRowHeight();

		for(int row = 0; row < rowCount; row++)
		{
			int emptyCellColSpan = 0;
			//int emptyCellWidth = 0;

			boolean allowRowResize = false;
			int maxTopPadding = 0; //for some strange reason, the top margin applies to all cells in the row
			int maxBottomPadding = 0; //for some strange reason, the bottom margin affects the row height; subtracting it here
			GridRow gridRow = grid.getRow(row);
			int rowSize = gridRow.size();
			for(int col = 0; col < rowSize; col++)
			{
				JRExporterGridCell gridCell = gridRow.get(col);
				JRLineBox box = gridCell.getBox();
				if (box != null)
				{
					Integer topPadding = box.getTopPadding() + Math.round(box.getTopPen().getLineWidth());
					if (
						topPadding != null 
						&& maxTopPadding < topPadding
						)
					{
						maxTopPadding = topPadding;
					}

					Integer bottomPadding = box.getBottomPadding();
					if (
						bottomPadding != null 
						&& maxBottomPadding < bottomPadding
						)
					{
						maxBottomPadding = bottomPadding;
					}
				}
				
				if (isFlexibleRowHeight && !allowRowResize) // when flexible row height is required, once allowRowResize becomes true, will remain true
				{
					JRPrintElement cellElement = gridCell.getElement();
					if (gridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
					{
						cellElement = ((OccupiedGridCell)gridCell).getOccupier().getElement();
					}
					allowRowResize = cellElement instanceof JRPrintText || cellElement instanceof JRPrintFrame;
				}
			}
			tableHelper.setRowMaxTopPadding(maxTopPadding);

			int rowHeight = gridLayout.getRowHeight(row) - maxBottomPadding;

			tableHelper.exportRowHeader(
				rowHeight,
				allowRowResize
				);

			int firstCutOffset = xCuts.getCutOffset(0);
			if (frameIndex == null && firstCutOffset > pageFormat.getLeftMargin())
			{
				tableHelper.exportEmptyCell(null, 1, startPage, bookmarkIndex, pageAnchor);
				startPage = false;
			}
			
			for(int col = 0; col < rowSize; col++)
			{
				JRExporterGridCell gridCell = gridRow.get(col);
				if (gridCell.getType() == JRExporterGridCell.TYPE_OCCUPIED_CELL)
				{
					if (emptyCellColSpan > 0)
					{
						//tableHelper.exportEmptyCell(gridCell, emptyCellColSpan);
						emptyCellColSpan = 0;
						//emptyCellWidth = 0;
					}

					OccupiedGridCell occupiedGridCell = (OccupiedGridCell)gridCell;
					ElementGridCell elementGridCell = (ElementGridCell)occupiedGridCell.getOccupier();
					tableHelper.exportOccupiedCells(elementGridCell, startPage, bookmarkIndex, pageAnchor);
					if (startPage)
					{
						// increment the bookmarkIndex for the first cell in the sheet, due to page anchor creation
						bookmarkIndex++;
					}
					col += elementGridCell.getColSpan() - 1;
				}
				else if (gridCell.getType() == JRExporterGridCell.TYPE_ELEMENT_CELL)
				{
					if (emptyCellColSpan > 0)
					{
						//writeEmptyCell(tableHelper, gridCell, emptyCellColSpan, emptyCellWidth, rowHeight);
						emptyCellColSpan = 0;
						//emptyCellWidth = 0;
					}

					JRPrintElement element = gridCell.getElement();

					if (element instanceof JRPrintLine)
					{
						exportLine(tableHelper, (JRPrintLine)element, gridCell);
					}
					else if (element instanceof JRPrintRectangle)
					{
						exportRectangle(tableHelper, (JRPrintRectangle)element, gridCell);
					}
					else if (element instanceof JRPrintEllipse)
					{
						exportEllipse(tableHelper, (JRPrintEllipse)element, gridCell);
					}
					else if (element instanceof JRPrintImage)
					{
						exportImage(tableHelper, (JRPrintImage)element, gridCell);
					}
					else if (element instanceof JRPrintText)
					{
						exportText(tableHelper, (JRPrintText)element, gridCell);
					}
					else if (element instanceof JRPrintFrame)
					{
						exportFrame(tableHelper, (JRPrintFrame)element, gridCell);
					}
					else if (element instanceof JRGenericPrintElement)
					{
						exportGenericElement(tableHelper, (JRGenericPrintElement)element, gridCell);
					}

					col += gridCell.getColSpan() - 1;
				}
				else
				{
					emptyCellColSpan++;
					//emptyCellWidth += gridCell.getWidth();
					tableHelper.exportEmptyCell(gridCell, 1, startPage, bookmarkIndex, pageAnchor);
					if (startPage)
					{
						// increment the bookmarkIndex for the first cell in the sheet, due to page anchor creation
						bookmarkIndex++;
					}
				}
				startPage = false;
			}

//			if (emptyCellColSpan > 0)
//			{
//				//writeEmptyCell(tableHelper, null, emptyCellColSpan, emptyCellWidth, rowHeight);
//			}
			
			int lastColumnOffset = xCuts.getCutOffset(xCuts.size() - 1);
			if (frameIndex == null && lastColumnOffset < pageFormat.getPageWidth() - pageFormat.getRightMargin())
			{
				tableHelper.exportEmptyCell(null, 1, startPage, bookmarkIndex, pageAnchor);
			}

			tableHelper.exportRowFooter();
		}

		tableHelper.exportFooter();
		// if a non-empty page was exported, the series of previous empty pages is ended
		emptyPageState = false;
	}


	/**
	 *
	 */
	protected void exportLine(DocxTableHelper tableHelper, JRPrintLine line, JRExporterGridCell gridCell)
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
		
		tableHelper.getCellHelper().exportHeader(line, gridCell);
		tableHelper.getParagraphHelper().exportEmptyParagraph(startPage, bookmarkIndex, pageAnchor);
		if (startPage)
		{
			// increment the bookmarkIndex for the first cell in the sheet, due to page anchor creation
			bookmarkIndex++;
		}
		tableHelper.getCellHelper().exportFooter();
	}


	/**
	 *
	 */
	protected void exportRectangle(DocxTableHelper tableHelper, JRPrintRectangle rectangle, JRExporterGridCell gridCell)
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = box.getPen();
		pen.setLineColor(rectangle.getLinePen().getLineColor());
		pen.setLineStyle(rectangle.getLinePen().getLineStyle());
		pen.setLineWidth(rectangle.getLinePen().getLineWidth());

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box
		
		tableHelper.getCellHelper().exportHeader(rectangle, gridCell);
		tableHelper.getParagraphHelper().exportEmptyParagraph(startPage, bookmarkIndex, pageAnchor);
		if (startPage)
		{
			// increment the bookmarkIndex for the first cell in the sheet, due to page anchor creation
			bookmarkIndex++;
		}
		tableHelper.getCellHelper().exportFooter();
	}


	/**
	 *
	 */
	protected void exportEllipse(DocxTableHelper tableHelper, JRPrintEllipse ellipse, JRExporterGridCell gridCell)
	{
		JRLineBox box = new JRBaseLineBox(null);
		JRPen pen = box.getPen();
		pen.setLineColor(ellipse.getLinePen().getLineColor());
		pen.setLineStyle(ellipse.getLinePen().getLineStyle());
		pen.setLineWidth(ellipse.getLinePen().getLineWidth());

		gridCell.setBox(box);//CAUTION: only some exporters set the cell box
		
		tableHelper.getCellHelper().exportHeader(ellipse, gridCell);
		tableHelper.getParagraphHelper().exportEmptyParagraph(startPage, bookmarkIndex, pageAnchor);
		if (startPage)
		{
			// increment the bookmarkIndex for the first cell in the sheet, due to page anchor creation
			bookmarkIndex++;
		}
		tableHelper.getCellHelper().exportFooter();
	}


	/**
	 *
	 */
	public void exportText(DocxTableHelper tableHelper, JRPrintText text, JRExporterGridCell gridCell)
	{
		tableHelper.getCellHelper().exportHeader(text, gridCell);

		JRStyledText styledText = getStyledText(text);

		int textLength = 0;

		if (styledText != null)
		{
			textLength = styledText.length();
		}

//		if (styleBuffer.length() > 0)
//		{
//			writer.write(" style=\"");
//			writer.write(styleBuffer.toString());
//			writer.write("\"");
//		}
//
//		writer.write(">");
		crtDocHelper.write("     <w:p>\n");

		tableHelper.getParagraphHelper().exportProps(text);
		if (startPage)
		{
			insertBookmark(pageAnchor, crtDocHelper);
		}
		if (text.getAnchorName() != null)
		{
			insertBookmark(text.getAnchorName(), crtDocHelper);
		}

		boolean startedHyperlink = startHyperlink(text, true);
		boolean isNewLineAsParagraph = false;
		if (HorizontalTextAlignEnum.JUSTIFIED.equals(text.getHorizontalTextAlign()))
		{
			if (text.hasProperties() && text.getPropertiesMap().containsProperty(DocxReportConfiguration.PROPERTY_NEW_LINE_AS_PARAGRAPH))
			{
				isNewLineAsParagraph = getPropertiesUtil().getBooleanProperty(text, DocxReportConfiguration.PROPERTY_NEW_LINE_AS_PARAGRAPH, false);
			}
			else
			{
				isNewLineAsParagraph = getCurrentItemConfiguration().isNewLineAsParagraph();
			}
		}

		if (textLength > 0)
		{
			exportStyledText(
				getCurrentJasperPrint().getDefaultStyleProvider().getStyleResolver().getBaseStyle(text), 
				styledText, 
				getTextLocale(text),
				getPropertiesUtil().getBooleanProperty(text, PROPERTY_HIDDEN_TEXT, false),
				startedHyperlink, 
				isNewLineAsParagraph,
				text.getRunDirection() == RunDirectionEnum.RTL
				);
		}

		if (startedHyperlink)
		{
			endHyperlink(true);
		}

		crtDocHelper.write("     </w:p>\n");

		tableHelper.getCellHelper().exportFooter();
	}


	/**
	 *
	 */
	protected void exportStyledText(
		JRStyle style, 
		JRStyledText styledText, 
		Locale locale, 
		boolean hiddenText, 
		boolean startedHyperlink, 
		boolean isNewLineJustified,
		boolean rtl
		)
	{
		StyledTextWriteContext context = new StyledTextWriteContext();
		
		Color elementBackcolor = null;
		Map<AttributedCharacterIterator.Attribute, Object> globalAttributes = styledText.getGlobalAttributes();
		if (globalAttributes != null)
		{
			elementBackcolor = (Color)styledText.getGlobalAttributes().get(TextAttribute.BACKGROUND);
		}
		
		String text = styledText.getText();

		int runLimit = 0;

		AttributedCharacterIterator iterator = styledText.getAttributedString().getIterator();

		while(runLimit < styledText.length() && (runLimit = iterator.getRunLimit()) <= styledText.length())
		{
			Map<Attribute,Object> attributes = iterator.getAttributes();

			String runText = text.substring(iterator.getIndex(), runLimit);

			context.next(attributes, runText);

			//if (context.listItemStartsWithNewLine() && !context.isListItemStart() && (context.isListItemEnd() || context.isListStart() || context.isListEnd()))
			//{
			//	runText = runText.substring(1);
			//}

			if (runText.length() > 0)
			{
				boolean localHyperlink = false;

				if (!startedHyperlink)
				{
					JRPrintHyperlink hyperlink = (JRPrintHyperlink)attributes.get(JRTextAttribute.HYPERLINK);
					if (hyperlink != null)
					{
						localHyperlink = startHyperlink(hyperlink, true);
					}
				}
				
				String bulletText = JRStyledTextUtil.getIndentedBulletText(context);
				
				crtRunHelper.export(
					style, 
					attributes, 
					(bulletText == null ? "" : bulletText) + runText,
					locale,
					hiddenText,
					invalidCharReplacement,
					elementBackcolor,
					isNewLineJustified,
					rtl
					);

				if (localHyperlink)
				{
					endHyperlink(true);
				}
			}

			iterator.setIndex(runLimit);
		}
	}


	/**
	 *
	 */
	public void exportImage(DocxTableHelper tableHelper, JRPrintImage image, JRExporterGridCell gridCell) throws JRException
	{
		int leftPadding = image.getLineBox().getLeftPadding();
		int topPadding = image.getLineBox().getTopPadding() + Math.round(image.getLineBox().getTopPen().getLineWidth()); // top border eats into cell space
		int rightPadding = image.getLineBox().getRightPadding();
		int bottomPadding = image.getLineBox().getBottomPadding();

		int availableImageWidth = image.getWidth() - leftPadding - rightPadding;
		availableImageWidth = availableImageWidth < 0 ? 0 : availableImageWidth;

		int availableImageHeight = image.getHeight() - topPadding - bottomPadding;
		availableImageHeight = availableImageHeight < 0 ? 0 : availableImageHeight;

		tableHelper.getCellHelper().exportHeader(image, gridCell);

		crtDocHelper.write("<w:p>\n");//FIXMEDOCX why is this here and not further down?
		tableHelper.getParagraphHelper().exportProps(image);

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
				Renderable onErrorRenderer = getRendererUtil().handleImageError(e, image.getOnErrorType());
				if (onErrorRenderer != null)
				{
					imageProcessorResult = imageProcessor.process(onErrorRenderer);
				}
			}
			
			if (imageProcessorResult != null)
			{
				int renderWidth = availableImageWidth;
				int renderHeight = availableImageHeight;
				
				float xoffset = 0;
				float yoffset = 0;

				double cropTop = 0;
				double cropLeft = 0;
				double cropBottom = 0;
				double cropRight = 0;
				
				int angle = 0;
				
				switch (image.getScaleImage())
				{
					case FILL_FRAME :
					{
						switch (ImageUtil.getRotation(image.getRotation(), imageProcessorResult.exifOrientation))
						{
							case LEFT:
								renderWidth = availableImageHeight;
								renderHeight = availableImageWidth;
								xoffset = (availableImageWidth - availableImageHeight) / 2;
								yoffset = - (availableImageWidth - availableImageHeight) / 2;
								angle = -90;
								break;
							case RIGHT:
								renderWidth = availableImageHeight;
								renderHeight = availableImageWidth;
								xoffset = (availableImageWidth - availableImageHeight) / 2;
								yoffset = - (availableImageWidth - availableImageHeight) / 2;
								angle = 90;
								break;
							case UPSIDE_DOWN:
								renderWidth = availableImageWidth;
								renderHeight = availableImageHeight;
								angle = 180;
								break;
							case NONE:
							default:
								renderWidth = availableImageWidth;
								renderHeight = availableImageHeight;
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

						renderWidth = availableImageWidth;
						renderHeight = availableImageHeight;

						switch (ImageUtil.getRotation(image.getRotation(), imageProcessorResult.exifOrientation))
						{
							case LEFT:
								if (dimension == null)
								{
									normalWidth = availableImageHeight;
									normalHeight = availableImageWidth;
								}
								renderWidth = availableImageHeight;
								renderHeight = availableImageWidth;
								xoffset = (availableImageWidth - availableImageHeight) / 2;
								yoffset = - (availableImageWidth - availableImageHeight) / 2;
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
								renderWidth = availableImageHeight;
								renderHeight = availableImageWidth;
								xoffset = (availableImageWidth - availableImageHeight) / 2;
								yoffset = - (availableImageWidth - availableImageHeight) / 2;
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
								renderWidth = availableImageHeight;
								renderHeight = availableImageWidth;
								ratioX = availableImageWidth / normalHeight;
								ratioY = availableImageHeight / normalWidth;
								ratioX = ratioX < ratioY ? ratioX : ratioY;
								ratioY = ratioX;
								imageWidth = (int)(normalHeight * ratioX);
								imageHeight = (int)(normalWidth * ratioY);
								xoffset = (availableImageWidth - availableImageHeight) / 2;
								yoffset = - (availableImageWidth - availableImageHeight) / 2;
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
								renderWidth = availableImageHeight;
								renderHeight = availableImageWidth;
								ratioX = availableImageWidth / normalHeight;
								ratioY = availableImageHeight / normalWidth;
								ratioX = ratioX < ratioY ? ratioX : ratioY;
								ratioY = ratioX;
								imageWidth = (int)(normalHeight * ratioX);
								imageHeight = (int)(normalWidth * ratioY);
								xoffset = (availableImageWidth - availableImageHeight) / 2;
								yoffset = - (availableImageWidth - availableImageHeight) / 2;
								cropLeft = ImageUtil.getXAlignFactor(image) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropRight = (1f - ImageUtil.getXAlignFactor(image)) * (availableImageHeight - imageHeight) / availableImageHeight;
								cropTop = ImageUtil.getYAlignFactor(image) * (availableImageWidth - imageWidth) / availableImageWidth;
								cropBottom = (1f - ImageUtil.getYAlignFactor(image)) * (availableImageWidth - imageWidth) / availableImageWidth;
								angle = 90;
								break;
							case UPSIDE_DOWN:
								renderWidth = availableImageWidth;
								renderHeight = availableImageHeight;
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
								renderWidth = availableImageWidth;
								renderHeight = availableImageHeight;
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

				if (startPage)
				{
					insertBookmark(pageAnchor, crtDocHelper);
				}
				if (image.getAnchorName() != null)
				{
					insertBookmark(image.getAnchorName(), crtDocHelper);
				}


//				boolean startedHyperlink = startHyperlink(image,false);

				crtDocHelper.write("<w:r>\n"); 
				crtDocHelper.write("<w:rPr/>\n"); 
				crtDocHelper.write("<w:drawing>\n");
				if (crtDocHelper == docHelper) // simple test to differentiate between document.xml writer and header.xml writer
				{
					// in main document writer, keep use of anchor for images, to avoid unnecessary regressions when introducing header writer for background band
					// anchor for images solves the issue with cell top margins eating up from top of the image
					crtDocHelper.write("<wp:anchor distT=\"0\" distB=\"0\" distL=\"0\" distR=\"0\" simplePos=\"0\" "
						+ "relativeHeight=\"0\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\">\n");
					crtDocHelper.write("<wp:simplePos x=\"0\" y=\"0\"/>\n");
					crtDocHelper.write("<wp:positionH relativeFrom=\"column\">\n");
					crtDocHelper.write("<wp:posOffset>" + LengthUtil.emu(xoffset) + "</wp:posOffset>\n");
					crtDocHelper.write("</wp:positionH>\n");
					crtDocHelper.write("<wp:positionV relativeFrom=\"paragraph\">\n");
					crtDocHelper.write("<wp:posOffset>" + LengthUtil.emu(yoffset + topPadding - tableHelper.getRowMaxTopPadding()) + "</wp:posOffset>\n");
					crtDocHelper.write("</wp:positionV>\n");
				}
				else
				{
					// in header writer, images need inline instead of anchor, otherwise they do not show up
					crtDocHelper.write("<wp:inline distT=\"0\" distB=\"0\" distL=\"0\" distR=\"0\">\n");
				}
				crtDocHelper.write("<wp:extent cx=\"" + LengthUtil.emu(renderWidth) + "\" cy=\"" + LengthUtil.emu(renderHeight) + "\"/>\n");
				crtDocHelper.write("<wp:effectExtent l=\"0\" t=\"0\" r=\"0\" b=\"0\"/>\n");
				crtDocHelper.write("<wp:wrapNone/>\n");

				int imageId = image.hashCode() > 0 ? image.hashCode() : -image.hashCode();
				String rId = IMAGE_LINK_PREFIX + getElementIndex(gridCell);
				crtDocHelper.write("<wp:docPr id=\"" + imageId + "\" name=\"Picture\">\n");
				if (getHyperlinkURL(image) != null)
				{
					crtDocHelper.write("<a:hlinkClick xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\" r:id=\"" + rId + "\"/>\n");
				}
				crtDocHelper.write("</wp:docPr>\n");
				crtDocHelper.write("<a:graphic>\n");
				crtDocHelper.write("<a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n");
				crtDocHelper.write("<pic:pic>\n");
				crtDocHelper.write("<pic:nvPicPr><pic:cNvPr id=\"" + imageId + "\" name=\"Picture\"/><pic:cNvPicPr/></pic:nvPicPr>\n");
				crtDocHelper.write("<pic:blipFill>\n");

				crtDocHelper.write("<a:blip r:embed=\"" + imageProcessorResult.imagePath + "\"/>");
				crtDocHelper.write("<a:srcRect/>");
				crtDocHelper.write("<a:stretch><a:fillRect");
				crtDocHelper.write(" l=\"" + (int)(100000 * cropLeft) + "\"");
				crtDocHelper.write(" t=\"" + (int)(100000 * cropTop) + "\"");
				crtDocHelper.write(" r=\"" + (int)(100000 * cropRight) + "\"");
				crtDocHelper.write(" b=\"" + (int)(100000 * cropBottom) + "\"");
				crtDocHelper.write("/></a:stretch>\n");
				crtDocHelper.write("</pic:blipFill>\n");
				crtDocHelper.write("<pic:spPr>\n");
				crtDocHelper.write("  <a:xfrm rot=\"" + (60000 * angle) + "\">\n");
				crtDocHelper.write("    <a:off x=\"0\" y=\"0\"/>\n");
				crtDocHelper.write("    <a:ext cx=\"" + LengthUtil.emu(renderWidth) + "\" cy=\"" + LengthUtil.emu(renderHeight) + "\"/>");
				crtDocHelper.write("  </a:xfrm>\n");
				crtDocHelper.write("  <a:prstGeom prst=\"rect\"></a:prstGeom>\n");
				crtDocHelper.write("</pic:spPr>\n");
				crtDocHelper.write("</pic:pic>\n");
				crtDocHelper.write("</a:graphicData>\n");
				crtDocHelper.write("</a:graphic>\n");
				if (crtDocHelper == docHelper) // simple test to differentiate between document.xml writer and header.xml writer
				{
					crtDocHelper.write("</wp:anchor>\n");
				}
				else
				{
					crtDocHelper.write("</wp:inline>\n");
				}
				crtDocHelper.write("</w:drawing>\n");
				crtDocHelper.write("</w:r>"); 

				String url =  getHyperlinkURL(image);

				if (url != null)
				{
					String targetMode = "";
					switch(image.getHyperlinkType())
					{
						case LOCAL_PAGE:
						case LOCAL_ANCHOR:
						{
							crtRelsHelper.exportImageLink(rId, "#"+url.replaceAll("\\W", ""), targetMode);
							break;
						}
						
						case REMOTE_PAGE:
						case REMOTE_ANCHOR:
						case REFERENCE:
						{
							targetMode = " TargetMode=\"External\"";
							crtRelsHelper.exportImageLink(rId, url, targetMode);
							break;
						}
						default:
						{
							break;
						}
					}
				}
				
//				if (startedHyperlink)
//				{
//					endHyperlink(false);
//				}
			}
		}

		crtDocHelper.write("</w:p>");

		tableHelper.getCellHelper().exportFooter();
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
			this.needDimension = imageElement.getScaleImage() != ScaleImageEnum.FILL_FRAME; 
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

//			if (image.isLazy()) //FIXMEDOCX learn how to link images				
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
							ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null
							);

					byte[] imageData = imageRenderer.getData(jasperReportsContext);
					exifOrientation = ImageUtil.getExifOrientation(imageData);
					String fileExtension = JRTypeSniffer.getImageTypeValue(imageData).getFileExtension();
					String imageName = IMAGE_NAME_PREFIX + imageIndex.toString() + (fileExtension == null ? "" : ("." + fileExtension));
					
					docxZip.addEntry(//FIXMEDOCX optimize with a different implementation of entry
						new FileBufferedZipEntry(
							"word/media/" + imageName,
							imageData
							)
						);
					
					// add all images to document rels helper, even if they are only used in background;
					// there are probably not many background images anyway and having them in document rels does not hurt
					relsHelper.exportImage(imageName);

					imagePath = imageName;
					//imagePath = "Pictures/" + imageName;

					if (imageRenderer == renderer)
					{
						//cache imagePath only for true ImageRenderable instances because the wrapping ones render with different width/height each time
						rendererToImagePathMap.put(renderer.getId(), new Pair<>(imagePath, exifOrientation));
					}
				}

				if (crtRelsHelper == headerRelsHelper)
				{
					// header rels helpers keep an internal set of image names for uniqueness,
					// so it is ok to always add the image to them, even if the image was already processed before;
					// on the other had, the document rels helper has the image added only when the image resource is created, so it benefits from
					// the rendererToImagePathMap uniqueness, even though it would thus contain also the background images, which would not be that many anyway.
					headerRelsHelper.exportImage(imagePath);
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
	protected void writeImageMap(String imageMapName, JRPrintHyperlink mainHyperlink, List imageMapAreas)
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

		if (mainHyperlink.getHyperlinkType() != NONE)
		{
			writer.write("  <area shape=\"default\"");
			writeImageAreaHyperlink(mainHyperlink);
			writer.write("/>\n");
		}

		writer.write("</map>\n");
	}


	protected void writeImageAreaCoordinates(JRPrintImageArea area)
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


	protected void writeImageAreaHyperlink(JRPrintHyperlink hyperlink)
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
	 * In deep grids, this is called only for empty frames.
	 */
	protected void exportFrame(DocxTableHelper tableHelper, JRPrintFrame frame, JRExporterGridCell gridCell) throws JRException
	{
		tableHelper.getCellHelper().exportHeader(frame, gridCell);
//		tableHelper.getCellHelper().exportProps(gridCell);

		boolean appendBackcolor =
			frame.getMode() == ModeEnum.OPAQUE
			&& (backcolor == null || frame.getBackcolor().getRGB() != backcolor.getRGB());

		if (appendBackcolor)
		{
			setBackcolor(frame.getBackcolor());
		}

		try
		{
			JRGridLayout layout = ((ElementGridCell) gridCell).getLayout();
			JRPrintElementIndex frameIndex =
				new JRPrintElementIndex(
						reportIndex,
						pageIndex,
						gridCell.getElementAddress()
						);
			exportGrid(layout, frameIndex);
		}
		finally
		{
			if (appendBackcolor)
			{
				restoreBackcolor();
			}
		}
		
		tableHelper.getParagraphHelper().exportEmptyParagraph();
		tableHelper.getCellHelper().exportFooter();
	}


	/**
	 *
	 */
	protected void exportGenericElement(DocxTableHelper tableHelper, JRGenericPrintElement element, JRExporterGridCell gridCell)
	{
		GenericElementDocxHandler handler = (GenericElementDocxHandler) 
		GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
				element.getGenericType(), DOCX_EXPORTER_KEY);

		if (handler != null)
		{
			JRDocxExporterContext exporterContext = new ExporterContext(tableHelper);

			handler.exportElement(exporterContext, element, gridCell);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No DOCX generic element handler for " 
						+ element.getGenericType());
			}
		}
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


	protected boolean startHyperlink(JRPrintHyperlink link, boolean isText)
	{
		String href = getHyperlinkURL(link);

		if (href != null)
		{
//			String id = (String)hyperlinksMap.get(href);
//			if (id == null)
//			{
//				id = "link" + hyperlinksMap.size();
//				hyperlinksMap.put(href, id);
//			}
//			
//			crtDocHelper.write("<w:hyperlink r:id=\"" + id + "\"");
//
//			String target = getHyperlinkTarget(link);//FIXMETARGET
//			if (target != null)
//			{
//				crtDocHelper.write(" tgtFrame=\"" + target + "\"");
//			}
//
//			crtDocHelper.write(">\n");

			crtDocHelper.write("<w:r><w:fldChar w:fldCharType=\"begin\"/></w:r>\n");
			String localType = (HyperlinkTypeEnum.LOCAL_ANCHOR == link.getHyperlinkType() || 
					HyperlinkTypeEnum.LOCAL_PAGE == link.getHyperlinkType()) ? "\\l " : "";
					
			crtDocHelper.write("<w:r><w:instrText xml:space=\"preserve\"> HYPERLINK " + localType +"\"" + JRStringUtil.xmlEncode(href,invalidCharReplacement) + "\"");

			String target = getHyperlinkTarget(link);//FIXMETARGET
			if (target != null)
			{
				crtDocHelper.write(" \\t \"" + target + "\"");
			}

			String tooltip = link.getHyperlinkTooltip(); 
			if (tooltip != null)
			{
				crtDocHelper.write(" \\o \"" + JRStringUtil.xmlEncode(tooltip, invalidCharReplacement) + "\"");
			}

			crtDocHelper.write(" </w:instrText></w:r>\n");
			crtDocHelper.write("<w:r><w:fldChar w:fldCharType=\"separate\"/></w:r>\n");
		}

		return href != null;
	}


	protected String getHyperlinkTarget(JRPrintHyperlink link)
	{
		String target = null;
		switch(link.getHyperlinkTarget())
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

		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(DocxReportConfiguration.PROPERTY_IGNORE_HYPERLINK, link);
		if (ignoreHyperlink == null)
		{
			ignoreHyperlink = getCurrentItemConfiguration().isIgnoreHyperlink();
		}

		if (!ignoreHyperlink)
		{
			JRHyperlinkProducer customHandler = getHyperlinkProducer(link);
			if (customHandler == null)
			{
				switch(link.getHyperlinkType())
				{
					case REFERENCE :
					{
						if (link.getHyperlinkReference() != null)
						{
							href = link.getHyperlinkReference();
						}
						break;
					}
					case LOCAL_ANCHOR :
					{
						if (link.getHyperlinkAnchor() != null)
						{
							href = link.getHyperlinkAnchor().replaceAll("\\W", "");
						}
						break;
					}
					case LOCAL_PAGE :
					{
						if (link.getHyperlinkPage() != null)
						{
							href = JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + link.getHyperlinkPage().toString();
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
							href = link.getHyperlinkReference() + "#" + link.getHyperlinkAnchor();
						}
						break;
					}
					case REMOTE_PAGE :
					{
						if (
							link.getHyperlinkReference() != null &&
							link.getHyperlinkPage() != null
							)
						{
							href = link.getHyperlinkReference() + "#" + JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + link.getHyperlinkPage().toString();
						}
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


	protected void endHyperlink(boolean isText)
	{
//		crtDocHelper.write("</w:hyperlink>\n");
		crtDocHelper.write("<w:r><w:fldChar w:fldCharType=\"end\"/></w:r>\n");
	}

	protected void insertBookmark(String bookmark, BaseHelper helper)
	{
		helper.write("<w:bookmarkStart w:id=\"" + bookmarkIndex);
		helper.write("\" w:name=\"" + (bookmark == null ? null : bookmark.replaceAll("\\W", "")));
		helper.write("\"/><w:bookmarkEnd w:id=\"" + bookmarkIndex++);
		helper.write("\"/>");
	}
	
	@Override
	protected void ensureInput()
	{
		super.ensureInput();

		exporterInput = new PrintPartUnrollExporterInput(exporterInput, getItemConfigurationInterface());

		jasperPrint = exporterInput.getItems().get(0).getJasperPrint();// this is just for the sake of
																		// getCurrentConfiguration() calls made prior to
																		// any setCurrentExporterInputItem() call
	}

	@Override
	protected JRStyledText getStyledText(JRPrintText textElement, boolean setBackcolor)
	{
		return styledTextUtil.getProcessedStyledText(textElement, 
				setBackcolor ? allSelector : noBackcolorSelector, getExporterKey());
	}

	@Override
	public String getExporterKey()
	{
		return DOCX_EXPORTER_KEY;
	}

	@Override
	public String getExporterPropertiesPrefix()
	{
		return DOCX_EXPORTER_PROPERTIES_PREFIX;
	}
}

class BackgroundExporterFilter implements ExporterFilter
{
	ExporterFilter parent;
	boolean include;
	
	public BackgroundExporterFilter(ExporterFilter parent, boolean include)
	{
		this.parent = parent;
		this.include = include;
	}

	@Override
	public boolean isToExport(JRPrintElement element) 
	{
		// normally, we should also test for reportName to be null, but part reports have non-null report name in origin, just like subreports;
		// in case there is a subreport with backround element, one way to avoid its elements going into docx background is to filter them out using docx origin filters
		boolean originMatches = element.getOrigin() == null ? false : BandTypeEnum.BACKGROUND == element.getOrigin().getBandType();
		return ((include && originMatches) || (!include && !originMatches)) && parent.isToExport(element);
	}
}

