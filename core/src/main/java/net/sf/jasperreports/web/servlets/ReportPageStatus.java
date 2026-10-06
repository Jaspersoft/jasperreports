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
package net.sf.jasperreports.web.servlets;

import net.sf.jasperreports.engine.fill.FillListener;

/**
 * Status of a page in a generated report.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReportPageStatus
{

	private static final long STATUS_NO_SUCH_PAGE = -1;
	private static final long STATUS_PAGE_FINAL = 0;
	
	/**
	 * Status that indicates that the requested page does not exist.
	 */
	public static final ReportPageStatus NO_SUCH_PAGE = new ReportPageStatus(STATUS_NO_SUCH_PAGE);
	
	/**
	 * Status to indicate that a page in its final form has been generated.
	 */
	public static final ReportPageStatus PAGE_FINAL = new ReportPageStatus(STATUS_PAGE_FINAL);
	
	/**
	 * Creates a status for a non-final generated page.
	 * 
	 * @param timestamp the timestamp of the last modification of the page
	 * @param modified whether the page was modified since the last request
	 */
	public static ReportPageStatus nonFinal(long timestamp, boolean modified)
	{
		return new ReportPageStatus(modified ? timestamp : -timestamp);
	}
	
	private final long status;
	
	protected ReportPageStatus(long status)
	{
		this.status = status;
	}

	/**
	 * Determines whether the page exists in the generated report.
	 * 
	 * @return whether the page exists in the generated report
	 */
	public boolean pageExists()
	{
		return status != STATUS_NO_SUCH_PAGE;
	}
	
	/**
	 * Determines whether the page is final.
	 * 
	 * @return whether the page is final
	 * @see FillListener#pageUpdated(net.sf.jasperreports.engine.JasperPrint, int)
	 */
	public boolean isPageFinal()
	{
		return status == STATUS_PAGE_FINAL;
	}
	
	/**
	 * Determines whether the page has been modified since the last request.
	 * 
	 * @return whether the page has been modified since the last request
	 */
	public boolean hasModified()
	{
		return status >= 0;
	}
	
	/**
	 * Returns the timestamp of the last modification on the page.
	 * 
	 * @return the timestamp of the last modification on the page
	 */
	public long getTimestamp()
	{
		return status < -1 ? -status : status;
	}
}
