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
package net.sf.jasperreports.engine;

import java.awt.Image;
import java.io.InputStream;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.print.JRPrinterAWT;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * Facade class for the printing functionality exposed by the JasperReports library.
 * <p>
 * After having filled a report, you have the option of viewing it, exporting it to a different
 * format, or (most commonly) printing it.
 * <p>
 * In JasperReports, you can print reports using this manager class. It contains various methods that
 * can send entire documents or portions of them to the printer. It also allows people to choose
 * whether to display the print dialog. one can display the content of a page from a
 * JasperReports document by generating a <code>java.awt.Image</code> object for it using this
 * manager class.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JasperPrintManager
{
	private static final String EXCEPTION_MESSAGE_KEY_NO_AVAILABLE_PRINTER = "print.no.available.printer";
	
	private JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	private JasperPrintManager(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	private static JasperPrintManager getDefaultInstance()
	{
		return new JasperPrintManager(DefaultJasperReportsContext.getInstance());
	}
	
	
	/**
	 *
	 */
	public static JasperPrintManager getInstance(JasperReportsContext jasperReportsContext)
	{
		return new JasperPrintManager(jasperReportsContext);
	}
	
	
	/**
	 *
	 */
	public boolean print(
		String sourceFileName,
		boolean withPrintDialog
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObjectFromFile(sourceFileName);

		return print(jasperPrint, withPrintDialog);
	}


	/**
	 *
	 */
	public boolean print(
		InputStream inputStream,
		boolean withPrintDialog
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		return print(jasperPrint, withPrintDialog);
	}


	/**
	 *
	 */
	public boolean print(
		JasperPrint jasperPrint,
		boolean withPrintDialog
		) throws JRException
	{
		//artf1936
		boolean checkAvailablePrinters = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(jasperPrint, PROPERTY_CHECK_AVAILABLE_PRINTERS, true);
		if (checkAvailablePrinters && !JRPrintServiceExporter.checkAvailablePrinters()) 
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_NO_AVAILABLE_PRINTER,
					(Object[])null);
		}
		//END - artf1936
		
		return 
			print(
				jasperPrint,
				0,
				jasperPrint.getPages().size() - 1,
				withPrintDialog
				);
	}


	/**
	 *
	 */
	public boolean print(
		String sourceFileName,
		int pageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObjectFromFile(sourceFileName);

		return print(jasperPrint, pageIndex, withPrintDialog);
	}


	/**
	 *
	 */
	public boolean print(
		InputStream inputStream,
		int pageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		return print(jasperPrint, pageIndex, withPrintDialog);
	}


	/**
	 *
	 */
	public boolean print(
		JasperPrint jasperPrint,
		int pageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return 
			print(
				jasperPrint,
				pageIndex,
				pageIndex,
				withPrintDialog
				);
	}


	/**
	 *
	 */
	public boolean print(
		String sourceFileName,
		int firstPageIndex,
		int lastPageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObjectFromFile(sourceFileName);

		return 
			print(
				jasperPrint,
				firstPageIndex,
				lastPageIndex,
				withPrintDialog
				);
	}


	/**
	 *
	 */
	public boolean print(
		InputStream inputStream,
		int firstPageIndex,
		int lastPageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		return 
			print(
				jasperPrint,
				firstPageIndex,
				lastPageIndex,
				withPrintDialog
				);
	}


	/**
	 *
	 */
	public boolean print(
		JasperPrint jasperPrint,
		int firstPageIndex,
		int lastPageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return 
			new JRPrinterAWT(jasperReportsContext, jasperPrint).printPages(
				firstPageIndex,
				lastPageIndex,
				withPrintDialog
				);
	}


	/**
	 *
	 */
	public Image printToImage(
		String sourceFileName,
		int pageIndex,
		float zoom
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObjectFromFile(sourceFileName);

		return printToImage(jasperPrint, pageIndex, zoom);
	}


	/**
	 *
	 */
	public Image printToImage(
		InputStream inputStream,
		int pageIndex,
		float zoom
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		return printToImage(jasperPrint, pageIndex, zoom);
	}


	/**
	 *
	 */
	public Image printToImage(
		JasperPrint jasperPrint,
		int pageIndex,
		float zoom
		) throws JRException
	{
		return new JRPrinterAWT(jasperReportsContext, jasperPrint).printPageToImage(pageIndex, zoom);
	}


	/**
	 * @see #print(String, boolean)
	 */
	public static boolean printReport(
		String sourceFileName,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(sourceFileName, withPrintDialog);
	}


	/**
	 * @see #print(InputStream, boolean)
	 */
	public static boolean printReport(
		InputStream inputStream,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(inputStream, withPrintDialog);
	}


	/**
	 * @see #print(JasperPrint, boolean)
	 */
	public static boolean printReport(
		JasperPrint jasperPrint,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(jasperPrint, withPrintDialog);
	}


	/**
	 * @see #print(String, int, boolean)
	 */
	public static boolean printPage(
		String sourceFileName,
		int pageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(sourceFileName, pageIndex, withPrintDialog);
	}


	/**
	 * @see #print(InputStream, int, boolean)
	 */
	public static boolean printPage(
		InputStream inputStream,
		int pageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(inputStream, pageIndex, withPrintDialog);
	}


	/**
	 * @see #print(JasperPrint, int, boolean)
	 */
	public static boolean printPage(
		JasperPrint jasperPrint,
		int pageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(jasperPrint, pageIndex, withPrintDialog);
	}


	/**
	 * @see #print(String, int, int, boolean)
	 */
	public static boolean printPages(
		String sourceFileName,
		int firstPageIndex,
		int lastPageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(sourceFileName, firstPageIndex, lastPageIndex, withPrintDialog);
	}


	/**
	 * @see #print(InputStream, int, int, boolean)
	 */
	public static boolean printPages(
		InputStream inputStream,
		int firstPageIndex,
		int lastPageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(inputStream, firstPageIndex, lastPageIndex, withPrintDialog);
	}


	/**
	 * @see #print(JasperPrint, int, int, boolean)
	 */
	public static boolean printPages(
		JasperPrint jasperPrint,
		int firstPageIndex,
		int lastPageIndex,
		boolean withPrintDialog
		) throws JRException
	{
		return getDefaultInstance().print(jasperPrint, firstPageIndex, lastPageIndex, withPrintDialog); 
	}


	/**
	 * @see #printToImage(String, int, float)
	 */
	public static Image printPageToImage(
		String sourceFileName,
		int pageIndex,
		float zoom
		) throws JRException
	{
		return getDefaultInstance().printToImage(sourceFileName, pageIndex, zoom);
	}


	/**
	 * @see #printToImage(InputStream, int, float)
	 */
	public static Image printPageToImage(
		InputStream inputStream,
		int pageIndex,
		float zoom
		) throws JRException
	{
		return getDefaultInstance().printToImage(inputStream, pageIndex, zoom);
	}


	/**
	 * @see #printToImage(JasperPrint, int, float)
	 */
	public static Image printPageToImage(
		JasperPrint jasperPrint,
		int pageIndex,
		float zoom
		) throws JRException
	{
		return getDefaultInstance().printToImage(jasperPrint, pageIndex, zoom);
	}


	/**
	 * Property whose value is used to check the availability of printers accepting jobs.
	 * <p/>
	 * This property is by default set to <code>true</code>.
	 */
	@Property(
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_7_3
			)
	public static final String PROPERTY_CHECK_AVAILABLE_PRINTERS = JRPropertiesUtil.PROPERTY_PREFIX + "awt.check.available.printers";
}
