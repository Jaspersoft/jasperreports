/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignGenericElement;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * A "generic" report element that will produce a 
 * {@link JRGenericPrintElement generic print element} in the generated
 * report.
 * <h3>Generic Elements</h3>
 * Generic report elements are like special placeholders that are put in the report template at
 * report design time, but are dealt with only at export time, when special content is
 * generated for them by the exporter.
 * <p/>
 * A good example of a generic element use case is someone wanting to embed Javascript
 * visualizations in reports exported to HTML format. JasperReports has built-in support for
 * displaying text and images, but there is no built-in element for displaying Javascript visualizations.
 * This person would need to do the following to achieve it:
 * <ul>
 * <li>A new HTML export handler is developed, bundled into a JAR and deployed in the
 * application. The export handler would be registered with JasperReports for a
 * specific generic element type.</li>
 * <li>Optionally, a report custom component is implemented so that report designers
 * would be able to use specialized syntax in JRXMLs. The component implementation would generate
 * a {@link net.sf.jasperreports.engine.JRGenericPrintElement} at fill time.</li>
 * <li>Report designers would include either generic report elements or report
 * components (if implemented) in report templates.</li>
 * <li>At fill time, generic print elements whose type would match the type for which the
 * export handler was registered would be produced. Report expressions embedded in
 * the design report element are evaluated and the result is included in the produced
 * generic element as parameter values.</li>
 * <li>When the report will get exported to HTML, the custom HTML export handler will
 * output HTML snippets that embed Javascript as part of the HTML output.
 * Generic element parameter values would be used to parametrize the resulting Javascript code.</li>
 * </ul>
 * But generic elements can be placeholders for any kind of special content, not necessarily
 * Javascript visualizations. The generic element handler implementation has the freedom to generate
 * any kind of output that can be embedded in the exporter report.
 * <p/>
 * Generic report elements cannot stretch at fill time, they will always produce
 * a print element that has the same size as the design element.
 * <p/>
 * They can be generated in filled reports either by a generic design element, or
 * by custom report components. Generic design elements can be used in simple
 * scenarios in which developing a specialized custom component would not benefit the
 * report design process. A generic design element consists of a generic element type, and a
 * list of element parameters.
 * <h3>Generic Type</h3>
 * The generic element type (see {@link #getGenericType()}) is a key that identifies a class/type 
 * of generic elements which are to be handled uniformly. Such a type is composed of a namespace 
 * (usually an URI associated with an organization or a product) and a name.
 * <p/>
 * The generic type is the key used to resolve an export handler for a generic element
 * present in a generated report. Handlers are registered for a specific generic element
 * types, and when a generic element is exported the extensions registry is queried for a
 * generic element handler that has been registered for the element type.
 * <h3>Generic Element Parameters</h3>
 * A generic element can contain one or more arbitrary element parameters. The parameter
 * values are provided by parameter expression when using generic design elements to
 * generate a generic element.
 * <p/>
 * At fill time, expressions associated with the parameters are evaluated and
 * the result is preserved in the print element.
 * <p/>
 * When the generic element is generated by a custom component element, its
 * implementation is responsible for populating the generated element with parameter
 * values.
 * <p/>
 * The parameters are queried by the export element handlers and the values are used to
 * produce the required output in the exported report.
 * <h3>Generic Element Export Handlers</h3>
 * Such a handler is an object responsible for handling generic print elements of a specific
 * type at export time. They usually come in bundles, which are sets generic element export
 * handlers that share the same type namespace.
 * <p/>
 * Generic element handlers are specific to a report exporter. Currently only the HTML
 * exporter features support for generic elements. A generic element handler that would be
 * used for the HTML exporter would implement the
 * {@link net.sf.jasperreports.engine.export.GenericElementHtmlHandler} interface
 * (which extends the
 * {@link net.sf.jasperreports.engine.export.GenericElementHandler} interface that is
 * common to all export handlers).
 * Handler bundles are deployed as JasperReports extensions, using
 * {@link net.sf.jasperreports.engine.export.GenericElementHandlerBundle} as
 * extension type. JasperReports includes
 * {@link net.sf.jasperreports.engine.export.DefaultElementHandlerBundle}, a
 * convenience handler bundle implementation that wraps a map of handlers per element
 * type and exporter type.
 * 
 * @see net.sf.jasperreports.engine.JRGenericPrintElement
 * @see net.sf.jasperreports.engine.export.GenericElementHandler
 * @see net.sf.jasperreports.engine.export.GenericElementHandlerBundle
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	JRXmlConstants.ATTRIBUTE_uuid,
	JRXmlConstants.ATTRIBUTE_key,
	JRXmlConstants.ATTRIBUTE_x,
	JRXmlConstants.ATTRIBUTE_y,
	JRXmlConstants.ATTRIBUTE_width,
	JRXmlConstants.ATTRIBUTE_height,
	JRXmlConstants.ATTRIBUTE_forecolor,
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_mode,
	JRXmlConstants.ATTRIBUTE_positionType,
	JRXmlConstants.ATTRIBUTE_stretchType,
	"printRepeatedValues",
	"printInFirstWholeBand",
	"printWhenDetailOverflows",
	JRXmlConstants.ATTRIBUTE_printWhenGroupChanges,
	"removeLineWhenBlank",
	JRXmlConstants.ATTRIBUTE_evaluationTime,
	JRXmlConstants.ATTRIBUTE_evaluationGroup,
	JRXmlConstants.ATTRIBUTE_style,
	JRXmlConstants.ELEMENT_property,
	JRXmlConstants.ELEMENT_propertyExpression,
	JRXmlConstants.ELEMENT_styleExpression,
	JRXmlConstants.ELEMENT_printWhenExpression,
	"genericType",
	JRXmlConstants.ELEMENT_parameter
	})
@JsonTypeName("generic")
@JsonDeserialize(as = JRDesignGenericElement.class)
public interface JRGenericElement extends JRElement, JREvaluation
{

	/**
	 * Returns the generic type of this element.
	 * This type will be propagated to the generated print element, and used to
	 * resolve export handler for the print element. 
	 * 
	 * @return the generic type of this element
	 */
	JRGenericElementType getGenericType();
	
	/**
	 * Returns the list of parameters of this element.
	 * 
	 * @return the list of parameters 
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_parameter)
	@JacksonXmlElementWrapper(useWrapping = false)
	JRGenericElementParameter[] getParameters();
	
}
