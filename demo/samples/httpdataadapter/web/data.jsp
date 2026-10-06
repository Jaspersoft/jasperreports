<%--
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
--%><%@ 
page errorPage="error.jsp" %><%@ 
page import="java.io.*" %><%

	String dataType = request.getParameter("type");
	
	if (dataType != null)
	{
		response.setContentType("application/" + dataType);

		int pageIndex = 1;
		String strPageIndex = request.getParameter("page");
		if (strPageIndex != null)
		{
			pageIndex = Integer.valueOf(strPageIndex);
		}

		InputStream is = new FileInputStream(application.getRealPath("WEB-INF/data/animals." + pageIndex + "." + dataType));
		InputStreamReader reader = new InputStreamReader(is);
		
		int ln = 0;
		char[] chars = new char[1000];
		
		while((ln = reader.read(chars)) > 0)
		{
			out.print(new String(chars, 0, ln));
		}
	}
%>