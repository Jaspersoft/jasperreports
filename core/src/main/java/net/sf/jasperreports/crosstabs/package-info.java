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
 * Contains classes for the Crosstab report element.
 * <p>
 * A crosstab is a special type of report element that summarizes data into a two-dimensional 
 * grid. Crosstabs usually display the joint distribution of two or more 
 * variables in the form of a table in which both rows and columns are dynamic, and in 
 * which the table cells use these variables to display aggregate data such as sums, counts, 
 * minimums, and maximums. 
 * </p><p>
 * Crosstabs are useful because they are easy to understand, can be used with any level of
 * data (nominal, ordinal, interval, or ratio), and provide greater insight than single
 * statistics.
 * </p>
 * <h3>Crosstab Attributes</h3>
 * When a crosstab does not fit entirely on the current page and either a column or row 
 * break occurs, the crosstab is split into multiple pieces and continues on the same page or 
 * overflows onto a new page. By default, the subsequent crosstab pieces redisplay the 
 * column and rows headers, in order to recreate the context for the values displayed inside 
 * the crosstab cells. To suppress this behavior, set the <code>isRepeatColumnHeaders</code> and 
 * <code>isRepeatRowHeaders</code> attributes to false. 
 * <p>
 * When a column break occurs and there is still enough space on the current page, the 
 * subsequent crosstab piece is placed below the previous one at a controlled offset that you 
 * can specify with the <code>columnBreakOffset</code> attribute.
 * </p><p>
 * Crosstabs can either be filled from left to right (the default) or from right to left (mainly 
 * for reports in right-to-left languages). When a crosstab is filled from right to left, the 
 * crosstab contents will start from the right extremity of the crosstab element area and 
 * grow toward the left. The filling direction can be specified using the <code>runDirection</code> 
 * attribute. 
 * </p><p>
 * The declared width of the crosstab element is important because, depending on the 
 * <code>ignoreWidth</code> attribute, the crosstab can either stretch beyond the width limit and fill all 
 * its columns before rendering the next row, or on the contrary, would be forced to stop 
 * rendering columns within the crosstab width limit and continue with the remaining 
 * columns only after all rows have started rendering. 
 * </p>
 * <h3>Crosstab Parameters</h3>
 * Crosstabs use an internal calculation engine for bucketing and preparing the aggregated 
 * data they display. However, sometimes it is useful to pass single values from the 
 * containing report and display them inside the crosstab. This would be the case for some 
 * crosstab header titles.
 * <p> 
 * Any number of crosstab parameters can be declared inside the <code>crosstab</code> element. Each 
 * parameter has its own name and type, as well as its own expression used at runtime to 
 * obtain the value to pass into the crosstab. 
 * </p><p>
 * All parameters must be declared explicitly using the corresponding 
 * <code>&lt;crosstabParameter&gt;</code> tag, even when no expression is associated with the parameter 
 * and all parameter values are passed from the parent report using a single 
 * <code>java.util.Map</code> instance through the <code>&lt;parametersMapExpression&gt;</code> tag. 
 * <br/>
 * Crosstab parameters can be referenced only from crosstab cell expressions using the 
 * <code>$P{}</code> syntax, so they can participate only in the displayed values.
 * </p>
 * <h3>Crosstab Dataset</h3>
 * The crosstab calculation engine aggregates data by iterating through an associated 
 * dataset. This can be the parent report's main dataset or a dataset run that uses one of the 
 * report's declared subdatasets. 
 * <br/>
 * Crosstab dataset resetting, incrementing, and filtering out data work the same as for the main dataset.
 * </p>
 * <h3>Presorted Data</h3>
 * The calculation engine of a crosstab works faster if the data in its associated dataset is 
 * already sorted in accordance with the row and column groups (buckets) declared by the 
 * crosstab, in this order: row buckets, and then column buckets. 
 * <p>
 * If data is not already sorted in the dataset before the iteration starts, then the crosstab 
 * calculation engine can sort it during the data aggregation process using supplied 
 * comparators. However, this will result in some performance loss. 
 * </p>
 * <h3>Data Grouping (Bucketing)</h3>
 * The original dataset data through which the crosstab calculation engine iterates to make 
 * the required data aggregation must be grouped in accordance with the declared rows and 
 * columns of the crosstab. Row and column groups in a crosstab rely on group items called 
 * buckets. A bucket definition consists of the following:
 * <ul>
 * <li>An expression evaluated at runtime that obtains the group items (buckets) in which 
 * to place the aggregated information</li>
 * <li>A comparator to sort the group items (buckets) in case the natural ordering of the 
 * values is not acceptable or even possible</li>
 * </ul>
 * <h3>Row Groups</h3>
 * Crosstabs can have any number of row groups, nested according to the order in which 
 * they were declared.
 * <p>
 * All groups require a unique name, specified using the <code>name</code> attribute. This 
 * name is used to reference the group when declaring the content of its corresponding cells 
 * or when referencing the bucket values of the group to display them in the group headers.
 * </p><p>
 * A row group can have one header for introducing the rows that correspond to each 
 * distinct bucket value and a special header for introducing the totals of the group when the 
 * crosstab ends or when a higher-level row group breaks due to a changing bucket value. 
 * Both header areas are optional. If present, they have a free-form layout. You can place 
 * almost any kind of report element inside, except for subreports, charts, and crosstabs. 
 * </p><p>
 * For each row header, specify the width in pixels using the <code>width</code> attribute. This value is 
 * used by the engine to render the headers that introduce bucket values. For the totals 
 * header, the width comes as a sum of the row headers it wraps. 
 * </p><p>
 * When multiple nested row groups are used in the crosstab, the height of the row headers 
 * for the higher-level groups grows in order to wrap the rows of the nested groups. The 
 * <code>headerPosition</code> attribute determines how the row header content should adapt to the 
 * increased height. 
 * </p><p>
 * The <code>totalPosition</code> attribute controls the appearance of the row that displays 
 * the totals for the row group. Possible values are:
 * <ul>
 * <li><code>Start</code> - the row that displays the totals for the group precedes the rows 
 * corresponding to the group's bucket values</li>
 * <li><code>End</code> - the row that displays the totals for the group is rendered after the 
 * rows corresponding to the group's bucket values</li>
 * <li><code>None</code> - the row that displays the totals for the group is not displayed</li>
 * </ul>
 * When multiple nested row groups are used in the crosstab, the height of the row headers 
 * for the higher-level groups grows in order to wrap the rows of the nested groups. The 
 * <code>headerPosition</code> attribute determines how the row header content should adapt to the 
 * increased height. The possible values for this attribute are as follows: 
 * <ul>
 * <li><code>Top</code> - the content of the row header does not stretch and remains at the top of the header area</li>
 * <li><code>Middle</code> - the content of the row header does not stretch and moves to the middle of the header area</li>
 * <li><code>Bottom</code> - the content of the row header does not stretch and moves to the bottom of the header area</li>
 * <li><code>Stretch</code> - the content of the row header adapts its height proportionally to the newly increased row header height</li>
 * </ul>
 * By default, the row header content stays at the top of the row header area.
 * </p>
 * <h3>Column Groups</h3>
 * A crosstab can contain any number of nested columns. The order of column groups is also important.
 * <p>
 * Column groups are also uniquely identified by the <code>name</code> attribute, typically to reference 
 * the column group (when declaring the content of its corresponding cells) or the bucket 
 * values of the group (for display in the group headers). 
 * </p><p>
 * Any column group can have two optional header regions, one at the top of the bucket 
 * columns and the other at the top of the column displaying the totals of the column group.
 * <br/> 
 * These column header regions have a free-form layout and can contain any kind of report 
 * element, except subreports, charts, and crosstabs. 
 * </p><p>
 * The <code>height</code> attribute specifies the height of the column headers in pixels. The header for
 * the group totals column takes its height from the total height of the column headers it wraps.
 * </p><p>
 * The column headers of crosstabs with multiple nested column groups must adapt their 
 * content to the increased width caused by the nested columns they wrap. There are four 
 * possibilities as specified by the values of the <code>headerPosition</code> attribute: 
 * <ul>
 * <li><code>Left</code> - the content of the column header does not stretch and remains to the left of the header area</li>
 * <li><code>Center</code> - the content of the column header does not stretch and moves to the center of the header area</li>
 * <li><code>Right</code> - the content of the column header does not stretch and moves to the right of the header area</li>
 * <li><code>Stretch</code> - the content of the column header adapts its width proportionally to the newly increased column header width</li>
 * </ul>
 * By default, the column header content stays to the left of the column header area.
 * </p>
 * The <code>totalPosition</code> attribute controls the appearance of the column that displays the 
 * totals for the column group:
 * <ul>
 * <li><code>Start</code> - the column that displays the totals for the group precedes the columns 
 * corresponding to the group's bucket values</li>
 * <li><code>End</code> - the column that displays the totals for the group is rendered after the columns 
 * corresponding to the group's bucket values</li>
 * <li><code>None</code> - the column that displays the totals for the group is not displayed</li>
 * </ul>
 * <h3>Measures</h3>
 * The crosstab calculation engine aggregates data, called a <i>measure</i>, while iterating 
 * through the associated dataset. A measure is typically displayed in the crosstab cells. For 
 * each thing that the crosstab needs for accumulating data during bucketing, a 
 * corresponding measure must be declared. 
 * <p>
 * Crosstab measures are identified by a unique name. The value of the <code>name</code> attribute 
 * of a measure cannot coincide with any row or column group names.
 * </p><p>
 * Just like report variables, crosstab measures have an associated type specified by the class attribute.
 * </p><p>
 * The <code>&lt;measureExpression&gt;</code> tag specifies the expression that produces the values used by the 
 * calculation engine to increment the measure during the data aggregation process.
 * </p><p>
 * Crosstab measures behave just like report variables. They store a value that is 
 * incremented with each iteration through the crosstab dataset. The supported types of 
 * calculations are the same for measure as for report variables, except for the calculation 
 * type <code>System</code>, which does not make sense for measures. 
 * </p><p>
 * Furthermore, custom-defined calculations can be introduced using implementations of 
 * the {@link net.sf.jasperreports.engine.fill.JRExtendedIncrementer JRExtendedIncrementer} interface.
 * </p><p>
 * In addition to the calculations supported by the report variables and mentioned in the 
 * preceding paragraph, one can use crosstabs to calculate and display percentage values for 
 * numerical measurements that have calculation type <code>Sum</code> or <code>Count</code>. To do this, set the 
 * <code>percentageOf</code> attribute to a value other than <code>None</code>. Currently, only percentages of the 
 * grand total of the crosstab are supported. 
 * </p><p>
 * The percentage calculation is a type of calculation that requires at least a second pass 
 * through the data after the totals are calculated. However, there may be other custom made 
 * calculations that require a similar second pass. To enable users to define their own 
 * types of calculations that require a second pass, implement the 
 * {@link net.sf.jasperreports.crosstabs.fill.JRPercentageCalculator JRPercentageCalculator} interface and 
 * associate it with the measure using the <code>percentageCalculatorClass</code> attribute. 
 * </p>
 * <h3>Built-in Crosstab Total Variables</h3>
 * The value of a measure is available inside a crosstab cell through a variable bearing the 
 * same name as the measure. In addition to the current value of the measure, totals of 
 * different levels corresponding to the cell can be accessed through variables named 
 * according to the following scheme: 
 * <ul>
 * <li><code>&lt;Measure&gt;_&lt;Column Group&gt;_ALL</code> - yields the total corresponding to a column 
 * group (that is, the total for all the entries in the column group from the same row)</li>
 * <li><code>&lt;Measure&gt;_&lt;Row Group&gt;_ALL</code> - yields the total corresponding to a row group 
 * (that is, the total for all the entries in the row group from the same column)</li>
 * <li><code>&lt;Measure&gt;_&lt;Row Group&gt;_&lt;Column Group&gt;_ALL</code> - yields the combined total 
 * corresponding to the row and column groups (that is, the total corresponding to all the entries in 
 * both row and column groups)</li>
 * </ul>
 * <p/>
 * For example, if one creates a crosstab having Year and Month column groups, a City
 * row group, and a Sales measure, the following variables can be used:
 * <ul>
 * <li><code>Sales</code>: The current measure value</li>
 * <li><code>Sales_Month_ALL</code>: The total for all the months (one year) corresponding to the current cell</li>
 * <li><code>Sales_Year_ALL</code>: The total for all the years</li>
 * <li><code>Sales_City_ALL</code>: The total for all the cities</li>
 * <li><code>Sales_City_Month_ALL</code>: The total for all the cities and all the months (one year)</li>
 * <li><code>Sales_City_Year_ALL</code>: The grand total</li>
 * </ul>
 * <p/>
 * These variables can be used in both detail and total cells. In total cells, such a variable
 * can be used to access a total corresponding to a higher-level group of the same
 * dimension (for example, in a <code>Month</code> total cell, <code>Sales_Year_ALL</code> can be used as the total
 * for all the years) or a total corresponding to a group on the other dimension (for example,
 * in a <code>Month</code> total cell, <code>Sales_City_ALL</code> can be used as the total for all the cities and one
 * year).
 * <p/>
 * A typical usage of these variables is to show measure values as percentages out of
 * arbitrary level totals.
 * <h3>Crosstab Governor</h3>
 * The crosstab calculation engine performs all calculations in memory. If large 
 * volumes of data are processed, it could be possible to run out of memory due to the large 
 * number of totals and aggregation variables that the engine keeps track of. 
 * </p><p>
 * To avoid the situation in which the JVM raises an OutOfMemory error, and thus triggers 
 * memory reclaim procedures with potentially serious effects on the application's overall 
 * behavior, a crosstab governor has been put in place. This is basically a simple memory 
 * consumption test that the engine performs when filling a crosstab, to check whether a 
 * given memory threshold has been reached. When the limit is reached, the program raises 
 * an exception that can be caught and dealt within the caller program, preventing a more 
 * serious OutOfMemory error from occurring. 
 * </p><p>
 * The governor threshold is given as an integer number representing the maximum number 
 * of cells multiplied by the number of measures in the generated crosstab. It can be set 
 * using the {@link net.sf.jasperreports.crosstabs.fill.calculation.BucketingService#PROPERTY_BUCKET_MEASURE_LIMIT net.sf.jasperreports.crosstab.bucket.measure.limit} configuration 
 * property. This property defaults to -1, meaning that the crosstab governor is disabled by 
 * default. 
 * </p>
 * <h3>Crosstab Cells</h3>
 * A crosstab cell is a rectangular area at the intersection of a crosstab row and a crosstab 
 * column. The cell is a free-form element that can contain any kind of report element 
 * except subreports, charts, and crosstabs. 
 * <p>
 * Crosstab cells are of two types:
 * <ul>
 * <li><i>detail crosstab cell</i> - both the row and the column correspond to bucket values, not totals.</li>
 * <li><i>total crosstab cell</i> - either the row or the column or both correspond to a group total.</li>
 * </ul>
 * The crosstab cell at the intersection of a row bucket value and a column bucket value 
 * (called the detail crosstab cell) can be declared using a <code>&lt;crosstabCell&gt;</code> tag in which 
 * both the <code>rowTotalGroup</code> and <code>columnTotalGroup</code> attributes are empty. For the detail 
 * crosstab cell, both the <code>width</code> and the <code>height</code> attributes are mandatory, specifying the 
 * size of the cell in pixels. 
 * </p><p>
 * Total crosstab cells are those declared using a <code>&lt;crosstabCell&gt;</code> tag for which at least 
 * one of the two <code>rowTotalGroup</code> and <code>columnTotalGroup</code> attributes are present and point 
 * to a row group or a column group, respectively. 
 * </p><p>
 * If the <code>rowTotalGroup</code> attribute is present, then the crosstab cell displays column totals 
 * for the mentioned row group. For such total crosstab cells, only the height is 
 * configurable, and the width is forced by the detail cell. 
 * </p><p>
 * If the <code>columnTotalGroup</code> attribute is present, then the cell displays row totals for the 
 * specified column group. For these cells, only the width is configurable, and the cell 
 * inherits the value of the height attribute from the detail cell. 
 * </p><p>
 * All crosstab cells can have a background color and a border, specified by the 
 * <code>background</code> attribute and the nested <code>&lt;box&gt;</code> tag, respectively. In the resulting document, 
 * each crosstab cell is transformed into a frame element containing all the nested elements 
 * of that cell.
 * </p><p>
 * The optional <code>&lt;crosstabHeaderCell&gt;</code> tag defines the content of the region found at the 
 * upper-left corner of the crosstab where column headers and row headers meet. The size 
 * of this cell is calculated automatically based on the defined row and column widths and 
 * heights. 
 * </p><p>
 * The optional <code>&lt;whenNoDataCell&gt;</code> defines a pseudo-crosstab cell used by the engine to
 * display something when the crosstab does not have any data. The crosstab dataset might
 * not have any virtual records to iterate through, raising the question of what to display in
 * the parent report.
 * <br/>
 * If this pseudo-cell is declared, its content is rendered if the crosstab data is missing,
 * allowing users to view messages such as "No data for the crosstab!" instead of only
 * empty space.
 * </p>
 */
package net.sf.jasperreports.crosstabs;
