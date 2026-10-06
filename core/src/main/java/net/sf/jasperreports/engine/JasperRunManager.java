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
package net.sf.jasperreports.engine;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

import net.sf.jasperreports.engine.fill.JRFiller;


/**
 * Facade class for the JasperReports engine. 
 * <p>
 * Sometimes it is useful to produce documents only in a popular format such as PDF or
 * HTML, without having to store on disk the serialized, intermediate
 * {@link net.sf.jasperreports.engine.JasperPrint} object produced by the report-filling
 * process.
 * </p><p>
 * This can be achieved using this manager class, which immediately exports the document
 * produced by the report-filling process into the desired output format.
 * </p>
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JasperRunManager
{
	private JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	private JasperRunManager(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	private static JasperRunManager getDefaultInstance()
	{
		return new JasperRunManager(DefaultJasperReportsContext.getInstance());
	}
	
	
	/**
	 *
	 */
	public static JasperRunManager getInstance(JasperReportsContext jasperReportsContext)
	{
		return new JasperRunManager(jasperReportsContext);
	}
	
	
	/**
	 * Fills a report and saves it directly into a PDF file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public String runToPdfFile(
		String sourceFileName, 
		Map<String,Object> params, 
		Connection conn
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/*   */
		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				JasperFillManager.getReportSource(jasperReportsContext, sourceFile), 
				params, conn);

		/*   */
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
		String destFileName = destFile.toString();

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfFile(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Fills a report and saves it directly into a PDF file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param sourceFileName the name of the compiled report file
	 * @param params the parameters map
	 * @return the name of the generated PDF file
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public String runToPdfFile(
		String sourceFileName, 
		Map<String,Object> params 
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/*   */
		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				JasperFillManager.getReportSource(jasperReportsContext, sourceFile), 
				params);

		/*   */
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
		String destFileName = destFile.toString();

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfFile(jasperPrint, destFileName);
		
		return destFileName;
	}

	
	/**
	 * Fills a report and saves it directly into a PDF file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public void runToPdfFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters, conn);

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfFile(jasperPrint, destFileName);
	}


	/**
	 * Fills a report and saves it directly into a PDF file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param destFileName PDF destination file name
	 * @param parameters     report parameters map
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public void runToPdfFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters 
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters);

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfFile(jasperPrint, destFileName);
	}

	
	/**
	 * Fills a report and sends it directly to an OutputStream in PDF format. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public void runToPdfStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(inputStream, parameters, conn);

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfStream(jasperPrint, outputStream);
	}


	/**
	 * Fills a report and sends it directly to an OutputStream in PDF format. 
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param inputStream compiled report input stream
	 * @param outputStream PDF output stream
	 * @param parameters parameters map
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public void runToPdfStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters 
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(inputStream, parameters);

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfStream(jasperPrint, outputStream);
	}

	
	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public byte[] runToPdf(
		String sourceFileName, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters, conn);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}


	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param parameters     report parameters map
	 * @return binary PDF output
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public byte[] runToPdf(
		String sourceFileName, 
		Map<String,Object> parameters 
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}

	
	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public byte[] runToPdf(
		InputStream inputStream, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(inputStream, parameters, conn);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}


	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param inputStream  input stream to read the compiled report design object from
	 * @param parameters   report parameters map
	 * @return binary PDF output
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public byte[] runToPdf(
		InputStream inputStream, 
		Map<String,Object> parameters 
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(inputStream, parameters);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}

	
	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public byte[] runToPdf(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(jasperReport, parameters, conn);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}


	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param jasperReport the compiled report
	 * @param parameters the parameters map
	 * @return binary PDF output
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public byte[] runToPdf(
		JasperReport jasperReport, 
		Map<String,Object> parameters 
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(jasperReport, parameters);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}

	
	/**
	 * Fills a report and saves it directly into a PDF file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public String runToPdfFile(
		String sourceFileName, 
		Map<String,Object> params, 
		JRDataSource jrDataSource
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/*   */
		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				JasperFillManager.getReportSource(jasperReportsContext, sourceFile), 
				params, jrDataSource);

		/*   */
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
		String destFileName = destFile.toString();

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfFile(jasperPrint, destFileName);
		
		return destFileName;
	}

	
	/**
	 * Fills a report and saves it directly into a PDF file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public void runToPdfFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters, jrDataSource);

		/*   */
		JasperExportManager.getInstance(jasperReportsContext).exportToPdfFile(jasperPrint, destFileName);
	}

	
	/**
	 * Fills a report and sends it directly to an OutputStream in PDF format. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public void runToPdfStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(inputStream, parameters, jrDataSource);

		JasperExportManager.getInstance(jasperReportsContext).exportToPdfStream(jasperPrint, outputStream);
	}

	
	/**
	 * Fills a report and sends it to an output stream in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public byte[] runToPdf(
		String sourceFileName, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters, jrDataSource);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}

	
	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public byte[] runToPdf(
		InputStream inputStream, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(inputStream, parameters, jrDataSource);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}

	
	/**
	 * Fills a report and returns byte array object containing the report in PDF format.
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public byte[] runToPdf(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(jasperReport, parameters, jrDataSource);

		return JasperExportManager.getInstance(jasperReportsContext).exportToPdf(jasperPrint);
	}


	/**
	 * Fills a report and saves it directly into a HTML file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public String runToHtmlFile(
		String sourceFileName, 
		Map<String,Object> params, 
		Connection conn
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/*   */
		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				JasperFillManager.getReportSource(jasperReportsContext, sourceFile), 
				params, conn);

		/*   */
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".html");
		String destFileName = destFile.toString();

		JasperExportManager.getInstance(jasperReportsContext).exportToHtmlFile(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Fills a report and saves it directly into a HTML file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param sourceFileName the name of the compiled report file
	 * @param params the parameters map
	 * @return the name of the generated HTML file
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public String runToHtmlFile(
		String sourceFileName, 
		Map<String,Object> params 
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/*   */
		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				JasperFillManager.getReportSource(jasperReportsContext, sourceFile), 
				params);

		/*   */
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".html");
		String destFileName = destFile.toString();

		JasperExportManager.getInstance(jasperReportsContext).exportToHtmlFile(jasperPrint, destFileName);
		
		return destFileName;
	}

	
	/**
	 * Fills a report and saves it directly into a HTML file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public void runToHtmlFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters, conn);

		JasperExportManager.getInstance(jasperReportsContext).exportToHtmlFile(jasperPrint, destFileName);
	}


	/**
	 * Fills a report and saves it directly into a HTML file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param destFileName name of the destination HTML file
	 * @param parameters     report parameters map
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.fill.JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public void runToHtmlFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters 
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters);

		JasperExportManager.getInstance(jasperReportsContext).exportToHtmlFile(jasperPrint, destFileName);
	}


	/**
	 * Fills a report and saves it directly into a HTML file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public String runToHtmlFile(
		String sourceFileName, 
		Map<String,Object> params, 
		JRDataSource jrDataSource
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/*   */
		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				JasperFillManager.getReportSource(jasperReportsContext, sourceFile), 
				params, jrDataSource);

		/*   */
		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".html");
		String destFileName = destFile.toString();

		JasperExportManager.getInstance(jasperReportsContext).exportToHtmlFile(jasperPrint, destFileName);
		
		return destFileName;
	}

	
	/**
	 * Fills a report and saves it directly into a HTML file. 
	 * The intermediate JasperPrint object is not saved on disk.
	 */
	public void runToHtmlFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		/*   */
		JasperPrint jasperPrint = jasperFillManager.fill(sourceFileName, parameters, jrDataSource);

		/*   */
		JasperExportManager.getInstance(jasperReportsContext).exportToHtmlFile(jasperPrint, destFileName);
	}
	
	
	/**
	 * @see #runToPdfFile(String, Map, Connection)
	 */
	public static String runReportToPdfFile(
		String sourceFileName, 
		Map<String,Object> params, 
		Connection conn
		) throws JRException
	{
		return getDefaultInstance().runToPdfFile(sourceFileName, params, conn);
	}


	/**
	 * @see #runToPdfFile(String, Map)
	 */
	public static String runReportToPdfFile(
		String sourceFileName, 
		Map<String,Object> params 
		) throws JRException
	{
		return getDefaultInstance().runToPdfFile(sourceFileName, params);
	}

	
	/**
	 * @see #runToPdfFile(String, String, Map, Connection)
	 */
	public static void runReportToPdfFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		getDefaultInstance().runToPdfFile(sourceFileName, destFileName, parameters, conn);
	}


	/**
	 * @see #runToPdfFile(String, String, Map)
	 */
	public static void runReportToPdfFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters 
		) throws JRException
	{
		getDefaultInstance().runToPdfFile(sourceFileName, destFileName, parameters);
	}

	
	/**
	 * @see #runToPdfStream(InputStream, OutputStream, Map, Connection)
	 */
	public static void runReportToPdfStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		getDefaultInstance().runToPdfStream(inputStream, outputStream, parameters, conn);
	}


	/**
	 * @see #runToPdfStream(InputStream, OutputStream, Map)
	 */
	public static void runReportToPdfStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters 
		) throws JRException
	{
		getDefaultInstance().runToPdfStream(inputStream, outputStream, parameters);
	}

	
	/**
	 * @see #runToPdf(String, Map, Connection)
	 */
	public static byte[] runReportToPdf(
		String sourceFileName, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		return getDefaultInstance().runToPdf(sourceFileName, parameters, conn);
	}


	/**
	 * @see #runToPdf(String, Map)
	 */
	public static byte[] runReportToPdf(
		String sourceFileName, 
		Map<String,Object> parameters 
		) throws JRException
	{
		return getDefaultInstance().runToPdf(sourceFileName, parameters);
	}

	
	/**
	 * @see #runToPdf(InputStream, Map, Connection)
	 */
	public static byte[] runReportToPdf(
		InputStream inputStream, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		return getDefaultInstance().runToPdf(inputStream, parameters, conn);
	}


	/**
	 * @see #runToPdf(InputStream, Map)
	 */
	public static byte[] runReportToPdf(
		InputStream inputStream, 
		Map<String,Object> parameters 
		) throws JRException
	{
		return getDefaultInstance().runToPdf(inputStream, parameters);
	}

	
	/**
	 * @see #runToPdf(JasperReport, Map, Connection)
	 */
	public static byte[] runReportToPdf(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		return getDefaultInstance().runToPdf(jasperReport, parameters, conn);
	}


	/**
	 * @see #runToPdf(JasperReport, Map)
	 */
	public static byte[] runReportToPdf(
		JasperReport jasperReport, 
		Map<String,Object> parameters 
		) throws JRException
	{
		return getDefaultInstance().runToPdf(jasperReport, parameters);
	}

	
	/**
	 * @see #runToPdfFile(String, Map, JRDataSource)
	 */
	public static String runReportToPdfFile(
		String sourceFileName, 
		Map<String,Object> params, 
		JRDataSource jrDataSource
		) throws JRException
	{
		return getDefaultInstance().runToPdfFile(sourceFileName, params, jrDataSource);
	}

	
	/**
	 * @see #runToPdfFile(String, String, Map, JRDataSource)
	 */
	public static void runReportToPdfFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		getDefaultInstance().runToPdfFile(sourceFileName, destFileName, parameters, jrDataSource);
	}

	
	/**
	 * @see #runToPdfStream(InputStream, OutputStream, Map, JRDataSource)
	 */
	public static void runReportToPdfStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		getDefaultInstance().runToPdfStream(inputStream, outputStream, parameters, jrDataSource);
	}

	
	/**
	 * @see #runToPdf(String, Map, JRDataSource)
	 */
	public static byte[] runReportToPdf(
		String sourceFileName, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		return getDefaultInstance().runToPdf(sourceFileName, parameters, jrDataSource);
	}

	
	/**
	 * @see #runToPdf(InputStream, Map, JRDataSource)
	 */
	public static byte[] runReportToPdf(
		InputStream inputStream, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		return getDefaultInstance().runToPdf(inputStream, parameters, jrDataSource);
	}

	
	/**
	 * @see #runToPdf(JasperReport, Map, JRDataSource)
	 */
	public static byte[] runReportToPdf(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		return getDefaultInstance().runToPdf(jasperReport, parameters, jrDataSource);
	}


	/**
	 * @see #runToHtmlFile(String, Map, Connection)
	 */
	public static String runReportToHtmlFile(
		String sourceFileName, 
		Map<String,Object> params, 
		Connection conn
		) throws JRException
	{
		return getDefaultInstance().runToHtmlFile(sourceFileName, params, conn);
	}


	/**
	 * @see #runToHtmlFile(String, Map)
	 */
	public static String runReportToHtmlFile(
		String sourceFileName, 
		Map<String,Object> params 
		) throws JRException
	{
		return getDefaultInstance().runToHtmlFile(sourceFileName, params);
	}

	
	/**
	 * @see #runToHtmlFile(String, String, Map, Connection)
	 */
	public static void runReportToHtmlFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		Connection conn
		) throws JRException
	{
		getDefaultInstance().runToHtmlFile(sourceFileName, destFileName, parameters, conn);
	}


	/**
	 * @see #runToHtmlFile(String, String, Map)
	 */
	public static void runReportToHtmlFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters 
		) throws JRException
	{
		getDefaultInstance().runToHtmlFile(sourceFileName, destFileName, parameters);
	}


	/**
	 * @see #runToHtmlFile(String, Map, JRDataSource)
	 */
	public static String runReportToHtmlFile(
		String sourceFileName, 
		Map<String,Object> params, 
		JRDataSource jrDataSource
		) throws JRException
	{
		return getDefaultInstance().runToHtmlFile(sourceFileName, params, jrDataSource);
	}

	
	/**
	 * @see #runToHtmlFile(String, String, Map, JRDataSource)
	 */
	public static void runReportToHtmlFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> parameters, 
		JRDataSource jrDataSource
		) throws JRException
	{
		getDefaultInstance().runToHtmlFile(sourceFileName, destFileName, parameters, jrDataSource);
	}
}
