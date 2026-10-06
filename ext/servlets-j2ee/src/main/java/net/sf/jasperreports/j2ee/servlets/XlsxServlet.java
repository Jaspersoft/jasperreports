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

import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class XlsxServlet extends AbstractXlsServlet
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;


	@Override
	protected JRXlsAbstractExporter getXlsExporter()
	{
		return new JRXlsxExporter(DefaultJasperReportsContext.getInstance());
	}

	@Override
	protected String getResponseContentType() {
		return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	}
	
	@Override
	protected void setResponseHeader(HttpServletResponse response) {
		response.setHeader("Content-Disposition", "inline; filename=\"file.xlsx\"");
	}
}
