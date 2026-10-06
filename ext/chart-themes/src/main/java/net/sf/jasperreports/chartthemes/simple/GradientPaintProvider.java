/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.chartthemes.simple;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.jackson.util.ColorDeserializer;
import net.sf.jasperreports.jackson.util.ColorSerializer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName("gradient-paint")
public class GradientPaintProvider implements PaintProvider
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	@JsonDeserialize(using = ColorDeserializer.class)
	@JsonSerialize(using = ColorSerializer.class)
	@JacksonXmlProperty(isAttribute = true)
	private Color color1;
	@JsonDeserialize(using = ColorDeserializer.class)
	@JsonSerialize(using = ColorSerializer.class)
	@JacksonXmlProperty(isAttribute = true)
	private Color color2;

	
	/**
	 *
	 */
	public GradientPaintProvider()
	{
	}
	
	
	/**
	 *
	 */
	public GradientPaintProvider(Color color1, Color color2)
	{
		this.color1 = color1;
		this.color2 = color2;
	}
	
	
	@Override
	public Paint getPaint()
	{
		return new GradientPaint(0, 0, color1, 0, 0, color2);
	}


	public Color getColor1() {
		return color1;
	}


	public void setColor1(Color color1) {
		this.color1 = color1;
	}


	public Color getColor2() {
		return color2;
	}


	public void setColor2(Color color2) {
		this.color2 = color2;
	}

}
