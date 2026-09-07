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

import net.sf.jasperreports.charts.design.JRDesignHighLowPlot;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.JRExpression;


/**
 * Used only in combination with High-Low charts, this type of plot lets users customize 
 * the labels for both axes, like all the other axis-oriented plots.
 * <br/>
 * This special type of plot draws the items as vertical lines that start at the high value and 
 * go downward to the low value. On each line the plot displays by default small ticks to 
 * indicate the open and close values corresponding to the current item. To suppress these 
 * ticks, set to false the two flags available inside the plot definition: <code>isShowCloseTicks</code> 
 * and <code>isShowOpenTicks</code>. 
 * 
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha,
	JRXmlConstants.ATTRIBUTE_labelRotation,
	"showOpenTicks",
	"showCloseTicks",
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
@JsonTypeName("highLow")
@JsonDeserialize(as = JRDesignHighLowPlot.class)
public interface JRHighLowPlot extends JRChartPlot, JRTimeAxisFormat, JRValueAxisFormat
{

	/**
	 * 
	 */
	public JRExpression getTimeAxisLabelExpression();
	
	/**
	 * 
	 */
	public JRExpression getValueAxisLabelExpression();

	/**
	 * 
	 */
	public JRExpression getDomainAxisMinValueExpression();

	/**
	 * 
	 */
	public JRExpression getDomainAxisMaxValueExpression();

	/**
	 * 
	 */
	public JRExpression getRangeAxisMinValueExpression();

	/**
	 * 
	 */
	public JRExpression getRangeAxisMaxValueExpression();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getShowOpenTicks();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getShowCloseTicks();
	
}
