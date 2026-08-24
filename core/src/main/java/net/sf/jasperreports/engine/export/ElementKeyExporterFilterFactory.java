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
package net.sf.jasperreports.engine.export;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Factory of {@link ElementKeyExporterFilter} instances.
 * 
 * The factory uses report properties to decide which element keys are to
 * be filtered on export.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementKeyExporterFilterFactory implements ExporterFilterFactory
{

	/**
	 * The prefix of element exclusion properties.
	 * 
	 * This prefix is appended to the exporter properties prefix, resulting
	 * in element exclusion properties such as
	 * <code>net.sf.jasperreports.export.xls.exclude.key.*</code>. 
	 */
	@Property(
			name = "net.sf.jasperreports.export.{format}.exclude.key.{suffix}",
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_0_1
			)
	public static final String PROPERTY_EXCLUDED_KEY_PREFIX = "exclude.key.";
	
	/**
	 * The exported report is searched for element exclusion properties, and
	 * if any is found a {@link ElementKeyExporterFilter} instance is returned.
	 * 
	 * Each property results in a excluded element key in the following manner:
	 * <ul>
	 * 	<li>If the property value is not empty, it is used as excluded element key.</li>
	 * 	<li>Otherwise, the property suffix is used as element key.</li>
	 * </ul>
	 * 
	 * @see #PROPERTY_EXCLUDED_KEY_PREFIX
	 */
	@Override
	public ExporterFilter getFilter(JRExporterContext exporterContext)
			throws JRException
	{
		ExporterFilter filter = null;

		JRAbstractExporter<?, ?, ?, ?> exporter = 
			exporterContext.getExporterRef() instanceof JRAbstractExporter<?, ?, ?, ?> 
			? (JRAbstractExporter<?, ?, ?, ?>)exporterContext.getExporterRef() 
			: null;
			
		if (exporter != null)
		{
			String excludeKeyPrefix = 
				exporter.getExporterPropertiesPrefix() + PROPERTY_EXCLUDED_KEY_PREFIX;
			JRPropertiesUtil propsUtil = JRPropertiesUtil.getInstance(
					exporterContext.getJasperReportsContext());
			List<PropertySuffix> props = propsUtil.getAllProperties(
					exporterContext.getExportedReport(), excludeKeyPrefix);
			if (!props.isEmpty())
			{
				Set<String> excludedKeys = new HashSet<>();
				for (PropertySuffix prop : props)
				{
					String key = prop.getValue();
					if (key == null || key.length() == 0)
					{
						key = prop.getSuffix();
					}
					excludedKeys.add(key);
				}
				
				filter = new ElementKeyExporterFilter(excludedKeys);
			}
		}
		
		return filter;
	}

}
