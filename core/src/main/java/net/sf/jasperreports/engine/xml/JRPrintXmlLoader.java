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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.xml.print.PrintXmlLoader;


/**
 * Utility class that helps reconverting XML documents into 
 * {@link net.sf.jasperreports.engine.JasperPrint} objects. 
 * <p>
 * Generated documents can be stored in XML format if they are exported using the
 * {@link net.sf.jasperreports.engine.export.JRXmlExporter}. After they're exported,
 * one can parse them back into {@link net.sf.jasperreports.engine.JasperPrint} objects
 * by using this class.
 * </p>
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRPrintXmlLoader
{
	
	private static final Log log = LogFactory.getLog(JRPrintXmlLoader.class);
	
	/**
	 *
	 */
	private final JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	protected JRPrintXmlLoader(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}


	/**
	 *
	 */
	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}


	/**
	 *
	 */
	public static JasperPrint loadFromFile(JasperReportsContext jasperReportsContext, String sourceFileName) throws JRException
	{
		JasperPrint jasperPrint = null;

		try (FileInputStream fis = new FileInputStream(sourceFileName))
		{
			JRPrintXmlLoader printXmlLoader = new JRPrintXmlLoader(jasperReportsContext);
			jasperPrint = printXmlLoader.loadXML(fis);
		}
		catch(IOException e)
		{
			throw new JRException(e);
		}

		return jasperPrint;
	}


	/**
	 * @see #loadFromFile(JasperReportsContext, String)
	 */
	public static JasperPrint loadFromFile(String sourceFileName) throws JRException
	{
		return loadFromFile(DefaultJasperReportsContext.getInstance(), sourceFileName);
	}


	/**
	 * @see #loadFromFile(String)
	 */
	public static JasperPrint load(String sourceFileName) throws JRException
	{
		return loadFromFile(sourceFileName);
	}


	/**
	 *
	 */
	public static JasperPrint load(JasperReportsContext jasperReportsContext, InputStream is) throws JRException
	{
		JasperPrint jasperPrint = null;

		JRPrintXmlLoader printXmlLoader = new JRPrintXmlLoader(jasperReportsContext);
		jasperPrint = printXmlLoader.loadXML(is);

		return jasperPrint;
	}


	/**
	 * @see #load(JasperReportsContext, InputStream)
	 */
	public static JasperPrint load(InputStream is) throws JRException
	{
		return load(DefaultJasperReportsContext.getInstance(), is);
	}


	/**
	 *
	 */
	private JasperPrint loadXML(InputStream is) throws JRException
	{
		PrintXmlLoader loader = new PrintXmlLoader();
		return loader.load(is);
	}

}
