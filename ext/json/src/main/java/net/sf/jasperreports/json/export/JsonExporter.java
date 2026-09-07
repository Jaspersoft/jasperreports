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
package net.sf.jasperreports.json.export;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintBookmark;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.PrintParts;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.HtmlFontFamily;
import net.sf.jasperreports.engine.export.HtmlFontUtil;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.util.HyperlinkData;
import net.sf.jasperreports.engine.util.PartsUtil;
import net.sf.jasperreports.export.ExportInterruptedException;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.HtmlReportConfiguration;
import net.sf.jasperreports.jackson.util.JacksonUtil;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JsonExporter extends JRAbstractExporter<JsonReportConfiguration, JsonExporterConfiguration, JsonExporterOutput, JsonExporterContext>
{
	private static final Log log = LogFactory.getLog(JsonExporter.class);
	
	public static final String REPORT_CONTEXT_PARAMETER_WEB_FONTS = "net.sf.jasperreports.html.webfonts"; //FIXME7 deprecate

	public static final String JSON_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "json";
	
	protected static final String JSON_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.json.";
	
	protected Writer writer;
	protected int reportIndex;
	protected int pageIndex;
	private boolean gotFirstJsonFragment;
	private JacksonUtil jacksonUtil;
	private List<HyperlinkData> hyperlinksData;
	
	public JsonExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	public JsonExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);

		exporterContext = new ExporterContext();
		jacksonUtil = JacksonUtil.getInstance(jasperReportsContext);
		hyperlinksData = new ArrayList<>();
	}


	@Override
	protected Class<JsonExporterConfiguration> getConfigurationInterface()
	{
		return JsonExporterConfiguration.class;
	}


	@Override
	protected Class<JsonReportConfiguration> getItemConfigurationInterface()
	{
		return JsonReportConfiguration.class;
	}
	

	@Override
	public String getExporterKey()
	{
		return JSON_EXPORTER_KEY;
	}

	@Override
	public String getExporterPropertiesPrefix()
	{
		return JSON_EXPORTER_PROPERTIES_PREFIX;
	}

	@Override
	public void exportReport() throws JRException
	{
		/*   */
		ensureJasperReportsContext();
		ensureInput();

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
			resetExportContext();//FIXMEEXPORT check if using same finally is correct; everywhere
		}
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
	}
	
	protected void exportReportToWriter() throws JRException, IOException
	{
		writer.write("{\n");

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

					exportPage(page);

					if (reportIndex < items.size() - 1 || pageIndex < endPageIndex)
					{
						writer.write("\n");
					}
				}
			}
		}

		writer.write("\n}");

		boolean flushOutput = getCurrentConfiguration().isFlushOutput();
		if (flushOutput)
		{
			writer.flush();
		}
	}
	
	protected void exportPage(JRPrintPage page) throws IOException, ExportInterruptedException
	{
		Collection<JRPrintElement> elements = page.getElements();
		Boolean exportReportComponentsOnly = getCurrentConfiguration().isReportComponentsExportOnly();

		exportReportConfig();

		if (exportReportComponentsOnly == null)
		{
			exportReportComponentsOnly = false;
		}

		if (!exportReportComponentsOnly)
		{
			exportElements(elements);
			exportWebFonts();
			exportHyperlinks();
			exportClickableElements();
		}

		exportBookmarks();
		exportParts();

		JRExportProgressMonitor progressMonitor = getCurrentItemConfiguration().getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}
	
	protected void exportElements(Collection<JRPrintElement> elements) throws IOException, ExportInterruptedException
	{
		if (elements != null && elements.size() > 0)
		{
			for (Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
			{
				checkInterrupted();
				JRPrintElement element = it.next();

				if (filter == null || filter.isToExport(element))
				{
					if (element instanceof JRPrintFrame)
					{
						exportFrame((JRPrintFrame)element);
					}
					else if (element instanceof JRGenericPrintElement)
					{
						exportGenericElement((JRGenericPrintElement) element);
					}
				}
			}
		}
	}
	
	protected void exportFrame(JRPrintFrame frame) throws IOException, ExportInterruptedException
	{
		exportElements(frame.getElements());
	}

	protected interface PrintBookmarkMixin {
		@JsonIgnore
		int getLevel();
	}

	protected void exportBookmarks() throws IOException
	{
		List<PrintBookmark> bookmarks = jasperPrint.getBookmarks();
		if (bookmarks != null && bookmarks.size() > 0)
		{
			if (gotFirstJsonFragment)
			{
				writer.write(",\n");
			} else
			{
				gotFirstJsonFragment = true;
			}
			
			writer.write("\"bkmrk_" + (bookmarks.hashCode() & 0x7FFFFFFF) + "\": ");
			writeBookmarks(bookmarks, writer, jacksonUtil);
		}
	}

	protected void exportReportConfig() throws IOException
	{
		if (gotFirstJsonFragment)
		{
			writer.write(",\n");
		} else
		{
			gotFirstJsonFragment = true;
		}

		writer.write("\"reportConfig\": {");
		writer.write("\"id\": \"reportConfig\",");
		writer.write("\"type\": \"reportConfig\",");

		List<JRPropertiesUtil.PropertySuffix> viewerProps = propertiesUtil.getAllProperties(
				jasperPrint,
				JRPropertiesUtil.PROPERTY_PREFIX + "htmlviewer.");

		writer.write("\"reportConfig\": " + jacksonUtil.getJsonString(viewerProps));
		writer.write("}");
	}

	public static void writeBookmarks(List<PrintBookmark> bookmarks, Writer writer, JacksonUtil jacksonUtil) throws IOException
	{
		// exclude the methods marked with @JsonIgnore in PrintBookmarkMixin from PrintBookmark implementation
		jacksonUtil.getObjectMapper().addMixIn(PrintBookmark.class, PrintBookmarkMixin.class);

		writer.write("{");

		writer.write("\"id\": \"bkmrk_" + (bookmarks.hashCode() & 0x7FFFFFFF) + "\",");
		writer.write("\"type\": \"bookmarks\",");
		writer.write("\"bookmarks\": " + jacksonUtil.getJsonString(bookmarks));

		writer.write("}");
	}

	protected void exportParts() throws IOException
	{
		PrintParts parts = PartsUtil.instance(jasperReportsContext).getVisibleParts(jasperPrint);

		if (parts != null && parts.hasParts())
		{
			if (gotFirstJsonFragment)
			{
				writer.write(",\n");
			} else
			{
				gotFirstJsonFragment = true;
			}
			
			writer.write("\"parts_" + (parts.hashCode() & 0x7FFFFFFF) + "\": ");
			writeParts(jasperPrint, parts, writer);
		}
	}

	public static void writeParts(JasperPrint jasperPrint, Writer writer) throws IOException
	{
		writeParts(DefaultJasperReportsContext.getInstance(), jasperPrint, writer);
	}

	public static void writeParts(JasperReportsContext jasperReportsContext,
			JasperPrint jasperPrint, Writer writer) throws IOException
	{
		PrintParts parts = PartsUtil.instance(jasperReportsContext).getVisibleParts(jasperPrint);
		writeParts(jasperPrint, parts, writer);
	}

	public static void writeParts(JasperPrint jasperPrint, PrintParts parts, Writer writer) throws IOException
	{
		writer.write("{");

		writer.write("\"id\": \"parts_" + (parts.hashCode() & 0x7FFFFFFF) + "\",");
		writer.write("\"type\": \"reportparts\",");
		writer.write("\"parts\": [");

		if (!parts.startsAtZero())
		{
			writer.write("{\"idx\": 0, \"name\": \"");
			writer.write(JsonStringEncoder.getInstance().quoteAsString(jasperPrint.getName()));
			writer.write("\"}");
			if (parts.partCount() > 1)
			{
				writer.write(",");
			}
		}

		Iterator<Map.Entry<Integer, PrintPart>> it = parts.partsIterator();

		while (it.hasNext())
		{
			Map.Entry<Integer, PrintPart> partsEntry = it.next();
			int idx = partsEntry.getKey();
			PrintPart part = partsEntry.getValue();
			
			writer.write("{\"idx\": " + idx + ", \"name\": \"");
			writer.write(JsonStringEncoder.getInstance().quoteAsString(part.getName()));
			writer.write("\"}");
			if (it.hasNext())
			{
				writer.write(",");
			}
		}

		writer.write("]");
		writer.write("}");
	}

	protected void exportWebFonts() throws IOException
	{
		HtmlResourceHandler fontHandler = getExporterOutput().getFontHandler();
		ReportContext reportContext = getReportContext();

		if (
			fontHandler != null
			&& reportContext != null 
			&& reportContext.containsParameter(REPORT_CONTEXT_PARAMETER_WEB_FONTS)
			) 
		{
			Map<String, HtmlFontFamily> fontsToProcess = 
				(Map<String, HtmlFontFamily>)reportContext.getParameterValue(REPORT_CONTEXT_PARAMETER_WEB_FONTS);

			ObjectMapper mapper = new ObjectMapper();
			ArrayNode webFonts = mapper.createArrayNode();

			for (HtmlFontFamily htmlFontFamily : fontsToProcess.values())
			{
				ObjectNode objNode = mapper.createObjectNode();
				objNode.put("id", htmlFontFamily.getId());
				String cssResourceName = HtmlFontUtil.getFontCSSResourceName(htmlFontFamily);
				objNode.put("path", fontHandler.getResourcePath(cssResourceName));
				webFonts.add(objNode);
			}
			
			if (gotFirstJsonFragment)
			{
				writer.write(",\n");
			} else
			{
				gotFirstJsonFragment = true;
			}
			writer.write("\"webfonts_" + (webFonts.hashCode() & 0x7FFFFFFF) + "\": {");

			writer.write("\"id\": \"webfonts_" + (webFonts.hashCode() & 0x7FFFFFFF) + "\",");
			writer.write("\"type\": \"webfonts\",");
			writer.write("\"webfonts\": " + jacksonUtil.getJsonString(webFonts));

			writer.write("}");
		}
	}

	protected void exportHyperlinks() throws IOException
	{
		ReportContext reportContext = getReportContext();
		String hyperlinksParameter = "net.sf.jasperreports.html.hyperlinks";
		if (reportContext != null && reportContext.containsParameter(hyperlinksParameter)) {
			List<HyperlinkData> contextHyperlinksData = (List<HyperlinkData>) reportContext.getParameterValue(hyperlinksParameter);
			hyperlinksData.addAll(contextHyperlinksData);
		}
		if (hyperlinksData.size() > 0) {
			String id = "hyperlinks_" + (hyperlinksData.hashCode() & 0x7FFFFFFF);
			if (gotFirstJsonFragment)
			{
				writer.write(",\n");
			} else
			{
				gotFirstJsonFragment = true;
			}
			writer.write("\"" + id + "\": {");

			writer.write("\"id\": \"" + id + "\",");
			writer.write("\"type\": \"hyperlinks\",");
			writer.write("\"hyperlinks\": ");

			ObjectMapper mapper = new ObjectMapper();
			ArrayNode hyperlinkArray = mapper.createArrayNode();

			for (HyperlinkData hd: hyperlinksData) {
				JRPrintHyperlink hyperlink = hd.getHyperlink();
				ObjectNode hyperlinkNode = jacksonUtil.hyperlinkToJsonObject(hyperlink);

				jacksonUtil.addProperty(hyperlinkNode, "id", hd.getId());
				jacksonUtil.addProperty(hyperlinkNode, "href", hd.getHref());
				jacksonUtil.addProperty(hyperlinkNode, "selector", hd.getSelector());

				hyperlinkArray.add(hyperlinkNode);
			}

			writer.write(jacksonUtil.getJsonString(hyperlinkArray));
			writer.write("}");
		}
	}

	protected void exportClickableElements() throws IOException
	{
		ReportContext reportContext = getReportContext();
		String clickableElementsParameter = "net.sf.jasperreports.html.clickable.elements";
		if (reportContext != null && reportContext.containsParameter(clickableElementsParameter))
		{
			Boolean useClickableElements = (Boolean) reportContext.getParameterValue(clickableElementsParameter);
			if (useClickableElements)
			{
				String id = "clickable_" + UUID.randomUUID();
				if (gotFirstJsonFragment)
				{
					writer.write(",\n");
				} else
				{
					gotFirstJsonFragment = true;
				}
				writer.write("\"" + id + "\": {");

				writer.write("\"id\": \"" + id + "\",");
				writer.write("\"type\": \"clickableElements\",");
				writer.write("\"selector\": \"td[data-eluuid]\"");
				writer.write("}");
			}
		}
	}

	/**
	 *
	 */
	protected void exportGenericElement(JRGenericPrintElement element) throws IOException
	{
		GenericElementJsonHandler handler = (GenericElementJsonHandler) 
				GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
						element.getGenericType(), JSON_EXPORTER_KEY);
		
		if (handler != null)
		{
			String fragment = handler.getJsonFragment(exporterContext, element);
			if (fragment != null && !fragment.isEmpty()) {
				if (gotFirstJsonFragment) {
					writer.write(",\n");
				} else {
					gotFirstJsonFragment = true;
				}
				writer.write(fragment);
			}
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No JSON generic element handler for " 
						+ element.getGenericType());
			}
		}
	}
	
	/**
	 * 
	 */
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
							href = "#" + HtmlExporter.JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + link.getHyperlinkPage().toString();
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
							href = link.getHyperlinkReference() + "#" + HtmlExporter.JR_PAGE_ANCHOR_PREFIX + "0_" + link.getHyperlinkPage().toString();
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

	/**
	 *
	 */
	public void addHyperlinkData(HyperlinkData hyperlinkData) {
		this.hyperlinksData.add(hyperlinkData);
	}

	/**
	 *
	 */
	public void addFontFamily(HtmlFontFamily htmlFontFamily) 
	{
		ReportContext reportContext = getReportContext();
		if (reportContext != null)
		{
			Map<String, HtmlFontFamily> fontsToProcess = 
				(Map<String, HtmlFontFamily>)reportContext.getParameterValue(REPORT_CONTEXT_PARAMETER_WEB_FONTS);
			
			if (fontsToProcess == null)
			{
				fontsToProcess = new HashMap<>();
				reportContext.setParameterValue(REPORT_CONTEXT_PARAMETER_WEB_FONTS, fontsToProcess);
			}
			
			fontsToProcess.put(htmlFontFamily.getId(), htmlFontFamily);
		}
	}

	
	protected class ExporterContext extends BaseExporterContext implements JsonExporterContext
	{
		@Override
		public String getHyperlinkURL(JRPrintHyperlink link)
		{
			return resolveHyperlinkURL(reportIndex, link);
		}
	}

}
