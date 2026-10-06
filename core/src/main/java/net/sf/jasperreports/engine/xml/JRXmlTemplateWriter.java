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
package net.sf.jasperreports.engine.xml;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * {@link JRTemplate Template} XML serializer.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRXmlTemplateLoader
 */
public class JRXmlTemplateWriter
{

	private static final Log log = LogFactory.getLog(JRXmlTemplateWriter.class);
	
	/**
	 * Default XML output encoding.
	 */
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * Returns the XML representation of a template.
	 * <p/>
	 * Uses {@link #DEFAULT_ENCODING the default encoding}.
	 * 
	 * @param template the template
	 * @return the XML representation of the template
	 */
	public static String writeTemplate(JRTemplate template)
	{
		return writeTemplate(template, DEFAULT_ENCODING);
	}
	
	/**
	 * Returns the XML representation of a template.
	 * <p/>
	 * Uses {@link #DEFAULT_ENCODING the default encoding}.
	 * 
	 * @param jasperReportsContext
	 * @param template the template
	 * @return the XML representation of the template
	 */
	public static String writeTemplate(JasperReportsContext jasperReportsContext, JRTemplate template)
	{
		return writeTemplate(jasperReportsContext, template, DEFAULT_ENCODING);
	}
	
	/**
	 * Returns the XML representation of a template.
	 * 
	 * @param template the template
	 * @param encoding the XML encoding to use
	 * @return the XML representation of the template
	 */
	public static String writeTemplate(JRTemplate template, String encoding)
	{
		return writeTemplate(DefaultJasperReportsContext.getInstance(), template, encoding);
	}
	
	/**
	 * Returns the XML representation of a template.
	 * 
	 * @param jasperReportsContext
	 * @param template the template
	 * @param encoding the XML encoding to use
	 * @return the XML representation of the template
	 */
	public static String writeTemplate(JasperReportsContext jasperReportsContext,
			JRTemplate template, String encoding)
	{
		StringWriter stringOut = new StringWriter();
		try
		{
			writeTemplate(jasperReportsContext, template, stringOut, encoding);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		return stringOut.toString();
	}
	
	/**
	 * Writes the XML representation of a template to an output stream.
	 * <p/>
	 * Uses {@link #DEFAULT_ENCODING the default encoding}.
	 * 
	 * @param template the template
	 * @param out the output stream
	 */
	public static void writeTemplate(JRTemplate template, OutputStream out)
	{
		writeTemplate(template, out, DEFAULT_ENCODING);
	}
	
	/**
	 * Writes the XML representation of a template to an output stream.
	 * <p/>
	 * Uses {@link #DEFAULT_ENCODING the default encoding}.
	 * 
	 * @param jasperReportsContext
	 * @param template the template
	 * @param out the output stream
	 */
	public static void writeTemplate(JasperReportsContext jasperReportsContext,
			JRTemplate template, OutputStream out)
	{
		writeTemplate(jasperReportsContext, template, out, DEFAULT_ENCODING);
	}
	
	/**
	 * Writes the XML representation of a template to an output stream.
	 * 
	 * @param template the template
	 * @param out the output stream
	 * @param encoding the XML encoding to use
	 */
	public static void writeTemplate(JRTemplate template, OutputStream out, String encoding)
	{
		writeTemplate(DefaultJasperReportsContext.getInstance(), template, out, encoding);
	}
	
	/**
	 * Writes the XML representation of a template to an output stream.
	 *
	 * @param jasperReportsContext
	 * @param template the template
	 * @param out the output stream
	 * @param encoding the XML encoding to use
	 */
	public static void writeTemplate(JasperReportsContext jasperReportsContext,
			JRTemplate template, OutputStream out, String encoding)
	{
		try
		{
			Writer writer = new OutputStreamWriter(out, encoding);
			writeTemplate(jasperReportsContext, template, writer, encoding);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	/**
	 * Writes the XML representation of a template to a file.
	 * <p/>
	 * Uses {@link #DEFAULT_ENCODING the default encoding}.
	 * 
	 * @param template the template
	 * @param outputFile the output file name
	 */
	public static void writeTemplateToFile(JRTemplate template, String outputFile)
	{
		writeTemplateToFile(template, outputFile, DEFAULT_ENCODING);
	}
	
	/**
	 * Writes the XML representation of a template to a file.
	 * <p/>
	 * Uses {@link #DEFAULT_ENCODING the default encoding}.
	 * 
	 * @param jasperReportsContext
	 * @param template the template
	 * @param outputFile the output file name
	 */
	public static void writeTemplateToFile(JasperReportsContext jasperReportsContext, 
			JRTemplate template, String outputFile)
	{
		writeTemplateToFile(jasperReportsContext, template, outputFile, DEFAULT_ENCODING);
	}
	
	/**
	 * Writes the XML representation of a template to a file.
	 * 
	 * @param template the template
	 * @param outputFile the output file name
	 * @param encoding the XML encoding to use
	 */
	public static void writeTemplateToFile(JRTemplate template, String outputFile, String encoding)
	{
		writeTemplateToFile(DefaultJasperReportsContext.getInstance(), 
				template, outputFile, encoding);
	}
	
	/**
	 * Writes the XML representation of a template to a file.
	 * 
	 * @param jasperReportsContext
	 * @param template the template
	 * @param outputFile the output file name
	 * @param encoding the XML encoding to use
	 */
	public static void writeTemplateToFile(JasperReportsContext jasperReportsContext,
			JRTemplate template, String outputFile, String encoding)
	{
		BufferedOutputStream fileOut = null;
		boolean close = true;
		try
		{
			fileOut = new BufferedOutputStream(new FileOutputStream(outputFile));
			writeTemplate(jasperReportsContext, template, fileOut, encoding);
			
			fileOut.flush();
			close = false;
			fileOut.close();
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (fileOut != null && close)
			{
				try
				{
					fileOut.close();
				}
				catch (IOException e)
				{
					log.warn("Could not close file " + outputFile, e);
				}
			}
		}
	}
	
	protected static void writeTemplate(JasperReportsContext jasperReportsContext, 
			JRTemplate template, Writer out, String encoding) throws IOException
	{
		List<ReportWriterFactory> writerFactories = jasperReportsContext.getExtensions(ReportWriterFactory.class);
		for (ReportWriterFactory writerFactory : writerFactories)
		{
			ReportWriter reportWriter = writerFactory.createReportWriter(jasperReportsContext);
			boolean written = reportWriter.writeTemplate(template, encoding, out);
			if (written)
			{
				return;
			}
		}
		String version = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(JRXmlWriter.PROPERTY_REPORT_VERSION);
		throw new JRRuntimeException("No template writer found for version " + version + ".");
	}
	
	private JRXmlTemplateWriter()
	{
		//NOOP
	}
}
