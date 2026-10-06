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

/**
 * Interface that represents a {@link net.sf.jasperreports.charts.JRChartCustomizer JRChartCustomizer} that is assigned 
 * a name after being instantiated using the default empty constructor. It will use that name to retrieve custom properties from 
 * the chart element, properties that are considered to be for its own use, having the name as part of the property name.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface NamedChartCustomizer extends JRChartCustomizer
{
	/**
	 *  
	 */
	public void setName(String name);
}
