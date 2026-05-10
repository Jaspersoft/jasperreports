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

import net.sf.jasperreports.charts.design.JRDesignScatterPlot;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.JRExpression;

/**
 * Scatter plots are used only with Scatter charts. They render items as points on a two-axis 
 * plot area. This plot closely resembles the Line plot, in that it lets users 
 * configure the labels for both axes, the rendering of lines to unite the item points, and the 
 * rendering of the small shapes that mark each item point on the target plot area. 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha,
	JRXmlConstants.ATTRIBUTE_labelRotation,
	"showLines",
	"showShapes",
	"xAxisLabelColor",
	"xAxisTickLabelColor",
	"xAxisTickLabelMask",
	"xAxisVerticalTickLabels",
	"xAxisLineColor",
	"yAxisLabelColor",
	"yAxisTickLabelColor",
	"yAxisTickLabelMask",
	"yAxisVerticalTickLabels",
	"yAxisLineColor",
	JRXmlConstants.ELEMENT_seriesColor,
	JRXmlConstants.ELEMENT_xAxisLabelExpression,
	"xAxisLabelFont",
	"xAxisTickLabelFont",
	JRXmlConstants.ELEMENT_yAxisLabelExpression,
	"yAxisLabelFont",
	"yAxisTickLabelFont",
	JRXmlConstants.ELEMENT_domainAxisMinValueExpression,
	JRXmlConstants.ELEMENT_domainAxisMaxValueExpression,
	JRXmlConstants.ELEMENT_rangeAxisMinValueExpression,
	JRXmlConstants.ELEMENT_rangeAxisMaxValueExpression
	})
@JsonTypeName("scatter")
@JsonDeserialize(as = JRDesignScatterPlot.class)
public interface JRScatterPlot extends JRChartPlot, JRXAxisFormat, JRYAxisFormat, JRCommonLinePlot
{
	
	/**
	 * @return the x axis label expression
	 */
	public JRExpression getXAxisLabelExpression();

	/**
	 * @return the y axis label expression
	 */
	public JRExpression getYAxisLabelExpression();

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
