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

/*
 * Contributors:
 * Greg Hilton 
 */

package net.sf.jasperreports.engine.export;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class MatcherExporterFilter implements ExporterFilter
{

	/**
	 *
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_5_5_0
			)
	public static final String PROPERTY_MATCHER_EXPORT_FILTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "export.matcher.filter.key";

	private Set<String> includes;
	private Set<String> excludes;

	@Override
	public boolean isToExport(JRPrintElement element)
	{
		if (element.hasProperties() && element.getPropertiesMap().containsProperty(PROPERTY_MATCHER_EXPORT_FILTER_KEY))
		{
			String matcherKey = element.getPropertiesMap().getProperty(PROPERTY_MATCHER_EXPORT_FILTER_KEY);
			if (matcherKey != null)
			{
				if (includes == null || includes.size() == 0)
				{
					if (excludes == null || excludes.size() == 0)
					{
						return true;
					}
					else
					{
						return !excludes.contains(matcherKey);
					}
				}
				else
				{
					if (excludes == null || excludes.size() == 0)
					{
						return includes.contains(matcherKey);
					}
					else
					{
						return includes.contains(matcherKey) && !excludes.contains(matcherKey);
					}
				}
			}
		}
		return true;
	}
	
	public MatcherExporterFilter(Set<String> includes, Set<String> excludes)
	{
		this.includes = includes;
		this.excludes = excludes;
	}
	
	public static MatcherExporterFilter getInstance(JRExporterContext exporterContext)
	{
		MatcherExporterFilter filter = null;
		
		Exporter exporter = exporterContext.getExporterRef();
		JRAbstractExporter typedExporter = exporter instanceof JRAbstractExporter ? (JRAbstractExporter)exporter : null;
		
		if (typedExporter != null)
		{
			String exporterKey = typedExporter.getExporterKey();
			if (exporterKey != null)
			{
				Set<String> includes = new HashSet<>();
				Set<String> excludes = new HashSet<>();
				List<MatcherExportFilterMapping> mappings = exporterContext.getJasperReportsContext().getExtensions(MatcherExportFilterMapping.class);
				for (MatcherExportFilterMapping mapping : mappings)
				{
					if (exporterKey.equals(mapping.getExporterKey()))
					{
						if (mapping.isIncludes())
						{
							includes.add(mapping.getValue());
						}
						else
						{
							excludes.add(mapping.getValue());
						}
					}
				}
				
				filter = new MatcherExporterFilter(includes, excludes);
			}
		}
		
		return filter;
	}
}
