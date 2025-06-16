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

/*
 * Contributors:
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 * Ling Li - lonecatz@users.sourceforge.net
 * Martin Clough - mtclough@users.sourceforge.net
 */
package net.sf.jasperreports.pdf;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRCommonGraphicElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintAnchor;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.base.JRBasePen;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.export.GenericElementHandlerEnviroment;
import net.sf.jasperreports.engine.export.HyperlinkUtil;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.export.JRHyperlinkProducerFactory;
import net.sf.jasperreports.engine.fonts.FontFace;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.FontInfo;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.ExifOrientationEnum;
import net.sf.jasperreports.engine.util.ImageUtil;
import net.sf.jasperreports.engine.util.JRImageLoader;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSingletonCache;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.engine.util.JRTypeSniffer;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.export.ExporterInputItem;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.pdf.classic.ClassicPdfProducer;
import net.sf.jasperreports.pdf.common.FontRecipient;
import net.sf.jasperreports.pdf.common.LineCapStyle;
import net.sf.jasperreports.pdf.common.PdfChunk;
import net.sf.jasperreports.pdf.common.PdfContent;
import net.sf.jasperreports.pdf.common.PdfDocument;
import net.sf.jasperreports.pdf.common.PdfDocumentWriter;
import net.sf.jasperreports.pdf.common.PdfFontStyle;
import net.sf.jasperreports.pdf.common.PdfImage;
import net.sf.jasperreports.pdf.common.PdfOutlineEntry;
import net.sf.jasperreports.pdf.common.PdfPhrase;
import net.sf.jasperreports.pdf.common.PdfProducer;
import net.sf.jasperreports.pdf.common.PdfProducerContext;
import net.sf.jasperreports.pdf.common.PdfProducerFactory;
import net.sf.jasperreports.pdf.common.PdfRadioCheck;
import net.sf.jasperreports.pdf.common.PdfTextAlignment;
import net.sf.jasperreports.pdf.common.PdfTextChunk;
import net.sf.jasperreports.pdf.common.PdfTextField;
import net.sf.jasperreports.pdf.common.PdfTextRendererContext;
import net.sf.jasperreports.pdf.common.TextDirection;
import net.sf.jasperreports.pdf.type.PdfFieldBorderStyleEnum;
import net.sf.jasperreports.pdf.type.PdfFieldCheckTypeEnum;
import net.sf.jasperreports.pdf.type.PdfFieldTypeEnum;
import net.sf.jasperreports.pdf.type.PdfPermissionsEnum;
import net.sf.jasperreports.pdf.type.PdfPrintScalingEnum;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;
import net.sf.jasperreports.pdf.type.PdfaConformanceEnum;
import net.sf.jasperreports.pdf.util.JRPdfaIccProfileNotFoundException;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.DimensionRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;
import net.sf.jasperreports.renderers.WrappingImageDataToGraphics2DRenderer;
import net.sf.jasperreports.renderers.WrappingRenderToImageDataRenderer;
import net.sf.jasperreports.renderers.WrappingSvgDataToGraphics2DRenderer;
import net.sf.jasperreports.renderers.util.RendererUtil;


