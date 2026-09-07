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
package net.sf.jasperreports.charts;

import java.util.Locale;
import java.util.TimeZone;

import org.jfree.data.general.Dataset;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net) 
 */
public interface ChartContext
{

	/**
	 * 
	 */
	public JasperReportsContext getJasperReportsContext();
	
	/**
	 * 
	 */
	public JRChart getChart();

	/**
	 * Returns the resolution of the report the chart belongs to. Charts lay themselves out in
	 * points, so anything derived from the pixel dimensions of the chart element has to be
	 * converted before it can be used as a chart coordinate.
	 */
	default int getReportDpi()
	{
		return JasperPrint.DEFAULT_REPORT_DPI;
	}

	/**
	 * 
	 */
	public Dataset getDataset();

	/**
	 * 
	 */
	public Object getLabelGenerator();

	/**
	 * 
	 */
	public Locale getLocale();

	public TimeZone getTimeZone();

	/**
	 * 
	 */
	public String evaluateTextExpression(JRExpression expression) throws JRException;

	/**
	 *
	 */
	public Object evaluateExpression(JRExpression expression) throws JRException;
}
