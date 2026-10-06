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

import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Common interface of design and print text elements.
 * <p/>
 * There are two kinds of text elements in JasperReports: static texts and text fields. As
 * their names suggest, the first are text elements with fixed, static content, they do not
 * change during the report-filling process, and they are used especially for introducing
 * labels into the final document. Text fields, however, have an associated expression that is
 * evaluated at runtime to produce the text content that will be displayed. Both types of text
 * elements share some properties, and those are introduced using a &lt;textElement&gt; tag.
 * <h3>Rotating Text</h3>
 * The <code>rotation</code> attribute (see {@link #getRotation()}), available for text elements, 
 * allows changing the text direction by rotating it 90 degrees to the right or to the left, or by 
 * rotating it 180 degrees to be rendered upside down. Possible values are:
 * <ul>
 * <li><code>None</code> - this is the default value</li>
 * <li><code>Left</code></li>
 * <li><code>Right</code></li>
 * <li><code>UpsideDown</code></li>
 * </ul>
 * <h3>Text Markup</h3>
 * Normally, all the text content in a text element has the style specified by the text element
 * attributes (text fore color, text background color, font name, font size, etc.). But in some
 * cases, users will want to highlight a few words inside a text element, usually by changing
 * the text fore color, changing the font style using an underline, or by making it bold or
 * italic. In such cases, the text content of that particular text element will no longer be pure
 * text. It will be specially structured XML content that includes style information in the
 * text itself, or some other form of markup language.
 * <p/>
 * All text elements have an option attribute called <code>markup</code> (see {@link #getMarkup()}) 
 * which can be used to specified the type of markup language that will be used inside the text element, 
 * to format its content.
 * <p/>
 * The following are the predefined values possible for the <code>markup</code> attribute:
 * <ul>
 * <li><code>none</code> - There are no markup tags. The content of the text element is plain text.This is the default value.</li>
 * <li><code>styled</code> - The content of the text element is <i>styled text</i>, an proprietary XML type of markup text described below.</li>
 * <li><code>html</code> - The content of the text element is Hyper Text Markup Language.</li>
 * <li><code>rtf</code> - The content of the text element is Rich Text Format.</li>
 * </ul>
 * <h3>Styled Text</h3>
 * The JasperReports proprietary markup language is called styled text and is an XML
 * based format in which the style of any portion of text inside a text element can be
 * changed by embedding that portion inside a <code>&lt;style&gt;</code> tag or other simple HTML tag
 * from the following list: <code>&lt;b&gt;</code>, <code>&lt;u&gt;</code>, <code>&lt;i&gt;</code>, 
 * <code>&lt;font&gt;</code>, <code>&lt;sup&gt;</code>, <code>&lt;sub&gt;</code>, <code>&lt;li&gt;</code>, or <code>&lt;br&gt;</code>. As
 * already mentioned, for styled text elements, the content is considered XML, and the
 * engine tries to parse it to extract the style information at runtime. If the parsing fails for
 * any reason, including malformed XML tags, then the engine will simply render that
 * content as pure text, not styled text.
 * <p/>
 * The XML structure of styled text is very simple and consists only of embedded <code>&lt;style&gt;</code>
 * tags and simple HTML tags. Those tags can be nested on an unlimited number of levels
 * to override certain style settings for the embedded text.
 * <p/>
 * The <code>&lt;style&gt;</code> tag has various attributes for altering the color, font, or other style
 * properties of the text. From the standard HTML <code>&lt;font&gt;</code> tag, only the <code>fontFace</code>, 
 * <code>color</code> and <code>size</code> attributes are recognized by the JasperReports engine.
 * <p/>
 * All style attributes inside a <code>&lt;style&gt;</code> or <code>&lt;font&gt;</code> 
 * tag are optional because each individual style property is inherited from the overall 
 * text element or from the parent <code>&lt;style&gt;</code> tag when nested <code>&lt;style&gt;</code> 
 * tags are used. Special XML characters like &amp;, &lt;, &gt;, ", and ' must be XML-encoded when placed 
 * inside a text field.
 * <p/>
 * To see how the markup and styled text features work in JasperReports, check the
 * <code>/demo/samples/markup</code> sample provided with the project source files.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRCommonText extends JRCommonElement, JRBoxContainer, JRParagraphContainer
{
	/**
	 * 
	 */
	public static final String MARKUP_NONE = "none";
	public static final String MARKUP_STYLED_TEXT = "styled";
	public static final String MARKUP_HTML = "html";
	public static final String MARKUP_RTF = "rtf";

	/**
	 * Gets the text rotation.
	 * @return a value representing one of the text rotation constants in {@link RotationEnum}
	 */
	@JsonIgnore
	public RotationEnum getRotation();
	
	/**
	 * Gets the text own rotation.
	 * @return a value representing one of the text rotation constants in {@link RotationEnum}
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_rotation)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_rotation, isAttribute = true)
	public RotationEnum getOwnRotation();
	
	/**
	 * Sets the text rotation.
	 * @param rotationEnum a value representing one of the text rotation constants in {@link RotationEnum}
	 */
	@JsonSetter
	public void setRotation(RotationEnum rotationEnum);
	
	/**
	 * Returns the text markup.
	 */
	@JsonIgnore
	public String getMarkup();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_markup)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_markup, isAttribute = true)
	public String getOwnMarkup();
	
	@JsonSetter
	public void setMarkup(String markup);

	@JsonIgnore
	float getFontSize();
	
}
