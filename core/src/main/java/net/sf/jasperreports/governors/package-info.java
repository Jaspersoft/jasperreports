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
 * Provides support for report governors.
 * <br/>
 * <h3>Report Governors</h3>
 * Report governors are just a breed of global scriptlets that enable us to tackle the problem of 
 * infinite loops that sometimes occur during the report generation. 
 * <p>
 * It is known that certain invalid report templates could cause the reporting engine to enter 
 * an infinite loop at runtime, while trying to generate the reports. Such invalid report 
 * templates cannot be detected at design time, because most of the time the conditions for 
 * entering the infinite loops depend on the actual data that is fed into the engine at runtime. 
 * </p><p>
 * There are many reasons for a report to be invalid and cause such infinite loops, but 
 * regardless of the actual cause, infinite loops occur when the reporting engine tries to 
 * layout a page in the generated report and the content of this current page overflows onto 
 * another page. On the second page where the content has overflown, some of the elements 
 * from the previous page need to appear again (either because they represent a page header 
 * or the user has specifically indicated that they should appear again by setting their 
 * <code>isPrintWhenDetailOverflows="true"</code>). Because of these elements appearing again 
 * on the new page, conditions are met again for the page to overflow to another new page. 
 * And so the engine has entered an infinite loop trying to layout new pages in the 
 * generated document and hoping that everything will fit nicely. Unfortunately it does not 
 * fit and there is no way for the program itself to realize it has entered an infinite loop. 
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
 */
package net.sf.jasperreports.governors;
