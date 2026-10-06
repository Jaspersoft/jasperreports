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

import java.awt.Color;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * An abstract representation of a report element. All report elements implement this interface. The interface contains
 * constants and methods that apply to all report elements.
 * <p/>
 * The properties that are common to all types of report elements are grouped in the
 * <code>&lt;reportElement&gt;</code> tag, which appears in the declaration of all report elements.
 * <h3>Element Key</h3>
 * Unlike variables and parameters, report elements are not required to have a name,
 * because normally you do not need to obtain any individual element inside a report
 * template. However, in some cases it is useful to be able to locate an element to alter one
 * of its properties before using the report template.
 * <p/>
 * This could be the case in an application for which the color of some elements in the
 * report template needs to change based on user input. To locate the report elements that
 * need to have their colors altered, the caller program could use the
 * {@link JRBand#getElementByKey(String) getElementByKey(String)} method available at band level. A key value must be
 * associated with the report element and it must be unique within the overall band for the
 * lookup to work.
 * <h3>Element Size</h3>
 * The <code>width</code> and <code>height</code> attributes are mandatory and represent the size of the report
 * element measured in pixels. Other element stretching settings may instruct the reporting
 * engine to ignore the specified element height. Even in this case, the attributes remain
 * mandatory since even when the height is calculated dynamically, the element will not be
 * smaller than the originally specified height.
 * <h3>Element Transparency</h3>
 * Report elements can either be transparent or opaque, depending on the value specified
 * for the <code>mode</code> attribute. The default value for this attribute depends on the type of the
 * report element. Graphic elements like rectangles and lines are opaque by default, while
 * images are transparent. Both static texts and text fields are transparent by default, and so
 * are the subreport elements.
 * <h3>Element Color</h3>
 * Two attributes represent colors: <code>forecolor</code> and <code>backcolor</code>. The 
 * <i>fore color</i> is for the
 * text of the text elements and the border of the graphic elements. The <i>background color</i>
 * fills the background of the specified report element, if it is not transparent.
 * <p/>
 * One can also use the decimal or hexadecimal representation for the desired color. The
 * preferred way to specify colors in JRXML is using the hexadecimal representation,
 * because it lets people control the level for each base color of the RGB system. For example,
 * one can display a text field in red by setting its <code>forecolor</code> attribute as follows:
 * <p/>
 * <code>forecolor="#FF0000"</code>
 * <p/>
 * The equivalent using the decimal representation would be the following:
 * <p/>
 * <code>forecolor="16711680"</code>
 * <p/>
 * The default fore color is <code>black</code> and the default background color is <code>white</code>.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRCommonElement extends JRStyleContainer
{

	@JacksonXmlProperty(isAttribute = true)
	public int getWidth();
	
	@JacksonXmlProperty(isAttribute = true)
	public int getHeight();
	
	/**
	 * Returns the string value that uniquely identifies the element.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getKey();

	/**
	 * Returns the element transparency mode.
	 * The default value depends on the type of the report element. Graphic elements like rectangles and lines are
	 * opaque by default, but the images are transparent. Both static texts and text fields are transparent
	 * by default, and so are the subreport elements.
	 */
	@JsonIgnore
	public ModeEnum getMode();
	
	@JsonGetter(JRXmlConstants.ATTRIBUTE_mode)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_mode, isAttribute = true)
	public ModeEnum getOwnMode();

	/**
	 * Sets the element transparency mode.
	 */
	@JsonSetter
	public void setMode(ModeEnum mode);
	
	/**
	 *
	 */
	@JsonIgnore
	public Color getForecolor();
	
	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_forecolor)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_forecolor, isAttribute = true)
	public Color getOwnForecolor();

	
	/**
	 *
	 */
	@JsonSetter
	public void setForecolor(Color forecolor);
	
	/**
	 *
	 */
	@JsonIgnore
	public Color getBackcolor();
	
	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_backcolor)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_backcolor, isAttribute = true)
	public Color getOwnBackcolor();
	
	/**
	 *
	 */
	@JsonSetter
	public void setBackcolor(Color backcolor);

}
