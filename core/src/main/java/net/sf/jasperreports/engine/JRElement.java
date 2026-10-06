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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.jackson.util.BooleanTrueAsEmptySerializer;
import net.sf.jasperreports.properties.PropertyConstants;


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
 * {@link JRBand#getElementByKey(String)} method available at band level. A key value must be
 * associated with the report element and it must be unique within the overall band for the
 * lookup to work.
 * <h3>Element Position</h3>
 * <dl>
 * <dt>Absolute position</dt>
 * <dd>The <code>x</code> and <code>y</code> attributes of any report element are mandatory and represent the x and y
 * coordinates, measured in pixels, that mark the absolute position of the top-left corner of
 * the specified element within its parent report section.</dd>
 * <dt>Relative position</dt>
 * <dd>Some report elements, such as text fields, have special properties that allow them to
 * stretch downward to acquire all the information they have to display. Their height is
 * calculated at runtime and may affect the neighboring elements in the same report section,
 * especially those placed immediately below them.
 * <br/>
 * The <code>positionType</code> attribute specifies the behavior that the report element will have if
 * the layout of the report section in which it is been placed is stretched.</dd>
 * </dl>
 * There are three possible values for the <code>positionType</code> attribute:
 * <ul>
 * <li><code>Float</code> - The element floats in its parent section if it is pushed downward
 * by other elements found above it. It tries to conserve the distance between it and
 * the neighboring elements placed immediately above it</li>
 * <li><code>FixRelativeToTop</code> - The current report element
 * simply ignores what happens to the other section elements and tries to conserve the
 * y offset measured from the top of its parent report section</li>
 * <li><code>FixRelativeToBottom</code> - If the height of the parent
 * report section is affected by elements that stretch, the current element tries to
 * conserve the original distance between its bottom margin and the bottom of the
 * band</li>
 * </ul>
 * A report element called e2 will float when another report element e1 stretches only if
 * these three conditions are met:
 * <ul>
 * <li>e2 has <code>positionType="Float"</code></li>
 * <li><code>e1.y + e1.height &lt;= e2.y</code></li>
 * <li><code>e1.width + e2.width &gt; max(e1.x + e1.width, e2.x + e2.width) - min(e1.x, e2.x)</code></li>
 * </ul>
 * The second and third conditions together imply that the element e2 must be placed
 * below the e1. By default, all elements have a fixed position relative to the top of the
 * band.
 * <h3>Skipping Element Display</h3>
 * The engine can decide at runtime if it really should display a report element when a
 * <code>&lt;printWhenExpression&gt;</code> is used, which is available for all types of report elements.
 * <p/>
 * If present, this report expression should return a <code>java.lang.Boolean</code> object or null. It
 * is evaluated every time the section containing the current element is generated, in order
 * to see whether this particular element should appear in the report or not. If the expression
 * returns null, it is equivalent to returning <code>java.lang.Boolean.FALSE</code>. If the
 * expression is missing, then the report element will get printed every time - that is, if
 * other settings do not intervene.
 * <h3>Reprinting Elements on Section Overflows</h3>
 * When generating a report section, the engine might be forced to start a new page or
 * column because the remaining space at the bottom of the current page or column is not
 * sufficient for all the section elements to fit in, probably because some elements have
 * stretched. In such cases, you might want to reprint some of the already displayed
 * elements on the new page or column to recreate the context in which the page/column
 * break occurred.
 * <p/>
 * To achieve this, set <code>isPrintWhenDetailOverflows="true"</code> for all report elements
 * you want to reappear on the next page or column.
 * <h3>Suppressing Repeated Values Display</h3>
 * First, let's see what exactly a "repeating value" is. It very much depends on the type of
 * the report element we are talking about. For textfield elements, this is very intuitive. 
 * For instance, in an ordinary phone book, one can see that
 * for some consecutive lines, the value of the Family Name column repeats itself. You might want 
 * to suppress the repeating Family Name values and print only their first occurrence. To 
 * do that, set the following for the text field that displays the family name:
 * <p/>
 * <code>isPrintRepeatedValues="false"</code>
 * <p/>
 * The static text elements behave in the same way. As you would expect, their value
 * always repeats and in fact never changes until the end of the report. This is why we call
 * them static texts. So, if you set <code>isPrintRepeatedValues="false"</code> for one of your
 * <code>&lt;staticText&gt;</code> elements, it is displayed only once, the first time, at the beginning of the
 * report, and never again.
 * <p/>
 * Now, what about graphic elements? An image is considered to be repeating itself if its
 * bytes are exactly the same from one occurrence to the next. This happens only if you
 * choose to cache your images using the <code>isUsingCache</code> attribute available in the 
 * <code>&lt;image&gt;</code> (see {@link net.sf.jasperreports.engine.JRImage}
 * element and if the corresponding <code>&lt;imageExpression&gt;</code> returns the same value from one
 * iteration to the next (the same file name, the same URL, etc.).
 * <p/>
 * Lines and rectangles always repeat themselves because they are static elements, just like
 * the static texts shown previously. So, when you suppress repeating values for a line or a
 * rectangle, it is displayed only once, at the beginning of the report, and then ignored until
 * the end of the report.
 * <p/>
 * <b>Note:</b> The <code>isPrintRepeatedValues</code> attribute works only if the corresponding
 * <code>&lt;printWhenExpression&gt;</code> is missing. If it is not missing, it will always dictate 
 * whether the element should be printed, regardless of the repeating values.
 * <p/>
 * If you decide to not display the repeating values for some of your report elements, you
 * can modify this behavior by indicating the exceptional occasions in which you might
 * want to have a particular value redisplayed during the report-generation process.
 * <p/>
 * When the repeating value spans multiple pages or columns, you can redisplay this
 * repeating value at least once for every page or column. If you set
 * <code>isPrintInFirstWholeBand="true"</code>, then the report element will reappear in the first
 * band of a new page or column that is not an overflow from a previous page or column.
 * Also, if the repeating value you have suppressed spans multiple groups, you can make it
 * reappear at the beginning of a certain report group if you specify the name of that
 * particular group in the <code>printWhenGroupChanges</code> attribute.
 * <h3>Removing Blank Space</h3>
 * When a report element is not displayed for some reason (for example,
 * <code>&lt;printWhenExpression&gt;</code> evaluates to <code>Boolean.FALSE</code>, or a repeated value is
 * suppressed), the area where the report element stood at design time will be left empty.
 * This blank space also appears if a text field displays only blank characters or an empty
 * text value. You can eliminate this unwanted blank space on the vertical axis only if
 * certain conditions are met.
 * <p/>
 * The blank space will be removed only if the empty text field doesn't share any vertical space with
 * other report elements that are printed, even if this empty textfield does not print. If this 
 * condition is met, then set <code>isRemoveLineWhenBlank= "true"</code> for the empty textfield.
 * <h3>Stretch Behavior</h3>
 * The <code>stretchType</code> attribute of a report element can be used to customize the stretch
 * behavior of the element when, on the same report section, there are text fields that stretch
 * themselves because their text content is too large for the original text field height. When
 * stretchable text fields are present on a report section, the height of the report section
 * itself is affected by the stretch.
 * <p/>
 * A report element can respond to the modification of the report section layout in three
 * ways:
 * <ul>
 * <li><code>NoStretch</code> - The report element preserves its original specified height.</li>
 * <li><code>RelativeToBandHeight</code> - The report element adapts its height
 * to match the new height of the report section it is placed on, which has been
 * affected by stretch.</li>
 * <li><code>RelativeToTallestObject</code> - Report elements can be made to automatically adapt their
 * height to fit the amount of stretch suffered by the tallest element in the group that
 * they are part of.</li>
 * </ul>
 * <h3>Custom Element Properties</h3>
 * Report elements can define arbitrary properties in the form of name/value pairs.
 * JasperReports itself recognizes and uses a set of such properties, and external code can
 * recognize further custom element properties.
 * <p/>
 * In report templates, element properties can be defined either as a static name/value pair,
 * or as a pair that has a static name and an expression that produces a dynamic value.
 * <p/>
 * Property value expressions are evaluated at the same moment at which the element itself
 * gets evaluated; hence for elements that have delayed evaluation, the dynamic properties
 * will be evaluated at the moment given by the delayed evaluation type. If the value
 * expression evaluates to null, no name/value pair will be set for the element.
 * <p/>
 * Properties that have dynamic values overwrite static properties: if the report has a static
 * property and a dynamic property with the same name, the dynamic property value will
 * override the static value, unless the value expression evaluates to null.
 * <p/>
 * Some custom element properties are used by the reporting engine at fill time, and others
 * are propagated to the generated elements in the filled report and used at export time.
 * The first category includes properties such as the ones used to customize text truncation.
 * Another example of element properties used at fill time
 * are custom chart properties that are recognized by a chart customizer.
 * <p/>
 * The second category includes properties that are defined for report design elements,
 * transferred to the print elements generated by the design elements and used when the
 * filled report gets exported. An example of such properties are the properties that specify
 * export filter criteria.
 * <p/>
 * The JasperReports exporters recognize a limited set of custom element properties, but
 * one can extend the built-in exporters to recognize and use further element properties.
 * This would allow users to introduce new export functionality, and to parameterize such
 * functionality per report element via custom properties.
 * <p/>
 * To determine which element properties need to be propagated into the filled report,
 * JasperReports uses a list of configurable property prefixes. Element properties that match
 * one of the configured property prefixes are copied into the print elements generated by
 * the element from the report template.
 * <p/>
 * The prefixes of properties to be propagated are configured via JasperReports global
 * properties of the form
 * {@link net.sf.jasperreports.engine.JasperPrint#PROPERTIES_PRINT_TRANSFER_PREFIX net.sf.jasperreports.print.transfer.&lt;arbitrary_suffix&gt;}. The values of
 * such properties are used as prefixes for properties that are to be transferred to the filled
 * report elements.
 * <p/>
 * The built-in JasperReports configuration defines a single such prefix:
 * <code>net.sf.jasperreports.export</code>. Consequently, all element properties that start with
 * this prefix will be propagated to the generated report elements by default.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRElement extends JRChild, JRCommonElement, JRPropertiesHolder, JRIdentifiable
{

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_20_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_ELEMENT_TEMPLATE_POPULATE_STYLE = JRPropertiesUtil.PROPERTY_PREFIX + "element.template.populate.style";

	/**
	 * Returns the position type for the element
	 * @return the position type
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public PositionTypeEnum getPositionType();

	/**
	 * Sets the position type for the element.
	 * @param positionType the position type
	 */
	public void setPositionType(PositionTypeEnum positionType);

	/**
	 * Indicates the stretch type for the element
	 * @return a value representing one of the stretch type constants in {@link StretchTypeEnum}
	 */
	@JacksonXmlProperty(isAttribute = true)
	public StretchTypeEnum getStretchType();
	
	/**
	 * Specifies how the engine should treat a missing image.
	 * @param stretchTypeEnum a value representing one of the stretch type constants in {@link StretchTypeEnum}
	 */
	public void setStretchType(StretchTypeEnum stretchTypeEnum);
	
	/**
	 * Specifies if the element value will be printed for every iteration, even if its value has not changed.
	 * @see JRElement#isRemoveLineWhenBlank()
	 * @see JRElement#isPrintInFirstWholeBand()
	 */
	@JsonSerialize(using = BooleanTrueAsEmptySerializer.class)
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isPrintRepeatedValues();
	
	/**
	 *
	 */
	public void setPrintRepeatedValues(boolean isPrintRepeatedValues);


	/**
	 * Gets the the section relative horizontal offset of the element top left corner.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getX();
	
	/**
	 * Sets the the section relative horizontal offset of the element top left corner.
	 */
	public void setX(int x);
	
	/**
	 * Gets the the section relative vertical offset of the element top left corner.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getY();
	
	/**
	 *
	 */
	public void setWidth(int width);
	
	/**
	 * Returns true if the remaining blank space appearing when the value is not printed will be removed. Under certain
	 * circumstances (the element has an empty string as its value or contains a repeated value that is supressed) the
	 * space reserved for the current element remains empty. If this method returns true, it means the engine will try
	 * to suppress the blank line, but will only succeed if no other elements occupy the same vertical space.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isRemoveLineWhenBlank();
	
	/**
	 * Specifies whether the remaining blank space appearing when the value is not printed will be removed. Under certain
	 * circumstances (the element has an empty string as its value or contains a repeated value that is supressed) the
	 * space reserved for the current element remains empty. If the parameter is set to true, it means the engine will try
	 * to suppress the blank line, but will only succeed if no other elements occupy the same vertical space.
	 */
	public void setRemoveLineWhenBlank(boolean isRemoveLineWhenBlank);
	
	/**
	 * Returns true if an element with a <i>printRepeatedValues</i> attribute set to true will be redisplayed for every
	 * new page or column that is not an overflow from a previous page or column.
	 * @see JRElement#isPrintRepeatedValues()
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isPrintInFirstWholeBand();
	
	/**
	 * Specifies whether an element with a <i>printRepeatedValues</i> attribute set to true should be redisplayed for every
	 * new page or column that is not an overflow from a previous page or column.
	 * @see JRElement#isPrintRepeatedValues()
	 */
	public void setPrintInFirstWholeBand(boolean isPrintInFirstWholeBand);
	
	/**
	 * If this is set to true, the element will be reprinted on the next page if the band does not fit in the current page.
	 * Actually if there is at least one element with this attribute, the band is redisplayed from the beginning, except
	 * those elements that fitted in the current page and have <i>isPrintWhenDetailOverflow</i> set to false.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isPrintWhenDetailOverflows();
	
	/**
	 * If this is set to true, the element will be reprinted on the next page if the band does not fit in the current page.
	 * Actually if there is at least one element with this attribute, the band is redisplayed from the beginning, except
	 * those elements that fitted in the current page and have <i>isPrintWhenDetailOverflow</i> set to false.
	 */
	public void setPrintWhenDetailOverflows(boolean isPrintWhenDetailOverflows);
	
	/**
	 * Gets the expression that is evaluated in order to decide if the element should be displayed. This
	 * expression always returns a boolean value.
	 */
	public JRExpression getPrintWhenExpression();
	
	/**
	 * Returns the group for which an element with a <i>printRepeatedValues</i> attribute set to true will be redisplayed
	 * even if the value has not changed.
	 * @see JRElement#isPrintRepeatedValues()
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getPrintWhenGroupChanges();
	
	/**
	 * Indicates the logical group that the element belongs to. More elements can be grouped in order to make some of them stretch
	 * relative to the height of their parent group.
	 * @see StretchTypeEnum
	 */
	@JsonIgnore
	public JRElementGroup getElementGroup();

	/**
	 * Gets the expression that is evaluated in order to obtain the runtime style name to be used for the element.
	 * When used, this expression takes precedence over the {@link #getStyle()} or {@link #getStyleNameReference()}.
	 */
	public JRExpression getStyleExpression();

	/**
	 *
	 */
	public void collectExpressions(JRExpressionCollector collector);


	/**
	 * Returns the list of dynamic/expression-based properties for this report element.
	 * 
	 * @return an array containing the expression-based properties of this report element
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_propertyExpression)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRPropertyExpression[] getPropertyExpressions();
	
	public JRElement clone(JRElementGroup parentGroup, int y);
}
