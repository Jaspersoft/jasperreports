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
package net.sf.jasperreports.components.charts;

import java.awt.Color;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 *
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha
	})
public interface ChartPlot extends Serializable, JRCloneable
{
	/**
	 * Gets the chart background color.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getBackcolor();
	
	/**
	 * Gets the transparency factor for this plot background. The range is from 0 to 1, where 0 means transparent and 1
	 * opaque. The default is 1.
	 * @return a float value between 0 and 1.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Float getBackgroundAlpha();
	
	/**
	 * Gets the transparency factor for this plot foreground. The range is from 0 to 1, where 0 means transparent and 1
	 * opaque. The default is 1.
	 * @return a float value between 0 and 1.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Float getForegroundAlpha();
	
//	/**
//	 * Returns a list of all the defined series colors.  Every entry in the list is of type JRChartPlot.JRSeriesColor.
//	 * If there are no defined series colors this method will return an empty list, not null. 
//	 */
//	public SortedSet getSeriesColors();
//	
//	/**
//	 * Removes all defined series colors.
//	 */
//	public void clearSeriesColors();
//	
//	/**
//	 * Adds the specified series color to the plot.
//	 */
//	public void addSeriesColor(JRChartPlot.JRSeriesColor seriesColor);
//	
//	/**
//	 * Set the list of series colors.
//	 * 
//	 * @param colors the list of series colors ({@link JRSeriesColor} instances}
//	 */
//	public void setSeriesColors(Collection colors);

}
