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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.NamedEnum;
import net.sf.jasperreports.engine.util.ClassUtils;
import net.sf.jasperreports.engine.util.JRColorUtil;
import net.sf.jasperreports.export.annotations.ExporterProperty;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PropertiesDefaultsConfigurationFactory<C extends CommonExportConfiguration>
{
	/**
	 * 
	 */
	private final JasperReportsContext jasperReportsContext;
	
	/**
	 * 
	 */
	public PropertiesDefaultsConfigurationFactory(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}

	
	/**
	 * 
	 */
	public C getConfiguration(final Class<C> configurationInterface)
	{
		return getProxy(configurationInterface, new PropertiesDefaultsInvocationHandler());
	}


	/**
	 * 
	 */
	private final C getProxy(Class<?> clazz, InvocationHandler handler)
	{
		List<Class<?>> allInterfaces = new ArrayList<>();

		if (clazz.isInterface())
		{
			allInterfaces.add(clazz);
		}
		else
		{
			List<Class<?>> lcInterfaces = ClassUtils.getInterfaces(clazz);
			allInterfaces.addAll(lcInterfaces);
		}

		@SuppressWarnings("unchecked")
		C proxy =
			(C)Proxy.newProxyInstance(
				ExporterConfiguration.class.getClassLoader(),
				allInterfaces.toArray(new Class<?>[allInterfaces.size()]),
				handler
				);
		
		return proxy;
	}


	/**
	 * 
	 */
	class PropertiesDefaultsInvocationHandler implements InvocationHandler
	{
		/**
		 * 
		 */
		public PropertiesDefaultsInvocationHandler()
		{
		}
		
		@Override
		public Object invoke(
			Object proxy, 
			Method method, 
			Object[] args
			) throws Throwable 
		{
			return getPropertyValue(method);
		}
	}
	
	
	/**
	 * 
	 */
	protected Object getPropertyValue(Method method)
	{
		Object value = null;
		ExporterProperty exporterProperty = method.getAnnotation(ExporterProperty.class);
		if (exporterProperty != null)
		{
			value = getPropertyValue(jasperReportsContext, exporterProperty, method.getReturnType());
		}
		return value;
	}
	
	
	/**
	 * 
	 */
	public static Object getPropertyValue(
		JasperReportsContext jasperReportsContext,
		ExporterProperty exporterProperty, 
		Class<?> type 
		)
	{
		Object value = null;
		
		String propertyName = exporterProperty.value();
		
		JRPropertiesUtil propertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);

		if (String[].class.equals(type))
		{
			List<PropertySuffix> properties = propertiesUtil.getProperties(propertyName);
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
			List<PropertySuffix> properties = propertiesUtil.getProperties(propertyName);
			if (properties != null && !properties.isEmpty())
			{
				value = properties.toArray(new PropertySuffix[properties.size()]);
			}
		}
		else if (Map.class.equals(type))
		{
			List<PropertySuffix> properties = propertiesUtil.getProperties(propertyName);
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
			String strValue = propertiesUtil.getProperty(propertyName);
			
			if (String.class.equals(type))
			{
				value = strValue;
			}
			else if (Character.class.equals(type))
			{
				value = JRPropertiesUtil.asCharacter(strValue);
			}
			else if (Integer.class.equals(type))
			{
				if (strValue == null)
				{
					if (!exporterProperty.nullDefault())
					{
						value = exporterProperty.intDefault();
					}
				}
				else
				{
					value = JRPropertiesUtil.asInteger(strValue);
				}
			}
			else if (Long.class.equals(type))
			{
				if (strValue == null)
				{
					if (!exporterProperty.nullDefault())
					{
						value = exporterProperty.longDefault();
					}
				}
				else
				{
					value = JRPropertiesUtil.asLong(strValue);
				}
			}
			else if (Float.class.equals(type))
			{
				if (strValue == null)
				{
					if (!exporterProperty.nullDefault())
					{
						value = exporterProperty.floatDefault();
					}
				}
				else
				{
					value = JRPropertiesUtil.asFloat(strValue);
				}
			}
			else if (Boolean.class.equals(type))
			{
				if (strValue == null)
				{
					if (!exporterProperty.nullDefault())
					{
						value = exporterProperty.booleanDefault();
					}
				}
				else
				{
					value = JRPropertiesUtil.asBoolean(strValue);
				}
			}
			else if (Color.class.equals(type))
			{
				if (strValue == null)
				{
					if (!exporterProperty.nullDefault())
					{
						strValue = exporterProperty.stringDefault();
						if (strValue.trim().length() == 0)
						{
							throw new JRRuntimeException(
								PropertiesExporterConfigurationFactory.EXCEPTION_MESSAGE_KEY_EXPORT_PROPERTIES_EMPTY_STRING_DEFAULT_NOT_SUPPORTED,
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
				if (strValue == null)
				{
					if (!exporterProperty.nullDefault())
					{
						strValue = exporterProperty.stringDefault();
					}
				}

				if (strValue != null)
				{
					try
					{
						Method byNameMethod = type.getMethod("getByName", new Class<?>[]{String.class});
						value = byNameMethod.invoke(null, strValue);
					}
					catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
					{
						throw new JRRuntimeException(e);
					}
				}
			}
			else
			{
				throw 
					new JRRuntimeException(
						PropertiesExporterConfigurationFactory.EXCEPTION_MESSAGE_KEY_EXPORT_PROPERTIES_TYPE_NOT_SUPPORTED, 
						new Object[]{type});
			}
		}
		
		return value;
	}
	
}
