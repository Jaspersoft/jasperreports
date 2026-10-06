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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import net.sf.jasperreports.engine.type.ExpressionTypeEnum;
import net.sf.jasperreports.jackson.util.ExpressionDeserializer;
import net.sf.jasperreports.jackson.util.ExpressionSerializer;

/**
 * Provides the JasperReports expressions functionality.
 * <h3>Expressions in JasperReports</h3>
 * Expressions are a powerful feature of JasperReports. They can be used to declare report
 * variables that perform various calculations, group data on the report, specify report text
 * field content, or further customize the appearance of report objects.
 * <p>
 * By default, the Java language is used for writing report expressions, but other scripting
 * languages can be used if a corresponding report compiler able to produce the information
 * needed for expression evaluation at runtime is available. Currently, JasperReports ships
 * with report compilers that can compile report templates using the Groovy scripting
 * language or JavaScript, inside report expressions.
 * </p><p>
 * For simplicity's sake, in the next paragraphs we'll assume that expressions
 * have been written using the Java language.
 * </p><p>
 * Since all JasperReports expressions are (or are assumed to be) real Java expressions, then 
 * any valid java class can be used inside them, as long as they are referred to by using the complete class
 * name (including the package), or are adding the proper imports to your report template. We also have to 
 * make sure that the classes we are using in the report expressions are available in the classpath when
 * the report is compiled and filled with data.
 * </p><p>
 * In a JRXML report template, there are several elements that define expressions,
 * including <code>&lt;variableExpression&gt;</code>, <code>&lt;initialValueExpression&gt;</code>,
 * <code>&lt;groupExpression&gt;</code>, <code>&lt;printWhenExpression&gt;</code>, 
 * <code>&lt;imageExpression&gt;</code>, <code>&lt;textFieldExpression&gt;</code>...
 * </p>
 * <h3>Expression Syntax</h3>
 * Report expressions would be useless if there were no way to reference in them the report
 * parameters, report fields, or declared report variables. For this reason, a special
 * JasperReports syntax on top of the scripting language allows introducing such
 * references in the report expressions created in the JRXML report template.
 * <p>
 * Report parameter references are introduced using the <code>$P{}</code> character sequence, as in the
 * following example:</p>
 * <pre>
 *   &lt;textFieldExpression&gt;
 *     $P{ReportTitle}
 *   &lt;/textFieldExpression&gt;</pre>
 * This example assumes that the report design declares a report parameter named
 * ReportTitle, whose class is <code>java.lang.String</code>. The text field will display the value
 * of this parameter when the report is filled.
 * <p>
 * To use a report field reference in an expression, one must put the name of the field
 * between the <code>$F{</code> and <code>}</code> character sequences. For example, to display the concatenated
 * values of two data source fields in a text field, define an expression like this one:</p>
 * <pre>
 *   &lt;textFieldExpression&gt;
 *     $F{FirstName} + " " + $F{LastName}
 *   &lt;/textFieldExpression&gt;</pre>
 * The expression can be even more complex, as in the following example:
 * <pre>
 *   &lt;textFieldExpression&gt;
 *     $F{FirstName} + " " + $F{LastName} + " was hired on " +
 *     (new SimpleDateFormat("MM/dd/yyyy")).format($F{HireDate}) + "."
 *   &lt;/textFieldExpression&gt;</pre>
 * To reference a report variable in an expression, you must put the name of the variable
 * between <code>$V{</code> and <code>}</code>, as in this example:
 * <pre>
 *   &lt;textFieldExpression&gt;
 *     "Total quantity : " + $V{QuantitySum} + " kg."
 *   &lt;/textFieldExpression&gt;</pre>
 * As you can see, the parameter, field, and variable references introduced by the special
 * JasperReports syntax are in fact real Java objects. Knowing their class from the
 * parameter, field or variable declaration made in the report template, you can even call
 * methods on those object references in your expressions.
 * <p>
 * Here's one way to extract and display the first letter from a java.lang.String report
 * field:</p>
 * <pre>
 *   &lt;textFieldExpression&gt;
 *     $F{FirstName}.substring(0, 1)
 *   &lt;/textFieldExpression&gt;</pre>
 * When support for internationalization was added to JasperReports, a new token was
 * introduced in the JasperReports syntax to allow access to the locale-specific resources
 * inside the report's associated resource bundle. The <code>$R{}</code> character syntax extracts the
 * locale-specific resource from the resource bundle based on the key that must be put
 * between the brackets:
 * <pre>
 *   &lt;textFieldExpression&gt;
 *     $R{report.title}
 *   &lt;/textFieldExpression&gt;</pre>
 * The preceding text field displays the title of the report by extracting the String value
 * from the resource bundle associated with the report template based on the runtime supplied
 * locale and the <code>report.title</code> key. 
 * <p>
 * In some rare cases (for example, debugging), there is the need to escape an expression
 * token like the ones described previously. The escape syntax for the tokens requires
 * duplicating the <code>$</code> character. Escaping a <code>$P{paramName}</code> token is achieved by writing 
 * <code>$$P{paramName}</code> in the expression. When escaped, an expression token is preserved as-is
 * in the resulting expression, and no attempt to parse the token is made.
 * </p>
 * <h3>Conditional Expressions</h3>
 * As the Java language documentation states, an expression is a series of variables,
 * operators, and method calls (constructed according to the syntax of the language) that
 * evaluate to a single value.
 * <p>
 * So even if we rely on the Java language for writing report expressions, we cannot use
 * Java statements like <code>if else</code>, <code>for</code>, or <code>while</code>.
 * </p><p>
 * However, quite often an expression must return a value that is calculated based on a
 * condition or even multiple conditions. To accomplish this, use the conditional operator
 * <code>?:</code>. One can even nest this operator inside a Java expression to obtain the desired output
 * based on multiple conditions.
 * </p><p>
 * The following text field displays No data if the value for the quantity field is null:</p>
 * <pre>
 *   &lt;textFieldExpression&gt;
 *     $F{quantity} == null ? "No data" : String.valueOf($F{quantity})
 *   &lt;/textFieldExpression&gt;</pre>
 * <h3>Expressions Calculator</h3>  
 * The expressions calculator is the entity inside JasperReports that evaluates
 * expressions and increments variables or datasets at report-filling time. When a report
 * template is compiled, the report compiler produces and stores in the compiled report
 * template ({@link net.sf.jasperreports.engine.JasperReport} object) information that it will use at report-filling time to
 * build an instance of the {@link net.sf.jasperreports.engine.fill.JRCalculator} class.
 * 
 * @see net.sf.jasperreports.engine.fill.JRCalculator
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonSerialize(using = ExpressionSerializer.class)
@JsonDeserialize(using = ExpressionDeserializer.class)
// jackson annotations in this interface are redundant because the custom serializer/deserializer is doing a better job for the json format
public interface JRExpression extends JRCloneable
{


	/**
	 *
	 */
	public static final byte EVALUATION_OLD = 1;
	public static final byte EVALUATION_ESTIMATED = 2;
	public static final byte EVALUATION_DEFAULT = 3;

	/**
	 * Dummy ID that is assigned to expression that are not used (and not collected).
	 */
	public static final Integer NOT_USED_ID = -1;

	/**
	 * ID that is assigned to expression that are not compiled and need to be interpreted at fill time.
	 */
	public static final Integer ID_INTERPRETED = -2;

	/**
	 *
	 */
	@JsonIgnore
	public int getId();
			
	/**
	 *
	 */
	@JsonIgnore
	public JRExpressionChunk[] getChunks();

	/**
	 *
	 */
	@JacksonXmlText
	public String getText();

	/**
	 *
	 */
	@JacksonXmlProperty(isAttribute = true)
	public ExpressionTypeEnum getType();

	default boolean isInterpreted()
	{
		return getId() == ID_INTERPRETED;
	}

}
