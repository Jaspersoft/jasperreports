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
 * Contains fill time implementations for the library's main interfaces and the entire 
 * engine used in the filling process (the actual core of JasperReports). 
 * <br/>
 * <h3>Filling Report Templates</h3>
 * The report-filling process is the most important piece of JasperReports library 
 * functionality, because it manipulates sets of data to produce high-quality documents. 
 * This is the main purpose of any reporting tool. 
 * <p>
 * The following things should be supplied to the report-filling process as input: 
 * <ul>
 * <li>a report template (in the compiled form)</li>
 * <li>parameters</li>
 * <li>data source</li>
 * </ul>
 * The output is always a single, final {@link net.sf.jasperreports.engine.JasperPrint} 
 * document ready to be viewed, printed, or exported to other formats. 
 * </p><p>
 * The {@link net.sf.jasperreports.engine.JasperFillManager} class is usually used for 
 * filling a report template with data. This class has various methods that fill report 
 * templates located on disk, come from input streams, or are supplied directly as in-memory 
 * {@link net.sf.jasperreports.engine.JasperReport} objects. After getting the report template, 
 * the report filling methods are calling the related <code>fill(...)</code> method in the 
 * {@link net.sf.jasperreports.engine.fill.JRFiller} class, that actually fills the report.
 * </p><p>
 * The output produced always corresponds to the type of input received. That is, when 
 * receiving a file name for the report template, the generated report is also placed in a file 
 * on disk. When the report template is read from an input stream, the generated report is 
 * written to an output stream, and so forth. 
 * </p><p>
 * The various utility methods for filling the reports may not be sufficient for a particular 
 * application - for example, loading report templates as resources from the classpath and 
 * outputting the generated documents to files on disk at a certain location. 
 * </p><p>
 * In such cases, manually loading the report template objects is to be considered before passing them 
 * to the report-filling routines using the 
 * {@link net.sf.jasperreports.engine.util.JRLoader} utility class. This way, one can 
 * retrieve report template properties, such as the report name, to construct the name of the 
 * resulting document and place it at the desired disk location. 
 * </p><p>
 * The report-filling manager class covers only the most common scenarios. However, you 
 * can always customize the report-filling process using the library's basic functionality just 
 * described.
 * </p>
 * <h3>Generated Reports</h3>
 * The output of the report-filling process is always a pixel-perfect document, ready for 
 * viewing, printing, or exporting to other formats. These documents come in the form of 
 * {@link net.sf.jasperreports.engine.JasperPrint} objects, which are serializable. This 
 * allows the parent application to store them or transfer them over the network if needed. 
 * <p>
 * At the top level, a {@link net.sf.jasperreports.engine.JasperPrint} object contains some document-specific information, 
 * like the name of the document, the page size, and its orientation (portrait or landscape). 
 * Then it points to a collection of page objects 
 * ({@link net.sf.jasperreports.engine.JRPrintPage} instances), each page having a 
 * collection of elements that make up its content. Elements on a page are absolutely 
 * positioned at x and y coordinates within that page and have a specified width and height 
 * in pixels. They can be lines, rectangles, ellipses, images, or text, with various style 
 * settings corresponding to their type. 
 * </p>
 * <h3>Filling Order</h3>
 * JasperReports templates allow the detail section to be smaller than the specified page 
 * width so that the output can be structured into multiple columns, like a newspaper. 
 * <p>
 * When multiple-column report templates are used, the order used for filling those 
 * columns is important. 
 * There are two possible column orders, that can be set in the report template using 
 * the <code>printOrder</code> attribute:
 * <ul>
 * <li><code>Vertical</code> - meaning that they run from top to bottom and then from left to right. 
 * This is the default order. The report filling process is provided by the 
 * {@link net.sf.jasperreports.engine.fill.JRVerticalFiller} class.</li>
 * <li><code>Horizontal</code> - meaning that they first run from left to right and then from top to bottom. In this case 
 * the report is filled using methods in the {@link net.sf.jasperreports.engine.fill.JRHorizontalFiller} class.</li>
 * </ul>
 * When filling report templates horizontally, dynamic text fields inside the detail section 
 * do not stretch to their entire text content, because this might cause misalignment on the 
 * horizontal axis of subsequent detail sections. The detail band actually behaves the same 
 * as the page and column footers, preserving its declared height when horizontal filling is 
 * used. 
 * </p>
 * <h3>Asynchronous Report Filling</h3>
 * JasperReports provides the 
 * {@link net.sf.jasperreports.engine.fill.AsynchronousFillHandle} class to be used 
 * for asynchronous report filling. The main benefit of this method is that the filling process 
 * can be canceled if it takes too much time. This can be useful, for example, in GUI 
 * applications where the user would be able to abort the filling after some time has elapsed 
 * and no result has been yet produced. 
 * <p>
 * When using this method, the filling is started on a new thread. The caller is notified 
 * about the progress of the filling process by way of listeners implementing the 
 * {@link net.sf.jasperreports.engine.fill.AsynchronousFilllListener} interface. The 
 * listeners are notified of the outcome of the filling process, which can be success, failure, 
 * or user cancellation. The handle is used to start the filling process, register listeners, and 
 * cancel the process if wanted. 
 * </p><p>
 * A typical usage of this handle is the following: 
 * <ul>
 * <li>The handle is created by calling the static <code>createHandle()</code> methods in {@link net.sf.jasperreports.engine.fill.AsynchronousFillHandle AsynchronousFillHandle}  
 * that take as arguments the report object, the parameter map, and the data source or the database 
 * connection to be used.</li>
 * <li>One or more listeners are registered with the handle by calling the <code>addListener()</code> 
 * method. In a GUI application, the listener could perform some actions to present to 
 * the user the outcome of the filling process.</li>
 * <li>The filling is started with a call to the <code>startFill()</code> method. In a GUI application, 
 * this could be the result of some user action; the user can also be notified that the 
 * filling has started and is in progress.</li>
 * <li>The filling can be canceled by calling <code>cancellFill()</code> on the handle. In a GUI, 
 * this would be the result of a user action.</li>
 * <li>The listeners are notified when the process finishes. There are three events defined 
 * for the listeners, only one of which will be called, depending on the outcome of the filling:
 * <ul>
 * <li><code>reportFinished()</code> - called when the filling has finished successfully; the 
 * filled report is passed as a parameter. In a GUI, the user would be presented the 
 * filled report or would be able to save/export it.</li>
 * <li><code>reportFillError()</code> - called when the filling ends in error; the exception that 
 * occurred is passed as a parameter.</li>
 * <li><code>reportCancelled()</code> - called when the filling is aborted by the user.</li>
 * </ul>
 * </li>
 * </ul>
 * <h3>Large File Support</h3>
 * If very large datasets are used for report filling, the size of the resulting 
 * {@link net.sf.jasperreports.engine.JasperPrint} object could also be very large and might cause 
 * the JVM to run out of memory.
 * <p>
 * To increase the memory available for the Java application, first use the <code>-Xmx</code> option 
 * when launching the JVM, since the default value for this parameter is fairly small. 
 * However, if you do this with large datasets (for example, containing tens of thousands or 
 * more records and resulting in documents that have thousands or more pages), the JVM 
 * may run out of memory. 
 * </p><p>
 * JasperReports offers a simple solution to the problem by introducing 
 * the report virtualizer. The virtualizer is a simple interface 
 * ({@link net.sf.jasperreports.engine.JRVirtualizer}) that enables the reporting engine 
 * to optimize memory consumption during report filling by removing parts of the 
 * JasperPrint object from memory and storing them on disk or in other temporary 
 * locations. If a report virtualizer is used during filling, the engine keeps only a limited 
 * number of pages from the generated JasperPrint object at a time and serializes all the 
 * other pages to a temporary storage location, usually the file system. 
 * </p><p>
 * Using a report virtualizer is very simple. You supply an instance of the 
 * {@link net.sf.jasperreports.engine.JRVirtualizer} interface as the value for the built-in 
 * {@link net.sf.jasperreports.engine.JRParameter#REPORT_VIRTUALIZER REPORT_VIRTUALIZER} 
 * parameter when filling the report.
 * </p><p>
 * In virtualized form, a generated {@link net.sf.jasperreports.engine.JasperPrint} document still behaves normally and can 
 * be subject to exporting, printing, or viewing processes, and the impact on memory 
 * consumption is minimal even when dealing with very large documents. 
 * </p><p>
 * When produced using a virtualizer (which itself performs partial document serialization 
 * into temporary files), once completed, a {@link net.sf.jasperreports.engine.JasperPrint} document can itself be serialized 
 * normally without any loss of information. During the serialization of a virtualized 
 * {@link net.sf.jasperreports.engine.JasperPrint} object, the program puts back together all the pieces and a single 
 * serialized file is produced. However, because this single file is probably very large, 
 * simple deserialization would not make sense (in fact, it wouldn't be possible, as the JVM 
 * would run out of memory, which is the reason for using virtualization in the first place). 
 * So in order to reload into memory a virtualized document that was serialized to a 
 * permanent storage facility, a report virtualizer is needed. This would be set using a local 
 * thread variable by calling the following: 
 * </p><pre>
 * JRVirtualizationHelper.setThreadVirtualizer(JRVirtualizer virtualizer)</pre>
 * <h3>File virtualizer</h3>
 * The library ships with a ready-to-use implementation of this interface called 
 * {@link net.sf.jasperreports.engine.fill.JRFileVirtualizer}, which stores document 
 * pages on disk during the filling process to free up memory. Once a {@link net.sf.jasperreports.engine.JasperPrint} object 
 * is produced using a report virtualizer, it can be exported to other formats or viewed 
 * directly using the library's built-in viewer component, even though this document is not 
 * fully loaded at any one time. The virtualizer ensures that pages are deserialized and 
 * loaded from their temporary storage location as needed during exporting or display. 
 * A single {@link net.sf.jasperreports.engine.fill.JRFileVirtualizer} instance can be shared across multiple report-filling 
 * processes so that the number of document pages kept in-memory at any one time will be 
 * limited by the virtualizer <code>maxSize</code> property, regardless of the number of reports that are 
 * generated simultaneously. 
 * <p>
 * Because it works with temporary files on disk, the file virtualizer has a built-in 
 * mechanism to remove those files after they are no longer needed (that is, after the 
 * generated document or the virtualizer itself have been disposed of by the JVM). The 
 * <code>cleanup()</code> method exposed by this virtualizer implementation can be also called 
 * manually so that the temporary files are removed from disk right away instead of after 
 * the finalization of the entities involved. 
 * </p><p>
 * To ensure that no virtualization files are left over on disk by the application that uses the 
 * file virtualizer, all these temporary files are registered with the JVM so that they are 
 * deleted automatically when the JVM exits normally. 
 * </p><p>
 * But using <code>File.deleteOnExit()</code> will accumulate JVM process memory on some 
 * virtual machine implementations (see 
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4513817">http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4513817</a>); you should 
 * avoid using this feature in long-running applications by turning it off using the 
 * {@link net.sf.jasperreports.engine.fill.JRFileVirtualizer#PROPERTY_TEMP_FILES_SET_DELETE_ON_EXIT net.sf.jasperreports.virtualizer.files.delete.on.exit} configuration property. 
 * </p>
 * <h3>Swap File Virtualizer</h3>
 * On some platforms, working with a large number of files in a single folder, or even the 
 * file manipulating processes themselves, may have a significant impact on performance or 
 * pose additional problems. This makes the use of the {@link net.sf.jasperreports.engine.fill.JRFileVirtualizer} 
 * implementation less effective. 
 * <p>
 * Fortunately, there is another implementation of a file-based report virtualizer that uses a 
 * single swap file and can also be shared among multiple report-filling processes. Instead 
 * of having one temporary file per virtualized page, we create a single file into which all 
 * virtualized pages are stored to and then retrieved from. 
 * </p><p>
 * This swap file virtualizer implementation is represented by the 
 * {@link net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer} class that is 
 * part of the JasperReports library core functionality, and works in combination with a 
 * {@link net.sf.jasperreports.engine.util.JRSwapFile} instance representing the target 
 * swap file. 
 * </p><p>
 * The {@link net.sf.jasperreports.engine.util.JRSwapFile} instance has to be created and configured prior to being passed to the 
 * swap virtualizer. You can create such an instance by specifying the target directory 
 * where the swap file will be created, the size of the blocks allocated by the swap file, and 
 * the minimum number of blocks by which the swap file will grow when its current size 
 * becomes insufficient. 
 * </p><p>
 * The {@link net.sf.jasperreports.engine.util.JRConcurrentSwapFile} class 
 * represents an enhanced implementation of the JRSwapFile that only works with JRE 
 * version 1.4 or later, because it uses a <code>java.nio.channels.FileChannel</code> to perform 
 * concurrent I/O on the swap file. 
 * </p>
 * <h3>In-Memory GZIP Virtualizer</h3>
 * The {@link net.sf.jasperreports.engine.fill.JRGzipVirtualizer} is a convenient 
 * report virtualizer implementation that does not rely on the file system to temporarily 
 * store unused/virtualized document pages during the report filling. Rather, it optimizes 
 * memory consumption by compressing those pages in-memory using a GZIP algorithm. 
 * Tests indicate that memory consumption during large report-generating processes is 
 * reduced up to a factor of ten when the in-memory GZIP report virtualizer is used. 
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 * 
 * @see net.sf.jasperreports.engine.JasperFillManager
 * @see net.sf.jasperreports.engine.JasperPrint
 * @see net.sf.jasperreports.engine.JasperReport
 * @see net.sf.jasperreports.engine.JRPrintPage
 * @see net.sf.jasperreports.engine.JRVirtualizer
 * @see net.sf.jasperreports.engine.util.JRConcurrentSwapFile
 * @see net.sf.jasperreports.engine.util.JRLoader
 * @see net.sf.jasperreports.engine.util.JRSwapFile
 */
package net.sf.jasperreports.engine.fill;
