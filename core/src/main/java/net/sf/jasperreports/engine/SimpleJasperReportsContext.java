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
package net.sf.jasperreports.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleJasperReportsContext implements JasperReportsContext
{
	/**
	 *
	 */
	private JasperReportsContext parent;
	
	private Map<String, Object> values = new ConcurrentHashMap<>(16, .75f, 1);// assume low update concurrency
	private Map<String, String> properties;
	private Map<Class<?>, List<?>> extensionsMap;

	/**
	 * Constructs a SimpleJasperReportsContext instance that has the DefaultJasperReportsContext.getInstance() as parent.
	 */
	public SimpleJasperReportsContext()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	/**
	 *
	 */
	public SimpleJasperReportsContext(JasperReportsContext parent)
	{
		this.parent = parent;
	}

	/**
	 *
	 */
	public void setParent(JasperReportsContext parent)
	{
		this.parent = parent;
	}
	
	public JasperReportsContext getParent()
	{
		return parent;
	}

	@Override
	public Object getValue(String key)
	{
		if (values.containsKey(key))
		{
			return values.get(key);
		}
		if (parent != null)
		{
			return parent.getValue(key);
		}
		return null;
	}

	@Override
	public Object getOwnValue(String key)
	{
		return values.get(key);
	}

	@Override
	public void setValue(String key, Object value)
	{
		values.put(key, value);
	}

	/**
	 *
	 */
	public void removeValue(String key)
	{
		values.remove(key);
	}
	
	/**
	 * Returns a list of extension objects for a specific extension type.
	 * 
	 * @param extensionType the extension type
	 * @param <T> generic extension type
	 * @return a list of extension objects
	 */
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		if (extensionsMap == null || !extensionsMap.containsKey(extensionType))
		{
			if (parent == null)
			{
				return null;
			}
			else
			{
				return parent.getExtensions(extensionType);
			}
		}
		else
		{
			@SuppressWarnings("unchecked")
			List<T> extensionsList = (List<T>)extensionsMap.get(extensionType);
			if (parent == null)
			{
				return extensionsList;
			}
			else
			{
				List<T> parentExtensions = parent.getExtensions(extensionType);
				if (extensionsList == null || extensionsList.isEmpty())
				{
					if (parentExtensions == null || parentExtensions.isEmpty())
					{
						return null;
					}
					else
					{
						return parentExtensions;
					}
				}
				else
				{
					if (parentExtensions == null || parentExtensions.isEmpty())
					{
						return extensionsList;
					}
					else
					{
						List<T> returnedList = new ArrayList<>();
						returnedList.addAll(extensionsList);
						returnedList.addAll(parentExtensions);
						return returnedList;
					}
				}
			}
		}
	}
	
	/**
	 *
	 */
	public <T> void setExtensions(Class<T> extensionType, List<? extends T> extensions)
	{
		if (extensionsMap == null)
		{
			extensionsMap = new HashMap<>();
		}
		extensionsMap.put(extensionType, extensions);
	}
	
	/**
	 *
	 */
	public void setExtensions(Map<Class<?>, List<?>> extensions)
	{
		extensionsMap = extensions;
	}
	
	/**
	 * Returns the value of the property.
	 * 
	 * @param key the key
	 * @return the property value
	 */
	@Override
	public String getProperty(String key)
	{
		if (properties != null && properties.containsKey(key))
		{
			return properties.get(key);
		}
		else
		{
			if (parent == null)
			{
				return null;
			}
			else
			{
				return parent.getProperty(key);
			}
		}
	}
	
	@Override
	public String getOwnProperty(String key)
	{
		return properties == null ? null : properties.get(key);
	}
	
	@Override
	public void setProperty(String key, String value)
	{
		if (properties == null)
		{
			properties = new HashMap<>();
		}
		
		properties.put(key, value);
	}
	
	@Override
	public void removeProperty(String key)
	{
		if (properties != null)
		{
			properties.remove(key);
		}
	}
	
	@Override
	public Map<String, String> getProperties()
	{
		if (properties == null)
		{
			if (parent == null)
			{
				return null;
			}
			else
			{
				return parent.getProperties();
			}
		}
		else
		{
			if (parent == null)
			{
				return properties;
			}
			else
			{
				Map<String, String> parentProperties = parent.getProperties();
				if (properties == null || properties.isEmpty())
				{
					if (parentProperties == null || parentProperties.isEmpty())
					{
						return null;
					}
					else
					{
						return parentProperties;
					}
				}
				else
				{
					if (parentProperties == null || parentProperties.isEmpty())
					{
						return properties;
					}
					else
					{
						Map<String, String> returnedMap = new HashMap<>();
						returnedMap.putAll(parentProperties);
						returnedMap.putAll(properties);
						return returnedMap;
					}
				}
			}
		}
	}

	public Map<String, String> getPropertiesMap()
	{
		return properties;
	}

	public void setPropertiesMap(Map<String, String> propertiesMap)
	{
		this.properties = propertiesMap;
	}
}
