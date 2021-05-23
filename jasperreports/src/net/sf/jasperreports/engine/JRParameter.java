/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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

import net.sf.jasperreports.engine.type.ParameterEvaluationTimeEnum;


/**
 * Provides functionality for report parameters.
 * <p>
 * Parameters are object references that are passed into the report-filling operations. They
 * are very useful for passing to the report engine data that it cannot normally find in its
 * data source.
 * </p><p>
 * For example, one could pass to the report engine the name of the user who launched the
 * report-filling operation if this name have to appear on the report. Or, if the report title 
 * depends on various conditions, it can be passed as parameter to the report during the report execution.
 * </p><p>
 * Declaring a parameter in a report template is very simple. Simply specify only its name
 * and its class:</p>
 * <pre>
 *   &lt;parameter name="ReportTitle" class="java.lang.String"/&gt;
 *   &lt;parameter name="MaxOrderID" class="java.lang.Integer"/&gt;
 *   &lt;parameter name="SummaryImage" class="java.awt.Image"/&gt;</pre>
 * The supplied values for the report parameters can be used in the various report
 * expressions, in the report SQL query, or even in a report scriptlet class.
 * <p>
 * Below are described the components that make a report parameter
 * definition complete.</p>
 * <h3>Parameter Name</h3>
 * The <code>name</code> attribute of the <code>&lt;parameter&gt;</code> element is mandatory and allows referencing the
 * parameter by its declared name. The naming conventions of JasperReports are similar to
 * those of the Java language regarding variable declaration. That means that the parameter
 * name should be a single word containing no special characters like a dot or a comma.
 * <h3>Parameter Class</h3>
 * The second mandatory attribute for a report parameter specifies the class name for the
 * parameter values. The <code>class</code> attribute can have any value as long it represents a class
 * name that is available in the classpath both at report-compilation time and report-filling
 * time.
 * <h3>Prompting for Parameter Values</h3>
 * In some GUI applications, it is useful to establish the set of report parameters for which
 * the application should request user input, before launching the report-filling process.
 * It is also useful to specify the text description that will prompt for the user input for each
 * of those parameters.
 * <p>
 * This is why we have the Boolean <code>isForPrompting</code> attribute in the parameter
 * declaration sequence and the inner <code>&lt;parameterDescription&gt;</code> element.</p>
 * <h3>Parameter Custom Properties</h3>
 * In addition to the parameter description and the prompting flag mentioned previously,
 * some applications might need to attach more information or metadata to a report
 * parameter definition. This is why report parameters can have any number of
 * custom-defined name/value property pairs, just like the report template itself could have
 * at the top level.
 * <h3>Parameter Default Value</h3>
 * Parameter values are supplied to the report-filling process packed in a <code>java.util.Map</code>
 * object with the parameter names as the keys. This way, there is no more need to supply a
 * value for each parameter every time.
 * <p>
 * If there is no value supplied for a parameter, its value is considered to be null, unless
 * a default value expression is specified in the report template for this particular report
 * parameter. This expression is only evaluated if there is no value supplied for the given
 * parameter.
 * </p><p>
 * Here's a <code>java.util.Date</code> parameter whose value will be the current date if you do not
 * supply a specific date value when filling the report:</p>
 * <pre>
 *   &lt;parameter name="MyDate" class="java.util.Date"&gt;
 *     &lt;defaultValueExpression&gt;
 *       new java.util.Date()
 *     &lt;/defaultValueExpression&gt;
 *   &lt;/parameter&gt;</pre>
 * In the default value expression of a parameter, one can only use previously defined
 * report parameters.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRParameter extends JRPropertiesHolder, JRCloneable
{


	/**
	 * A <tt>Map</tt> containing report parameters passed by users at fill time.
	 * <p>
	 * This parameter is especially useful when you want to pass to the subreports the same set
	 * of report parameters that the master report has received.
	 */
	public static final String REPORT_PARAMETERS_MAP = "REPORT_PARAMETERS_MAP";


	/**
	 * A <tt>net.sf.jasperreports.engine.JasperReportsContext</tt> instance representing the current report filling context.
	 */
	public static final String JASPER_REPORTS_CONTEXT = "JASPER_REPORTS_CONTEXT";


	/**
	 * This parameter gives access to the current {@link net.sf.jasperreports.engine.JasperReport} 
	 * template object that is being filled.
	 */
	public static final String JASPER_REPORT = "JASPER_REPORT";


	/**
	 * A <code>java.sql.Connection</code> needed to run the default report query.
	 * <p>
	 * This report parameter points to the <code>java.sql.Connection</code> object that was supplied to
	 * the engine for execution of the SQL report query through JDBC, if it is the case. It has a
	 * value different than null only if the report (or subreport) has received a
	 * <code>java.sql.Connection</code> when the report-filling process was launched, and not a
	 * {@link net.sf.jasperreports.engine.JRDataSource} instance.
	 * </p><p>
	 * This parameter is also useful for passing the same JDBC connection object that was used
	 * by the master report to its subreports.
	 */
	public static final String REPORT_CONNECTION = "REPORT_CONNECTION";


	/**
	 * An integer allowing users to limit the datasource size.
	 * <p>
	 * Some users may want to limit the number of records from the report data source during the
	 * report-filling process. This built-in parameter accepts <code>java.lang.Integer</code> values
	 * representing the number of records from the data source that the engine will process
	 * during the report filling. When the internal record counter reaches the specified value,
	 * the engine will assume that it has reached the last record from the data source and will
	 * stop the iteration through the rest of the data source.
	 * 
	 */
	public static final String REPORT_MAX_COUNT = "REPORT_MAX_COUNT";


	/**
	 * A {@link net.sf.jasperreports.engine.JRDataSource} instance representing the report data source. 
	 * JasperReports defines some convenience implementations
	 * of {@link net.sf.jasperreports.engine.JRDataSource}, but users may create their own data sources for specific needs.
	 * <p>
	 * When filling a report, a data source object is either directly supplied by the parent
	 * application or created behind the scenes by the reporting engine when a JDBC
	 * connection is supplied. This built-in parameter allows you access to the report's data
	 * source in the report expressions or in the scriptlets.
	 * 
	 */
	public static final String REPORT_DATA_SOURCE = "REPORT_DATA_SOURCE";


	/**
	 * A {@link JRAbstractScriptlet} containing an instance of the report scriptlet provided by the user.
	 * <p>
	 * This built-in parameter points to the report scriptlet specified using the 
	 * <code>scriptletClass</code> attribute available at report or dataset level.
	 * </p><p>
	 * Even if the report or dataset does not declare any scriptlet, this
	 * built-in parameter will point to a
	 * {@link net.sf.jasperreports.engine.JRAbstractScriptlet} instance, which in this case
	 * is a {@link net.sf.jasperreports.engine.JRDefaultScriptlet} object. When using
	 * scriptlets, the scriptlet built-in parameters referencing the scriptlet instances that are created
	 * when filling the report, allow specific methods to be called on them. This is so the data
	 * that the scriptlet objects have prepared during the filling process can be used or
	 * manipulated.
	 */
	public static final String REPORT_SCRIPTLET = "REPORT_SCRIPTLET";


	/**
	 * A <tt>java.util.Locale</tt> instance containing the resource bundle desired locale. This parameter should be used in
	 * conjunction with {@link #REPORT_RESOURCE_BUNDLE}.
	 * <p>
	 * Report templates can be reused to generate documents in different languages. The target
	 * language used during report filling is specified by the <code>java.util.Locale</code> object
	 * supplied as the value for this built-in parameter. The engine uses
	 * <code>Locale.getDefault()</code> if no value is explicitly supplied for this parameter at
	 * runtime.
	 */
	public static final String REPORT_LOCALE = "REPORT_LOCALE";


	/**
	 * The <code>java.util.ResourceBundle</code> containing localized messages. If the resource bundle base name is specified at
	 * design time, the engine will try to load the resource bundle using specified name and locale.
	 * <p>
	 * This parameter points to the <code>java.util.ResourceBundle</code> object that contains
	 * localized information associated with the report template. This object can be supplied
	 * directly by the caller application or created by the engine using the resource bundle base
	 * name specified in the <code>resourceBundle</code> attribute of the report template. The engine tries
	 * to read locale-specific information from this object based on the report-supplied locale
	 * and the key used inside the report expressions.
	 */
	public static final String REPORT_RESOURCE_BUNDLE = "REPORT_RESOURCE_BUNDLE";
	

	/**
	 * A <code>java.util.TimeZone</code> instance to use for date formatting.
	 * <p>
	 * The <code>java.util.TimeZone</code> instance supplied as value for this built-in parameter is used
	 * during the report-filling process to format all date and time values. If no value is supplied
	 * for this parameter at runtime, the default time zone of the host machine is used.
	 */
	public static final String REPORT_TIME_ZONE = "REPORT_TIME_ZONE";


	/**
	 * The {@link net.sf.jasperreports.engine.JRVirtualizer} to be used for page virtualization.  This parameter is optional.
	 * <p>
	 * When very large reports are generated and memory becomes insufficient, the engine can
	 * rely on the report virtualization mechanism to optimize memory consumption during
	 * report filling. Report virtualization is activated by supplying an instance of the
	 * {@link net.sf.jasperreports.engine.JRVirtualizer} interface as the value for this
	 * built-in parameter. By doing this, the engine will store temporary
	 * data in a serialized form in order to minimize the amount of memory needed during
	 * report filling.
	 */
	public static final String REPORT_VIRTUALIZER = "REPORT_VIRTUALIZER";

	
	/**
	 * A <code>java.lang.ClassLoader</code> instance to be used during the report filling process to load resources.
	 * <p>
	 * Resources such as images, fonts, and subreport templates can be referenced using their relative
	 * classpath location. By default, JasperReports uses the current thread's context class
	 * loader to locate the resource. If that fails, it then falls back to the class loader that loads
	 * the library's classes themselves. To extend the resource-lookup mechanism and give
	 * greater flexibility to the library, one can pass a custom-made class loader implementation
	 * as the value for this built-in fill-time parameter. This would allow
	 * applications to load resources from repository locations that are not normally part of the
	 * overall application classpath.
	 */
	public static final String REPORT_CLASS_LOADER = "REPORT_CLASS_LOADER";

	
	/**
	 * A {@link net.sf.jasperreports.engine.util.FormatFactory} instance to be used 
	 * during the report filling process to create instances of <code>java.text.DateFormat</code> to format date text
	 * fields and instances of <code>java.text.NumberFormat</code> to format numeric text fields.
	 * <p>
	 * The value for this parameter is an instance of the
	 * {@link net.sf.jasperreports.engine.util.FormatFactory} interface, which is either
	 * provided directly by the calling program or created internally by the reporting engine,
	 * using the <code>formatFactoryClass</code> attribute of the report template. If this parameter is
	 * provided with a value by the report-filling process caller, it takes precedence over the
	 * attribute in the report template.
	 */
	public static final String REPORT_FORMAT_FACTORY = "REPORT_FORMAT_FACTORY";

	/**
	 * An option allowing the remainder of a vertically-filled report's detail section to be filled with blank rows of
	 * data when the main data set is exhausted.
	 */
	public static final String REPORT_FILL_DETAIL_WITH_BLANK_ROWS = "REPORT_FILL_DETAIL_WITH_BLANK_ROWS";
	
	/**
	 * This built-in flag parameter specifies whether to ignore pagination.
	 * <p>
	 * By default, JasperReports produces page-oriented documents that are ready for printing.
	 * </p><p>
	 * Sometimes, especially in Web applications, pagination is irrelevant. One way to avoid
	 * breaking documents into multiple pages and to obtain a more flow-oriented document
	 * layout is to set this built-in parameter to <code>Boolean.TRUE</code> at runtime. By doing this, the
	 * engine will ignore all the report settings that are related to page breaking and will
	 * produce a document that contains a single very large page.
	 * </p><p>
	 * When used, this fill-time parameter overrides the value of the isIgnorePagination
	 * property of the report template.
	 */
	public static final String IS_IGNORE_PAGINATION = "IS_IGNORE_PAGINATION";
	
	public static final String MAX_PAGE_HEIGHT = JRPropertiesUtil.PROPERTY_PREFIX + "max.page.height";
	
	public static final String MAX_PAGE_WIDTH = JRPropertiesUtil.PROPERTY_PREFIX + "max.page.width";

	
	/**
	 * A {@link java.util.Collection collection} of {@link JRTemplate templates} passed to the
	 * report at fill time.
	 * <p/>
	 * These templates add to the ones specified in the report (see {@link JRReport#getTemplates()}).
	 * In the final templates list they are placed after the report templates; therefore styles from
	 * these templates can use and override styles in the report templates.
	 * They are, however, placed before the report styles hence report styles can use and override
	 * styles from these templates.
	 */
	public static final String REPORT_TEMPLATES = "REPORT_TEMPLATES";


	/**
	 * 
	 */
	public static final String SORT_FIELDS = "SORT_FIELDS";

	/**
	 * 
	 */
	public static final String REPORT_CONTEXT = "REPORT_CONTEXT";
	
	/**
	 * A {@link DatasetFilter} to be used in addition to {@link JRDataset#getFilterExpression()}
	 * for filtering dataset rows.
	 */
	public static final String FILTER = "FILTER";

	/**
	 *
	 */
	public String getName();
		
	/**
	 *
	 */
	public String getDescription();
		
	/**
	 *
	 */
	public void setDescription(String description);
		
	/**
	 *
	 */
	public Class<?> getValueClass();

	/**
	 *
	 */
	public String getValueClassName();

	/**
	 *
	 */
	public boolean isSystemDefined();

	/**
	 *
	 */
	public boolean isForPrompting();

	/**
	 * Specifies when the default value expression of a parameter is evaluated.
	 */
	public ParameterEvaluationTimeEnum getEvaluationTime();

	/**
	 *
	 */
	public JRExpression getDefaultValueExpression();
	
	/**
	 * Returns the parameter nested value type.
	 * 
	 * <p>
	 * The parameter nested value type is used when the parameter value class
	 * is not sufficient in determining the expected type of the parameter values.
	 * The most common such scenario is when the parameter value class is
	 * {@link java.util.Collection} or a derived class, in which case the nested
	 * type specifies the type of values which are to be placed inside the collection.
	 * 
	 * @return the nested value type for this parameter, 
	 * or <code>null</code> if none set
	 * 
	 * @see #getValueClass()
	 */
	public Class<?> getNestedType();

	/**
	 * Returns the name of the parameter nested value type.
	 * 
	 * @return the name of the nested value type for this parameter,
	 * or <code>null</code> if none set
	 * 
	 * @see #getNestedType()
	 */
	public String getNestedTypeName();

}
