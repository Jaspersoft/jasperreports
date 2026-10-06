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
 * Contains interfaces for chart plots and chart datasets.
 * <br/>
 * <h3>The Built-In Chart Component</h3>
 * JasperReports provides built-in support for charts using the chart component based on the 
 * <a href="http://www.jfree.org">JFreeChart</a> library. It exposes a limited set of visual 
 * properties that the charting package actually supports. This limited set should be 
 * sufficient for the majority of users, and in the future it may be extended to accommodate 
 * community feedback and requests. 
 * <br/>
 * With the built-in chart component, users only have to apply the desired visual settings and 
 * define the expressions that will help the engine build the chart dataset incrementally 
 * during the iteration through the report data source. 
 * <br/>
 * When including and configuring a chart component, three entities are involved:
 * <ul>
 * <li>The overall chart component</li>
 * <li>The chart dataset (which groups chart data-related settings)</li>
 * <li>The chart plot (which groups visual settings related to the way the chart items are rendered)</li>
 * </ul>
 * JasperReports currently supports the following types of charts: Pie, Bar, 
 * XY Bar, Stacked Bar, Line, XY Line, Area, Stacked Area, XY Area, Scatter Plot, 
 * Bubble, Time Series, High-Low-Open-Close, Candlestick and Gantt. 
 * <br/>
 * For each type of chart there is a special JRXML tag that groups various chart settings, 
 * including the dataset and the plot. 
 * <br/>
 * <h3>Chart Properties</h3>
 * All chart types have a common set of properties. Charts are normal report elements, so 
 * they share some of their properties with all the other report elements. Charts are also 
 * box elements and can have hyperlinks associated with them.
 * <br/>
 * Charts resemble text fields and images in that they can postpone their actual rendering 
 * until all the data needed for this operation becomes available to the reporting engine. 
 * Data needed by a chart is gathered by the associated dataset during iteration through the 
 * report data. However, there are situations that require displaying charts at the beginning of a document, 
 * where the necessary data is not yet available, given the way the engine process data and 
 * renders the final document. 
 * <br/>
 * In such cases, the chart evaluation can be postponed using the <code>evaluationTime</code> and 
 * <code>evaluationGroup</code> attributes, which work in the same manner as for text fields and 
 * images. 
 * <br/>
 * Chart-specific settings that apply to all types of charts are grouped under a special 
 * JRXML tag called <code>&lt;chart&gt;</code>:
 * <ul>
 * <li><code>isShowLegend</code>: flag attribute that specifies if the legend is visible on the chart</li>
 * <li><code>customizerClass</code>: attribute that specifies the name of a chart customizer class</li>
 * <li><code>renderType</code>: attribute that specifies the chart rendering type</li>
 * <li><code>theme</code>: attribute that specifies the name of a custom chart theme</li>
 * <li><code>chartTitle</code>: element to customize the chart title</li>
 * <li><code>chartSubtitle</code>: element to customize the chart subtitle</li>
 * <li><code>chartLegend</code>: element to customize the chart legend</li>
 * </ul>
 * <h3>Chart Rendering</h3>
 * In generated reports the output produced by a chart element is an image element. Image elements are drawn using 
 * implementations of the {@link net.sf.jasperreports.renderers.Renderable Renderable} interface.
 * The <code>renderType</code> attribute specifies the renderer implementation that will be used to render the 
 * chart during export or report display. By default, JasperReports recognizes the following values for this attribute: 
 * <ul>
 * <li><code>draw</code>: the chart is drawn directly on the target graphic context using the JFreeChart API</li>
 * <li><code>image</code>: an image is first produced from the chart and this image in turn gets rendered onto the target graphic context</li>
 * <li><code>svg</code>: the chart is transformed into the SVG format and from that format is then rendered onto the target graphic context</li>
 * </ul>
 * <h3>Chart Title, Subtitle and Legend</h3>
 * All charts can have a title, a subtitle and a legend. All of them are optional and can be customized 
 * for color, font, and position.
 * <br/>
 * <h3>Chart Customizer and Chart Themes</h3>
 * To provide full control over chart customization even when using the built-in chart 
 * component, JasperReports can make use of either a chart theme implementation, or of a 
 * chart customizer implementation associated with the chart element, or both. 
 * <br/>
 * Chart themes are a more recent addition to the library and in a way they deprecate the 
 * chart customizers because they bring enhanced capabilities in controlling chart output.
 * <br/>
 * A chart customizer is an implementation of the 
 * {@link net.sf.jasperreports.charts.JRChartCustomizer JRChartCustomizer} interface that is associated 
 * with the chart element using the customizerClass attribute. The easiest way to 
 * implement this interface is by extending the 
 * {@link net.sf.jasperreports.charts.JRAbstractChartCustomizer JRAbstractChartCustomizer} class and thus 
 * having access to parameters, fields, and variables, for more flexible chart customization 
 * based on report data.
 * <br/>
 * Chart customizer only allow modifying the 
 * JFreeChart object that is created externally and passed in to them.
 * <br/>
 * Chart themes give more control over chart output, including 
 * the creation of the JFreeChart object itself. Also, chart themes 
 * affect a whole range of chart types across multiple reports and are not necessarily tied to 
 * a specific chart element within a report. They can even apply globally to all charts within 
 * a given JasperReports deployment, applying a new look and feel to all charts created.
 * <br/>
 * A chart theme can be set globally using a configuration property within the 
 * <code>jasperreports.properties</code> file as follows: 
 * <br/><br/>
 * <code>net.sf.jasperreports.chart.theme=theme_name</code> 
 * <br/><br/>
 * The global chart theme can be overridden at report level using the following report 
 * property in the report template: 
 * <br/><br/>
 * <code>&lt;property name="net.sf.jasperreports.chart.theme" value="theme_name"/&gt;</code> 
 * <br/><br/>
 * If needed, at chart element level, the chart theme is specified using the <code>theme</code> attribute. 
 * <br/>
 * <h3>Chart Datasets</h3>
 * One of the most important considerations when putting a chart element into a report 
 * template is the data mapping. The chart will need to extract its data from whatever data is 
 * available inside the report at runtime. 
 * <br/>
 * The data-oriented component for mapping report data and retrieving chart data at runtime is 
 * the chart dataset. A chart dataset is an entity that somewhat resembles a report variable because it gets 
 * initialized and incremented at specified moments during the report-filling process and 
 * iteration through the report data source. 
 * <br/>
 * Like a report variable, at any moment a chart dataset holds a certain value, which is a complex data 
 * structure that gets incremented and will be used for rendering the chart at the appropriate moment. 
 * <br/>
 * Several types of chart datasets are available in JasperReports because each type of chart 
 * works with certain datasets: Pie, Category, XY, Time Series, Time Period, XYZ, High-Low and Gantt. 
 * <br/>
 * The JasperReports object model uses the 
 * {@link net.sf.jasperreports.charts.JRChartDataset JRChartDataset} interface to define chart datasets.
 * There are implementations of this interface for each of the aforementioned dataset types. 
 * All chart datasets initialize and increment in the same way, and differ only in the type of 
 * data or data series they map. 
 * Common dataset properties are grouped under the <code>&lt;dataset&gt;</code> tag in JRXML format. 
 * <br/>
 * <h3>Chart Plot</h3>
 * The chart plot is the area of the chart on which the axes and items are rendered. Plots 
 * differ based on the type of chart. Some plots specialize in drawing pies; others specialize 
 * in drawing bar items or lines. 
 * <br/>
 * Each type of plot comes with its own set of properties or attributes for customizing the 
 * chart's appearance and behavior. 
 * <br/>
 * There is, however, a subset of plot properties common to all plot types, exposed in the 
 * {@link net.sf.jasperreports.charts.JRChartPlot JRChartPlot} interface. They are grouped
 * under the <code>&lt;plot&gt;</code> tag in JRXML and can be part of any chart/plot definition in the report 
 * template:
 * <dl>
 * <dt><b>Plot Background Color</b></dt>
 * <dd>The <code>backcolor</code> attribute can be used to specify the color used for drawing the plot's area background.</dd>
 * <dt><b>Plot Orientation</b></dt>
 * <dd>Some types of plots can draw their items either vertically or horizontally. For instance, Bar charts can display 
 * either vertical or horizontal bars. Pie charts do not use this setting, but since the majority of charts do have a 
 * concept of orientation, the attribute was included among the common plot settings.</dd>
 * <dt><b>Plot Transparency</b></dt>
 * <dd>When filling up the background with a specified color or drawing items on the target device, the plot can use a 
 * customizable degree of transparency, which you can control using the <code>backgroundAlpha</code> and <code>foregroundAlpha</code> 
 * attributes. These attributes accept numeric values ranging from 0 to 1. The default for both attributes is 1, which means 
 * drawings on the plot area are opaque.</dd>
 * <dt><b>Label Rotation</b></dt>
 * <dd>The text labels on the x axis of a chart can be rotated clockwise or counterclockwise by setting a positive or a 
 * negative numeric value representing the number of degrees to the labelRotation attribute of the plot. This attribute 
 * applies only to charts for which the x axis is not numeric or does not display dates.</dd>
 * <dt><b>Series Colors</b></dt>
 * <dd>To control the color of each series in a chart displaying multiple series, you can use the <code>&lt;seriesColor&gt;</code> 
 * tag available at the chart-plot level. If only one <code>&lt;seriesColor&gt;</code> tag is specified, it becomes the color 
 * of the first series. If more than one <code>&lt;seriesColor&gt;</code> tag is specified, the chart will cycle through the 
 * supplied colors.
 * <br/> 
 * Pie charts do not have multiple series, but they do need different colors for each slice, so the specified colors 
 * will be used. Meter and Thermometer charts do not have series and will ignore any <code>&lt;seriesColor&gt;</code> settings. 
 * <br/> 
 * When used in a chart that is part of a Multiaxis chart, the series colors are treated a bit differently. The default 
 * color series to cycle through is defined in the plot of the Multiaxis chart, and the color series for the nested charts 
 * define series colors for that chart only. This is useful when a Multiaxis chart contains several line charts, each with 
 * one series. By default every line will be the first in its plot and will have the first color defined in the Multiaxis 
 * plot, so every line will be the same color. To solve this, a <code>&lt;seriesColor&gt;</code> can be set for each nested 
 * chart to override the default colors. 
 * <br/> 
 * All series colors are sorted by the value of the <code>seriesOrder</code> attribute and appear in that order when coloring the series.</dd>
 * </dl>
 *  
 */
package net.sf.jasperreports.charts;