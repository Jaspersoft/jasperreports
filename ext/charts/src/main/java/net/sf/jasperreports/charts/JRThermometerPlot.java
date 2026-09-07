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

import java.awt.Color;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.design.JRDesignThermometerPlot;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.charts.type.ValueLocationEnum;

/**
 * Represents the display options of a Thermometer chart.  A Thermometer chart
 * consists of the outline of a thermometer, a scale showing the reading of
 * the thermometer, three optional ranges corresponding to "good", "warning"
 * and "critical", and the textual display of the value.
 *
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha,
	JRXmlConstants.ATTRIBUTE_labelRotation,
	"valueLocation",
	"mercuryColor",
	JRXmlConstants.ELEMENT_seriesColor,
	JRXmlConstants.ELEMENT_dataRange,
	JRXmlConstants.ELEMENT_valueDisplay,
	"lowRange",
	"mediumRange",
	"highRange"
	})
@JsonTypeName("thermometer")
@JsonDeserialize(as = JRDesignThermometerPlot.class)
public interface JRThermometerPlot extends JRChartPlot
{
	/**
	 * Returns the range of values displayed by this thermometer.  This range
	 * corresponds with the scale on the thermometer.
	 *
	 * @return the range of values displayed by this thermometer
	 */
	public JRDataRange getDataRange();

	/**
	 * Returns formatting information for the textual representation of the value.
	 *
	 * @return formatting information for the value
	 */
	public JRValueDisplay getValueDisplay();

	/**
	 * Returns the location where the value of the thermometer will be shown.  The
	 * return value will be one of {@link net.sf.jasperreports.charts.type.ValueLocationEnum#NONE NONE},
	 * {@link net.sf.jasperreports.charts.type.ValueLocationEnum#LEFT LEFT}, {@link net.sf.jasperreports.charts.type.ValueLocationEnum#RIGHT RIGHT} or
	 * {@link net.sf.jasperreports.charts.type.ValueLocationEnum#BULB BULB}.
	 *
	 * @return the location where the value of the thermometer will be shown
	 */
	@JacksonXmlProperty(isAttribute = true)
	public ValueLocationEnum getValueLocation();

	/**
	 * Returns the color of the "mercury" in the thermometer when the value is
	 * not in a defined range.
	 *
	 * @return the default color of the mercury in the thermometer
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Color getMercuryColor();

	/**
	 * Returns the low range, or <code>null</code> if undefined.
	 *
	 * @return the low range, or <code>null</code> if undefined.
	 */
	public JRDataRange getLowRange();

	/**
	 * Returns the medium range, or <code>null</code> if undefined.
	 *
	 * @return the medium range, or <code>null</code> if undefined.
	 */
	public JRDataRange getMediumRange();

	/**
	 * Returns the high range, or <code>null</code> if undefined.
	 *
	 * @return the high range, or <code>null</code> if undefined.
	 */
	public JRDataRange getHighRange();
}
