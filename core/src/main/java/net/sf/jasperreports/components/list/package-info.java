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
* Contains classes for the built-in List component.
* <p>
* A list component is a report element that iterates on a set of records and renders a cell for 
* each record.
* </p><p>
* The data for a list is obtained via a subdataset defined in the report. The list component 
* includes a <code>&lt;datasetRun&gt;</code> JRXML element (equivalent to a 
* {@link net.sf.jasperreports.engine.JRDatasetRun JRDatasetRun} at the API level), which contains 
* information required to instantiate a report subdataset. A data source object can be sent 
* to the subdataset, or, when the subdataset embeds a query, connection parameters and 
* query parameters can be provided. The subdataset is instantiated when the list 
* component is evaluated and it iterates through the records it produces. 
* </p><p>
* The list cell consists of a report element which will be rendered for each record in the 
* dataset. This element can be a frame which nests several elements. The list cell is 
* evaluated in the context of the subdataset, therefore expressions used by the elements can 
* only refer to parameters, fields and variables which are defined in the subdataset. Also, 
* delayed evaluation times will not work for elements in the list cells because the elements 
* are not evaluated in the context of the main report dataset. 
* <br/>
* The cell width is the same as the defined width of the component element; the height is 
* given by an attribute of the list. The contents of the cell must fit within the width and 
* height of the cell. 
* </p><p>
* When iterating on the dataset records, the list component renders cells vertically in a 
* single column if <code>printOrder</code> is <code>Vertical</code>, or it renders cells side by side, in a single 
* row, if the <code>printOrder</code> attribute is set to <code>Horizontal</code>. For horizontally filled lists, the 
* row breaking behavior is controlled by the <code>ignoreWidth</code> attribute, which either forces a 
* row break when list width limit is reached, or lets the cells stretch beyond the list 
* declared width limit. The list cell height is used as the column's minimum height. If the 
* space remaining at the bottom of the page is less than the cell height, a column/page 
* overflow is triggered and the list continues rendering on a new column/page. 
* <br/>
* The height of the list component element itself can be greater than the list cell height. 
* The list element height is used as minimum height of the entire list, that is, the list will 
* not start rendering unless the space remaining on the page is at least the height of the 
* element. 
*/
package net.sf.jasperreports.components.list;