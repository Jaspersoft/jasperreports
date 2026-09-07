<%--
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
--%>

<%@ page errorPage="error.jsp" %>
<%@ page import="net.sf.jasperreports.engine.*" %>

<%
	JasperCompileManager.compileReportToFile(application.getRealPath("/reports/WebappReport.jrxml"));
%>

<html>
<head>
<title>JasperReports - Web Application Sample</title>
<link rel="stylesheet" type="text/css" href="../stylesheet.css" title="Style">
</head>

<body bgcolor="white">

<span class="bold">The JRXML report design was successfully compiled.</span>

</body>
</html>
