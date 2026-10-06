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
package net.sf.jasperreports.export;

import javax.print.PrintService;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;

import net.sf.jasperreports.engine.export.JRPrintServiceExporter;


/**
 * Interface containing settings used by the print service exporter.
 *
 * @see JRPrintServiceExporter
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface PrintServiceExporterConfiguration extends Graphics2DExporterConfiguration
{
	/**
	 * Returns an instance of <tt>javax.print.attribute.PrintRequestAttributeSet</tt>.
	 */
	public PrintRequestAttributeSet getPrintRequestAttributeSet();

	/**
	 * Returns an instance of <tt>javax.print.attribute.PrintServiceAttributeSet</tt>.
	 */
	public PrintServiceAttributeSet getPrintServiceAttributeSet();

	/**
	 * Returns a boolean value specifying whether a page setup dialog should be opened before printing.
	 */
	public Boolean isDisplayPageDialog();

	/**
	 * Returns a boolean value specifying whether a page setup dialog should be opened only once before printing in a batch export job.
	 */
	public Boolean isDisplayPageDialogOnlyOnce();

	/**
	 * Returns a boolean value specifying whether a print setup dialog should be opened before printing.
	 */
	public Boolean isDisplayPrintDialog();

	/**
	 * Returns a boolean value specifying whether a print setup dialog should be opened only once in a batch export job.
	 */
	public Boolean isDisplayPrintDialogOnlyOnce();

	/**
	 * Returns an instance of <tt>javax.print.PrintService</tt>, useful if users do not want JPS to lookup for an available print service.
	 */
	public PrintService getPrintService();
}