/**
 * Exports a JasperReports document to PDF format. It has binary output type and exports the document to
 * a free-form layout.
 * <p/>
 * As its name indicates, PDF is a very precise and complex document format that ensures
 * documents will look and print the same on all platforms.
 * This is why the PDF exporter implemented by the
 * {@link net.sf.jasperreports.pdf.JRPdfExporter} class in JasperReports is
 * one of the best exporters. The output it produces is almost of the same quality as that
 * produced by the {@link net.sf.jasperreports.engine.export.JRGraphics2DExporter},
 * which is always the reference.
 * <p/>
 * The {@link net.sf.jasperreports.pdf.JRPdfExporter} implementation uses OpenPDF, 
 * which is a specialized PDF-generating library. PDF is a binary document format that allows 
 * absolute positioning of the elements inside a page, so the existing PDF exporter does not 
 * have the limitations of a grid exporter.
 * <p/>
 * It also works very well in batch mode because it allows concatenation of multiple
 * documents within the same PDF file, even if the files have different page sizes.
 * <h3>Font Mappings</h3>
 * Exporting to PDF requires mapping the fonts using three attributes: 
 * <code>pdfFontName</code>, <code>pdfEncoding</code> and <code>isPdfEmbedded</code>. 
 * Even though these three attributes are still supported in JRXML and
 * the API, we recommend making the PDF font mappings at export time using font
 * extensions. 
 * <p/>
 * When exporting documents to PDF, for each combination of the three <code>fontName</code>,
 * <code>isBold</code>, and <code>isItalic</code> font attributes, there must be an equivalent 
 * combination of the PDF-related font attributes <code>pdfFontName</code>, <code>pdfEncoding</code> 
 * and <code>isPdfEmbedded</code>.
 * <p/>
 * <i>Equivalent combination</i> means one that causes the text elements to be rendered exactly
 * the same (or at least as closely as possible) in PDF and the built-in Graphics2D
 * exporter, which is the reference.
 * <p/>
 * In some cases, there is no font file available to use with the pdfFontName attribute in
 * order to render bold and italic text exactly like the Graphics2D exporter renders it in
 * AWT. Those fonts might only have a normal style variant and no variants for bold and
 * italic. In such cases, the PDF exporter (the OpenPDF library, to be more precise) is able to
 * simulate those styles by applying transformations to the normal font glyphs. The 
 * {@link net.sf.jasperreports.pdf.JRPdfExporter} internally acquires the needed PDF 
 * font based on the font extension mechanism (see the <code>getFont(Map, Locale, boolean)</code>
 * method.
 * <h3>Batch Mode Bookmarks</h3>
 * When several JasperPrint documents must be concatenated in the same PDF file by
 * batch export, one can introduce PDF bookmarks in the resulting PDF document to mark
 * the beginning of each individual document that was part of the initial document list.
 * <p/>
 * These bookmarks have the same name as the original JasperPrint document as
 * specified by the <code>jasperPrint.getName()</code> property. However, users can turn on and off
 * the creation of those bookmarks by turning on or off the 
 * {@link net.sf.jasperreports.pdf.PdfExporterConfiguration#isCreatingBatchModeBookmarks() isCreatingBatchModeBookmarks()}
 * exporter configuration setting. The exporter does not create such bookmarks by default.
 * <h3>Encrypted PDF</h3>
 * In some cases, users might want to encrypt the PDF documents generated by
 * JasperReports so that only authorized viewers can have access to those documents.
 * There are five exporter configuration settings for this (see {@link net.sf.jasperreports.pdf.PdfExporterConfiguration}):
 * <ul>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#isEncrypted() isEncrypted()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#is128BitKey() is128BitKey()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getUserPassword() getUserPassword()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getOwnerPassword() getOwnerPassword()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getPermissions() getPermissions()}</li>
 * </ul>
 * <h3>PDF Version and Compression</h3>
 * Some applications require marking the generated files with a particular PDF
 * specifications version. Related export configuration settings are the following
 * (see {@link net.sf.jasperreports.pdf.PdfExporterConfiguration}):
 * <ul>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getPdfVersion() getPdfVersion()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#isCompressed() isCompressed()}</li>
 * </ul>
 * Since version 1.5, the PDF format supports compression. By default, the PDF exporter in
 * JasperReports does not create compressed PDF documents, but this feature can be turned
 * on using the {@link net.sf.jasperreports.pdf.PdfExporterConfiguration#isCompressed() isCompressed()} 
 * exporter configuration setting. Note that because compressed PDFs
 * are available only since PDF version 1.5, the PDF version of the resulting document is
 * set to 1.5 automatically if compression is turned on.
 * <h3>Word Wrap and Line Break Policy</h3>
 * By default, the PDF exporter does not guarantee that text with the same style properties
 * will be rendered exactly as it is using AWT. The word wrap and line break policy is
 * slightly different, and in some cases it might cause portions of text to disappear at the
 * end of longer text paragraphs.
 * <p/>
 * To make sure this does not happen, one can configure the PDF exporter to use the AWT
 * word wrap and line break policy by setting the 
 * {@link net.sf.jasperreports.pdf.PdfReportConfiguration#isForceLineBreakPolicy() isForceLineBreakPolicy()} 
 * exporter configuration setting to true. Note that this feature is not turned on by default, because it affects the
 * exporter performance. This default behavior that applies in the absence of the mentioned
 * export parameter can be controlled using the
 * {@link net.sf.jasperreports.pdf.PdfReportConfiguration#PROPERTY_FORCE_LINEBREAK_POLICY net.sf.jasperreports.export.pdf.force.linebreak.policy} configuration
 * property
 * <h3>JavaScript Actions</h3>
 * The PDF specifications provide a means for the automation of various processes, such as
 * the automatic printing of the document when it is opened. PDF viewer applications are
 * able to execute Acrobat JavaScript code that is embedded in the PDF and associated with
 * different events.
 * <p/>
 * JasperReports only allows inserting Acrobat JavaScript code. This code gets executed
 * when the PDF document is opened in the viewer. This can be achieved using the
 * {@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getPdfJavaScript() getPdfJavaScript()} 
 * configuration setting, which retrieve the Acrobat JavaScript source code. 
 * Note that Acrobat JavaScript is a programming language based on JavaScript. More
 * details about this can be found in the OpenPDF documentation.
 * <h3>Metadata Information</h3>
 * PDF documents can store metadata information such as the author of the document, its
 * title, and keywords. JasperReports exposes this feature of PDF through special exporter
 * configuration settings available in the {@link net.sf.jasperreports.pdf.PdfExporterConfiguration}
 * class. They are all listed following:
 * <ul>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getMetadataAuthor() getMetadataAuthor()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getMetadataCreator() getMetadataCreator()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getMetadataKeywords() getMetadataKeywords()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getMetadataSubject() getMetadataSubject()}</li>
 * <li>{@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getMetadataTitle() getMetadataTitle()}</li>
 * </ul>
 * <h3>Rendering SVG Using Shapes</h3>
 * The {@link net.sf.jasperreports.pdf.PdfReportConfiguration#isForceSvgShapes() isForceSvgShapes()} 
 * flag is used to force the rendering of SVG images using shapes on the PDF <code>Graphics2D</code> 
 * context. This allows fonts to be rendered as shapes, thus avoiding any font mapping issues that 
 * might cause Unicode text to not show up properly; however, it has the disadvantage of producing
 * larger PDF files.
 * <p/>
 * By default, the flag is set to true, mainly due to backward-compatibility reasons. To
 * reduce PDF file size for documents containing SVG images such as charts, this flag
 * should be set to false. However, in such a case, the accuracy of the text content
 * rendered by the SVG element in PDF depends on the correct PDF font information being
 * available in the SVG implementation itself.
 * <p/>
 * In JasperReports, SVG elements are rendered using 
 * {@link net.sf.jasperreports.renderers.Renderable} implementations,
 * which are most likely subclasses of the {@link net.sf.jasperreports.renderers.AbstractRenderToImageDataRenderer} 
 * class. SVG renderer implementations should be concerned only with
 * implementing the 
 * <p/>
 * <code>public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle)</code> 
 * <p/>
 * method, which should contain all the code
 * required for rendering the SVG on a Graphics2D context. Correct PDF font information
 * means that the <code>java.awt.Font</code> objects used to draw text on the <code>Graphics2D</code> 
 * context should have PDF-related text attributes embedded so that when rendered on a PDF
 * <code>Graphics2D</code> context, the exporter can make use of them. Embedding PDF-related text
 * attributes into the SVG means using the following text attributes when creating
 * <code>java.awt.Font</code> to render text in the SVG renderer implementation:
 * <ul>
 * <li>{@link net.sf.jasperreports.engine.util.JRTextAttribute#PDF_FONT_NAME PDF_FONT_NAME}</li>
 * <li>{@link net.sf.jasperreports.engine.util.JRTextAttribute#PDF_ENCODING PDF_ENCODING}</li>
 * <li>{@link net.sf.jasperreports.engine.util.JRTextAttribute#IS_PDF_EMBEDDED IS_PDF_EMBEDDED}</li>
 * </ul>
 * <p/>
 * The built-in chart component in JasperReports hides this complexity of dealing with
 * fonts in a SVG renderer by exposing to the end user the usual three PDF-specific font
 * attributes (<code>pdfFontName</code>, <code>pdfEncoding</code>, and <code>isPdfEmbedded</code>) 
 * to be set along with the normal font attributes every time a font setting is made for the chart 
 * title, subtitle, chart legend, or axis. This feature can be controlled system-wide using the
 * {@link net.sf.jasperreports.pdf.PdfReportConfiguration#PROPERTY_FORCE_SVG_SHAPES net.sf.jasperreports.export.pdf.force.svg.shapes} configuration property.
 * The {@link net.sf.jasperreports.pdf.PdfReportConfiguration#isForceSvgShapes() isForceSvgShapes()} 
 * export configuration setting overrides the configuration property value, if present.
 * <h3>Section 508 Compliance</h3>
 * PDF files can contain hidden tags that describe the structure of the document. Some of
 * the tags are used by the automated reader tool that reads PDF documents aloud to people
 * with disabilities.
 * <p/>
 * The PDF tags feature of JasperReports allows adding hidden PDF tags to the files
 * generated by the JasperReports PDF exporter. The resulting files comply with the
 * requirements of the Section 508 of the U.S. Rehabilitation Act
 * (<a href="http://www.section508.gov">http://www.section508.gov/</a>).
 * <h3>Producing Tagged PDF Files</h3>
 * By default, the JasperReports exporter does not put any hidden structural tags inside its
 * generated PDF files. In order to turn on the creation of hidden structural tags, any of the
 * following can be used:
 * <ul>
 * <li>setting to true the {@link net.sf.jasperreports.pdf.PdfExporterConfiguration#isTagged() isTagged()}
 * configuration flag</li>
 * <li>setting to true the {@link net.sf.jasperreports.pdf.PdfExporterConfiguration#PROPERTY_TAGGED net.sf.jasperreports.export.pdf.tagged} configuration property.</li>
 * </ul>
 * <h3>Setting the PDF File Language</h3>
 * When a full accessibility check is requested from Acrobat Professional, among the things
 * it determines is whether the PDF file or the various pieces of content inside it have a
 * language set. JasperReports allows setting the language for the entire content by doing
 * any one of the following:
 * <ul>
 * <li>using the {@link net.sf.jasperreports.pdf.PdfExporterConfiguration#getTagLanguage() getTagLanguage()}
 * configuration setting to retrieve the language as a <code>java.lang.String</code> value;</li>
 * <li>using the {@link net.sf.jasperreports.pdf.PdfExporterConfiguration#PROPERTY_TAG_LANGUAGE net.sf.jasperreports.export.pdf.tag.language} configuration property 
 * globally or at report level</li>
 * </ul>
 * <h3>Alternate Text for Images</h3>
 * In tagged PDF files, image elements can be described in alternate text that is read by the
 * automated reader. The text is specified using the <code>hyperlinkTooltipExpression</code>
 * property of the image element in JRXML.
 * <p/>
 * For more information about tagged PDF documents in JasperReports, 
 * please consult the {@link net.sf.jasperreports.pdf.JRPdfExporterTagHelper} class.
 * 
 * @see net.sf.jasperreports.pdf.PdfExporterConfiguration
 * @see net.sf.jasperreports.pdf.PdfReportConfiguration
 * @see net.sf.jasperreports.engine.util.JRTextAttribute#IS_PDF_EMBEDDED
 * @see net.sf.jasperreports.engine.util.JRTextAttribute#PDF_ENCODING
 * @see net.sf.jasperreports.engine.util.JRTextAttribute#PDF_FONT_NAME
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRPdfExporter extends JRAbstractExporter<PdfReportConfiguration, PdfExporterConfiguration, OutputStreamExporterOutput, JRPdfExporterContext>
{

	private static final Log log = LogFactory.getLog(JRPdfExporter.class);
	
	public static final String PDF_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.pdf.";

	public static final String EXCEPTION_MESSAGE_KEY_DOCUMENT_ERROR = "export.pdf.document.error";
	public static final String EXCEPTION_MESSAGE_KEY_FONT_LOADING_ERROR = "export.pdf.font.loading.error";
	public static final String EXCEPTION_MESSAGE_KEY_REPORT_GENERATION_ERROR = "export.pdf.report.generation.error";
	
	/**
	 * Prefix of properties that specify font files for the PDF exporter.
	 */
	@Property(
		name = "net.sf.jasperreports.export.pdf.font.{arbitrary_name}",
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.GLOBAL},
		sinceVersion = PropertyConstants.VERSION_1_0_0
		)
	public static final String PDF_FONT_FILES_PREFIX = PDF_EXPORTER_PROPERTIES_PREFIX + "font.";
	
	/**
	 * Prefix of properties that specify font directories for the PDF exporter.
	 */
	@Property(
		name = "net.sf.jasperreports.export.pdf.fontdir.{arbitrary_name}",
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.GLOBAL},
		sinceVersion = PropertyConstants.VERSION_1_0_0
		)
	public static final String PDF_FONT_DIRS_PREFIX = PDF_EXPORTER_PROPERTIES_PREFIX + "fontdir.";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0,
		valueType = PdfFieldTypeEnum.class
		)
	public static final String PDF_FIELD_TYPE = PDF_EXPORTER_PROPERTIES_PREFIX + "field.type";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0,
		valueType = Boolean.class,
		defaultValue = PropertyConstants.BOOLEAN_FALSE
		)
	public static final String PDF_FIELD_TEXT_MULTILINE = PDF_EXPORTER_PROPERTIES_PREFIX + "field.text.multiline";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0
		)
	public static final String PDF_FIELD_VALUE = PDF_EXPORTER_PROPERTIES_PREFIX + "field.value";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0,
		valueType = PdfFieldCheckTypeEnum.class
		)
	public static final String PDF_FIELD_CHECK_TYPE = PDF_EXPORTER_PROPERTIES_PREFIX + "field.check.type";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0
		)
	public static final String PDF_FIELD_NAME = PDF_EXPORTER_PROPERTIES_PREFIX + "field.name";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0,
		valueType = Boolean.class
		)
	public static final String PDF_FIELD_CHECKED = PDF_EXPORTER_PROPERTIES_PREFIX + "field.checked";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0,
		valueType = Boolean.class
		)
	public static final String PDF_FIELD_READ_ONLY = PDF_EXPORTER_PROPERTIES_PREFIX + "field.read.only";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.GLOBAL, PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0,
		valueType = PdfFieldBorderStyleEnum.class
		)
	public static final String PDF_FIELD_BORDER_STYLE = PDF_EXPORTER_PROPERTIES_PREFIX + "field.border.style";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		defaultValue = "|",
		scopes = {PropertyScope.GLOBAL, PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0
		)
	public static final String PDF_FIELD_CHOICE_SEPARATORS = PDF_EXPORTER_PROPERTIES_PREFIX + "field.choice.separators";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0
		)
	public static final String PDF_FIELD_CHOICES = PDF_EXPORTER_PROPERTIES_PREFIX + "field.choices";
	
	/**
	 * 
	 */
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.GLOBAL, PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.ELEMENT},
		sinceVersion = PropertyConstants.VERSION_6_12_0,
		valueType = Boolean.class,
		defaultValue = PropertyConstants.BOOLEAN_FALSE
		)
	public static final String PDF_FIELD_COMBO_EDIT = PDF_EXPORTER_PROPERTIES_PREFIX + "field.combo.edit";
	
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.GLOBAL, PropertyScope.CONTEXT, PropertyScope.REPORT},
		sinceVersion = PropertyConstants.VERSION_6_20_1,
		valueType = Boolean.class,
		defaultValue = PropertyConstants.BOOLEAN_FALSE
		)
	public static final String LEGACY_TARGET_BLANK_LINKS = PDF_EXPORTER_PROPERTIES_PREFIX + "legacy.target.blank.links";
	
	@Property(
		category = PropertyConstants.CATEGORY_EXPORT,
		scopes = {PropertyScope.GLOBAL, PropertyScope.CONTEXT, PropertyScope.REPORT},
		sinceVersion = PropertyConstants.VERSION_6_20_5,
		valueType = Boolean.class,
		defaultValue = PropertyConstants.BOOLEAN_FALSE
		)
	public static final String LEGACY_TEXT_MEASURING_FIX = PDF_EXPORTER_PROPERTIES_PREFIX + "legacy.text.measuring.fix";
	
	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String PDF_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "pdf";
	
	public static final String PDF_PRODUCER_FACTORY_PROPERTY = PDF_EXPORTER_PROPERTIES_PREFIX + "producer.factory";
	
	private static final String EMPTY_BOOKMARK_TITLE = "";

	/**
	 *
	 */
	protected static final String JR_PAGE_ANCHOR_PREFIX = "JR_PAGE_ANCHOR_";

	protected static boolean fontsRegistered;
	
	private static final JRSingletonCache<PdfProducerFactory> pdfProducerCache = 
			new JRSingletonCache<>(PdfProducerFactory.class);

	protected class ExporterContext extends BaseExporterContext implements JRPdfExporterContext
	{
		@Override
		public PdfWriter getPdfWriter()
		{
			if (pdfProducer instanceof ClassicPdfProducer)
			{
				return ((ClassicPdfProducer) pdfProducer).getPdfWriter();
			}
			
			throw new JRRuntimeException("Not using the classic PDF producer");
		}

		@Override
		public PdfProducer getPdfProducer()
		{
			return pdfProducer;
		}
	}
	
	/**
	 *
	 */
	protected PdfProducer pdfProducer;
	protected PdfContent pdfContent;
	
	protected JRPdfExporterTagHelper tagHelper = new JRPdfExporterTagHelper(this);
	protected ColorSpace cmykColorSpace;

	protected int reportIndex;
	protected PrintPageFormat pageFormat;
	protected int crtDocumentPageNumber;
	
	protected int permissions;

	/**
	 *
	 */
	protected RenderersCache renderersCache;
	protected Map<String,Pair<PdfImage, ExifOrientationEnum>> loadedImagesMap;
	protected PdfImage pxImage;

	private BookmarkStack bookmarkStack;

	private int crtPageOffsetX;
	private int crtPageOffsetY;
	private int crtOddPageOffsetX;
	private int crtOddPageOffsetY;
	private int crtEvenPageOffsetX;
	private int crtEvenPageOffsetY;
	
	private boolean bookmarksEnabled;

	private boolean awtIgnoreMissingFont;
	private boolean defaultIndentFirstLine;
	private boolean defaultJustifyLastLine;
	private boolean legacyTargetBlankLinks;
	/**
	 * @deprecated To be removed.
	 */
	private boolean legacyTextMeasuringFix;

	private PdfVersionEnum minimalVersion;

	/**
	 * @see #JRPdfExporter(JasperReportsContext)
	 */
	public JRPdfExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	
	/**
	 *
	 */
	public JRPdfExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
	}


	@Override
	protected Class<PdfExporterConfiguration> getConfigurationInterface()
	{
		return PdfExporterConfiguration.class;
	}


	@Override
	protected Class<PdfReportConfiguration> getItemConfigurationInterface()
	{
		return PdfReportConfiguration.class;
	}
	

	/**
	 *
	 */
	protected PdfImage getPxImage()
	{
		if (pxImage == null)
		{
			try
			{
				pxImage = pdfProducer.createImage(JRLoader.loadBytesFromResource(JRImageLoader.PIXEL_IMAGE_RESOURCE), false);
			}
			catch(Exception e)
			{
				throw new JRRuntimeException(e);
			}
		}

		return pxImage;
	}


	@Override
	public void exportReport() throws JRException
	{
		registerFonts();

		/*   */
		ensureJasperReportsContext();
		ensureInput();

		initExport();

		ensureOutput();
		
		OutputStream outputStream = getExporterOutput().getOutputStream();

		try
		{
			exportReportToStream(outputStream);
		}
		finally
		{
			getExporterOutput().close();
			resetExportContext();
		}
	}


	@Override
	protected void initExport()
	{
		super.initExport();
		
		PdfExporterConfiguration configuration = getCurrentConfiguration();
		
		Boolean isTagged = configuration.isTagged();
		if (isTagged != null)
		{
			tagHelper.setTagged(isTagged); 
		}

		tagHelper.setLanguage(configuration.getTagLanguage()); 
		
		this.permissions = getIntegerPermissions(configuration.getAllowedPermissions()) & (~getIntegerPermissions(configuration.getDeniedPermissions()));
		crtDocumentPageNumber = 0;
		
		awtIgnoreMissingFont = getPropertiesUtil().getBooleanProperty(
				JRStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT);//FIXMECONTEXT replace with getPropertiesUtil in all exporters
		
		pdfProducer = createPdfProducer();
	}

	@Override
	protected void initReport()
	{
		super.initReport();

		PdfReportConfiguration configuration = getCurrentItemConfiguration();
		
		pdfProducer.setForceLineBreakPolicy(configuration.isForceLineBreakPolicy());
		
		defaultIndentFirstLine = propertiesUtil.getBooleanProperty(jasperPrint, JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE, true);
		defaultJustifyLastLine = propertiesUtil.getBooleanProperty(jasperPrint, JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE, false);
		legacyTargetBlankLinks = propertiesUtil.getBooleanProperty(jasperPrint, LEGACY_TARGET_BLANK_LINKS, false);
		legacyTextMeasuringFix = propertiesUtil.getBooleanProperty(jasperPrint, LEGACY_TEXT_MEASURING_FIX, false);
		
		crtOddPageOffsetX = configuration.getOddPageOffsetX();
		crtOddPageOffsetY = configuration.getOddPageOffsetY();
		crtEvenPageOffsetX = configuration.getEvenPageOffsetX();
		crtEvenPageOffsetY = configuration.getEvenPageOffsetY();
		
		bookmarksEnabled = configuration.isBookmarksEnabled();
		
		pdfProducer.initReport();

		renderersCache = new RenderersCache(getJasperReportsContext());
		loadedImagesMap = new HashMap<>();
	}

	protected PdfProducerFactory getPdfProducerFactory()
	{
		String producerFactory = propertiesUtil.getProperty(PDF_PRODUCER_FACTORY_PROPERTY);
		try
		{
			return pdfProducerCache.getCachedInstance(producerFactory);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	protected PdfProducerContext createPdfProducerContext()
	{
		return new PdfProducerContext()
		{			
			@Override
			public JRPdfExporter getExporter()
			{
				return JRPdfExporter.this;
			}

			@Override
			public JasperReportsContext getJasperReportsContext()
			{
				return jasperReportsContext;
			}
			
			@Override
			public JRPropertiesUtil getProperties()
			{
				return propertiesUtil;
			}
			
			@Override
			public FontUtil getFontUtil()
			{
				return fontUtil;
			}
			
			@Override
			public JRStyledTextUtil getStyledTextUtil()
			{
				return styledTextUtil;
			}

			@Override
			public boolean isTagged()
			{
				return tagHelper.isTagged;
			}

			@Override
			public void setMinimalVersion(PdfVersionEnum version)
			{
				minimalVersion = version;
			}
			
			@Override
			public JasperPrint getCurrentJasperPrint()
			{
				return JRPdfExporter.this.getCurrentJasperPrint();
			}
			
			@Override
			public void setFont(Map<Attribute, Object> attributes, Locale locale, boolean setFontLines, FontRecipient recipient)
			{
				JRPdfExporter.this.setFont(attributes, locale, setFontLines, recipient);
			}

			@Override
			public JRException handleDocumentException(Exception e) 
			{
				return new JRException(EXCEPTION_MESSAGE_KEY_DOCUMENT_ERROR,
					new Object[]{jasperPrint.getName()}, e);
			}

			@Override
			public ColorSpace getCMYKColorSpace()
			{
				return cmykColorSpace;
			}
		};
	}
	
	protected PdfTextRendererContext createPdfTextRendererContext(final JRPrintText text, final JRStyledText styledText, final Locale textLocale)
	{
		return new PdfTextRendererContext() 
		{
			@Override
			public Locale getTextLocale() 
			{
				return textLocale;
			}
			
			@Override
			public JRPrintText getPrintText() 
			{
				return text;
			}
			
			@Override
			public JRStyledText getStyledText() 
			{
				return styledText;
			}
			
			/**
			 * @deprecated To be removed.
			 */
			@Override
			public boolean getLegacyTextMeasuringFix() 
			{
				return legacyTextMeasuringFix;
			}
			
			@Override
			public boolean getJustifyLastLine() 
			{
				return defaultJustifyLastLine;
			}
			
			@Override
			public boolean getIndentFirstLine() 
			{
				return defaultIndentFirstLine;
			}
			
			@Override
			public boolean getAwtIgnoreMissingFont() 
			{
				return awtIgnoreMissingFont;
			}
		};
	}

	protected PdfProducer createPdfProducer()
	{
		PdfProducerFactory producerFactory = getPdfProducerFactory();
		PdfProducerContext producerContext = createPdfProducerContext();
		return producerFactory.createProducer(producerContext);
	}
	
	/**
	 *
	 */
	protected void exportReportToStream(OutputStream os) throws JRException
	{
		//ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		PdfExporterConfiguration configuration = getCurrentConfiguration();

		pageFormat = jasperPrint.getPageFormat(0);

		PdfDocument document = pdfProducer.createDocument(pageFormat);

		boolean closeDocuments = true;
		try
		{
			PdfDocumentWriter pdfWriter = pdfProducer.createWriter(os);

			tagHelper.setPdfProducer(pdfProducer);
			
			PdfVersionEnum pdfVersion = configuration.getPdfVersion();
			if (pdfVersion != null)
			{
				pdfWriter.setPdfVersion(pdfVersion);
			}
			
			if (minimalVersion != null)
			{
				pdfWriter.setMinimalPdfVersion(minimalVersion);
			}
			
			if (configuration.isCompressed())
			{
				pdfWriter.setFullCompression();
			}
			if (configuration.isEncrypted())
			{
				int perms = configuration.isOverrideHints() == null || configuration.isOverrideHints()
					? (configuration.getPermissions() != null 
						? (Integer)configuration.getPermissions() 
						: permissions) 
					: (permissions != 0 
						? permissions 
						:(configuration.getPermissions() != null 
							? (Integer)configuration.getPermissions() 
							: 0));
				
						pdfWriter.setEncryption(configuration.getUserPassword(), configuration.getOwnerPassword(), 
								perms, configuration.is128BitKey());
			}
			

			PdfPrintScalingEnum printScaling = configuration.getPrintScaling();
			pdfWriter.setPrintScaling(printScaling);
			
			boolean justifiedLetterSpacing = propertiesUtil.getBooleanProperty(jasperPrint, 
					PdfExporterConfiguration.PROPERTY_JUSTIFIED_LETTER_SPACING, false);
			if (!justifiedLetterSpacing)
			{
				pdfWriter.setNoSpaceCharRatio();
			}

			// Add meta-data parameters to generated PDF document
			// mtclough@users.sourceforge.net 2005-12-05
			String title = configuration.getMetadataTitle();
			if( title != null )
			{
				document.addTitle(title);
				if(configuration.isDisplayMetadataTitle()){
					pdfWriter.setDisplayMetadataTitle();
				}
			}
			String author = configuration.getMetadataAuthor();
			if( author != null )
			{
				document.addAuthor(author);
			}
			String subject = configuration.getMetadataSubject();
			if( subject != null )
			{
				document.addSubject(subject);
			}
			String keywords = configuration.getMetadataKeywords();
			if( keywords != null )
			{
				document.addKeywords(keywords);
			}
			String creator = configuration.getMetadataCreator();
			if( creator == null )
			{
				@SuppressWarnings("deprecation") //this can be replaced only after abandoning Java 8 support 
				String depCreator = "JasperReports Library version " + Package.getPackage("net.sf.jasperreports.engine").getImplementationVersion();
				creator = depCreator;
			}
			document.addCreator(creator);
			String producer = configuration.getMetadataProducer();
			if( producer != null )
			{
				document.addProducer(producer);
			}
			
			//accessibility check: tab order follows the structure of the document
			pdfWriter.setTabOrderStructure();
			
			//accessibility check: setting the document primary language
			String language = configuration.getTagLanguage();
			if(language != null){
				pdfWriter.setLanguage(language);
			}

			// BEGIN: PDF/A support
			PdfaConformanceEnum pdfaConformance = configuration.getPdfaConformance();
			boolean gotPdfa = false;
			if (pdfaConformance != null && pdfaConformance != PdfaConformanceEnum.NONE)
			{
				pdfWriter.setPdfaConformance(pdfaConformance);
				gotPdfa = true;
			}

			if (gotPdfa) 
			{
				pdfWriter.createXmpMetadata(title, subject, keywords);
			} else 
			{
				pdfWriter.setRgbTransparencyBlending(true);
			}
			// END: PDF/A support
			
			document.open();
			
			if (
				configuration.isUseCMYKColors()
				|| configuration.isEmbedIccProfile()
				|| gotPdfa
				)
			{
				String iccProfilePath = configuration.getIccProfilePath();
				if (iccProfilePath != null && iccProfilePath.trim().length() > 0)
				{
					byte[] iccBytes = getRepository().getBytesFromLocation(iccProfilePath);
					if (configuration.isUseCMYKColors())
					{
						ICC_Profile profile = ICC_Profile.getInstance(iccBytes);
						cmykColorSpace = new ICC_ColorSpace(profile);
					}

					// BEGIN: PDF/A support
					if (gotPdfa || configuration.isEmbedIccProfile()) 
					{
						pdfWriter.setIccProfilePath(iccProfilePath, new ByteArrayInputStream(iccBytes));
					}
					// END: PDF/A support
				}
				else
				{
					throw new JRPdfaIccProfileNotFoundException();
				}
			}
			
			String pdfJavaScript = configuration.getPdfJavaScript();
			if(pdfJavaScript != null)
			{
				pdfWriter.addJavaScript(pdfJavaScript);
			}

			pdfContent = pdfProducer.createPdfContent();

			tagHelper.init();

			List<ExporterInputItem> items = exporterInput.getItems();

			initBookmarks(items);
			
			boolean isCreatingBatchModeBookmarks = configuration.isCreatingBatchModeBookmarks();

			for (reportIndex = 0; reportIndex < items.size(); reportIndex++)
			{
				ExporterInputItem item = items.get(reportIndex);

				setCurrentExporterInputItem(item);
				
				pageFormat = jasperPrint.getPageFormat(0);

				setPageSize(null);
				
				boolean pageExported = false;
				List<JRPrintPage> pages = jasperPrint.getPages();
				if (pages != null && pages.size() > 0)
				{
					if (items.size() > 1)
					{
						pdfProducer.newPage();

						if( isCreatingBatchModeBookmarks )
						{
							//add a new level to our outline for this report
							addBookmark(0, jasperPrint.getName(), 0, 0);
						}
					}
					
					PdfReportConfiguration lcItemConfiguration = getCurrentItemConfiguration();

					boolean sizePageToContent = lcItemConfiguration.isSizePageToContent();
					
					PrintPageFormat oldPageFormat = null;

					PageRange pageRange = getPageRange();
					int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
					int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

					for (int pageIndex = startPageIndex; pageIndex <= endPageIndex; pageIndex++)
					{
						checkInterrupted();

						JRPrintPage page = pages.get(pageIndex);

						pageFormat = jasperPrint.getPageFormat(pageIndex);
						
						crtPageOffsetX = 0;
						crtPageOffsetY = 0;
						
						if (sizePageToContent || oldPageFormat != pageFormat)
						{
							setPageSize(sizePageToContent ? page : null);
						}
						
						pdfProducer.newPage();

						pdfProducer.getPdfContent().setLineCap(LineCapStyle.PROJECTING_SQUARE);

						writePageAnchor(pageIndex);
						
						crtDocumentPageNumber++;

						/*   */
						exportPage(page);
						pageExported = true;
						
						oldPageFormat = pageFormat;
					}
				}

				if (!pageExported)
				{
					pdfProducer.newPage();
					pdfContent.setLiteral("\n");
				}
			}

			closeDocuments = false;
			pdfProducer.close();
		}
		catch(IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_REPORT_GENERATION_ERROR,
					new Object[]{jasperPrint.getName()}, 
					e);
		}
		finally
		{
			if (closeDocuments) //only on exception
			{
				try
				{
					pdfProducer.close();
				}
				catch (Exception e)
				{
					// ignore, let the original exception propagate
				}
			}
		}

		//return os.toByteArray();
	}


	protected void writePageAnchor(int pageIndex) 
	{
		Map<Attribute,Object> attributes = new HashMap<>();
		fontUtil.getAttributesWithoutAwtFont(attributes, new JRBasePrintText(jasperPrint.getDefaultStyleProvider()));
		PdfTextChunk chunk = pdfProducer.createChunk(" ", attributes, getLocale());
		
		chunk.setLocalDestination(JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + (pageIndex + 1));

		tagHelper.startPageAnchor();
		
		PdfPhrase phrase = pdfProducer.createPhrase(chunk);
		
		phrase.go(
			0,
			pageFormat.getPageHeight(),
			1,
			1,
			0,
			0,
			PdfTextAlignment.LEFT,
			TextDirection.DEFAULT
			);

		tagHelper.endPageAnchor();
	}

	/**
	 *
	 */
	protected void setPageSize(JRPrintPage page) throws JRException, IOException
	{
		int minX = pageFormat.getLeftMargin();
		int minY = pageFormat.getTopMargin();
		int pageWidth = 0; 
		int pageHeight = 0;

		if (page != null)
		{
			Collection<JRPrintElement> elements = page.getElements();
			for (JRPrintElement element : elements)
			{
				int elementRight = element.getX() + element.getWidth();
				int elementBottom = element.getY() + element.getHeight();
				minX = minX < element.getX() ? minX : element.getX();
				minY = minY < element.getY() ? minY : element.getY();
				pageWidth = pageWidth < elementRight ? elementRight : pageWidth;
				pageHeight = pageHeight < elementBottom ? elementBottom : pageHeight;
			}
			
			pageWidth += pageFormat.getRightMargin();
			pageHeight += pageFormat.getBottomMargin();
		}
		
		crtPageOffsetX = minX < pageFormat.getLeftMargin() ? pageFormat.getLeftMargin() - minX : 0;
		crtPageOffsetY = minY < pageFormat.getTopMargin() ? pageFormat.getTopMargin() - minY : 0;
		pageWidth += crtPageOffsetX;
		pageHeight += crtPageOffsetY;
		pageWidth = pageWidth < pageFormat.getPageWidth() ? pageFormat.getPageWidth() : pageWidth; 
		pageHeight = pageHeight < pageFormat.getPageHeight() ? pageFormat.getPageHeight() : pageHeight; 
		
		pdfProducer.setPageSize(pageFormat, pageWidth, pageHeight);
	}

	/**
	 *
	 */
	protected void exportPage(JRPrintPage page) throws JRException, IOException
	{
		tagHelper.startPage();
		
		Collection<JRPrintElement> elements = page.getElements();
		exportElements(elements);

		pdfProducer.endPage();
		
		tagHelper.endPage();

		JRExportProgressMonitor progressMonitor = getCurrentItemConfiguration().getProgressMonitor();
		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport();
		}
	}

	protected void exportElements(Collection<JRPrintElement> elements) throws IOException, JRException
	{
		if (elements != null && elements.size() > 0)
		{
			for(Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
			{
				checkInterrupted();
				
				JRPrintElement element = it.next();

				if (filter == null || filter.isToExport(element))
				{
					tagHelper.startElement(element);

					String strFieldType = element.getPropertiesMap().getProperty(PDF_FIELD_TYPE);
					PdfFieldTypeEnum fieldType = PdfFieldTypeEnum.getByName(strFieldType);
					if (fieldType == PdfFieldTypeEnum.CHECK)
					{
						exportFieldCheck(element);
					}
					else if (fieldType == PdfFieldTypeEnum.RADIO)
					{
						exportFieldRadio(element);
					}
					else if (element instanceof JRPrintLine)
					{
						exportLine((JRPrintLine)element);
					}
					else if (element instanceof JRPrintRectangle)
					{
						exportRectangle((JRPrintRectangle)element);
					}
					else if (element instanceof JRPrintEllipse)
					{
						exportEllipse((JRPrintEllipse)element);
					}
					else if (element instanceof JRPrintImage)
					{
						exportImage((JRPrintImage)element);
					}
					else if (element instanceof JRPrintText)
					{
						if (
							fieldType == PdfFieldTypeEnum.TEXT
							|| fieldType == PdfFieldTypeEnum.COMBO
							|| fieldType == PdfFieldTypeEnum.LIST
							)
						{
							exportFieldText((JRPrintText)element, fieldType);
						}
						else
						{
							exportText((JRPrintText)element);
						}
					}
					else if (element instanceof JRPrintFrame)
					{
						exportFrame((JRPrintFrame)element);
					}
					else if (element instanceof JRGenericPrintElement)
					{
						exportGenericElement((JRGenericPrintElement) element);
					}

					tagHelper.endElement(element);
				}
			}
		}
	}


	/**
	 *
	 */
	protected void exportLine(JRPrintLine line)
	{
		int lcOffsetX = getOffsetX();
		int lcOffsetY = getOffsetY();

		float lineWidth = line.getLinePen().getLineWidth(); 
		if (lineWidth > 0f)
		{
			preparePen(line.getLinePen(), LineCapStyle.BUTT);

			if (line.getWidth() == 1)
			{
				if (line.getHeight() != 1)
				{
					//Vertical line
					if (line.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
					{
						pdfContent.strokeLine(
							line.getX() + lcOffsetX + 0.5f - lineWidth / 3,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY,
							line.getX() + lcOffsetX + 0.5f - lineWidth / 3,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight()
							);
						
						pdfContent.strokeLine(
							line.getX() + lcOffsetX + 0.5f + lineWidth / 3,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY,
							line.getX() + lcOffsetX + 0.5f + lineWidth / 3,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight()
							);
					}
					else
					{
						pdfContent.strokeLine(
							line.getX() + lcOffsetX + 0.5f,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY,
							line.getX() + lcOffsetX + 0.5f,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight()
							);
					}
				}
			}
			else
			{
				if (line.getHeight() == 1)
				{
					//Horizontal line
					if (line.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
					{
						pdfContent.strokeLine(
							line.getX() + lcOffsetX,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - 0.5f + lineWidth / 3,
							line.getX() + lcOffsetX + line.getWidth(),
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - 0.5f + lineWidth / 3
							);
						
						pdfContent.strokeLine(
							line.getX() + lcOffsetX,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - 0.5f - lineWidth / 3,
							line.getX() + lcOffsetX + line.getWidth(),
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - 0.5f - lineWidth / 3
							);
					}
					else
					{
						pdfContent.strokeLine(
							line.getX() + lcOffsetX,
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - 0.5f,
							line.getX() + lcOffsetX + line.getWidth(),
							pageFormat.getPageHeight() - line.getY() - lcOffsetY - 0.5f
							);
					}
				}
				else
				{
					//Oblique line
					if (line.getDirection() != LineDirectionEnum.BOTTOM_UP)
					{
						if (line.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
						{
							double xtrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getWidth(), 2) / Math.pow(line.getHeight(), 2))); 
							double ytrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getHeight(), 2) / Math.pow(line.getWidth(), 2))); 
							
							pdfContent.strokeLine(
								line.getX() + lcOffsetX + (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY + (float)ytrans,
								line.getX() + lcOffsetX + line.getWidth() + (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight() + (float)ytrans
								);
							
							pdfContent.strokeLine(
								line.getX() + lcOffsetX - (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - (float)ytrans,
								line.getX() + lcOffsetX + line.getWidth() - (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight() - (float)ytrans
								);
						}
						else
						{
							pdfContent.strokeLine(
								line.getX() + lcOffsetX,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY,
								line.getX() + lcOffsetX + line.getWidth(),
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight()
								);
						}
					}
					else
					{
						if (line.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
						{
							double xtrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getWidth(), 2) / Math.pow(line.getHeight(), 2))); 
							double ytrans = lineWidth / (3 * Math.sqrt(1 + Math.pow(line.getHeight(), 2) / Math.pow(line.getWidth(), 2))); 
							
							pdfContent.strokeLine(
								line.getX() + lcOffsetX + (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight() - (float)ytrans,
								line.getX() + lcOffsetX + line.getWidth() + (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - (float)ytrans
								);

							pdfContent.strokeLine(
								line.getX() + lcOffsetX - (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight() + (float)ytrans,
								line.getX() + lcOffsetX + line.getWidth() - (float)xtrans,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY + (float)ytrans
								);
						}
						else
						{
							pdfContent.strokeLine(
								line.getX() + lcOffsetX,
								pageFormat.getPageHeight() - line.getY() - lcOffsetY - line.getHeight(),
								line.getX() + lcOffsetX + line.getWidth(),
								pageFormat.getPageHeight() - line.getY() - lcOffsetY
								);
						}
					}
				}
			}

			resetPen();
			pdfContent.setLineDash(0f);
			pdfContent.setLineCap(LineCapStyle.PROJECTING_SQUARE);
		}
	}


	/**
	 *
	 */
	protected void exportRectangle(JRPrintRectangle rectangle)
	{
		pdfContent.setFillColor(rectangle.getBackcolor());
		preparePen(rectangle.getLinePen(), LineCapStyle.PROJECTING_SQUARE);

		float lineWidth = rectangle.getLinePen().getLineWidth();
		int lcOffsetX = getOffsetX();
		int lcOffsetY = getOffsetY();
		
		if (rectangle.getMode() == ModeEnum.OPAQUE)
		{
			pdfContent.fillRoundRectangle(
				rectangle.getX() + lcOffsetX,
				pageFormat.getPageHeight() - rectangle.getY() - lcOffsetY - rectangle.getHeight(),
				rectangle.getWidth(),
				rectangle.getHeight(),
				rectangle.getRadius()
				);
		}

		if (lineWidth > 0f)
		{
			if (rectangle.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
			{
				pdfContent.strokeRoundRectangle(
					rectangle.getX() + lcOffsetX - lineWidth / 3,
					pageFormat.getPageHeight() - rectangle.getY() - lcOffsetY - rectangle.getHeight() - lineWidth / 3,
					rectangle.getWidth() + 2 * lineWidth / 3,
					rectangle.getHeight() + 2 * lineWidth / 3,
					rectangle.getRadius()
					);
				
				pdfContent.strokeRoundRectangle(
					rectangle.getX() + lcOffsetX + lineWidth / 3,
					pageFormat.getPageHeight() - rectangle.getY() - lcOffsetY - rectangle.getHeight() + lineWidth / 3,
					rectangle.getWidth() - 2 * lineWidth / 3,
					rectangle.getHeight() - 2 * lineWidth / 3,
					rectangle.getRadius()
					);
			}
			else
			{
				pdfContent.strokeRoundRectangle(
					rectangle.getX() + lcOffsetX,
					pageFormat.getPageHeight() - rectangle.getY() - lcOffsetY - rectangle.getHeight(),
					rectangle.getWidth(),
					rectangle.getHeight(),
					rectangle.getRadius()
					);
			}
		}

		resetPen();
		pdfContent.resetFillColor();
		pdfContent.setLineDash(0f);
	}


	/**
	 *
	 */
	protected void exportEllipse(JRPrintEllipse ellipse)
	{
		pdfContent.setFillColor(ellipse.getBackcolor());
		preparePen(ellipse.getLinePen(), LineCapStyle.PROJECTING_SQUARE);

		float lineWidth = ellipse.getLinePen().getLineWidth();
		int lcOffsetX = getOffsetX();
		int lcOffsetY = getOffsetY();
		
		if (ellipse.getMode() == ModeEnum.OPAQUE)
		{
			pdfContent.fillEllipse(
				ellipse.getX() + lcOffsetX,
				pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY - ellipse.getHeight(),
				ellipse.getX() + lcOffsetX + ellipse.getWidth(),
				pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY
				);
		}

		if (lineWidth > 0f)
		{
			if (ellipse.getLinePen().getLineStyle() == LineStyleEnum.DOUBLE)
			{
				pdfContent.strokeEllipse(
					ellipse.getX() + lcOffsetX - lineWidth / 3,
					pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY - ellipse.getHeight() - lineWidth / 3,
					ellipse.getX() + lcOffsetX + ellipse.getWidth() + lineWidth / 3,
					pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY + lineWidth / 3
					);

				pdfContent.strokeEllipse(
					ellipse.getX() + lcOffsetX + lineWidth / 3,
					pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY - ellipse.getHeight() + lineWidth / 3,
					ellipse.getX() + lcOffsetX + ellipse.getWidth() - lineWidth / 3,
					pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY - lineWidth / 3
					);
			}
			else
			{
				pdfContent.strokeEllipse(
					ellipse.getX() + lcOffsetX,
					pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY - ellipse.getHeight(),
					ellipse.getX() + lcOffsetX + ellipse.getWidth(),
					pageFormat.getPageHeight() - ellipse.getY() - lcOffsetY
					);
			}
		}

		resetPen();
		pdfContent.resetFillColor();
		
		pdfContent.setLineDash(0f);
	}


	/**
	 *
	 */
	public void exportImage(JRPrintImage printImage) throws IOException,  JRException
	{
		if (printImage.getMode() == ModeEnum.OPAQUE)
		{
			pdfContent.setFillColor(printImage.getBackcolor());
			pdfContent.fillRectangle(
				printImage.getX() + getOffsetX(),
				pageFormat.getPageHeight() - printImage.getY() - getOffsetY(),
				printImage.getWidth(),
				- printImage.getHeight()
				);
			pdfContent.resetFillColor();
		}

		InternalImageProcessor imageProcessor =
			new InternalImageProcessor(printImage);
		
		Renderable renderer = printImage.getRenderer();

		if (
			renderer != null 
			&& imageProcessor.availableImageWidth > 0 
			&& imageProcessor.availableImageHeight > 0
			)
		{
			InternalImageProcessorResult imageProcessorResult = null;
			
			try
			{
				imageProcessorResult = imageProcessor.process(renderer);
			}
			catch (Exception e)
			{
				Renderable onErrorRenderer = getRendererUtil().handleImageError(e, printImage.getOnErrorType());
				if (onErrorRenderer != null)
				{
					imageProcessorResult = imageProcessor.process(onErrorRenderer);
				}
			}

			if (imageProcessorResult != null)
			{
				setAnchor(imageProcessorResult.chunk, printImage, printImage);

				PdfImage pxImage = getPxImage();
				pxImage.scaleAbsolute(printImage.getWidth(), printImage.getHeight());
				PdfChunk pxChunk = pdfProducer.createChunk(pxImage);

				boolean wasHyperlinkSet = setHyperlinkInfo(pxChunk, printImage);
				boolean usePxImage = (tagHelper.isTagged && printImage.getHyperlinkTooltip() != null) || wasHyperlinkSet;

				tagHelper.startImage(printImage);
				
				PdfPhrase phrase = pdfProducer.createPhrase(imageProcessorResult.chunk);
				
				int upperY = pageFormat.getPageHeight() - printImage.getY() - imageProcessor.topPadding - getOffsetY() - imageProcessorResult.yoffset;
				int lowerX = printImage.getX() + imageProcessor.leftPadding + getOffsetX() + imageProcessorResult.xoffset;
				phrase.go(
					lowerX,
					upperY,
					lowerX + imageProcessorResult.scaledWidth,
					upperY - imageProcessorResult.scaledHeight,
					0,
					0,
					PdfTextAlignment.LEFT,
					TextDirection.DEFAULT
					);

				if (usePxImage)
				{
					PdfPhrase pxPhrase = pdfProducer.createPhrase(pxChunk);
					pxPhrase.go(
						printImage.getX() + getOffsetX(),
						pageFormat.getPageHeight() - printImage.getY() - getOffsetY(),
						printImage.getX() + getOffsetX() + printImage.getWidth(),
						pageFormat.getPageHeight() - printImage.getY() - getOffsetY() - printImage.getHeight(),
						0,
						0,
						PdfTextAlignment.LEFT,
						TextDirection.DEFAULT
						);
				}
	
				tagHelper.endImage();
			}
		}


		if (
			printImage.getLineBox().getTopPen().getLineWidth() <= 0f &&
			printImage.getLineBox().getLeftPen().getLineWidth() <= 0f &&
			printImage.getLineBox().getBottomPen().getLineWidth() <= 0f &&
			printImage.getLineBox().getRightPen().getLineWidth() <= 0f
			)
		{
			if (printImage.getLinePen().getLineWidth() > 0f)
			{
				exportPen(printImage.getLinePen(), printImage);
			}
		}
		else
		{
			/*   */
			exportBox(
				printImage.getLineBox(),
				printImage
				);
		}
	}

	private class InternalImageProcessor
	{
		private final JRPrintImage printImage;
		private final RenderersCache imageRenderersCache;
		
		private final int topPadding;
		private final int leftPadding;
		private final int bottomPadding;
		private final int rightPadding;

		private final int availableImageWidth;
		private final int availableImageHeight;
		
		private InternalImageProcessor(JRPrintImage printImage)
		{
			this.printImage = printImage;
			this.imageRenderersCache = printImage.isUsingCache() ? renderersCache : new RenderersCache(getJasperReportsContext());
			
			topPadding = printImage.getLineBox().getTopPadding();
			leftPadding = printImage.getLineBox().getLeftPadding();
			bottomPadding = printImage.getLineBox().getBottomPadding();
			rightPadding = printImage.getLineBox().getRightPadding();

			int tmpAvailableImageWidth = printImage.getWidth() - leftPadding - rightPadding;
			availableImageWidth = tmpAvailableImageWidth < 0 ? 0 : tmpAvailableImageWidth;

			int tmpAvailableImageHeight = printImage.getHeight() - topPadding - bottomPadding;
			availableImageHeight = tmpAvailableImageHeight < 0 ? 0 : tmpAvailableImageHeight;
		}
		
		private InternalImageProcessorResult process(Renderable renderer) throws JRException, IOException
		{
			InternalImageProcessorResult imageProcessorResult = null;

			if (renderer instanceof ResourceRenderer)
			{
				renderer = imageRenderersCache.getLoadedRenderer((ResourceRenderer)renderer);
			}
			
			if (renderer instanceof Graphics2DRenderable)
			{
				imageProcessorResult = processGraphics2D(renderer.getId(), (Graphics2DRenderable)renderer);
			}
			else if (renderer instanceof DataRenderable)
			{
				boolean isSvgData = getRendererUtil().isSvgData((DataRenderable)renderer);
				
				if (isSvgData)
				{
					imageProcessorResult = 
						processGraphics2D(
							renderer.getId(),
							new WrappingSvgDataToGraphics2DRenderer((DataRenderable)renderer)
							);
				}
				else
				{
					switch(printImage.getScaleImage())
					{
						case CLIP :
						{
							imageProcessorResult = imageClip(getImageFromCache(renderer.getId(), (DataRenderable)renderer));
							break;
						}
						case FILL_FRAME :
						{
							imageProcessorResult = imageFillFrame(getImageFromCache(renderer.getId(), (DataRenderable)renderer));
							break;
						}
						case RETAIN_SHAPE :
						default :
						{
							imageProcessorResult = imageRetainShape(getImageFromCache(renderer.getId(), (DataRenderable)renderer));
						}
					}
				}
			}
			else
			{
				throw 
					new JRException(
						RendererUtil.EXCEPTION_MESSAGE_KEY_RENDERABLE_MUST_IMPLEMENT_INTERFACE,
						new Object[]{
							renderer.getClass().getName(),
							DataRenderable.class.getName() 
								+ " or " + Graphics2DRenderable.class.getName() 
							}
						);
			}

			return imageProcessorResult;
		}
		
		private Pair<PdfImage, ExifOrientationEnum> getImageFromCache(String rendererId, DataRenderable renderer) throws JRException
		{
			Pair<PdfImage, ExifOrientationEnum> imagePair = null;
			
			if (printImage.isUsingCache() && loadedImagesMap.containsKey(rendererId))
			{
				imagePair = loadedImagesMap.get(rendererId);
			}
			else
			{
				byte[] data = renderer.getData(jasperReportsContext);
				
				if (ImageTypeEnum.WEBP == JRTypeSniffer.getImageTypeValue(data))
				{
					WrappingImageDataToGraphics2DRenderer graphics2DRenderer = new WrappingImageDataToGraphics2DRenderer(renderer);
					data = new WrappingRenderToImageDataRenderer(graphics2DRenderer, graphics2DRenderer, null).getData(jasperReportsContext);
				}
				
				try
				{
					imagePair = 
						new Pair<>(
							pdfProducer.createImage(data, true), 
							ImageUtil.getExifOrientation(data)
							);
				}
				catch (Exception e)
				{
					throw new JRException(e);
				}

				if (printImage.isUsingCache())
				{
					loadedImagesMap.put(rendererId, imagePair);
				}
			}
			
			return imagePair;
		}
			
		private InternalImageProcessorResult imageClip(Pair<PdfImage, ExifOrientationEnum> imagePair) throws JRException
		{
			PdfImage image = imagePair.first();

			image.scalePercent(100f); // reset scaling and rotation here for images taken from cache, because it affects the plain size used for clipping
			image.setRotationDegrees(0);
			
			int plainWidth = (int)image.getPlainWidth();
			int plainHeight = (int)image.getPlainHeight();

			int clipWidth = Math.min(plainWidth, availableImageWidth);
			int clipHeight = Math.min(plainHeight, availableImageHeight);
			int xoffset = (int)(ImageUtil.getXAlignFactor(printImage) * (availableImageWidth - plainWidth));
			int yoffset = (int)(ImageUtil.getYAlignFactor(printImage) * (availableImageHeight - plainHeight));
			int translateX = xoffset;
			int translateY = yoffset;
			int angle = 0;
			
			switch (ImageUtil.getRotation(printImage.getRotation(), imagePair.second()))
			{
				case LEFT :
				{
					clipWidth = Math.min(plainWidth, availableImageHeight);
					clipHeight = Math.min(plainHeight, availableImageWidth);
					xoffset = (int)(ImageUtil.getYAlignFactor(printImage) * (availableImageWidth - plainHeight));
					yoffset = (int)((1f - ImageUtil.getXAlignFactor(printImage)) * (availableImageHeight - plainWidth));
					translateX = (int)(ImageUtil.getXAlignFactor(printImage) * (availableImageHeight - plainWidth));
					translateY = xoffset;
					angle = 90;
					break;
				}
				case RIGHT :
				{
					clipWidth = Math.min(plainWidth, availableImageHeight);
					clipHeight = Math.min(plainHeight, availableImageWidth);
					xoffset = (int)((1f - ImageUtil.getYAlignFactor(printImage)) * (availableImageWidth - plainHeight));
					yoffset = (int)(ImageUtil.getXAlignFactor(printImage) * (availableImageHeight - plainWidth));
					translateX = (int)(ImageUtil.getXAlignFactor(printImage) * (availableImageHeight - plainWidth));
					translateY = (int)(ImageUtil.getYAlignFactor(printImage) * (availableImageWidth - plainHeight));
					angle = -90;
					break;
				}
				case UPSIDE_DOWN :
				{
					clipWidth = Math.min(plainWidth, availableImageWidth);
					clipHeight = Math.min(plainHeight, availableImageHeight);
					xoffset = (int)((1f - ImageUtil.getXAlignFactor(printImage)) * (availableImageWidth - plainWidth));
					yoffset = (int)((1f - ImageUtil.getYAlignFactor(printImage)) * (availableImageHeight - plainHeight));
					translateX = (int)(ImageUtil.getXAlignFactor(printImage) * (availableImageWidth - plainWidth));
					translateY = (int)(ImageUtil.getYAlignFactor(printImage) * (availableImageHeight - plainHeight));
					angle = 180;
					break;
				}
				case NONE :
				default :
				{
				}
			}

			translateX = translateX > 0 ? 0 : translateX;
			translateY = translateY > 0 ? 0 : translateY;

			image = pdfProducer.clipImage(image, clipWidth, clipHeight, translateX, translateY);

			image.scaleAbsolute(plainWidth, plainHeight);
			image.setRotationDegrees(angle);

			PdfChunk chunk = pdfProducer.createChunk(image);
			return 
				new InternalImageProcessorResult(
					chunk, 
					image.getScaledWidth(), 
					image.getScaledHeight(),
					xoffset,
					yoffset
					);
		}

		private InternalImageProcessorResult imageFillFrame(Pair<PdfImage, ExifOrientationEnum> imagePair) throws JRException
		{
			PdfImage image = imagePair.first();
			
			switch (ImageUtil.getRotation(printImage.getRotation(), imagePair.second()))
			{
				case LEFT :
				{
					image.scaleAbsolute(availableImageHeight, availableImageWidth);
					image.setRotationDegrees(90);
					break;
				}
				case RIGHT :
				{
					image.scaleAbsolute(availableImageHeight, availableImageWidth);
					image.setRotationDegrees(-90);
					break;
				}
				case UPSIDE_DOWN :
				{
					image.scaleAbsolute(availableImageWidth, availableImageHeight);
					image.setRotationDegrees(180);
					break;
				}
				case NONE :
				default :
				{
					image.scaleAbsolute(availableImageWidth, availableImageHeight);
					image.setRotationDegrees(0);
				}
			}
			
			PdfChunk chunk = pdfProducer.createChunk(image);
			return 
				new InternalImageProcessorResult(
					chunk, 
					image.getScaledWidth(), 
					image.getScaledHeight(),
					0,
					0
					);
		}

		private InternalImageProcessorResult imageRetainShape(Pair<PdfImage, ExifOrientationEnum> imagePair) throws JRException
		{
			float plainWidth = 0;
			float plainHeight = 0;
			int xoffset = 0;
			int yoffset = 0;

			PdfImage image = imagePair.first();
			
			image.setRotationDegrees(0); // reset angle here for images taken from cache, even if it gets set again later below, because it affects the way scaleToFit works
			
			switch (ImageUtil.getRotation(printImage.getRotation(), imagePair.second()))
			{
				case LEFT :
				{
					image.scaleToFit(availableImageHeight, availableImageWidth);
					plainWidth = image.getPlainHeight();
					plainHeight = image.getPlainWidth();
					image.setRotationDegrees(90);
					break;
				}
				case RIGHT :
				{
					image.scaleToFit(availableImageHeight, availableImageWidth);
					plainWidth = image.getPlainHeight();
					plainHeight = image.getPlainWidth();
					image.setRotationDegrees(-90);
					break;
				}
				case UPSIDE_DOWN :
				{
					image.scaleToFit(availableImageWidth, availableImageHeight);
					plainWidth = image.getPlainWidth();
					plainHeight = image.getPlainHeight();
					image.setRotationDegrees(180);
					break;
				}
				case NONE :
				default :
				{
					image.scaleToFit(availableImageWidth, availableImageHeight);
					plainWidth = image.getPlainWidth();
					plainHeight = image.getPlainHeight();
					image.setRotationDegrees(0);
				}
			}
			
			switch (printImage.getRotation())
			{
				case LEFT :
				{
					xoffset = (int)(ImageUtil.getYAlignFactor(printImage) * (availableImageWidth - plainWidth));
					yoffset = (int)((1f - ImageUtil.getXAlignFactor(printImage)) * (availableImageHeight - plainHeight));
					break;
				}
				case RIGHT :
				{
					xoffset = (int)((1f - ImageUtil.getYAlignFactor(printImage)) * (availableImageWidth - plainWidth));
					yoffset = (int)(ImageUtil.getXAlignFactor(printImage) * (availableImageHeight - plainHeight));
					break;
				}
				case UPSIDE_DOWN :
				{
					xoffset = (int)((1f - ImageUtil.getXAlignFactor(printImage)) * (availableImageWidth - plainWidth));
					yoffset = (int)((1f - ImageUtil.getYAlignFactor(printImage)) * (availableImageHeight - plainHeight));
					break;
				}
				case NONE :
				default :
				{
					xoffset = (int)(ImageUtil.getXAlignFactor(printImage) * (availableImageWidth - plainWidth));
					yoffset = (int)(ImageUtil.getYAlignFactor(printImage) * (availableImageHeight - plainHeight));
				}
			}

			xoffset = (xoffset < 0 ? 0 : xoffset);
			yoffset = (yoffset < 0 ? 0 : yoffset);
			
			PdfChunk chunk = pdfProducer.createChunk(image);
			return 
				new InternalImageProcessorResult(
					chunk, 
					image.getScaledWidth(), 
					image.getScaledHeight(),
					xoffset,
					yoffset
					);
		}
		
		private InternalImageProcessorResult processGraphics2D(String rendererId, Graphics2DRenderable renderer) throws JRException, IOException
		{
			Pair<PdfImage, ExifOrientationEnum> imagePair = null;
			
			if (printImage.isUsingCache() && loadedImagesMap.containsKey(rendererId))
			{
				imagePair = loadedImagesMap.get(rendererId);
			}
			else
			{
				double renderWidth = 0;
				double renderHeight = 0;
				
				Dimension2D dimension = 
					renderer instanceof DimensionRenderable 
					? ((DimensionRenderable)renderer).getDimension(jasperReportsContext) 
					: null;
				if (dimension == null)
				{
					renderWidth = availableImageWidth;
					renderHeight = availableImageHeight;
				}
				else
				{
					renderWidth = dimension.getWidth();
					renderHeight = dimension.getHeight();
				}
				
				PdfImage image = 
					pdfProducer.drawImage(
						printImage, renderer, getCurrentItemConfiguration().isForceSvgShapes(), 
						renderWidth,  renderHeight 
						);
				
				try
				{
					imagePair = 
						new Pair<>(
							image, 
							ExifOrientationEnum.NORMAL
							);
				}
				catch (Exception e)
				{
					throw new JRException(e);
				}

				if (printImage.isUsingCache())
				{
					loadedImagesMap.put(rendererId, imagePair);
				}
			}
			
			InternalImageProcessorResult imageProcessorResult = null;

			switch(printImage.getScaleImage())
			{
				case CLIP :
				{
					imageProcessorResult = imageClip(imagePair);
					break;
				}
				case FILL_FRAME :
				{
					imageProcessorResult = imageFillFrame(imagePair);
					break;
				}
				case RETAIN_SHAPE :
				default :
				{
					imageProcessorResult = imageRetainShape(imagePair);
				}
			}

			return imageProcessorResult;
		}
	}

	private class InternalImageProcessorResult
	{
		private final PdfChunk chunk;
		private final float scaledWidth;
		private final float scaledHeight;
		private final int xoffset;
		private final int yoffset;
		
		private InternalImageProcessorResult(
				PdfChunk chunk,
				float scaledWidth,
				float scaledHeight,
				int xoffset,
				int yoffset
			)
		{
			this.chunk = chunk;
			this.scaledWidth = scaledWidth;
			this.scaledHeight = scaledHeight;
			this.xoffset = xoffset;
			this.yoffset = yoffset;
		}
	}


	/**
	 *
	 */
	protected boolean setHyperlinkInfo(PdfChunk chunk, JRPrintHyperlink link)
	{
		boolean wasHyperlinkSet = false;

		if (link != null)
		{
			Boolean ignoreHyperlink = HyperlinkUtil.getIgnoreHyperlink(PdfReportConfiguration.PROPERTY_IGNORE_HYPERLINK, link);
			if (ignoreHyperlink == null)
			{
				ignoreHyperlink = getCurrentItemConfiguration().isIgnoreHyperlink();
			}
			
			if (!ignoreHyperlink)
			{
				switch(link.getHyperlinkType())
				{
					case REFERENCE :
					{
						JRHyperlinkProducer hyperlinkProducer = getHyperlinkProducer(link);
						String referenceURL = hyperlinkProducer == null ? link.getHyperlinkReference() : hyperlinkProducer.getHyperlink(link);
						wasHyperlinkSet = setReferenceHyperlink(chunk, link, referenceURL);
						break;
					}
					case LOCAL_ANCHOR :
					{
						if (link.getHyperlinkAnchor() != null)
						{
							chunk.setLocalGoto(link.getHyperlinkAnchor());
							wasHyperlinkSet = true;
						}
						break;
					}
					case LOCAL_PAGE :
					{
						if (link.getHyperlinkPage() != null)
						{
							chunk.setLocalGoto(JR_PAGE_ANCHOR_PREFIX + reportIndex + "_" + link.getHyperlinkPage().toString());
							wasHyperlinkSet = true;
						}
						break;
					}
					case REMOTE_ANCHOR :
					{
						if (
							link.getHyperlinkReference() != null &&
							link.getHyperlinkAnchor() != null
							)
						{
							chunk.setRemoteGoto(
								link.getHyperlinkReference(),
								link.getHyperlinkAnchor()
								);
							wasHyperlinkSet = true;
						}
						break;
					}
					case REMOTE_PAGE :
					{
						if (
							link.getHyperlinkReference() != null &&
							link.getHyperlinkPage() != null
							)
						{
							chunk.setRemoteGoto(
								link.getHyperlinkReference(),
								link.getHyperlinkPage()
								);
							wasHyperlinkSet = true;
						}
						break;
					}
					case CUSTOM :
					{
						JRHyperlinkProducerFactory hyperlinkProducerFactory = getCurrentItemConfiguration().getHyperlinkProducerFactory();
						if (hyperlinkProducerFactory != null)
						{
							String hyperlink = hyperlinkProducerFactory.produceHyperlink(link);
							wasHyperlinkSet = setReferenceHyperlink(chunk, link, hyperlink);
						}
						break;
					}
					case NONE :
					default :
					{
						break;
					}
				}
			}
		}

		return wasHyperlinkSet;
	}

	protected boolean setReferenceHyperlink(PdfChunk chunk, JRPrintHyperlink link, String referenceURL)
	{
		boolean wasHyperlinkSet = false;

		if (referenceURL != null)
		{
			switch(link.getHyperlinkTarget())
			{
				case BLANK :
				{
					if (legacyTargetBlankLinks)
					{
						chunk.setJavaScriptAction(
								"if (app.viewerVersion < 7)"
									+ "{this.getURL(\"" + referenceURL + "\");}"
									+ "else {app.launchURL(\"" + referenceURL + "\", true);};"
							);
						wasHyperlinkSet = true;
						break;
					}
				}
				case SELF :
				default :
				{
					chunk.setAnchor(referenceURL);
					wasHyperlinkSet = true;
					break;
				}
			}
		}

		return wasHyperlinkSet;
	}
	
	@Override
	protected Locale getTextLocale(JRPrintText text)
	{
		// only overriding for package access
		return super.getTextLocale(text);
	}


	/**
	 *
	 */
	protected void getPhrase(AttributedString as, String text, JRPrintText textElement,
			PdfPhrase phrase)
	{
		getPhrase(as, 0, text.length(), text, textElement, phrase);
	}

	/**
	 *
	 */
	protected void getPhrase(AttributedString as, int beginIndex, int endIndex, String text, JRPrintText textElement,
			PdfPhrase phrase)
	{
		int runLimit = beginIndex;

		AttributedCharacterIterator iterator = as.getIterator(null, beginIndex, endIndex);
		Locale locale = getTextLocale(textElement);
		 
		boolean firstChunk = true; //FIXMENOW will have multiple anchors in case multiple paragraphs in same text element?
		while (runLimit < endIndex && (runLimit = iterator.getRunLimit()) <= endIndex)
		{
			Map<Attribute,Object> attributes = iterator.getAttributes();
			PdfTextChunk chunk = getChunk(attributes, text.substring(iterator.getIndex(), runLimit), locale);
			
			if (firstChunk)
			{
				// only set anchor + bookmark for the first chunk in the text
				setAnchor(chunk, textElement, textElement);
			}
			
			JRPrintHyperlink hyperlink = textElement;
			if (hyperlink.getHyperlinkType() == HyperlinkTypeEnum.NONE)
			{
				hyperlink = (JRPrintHyperlink)attributes.get(JRTextAttribute.HYPERLINK);
			}
			
			setHyperlinkInfo(chunk, hyperlink);
			phrase.add(chunk);

			iterator.setIndex(runLimit);
			firstChunk = false;
		}
	}


	/**
	 *
	 */
	protected PdfTextChunk getChunk(Map<Attribute,Object> attributes, String text, Locale locale)
	{
		// underline and strikethrough are set on the chunk below
		PdfTextChunk chunk = pdfProducer.createChunk(text, attributes, locale);
		
		if (hasUnderline(attributes))
		{
			chunk.setUnderline();
		}
		
		if (hasStrikethrough(attributes))
		{
			chunk.setStrikethrough();
		}

		Color backcolor = (Color)attributes.get(TextAttribute.BACKGROUND);
		if (backcolor != null)
		{
			chunk.setBackground(backcolor);
		}

		Object script = attributes.get(TextAttribute.SUPERSCRIPT);
		if (script != null)
		{
			if (TextAttribute.SUPERSCRIPT_SUPER.equals(script))
			{
				chunk.setSuperscript();
			}
			else if (TextAttribute.SUPERSCRIPT_SUB.equals(script))
			{
				chunk.setSubscript();
			}
		}

		return chunk;
	}

	protected boolean hasUnderline(Map<Attribute,Object> textAttributes)
	{
		Integer underline = (Integer) textAttributes.get(TextAttribute.UNDERLINE);
		return TextAttribute.UNDERLINE_ON.equals(underline);
	}

	protected boolean hasStrikethrough(Map<Attribute,Object> textAttributes)
	{
		Boolean strike = (Boolean) textAttributes.get(TextAttribute.STRIKETHROUGH);
		return TextAttribute.STRIKETHROUGH_ON.equals(strike);
	}


	/**
	 * Creates a PDF font.
	 * 
	 * @param attributes the text attributes of the font
	 * @param locale the locale for which to create the font
	 * @param setFontLines whether to set underline and strikethrough as font style
	 * @param recipient the font recipient
	 */
	protected void setFont(Map<Attribute,Object> attributes, Locale locale, boolean setFontLines,
			FontRecipient recipient)
	{
		JRFont jrFont = new JRBaseFont(attributes);

		Exception initialException = null;

		Color forecolor = (Color)attributes.get(TextAttribute.FOREGROUND);

		// use the same font scale ratio as in JRStyledText.getAwtAttributedString
		float fontSizeScale = 1f;
		Integer scriptStyle = (Integer) attributes.get(TextAttribute.SUPERSCRIPT);
		if (scriptStyle != null && (
				TextAttribute.SUPERSCRIPT_SUB.equals(scriptStyle)
				|| TextAttribute.SUPERSCRIPT_SUPER.equals(scriptStyle)))
		{
			fontSizeScale = 2f / 3;
		}
		
		String pdfFontName = null;
		String pdfEncoding = null;
		boolean isPdfEmbedded = false;
		boolean isPdfSimulatedBold = false;
		boolean isPdfSimulatedItalic = false;

		FontInfo fontInfo = (FontInfo) attributes.get(JRTextAttribute.FONT_INFO);
		if (fontInfo == null)
		{
			fontInfo = fontUtil.getFontInfo(jrFont.getFontName(), locale);
		}
		
		if (fontInfo == null)
		{
			//fontName NOT found in font extensions
			pdfFontName = jrFont.getPdfFontName();
			pdfEncoding = jrFont.getPdfEncoding();
			isPdfEmbedded = jrFont.isPdfEmbedded();
		}
		else
		{
			//fontName found in font extensions
			FontFamily family = fontInfo.getFontFamily();
			
			int pdfFontStyle = java.awt.Font.PLAIN;
			
			FontFace fontFace = fontInfo.getFontFace();
			if (fontFace != null)
			{
				pdfFontName = fontFace.getPdf();
				pdfFontName = pdfFontName == null ? fontFace.getTtf() : pdfFontName;
				pdfFontStyle = fontInfo.getStyle();
			}
			
			if (pdfFontName == null && jrFont.isBold() && jrFont.isItalic())
			{
				fontFace = family.getBoldItalicFace();
				if (fontFace != null)
				{
					pdfFontName = fontFace.getPdf();
					pdfFontName = pdfFontName == null ? fontFace.getTtf() : pdfFontName;
					pdfFontStyle = java.awt.Font.BOLD | java.awt.Font.ITALIC;
				}
			}
			
			if (pdfFontName == null && jrFont.isBold())
			{
				fontFace = family.getBoldFace();
				if (fontFace != null)
				{
					pdfFontName = fontFace.getPdf();
					pdfFontName = pdfFontName == null ? fontFace.getTtf() : pdfFontName;
					pdfFontStyle = java.awt.Font.BOLD;
				}
			}
			
			if (pdfFontName == null && jrFont.isItalic())
			{
				fontFace = family.getItalicFace();
				if (fontFace != null)
				{
					pdfFontName = fontFace.getPdf();
					pdfFontName = pdfFontName == null ? fontFace.getTtf() : pdfFontName;
					pdfFontStyle = java.awt.Font.ITALIC;
				}
			}
			
			if (pdfFontName == null)
			{
				fontFace = family.getNormalFace();
				if (fontFace != null)
				{
					pdfFontName = fontFace.getPdf();
					pdfFontName = pdfFontName == null ? fontFace.getTtf() : pdfFontName;
					pdfFontStyle = java.awt.Font.PLAIN;
				}
			}

			if (pdfFontName == null)
			{
				pdfFontName = jrFont.getPdfFontName();
			}

			pdfEncoding = family.getPdfEncoding() == null ? jrFont.getPdfEncoding() : family.getPdfEncoding();
			isPdfEmbedded = family.isPdfEmbedded() == null ? jrFont.isPdfEmbedded() : family.isPdfEmbedded(); 
			isPdfSimulatedBold = jrFont.isBold() && ((pdfFontStyle & java.awt.Font.BOLD) == 0); 
			isPdfSimulatedItalic = jrFont.isItalic() && ((pdfFontStyle & java.awt.Font.ITALIC) == 0); 
		}

		PdfFontStyle pdfFontStyle = new PdfFontStyle(isPdfSimulatedBold, isPdfSimulatedItalic,
				setFontLines && jrFont.isUnderline(), setFontLines && jrFont.isStrikeThrough());
		
		try
		{
			recipient.setFont(
				pdfFontName,
				pdfEncoding,
				isPdfEmbedded,
				jrFont.getFontSize() * fontSizeScale,
				pdfFontStyle,
				forecolor
				);
		}
		catch(Exception e)
		{
			initialException = e;
		}

		if (!recipient.hasFont())
		{
			byte[] bytes = null;

			try
			{
				bytes = getRepository().getBytesFromLocation(pdfFontName);
			}
			catch(JRException e)
			{
				throw //NOPMD
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_FONT_LOADING_ERROR,
						new Object[]{pdfFontName, pdfEncoding, isPdfEmbedded},
						initialException);
			}

			recipient.setFont(pdfFontName, pdfEncoding, isPdfEmbedded, 
					jrFont.getFontSize() * fontSizeScale, pdfFontStyle, forecolor, bytes);
		}
	}


	/**
	 *
	 */
	public void exportText(JRPrintText text)
	{
		JRStyledText styledText = styledTextUtil.getProcessedStyledText(text, noBackcolorSelector, null);

		if (styledText == null)
		{
			return;
		}
		
		AbstractPdfTextRenderer textRenderer = getTextRenderer(text, styledText);
		textRenderer.initialize(this, pdfProducer, tagHelper, text, styledText, getOffsetX(), getOffsetY());

		double angle = 0;

		switch (text.getRotation())
		{
			case LEFT :
			{
				angle = Math.PI / 2;
				break;
			}
			case RIGHT :
			{
				angle = - Math.PI / 2;
				break;
			}
			case UPSIDE_DOWN :
			{
				angle = Math.PI;
				break;
			}
			case NONE :
			default :
			{
			}
		}

		AffineTransform atrans = new AffineTransform();
		atrans.rotate(angle, textRenderer.getX(), pageFormat.getPageHeight() - textRenderer.getY());
		pdfContent.transform(atrans);

		if (text.getMode() == ModeEnum.OPAQUE)
		{
			Color backcolor = text.getBackcolor();
			pdfContent.setFillColor(backcolor);
			pdfContent.fillRectangle(
				textRenderer.getX(),
				pageFormat.getPageHeight() - textRenderer.getY(),
				textRenderer.getWidth(),
				- textRenderer.getHeight()
				);
			pdfContent.resetFillColor();
		}
		
		int forecolorAlpha = getSingleForecolorAlpha(styledText);
		pdfContent.setFillColorAlpha(forecolorAlpha);
		
		/* rendering only non empty texts  */
		if (styledText.length() > 0)
		{
			textRenderer.render();
		}
		
		pdfContent.resetFillColor();

		atrans = new AffineTransform();
		atrans.rotate(-angle, textRenderer.getX(), pageFormat.getPageHeight() - textRenderer.getY());
		pdfContent.transform(atrans);

		/*   */
		exportBox(
			text.getLineBox(),
			text
			);
	}
	
	protected int getSingleForecolorAlpha(JRStyledText styledText)
	{
		Color forecolor = (Color) styledText.getGlobalAttributes().get(TextAttribute.FOREGROUND);
		if (forecolor == null || forecolor.getAlpha() == 255)
		{
			return 255;
		}
		
		List<JRStyledText.Run> runs = styledText.getRuns();
		if (runs.size() > 1)
		{
			for (JRStyledText.Run run : runs)
			{
				Color runForecolor = (Color) run.attributes.get(TextAttribute.FOREGROUND);
				if (runForecolor != null && runForecolor.getAlpha() != forecolor.getAlpha())
				{
					//per run alpha currently not working because there's no support in Chunk
					//falling back to opaque
					return 255;
				}
			}
		}
		
		return forecolor.getAlpha();
	}
	
	/**
	 *
	 */
	public void exportFieldText(JRPrintText text, PdfFieldTypeEnum fieldType)
	{
		String fieldName = text.getPropertiesMap().getProperty(PDF_FIELD_NAME);
		fieldName = fieldName == null || fieldName.trim().length() == 0 ? "FIELD_" + text.getUUID() : fieldName;
		
		String value = null;
		if (text.getPropertiesMap().containsProperty(PDF_FIELD_VALUE))
		{
			value = text.getPropertiesMap().getProperty(PDF_FIELD_VALUE);
		}
		else
		{
			value = text.getFullText();
		}
		
		int llx = text.getX() + exporterContext.getOffsetX();
		int lly = jasperPrint.getPageHeight() - text.getY() - exporterContext.getOffsetY();
		int urx = llx + text.getWidth();
		int ury = jasperPrint.getPageHeight() - text.getY() - exporterContext.getOffsetY() - text.getHeight();
		
		PdfTextField pdfTextField;
		switch (fieldType)
		{
		case TEXT:
			pdfTextField = pdfProducer.createTextField(llx, lly, urx, ury, fieldName);			
			if (value != null)
			{
				pdfTextField.setText(value);
			}
			break;
		case COMBO:
			String[] comboChoices = getPdfFieldChoices(text);
			pdfTextField = pdfProducer.createComboField(llx, lly, urx, ury, fieldName, value, comboChoices);
			if (propertiesUtil.getBooleanProperty(PDF_FIELD_COMBO_EDIT, false, text, jasperPrint))
			{
				pdfTextField.setEdit();
			}
			break;
		case LIST:
			String[] listChoices = getPdfFieldChoices(text);
			pdfTextField = pdfProducer.createListField(llx, lly, urx, ury, fieldName, value, listChoices);			
			break;
		default:
			throw new JRRuntimeException("Unknown field type " + fieldType);
		}
		
		if (ModeEnum.OPAQUE == text.getMode())
		{
			pdfTextField.setBackgroundColor(text.getBackcolor());
		}
		pdfTextField.setTextColor(text.getForecolor());
		
		switch (text.getHorizontalTextAlign())
		{
			case RIGHT :
				pdfTextField.setAlignment(PdfTextAlignment.RIGHT);
				break;
			case CENTER :
				pdfTextField.setAlignment(PdfTextAlignment.CENTER);
				break;
			case JUSTIFIED :
				pdfTextField.setAlignment(PdfTextAlignment.JUSTIFIED);
				break;
			case LEFT :
			default :
				pdfTextField.setAlignment(PdfTextAlignment.LEFT);
		}
		
		JRPen pen = getFieldPen(text);
		if (pen != null)
		{
			float borderWidth = Math.round(pen.getLineWidth());
			if (borderWidth > 0)
			{
				pdfTextField.setBorderColor(pen.getLineColor());
				pdfTextField.setBorderWidth(borderWidth);
				String strBorderStyle = propertiesUtil.getProperty(PDF_FIELD_BORDER_STYLE, text, jasperPrint);
				PdfFieldBorderStyleEnum borderStyle = PdfFieldBorderStyleEnum.getByName(strBorderStyle);
				if (borderStyle == null)
				{
					borderStyle = pen.getLineStyle() == LineStyleEnum.DASHED ? PdfFieldBorderStyleEnum.DASHED : PdfFieldBorderStyleEnum.SOLID;
				}
				pdfTextField.setBorderStyle(borderStyle);
			}
		}

		String readOnly = text.getPropertiesMap().getProperty(PDF_FIELD_READ_ONLY);
		if (readOnly != null)
		{
			if (Boolean.valueOf(readOnly))
			{
				pdfTextField.setReadOnly();
			}
		}
		
//		pdfTextField.setExtraMargin(0, 0);
		
		Map<Attribute,Object> attributes = new HashMap<>();
		fontUtil.getAttributesWithoutAwtFont(attributes, text);
		pdfTextField.setFont(attributes, getLocale());
		pdfTextField.setFontSize(text.getFontSize());
//		pdfTextField.setExtensionFont(pdfFont.getBaseFont());
		
		boolean isMultiLine = JRPropertiesUtil.asBoolean(text.getPropertiesMap().getProperty(PDF_FIELD_TEXT_MULTILINE), false);
		if (isMultiLine)
		{
			pdfTextField.setMultiline();
		}
		
		if (pageFormat.getOrientation() == OrientationEnum.LANDSCAPE)
		{
			pdfTextField.setRotation(90);
		}
		pdfTextField.setVisible();

		pdfTextField.add();
	}

	protected String[] getPdfFieldChoices(JRPrintText text)
	{
		String[] choices = null;
		String strChoices = text.getPropertiesMap().getProperty(PDF_FIELD_CHOICES);
		if (strChoices != null && strChoices.trim().length() > 0)
		{
			String choiceSeparators = propertiesUtil.getProperty(PDF_FIELD_CHOICE_SEPARATORS, text, jasperPrint);
			StringTokenizer tkzer = new StringTokenizer(strChoices, choiceSeparators);
			List<String> choicesList = new ArrayList<>();
			while (tkzer.hasMoreTokens())
			{
				choicesList.add(tkzer.nextToken());
			}
			choices = choicesList.toArray(new String[choicesList.size()]);
		}
		return choices;
	}
	
	/**
	 *
	 */
	public void exportFieldCheck(JRPrintElement element)
	{
		String fieldName = element.getPropertiesMap().getProperty(PDF_FIELD_NAME);
		fieldName = fieldName == null || fieldName.trim().length() == 0 ? "FIELD_" + element.getUUID() : fieldName;
		
		PdfRadioCheck checkField = pdfProducer.createCheckField(
				element.getX() + exporterContext.getOffsetX(),
				jasperPrint.getPageHeight() - element.getY() - exporterContext.getOffsetY(),
				element.getX() + exporterContext.getOffsetX() + element.getWidth(),
				jasperPrint.getPageHeight() - element.getY() - exporterContext.getOffsetY() - element.getHeight(),
				fieldName,
				"checked"
		);
		
		PdfFieldCheckTypeEnum checkType = PdfFieldCheckTypeEnum.getByName(element.getPropertiesMap().getProperty(PDF_FIELD_CHECK_TYPE));
		if (checkType != null)
		{
			checkField.setCheckType(checkType);
		}

		if (ModeEnum.OPAQUE == element.getMode())
		{
			checkField.setBackgroundColor(element.getBackcolor());
		}
		checkField.setTextColor(element.getForecolor());

		JRPen pen = getFieldPen(element);
		if (pen != null)
		{
			float borderWidth = Math.round(pen.getLineWidth());
			if (borderWidth > 0)
			{
				checkField.setBorderColor(pen.getLineColor());
				checkField.setBorderWidth(borderWidth);
				String strBorderStyle = propertiesUtil.getProperty(PDF_FIELD_BORDER_STYLE, element, jasperPrint);
				PdfFieldBorderStyleEnum borderStyle = PdfFieldBorderStyleEnum.getByName(strBorderStyle);
				if (borderStyle == null)
				{
					borderStyle = pen.getLineStyle() == LineStyleEnum.DASHED ? PdfFieldBorderStyleEnum.DASHED : PdfFieldBorderStyleEnum.SOLID;
				}
				checkField.setBorderStyle(borderStyle);
			}
		}
		
		String checked = element.getPropertiesMap().getProperty(PDF_FIELD_CHECKED);
		if (checked != null)
		{
			checkField.setChecked(Boolean.valueOf(checked));
		}

		String readOnly = element.getPropertiesMap().getProperty(PDF_FIELD_READ_ONLY);
		if (readOnly != null)
		{
			if (Boolean.valueOf(readOnly))
			{
				checkField.setReadOnly();
			}
		}

		checkField.add();
	}
	
	/**
	 *
	 */
	public void exportFieldRadio(JRPrintElement element) throws IOException
	{
		String fieldName = element.getPropertiesMap().getProperty(PDF_FIELD_NAME);
		fieldName = fieldName == null || fieldName.trim().length() == 0 ? "FIELD_" + element.getUUID() : fieldName;
		
		PdfRadioCheck radioField = pdfProducer.getRadioField(
				element.getX() + exporterContext.getOffsetX(),
				jasperPrint.getPageHeight() - element.getY() - exporterContext.getOffsetY(),
				element.getX() + exporterContext.getOffsetX() + element.getWidth(),
				jasperPrint.getPageHeight() - element.getY() - exporterContext.getOffsetY() - element.getHeight(),
				fieldName,
				"FIELD_" + element.getUUID());

		PdfFieldCheckTypeEnum checkType = PdfFieldCheckTypeEnum.getByName(element.getPropertiesMap().getProperty(PDF_FIELD_CHECK_TYPE));
		if (checkType != null)
		{
			radioField.setCheckType(checkType);
		}

		if (ModeEnum.OPAQUE == element.getMode())
		{
			radioField.setBackgroundColor(element.getBackcolor());
		}
		radioField.setTextColor(element.getForecolor());

		JRPen pen = getFieldPen(element);
		if (pen != null)
		{
			float borderWidth = Math.round(pen.getLineWidth());
			if (borderWidth > 0)
			{
				radioField.setBorderColor(pen.getLineColor());
				radioField.setBorderWidth(borderWidth);
				String strBorderStyle = propertiesUtil.getProperty(PDF_FIELD_BORDER_STYLE, element, jasperPrint);
				PdfFieldBorderStyleEnum borderStyle = PdfFieldBorderStyleEnum.getByName(strBorderStyle);
				if (borderStyle == null)
				{
					borderStyle = pen.getLineStyle() == LineStyleEnum.DASHED ? PdfFieldBorderStyleEnum.DASHED : PdfFieldBorderStyleEnum.SOLID;
				}
				radioField.setBorderStyle(borderStyle);
			}
		}
		
		radioField.setOnValue("FIELD_" + element.getUUID());

		String checked = element.getPropertiesMap().getProperty(PDF_FIELD_CHECKED);
		radioField.setChecked(Boolean.valueOf(checked)); // need to set to false if previous button was checked

		// setting the read-only option has to occur before the getRadioGroup() call
		String readOnly = element.getPropertiesMap().getProperty(PDF_FIELD_READ_ONLY);
		if (readOnly != null)
		{
			if (Boolean.valueOf(readOnly))
			{
				radioField.setReadOnly();
			}
		}

		radioField.addToGroup();
	}
	
	protected JRPen getFieldPen(JRPrintElement element)
	{
		JRPen pen = null;

		JRLineBox box = element instanceof JRBoxContainer ? ((JRBoxContainer)element).getLineBox() : null;
		
		if (box == null)
		{
			pen = element instanceof JRCommonGraphicElement ? ((JRCommonGraphicElement)element).getLinePen() : null;
		}
		else
		{
			Float lineWidth = box.getPen().getLineWidth();
			if (lineWidth == 0)
			{
				// PDF fields do not support side borders
				// in case side borders are defined for the report element, ensure that all 4 are declared and all of them come with the same settings
				if(
					((JRBasePen)box.getTopPen()).isIdentical(box.getLeftPen())
					&& ((JRBasePen)box.getTopPen()).isIdentical(box.getBottomPen())
					&& ((JRBasePen)box.getTopPen()).isIdentical(box.getRightPen())
					&& box.getTopPen().getLineWidth() > 0
					)
				{
					pen = new JRBasePen(box);
					pen.setLineWidth(box.getTopPen().getLineWidth());
					pen.setLineColor(box.getTopPen().getLineColor());
					pen.setLineStyle(box.getTopPen().getLineStyle());
				}
			}
			else
			{
				pen = new JRBasePen(box);
				pen.setLineWidth(lineWidth);
				pen.setLineColor(box.getPen().getLineColor());
				pen.setLineStyle(box.getPen().getLineStyle());
			}
		}

		return pen;
	}
	
	protected AbstractPdfTextRenderer getTextRenderer(JRPrintText text, JRStyledText styledText)
	{
		return 
			pdfProducer.getTextRenderer(
				createPdfTextRendererContext(text, styledText, getTextLocale(text))
				);
	}


	/**
	 *
	 */
	protected void exportBox(JRLineBox box, JRPrintElement element)
	{
		exportTopPen(box.getTopPen(), box.getLeftPen(), box.getRightPen(), element);
		exportLeftPen(box.getTopPen(), box.getLeftPen(), box.getBottomPen(), element);
		exportBottomPen(box.getLeftPen(), box.getBottomPen(), box.getRightPen(), element);
		exportRightPen(box.getTopPen(), box.getBottomPen(), box.getRightPen(), element);

		pdfContent.setLineDash(0f);
		pdfContent.setLineCap(LineCapStyle.PROJECTING_SQUARE);
	}


	/**
	 *
	 */
	protected void exportPen(JRPen pen, JRPrintElement element)
	{
		exportTopPen(pen, pen, pen, element);
		exportLeftPen(pen, pen, pen, element);
		exportBottomPen(pen, pen, pen, element);
		exportRightPen(pen, pen, pen, element);

		pdfContent.setLineDash(0f);
		pdfContent.setLineCap(LineCapStyle.PROJECTING_SQUARE);
	}


	/**
	 *
	 */
	protected void exportTopPen(
		JRPen topPen, 
		JRPen leftPen, 
		JRPen rightPen, 
		JRPrintElement element)
	{
		if (topPen.getLineWidth() > 0f)
		{
			float leftOffset = leftPen.getLineWidth() / 2;
			float rightOffset = rightPen.getLineWidth() / 2;
			int lcOffsetX = getOffsetX();
			int lcOffsetY = getOffsetY();
			
			preparePen(topPen, LineCapStyle.BUTT);
			
			if (topPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float topOffset = topPen.getLineWidth();

				pdfContent.strokeLine(
					element.getX() + lcOffsetX - leftOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY + topOffset / 3,
					element.getX() + lcOffsetX + element.getWidth() + rightOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY + topOffset / 3
					);

				pdfContent.strokeLine(
					element.getX() + lcOffsetX + leftOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - topOffset / 3,
					element.getX() + lcOffsetX + element.getWidth() - rightOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - topOffset / 3
					);
			}
			else
			{
				pdfContent.strokeLine(
					element.getX() + lcOffsetX - leftOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY,
					element.getX() + lcOffsetX + element.getWidth() + rightOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY
					);
			}
			
			resetPen();
		}
	}


	/**
	 *
	 */
	protected void exportLeftPen(JRPen topPen, JRPen leftPen, JRPen bottomPen, JRPrintElement element)
	{
		if (leftPen.getLineWidth() > 0f)
		{
			float topOffset = topPen.getLineWidth() / 2;
			float bottomOffset = bottomPen.getLineWidth() / 2;
			int lcOffsetX = getOffsetX();
			int lcOffsetY = getOffsetY();

			preparePen(leftPen, LineCapStyle.BUTT);

			if (leftPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float leftOffset = leftPen.getLineWidth();

				pdfContent.strokeLine(
					element.getX() + lcOffsetX - leftOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY + topOffset,
					element.getX() + lcOffsetX - leftOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() - bottomOffset
					);

				pdfContent.strokeLine(
					element.getX() + lcOffsetX + leftOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - topOffset / 3,
					element.getX() + lcOffsetX + leftOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() + bottomOffset / 3
					);
			}
			else
			{
				pdfContent.strokeLine(
					element.getX() + lcOffsetX,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY + topOffset,
					element.getX() + lcOffsetX,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() - bottomOffset
					);
			}
			
			resetPen();
		}
	}


	/**
	 *
	 */
	protected void exportBottomPen(JRPen leftPen, JRPen bottomPen, JRPen rightPen, JRPrintElement element)
	{
		if (bottomPen.getLineWidth() > 0f)
		{
			float leftOffset = leftPen.getLineWidth() / 2;
			float rightOffset = rightPen.getLineWidth() / 2;
			int lcOffsetX = getOffsetX();
			int lcOffsetY = getOffsetY();
			
			preparePen(bottomPen, LineCapStyle.BUTT);
			
			if (bottomPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float bottomOffset = bottomPen.getLineWidth();

				pdfContent.strokeLine(
					element.getX() + lcOffsetX - leftOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() - bottomOffset / 3,
					element.getX() + lcOffsetX + element.getWidth() + rightOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() - bottomOffset / 3
					);

				pdfContent.strokeLine(
					element.getX() + lcOffsetX + leftOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() + bottomOffset / 3,
					element.getX() + lcOffsetX + element.getWidth() - rightOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() + bottomOffset / 3
					);
			}
			else
			{
				pdfContent.strokeLine(
					element.getX() + lcOffsetX - leftOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight(),
					element.getX() + lcOffsetX + element.getWidth() + rightOffset,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight()
					);
			}
			
			resetPen();
		}
	}


	/**
	 *
	 */
	protected void exportRightPen(JRPen topPen, JRPen bottomPen, JRPen rightPen, JRPrintElement element)
	{
		if (rightPen.getLineWidth() > 0f)
		{
			float topOffset = topPen.getLineWidth() / 2;
			float bottomOffset = bottomPen.getLineWidth() / 2;
			int lcOffsetX = getOffsetX();
			int lcOffsetY = getOffsetY();

			preparePen(rightPen, LineCapStyle.BUTT);

			if (rightPen.getLineStyle() == LineStyleEnum.DOUBLE)
			{
				float rightOffset = rightPen.getLineWidth();

				pdfContent.strokeLine(
					element.getX() + lcOffsetX + element.getWidth() + rightOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY + topOffset,
					element.getX() + lcOffsetX + element.getWidth() + rightOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() - bottomOffset
					);

				pdfContent.strokeLine(
					element.getX() + lcOffsetX + element.getWidth() - rightOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - topOffset / 3,
					element.getX() + lcOffsetX + element.getWidth() - rightOffset / 3,
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() + bottomOffset / 3
					);
			}
			else
			{
				pdfContent.strokeLine(
					element.getX() + lcOffsetX + element.getWidth(),
					pageFormat.getPageHeight() - element.getY() - lcOffsetY + topOffset,
					element.getX() + lcOffsetX + element.getWidth(),
					pageFormat.getPageHeight() - element.getY() - lcOffsetY - element.getHeight() - bottomOffset
					);
			}
			
			resetPen();
		}
	}


	/**
	 *
	 */
	private void preparePen(JRPen pen, LineCapStyle lineCap)
	{
		float lineWidth = pen.getLineWidth();

		if (lineWidth <= 0)
		{
			return;
		}
		
		PdfContent pdfContent = pdfProducer.getPdfContent();
		pdfContent.setLineWidth(lineWidth);
		pdfContent.setLineCap(lineCap);

		Color color = pen.getLineColor();
		pdfContent.setStrokeColor(color);

		switch (pen.getLineStyle())
		{
			case DOUBLE :
			{
				pdfContent.setLineWidth(lineWidth / 3);
				pdfContent.setLineDash(0f);
				break;
			}
			case DOTTED :
			{
				switch (lineCap)
				{
					case BUTT :
					{
						pdfContent.setLineDash(lineWidth, lineWidth, 0f);
						break;
					}
					case PROJECTING_SQUARE :
					{
						pdfContent.setLineDash(0, 2 * lineWidth, 0f);
						break;
					}
					default :
				}
				break;
			}
			case DASHED :
			{
				switch (lineCap)
				{
					case BUTT :
					{
						pdfContent.setLineDash(5 * lineWidth, 3 * lineWidth, 0f);
						break;
					}
					case PROJECTING_SQUARE :
					{
						pdfContent.setLineDash(4 * lineWidth, 4 * lineWidth, 0f);
						break;
					}
					default :
				}
				break;
			}
			case SOLID :
			default :
			{
				pdfContent.setLineDash(0f);
				break;
			}
		}
	}
	
	private void resetPen()
	{
		pdfContent.resetStrokeColor();
	}

	protected static synchronized void registerFonts ()
	{
		//TODO lucian
		if (!fontsRegistered)
		{
			List<PropertySuffix> fontFiles = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).getProperties(PDF_FONT_FILES_PREFIX);//FIXMECONTEXT no default here and below
			if (!fontFiles.isEmpty())
			{
				for (Iterator<PropertySuffix> i = fontFiles.iterator(); i.hasNext();)
				{
					JRPropertiesUtil.PropertySuffix font = i.next();
					String file = font.getValue();
					if (file.toLowerCase().endsWith(".ttc"))
					{
						FontFactory.register(file);
					}
					else
					{
						String alias = font.getSuffix();
						FontFactory.register(file, alias);
					}
				}
			}

			List<PropertySuffix> fontDirs = JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).getProperties(PDF_FONT_DIRS_PREFIX);
			if (!fontDirs.isEmpty())
			{
				for (Iterator<PropertySuffix> i = fontDirs.iterator(); i.hasNext();)
				{
					JRPropertiesUtil.PropertySuffix dir = i.next();
					FontFactory.registerDirectory(dir.getValue());
				}
			}

			fontsRegistered = true;
		}
	}


	static protected class Bookmark
	{
		final PdfOutlineEntry pdfOutline;
		final int level;

		Bookmark(Bookmark parent, int x, int top, String title)
		{
			this.pdfOutline = parent.pdfOutline.createChild(title, x, top);
			this.level = parent.level + 1;
		}

		Bookmark(Bookmark parent, String title)
		{
			this.pdfOutline = parent.pdfOutline.createChild(title);
			this.level = parent.level + 1;
		}

		Bookmark(PdfOutlineEntry pdfOutline, int level)
		{
			this.pdfOutline = pdfOutline;
			this.level = level;
		}
	}

	static protected class BookmarkStack
	{
		LinkedList<Bookmark> stack;

		BookmarkStack()
		{
			stack = new LinkedList<>();
		}

		void push(Bookmark bookmark)
		{
			stack.add(bookmark);
		}

		Bookmark pop()
		{
			return stack.removeLast();
		}

		Bookmark peek()
		{
			return stack.getLast();
		}
	}


	protected void initBookmarks(List<ExporterInputItem> items)
	{
		bookmarkStack = new BookmarkStack();

		int rootLevel = items.size() > 1 && getCurrentConfiguration().isCreatingBatchModeBookmarks() ? -1 : 0;
		Bookmark bookmark = new Bookmark(pdfProducer.getRootOutline(), rootLevel);
		bookmarkStack.push(bookmark);
	}


	protected void addBookmark(int level, String title, int x, int y)
	{
		if (!bookmarksEnabled)
		{
			return;
		}
		
		Bookmark parent = bookmarkStack.peek();
		// searching for parent
		while(parent.level >= level)
		{
			bookmarkStack.pop();
			parent = bookmarkStack.peek();
		}

		if (!getCurrentItemConfiguration().isCollapseMissingBookmarkLevels())
		{
			// creating empty bookmarks in order to preserve the bookmark level
			for (int i = parent.level + 1; i < level; ++i)
			{
				Bookmark emptyBookmark = new Bookmark(parent, EMPTY_BOOKMARK_TITLE);
				bookmarkStack.push(emptyBookmark);
				parent = emptyBookmark;
			}
		}
		int height = OrientationEnum.LANDSCAPE.equals(pageFormat.getOrientation()) 
				? y 
				: pageFormat.getPageHeight() - y;
		Bookmark bookmark = new Bookmark(parent, x, height, title);
		bookmarkStack.push(bookmark);
	}


	protected void setAnchor(PdfChunk chunk, JRPrintAnchor anchor, JRPrintElement element)
	{
		String anchorName = anchor.getAnchorName();
		
		if (anchorName != null)
		{
			chunk.setLocalDestination(anchorName);
		}
		
		
		if (anchor.getBookmarkLevel() != JRAnchor.NO_BOOKMARK)
		{
			if (anchorName == null)
			{
				anchorName = element instanceof JRPrintText ? ((JRPrintText)element).getFullText() : null;
			}
			
			int x = OrientationEnum.LANDSCAPE.equals(pageFormat.getOrientation()) 
					? getOffsetY() + element.getY()
					: getOffsetX() + element.getX(); 
			int y = OrientationEnum.LANDSCAPE.equals(pageFormat.getOrientation()) 
					? getOffsetX() + element.getX()
					: getOffsetY() + element.getY(); 
			addBookmark(anchor.getBookmarkLevel(), anchorName, x, y);
		}
	}


	public void exportFrame(JRPrintFrame frame) throws IOException, JRException
	{
		if (frame.getMode() == ModeEnum.OPAQUE)
		{
			int x = frame.getX() + getOffsetX();
			int y = frame.getY() + getOffsetY();

			Color backcolor = frame.getBackcolor();
			
			PdfContent pdfContent = pdfProducer.getPdfContent();
			pdfContent.setFillColor(backcolor);
			pdfContent.fillRectangle(
				x,
				pageFormat.getPageHeight() - y,
				frame.getWidth(),
				- frame.getHeight()
				);
			pdfContent.resetFillColor();
		}

		setFrameElementsOffset(frame, false);
		try
		{
			exportElements(frame.getElements());
		}
		finally
		{
			restoreElementOffsets();
		}

		exportBox(frame.getLineBox(), frame);
	}


	/**
	 *
	 */
	protected PrintPageFormat getCurrentPageFormat()
	{
		return pageFormat;
	}


	@Override
	protected int getOffsetX()
	{
		return 
			super.getOffsetX() 
			+ (insideFrame() 
				? 0 
				: (crtPageOffsetX 
					+ (crtDocumentPageNumber % 2 == 0 
						? crtEvenPageOffsetX 
						: crtOddPageOffsetX)));
	}


	@Override
	protected int getOffsetY()
	{
		return 
			super.getOffsetY() 
			+ (insideFrame() 
				? 0 
				: (crtPageOffsetY 
					+ (crtDocumentPageNumber % 2 == 0 
						? crtEvenPageOffsetY 
						: crtOddPageOffsetY)));
	}


	/**
	 *
	 */
	protected void exportGenericElement(JRGenericPrintElement element)
	{
		GenericElementPdfHandler handler = (GenericElementPdfHandler) 
				GenericElementHandlerEnviroment.getInstance(getJasperReportsContext()).getElementHandler(
						element.getGenericType(), PDF_EXPORTER_KEY);
		
		if (handler != null)
		{
			handler.exportElement(exporterContext, element);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("No PDF generic element handler for " 
						+ element.getGenericType());
			}
		}
	}

	
	@Override
	public String getExporterKey()
	{
		return PDF_EXPORTER_KEY;
	}

	
	@Override
	public String getExporterPropertiesPrefix()
	{
		return PDF_EXPORTER_PROPERTIES_PREFIX;
	}
	
	public static int getIntegerPermissions(String permissions) {
		int permission = 0;
		if(permissions != null && permissions.length() > 0) {
			String[] perms = permissions.split("\\|");
			for(String perm : perms) {
				if(PdfPermissionsEnum.ALL.equals(PdfPermissionsEnum.getByName(perm))) {
					permission = PdfExporterConfiguration.ALL_PERMISSIONS;
					break;
				}
				if(perm != null && perm.length()>0) {
					permission |= PdfPermissionsEnum.getByName(perm).getPdfPermission();
				}
			}
		}
		return permission;
	}
}
