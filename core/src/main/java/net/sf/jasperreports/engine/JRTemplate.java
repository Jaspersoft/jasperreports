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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * A template that can be used by report.
 * <p/>
 * Templates contain report styles that can be used by reports once the template is included in the report.
 * This allows styles to be externalized and reused across several reports.
 * <p/>
 * A template can in its turn include other templates, see {@link #getIncludedTemplates() getIncludedTemplates()}.
 * <p/>
 * <h3>Style Templates</h3>
 * Report styles can also be defined in external style template files that are referenced by
 * report templates. This allows report designers to define in a single place a common look
 * for a set of reports.
 * <p/>
 * A style template is an XML file that contains one or more style definitions. A template
 * can include references to other style template files, hence one can organize a style library
 * as a hierarchical set of style template files.
 * <p/>
 * Style template files use by convention the *.jrtx extension, but this is not mandatory.
 * <p/>
 * The <code>&lt;jasperTemplate&gt;</code> element is the root of a style template file. The 
 * <code>&lt;template&gt;</code> element is used to include references to other template files; 
 * the contents of this element is interpreted as the location of the referred template file.
 * <p/>
 * The <code>&lt;style&gt;</code> element is identical to the element with the same name from report design
 * templates (JRXML files), with the exception that a style in a style template cannot
 * contain conditional styles. This limitation is caused by the fact that conditional styles
 * involve report expressions, and expressions can only be interpreted in the context of a
 * single report definition. Each style must specify a name, and the style names have to be
 * unique inside a style template.
 * <p/>
 * A report can use style templates by explicitly referring them in its definition. References
 * to a style templates are included in JRXML reports as <code>&lt;template&gt;</code> elements. Such an
 * element contains an expression that is resolved at fill time to a style template instance.
 * <p/>
 * The template expression can only use constants/literals and report parameters. Variables
 * and fields cannot be used because the template expressions are evaluated before the
 * report calculation engine is initialized. If the template expression evaluates to null, the
 * engine ignores the template reference.
 * <p/>
 * Style template locations are interpreted in the same manner as image or subreport
 * locations, that is, the engine attempts to load the location as an URL, a disk file or a
 * classpath resource.
 * <p/>
 * Styles from included template files can be used in the report just as local styles, that is,
 * they can be referred by report elements or used as base styles for other (derived) styles.
 * The style templates are loaded at report fill time, and style name references are resolved
 * once all the templates have been loaded. If a style name reference cannot be resolved,
 * that is, no style can be found in the loaded templates for a style name used in the report,
 * the fill process will fail with an exception that specifies which style could not be
 * resolved.
 * <p/>
 * When loading style templates and resolving style names to styles, a tree/graph of style
 * templates is created, the top of the tree being the set of styles defined in the report. On
 * this tree, style name references are resolved to the last style that matches the name in a
 * depth-first traversal. For instance, if a report contains a style named Normal and the
 * report also includes a style template that contains a style named Normal, an element
 * referring the Normal style would use the style defined in the report. The same principle
 * applies to the way the default style is determined: the last style which is marked as
 * default is used as report default style.
 * <p/>
 * The following example illustrates how style templates can be referenced in a JRXML
 * report definition:
 * <pre>
 * &lt;jasperReport ...&gt;
 *   &lt;template&gt;"report_styles.jrtx"&lt;/template&gt;
 *   &lt;!-- parameters can be used in style template expressions --&gt;
 *   &lt;template&gt;$P{BaseTemplateLocation} + "report_styles.jrtx"&lt;/template&gt;
 *   &lt;template class="java.net.URL"&gt;$P{StyleTemplateURL}&lt;/template&gt;
 *   &lt;parameter name="BaseTemplateLocation"/&gt;
 *   &lt;parameter name="StyleTemplateURL" class="java.net.URL"/&gt;
 *   ...
 * </pre>
 * At the API level, style templates are represented by
 * {@link net.sf.jasperreports.engine.JRTemplate} instances. A reference to a
 * style template in a report is represented by a
 * {@link net.sf.jasperreports.engine.JRReportTemplate} instance. Such references can
 * be added to a {@link net.sf.jasperreports.engine.design.JasperDesign} object 
 * via the {@link net.sf.jasperreports.engine.design.JasperDesign#addTemplate(JRReportTemplate) addTemplate(JRReportTemplate)} method, and the list of
 * template references can be obtained by calling the {@link net.sf.jasperreports.engine.design.JasperDesign#getTemplates() getTemplates()} method.
 * <p/>
 * In more complex cases, style templates can be injected into a report by using the built-in
 * {@link net.sf.jasperreports.engine.JRParameter#REPORT_TEMPLATES REPORT_TEMPLATES} report parameter. 
 * This parameter expects as value a collection (as
 * in <code>java.util.Collection</code>) of {@link net.sf.jasperreports.engine.JRTemplate}
 * instances. The user report fill code can either load style templates on its own from
 * template XML files using
 * {@link net.sf.jasperreports.engine.xml.JRXmlTemplateLoader}, or instantiate style
 * template objects by any other means, and pass the dynamically loaded style template list
 * to the report fill process using the {@link net.sf.jasperreports.engine.JRParameter#REPORT_TEMPLATES REPORT_TEMPLATES} parameter.
 * <p/>
 * Style templates passed to a report via the {@link net.sf.jasperreports.engine.JRParameter#REPORT_TEMPLATES REPORT_TEMPLATES} 
 * parameter are placed after
 * the templates referenced in the report definition, hence styles from the parameter
 * templates override styles with identical names from the statically referenced templates.
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRReport#getTemplates()
 * @see JRParameter#REPORT_TEMPLATES
 * @see JRStyleContainer#getStyleNameReference()
 */
@JsonRootName("jasperTemplate")
@JsonDeserialize(as = JRSimpleTemplate.class)
public interface JRTemplate
{

	/**
	 * Returns the DPI resolution at which the dimensional style properties
	 * (lengths and thicknesses) in this template are expressed. Defaults to 72.
	 *
	 * @return the template DPI
	 */
	@JacksonXmlProperty(isAttribute = true)
	int getDpi();

	/**
	 * Returns the templates included/referenced by this template.
	 * 
	 * @return the included templates
	 */
	@JsonGetter("templates")
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_template)
	@JacksonXmlElementWrapper(useWrapping = false)
	JRTemplateReference[] getIncludedTemplates();
	
	/**
	 * Returns the styles defined in this template.
	 * 
	 * @return the template styles
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_style)
	@JacksonXmlElementWrapper(useWrapping = false)
	JRStyle[] getStyles();

}
