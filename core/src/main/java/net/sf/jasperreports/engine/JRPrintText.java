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
import net.sf.jasperreports.engine.fill.TextFormat;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRPrintText extends JRPrintElement, JRTextAlignment, JRPrintAnchor, JRPrintHyperlink, JRFont, JRCommonText, TextFormat
{
	/**
	 * Specifies if the first line in a paragraph should be indented.
	 */
	@Property(
		valueType = Boolean.class,
		defaultValue = PropertyConstants.BOOLEAN_TRUE,
		scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0
		)
	public static final String PROPERTY_AWT_INDENT_FIRST_LINE = JRPropertiesUtil.PROPERTY_PREFIX + "awt.indent.first.line";
	

	/**
	 * Specifies if the last line in a paragraph should be justified.
	 */
	@Property(
		valueType = Boolean.class,
		defaultValue = PropertyConstants.BOOLEAN_FALSE,
		scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0
		)
	public static final String PROPERTY_AWT_JUSTIFY_LAST_LINE = JRPropertiesUtil.PROPERTY_PREFIX + "awt.justify.last.line";
	

	/**
	 * Zero-length line break offset array used for {@link #getLineBreakOffsets()}
	 * when the text does not have any line breaks.
	 */
	public static final short[] ZERO_LINE_BREAK_OFFSETS = new short[0];

	
	/**
	 * Set the text for this object.
	 * 
	 * @param text the text
	 * @see #setTextTruncateIndex(Integer)
	 */
	public void setText(String text);

	/**
	 * 
	 */
	public Object getValue();
		
	/**
	 * 
	 */
	public void setValue(Object value);

	/**
	 * Returns the index to which this object's text is to be truncated.
	 * 
	 * This index is usually set at report fill time when the engine is instructed
	 * to keep the full text in the print text object.
	 * 
	 * @return the index to which this object's text is to be truncated
	 * @see JRTextElement#PROPERTY_PRINT_KEEP_FULL_TEXT
	 * @see #getTextTruncateSuffix()
	 */
	public Integer getTextTruncateIndex();

	/**
	 * Sets the index to which this object's text is to be truncated.
	 * 
	 * The text is truncated when {@link JRStyledTextUtil#getTruncatedText(JRPrintText)} or {@link #getFullStyledText(JRStyledTextAttributeSelector)}
	 * are called.
	 * 
	 * @param index the index to which this object's text is to be truncated
	 */
	public void setTextTruncateIndex(Integer index);
	
	/**
	 * Returns the suffix that is to be appended to the truncated text
	 * (as returned by {@link JRStyledTextUtil#getTruncatedText(JRPrintText)}.
	 * 
	 * @return the truncated text suffix
	 * @see JRTextElement#PROPERTY_TRUNCATE_SUFFIX
	 */
	public String getTextTruncateSuffix();
	
	/**
	 * Sets the suffix to be appended to the truncated text.
	 * 
	 * @param suffix the suffix to be appended to the truncated text
	 * @see #getTextTruncateSuffix()
	 */
	public void setTextTruncateSuffix(String suffix);
	
	/**
	 * Returns the full (not truncated) text of this object.
	 * 
	 * @return the full text of this object
	 * @see JRStyledTextUtil#getTruncatedText(JRPrintText)
	 * @see #getTextTruncateIndex()
	 */
	public String getFullText();
	
	/**
	 * Returns the original text that was set in this object.
	 * 
	 * @return the original text
	 */
	public String getOriginalText();
	
	/**
	 * Returns the full styled text of this object.
	 * 
	 * @param attributeSelector the styled text attribute selector
	 * @return the full styled text of this object
	 * @see #getFullText()
	 */
	public JRStyledText getFullStyledText(JRStyledTextAttributeSelector attributeSelector);
	
	/**
	 * 
	 */
	public void setTextFormat(TextFormat textFormat);
	
	/**
	 *
	 */
	public float getLineSpacingFactor();
		
	/**
	 *
	 */
	public void setLineSpacingFactor(float lineSpacingFactor);

	/**
	 *
	 */
	public float getLeadingOffset();
		
	/**
	 *
	 */
	public void setLeadingOffset(float leadingOffset);

	/**
	 * Gets the text run direction.
	 * @return a value representing one of the run direction constants in {@link RunDirectionEnum}
	 */
	public RunDirectionEnum getRunDirection();
	
	/**
	 * Sets the text run direction.
	 * @param runDirectionEnum a value representing one of the text run direction constants in {@link RunDirectionEnum}
	 */
	public void setRunDirection(RunDirectionEnum runDirectionEnum);
	
	/**
	 *
	 */
	public Float getAverageCharWidth();
		
	/**
	 *
	 */
	public void setAverageCharWidth(Float averageCharWidth);
		
	/**
	 *
	 */
	public float getTextHeight();
		
	/**
	 *
	 */
	public void setTextHeight(float textHeight);
		
	/**
	 * Returns the type of the value which was used to generate this text.
	 * <p>
	 * {@link JRTextField Text fields} that have a non-<code>String</code> expression
	 * save the value type using this attribute.  This information can be used by
	 * exporters to treat numerical or date texts (for instance) in a special manner.
	 * </p>
	 * 
	 * @return the type of the original value used to generate the text
	 */
	@Override
	public String getValueClassName();
	
	/**
	 * Returns the pattern used to format a value that was the source of this text. 
	 * <p>
	 * The pattern can be used to parse the text back to its source value.
	 * </p>
	 * 
	 * @return the pattern used to format this text's source value
	 * @see #getValueClassName()
	 */
	@Override
	public String getPattern();
	

	/**
	 * Returns the name of the class implementing the {@link net.sf.jasperreports.engine.util.FormatFactory FormatFactory}
	 * interface to use with this text element, in case it is not the same as the one for the overall document.
	 */
	@Override
	public String getFormatFactoryClass();
	
	
	/**
	 * Returns the code of the <code>java.util.Locale</code> which was used 
	 * while formatting the source value of the text.
	 * <p>
	 * The code is created using the {@link java.util.Locale#toString() java.util.Locale.toString()}
	 * convention.
	 * </p>
	 * <p>
	 * When this attribute is null, the locale returned by
	 * {@link JasperPrint#getLocaleCode() JasperPrint.getLocaleCode()} is used.
	 * This way, the locale is specified in a single place when all the (or many ) texts from a print object
	 * were formatted using the same locale.
	 * </p>
	 * 
	 * @return the code of the <code>java.util.Locale</code> used when formatting this text's source value
	 */
	@Override
	public String getLocaleCode();
	
	
	/**
	 * Returns the {@link java.util.TimeZone#getID() ID} of the <code>java.util.TimeZone</code>
	 * used to format this text's date source value.
	 * <p>
	 * When this attribute is null, the time zone returned by
	 * {@link JasperPrint#getTimeZoneId() JasperPrint.getTimeZoneId()} is used.
	 * </p>
	 * 
	 * @return the {@link java.util.TimeZone#getID() ID} of the <code>java.util.TimeZone</code>
	 * used to format this text's date source value
	 */
	@Override
	public String getTimeZoneId();

	
	/**
	 * Returns the line break offsets for the text if saved during report fill.
	 * 
	 * <p>
	 * The array of offsets is incremental, each offset adds to the
	 * previous value.  I.e. the positions at which line breaks occur
	 * are <code>offset[0]</code>, <code>offset[0] + offset[1]</code>,
	 * <code>offset[0] + offset[1] + offset[2]</code> and so on.
	 * 
	 * <p>
	 * This array does not include positions for explicit line breaks in the
	 * text, i.e. for line breaks that occur due to new line characters in
	 * the text.  The array only includes line break positions within a 
	 * paragraph in the text.
	 * 
	 * <p>
	 * If the text was meant to have saved line break offsets but no (non
	 * explicit) breaks were detected (e.g. if the text is a single line),
	 * this method will return a zero-length array.  If the text was not meant
	 * to include saved line breaks, the method will return <code>null</code>.
	 * 
	 * @return the line break offsets (possibly zero-length array), or
	 * <code>null</code> if no line break positions were saved during the fill
	 * 
	 * @see JRTextElement#PROPERTY_SAVE_LINE_BREAKS
	 */
	public short[] getLineBreakOffsets();

	
	/**
	 * Sets the line break offsets for the text.
	 * 
	 * @param lineBreakOffsets the line break offsets
	 * @see #getLineBreakOffsets()
	 */
	public void setLineBreakOffsets(short[] lineBreakOffsets);

}
