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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.dataadapters.DataAdapterParameterContributorFactory;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.engine.util.JRLoader;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class ExcelDataAdapterApp extends AbstractSampleApp
{
	

	/**
	 *
	 */
	public static void main(String[] args)
	{
		main(new ExcelDataAdapterApp(), args);
	}
	
	
	@Override
	public void test() throws JRException
	{
		fill();
		pdf();
		html();
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
		Set<String> states = new HashSet<String>();
		states.add("Active");
		states.add("Trial");
		parameters.put("IncludedStates", states);

		//query executer mode
		parameters.put("DataFile", "XLS query executer mode for Excel data adapter");
		JasperFillManager.fillReportToFile("target/reports/ExcelXlsQeDataAdapterReport.jasper", new HashMap<String, Object>(parameters));
		parameters.put("DataFile", "XLSX query executer mode for Excel data adapter");
		JasperFillManager.fillReportToFile("target/reports/ExcelXlsxQeDataAdapterReport.jasper", new HashMap<String, Object>(parameters));
		
		JasperReport jasperReport = (JasperReport)JRLoader.loadObjectFromFile("target/reports/ExcelXlsQeDataAdapterReport.jasper");
		jasperReport.setProperty(DataAdapterParameterContributorFactory.PROPERTY_DATA_ADAPTER_LOCATION, "data/XlsQeDataAdapter.jrdax");
		JasperFillManager.fillReportToFile(jasperReport, "target/reports/XlsQeDataAdapterReport.jrprint", new HashMap<String, Object>(parameters));

		jasperReport = (JasperReport)JRLoader.loadObjectFromFile("target/reports/ExcelXlsxQeDataAdapterReport.jasper");
		jasperReport.setProperty(DataAdapterParameterContributorFactory.PROPERTY_DATA_ADAPTER_LOCATION, "data/XlsxQeDataAdapter.jrdax");
		JasperFillManager.fillReportToFile(jasperReport, "target/reports/XlsxQeDataAdapterReport.jrprint", new HashMap<String, Object>(parameters));
		
		//data source mode
		parameters.put("DataFile", "Excel data adapter for XLS data source");
		JasperFillManager.fillReportToFile("target/reports/ExcelXlsDataAdapterReport.jasper", new HashMap<String, Object>(parameters));
		parameters.put("DataFile", "Excel data adapter for XLSX data source");
		JasperFillManager.fillReportToFile("target/reports/ExcelXlsxDataAdapterReport.jasper", new HashMap<String, Object>(parameters));
		
		jasperReport = (JasperReport)JRLoader.loadObjectFromFile("target/reports/ExcelXlsDataAdapterReport.jasper");
		jasperReport.setProperty(DataAdapterParameterContributorFactory.PROPERTY_DATA_ADAPTER_LOCATION, "data/XlsDataAdapter.jrdax");
		JasperFillManager.fillReportToFile(jasperReport, "target/reports/XlsDataAdapterReport.jrprint", new HashMap<String, Object>(parameters));

		jasperReport = (JasperReport)JRLoader.loadObjectFromFile("target/reports/ExcelXlsxDataAdapterReport.jasper");
		jasperReport.setProperty(DataAdapterParameterContributorFactory.PROPERTY_DATA_ADAPTER_LOCATION, "data/XlsxDataAdapter.jrdax");
		JasperFillManager.fillReportToFile(jasperReport, "target/reports/XlsxDataAdapterReport.jrprint", new HashMap<String, Object>(parameters));
		
		System.out.println("Filling time : " + (System.currentTimeMillis() - start));
	}


	/**
	 *
	 */
	public void print() throws JRException
	{
		long start = System.currentTimeMillis();
		JasperPrintManager.printReport("target/reports/ExcelXlsDataAdapterReport.jrprint", true);
		System.out.println("Printing time : " + (System.currentTimeMillis() - start));
	}


	/**
	 *
	 */
	public void pdf() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File reportFile = files[i];
			long start = System.currentTimeMillis();
			String fileName = reportFile.getAbsolutePath();
			JasperExportManager.exportReportToPdfFile(
				fileName, 
				fileName.substring(0, fileName.indexOf(".jrprint")) + ".pdf"
				);
			System.out.println("PDF creation time : " + (System.currentTimeMillis() - start));
		}
	}


	/**
	 *
	 */
	public void html() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File reportFile = files[i];
			long start = System.currentTimeMillis();
			String fileName = reportFile.getAbsolutePath();
			JasperExportManager.exportReportToHtmlFile(
					fileName, 
					fileName.substring(0, fileName.indexOf(".jrprint")) + ".html"
				);
			System.out.println("HTML creation time : " + (System.currentTimeMillis() - start));
		}
		
	}


}
