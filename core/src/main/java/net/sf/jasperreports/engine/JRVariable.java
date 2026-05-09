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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignVariable;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * An interface for implementing classes that deal with report variables. This interface defines constants for names of
 * built-in variables and for reset, increment and calculation types.
 * <h3>Report Variables</h3>
 * Report variables are special objects built on top of a report expression. They can simplify
 * the report template by isolating in one place an expression that is heavily used
 * throughout the report template, and they can perform various calculations based on the
 * corresponding expression.
 * <p/>
 * In its expression, a variable can reference other report variables, fields, or parameters.
 * With every iteration through the data source, variables are evaluated/incremented in the
 * same order as they are declared. Therefore, the order of variables as they appear in the
 * report template is very important.
 * <h3>Variable Name</h3>
 * Just as for parameters and fields, the name attribute of the <code>&lt;variable&gt;</code> element is
 * mandatory and allows referencing the variable by its declared name in report
 * expressions.
 * <h3>Variable Class</h3>
 * The class attribute contains the name of the class to which the variable values belong.
 * The default is <code>java.lang.String</code>, but you can declare report variables of any class as
 * long as the class is available in the classpath, both at report-compilation time and report-filling
 * time.
 * <h3>Reset Type</h3>
 * The value of a report variable can change with every iteration, but it can be brought back
 * to the value returned by its initial value expression at specified times during the report filling
 * process. This behavior is controlled using the resetType attribute, which
 * indicates when the variable should be reinitialized during the report-filling process.
 * There are five reset types for a variable:
 * <ul>
 * <li><code>None</code> - The variable will never be initialized using its initial value expression and
 * will only contain values obtained by evaluating the variable's expression</li>
 * <li><code>Report</code> - The variable is initialized only once, at the beginning of the
 * report-filling process, with the value returned by the variable's initial value
 * expression</li>
 * <li><code>Page</code> - The variable is reinitialized at the beginning of each new page.</li>
 * <li><code>Column</code> - The variable is reinitialized at the beginning of each new column</li>
 * <li><code>Group</code> - The variable is reinitialized every time the group specified by 
 * the <code>resetGroup</code> attributes breaks</li>
 * </ul>
 * <h3>Reset Group</h3>
 * If present, the <code>resetGroup</code> attribute contains the name of a report group and works only
 * in conjunction with the <code>resetType</code> attribute, whose value must be
 * <code>resetType="Group"</code>.
 * <h3>Increment Type</h3>
 * This property lets you choose the exact moment to increment the variable. By default,
 * variables are incremented with each record in the data source, but in reports with
 * multiple levels of data grouping, some variables might calculate higher-level totals and
 * would need to be incremented only occasionally, not with every iteration through the
 * data source.
 * <p/>
 * This attribute uses the same values as the resetType attribute, as follows:
 * <ul>
 * <li><code>None</code> - The variable is incremented with every record during the
 * iteration through the data source</li>
 * <li><code>Report</code> - The variable never gets incremented during the report filling process.</li>
 * <li><code>Page</code> - The variable is incremented with each new page.</li>
 * <li><code>Column</code> - The variable is incremented with each new column.</li>
 * <li><code>Group</code> - The variable is incremented every time the group specified
 * by the <code>incrementGroup</code> attributes breaks</li>
 * </ul>
 * <h3>Increment Group</h3>
 * If present, the <code>incrementGroup</code> attribute contains the name of a report group. It works
 * only in conjunction with the <code>incrementType</code> attribute, whose value must be
 * <code>incrementType="Group"</code>.
 * <h3>Calculations</h3>
 * As mentioned, variables can perform built-in types of calculations on their corresponding
 * expression values. Following are described all the possible values for the
 * <code>calculation</code> attribute of the <code>&lt;variable&gt;</code> element.
 * <dl>
 * <dt><code>Nothing</code></dt>
 * <dd>This is the default calculation type that a variable performs. It means that the variable's
 * value is recalculated with every iteration in the data source and that the value returned is
 * obtained by simply evaluating the variable's expression.</dd>
 * <dt><code>Count</code></dt>
 * <dd>A count variable includes in the count the non-null values returned after evaluating the
 * variable's main expression, with every iteration in the data source. Count variables must
 * always be of a numeric type. However, they can have non-numeric expressions as their
 * main expression since the engine does not care about the expression type, but only
 * counts for the non-null values returned, regardless of their type.
 * <br/>
 * Only the variable's initial value expression should be numeric and compatible with the
 * variable's type, since this value will be directly assigned to the count variable when
 * initialized.</dd>
 * <dt><code>DistinctCount</code></dt>
 * <dd>This type of calculation works just like the <code>Count</code> calculation, the only difference being
 * that it ignores repeating values and counts only for distinct non-null values.</dd>
 * <dt><code>Sum</code></dt>
 * <dd>The reporting engine can sum up the values returned by the variable's main expression if
 * this type of calculation is chosen; but make sure the variable has a numeric type. One
 * cannot calculate the sum of a <code>java.lang.String</code> or <code>java.util.Date</code> type of report
 * variable unless a customized variable incrementer is used.</dd>
 * <dt><code>Average</code></dt>
 * <dd>The reporting engine can also calculate the average for the series of values obtained by
 * evaluating the variable's expression for each record in the data source. This type of
 * calculation can be performed only for numeric variables.</dd>
 * <dt><code>Lowest</code> and <code>Highest</code></dt>
 * <dd>Choose this type of calculation when you want to obtain the lowest or highest value in
 * the series of values obtained by evaluating the variable's expression for each data source
 * record.</dd>
 * <dt><code>StandardDeviation</code> and <code>Variance</code></dt>
 * <dd>In some special reports, you might want to perform more advanced types of calculations
 * on numeric expressions. JasperReports has built-in algorithms to obtain the standard
 * deviation and the variance for the series of values returned by evaluation of a report
 * variable's expression.</dd>
 * <dt><code>System</code></dt>
 * <dd>This type of calculation can be chosen only when you don't want the engine to calculate
 * any value for the variable. That means you are calculating the value for that variable
 * yourself, almost certainly using the scriptlets functionality of JasperReports.
 * For this type of calculation, the only thing the engine does is to conserve the calculated value 
 * from one iteration in the data source to the next.</dd>
 * <dt><code>First</code></dt>
 * <dd>When using the calculation type <code>First</code>, the variable will keep the value obtained after
 * the first incrementation and will not change it until the reset event occurs.</dd>
 * </dl>
 * Here is a simple report variable declaration that calculates the sum for a numeric report
 * field called <code>Quantity</code>:
 * <pre>
 *   &lt;variable name="QuantitySum" class="java.lang.Double" calculation="Sum"&gt;
 *     &lt;variableExpression&gt;$F{Quantity}&lt;/variableExpression&gt;
 *   &lt;/variable&gt;</pre>
 * If you want the sum of this field for each page, here's the complete variable declaration:
 * <pre>
 *   &lt;variable name="QuantitySum" class="java.lang.Double" resetType="Page" calculation="Sum"&gt;
 *     &lt;variableExpression&gt;$F{Quantity}&lt;/variableExpression&gt;
 *     &lt;initialValueExpression&gt;0&lt;/initialValueExpression&gt;
 *   &lt;/variable&gt;</pre>
 * In this example, the page sum variable will be initialized with zero at the beginning of
 * each new page.
 * <h3>Incrementers</h3>
 * All calculations in the JasperReports engine are performed incrementally. This is
 * obvious for variables that calculate counts, sums, or the highest and lowest value of a
 * series, but is also true for more complex calculations like average or standard deviation.
 * There are formulas that allow updating the average value of a series when a new element
 * is added, so the average is updated with each iteration through the data source.
 * <p/>
 * JasperReports provides a built-in set of calculations that depend on the type of the data
 * involved. You can also create custom calculation capabilities using simple interfaces.
 * <p/>
 * If a variable needs to perform a certain type of calculation on some special data,
 * implement the {@link net.sf.jasperreports.engine.fill.JRIncrementer} interface and
 * associate that implementation with a report variable that shows the JasperReports engine
 * how to handle that custom calculation.
 * <p/>
 * To associate custom types of calculations with a given report variable, set the
 * <code>incrementerFactoryClass</code> attribute to the name of a class that implements the
 * {@link net.sf.jasperreports.engine.fill.JRIncrementerFactory} interface. The
 * factory class will be used by the engine to instantiate incrementer objects at runtime
 * depending on the calculation attribute set for the variable.
 * <p/>
 * Such customized calculations could be useful for making JasperReports sum up
 * <code>java.lang.String</code> values or for teaching it how to calculate the average value of some
 * custom-made numeric data (third-party optimized implementations of big decimal
 * numbers, for instance).
 * <h3>GroupName_COUNT Built-In Variable</h3>
 * When declaring a report group, the engine will automatically create a count variable that will calculate
 * the number of records that make up the current group (number of records processed between group ruptures).
 * <p/>
 * The name for this variable comes from the name of the group it corresponds to, suffixed with the
 * "<code>_COUNT</code>" sequence. It can be used like any other report variable, in any report expression (even in the
 * current group expression like you can see done in the "BreakGroup" of the <i>jasper</i> sample).
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"name",
	"resetType",
	"incrementType",
	"calculation",
	"resetGroup",
	"incrementGroup",
	"incrementerFactoryClass",
	"class",
	"description",
	"expression",
	"initialValueExpression"
	})
