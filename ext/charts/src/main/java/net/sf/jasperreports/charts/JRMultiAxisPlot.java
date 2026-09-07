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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.charts.design.JRDesignMultiAxisPlot;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * Represents the display options and nested charts of a multiple axis chart. 
 * The display options set at the root of a multiple axis chart override the 
 * options set in the nested charts.
 *
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_backgroundAlpha,
	JRXmlConstants.ATTRIBUTE_foregroundAlpha,
	JRXmlConstants.ATTRIBUTE_labelRotation,
	JRXmlConstants.ELEMENT_seriesColor,
	JRXmlConstants.ATTRIBUTE_axis
	})
@JsonTypeName("multiAxis")
@JsonDeserialize(as = JRDesignMultiAxisPlot.class)
public interface JRMultiAxisPlot extends JRChartPlot
{
	/**
	 * Returns a List of all the children axis.  Each element is of type 
	 * {@link net.sf.jasperreports.charts.JRChartAxis JRChartAxis}.
	 */
	@JsonManagedReference
	@JacksonXmlProperty(localName = "axis")
	@JacksonXmlElementWrapper(useWrapping = false)
	public List<JRChartAxis> getAxes();
}
