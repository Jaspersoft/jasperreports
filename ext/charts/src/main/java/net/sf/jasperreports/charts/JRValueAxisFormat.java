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
 * @author Flavus Sana (flavius_sana@users.sourceforge.net) 
 */
@JsonPropertyOrder({
	"valueAxisLabelColor",
	"valueAxisTickLabelColor",
	"valueAxisTickLabelMask",
	"valueAxisVerticalTickLabels",
	"valueAxisLineColor",
	"valueAxisLabelFont",
	"valueAxisTickLabelFont"
	})
public interface JRValueAxisFormat
{

	/**
	 * 
	 */
	public JRFont getValueAxisLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getValueAxisLabelColor();

	/**
	 * 
	 */
	@JsonGetter("valueAxisLabelColor")
	@JacksonXmlProperty(localName = "valueAxisLabelColor", isAttribute = true)
	public Color getOwnValueAxisLabelColor();

	/**
	 * 
	 */
	public JRFont getValueAxisTickLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getValueAxisTickLabelColor();
	
	/**
	 * 
	 */
	@JsonGetter("valueAxisTickLabelColor")
	@JacksonXmlProperty(localName = "valueAxisTickLabelColor", isAttribute = true)
	public Color getOwnValueAxisTickLabelColor();
	
	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getValueAxisTickLabelMask();
	
	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getValueAxisVerticalTickLabels();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getValueAxisLineColor();
	
	/**
	 * 
	 */
	@JsonGetter("valueAxisLineColor")
	@JacksonXmlProperty(localName = "valueAxisLineColor", isAttribute = true)
	public Color getOwnValueAxisLineColor();
}