@JsonDeserialize(as = JRDesignVariable.class)
public interface JRVariable extends JRCloneable
{
	/**
	 * Built-in variable that contains the total number of records read from the datasource. After finishing iterating throught the
	 * datasource, it will contain the total number of records that were processed.
	 */
	public static final String REPORT_COUNT = "REPORT_COUNT";


	/**
	 * Built-in variable containing the number of records that were processed when generating the current page.
	 */
	public static final String PAGE_COUNT = "PAGE_COUNT";


	/**
	 * This variable contains the number of records that were processed when generating the current column.
	 */
	public static final String COLUMN_COUNT = "COLUMN_COUNT";


	/**
	 * Built-in variable containing the current page number. At the end of the report filling process, it will contain the total
	 * number of pages for the resulting document.
	 */
	public static final String PAGE_NUMBER = "PAGE_NUMBER";


	/**
	 * Built-in variable containing the current column number.
	 */
	public static final String COLUMN_NUMBER = "COLUMN_NUMBER";

	/**
	 * A variable that provides the current master report page number.
	 * 
	 * <p>
	 * It can only be used in text elements with {@link EvaluationTimeEnum#MASTER Master} evaluation time,
	 * it evaluates to <code>null</code> before the moment in which master elements are resolved.
	 * </p> 
	 */
	public static final String MASTER_CURRENT_PAGE = "MASTER_CURRENT_PAGE";


