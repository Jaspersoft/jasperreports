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
 * Provides support for extension points.
 * <br/>
 * <h3>Extension Support</h3>
 * The JasperReports functionality can be extended in various ways, but is usually done by 
 * implementing public interfaces that the library already exposes. 
 * </p><p>
 * The best example is with report data sources. Report data sources are instances of the 
 * JRDataSource interface. In cases where reporting data is available in a custom format 
 * which is not understood by the built-in data source implementations that the 
 * JasperReports library comes with, the parent application needs to implement that 
 * JRDataSource and thus teach the reporting engine how to iterate through that custom 
 * data and how to get the values of the fields corresponding to each virtual record in that 
 * virtual data table. By implementing the data source interface that JasperReports exposes, 
 * the parent application as thus extended the library's functionality. 
 * </p><p>
 * This is the case with other public interfaces that the library exposes, including, but not 
 * limited to custom components, query executers, hyperlink producers, chart themes, font 
 * bundles, etc. 
 * </p><p>
 * In order to simplify the way JasperReports can be extended and the way those extensions 
 * are deployed, or made available to a given JasperReports deployment, built-in support 
 * for extensions was introduced in the library and then various features of the library 
 * where made extension-friendly and thus transformed into extension points. 
 * </p><p>
 * The extension support of JasperReports allows people to 
 * implement the various interfaces that the library exposes through predefined extension 
 * points and to put together all those implementation classes in single JAR, which can be 
 * then added to the classpath of the application where JasperReports runs, making them all 
 * available to that JasperReports running instance. The only requirement for the 
 * construction of the JAR file is the presence of a 
 * <code>jasperreports_extension.properties</code> file in its root package. This properties file 
 * is used for describing the content of the JAR. 
 * </p><p>
 * Basically, at runtime, the JasperReports engine will read this file and try to understand 
 * what extensions where provided within the JAR, making them available to all interested 
 * extension points.
 * </p>
 * <h3>Extension registry</h3>
 * JasperReports looks for available extensions by loading all the 
 * <code>jasperreports_extension.properties</code> files that it can find in the default package. 
 * <p>
 * Note that many such files can be found, because although they are all having the same 
 * name and being in the default Java package, they can actually sit in different JAR files 
 * that are present in the application's classpath. 
 * </p><p>
 * This special properties file should contain properties in the following format:</p>
 * <pre> 
 * net.sf.jasperreports.extension.registry.factory.&lt;registry_id&gt;=&lt;extension_registry_factory_class&gt;</pre>
 * Each such property gives the name of an extension registry factory class and also names 
 * the extension registry (through the property's suffix). Some extension registry factories 
 * make use of this suffix. For example, they could use this name of the extension registry 
 * to look for other properties in the same file, properties that would help configure the 
 * extension registry factory itself. 
 * </p><p>
 * The extension registry factory should be an implementation of the 
 * {@link net.sf.jasperreports.extensions.ExtensionsRegistryFactory} 
 * interface. It produces instances of the 
 * {@link net.sf.jasperreports.extensions.ExtensionsRegistry} interface, 
 * based on the <code>registry_id</code> value (the suffix mentioned above) and the properties read 
 * from the current <code>jasperreports_extension.properties</code> file. 
 * The signature of the single method that this factory interface exposes is as follows: 
 * </p><p>
 * {@link net.sf.jasperreports.extensions.ExtensionsRegistryFactory#createRegistry(String, net.sf.jasperreports.engine.JRPropertiesMap) createRegistry(String, JRPropertiesMap)}
 * </p><p>
 * The extension registry obtained from the factory is able to return a list of actual 
 * extension point implementations based on extension point class type. 
 * </p><p>
 * For example, in JasperReports, query executers can be added as extensions in the form of 
 * query executer bundles. The predefined query executer extension point in JasperReports 
 * is represented by the {@link net.sf.jasperreports.engine.query.JRQueryExecuterFactoryBundle}
 * interface, from which query 
 * executer implementation are retrieved based on the query language name. The extension 
 * registry implementation is expected to return a list of query executer factory bundles in 
 * this case. 
 * </p><p>
 * Another example of an extension point in JasperReports are the chart themes. They are 
 * also expected to come in bundles, based on their name, so the associated extension point 
 * interface for them is the {@link net.sf.jasperreports.charts.ChartThemeBundle} interface. 
 * In this case, the extension registry implementation is expected to return a list of 
 * chart theme bundle instances. 
 * </p><p>
 * A third example of an extension point is represented by the hyperlink producers. The 
 * associated extension point interface is the 
 * {@link net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory} interface 
 * and thus the extension registry implementation should return a list of those. 
 * </p>
 * <h3>Spring Extension Registry Factory</h3>
 * JasperReports is shipped with a convenience implementation of the 
 * {@link net.sf.jasperreports.extensions.ExtensionsRegistryFactory} 
 * interface that can be used to load extensions from a Spring bean XML file.
 * <p> 
 * This convenience extension registry factory implementation is the 
 * {@link net.sf.jasperreports.spring.SpringExtensionsRegistryFactory} class
 * and works by loading a Spring beans XML file and using beans of specific types as 
 * extensions. 
 * </p><p>
 * The factory requires a property named 
 * {@link net.sf.jasperreports.extensions.DefaultExtensionsRegistry#PROPERTY_REGISTRY_PREFIX net.sf.jasperreports.extension.&lt;registry_id&gt;.spring.beans.resource} to 
 * be present in the properties map passed to the 
 * </p><p>
 * <code>{@link net.sf.jasperreports.extensions.ExtensionsRegistryFactory#createRegistry(String, net.sf.jasperreports.engine.JRPropertiesMap) createRegistry(String, JRPropertiesMap)}</code>
 * </p><p>
 * method. The value of this property must resolve to a resource name 
 * which is loaded from the context class loader, and parsed as a Spring beans XML file. 
 * </p><p>
 * Once the Spring beans XML file is loaded, this factory creates a 
 * {@link net.sf.jasperreports.spring.SpringExtensionsRegistry} instance which
 * will use the bean factory. 
 * This Spring-based extension registry factory is used by the built-in chart themes extensions mechanism. 
 * 
 * <h3>Related Documentation</h3>
 * <a href="http://community.jaspersoft.com/wiki/jasperreports-library-tutorial">JasperReports Tutorial</a>
 */
package net.sf.jasperreports.extensions;
