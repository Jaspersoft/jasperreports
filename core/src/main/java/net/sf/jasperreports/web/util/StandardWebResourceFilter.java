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
package net.sf.jasperreports.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontFace;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StandardWebResourceFilter implements WebResourceFilter
{
	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			defaultValue = "true",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_6,
			valueType = Boolean.class
			)
	public static final String PROPERTY_RESOURCE_FILTER_ENABLED = 
			JRPropertiesUtil.PROPERTY_PREFIX + "web.resource.filter.enabled";
	
	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_6,
			name = "net.sf.jasperreports.web.resource.paths.{arbitrary_name}"
			)
	public static final String PROPERTY_PREFIX_RESOURCE_PATHS = 
			JRPropertiesUtil.PROPERTY_PREFIX + "web.resource.paths.";
	
	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_6,
			name = "net.sf.jasperreports.web.resource.pattern.{arbitrary_name}"
			)
	public static final String PROPERTY_PREFIX_RESOURCE_PATTERN = 
			JRPropertiesUtil.PROPERTY_PREFIX + "web.resource.pattern.";
	
	private boolean filterEnabled;
	private List<WebResourceWhitelist> whitelists;
	
	private Map<String, Boolean> visibilityCache = new ConcurrentHashMap<>();

	public StandardWebResourceFilter(JasperReportsContext jasperReportsContext)
	{
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(jasperReportsContext);
		filterEnabled = properties.getBooleanProperty(PROPERTY_RESOURCE_FILTER_ENABLED);
		if (filterEnabled)
		{
			whitelists = new ArrayList<>();
			
			StandardWebResourceWhitelist whitelist = new StandardWebResourceWhitelist();
			loadPropertiesWhitelistResources(properties, whitelist);
			loadPropertiesWhitelistPatterns(properties, whitelist);
			loadFontsWhitelist(jasperReportsContext, whitelist);
			whitelists.add(whitelist);
			
			List<WebResourceWhitelist> extensionWhitelists = jasperReportsContext.getExtensions(
					WebResourceWhitelist.class);
			whitelists.addAll(extensionWhitelists);			
		}		
	}

	private static void loadPropertiesWhitelistResources(JRPropertiesUtil propertiesUtil, StandardWebResourceWhitelist whitelist)
	{
		List<PropertySuffix> properties = propertiesUtil.getProperties(PROPERTY_PREFIX_RESOURCE_PATHS);
		for (PropertySuffix propertySuffix : properties)
		{
			String whitelistString = propertySuffix.getValue();
			whitelist.addResources(whitelistString);
		}
	}

	private static void loadPropertiesWhitelistPatterns(JRPropertiesUtil propertiesUtil, StandardWebResourceWhitelist whitelist)
	{
		List<PropertySuffix> properties = propertiesUtil.getProperties(PROPERTY_PREFIX_RESOURCE_PATTERN);
		for (PropertySuffix propertySuffix : properties)
		{
			String whitelistString = propertySuffix.getValue();
			whitelist. addPattern(whitelistString);
		}
	}

	private static void loadFontsWhitelist(JasperReportsContext jasperReportsContext, StandardWebResourceWhitelist whitelist)
	{
		List<FontFamily> families = jasperReportsContext.getExtensions(FontFamily.class);
		for (Iterator<FontFamily> itf = families.iterator(); itf.hasNext();)
		{
			loadFontFamily(itf.next(), whitelist);
		}
	}

	private static void loadFontFamily(FontFamily fontFamily, StandardWebResourceWhitelist whitelist)
	{
		loadFontFace(fontFamily.getNormalFace(), whitelist);
		loadFontFace(fontFamily.getBoldFace(), whitelist);
		loadFontFace(fontFamily.getItalicFace(), whitelist);
		loadFontFace(fontFamily.getBoldItalicFace(), whitelist);
	}

	private static void loadFontFace(FontFace fontFace, StandardWebResourceWhitelist whitelist)
	{
		if (fontFace != null)
		{
			String resource = fontFace.getEot();
			if (resource != null && resource.trim().length() > 0)
			{
				whitelist.addResources(resource);
			}
			resource = fontFace.getSvg();
			if (resource != null && resource.trim().length() > 0)
			{
				whitelist.addResources(resource);
			}
			resource = fontFace.getTtf();
			if (resource != null && resource.trim().length() > 0)
			{
				whitelist.addResources(resource);
			}
			resource = fontFace.getWoff();
			if (resource != null && resource.trim().length() > 0)
			{
				whitelist.addResources(resource);
			}
		}
	}

	public boolean isFilteringEnabled()
	{
		return filterEnabled;
	}
	
	@Override
	public boolean isResourceVisible(String resource)
	{
		return visibilityCache.computeIfAbsent(resource, key -> visible(key));
	}

	protected boolean visible(String resource)
	{
		boolean visible;
		if (filterEnabled)
		{
			visible = false;
			for (WebResourceWhitelist whitelist : whitelists)
			{
				if (whitelist.includesResource(resource))
				{
					visible = true;
					break;
				}
			}
		}
		else
		{
			visible = true;
		}
		return visible;
	}
}
