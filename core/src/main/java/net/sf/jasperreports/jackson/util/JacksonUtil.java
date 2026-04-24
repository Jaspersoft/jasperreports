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
package net.sf.jasperreports.jackson.util;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamWriter2;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.util.DefaultXmlPrettyPrinter;

import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentsBundle;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartComponentsBundle;
import net.sf.jasperreports.engine.part.PartComponentsEnvironment;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRValueStringUtils;
import net.sf.jasperreports.engine.xml.ReportWriterConfiguration;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JacksonUtil
{
	/**
	 * 
	 */
	private static final String OBJECT_MAPPER_CONTEXT_KEY = "net.sf.jasperreports.jackson.object.mapper";
	private static final String XML_MAPPER_CONTEXT_KEY = "net.sf.jasperreports.jackson.xml.mapper";
	
	private final JasperReportsContext jasperReportsContext;
	
	
	private final static DefaultXmlPrettyPrinter TAB_XML_PRETTY_PRINTER = new DefaultXmlPrettyPrinter();
	
	static
	{
		DefaultXmlPrettyPrinter.Indenter indenter = new DefaultXmlPrettyPrinter.Indenter() 
		{
			@Override
			public void writeIndentation(XMLStreamWriter2 sw, int level) throws XMLStreamException 
			{
	            sw.writeRaw("\n");
	            for (int i = 0; i < level; i++)
	            {
		            sw.writeRaw("\t");
	            }
			}
			
			@Override
			public void writeIndentation(JsonGenerator g, int level) throws IOException 
			{
	            g.writeRaw("\n");
	            for (int i = 0; i < level; i++)
	            {
		            g.writeRaw("\t");
	            }
			}
			
			@Override
			public boolean isInline() 
			{
				return false;
			}
		};
		TAB_XML_PRETTY_PRINTER.indentArraysWith(indenter);
		TAB_XML_PRETTY_PRINTER.indentObjectsWith(indenter);
	}


	/**
	 *
	 */
	protected JacksonUtil(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	public static JacksonUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return new JacksonUtil(jasperReportsContext);
	}
	
	
	public ObjectMapper getObjectMapper()
	{
		ObjectMapper mapper = (ObjectMapper)jasperReportsContext.getOwnValue(OBJECT_MAPPER_CONTEXT_KEY);
		if (mapper == null)
		{
			mapper = new ObjectMapper();
			
			configureMapper(mapper);

			jasperReportsContext.setValue(OBJECT_MAPPER_CONTEXT_KEY, mapper);
		}
		return mapper;
	}
	
	
	public XmlMapper getXmlMapper()
	{
		//FIXME if the JasperReportsContext instance is shared but the thread classloader changes, 
		// the cached object does not have extensions from the current classloader
		XmlMapper mapper = (XmlMapper)jasperReportsContext.getOwnValue(XML_MAPPER_CONTEXT_KEY);
		if (mapper == null)
		{
			mapper = new XmlMapper();
			
			configureMapper(mapper);
			
			jasperReportsContext.setValue(XML_MAPPER_CONTEXT_KEY, mapper);
		}
		return mapper;
	}
	
	
	private void configureMapper(ObjectMapper mapper)
	{
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(Include.NON_NULL);
		//mapper.setSerializationInclusion(Include.NON_EMPTY); // these are enum values
		//mapper.setSerializationInclusion(Include.NON_DEFAULT);
		
		List<JacksonMapping> jacksonMappings = jasperReportsContext.getExtensions(JacksonMapping.class);
		for (JacksonMapping jacksonMapping : jacksonMappings)
		{
			register(mapper, jacksonMapping);
		}
		
		ComponentsEnvironment componentsEnvironment = ComponentsEnvironment.getInstance(jasperReportsContext);
		for (ComponentsBundle bundle : componentsEnvironment.getBundles())
		{
			for (Class<? extends Component> componentType : bundle.getComponentTypes())
			{
				mapper.registerSubtypes(componentType);
			}
		}
		
		PartComponentsEnvironment partComponentsEnvironment = PartComponentsEnvironment.getInstance(jasperReportsContext);
		for (PartComponentsBundle bundle : partComponentsEnvironment.getBundles())
		{
			for (Class<? extends PartComponent> componentType : bundle.getComponentTypes())
			{
				mapper.registerSubtypes(componentType);
			}
		}
		
		ReportWriterConfiguration reportWriterConfig = new ReportWriterConfiguration(jasperReportsContext);
		
		SimpleModule module = new SimpleModule();
		// these global mappings are here only for implementations that need context or for third-party classes/interfaces
		module.addSerializer(UUID.class, new UuidSerializer(reportWriterConfig.isExcludeUuids()));
		module.addSerializer(Color.class, new ColorSerializer());
		module.addDeserializer(Color.class, new ColorDeserializer());
		module.addSerializer(JRPropertiesMap.class, new PropertiesMapSerializer(reportWriterConfig));
		// for our own classes and interfaces, we use class level annotations
		mapper.registerModule(module);

		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter(VersionPropertyFilter.FILTER_ID, new VersionPropertyFilter());
		filterProvider.setFailOnUnknownId(false);
		mapper.setFilterProvider(filterProvider);
		mapper.addMixIn(Object.class, VersionFilterMixin.class);

		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	
	
	/**
	 *
	 */
	private static void register(ObjectMapper mapper, JacksonMapping mapping)
	{
		try
		{
			Class<?> clazz = JRClassLoader.loadClassForRealName(mapping.getClassName());
			// in theory, we could register subtypes without specifying a name here, just the class,
			// using properties without suffix in the extension config file.
			// in such case, the name would be provided by the @JsonTypeName annotation in the registered class.
			// but there is really no reason to do that, especially since it would rely on class hierarchy introspection
			// and would thus need additional processing, without any apparent advantage
			mapper.registerSubtypes(new NamedType(clazz, mapping.getName()));
		}
		catch (ClassNotFoundException e)
		{
			throw new JRRuntimeException(e);
		}
	}


	/**
	 *
	 */
	public <T> T loadObject(String jsonData, Class<T> clazz)
	{
		T result = null;
		if (jsonData != null) 
		{
			ObjectMapper mapper = getObjectMapper();
			
			try 
			{
				result = mapper.readValue(jsonData, clazz);
			}
			catch (JsonProcessingException e) 
			{
				throw new JRRuntimeException(e);
			}
		}
		return result;
	}


	/**
	 *
	 */
	public <T> T loadXml(String xmlData, Class<T> clazz)
	{
		T result = null;
		if (xmlData != null) 
		{
			XmlMapper mapper = getXmlMapper();
			
			try 
			{
				result = mapper.readValue(xmlData, clazz);
			}
			catch (JsonProcessingException e) 
			{
				throw new JRRuntimeException(e);
			}
		}
		return result;
	}


	/**
	 *
	 */
	public <T> T loadXml(InputStream is, Class<T> clazz)
	{
		T result = null;
		if (is != null) 
		{
			XmlMapper mapper = getXmlMapper();
			
			try 
			{
				result = mapper.readValue(is, clazz);
			}
			catch (JacksonException e) 
			{
				throw new JacksonRuntimeException(e);
			}
			catch (IOException e) 
			{
				throw new JRRuntimeException(e);
			}
		}
		return result;
	}


	/**
	 * 
	 */
	public <T> List<T> loadList(String jsonData, Class<T> clazz)
	{
		List<T> result = null;
		if (jsonData != null) 
		{
			ObjectMapper mapper = getObjectMapper();
			
			try 
			{
				result = mapper.readValue(jsonData, mapper.getTypeFactory().constructParametricType(List.class, clazz));
			}
			catch (IOException e) 
			{
				throw new JRRuntimeException(e);
			}
		}
		return result;
	}


	/*
	 * 
	 */
	public <T> List<T> loadAsList(String jsonData, Class<T> clazz) {
		List<T> result = null;
		
		if (jsonData != null) {
			String trimmedData = jsonData.trim();
			if (trimmedData.startsWith("{")) {
				result = new ArrayList<>();
				result.add(loadObject(trimmedData, clazz));
			} else if (trimmedData.startsWith("[")) {
				result = loadList(trimmedData, clazz);
			}
		}
		
		return result;
	}
	

	/**
	 * 
	 */
	public String getJsonString(Object object) 
	{
		ObjectMapper mapper = getObjectMapper();
		try
		{
			return mapper.writeValueAsString(object);
		} 
		catch (IOException e) 
		{
			throw new JRRuntimeException(e);
		}
	}

	public String getIndentedJsonString(Object object)
	{
		ObjectMapper mapper = getObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try
		{
			return mapper.writeValueAsString(object);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			mapper.disable(SerializationFeature.INDENT_OUTPUT);
		}
	}
	
	
	/**
	 * 
	 */
	public String getXmlString(Object object)
	{
		XmlMapper mapper = getXmlMapper();
		try
		{
			return mapper.writer(TAB_XML_PRETTY_PRINTER).writeValueAsString(object);
		} 
		catch (IOException e) 
		{
			throw new JRRuntimeException(e);
		}
	}
	
	public void writeXml(Object object, Writer writer)
	{
		writeXml(object, writer, null);
	}

	public void writeXml(Object object, Writer writer, String targetVersion)
	{
		XmlMapper mapper = getXmlMapper();
		try
		{
			ObjectWriter objectWriter = mapper.writer(TAB_XML_PRETTY_PRINTER);
			if (targetVersion != null)
			{
				objectWriter = objectWriter.withAttribute(
					VersionPropertyFilter.ATTRIBUTE_TARGET_VERSION, targetVersion
				);
			}
			objectWriter.writeValue(writer, object);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	
	/**
	 *
	 */
	public ObjectNode hyperlinkToJsonObject(JRPrintHyperlink hyperlink)
	{
		ObjectNode hyperlinkNode = getObjectMapper().createObjectNode();

		addProperty(hyperlinkNode, "type", hyperlink.getLinkType());
		addProperty(hyperlinkNode, "typeValue", hyperlink.getHyperlinkType().getName());
		addProperty(hyperlinkNode, "target", hyperlink.getLinkTarget());
		addProperty(hyperlinkNode, "targetValue", hyperlink.getHyperlinkTarget().getHtmlValue(), hyperlink.getLinkTarget());
		addProperty(hyperlinkNode, "tooltip", hyperlink.getHyperlinkTooltip());
		addProperty(hyperlinkNode, "anchor", hyperlink.getHyperlinkAnchor());
		addProperty(hyperlinkNode, "page", String.valueOf(hyperlink.getHyperlinkPage()));
		addProperty(hyperlinkNode, "reference", hyperlink.getHyperlinkReference());

		JRPrintHyperlinkParameters hParams =  hyperlink.getHyperlinkParameters();
		if (hParams != null && hParams.getParameters().size() > 0)
		{
			ObjectNode params = getObjectMapper().createObjectNode();

			for (JRPrintHyperlinkParameter hParam: hParams.getParameters())
			{
				if (hParam.getValue() != null)
				{
					if (Collection.class.isAssignableFrom(hParam.getValue().getClass()))
					{
						ArrayNode paramValues = getObjectMapper().createArrayNode();
						Collection col = (Collection) hParam.getValue();
						for (Iterator it = col.iterator(); it.hasNext();)
						{
							Object next = it.next();
							paramValues.add(JRValueStringUtils.serialize(next.getClass().getName(), next));
						}
						params.set(hParam.getName(), paramValues);
					}
					else
					{
						params.put(hParam.getName(), JRValueStringUtils.serialize(hParam.getValueClass(), hParam.getValue()));
					}
				}
			}

			hyperlinkNode.set("params", params);
		}

		return hyperlinkNode;
	}


	/**
	 *
	 */
	public void addProperty(ObjectNode objectNode, String property, String value)
	{
		addProperty(objectNode, property, value, null);
	}


	/**
	 *
	 */
	public void addProperty(ObjectNode objectNode, String property, String value, String altValue)
	{
		if (value != null && !value.equals("null"))
		{
			objectNode.put(property, value);
		}
		else if (altValue != null && !altValue.equals("null"))
		{
			objectNode.put(property, altValue);
		}
	}
}
