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
	"timeAxisLabelColor",
	"timeAxisTickLabelColor",
	"timeAxisTickLabelMask",
	"timeAxisVerticalTickLabels",
	"timeAxisLineColor",
	"timeAxisLabelFont",
	"timeAxisTickLabelFont"
	})
public interface JRTimeAxisFormat
{

	/**
	 * 
	 */
	public JRFont getTimeAxisLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getTimeAxisLabelColor();
	
	/**
	 * 
	 */
	@JsonGetter("timeAxisLabelColor")
	@JacksonXmlProperty(localName = "timeAxisLabelColor", isAttribute = true)
	public Color getOwnTimeAxisLabelColor();
	
	/**
	 * 
	 */
	public JRFont getTimeAxisTickLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getTimeAxisTickLabelColor();

	/**
	 * 
	 */
	@JsonGetter("timeAxisTickLabelColor")
	@JacksonXmlProperty(localName = "timeAxisTickLabelColor", isAttribute = true)
	public Color getOwnTimeAxisTickLabelColor();

	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getTimeAxisTickLabelMask();

	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getTimeAxisVerticalTickLabels();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getTimeAxisLineColor();
	
	/**
	 * 
	 */
	@JsonGetter("timeAxisLineColor")
	@JacksonXmlProperty(localName = "timeAxisLineColor", isAttribute = true)
	public Color getOwnTimeAxisLineColor();

}
