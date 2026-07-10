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
	"categoryAxisLabelColor",
	"categoryAxisTickLabelColor",
	"categoryAxisTickLabelMask",
	"categoryAxisVerticalTickLabels",
	"categoryAxisLineColor",
	"categoryAxisTickLabelRotation",
	"categoryAxisLabelFont",
	"categoryAxisTickLabelFont"
	})
public interface JRCategoryAxisFormat
{

	public static final String PROPERTY_CATEGORY_AXIS_TICK_LABEL_ROTATION = "categoryAxisTickLabelRotation";
	
	/**
	 * 
	 */
	public JRFont getCategoryAxisLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getCategoryAxisLabelColor();
		
	/**
	 * 
	 */
	@JsonGetter("categoryAxisLabelColor")
	@JacksonXmlProperty(localName = "categoryAxisLabelColor", isAttribute = true)
	public Color getOwnCategoryAxisLabelColor();
		
	/**
	 * 
	 */
	public JRFont getCategoryAxisTickLabelFont();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getCategoryAxisTickLabelColor();
	
	/**
	 * 
	 */
	@JsonGetter("categoryAxisTickLabelColor")
	@JacksonXmlProperty(localName = "categoryAxisTickLabelColor", isAttribute = true)
	public Color getOwnCategoryAxisTickLabelColor();
	
	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getCategoryAxisTickLabelMask();
	
	/**
	 * 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getCategoryAxisVerticalTickLabels();
	
	/**
	 * 
	 */
	@JsonIgnore
	public Color getCategoryAxisLineColor();
	
	/**
	 * 
	 */
	@JsonGetter("categoryAxisLineColor")
	@JacksonXmlProperty(localName = "categoryAxisLineColor", isAttribute = true)
	public Color getOwnCategoryAxisLineColor();
	
	/**
	 * Gets the angle in degrees to rotate the data axis labels.  The range is -360 to 360.  A positive value angles
	 * the label so it reads downwards wile a negative value angles the label so it reads upwards.  Only charts that
	 * use a category based axis (such as line or bar charts) support label rotation.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Double getCategoryAxisTickLabelRotation();
	
	/**
	 * Sets the angle in degrees to rotate the data axis labels.  The range is -360 to 360.  A positive value angles
	 * the label so it reads downwards wile a negative value angles the label so it reads upwards.  Only charts that
	 * use a category based axis (such as line or bar charts) support label rotation.
	 */
	public void setCategoryAxisTickLabelRotation(Double labelRotation);
	
}
