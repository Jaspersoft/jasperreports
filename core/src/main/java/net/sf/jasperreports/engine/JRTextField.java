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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.type.TextAdjustEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.properties.PropertyConstants;

//import java.text.Format;


/**
 * An abstract representation of a report text. It provides functionality for dynamic texts.
 * <p/>
 * Unlike static text elements, which do not change their text content, text fields have an
 * associated expression that is evaluated with every iteration in the data source to obtain
 * the text content to be displayed.
 * <h3>Variable-Height Text Fields</h3>
 * Because text fields have dynamic content, most of the time one can't anticipate the exact
 * amount of space to provide for them. If the space reserved for the text fields is not
 * sufficient, the text content is truncated so that it fits into the available area.
 * <p/>
 * This scenario is not always acceptable, so one can let the reporting engine calculate the
 * amount of space required to display the entire content of the text field at runtime, and
 * automatically adjust the size of the report element.
 * <p/>
 * To do this, set the <code>isStretchWithOverflow</code> flag to true for the particular text field elements
 * you are interested in. By doing this, you'll ensure that if the specified height for the text
 * field is not sufficient, it will automatically be increased (never decreased) in order to be
 * able to display the entire text content.
 * When text fields are affected by this stretch mechanism, the entire report section to
 * which they belong is also stretched.
 * <h3>Text Field Expression</h3>
 * A text field expression is introduced in the JRXML template by the <code>&lt;textFieldExpression&gt;</code> 
 * element (see {@link #getExpression()}) and can return values from various types, like the ones listed below:
 * <ul>
 * <li><code>java.lang.Boolean</code></li>
 * <li><code>java.lang.Byte</code></li>
 * <li><code>java.util.Date</code></li>
 * <li><code>java.sql.Timestamp</code></li>
 * <li><code>java.sql.Time</code></li>
 * <li><code>java.lang.Double</code></li>
 * <li><code>java.lang.Float</code></li>
 * <li><code>java.lang.Integer</code></li>
 * <li><code>java.lang.Long</code></li>
 * <li><code>java.lang.Short</code></li>
 * <li><code>java.math.BigDecimal</code></li>
 * <li><code>java.lang.Number</code></li>
 * <li><code>java.lang.String</code></li>
 * </ul>
 * <h3>Evaluating Text Fields</h3>
 * JasperReports provides a feature (the <code>evaluationTime</code> attribute 
 * inherited from {@link net.sf.jasperreports.engine.JREvaluation})
 * that lets you decide the exact moment you want the text field expression to be evaluated,
 * avoiding the default behavior in which the expression is evaluated immediately when the
 * current report section is generated.
 * <p/>
 * The <code>evaluationTime</code> attribute can have one of the following values 
 * (see {@link net.sf.jasperreports.engine.JREvaluation#getEvaluationTime()}):
 * <ul>
 * <li><code>Now</code> - The text field expression is evaluated when the current band is filled.</li>
 * <li><code>Report</code> - The text field expression is evaluated when the end of the report is reached.</li>
 * <li><code>Page</code> - The text field expression is evaluated when the end of the current page is reached</li>
 * <li><code>Column</code> - The text field expression is evaluated when the end of the current column is reached</li>
 * <li><code>Group</code> - The text field expression is evaluated when the group specified by the <code>evaluationGroup</code> 
 * attribute (see {@link #getEvaluationGroup()}) changes</li>
 * <li><code>Auto</code> - Each variable participating in the text field expression is evaluated at a time corresponding 
 * to its reset type. Fields are evaluated <code>Now</code>. This evaluation type should be used for expressions 
 * that combine values evaluated at different times, like the percentage out of a total</li>
 * </ul>
 * <p/>
 * Text fields with delayed evaluation do not stretch to acquire all the expression's content. This is
 * because the text element height is calculated when the report section is generated, and even if the engine comes
 * back later with the text content of the text field, the element height will not adapt, because this would ruin the
 * already created layout.
 * <p/>
 * Also, avoid using evaluation type <code>Auto</code> when other types suffice, as it can lead to performance loss.
 * <h3>Suppressing the Display of the Null Values</h3>
 * If the text field expression returns null, your text field will display the null text in the
 * generated document. A simple way to avoid this is to set the <code>isBlankWhenNull</code> attribute
 * (see {@link #isBlankWhenNull()}) to true. By doing this, the text field will cease to display null 
 * and will instead display an empty string. This way nothing will appear on your document if the text field value is
 * null.
 * <h3>Formatting Output</h3>
 * When dealing with numeric or date/time values, you can use the Java API to
 * format the output of the text field expressions. But there is a more convenient way to do
 * it: by using either the <code>pattern</code> attribute (see {@link #getPattern()}) or the 
 * <code>&lt;patternExpression&gt;</code> element (see {@link #getPatternExpression()}).
 * <p/>
 * The engine instantiates the <code>java.text.DecimalFormat</code> class if the text field
 * expression returns subclasses of the <code>java.lang.Number</code> class, or instantiates the
 * <code>java.text.SimpleDataFormat</code> if the text field expression returns <code>java.util.Date</code>,
 * <code>java.sql.Timestamp</code> or <code>java.sql.Time</code> objects.
 * For numeric fields, the value you should supply to this attribute is the same as if you
 * formatted the value using <code>java.text.DecimalFormat</code>.
 * For date/time fields, the value of this attribute has to be one of the following:
 * <ul>
 * <li>A style for the date part of the value and one for the time part, separated by a
 * comma, or one style for both the date part and the time part. A style is one of
 * <code>Short</code>, <code>Medium</code>, <code>Long</code>, <code>Full</code>, <code>Default</code> (corresponding to
 * <code>java.text.DateFormat</code> styles), or <code>Hide</code>. The formatter is constructed by calling
 * one of the <code>getDateTimeInstance()</code>, <code>getDateInstance()</code>, or
 * <code>getTimeInstance()</code> methods of <code>java.text.DateFormat</code> (depending on one of
 * the date/time parts being hidden) and supplying the date/time styles and report
 * locale.</li>
 * <li>A pattern that can be supplied to <code>java.text.SimpleDateFormat</code>. Note that in
 * this case the internationalization support is limited.</li>
 * </ul>
 * For more details about the syntax of this pattern attribute, check the Java API
 * documentation for the <code>java.text.DecimalFormat</code> and
 * <code>java.text.SimpleDateFormat</code> classes.
 * 
 * @see net.sf.jasperreports.engine.JREvaluation
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonTypeName("textField")
@JsonDeserialize(as = JRDesignTextField.class)
public interface JRTextField extends JRTextElement, JREvaluation, JRAnchor, JRHyperlink
{

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_5_0_0
			)
	public static final String PROPERTY_FORMAT_TIMEZONE = JRPropertiesUtil.PROPERTY_PREFIX + "pattern.timezone";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_1_1
			)
	public static final String PROPERTY_SQL_DATE_FORMAT_TIMEZONE = JRPropertiesUtil.PROPERTY_PREFIX + "sql.date.pattern.timezone";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_1_1
			)
	public static final String PROPERTY_SQL_TIMESTAMP_FORMAT_TIMEZONE = JRPropertiesUtil.PROPERTY_PREFIX + "sql.timestamp.pattern.timezone";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_6_1_1
			)
	public static final String PROPERTY_SQL_TIME_FORMAT_TIMEZONE = JRPropertiesUtil.PROPERTY_PREFIX + "sql.time.pattern.timezone";

	public static final String FORMAT_TIMEZONE_SYSTEM = "System";

	/**
	 * Provides a default pattern to be used for <code>java.sql.Date</code> values.
	 * 
	 * <p>
	 * Locale specific values can be configured by appending _&lt;locale code&gt; to the property name.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at global/JasperReports context level.
	 * </p>
	 * @since 6.0.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_0_0
			)
	public static final String PROPERTY_PATTERN_DATE = JRPropertiesUtil.PROPERTY_PREFIX + "text.pattern.date";

	/**
	 * Provides a default pattern to be used for <code>java.sql.Time</code> values.
	 * 
	 * <p>
	 * Locale specific values can be configured by appending _&lt;locale code&gt; to the property name.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at global/JasperReports context level.
	 * </p>
	 * @since 6.0.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_0_0
			)
	public static final String PROPERTY_PATTERN_TIME = JRPropertiesUtil.PROPERTY_PREFIX + "text.pattern.time";

	/**
	 * Provides a default pattern to be used for <code>java.util.Date</code> values other than
	 * <code>java.sql.Date</code> and <code>java.sql.Time</code>.
	 * 
	 * <p>
	 * Locale specific values can be configured by appending _&lt;locale code&gt; to the property name.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at global/JasperReports context level.
	 * </p>
	 * @since 6.0.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_0_0
			)
	public static final String PROPERTY_PATTERN_DATETIME = JRPropertiesUtil.PROPERTY_PREFIX + "text.pattern.datetime";

	/**
	 * Provides a default pattern to be used for numerical values that are known to be integer,
	 * i.e. integer primitive wrapper types and <code>java.math.BigInteger</code>.
	 * 
	 * <p>
	 * Locale specific values can be configured by appending _&lt;locale code&gt; to the property name.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at global/JasperReports context level.
	 * </p>
	 * @since 6.0.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_0_0
			)
	public static final String PROPERTY_PATTERN_INTEGER = JRPropertiesUtil.PROPERTY_PREFIX + "text.pattern.integer";

	/**
	 * Provides a default pattern to be used for numerical values other than the integer types.
	 * 
	 * <p>
	 * Locale specific values can be configured by appending _&lt;locale code&gt; to the property name.
	 * </p>
	 * 
	 * <p>
	 * The property can be set at global/JasperReports context level.
	 * </p>
	 * @since 6.0.0
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_0_0
			)
	public static final String PROPERTY_PATTERN_NUMBER = JRPropertiesUtil.PROPERTY_PREFIX + "text.pattern.number";
	
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "false",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_7_0_4,
			valueType = Boolean.class
			)
	String PROPERTY_EMPTY_OVERFLOW_ON_STRETCH = 
			JRPropertiesUtil.PROPERTY_PREFIX + "text.empty.overflow.on.stretch";
		
	/**
	 * Gets the text adjust type.
	 * @return a value representing one of the text adjust constants in {@link TextAdjustEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public TextAdjustEnum getTextAdjust();

	/**
	 * Sets the text adjust type.
	 * @param textAdjust a value representing one of the text adjust type constants in {@link TextAdjustEnum}
	 */
	public void setTextAdjust(TextAdjustEnum textAdjust);
		
	/**
	 * Gets the pattern used for this text field. The pattern will be used in a <tt>SimpleDateFormat</tt> for dates
	 * and a <tt>DecimalFormat</tt> for numeric text fields. The pattern format must follow one of these two classes
	 * formatting rules, as specified in the JDK API docs.
	 * @return a string containing the pattern.
	 */
	@JsonIgnore
	public String getPattern();
		
	@JsonGetter(JRXmlConstants.ATTRIBUTE_pattern)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_pattern, isAttribute = true)
	public String getOwnPattern();

	/**
	 * Sets the pattern used for this text field. The pattern will be used in a <tt>SimpleDateFormat</tt> for dates
	 * and a <tt>DecimalFormat</tt> for numeric text fields. The pattern format must follow one of these two classes
	 * formatting rules, as specified in the JDK API docs. If the pattern is incorrect, the exception thrown by formatter
	 * classes will be rethrown by the JasperReports fill engine.
	 */
	@JsonSetter
	public void setPattern(String pattern);
		
	/**
	 * Indicates whether an empty string will be displayed if the field's expression evaluates to <code>null</code>.
	 * @return true if an empty string will be displayed instead of null values, false otherwise
	 */
	@JsonIgnore
	public boolean isBlankWhenNull();

	@JsonGetter("blankWhenNull")
	@JacksonXmlProperty(localName = "blankWhenNull", isAttribute = true)
	public Boolean isOwnBlankWhenNull();

	/**
	 * Specifies whether an empty string should be displayed if the field's expression evaluates to <code>null</code>.
	 * @param isBlank true if an empty string will be displayed instead of null values, false otherwise
	 */
	@JsonSetter
	public void setBlankWhenNull(Boolean isBlank);

	/**
	 * Gets the expression for this field. The result obtained after evaluating this expression will be dispayed as
	 * the field text.
	 */
	public JRExpression getExpression();
		
	/**
	 * Gets the pattern expression, in case the patter needs to be dynamic.
	 * @see #getPattern()
	 */
	public JRExpression getPatternExpression();

}
