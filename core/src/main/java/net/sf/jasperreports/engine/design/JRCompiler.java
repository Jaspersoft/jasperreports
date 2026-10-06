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
package net.sf.jasperreports.engine.design;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * This interface exposes methods to load expressions evaluators and compile report templates.
 * <p>
 * Source report templates, created either by using the API or by parsing JRXML files, are
 * subject to the report compilation process before they are filled with data.
 * This is necessary to make various consistency validations and to incorporate into these
 * report templates data used to evaluate all report expressions at runtime.
 * </p><p>
 * The compilation process transforms
 * {@link net.sf.jasperreports.engine.design.JasperDesign} objects into
 * {@link net.sf.jasperreports.engine.JasperReport} objects. Both classes are
 * implementations of the same basic {@link net.sf.jasperreports.engine.JRReport}
 * interface. However, {@link net.sf.jasperreports.engine.JasperReport} objects 
 * cannot be modified once they are produced,
 * while {@link net.sf.jasperreports.engine.design.JasperDesign} objects can. 
 * This is because some modifications made on the
 * report template would probably require re-validation, or if a report expression is
 * modified, the compiler-associated data stored inside the report template would have to be
 * updated.
 * </p><p>
 * {@link net.sf.jasperreports.engine.design.JasperDesign} objects are produced when parsing JRXML files using the
 * {@link net.sf.jasperreports.engine.xml.JRXmlLoader} or created directly by the parent
 * application if dynamic report templates are required. The GUI tools for editing
 * JasperReports templates also work with this class to make in-memory modifications to
 * the report templates before storing them on disk.
 * </p><p>
 * A {@link net.sf.jasperreports.engine.design.JasperDesign} object must be 
 * subject to the report compilation process to produce a
 * {@link net.sf.jasperreports.engine.JasperReport} object.
 * </p><p>
 * Central to this process is this {@link net.sf.jasperreports.engine.design.JRCompiler}
 * interface, which defines few methods, one being the following:
 * </p><p>
 * <code>public JasperReport compileReport(JasperDesign design) throws JRException;</code>
 * </p><p>
 * There are several implementations for this compiler interface depending on the language
 * used for the report expressions or the mechanism used for their runtime evaluation.
 * </p>
 * <h3>Expressions Scripting Language</h3>
 * The default language for the report expressions is Java, but report expressions
 * can be written in Groovy, JavaScript or any other scripting language as long as a report
 * compiler implementation that can evaluate them at runtime is available.
 * <p>
 * JasperReports currently ships report compiler implementations for the Groovy scripting
 * language (<a href="http://groovy.codehaus.org">http://groovy.codehaus.org</a>) and 
 * JavaScript (<a href="http://www.mozilla.org/rhino">http://www.mozilla.org/rhino</a>). </p>
 * The related compiler implementation classes are:
 * <ul>
 * <li>{@link net.sf.jasperreports.groovy.JRGroovyCompiler}</li>
 * <li>{@link net.sf.jasperreports.javascript.JavaScriptCompiler}</li>
 * </ul>
 * <p>
 * Since the most common scenario is to use the Java language for writing report
 * expressions, default implementations of the report compiler interface are shipped with
 * the library and are ready to use. They generate a Java class from the report expressions
 * and store bytecode in the generated {@link net.sf.jasperreports.engine.JasperReport}
 * object for use at report-filling time.</p>
 * <h3>Java Compilers</h3>
 * The Java report compilers come in different flavors depending on the Java compiler used
 * to compile the class that is generated on the fly:
 * <ul>
 * <li>{@link net.sf.jasperreports.jdt.JRJdtCompiler}</li>
 * <li>{@link net.sf.jasperreports.engine.design.JRJdk13Compiler}</li>
 * <li>{@link net.sf.jasperreports.engine.design.JRJavacCompiler}</li>
 * </ul>
 * @see net.sf.jasperreports.engine.JasperReport
 * @see net.sf.jasperreports.engine.design.JasperDesign
 * @see net.sf.jasperreports.engine.xml.JRXmlLoader
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRCompiler
{

	/**
	 * The name of the class to be used for report compilation.
	 * <p>
	 * No default value.
	 * 
	 * @deprecated Replaced by {@link net.sf.jasperreports.engine.design.JRCompiler#COMPILER_PREFIX}.
	 */
	public static final String COMPILER_CLASS = JRPropertiesUtil.PROPERTY_PREFIX + "compiler.class";

	/**
	 * Prefix for properties that map report compilers to expression languages.
	 * <p/>
	 * Properties having this prefix are used to indicate the JRCompiler implementation to be used when compiling
	 * report designs that rely on the expression language specified as property suffix.
	 */
	@Property(
			name = "net.sf.jasperreports.compiler.{language}",
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_2_0_1
			)
	public static final String COMPILER_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "compiler.";
	
	/**
	 * Whether to keep the java file generated when the report is compiled.
	 * <p>
	 * Defaults to <code>false</code>.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_0_0,
			valueType = Boolean.class
			)
	public static final String COMPILER_KEEP_JAVA_FILE = JRPropertiesUtil.PROPERTY_PREFIX + "compiler.keep.java.file";
	
	/**
	 * The temporary directory used by the report compiler. 
	 * <p>
	 * Defaults to <code>System.getProperty("java.io.tmpdir")</code>.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			defaultValue = "System.getProperty(\"java.io.tmpdir\")",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_0_0
			)
	public static final String COMPILER_TEMP_DIR = JRPropertiesUtil.PROPERTY_PREFIX + "compiler.temp.dir";
	
	/**
	 * The classpath used by the report compiler. 
	 * <p>
	 * Defaults to <code>System.getProperty("java.class.path")</code>.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			defaultValue = "System.getProperty(\"java.class.path\")",
			sinceVersion = PropertyConstants.VERSION_1_0_0
			)
	public static final String COMPILER_CLASSPATH = JRPropertiesUtil.PROPERTY_PREFIX + "compiler.classpath";

	
	/**
	 * Compiles a report design.
	 * <p>
	 * The compilation consists of verification of the design, generation of expression evaluators
	 * and construction of a serializable read-only version of the report.
	 * <p>
	 * A report compiler should usually extend {@link JRAbstractCompiler JRAbstractCompiler}.
	 * 
	 * 
	 * @param jasperDesign the report design
	 * @return the compiled report
	 * @throws JRException
	 */
	public JasperReport compileReport(JasperDesign jasperDesign) throws JRException;


	/**
	 * Loads the evaluator for a report's main dataset.
	 * 
	 * @param jasperReport the report
	 * @return the evaluator for the report's main dataset
	 * @throws JRException
	 */
	public JREvaluator loadEvaluator(JasperReport jasperReport) throws JRException;

	
	/**
	 * Loads a expression evaluator class for a dataset of a report.
	 * 
	 * @param jasperReport the report
	 * @param dataset the dataset
	 * @return an instance of the dataset evaluator class
	 * @throws JRException
	 */
	public JREvaluator loadEvaluator(JasperReport jasperReport, JRDataset dataset) throws JRException;

	
	/**
	 * Loads a expression evaluator class for a crosstab of a report.
	 * 
	 * @param jasperReport the report
	 * @param crosstab the crosstab
	 * @return an instance of the dataset evaluator class
	 * @throws JRException
	 */
	public JREvaluator loadEvaluator(JasperReport jasperReport, JRCrosstab crosstab) throws JRException;

}
