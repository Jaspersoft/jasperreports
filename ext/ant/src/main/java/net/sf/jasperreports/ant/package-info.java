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
* Provides Ant task classes for batch-processing report files.
* <br/>
* <h3>Ant Tasks</h3>
* When the number of different report files that one has to deal with in a project is 
* significant, there is a need for automating repeating or re-occurring tasks that are to be 
* performed on those files. 
* <br/>
* From a design point of view, the most notable operation that needs to be performed on 
* report source files after they are finished and ready to be deployed is the report 
* compilation. Sometimes reports need to be decompiled in order to reproduce their 
* corresponding source files and perform additional design work on them, or when the 
* same modification needs to be performed identically on all reports. 
* <br/>
* For these re-occurring tasks, JasperReports provides built-in ready-to-use Ant task 
* definitions, based on the {@link net.sf.jasperreports.ant.JRBaseAntTask JRBaseAntTask} class.
* <br/>
* <h3>Ant Task for Compiling Reports</h3>
* Since report template compilation is more like a design-time job than a runtime one, a 
* custom Ant task has been provided with the library to simplify application development. 
* <br/>
* This Ant task is implemented by the {@link net.sf.jasperreports.ant.JRAntCompileTask JRAntCompileTask} class. Its syntax and behavior are 
* very similar to the built-in <code>&lt;javac&gt;</code> Ant task. 
* <br/>
* The report template compilation task can be declared like this, in a project's <code>build.xml</code> 
* file: 
* <pre>
* &lt;taskdef name="jrc" 
* classname="net.sf.jasperreports.ant.JRAntCompileTask"&gt; 
*   &lt;classpath&gt; 
*     &lt;fileset dir="./lib"&gt; 
*       &lt;include name="** /*.jar"/&gt; 
*     &lt;/fileset&gt; 
*   &lt;/classpath&gt; 
* &lt;/taskdef&gt; 
*</pre> 
* In the preceding example, the <code>lib</code> folder should contain the <code>jasperreports-x.x.x.jar</code> file 
* along with its other required libraries. 
* <br/>
* You can then use this user-defined Ant task to compile multiple JRXML report template 
* files in a single operation by specifying the root directory that contains those files or by 
* selecting them using file patterns. 
* <br/>
* <h3>Attributes of the Report Compilation Task</h3>
* Following is the list of attributes that can be used inside the Ant report compilation task 
* to specify the source files, the destination directory, and other configuration properties:
* <ul>
* <li><code>srcdir</code>: Location of the JRXML report template files to be compiled. Required unless nested <code>&lt;src&gt;</code> elements are present.</li>
* <li><code>destdir</code>: Location to store the compiled report template files (the same as the source directory by default).</li>
* <li><code>compiler</code>: Name of the class that implements the {@link net.sf.jasperreports.engine.design.JRCompiler JRCompiler} interface to be used for compiling the reports (optional).</li>
* <li><code>xmlvalidation</code>: Flag to indicate whether the XML validation should be performed on the source report template files (true by default).</li>
* <li><code>tempdir</code>: Location to store the temporarily generated files (the current working directory by default).</li>
* <li><code>keepjava</code>: Flag to indicate if the temporary Java files generated on the fly should be kept and not deleted automatically (false by default).</li>
* </ul>
* The report template compilation task supports nested <code>&lt;src&gt;</code> and <code>&lt;classpath&gt;</code> 
* elements, just like the Ant <code>&lt;javac&gt;</code> built-in task.
* <br/>
* <h3>Ant Task for Decompiling Reports</h3>
* Sometimes it happens that report templates are to be found only in their compiled form. 
* <br/>
* The source report template files might have been lost and we might have only the 
* compiled report template on which we need to make some modifications. 
* <br/>
* In such cases, the Ant task for decompiling report template files that JasperReports 
* provides becomes very handy. It is implemented by the {@link net.sf.jasperreports.ant.JRAntDecompileTask JRAntDecompileTask} class and its declaration inside a 
* <code>build.xml</code> file should be as follows:
* <pre> 
* &lt;taskdef name="jrdc" 
* classname="net.sf.jasperreports.ant.JRAntDecompileTask"&gt; 
*   &lt;classpath refid="classpath"/&gt; 
* &lt;/taskdef&gt;
* </pre>
* In the above example, the <code>classpath</code> should contain the <code>jasperreports-x.x.x.jar</code> file along with its other required 
* libraries. 
* <br/>
* This task works similarly to the report compilation task, but it does the reverse operation. 
* The files to be decompiled can be specified using the <code>srcdir</code> attribute for their root 
* folder or, for more sophisticated file match patterns, a nested <code>&lt;src&gt;</code> tag. The output 
* folder for the generated files is specified using the <code>destdir</code> attribute. 
* <br/>
* <h3>Ant Task for Updating Reports</h3>
* Although JasperReports always guarantees backward compatibility of report templates 
* when upgrading to a newer version, sometimes tags or attributes in JRXML are 
* deprecated and replaced with newer ones that offer enhanced capabilities. So while the 
* deprecated attributes and tags still work, it is always advisable to use the latest syntax 
* and thus get rid of the deprecation warnings. 
* <br/>
* Upgrading a report template to the latest JasperReports syntax is very easy; all that needs 
* to be done is to load the report and save it again using the API's utility classes, such as 
* the {@link net.sf.jasperreports.engine.xml.JRXmlLoader JRXmlLoader} or {@link net.sf.jasperreports.engine.util.JRLoader JRLoader} 
* and the {@link net.sf.jasperreports.engine.xml.JRXmlWriter JRXmlWriter}. 
* <br/>
* This operation can be automated for any number of files using the Ant report update task 
* provided by the JasperReports library in the 
* {@link net.sf.jasperreports.ant.JRAntUpdateTask JRAntUpdateTask} class, which should have the 
* following definition in a <code>build.xml</code> file: 
* <pre>
* &lt;taskdef name="jru" 
* classname="net.sf.jasperreports.ant.JRAntUpdateTask"&gt; 
*   &lt;classpath refid="classpath"/&gt; 
* &lt;/taskdef&gt;
* </pre> 
* This task is useful also in situations where the same modification needs to be applied on 
* a number of different report files. The required modifications can be performed using the 
* JasperReport API after the report design object has been loaded but before it is saved 
* again. 
* <br/>
* <h3>Related documentation:</h3>
* <ul>
* <li><a href="http://ant.apache.org/manual/index.html" target="_blank">Apache Ant Manual</a></li>
* </ul>
* 
*/
package net.sf.jasperreports.ant;