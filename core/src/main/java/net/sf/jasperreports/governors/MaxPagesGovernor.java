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
package net.sf.jasperreports.governors;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class MaxPagesGovernor extends JRDefaultScriptlet
{

	/**
	 *
	 */
	@Property(
			category = PropertyConstants.CATEGORY_GOVERNOR,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_1_4
			)
	public static final String PROPERTY_MAX_PAGES_ENABLED = JRPropertiesUtil.PROPERTY_PREFIX + "governor.max.pages.enabled";
	
	@Property(
			category = PropertyConstants.CATEGORY_GOVERNOR,
			valueType = Integer.class,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_1_4
			)
	public static final String PROPERTY_MAX_PAGES = JRPropertiesUtil.PROPERTY_PREFIX + "governor.max.pages";

	/**
	 *
	 */
	private int pages;
	private int maxPages;

	
	/**
	 *
	 */
	public MaxPagesGovernor(int maxPages)
	{
		this.maxPages = maxPages;
	}


	@Override
	public void afterPageInit() throws JRScriptletException
	{
		// cannot use PAGE_NUMBER variable because of timing issues and because of isResetPageNumber flag
		pages++;
		if (maxPages < pages)
		{
			throw 
				new MaxPagesGovernorException(
					((JasperReport)getParameterValue(JRParameter.JASPER_REPORT, false)).getName(),
					maxPages
					);
		}
	}


}
