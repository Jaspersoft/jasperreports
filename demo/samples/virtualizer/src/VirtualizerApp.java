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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.view.JasperViewer;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class VirtualizerApp extends AbstractSampleApp
{


	/**
	 *
	 */
	public static void main(String[] args) 
	{
		main(new VirtualizerApp(), args);
	}
	
	
	@Override
	public void test() throws JRException
	{
		export();
	}
	
	
	/**
	 *
	 */
	public void view() throws JRException
	{
		JasperPrint jasperPrint = fillReport();

		JasperViewer.viewReport(jasperPrint, true);
	}
	
	
	/**
	 *
	 */
	public void print() throws JRException
	{
		JasperPrint jasperPrint = fillReport();

		JasperPrintManager.printReport(jasperPrint, true);
	}
	
	
	/**
	 *
	 */
	public void pdf() throws JRException
	{
		JasperPrint jasperPrint = fillReport();

		exportPdf(jasperPrint);
	}
	
	
	/**
	 *
	 */
	public void xml() throws JRException
	{
		JasperPrint jasperPrint = fillReport();

		exportXml(jasperPrint, false);
	}
	
	
	/**
	 *
	 */
	public void xmlEmbed() throws JRException
	{
		JasperPrint jasperPrint = fillReport();

		exportXml(jasperPrint, true);
	}
	
	
	/**
	 *
	 */
	public void csv() throws JRException
	{
		JasperPrint jasperPrint = fillReport();

		exportCsv(jasperPrint);
	}
	
	
	/**
	 *
	 */
	public void export() throws JRException
	{
		// creating the virtualizer
		JRFileVirtualizer virtualizer = new JRFileVirtualizer(2, "tmp");

		JasperPrint jasperPrint = fillReport(virtualizer);

		exportPdf(jasperPrint);
		exportXml(jasperPrint, false);
		exportHtml(jasperPrint);
		exportCsv(jasperPrint);
		
		// manually cleaning up
		virtualizer.cleanup();
	}
	

	private static JasperPrint fillReport() throws JRException
	{
		// creating the virtualizer
		JRFileVirtualizer virtualizer = new JRFileVirtualizer(2, "tmp");
		
		return fillReport(virtualizer);
	}


	private static JasperPrint fillReport(JRFileVirtualizer virtualizer) throws JRException
	{
		long start = System.currentTimeMillis();

		// Virtualization works only with in memory JasperPrint objects.
		// All the operations will first fill the report and then export
		// the filled object.
		
		// creating the data source
		JRDataSource dataSource = new JREmptyDataSource(1000);
		
		// Preparing parameters
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

		// filling the report
		JasperPrint jasperPrint = JasperFillManager.fillReport("target/reports/VirtualizerReport.jasper", parameters, dataSource);
		
		virtualizer.setReadOnly(true);

		System.out.println("Filling time : " + (System.currentTimeMillis() - start));
		return jasperPrint;
	}


	private static void exportCsv(JasperPrint jasperPrint) throws JRException
	{
		long start = System.currentTimeMillis();
		JRCsvExporter exporter = new JRCsvExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput("target/reports/" + jasperPrint.getName() + ".csv"));

		exporter.exportReport();

		System.out.println("CSV creation time : " + (System.currentTimeMillis() - start));
	}

	
	private static void exportHtml(JasperPrint jasperPrint) throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToHtmlFile(jasperPrint, "target/reports/" + jasperPrint.getName() + ".html");
		System.out.println("HTML creation time : " + (System.currentTimeMillis() - start));
	}

	
	private static void exportXml(JasperPrint jasperPrint, boolean embedded) throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToXmlFile(jasperPrint, "target/reports/" + jasperPrint.getName() + ".jrpxml", embedded);
		System.out.println("XML creation time : " + (System.currentTimeMillis() - start));
	}

	
	private static void exportPdf(JasperPrint jasperPrint) throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToPdfFile(jasperPrint, "target/reports/" + jasperPrint.getName() + ".pdf");
		System.out.println("PDF creation time : " + (System.currentTimeMillis() - start));
	}

}
