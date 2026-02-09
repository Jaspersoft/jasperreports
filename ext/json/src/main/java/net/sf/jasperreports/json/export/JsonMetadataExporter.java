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
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.sf.jasperreports.json.export.schema.JsonMetadataProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.data.BooleanTextValue;
import net.sf.jasperreports.engine.export.data.DateTextValue;
import net.sf.jasperreports.engine.export.data.NumberTextValue;
import net.sf.jasperreports.engine.export.data.StringTextValue;
import net.sf.jasperreports.engine.export.data.TextValue;
import net.sf.jasperreports.engine.export.data.TextValueHandler;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.export.ExportInterruptedException;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.WriterExporterOutput;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonMetadataExporter extends JRAbstractExporter<JsonMetadataReportConfiguration, JsonExporterConfiguration, WriterExporterOutput, JsonExporterContext>
{

	private static final Log log = LogFactory.getLog(JsonMetadataExporter.class);

	public static final String JSON_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "json";

	protected static final String JSON_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.json.";
	
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_0
			)
	public static final String JSON_EXPORTER_PATH_PROPERTY = JSON_EXPORTER_PROPERTIES_PREFIX + "path";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_0,
			valueType = Boolean.class
			)
	public static final String JSON_EXPORTER_REPEAT_VALUE_PROPERTY = JSON_EXPORTER_PROPERTIES_PREFIX + "repeat.value";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_0_0
			)
	public static final String JSON_EXPORTER_DATA_PROPERTY = JSON_EXPORTER_PROPERTIES_PREFIX + "data";

	@Property(
			name = "net.sf.jasperreports.export.json.repeat.{path}",
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_1_0,
			valueType = Boolean.class
			)
	public static final String JSON_EXPORTER_REPEAT_PROPERTIES_PREFIX = JSON_EXPORTER_PROPERTIES_PREFIX + "repeat.";
	@Property(
			name = "net.sf.jasperreports.export.json.number.{path}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_1_0
			)
	public static final String JSON_EXPORTER_NUMBER_PROPERTIES_PREFIX = JSON_EXPORTER_PROPERTIES_PREFIX + "number.";
	@Property(
			name = "net.sf.jasperreports.export.json.date.{path}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_1_0
			)
	public static final String JSON_EXPORTER_DATE_PROPERTIES_PREFIX = JSON_EXPORTER_PROPERTIES_PREFIX + "date.";
	@Property(
			name = "net.sf.jasperreports.export.json.boolean.{path}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_1_0
			)
	public static final String JSON_EXPORTER_BOOLEAN_PROPERTIES_PREFIX = JSON_EXPORTER_PROPERTIES_PREFIX + "boolean.";
	@Property(
			name = "net.sf.jasperreports.export.json.string.{path}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_1_0
			)
	public static final String JSON_EXPORTER_STRING_PROPERTIES_PREFIX = JSON_EXPORTER_PROPERTIES_PREFIX + "string.";

	protected final DateFormat isoDateFormat = JRDataUtils.getIsoDateFormat();

	protected Writer writer;
	protected int reportIndex;
	protected int pageIndex;

	private final JsonMetadataProcessor jsonProcessor;

	public JsonMetadataExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	public JsonMetadataExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);

		exporterContext = new ExporterContext();
		jsonProcessor = new JsonMetadataProcessor();
	}


	@Override
	protected Class<JsonExporterConfiguration> getConfigurationInterface()
	{
		return JsonExporterConfiguration.class;
	}


	@Override
	protected Class<JsonMetadataReportConfiguration> getItemConfigurationInterface()
	{
		return JsonMetadataReportConfiguration.class;
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
		jsonProcessor.setWriter(writer);

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
		List<ExporterInputItem> items = exporterInput.getItems();

		for(reportIndex = 0; reportIndex < items.size(); reportIndex++)//FIXMEJSONMETA deal with batch export
		{
			ExporterInputItem item = items.get(reportIndex);

			setCurrentExporterInputItem(item);

			JsonMetadataReportConfiguration currentItemConfiguration = getCurrentItemConfiguration();

			jsonProcessor.setEscapeMembers(currentItemConfiguration.isEscapeMembers());
			String jsonSchemaResource = currentItemConfiguration.getJsonSchemaResource();

			if (jsonSchemaResource != null) {
				try (
					Scanner scanner = 
						new Scanner(
							getRepository().getInputStreamFromLocation(jsonSchemaResource), 
							StandardCharsets.UTF_8.name()
							)
					)
				{
					String jsonSchema = scanner.useDelimiter("\\A").next();
					jsonProcessor.getJsonSchema().initialize(jsonSchema);
				}
			} else {
				if (log.isWarnEnabled()) {
					log.warn("No JSON Schema provided!");
				}
			}

			List<JRPrintPage> pages = jasperPrint.getPages();
			if (pages != null && pages.size() > 0)
			{
				PageRange pageRange = getPageRange();
				int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
				int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

				JRPrintPage page = null;
				for(pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
				{
					checkInterrupted();

					page = pages.get(pageIndex);

					exportPage(page);
				}

				jsonProcessor.closeOpenNodes();
			}
		}

		boolean flushOutput = getCurrentConfiguration().isFlushOutput();
		if (flushOutput)
		{
			writer.flush();
		}
	}

	protected void exportPage(JRPrintPage page) throws IOException, ExportInterruptedException
	{
		Collection<JRPrintElement> elements = page.getElements();

		exportElements(elements);

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
			for(Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
			{
				checkInterrupted();
				JRPrintElement element = it.next();

				if (filter == null || filter.isToExport(element))
				{
					exportElement(element);

					if (element instanceof JRGenericPrintElement)
					{
						//exportElement(element);
					}
					else if (element instanceof JRPrintFrame)
					{
						exportElements(((JRPrintFrame) element).getElements());
					}
				}
			}
		}
	}

	protected void exportElement(JRPrintElement element) throws IOException
	{
		JRPropertiesMap propMap = element.getPropertiesMap();

		List<PropertySuffix> properties = JRPropertiesUtil.getProperties(element, JSON_EXPORTER_PROPERTIES_PREFIX);

		for (PropertySuffix property : properties)
		{
			String propertyPath;
			Object value;
			boolean legacyPathProperty;
			boolean repeatValue = false;

			String propertyName = property.getKey();

			if (propertyName.equals(JSON_EXPORTER_PATH_PROPERTY))
			{
				legacyPathProperty = true;
				propertyPath = property.getValue();
				repeatValue = getPropertiesUtil().getBooleanProperty(propMap, JSON_EXPORTER_REPEAT_VALUE_PROPERTY, false);
				value = null;
			}
			else
			{
				legacyPathProperty = false;
				if (propertyName.startsWith(JSON_EXPORTER_STRING_PROPERTIES_PREFIX))
				{
					propertyPath = propertyName.substring(JSON_EXPORTER_STRING_PROPERTIES_PREFIX.length());
					repeatValue = getPropertiesUtil().getBooleanProperty(propMap, JSON_EXPORTER_REPEAT_PROPERTIES_PREFIX + propertyPath, false);
					value = property.getValue();
				}
				else if (propertyName.startsWith(JSON_EXPORTER_NUMBER_PROPERTIES_PREFIX))
				{
					propertyPath = propertyName.substring(JSON_EXPORTER_NUMBER_PROPERTIES_PREFIX.length());
					repeatValue = getPropertiesUtil().getBooleanProperty(propMap, JSON_EXPORTER_REPEAT_PROPERTIES_PREFIX + propertyPath, false);
					value = Double.parseDouble(property.getValue());
				}
				else if (propertyName.startsWith(JSON_EXPORTER_DATE_PROPERTIES_PREFIX))
				{
					propertyPath = propertyName.substring(JSON_EXPORTER_DATE_PROPERTIES_PREFIX.length());
					repeatValue = getPropertiesUtil().getBooleanProperty(propMap, JSON_EXPORTER_REPEAT_PROPERTIES_PREFIX + propertyPath, false);
					try
					{
						value = isoDateFormat.parse(property.getValue());
					}
					catch (ParseException e)
					{
						throw new JRRuntimeException(e);
					}
				}
				else if (propertyName.startsWith(JSON_EXPORTER_BOOLEAN_PROPERTIES_PREFIX))
				{
					propertyPath = propertyName.substring(JSON_EXPORTER_BOOLEAN_PROPERTIES_PREFIX.length());
					repeatValue = getPropertiesUtil().getBooleanProperty(propMap, JSON_EXPORTER_REPEAT_PROPERTIES_PREFIX + propertyPath, false);
					value = Boolean.parseBoolean(property.getValue());
				}
				else
				{
					propertyPath = null;
					value = null;
				}
			}

			if (propertyPath != null && !propertyPath.isEmpty())
			{
				jsonProcessor.processElement(
						() -> {
							if (log.isDebugEnabled())
							{
								log.debug("found element with propertyPath: " + propertyPath);
							}

							if (legacyPathProperty)
							{
								return getValue(element);
							}

							return value;
						}, propertyPath, repeatValue);
			}
		}
	}

	private Object getValue(JRPrintElement element)
	{
		Object value;
		final String textStr;
		final boolean hasDataProp;
		if (element.getPropertiesMap().containsProperty(JSON_EXPORTER_DATA_PROPERTY)) 
		{
			hasDataProp = true;
			textStr = element.getPropertiesMap().getProperty(JSON_EXPORTER_DATA_PROPERTY);
		}
		else
		{
			hasDataProp = false;
			if (element instanceof JRPrintText)
			{
				JRPrintText printText = (JRPrintText)element; 
				JRStyledText styledText = getStyledText(printText);

				if (styledText != null)
				{
					textStr = styledText.getText();
				}
				else
				{
					textStr = null;
				}
			}
			else
			{
				textStr = null;
			}
		}

		if (element instanceof JRPrintText)
		{
			JRPrintText printText = (JRPrintText)element; 
			TextValue textValue = getTextValue(printText, textStr);
			LocalTextValueHandler handler = new LocalTextValueHandler(hasDataProp, textStr);
			try
			{
				textValue.handle(handler);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			value = handler.getValue();
		}
		else
		{
			value = textStr;
		}

		return value;
	}

	@Override
	protected JRStyledText getStyledText(JRPrintText textElement)
	{
		JRStyledText styledText = textElement.getFullStyledText(noneSelector);
		
		if (styledText != null && !JRCommonText.MARKUP_NONE.equals(textElement.getMarkup()))
		{
			styledText = JRStyledTextUtil.getBulletedText(styledText);
		}

		return styledText;
	}

	protected class ExporterContext extends BaseExporterContext implements JsonExporterContext
	{
		@Override
		public String getHyperlinkURL(JRPrintHyperlink link)
		{
			return ""; // FIXMEJSONMETA should we treat hyperlinks?
		}
	}

	private class LocalTextValueHandler implements TextValueHandler
	{
		Object value;
		boolean hasDataProp;
		String textStr;

		public LocalTextValueHandler(boolean hasDataProp, String textStr)
		{
			this.hasDataProp = hasDataProp;
			this.textStr = textStr;
		}

		public Object getValue()
		{
			return value;
		}

		@Override
		public void handle(StringTextValue textValue) {
			value = textValue.getText();
		}

		@Override
		public void handle(NumberTextValue textValue) {
			if (hasDataProp) {
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
		}

		@Override
		public void handle(DateTextValue textValue) {
			if (hasDataProp) {
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
				value = textValue.getValue();
			}
		}

		@Override
		public void handle(BooleanTextValue textValue) {
			value = hasDataProp ? Boolean.valueOf(textStr) : textValue.getValue();
		}

	}
}
