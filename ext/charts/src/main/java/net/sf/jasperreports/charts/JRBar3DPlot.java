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

import net.sf.jasperreports.charts.design.JRDesignBar3DPlot;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Type of plot 
 * used to render Bar 3D and the Stacked Bar 3D charts. 
 * <br/>
 * Like the {@link net.sf.jasperreports.charts.JRBarPlot JRBarPlot}, the JRBar3DPlot 
 * allows customization of the labels for both of its axes and the display of the item labels
 * 
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 * @deprecated To be removed.
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha,
	JRXmlConstants.ATTRIBUTE_labelRotation,
	"showTickMarks",
	"showTickLabels",
	"showLabels",
	JRXmlConstants.ATTRIBUTE_xOffset,
	JRXmlConstants.ATTRIBUTE_yOffset,
	"categoryAxisLabelColor",
	"categoryAxisTickLabelColor",
	"categoryAxisTickLabelMask",
	"categoryAxisVerticalTickLabels",
	"categoryAxisLineColor",
	"categoryAxisTickLabelRotation",
	"valueAxisLabelColor",
	"valueAxisTickLabelColor",
	"valueAxisTickLabelMask",
	"valueAxisVerticalTickLabels",
	"valueAxisLineColor",
	JRXmlConstants.ELEMENT_seriesColor,
	JRXmlConstants.ELEMENT_categoryAxisLabelExpression,
	"categoryAxisLabelFont",
	"categoryAxisTickLabelFont",
	JRXmlConstants.ELEMENT_valueAxisLabelExpression,
	"valueAxisLabelFont",
	"valueAxisTickLabelFont",
	JRXmlConstants.ELEMENT_domainAxisMinValueExpression,
	JRXmlConstants.ELEMENT_domainAxisMaxValueExpression,
	JRXmlConstants.ELEMENT_rangeAxisMinValueExpression,
	JRXmlConstants.ELEMENT_rangeAxisMaxValueExpression,
	JRXmlConstants.ELEMENT_itemLabel
	})
@JsonTypeName("bar3D")
@JsonDeserialize(as = JRDesignBar3DPlot.class)
public interface JRBar3DPlot extends JRBarPlot
{

	/**
	 * @return the x offset
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Double getXOffset();
	
	/**
	 * Sets the x offset
	 * @param xOffset the x offset
	 */
	public void setXOffset(Double xOffset);
	
	/**
	 * @return the y offset
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Double getYOffset();
	
	/**
	 * Sets the y offset
	 * @param yOffset the y offset
	 */
	public void setYOffset(Double yOffset);

}
