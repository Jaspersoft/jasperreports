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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.base.JRBaseParagraph;
import net.sf.jasperreports.engine.type.LineSpacingEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_lineSpacing,
	JRXmlConstants.ATTRIBUTE_lineSpacingSize,
	JRXmlConstants.ATTRIBUTE_firstLineIndent,
	JRXmlConstants.ATTRIBUTE_leftIndent,
	JRXmlConstants.ATTRIBUTE_rightIndent,
	JRXmlConstants.ATTRIBUTE_spacingBefore,
	JRXmlConstants.ATTRIBUTE_spacingAfter,
	JRXmlConstants.ATTRIBUTE_tabStopWidth,
	"tabStops"
	})
@JsonDeserialize(as = JRBaseParagraph.class)
public interface JRParagraph extends JRStyleContainer
{
	/**
	 * Specifies the default value for the text line spacing size, if no explicit information is provided in a 
	 * JR paragraph. This value is used in combination with the size-dependent line spacings in {@link LineSpacingEnum}: 
	 * <ul>
	 * <li>AT_LEAST</li>
	 * <li>FIXED</li>
	 * <li>PROPORTIONAL</li>
	 * </ul>
	 * 
	 * @see net.sf.jasperreports.engine.export.AbstractTextRenderer#getLineHeight(boolean, JRParagraph, float, float)
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "1",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Integer.class
			)
	public static final String DEFAULT_LINE_SPACING_SIZE = JRPropertiesUtil.PROPERTY_PREFIX + "default.line.spacing.size";
	
	/**
	 * Specifies the default value (in pixels) for the first line indent, if no explicit information is provided in a JR paragraph. 
	 * <br/>
	 * By default the first line in JR paragraphs has no indentation. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Integer.class
			)
	public static final String DEFAULT_FIRST_LINE_INDENT = JRPropertiesUtil.PROPERTY_PREFIX + "default.first.line.indent";
	
	/**
	 * Specifies the default value (in pixels) for the left indent, if no explicit information is provided in a JR paragraph. 
	 * <br/>
	 * By default JR paragraphs provide no left indentation. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Integer.class
			)
	public static final String DEFAULT_LEFT_INDENT = JRPropertiesUtil.PROPERTY_PREFIX + "default.left.indent";
	
	/**
	 * Specifies the default value (in pixels) for the right indent, if no explicit information is provided in a JR paragraph. 
	 * <br/>
	 * By default JR paragraphs provide no right indentation. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Integer.class
			)
	public static final String DEFAULT_RIGHT_INDENT = JRPropertiesUtil.PROPERTY_PREFIX + "default.right.indent";
	
	/**
	 * Specifies the default value (in pixels) for the spacing before lines, if no explicit information is provided in a JR paragraph. 
	 * <br/>
	 * By default JR paragraphs provide no spacing before lines. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Integer.class
			)
	public static final String DEFAULT_SPACING_BEFORE = JRPropertiesUtil.PROPERTY_PREFIX + "default.spacing.before";
	
	/**
	 * Specifies the default value (in pixels) for the spacing after lines, if no explicit information is provided in a JR paragraph. 
	 * <br/>
	 * By default JR paragraphs provide no spacing after lines. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Integer.class
			)
	public static final String DEFAULT_SPACING_AFTER = JRPropertiesUtil.PROPERTY_PREFIX + "default.spacing.after";
	
	/**
	 * Specifies the default value (in pixels) for the tab stop width, if no explicit information is provided in a paragraph. 
	 * <br/>
	 * By default JR paragraphs provide 40 pixels wide tab stops. 
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "40",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_0_2,
			valueType = Integer.class
			)
	public static final String DEFAULT_TAB_STOP_WIDTH = JRPropertiesUtil.PROPERTY_PREFIX + "default.tab.stop.width";

	/**
	 * Gets the text line spacing.
	 * @return a value representing one of the line spacing constants in {@link LineSpacingEnum}
	 */
	@JsonIgnore
	public JRParagraphContainer getParagraphContainer();

	/**
	 * 
	 */
	public JRParagraph clone(JRParagraphContainer paragraphContainer);

	public void populateStyle();

	/**
	 * Gets the text line spacing.
	 * @return a value representing one of the line spacing constants in {@link LineSpacingEnum}
	 */
	@JsonIgnore
	public LineSpacingEnum getLineSpacing();
	