	/**
	 * A variable that provides the number of master report pages.
	 * 
	 * <p>
	 * It can only be used in text elements with {@link EvaluationTimeEnum#MASTER Master} evaluation time,
	 * it evaluates to <code>null</code> before the moment in which master elements are resolved.
	 * </p> 
	 */
	public static final String MASTER_TOTAL_PAGES = "MASTER_TOTAL_PAGES";

	/**
	 * Returns the name of the variable. Since all variables are stored in a map, the variable names are the keys in the map.
	 * @return a string containing the variable name
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getName();


	/**
	 * Gets the variable optional description.
	 */
	@JacksonXmlCData
	public String getDescription();
		
	/**
	 * Sets the variable description.
	 */
	public void setDescription(String description);
		
	/**
	 * Returns the class of the variable value. Any class is allowed as long as it is in the classpath at compile and run time.
	 * @return a <tt>Class</tt> instance representing the variable value class
	 */
	@JsonIgnore
	public Class<?> getValueClass();
		
	/**
	 * Returns the string name of the variable value class.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_class)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_class, isAttribute = true)
	public String getValueClassName();
		
	/**
	 * Returns the class of the incrementer factory used for choosing the right incrementer for the variable value.
	 * @return the <tt>Class</tt> instance of the incrementer factory
	 * @see net.sf.jasperreports.engine.fill.JRIncrementer
	 * @see net.sf.jasperreports.engine.fill.JRIncrementerFactory
	 */
	@JsonIgnore
	public Class<?> getIncrementerFactoryClass();
		
	/**
	 * Returns the string name of the variable value class.
	 */
	@JsonGetter(JRXmlConstants.ATTRIBUTE_incrementerFactoryClass)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_incrementerFactoryClass, isAttribute = true)
	public String getIncrementerFactoryClassName();
		
	/**
	 * Gets the variable reset type.
	 * @return a value representing one of the reset type constants in {@link ResetTypeEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public ResetTypeEnum getResetType();
	
	/**
	 * Gets the variable increment type.
	 * @return a value representing one of the reset type constants in {@link IncrementTypeEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public IncrementTypeEnum getIncrementType();
	
	/**
	 * Gets the variable calculation type.
	 * @return a value representing one of the calculation type constants in {@link CalculationEnum}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public CalculationEnum getCalculation();

	/**
	 * Returns <code>true</code> if the variable calculation type is system defined.
	 * @see CalculationEnum#SYSTEM
	 */
	@JsonIgnore
	public boolean isSystemDefined();

	/**
	 * Returns the main expression for this variable. The expression must be numeric for certain calculation types.
	 * @return a {@link JRExpression} instance containing the expression.
	 */
	public JRExpression getExpression();
		
	/**
	 * Returns the initial value expression for this variable. The expression must be numeric for certain calculation types.
	 * @return a {@link JRExpression} instance containing the initial expression.
	 */
	public JRExpression getInitialValueExpression();
		
	/**
	 * Returns the group whose break triggers the variable reset. Only used when {@link JRVariable#getResetType()} returns
	 * {@link ResetTypeEnum#GROUP}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getResetGroup();
		
	/**
	 * Returns the group whose break triggers the variable increment. Only used when {@link JRVariable#getIncrementType()} returns
	 * {@link IncrementTypeEnum#GROUP}.
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getIncrementGroup();
		
}
