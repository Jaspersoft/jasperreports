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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.design.JRDesignHyperlink;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * An interface providing hyperlink functionality. It must be implemented by elements that can contain hyperlinks.
 * <h3>Hyperlinks</h3>
 * JasperReports allows users to create drill-down reports, which introduce tables of contents
 * in generated documents or redirect viewers to external documents using special report
 * elements called <i>hyperlinks</i>.
 * <p/>
 * When the user clicks a hyperlink, he or she is redirected to a local destination within the
 * current document or to an external resource. Hyperlinks are not the only actors in this
 * viewer-redirecting scenario. You also need a way to specify the possible hyperlink
 * destinations in a document. These local destinations are called <i>anchors</i>.
 * <p/>
 * There are no special report elements that introduce hyperlinks or anchors in a report
 * template, but rather special settings that make a usual report element a hyperlink and/or
 * an anchor.
 * <p/>
 * In JasperReports, only text field, image, and chart elements can be hyperlinks or anchors.
 * This is because all these types of elements offer special settings that allow you to specify
 * the hyperlink reference to which the hyperlink will point to or the name of the local
 * anchor. Note that a particular text field or image can be both anchor and hyperlink at the
 * same time.
 * <p/>
 * There are five standard types of hyperlinks supported by JasperReports by default. These
 * are described below.
 * <h3>Hyperlink Type</h3>
 * The <code>hyperlinkType</code> attribute (see {@link #getHyperlinkType()}) attribute 
 * can hold any text value, but by default, the engine recognizes the following standard hyperlink types:
 * <ul>
 * <li><code>None</code> - By default, neither the text fields nor the images represent
 * hyperlinks, even if the special hyperlink expressions are present.</li>
 * <li><code>Reference</code> - The current hyperlink points to an external resource specified
 * by the corresponding <code>&lt;hyperlinkReferenceExpression&gt;</code> element, usually a
 * URL.</li>
 * <li><code>LocalAnchor</code> - The current hyperlink points to a local anchor specified by the
 * corresponding <code>&lt;hyperlinkAnchorExpression&gt;</code> tag.</li>
 * <li><code>LocalPage</code> - The current hyperlink points to a one-based page index within the
 * current document specified by the corresponding <code>&lt;hyperlinkPageExpression&gt;</code>
 * element.</li>
 * <li><code>RemoteAnchor</code> - The current hyperlink points to an anchor specified by the
 * <code>&lt;hyperlinkAnchorExpression&gt;</code> element within an external document indicated
 * by the corresponding <code>&lt;hyperlinkReferenceExpression&gt;</code> element</li>
 * <li><code>RemotePage</code> - The current hyperlink points to a one-based page index specified by
 * the <code>&lt;hyperlinkPageExpression&gt;</code> element, within an external document
 * indicated by the corresponding <code>&lt;hyperlinkReferenceExpression&gt;</code> element</li>
 * </ul>
 * Any <code>hyperlinkType</code> value not in the preceding list is considered a custom hyperlink
 * type. More details about those are given in the "Custom Hyperlinks" section, which
 * follows.
 * <h3>Hyperlink Expressions</h3>
 * Depending on the standard hyperlink type specified, one or two of the following
 * expressions are evaluated and used to build the reference to which the hyperlink element
 * will point:
 * <ul>
 * <li><code>&lt;hyperlinkReferenceExpression&gt;</code> - see the related method {@link #getHyperlinkReferenceExpression()}</li>
 * <li><code>&lt;hyperlinkAnchorExpression&gt;</code> - see the related method {@link #getHyperlinkAnchorExpression()}</li>
 * <li><code>&lt;hyperlinkPageExpression&gt;</code> - see the related method {@link #getHyperlinkPageExpression()}</li>
 * </ul>
 * Note that the first two should always return <code>java.lang.String</code> and the third should
 * return <code>java.lang.Integer</code> values.
 * <p/>
 * There are situations when hyperlinks should be displayed only if a certain condition is met. In such cases one can 
 * use the Boolean expression <code>&lt;hyperlinkWhenExpression&gt;</code> (see {@link #getHyperlinkWhenExpression()}).
 * <h3>Hyperlink Target</h3>
 * All hyperlink elements, like textfields, images, and charts, also expose an attribute
 * called <code>hyperlinkTarget</code> (see {@link #getHyperlinkTarget()}). Its purpose is to 
 * help customize the behavior of the specified link when it is clicked in the viewer.
 * <p/>
 * Possible values for this attribute:
 * <ul>
 * <li><code>Self</code> - The document to which the hyperlink points will be opened in the current
 * viewer window</li>
 * <li><code>Blank</code> - The document to which the hyperlink points will be opened in a new viewer
 * window</li>
 * <li><code>Parent</code> - The document to which the hyperlink points will be opened in the parent
 * frame</li>
 * <li><code>Top</code> - The document to which the hyperlink points will be opened in the top frame</li>
 * <li><i>Custom target/Parameter name/Frame name</i> - When the target value is not one of
 * the above-mentioned standard predefined target values, the target is either a custom
 * target that has to be processed by a registered target producer, or it is the name of a hyperlink parameter that
 * gives the actual target value, or, if neither of the above apply, it is the name of the
 * frame in which the document will be opened.</li>
 * </ul>
 * If the target is not specified, the default hyperlink target is <code>Self</code>.
 * <h3>Custom Hyperlink Target</h3>
 * Sometimes, the hyperlink target is not known at report design time and has to be
 * specified dynamically at runtime, depending on the environment where the report runs.
 * <p/>
 * In such cases, the value of the hyperlink target must be calculated based on some runtime
 * parameters or values. Targets defined at runtime are called custom hyperlink targets, as
 * opposed to the standard hyperlink targets.
 * <p/>
 * Custom hyperlink targets are generated by hyperlink target producers, which are classes
 * that implement the {@link net.sf.jasperreports.engine.export.JRHyperlinkTargetProducer}
 * interface. Hyperlink target producers can be added to the JasperReports engine in a
 * transparent way, by registering instances of the
 * {@link net.sf.jasperreports.engine.export.JRHyperlinkTargetProducerFactory}
 * class as extensions. 
 * <p/>
 * When the JasperReports engine encounters a custom target value specified in the target
 * attribute of a hyperlink, it first interrogates all registered hyperlink target producer
 * factories to obtain a target producer for this custom hyperlink. If no target producer is
 * found, the engine looks for any hyperlink parameter having the same name as the
 * specified custom target. If one is found, the engine takes its value as the true target to
 * use. If no parameter is found, the custom target value is considered a frame name into
 * which the hyperlink document must be opened.
 * <h3>Hyperlink ToolTips</h3>
 * The hyperlink element can have a ToolTip, which is controlled by the
 * <code>&lt;hyperlinkTooltipExpression&gt;</code> tag (see {@link #getHyperlinkTooltipExpression()}). 
 * The type of the expression should be <code>java.lang.String</code>. The ToolTip expression will be 
 * evaluated along with the hyperlink and the result will be saved in the generated document.
 * <p/>
 * The built-in JasperReports viewer and the HTML exporter will honor the hyperlink
 * ToolTip and display it while the user views the report.
 * <h3>Custom Hyperlinks</h3>
 * In addition to the standard hyperlink types, users can define hyperlinks having custom
 * types. A custom-typed hyperlink can have arbitrary parameters and is meant to be
 * processed by a hyperlink handler registered while exporting the report.
 * <p/>
 * When a hyperlink is declared as having a type other than the built-in types, the hyperlink
 * is considered of custom type and the user is expected to provide handlers to process the
 * hyperlink when the report is exported.
 * <p/>
 * Arbitrary hyperlink parameters can be added to a custom hyperlink using the
 * <code>&lt;hyperlinkParameter&gt;</code> tag. These parameters are made available to the custom
 * hyperlink handler so that it can generate a final hyperlink depending on the parameter
 * values.
 * <p/>
 * Hyperlink parameter expressions are evaluated along with the hyperlink, and the results
 * are kept in the generated hyperlink object as parameter values.
 * <p/>
 * When exporting the report to other formats such as HTML or PDF, the user can set a
 * factory of hyperlink handlers using the 
 * {@link net.sf.jasperreports.export.ReportExportConfiguration#getHyperlinkProducerFactory() getHyperlinkProducerFactory()} 
 * export configuration setting. A factory is an implementation of
 * {@link net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory}, which is
 * responsible for creating a hyperlink handler for a custom hyperlink type. This hyperlink
 * handler created by the factory is a
 * {@link net.sf.jasperreports.engine.export.JRHyperlinkProducer} instance, and it is
 * used for generating a hyperlink reference in the export document by assembling
 * hyperlink parameters and other information supplied at export time.
 * <p/>
 * To handle custom hyperlinks in the built-in Swing viewer, one needs to register a
 * hyperlink listener by calling 
 * {@link net.sf.jasperreports.swing.JRViewerPanel#addHyperlinkListener(net.sf.jasperreports.view.JRHyperlinkListener) addHyperlinkListener(JRHyperlinkListener)} on the
 * {@link net.sf.jasperreports.swing.JRViewerPanel} component. The listener is an
 * implementation of the {@link net.sf.jasperreports.view.JRHyperlinkListener}
 * interface. When a report hyperlink gets clicked, the listener queries the hyperlink type
 * and performs the desired actions.
 * <h3>Anchors</h3>
 * If present in a text field or image element declaration, the <code>&lt;anchorNameExpression&gt;</code>
 * tag transforms that particular text field or image into a local anchor of the resulting
 * document, to which hyperlinks can point. The anchor will bear the name returned after
 * evaluation of the anchor name expression, which should always return
 * <code>java.lang.String</code> values.
 * <h3>Bookmarks</h3>
 * Some of the document formats, such as PDF, have built-in support for tables of contents
 * and bookmarks. To allow you to make use of this, JasperReports lets you transform
 * anchors into document bookmarks. To be used as bookmarks, anchors should have an
 * indentation level set. To do this, set a positive integer value for the <code>bookmarkLevel</code>
 * attribute available for all hyperlink elements in JasperReports.
 *
 * @see net.sf.jasperreports.engine.JRAnchor
 * @see net.sf.jasperreports.engine.export.JRHyperlinkProducer
 * @see net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory
 * @see net.sf.jasperreports.engine.export.JRHyperlinkTargetProducer
 * @see net.sf.jasperreports.engine.export.JRHyperlinkTargetProducerFactory
 * @see net.sf.jasperreports.view.JRHyperlinkListener
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonDeserialize(as = JRDesignHyperlink.class)
public interface JRHyperlink extends JRCloneable
{


	/**
	 * Retrieves the hyperlink type for the element.
	 * <p>
	 * The actual hyperlink type is determined by {@link #getLinkType() getLinkType()}.
	 * This method can is used to determine whether the hyperlink type is one of the
	 * built-in types or a custom type. 
	 * When hyperlink is of custom type, {@link HyperlinkTypeEnum#CUSTOM CUSTOM} is returned.
	 * </p>
	 * @return one of the hyperlink type constants
	 * @see #getLinkType()
	 */
	@JsonIgnore
	public HyperlinkTypeEnum getHyperlinkType();


	/**
	 * Retrieves the hyperlink target for the element.
	 * <p>
	 * The actual hyperlink target is determined by {@link #getLinkTarget() getLinkTarget()}.
	 * This method can is used to determine whether the hyperlink target is one of the
	 * built-in target names or a custom one. 
	 * When hyperlink has a custom target name, {@link HyperlinkTargetEnum#CUSTOM} is returned.
	 * </p>
	 * @return one of the hyperlink target constants
	 * @see #getLinkTarget()
	 */
	@JsonIgnore
	public HyperlinkTargetEnum getHyperlinkTarget();

	
	/**
	 * Returns the expression whose value represents the hyperlink reference. It is only used when the hyperlink type is
	 * reference or anchor
	 */
	public JRExpression getHyperlinkReferenceExpression();


	/**
	 * Returns the expression that is evaluated in order to decide if the hyperlink should be displayed. This
	 * expression always returns a boolean value.
	 */
	public JRExpression getHyperlinkWhenExpression();


	/**
	 * Returns the expression whose value represents the anchor. It is only used when the hyperlink type is anchor.
	 */
	public JRExpression getHyperlinkAnchorExpression();


	/**
	 * Returns an integer representing the page index of the link. It is only used when the hyperlink type is page.
	 * If the expression does not evaluate to an integer, an exception will be thrown.
	 */
	public JRExpression getHyperlinkPageExpression();

	
	/**
	 * Returns the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @return the hyperlink type
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getLinkType();
	
	/**
	 * Returns the hyperlink target name.
	 * <p>
	 * The type can be one of the built-in names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary name.
	 * </p>
	 * @return the hyperlink target name
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getLinkTarget();
	
	
	/**
	 * Returns the list of hyperlink parameters.
	 * <p>
	 * The parameters can be used by custom hyperlink types to generate
	 * dynamic links.
	 * </p>
	 * @return the list of hyperlink parameters
	 */
	@JacksonXmlProperty(localName = JRXmlConstants.ELEMENT_hyperlinkParameter)
	@JacksonXmlElementWrapper(useWrapping = false)
	public JRHyperlinkParameter[] getHyperlinkParameters();
	
	
	/**
	 * Returns the expression which will generate the hyperlink tooltip.
	 * 
	 * @return the expression which will generate the hyperlink tooltip
	 */
	public JRExpression getHyperlinkTooltipExpression();

}
