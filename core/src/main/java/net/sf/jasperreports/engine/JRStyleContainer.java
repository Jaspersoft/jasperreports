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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRStyleContainer
{

	/**
	 *
	 */
	@JsonIgnore
	public JRDefaultStyleProvider getDefaultStyleProvider();

	/**
	 * Returns this object's style. 
	 * 
	 * @return this object's style
	 */
	@JsonIgnore
	public JRStyle getStyle();

	/**
	 * Returns the name of an external style which is to be used by this object.
	 * <p/>
	 * External styles are defined in {@link JRTemplate templates} and are resolved
	 * at fill time.
	 * This attribute is only effective if no direct style (as returned by {@link #getStyle() getStyle()}
	 * is specified for this object.
	 * 
	 * @return the name of an external style
	 */
	@JsonIgnore
	public String getStyleNameReference();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_style)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_style, isAttribute = true)
	public default String getStyleName()
	{
		if (getStyle() != null)
		{
			return getStyle().getName();
		}
		
		return getStyleNameReference();
	}

}
