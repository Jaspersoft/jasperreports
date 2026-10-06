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
package net.sf.jasperreports.charts;

import org.jfree.chart.JFreeChart;

import net.sf.jasperreports.engine.JRException;

/**
 * Chart themes give more control over chart output, including 
 * the creation of the JFreeChart object itself. Also, chart themes 
 * affect a whole range of chart types across multiple reports and are not necessarily tied to 
 * a specific chart element within a report. They can even apply globally to all charts within 
 * a given JasperReports deployment, applying a new look and feel to all charts created.
 * <br/>
 * A chart theme can be set globally using a configuration property within the 
 * <code>jasperreports.properties</code> file as follows: 
 * <br/><br/>
 * <code>net.sf.jasperreports.chart.theme=&lt;theme_name&gt;</code> 
 * <br/><br/>
 * The global chart theme can be overridden at report level using the following report 
 * property in the report template: 
 * <br/><br/>
 * <code>&lt;property name="net.sf.jasperreports.chart.theme" value="&lt;theme_name&gt;"/&gt;</code> 
 * <br/><br/>
 * If needed, at chart element level, the chart theme is specified using the <code>theme</code> attribute. 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net) 
 */
public interface ChartTheme
{

	/**
	 * @return an <code>org.jfree.chart.JFreeChart</code> object representing the chart 
	 */
	public JFreeChart createChart(ChartContext chartContext) throws JRException;

}
