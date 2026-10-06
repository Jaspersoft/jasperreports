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
package net.sf.jasperreports.ohloh;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.export.GenericElementHtmlHandler;
import net.sf.jasperreports.engine.export.JRHtmlExporterContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class OhlohWidgetHtmlHandler implements
		GenericElementHtmlHandler
{

	private String projectIDParameter;
	private String widgetName;
	
	public String getWidgetName()
	{
		return widgetName;
	}

	public void setWidgetName(String widgetName)
	{
		this.widgetName = widgetName;
	}

	public String getProjectIDParameter()
	{
		return projectIDParameter;
	}

	public void setProjectIDParameter(String projectIDParameter)
	{
		this.projectIDParameter = projectIDParameter;
	}

	@Override
	public boolean toExport(JRGenericPrintElement element)
	{
		return getProjectID(element) != null;
	}

	protected String getProjectID(JRGenericPrintElement element)
	{
		return (String) element.getParameterValue(getProjectIDParameter());
	}

	@Override
	public String getHtmlFragment(JRHtmlExporterContext context, JRGenericPrintElement element)
	{
		StringBuilder script = new StringBuilder(128);
		script.append("<script type='text/javascript' src='https://www.openhub.net/p/");
		script.append(getProjectID(element));
		script.append("/widgets/");
		script.append(getWidgetName());
		script.append("?format=js'></script>");
		return script.toString();
	}
	
}
