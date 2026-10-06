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
public class TimeoutGovernor extends JRDefaultScriptlet
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
	public static final String PROPERTY_TIMEOUT_ENABLED = JRPropertiesUtil.PROPERTY_PREFIX + "governor.timeout.enabled";
	
	@Property(
			category = PropertyConstants.CATEGORY_GOVERNOR,
			valueType = Long.class,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_1_4
			)
	public static final String PROPERTY_TIMEOUT = JRPropertiesUtil.PROPERTY_PREFIX + "governor.timeout";

	/**
	 *
	 */
	private long startTime;
	private long timeout;

	
	/**
	 *
	 */
	public TimeoutGovernor(long timeout)
	{
		this.timeout = timeout;
	}


	@Override
	public void beforeReportInit() throws JRScriptletException
	{
		startTime = System.currentTimeMillis();
	}


	@Override
	public void beforeDetailEval() throws JRScriptletException
	{
		long ellapsedTime = System.currentTimeMillis() - startTime;
		if (timeout < ellapsedTime)
		{
			throw 
				new TimeoutGovernorException(
					((JasperReport)getParameterValue(JRParameter.JASPER_REPORT, false)).getName(),
					timeout
					);
		}
	}


}
