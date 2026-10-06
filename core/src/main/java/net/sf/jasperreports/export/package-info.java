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
 * Provides exporter input, exporter output and exporter configurations
 * <p>
 * <h3>Exporter Input</h3>
 * All the input data the exporter might need is supplied by the so-called exporter 
 * input before the exporting process is started. This is because the exporting process 
 * is always invoked by calling the {@link net.sf.jasperreports.export.Exporter#exportReport() exportReport()} method of the 
 * {@link net.sf.jasperreports.export.Exporter} interface, and this method does not 
 * receive any parameters when called. 
 * <p>
 * The input data for an exporter comes in the form of one or more 
 * {@link net.sf.jasperreports.engine.JasperPrint} 
 * objects that must be exported to some other document format, along with other optional 
 * export settings. These 
 * {@link net.sf.jasperreports.engine.JasperPrint} 
 * objects may be already in memory, come from the network through an input stream, or 
 * reside in files on disk. 
 * </p><p>
 * An exporter should be able to handle such a wide range of document sources. In fact, all 
 * the exporter implementations that are shipped inside the library already do this. They all 
 * extend the {@link net.sf.jasperreports.engine.JRAbstractExporter} class, which 
 * holds all the logic for dealing with the source documents that need to be exported inside 
 * its defined {@link net.sf.jasperreports.engine.JRAbstractExporter#setExporterInput(ExporterInput) setExporterInput(ExporterInput)} method. 
 * </p><p>
 * As shown in the method signature, 
 * all we need is an {@link net.sf.jasperreports.export.ExporterInput} instance that holds the necessary 
 * input data. This object must implement the <code>public List&lt;ExporterInputItem&gt; getItems()</code> 
 * method in order to obtain a list of {@link net.sf.jasperreports.export.ExporterInputItem} objects. Each 
 * {@link net.sf.jasperreports.export.ExporterInputItem} object in the list contains a single 
 * {@link net.sf.jasperreports.engine.JasperPrint} object along with its related export 
 * configuration settings. 
 * </p>
 * <h3>Exporter Output</h3>
 * There are at least three types of exporters, depending on the type of output they produce: 
 * <ul>
 * <li>Exporters that export to text- or character-based file formats (HTML, RTF, CSV, TXT, XML ... exporters)</li>
 * <li>Exporters that export to binary file formats (PDF, XLS ... exporters)</li>
 * <li>Exporters that export directly to graphic devices (Graphics2D and Java Print Service exporters)</li>
 * </ul>
 * The first two categories of exporters reuse generic exporter settings for configuring 
 * their output. A text- or character-oriented exporter first looks into its 
 * {@link net.sf.jasperreports.export.ExporterOutput} setting to see whether it needs to 
 * output the text content it produces to:
 * <ul>
 * <li>a supplied <code>java.lang.StringBuffer</code> object</li>
 * <li>a supplied <code>java.io.Writer</code> object</li>
 * <li>a supplied <code>java.io.OutputStream</code> object</li>
 * <li>a <code>java.io.File</code> object</li>
 * <li>a file with a given <code>java.lang.String</code> file name</li>
 * </ul>
 * If none of these OUT parameters have been set, then the exporter throws an exception to inform the caller. 
 * </p><p>
 * A binary exporter uses similar logic to find the output destination for the binary content 
 * it produces. It checks generic exporter output settings in this exact order: <code>java.io.OutputStream</code>, 
 * <code>java.io.File</code>, or file with a given <code>java.lang.String</code> file name. 
 * </p><p>
 * Special exporters that do not produce character or binary output but rather render the 
 * document directly on a target device have special export settings to configure their 
 * output. 
 * </p><p>
 * Output settings for all built-in exporters can be set using the 
 * {@link net.sf.jasperreports.engine.JRAbstractExporter#setExporterOutput(ExporterOutput) setExporterOutput(O exporterOutput)} method inherited from their 
 * {@link net.sf.jasperreports.engine.JRAbstractExporter} parent class. The <code>exporterOutput</code> 
 * argument must be an instance of {@link net.sf.jasperreports.export.ExporterOutput} interface. 
 * </p>
 * <h3>Export Configuration Settings</h3>
 * Other export configuration settings can be communicated to exporters using the 
 * <code>public void {@link net.sf.jasperreports.engine.JRAbstractExporter#setConfiguration(ExporterConfiguration) setConfiguration(C configuration)}</code> and 
 * <code>public void {@link net.sf.jasperreports.engine.JRAbstractExporter#setConfiguration(ReportExportConfiguration) setConfiguration(RC configuration)}</code> 
 * {@link net.sf.jasperreports.engine.JRAbstractExporter} parent class. The first method accepts 
 * an {@link net.sf.jasperreports.export.ExporterConfiguration} argument and applies settings per exporter. 
 * The second one accepts a {@link net.sf.jasperreports.export.ReportExportConfiguration} and applies 
 * settings per each exported report in a batch export.
 * <p>
 * Below are some examples of report configuration settings available in the 
 * {@link net.sf.jasperreports.export.ReportExportConfiguration} class:
 * <ul>
 * <li>the start page index - specifies the 0-based index for the first page in a page range to be exported</li>
 * <li>the end page index - specifies the 0-based index for the last page in a page range to be exported</li>
 * <li>the page index - specifies the 0-based index for a single page to be exported. If present, this setting 
 * overrides both the first page index and the end page index</li>
 * <li>the x offset - to move the page content horizontally, when it doesn't always fit with the printer page margins</li>
 * <li>the y offset - to move the page content vertically, when it doesn't always fit with the printer page margins</li>
 * <li>etc</li>
 * </ul>
 * </p>
 * 
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 * 
 * @see net.sf.jasperreports.engine.JasperPrint
 * @see net.sf.jasperreports.engine.JRAbstractExporter
 */
package net.sf.jasperreports.export;
