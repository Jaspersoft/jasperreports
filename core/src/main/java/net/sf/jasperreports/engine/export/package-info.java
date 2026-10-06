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
 * Provides utility classes for exporting reports to various popular formats such as 
 * PDF, HTML, RTF, CSV, Excel, DOCX, PPTX, ODT, ODS, XML, Text, etc. 
 * <br/>
 * <h3>Exporting Reports</h3>
 * In some application environments, it is useful to transform the JasperReports-generated 
 * documents from the proprietary format into other, more popular formats like PDF, 
 * HTML, RTF, or XLSX. This way, users can view those reports without having to install 
 * special viewers on their systems, which is especially important in the case of documents 
 * sent over a network.
 * <p> 
 * There is a facade class in JasperReports for this type of functionality: 
 * {@link net.sf.jasperreports.engine.JasperExportManager JasperExportManager}; it can be used to obtain 
 * PDF, HTML, or XML content for the documents produced by the report-filling process. 
 * Exporting means taking a {@link net.sf.jasperreports.engine.JasperPrint JasperPrint} object, 
 * which represents a JasperReports document, and transforming it into a different format. 
 * The main reason to export reports into other formats is to allow more people to view 
 * those reports. HTML reports can be viewed by anybody these days, since at least one 
 * browser is available on any system. Viewing JasperReports documents in their 
 * proprietary form would require the installation of special software on the target platform 
 * (at least a Java applet, if not more). 
 * </p><p>
 * With time, more and more output formats will be supported by the JasperReports library. 
 * For the moment, the various exporter implementations shipped with the library produce 
 * Graphics2D, PDF, HTML, RTF, CSV, Excel, DOCX, PPTX, ODT, ODS, XML, pure text and JSON output. 
 * </p><p>
 * The {@link net.sf.jasperreports.engine.JasperExportManager JasperExportManager} class offers 
 * easy access for only the PDF, HTML, and XML implementations, as these have historically been 
 * the most common formats or required the least export configuration. 
 * </p><p>
 * Here's how you can export your report to HTML format using the facade export 
 * manager class: 
 * <pre>
 * JasperExportManager.exportReportToHtmlFile(myReport); </pre>
 * To avoid excessive utility methods, this class was originally written such that the default 
 * settings only offer easy access to the most common export formats. When new exporters 
 * were added to the library, the export manager class was not extended, and users were 
 * encouraged to use the exporter classes directly. Only by doing that could they fully 
 * customize the behavior of that particular exporter using specific exporter parameters. 
 * </p><p>
 * JasperReports tries to expose its exporting functionality in a flexible way and allow users
 * to fully customize how documents are exported, as well as extend the existing
 * functionality if needed. All document exporting in JasperReports is done through a very
 * simple interface called {@link net.sf.jasperreports.export.Exporter}. Every
 * document format that JasperReports currently supports has an implementation of this
 * interface. When a report must be exported, an instance of the desired exporter
 * implementation is created and configured before the export method is called to launch
 * the actual export process on that exporter.
 * </p>
 * <h3>Exporter Input</h3>
 * All the input data the exporter might need is supplied by the so-called exporter 
 * input before the exporting process is started. This is because the exporting process 
 * is always invoked by calling the <code>exportReport()</code> method of the 
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
 * its defined <code>setExporterInput(ExporterInput exporterInput)</code> method. 
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
 * <code>public void setExporterOutput(O exporterOutput)</code> method inherited from their 
 * {@link net.sf.jasperreports.engine.JRAbstractExporter} parent class. The <code>exporterOutput</code> 
 * argument must be an instance of {@link net.sf.jasperreports.export.ExporterOutput} interface. 
 * </p>
 * <h3>Other Export Configuration Settings</h3>
 * Other export configuration settings can be communicated to exporters using the 
 * <code>public void setConfiguration(C configuration)</code> and 
 * <code>public void setConfiguration(RC configuration)</code> inherited from the 
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
 * <h3>Batch Mode Export</h3>
 * The first thing an exporter needs to know is whether it is acting on a single 
 * {@link net.sf.jasperreports.engine.JasperPrint} document or a list with several such generated documents. Exporting 
 * multiple {@link net.sf.jasperreports.engine.JasperPrint} objects to a single resulting document is called batch mode 
 * exporting. 
 * <p>
 * Not all exporters can work in batch mode, but those that do first look into the supplied 
 * exporter input values to see whether a <code>java.util.List</code> of {@link net.sf.jasperreports.engine.JasperPrint} 
 * object has been supplied to them. If so, the exporter 
 * loops through this list of documents and produces a single document from them. 
 * </p><p>
 * If the exporters act on a single document, then they check whether an in-memory {@link net.sf.jasperreports.engine.JasperPrint} 
 * value is supplied to the exporter input data. If no such value is found for this setting, then the input for the 
 * exporter is a single {@link net.sf.jasperreports.engine.JasperPrint} document to be loaded from an input stream, an URL, 
 * a file object, or a file name. If the exporter does not find any of these 
 * settings, then it throws an exception telling the caller that no input source was set for the export process. 
 * </p>
 * <h3>Exporter Filters</h3>
 * When exporting a report to any format, it is possible to filter the elements from the 
 * generated report by skipping elements that do meet a certain condition. This allows 
 * report designers to control what gets exported to each format. In many cases, it's not 
 * desirable to export all report sections and elements to all output formats; for instance, 
 * some visual report elements should only be displayed in output formats that are meant 
 * for on-screen viewing and not in other data-centric output formats. 
 * </p><p>
 * JasperReports comes with two built-in filter implementations that cover the most 
 * frequent use cases. It also defines a set of interfaces that can be used to introduce other 
 * filter implementations. Custom export filter can be written by users to support specific 
 * filtering mechanisms. 
 * </p><p>
 * When exporting a report, a filter can be explicitly specified using an export parameter, or 
 * a filter can be implicitly created based on the properties/export hints present in the report. 
 * </p><p>
 * To explicitly specify an export filter, the exporter configuration setting 
 * <code>public ExporterFilter getExporterFilter()</code> (method in the 
 * {@link net.sf.jasperreports.export.ReportExportConfiguration}  
 * class) should be used to pass a filter object, which would be an instance of a class that 
 * implements the {@link net.sf.jasperreports.engine.export.ExporterFilter} 
 * interface. The filter object can be of one the built-in export filter types, or of a custom 
 * filter implementation type. 
 * </p><p>
 * When no value is found for the export filter setting, the exporter will use a default filter 
 * factory to instantiate a filter that will be used for the export. The default filter factory 
 * class is set via a property named 
 * {@link net.sf.jasperreports.engine.JRAbstractExporter#PROPERTY_DEFAULT_FILTER_FACTORY net.sf.jasperreports.engine.export.default.filter.factory}. 
 * </p><p>
 * The built-in default filter factory implementation calls all registered filter factories and 
 * allows each of them to apply filters on the exporter report. If any of the filters decides to 
 * exclude an element, the element will be excluded from the export process. In most cases 
 * the built-in default filter factory provides the desired behavior. However users can 
 * choose to change it by setting another value for the default filter factory property. 
 * To allow a custom export filter implementation to be used by the implicit export filter 
 * mechanism, one needs to register an export filter factory class with JasperReports. To do 
 * so, a property named {@link net.sf.jasperreports.engine.export.DefaultExporterFilterFactory#PROPERTY_EXPORTER_FILTER_FACTORY_PREFIX net.sf.jasperreports.engine.export.filter.factory.&lt;factory_name&gt;} has 
 * to be included in the <code>jasperreports.properties</code> file (or set at runtime via 
 * {@link net.sf.jasperreports.engine.JRPropertiesUtil JRPropertiesUtil}). The factory name is an arbitrary suffix, and the property value should 
 * be the name of a class that implements 
 * {@link net.sf.jasperreports.engine.export.ExporterFilterFactory ExporterFilterFactory}. The engine 
 * uses the class name to instantiate an export filter factory, therefore the factory class 
 * needs to have an accessible no-argument constructor. 
 * </p><p>
 * Each registered filter factory has the chance of producing a filter every time a report 
 * export occurs. The filter factory receives an object that contains information about the 
 * current export process, including the exporter report and a property prefix that 
 * corresponds to the exporter, and decides based on this information whether it applies to 
 * the current export or not. This would usually involve consulting custom properties of the 
 * exporter report to determine whether the report contains properties that indicate some 
 * filtering criteria. The recommended practice is to make the filter factory recognize 
 * properties that have a specific suffix appended to the exporter property prefix. For 
 * instance, the element key filter factory recognizes properties that have <code>exclude.key</code> 
 * appended after the exporter property prefix. 
 * </p><p>
 * If the exporter factory decides that it applies to the current report, it needs to return a non 
 * null exporter filter, which is an instance of a class that implements 
 * {@link net.sf.jasperreports.engine.export.ExporterFilter}. This filter will be 
 * applied to each element in the generated report and will be able to trigger the exclusion 
 * elements that match a given criteria. 
 * </p><p>
 * Each exporter uses a different property prefix such that different filter criteria can be set 
 * for each exporter. The built-in exporters use property prefixes of the form 
 * <code>net.sf.jasperreports.export.&lt;output_format&gt;</code>. Below are the 
 * property prefixes for the built-in output formats: 
 * <ul>
 * <li><code>Java Print/Graphics2D</code> - <code>net.sf.jasperreports.export.graphics2d</code></li>
 * <li><code>PDF</code> - <code>net.sf.jasperreports.export.pdf</code></li>
 * <li><code>RTF</code> - <code>net.sf.jasperreports.export.rtf</code></li>
 * <li><code>XML</code> - <code>net.sf.jasperreports.export.xml</code></li>
 * <li><code>HTML</code> - <code>net.sf.jasperreports.export.html</code></li>
 * <li><code>Excel</code> - <code>net.sf.jasperreports.export.xls</code></li>
 * <li><code>DOCX</code> - <code>net.sf.jasperreports.export.docx</code></li>
 * <li><code>PPTX</code> - <code>net.sf.jasperreports.export.pptx</code></li>
 * <li><code>ODT</code> - <code>net.sf.jasperreports.export.odt</code></li>
 * <li><code>ODS</code> - <code>net.sf.jasperreports.export.ods</code></li>
 * <li><code>CSV</code> - <code>net.sf.jasperreports.export.csv</code></li>
 * <li><code>Text</code> - <code>net.sf.jasperreports.export.txt</code></li>
 * </ul>
 * In case no filter instance is passed at export time, the exporter searches for some 
 * configuration properties with a given prefix, both at report level (exporter hints) and 
 * globally, in order to decide if a default exporter filter instance should be created on-the-fly 
 * and used internally, when exporting the current document. 
 * </p><p>
 * If created, this default exporter filter will filter out content from the exported document 
 * based on element origin information. Elements present in JasperReports generated 
 * documents keep information about their origin. The origin of an element is defined by its 
 * parent section in the initial report template, and optionally the name of the group and/or 
 * subreport that the element originated from. 
 * </p><p>
 * This built-in filter implementations also exclude from export elements that match a given 
 * element key.
 * </p><p>
 * Element keys are set at report design time and are propagated into generated reports. 
 * Each element in a filled report has the same key as the element from the report template 
 * that generated it. 
 * </p><p>
 * To trigger an element key filter, the report designer needs to define one or more report 
 * properties that start with <code>&lt;exporter_property_prefix&gt;.exclude.key</code>. Each such 
 * property matches a single element key which is to be excluded by the filter. The element 
 * key is given by the property value, or if no value is set for the property, by the property 
 * suffix. 
 * </p>
 * <h3>Monitoring Export Progress</h3>
 * Some applications need to display a progress bar to show the user how much has been 
 * already processed from the supplied document and how much remains to be exported. 
 * <p>
 * All exporters can inform the caller program of their progress through a simple interface 
 * called {@link net.sf.jasperreports.engine.export.JRExportProgressMonitor}. To 
 * monitor the exporter's progress, implement this interface and supply an instance of its 
 * export progress monitor class as the value for the <code>getProgressMonitor()</code> report 
 * exporter configuration setting, which is recognized by almost all built-in exporters. 
 * </p><p>
 * The interface has only one method, <code>afterPageExport()</code>, which gets called by the 
 * exporter on the monitor object after exporting each page from the supplied document. 
 * </p><p>
 * The monitor object can keep track of the number of pages already exported and the total 
 * number of pages to be exported by checking the number of pages in the source 
 * {@link net.sf.jasperreports.engine.JasperPrint} object. 
 * </p>
 * <h3>Grid Exporters</h3>
 * The main goal of the JasperReports library is to produce high-quality, pixel-perfect 
 * documents for printing. The documents it produces can have rich content, and all the 
 * elements on a given page are positioned and sized absolutely. The library tries to keep 
 * the same document quality throughout all supported export formats, but there are some 
 * limitations for each of these formats. All existing exporters fall into one of two 
 * categories, depending on the way the content of the documents they produce can be 
 * structured: 
 * <ul>
 * <li>exporters that target document formats that support free-form page content (Graphics2D, PDF, RTF, XML exporters).</li>
 * <li>exporters that target document formats that only support relative positioning of 
 * elements on a page or a grid-based layout (HTML, Excel, CSV exporters).</li>
 * </ul>
 * Exporters from this second category are also known as <i>grid exporters</i> because the layout 
 * of the documents they produce is formed by a grid. For instance, the HTML exporter will 
 * generate a <code>&lt;table&gt;</code> element for each page and try to put each element on that page 
 * inside a <code>&lt;td&gt;</code> tag. Likewise, the XLS exporter must put each element inside a sheet cell. 
 * </p><p>
 * These grid exporters have an obvious limitation: a built-in algorithm for transforming an 
 * absolutely positioned page layout into a grid-based layout. This algorithm analyzes each 
 * page and tries to build a virtual table in which to place elements so that the overall layout 
 * of the document remains intact. However, since a table cell can contain only a single 
 * element, elements that overlap in the initial absolutely positioned layout will not display 
 * correctly in a grid-based layout. In fact, when two elements overlap, the element behind 
 * will not even appear in the grid-based layout. 
 * </p><p>
 * When the report templates are very complex or agglomerated, passing from absolute
 * positioning to grid or table layout produces very complex tables with many unused rows
 * and columns, in order to make up for the empty space between elements or their special
 * alignment. Here are a few very simple guidelines for obtaining optimized HTML, XLS,
 * or CSV documents when using the built-in JasperReports grid exporters:
 * <ol>
 * <li>Minimize the number of rows and columns in the grid-oriented formats (the number of "cuts"). 
 * To do this, align your report elements as often as you can, both on the 
 * horizontal and the vertical axes, and eliminate the space between elements.</li>
 * <li>Make sure report elements will not overlap when the report is generated. If two 
 * elements share a region, they cannot share the same cell in the resulting grid structure. 
 * Overlapping elements might lead to unexpected results.</li>
 * </ol>
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 * 
 * @see net.sf.jasperreports.engine.JasperExportManager
 * @see net.sf.jasperreports.engine.JasperPrint
 * @see net.sf.jasperreports.engine.JRAbstractExporter
 * @see net.sf.jasperreports.export.Exporter
 * @see net.sf.jasperreports.export.ExporterConfiguration
 * @see net.sf.jasperreports.export.ExporterInput
 * @see net.sf.jasperreports.export.ExporterInputItem
 * @see net.sf.jasperreports.export.ExporterOutput
 * @see net.sf.jasperreports.export.ReportExportConfiguration
 */
package net.sf.jasperreports.engine.export;

