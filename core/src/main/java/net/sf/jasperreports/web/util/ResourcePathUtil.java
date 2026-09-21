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

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ResourcePathUtil
{
	private static final String EXCEPTION_MESSAGE_KEY_CONFIG_PROPERTY_NOT_SET = "web.util.config.property.not.set";

	protected JRPropertiesUtil propertiesUtil;
	
	protected ResourcePathUtil(JasperReportsContext jasperReportsContext) 
	{
		this.propertiesUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
	}
	
	public static ResourcePathUtil getInstance(JasperReportsContext jasperReportsContext) 
	{
		return new ResourcePathUtil(jasperReportsContext);
	}
	
	/**
	 *
	 */
	public String getReportExecutionPath() 
	{
		String path = propertiesUtil.getProperty(WebConstants.PROPERTY_REPORT_EXECUTION_PATH);
		if (path == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CONFIG_PROPERTY_NOT_SET,
					new Object[]{WebConstants.PROPERTY_REPORT_EXECUTION_PATH});
		}
		return path;
	}
	
	/**
	 *
	 */
	public String getResourcesPath() 
	{
		String path = propertiesUtil.getProperty(WebConstants.PROPERTY_REPORT_RESOURCES_PATH);
		if (path == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CONFIG_PROPERTY_NOT_SET,
					new Object[]{WebConstants.PROPERTY_REPORT_RESOURCES_PATH});
		}
		return path;
	}

	public String getResourcesBasePath() 
	{
		String resourcesBasePath = getResourcesPath();
		
		String resourceUriParamName = propertiesUtil.getProperty(WebConstants.PROPERTY_REQUEST_PARAMETER_RESOURCE_URI);

		return resourcesBasePath + "?" + resourceUriParamName + "=";
	}

	public boolean isComponentMetadataEmbedded()
	{
		return Boolean.parseBoolean(propertiesUtil.getProperty(WebConstants.PROPERTY_EMBED_COMPONENT_METADATA));
	}
}
