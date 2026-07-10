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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRFont;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"yAxisLabelColor",
	"yAxisTickLabelColor",
	"yAxisTickLabelMask",
	"yAxisVerticalTickLabels",
	"yAxisLineColor",
	"yAxisLabelFont",
	"yAxisTickLabelFont"
	})
public interface JRYAxisFormat
{
	
	/**
	 * 
	 */
	public JRFont getYAxisLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getYAxisLabelColor();
	
	/**
	 * 
	 */
	@JsonGetter("yAxisLabelColor")
	@JacksonXmlProperty(localName = "yAxisLabelColor", isAttribute = true)
	public Color getOwnYAxisLabelColor();
	
	/**
	 * 
	 */
	public JRFont getYAxisTickLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getYAxisTickLabelColor();

	/**
	 * 
	 */
	@JsonGetter("yAxisTickLabelColor")
	@JacksonXmlProperty(localName = "yAxisTickLabelColor", isAttribute = true)
	public Color getOwnYAxisTickLabelColor();

	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getYAxisTickLabelMask();

	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getYAxisVerticalTickLabels();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getYAxisLineColor();
	
	/**
	 * 
	 */
	@JsonGetter("yAxisLineColor")
	@JacksonXmlProperty(localName = "yAxisLineColor", isAttribute = true)
	public Color getOwnYAxisLineColor();
	
}
