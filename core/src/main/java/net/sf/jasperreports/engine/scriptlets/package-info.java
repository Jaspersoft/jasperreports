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
/**
 * Contains factory interfaces and implementations for global report scriptlets. 
 * <p>
 * All the data displayed in a report comes from the report parameters and report fields. 
 * This data can be processed using the report variables and their expressions. Some 
 * variables are initialized according to their reset type when the report starts, or when a 
 * page or column break is encountered, or when a group changes. Furthermore, variables 
 * are evaluated every time new data is fetched from the data source (for every row). 
 * </p><p>
 * But simple variable expressions cannot always implement complex functionality. This is 
 * where scriptlets come in. Scriptlets are sequences of Java code that are executed every 
 * time a report event occurs. Through scriptlets, users can affect the values stored by the 
 * report variables. Since scriptlets work mainly with report variables, it is important to 
 * have full control over the exact moment the scriptlet is executed. 
 * </p><p>
 * JasperReports allows the execution of custom Java code before or after it initializes the 
 * report variables according to their reset type: <code>Report</code>, <code>Page</code>, 
 * <code>Column</code>, or <code>Group</code>. 
 * </p><p>
 * In order to make use of this functionality, users need only create a scriptlet class, by 
 * extending one of the following two classes: </p>
 * <ul>
 * <li>{@link net.sf.jasperreports.engine.JRAbstractScriptlet}</li>
 * <li>{@link net.sf.jasperreports.engine.JRDefaultScriptlet}</li>
 * </ul>
 * Any number of scriptlets can be specified per report. If no scriplet is specified for a 
 * report, the engine still creates a single {@link net.sf.jasperreports.engine.JRDefaultScriptlet} 
 * instance and registers it with the built-in {@link net.sf.jasperreports.engine.JRParameter#REPORT_SCRIPTLET REPORT_SCRIPTLET} 
 * parameter. 
 * <p>
 * For each scriplet, a name and a class extending the 
 * {@link net.sf.jasperreports.engine.JRAbstractScriptlet} class must 
 * be specified. The class must be available in the classpath at report filling time and must 
 * have an empty constructor, so that the engine can instantiate it on the fly.
 * </p><p> 
 * The only case when the name of the scriptlet is not required is when it is specified using 
 * the <code>scriptletClass</code> attribute of the <code>&lt;jasperReport&gt;</code> element. 
 * The scriptlet instance created with this attribute, acts like the first scriptlet in the 
 * list of scriptlets and has the predefined name <code>REPORT</code>. 
 * </p><p> 
 * When creating a JasperReports scriptlet class, there are several methods that developers 
 * should implement or override, including {@link net.sf.jasperreports.engine.JRAbstractScriptlet#beforeReportInit() beforeReportInit()}, 
 * {@link net.sf.jasperreports.engine.JRAbstractScriptlet#afterReportInit() afterReportInit()}, 
 * {@link net.sf.jasperreports.engine.JRAbstractScriptlet#beforePageInit() beforePageInit()}, 
 * {@link net.sf.jasperreports.engine.JRAbstractScriptlet#afterPageInit() afterPageInit()}, 
 * {@link net.sf.jasperreports.engine.JRAbstractScriptlet#beforeGroupInit(String)}, and 
 * {@link net.sf.jasperreports.engine.JRAbstractScriptlet#afterGroupInit(String)}. The report engine calls these methods at the appropriate time when 
 * filling the report. 
 * </p><p> 
 * For more complex reports containing very complicated report expressions for grouping 
 * or displaying data, create a separate class to which you then make calls from simplified 
 * report expressions. The scriptlet classes are ideal for this. This is because the reporting 
 * engine supplies you with references to the scriptlet objects it creates on the fly using the 
 * built-in <code>&lt;ScriptletName&gt;_SCRIPTLET</code> parameters. 
 * </p><p> 
 * Scriptlet objects are not instantiated by the engine if an instance is already provided as a 
 * value for the corresponding <code>&lt;ScriptletName&gt;_SCRIPTLET</code> parameter, by the caller. 
 * </p><p>
 * Another way to associate scriptlets with reports is by declaring the scriptlets globally so 
 * that they can apply to all reports being filled in the given JasperReports deployment. 
 * This is made easy by the fact that scriptlets can be added to JasperReports as extensions. 
 * The scriptlet extension point is represented by the 
 * {@link net.sf.jasperreports.engine.scriptlets.ScriptletFactory} interface. 
 * </p><p>
 * JasperReports will load all scriptlet factories available through extensions at runtime and 
 * will ask each one of them for the list of scriptlets instances that they want to apply to the 
 * current report that is being run. When asking for the list of scriptlet instances, the engine 
 * gives some context information that the factory could use in order to decide which 
 * scriptlets actually apply to the current report. For example, some scriptlets could look for 
 * certain report properties in the report template to see if they should be triggered or stay 
 * dormant during the current report execution. 
 * </p>
 * <h3>Report Governors</h3>
 * Report governors are just a breed of global scriptlets that enable us to tackle the problem of 
 * infinite loops that sometimes occur during the report generation. 
 * <p>
 * It is known that certain invalid report templates could cause the reporting engine to enter 
 * an infinite loop at runtime, while trying to generate the reports. Such invalid report 
 * templates cannot be detected at design time, because most of the time the conditions for 
 * entering the infinite loops depend on the actual data that is fed into the engine at runtime. 
 * </p><p>
 * We cannot anticipate that certain report templates will cause the engine to 
 * enter an infinite loop, and while within an infinite loop, there is no way for the program 
 * to know it is trapped in such a loop. 
 * </p><p>
 * And here's where report governors become handy, because they can help deciding 
 * whether a certain report has entered an infinite loop and they can stop it, preventing 
 * resource exhaustion for the machine that runs the report. 
 * </p><p>
 * JasperReports is shipped with two simple report governors that would stop a report 
 * execution based on a specified maximum number of pages or a specified timeout 
 * interval. 
 * </p><p>
 * The {@link net.sf.jasperreports.governors.MaxPagesGovernor} is a global scriptlet 
 * that is looking for two configuration properties to decide if it applies or not to the report 
 * currently being run:</p>
 * <pre> 
 *   net.sf.jasperreports.governor.max.pages.enabled=[true|false] 
 *   net.sf.jasperreports.governor.max.pages=[integer]</pre>
 * The {@link net.sf.jasperreports.governors.TimeoutGovernor} is also a global scriptlet 
 * that is looking for the following two configuration properties to decide if it applies or 
 * not: 
 * <pre>
 *   net.sf.jasperreports.governor.timeout.enabled=[true|false]
 *   net.sf.jasperreports.governor.timeout=[milliseconds]</pre>
 * The properties for both governors can be set globally, in the 
 * jasperreports.properties file, or at report level, as custom report properties. This is 
 * useful because different reports can have different estimated size or timeout limits and 
 * also because you might want turn on the governors for all reports, while turning it off for 
 * some, or vice-versa. 
 * 
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 * 
 * @see net.sf.jasperreports.engine.JRAbstractScriptlet
 * @see net.sf.jasperreports.engine.JRDefaultScriptlet
 * @see net.sf.jasperreports.engine.JRParameter#REPORT_SCRIPTLET
 * @see net.sf.jasperreports.governors.MaxPagesGovernor
 * @see net.sf.jasperreports.governors.TimeoutGovernor
 */
package net.sf.jasperreports.engine.scriptlets;
