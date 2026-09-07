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

import net.sf.jasperreports.charts.design.JRDesignAreaPlot;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Area charts and Stacked Area charts rely on this plot to render their axes and items. This 
 * kind of plot allows users to specify only the labels for both axes and their format. 
 * Currently no other settings are necessary. 
 * 
 * @author Flavus Sana (flavius_sana@users.sourceforge.net) 
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha,
	JRXmlConstants.ATTRIBUTE_labelRotation,
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
	JRXmlConstants.ELEMENT_rangeAxisMaxValueExpression
	})
@JsonTypeName("area")
@JsonDeserialize(as = JRDesignAreaPlot.class)
public interface JRAreaPlot extends JRCategoryPlot
{
}
