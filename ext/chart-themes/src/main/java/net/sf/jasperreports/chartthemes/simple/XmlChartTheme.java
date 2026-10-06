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
package net.sf.jasperreports.chartthemes.simple;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.VersionComparator;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.jackson.util.JacksonUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XmlChartTheme extends SimpleChartTheme
{
	/**
	 *
	 */
	private String file;
	
	/**
	 *
	 */
	public XmlChartTheme()
	{
	}
	
	/**
	 *
	 */
	public XmlChartTheme(String file)
	{
		this.file = file;
	}
	

	/**
	 *
	 */
	public void setFile(String file)
	{
		this.file = file;
		this.chartThemeSettings = null;
	}
	
	
	@Override
	public ChartThemeSettings getChartThemeSettings()
	{
		if (chartThemeSettings == null)
		{
			chartThemeSettings = loadSettings(file);
		}
		return chartThemeSettings;
	}
	
	
	/**
	 *
	 */
	public static ChartThemeSettings loadSettings(String file)
	{
		InputStream is = null; 
		
		try
		{
			is = JRLoader.getLocationInputStream(file);
			return loadSettings(is);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}
	
	
	/**
	 *
	 */
	public static ChartThemeSettings loadSettings(InputStream is)
	{
		return JacksonUtil.getInstance(DefaultJasperReportsContext.getInstance()).loadXml(is, ChartThemeSettings.class);
	}
	
	
	/**
	 * @deprecated Replaced by {@link #saveSettings(JasperReportsContext, ChartThemeSettings, Writer)}.
	 */
	public static void saveSettings(ChartThemeSettings settings, Writer writer)
	{
		saveSettings(DefaultJasperReportsContext.getInstance(), settings, writer);
	}
	

	/**
	 *
	 */
	public static void saveSettings(JasperReportsContext jasperReportsContext, ChartThemeSettings settings, Writer writer)
	{
		String targetVersion = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(JRXmlWriter.PROPERTY_REPORT_VERSION);
		VersionComparator versionComparator = new VersionComparator();
		
		if (versionComparator.compare(targetVersion, JRConstants.VERSION_6_19_0) >= 0)
		{
			String xml = JacksonUtil.getInstance(DefaultJasperReportsContext.getInstance()).getXmlString(settings);
			try
			{
				writer.write(xml);
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		else
		{
			try
			{
				Class clazz = XmlChartTheme.class.getClassLoader().loadClass("net.sf.jasperreports.chartthemes.simple.XmlChartThemeCastorWriter");
				Method method = clazz.getMethod("saveSettings", ChartThemeSettings.class, Writer.class);
				method.invoke(null, settings, writer);
			}
			catch (ClassNotFoundException | NoSuchMethodException |  InvocationTargetException |  IllegalAccessException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}
	

	/**
	 * @deprecated Replaced by {@link #saveSettings(JasperReportsContext, ChartThemeSettings, File)}.
	 */
	public static void saveSettings(ChartThemeSettings settings, File file)
	{
		saveSettings(DefaultJasperReportsContext.getInstance(), settings, file);
	}
	

	/**
	 *
	 */
	public static void saveSettings(JasperReportsContext jasperReportsContext, ChartThemeSettings settings, File file)
	{
		Writer writer = null;
		
		try
		{
			writer = new FileWriter(file);
			saveSettings(jasperReportsContext, settings, writer);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}
	

	/**
	 * @deprecated Replaced by {@link #saveSettings(JasperReportsContext, ChartThemeSettings)}.
	 */
	public static String saveSettings(ChartThemeSettings settings)
	{
		return saveSettings(DefaultJasperReportsContext.getInstance(), settings);
	}
	

	/**
	 *
	 */
	public static String saveSettings(JasperReportsContext jasperReportsContext, ChartThemeSettings settings)
	{
		StringWriter writer = new StringWriter();
		
		try
		{
			saveSettings(jasperReportsContext, settings, writer);
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch(IOException e)
			{
			}
		}
		
		return writer.toString();
	}

}
