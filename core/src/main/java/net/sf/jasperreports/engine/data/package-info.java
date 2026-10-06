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
 * Provides various report data source implementations and allows creating custom data sources. 
 * <br/>
 * <h3>Data Sources</h3>
 * When filling the report, the JasperReports engine iterates through the records of the 
 * supplied data source object and generates every section according to the template defined 
 * in the report design. 
 * <p>
 * Normally, the engine expects to receive a 
 * {@link net.sf.jasperreports.engine.JRDataSource JRDataSource} object as the data source of the 
 * report that it has to fill. But another feature lets users supply a JDBC 
 * connection object instead of the usual data source object when the report data is found in 
 * a relational database. 
 * The {@link net.sf.jasperreports.engine.JRDataSource JRDataSource} interface is very simple. It  
 * exposes only two methods: 
 * <pre>
 * public boolean next() throws JRException; 
 * public Object getFieldValue(JRField jrField) throws JRException;</pre> 
 * The <code>next()</code> method is called on the data source object by the reporting engine when 
 * iterating through the data at report-filling time. The second method provides the value 
 * for each report field in the current data source record. 
 * </p><p>
 * It is very important to know that the only way to retrieve data from the data source is by 
 * using the report fields. A data source object is more like a table with columns and rows 
 * containing data in the table cells. The rows of this table are the records through which the 
 * reporting engine iterates when filling the report and each column should be mapped to a 
 * report field, so that we can make use of the data source content in the report expressions. 
 * </p><p>
 * There are several default implementations of the 
 * {@link net.sf.jasperreports.engine.JRDataSource JRDataSource} interface.
 * </p>
 * <h3>JDBC Data Sources</h3>
 * The {@link net.sf.jasperreports.engine.JRResultSetDataSource JRResultSetDataSource} is a very useful 
 * implementation of the {@link net.sf.jasperreports.engine.JRDataSource JRDataSource} interface 
 * because it wraps a <code>java.sql.ResultSet</code> object. Since most reports are generated using 
 * data in relational databases, this is probably the most commonly used implementation for 
 * the data source interface.
 * </p><p>
 * Interestingly, you might end up using this implementation even if you do not instantiate 
 * this class yourself when filling your reports. This is what happens: if you specify the 
 * SQL query in your report template, the reporting engine executes the specified SQL 
 * query and wraps the returned <code>java.sql.ResultSet</code> object in a 
 * {@link net.sf.jasperreports.engine.JRResultSetDataSource JRResultSetDataSource} instance. The only thing 
 * the engine needs to execute the query is a <code>java.sql.Connection</code> object.This connection object 
 * may be supplied instead of supplying the usual data source object.
 * </p><p>
 * In many other cases the SQL query is executed in the parent application, outside 
 * JasperReports. In such a situation, one could manually wrap the 
 * <code>java.sql.ResultSet</code> obtained using an instance of this data source class before calling 
 * the report-filling process. 
 * </p><p>
 * The most important thing to know when using this type of data source is that one must 
 * declare a report field for each column in the result set. The name of the report field must 
 * be the same as the name of the column it maps, as well as the data type. 
 * If this is not possible for some reason, the data source also allows users to retrieve data 
 * from a particular column in the <code>java.sql.ResultSet</code> by index. The report field that 
 * maps the specified column can be named <code>COLUMN_x</code>, where x is the one-based index of 
 * the result set column. 
 * </p><p>
 * For maximum portability, as stated in the JDBC documentation, the values from a 
 * <code>java.sql.ResultSet</code> object should be retrieved from left to right and only once. To 
 * ensure that they work this way, consider declaring the report fields in the same order as 
 * they appear in the SQL query. 
 * </p>
 * <h3>JavaBeans Data Sources</h3>
 * The library provides two data source implementations that can wrap collections or 
 * arrays of JavaBean objects. Both implementations rely on Java reflection to retrieve 
 * report field data from the JavaBean objects wrapped inside the data sources. These data 
 * sources can be used to generate reports using data already available in-memory in the 
 * form of EJBs, Hibernate, JDO objects, or even POJOs. 
 * <p>
 * The {@link net.sf.jasperreports.engine.data.JRBeanArrayDataSource JRBeanArrayDataSource} is for 
 * wrapping an array of JavaBean objects to use for filling a report with data, and the 
 * {@link net.sf.jasperreports.engine.data.JRBeanCollectionDataSource JRBeanCollectionDataSource} is for 
 * wrapping a collection of JavaBeans. Each object inside the array or the collection will be 
 * seen as one record in this type of data source. 
 * </p><p>
 * The mapping between a particular JavaBean property and the corresponding report field 
 * is made by naming conventions. The name of the report field must be the same as the 
 * name of the JavaBean property as specified by the JavaBeans specifications.
 * </p><p>
 * For instance, to retrieve the value of a report field named <code>productDescription</code>, the 
 * program will try to call through reflection a method called <code>getProductDescription()</code> 
 * on the current JavaBean object. 
 * </p><p>
 * The JavaBeans data source implementation contain few methods that are useful in certain cases:
 * <ul>
 * <li><code>getData()</code> - returns the underlying bean collection or array used by the data source.</li>
 * <li><code>getRecordCount()</code> - returns the total number of beans contained in the collection 
 * or array used by the data source.</li>
 * <li><code>cloneDataSource()</code> - returns a copy of data source by creating a fresh data source 
 * that uses the same underlying JavaBeans collection or array. This method can be used when a 
 * master report contains a subreport that needs to iterate on the same JavaBeans collection as the master.</li>
 * </ul>
 * </p>
 * <h3>Map-Based Data Sources</h3>
 * JasperReports library comes with two data source implementations that can wrap arrays or 
 * collections of <code>java.util.Map</code> objects. 
 * <p>
 * The {@link net.sf.jasperreports.engine.data.JRMapArrayDataSource JRMapArrayDataSource} wraps an array 
 * of <code>java.util.Map</code> objects, and 
 * {@link net.sf.jasperreports.engine.data.JRMapCollectionDataSource JRMapCollectionDataSource} can be used 
 * to wrap a <code>java.util.Collection</code> of <code>Map</code> objects. 
 * </p><p>
 * These implementations are useful if the parent application already stores the reporting 
 * data available in-memory as <code>java.util.Map</code> objects. Each <code>Map</code> object in the wrapped array or 
 * collection is considered a virtual record in the data source, and the value of each report 
 * field is extracted from the map using the report field name as the key. 
 * </p><p>
 * Map-based data source implementations contain the same set of utility methods as 
 * JavaBeans data sources: 
 * <ul>
 * <li><code>getData()</code> to access the underlying map collection or array</li>
 * <li><code>getRecordCount()</code> to return the total number of maps/records</li>
 * <li><code>cloneDataSource()</code> - to create a fresh copy of the data source</li>
 * </ul>
 * </p>
 * <h3>TableModel Data Sources</h3>
 * In some Swing-based desktop client applications, the reporting data might already be 
 * available in the form of a <code>javax.swing.table.TableModel</code> implementation used for 
 * rendering <code>javax.swing.JTable</code> components on various forms. JasperReports can 
 * generate reports using this kind of data if a given <code>javax.swing.table.TableModel</code> 
 * object is wrapped in a 
 * {@link net.sf.jasperreports.engine.data.JRTableModelDataSource JRTableModelDataSource} instance before 
 * being passed as the data source for the report-filling process. 
 * <p>
 * There are two ways to use this type of data source. Normally, to retrieve data from it, 
 * one must declare a report field for each column in the 
 * <code>javax.swing.table.TableModel</code> object bearing the same name as the column it 
 * maps. Sometimes it is not possible or desirable to use the column name, however, 
 * because the report field name and columns could still be bound to report fields using 
 * their zero-based index instead of their names. 
 * For instance, if you know that a particular column is the third column in the table model 
 * object (index=2), then you could name the corresponding field "COLUMN_2" and use the 
 * column data without problems. 
 * </p>
 * <h3>XML Data Sources</h3>
 * XML documents can be used as report data sources by means of a data source 
 * implementation. JasperReports features a built-in XML data source implementation 
 * ({@link net.sf.jasperreports.engine.data.JRXmlDataSource JRXmlDataSource}) that is based on DOM 
 * and uses XPath expressions to select data from the XML document. 
 * <p>
 * An XML data source instantiation involves the following inputs: 
 * <ul>
 * <li>An XML document. The parsed document, its location, or its source is provided as 
 * an argument to the data source constructor.</li>
 * <li>An XPath expression to select the node set that corresponds to the data source 
 * record list. The expression is passed to the data source as a constructor argument. 
 * The default XPath expression selects the document node itself; in this case the data 
 * source would produce a single record. The XPath expression is executed when the 
 * data source is instantiated; each item in the resulting node set will generate a 
 * record/row in the data source.</li>
 * <li>For every field in the report/data set, an XPath expression to select the field value 
 * for each record is needed. The field's XPath expression is provided by the {@link net.sf.jasperreports.engine.data.AbstractXmlDataSource#PROPERTY_FIELD_EXPRESSION} 
 * custom field property. The use of the {@link net.sf.jasperreports.engine.JRField#getDescription() field description} to specify the XPath expression 
 * is still supported, but is now discouraged, the above mentioned custom property taking precedence 
 * over the field description. The field's XPath expression is 
 * executed for each record using as a context node the current node from the main 
 * node set.</li>
 * </ul>
 * An XML data source can be used create sub-data sources to be used for subreports or subdatasets. 
 * There are two methods of creating a sub-data source from a parent XML data source:
 * <ul>
 * <li>A sub-data source can be created for a new document that uses the current node as 
 * a root node. An XPath expression can additionally be specified to select the list of 
 * nodes for the sub-data source. The <code>subDataSource()</code> and 
 * <code>subDataSource(String selectExpression)</code> methods should be used to create 
 * sub-data sources in this scenario.</li>
 * <li>The same document can be reused for a new sub-data source, which would specify 
 * a different XPath expression for the main node set. This can be accomplished via 
 * <code>dataSource()</code> and <code>dataSource(String selectExpression)</code> methods calls.</li>
 * </ul>
 * The XML data source provides localization support for both number and date/time
 * values rendered as text in the wrapped XML document.
 * </p><p>
 * In order to parse these text values into <code>java.lang.Number</code> or <code>java.util.Date</code> values
 * according to the declared report field type in the report template, the program needs to
 * know which pattern and locale to use. For date/time report fields, if the text value inside
 * the XML representing time is rendered in a specific time zone, then this time zone needs
 * to be provided to the data source so that it is taken into account when parsing.
 * </p><p>
 * There are four setter methods in the JRXmlDataSource class for specifying:
 * <ul>
 * <li><code>setNumberPattern(java.lang.String)</code> - to use for parsing all text values corresponding 
 * to report fields of type <code>java.lang.Number</code> or any subclass of it</li>
 * <li><code>setDatePattern(java.lang.String)</code> - to use for parsing all date/time values corresponding 
 * to report fields of type <code>java.util.Date</code> or any subclass of it</li>
 * <li><code>setLocale(java.util.Locale)</code> - to use for getting localized number and date parsers</li>
 * <li><code>setTimeZone(java.util.TimeZone)</code> - to use for properly translating time values when 
 * they are not expressed in GMT</li>
 * </ul>
 * Patterns should be non-localized and in accordance with the <code>java.text.DecimalFormat</code> and 
 * <code>java.text.SimpleDateFormat</code> pattern syntax. If specific patterns are not supplied, the defaults for 
 * these two format classes apply.
 * </p><p>
 * XML data sources work by interpreting XPath expressions and selecting nodes and 
 * values from the XML document based on these expressions. This functionality related to 
 * XPath processing has been extracted into a generic service interface called 
 * {@link net.sf.jasperreports.engine.util.xml.JRXPathExecuter JRXPathExecuter}. 
 * </p><p> 
 * The XPath executer implementation used by XML data sources can be configured via a 
 * JasperReports property named {@link net.sf.jasperreports.engine.util.xml.JRXPathExecuterUtils#PROPERTY_XPATH_EXECUTER_FACTORY net.sf.jasperreports.xpath.executer.factory}. 
 * This property gives the name of a XPath executer factory class, which has to implement 
 * the {@link net.sf.jasperreports.engine.util.xml.JRXPathExecuterFactory JRXPathExecuterFactory}. 
 * JasperReports has two XPath executer implementations, one based on 
 * Apache Xalan (<a href="http://xml.apache.org/xalan-j">http://xml.apache.org/xalan-j/</a>), now moved into a separate jasperreports-xalan artifact,
 * and the second based on Jaxen (<a href="http://www.cafeconleche.org/jaxen/">http://www.cafeconleche.org/jaxen/</a>), now used by default.
 * </p><p> 
 * In many cases, the Jaxen XPath executor provides better performance than the 
 * executor that uses Xalan. But if backward compatibility is required, for switching to the Xalan XPath executer, one needs to set the 
 * {@link net.sf.jasperreports.engine.util.xml.JRXPathExecuterUtils#PROPERTY_XPATH_EXECUTER_FACTORY net.sf.jasperreports.xpath.executer.factory} property to 
 * the net.sf.jasperreports.xalan.util.XalanXPathExecuterFactory class, which 
 * is usually done by including the following line in the jasperreports.properties 
 * configuration file: 
 * <pre>
 * net.sf.jasperreports.xpath.executer.factory=net.sf.jasperreports.xalan.util.XalanXPathExecuterFactory
 * </pre> 
 * and make sure the optional jasperreports-xalan-x.x.x.jar is in the classpath of the application.
 * To switch back to Jaxen, one would comment or remove the property line, or explicitly set the property to 
 * {@link net.sf.jasperreports.jaxen.util.xml.JaxenXPathExecuterFactory JaxenXPathExecuterFactory}. 
 * </p>
 * <h3>CSV Data Sources</h3>
 * Sometimes data that users need to fill the report with is found in plain text files, in a 
 * certain format, such as the popular CSV (comma-separated value). 
 * <p>
 * JasperReports provides an implementation for such a data source, by wrapping the CSV 
 * data from a text file into a {@link net.sf.jasperreports.engine.data.JRCsvDataSource JRCsvDataSource}. 
 * The CSV data source usually needs to read a file from disk, or at least from an input 
 * stream. Thus, the {@link net.sf.jasperreports.engine.data.JRCsvDataSource JRCsvDataSource} can be initialized in three ways, depending on 
 * where it gets the data:
 * <ul>
 * <li>a file - <code>new JRCsvDataSource(File)</code></li>
 * <li>an input stream - <code>new JRCsvDataSource(InputStream)</code></li>
 * <li>a reader - <code>new JRCsvDataSource(Reader)</code></li>
 * </ul>
 * The CSV format employs certain formatting rules. Data rows are separated by a record 
 * delimiter (text sequence) and fields inside each row are separated by a field delimiter 
 * (character). Fields containing delimiter characters can be placed inside quotes. If fields 
 * contain quotes themselves, these are duplicated (for example, "John ""Doe""" will be 
 * displayed as John "Doe"). 
 * </p><p>
 * The default values in JasperReports (and also the most common for CSV files) are a 
 * comma for field delimiter and a newline (<code>\n</code>) for record delimiter. Users can override 
 * these default values by calling <code>setFieldDelimiter(char)</code> and 
 * <code>setRecordDelimiter(String)</code>. For example, on some systems, users may need to 
 * replace the default <code>\n</code> delimiter with <code>\r\n</code>. 
 * </p><p>
 * Since CSV does not specify column names, the default convention is to name report 
 * fields <code>COLUMN_x</code> and map each column with the field found at index x in each row (these 
 * indices start with 0). To avoid this situation, users have two possible solutions:
 * <ul>
 * <li>using the <code>setUseFirstRowAsHeader(true)</code> method to force the program to read 
 * the column name from the first line of the CSV file.</li>
 * <li>Providing an array of column names using the <code>setColumnNames(String[])</code> method.</li>
 * </ul>
 * Note that in both cases, the number of provided column names must be at least equal 
 * with the number of actual fields in any record, otherwise an exception will be thrown. 
 * Also, for any column name in the data source, an equivalent report field must exist. 
 * </p><p>
 * Handling data types for fields in CSV data sources is special since the CSV file format 
 * does not provide such information. This matter is solved by trying to match each field in 
 * the data source to its corresponding report field type. For number and date/time fields, 
 * converting text values to <code>java.lang.Number</code> and <code>java.util.Date</code> values respectively 
 * requires parsing using format objects. This is controlled by specifying the date and 
 * number format objects to be used with the 
 * {@link net.sf.jasperreports.engine.data.JRCsvDataSource JRCsvDataSource} instance by calling its 
 * <code>setDateFormat(DateFormat)</code> and <code>setNumberFormat(NumberFormat)</code> methods 
 * before passing it to the report-filling process. 
 * </p><p>
 * The CSV data source implementation also has a JRCsvDataSourceProvider class, 
 * useful for design tools creators. 
 * </p>
 * <h3>Excel Data Sources</h3>
 * When reporting data is in Microsoft Excel files (XLS or XLSX), the 
 * {@link net.sf.jasperreports.poi.data.ExcelDataSource ExcelDataSource} data source
 * implementation can be used to read it and feed it into the report. 
 * <p>
 * The XLS data source uses the Apache POI library to load the Excel workbook and read 
 * from it. Instances of this data source can be created by supplying either an in-memory 
 * workbook object, a file, or an input stream to read the data from. 
 * </p><p>
 * Report-field mapping for this data source implementation is very similar to the CSV data 
 * source field-mapping explained in the previous section. It works on the assumption that 
 * the workbook contains data in a tabular form (rows are records and columns contain 
 * report-field values). 
 * </p>
 * <h3>Empty Data Sources</h3>
 * The {@link net.sf.jasperreports.engine.JREmptyDataSource JREmptyDataSource} class is a very simple 
 * data source implementation that simulates a data source with a given number of virtual 
 * records inside. It is called "empty data source" because even though it has one or more 
 * records inside, all the report fields are null for all the virtual records of the data source. 
 * <p>
 * Such a simple data source implementation is used by the UI tools to offer basic report 
 * preview functionality, or in special report templates, or for testing and debugging 
 * purposes. 
 * </p>
 * <h3>Rewindable Data Sources</h3>
 * The {@link net.sf.jasperreports.engine.JRRewindableDataSource JRRewindableDataSource} is an extension of 
 * the basic {@link net.sf.jasperreports.engine.JRDataSource JRDataSource}interface, to which it adds 
 * the possibility of moving the record pointer back before the first virtual record. It adds 
 * only one method, called <code>moveFirst()</code>, to the interface. 
 * <p>
 * Rewindable data sources are useful when working with subreports. If a subreport is 
 * placed inside a band that is not allowed to split due to the isSplitAllowed="false" 
 * setting and there is not enough space on the current page for the subreport to be rendered, 
 * then the engine has to give up rendering the current band, introduce a page break, and 
 * restart the band and the subreport on the next page. But since the subreport has already 
 * consumed some of the supplied data source records when trying to render the band on 
 * the previous page, it needs to move the record pointer of the data source back before the 
 * first data source for the subreport to restart properly. 
 * </p><p>
 * All built-in data source implementations are rewindable except for the 
 * {@link net.sf.jasperreports.engine.JRResultSetDataSource JRResultSetDataSource}, which does not support 
 * moving the record pointer back. This is a problem only if this data source is used to 
 * manually wrap a <code>java.sql.ResultSet</code> before passing it to the subreport. It is not a 
 * problem if the SQL query resides in the subreport template because the engine will reexecute 
 * it when restarting the subreport on the next page. 
 * </p>
 * <h3>Data Source Provider</h3>
 * To simplify integration with the GUI tools for creating and previewing report templates, 
 * the JasperReports library has published an interface that allows those tools to create and 
 * dispose of data source objects. This is the standard way to plug custom data sources into 
 * a design tool. 
 * <p>
 * This is very useful when the developer wants to preview the reports with the design tool 
 * and use the actual data that the target application will supply at runtime. In order to 
 * achieve this, simply create a custom implementation of the 
 * {@link net.sf.jasperreports.engine.JRDataSourceProvider JRDataSourceProvider} interface and make it 
 * available to the design tool to create the required data sources to use during report preview. 
 * </p><p>
 * The data source provider interface has only a few methods that allow creating and 
 * disposing of data source objects and also methods for listing the available report fields 
 * inside the data source if possible. Knowing which fields will be found in the created data 
 * sources helps you to create report field wizards inside the design tools to simplify report 
 * creation.
 * </p><p> 
 * The library also comes with an abstract implementation of the 
 * {@link net.sf.jasperreports.engine.JRDataSourceProvider JRDataSourceProvider} 
 * interface that can be used as the base class for creating data source provider 
 * implementations that produce JavaBean-based data sources. 
 * </p><p>
 * The {@link net.sf.jasperreports.engine.data.JRAbstractBeanDataSourceProvider JRAbstractBeanDataSourceProvider} 
 * uses Java reflection to provide available report fields names for a given JavaBean class. 
 * </p>
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 */
package net.sf.jasperreports.engine.data;
