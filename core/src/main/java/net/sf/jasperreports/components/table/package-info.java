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
* Contains classes for the built-in Table component.
* <br/>
* <h3>The Table Component</h3>
* The majority of reports created by a reporting tool have a tabular structure because, most 
* of the time, the tool has to iterate through a set of records, extracting values from the 
* same fields in each record, then display the values one beside the other, resulting in the 
* table-structured content. You could always design the tables' presentation in 
* JasperReports. However, it did not produce a fully developed table. Text fields and other 
* elements were simply aligned across the different report bands. When they were filled 
* with data at runtime, the output looked like a table even though there is no tabular 
* structure at design time. 
* <p>
* In order to obtain truly dynamic table structures, the reporting engine was  
* extended by introducing the Table component inside report templates. The Table component 
* advances JasperReport's capabilities a few steps when compared to the List component.
* </p><p>
* Just like the List component, the Table component iterates through the records of a 
* dataset. But for each of these records, it renders a different cell for each of its declared 
* columns (not just one cell per record, as the List component does). From this perspective, 
* the List component is like a table with only one column. Furthermore, the Table 
* component allows defining cells for column headers and row headers and also has the 
* concept of grouping, just like a normal report template has. Viewed in this way, the 
* Table component is as powerful as a subreport; its content is structured in columns and 
* cells, and its definition is embedded in the containing report template instead of being a 
* separate file altogether. 
* </p><p>
* The Table component gets its data from a subdataset defined in the report. The 
* component includes a <code>&lt;datasetRun&gt;</code> JRXML element (equivalent to a 
* {@link net.sf.jasperreports.engine.JRDatasetRun JRDatasetRun} at the API level), 
* which contains the information required to instantiate the subdataset. A data source 
* object can be sent to the subdataset, or, when the subdataset embeds a query, connection 
* parameters and query parameters can be sent. The subdataset is instantiated when the 
* Table component is evaluated and it iterates through the records it produces. 
* </p><p>
* From a high-level perspective, a Table component is a list of columns. These columns 
* can be grouped and can form a hierarchy, with adjacent columns sharing a common 
* header and/or footer. When the table is generated at runtime, columns can be 
* skipped/hidden based on a Boolean condition that can be associated with each column. 
* </p><p>
* Within a column, the Table component declares sections for grouping the content, such 
* as a table header and footer, column and row header and footer, and an unlimited number 
* of nested group headers and footers. For each section, the column can specify a cell. 
* Overall, the output of the table is made up of a series of cells that correspond to the 
* content of the table, including, for instance, cells for column and row headers and 
* footers, and headings and summaries for the different levels of groups in the table. The 
* table cells behave much like frame elements, because they can contain several nested 
* elements in a free-form layout. 
* </p><p>
* Each table cell is evaluated in the context of the subdataset, therefore expressions used 
* by the elements can only refer to parameters, fields and variables that are defined in the 
* table's associated subdataset. However, unlike the List component, delayed evaluation 
* times do work for elements in the table cells, with respect to the context of the table 
* dataset. 
* </p>
*/
package net.sf.jasperreports.components.table;