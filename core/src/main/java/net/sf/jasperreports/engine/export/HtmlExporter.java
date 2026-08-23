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
package net.sf.jasperreports.engine.export;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.awt.geom.Dimension2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.components.headertoolbar.HeaderToolbarElement;
import net.sf.jasperreports.crosstabs.CrosstabConstants;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAnchor;
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
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintImageArea;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintElementId;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.export.tabulator.Cell;
import net.sf.jasperreports.engine.export.tabulator.CellVisitor;
import net.sf.jasperreports.engine.export.tabulator.Column;
import net.sf.jasperreports.engine.export.tabulator.ElementCell;
import net.sf.jasperreports.engine.export.tabulator.FrameCell;
import net.sf.jasperreports.engine.export.tabulator.LayeredCell;
import net.sf.jasperreports.engine.export.tabulator.NestedTableCell;
import net.sf.jasperreports.engine.export.tabulator.Row;
import net.sf.jasperreports.engine.export.tabulator.SplitCell;
import net.sf.jasperreports.engine.export.tabulator.Table;
import net.sf.jasperreports.engine.export.tabulator.TableCell;
import net.sf.jasperreports.engine.export.tabulator.TableCell.CellType;
import net.sf.jasperreports.engine.export.tabulator.TablePosition;
import net.sf.jasperreports.engine.export.tabulator.Tabulator;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.LineSpacingEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.util.ExifOrientationEnum;
import net.sf.jasperreports.engine.util.HyperlinkData;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.engine.util.StyledTextListWriter;
import net.sf.jasperreports.engine.util.StyledTextWriteContext;
import net.sf.jasperreports.export.AccessibilityUtil;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.HtmlExporterConfiguration;
import net.sf.jasperreports.export.HtmlReportConfiguration;
import net.sf.jasperreports.export.type.AccessibilityTagEnum;
import net.sf.jasperreports.export.type.HtmlBorderCollapseEnum;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.AreaHyperlinksRenderable;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;
import net.sf.jasperreports.renderers.util.RendererUtil;
import net.sf.jasperreports.renderers.util.SvgDataSniffer;
import net.sf.jasperreports.renderers.util.SvgFontProcessor;
import net.sf.jasperreports.search.HitSpanInfo;
import net.sf.jasperreports.search.HitTermInfo;
import net.sf.jasperreports.search.SpansInfo;
import net.sf.jasperreports.util.Base64Util;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class HtmlExporter extends AbstractHtmlExporter<HtmlReportConfiguration, HtmlExporterConfiguration>
{
	private static final Log log = LogFactory.getLog(HtmlExporter.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_INTERNAL_ERROR = "export.html.internal.error";
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_ROTATION_VALUE = "export.html.unexpected.rotation.value";
	private static final String EXCEPTION_MESSAGE_KEY_HREF_NOT_ALLOWED = "export.html.href.not.allowed";
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String HTML_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "html";
	
	/**
	 *
	 */
	public static final String HTML_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.html.";

	/**
	 * Property that provides the value for the <code>class</code> CSS style property to be applied 
	 * to elements in the table generated for the report. The value of this property 
	 * will be used as the value for the <code>class</code> attribute of the <code>&lt;td&gt;</code> tag for the element when exported to HTML and/or 
	 * the <code>class</code> attribute of the <code>&lt;span&gt;</code> or <code>&lt;div&gt;</code> tag for the element, when exported to XHTML/CSS.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_0_1
			)
	public static final String PROPERTY_HTML_CLASS = HTML_EXPORTER_PROPERTIES_PREFIX + "class";

	/**
	 *
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_7_0
			)
	public static final String PROPERTY_HTML_ID = HTML_EXPORTER_PROPERTIES_PREFIX + "id";

	/**
	 * Flag property that enables whitelist filtering of the hyperlink URLs (<code>href</code> attributes)
	 * produced by the HTML exporter.
	 * <p>
	 * When set to <code>true</code>, only the hyperlink URLs that match one of the patterns
	 * configured through {@link #PROPERTY_PREFIX_HREF_ALLOW_PATTERN} are allowed in the output; any non-matching
	 * URL causes the export to fail with a {@link JRRuntimeException}. If the flag is enabled but no allow patterns
	 * are configured, no hyperlink URL is permitted.
	 * </p>
	 * <p>
	 * The filtering applies to all resolved hyperlink URLs, including those generated internally by the exporter,
	 * such as local anchors and local page links.
	 * </p>
	 * <p>
	 * The property can only be set globally and it defaults to <code>false</code>, meaning that no filtering is performed.
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_8,
			valueType = Boolean.class
			)
	public static final String PROPERTY_HREF_FILTER_ENABLED =
			HTML_EXPORTER_PROPERTIES_PREFIX + "href.filter.enabled";

	/**
	 * Property prefix used to define hyperlink URL patterns that are allowed in the output produced by the HTML exporter.
	 * <p>
	 * The property value should be a Java regular expression pattern that is matched against the entire <code>href</code> URL.
	 * Multiple patterns can be configured by using different arbitrary suffixes.
	 * </p>
	 * <p>
	 * The {@link #PROPERTY_HREF_FILTER_ENABLED} flag needs to be set in order for the filtering to apply.
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_8,
			name = "net.sf.jasperreports.export.html.href.allow.pattern.{arbitrary_name}"
			)
	public static final String PROPERTY_PREFIX_HREF_ALLOW_PATTERN =
			HTML_EXPORTER_PROPERTIES_PREFIX + "href.allow.pattern.";

	public static final String REPORT_CONTEXT_PARAMETER_WEB_FONTS = "net.sf.jasperreports.html.webfonts";

	protected JRHyperlinkTargetProducerFactory targetProducerFactory;		
	
	protected Map<String,String> rendererToImagePathMap;
	protected Map<Pair<String, Rectangle>,String> imageMaps;
	protected RenderersCache renderersCache;

	protected Writer writer;
	protected int reportIndex;
	protected int pageIndex;
	
	protected LinkedList<Color> backcolorStack = new LinkedList<>();
	
	protected ExporterFilter tableFilter;
	
	protected int pointerEventsNoneStack = 0;

	private List<HyperlinkData> hyperlinksData = new ArrayList<>();
	private boolean hrefFilterEnabled;
	private List<Pattern> hrefAllowPatterns;
	
	private boolean defaultIndentFirstLine;
	private boolean defaultJustifyLastLine;

	private float currentZoomRatio;
	private String currentSizeUnit;

	public HtmlExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	public HtmlExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);

		exporterContext = new ExporterContext();
	}
	
	@Override
	public String getExporterKey()
	{
		return HTML_EXPORTER_KEY;
	}

	@Override
	public String getExporterPropertiesPrefix()
	{
		return HTML_EXPORTER_PROPERTIES_PREFIX;
	}

	@Override
	public void exportReport() throws JRException
	{
		/*   */
		ensureJasperReportsContext();
		ensureInput();

		rendererToImagePathMap = new HashMap<>();
		imageMaps = new HashMap<>();
		renderersCache = new RenderersCache(getJasperReportsContext());

		fontsToProcess = new HashMap<String, HtmlFontFamily>();
		
		//FIXMENOW check all exporter properties that are supposed to work at report level
		
		initExport();
		
		ensureOutput();

		writer = getExporterOutput().getWriter();

		try
		{
			exportReportToWriter();
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_OUTPUT_WRITER_ERROR,
					new Object[]{jasperPrint.getName()}, 
					e);
		}
		finally
		{
			getExporterOutput().close();
			resetExportContext();
		}
	}

	
	@Override
	protected Class<HtmlExporterConfiguration> getConfigurationInterface()
	{
		return HtmlExporterConfiguration.class;
	}

	
	@Override
	protected Class<HtmlReportConfiguration> getItemConfigurationInterface()
	{
		return HtmlReportConfiguration.class;
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

		HtmlReportConfiguration configuration = getCurrentItemConfiguration();
		
		if (configuration.isRemoveEmptySpaceBetweenRows())
		{
			log.info("Removing empty space between rows not supported");
		}

		// this is the filter used to create the table, taking in consideration unhandled generic elements
		tableFilter = new GenericElementsFilterDecorator(jasperReportsContext, HTML_EXPORTER_KEY, filter);
		
		defaultIndentFirstLine = propertiesUtil.getBooleanProperty(jasperPrint, JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE, true);
		defaultJustifyLastLine = propertiesUtil.getBooleanProperty(jasperPrint, JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE, false);
		
		currentSizeUnit = configuration.getSizeUnit().getName();
		
		Float zoomRatio = configuration.getZoomRatio();
		if (zoomRatio != null)
		{
			if (zoomRatio <= 0)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INVALID_ZOOM_RATIO,  
						new Object[]{currentZoomRatio} 
						);
			}
			currentZoomRatio = zoomRatio;
		}
		else
		{
			currentZoomRatio = DEFAULT_ZOOM;
		}
	}
	

	@Override
	protected void setJasperReportsContext(JasperReportsContext jasperReportsContext)
	{
		super.setJasperReportsContext(jasperReportsContext);
		
		targetProducerFactory = new DefaultHyperlinkTargetProducerFactory(jasperReportsContext);

		loadHrefAllowPatterns();
	}

	private void loadHrefAllowPatterns()
	{
		hrefFilterEnabled = propertiesUtil.getBooleanProperty(PROPERTY_HREF_FILTER_ENABLED);
		if (!hrefFilterEnabled)
		{
			hrefAllowPatterns = null;
			return;
		}

		List<JRPropertiesUtil.PropertySuffix> properties =
				propertiesUtil.getProperties(PROPERTY_PREFIX_HREF_ALLOW_PATTERN);
		if (properties.isEmpty())
		{
			hrefAllowPatterns = null;
		}
		else
		{
			hrefAllowPatterns = new ArrayList<>(properties.size());
			for (JRPropertiesUtil.PropertySuffix propertySuffix : properties)
			{
				String patternValue = propertySuffix.getValue();
				if (patternValue != null && patternValue.trim().length() > 0)
				{
					hrefAllowPatterns.add(Pattern.compile(patternValue));
				}
			}
			if (hrefAllowPatterns.isEmpty())
			{
				hrefAllowPatterns = null;
			}
		}
	}

	private void checkHrefAllowed(String href)
	{
		if (!hrefFilterEnabled || href == null)
		{
			return;
		}

		if (hrefAllowPatterns != null)
		{
			for (Pattern pattern : hrefAllowPatterns)
			{
				if (pattern.matcher(href).matches())
				{
					return;
				}
			}
		}
		throw new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_HREF_NOT_ALLOWED,
				new Object[]{href});
	}

	
	protected void exportReportToWriter() throws JRException, IOException
	{
		HtmlExporterConfiguration configuration = getCurrentConfiguration(); 
		String htmlHeader = configuration.getHtmlHeader();
		String betweenPagesHtml = configuration.getBetweenPagesHtml();
		String htmlFooter = configuration.getHtmlFooter();
		boolean flushOutput = configuration.isFlushOutput();//FIXMEEXPORT maybe move flush flag to output

		if (htmlHeader == null)
		{
			writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
			writer.write("<html>\n");
			writer.write("<head>\n");
			writer.write("  <title></title>\n");
			writer.write("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=");
			writer.write(JRStringUtil.encodeXmlAttribute(getExporterOutput().getEncoding()));
			writer.write("\"/>\n");
			writer.write("  <style type=\"text/css\">\n");
			writer.write("    a {text-decoration: none}\n");
			writer.write("  </style>\n");
			writer.write("</head>\n");
			writer.write("<body text=\"#000000\" link=\"#000000\" alink=\"#000000\" vlink=\"#000000\">\n");
			writer.write("<table role=\"none\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
			writer.write("<tr><td width=\"50%\">&nbsp;</td><td align=\"center\">\n");
			writer.write("\n");
		}
		else
		{
			writer.write(htmlHeader);
		}

		List<ExporterInputItem> items = exporterInput.getItems();
		
		for (reportIndex = 0; reportIndex < items.size(); reportIndex++)
		{
			ExporterInputItem item = items.get(reportIndex);

			setCurrentExporterInputItem(item);
			
			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				PageRange pageRange = getPageRange();
				int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
				int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

				JRPrintPage page = null;
				for (pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
				{
					checkInterrupted();

					page = pages.get(pageIndex);

					/*   */
					exportPage(page);

					if (reportIndex < items.size() - 1 || pageIndex < endPageIndex)
					{
						if (betweenPagesHtml == null)
						{
							writer.write("<br/>\n<br/>\n");
						}
						else
						{
							writer.write(betweenPagesHtml);
						}
					}

					writer.write("\n");
				}
			}
		}

		ReportContext reportContext = getReportContext();
		if (fontsToProcess != null && fontsToProcess.size() > 0)// when no resourceHandler, fonts are not processed 
		{
			if (reportContext == null) 
			{
				HtmlResourceHandler fontHandler = getExporterOutput().getFontHandler();
				if (fontHandler == null)
				{
					HtmlResourceHandler resourceHandler = getExporterOutput().getResourceHandler();
					fontHandler = resourceHandler;
				}

				for (HtmlFontFamily htmlFontFamily : fontsToProcess.values())
				{
					// the fontHandler is used only here, to point to the URL that generates the dynamic CSS for static HTML export in server environments;
					// in non server environments, the static HTML saved to file system uses the static resource handler to point to the font CSS file, 
					// which was also saved using a static resource handler
					String cssResourceName = HtmlFontUtil.getFontCSSResourceName(htmlFontFamily);
					writer.write("<link class=\"jrWebFont\" rel=\"stylesheet\" href=\"" + JRStringUtil.encodeXmlAttribute(fontHandler.getResourcePath(cssResourceName)) + "\">\n");
				}
				
				// generate script tag on static export only
				writer.write("<!--[if IE]>\n");
				writer.write("<script>\n");
				writer.write("var links = document.querySelectorAll('link.jrWebFont');\n");
				writer.write("setTimeout(function(){ if (links) { for (var i = 0; i < links.length; i++) { links.item(i).href = links.item(i).href; } } }, 0);\n");
				writer.write("</script>\n");
				writer.write("<![endif]-->\n");
			}
			else
			{
				reportContext.setParameterValue(REPORT_CONTEXT_PARAMETER_WEB_FONTS, fontsToProcess);
			}
		}

		// place hyperlinksData on reportContext
		if (hyperlinksData.size() > 0) 
		{
			//for sure reportContext is not null, because otherwise there would be no item in the hyperilnkData
			reportContext.setParameterValue("net.sf.jasperreports.html.hyperlinks", hyperlinksData);
		}

		if (getCurrentItemConfiguration().isIncludeElementUUID())
		{
			reportContext.setParameterValue("net.sf.jasperreports.html.clickable.elements", Boolean.TRUE);
		}
		
		if (htmlFooter == null)
		{
			writer.write("</td><td width=\"50%\">&nbsp;</td></tr>\n");
			writer.write("</table>\n");
			writer.write("</body>\n");
			writer.write("</html>\n");
		}
		else
		{
			writer.write(htmlFooter);
		}

		if (flushOutput)
		{
			writer.flush();
		}
	}
	
	protected void exportPage(JRPrintPage page) throws IOException
	{
		HtmlReportConfiguration configuration = getCurrentItemConfiguration();

		Tabulator tabulator = new Tabulator(tableFilter, page.getElements(), configuration.isAccessibleHtml());
		tabulator.tabulate(getOffsetX(), getOffsetY());
		
		boolean isIgnorePageMargins = configuration.isIgnorePageMargins();
		if (!isIgnorePageMargins)
		{
			PrintPageFormat pageFormat = jasperPrint.getPageFormat(pageIndex);
			tabulator.addMargins(pageFormat.getPageWidth(), pageFormat.getPageHeight());
		}
		
		Table table = tabulator.getTable();
		
		boolean isWhitePageBackground = configuration.isWhitePageBackground();
		if (isWhitePageBackground)
		{
			setBackcolor(Color.white);
		}
		
		CellElementVisitor elementVisitor = new CellElementVisitor();
		TableVisitor tableVisitor = new TableVisitor(tabulator, elementVisitor);
		
		exportTable(tableVisitor, table, isWhitePageBackground, true);
		
		if (isWhitePageBackground)
		{
			restoreBackcolor();
		}
		
		JRExportProgressMonitor progressMonitor = configuration.getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}

	public void exportElements(List<JRPrintElement> elements) throws IOException
	{
		Tabulator tabulator = new Tabulator(tableFilter, elements, getCurrentItemConfiguration().isAccessibleHtml());
		tabulator.tabulate();
		
		Table table = tabulator.getTable();
		
		CellElementVisitor elementVisitor = new CellElementVisitor();
		TableVisitor tableVisitor = new TableVisitor(tabulator, elementVisitor);
		
		exportTable(tableVisitor, table, false, false);
	}

	protected void exportTable(TableVisitor tableVisitor, Table table, boolean whiteBackground, boolean isMainReportTable) throws IOException
	{
		SortedSet<Column> columns = table.getColumns().getUserEntries();
		SortedSet<Row> rows = table.getRows().getUserEntries();
		if (columns.isEmpty() || rows.isEmpty())
		{
			// TODO lucianc empty page
			return;
		}
		
		String tableId = null;
		
		if (isMainReportTable)
		{
			int totalWidth = columns.last().getEndCoord() - columns.first().getStartCoord();
		 	int totalHeight = rows.last().getEndCoord() - rows.first().getStartCoord();
		 	tableId = JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (pageIndex + 1);
			writer.write("<table id=\"");
			writer.write(tableId);
			writer.write("\" role=\"none\" class=\"jrPage\" data-jr-height=\"");
			writer.write(String.valueOf(totalHeight)); // no need for size unit
			writer.write("\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"empty-cells: show; width: ");
			writer.write(toSizeUnit(totalWidth));
			writer.write(";");
		}
		else
		{
			writer.write("<table");
			if (table.getRole() != null)
			{
				writer.write(" role=\"");
				writer.write(table.getRole());
				writer.write("\"");
			}
			writer.write(" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"empty-cells: show; width: 100%;");
		}
		
		HtmlBorderCollapseEnum borderCollapse = getCurrentItemConfiguration().getBorderCollapseValue();
		if (borderCollapse != null)
		{
			writer.write(" border-collapse: ");
			writer.write(borderCollapse.getName());
			writer.write(";");
		}
		
		if (whiteBackground)
		{
			writer.write(" background-color: white;");
		}
		writer.write("\">\n");

		if (isMainReportTable)
		{
			writer.write("<style type=\"text/css\">\n");
			writer.write("  #" + tableId + " th {font-weight: normal;}\n");
			writer.write("  #" + tableId + " ul {list-style-type: disc; padding-inline-start: 40px; margin: 0px;}\n");
			writer.write("  #" + tableId + " ol {list-style-type: decimal; padding-inline-start: 40px; margin: 0px;}\n");
			writer.write("</style>\n");
		}
		
		// TODO lucianc check whether we can use the first row for setting col widths
		writer.write("<tr role=\"none\" valign=\"top\" style=\"height:0\">\n");
		for (Column col : columns)
		{
			writer.write("<td style=\"width:");
			writer.write(toSizeUnit(col.getExtent()));
			writer.write("\"></td>\n");
		}
		writer.write("</tr>\n");
		
		for (Row row : rows)
		{
			writer.write("<tr valign=\"top\" style=\"height:");
			writer.write(toSizeUnit(row.getExtent()));
			writer.write("\">\n");

			int emptySpan = 0;
			for (Column col : columns)
			{
				Cell cell = row.getCell(col);
				if (cell == null)
				{
					++emptySpan;
				}
				else
				{
					if (emptySpan > 0)
					{
						writeEmptyCell(emptySpan, 1);
					}
					emptySpan = 0;

					TablePosition position = new TablePosition(table, col, row);
					cell.accept(tableVisitor, position);
				}
			}
			if (emptySpan > 0)
			{
				writeEmptyCell(emptySpan, 1);
			}
			
			writer.write("</tr>\n");
		}
		
		writer.write("</table>\n");
	}

	protected void writeText(JRPrintText text, TableCell cell)
			throws IOException
	{
		JRStyledText styledText = getStyledText(text);
		int textLength = styledText == null ? 0 : styledText.length();
		
		startCell(text, cell);
		
		if (text.getRunDirection() == RunDirectionEnum.RTL)
		{
			writer.write(" dir=\"rtl\"");
		}

		StringBuilder styleBuffer = new StringBuilder();

		String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;

		switch (text.getVerticalTextAlign())
		{
			case BOTTOM :
			{
				verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
				break;
			}
			case MIDDLE :
			{
				verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
				break;
			}
			case TOP :
			case JUSTIFIED :
			default :
			{
				verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
			}
		}

		appendElementCellGenericStyle(cell, styleBuffer);
		appendBackcolorStyle(cell, styleBuffer);
		appendBorderStyle(cell.getBox(), styleBuffer);
		appendPaddingStyle(text.getLineBox(), styleBuffer);

		String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
		if (textLength > 0)
		{
			switch (text.getHorizontalTextAlign())
			{
				case RIGHT :
				{
					horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
					break;
				}
				case CENTER :
				{
					horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
					break;
				}
				case JUSTIFIED :
				{
					horizontalAlignment = CSS_TEXT_ALIGN_JUSTIFY;
					break;
				}
				case LEFT :
				default :
				{
					horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
				}
			}
		}

		if (getCurrentItemConfiguration().isWrapBreakWord())
		{
			styleBuffer.append("width: " + toSizeUnit(text.getWidth()) + "; ");
			styleBuffer.append("word-wrap: break-word; ");
		}
		
		if (text.getLineBreakOffsets() != null)
		{
			//if we have line breaks saved in the text, set nowrap so that
			//the text only wraps at the explicit positions
			styleBuffer.append("white-space: nowrap; ");
		}
		
		styleBuffer.append("text-indent: " + text.getParagraph().getFirstLineIndent() + "px; ");

		String rotation = null;
		StringBuilder spanStyleBuffer = new StringBuilder();
		StringBuilder divStyleBuffer = new StringBuilder();
		if (text.getRotation() == RotationEnum.NONE)
		{
			if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP))
			{
				styleBuffer.append(" vertical-align: ");
				styleBuffer.append(verticalAlignment);
				styleBuffer.append(";");
			}

			//writing text align every time even when it's left
			//because IE8 with transitional defaults to center 
			styleBuffer.append("text-align: ");
			styleBuffer.append(horizontalAlignment);
			styleBuffer.append(";");
		}
		else
		{
			rotation = setRotationStyles(text, horizontalAlignment, 
					spanStyleBuffer, divStyleBuffer);
		}
		
		writeStyle(styleBuffer);
		
		finishStartCell();
		
		if (text.getAnchorName() != null)
		{
			writer.write("<a id=\"");
			writer.write(JRStringUtil.encodeXmlAttribute(text.getAnchorName()));
			writer.write("\"></a>"); // <a> tags must have content (be closed with separate closing tag), otherwise browsers will make them wrap around the next tag
		}
		
		if (text.getBookmarkLevel() != JRAnchor.NO_BOOKMARK)
		{
			writer.write("<a id=\"");
			writer.write(JR_BOOKMARK_ANCHOR_PREFIX + reportIndex + "_" + pageIndex + "_" + cell.getElementAddress());
			writer.write("\"></a>"); // <a> tags must have content (be closed with separate closing tag), otherwise browsers will make them wrap around the next tag
		}

		if (rotation != null)
		{
			writer.write("<div style=\"position: relative; overflow: hidden; ");
			writer.write(divStyleBuffer.toString());
			writer.write("\">\n");
			writer.write("<span class=\"rotated\" data-rotation=\"");
			writer.write(rotation);
			writer.write("\" style=\"position: absolute; display: table; ");
			writer.write(spanStyleBuffer.toString());
			writer.write("\">");
			writer.write("<span style=\"display: table-cell; vertical-align:"); //display:table-cell conflicts with overflow: hidden;
			writer.write(verticalAlignment);
			writer.write(";\">");
		}
		
		AccessibilityTagEnum headingTag = startHeading(text);
		boolean hyperlinkStarted = startHyperlink(text);

		if (textLength > 0)
		{
			//only use text tooltip when no hyperlink present
//			String textTooltip = hyperlinkStarted ? null : text.getHyperlinkTooltip();
			exportStyledText(text, styledText, text.getHyperlinkTooltip(), hyperlinkStarted);
		}

		if (hyperlinkStarted)
		{
			endHyperlink();
		}

		endHeading(headingTag);
		
		if (rotation != null)
		{
			writer.write("</span></span></div>");
		}

		endCell(cell.getCellType());
	}

	protected String setRotationStyles(JRPrintText text, String horizontalAlignment, 
			StringBuilder spanStyleBuffer, StringBuilder divStyleBuffer)
	{
		String rotation;
		int textWidth = text.getWidth() - text.getLineBox().getLeftPadding() - text.getLineBox().getRightPadding();
		int textHeight = text.getHeight() - text.getLineBox().getTopPadding() - text.getLineBox().getBottomPadding();
		int rotatedWidth;
		int rotatedHeight;
		
		int rotationIE;
		int rotationAngle;
		int translateX;
		int translateY;
		switch (text.getRotation())
		{
			case LEFT : 
			{
				translateX = - (textHeight - textWidth) / 2;
				translateY = (textHeight - textWidth) / 2;
				rotatedWidth = textHeight;
				rotatedHeight = textWidth;
				rotationIE = 3;
				rotationAngle = -90;
				rotation = "left";
				break;
			}
			case RIGHT : 
			{
				translateX = - (textHeight - textWidth) / 2;
				translateY = (textHeight - textWidth) / 2;
				rotatedWidth = textHeight;
				rotatedHeight = textWidth;
				rotationIE = 1;
				rotationAngle = 90;
				rotation = "right";
				break;
			}
			case UPSIDE_DOWN : 
			{
				translateX = 0;
				translateY = 0;
				rotatedWidth = textWidth;
				rotatedHeight = textHeight;
				rotationIE = 2;
				rotationAngle = 180;
				rotation = "upsideDown";
				break;
			}
			case NONE :
			default :
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNEXPECTED_ROTATION_VALUE,  
						new Object[]{text.getRotation()}
						);
			}
		}

		appendSizeStyle(textWidth, textHeight, divStyleBuffer);
		appendSizeStyle(rotatedWidth, rotatedHeight, spanStyleBuffer);

		spanStyleBuffer.append("text-align: ");
		spanStyleBuffer.append(horizontalAlignment);
		spanStyleBuffer.append(";");
		
		spanStyleBuffer.append("-webkit-transform: translate(" + translateX + "px," + translateY + "px) ");
		spanStyleBuffer.append("rotate(" + rotationAngle + "deg); ");
		spanStyleBuffer.append("-moz-transform: translate(" + translateX + "px," + translateY + "px) ");
		spanStyleBuffer.append("rotate(" + rotationAngle + "deg); ");
		spanStyleBuffer.append("-ms-transform: translate(" + translateX + "px," + translateY + "px) ");
		spanStyleBuffer.append("rotate(" + rotationAngle + "deg); ");
		spanStyleBuffer.append("-o-transform: translate(" + translateX + "px," + translateY + "px) ");
		spanStyleBuffer.append("rotate(" + rotationAngle + "deg); ");
		spanStyleBuffer.append("filter: progid:DXImageTransform.Microsoft.BasicImage(rotation=" + rotationIE + "); ");
		return rotation;
	}

	protected void appendSizeStyle(int width, int height, StringBuilder styleBuffer)
	{
		styleBuffer.append("width:");
		styleBuffer.append(toSizeUnit(width));
		styleBuffer.append(";");

		styleBuffer.append("height:");
		styleBuffer.append(toSizeUnit(height));
		styleBuffer.append(";");
	}

	public void writeImage(JRPrintImage image, TableCell cell)
			throws IOException, JRException
	{
		startCell(image, cell);

		int availableImageWidth = image.getWidth() - image.getLineBox().getLeftPadding() - image.getLineBox().getRightPadding();
		if (availableImageWidth < 0)
		{
			availableImageWidth = 0;
		}
	
		int availableImageHeight = image.getHeight() - image.getLineBox().getTopPadding() - image.getLineBox().getBottomPadding();
		if (availableImageHeight < 0)
		{
			availableImageHeight = 0;
		}

		String horizontalAlignment = getImageHorizontalAlignmentStyle(image);
		String verticalAlignment = getImageVerticalAlignmentStyle(image);

		StringBuilder styleBuffer = new StringBuilder();
		ScaleImageEnum scaleImage = image.getScaleImage();
		if (scaleImage != ScaleImageEnum.CLIP)
		{
			// clipped images are absolutely positioned within a div
			if (!horizontalAlignment.equals(CSS_TEXT_ALIGN_LEFT))
			{
				styleBuffer.append("text-align: ");
				styleBuffer.append(horizontalAlignment);
				styleBuffer.append(";");
			}

			if (!verticalAlignment.equals(HTML_VERTICAL_ALIGN_TOP))
			{
				styleBuffer.append(" vertical-align: ");
				styleBuffer.append(verticalAlignment);
				styleBuffer.append(";");
			}
		}
		
		RotationEnum rotation = image.getRotation();
		
		Renderable renderer = image.getRenderer();

		boolean isLazy = RendererUtil.isLazy(renderer);
		
		if (
			isLazy
			|| (scaleImage == ScaleImageEnum.CLIP && availableImageHeight > 0)
			|| rotation != RotationEnum.NONE
			)
		{
			// some browsers need td height so that height: 100% works on the div used for clipped images.
			// we're using the height without paddings because that's closest to the HTML size model.
			styleBuffer.append("height: ");
			styleBuffer.append(toSizeUnit(availableImageHeight));
			styleBuffer.append("; ");
		}

		appendElementCellGenericStyle(cell, styleBuffer);
		appendBackcolorStyle(cell, styleBuffer);
		
		boolean addedToStyle = appendBorderStyle(cell.getBox(), styleBuffer);
		if (!addedToStyle)
		{
			appendPen(
				styleBuffer,
				image.getLinePen(),
				null
				);
		}

		appendPaddingStyle(image.getLineBox(), styleBuffer);

		writeStyle(styleBuffer);

		finishStartCell();

		if (image.getAnchorName() != null)
		{
			writer.write("<a id=\"");
			writer.write(JRStringUtil.encodeXmlAttribute(image.getAnchorName()));
			writer.write("\"></a>"); // <a> tags must have content (be closed with separate closing tag), otherwise browsers will make them wrap around the next tag
		}
		
		if (image.getBookmarkLevel() != JRAnchor.NO_BOOKMARK)
		{
			writer.write("<a id=\"");
			writer.write(JR_BOOKMARK_ANCHOR_PREFIX + reportIndex + "_" + pageIndex + "_" + cell.getElementAddress());
			writer.write("\"></a>"); // <a> tags must have content (be closed with separate closing tag), otherwise browsers will make them wrap around the next tag
		}
		
		if (renderer != null)
		{
			boolean useBackgroundImage = 
				((isLazy 
					&& ((scaleImage == ScaleImageEnum.RETAIN_SHAPE || scaleImage == ScaleImageEnum.REAL_HEIGHT || scaleImage == ScaleImageEnum.REAL_SIZE) 
							|| !(image.getHorizontalImageAlign() == HorizontalImageAlignEnum.LEFT && image.getVerticalImageAlign() == VerticalImageAlignEnum.TOP)))
				|| rotation != RotationEnum.NONE)
				&& isUseBackgroundImageToAlign(image);
					
			boolean useDiv = (scaleImage == ScaleImageEnum.CLIP || useBackgroundImage);
			if (useDiv)
			{
				writer.write("<div style=\"width: 100%; height: 100%; position: relative; overflow: hidden;\">\n");
			}
			
			boolean hasAreaHyperlinks = 
				renderer instanceof AreaHyperlinksRenderable
				&& ((AreaHyperlinksRenderable)renderer).hasImageAreaHyperlinks();

			boolean hasHyperlinks = false;

			boolean hyperlinkStarted;
			if (hasAreaHyperlinks)
			{
				hyperlinkStarted = false;
				hasHyperlinks = true;
			}
			else
			{
				hyperlinkStarted = startHyperlink(image);
				hasHyperlinks = hyperlinkStarted;
			}
			
			String imageMapName = null;
			List<JRPrintImageAreaHyperlink> imageMapAreas = null;

			if (hasAreaHyperlinks)
			{
				Rectangle renderingArea = new Rectangle(image.getWidth(), image.getHeight());
				
				if (renderer instanceof DataRenderable)
				{
					imageMapName = imageMaps.get(new Pair<String, Rectangle>(renderer.getId(), renderingArea));
				}

				if (imageMapName == null)
				{
					Renderable originalRenderer = image.getRenderer();
					imageMapName = "map_" + getElementIndex(cell).toString() + "-" + originalRenderer.getId();//use renderer.getId()?
					imageMapAreas = ((AreaHyperlinksRenderable) originalRenderer).getImageAreaHyperlinks(renderingArea);//FIXMECHART
					
					if (renderer instanceof DataRenderable)
					{
						imageMaps.put(new Pair<>(renderer.getId(), renderingArea), imageMapName);
					}
				}
			}

			InternalImageProcessor imageProcessor = 
				new InternalImageProcessor(
					image,
					isLazy,
					!useBackgroundImage && scaleImage != ScaleImageEnum.FILL_FRAME && !isLazy,
					cell,
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
				if (useBackgroundImage)
				{
					int width = availableImageWidth;
					int height = availableImageHeight;
					int translateX = 0;
					int translateY = 0;
					int angle = 0;
					String backgroundSize = null;
					
					switch (rotation)
					{
						case LEFT:
							width = availableImageHeight;
							height = availableImageWidth;
							translateX = (availableImageWidth - availableImageHeight) / 2;
							translateY = - translateX;
							angle = -90;
							break;
						case RIGHT:
							width = availableImageHeight;
							height = availableImageWidth;
							translateX = (availableImageWidth - availableImageHeight) / 2;
							translateY = - translateX;
							angle = 90;
							break;
						case UPSIDE_DOWN:
							angle = 180;
							break;
						case NONE:
						default:
							break;
					}

					switch (scaleImage)
					{
						case FILL_FRAME :
						{
							backgroundSize = "100% 100%";
							break;
						}
						case CLIP :
						{
							backgroundSize = "auto";
							break;
						}
						case RETAIN_SHAPE :
						default :
						{
							backgroundSize = "contain";
						}
					}
					
					writer.write("<div style=\"width: " + width + "px; height: " + height + "px; position: absolute; overflow: hidden; "
						+ "left: " + translateX + "px;top: " + translateY + "px; transform: rotate(" + angle + "deg);\">");
					writer.write("<div style=\"width: 100%; height: 100%; background-image: url('");
					String imagePath = imageProcessorResult.imageSource;
					if (imagePath != null)
					{
						writer.write(JRStringUtil.encodeXmlAttribute(imagePath));
					}
					writer.write(
						"'); background-repeat: no-repeat; background-position: " 
						+ horizontalAlignment + " " 
						+ (image.getVerticalImageAlign() == VerticalImageAlignEnum.MIDDLE ? "center" : verticalAlignment) 
						+ ";background-size: " + backgroundSize + ";\"></div>");
					writer.write("</div>");
				}
				else if (imageProcessorResult.isEmbededSvgData)
				{
					writer.write("<svg");

					switch (scaleImage)
					{
						case FILL_FRAME :
						{
							Dimension2D dimension = imageProcessorResult.dimension;
							if (dimension != null)
							{
								writer.write(" viewBox=\"0 0 ");
								writer.write(String.valueOf(dimension.getWidth()));
								writer.write(" ");
								writer.write(String.valueOf(dimension.getHeight()));
								writer.write("\"");
							}
				
							writer.write(" width=\"");
							writer.write(String.valueOf(availableImageWidth));
							writer.write("\"");
							writer.write(" height=\"");
							writer.write(String.valueOf(availableImageHeight));
							writer.write("\"");
							writer.write(" preserveAspectRatio=\"none\"");

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
	
								writer.write(" viewBox=\"");
								writer.write(String.valueOf((int)(ImageUtil.getXAlignFactor(image) * (normalWidth - availableImageWidth))));
								writer.write(" ");
								writer.write(String.valueOf((int)(ImageUtil.getYAlignFactor(image) * (normalHeight - availableImageHeight))));
								writer.write(" ");
								writer.write(String.valueOf(availableImageWidth));
								writer.write(" ");
								writer.write(String.valueOf(availableImageHeight));
								writer.write("\"");
							}

							writer.write(" width=\"");
							writer.write(String.valueOf(availableImageWidth));
							writer.write("\"");
							writer.write(" height=\"");
							writer.write(String.valueOf(availableImageHeight));
							writer.write("\"");

							break;
						}
						case RETAIN_SHAPE :
						default :
						{
							//considering the IF above, if we get here, then for sure isLazy() is false, so we can ask the renderer for its dimension
							if (availableImageHeight > 0)
							{
								double normalWidth = availableImageWidth;
								double normalHeight = availableImageHeight;

								Dimension2D dimension = imageProcessorResult.dimension;
								if (dimension != null)
								{
									normalWidth = dimension.getWidth();
									normalHeight = dimension.getHeight();

									writer.write(" viewBox=\"0 0 ");
									writer.write(String.valueOf(normalWidth));
									writer.write(" ");
									writer.write(String.valueOf(normalHeight));
									writer.write("\"");
								}
								
								double ratio = normalWidth / normalHeight;
				
								if ( ratio > (double)availableImageWidth / (double)availableImageHeight )
								{
									writer.write(" width=\"");
									writer.write(String.valueOf(availableImageWidth));
									writer.write("\"");
								}
								else
								{
									writer.write(" height=\"");
									writer.write(String.valueOf(availableImageHeight));
									writer.write("\"");
								}
							}
						}
					}

					writer.write("><g>\n");
					writer.write(imageProcessorResult.imageSource);
					writer.write("</g></svg>");
				}
				else
				{
					writer.write("<img");
					writer.write(" src=\"");
					String imagePath = imageProcessorResult.imageSource;
					if (imagePath != null)
					{
						writer.write(JRStringUtil.encodeXmlAttribute(imagePath));
					}
					writer.write("\"");
				
					switch (scaleImage)
					{
						case FILL_FRAME :
						{
							writer.write(" style=\"width: ");
							writer.write(toSizeUnit(availableImageWidth));
							writer.write("; height: ");
							writer.write(toSizeUnit(availableImageHeight));
							writer.write("\"");
				
							break;
						}
						case CLIP :
						{
							int positionLeft;
							int positionTop;
							
							HorizontalImageAlignEnum horizontalAlign = image.getHorizontalImageAlign();
							VerticalImageAlignEnum verticalAlign = image.getVerticalImageAlign();
							if (  
								isLazy
								|| (horizontalAlign == HorizontalImageAlignEnum.LEFT && verticalAlign == VerticalImageAlignEnum.TOP)
								)
							{
								// no need to compute anything
								positionLeft = 0;
								positionTop = 0;
							}
							else
							{
								double normalWidth = availableImageWidth;
								double normalHeight = availableImageHeight;

								Dimension2D dimension = imageProcessorResult.dimension;
								if (dimension != null)
								{
									normalWidth = dimension.getWidth();
									normalHeight = dimension.getHeight();
								}
								
								ExifOrientationEnum exifOrientation = ExifOrientationEnum.NORMAL;
								
								DataRenderable dataRenderable = renderer instanceof DataRenderable ? (DataRenderable)renderer : null;
								if (dataRenderable != null)
								{
									exifOrientation = ImageUtil.getExifOrientation(dataRenderable.getData(getJasperReportsContext()));
								}
								
								if (
									ExifOrientationEnum.LEFT == exifOrientation
									|| ExifOrientationEnum.RIGHT == exifOrientation
									)
								{
									double t = normalWidth;
									normalWidth = normalHeight;
									normalHeight = t;
								}

								// these calculations assume that the image td does not stretch due to other cells.
								// when that happens, the image will not be properly aligned.
								positionLeft = (int) (ImageUtil.getXAlignFactor(horizontalAlign) * (availableImageWidth - normalWidth));
								positionTop = (int) (ImageUtil.getYAlignFactor(verticalAlign) * (availableImageHeight - normalHeight));
							}
							
							writer.write(" style=\"position: absolute; left:");
							writer.write(toSizeUnit(positionLeft));
							writer.write("; top: ");
							writer.write(toSizeUnit(positionTop));
							// not setting width, height and clip as it doesn't seem needed plus it fixes clip for lazy images
							writer.write(";\"");

							break;
						}
						case RETAIN_SHAPE :
						default :
						{
							//considering the IF above, if we get here, then for sure isLazy() is false, so we can ask the renderer for its dimension
							if (availableImageHeight > 0)
							{
								double normalWidth = availableImageWidth;
								double normalHeight = availableImageHeight;

								Dimension2D dimension = imageProcessorResult.dimension;
								if (dimension != null)
								{
									normalWidth = dimension.getWidth();
									normalHeight = dimension.getHeight();
								}
								
								ExifOrientationEnum exifOrientation = ExifOrientationEnum.NORMAL;
								
								DataRenderable dataRenderable = renderer instanceof DataRenderable ? (DataRenderable)renderer : null;
								if (dataRenderable != null)
								{
									exifOrientation = ImageUtil.getExifOrientation(dataRenderable.getData(getJasperReportsContext()));
								}
								
								if (
									ExifOrientationEnum.LEFT == exifOrientation
									|| ExifOrientationEnum.RIGHT == exifOrientation
									)
								{
									double t = normalWidth;
									normalWidth = normalHeight;
									normalHeight = t;
								}

								double ratio = normalWidth / normalHeight;
				
								if ( ratio > (double)availableImageWidth / (double)availableImageHeight )
								{
									writer.write(" style=\"width: ");
									writer.write(toSizeUnit(availableImageWidth));
									writer.write("\"");
								}
								else
								{
									writer.write(" style=\"height: ");
									writer.write(toSizeUnit(availableImageHeight));
									writer.write("\"");
								}
							}
						}
					}
					
					if (imageMapName != null)
					{
						writer.write(" usemap=\"#" + imageMapName + "\"");
					}
					
					if (hasHyperlinks)
					{
						writer.write(" border=\"0\"");
					}
					
					if (image.getHyperlinkTooltip() != null)
					{
						String tooltip = JRStringUtil.encodeXmlAttribute(image.getHyperlinkTooltip()); 
						writer.write(" alt=\"");
						writer.write(tooltip);
						writer.write("\"");
						writer.write(" title=\"");
						writer.write(tooltip);
						writer.write("\"");
					}
					else
					{
						writer.write(" alt=\"\""); // screen readers do not read images with empty alt attribute, which are considered just decorations
					}
					
					writer.write("/>");
				}
			}

			if (hyperlinkStarted)
			{
				endHyperlink();
			}
			
			if (useDiv)
			{
				writer.write("</div>");
			}
			
			if (imageMapAreas != null)
			{
				writer.write("\n");
				writeImageMap(imageMapName, image, imageMapAreas);
			}
		}
		
		endCell(cell.getCellType());
	}

	
	private class InternalImageProcessor
	{
		private final JRPrintImage imageElement;
		private final RenderersCache imageRenderersCache;
		private final boolean isLazy; 
		private final boolean embedImage; 
		private final boolean needDimension; 
		private final TableCell cell;
		private final int availableImageWidth;
		private final int availableImageHeight;

		protected InternalImageProcessor(
			JRPrintImage imageElement,
			boolean isLazy,
			boolean needDimension, 
			TableCell cell,
			int availableImageWidth,
			int availableImageHeight
			)
		{
			this.imageElement = imageElement;
			this.imageRenderersCache = imageElement.isUsingCache() ? renderersCache : new RenderersCache(getJasperReportsContext());
			this.isLazy = isLazy;
			this.embedImage = isEmbedImage(imageElement);
			this.needDimension = needDimension;
			this.cell = cell;
			this.availableImageWidth = availableImageWidth;
			this.availableImageHeight = availableImageHeight;
		}
		
		protected InternalImageProcessorResult process(Renderable renderer) throws JRException, IOException
		{
			String imageSource = null;
			Dimension2D dimension = null;
			boolean isEmbededSvgData = false;
			
			if (isLazy)
			{
				// we do not cache imagePath for lazy images because the short location string is already cached inside the render itself
				imageSource = RendererUtil.getResourceLocation(renderer);
			}
			else
			{
				if (renderer instanceof ResourceRenderer)
				{
					renderer = imageRenderersCache.getLoadedRenderer((ResourceRenderer)renderer);
				}

				// check dimension first, to avoid caching renderers that might not be used eventually, due to their dimension errors 
				if (needDimension)
				{
					DimensionRenderable dimensionRenderer = imageRenderersCache.getDimensionRenderable(renderer);
					dimension = dimensionRenderer == null ? null :  dimensionRenderer.getDimension(jasperReportsContext);
				}

				if (
					!embedImage //we do not cache imagePath for embedded images because it is too big
					&& renderer instanceof DataRenderable //we do not cache imagePath for non-data renderers because they render width different width/height each time
					&& rendererToImagePathMap.containsKey(renderer.getId())
					)
				{
					imageSource = rendererToImagePathMap.get(renderer.getId());
				}
				else
				{
					if (embedImage)
					{
						Dimension dim =	null;
						
						if (
							imageElement.getRotation() == RotationEnum.LEFT
							|| imageElement.getRotation() == RotationEnum.RIGHT
							)
						{
							dim =	new Dimension(availableImageHeight, availableImageWidth);
						}
						else
						{
							dim =	new Dimension(availableImageWidth, availableImageHeight);
						}

						DataRenderable dataRenderer = null;
						
						if (isConvertSvgToImage(imageElement))
						{
							dataRenderer = 
								getRendererUtil().getImageDataRenderable(
									imageRenderersCache,
									renderer,
									dim,
									ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null
									);
						}
						else
						{
							dataRenderer = 
								getRendererUtil().getDataRenderable(
									renderer,
									dim,
									ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null
									);
						}
						
						byte[] imageData = dataRenderer.getData(jasperReportsContext);

						SvgDataSniffer.SvgInfo svgInfo = getRendererUtil().getSvgInfo(imageData);
						
						if (svgInfo != null)
						{
							if (isEmbeddedSvgUseFonts(imageElement))
							{
								Locale locale = getLocale();

								SvgFontProcessor svgFontProcessor = new SvgFontProcessor(jasperReportsContext, locale) 
								{
									@Override
									public String getFontFamily(String fontFamily, Locale locale) 
									{
										// Here we rely on the ability of FontUtil.getFontInfoIgnoreCase(fontFamily, locale) method to
										// find fonts from font extensions based on the java.awt.Font.getFamily() of their font faces.
										// This is because the SVG produced by Batik stores the family name of the AWT fonts used to
										// render text on the Batik Graphics2D implementation, as it knows nothing about family names from JR extensions.
										return HtmlExporter.this.getFontFamily(true, fontFamily, locale);
									}
								};
								
								imageData = svgFontProcessor.process(imageData);
							}
							
							isEmbededSvgData = true;
							
							String encoding = svgInfo.getEncoding();
							imageSource = new String(imageData, encoding == null ? "UTF-8" : encoding);
							
							// we might have received needDimension false above, as a hint, but if we arrive here, 
							// we definitely need to attempt getting the dimension of the SVG, regardless of scale image type
							DimensionRenderable dimensionRenderer = imageRenderersCache.getDimensionRenderable(renderer);
							dimension = dimensionRenderer == null ? null :  dimensionRenderer.getDimension(jasperReportsContext);
						}
						else
						{
							String imageMimeType = JRTypeSniffer.getImageTypeValue(imageData).getMimeType();

							ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							
							Base64Util.encode(bais, baos);
							
							imageSource = "data:" + imageMimeType + ";base64," + new String(baos.toByteArray(), "UTF-8"); // UTF-8 is fine as we just need an ASCII compatible encoding for the Base64 array
						}
						
						//don't cache embedded imageSource as they are not image paths
					}
					else
					{
						HtmlResourceHandler imageHandler = getExporterOutput().getImageHandler();
						if (imageHandler != null)
						{
							Dimension dim =	null;
							
							if (
								imageElement.getRotation() == RotationEnum.LEFT
								|| imageElement.getRotation() == RotationEnum.RIGHT
								)
							{
								dim =	new Dimension(availableImageHeight, availableImageWidth);
							}
							else
							{
								dim =	new Dimension(availableImageWidth, availableImageHeight);
							}

							DataRenderable dataRenderer = null;
							
							if (isConvertSvgToImage(imageElement))
							{
								dataRenderer = 
									getRendererUtil().getImageDataRenderable(
										imageRenderersCache,
										renderer,
										dim,
										ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null
										);
							}
							else
							{
								dataRenderer = 
									getRendererUtil().getDataRenderable(
										renderer,
										dim,
										ModeEnum.OPAQUE == imageElement.getMode() ? imageElement.getBackcolor() : null
										);
							}

							byte[] imageData = dataRenderer.getData(jasperReportsContext);
							
							String fileExtension = 
								getRendererUtil().isSvgData(imageData)
								? RendererUtil.SVG_FILE_EXTENSION
								: JRTypeSniffer.getImageTypeValue(imageData).getFileExtension();

							String imageName = getImageName(getElementIndex(cell), fileExtension);

							imageHandler.handleResource(imageName, imageData);
							
							imageSource = imageHandler.getResourcePath(imageName);

							if (dataRenderer == renderer)
							{
								//cache imagePath only for true ImageRenderable instances because the wrapping ones render with different width/height each time
								rendererToImagePathMap.put(renderer.getId(), imageSource);
							}
						}
						//does not make sense to cache null imagePath, in the absence of an image handler
					}
				}
			}
			
			return 
				new InternalImageProcessorResult(
					imageSource, 
					dimension,
					isEmbededSvgData
					);
		}
	}
	
	
	private static class InternalImageProcessorResult
	{
		protected final String imageSource;
		protected final Dimension2D dimension;
		protected final boolean isEmbededSvgData;
		
		protected InternalImageProcessorResult(
			String imagePath, 
			Dimension2D dimension,
			boolean isEmbededSvgData
			)
		{
			this.imageSource = imagePath;
			this.dimension = dimension;
			this.isEmbededSvgData = isEmbededSvgData;
		}
	}


	protected String getImageHorizontalAlignmentStyle(JRPrintImage image)
	{
		String horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
		switch (image.getHorizontalImageAlign())
		{
			case RIGHT :
			{
				horizontalAlignment = CSS_TEXT_ALIGN_RIGHT;
				break;
			}
			case CENTER :
			{
				horizontalAlignment = CSS_TEXT_ALIGN_CENTER;
				break;
			}
			case LEFT :
			default :
			{
				horizontalAlignment = CSS_TEXT_ALIGN_LEFT;
			}
		}
		return horizontalAlignment;
	}

	protected String getImageVerticalAlignmentStyle(JRPrintImage image)
	{
		String verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
		switch (image.getVerticalImageAlign())
		{
			case BOTTOM :
			{
				verticalAlignment = HTML_VERTICAL_ALIGN_BOTTOM;
				break;
			}
			case MIDDLE :
			{
				verticalAlignment = HTML_VERTICAL_ALIGN_MIDDLE;
				break;
			}
			case TOP :
			default :
			{
				verticalAlignment = HTML_VERTICAL_ALIGN_TOP;
			}
		}
		return verticalAlignment;
	}

	protected JRPrintElementIndex getElementIndex(TableCell cell)
	{
		String elementAddress = cell.getElementAddress();
		JRPrintElementIndex elementIndex = new JRPrintElementIndex(reportIndex, pageIndex,
						elementAddress);
		return elementIndex;
	}

	protected void writeImageMap(String imageMapName, JRPrintImage image, List<JRPrintImageAreaHyperlink> imageMapAreas) throws IOException
	{
		writer.write("<map name=\"" + imageMapName + "\">\n");

		for (ListIterator<JRPrintImageAreaHyperlink> it = imageMapAreas.listIterator(imageMapAreas.size()); it.hasPrevious();)
		{
			JRPrintImageAreaHyperlink areaHyperlink = it.previous();
			JRPrintHyperlink link = areaHyperlink.getHyperlink();
			JRPrintImageArea area = areaHyperlink.getArea();

			writer.write("  <area shape=\"" + JRPrintImageArea.getHtmlShape(area.getShape()) + "\"");
			
			writeImageAreaCoordinates(area.getCoordinates());			
			writeImageAreaHyperlink(link);
			writer.write("/>\n");
		}
		
		if (image.getHyperlinkType() != HyperlinkTypeEnum.NONE)
		{
			writer.write("  <area shape=\"default\"");
			writeImageAreaCoordinates(new int[]{0, 0, image.getWidth(), image.getHeight()});//for IE
			writeImageAreaHyperlink(image);
			writer.write("/>\n");
		}
		
		writer.write("</map>\n");
	}
	
	protected void writeImageAreaCoordinates(int[] coords) throws IOException
	{
		if (coords != null && coords.length > 0)
		{
			StringBuilder coordsEnum = new StringBuilder(coords.length * 4);
			coordsEnum.append((int)toZoom(coords[0]));
			for (int i = 1; i < coords.length; i++)
			{
				coordsEnum.append(',');
				coordsEnum.append((int)toZoom(coords[i]));
			}
			writer.write(" coords=\"" + coordsEnum + "\"");
		}		
	}


	protected void writeImageAreaHyperlink(JRPrintHyperlink hyperlink) throws IOException
	{
		if (getReportContext() != null)
		{
			if (hyperlink.getLinkType() != null)
			{
				int id = hyperlink.hashCode() & 0x7FFFFFFF;
				writer.write(" class=\"_jrHyperLink " + JRStringUtil.encodeXmlAttribute(hyperlink.getLinkType()) + "\" data-id=\"" + id + "\"");

				HyperlinkData hyperlinkData = new HyperlinkData();
				hyperlinkData.setId(String.valueOf(id));
				hyperlinkData.setHref(getHyperlinkURL(hyperlink));
				hyperlinkData.setSelector("._jrHyperLink." + hyperlink.getLinkType());
				hyperlinkData.setHyperlink(hyperlink);

				hyperlinksData.add(hyperlinkData);
			}
		}
		else
		{
			String href = getHyperlinkURL(hyperlink);
			if (href == null)
			{
				writer.write(" nohref=\"nohref\"");
			}
			else
			{
				writer.write(" href=\"" + JRStringUtil.encodeXmlAttribute(href) + "\"");

				String target = getHyperlinkTarget(hyperlink);
				if (target != null)
				{
					writer.write(" target=\"");
					writer.write(JRStringUtil.encodeXmlAttribute(target));
					writer.write("\"");
				}
			}
		}

		if (hyperlink.getHyperlinkTooltip() != null)
		{
			writer.write(" title=\"");
			writer.write(JRStringUtil.encodeXmlAttribute(hyperlink.getHyperlinkTooltip()));
			writer.write("\"");
		}
	}

	protected void writeRectangle(JRPrintRectangle rectangle, TableCell cell) throws IOException
	{
		startCell(rectangle, cell);
		
		int radius = rectangle.getRadius();
		if (radius == 0)
		{
			StringBuilder styleBuffer = new StringBuilder();
			appendElementCellGenericStyle(cell, styleBuffer);
			appendBackcolorStyle(cell, styleBuffer);
			appendPen(
				styleBuffer,
				rectangle.getLinePen(),
				null
				);
			writeStyle(styleBuffer);
		}

		finishStartCell();

		if (radius != 0)
		{
			float lineDiff = rectangle.getLinePen().getLineWidth() / 2;
			writer.write("<svg height=\"" + rectangle.getHeight() + "\" width=\"" + rectangle.getWidth() + "\">");
			writer.write("<rect x=\"" + lineDiff + "\" y=\"" + lineDiff + "\" rx=\"" + radius + "\" ry=\"" + radius + "\" ");
			writer.write("height=\"" + (rectangle.getHeight() - 2 * lineDiff) + "\" width=\"" + (rectangle.getWidth() - 2 * lineDiff) + "\" ");
			writeSvgStyle(rectangle);
			writer.write("\"/></svg>");
		}

		endCell(cell.getCellType());
	}

	protected void writeEllipse(JRPrintEllipse ellipse, TableCell cell) throws IOException
	{
		startCell(ellipse, cell);

		finishStartCell();

		float lineDiff = ellipse.getLinePen().getLineWidth() / 2;
		writer.write("<svg height=\"" + ellipse.getHeight() + "\" width=\"" + ellipse.getWidth() + "\">");
		writer.write("<ellipse cx=\"" + (ellipse.getWidth() / 2) + "\" cy=\"" + (ellipse.getHeight() / 2));
		writer.write("\" rx=\"" + (ellipse.getWidth() / 2 - lineDiff) + "\" ry=\"" + (ellipse.getHeight() / 2 - lineDiff) + "\" ");
		writeSvgStyle(ellipse);
		writer.write("\"/></svg>");
		
		endCell(cell.getCellType());
	}

	protected void writeSvgStyle(JRPrintGraphicElement element) throws IOException
	{
		writer.write("style=\"fill:" + JRColorUtil.getCssColor(element.getBackcolor()) + ";");
		writer.write("stroke:" + JRColorUtil.getCssColor(element.getLinePen().getLineColor()) + ";");
		writer.write("stroke-width:" + element.getLinePen().getLineWidth() + ";");

		switch (element.getLinePen().getLineStyle())
		{
			case DOTTED :
			{
				writer.write("stroke-dasharray:" + element.getLinePen().getLineWidth() + "," + element.getLinePen().getLineWidth() + ";");
				break;
			}
			case DASHED :
			{
				writer.write("stroke-dasharray:" + 5 * element.getLinePen().getLineWidth() + "," + 3 * element.getLinePen().getLineWidth() + ";");
				break;
			}
			case DOUBLE : //FIXME: there is no built-in svg support for double stroke style; strokes could be rendered twice as a workaround
			case SOLID :
			default :
			{
				break;
			}
		}
	}

	protected void writeLine(JRPrintLine line, TableCell cell)
			throws IOException
	{		
		startCell(line, cell);
		if (isOblique(line))
		{
			finishStartCell();
			
			int width = line.getWidth();
			int height = line.getHeight();
			LineDirectionEnum lineDirection = line.getDirection();
			int y1 = lineDirection == LineDirectionEnum.BOTTOM_UP ? height : 0;
			int y2 = lineDirection == LineDirectionEnum.BOTTOM_UP ? 0 : height;
			
			writer.write("<svg height=\"" + height + "\" width=\"" + width + "\">");
			writer.write("<line x1=\"0\" y1=\"" + y1 +"\" x2=\"" + width + "\" y2=\"" + y2 + "\" ");
			writeSvgStyle(line);
			writer.write("\"/></svg>");
		}
		else
		{
			StringBuilder styleBuffer = new StringBuilder();

			appendElementCellGenericStyle(cell, styleBuffer);
			appendBackcolorStyle(cell, styleBuffer);
			
			String side = null;
			float ratio = line.getWidth() / line.getHeight();
			if (ratio > 1)
			{
				if (line.getDirection() == LineDirectionEnum.TOP_DOWN)
				{
					side = "top";
				}
				else
				{
					side = "bottom";
				}
			}
			else
			{
				if (line.getDirection() == LineDirectionEnum.TOP_DOWN)
				{
					side = "left";
				}
				else
				{
					side = "right";
				}
			}

			appendPen(
				styleBuffer,
				line.getLinePen(),
				side
				);

			writeStyle(styleBuffer);

			finishStartCell();			
		}
		
		endCell(cell.getCellType());
	}
	
	
	protected boolean isOblique(JRPrintLine line){
		return line.getWidth() > 1 && line.getHeight() > 1;
	}
	
	protected void writeGenericElement(JRGenericPrintElement element, TableCell cell) throws IOException, JRException
	{
		GenericElementHtmlHandler handler = (GenericElementHtmlHandler) 
				GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
						element.getGenericType(), HTML_EXPORTER_KEY);
		
		if (handler == null)
		{
			if (log.isDebugEnabled())
			{
				log.debug("No HTML generic element handler for " 
						+ element.getGenericType());
			}
			
			writeEmptyCell(cell.getColumnSpan(), cell.getRowSpan());// TODO lucianc backcolor/borders?
		}
		else
		{
			String htmlFragment = handler.getHtmlFragment(exporterContext, element);
			if (htmlFragment == null)
			{
				handler.exportElement(exporterContext, element, cell);
			}
			else
			{
				startCell(element, cell);

				StringBuilder styleBuffer = new StringBuilder();
				appendElementCellGenericStyle(cell, styleBuffer);
				appendBackcolorStyle(cell, styleBuffer);
				appendBorderStyle(cell.getBox(), styleBuffer);
				writeStyle(styleBuffer);

				finishStartCell();
				
				writer.write(htmlFragment);
				
				endCell(cell.getCellType());
			}
		}
	}
	
	protected void writeLayers(List<Table> layers, TableVisitor tableVisitor, TableCell cell) throws IOException
	{
		startCell(cell);

		StringBuilder styleBuffer = new StringBuilder();
		appendElementCellGenericStyle(cell, styleBuffer);
		appendBackcolorStyle(cell, styleBuffer);
		appendBorderStyle(cell.getBox(), styleBuffer);
		writeStyle(styleBuffer);

		finishStartCell();
		
		// layers need to always specify backcolors
		setBackcolor(null);
		writer.write("<div style=\"width: 100%; height: 100%; position: relative;\">\n");

		for (ListIterator<Table> it = layers.listIterator(); it.hasNext();)
		{
			Table table = it.next();
			
			StringBuilder layerStyleBuffer = new StringBuilder();
			if (it.hasNext()) {
				layerStyleBuffer.append("position: absolute; overflow: hidden; ");
			} else {
				layerStyleBuffer.append("position: relative; ");
			}
			layerStyleBuffer.append("width: 100%; height: 100%; ");
			
			if (it.previousIndex() > 0) {
				layerStyleBuffer.append("pointer-events: none; ");
			}

			writer.write("<div style=\"");
			writer.write(layerStyleBuffer.toString());
			writer.write("\">\n");

			++pointerEventsNoneStack;
			exportTable(tableVisitor, table, false, false);
			--pointerEventsNoneStack;
			
			writer.write("</div>\n");
		}
		
		writer.write("</div>\n");
		restoreBackcolor();

		endCell(cell.getCellType());
	}
	
	protected void writeNestedTable(Table table, TableVisitor tableVisitor, TableCell cell) throws IOException
	{
		startCell(cell);

		StringBuilder styleBuffer = new StringBuilder();
		appendElementCellGenericStyle(cell, styleBuffer);
		appendBackcolorStyle(cell, styleBuffer);
		appendBorderStyle(cell.getBox(), styleBuffer);
		appendPaddingStyle(cell.getBox(), styleBuffer);
		writeStyle(styleBuffer);

		finishStartCell();

		exportTable(tableVisitor, table, false, false);

		endCell(cell.getCellType());
	}

	protected void startCell(JRPrintElement element, TableCell cell) throws IOException
	{
		startCell(cell.getColumnSpan(), cell.getRowSpan(), cell.getCellType());

		String dataAttr = getDataAttributes(element, cell);
		if (dataAttr != null)
		{
			writer.write(dataAttr);
		}
	}
	
	public String getDataAttributes(JRPrintElement element, TableCell cell)
	{
		StringBuilder sb = new StringBuilder();
		
		String id = getCellProperty(element, cell, PROPERTY_HTML_ID);
		if (id != null)
		{
			sb.append(" id=\"" + JRStringUtil.encodeXmlAttribute(id) +"\"");
		}
		String clazz = getCellProperty(element, cell, PROPERTY_HTML_CLASS);
		if (clazz != null)
		{
			sb.append(" class=\"" + JRStringUtil.encodeXmlAttribute(clazz) +"\"");
		}

		if (element instanceof JRPrintText && ((JRPrintText) element).getValue() instanceof Number 
				&& getCurrentItemConfiguration().isIncludeElementUUID())
		{
			sb.append(" data-eluuid=\"" + element.getUUID() + "\"");
		}

		String colUuid = getCellProperty(element, cell, HeaderToolbarElement.PROPERTY_COLUMN_UUID);//FIXMEJIVE register properties like this in a pluggable way; extensions?
		if (colUuid != null)
		{
			sb.append(" data-coluuid=\"" + JRStringUtil.encodeXmlAttribute(colUuid) + "\"");
		}
		String cellId = getCellProperty(element, cell, HeaderToolbarElement.PROPERTY_CELL_ID);
		if (cellId != null)
		{
			sb.append(" data-cellid=\"" + JRStringUtil.encodeXmlAttribute(cellId) + "\"");
		}
		String tableUuid = getCellProperty(element, cell, HeaderToolbarElement.PROPERTY_TABLE_UUID);
		if (tableUuid != null)
		{
			sb.append(" data-tableuuid=\"" + JRStringUtil.encodeXmlAttribute(tableUuid) + "\"");
		}
		String columnIndex = getCellProperty(element, cell, HeaderToolbarElement.PROPERTY_COLUMN_INDEX);
		if (columnIndex != null)
		{
			sb.append(" data-colidx=\"" + JRStringUtil.encodeXmlAttribute(columnIndex) + "\"");
		}
		
		String xtabId = getCellProperty(element, cell, CrosstabConstants.PROPERTY_CROSSTAB_ID);
		if (xtabId != null)
		{
			sb.append(" " + CrosstabConstants.ATTRIBUTE_CROSSTAB_ID + "=\"" 
					+ JRStringUtil.encodeXmlAttribute(xtabId) + "\"");
		}
		
		String xtabColIdx = getCellProperty(element, cell, CrosstabConstants.PROPERTY_COLUMN_INDEX);
		if (xtabColIdx != null)
		{
			sb.append(" " + CrosstabConstants.ATTRIBUTE_COLUMN_INDEX + "=\"" 
					+ JRStringUtil.encodeXmlAttribute(xtabColIdx) + "\"");
		}
		
		return sb.length() > 0 ? sb.toString() : null;
	}
	
	protected String getCellProperty(JRPrintElement element, TableCell cell, String key)
	{
		String property = null;
		if (element != null)
		{
			property = getPropertiesUtil().getProperty(element, key);
		}
		
		if (property == null)
		{
			Tabulator tabulator = cell.getTabulator();
			for (FrameCell parentCell = cell.getCell().getParent(); 
					parentCell != null && property == null;
					parentCell = parentCell.getParent())
			{
				JRPrintElement parentElement = tabulator.getCellElement(parentCell);
				property = getPropertiesUtil().getProperty(parentElement, key);
			}
		}
		return property;
	}
	
	protected void startCell(TableCell cell) throws IOException
	{
		startCell(cell.getElement(), cell);
	}

	protected void startCell(int colSpan, int rowSpan, CellType cellType) throws IOException
	{
		String tag = "td"; 
		String role = null; 
		if (cellType == CellType.COLUMN_HEADER)
		{
			tag = "th";
			role = "columnheader";
		}
		else  if (cellType == CellType.ROW_HEADER)
		{
			tag = "th";
			role = "rowheader";
		}
		writer.write("<");
		writer.write(tag);
		if (role != null)
		{
			writer.write(" role=\"");
			writer.write(role);
			writer.write("\"");
		}
		if (colSpan > 1)
		{
			writer.write(" colspan=\"");
			writer.write(Integer.toString(colSpan));
			writer.write("\"");
		}
		if (rowSpan > 1)
		{
			writer.write(" rowspan=\"");
			writer.write(Integer.toString(rowSpan));
			writer.write("\"");
		}		
	}
	
	protected void finishStartCell() throws IOException
	{
		writer.write(">\n");
	}
	
	protected void endCell(CellType cellType) throws IOException
	{
		if (
			cellType == CellType.COLUMN_HEADER
			|| cellType == CellType.ROW_HEADER
			)
		{
			writer.write("</th>\n");
		}
		else
		{
			writer.write("</td>\n");
		}
	}
	
	protected void writeEmptyCell(int colSpan, int rowSpan) throws IOException
	{
		startCell(colSpan, rowSpan, null);
		finishStartCell();
		endCell(null);
	}
	
	protected void writeFrameCell(TableCell cell) throws IOException
	{
		startCell(cell);
		
		StringBuilder styleBuffer = new StringBuilder();
		appendElementCellGenericStyle(cell, styleBuffer);
		appendBackcolorStyle(cell, styleBuffer);
		appendBorderStyle(cell.getBox(), styleBuffer);
		writeStyle(styleBuffer);

		finishStartCell();
		endCell(cell.getCellType());
	}

	protected void writeStyle(StringBuilder styleBuffer) throws IOException
	{
		if (styleBuffer.length() > 0)
		{
			writer.write(" style=\"");
			writer.write(styleBuffer.toString());
			writer.write("\"");
		}
	}
	
	protected void appendElementCellGenericStyle(TableCell cell, StringBuilder styleBuffer)
	{
		if (pointerEventsNoneStack > 0 && cell.getElement() != null)
		{
			styleBuffer.append("pointer-events: auto; ");
		}
	}

	protected void setBackcolor(Color color)
	{
		backcolorStack.addFirst(color);
	}

	protected void restoreBackcolor()
	{
		backcolorStack.removeFirst();
	}

	protected boolean matchesBackcolor(Color backcolor)
	{
		if (backcolorStack.isEmpty())
		{
			return false;
		}
		
		Color currentBackcolor = backcolorStack.getFirst();
		return currentBackcolor != null && backcolor.getRGB() == currentBackcolor.getRGB();
	}
	
	protected Color appendBackcolorStyle(TableCell cell, StringBuilder styleBuffer)
	{
		Color cellBackcolor = cell.getBackcolor();
		if (cellBackcolor != null && !matchesBackcolor(cellBackcolor))
		{
			styleBuffer.append("background-color: ");
			styleBuffer.append(JRColorUtil.getCssColor(cellBackcolor));
			styleBuffer.append("; ");

			return cellBackcolor;
		}

		return null;
	}

	protected boolean appendBorderStyle(JRLineBox box, StringBuilder styleBuffer)
	{
		boolean addedToStyle = false;

		if (box != null)
		{
			LineStyleEnum tps = box.getTopPen().getLineStyle();
			LineStyleEnum lps = box.getLeftPen().getLineStyle();
			LineStyleEnum bps = box.getBottomPen().getLineStyle();
			LineStyleEnum rps = box.getRightPen().getLineStyle();
			
			float tpw = box.getTopPen().getLineWidth();
			float lpw = box.getLeftPen().getLineWidth();
			float bpw = box.getBottomPen().getLineWidth();
			float rpw = box.getRightPen().getLineWidth();
			
			if (0f < tpw && tpw < 1f) {
				tpw = 1f;
			}
			if (0f < lpw && lpw < 1f) {
				lpw = 1f;
			}
			if (0f < bpw && bpw < 1f) {
				bpw = 1f;
			}
			if (0f < rpw && rpw < 1f) {
				rpw = 1f;
			}
			
			Color tpc = box.getTopPen().getLineColor();
			
			// try to compact all borders into one css property
			if (tps == lps &&												// same line style
					tps == bps &&
					tps == rps &&
					tpw == lpw &&											// same line width
					tpw == bpw &&
					tpw == rpw &&
					tpc.equals(box.getLeftPen().getLineColor()) &&			// same line color
					tpc.equals(box.getBottomPen().getLineColor()) &&
					tpc.equals(box.getRightPen().getLineColor())) 
			{
				addedToStyle |= appendPen(
						styleBuffer,
						box.getTopPen(),
						null
						);
			} else {
				addedToStyle |= appendPen(
					styleBuffer,
					box.getTopPen(),
					"top"
					);
				addedToStyle |= appendPen(
					styleBuffer,
					box.getLeftPen(),
					"left"
					);
				addedToStyle |= appendPen(
					styleBuffer,
					box.getBottomPen(),
					"bottom"
					);
				addedToStyle |= appendPen(
					styleBuffer,
					box.getRightPen(),
					"right"
					);
			}
		}
		
		return addedToStyle;
	}
	
	protected boolean appendPen(StringBuilder sb, JRPen pen, String side)
	{
		boolean addedToStyle = false;
		
		float borderWidth = pen.getLineWidth();
		if (0f < borderWidth && borderWidth < 1f)
		{
			borderWidth = 1f;
		}

		String borderStyle = null;
		switch (pen.getLineStyle())
		{
			case DOUBLE :
			{
				borderStyle = "double";
				break;
			}
			case DOTTED :
			{
				borderStyle = "dotted";
				break;
			}
			case DASHED :
			{
				borderStyle = "dashed";
				break;
			}
			case SOLID :
			default :
			{
				borderStyle = "solid";
				break;
			}
		}

		if (borderWidth > 0f)
		{
			sb.append("border");
			if (side != null)
			{
				sb.append("-");
				sb.append(side);
			}

			sb.append(": ");
			sb.append(toSizeUnit(borderWidth));
			
			sb.append(" ");
			sb.append(borderStyle);

			sb.append(" ");
			sb.append(JRColorUtil.getCssColor(pen.getLineColor()));
			sb.append("; ");

			addedToStyle = true;
		}

		return addedToStyle;
	}
	
	protected boolean appendPaddingStyle(JRLineBox box, StringBuilder styleBuffer)
	{
		boolean addedToStyle = false;
		
		if (box != null)
		{
			Integer tp = box.getTopPadding();
			Integer lp = box.getLeftPadding();
			Integer bp = box.getBottomPadding();
			Integer rp = box.getRightPadding();
			
			// try to compact all paddings into one css property
			if (tp == lp && tp == bp && tp == rp)
			{
				addedToStyle |= appendPadding(
						styleBuffer,
						tp,
						null
						);
			} else 
			{
				addedToStyle |= appendPadding(
						styleBuffer,
						box.getTopPadding(),
						"top"
						);
				addedToStyle |= appendPadding(
						styleBuffer,
						box.getLeftPadding(),
						"left"
						);
				addedToStyle |= appendPadding(
						styleBuffer,
						box.getBottomPadding(),
						"bottom"
						);
				addedToStyle |= appendPadding(
						styleBuffer,
						box.getRightPadding(),
						"right"
						);
			}
		}
		
		return addedToStyle;
	}
	
	protected boolean appendPadding(StringBuilder sb, Integer padding, String side)
	{
		boolean addedToStyle = false;
		
		if (padding > 0)
		{
			sb.append("padding");
			if (side != null)
			{
				sb.append("-");
				sb.append(side);
			}
			sb.append(": ");
			sb.append(toSizeUnit(padding));
			sb.append("; ");

			addedToStyle = true;
		}
		
		return addedToStyle;
	}


	protected AccessibilityTagEnum startHeading(JRPrintText text) throws IOException
	{
		AccessibilityTagEnum headingTag = null;
		
		if (text.getPropertiesMap().containsProperty(AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG))
		{
			AccessibilityTagEnum accessibilityTag = 
				AccessibilityTagEnum.getByName(
					text.getPropertiesMap().getProperty(AccessibilityUtil.PROPERTY_ACCESSIBILITY_TAG)
					);
			if (accessibilityTag != null && accessibilityTag.name().startsWith("H"))
			{
				headingTag = accessibilityTag;
				writer.write("<");
				writer.write(headingTag.getName());
				writer.write(" style=\"margin:0\">");
			}
		}

		return headingTag;
	}

	
	protected void endHeading(AccessibilityTagEnum headingTag) throws IOException
	{
		if (headingTag != null)
		{
			writer.write("</");
			writer.write(headingTag.getName());
			writer.write(">");
		}
	}

	
	protected boolean startHyperlink(JRPrintHyperlink link) throws IOException
	{
		boolean hyperlinkStarted = false,
				canWrite = false;

		if (getReportContext() != null)
		{
			Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(HtmlReportConfiguration.PROPERTY_IGNORE_HYPERLINK, link);
			if (ignoreHyperlink == null)
			{
				ignoreHyperlink = getCurrentItemConfiguration().isIgnoreHyperlink();
			}

			if (!ignoreHyperlink && link.getLinkType() != null)
			{
				canWrite = true;
				int id = link.hashCode() & 0x7FFFFFFF;

				writer.write("<span class=\"_jrHyperLink " + JRStringUtil.encodeXmlAttribute(link.getLinkType()) + "\" data-id=\"" + id + "\"");

				HyperlinkData hyperlinkData = new HyperlinkData();
				hyperlinkData.setId(String.valueOf(id));
				hyperlinkData.setHref(getHyperlinkURL(link));
				hyperlinkData.setSelector("._jrHyperLink." + link.getLinkType());
				hyperlinkData.setHyperlink(link);

				hyperlinksData.add(hyperlinkData);
				hyperlinkStarted = true;
			}
		}
		else
		{
			String href = getHyperlinkURL(link);

			if (href != null)
			{
				canWrite = true;
				writer.write("<a href=\"");
				writer.write(JRStringUtil.encodeXmlAttribute(href));
				writer.write("\"");

				String target = getHyperlinkTarget(link);
				if (target != null)
				{
					writer.write(" target=\"");
					writer.write(JRStringUtil.encodeXmlAttribute(target));
					writer.write("\"");
				}
			}

			hyperlinkStarted = href != null;
		}

		if (canWrite)
		{
			if (link.getHyperlinkTooltip() != null)
			{
				writer.write(" title=\"");
				writer.write(JRStringUtil.encodeXmlAttribute(link.getHyperlinkTooltip()));
				writer.write("\"");
			}

			writer.write(">");
		}

		return hyperlinkStarted;
	}

	protected void endHyperlink() throws IOException
	{
		if (getReportContext() != null) {
			writer.write("</span>");
		}
		else
		{
			writer.write("</a>");
		}
	}

	protected String getHyperlinkURL(JRPrintHyperlink link)
	{
		return resolveHyperlinkURL(reportIndex, link);
	}
	
	protected String resolveHyperlinkURL(int reportIndex, JRPrintHyperlink link)
	{
		String href = null;
		
		Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(HtmlReportConfiguration.PROPERTY_IGNORE_HYPERLINK, link);
		if (ignoreHyperlink == null)
		{
			ignoreHyperlink = getCurrentItemConfiguration().isIgnoreHyperlink();
		}

		if (!ignoreHyperlink)
		{
			JRHyperlinkProducer customHandler = getHyperlinkProducer(link);		
			if (customHandler == null)
			{
				switch (link.getHyperlinkType())
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
							href = "#" + link.getHyperlinkAnchor();
						}
						break;
					}
					case LOCAL_PAGE :
					{
						if (link.getHyperlinkPage() != null)
						{
							href = "#" + JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + link.getHyperlinkPage().toString();
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
							href = link.getHyperlinkReference() + "#" + JR_PAGE_ANCHOR_PREFIX + "0_" + link.getHyperlinkPage().toString();
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
		
		checkHrefAllowed(href);
		return href;
	}

	protected String getHyperlinkTarget(JRPrintHyperlink link)
	{
		String target = null;
		JRHyperlinkTargetProducer producer = targetProducerFactory.getHyperlinkTargetProducer(link.getLinkTarget());		
		if (producer == null)
		{
			switch (link.getHyperlinkTarget())
			{
				case BLANK :
				{
					target = "_blank";//FIXME make reverse for html markup hyperlinks
					break;
				}
				case PARENT :
				{
					target = "_parent";
					break;
				}
				case TOP :
				{
					target = "_top";
					break;
				}
				case CUSTOM :
				{
					boolean paramFound = false;
					List<JRPrintHyperlinkParameter> parameters = link.getHyperlinkParameters() == null ? null : link.getHyperlinkParameters().getParameters();
					if (parameters != null)
					{
						for (JRPrintHyperlinkParameter parameter : parameters)
						{
							if (link.getLinkTarget().equals(parameter.getName()))
							{
								target = parameter.getValue() == null ? null : parameter.getValue().toString();
								paramFound = true;
								break;
							}
						}
					}
					if (!paramFound)
					{
						target = link.getLinkTarget();
					}
					break;
				}
				case SELF :
				default :
				{
				}
			}
		}
		else
		{
			target = producer.getHyperlinkTarget(link);
		}

		return target;
	}

	public String toSizeUnit(float size)
	{
		Number number = toZoom(size);
		if (number.intValue() == number.floatValue())
		{
			number = number.intValue();
		}

		return String.valueOf(number) + currentSizeUnit;
	}

	protected float toZoom(float size)//FIXMEEXPORT cache this
	{
		return (currentZoomRatio * size);
	}

	private void addSearchAttributes(JRStyledText styledText, JRPrintText textElement) {
		ReportContext reportContext = getReportContext();
		if (reportContext != null) {
			SpansInfo spansInfo = (SpansInfo) reportContext.getParameterValue("net.sf.jasperreports.search.term.highlighter");
			PrintElementId pei = PrintElementId.forElement(textElement);

			if (spansInfo != null && spansInfo.hasHitSpanInfo(pei.toString())) {
				List<HitSpanInfo> hitSpanInfos = JRCloneUtils.cloneList(spansInfo.getHitSpanInfo(pei.toString()));

				short[] lineBreakOffsets = textElement.getLineBreakOffsets();
				if (lineBreakOffsets != null && lineBreakOffsets.length > 0) {
					int sz = lineBreakOffsets.length;
					Set<HitTermInfo> processedHitTerms = new HashSet<>();
					for (HitSpanInfo si: hitSpanInfos) {
						for (HitTermInfo ti : si.getHitTermInfoList()) {
							// prevent overlapping terms to have the offset applied multiple times
							if (!processedHitTerms.contains(ti)) {
								int totalOffset = 0;
								for (int i = 0; i < sz; i++) {
									totalOffset += lineBreakOffsets[i];
									if (totalOffset <= ti.getStart()) {
										ti.setStart(ti.getStart() + 1);
										ti.setEnd(ti.getEnd() + 1);
									}
									// take into account the word break
									else if (totalOffset > ti.getStart() && totalOffset < ti.getEnd()) {
										ti.setEnd(ti.getEnd() + 1);
									} else {
										break;
									}
								}
								processedHitTerms.add(ti);
							}
						}
					}
				}

				AttributedString attributedString = styledText.getAttributedString();
				for (HitSpanInfo hsi: hitSpanInfos) {
					List<HitTermInfo> htiList = hsi.getHitTermInfoList();
					for (int i = 0, sz = htiList.size(); i < sz ; i++) {
						String searchResultClasses;
						if (i == 0 && spansInfo.getTermsPerQuery() > 1) {
							searchResultClasses = "jr_search_result jr_search_result_start";
						} else {
							searchResultClasses = "jr_search_result";
						}
						attributedString.addAttribute(JRTextAttribute.SEARCH_HIGHLIGHT, searchResultClasses,
								htiList.get(i).getStart(), htiList.get(i).getEnd());

						// if there are more terms in the current span, mark the space between the current term and the
						// next one as whitespace to make it highlightable
						if (i + 1 < sz) {
							attributedString.addAttribute(JRTextAttribute.SEARCH_HIGHLIGHT_WHITESPACE,
								"jr_search_result", htiList.get(i).getEnd(), htiList.get(i+1).getStart());
						}
					}
				}
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("No ReportContext to hold search data!");
			}
		}
	}
	
	protected void exportStyledText(JRPrintText printText, JRStyledText styledText, String tooltip, boolean hyperlinkStarted) throws IOException
	{
		StyledTextWriteContext context = new StyledTextWriteContext();

		String allText = styledText.getText();

		addSearchAttributes(styledText, printText);

		AttributedCharacterIterator allParagraphs = styledText.getAttributedString().getIterator();
		
		int tokenPosition = 0;
		int prevParagraphStart = 0;
		String prevParagraphText = null;

		boolean indentFirstLine = true;
		Integer firstLineIndent = printText.getParagraph().getFirstLineIndent(); 
		if (firstLineIndent != 0)
		{
			indentFirstLine = defaultIndentFirstLine;
			if (printText.getPropertiesMap().containsProperty(JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE))
			{
				indentFirstLine = propertiesUtil.getBooleanProperty(printText, JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE, defaultIndentFirstLine);
			}
		}

		boolean justifyLastLine = false;
		if (HorizontalTextAlignEnum.JUSTIFIED == printText.getHorizontalTextAlign())
		{
			justifyLastLine = defaultJustifyLastLine;
			if (printText.getPropertiesMap().containsProperty(JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE))
			{
				justifyLastLine = propertiesUtil.getBooleanProperty(printText, JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE, defaultJustifyLastLine);
			}
		}

		boolean isFirstParagraph = true;
		boolean isLastParagraph = false;
		
		if (
			(firstLineIndent != 0 && allText.indexOf('\n') > 0)
			|| (!indentFirstLine || justifyLastLine)
			)
		{
			StringTokenizer tkzer = new StringTokenizer(allText, "\n", true);

			// text is split into paragraphs, using the newline character as delimiter
			while (tkzer.hasMoreTokens()) 
			{
				String token = tkzer.nextToken();

				if ("\n".equals(token))
				{
					exportParagraph(
						context,
						printText, allParagraphs, prevParagraphStart, prevParagraphText,
						isFirstParagraph && !indentFirstLine ? (Integer)0 : firstLineIndent,
						isLastParagraph && justifyLastLine, 
						tooltip, hyperlinkStarted
						);
					
					isFirstParagraph = false;
					isLastParagraph = !tkzer.hasMoreTokens();
					prevParagraphStart = tokenPosition + (tkzer.hasMoreTokens() || tokenPosition == 0 ? 1 : 0);
					prevParagraphText = null;
				}
				else
				{
					prevParagraphStart = tokenPosition;
					prevParagraphText = token;
				}

				tokenPosition += token.length();
			}
		}
		else
		{
			prevParagraphText = allText;
			firstLineIndent = null; // null means we don't need to use a <div> to force first line indent as it was already dealt-with in <td> style
		}
		
		if (prevParagraphStart < allText.length())
		{
			exportParagraph(
				context,
				printText, allParagraphs, prevParagraphStart, prevParagraphText,
				isFirstParagraph && !indentFirstLine ? (Integer)0 : firstLineIndent,
				justifyLastLine, // isLastParagraph would be considered true here, so no point in keeping && operation 
				tooltip, hyperlinkStarted
				);
		}
	}
	
	protected void exportParagraph(
		StyledTextWriteContext context,
		JRPrintText printText, 
		AttributedCharacterIterator allParagraphs,
		int paragraphStart,
		String paragraphText, 
		Integer firstLineIndent,
		boolean justifyLastLine,
		String tooltip, 
		boolean hyperlinkStarted
		) throws IOException
	{
		Locale locale = getTextLocale(printText);
		LineSpacingEnum lineSpacing = printText.getParagraph().getLineSpacing();
		Float lineSpacingSize = printText.getParagraph().getLineSpacingSize();
		Integer leftIndent = printText.getParagraph().getLeftIndent();
		Integer rightIndent = printText.getParagraph().getRightIndent();
		float lineSpacingFactor = printText.getLineSpacingFactor();
		Color backcolor = printText.getBackcolor();
		
		AttributedCharacterIterator paragraph = null;
		
		if (paragraphText == null)
		{
			paragraphText = "\n";
			paragraph = 
				new AttributedString(
					paragraphText,
					new AttributedString(
						allParagraphs, 
						paragraphStart, 
						paragraphStart + paragraphText.length()
						).getIterator().getAttributes()
					).getIterator();
		}
		else
		{
			paragraph = 
				new AttributedString(
					allParagraphs, 
					paragraphStart, 
					paragraphStart + paragraphText.length()
					).getIterator();
		}

		if (
			firstLineIndent != null || justifyLastLine 
			|| (leftIndent != null && leftIndent > 0)
			|| (rightIndent != null && rightIndent > 0)
			)
		{
			writer.write("<div style=\"");
			if (firstLineIndent != null)
			{
				writer.write("text-indent:" + firstLineIndent + "px;");
			}
			if (justifyLastLine)
			{
				writer.write("text-align-last:justify;");
			}
			if (leftIndent != null && leftIndent > 0)
			{
				writer.write("padding-left:" + leftIndent + "px;");
			}
			if (rightIndent != null && rightIndent > 0)
			{
				writer.write("padding-right:" + rightIndent + "px;");
			}
			writer.write("\">");
		}
		
		int runLimit = 0;

		boolean first = true;
		boolean startedSpan = false;

		while (runLimit < paragraphText.length() && (runLimit = paragraph.getRunLimit()) <= paragraphText.length())
		{
			//if there are several text runs, write the tooltip into a parent <span>
			if (first && runLimit < paragraphText.length() && tooltip != null)
			{
				startedSpan = true;
				writer.write("<span title=\"");
				writer.write(JRStringUtil.encodeXmlAttribute(tooltip));
				writer.write("\">");
				//reset the tooltip so that inner <span>s to not use it
				tooltip = null;
			}
			first = false;

			Map<Attribute,Object> attributes = paragraph.getAttributes();
			boolean highlightWhitespaceStarted = false;
			boolean highlightStarted = false;

			String searchResultWhitespaceClasses = (String) attributes.get(JRTextAttribute.SEARCH_HIGHLIGHT_WHITESPACE);
			if (searchResultWhitespaceClasses != null)
			{
				highlightWhitespaceStarted = true;
				writer.write("<span class=\"" + searchResultWhitespaceClasses + "\">");
			}

			String searchResultClasses = (String) attributes.get(JRTextAttribute.SEARCH_HIGHLIGHT);
			if (searchResultClasses != null)
			{
				highlightStarted = true;
				writer.write("<span class=\"" + searchResultClasses + "\">");
			}

			String textRunStyle = 
				getTextRunStyle(
					attributes,
					locale,
					lineSpacing,
					lineSpacingSize,
					lineSpacingFactor,
					backcolor,
					hyperlinkStarted
					);
			
			String runText = paragraphText.substring(paragraph.getIndex(), runLimit);

			context.next(attributes, runText);

			//if (context.listItemStartsWithNewLine() && !context.isListItemStart() && (context.isListItemEnd() || context.isListStart() || context.isListEnd()))
			//{
			//	runText = runText.substring(1);
			//}

			context.writeLists(new HtmlStyledTextListWriter(textRunStyle));

			exportStyledTextRun(
				textRunStyle,
				attributes,
				runText,
				tooltip,
				hyperlinkStarted
			);

			if (highlightWhitespaceStarted)
			{
				writer.write("</span>");
			}

			if (highlightStarted)
			{
				writer.write("</span>");
			}

			paragraph.setIndex(runLimit);
		}

		context.next(null, null);

		context.writeLists(new HtmlStyledTextListWriter(null));

		if (startedSpan)
		{
			writer.write("</span>");
		}
		
		if (
			firstLineIndent != null || justifyLastLine
			|| (leftIndent != null && leftIndent > 0)
			|| (rightIndent != null && rightIndent > 0)
			)
		{
			writer.write("</div>");
		}
	}
	
	protected void exportStyledTextRun(
		String textRunStyle,
		Map<Attribute,Object> attributes,
		String text,
		String tooltip,
		boolean hyperlinkStarted
		) throws IOException
	{
		boolean localHyperlink = false;
		JRPrintHyperlink hyperlink = (JRPrintHyperlink)attributes.get(JRTextAttribute.HYPERLINK);
		if (!hyperlinkStarted && hyperlink != null)
		{
			localHyperlink = startHyperlink(hyperlink);
		}

		writer.write("<span style=\"");
		writer.write(textRunStyle);
		writer.write("\"");

		if (tooltip != null)
		{
			writer.write(" title=\"");
			writer.write(JRStringUtil.encodeXmlAttribute(tooltip));
			writer.write("\"");
		}
			
		writer.write(">");

		StringTokenizer tkzer = new StringTokenizer(text, "\n\u0085", true);

		// text is split into paragraphs here because it might have not been split during initial styled text processing
		// and was processed as a whole because there was no need to do so due to lack of paragraph styling; 
		// htmlEncode(String) no longer takes care of newline characters, so we do it here
		while (tkzer.hasMoreTokens()) 
		{
			String token = tkzer.nextToken();
			
			if ("\n".equals(token))
			{
				writer.write("<br/>");
			}
			else if ("\u0085".equals(token))
			{
				writer.write("<br aria-hidden=\"true\"/>");
			}
			else
			{
				writer.write(
					JRStringUtil.htmlEncode(token)
					);
			}
		}

		writer.write("</span>");

		if (localHyperlink)
		{
			endHyperlink();
		}
	}
		
	protected String getTextRunStyle(
		Map<Attribute,Object> attributes,
		Locale locale,
		LineSpacingEnum lineSpacing,
		Float lineSpacingSize,
		float lineSpacingFactor,
		Color backcolor,
		boolean hyperlinkStarted
		) throws IOException
	{
		StringBuilder styleBuffer = new StringBuilder();
		
		boolean isBold = TextAttribute.WEIGHT_BOLD.equals(attributes.get(TextAttribute.WEIGHT));
		boolean isItalic = TextAttribute.POSTURE_OBLIQUE.equals(attributes.get(TextAttribute.POSTURE));

		String fontFamily = resolveFontFamily(attributes, locale);

		// do not put single quotes around family name here because the value might already contain quotes, 
		// especially if it is coming from font extension export configuration
		styleBuffer.append("font-family: ");
		// don't encode single quotes as the output would be too verbose and too much of a chance compared to previous releases
		styleBuffer.append(JRStringUtil.encodeXmlAttribute(fontFamily, true)); 
		styleBuffer.append("; ");

		Color forecolor = (Color)attributes.get(TextAttribute.FOREGROUND);
		if (!hyperlinkStarted || !Color.black.equals(forecolor))
		{
			styleBuffer.append("color: ");
			styleBuffer.append(JRColorUtil.getCssColor(forecolor));
			styleBuffer.append("; ");
		}

		Color runBackcolor = (Color)attributes.get(TextAttribute.BACKGROUND);
		if (runBackcolor != null && !runBackcolor.equals(backcolor))
		{
			styleBuffer.append("background-color: ");
			styleBuffer.append(JRColorUtil.getCssColor(runBackcolor));
			styleBuffer.append("; ");
		}

		styleBuffer.append("font-size: ");
		styleBuffer.append(toSizeUnit((Float)attributes.get(TextAttribute.SIZE)));
		styleBuffer.append(";");
			
		switch (lineSpacing)
		{
			case ONE_AND_HALF:
			{
				if (lineSpacingFactor == 0)
				{
					styleBuffer.append(" line-height: 1.5;");
				}
				else
				{
					styleBuffer.append(" line-height: " + lineSpacingFactor + ";");
				}
				break;
			}
			case DOUBLE:
			{
				if (lineSpacingFactor == 0)
				{
					styleBuffer.append(" line-height: 2.0;");
				}
				else
				{
					styleBuffer.append(" line-height: " + lineSpacingFactor + ";");
				}
				break;
			}
			case PROPORTIONAL:
			{
				if (lineSpacingSize != null) {
					styleBuffer.append(" line-height: " + lineSpacingSize + ";");
				}
				break;
			}
			case AT_LEAST:
			case FIXED:
			{
				if (lineSpacingSize != null) {
					styleBuffer.append(" line-height: " + lineSpacingSize + "px;");
				}
				break;
			}
			case SINGLE:
			default:
			{
				if (lineSpacingFactor == 0)
				{
					styleBuffer.append(" line-height: 1; *line-height: normal;");
				}
				else
				{
					styleBuffer.append(" line-height: " + lineSpacingFactor + ";");
				}
				break;
			}
		}

		/*
		if (!horizontalAlignment.equals(CSS_TEXT_ALIGN_LEFT))
		{
			styleBuffer.append(" text-align: ");
			styleBuffer.append(horizontalAlignment);
			styleBuffer.append(";");
		}
		*/

		if (isBold)
		{
			styleBuffer.append(" font-weight: bold;");
		}
		if (isItalic)
		{
			styleBuffer.append(" font-style: italic;");
		}
		if (TextAttribute.UNDERLINE_ON.equals(attributes.get(TextAttribute.UNDERLINE)))
		{
			styleBuffer.append(" text-decoration: underline;");
		}
		if (TextAttribute.STRIKETHROUGH_ON.equals(attributes.get(TextAttribute.STRIKETHROUGH)))
		{
			styleBuffer.append(" text-decoration: line-through;");
		}

		if (TextAttribute.SUPERSCRIPT_SUPER.equals(attributes.get(TextAttribute.SUPERSCRIPT)))
		{
			styleBuffer.append(" vertical-align: super;");
		}
		else if (TextAttribute.SUPERSCRIPT_SUB.equals(attributes.get(TextAttribute.SUPERSCRIPT)))
		{
			styleBuffer.append(" vertical-align: sub;");
		}
		
		return styleBuffer.toString();
	}

	protected class TableVisitor implements CellVisitor<TablePosition, Void, IOException>
	{
		private final Tabulator tabulator;
		private final PrintElementVisitor<TableCell> elementVisitor;
		
		public TableVisitor(Tabulator tabulator, PrintElementVisitor<TableCell> elementVisitor)
		{
			this.tabulator = tabulator;
			this.elementVisitor = elementVisitor;
		}
		
		@Override
		public Void visit(ElementCell cell, TablePosition position)
		{
			TableCell tableCell = tabulator.getTableCell(position, cell);
			JRPrintElement element = tableCell.getElement();
			element.accept(elementVisitor, tableCell);
			return null;
		}

		@Override
		public Void visit(SplitCell cell, TablePosition position)
		{
			//NOP
			return null;
		}

		@Override
		public Void visit(FrameCell frameCell, TablePosition position) throws IOException
		{
			TableCell tableCell = tabulator.getTableCell(position, frameCell);
			HtmlExporter.this.writeFrameCell(tableCell);
			return null;
		}

		@Override
		public Void visit(LayeredCell layeredCell, TablePosition position)
				throws IOException
		{
			TableCell tableCell = tabulator.getTableCell(position, layeredCell);
			HtmlExporter.this.writeLayers(layeredCell.getLayers(), this, tableCell);
			return null;
		}

		@Override
		public Void visit(NestedTableCell nestedTableCell, TablePosition position) throws IOException
		{
			TableCell tableCell = tabulator.getTableCell(position, nestedTableCell);
			HtmlExporter.this.writeNestedTable(nestedTableCell.getTable(), this, tableCell);
			return null;
		}
	}

	protected class CellElementVisitor implements PrintElementVisitor<TableCell>
	{
		@Override
		public void visit(JRPrintText textElement, TableCell cell)
		{
			try
			{
				writeText(textElement, cell);
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void visit(JRPrintImage image, TableCell cell)
		{
			try
			{
				writeImage(image, cell);
			}
			catch (IOException | JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void visit(JRPrintRectangle rectangle, TableCell cell)
		{
			try
			{
				writeRectangle(rectangle, cell);
			} 
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void visit(JRPrintLine line, TableCell cell)
		{
			try
			{
				writeLine(line, cell);
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void visit(JRPrintEllipse ellipse, TableCell cell)
		{
			try
			{
				writeEllipse(ellipse, cell);
			} 
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void visit(JRPrintFrame frame, TableCell cell)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INTERNAL_ERROR,  
					(Object[])null 
					);
		}

		@Override
		public void visit(JRGenericPrintElement printElement, TableCell cell)
		{
			try
			{
				writeGenericElement(printElement, cell);
			} 
			catch (IOException | JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}

	protected class ExporterContext extends BaseExporterContext implements JRHtmlExporterContext
	{
		@Override
		public String getHyperlinkURL(JRPrintHyperlink link)
		{
			return HtmlExporter.this.getHyperlinkURL(link);
		}
	}
	
	protected class HtmlStyledTextListWriter implements StyledTextListWriter
	{
		private String textRunStyle;
		
		public HtmlStyledTextListWriter(String textRunStyle)
		{
			this.textRunStyle = textRunStyle;
		}

		@Override
		public void startUl() 
		{
			try
			{
				writer.write("<ul>");
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void endUl() 
		{
			try
			{
				writer.write("</ul>");
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void startOl(String type, int cutStart) 
		{
			try
			{
				writer.write("<ol");
				if (type != null)
				{
					switch (type)
					{
						case "a" : { type = "lower-alpha"; break; }
						case "A" : { type = "upper-alpha"; break; }
						case "i" : { type = "lower-roman"; break; }
						case "I" : { type = "upper-roman"; break; }
						case "1" :
						default : { type = null; }
					}
					if (type != null)
					{
						writer.write(" style=\"list-style-type: " + type + "\"");
					}
				}
				if (cutStart > 1)
				{
					writer.write(" start=\"" + cutStart + "\"");
				}
				writer.write(">");
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void endOl() 
		{
			try
			{
				writer.write("</ol>");
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void startLi(boolean noBullet) 
		{
			try
			{
				writer.write("<li");
				if (noBullet || textRunStyle != null)
				{
					writer.write(" style=\"");
					writer.write(noBullet ? "list-style-type: none; " : "");
					writer.write(textRunStyle == null ? "" : textRunStyle);
					writer.write("\"");
				}
				writer.write(">");
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}

		@Override
		public void endLi() 
		{
			try
			{
				writer.write("</li>");
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}
}
