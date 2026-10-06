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
 * Contains classes for built-in components.
 * <p/>
 * In addition to the standard report elements, such as text fields, images, charts, subreports 
 * and crosstabs JasperReports has an 
 * increasing number of core components that can be used to add content to generated 
 * reports. Some of them are used for greatly simplifying the creation of complex layouts, 
 * others are used to embed highly specialized visualization packages, such as barcodes and 
 * other types of graphics, or to generate report interactivity, like the Header Toolbar component.
 * <p/>
 * The purpose of this feature is to allow users to extend the functionality of the
 * JasperReports engine by plugging-in custom-made visual components that would take
 * data as input and produce content that is embedded into the resulting documents. Custom
 * made components can be anything from simple barcodes to tables, and professional
 * charting packages.
 * <p/>
 * The custom component support is relying on two other major features of the
 * JasperReports library: extension support and generic elements and you need to be familiar with 
 * them before delving into custom components.
 * <p/>
 * A custom component is a custom report element that has a defined structure and
 * produces some output in generated reports, just like usual report elements do.
 * Just like a normal chart element in a report template will produce an image element in
 * the generated document, or just like a crosstab element present in the report template will
 * produce a series of frames and text fields that makes up a tabular structure in the
 * generated documents, generic custom components declared in a report template could
 * generate any combination of simple elements and generic elements as the output of their
 * built-in calculation and layout logic.
 * <p/>
 * Custom components are supposed to come in bundles. A component bundle is a package
 * comprising of one or several components that share the same XML namespace and
 * schema. For example, a chart component bundle can contain multiple chart components,
 * one for each type of chart (pie chart component, bar chart component and so on).
 * <h3>Custom Components Use Cases Examples</h3>
 * In the following use case example, we consider a JasperReports user that wants to replace the
 * JFreeChart library (bundled with JasperReports by default for the built-in charting
 * support) with some other charting package.
 * <p/>
 * Following are the few steps required to achieve it:
 * <ul>
 * <li>A new component bundle is developed. The bundle can contain several
 * components, that is, one per chart type.</li>
 * <li>An XML schema for the chart components is designed.</li>
 * <li>The schema uses a namespace reserved for the chart component implementation.</li>
 * <li>A Java object model is designed for the chart components.</li>
 * <li>Chart components can (optionally) use report subdatasets to produce data.</li>
 * <li>The component implementation produces
 * {@link net.sf.jasperreports.engine.JRPrintImage} instances when the report is
 * filled. The images can use a custom image renderer
 * ({@link net.sf.jasperreports.renderers.Renderable}) implementation.</li>
 * <li>The developed component code is bundled into a JAR and placed on the application's classpath.</li>
 * <li>Report designers can now embed custom charts into JRXMLs.</li>
 * </ul>
 * <h3>Custom Component Implementation Overview</h3>
 * A typical implementation of a custom component would involve the following:
 * <ul>
 * <li>A <code>jasperreports_extension.properties</code> resource which registers an
 * extension factory that reads a Spring beans file.
 * <br/>
 * The Spring beans file that includes one or more beans of type
 * {@link net.sf.jasperreports.engine.component.ComponentsBundle}. Most of the
 * time the default implementation
 * {@link net.sf.jasperreports.engine.component.DefaultComponentsBundle} is used.</li>
 * <li>A component bundle includes:
 * <li>A set of component types retrieved via {@link net.sf.jasperreports.engine.component.ComponentsBundle#getComponentTypes()} method</li>
 * </ul>
 * </li>
 * <li>One or more component managers,
 * {@link net.sf.jasperreports.engine.component.ComponentManager}
 * implementations, usually instances of
 * {@link net.sf.jasperreports.engine.component.DefaultComponentManager}. A
 * component manager is an entry point through which the handlers for a single
 * component type can be accessed. It contains:
 * <ul>
 * <li>A component compiler
 * ({@link net.sf.jasperreports.engine.component.ComponentCompiler}
 * implementation) that handles a component instance during report compile. It
 * contains methods to collect report expressions from a component instance, to
 * validate such a component and to create a component object that is included in
 * the compiled report.</li>
 * <li>A component converter
 * ({@link net.sf.jasperreports.engine.component.ComponentDesignConverter}
 * implementation) which onverts a component element into a print element that represents a preview
 * of the component.</li>
 * <li>A factory of component fill instances,
 * {@link net.sf.jasperreports.engine.component.ComponentFillFactory}
 * implementation.</li>
 * <li>Such a factory would produce custom
 * {@link net.sf.jasperreports.engine.component.FillComponent} instances. A
 * fill component object is responsible for managing a component at fill time. A
 * typical implementation would evaluate a set of expressions and use the results
 * to create a print element to be included in the generated report.</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * <li>If the component needs to generate an output elements that do not fall into the types
 * built into JasperReports (such as texts/images), the component can produce generic
 * print elements.
 * <br/>
 * To handle such generic elements at export time, custom generic element handler
 * implementations need to be written</li>
 * <li>If the component implementation generates charts, or, more generically, if it needs
 * to collect data from several records in the report dataset or from subdatasets, the
 * component object model will contain classes derived from abstract JasperReports
 * element dataset classes. These classes offer the infrastructure required to collect
 * data from report datasets.
 * <br/>
 * More specifically, the following classes would be extended by the component
 * implementation:
 * <ul>
 * <li>{@link net.sf.jasperreports.engine.design.JRDesignElementDataset}, for
 * the report design/JRXML parsing stage.</li>
 * <li>{@link net.sf.jasperreports.engine.base.JRBaseElementDataset}, to be
 * included in compiled reports.</li>
 * <li>{@link net.sf.jasperreports.engine.fill.JRFillElementDataset}, to be
 * used at fill time.</li>
 * </ul>
 * </li>
 * </ul>
 * <p/>
 * When implementing custom components, the most helpful information can be found in
 * the form of JasperReports API Javadoc and samples. The following samples found in the
 * /demo/samples folder of the JasperReports project distribution package show custom
 * components:
 * <ul>
 * <li><code>Barbecue</code>: A sample that illustrates the use of the Barbecue component 
 * built into JasperReports.</li>
 * <li><code>Barcode4j</code>: A sample that displays barcodes using the Barcode4j component 
 * built into JasperReports.</li>
 * <li><code>xchartcomponent</code>: A chart component implementation that outputs axis charts
 * generated by the XChart library (<a href="http://knowm.org/open-source/xchart/">http://knowm.org/open-source/xchart/</a>).</li>
 * </ul>
 */
package net.sf.jasperreports.components;