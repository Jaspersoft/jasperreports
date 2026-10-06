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

import net.sf.jasperreports.engine.JasperPrint;

/**
 * {@link JasperPrint} accessor object.
 * 
 * Such an object is usually placed on the session when a report is generated.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JasperPrintAccessor
{
	
	public static final String REPORT_CONTEXT_PARAMETER_JASPER_PRINT_ACCESSOR = "net.sf.jasperreports.web.jasper_print.accessor";

	/**
	 * Ensures that a page is available in the generated report.
	 * 
	 * @param pageIdx the page index
	 * @param pageTimestamp 
	 * @return the status of the requested page
	 */
	ReportPageStatus pageStatus(int pageIdx, Long pageTimestamp);
	
	/**
	 * Returns the generated report.
	 * 
	 * @return the generated report
	 */
	JasperPrint getJasperPrint();
	
	/**
	 * Returns the generated report, ensuring before that the report generation has ended.
	 * 
	 * @return the final generated report
	 */
	JasperPrint getFinalJasperPrint();

	/**
	 * Returns the status of the report execution.
	 * 
	 * @return the status of the report execution
	 */
	ReportExecutionStatus getReportStatus();

}
