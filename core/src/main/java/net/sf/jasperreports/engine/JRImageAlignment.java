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

import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * An interface that defines constants useful for image alignment.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRImageAlignment extends JRStyleContainer
{

	/**
	 * Gets the image horizontal alignment.
	 * @return a value representing one of the horizontal image alignment constants in {@link HorizontalImageAlignEnum}
	 */
	@JsonIgnore
	public HorizontalImageAlignEnum getHorizontalImageAlign();

	/*
	 * JACKSON-TIP
	 * When JacksonXmlProperty is used, it needs localName explicitly, because it does not use the name of the getter. 
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_hImageAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_hImageAlign, isAttribute = true)
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign();

	/**
	 * Sets the image horizontal alignment.
	 * @param horizontalAlignment a value representing one of the horizontal image alignment constants in {@link HorizontalImageAlignEnum}
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_hImageAlign)
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalAlignment);

	/**
	 * Gets the image vertical alignment.
	 * @return a value representing one of the vertical image alignment constants in {@link VerticalImageAlignEnum}
	 */
	@JsonIgnore
	public VerticalImageAlignEnum getVerticalImageAlign();
	
	@JsonGetter(JRXmlConstants.ATTRIBUTE_vImageAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_vImageAlign, isAttribute = true)
	public VerticalImageAlignEnum getOwnVerticalImageAlign();

	/**
	 * Gets the image vertical alignment.
	 * @param verticalAlignment a value representing one of the vertical image alignment constants in {@link VerticalImageAlignEnum}
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_vImageAlign)
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalAlignment);

}
