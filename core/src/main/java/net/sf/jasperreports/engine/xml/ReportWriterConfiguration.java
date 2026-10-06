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
package net.sf.jasperreports.engine.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ReportWriterConfiguration
{
	
	/**
	 *
	 */
	private JasperReportsContext jasperReportsContext;
	
	private List<Pattern> excludePropertiesPattern;
	private boolean excludeUuids;

	/**
	 *
	 */
	public ReportWriterConfiguration(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
		
		initExcludeProperties();
	}


	private void initExcludeProperties()
	{
		JasperReportsContext context = jasperReportsContext == null 
				? DefaultJasperReportsContext.getInstance() : jasperReportsContext;
		List<PropertySuffix> excludeProperties = JRPropertiesUtil.getInstance(context).getProperties(
				JRXmlWriter.PREFIX_EXCLUDE_PROPERTIES);

		excludePropertiesPattern = new ArrayList<>(excludeProperties.size());
		for (PropertySuffix propertySuffix : excludeProperties)
		{
			String regex = propertySuffix.getValue();
			Pattern pattern = Pattern.compile(regex);
			excludePropertiesPattern.add(pattern);
		}

		excludeUuids = JRPropertiesUtil.getInstance(context).getBooleanProperty(JRXmlWriter.PROPERTY_EXCLUDE_UUIDS);
	}


	/**
	 *
	 */
	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}


	/**
	 *
	 */
	public boolean isExcludeUuids()
	{
		return excludeUuids;
	}


	/**
	 *
	 */
	public boolean isPropertyToWrite(String propertyName)
	{
		boolean toWrite = true;
		for (Pattern pattern : excludePropertiesPattern)
		{
			if (pattern.matcher(propertyName).matches())
			{
				// excluding
				toWrite = false;
				break;
			}
		}
		return toWrite;
	}
	
}
