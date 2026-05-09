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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.design.JRDesignBubblePlot;
import net.sf.jasperreports.charts.type.ScaleTypeEnum;
import net.sf.jasperreports.engine.JRExpression;

/**
 * Only Bubble charts use this type of plot. Like all other two-axis plots, it lets users control 
 * the labels displayed for each axis. 
 * <br/>
 * The plot draws an ellipse for each item present in the dataset for a given series. Usually 
 * this is a circle whose radius is specified by the <code>Z</code> value in that chart item. However, the 
 * plot needs to know whether the <code>Z</code> value is proportional to its corresponding <code>X</code> value or to 
 * its corresponding <code>Y</code> value in order to calculate the actual size of the bubble. 
 * <br/>
 * The type of bubble scaling is specified by the <code>scaleType</code> attribute that the plot exposes: 
 * <ul>
 * <li>Range axis scaling: The bubble is a circle with the radius proportional to the <code>Y</code> 
 * value for each item (<code>scaleType="RangeAxis"</code>).</li>
 * <li>Domain axis scaling: The bubble is a circle with the radius proportional to the 
 * <code>X</code> value for each item (<code>scaleType="DomainAxis"</code>).</li>
 * <li>Scaling on both axes: The bubble is an ellipse with the height proportional to 
 * the <code>Y</code> value and the width proportional to the <code>X</code> value for each item
 * (<code>scaleType="BothAxes"</code>).</li>
 * </ul>
 * By default, bubbles scale on the range axis. 
 *
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"backcolor",
	"orientation",
	"backgroundAlpha",
	"foregroundAlpha",
	"labelRotation",
	"scaleType",
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
	"seriesColor",
	"xAxisLabelExpression",
	"xAxisLabelFont",
	"xAxisTickLabelFont",
	"yAxisLabelExpression",
	"yAxisLabelFont",
	"yAxisTickLabelFont",
	"domainAxisMinValueExpression",
	"domainAxisMaxValueExpression",
	"rangeAxisMinValueExpression",
	"rangeAxisMaxValueExpression"
	})
@JsonTypeName("bubble")
@JsonDeserialize(as = JRDesignBubblePlot.class)
public interface JRBubblePlot extends JRChartPlot, JRXAxisFormat, JRYAxisFormat
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
	 * @return the scale type. Possible values are:
	 * <ul>
	 * <li>{@link net.sf.jasperreports.charts.type.ScaleTypeEnum#ON_BOTH_AXES ON_BOTH_AXES}</li>
	 * <li>{@link net.sf.jasperreports.charts.type.ScaleTypeEnum#ON_DOMAIN_AXIS ON_DOMAIN_AXIS}</li>
	 * <li>{@link net.sf.jasperreports.charts.type.ScaleTypeEnum#ON_RANGE_AXIS ON_RANGE_AXIS}</li>
	 * </ul>
	 * @see net.sf.jasperreports.charts.type.ScaleTypeEnum
	 */
	@JacksonXmlProperty(isAttribute = true)
	public ScaleTypeEnum getScaleType();

	/**
	 * Sets the scale type.
	 * @param scaleType the scale type
	 */
	public void setScaleType(ScaleTypeEnum scaleType);

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
