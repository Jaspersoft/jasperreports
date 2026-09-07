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
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class TextApp extends AbstractSampleApp
{


	/**
	 *
	 */
	public static void main(String[] args) 
	{
		main(new TextApp(), args);
	}
	
	
	@Override
	public void test() throws JRException
	{
		fill();
		pdf();
		xml();
		text();
	}
	
	
	/**
	 *
	 */
	public void fill() throws JRException
	{
		long start = System.currentTimeMillis();
		//Preparing parameters
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ReportTitle", "Address Report");
		parameters.put("FilterClause", "'Boston', 'Chicago', 'Oslo'");
		parameters.put("OrderClause", "City");

		JasperFillManager.fillReportToFile("target/reports/TextReport.jasper", parameters, getDemoHsqldbConnection());
		System.out.println("Filling time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void print() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperPrintManager.printReport("target/reports/TextReport.jrprint", true);
		System.out.println("Printing time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void text() throws JRException
	{
		long start = System.currentTimeMillis();
		JRTextExporter exporter = new JRTextExporter();
		File sourceFile = new File("target/reports/TextReport.jrprint");
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".txt");

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));
		exporter.exportReport();

		System.out.println("Text creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void pdf() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperExportManager.exportReportToPdfFile("target/reports/TextReport.jrprint");
		System.out.println("PDF creation time : " + (System.currentTimeMillis() - start));
	}
	
	
	/**
	 *
	 */
	public void xml() throws JRException
	{
		long start = System.currentTimeMillis();
		File sourceFile = new File("target/reports/TextReport.jrprint");

		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".jrpxml");
		
		JRXmlExporter exporter = new JRXmlExporter();
		
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleXmlExporterOutput(destFile));
		
		exporter.exportReport();

		System.out.println("XML creation time : " + (System.currentTimeMillis() - start));
	}


}
