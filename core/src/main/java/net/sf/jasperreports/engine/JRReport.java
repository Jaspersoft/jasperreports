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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.jackson.util.ParameterSerializer;
import net.sf.jasperreports.jackson.util.SectionSerializer;
import net.sf.jasperreports.jackson.util.VariableSerializer;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * An abstract representation of a Jasper report. This interface is inherited by all report implementations
 * (designs, compiled reports, filled reports). It only contains constants and getters and setters for the most common
 * report properties and elements. Below are listed these properties:
 * <p>
 * <h3>Report Name</h3>
 * Every report design needs a name. Its name is important because the library uses it when
 * generating files, especially when the default behavior is preferred for compiling, filling,
 * or exporting the report.
 * <p>
 * The name of the report is specified using the <code>name</code> attribute, and its inclusion 
 * is mandatory. Spaces are not allowed in the report name - it must be a single word.
 * </p>
 * <h3>Language</h3>
 * Report expressions are usually written using the Java language. However, one can use
 * other languages as long as a report compiler is available to help evaluate these
 * expressions at report-filling time.
 * <p>
 * The default value for the <code>language</code> property is <code>java</code>, meaning that the Java language is
 * used for writing expressions, and that a report compiler capable of generating and
 * compiling a Java class on the fly is used for producing the bytecode needed for
 * expression evaluation at runtime.
 * </p><p>
 * Report compilers reference this property to see whether they can compile the supplied
 * report template or whether a different report compiler should be used, depending on the
 * actual scripting language.
 * </p>
 * <h3>Column Count</h3>
 * JasperReports lets users create reports with more than one column on each page.
 * <p>
 * Multicolumn report templates also have an associated column-filling order specified by
 * the <code>printOrder</code> attribute.
 * </p><p>
 * By default, the reporting engine creates reports with one column on each page.
 * </p>
 * <h3>Print Order</h3>
 * For reports having more that one column, it is important to specify the order in which the 
 * columns will be filled. One can do this using the printOrder attribute with 2 possible values:
 * <ul>
 * <li><code>Vertical</code> - columns are filled from top to bottom and then left to right. This is the default print order.</li>
 * <li><code>Horizontal</code> - columns are filled from left to right and then top to bottom</li>
 * </ul>
 * <h3>Page Size</h3>
 * There are two attributes at this level to specify the page size of the document that will be
 * generated: <code>pageWidth</code> and <code>pageHeight</code>. Like all the other JasperReports attributes that
 * represent element dimensions and position, these are specified in pixels. JasperReports
 * uses the default Java resolution of 72 dots per inch (DPI). This means that
 * <code>pageWidth="595"</code> will be about 8.26 inches, which is roughly the width of an A4 sheet
 * of paper.
 * <p>
 * The default page size corresponds to an A4 sheet of paper:
 * </p><p>
 * <code>pageWith="595" pageHeight="842"</code>
 * </p>
 * <h3>Page Orientation</h3>
 * The <code>orientation</code> attribute determines whether the documents use the 
 * <code>Portrait</code> or the <code>Landscape</code> format.
 * <p>
 * This <code>orientation</code> attribute is useful only at report-printing time to inform the printer 
 * about the page orientation, and in some special exporters. The default page orientation 
 * is <code>Portrait</code>.
 * </p>
 * <h3>Page Margins</h3>
 * Once the page size is decided, one can specify what margins the reporting engine should
 * preserve when generating the reports. Four attributes control this: <code>topMargin</code>,
 * <code>leftMargin</code>, <code>bottomMargin</code>, and <code>rightMargin</code>.
 * <p>
 * The default margin for the top and bottom of the page is 20 pixels. The default margin 
 * for the right and left margins is 30 pixels.
 * </p>
 * <h3>Column Size and Spacing</h3>
 * Reports may have more that one column, as specified in the
 * <code>columnCount</code> attribute. However, the reporting engine has to know how large a column
 * can be and how much space should be allowed between columns. Two attributes control
 * this: <code>columnWidth</code> and <code>columnSpacing</code>.
 * <p>
 * Also, when a report is compiled JasperReports checks whether the width of the overall
 * columns and the space between them exceed the specified page width and page margins.
 * </p><p>
 * Since there is only one column by default, the default column spacing is 0 pixels and the
 * default column width is 555 pixels (the default page width minus the default left and
 * right margins).
 * </p>
 * <h3>Empty Data Source Behavior</h3>
 * The data source for a report might not contain any records. In this case, it is not clear
 * what the output should be. Some people may expect to see a blank document and others may
 * want some of the report sections to be displayed anyway.
 * <p>
 * The <code>whenNoDataType</code> attribute lets users decide how the generated document should look
 * when there is no data in the data source supplied to it. Possible values of this attribute are as follows:</p>
 * <ul>
 * <li><code>NoPages</code> - This is the default setting. The generated document will have no pages in it. 
 * Viewers might throw an error when trying to load such documents.</li>
 * <li><code>BlankPage</code> - the generated document will contain a single blank page</li>
 * <li><code>AllSectionsNoDetail</code> - All the report sections except the <code>detail</code> 
 * section will appear in the generated document.</li>
 * <li><code>NoData</code> - the generated document will contain only the <code>NoData</code> section</li>
 * </ul>
 * <h3>Title and Summary Section Placement</h3>
 * To display the title or/and summary section on a separate page, set one or both of the
 * following attributes to true:
 * <ul>
 * <li><code>isTitleNewPage</code></li>
 * <li><code>isSummaryNewPage</code></li>
 * </ul>
 * Both of these Boolean attributes are set to false by default.
 * <p>
 * When the summary section stretches to a new page, or starts on a new page altogether, it
 * is not accompanied by the page header and page footer. In order to force the page header
 * and page footer to reappear on summary trailing pages, set the
 * <code>isSummaryWithPageHeaderAndFooter</code> to true; it is set to false by default.
 * </p>
 * <h3>Column Footer Placement</h3>
 * The <code>isFloatColumnFooter</code> Boolean property lets users customize the behavior of the
 * column footer section. By default, this section is rendered at the bottom of the page, just
 * above the page footer. In certain cases, it is useful to render it higher on the page, just
 * below the last detail or group footer on that particular column. To do this, set the
 * <code>isFloatColumnFooter</code> property to true.
 * <h3>Scriptlet Class</h3>
 * There can be multiple scriptlet instances associated with a report. The <code>scriptletClass</code> 
 * attribute is just a convenient way to specify the scriptlet class in case there is
 * only one scriptlet associated with the report.
 * <p>
 * This attribute is used by the engine only if no value is supplied for the built-in
 * {@link net.sf.jasperreports.engine.JRParameter#REPORT_SCRIPTLET REPORT_SCRIPTLET} parameter. 
 * If neither the attribute nor the parameter
 * is used, and no other scriptlet is specified for the report using named scriptlet tags the
 * reporting engine uses a single
 * {@link net.sf.jasperreports.engine.JRDefaultScriptlet} instance as the report
 * scriptlet.
 * </p>
 * <h3>Resource Bundle</h3>
 * To generate reports in different languages from the same report template, associate a
 * resource bundle with the template and make sure that the locale-specific resources inside
 * report expressions are retrieved based on the <code>$R{}</code> syntax.
 * <p>
 * There are two ways to associate the <code>java.util.ResourceBundle</code> object with the
 * report template.
 * </p><p>
 * The first is a static association made by setting the <code>resourceBundle</code> property of the
 * report template object to the base name of the target resource bundle.
 * </p><p>
 * A dynamic association can be made by supplying a <code>java.util.ResourceBundle</code>
 * object as the value for the 
 * {@link net.sf.jasperreports.engine.JRParameter#REPORT_RESOURCE_BUNDLE REPORT_RESOURCE_BUNDLE} 
 * parameter at report-filling time.
 * </p>
 * <h3>Missing Resources Behavior</h3>
 * The <code>whenResourceMissingType</code> property allows users to choose the desired behavior
 * of the engine when it deals with missing locale-specific resources in the supplied
 * resource bundle. There are four different values that can be used to deal with missing
 * resources:
 * <ul>
 * <li><code>Null</code> - The null value is used</li>
 * <li><code>Empty</code> - An empty string is used</li>
 * <li><code>Key</code> - The key is used</li>
 * <li><code>Error</code> - An exception is raised in case a locale-specific resource is not found in the 
 * supplied resource bundle for the given key and locale</li>
 * </ul>
 * <h3>Pagination</h3>
 * When the <code>isIgnorePagination</code> property is set to true, the report-filling engine will
 * completely ignore page break-related settings inside the report template and generate the
 * document on a single, very long page. The value of this property can be overridden at
 * runtime using the optional, built-in 
 * {@link net.sf.jasperreports.engine.JRParameter#IS_IGNORE_PAGINATION IS_IGNORE_PAGINATION} parameter.
 * <h3>Formatting Numbers, Dates and Times</h3>
 * The <code>formatFactoryClass</code> attribute lets users specify the name of the factory class
 * implementing the {@link net.sf.jasperreports.engine.util.FormatFactory} interface,
 * which should be instantiated by the engine in order to produce <code>java.text.DateFormat</code>
 * and <code>java.text.NumberFormat</code> objects to use for date and number formatting in the
 * current report.
 * <p>
 * This attribute specifying the factory class name is used only if no value is supplied for
 * the built-in {@link net.sf.jasperreports.engine.JRParameter#REPORT_FORMAT_FACTORY REPORT_FORMAT_FACTORY} parameter.
 * </p><p>
 * If neither of the attribute nor the parameter is used, the engine will eventually instantiate
 * the {@link net.sf.jasperreports.engine.util.DefaultFormatFactory} implementation
 * of the factory interface, which produces <code>java.text.SimpleDateFormat</code> and
 * <code>java.text.DecimalFormat</code> objects for date and number formatting.
 * This attribute or the built-in {@link net.sf.jasperreports.engine.JRParameter#REPORT_FORMAT_FACTORY REPORT_FORMAT_FACTORY}
 * parameter should be used only if the report relies on custom date and number formatters.
 * </p>
 * <h3>Custom Properties</h3>
 * Sometimes it is useful to put some information into the report template itself. This
 * information can be used by the parent application at runtime after loading the report
 * template, or it can be used by the UI report-design tools to store designer-specific
 * information, like whether to display the rules, the size of the snap grid, and so on.
 * <p>
 * Some of the properties can be transferred onto the generated document and can be used
 * by exporter to tailor their behavior.
 * </p><p>
 * The report templates can store application or user-defined properties in the form of
 * named values that can be archived by using any number or <code>&lt;property&gt;</code> tags inside the
 * report template.
 * </p><p>
 * It is recommended that property names rely on some namespace policy, just as Java
 * application packages do, to ensure that no naming conflict arises when several
 * applications store information in the same report template.
 * </p><p>
 * Here is how a named value can be put inside the report template:</p>
 * <pre>
 *   &lt;property name="com.mycompany.report.author" value="John Smith"/&gt;
 *   &lt;property name="com.mycompany.report.description" value="Displays sales data"/&gt;</pre>
 * At runtime, this application-defined data can be retrieved from the report template using
 * the public method {@link net.sf.jasperreports.engine.JRPropertiesHolder#getPropertiesMap() getPropertiesMap()} 
 * inherited from the {@link net.sf.jasperreports.engine.JRPropertiesHolder} interface.
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
 * All report elements can reference a report style to inherit all or part of the style
 * properties. A report style declaration groups all the style-related properties supported
 * throughout the library, but an individual element inherits only those style properties that
 * apply to it. The others will be ignored.
 * </p><p>
 * Sometimes users need to change a report element style at runtime based on certain
 * conditions (for example, to alternate adjacent row colors in a report detail section). To
 * achieve this goal, one can set some style properties to be enabled only if a specified
 * condition is true. This is done using conditional styles.
 * </p><p>
 * The default style of a report can be accessed using the 
 * {@link net.sf.jasperreports.engine.JRDefaultStyleProvider#getDefaultStyle() getDefaultStyle()} method inherited from the 
 * {@link net.sf.jasperreports.engine.JRDefaultStyleProvider} interface.
 * </p>
 * @see net.sf.jasperreports.engine.JRParameter#IS_IGNORE_PAGINATION
 * @see net.sf.jasperreports.engine.JRParameter#REPORT_RESOURCE_BUNDLE
 * @see net.sf.jasperreports.engine.JRParameter#REPORT_SCRIPTLET
 * @see net.sf.jasperreports.engine.JRPropertiesMap
 * @see net.sf.jasperreports.engine.JRStyle
 * @see net.sf.jasperreports.engine.util.DefaultFormatFactory
 * @see net.sf.jasperreports.engine.util.FormatFactory
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_name,
	JRXmlConstants.ATTRIBUTE_language,
	JRXmlConstants.ATTRIBUTE_columnCount,
	JRXmlConstants.ATTRIBUTE_printOrder,
	JRXmlConstants.ATTRIBUTE_columnDirection,
	JRXmlConstants.ATTRIBUTE_pageWidth,
	JRXmlConstants.ATTRIBUTE_pageHeight,
	JRXmlConstants.ATTRIBUTE_dpi,
	JRXmlConstants.ATTRIBUTE_orientation,
	JRXmlConstants.ATTRIBUTE_whenNoDataType,
	JRXmlConstants.ATTRIBUTE_sectionType,
	JRXmlConstants.ATTRIBUTE_columnWidth,
	JRXmlConstants.ATTRIBUTE_columnSpacing,
	JRXmlConstants.ATTRIBUTE_leftMargin,
	JRXmlConstants.ATTRIBUTE_rightMargin,
	JRXmlConstants.ATTRIBUTE_topMargin,
	JRXmlConstants.ATTRIBUTE_bottomMargin,
	JRXmlConstants.ATTRIBUTE_isTitleNewPage,
	JRXmlConstants.ATTRIBUTE_isSummaryNewPage,
	JRXmlConstants.ATTRIBUTE_isSummaryWithPageHeaderAndFooter,
	JRXmlConstants.ATTRIBUTE_isFloatColumnFooter,
	JRXmlConstants.ATTRIBUTE_scriptletClass,
	JRXmlConstants.ATTRIBUTE_formatFactoryClass,
	JRXmlConstants.ATTRIBUTE_resourceBundle,
	JRXmlConstants.ATTRIBUTE_whenResourceMissingType,
	JRXmlConstants.ATTRIBUTE_isIgnorePagination,
	JRXmlConstants.ATTRIBUTE_uuid,
	"properties",
	JRXmlConstants.ELEMENT_property,
	"propertyExpressions",
	JRXmlConstants.ELEMENT_propertyExpression,
	"imports",
	JRXmlConstants.ELEMENT_import,
	"templates",
	JRXmlConstants.ELEMENT_template,
	"styles", // for JSON 
	JRXmlConstants.ATTRIBUTE_style, // for XML
	"datasets",
	JRXmlConstants.ELEMENT_dataset,
	"scriptlets",
	JRXmlConstants.ELEMENT_scriptlet,
	"parameters", // for JSON 
	JRXmlConstants.ELEMENT_parameter, // for XML
	"query",
	"fields", // for JSON 
	JRXmlConstants.ELEMENT_field, // for XML
	"sortFields", // for JSON 
	JRXmlConstants.ELEMENT_sortField, // for XML
	"variables", // for JSON 
	JRXmlConstants.ELEMENT_variable, // for XML
	JRXmlConstants.ELEMENT_filterExpression,
	"groups", // for JSON 
	JRXmlConstants.ATTRIBUTE_group, // for XML
	JRXmlConstants.ELEMENT_background,
	JRXmlConstants.ELEMENT_title,
	JRXmlConstants.ELEMENT_pageHeader,
	JRXmlConstants.ELEMENT_columnHeader,
	JRXmlConstants.ELEMENT_detail,
	JRXmlConstants.ELEMENT_columnFooter,
	JRXmlConstants.ELEMENT_pageFooter,
	JRXmlConstants.ELEMENT_lastPageFooter,
	JRXmlConstants.ELEMENT_summary,
	JRXmlConstants.ELEMENT_noData
	})
@JsonRootName("jasperReport")
@JsonDeserialize(as = JasperDesign.class)
public interface JRReport extends JRDefaultStyleProvider, JRPropertiesHolder, JRIdentifiable
{

	/**
	 * Property that specifies a default value for the <code>whenNoDataType</code> attribute of report.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "NoPages",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_3_1,
			valueType = WhenNoDataTypeEnum.class
			)
	public static final String CONFIG_PROPERTY_WHEN_NO_DATA_TYPE = JRPropertiesUtil.PROPERTY_PREFIX + "when.no.data.type";

	/**
	 * A constant used to specify that the language used by expressions is Java.
	 */
	public static final String LANGUAGE_JAVA = "java";

	/**
	 * A constant used to specify that the language used by expressions is Groovy.
	 */
	public static final String LANGUAGE_GROOVY = "groovy";


	/**
	 * Gets the report name.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();

	/**
	 * Gets the report language. Should be Java or Groovy.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getLanguage();

	/**
	 * Gets the number of columns on each page
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Integer getColumnCount();

	/**
	 * Specifies whether columns will be filled horizontally or vertically.
	 * @return a value representing one of the print order constants in {@link PrintOrderEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public PrintOrderEnum getPrintOrder();
	
	/**
	 * Specifies whether columns will be filled from left to right or from right to left.
	 * @return a value representing one of the column direction constants in {@link RunDirectionEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public RunDirectionEnum getColumnDirection();
	
	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getPageWidth();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getPageHeight();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getDpi();

	/**
	 * Specifies whether document pages will be rendered in a portrait or landscape layout.
	 * @return a value representing one of the orientation constants in {@link OrientationEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public OrientationEnum getOrientation();

	/**
	 * Specifies the report behavior in case of empty datasources.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public WhenNoDataTypeEnum getWhenNoDataType();

	/**
	 * Sets the report behavior in case of empty datasources.
	 */
	public void setWhenNoDataType(WhenNoDataTypeEnum whenNoDataType);

	/**
	 * Specifies whether report sections are made of bands or of parts.
	 * @return a value representing one of the section type constants in {@link SectionTypeEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public SectionTypeEnum getSectionType();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getColumnWidth();

	/**
	 * Specifies the space between columns on the same page.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public int getColumnSpacing();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getLeftMargin();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getRightMargin();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getTopMargin();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public int getBottomMargin();

	/**
	 * Specifies if the title section will be printed on a separate initial page.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isTitleNewPage();

	/**
	 * Specifies if the summary section will be printed on a separate last page.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isSummaryNewPage();

	/**
	 * Specifies if the summary section will be accompanied by the page header and footer.
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isSummaryWithPageHeaderAndFooter();

	/**
	 * Specifies if the column footer section will be printed at the bottom of the column or if it
	 * will immediately follow the last detail or group footer printed on the current column.

	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isFloatColumnFooter();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getScriptletClass();

	/**
	 * Gets the name of the class implementing the {@link net.sf.jasperreports.engine.util.FormatFactory FormatFactory}
	 * interface to use with this report.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getFormatFactoryClass();

	/**
	 * Gets the base name of the report associated resource bundle.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getResourceBundle();

	/**
	 * Gets an array of report properties names.
	 */
	@JsonIgnore
	public String[] getPropertyNames();

	/**
	 * Gets a property value
	 * @param name the property name
	 */
	public String getProperty(String name);

	/**
	 *
	 */
	public void setProperty(String name, String value);

	/**
	 *
	 */
	public void removeProperty(String name);

	/**
	 * Returns the list of dynamic/expression-based properties for this report.
	 * 
	 * @return an array containing the expression-based properties of this report
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_propertyExpression)
	@JacksonXmlElementWrapper(useWrapping = false)
	public DatasetPropertyExpression[] getPropertyExpressions();
	
	/**
	 * Gets an array of imports (needed if report expression require additional classes in order to compile).
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_import)
	@JacksonXmlElementWrapper(useWrapping = false)
	public String[] getImports();

	/**
	 * Gets an array of report styles.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_style)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRStyle[] getStyles();

	/**
	 *
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_scriptlet)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRScriptlet[] getScriptlets();

	/**
	 *
	 */
	@JsonSerialize(contentUsing = ParameterSerializer.class)
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_parameter)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRParameter[] getParameters();

	/**
	 *
	 */
	public JRQuery getQuery();

	/**
	 *
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_field)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRField[] getFields();

	/**
	 *
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_sortField)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRSortField[] getSortFields();

	/**
	 *
	 */
	@JsonSerialize(contentUsing = VariableSerializer.class)
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_variable)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRVariable[] getVariables();

	/**
	 *
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_group)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRGroup[] getGroups();

	/**
	 *
	 */
	public JRBand getBackground();

	/**
	 *
	 */
	public JRBand getTitle();

	/**
	 *
	 */
	public JRBand getPageHeader();

	/**
	 *
	 */
	public JRBand getColumnHeader();

	/**
	 *
	 */
	@JsonProperty("detail")
	@JsonInclude(Include.NON_EMPTY)
	@JsonSerialize(using = SectionSerializer.class)
	public JRSection getDetailSection();

	/**
	 *
	 */
	public JRBand getColumnFooter();

	/**
	 *
	 */
	public JRBand getPageFooter();

	/**
	 *
	 */
	public JRBand getLastPageFooter();

	/**
	 *
	 */
	public JRBand getSummary();

	/**
	 *
	 */
	public JRBand getNoData();

	/**
	 * Returns the resource missing handling type.
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public WhenResourceMissingTypeEnum getWhenResourceMissingType();

	/**
	 * Sets the resource missing handling type.
	 * @param whenResourceMissingType the resource missing handling type
	 */
	public void setWhenResourceMissingType(WhenResourceMissingTypeEnum whenResourceMissingType);


	/**
	 * Returns the main report dataset.
	 * <p>
	 * The main report dataset consists of all parameters, fields, variables and groups of the report.
	 *
	 * @return the main report dataset
	 */
	@JsonIgnore
	public JRDataset getMainDataset();


	/**
	 * Returns the datasets of this report.
	 *
	 * @return the datasets of this report
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_dataset)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRDataset[] getDatasets();


	/**
	 * Decides whether to use pagination when filling the report.
	 * <p>
	 * If set to <code>true</code> the report will be generated on one long page.
	 * <p>
	 * The design attribute can be overridden at fill time by the {@link JRParameter#IS_IGNORE_PAGINATION IS_IGNORE_PAGINATION}
	 * parameter.
	 *
	 * @return whether to use pagination when filling the report
	 */
	@JsonInclude(Include.NON_DEFAULT)
	@JacksonXmlProperty(isAttribute = true)
	public boolean isIgnorePagination();


	/**
	 * Returns the list of report templates.
	 * <p/>
	 * A report template is an expression which resolves at runtime to a {@link JRTemplate template}.
	 * Templates include styles which can be used in the report.
	 * <p/>
	 * The order in which the templates are included in the report is important:
	 * <ul>
	 * 	<li>A style's parent must appear before the style itself.</li>
	 * 	<li>A style overrides styles with the same name that are placed before it.
	 * 		Also, report styles override templates styles with the same name.</li>
	 * </ul>
	 *
	 * @return the list of report templates, or <code>null</code> if none
	 * @see JRTemplate
	 * @see JRParameter#REPORT_TEMPLATES
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_template)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRReportTemplate[] getTemplates();

}
