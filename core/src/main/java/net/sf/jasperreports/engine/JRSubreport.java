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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.type.OverflowType;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * An abstract representation of a subreport.
 * <h3>Subreport Overview</h3>
 * Subreports are an important feature of a report-generating tool. They enable you to create
 * more complex reports and simplify the design work. Subreports are very useful when
 * creating master-detail reports or when the structure of a single report is not sufficient to
 * describe the complexity of the desired output document. Subreport elements are introduced by the 
 * <code>&lt;subreport&gt;</code> tag in the JRXML file.
 * <p/>
 * A subreport is in fact a normal report that has been incorporated into another report. You
 * can overlap subreports or make a subreport that contains other subreports, up to any level
 * of nesting. Subreports are compiled and filled just like normal reports. Any report
 * template can be used as a subreport when incorporated into another report template,
 * without anything inside it having to change.
 * <p/>
 * Just like normal report templates, subreport templates are in fact
 * {@link net.sf.jasperreports.engine.JasperReport} objects, which are obtained after
 * compiling a {@link net.sf.jasperreports.engine.design.JasperDesign} object.
 * <p/>
 * Subreport elements have an expression that is evaluated at runtime to obtain the source of the
 * {@link net.sf.jasperreports.engine.JasperReport} object to load.
 * The so-called subreport expression is introduced by the <code>&lt;subreportExpression&gt;</code>
 * tag (see {@link #getExpression()}) and can return values from the following classes:
 * <ul>
 * <li><code>java.lang.String</code> (default)</li>
 * <li><code>java.io.File</code></li>
 * <li><code>java.io.InputStream</code></li>
 * <li><code>java.net.URL</code></li>
 * <li><code>{@link net.sf.jasperreports.engine.JasperReport}</code></li>
 * </ul>
 * <p/>
 * <b>Note:</b> When the subreport expression returns a <code>java.lang.String</code> value, the engine tries to see
 * whether the value represents a URL from which to load the subreport template object. If the value is not a valid
 * URL representation, then the engine will try to locate a file on disk and load the subreport template from it,
 * assuming that the value represents a file name. If no file is found, it will finally assume that the string value
 * represents the location of a classpath resource and will try to load the subreport template from there. Only if all
 * those fail will an exception be thrown.
 * <h3>Caching Subreports</h3>
 * A subreport element can load different subreport templates with every evaluation, giving
 * users great flexibility in shaping their documents.
 * <p/>
 * However, most of the time, the subreport elements on a report are in fact static and their
 * sources do not necessarily change with each new evaluation of the subreport expression.
 * Usually, the subreport templates are loaded from fixed locations: files on disk or static
 * URLs. If the same subreport template is filled multiple times on a report, there is no
 * point in loading the subreport template object from the source file every time it is filled
 * with data. To avoid this, one can instruct the reporting engine to cache the subreport
 * template object. This way, one make sure that the subreport template is loaded from disk
 * or from its particular location only once, after which it will be reused only when it must
 * be filled.
 * <p/>
 * If the <code>isUsingCache</code> attribute (see {@link #getUsingCache()} is set to true, 
 * the reporting engine will try to recognize previously loaded subreport template objects, 
 * using their specified source. For example, it will recognize a subreport object if its 
 * source is a file name that it has already loaded, or if it is the same URL.
 * <p/>
 * This caching functionality is available only for subreport elements that have expressions
 * returning <code>java.lang.String</code> objects as the subreport template source, representing file
 * names, URLs, or classpath resources. That's because the engine uses the subreport
 * source string as the key to recognize that it is the same subreport template that it has
 * cached.
 * <h3>Subreport Parameters</h3>
 * Since subreports are normal reports themselves, they are compiled and filled just like
 * other reports. This means that they also require a data source from which to get the data
 * when they are filled. They can also rely on parameters for additional information to use
 * when being filled.
 * <p/>
 * There are two ways to supply parameter values to a subreport.
 * You can supply a map containing the parameter values, as when filling a normal report
 * with data, using one of the <code>fillReportXXX()</code> methods exposed by the
 * {@link net.sf.jasperreports.engine.JasperFillManager} class.
 * To do this, use the <code>&lt;parametersMapExpression&gt;</code> element 
 * (see {@link #getParametersMapExpression()}), which introduces the
 * expression that will be evaluated to obtain the specified parameters map. This expression
 * should always return a <code>java.util.Map</code> object in which the keys are the parameter
 * names.
 * <p/>
 * In addition to (or instead of) supplying the parameter values in a map, you can supply the
 * parameter values individually, one by one, using a <code>&lt;subreportParameter&gt;</code> 
 * element (see {@link #getParameters()}) for
 * each relevant parameter. To do this, specify the name of the corresponding parameter
 * using the mandatory <code>name</code> attribute and provide an expression that will be evaluated at
 * runtime to obtain the value for that particular parameter, the value that will be supplied
 * to the subreport-filling routines.
 * <p/>
 * Note that you can use both ways to provide subreport parameter values simultaneously.
 * When this happens, the parameter values specified individually, using the
 * <code>&lt;subreportParameter&gt;</code> element, override the parameter values present in the
 * parameters map that correspond to the same subreport parameter. If the map does not
 * contain corresponding parameter values already, the individually specified parameter
 * values are added to the map.
 * <p/>
 * <b>Caution:</b> When you supply the subreport parameter values, be aware that the reporting engine will affect the
 * <code>java.util.Map</code> object it receives, adding the built-in report parameter values that correspond to the
 * subreport. This map is also affected by the individually specified subreport parameter values, as already
 * explained.
 * <p/>
 * To avoid altering the original java.util.Map object that you send, wrap it in a different map before
 * supplying it to the subreport-filling process, as follows:
 * <p/>
 * <code>new HashMap(myOriginalMap)</code>
 * <p/>
 * This way, the original map object remains unaffected and modifications are made to the wrapping map object.
 * This is especially useful when you want to supply to the subreport the same set of parameters that the master
 * report has received and you are using the built-in 
 * {@link net.sf.jasperreports.engine.JRParameter#REPORT_PARAMETERS_MAP REPORT_PARAMETERS_MAP} report parameter of the
 * master report. However, you don't want to affect the value of this built-in parameter, so a clone of the original
 * map needs to be used. Starting with JasperReports 3.0.1, 
 * {@link net.sf.jasperreports.engine.JRParameter#REPORT_PARAMETERS_MAP REPORT_PARAMETERS_MAP} is automatically
 * cloned when it is used as subreport parameters map. 
 * <h3>Subreport Data Source</h3>
 * Subreports require a data source in order to generate their content, just like normal
 * reports.
 * <p/>
 * When filling a report,
 * one must supply either a data source object or a connection object, depending on the
 * report type.
 * <p/>
 * Subreports behave in the same way and expect to receive the same kind of input when
 * they are being filled. One can supply to the subreport either a data source using the
 * <code>&lt;dataSourceExpression&gt;</code> element (see {@link #getDataSourceExpression()}) 
 * or a JDBC connection (see {@link #getConnectionExpression()}) for the engine to execute the
 * subreport's internal SQL query using the <code>&lt;connectionExpression&gt;</code> element. These
 * two XML elements cannot both be present at the same time in a <code>&lt;subreport&gt;</code> element
 * declaration. This is because you cannot supply both a data source and a connection for
 * your subreport.
 * <p/>
 * The report engine expects that the data source expression will return a
 * {@link net.sf.jasperreports.engine.JRDataSource} object or that the connection
 * expression will return a <code>java.sql.Connnection</code> object - whichever is present.
 * <h3>Returning Values from a Subreport</h3>
 * Values calculated by a subreport can be returned to the parent report. More specifically,
 * after a subreport is filled, values of the subreport variables can be either copied or
 * accumulated (using an incrementer) to variables of the caller report.
 * <p/>
 * One or many <code>&lt;returnValue&gt;</code> elements (see {@link #getReturnValues()}) can be used inside 
 * a <code>&lt;subreport&gt;</code> to specify values to be returned from the subreport.
 * <p/>
 * A subreport return value is represented by the {@link net.sf.jasperreports.engine.JRSubreportReturnValue}
 * class that provides information about the subreport variable, master variable, calculation type
 * and incrementer factory.
 * <p/>
 * Note that the value from the subreport is not returned on a column or page break, but
 * only when the subreport filling is done. Also note that the calculation is a two-level
 * process - that is, if the subreport computes a total average and the master accumulates
 * values from the subreports using calculated averages, then the master result will be the
 * average of the subreport averages, not the average of the combined subreport records.
 * <h3>Subreport Runners</h3>
 * By default, JasperReports uses multiple threads to render subreports. There is a separate
 * thread for the master report and one thread for each subreport element found in the report
 * template hierarchy. Each of these threads deals with the filling of its associated report
 * template, which is either a master report or an embedded subreport. Even though
 * multiple threads are involved when subreports are present, those threads do not actually
 * run simultaneously; rather, they pass the control from one another at specific moments,
 * usually when page breaks occur. At any one moment, there is only one report or
 * subreport-filling thread in execution, the others being in wait state.
 * <p/>
 * Using multiple threads was the easiest way to add subreporting functionality in
 * JasperReports. It allowed the reuse of the existing report-filling logic. However, while
 * initially easy to implement, the solution proved to have some drawbacks due to the heavy
 * use of threads. One of the most important limitations was that J2EE containers
 * discourage any use of threads. Also, some operating systems manage threads poorly,
 * which resulted in decreased performance and heavy memory usage.
 * <p/>
 * The alternate solution to this was found in a concept called <i>Java continuations</i>.
 * From among several third-partly libraries implementing this concept that were available 
 * at the time, JasperReports proved to work well with Jakarta Commons Javaflow.
 * <p/>
 * In order to avoid breaking any existing functionality and also allow users to turn off
 * multi-threading when working with subreports in JasperReports, the solution was to
 * isolate subreport-filling logic into a separate abstract class called
 * {@link net.sf.jasperreports.engine.fill.JRSubreportRunnable}, which would have
 * two interchangeable implementations:
 * <ul>
 * <li>{@link net.sf.jasperreports.engine.fill.JRThreadSubreportRunner} - The
 * initial thread-based implementation</li>
 * <li>{@link net.sf.jasperreports.engine.fill.JRContinuationSubreportRunner} - A
 * Javaflow-based implementation</li>
 * </ul>
 * <p/>
 * Switching between the preceding subreport runner implementation is not done through
 * direct instantiation, but rather through a configuration property called
 * {@link net.sf.jasperreports.engine.fill.JRSubreportRunnerFactory#SUBREPORT_RUNNER_FACTORY net.sf.jasperreports.subreport.runner.factory}. This configuration property
 * should point to a
 * {@link net.sf.jasperreports.engine.fill.JRSubreportRunnerFactory}
 * implementation able to produce the needed {@link net.sf.jasperreports.engine.fill.JRSubreportRunnable} 
 * objects at runtime. That could be one of the following two:
 * <ul>
 * <li>{@link net.sf.jasperreports.engine.fill.JRContinuationSubreportRunnerFactory}</li>
 * <li>{@link net.sf.jasperreports.engine.fill.JRThreadSubreportRunnerFactory}</li>
 * </ul>
 * <p/>
 * The default value for the factory configuration property is
 * {@link net.sf.jasperreports.engine.fill.JRThreadSubreportRunnerFactory}, 
 * for backward-compatibility reasons.
 * <p/>
 * <b>Note:</b> A special JasperReports JAR file built using Javaflow byte code instrumentation is available for
 * download with each JasperReports release and should be used when Java Continuations support during
 * subreport filling is needed. In such cases, the Jakarta Commons Javaflow library is required; it can be found in
 * the /lib directory of the JasperReports project distribution package.
 * 
 * @see net.sf.jasperreports.engine.JasperReport
 * @see net.sf.jasperreports.engine.JRSubreportReturnValue
 * @see net.sf.jasperreports.engine.design.JasperDesign
 * @see net.sf.jasperreports.engine.fill.JRContinuationSubreportRunner
 * @see net.sf.jasperreports.engine.fill.JRContinuationSubreportRunnerFactory
 * @see net.sf.jasperreports.engine.fill.JRSubreportRunnable
 * @see net.sf.jasperreports.engine.fill.JRSubreportRunnerFactory
 * @see net.sf.jasperreports.engine.fill.JRThreadSubreportRunner
 * @see net.sf.jasperreports.engine.fill.JRThreadSubreportRunnerFactory
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	"uuid",
	"key",
	"x",
	"y",
	"width",
	"height",
	"forecolor",
	"backcolor",
	"mode",
	"positionType",
	"stretchType",
	"printRepeatedValues",
	"printInFirstWholeBand",
	"printWhenDetailOverflows",
	"printWhenGroupChanges",
	"removeLineWhenBlank",
	"usingCache",
	"runToBottom",
	"overflowType",
	"style",
	"printWhenExpression",
	"styleExpression",
	"property",
	"propertyExpression",
	"parametersMapExpression",
	"parameter",
	"connectionExpression",
	"dataSourceExpression",
	"expression",
	"returnValue"
	})
@JsonTypeName("subreport")
@JsonDeserialize(as = JRDesignSubreport.class)
public interface JRSubreport extends JRElement
{


	/**
	 *
	 */
	public JRExpression getParametersMapExpression();

	/**
	 *
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_parameter)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRSubreportParameter[] getParameters();

	/**
	 *
	 */
	public JRExpression getConnectionExpression();

	/**
	 *
	 */
	public JRExpression getDataSourceExpression();

	/**
	 *
	 */
	public JRExpression getExpression();
	
	/**
	 * Returns the list of subreport copied values.
	 *
	 * @return the list of subreport copied values.
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_returnValue)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRSubreportReturnValue[] getReturnValues();

	

	/**
	 * Indicates if the engine is loading the current subreport from cache.
	 * Implementations of this method return the actual value for the internal flag that was explicitly 
	 * set on this subreport.
	 * @return Boolean.TRUE if the subreport should be loaded from cache, Boolean.FALSE otherwise 
	 * or null in case the flag was never explicitly set on this subreport element
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean getUsingCache();
	
	
	/**
	 * Specifies if the engine should be loading the current subreport from cache. If set to Boolean.TRUE, the reporting engine
	 * will try to recognize previously loaded subreports using their specified source. For example, it will recognize
	 * an subreport if the subreport source is a file name that it has already loaded, or if it is the same URL.
	 * <p>
	 * If set to null, the engine will rely on some default value which depends on the type of the subreport expression.
	 * The cache is turned on by default only for subreports that have <tt>java.lang.String</tt> objects in their expressions.
	 */
	public void setUsingCache(Boolean isUsingCache);
	
	/**
	 * Specifies whether the subreport element will consume the entire vertical
	 * space available on the report page.
	 * 
	 * @return whether the subreport element will consume the entire space down to 
	 * the bottom of the page
	 * @see #setRunToBottom(Boolean) 
	 */
	@JacksonXmlProperty(isAttribute = true)
	public Boolean isRunToBottom();

	/**
	 * Sets the flag that Specifies whether the subreport element will consume the
	 * entire vertical space available on the report page.
	 * 
	 * <p>
	 * This flag should be set to <code>true</code> if the subreport needs to always
	 * print its column and page footers at the bottom of the report page, even when
	 * the subreport data does not stretch to the bottom.
	 * 
	 * <p>
	 * Note that when {@link JRReport#isFloatColumnFooter() isFloatColumnFooter}
	 * is set for the subreport, the column footers will not be printed at the bottom
	 * of the page even if this flag is set.
	 * 
	 * @param runToBottom whether the subreport element will consume the entire
	 * space down to the bottom of the page
	 */
	public void setRunToBottom(Boolean runToBottom);
	
	@JacksonXmlProperty(isAttribute = true)
	public OverflowType getOverflowType();
	
	public void setOverflowType(OverflowType overflowType);

}
