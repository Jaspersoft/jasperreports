/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.DefaultedMessageProvider;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalizedMessageProvider;
import net.sf.jasperreports.engine.util.MessageProvider;
import net.sf.jasperreports.engine.util.MessageUtil;
import net.sf.jasperreports.metadata.properties.CompiledPropertiesMetadata;
import net.sf.jasperreports.metadata.properties.CompiledPropertyMetadata;
import net.sf.jasperreports.metadata.properties.PropertyMetadataConstants;
import net.sf.jasperreports.metadata.properties.StandardPropertiesMetadataSerialization;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ResourcePropertiesMetadataReader
{

	private static final Log log = LogFactory.getLog(ResourcePropertiesMetadataReader.class);
	
	private static final ResourcePropertiesMetadataReader INSTANCE = new ResourcePropertiesMetadataReader();
	
	public static ResourcePropertiesMetadataReader instance()
	{
		return INSTANCE;
	}
	
	protected ResourcePropertiesMetadataReader()
	{
	}

	public Map<String, PropertyMetadata> readProperties(JasperReportsContext context, Locale locale)
	{
		MessageUtil messageUtil = MessageUtil.getInstance(context);
		
		Map<String, PropertyMetadata> properties = new LinkedHashMap<>();
		List<URL> resources = JRLoader.getResources(StandardPropertiesMetadataSerialization.EXTENSION_RESOURCE_NAME);
		StandardPropertiesMetadataSerialization metadataSerialization = StandardPropertiesMetadataSerialization.instance();
		for (URL resource : resources)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Loading properties metadata from " + resource);
			}
			
			InputStream in = null;
			try
			{
				in = resource.openStream();
				CompiledPropertiesMetadata resourceProperties = metadataSerialization.readProperties(in);
				if (log.isDebugEnabled())
				{
					log.debug("Loaded " + resourceProperties.getProperties().size() + " properties from " + resource);
				}
				
				MessageProvider messageProvider = messageUtil.getMessageProvider(
						resourceProperties.getMessagesName());
				messageProvider = DefaultedMessageProvider.wrap(messageProvider, 
						resourceProperties.getMessagesName() + PropertyMetadataConstants.MESSAGES_DEFAULTS_SUFFIX);
				LocalizedMessageProvider localizedMessageProvider = new LocalizedMessageProvider(messageProvider, locale);
				
				for (CompiledPropertyMetadata compiledProperty : resourceProperties.getProperties())
				{
					if (!properties.containsKey(compiledProperty.getName()))
					{
						PropertyMetadata property = toProperty(compiledProperty, localizedMessageProvider);
						properties.put(compiledProperty.getName(), property);
					}
					else if (log.isDebugEnabled())
					{
						log.debug("Found duplicate property " + compiledProperty.getName());
					}
				}
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
			finally
			{
				if (in != null)
				{
					try
					{
						in.close();
					}
					catch (IOException e)
					{
						log.warn("Failed to close input stream for " + resource, e);
					}
				}
			}
		}
		
		return properties;
	}
	
	protected PropertyMetadata toProperty(CompiledPropertyMetadata compiledProperty, 
			LocalizedMessageProvider messageProvider)
	{
		StandardPropertyMetadata property = new StandardPropertyMetadata();
		String name = compiledProperty.getName();
		property.setName(name);
		property.setCategory(compiledProperty.getCategory());
		property.setConstantDeclarationClass(compiledProperty.getConstantDeclarationClass());
		property.setConstantFieldName(compiledProperty.getConstantFieldName());
		property.setLabel(messageProvider.getMessage(PropertyMetadataConstants.PROPERTY_LABEL_PREFIX + name));
		property.setDescription(messageProvider.getMessage(PropertyMetadataConstants.PROPERTY_DESCRIPTION_PREFIX + name));
		property.setDefaultValue(compiledProperty.getDefaultValue());
		property.setScopes(compiledProperty.getScopes());//TODO lucianc copy?
		property.setScopeQualifications(compiledProperty.getScopeQualifications());//TODO lucianc copy?
		property.setSinceVersion(compiledProperty.getSinceVersion());
		property.setValueType(compiledProperty.getValueType());
		property.setDeprecated(compiledProperty.isDeprecated());
		return property;
	}

}
