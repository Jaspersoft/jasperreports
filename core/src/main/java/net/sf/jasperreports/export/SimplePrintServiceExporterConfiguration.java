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


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimplePrintServiceExporterConfiguration extends SimpleGraphics2DExporterConfiguration implements PrintServiceExporterConfiguration
{
	private PrintRequestAttributeSet printRequestAttributeSet;
	private PrintServiceAttributeSet printServiceAttributeSet;
	private Boolean isDisplayPageDialog;
	private Boolean isDisplayPageDialogOnlyOnce;
	private Boolean isDisplayPrintDialog;
	private Boolean isDisplayPrintDialogOnlyOnce;
	private PrintService printService;

	/**
	 *
	 */
	public SimplePrintServiceExporterConfiguration()
	{
	}

	@Override
	public PrintRequestAttributeSet getPrintRequestAttributeSet()
	{
		return printRequestAttributeSet;
	}

	/**
	 *
	 */
	public void setPrintRequestAttributeSet(PrintRequestAttributeSet printRequestAttributeSet)
	{
		this.printRequestAttributeSet = printRequestAttributeSet;
	}

	@Override
	public PrintServiceAttributeSet getPrintServiceAttributeSet()
	{
		return printServiceAttributeSet;
	}

	/**
	 *
	 */
	public void setPrintServiceAttributeSet(PrintServiceAttributeSet printServiceAttributeSet)
	{
		this.printServiceAttributeSet = printServiceAttributeSet;
	}

	@Override
	public Boolean isDisplayPageDialog()
	{
		return isDisplayPageDialog;
	}

	/**
	 *
	 */
	public void setDisplayPageDialog(Boolean isDisplayPageDialog)
	{
		this.isDisplayPageDialog = isDisplayPageDialog;
	}

	@Override
	public Boolean isDisplayPageDialogOnlyOnce()
	{
		return isDisplayPageDialogOnlyOnce;
	}

	/**
	 *
	 */
	public void setDisplayPageDialogOnlyOnce(Boolean isDisplayPageDialogOnlyOnce)
	{
		this.isDisplayPageDialogOnlyOnce = isDisplayPageDialogOnlyOnce;
	}

	@Override
	public Boolean isDisplayPrintDialog()
	{
		return isDisplayPrintDialog;
	}

	/**
	 *
	 */
	public void setDisplayPrintDialog(Boolean isDisplayPrintDialog)
	{
		this.isDisplayPrintDialog = isDisplayPrintDialog;
	}

	@Override
	public Boolean isDisplayPrintDialogOnlyOnce()
	{
		return isDisplayPrintDialogOnlyOnce;
	}

	/**
	 *
	 */
	public void isDisplayPrintDialogOnlyOnce(Boolean isDisplayPrintDialogOnlyOnce)
	{
		this.isDisplayPrintDialogOnlyOnce = isDisplayPrintDialogOnlyOnce;
	}

	@Override
	public PrintService getPrintService()
	{
		return printService;
	}

	/**
	 *
	 */
	public void setPrintService(PrintService printService)
	{
		this.printService = printService;
	}
}
