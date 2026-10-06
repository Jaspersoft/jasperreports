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
 * Contains the Jasper viewer implementation.
 * <br/>
 * <h3>Viewing Reports</h3>
 * The JasperReports library comes with built-in viewers for viewing the generated reports in its 
 * proprietary format or in the proprietary XML format produced by the internal XML 
 * exporter. It is a Swing-based component. Other Java applications can easily integrate this 
 * component without having to export the documents into more popular formats in order to 
 * be viewed or printed. The {@link net.sf.jasperreports.swing.JRViewer} class represents 
 * this visual component. It can be customized to respond to a particular application's needs 
 * by subclassing it. For example, you could add or remove buttons from the existing 
 * toolbar.
 * <p> 
 * JasperReports also comes with a simple Swing application that uses the visual 
 * component for viewing the reports. This application helps view reports stored on disk in 
 * the JasperReports <code>*.jrprint</code> proprietary format or in the JRPXML format produced by 
 * the default XML exporter. 
 * </p><p>
 * This simple Java Swing application is implemented in the 
 * {@link net.sf.jasperreports.view.JasperViewer} class and is used in almost all the 
 * provided samples for viewing the generated reports. 
 * </p><p>
 * To view a sample report if you have the Ant build tool installed on your system, go to the 
 * desired sample directory and launch the following from the command line:</p>
 * <pre>
 * &gt;ant view
 * or
 * &gt;ant viewXml</pre>
 * <h3>Customizing Viewers</h3>
 * Built-in viewers are represented by the following two classes:
 * <ul>
 * <li>{@link net.sf.jasperreports.view.JasperViewer} - use this class to view generated reports, 
 * either as in-memory objects or serialized objects on disk or even stored in XML format.</li>
 * <li>{@link net.sf.jasperreports.view.JasperDesignViewer} - use this class to preview report templates, 
 * either in JRXML or compiled form.</li>
 * </ul>
 * However, these default viewers might not suit everybody's needs. You may want to 
 * customize them to adapt to certain application requirements. If you need to do this, be 
 * aware that these viewers actually use other, more basic visual components that come 
 * with the JasperReports library.
 * <p> 
 * The report viewers mentioned previously use the visual component represented by the 
 * {@link net.sf.jasperreports.swing.JRViewer} class and its companions. It is in fact a 
 * special <code>javax.swing.JPanel</code> component that is capable of displaying generated 
 * reports. It can be easily incorporated into other Java Swing-based applications or 
 * applets. 
 * </p><p>
 * If the functionality of this basic visual component is not sufficient, one can 
 * adapt it by subclassing it. For example, to create an extra button on the toolbar of this 
 * viewer, extend the component and add that button in the new visual component 
 * obtained by subclassing. 
 * </p><p>
 * Another very important issue is that the default report viewer that comes with the library 
 * does not know how to deal with document hyperlinks that point to external resources. It 
 * deals only with local references by redirecting the viewer to the corresponding local 
 * anchor. 
 * </p><p>
 * However, JasperReports lets users handle the clicks made on document hyperlinks that 
 * point to external documents. To do this, simply implement the 
 * {@link net.sf.jasperreports.view.JRHyperlinkListener} interface and add an instance 
 * of this listener class to register with the viewer component, using the 
 * {@link net.sf.jasperreports.swing.JRViewerPanel#addHyperlinkListener(JRHyperlinkListener) addHyperlinkListener(JRHyperlinkListener)} method exposed by the 
 * {@link net.sf.jasperreports.swing.JRViewerPanel} class. By doing this, you ensure that the 
 * viewer will also call the implementation of the 
 * {@link net.sf.jasperreports.swing.JRViewerPanel#gotoHyperlink(net.sf.jasperreports.engine.JRPrintHyperlink) gotoHyperlink(JRPrintHyperlink)} method in which 
 * the external references are handled. 
 * </p><p>
 * There are two ways of rendering the current document page on the viewer component: 
 * <ul>
 * <li>Creating an in-memory buffered image and displaying that image</li>
 * <li>Rendering the page content directly to the Graphics2D context of the viewer component</li>
 * </ul>
 * The first approach has the advantage of smoother scroll operations, since the page 
 * content is rendered only once as an image, after which the scroll operations occur within 
 * the view port of that image. The drawback is that at high zoom ratios, the image could 
 * become so large that an out-of-memory error might occur. 
 * </p><p>
 * The second approach avoids any potential memory problems at high zoom ratios by 
 * rendering page content directly onto the view component, but this results in a drop of 
 * performance that can be seen when scrolling the page. 
 * </p><p>
 * Switching between the two rendering methods can be controlled by setting the 
 * {@link net.sf.jasperreports.swing.JRViewer#VIEWER_RENDER_BUFFER_MAX_SIZE net.sf.jasperreports.viewer.render.buffer.max.size} configuration property. 
 * The value of this property represents the maximum size (in pixels) of a buffered image 
 * that would be used by the {@link net.sf.jasperreports.swing.JRViewer} component to render 
 * a report page (the first rendering technique). If rendering a report page requires an 
 * image larger than this threshold (that is, image width x image height &gt; maximum size), 
 * then the report page will be rendered directly on the viewer component. 
 * </p><p>
 * By default, this configuration property is set to 0, which means that only direct rendering 
 * is performed, no matter what the zoom ratio. 
 * 
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 * 
 * @see net.sf.jasperreports.view.JRHyperlinkListener
 */
package net.sf.jasperreports.view;
