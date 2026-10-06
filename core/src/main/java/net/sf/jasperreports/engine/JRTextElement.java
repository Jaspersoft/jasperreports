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

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.properties.PropertyConstants;



/**
 * An abstract representation of a report text element. It provides basic functionality for static texts and text fields.
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRTextElement extends JRElement, JRTextAlignment, JRFont, JRCommonText
{

	/**
	 * Property used to determine whether the fill process must preserve the original text
	 * for text elements that are not able to fit their entire contents.
	 * 
	 * <p>
	 * When this property is set, the engine saves the original text in the
	 * {@link JRPrintText print text object} along with the index at which the
	 * text is to be truncated by the print object.
	 * </p>
	 * 
	 * <p>
	 * This property can be set at the following levels (listed in the order of precedence):
	 * <ul>
	 * 	<li>at {@link JRTextElement text element} level</li>
	 * 	<li>at {@link JRReport report} level</li>
	 * 	<li>globally in jasperreports.properties or via {@link JRPropertiesUtil}</li>
	 * </ul> 
	 * </p>
	 * 
	 * @see JRPrintText#getFullText()
	 * @see JRStyledTextUtil#getTruncatedText(JRPrintText)
	 * @see JRPrintText#getTextTruncateIndex()
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_2_0_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_PRINT_KEEP_FULL_TEXT = JRPropertiesUtil.PROPERTY_PREFIX + "print.keep.full.text";
	
	/**
	 * Boolean property that determines whether text elements are to be truncated
	 * at the last character that fits.
	 * 
	 * <p>
	 * By default, when the entire text of a text element does not fit the element's area,
	 * the text is truncated at the last word that fits the area.
	 * This property can instruct the engine to truncate the text at the last character
	 * that fits.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at the same levels as {@link #PROPERTY_PRINT_KEEP_FULL_TEXT}.
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_2_0_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_TRUNCATE_AT_CHAR = JRPropertiesUtil.PROPERTY_PREFIX + "text.truncate.at.char";

	/**
	 * Property whose value is used as a suffix for the truncated text.
	 * 
	 * <p>
	 * The suffix is appended to the text when truncation occurs.
	 * If the property is not defined or empty (which is the case by default),
	 * no suffix will be used when the text is truncated.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at the same levels as {@link #PROPERTY_PRINT_KEEP_FULL_TEXT}.
	 * </p>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "empty string",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_2_0_3
			)
	public static final String PROPERTY_TRUNCATE_SUFFIX = JRPropertiesUtil.PROPERTY_PREFIX + "text.truncate.suffix";
	
	/**
	 * Boolean property that determines whether the positions where text line
	 * break occurs are to be saved during report fill in oder to be used at
	 * export time.
	 * 
	 * <p>
	 * At report fill time, each text element is measured in order to determine
	 * how long it needs to stretch or where it needs to be truncated.
	 * During this measurement, the text wraps at certain positions in order to
	 * fit the text element defined width.
	 * 
	 * <p>
	 * Setting this property to true instructs the engine to save the positions
	 * at which line breaks occur in the generated print element.
	 * The positions can be used by report exporters that want to enforce line
	 * breaks to occur at exactly the same position as they did during text
	 * measurement at fill time.
	 * 
	 * <p>
	 * Currently, the HTML exporter will make use of the saved line break
	 * positions by introducing explicit line breaks.
	 * 
	 * <p>
	 * The property can be set globally, at report level or at text element level.
	 * 
	 * @see JRPrintText#getLineBreakOffsets()
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_5_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_SAVE_LINE_BREAKS = JRPropertiesUtil.PROPERTY_PREFIX 
			+ "text.save.line.breaks";
	
}
