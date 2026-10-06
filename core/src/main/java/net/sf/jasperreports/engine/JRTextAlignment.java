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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * An interface that defines constants useful for text alignment.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRTextAlignment extends JRStyleContainer
{

	/**
	 * Gets the text horizontal alignment.
	 * @return a value representing one of the horizontal text alignment constants in {@link HorizontalTextAlignEnum}
	 */
	@JsonIgnore
	public HorizontalTextAlignEnum getHorizontalTextAlign();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_hTextAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_hTextAlign, isAttribute = true)
	public HorizontalTextAlignEnum getOwnHorizontalTextAlign();

	/**
	 * Sets the text horizontal alignment.
	 * @param horizontalAlignment a value representing one of the horizontal text alignment constants in {@link HorizontalTextAlignEnum}
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_hTextAlign)
	public void setHorizontalTextAlign(HorizontalTextAlignEnum horizontalAlignment);

	/**
	 * Gets the text vertical alignment.
	 * @return a value representing one of the vertical text alignment constants in {@link VerticalTextAlignEnum}
	 */
	@JsonIgnore
	public VerticalTextAlignEnum getVerticalTextAlign();
	
	@JsonGetter(JRXmlConstants.ATTRIBUTE_vTextAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_vTextAlign, isAttribute = true)
	public VerticalTextAlignEnum getOwnVerticalTextAlign();

	/**
	 * Gets the text vertical alignment.
	 * @param verticalAlignment a value representing one of the vertical text alignment constants in {@link VerticalTextAlignEnum}
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_vTextAlign)
	public void setVerticalTextAlign(VerticalTextAlignEnum verticalAlignment);

}
