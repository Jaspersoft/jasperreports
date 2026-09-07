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
package net.sf.jasperreports.samples.springboot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRCsvDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@SpringBootApplication
@RestController
public class SpringBootApp
{

	public static void main(String[] args)
	{
		SpringApplication.run(SpringBootApp.class, args);
	}

	@GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
	public String index()
	{
		StringBuilder html = new StringBuilder();
		html.append("<html><head><title>JasperReports Spring Boot Sample</title></head><body>");
		html.append("<h1>Available Reports</h1>");

		File reportsDir = new File("target/reports");
		File[] jasperFiles = reportsDir.listFiles((dir, name) -> name.endsWith(".jasper"));
		if (jasperFiles != null && jasperFiles.length > 0)
		{
			Arrays.sort(jasperFiles);
			html.append("<ul>");
			for (File file : jasperFiles)
			{
				String reportName = file.getName().replace(".jasper", "");
				html.append("<li><a href=\"rest/pdf/")
					.append(reportName)
					.append("\">")
					.append(reportName)
					.append("</a></li>");
			}
			html.append("</ul>");
		}
		else
		{
			html.append("<p>No compiled reports found in target/reports/. Run <code>mvn compile</code> first.</p>");
		}

		html.append("</body></html>");
		return html.toString();
	}

	@GetMapping("/rest/pdf/{reportName}")
	public ResponseEntity<byte[]> pdf(@PathVariable String reportName) throws JRException
	{
		Map<String, Object> parameters = getParameters(reportName);

		JasperPrint jasperPrint = JasperFillManager.fillReport(
			"target/reports/" + reportName + ".jasper",
			parameters
		);

		byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + reportName + ".pdf")
			.contentType(MediaType.APPLICATION_PDF)
			.body(pdfBytes);
	}

	private Map<String, Object> getParameters(String reportName) throws JRException
	{
		if ("AllChartsReport".equals(reportName))
		{
			return getChartParameters();
		}
		return null;
	}

	private Map<String, Object> getChartParameters() throws JRException
	{
		try
		{
			Map<String, Object> parameters = new HashMap<>();

			for (int i = 1; i <= 7; i++)
			{
				JRCsvDataSource cds = new JRCsvDataSource(JRLoader.getLocationInputStream("data/categoryDatasource.csv"), "UTF-8");
				cds.setRecordDelimiter("\r\n");
				cds.setUseFirstRowAsHeader(true);
				parameters.put("categoryDatasource" + i, cds);
			}

			for (int i = 1; i <= 2; i++)
			{
				JRCsvDataSource pds = new JRCsvDataSource(JRLoader.getLocationInputStream("data/pieDatasource.csv"), "UTF-8");
				pds.setRecordDelimiter("\r\n");
				pds.setUseFirstRowAsHeader(true);
				parameters.put("pieDatasource" + i, pds);
			}

			JRCsvDataSource tpds = new JRCsvDataSource(JRLoader.getLocationInputStream("data/timePeriodDatasource.csv"), "UTF-8");
			tpds.setRecordDelimiter("\r\n");
			tpds.setUseFirstRowAsHeader(true);
			parameters.put("timePeriodDatasource1", tpds);

			for (int i = 1; i <= 3; i++)
			{
				JRCsvDataSource tsds = new JRCsvDataSource(JRLoader.getLocationInputStream("data/timeSeriesDatasource.csv"), "UTF-8");
				tsds.setRecordDelimiter("\r\n");
				tsds.setUseFirstRowAsHeader(true);
				tsds.setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
				parameters.put("timeSeriesDatasource" + i, tsds);
			}

			for (int i = 1; i <= 5; i++)
			{
				JRCsvDataSource xyds = new JRCsvDataSource(JRLoader.getLocationInputStream("data/xyDatasource.csv"), "UTF-8");
				xyds.setRecordDelimiter("\r\n");
				xyds.setUseFirstRowAsHeader(true);
				parameters.put("xyDatasource" + i, xyds);
			}

			return parameters;
		}
		catch (java.io.UnsupportedEncodingException e)
		{
			throw new JRException(e);
		}
	}
}
