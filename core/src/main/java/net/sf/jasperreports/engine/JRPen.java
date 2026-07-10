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
package net.sf.jasperreports.engine;

import java.awt.Color;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.base.JRBasePen;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * This interface is used to customize line settings such as width, style and color. 
 * This is useful for drawing graphic elements as well as drawing borders around text elements and images.
 * <h3>Line Width</h3>
 * The <code>lineWidth</code> attribute represents the width of the line measured in points. 
 * Can be accessed using the {@link #getLineWidth()} method.
 * <h3>Line Style</h3>
 * The <code>lineStyle</code> attribute represents the line style and has one of the following predefined values
 * (see {@link #getLineStyle()}): 
 * <ul>
 * <li><code>Solid</code></li>
 * <li><code>Dashed</code></li>
 * <li><code>Dotted</code></li>
 * <li><code>Double</code></li>
 * </ul>
 * <h3>Line Color</h3>
 * The <code>lineColor</code> attribute represents the color of the line. 
 * Can be accessed using the {@link #getLineColor()} method.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_lineWidth,
	JRXmlConstants.ATTRIBUTE_lineStyle,
	JRXmlConstants.ATTRIBUTE_lineColor
	})
@JsonDeserialize(as = JRBasePen.class)
public interface JRPen
{

	public static final Float LINE_WIDTH_0 = 0f;
	public static final Float LINE_WIDTH_1 = 1f;


	/**
	 *
	 */
	@JsonIgnore
	public JRPenContainer getPenContainer();

	/**
	 * 
	 */
	public JRPen clone(JRPenContainer penContainer);
	
	public void populateStyle();

	/**
	 * Gets the line width used for this pen.
	 * @return line width
	 */
	@JsonIgnore
	public Float getLineWidth();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_lineWidth)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_lineWidth, isAttribute = true)
	public Float getOwnLineWidth();

	/**
	 * Sets the line width.
	 * @param lineWidth the line width
	 */
	@JsonSetter
	public void setLineWidth(Float lineWidth);

	/**
	 * Indicates the line style used for this pen.
	 * @return a value representing one of the line style constants in {@link LineStyleEnum}
	 */
	@JsonIgnore
	public LineStyleEnum getLineStyle();
	
	/**
	 * Indicates the line style used for this pen.
	 * @return a value representing one of the line style constants in {@link LineStyleEnum}
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_lineStyle)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_lineStyle, isAttribute = true)
	public LineStyleEnum getOwnLineStyle();
	
	/**
	 * Specifies the line style.
	 * @param lineStyleEnum a value representing one of the line style constants in {@link LineStyleEnum}
	 */
	@JsonSetter
	public void setLineStyle(LineStyleEnum lineStyleEnum);
	
	/**
	 * Gets the line color.
	 */
	@JsonIgnore
	public Color getLineColor();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_lineColor)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_lineColor, isAttribute = true)
	public Color getOwnLineColor();

	/**
	 * Sets the line color.
	 */
	@JsonSetter
	public void setLineColor(Color color);

}
