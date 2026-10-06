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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.type.FillEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.jackson.util.PenSerializer;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Provides a collection of style settings declared at the report level.
 * <h3>Report Styles</h3>
 * A report style is a collection of style settings declared at the report level. These settings
 * can be reused throughout the entire report template when setting the style properties of
 * report elements.
 * <p>
 * The <code>name</code> attribute of a <code>&lt;style&gt;</code> element is mandatory. It must be unique because it 
 * references the corresponding report style throughout the report.
 * </p><p>
 * One can use <code>isDefault="true"</code> for one of your report style declarations to mark the 
 * default for elements that do not or cannot have another style specified.
 * </p><p>
 * Each report style definition can reference another style definition from which it will
 * inherit some or all of its properties. The <code>style</code> attribute inside a <code>&lt;style&gt;</code> element 
 * specifies the name of the parent report style.
 * </p><p>
 * Below is a list with the main style attributes:</p>
 * <ul>
 * <li><code>name</code> - the name of the style. Required.</li>
 * <li><code>isDefault</code> - flag that specifies if the style represents the default report style.</li>
 * <li><code>style</code> - the name of the parent style</li>
 * <li><code>mode</code> - specifies whether report elements are opaque or transparent. Possible values are:
 * <ul>
 * <li><code>Opaque</code></li>
 * <li><code>Transparent</code></li>
 * </ul>
 * </li>
 * <li><code>forecolor</code> - the font color for text elements or the line color for graphic elements</li>
 * <li><code>backcolor</code> - the element background color</li>
 * <li><code>fill</code> - the fill pattern. Possible value is <code>Solid</code></li>
 * <li><code>radius</code> - the radius for some graphic elements</li>
 * <li><code>hTextAlign</code> - the horizontal alignment of the text within a text element. Possible values are:
 * <ul>
 * <li><code>Left</code> - default setting</li>
 * <li><code>Center</code></li>
 * <li><code>Right</code></li>
 * <li><code>Justified</code></li>
 * </ul>
 * </li>
 * <li><code>vTextAlign</code> - the vertical alignment of the text within a text element. Possible values are:
 * <ul>
 * <li><code>Top</code> - default setting</li>
 * <li><code>Middle</code></li>
 * <li><code>Bottom</code></li>
 * </ul>
 * </li>
 * <li><code>hImageAlign</code> - the horizontal alignment of the image within an image element. Possible values are:
 * <ul>
 * <li><code>Left</code> - default setting</li>
 * <li><code>Center</code></li>
 * <li><code>Right</code></li>
 * </ul>
 * </li>
 * <li><code>vImageAlign</code> - the vertical alignment of the image within an image element. Possible values are:
 * <ul>
 * <li><code>Top</code> - default setting</li>
 * <li><code>Middle</code></li>
 * <li><code>Bottom</code></li>
 * </ul>
 * </li>
 * <li><code>rotation</code> - the text rotation within a text element. Possible values are:
 * <ul>
 * <li><code>None</code> - default setting</li>
 * <li><code>Left</code></li>
 * <li><code>Right</code></li>
 * <li><code>UpsideDown</code></li>
 * </ul>
 * </li>
 * <li><code>fontName</code> - the name of the font face</li>
 * <li><code>fontSize</code> - the font size</li>
 * <li><code>isBold</code> - flag that specifies whether the font is bold</li>
 * <li><code>isItalic</code> - flag that specifies whether the font is italic</li>
 * <li><code>isUnderline</code> - flag that specifies whether the font is underlined</li>
 * <li><code>isStrikeThrough</code> - flag that specifies whether the font is strike through</li>
 * <li><code>pattern</code> - specifies the format pattern for text elements</li>
 * <li><code>isBlankWhenNull</code> - flag that specifies whether null values should be represented as blanks</li>
 * <li><code>markup</code> - the markup style for the text elements. Possible values are:
 * <ul>
 * <li><code>none</code> - default setting</li>
 * <li><code>styled</code></li>
 * <li><code>html</code></li>
 * <li><code>rtf</code></li>
 * </ul>
 * </li>
 * </ul>
 * <p>
 * A style also may contain:</p>
 * <ul>
 * <li>a {@link net.sf.jasperreports.engine.JRPen} element that can be retrieved 
 * using the {@link #getLinePen()} method.</li>
 * <li>a {@link net.sf.jasperreports.engine.JRLineBox} element that can be retrieved 
 * using the {@link #getLineBox()} method.</li>
 * <li>a {@link net.sf.jasperreports.engine.JRParagraph} element that can be retrieved 
 * using the {@link #getParagraph()} method inherited from the 
 *  {@link net.sf.jasperreports.engine.JRParagraphContainer} interface.</li>
 * </ul>
 * <p>
 * All report elements can reference a report style to inherit all or part of the style
 * properties. A report style declaration groups all the style-related properties supported
 * throughout the library, but an individual element inherits only those style properties that
 * apply to it. The others will be ignored.
 * </p>
 * <h3>Conditional Styles</h3>
 * Sometimes users need to change a report element style at runtime based on certain
 * conditions (for example, to alternate adjacent row colors in a report detail section). To
 * achieve this goal, one can set some style properties to be enabled only if a specified
 * condition is true. This is done using conditional styles.
 * </p><p>
 * A conditional style has two elements: a Boolean condition expression and a <code>style</code>. The
 * style is used only if the condition evaluates to true.
 * </p><p>
 * An important aspect is the priority of styles. When applied, a conditional style will
 * override the properties of its parent style.
 * </p><p>
 * A style can contain more than one conditional style. In this case, all conditionals that
 * evaluate to true will be appended to the existing style (the second style will be
 * appended to the first, and so on).
 * </p><p>
 * By default, the style condition expressions are evaluated during the report filling process
 * at the time that the style reference is used. The conditional expression evaluation will use
 * the current values of referenced variables and fields, regardless of the <code>evaluationTime</code>
 * attribute of the element that makes use of the style.
 * </p><p>
 * If the evaluation of the condition expression of the style needs to be delayed, just like the
 * value of the text field or the image element that uses the conditional style, the
 * {@link #PROPERTY_EVALUATION_TIME_ENABLED net.sf.jasperreports.style.evaluation.time.enabled} configuration property
 * should be set to true.
 * </p>
 * <h3>Style Templates</h3>
 * Report styles can also be defined in external style template files that are referenced by
 * report templates. This allows report designers to define in a single place a common look
 * for a set of reports.
 * </p><p>
 * A style template is an XML file that contains one or more style definitions. A template
 * can include references to other style template files, hence one can organize a style library
 * as a hierarchical set of style template files.
 * </p><p>
 * Style template files use by convention the <code>*.jrtx</code> extension, but this is not mandatory.
 * </p><p>
 * A report can use style templates by explicitly referring them in its definition. References
 * to a style templates are included in JRXML reports as <code>&lt;template&gt;</code> elements. Such an
 * element contains an expression that is resolved at fill time to a style template instance.
 * </p>
 * @see net.sf.jasperreports.engine.JRLineBox
 * @see net.sf.jasperreports.engine.JRParagraph
 * @see net.sf.jasperreports.engine.JRPen
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_name,
	"default",
	JRXmlConstants.ATTRIBUTE_style,
	JRXmlConstants.ATTRIBUTE_mode,
	JRXmlConstants.ATTRIBUTE_forecolor,
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_fill,
	JRXmlConstants.ATTRIBUTE_radius,
	JRXmlConstants.ATTRIBUTE_scaleImage,
	JRXmlConstants.ATTRIBUTE_hTextAlign,
	JRXmlConstants.ATTRIBUTE_vTextAlign,
	JRXmlConstants.ATTRIBUTE_hImageAlign,
	JRXmlConstants.ATTRIBUTE_vImageAlign,
	JRXmlConstants.ATTRIBUTE_rotation,
	JRXmlConstants.ATTRIBUTE_markup,
	JRXmlConstants.ATTRIBUTE_pattern,
	"blankWhenNull",
	JRXmlConstants.ATTRIBUTE_fontName,
	JRXmlConstants.ATTRIBUTE_fontSize,
	"bold",
	"italic",
	"underline",
	"strikeThrough",
	JRXmlConstants.ATTRIBUTE_pdfFontName,
	JRXmlConstants.ATTRIBUTE_pdfEncoding,
	"pdfEmbedded",
	JRXmlConstants.ELEMENT_pen,
	JRXmlConstants.ELEMENT_box,
	JRXmlConstants.ELEMENT_paragraph,
	"conditionalStyles",
	JRXmlConstants.ELEMENT_conditionalStyle
	})
@JsonDeserialize(as = JRDesignStyle.class)
public interface JRStyle extends JRBoxContainer, JRPenContainer, JRParagraphContainer, JRCloneable
{
	
	/**
	 * A flag the determines whether the style of an element is evaluated at
	 * the element evaluation time, or at the time the band on which the element
	 * is placed is rendered.
	 * 
	 * <p>
	 * This applies to report elements that can have delayed evaluations times
	 * (such as text fields and images).  When this flag is set to
	 * <code>true</code>, conditional style expressions of the style that is
	 * associated with the element are evaluated at the moment the element is
	 * set to evaluate, and the resulting style to the generated print element.
	 * 
	 * <p>
	 * By default, this flag is set to <code>false</code>.  The property can be
	 * set globally, at report level and at element level.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_5_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_EVALUATION_TIME_ENABLED = 
		JRPropertiesUtil.PROPERTY_PREFIX + "style.evaluation.time.enabled";
	
	/**
	 * Gets the style unique name.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();

	/**
	 * Gets a flag that specifies if this is the default report style.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isDefault();

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

	@JsonIgnore
	public Color getForecolor();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_forecolor)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_forecolor, isAttribute = true)
	public Color getOwnForecolor();

	@JsonIgnore
	public Color getBackcolor();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_backcolor)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_backcolor, isAttribute = true)
	public Color getOwnBackcolor();

	/**
	 * 
	 */
	@JsonGetter(JRXmlConstants.ELEMENT_pen)
	@JsonInclude(Include.NON_EMPTY)
	@JsonSerialize(using = PenSerializer.class)
	public JRPen getLinePen();

	/**
	 * Indicates the fill type used for this element.
	 * @return one of the fill constants in {@link FillEnum}.
	 */
	@JsonIgnore
	public FillEnum getFill();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_fill)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_fill, isAttribute = true)
	public FillEnum getOwnFill();

	/**
	 * Indicates the corner radius for rectangles with round corners. The default is 0.
	 */
	@JsonIgnore
	public Integer getRadius();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_radius)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_radius, isAttribute = true)
	public Integer getOwnRadius();

	/**
	 * Gets the image scale type.
	 * @return one of the scale types defined in {@link ScaleImageEnum}
	 */
	@JsonIgnore
	public ScaleImageEnum getScaleImage();

	/**
	 * Gets the image own scale type.
	 * @return one of the scale types defined in {@link ScaleImageEnum}
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_scaleImage)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_scaleImage, isAttribute = true)
	public ScaleImageEnum getOwnScaleImage();

	/**
	 * Gets the horizontal text alignment of the element.
	 * @return one of the alignment values defined in {@link HorizontalTextAlignEnum}
	 */
	@JsonIgnore
	public HorizontalTextAlignEnum getHorizontalTextAlign();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_hTextAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_hTextAlign, isAttribute = true)
	public HorizontalTextAlignEnum getOwnHorizontalTextAlign();

	/**
	 * Gets the vertical text alignment of the element.
	 * @return one of the alignment values defined in {@link VerticalTextAlignEnum}
	 */
	@JsonIgnore
	public VerticalTextAlignEnum getVerticalTextAlign();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_vTextAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_vTextAlign, isAttribute = true)
	public VerticalTextAlignEnum getOwnVerticalTextAlign();

	/**
	 * Gets the horizontal image alignment of the element.
	 * @return one of the alignment values defined in {@link HorizontalImageAlignEnum}
	 */
	@JsonIgnore
	public HorizontalImageAlignEnum getHorizontalImageAlign();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_hImageAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_hImageAlign, isAttribute = true)
	public HorizontalImageAlignEnum getOwnHorizontalImageAlign();

	/**
	 * Gets the vertical image alignment of the element.
	 * @return one of the alignment values defined in {@link VerticalImageAlignEnum}
	 */
	@JsonIgnore
	public VerticalImageAlignEnum getVerticalImageAlign();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_vImageAlign)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_vImageAlign, isAttribute = true)
	public VerticalImageAlignEnum getOwnVerticalImageAlign();

	/**
	 * Gets the text rotation.
	 * @return a value representing one of the rotation values in the {@link RotationEnum}.
	 */
	@JsonIgnore
	public RotationEnum getRotation();
	
	/**
	 * Gets the text own rotation.
	 * @return a value representing one of the rotation values in the {@link RotationEnum}.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_rotation)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_rotation, isAttribute = true)
	public RotationEnum getOwnRotation();
	
	/**
	 * Returns the markup language used to format the text.
	 */
	@JsonIgnore
	public String getMarkup();

	@JsonGetter(JRXmlConstants.ATTRIBUTE_markup)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_markup, isAttribute = true)
	public String getOwnMarkup();

	/**
	 *
	 */
	@JsonIgnore
	public String getFontName();

	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_fontName)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_fontName, isAttribute = true)
	public String getOwnFontName();

	/**
	 *
	 */
	@JsonIgnore
	public Boolean isBold();

	/**
	 *
	 */
	@JsonGetter("bold")
	@JacksonXmlProperty(localName = "bold", isAttribute = true)
	public Boolean isOwnBold();

	/**
	 *
	 */
	@JsonIgnore
	public Boolean isItalic();

	/**
	 *
	 */
	@JsonGetter("italic")
	@JacksonXmlProperty(localName = "italic", isAttribute = true)
	public Boolean isOwnItalic();

	/**
	 *
	 */
	@JsonIgnore
	public Boolean isUnderline();

	/**
	 *
	 */
	@JsonGetter("underline")
	@JacksonXmlProperty(localName = "underline", isAttribute = true)
	public Boolean isOwnUnderline();

	/**
	 *
	 */
	@JsonIgnore
	public Boolean isStrikeThrough();

	/**
	 *
	 */
	@JsonGetter("strikeThrough")
	@JacksonXmlProperty(localName = "strikeThrough", isAttribute = true)
	public Boolean isOwnStrikeThrough();

	/**
	 *
	 */
	@JsonIgnore
	public Float getFontSize();

	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_fontSize)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_fontSize, isAttribute = true)
	public Float getOwnFontSize();

	/**
	 *
	 */
	@JsonIgnore
	public String getPdfFontName();

	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_pdfFontName)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_pdfFontName, isAttribute = true)
	public String getOwnPdfFontName();

	/**
	 *
	 */
	@JsonIgnore
	public String getPdfEncoding();

	/**
	 *
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_pdfEncoding)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_pdfEncoding, isAttribute = true)
	public String getOwnPdfEncoding();

	/**
	 *
	 */
	@JsonIgnore
	public Boolean isPdfEmbedded();

	/**
	 *
	 */
	@JsonGetter("pdfEmbedded")
	@JacksonXmlProperty(localName = "pdfEmbedded", isAttribute = true)
	public Boolean isOwnPdfEmbedded();

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
	 *
	 */
	@JsonIgnore
	public Boolean isBlankWhenNull();

	/**
	 *
	 */
	@JsonGetter("blankWhenNull")
	@JacksonXmlProperty(localName = "blankWhenNull", isAttribute = true)
	public Boolean isOwnBlankWhenNull();

	/**
	 *
	 */
	@JsonSetter
	public void setForecolor(Color forecolor);

	/**
	 *
	 */
	@JsonSetter
	public void setBackcolor(Color backcolor);

	/**
	 *
	 */
	@JsonSetter
	public void setMode(ModeEnum mode);

	/**
	 * 
	 */
	@JsonSetter
	public void setFill(FillEnum fill);

	/**
	 *
	 */
	@JsonSetter
	public void setRadius(Integer radius);

	/**
	 *
	 */
	@JsonSetter
	public void setScaleImage(ScaleImageEnum scaleImage);

	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_hTextAlign)
	public void setHorizontalTextAlign(HorizontalTextAlignEnum horizontalAlignment);

	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_vTextAlign)
	public void setVerticalTextAlign(VerticalTextAlignEnum verticalAlignment);

	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_hImageAlign)
	public void setHorizontalImageAlign(HorizontalImageAlignEnum horizontalAlignment);

	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_vImageAlign)
	public void setVerticalImageAlign(VerticalImageAlignEnum verticalAlignment);

	@JsonSetter
	public void setRotation(RotationEnum rotation);

	/**
	 *
	 */
	@JsonSetter
	public void setFontName(String fontName);

	/**
	 *
	 */
	@JsonSetter
	public void setBold(Boolean bold);

	/**
	 *
	 */
	@JsonSetter
	public void setItalic(Boolean italic);

	/**
	 *
	 */
	@JsonSetter
	public void setPdfEmbedded(Boolean pdfEmbedded);

	/**
	 *
	 */
	@JsonSetter
	public void setStrikeThrough(Boolean strikeThrough);

	/**
	 *
	 */
	@JsonSetter
	public void setMarkup(String markup);

	/**
	 *
	 */
	@JsonSetter
	public void setUnderline(Boolean underline);

	/**
	 *
	 */
	@JsonSetter
	public void setPattern(String pattern);

	/**
	 *
	 */
	@JsonSetter
	public void setBlankWhenNull(Boolean isBlankWhenNull);

	/**
	 *
	 */
	@JsonSetter
	public void setPdfEncoding(String pdfEncoding);

	/**
	 *
	 */
	@JsonSetter
	public void setPdfFontName(String pdfFontName);

	/**
	 *
	 */
	@JsonSetter
	public void setFontSize(Float fontSize);

	/**
	 *
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_conditionalStyle)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRConditionalStyle[] getConditionalStyles();
}
