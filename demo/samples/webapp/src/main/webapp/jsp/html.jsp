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
<%@ page import="net.sf.jasperreports.jakarta.servlets.ImageServlet" %>
<%@ page import="net.sf.jasperreports.web.util.WebHtmlResourceHandler" %>


<%
	File reportFile = new File(application.getRealPath("/reports/WebappReport.jasper"));
    if (!reportFile.exists())
		throw new JRRuntimeException("File WebappReport.jasper not found. The report design must be compiled first.");

	JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile(reportFile.getPath());

	Map parameters = new HashMap();
	parameters.put("ReportTitle", "Address Report");
	parameters.put("BaseDir", reportFile.getParentFile());
				
	JasperPrint jasperPrint = 
		JasperFillManager.fillReport(
			jasperReport, 
			parameters, 
			new WebappDataSource()
			);
				
	HtmlExporter exporter = new HtmlExporter();

	session.setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, jasperPrint);
	
	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(out);
	output.setImageHandler(new WebHtmlResourceHandler("../servlets/image?image={0}"));
	exporter.setExporterOutput(output);
	
	exporter.exportReport();
%>

