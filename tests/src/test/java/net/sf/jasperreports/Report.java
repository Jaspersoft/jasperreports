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
package net.sf.jasperreports;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.BiConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.ITestContext;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Report
{

	private static final Log log = LogFactory.getLog(Report.class);
	
	private String jrxml;
	private String jrpxml;
	private BiConsumer<Report, JasperPrint> printConsumer = Report::checkDigest;
	
	public Report(String basename)
	{
		this(basename + ".jrxml", basename + ".jrpxml");
	}
	
	public Report(String jrxml, String jrpxml)
	{
		this.jrxml = jrxml;
		this.jrpxml = jrpxml;
	}
	
	protected SimpleJasperReportsContext jasperReportsContext;
	protected JasperReport report;
	private JasperFillManager fillManager;
	private String referenceJRPXMLDigest;
	private ITestContext testContext;
	private String runName;
	private Set<String> outputFileNames = new HashSet<>();

	public void init(ITestContext testContext)
	{
		this.testContext = testContext;
		jasperReportsContext = new SimpleJasperReportsContext();
		
		try
		{
			compileReport();
			readReferenceDigest();
		}
		catch (JRException | IOException | NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String getJRXML()
	{
		return jrxml;
	}
	
	public void addPrintConsumer(BiConsumer<Report, JasperPrint> printConsumer)
	{
		this.printConsumer = this.printConsumer.andThen(printConsumer);
	}
	
	public JasperReport compileReport() throws JRException, IOException
	{
		InputStream jrxmlInput = JRLoader.getResourceInputStream(jrxml);
		JasperDesign design;
		try
		{
			design = JRXmlLoader.load(jrxmlInput);
		}
		finally
		{
			jrxmlInput.close();
		}
		
		report = JasperCompileManager.compileReport(design);
		
		//TODO do we need this here?
		fillManager = JasperFillManager.getInstance(jasperReportsContext);
		
		return report;
	}

	protected void readReferenceDigest() throws JRException, NoSuchAlgorithmException
	{
		byte[] jrpxmlBytes = JRLoader.loadBytesFromResource(jrpxml);
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.update(jrpxmlBytes);
		referenceJRPXMLDigest = toDigestString(digest);
		log.debug("Reference report digest for " + jrpxml + " is " + referenceJRPXMLDigest);
	}
	
	public void runReport(Map<String, Object> params)
	{
		runReport(null, params);
	}
	
	/**
	 * Runs the report and checks the result.
	 * 
	 * @param runName name that distinguishes the output files of this run from the ones of other runs of the same report,
	 * or <code>null</code> if the report is run only once
	 * @param params the report parameters
	 */
	public void runReport(String runName, Map<String, Object> params)
	{
		Map<String, Object> reportParams = reportParams(params);
		try
		{
			JasperPrint print = fillManager.fill(report, reportParams);
			reportComplete(runName, reportParams, print);
		}
		catch (JRException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> reportParams(Map<String, Object> params)
	{
		if (params == null)
		{
			params = new HashMap<>();
		}
		params.put(JRParameter.REPORT_LOCALE, Locale.US);
		params.put(JRParameter.REPORT_TIME_ZONE, TimeZone.getTimeZone("GMT"));
		return params;
	}

	protected void reportComplete(String runName, Map<String, Object> params, JasperPrint print)
	{
		JRVirtualizer virtualizer = (JRVirtualizer) params.get(JRParameter.REPORT_VIRTUALIZER);
		if (virtualizer instanceof JRAbstractLRUVirtualizer)
		{
			((JRAbstractLRUVirtualizer) virtualizer).setReadOnly(true);
		}
		
		assert !print.getPages().isEmpty();
		
		this.runName = runName;
		try
		{
			printConsumer.accept(this, print);
		}
		finally
		{
			this.runName = null;
		}
		
		if (virtualizer != null)
		{
			virtualizer.cleanup();
		}
	}

	public void checkDigest(JasperPrint print)
	{
		checkDigest(print, null);
	}

	/**
	 * Checks the export of the report against the reference JRPXML.
	 * 
	 * @param print the report
	 * @param checkName name that distinguishes the output file of this check from the ones of other checks of the same run,
	 * such as the ones of print consumers, or <code>null</code> for the check of the filled report
	 */
	public void checkDigest(JasperPrint print, String checkName)
	{
		try
		{
			File outputFile = createXmlOutputFile(checkName);
			String digestString = xmlDigest(print, outputFile);
			log.debug("Report " + jrxml + " got " + digestString);
			assert digestString.equals(referenceJRPXMLDigest)
				: "Report " + jrxml + " output at " + outputFile.getAbsolutePath() + " does not match " + jrpxml;
		} 
		catch (NoSuchAlgorithmException | JRException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected String xmlDigest(JasperPrint print, File outputFile) 
			throws NoSuchAlgorithmException, FileNotFoundException, JRException, IOException
	{
		log.debug("XML export output at " + outputFile.getAbsolutePath());
		
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		try (
			DigestOutputStream out = 
				new DigestOutputStream(
					new BufferedOutputStream(new FileOutputStream(outputFile)), 
					digest
					)
			)
		{
			xmlExport(print, out);
		}
		
		return toDigestString(digest);
	}

	protected String toDigestString(MessageDigest digest)
	{
		byte[] digestBytes = digest.digest();
		StringBuilder digestString = new StringBuilder(digestBytes.length * 2);
		for (byte b : digestBytes)
		{
			digestString.append(String.format("%02x", b));
		}
		return digestString.toString();
	}
	
	/**
	 * Creates the export file under the TestNG output directory, at the path of the reference JRPXML.
	 * The file is not deleted, so that it can be compared with the reference when the digests differ.
	 * The same report can be checked several times (several runs, or print consumers such as {@link PrintSerializer}),
	 * so the run name and the check name, when present, are added to the file name to give each check its own file.
	 */
	protected File createXmlOutputFile(String checkName) throws IOException
	{
		String suffix = (runName == null ? "" : "." + runName) + (checkName == null ? "" : "." + checkName);
		String extension = ".jrpxml";
		String outputFileName = jrpxml.endsWith(extension)
			? jrpxml.substring(0, jrpxml.length() - extension.length()) + suffix + extension
			: jrpxml + suffix;
		// a check with the same name would overwrite the output of an earlier one, which might be the one that failed
		if (!outputFileNames.add(outputFileName))
		{
			throw new IllegalStateException("Report " + jrxml + " already has a check with output " + outputFileName 
				+ ", the runs or the checks need distinct names");
		}
		
		File outputFile = new File(new File(testContext.getOutputDirectory()), outputFileName);
		File outputDir = outputFile.getParentFile();
		if (!outputDir.exists())
		{
			outputDir.mkdirs();
		}
		return outputFile;
	}

	protected void xmlExport(JasperPrint print, OutputStream out) throws JRException, IOException
	{
		JRXmlExporter exporter = new JRXmlExporter();
		exporter.setExporterInput(new SimpleExporterInput(print));
		SimpleXmlExporterOutput output = new SimpleXmlExporterOutput(out);
		output.setEmbeddingImages(true);
		exporter.setExporterOutput(output);
		exporter.exportReport();
		out.close();
	}

}
