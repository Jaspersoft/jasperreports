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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.charts.design.JRDesignTimeSeriesPlot;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.JRExpression;

/**
 * This type of plot is similar to the Line plot and Scatter plot in that it lets users configure 
 * the labels for both axes, the rendering of lines to connect the item points, and the rendering 
 * of the small shapes that mark each item point on the target plot area. It is used only in 
 * combination with Time Series charts. 
 * 
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha,
	JRXmlConstants.ATTRIBUTE_labelRotation,
	"showLines",
	"showShapes",
	"timeAxisLabelColor",
	"timeAxisTickLabelColor",
	"timeAxisTickLabelMask",
	"timeAxisVerticalTickLabels",
	"timeAxisLineColor",
	"valueAxisLabelColor",
	"valueAxisTickLabelColor",
	"valueAxisTickLabelMask",
	"valueAxisVerticalTickLabels",
	"valueAxisLineColor",
	JRXmlConstants.ELEMENT_seriesColor,
	JRXmlConstants.ELEMENT_timeAxisLabelExpression,
	"timeAxisLabelFont",
	"timeAxisTickLabelFont",
	JRXmlConstants.ELEMENT_valueAxisLabelExpression,
	"valueAxisLabelFont",
	"valueAxisTickLabelFont",
	JRXmlConstants.ELEMENT_domainAxisMinValueExpression,
	JRXmlConstants.ELEMENT_domainAxisMaxValueExpression,
	JRXmlConstants.ELEMENT_rangeAxisMinValueExpression,
	JRXmlConstants.ELEMENT_rangeAxisMaxValueExpression
	})
@JsonTypeName("timeSeries")
@JsonDeserialize(as = JRDesignTimeSeriesPlot.class)
public interface JRTimeSeriesPlot extends JRChartPlot, JRTimeAxisFormat, JRValueAxisFormat, JRCommonLinePlot
{

	/**
	 * @return the time axis label expression
	 */
	public JRExpression getTimeAxisLabelExpression();

	/**
	 * @return the value axis label expression
	 */
	public JRExpression getValueAxisLabelExpression();

	/**
	 * @return the minimum value expression for the domain axis
	 */
	public JRExpression getDomainAxisMinValueExpression();

	/**
	 * @return the maximum value expression for the domain axis
	 */
	public JRExpression getDomainAxisMaxValueExpression();

	/**
	 * @return the minimum value expression for the range axis
	 */
	public JRExpression getRangeAxisMinValueExpression();

	/**
	 * @return the maximum value expression for the range axis
	 */
	public JRExpression getRangeAxisMaxValueExpression();
	
}
