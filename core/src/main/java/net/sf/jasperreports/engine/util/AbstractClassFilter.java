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
package net.sf.jasperreports.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class AbstractClassFilter implements ClassFilter
{
	protected abstract String getClassFilterEnabledPropertyName();

	protected abstract String getClassWhitelistPropertyPrefix();
	
	protected abstract String getClassNotVisibleExceptionMessageKey();
	
	private boolean filterEnabled;
	private List<ClassWhitelist> whitelists;
	
	private Map<String, Boolean> visibilityCache = new ConcurrentHashMap<>();

	public AbstractClassFilter(JasperReportsContext jasperReportsContext)
	{
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(jasperReportsContext);
		filterEnabled = properties.getBooleanProperty(getClassFilterEnabledPropertyName());
		if (filterEnabled)
		{
			whitelists = new ArrayList<>();
			
			StandardClassWhitelist whitelist = new StandardClassWhitelist();
			loadPropertiesWhitelist(properties, whitelist);
			whitelists.add(whitelist);

			addExtraWhitelists(jasperReportsContext, whitelists);
		}
	}

	private void loadPropertiesWhitelist(JRPropertiesUtil propertiesUtil, 
			StandardClassWhitelist whitelist)
	{
		List<PropertySuffix> properties = propertiesUtil.getProperties(getClassWhitelistPropertyPrefix());
		for (PropertySuffix propertySuffix : properties)
		{
			String whitelistString = propertySuffix.getValue();
			whitelist.addWhitelist(whitelistString);
		}
	}

	protected abstract void addExtraWhitelists(JasperReportsContext jasperReportsContext,
			List<ClassWhitelist> whitelists);

	public boolean isFilteringEnabled()
	{
		return filterEnabled;
	}
	
	@Override
	public void checkClassVisibility(String className) throws JRRuntimeException
	{
		boolean visible = isClassVisible(className);
		if (!visible)
		{
			throw new JRRuntimeException(getClassNotVisibleExceptionMessageKey(), new Object[] {className});
		}
	}
	
	public boolean isClassVisible(String className)
	{
		Boolean visible = visibilityCache.get(className);
		if (visible == null)
		{
			visible = visible(className);
			visibilityCache.put(className, visible);
		}
		return visible;
	}

	protected boolean visible(String className)
	{
		boolean visible;
		if (filterEnabled)
		{
			visible = false;
			for (ClassWhitelist whitelist : whitelists)
			{
				if (whitelist.includesClass(className))
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
