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
package net.sf.jasperreports.export;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.NamedEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.export.annotations.ExporterProperty;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PropertiesExporterConfigurationFactory<C extends CommonExportConfiguration>
{
	public static final String EXCEPTION_MESSAGE_KEY_EXPORT_PROPERTIES_EMPTY_STRING_DEFAULT_NOT_SUPPORTED = "export.common.properties.empty.string.default.not.supported";
	public static final String EXCEPTION_MESSAGE_KEY_EXPORT_PROPERTIES_TYPE_NOT_SUPPORTED = "export.common.properties.type.not.supported";

	/**
	 * FIXMEEXPORT this static method can be moved
	 */
	public static Object getPropertyValue(
		JasperReportsContext jasperReportsContext,
		JRPropertiesHolder propertiesHolder,
		ExporterProperty exporterProperty, 
		Class<?> type 
		)
	{
		Object value = null;
		
		String propertyName = exporterProperty.value();
		
		if (String[].class.equals(type))
		{
			List<PropertySuffix> properties = JRPropertiesUtil.getProperties(propertiesHolder, propertyName);
			if (properties != null && !properties.isEmpty())
			{
				String[] values = new String[properties.size()];
				for (int i = 0; i < values.length; i++)
				{
					values[i] = properties.get(i).getValue();
				}
				
				value = values;
			}
		}
		else if (PropertySuffix[].class.equals(type))
		{
			List<PropertySuffix> properties = JRPropertiesUtil.getProperties(propertiesHolder, propertyName);
			if (properties != null && !properties.isEmpty())
			{
				value = properties.toArray(new PropertySuffix[properties.size()]);
			}
		}
		else if (Map.class.equals(type))
		{
			List<PropertySuffix> properties = JRPropertiesUtil.getProperties(propertiesHolder, propertyName);
			if (properties != null && !properties.isEmpty())
			{
				Map<String,String> values = new HashMap<>();
				for (PropertySuffix propertySuffix : properties)
				{
					values.put(propertySuffix.getSuffix(), propertySuffix.getValue());
				}
				value = values;
			}
		}
		else
		{
			JRPropertiesUtil propertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
			if (String.class.equals(type))
			{
				value = propertiesUtil.getProperty(propertiesHolder, propertyName);
			}
			else if (Character.class.equals(type))
			{
				value = propertiesUtil.getCharacterProperty(propertiesHolder, propertyName);
			}
			else if (Integer.class.equals(type))
			{
				if (exporterProperty.nullDefault())
				{
					value = propertiesUtil.getIntegerProperty(propertiesHolder, propertyName);
				}
				else
				{
					value = propertiesUtil.getIntegerProperty(propertiesHolder, propertyName, exporterProperty.intDefault());
				}
			}
			else if (Long.class.equals(type))
			{
				value = propertiesUtil.getLongProperty(propertiesHolder, propertyName, exporterProperty.longDefault());
			}
			else if (Float.class.equals(type))
			{
				if (exporterProperty.nullDefault())
				{
					value = propertiesUtil.getFloatProperty(propertiesHolder, propertyName);
				}
				else
				{
					value = propertiesUtil.getFloatProperty(propertiesHolder, propertyName, exporterProperty.floatDefault());
				}
			}
			else if (Boolean.class.equals(type))
			{
				value = propertiesUtil.getBooleanProperty(propertiesHolder, propertyName, exporterProperty.booleanDefault());
			}
			else if (Color.class.equals(type))
			{
				String strValue = propertiesUtil.getProperty(propertiesHolder, propertyName);

				if (strValue == null)
				{
					if (!exporterProperty.nullDefault())
					{
						strValue = exporterProperty.stringDefault();
						if (strValue.trim().length() == 0)
						{
							throw new JRRuntimeException(
								EXCEPTION_MESSAGE_KEY_EXPORT_PROPERTIES_EMPTY_STRING_DEFAULT_NOT_SUPPORTED,
								new Object[]{propertyName}
								);
						}
					}
				}

				if (strValue != null)
				{
					value = JRColorUtil.getColor(strValue, null);
				}
			}
			else if (NamedEnum.class.isAssignableFrom(type))
			{
				value = propertiesUtil.getProperty(propertiesHolder, propertyName);
				try
				{
					Method byNameMethod = type.getMethod("getByName", new Class<?>[]{String.class});
					value = byNameMethod.invoke(null, value);
				}
				catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
				{
					throw new JRRuntimeException(e);
				}
			}
			else
			{
				throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_EXPORT_PROPERTIES_TYPE_NOT_SUPPORTED, 
					new Object[]{type});
			}
		}
		
		return value;
	}
}
