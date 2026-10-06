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

/**
 * Information related to the status of a report execution.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReportExecutionStatus
{

	/**
	 * Report execution statuses.
	 * 
	 * @see JasperPrintAccessor#getReportStatus()
	 */
	public static enum Status
	{
		/**
		 * The report execution is in progress.
		 */
		RUNNING, 
		
		/**
		 * The report execution has finished successfully.
		 */
		FINISHED,
		
		/**
		 * There was an error during the report execution.
		 * 
		 * @see ReportExecutionStatus#getError()
		 */
		ERROR,
		
		/**
		 * The report execution was canceled by the user.
		 */
		CANCELED;
	}
	
	/**
	 * Creates a finished status.
	 * 
	 * @param pageCount the total number of pages
	 * @return a finished status
	 */
	public static ReportExecutionStatus finished(int pageCount)
	{
		return new ReportExecutionStatus(Status.FINISHED, pageCount, pageCount, null);
	}
	
	/**
	 * Creates a canceled status
	 * 
	 * @param pageCount the current number of pages
	 * @return a canceled status
	 */
	public static ReportExecutionStatus canceled(int pageCount)
	{
		return new ReportExecutionStatus(Status.CANCELED, null, pageCount, null);
	}
	
	/**
	 * Creates an error status.
	 * 
	 * @param pageCount the current number of pages
	 * @param error the error
	 * @return an error status
	 */
	public static ReportExecutionStatus error(int pageCount, Throwable error)
	{
		return new ReportExecutionStatus(Status.ERROR, null, pageCount, error);
	}
	
	/**
	 * Create a running status.
	 * 
	 * @param pageCount the current number of pages
	 * @return a running status
	 */
	public static ReportExecutionStatus running(int pageCount)
	{
		return new ReportExecutionStatus(Status.RUNNING, null, pageCount, null);
	}

	private final Status status;
	private final Integer totalPageCount;
	private final int currentPageCount;
	private final Throwable error;
	
	protected ReportExecutionStatus(Status status, 
			Integer totalPageCount, int currentPageCount, Throwable error)
	{
		this.status = status;
		this.totalPageCount = totalPageCount;
		this.currentPageCount = currentPageCount;
		this.error = error;
	}

	/**
	 * Returns the status of the execution.
	 * 
	 * @return the status of the execution
	 */
	public Status getStatus()
	{
		return status;
	}

	/**
	 * Returns the total number of pages in the report, or <code>null</code> if not yet known.
	 * 
	 * @return the total number of pages in the report if known
	 */
	public Integer getTotalPageCount()
	{
		return totalPageCount;
	}

	/**
	 * Returns the number of pages generated so far.
	 * 
	 * @return the current number of pages
	 */
	public int getCurrentPageCount()
	{
		return currentPageCount;
	}

	/**
	 * Returns the error encountered during the report generation, if any.
	 * 
	 * @return the error encountered during the report generation, if any
	 */
	public Throwable getError()
	{
		return error;
	}

}
