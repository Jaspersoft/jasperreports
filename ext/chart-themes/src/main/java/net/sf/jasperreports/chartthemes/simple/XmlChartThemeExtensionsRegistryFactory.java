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
package net.sf.jasperreports.chartthemes.simple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.jasperreports.charts.ChartTheme;
import net.sf.jasperreports.chartthemes.ChartThemeMapBundle;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.extensions.DefaultExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class XmlChartThemeExtensionsRegistryFactory implements
		ExtensionsRegistryFactory
{

	/**
	 * 
	 */
	public final static String XML_CHART_THEME_PROPERTY_PREFIX = 
		JRPropertiesUtil.PROPERTY_PREFIX + "xml.chart.theme.";
	public final static String PROPERTY_XML_CHART_THEME_REGISTRY_FACTORY =
		DefaultExtensionsRegistry.PROPERTY_REGISTRY_FACTORY_PREFIX + "xml.chart.themes";
	
	@Override
	public ExtensionsRegistry createRegistry(String registryId,
			JRPropertiesMap properties)
	{
		List<PropertySuffix> themeProperties = JRPropertiesUtil.getProperties(properties, 
				XML_CHART_THEME_PROPERTY_PREFIX);
		Map<String, ChartTheme> themes = new HashMap<>();
		for (PropertySuffix themeProp : themeProperties)
		{
			String themeName = themeProp.getSuffix();
			String themeLocation = themeProp.getValue();
			XmlChartTheme theme = new XmlChartTheme(themeLocation);
			themes.put(themeName, theme);
		}
		
		ChartThemeMapBundle bundle = new ChartThemeMapBundle();
		bundle.setThemes(themes);
		return new ChartThemeBundlesExtensionsRegistry(bundle);
	}

	/**
	 * @deprecated Replaced by {@link #saveToJar(JasperReportsContext, ChartThemeSettings, String, File)}.
	 */
	public static void saveToJar(ChartThemeSettings settings, String themeName, File file) throws IOException
	{
		saveToJar(DefaultJasperReportsContext.getInstance(), settings, themeName, file);
	}

	/**
	 * 
	 */
	public static void saveToJar(JasperReportsContext jasperReportsContext, ChartThemeSettings settings, String themeName, File file) throws IOException
	{
		FileOutputStream fos = null;

		try
		{
			fos = new FileOutputStream(file);
			ZipOutputStream zipos = new ZipOutputStream(fos);
			zipos.setMethod(ZipOutputStream.DEFLATED);
			
			ZipEntry propsEntry = new ZipEntry("jasperreports_extension.properties");
			zipos.putNextEntry(propsEntry);
			Properties props = new Properties();
			props.put(PROPERTY_XML_CHART_THEME_REGISTRY_FACTORY, XmlChartThemeExtensionsRegistryFactory.class.getName());
			props.put(XML_CHART_THEME_PROPERTY_PREFIX + themeName, themeName + ".jrctx");
			props.store(zipos, null);

			ZipEntry jrctxEntry = new ZipEntry(themeName + ".jrctx");
			zipos.putNextEntry(jrctxEntry);
			OutputStreamWriter jrctxWriter = new OutputStreamWriter(zipos);
			XmlChartTheme.saveSettings(jasperReportsContext, settings, jrctxWriter);
			jrctxWriter.flush();

			zipos.flush();
			zipos.finish();
		}
		finally
		{
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

}
