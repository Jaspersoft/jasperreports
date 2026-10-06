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
package net.sf.jasperreports.j2ee.servlets;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class BaseHttpServlet extends HttpServlet
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	
	/**
	 *
	 */
	public static final String DEFAULT_JASPER_PRINT_LIST_SESSION_ATTRIBUTE = "net.sf.jasperreports.j2ee.jasper_print_list";
	public static final String DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE = "net.sf.jasperreports.j2ee.jasper_print";

	public static final String JASPER_PRINT_LIST_REQUEST_PARAMETER = "jrprintlist";
	public static final String JASPER_PRINT_REQUEST_PARAMETER = "jrprint";

	public static final String BUFFERED_OUTPUT_REQUEST_PARAMETER = "buffered"; 
	
			
	/**
	 *
	 */
	public JasperReportsContext getJasperReportsContext()
	{
		return DefaultJasperReportsContext.getInstance();
	}

	/**
	 *
	 */
	public static List<JasperPrint> getJasperPrintList(HttpServletRequest request)
	{
		String jasperPrintListSessionAttr = request.getParameter(JASPER_PRINT_LIST_REQUEST_PARAMETER);
		if (jasperPrintListSessionAttr == null)
		{
			jasperPrintListSessionAttr = DEFAULT_JASPER_PRINT_LIST_SESSION_ATTRIBUTE;
		}

		String jasperPrintSessionAttr = request.getParameter(JASPER_PRINT_REQUEST_PARAMETER);
		if (jasperPrintSessionAttr == null)
		{
			jasperPrintSessionAttr = DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE;
		}
		
		List<JasperPrint> jasperPrintList = (List<JasperPrint>)request.getSession().getAttribute(jasperPrintListSessionAttr);
		if (jasperPrintList == null)
		{
			JasperPrint jasperPrint = (JasperPrint)request.getSession().getAttribute(jasperPrintSessionAttr);
			if (jasperPrint != null)
			{
				jasperPrintList = new ArrayList<>();
				jasperPrintList.add(jasperPrint);
			}
		}
		
		return jasperPrintList;
	}


}
