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

<%@ page import="java.io.File" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<%@ page import="datasource.WebappDataSource" %>
<%@ page import="net.sf.jasperreports.engine.JRRuntimeException" %>
<%@ page import="net.sf.jasperreports.engine.JasperFillManager" %>
<%@ page import="net.sf.jasperreports.engine.JasperPrint" %>
<%@ page import="net.sf.jasperreports.engine.JasperReport" %>
<%@ page import="net.sf.jasperreports.engine.export.HtmlExporter" %>
<%@ page import="net.sf.jasperreports.engine.util.JRLoader" %>
<%@ page import="net.sf.jasperreports.export.SimpleExporterInput" %>
<%@ page import="net.sf.jasperreports.export.SimpleHtmlExporterOutput" %>
<%@ page import="net.sf.jasperreports.export.SimpleHtmlExporterConfiguration" %>
<%@ page import="net.sf.jasperreports.export.SimpleHtmlReportConfiguration" %>
<%@ page import="net.sf.jasperreports.jakarta.servlets.ImageServlet" %>
<%@ page import="net.sf.jasperreports.web.util.WebHtmlResourceHandler" %>

<%
	JasperPrint jasperPrint = (JasperPrint)session.getAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE);
	
	if (request.getParameter("reload") != null || jasperPrint == null)
	{
		File reportFile = new File(application.getRealPath("/reports/WebappReport.jasper"));
		if (!reportFile.exists())
			throw new JRRuntimeException("File WebappReport.jasper not found. The report design must be compiled first.");

		JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());

		Map parameters = new HashMap();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("BaseDir", reportFile.getParentFile());
					
		jasperPrint = 
			JasperFillManager.fillReport(
				jasperReport, 
				parameters, 
				new WebappDataSource()
				);
				
		session.setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
	}
	
	HtmlExporter exporter = new HtmlExporter();
	
	int pageIndex = 0;
	int lastPageIndex = 0;
	if (jasperPrint.getPages() != null)
	{
		lastPageIndex = jasperPrint.getPages().size() - 1;
	}

	String pageStr = request.getParameter("page");
	try
	{
		pageIndex = Integer.parseInt(pageStr);
	}
	catch(Exception e)
	{
	}
	
	if (pageIndex < 0)
	{
		pageIndex = 0;
	}

	if (pageIndex > lastPageIndex)
	{
		pageIndex = lastPageIndex;
	}
	
	StringBuffer sbuffer = new StringBuffer();

	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	
	SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(sbuffer);
	output.setImageHandler(new WebHtmlResourceHandler("../servlets/image?image={0}"));
	exporter.setExporterOutput(output);
	
	SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
	reportConfig.setPageIndex(pageIndex);
	exporter.setConfiguration(reportConfig);
	
	SimpleHtmlExporterConfiguration exporterConfig = new SimpleHtmlExporterConfiguration();
	exporterConfig.setHtmlHeader("");
	exporterConfig.setBetweenPagesHtml("");
	exporterConfig.setHtmlFooter("");
	exporter.setConfiguration(exporterConfig);
	
	exporter.exportReport();
%>

<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<head>
  <style type="text/css">
    a {text-decoration: none}
  </style>
</head>
<body text="#000000" link="#000000" alink="#000000" vlink="#000000">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
<tr>
  <td width="50%">&nbsp;</td>
  <td align="left">
    <hr size="1" color="#000000">
    <table width="100%" cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td><a href="viewer.jsp?reload=true"><img src="../images/reload.GIF" border="0"></a></td>
        <td>&nbsp;&nbsp;&nbsp;</td>
<%
	if (pageIndex > 0)
	{
%>
        <td><a href="viewer.jsp?page=0"><img src="../images/first.GIF" border="0"></a></td>
        <td><a href="viewer.jsp?page=<%=pageIndex - 1%>"><img src="../images/previous.GIF" border="0"></a></td>
<%
	}
	else
	{
%>
        <td><img src="../images/first_grey.GIF" border="0"></td>
        <td><img src="../images/previous_grey.GIF" border="0"></td>
<%
	}

	if (pageIndex < lastPageIndex)
	{
%>
        <td><a href="viewer.jsp?page=<%=pageIndex + 1%>"><img src="../images/next.GIF" border="0"></a></td>
        <td><a href="viewer.jsp?page=<%=lastPageIndex%>"><img src="../images/last.GIF" border="0"></a></td>
<%
	}
	else
	{
%>
        <td><img src="../images/next_grey.GIF" border="0"></td>
        <td><img src="../images/last_grey.GIF" border="0"></td>
<%
	}
%>
        <td width="100%">&nbsp;</td>
      </tr>
    </table>
    <hr size="1" color="#000000">
  </td>
  <td width="50%">&nbsp;</td>
</tr>
<tr>
  <td width="50%">&nbsp;</td>
  <td align="center">

<%=sbuffer.toString()%>

  </td>
  <td width="50%">&nbsp;</td>
</tr>
</table>
</body>
</html>
