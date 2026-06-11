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
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.poi.export.JRXlsExporter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ImagesApp extends AbstractSampleApp
{


	/**
	 *
	 */
	public static void main(String[] args) 
	{
		main(new ImagesApp(), args);
	}
	
	
	@Override
	public void test() throws JRException
	{
		fill();
		pdf();
		xmlEmbed();
		xml();
		html();
		rtf();
		xls();
		csv();
		odt();
		ods();
		docx();
		xlsx();
		pptx();
	}
	
	
	/**
	 *
	 */
	public void fill() throws JRException
	{
		String destFile = JasperFillManager.fillReportToFile("target/reports/ImagesReport.jasper", null);
		System.out.println(destFile);

		fill("dukesign.jpg", "panoramic");
		fill("dukewave.png", "upright");
	}
	
	
	/**
	 *
	 */
	private void fill(String imageFileName, String folderName) throws JRException
	{
		fill(imageFileName, folderName, RotationEnum.NONE);
		fill(imageFileName, folderName, RotationEnum.LEFT);
		fill(imageFileName, folderName, RotationEnum.RIGHT);
		fill(imageFileName, folderName, RotationEnum.UPSIDE_DOWN);
	}
	
	
	/**
	 *
	 */
	private void fill(String imageFileName, String folderName, RotationEnum rotation) throws JRException
	{
		File folder = new File("target/reports/" + folderName + (rotation == RotationEnum.NONE ? "" : ("-" + rotation.getName().toLowerCase())));
		folder.mkdirs();
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("ImageFileName", imageFileName);

		File[] files = getFiles(new File("target/reports/scale"), "jasper");
		for(int i = 0; i < files.length; i++)
		{
			File reportFile = files[i];
			JasperReport jasperReport = (JasperReport)JRLoader.loadObject(reportFile);
			jasperReport.getStyles()[0].setRotation(rotation);
			File destFile = new File(folder, reportFile.getName().substring(0, reportFile.getName().length() - ".jasper".length()) + ".jrprint"); 
			JasperFillManager.fillReportToFile(
				jasperReport, 
				destFile.getAbsolutePath(), 
				parameters
				);
			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void print() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File reportFile = files[i];
			JasperPrintManager.printReport(reportFile.getAbsolutePath(), true);
			System.out.println(reportFile);
		}
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
			String destFile = JasperExportManager.exportReportToPdfFile(reportFile.getAbsolutePath());
			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void xml() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File reportFile = files[i];
			String destFile = JasperExportManager.exportReportToXmlFile(reportFile.getAbsolutePath(), false);
			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void xmlEmbed() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File reportFile = files[i];
			String destFile = JasperExportManager.exportReportToXmlFile(reportFile.getAbsolutePath(), true);
			System.out.println(destFile);
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
			String destFile = JasperExportManager.exportReportToHtmlFile(reportFile.getAbsolutePath());
			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void rtf() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");

			JRRtfExporter exporter = new JRRtfExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void xls() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");

			JRXlsExporter exporter = new JRXlsExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
			SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
			configuration.setOnePagePerSheet(false);
			exporter.setConfiguration(configuration);

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void csv() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".csv");

			JRCsvExporter exporter = new JRCsvExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleWriterExporterOutput(destFile));

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void odt() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".odt");

			JROdtExporter exporter = new JROdtExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void ods() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".ods");

			JROdsExporter exporter = new JROdsExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void docx() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".docx");

			JRDocxExporter exporter = new JRDocxExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void xlsx() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xlsx");

			JRXlsxExporter exporter = new JRXlsxExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setOnePagePerSheet(false);
			exporter.setConfiguration(configuration);

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
	/**
	 *
	 */
	public void pptx() throws JRException
	{
		File[] files = getFiles(new File("target/reports"), "jrprint");
		for(int i = 0; i < files.length; i++)
		{
			File sourceFile = files[i];

			JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

			File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pptx");

			JRPptxExporter exporter = new JRPptxExporter();

			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFile));

			exporter.exportReport();

			System.out.println(destFile);
		}
	}
	
	
}