	/**
	 * Gets the text own line spacing.
	 * @return a value representing one of the line spacing constants in {@link LineSpacingEnum}
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_lineSpacing)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_lineSpacing, isAttribute = true)
	public LineSpacingEnum getOwnLineSpacing();
	
	/**
	 * Sets the text line spacing.
	 * @param lineSpacing a value representing one of the line spacing constants in {@link LineSpacingEnum}
	 */
	@JsonSetter
	public void setLineSpacing(LineSpacingEnum lineSpacing);
	
	/**
	 * Gets the text line spacing size to be used in combination with the line spacing type.
	 */
	@JsonIgnore
	public Float getLineSpacingSize();
	
	/**
	 * Gets the text own line spacing size to be used in combination with the line spacing type.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_lineSpacingSize)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_lineSpacingSize, isAttribute = true)
	public Float getOwnLineSpacingSize();
	
	/**
	 * Sets the text line spacing size to be used in combination with the line spacing type.
	 */
	@JsonSetter
	public void setLineSpacingSize(Float lineSpacingSize);
	
	/**
	 * Gets the text left indent.
	 */
	@JsonIgnore
	public Integer getLeftIndent();
	
	/**
	 * Gets the text own left indent.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_leftIndent)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_leftIndent, isAttribute = true)
	public Integer getOwnLeftIndent();
	
	/**
	 * Sets the text own left indent.
	 */
	@JsonSetter
	public void setLeftIndent(Integer leftIndent);
	
	/**
	 * Gets the text first line indent.
	 */
	@JsonIgnore
	public Integer getFirstLineIndent();
	
	/**
	 * Gets the text own first line indent.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_firstLineIndent)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_firstLineIndent, isAttribute = true)
	public Integer getOwnFirstLineIndent();
	
	/**
	 * Sets the text own first line indent.
	 */
	@JsonSetter
	public void setFirstLineIndent(Integer firstLineIndent);
	
	/**
	 * Gets the text right indent.
	 */
	@JsonIgnore
	public Integer getRightIndent();
	
	/**
	 * Gets the text own right indent.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_rightIndent)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_rightIndent, isAttribute = true)
	public Integer getOwnRightIndent();
	
	/**
	 * Sets the text own right indent.
	 */
	@JsonSetter
	public void setRightIndent(Integer rightIndent);

	/**
	 * Gets the text spacing before.
	 */
	@JsonIgnore
	public Integer getSpacingBefore();
	
	/**
	 * Gets the text own spacing before.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_spacingBefore)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_spacingBefore, isAttribute = true)
	public Integer getOwnSpacingBefore();
	
	/**
	 * Sets the text own spacing before.
	 */
	@JsonSetter
	public void setSpacingBefore(Integer spacingBefore);
	
	/**
	 * Gets the text spacing after.
	 */
	@JsonIgnore
	public Integer getSpacingAfter();
	
	/**
	 * Gets the text own spacing after.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_spacingAfter)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_spacingAfter, isAttribute = true)
	public Integer getOwnSpacingAfter();
	
	/**
	 * Sets the text own spacing after.
	 */
	@JsonSetter
	public void setSpacingAfter(Integer spacingAfter);
	
	/**
	 * Gets the text tab stop width.
	 */
	@JsonIgnore
	public Integer getTabStopWidth();
	
	/**
	 * Gets the text own tab stop width.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_tabStopWidth)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_tabStopWidth, isAttribute = true)
	public Integer getOwnTabStopWidth();
	
	/**
	 * Sets the text own tab stop width.
	 */
	@JsonSetter
	public void setTabStopWidth(Integer tabStopWidth);

	/**
	 * Gets the custom tab stops.
	 */
	@JsonIgnore
	public TabStop[] getTabStops();

	/**
	 * Gets the custom tab stops.
	 */
	@JsonGetter("tabStops")
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_tabStop)
	@JacksonXmlElementWrapper(useWrapping = false)
	public TabStop[] getOwnTabStops();
	
	/**
	 *
	 */
	public void addTabStop(TabStop tabStop);
	
	/**
	 *
	 */
	public void addTabStop(int index, TabStop tabStop);

	/**
	 *
	 */
	public void removeTabStop(int index);

	/**
	 *
	 */
	public void removeTabStop(TabStop tabStop);

}
