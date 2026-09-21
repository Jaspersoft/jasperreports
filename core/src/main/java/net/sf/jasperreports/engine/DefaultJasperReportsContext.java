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
package net.sf.jasperreports.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.design.JRCompiler;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DefaultJasperReportsContext implements JasperReportsContext
{
	
	/**
	 * The default properties file.
	 */
	public static final String DEFAULT_PROPERTIES_FILE = "jasperreports.properties";
	
	/**
	 * The name of the system property that specifies the properties file name.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			defaultValue = "jasperreports.properties",
			scopes = {PropertyScope.SYSTEM},
			sinceVersion = PropertyConstants.VERSION_1_0_0
			)
	public static final String PROPERTIES_FILE = JRPropertiesUtil.PROPERTY_PREFIX + "properties";

	/**
	 *
	 */
	private static final DefaultJasperReportsContext INSTANCE = new DefaultJasperReportsContext();
	
	private static final String EXCEPTION_MESSAGE_KEY_LOAD_DEFAULT_PROPERTIES_FAILURE = "engine.context.load.default.properties.failure";
	private static final String EXCEPTION_MESSAGE_KEY_LOAD_PROPERTIES_FAILURE = "engine.context.load.properties.failure";
	private static final String EXCEPTION_MESSAGE_KEY_LOAD_PROPERTIES_FILE_FAILURE = "engine.context.load.properties.file.failure";
	private static final String EXCEPTION_MESSAGE_KEY_DEFAULT_PROPERTIES_FILE_NOT_FOUND = "engine.context.default.properties.file.not.found";
	
	private Map<String, Object> values = new ConcurrentHashMap<>(16, .75f, 1);// assume low update concurrency

	// FIXME remove volatile after we get rid of restoreProperties()
	protected volatile ConcurrentHashMap<String, String> properties;
	
	/**
	 *
	 */
	private DefaultJasperReportsContext()
	{
		initProperties();
	}

	/**
	 *
	 */
	public static DefaultJasperReportsContext getInstance()//FIXMECONTEXT check this use of this
	{
		return INSTANCE;
	}

	/**
	 * Loads the properties. 
	 */
	protected void initProperties()
	{
		try
		{
			Properties defaults = getDefaultProperties();
			String propFile = getSystemProperty(PROPERTIES_FILE);
			Properties loadedProps;
			if (propFile == null)
			{
				loadedProps = JRPropertiesUtil.loadProperties(DEFAULT_PROPERTIES_FILE, defaults);
				if (loadedProps == null)
				{
					loadedProps = new Properties(defaults);
				}
			}
			else
			{
				loadedProps = JRPropertiesUtil.loadProperties(propFile, defaults);
				if (loadedProps == null)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_LOAD_PROPERTIES_FILE_FAILURE,
							new Object[]{propFile});
				}
			}

			//FIXME configurable concurrency level?
			properties = new ConcurrentHashMap<>();
			for (Enumeration<?> names = loadedProps.propertyNames(); names.hasMoreElements();)
			{
				String name = (String) names.nextElement();
				String value = loadedProps.getProperty(name);
				properties.put(name, value);
			}
			
			loadSystemProperties();
		}
		catch (JRException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_LOAD_PROPERTIES_FAILURE,
					(Object[])null,
					e);
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	protected void loadSystemProperties()
	{
		loadSystemProperty("jasper.reports.compiler.class", JRCompiler.COMPILER_CLASS);
		loadSystemProperty("jasper.reports.compile.keep.java.file", JRCompiler.COMPILER_KEEP_JAVA_FILE);
		loadSystemProperty("jasper.reports.compile.temp", JRCompiler.COMPILER_TEMP_DIR);
		loadSystemProperty("jasper.reports.compile.class.path", JRCompiler.COMPILER_CLASSPATH);	
	}

	/**
	 * Sets the default properties.
	 * 
	 * @return the default properties
	 */
	protected static Properties getDefaultProperties() throws JRException
	{
		Properties defaults = new Properties();
		
		InputStream is = JRPropertiesUtil.class.getResourceAsStream("/default.jasperreports.properties");
		
		if (is == null)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DEFAULT_PROPERTIES_FILE_NOT_FOUND,
					(Object[])null);
		}

		try
		{
			defaults.load(is);
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_LOAD_DEFAULT_PROPERTIES_FAILURE, 
					null, 
					e);
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
			{
			}
		}
		
		String tempDir = getSystemProperty("java.io.tmpdir");
		if (tempDir != null)
		{
			defaults.setProperty(JRCompiler.COMPILER_TEMP_DIR, tempDir);
		}
		
		String classPath = getSystemProperty("java.class.path");
		if (classPath != null)
		{
			defaults.setProperty(JRCompiler.COMPILER_CLASSPATH, classPath);
		}

		return defaults;
	}

	/**
	 * 
	 */
	protected static String getSystemProperty(String propertyName)
	{
		try
		{
			return System.getProperty(propertyName);
		}
		catch (SecurityException e)
		{
			// This could fail if we are in the applet viewer or some other 
			// restrictive environment, but it should be safe to simply return null.
			// We cannot log this properly using a logging API, 
			// as we want to keep applet JAR dependencies to a minimum.
			return null;
		}
	}

	/**
	 * 
	 */
	protected void loadSystemProperty(String sysKey, String propKey)
	{
		String val = getSystemProperty(sysKey);
		if (val != null)
		{
			properties.put(propKey, val);
		}
	}

	@Override
	public Object getValue(String key)
	{
		return values.get(key);
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
	 * Returns a list of extension objects for a specific extension type.
	 * 
	 * @param extensionType the extension type
	 * @param <T> generic extension type
	 * @return a list of extension objects
	 */
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		return ExtensionsEnvironment.getExtensionsRegistry().getExtensions(extensionType);
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
		return properties.get(key);
	}
	
	@Override
	public String getOwnProperty(String key)
	{
		return properties.get(key);
	}
	
	@Override
	public void setProperty(String key, String value)
	{
		properties.put(key, value);
	}
	
	@Override
	public void removeProperty(String key)
	{
		properties.remove(key);
	}
	
	@Override
	public Map<String, String> getProperties()
	{
		return properties;
	}
}
