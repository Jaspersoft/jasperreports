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
 * Joakim Sandström - sanjoa@users.sourceforge.net
 */
package net.sf.jasperreports.engine.export;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintEllipse;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintGraphicElement;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.PrintBookmark;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.PrintParts;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRValueStringUtils;
import net.sf.jasperreports.engine.util.JRXmlWriteHelper;
import net.sf.jasperreports.engine.util.VersionComparator;
import net.sf.jasperreports.engine.util.XmlNamespace;
import net.sf.jasperreports.engine.xml.JRXmlConstants;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.engine.xml.XmlValueHandlerUtils;
import net.sf.jasperreports.export.ExporterConfiguration;
import net.sf.jasperreports.export.ReportExportConfiguration;
import net.sf.jasperreports.export.XmlExporterOutput;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.renderers.DataRenderable;
import net.sf.jasperreports.renderers.Renderable;
import net.sf.jasperreports.renderers.RenderersCache;
import net.sf.jasperreports.renderers.ResourceRenderer;
import net.sf.jasperreports.renderers.util.RendererUtil;
import net.sf.jasperreports.util.Base64Util;


/**
 * Exports a JasperReports document to an XML file that contains the same data as a 
 * {@link net.sf.jasperreports.engine.JasperPrint} object, but in XML format, instead 
 * of a serialized class. As report templates are defined using the
 * special XML syntax JRXML, the JasperReports library also has a special XML structure
 * for storing generated documents in XML format. This format is called JRPXML because
 * the files produced by the JRXmlExporter usually have the <code>*.jrpxml</code> extension.
 * <p/>
 * Such XML files can be parsed back into 
 * {@link net.sf.jasperreports.engine.JasperPrint}
 * object using the {@link net.sf.jasperreports.engine.xml.JRPrintXmlLoader} utility class. 
 * Their structure is validated against an internal XSD file called <code>jasperprint.xsd</code>, 
 * that provides the details of the JRPXML structure. Valid JRPXML files
 * should point to the internal XSD file using a public location, as follows:
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;jasperPrint 
 *   xmlns="http://jasperreports.sourceforge.net/jasperreports/print" 
 *   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 *   xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports/print http://jasperreports.sourceforge.net/xsd/jasperprint.xsd"
 *   ...&gt;
 * </pre>
 * The root element of a JRPXML document is <code>&lt;jasperPrint&gt;</code>, which contains a list of
 * report custom properties (<code>&lt;property&gt;</code> tags), a list of element origins
 * (<code>&lt;origin&gt;</code> tags), a list of
 * report style definitions (<code>&lt;style&gt;</code> tags) that are reused by report elements throughout
 * the document, and a list of pages (<code>&lt;page&gt;</code> tags), each of which contains a nested list of
 * elements like lines, rectangles, ellipses, images, and texts.
 * <p/>
 * The quality of this exporter is equal to the <code>Graphics2D</code> exporter because it preserves
 * 100% of the initial document content and properties. There is no loss in document
 * quality when exporting to XML because the resulting XML content can be loaded back
 * into a {@link net.sf.jasperreports.engine.JasperPrint} object that will look the same as the original one.
 * <p/>
 * The built-in viewers can display documents exported in JRPXML format because they
 * actually rely on the {@link net.sf.jasperreports.engine.xml.JRPrintXmlLoader} to load the 
 * document back into a {@link net.sf.jasperreports.engine.JasperPrint} object before 
 * rendering it on the screen.
 * <h3>Embedding Images</h3>
 * When exporting XML, pay special attention to how images are stored. The two ways are
 * as follows:
 * <ul>
 * <li>If the exporter outputs to a file on disk, it stores the images contained by the source
 * document in separate files that accompany the main JRPXML file. The image files
 * are put in a directory that takes its name from the original destination file name
 * plus the <code>_files</code> suffix, the same directory as the JRPXML file.</li>
 * <li>The exporter can embed images in the JRPXML file itself by encoding their binary
 * data using a Base64 encoder. This simplifies transfer over the network or by direct
 * output to streams.</li>
 * </ul>
 * <p/>
 * To determine how to handle images, set the 
 * {@link net.sf.jasperreports.export.XmlExporterOutput#isEmbeddingImages() isEmbeddingImages()} 
 * exporter output flag,
 * which expects a <code>java.lang.Boolean</code>. By default, the images are embedded in the
 * resulting XML.
 * 
 * @see net.sf.jasperreports.engine.JasperPrint
 * @see net.sf.jasperreports.engine.xml.JRPrintXmlLoader
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRXmlExporter extends JRAbstractExporter<ReportExportConfiguration, ExporterConfiguration, XmlExporterOutput, JRXmlExporterContext>
{
	/**
	 *
	 */
	private static final String XML_EXPORTER_PROPERTIES_PREFIX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xml.";
	
	public static final String EXCEPTION_MESSAGE_KEY_EMBEDDING_IMAGE_ERROR = "export.xml.embedding.image.error";
	public static final String EXCEPTION_MESSAGE_KEY_REPORT_STYLE_NOT_FOUND = "export.xml.report.style.not.found";

	/**
	 * The exporter key, as used in
	 * {@link GenericElementHandlerEnviroment#getElementHandler(JRGenericElementType, String)}.
	 */
	public static final String XML_EXPORTER_KEY = JRPropertiesUtil.PROPERTY_PREFIX + "xml";

	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_0_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_START_PAGE_INDEX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xml.start.page.index";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_0_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_END_PAGE_INDEX = JRPropertiesUtil.PROPERTY_PREFIX + "export.xml.end.page.index";
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_3_0_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_PAGE_COUNT = JRPropertiesUtil.PROPERTY_PREFIX + "export.xml.page.count";
	
	/**
	 * Stores the text sequence used to replace invalid XML characters
	 */
	@Property(
			category = PropertyConstants.CATEGORY_EXPORT,
			defaultValue = PropertyConstants.QUESTION_MARK,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_4_7_1
			)
	public static final String PROPERTY_REPLACE_INVALID_CHARS = JRPropertiesUtil.PROPERTY_PREFIX + "export.xml.replace.invalid.chars";//FIXMEEXPORT do something about it
	protected static final String DEFAULT_OBJECT_TYPE = "java.lang.String";
	protected static final String IMAGE_PREFIX = "img_";
	
	public static final XmlNamespace JASPERPRINT_NAMESPACE = 
			new XmlNamespace(JRXmlConstants.JASPERPRINT_NAMESPACE, null, JRXmlConstants.JASPERPRINT_XSD_SYSTEM_ID);

	/**
	 *
	 */
	protected JRXmlWriteHelper xmlWriter;
	protected String version;
	protected VersionComparator versionComparator = new VersionComparator();
	
	protected Map<String,String> rendererToImagePathMap;
	protected RenderersCache renderersCache;
//	protected Map fontsMap = new HashMap();
	protected Map<String,JRStyle> stylesMap = new HashMap<>();

	/**
	 *
	 */
	protected boolean isEmbeddingImages = true;

	/**
	 * 
	 */
	private static int imageId;


	protected class ExporterContext extends BaseExporterContext implements JRXmlExporterContext
	{
	}


	/**
	 * @see #JRXmlExporter(JasperReportsContext)
	 */
	public JRXmlExporter()
	{
		this(DefaultJasperReportsContext.getInstance());
	}

	
	/**
	 *
	 */
	public JRXmlExporter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
		
		exporterContext = new ExporterContext();
	}


	@Override
	protected Class<ExporterConfiguration> getConfigurationInterface()
	{
		return ExporterConfiguration.class;
	}


	@Override
	protected Class<ReportExportConfiguration> getItemConfigurationInterface()
	{
		return ReportExportConfiguration.class;
	}
	

	@Override
	public void exportReport() throws JRException
	{
		/*   */
		ensureJasperReportsContext();
		ensureInput();

		initExport();
		
		ensureOutput();

		Boolean lcIsEmbeddingImages = getExporterOutput().isEmbeddingImages();
		if (lcIsEmbeddingImages == null)
		{
			lcIsEmbeddingImages = Boolean.TRUE;
		}
		isEmbeddingImages = lcIsEmbeddingImages;
		
		if (!isEmbeddingImages)
		{
			rendererToImagePathMap = new HashMap<>();
		}

		renderersCache = new RenderersCache(getJasperReportsContext());

		Writer writer = getExporterOutput().getWriter();

		try
		{
			exportReportToStream(writer);
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
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
	}


	@Override
	protected void initReport()
	{
		super.initReport();
	}
	

	/**
	 *
	 */
	protected XmlNamespace getNamespace()
	{
		return JASPERPRINT_NAMESPACE;
	}

	protected void exportReportToStream(Writer writer) throws JRException, IOException
	{
		version = getPropertiesUtil().getProperty(jasperPrint, JRXmlWriter.PROPERTY_REPORT_VERSION);
		
		xmlWriter = new JRXmlWriteHelper(writer);
		
		xmlWriter.writeProlog(getExporterOutput().getEncoding());

		xmlWriter.startElement(JRXmlConstants.ELEMENT_jasperPrint, getNamespace());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, jasperPrint.getName());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_pageWidth, jasperPrint.getPageWidth());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_pageHeight, jasperPrint.getPageHeight());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_topMargin, jasperPrint.getTopMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_leftMargin, jasperPrint.getLeftMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_bottomMargin, jasperPrint.getBottomMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_rightMargin, jasperPrint.getRightMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_orientation, jasperPrint.getOrientation(), OrientationEnum.PORTRAIT);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_formatFactoryClass, jasperPrint.getFormatFactoryClass());		
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_locale, jasperPrint.getLocaleCode());		
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_timezone, jasperPrint.getTimeZoneId());
		
		setCurrentExporterInputItem(exporterInput.getItems().get(0));
		
		List<JRPrintPage> pages = jasperPrint.getPages();
	
		PageRange pageRange = getPageRange();
		int startPageIndex = (pageRange == null || pageRange.getStartPageIndex() == null) ? 0 : pageRange.getStartPageIndex();
		int endPageIndex = (pageRange == null || pageRange.getEndPageIndex() == null) ? (pages.size() - 1) : pageRange.getEndPageIndex();

		//FIXME this leads to property duplication if a JasperPrint is loaded
		//from a *.jrpxml and exported back to xml
		xmlWriter.startElement(JRXmlConstants.ELEMENT_property);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, PROPERTY_START_PAGE_INDEX);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_value, String.valueOf(startPageIndex));
		xmlWriter.closeElement();

		xmlWriter.startElement(JRXmlConstants.ELEMENT_property);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, PROPERTY_END_PAGE_INDEX);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_value, String.valueOf(endPageIndex));
		xmlWriter.closeElement();

		xmlWriter.startElement(JRXmlConstants.ELEMENT_property); //FIXME make this configurable?
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, PROPERTY_PAGE_COUNT);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_value, jasperPrint.getPages() == null ? null : String.valueOf(jasperPrint.getPages().size()));
		xmlWriter.closeElement();

		exportProperties(jasperPrint);
		
		JROrigin[] origins = jasperPrint.getOrigins();
		if (origins != null && origins.length > 0)
		{
			for (int i = 0; i < origins.length; i++)
			{
				exportOrigin(origins[i]);
			}
		}

		JRStyle[] styles = jasperPrint.getStyles();
		if (styles != null && styles.length > 0)
		{
			for (int i = 0; i < styles.length; i++)
			{
				stylesMap.put(styles[i].getName(), styles[i]);
				exportStyle(styles[i]);
			}
		}

		exportBookmarks(jasperPrint.getBookmarks());
		
		PrintParts parts = jasperPrint.getParts();
		if (parts != null && parts.hasParts())
		{
			for (Iterator<Map.Entry<Integer, PrintPart>> it = parts.partsIterator(); it.hasNext();)
			{
				Map.Entry<Integer, PrintPart> partsEntry = it.next();
				/*   */
				exportPart(partsEntry.getKey(), partsEntry.getValue());
			}
		}

		if (pages != null && pages.size() > 0)
		{
			JRPrintPage page = null;
			for (int i = startPageIndex; i <= endPageIndex; i++)
			{
				checkInterrupted();
				
				page = pages.get(i);
	
				/*   */
				exportPage(page);
			}
		}

		xmlWriter.closeElement();
		
		writer.flush();
	}


	protected void exportProperties(JRPropertiesHolder propertiesHolder) throws IOException
	{
		if (propertiesHolder.hasProperties())
		{
			JRPropertiesMap propertiesMap = propertiesHolder.getPropertiesMap();
			String[] propertyNames = propertiesMap.getPropertyNames();
			if (propertyNames != null && propertyNames.length > 0)
			{
				for (int i = 0; i < propertyNames.length; i++)
				{
					xmlWriter.startElement(JRXmlConstants.ELEMENT_property);
					xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, propertyNames[i]);
					String value = propertiesMap.getProperty(propertyNames[i]);
					if (value != null)
					{
						String encodedValue = JRStringUtil.encodeXmlAttribute(value);
						if (
							isNewerVersionOrEqual(JRConstants.VERSION_6_4_0)
							&& encodedValue.length() != value.length()
							&& value.trim().equals(value)
							)
						{
							xmlWriter.writeCDATA(value);
						}
						else
						{
							xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_value, encodedValue);
						}
					}
					xmlWriter.closeElement();
				}
			}
		}
	}


	/**
	 * @throws IOException 
	 */
	protected void exportStyle(JRStyle style) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_style);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, style.getName());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isDefault, style.isDefault(), false);

		if (style.getStyle() != null)
		{
			JRStyle baseStyle = stylesMap.get(style.getStyle().getName());
			if (baseStyle != null)
			{
				xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_style, style.getStyle().getName());
			}
			else
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_REPORT_STYLE_NOT_FOUND,  
						new Object[]{style.getStyle().getName()} 
						);
			}
		}
	
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_mode, style.getOwnMode());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_forecolor, style.getOwnForecolor());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_backcolor, style.getOwnBackcolor());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_fill, style.getOwnFill());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_radius, style.getOwnRadius());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_scaleImage, style.getOwnScaleImage());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hTextAlign, style.getOwnHorizontalTextAlign());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hImageAlign, style.getOwnHorizontalImageAlign());
		VerticalTextAlignEnum vTextAlign = style.getOwnVerticalTextAlign();
		if (isOlderVersionThan(JRConstants.VERSION_6_2_1))
		{
			vTextAlign = vTextAlign == VerticalTextAlignEnum.JUSTIFIED ? VerticalTextAlignEnum.TOP : vTextAlign;
		}
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_vTextAlign, vTextAlign);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_vImageAlign, style.getOwnVerticalImageAlign());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_rotation, style.getOwnRotation());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_markup, style.getOwnMarkup());
		//xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_pattern, style.getOwnPattern());//FIXME if pattern in text field is equal to this, then it should be removed there (inheritance)
		//xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isBlankWhenNull, style.isOwnBlankWhenNull());
		
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_fontName, style.getOwnFontName());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_fontSize, style.getOwnFontSize(), true);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isBold, style.isOwnBold());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isItalic, style.isOwnItalic());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isUnderline, style.isOwnUnderline());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isStrikeThrough, style.isOwnStrikeThrough());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_pdfFontName, style.getOwnPdfFontName());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_pdfEncoding, style.getOwnPdfEncoding());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isPdfEmbedded, style.isOwnPdfEmbedded());
		
		exportPen(style.getLinePen());
		exportBox(style.getLineBox());
		exportParagraph(style.getParagraph());
		
		xmlWriter.closeElement();
	}


	/**
	 * @throws IOException 
	 */
	protected void exportOrigin(JROrigin origin) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_origin);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_report, origin.getReportName());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_group, origin.getGroupName());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_band, origin.getBandType());
		xmlWriter.closeElement();
	}


	protected void exportBookmarks(List<PrintBookmark> bookmarks) throws IOException
	{
		if (bookmarks != null && bookmarks.size() > 0)
		{
			for (PrintBookmark bookmark : bookmarks)
			{
				exportBookmark(bookmark);
			}
		}
	}


	protected void exportBookmark(PrintBookmark bookmark) throws IOException
	{
		if (bookmark != null)
		{
			xmlWriter.startElement(JRXmlConstants.ELEMENT_bookmark);
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_label, bookmark.getLabel());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_pageIndex, bookmark.getPageIndex());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_elementAddress, bookmark.getElementAddress());
			
			exportBookmarks(bookmark.getBookmarks());

			xmlWriter.closeElement();
		}
	}


	/**
	 *
	 */
	protected void exportPart(Integer pageIndex, PrintPart part) throws JRException, IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_part);

		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_pageIndex, pageIndex);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, part.getName());
		PrintPageFormat pageFormat = part.getPageFormat();
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_pageWidth, pageFormat.getPageWidth());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_pageHeight, pageFormat.getPageHeight());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_topMargin, pageFormat.getTopMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_leftMargin, pageFormat.getLeftMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_bottomMargin, pageFormat.getBottomMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_rightMargin, pageFormat.getRightMargin());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_orientation, pageFormat.getOrientation(), OrientationEnum.PORTRAIT);
		
		exportProperties(part);

		xmlWriter.closeElement();
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportPage(JRPrintPage page) throws JRException, IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_page);

		exportElements(page.getElements());

		xmlWriter.closeElement();
		
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
			for (Iterator<JRPrintElement> it = elements.iterator(); it.hasNext();)
			{
				exportElement(it.next());
			}
		}
	}


	public void exportElement(JRPrintElement element) throws IOException, JRException
	{
		checkInterrupted();
		if (filter == null || filter.isToExport(element))
		{
			if (element instanceof JRPrintLine)
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
				exportText((JRPrintText)element);
			}
			else if (element instanceof JRPrintFrame)
			{
				exportFrame((JRPrintFrame) element);
			}
			else if (element instanceof JRGenericPrintElement)
			{
				exportGenericElement((JRGenericPrintElement) element);
			}
		}
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportLine(JRPrintLine line) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_line);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_direction, line.getDirection(), LineDirectionEnum.TOP_DOWN);

		exportReportElement(line);
		exportGraphicElement(line);
		
		xmlWriter.closeElement();
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportReportElement(JRPrintElement element) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_reportElement);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_uuid, element.getUUID() == null ? null : element.getUUID().toString());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_key, element.getKey());
		JRStyle style = element.getStyle();
		if (style != null)
		{
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_style, style.getName());
		}
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_mode, element.getOwnMode());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_x, element.getX() + getOffsetX());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_y, element.getY() + getOffsetY());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_width, element.getWidth());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_height, element.getHeight());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_forecolor, element.getOwnForecolor());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_backcolor, element.getOwnBackcolor());
		JROrigin origin = element.getOrigin();
		if (origin != null)
		{
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_origin, jasperPrint.getOriginsMap().get(origin));
		}
		
		int elementId = element.getSourceElementId();
		if (elementId != JRPrintElement.UNSET_SOURCE_ELEMENT_ID)
		{
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_srcId, elementId);
		}
		
		int printId = element.getPrintElementId();
		if (printId != JRPrintElement.UNSET_PRINT_ELEMENT_ID)
		{
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_printId, printId);
		}
		
		exportProperties(element);
		
		xmlWriter.closeElement();
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportGraphicElement(JRPrintGraphicElement element) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_graphicElement);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_fill, element.getOwnFill());
		exportPen(element.getLinePen());
		xmlWriter.closeElement(true);
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportPen(JRPen pen) throws IOException
	{
		exportPen(JRXmlConstants.ELEMENT_pen, pen);
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportPen(String element, JRPen pen) throws IOException
	{
		xmlWriter.startElement(element);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_lineWidth, pen.getOwnLineWidth());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_lineStyle, pen.getOwnLineStyle());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_lineColor, pen.getOwnLineColor());
		xmlWriter.closeElement(true);
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportRectangle(JRPrintRectangle rectangle) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_rectangle);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_radius, rectangle.getOwnRadius());

		exportReportElement(rectangle);
		exportGraphicElement(rectangle);
		
		xmlWriter.closeElement();
	}


	/**
	 * @throws IOException 
	 *
	 */
	protected void exportEllipse(JRPrintEllipse ellipse) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_ellipse);

		exportReportElement(ellipse);
		exportGraphicElement(ellipse);
		
		xmlWriter.closeElement();
	}


	/**
	 * @throws JRException 
	 * @throws IOException 
	 *
	 */
	public void exportImage(JRPrintImage image) throws JRException, IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_image);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_scaleImage, image.getOwnScaleImage());
		if (isNewerVersionOrEqual(JRConstants.VERSION_6_10_0))
		{
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_rotation, image.getOwnRotation());
		}
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hAlign, image.getOwnHorizontalImageAlign());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_vAlign, image.getOwnVerticalImageAlign());
		
		Renderable renderer = image.getRenderer();
		boolean isLazy = RendererUtil.isLazy(renderer);

		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isLazy, isLazy, false);

		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_onErrorType, image.getOnErrorType(), OnErrorTypeEnum.ERROR);
		
		JRHyperlinkProducerFactory hyperlinkProducerFactory = getCurrentItemConfiguration().getHyperlinkProducerFactory();
		if (hyperlinkProducerFactory == null)
		{
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkType, image.getLinkType(), HyperlinkTypeEnum.NONE.getName());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkReference, image.getHyperlinkReference());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkAnchor, image.getHyperlinkAnchor());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkPage, image.getHyperlinkPage());
		}
		else
		{
			String reference = hyperlinkProducerFactory.produceHyperlink(image);
			if (reference != null)
			{
				xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkType, HyperlinkTypeEnum.REFERENCE);
				xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkReference, reference);
			}
		}
		
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTarget, image.getLinkTarget(), HyperlinkTargetEnum.SELF.getName());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTooltip, image.getHyperlinkTooltip());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_anchorName, image.getAnchorName());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_bookmarkLevel, image.getBookmarkLevel(), JRAnchor.NO_BOOKMARK);

		exportReportElement(image);
		exportBox(image.getLineBox());
		exportGraphicElement(image);
		
		if (renderer != null)
		{
			xmlWriter.startElement(JRXmlConstants.ELEMENT_imageSource);
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isEmbedded, isEmbeddingImages && !isLazy, false);
	
			String imageSource = "";
			
			if (isLazy)
			{
				// we do not cache imagePath for lazy images because the short location string is already cached inside the render itself
				imageSource = RendererUtil.getResourceLocation(renderer);
			}
			else
			{
				if (renderer instanceof ResourceRenderer)
				{
					RenderersCache imageRenderersCache = image.isUsingCache() ? renderersCache
							//creating a fresh RenderersCache for a single call to preserve homogeneity with other exporters
							: new RenderersCache(getJasperReportsContext());
					renderer = imageRenderersCache.getLoadedRenderer((ResourceRenderer)renderer);
				}

				if (
					!isEmbeddingImages //we do not cache imageSource for embedded images because it is too big
					&& renderer instanceof DataRenderable //we do not cache imagePath for non-data renderers because they render width different width/height each time
					&& rendererToImagePathMap.containsKey(renderer.getId())
					)
				{
					imageSource = rendererToImagePathMap.get(renderer.getId());
				}
				else
				{
					if (isEmbeddingImages)
					{
						DataRenderable dataRenderer = 
							getRendererUtil().getDataRenderable(
								renderer,
								new Dimension(
									Math.max(image.getWidth() - image.getLineBox().getLeftPadding() - image.getLineBox().getRightPadding(), 0), 
									Math.max(image.getHeight() - image.getLineBox().getTopPadding() - image.getLineBox().getBottomPadding(), 0)
									),
								ModeEnum.OPAQUE == image.getMode() ? image.getBackcolor() : null
								);
							
						try
						{
							ByteArrayInputStream bais = new ByteArrayInputStream(dataRenderer.getData(jasperReportsContext));
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							
							Base64Util.encode(bais, baos);
							
							imageSource = new String(baos.toByteArray(), "UTF-8"); // UTF-8 is fine as we just need an ASCII compatible encoding for the Base64 array
						}
						catch (IOException e)
						{
							throw 
								new JRException(
									EXCEPTION_MESSAGE_KEY_EMBEDDING_IMAGE_ERROR,
									null, 
									e);
						}
						//don't cache the base64 encoded image as imageSource because they are too big
					}
					else
					{
						XmlResourceHandler imageHandler = getExporterOutput().getImageHandler();
						if (imageHandler != null)
						{
							DataRenderable dataRenderer = 
								getRendererUtil().getDataRenderable(
									renderer,
									new Dimension(
										Math.max(image.getWidth() - image.getLineBox().getLeftPadding() - image.getLineBox().getRightPadding(), 0), 
										Math.max(image.getHeight() - image.getLineBox().getTopPadding() - image.getLineBox().getBottomPadding(), 0)
										),
									ModeEnum.OPAQUE == image.getMode() ? image.getBackcolor() : null
									);

							String imageName = IMAGE_PREFIX + getNextImageId();
							
							imageHandler.handleResource(imageName, dataRenderer.getData(jasperReportsContext));
							
							imageSource = imageHandler.getResourceSource(imageName);

							if (dataRenderer == renderer)
							{
								//cache imagePath only for true DataRenderable instances because the wrapping ones render with different width/height each time
								rendererToImagePathMap.put(renderer.getId(), imageSource);
							}
						}
					}
				}
			}
			
			xmlWriter.writeCDATA(imageSource);
			xmlWriter.closeElement();
		}
		
		if (hyperlinkProducerFactory == null)
		{
			exportHyperlinkParameters(image);
		}
		
		xmlWriter.closeElement();
	}


	/**
	 * @throws IOException 
	 *
	 */
	public void exportText(JRPrintText text) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_text);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_textAlignment, text.getOwnHorizontalTextAlign());
		VerticalTextAlignEnum vTextAlign = text.getOwnVerticalTextAlign();
		if (isOlderVersionThan(JRConstants.VERSION_6_2_1))
		{
			vTextAlign = vTextAlign == VerticalTextAlignEnum.JUSTIFIED ? VerticalTextAlignEnum.TOP : vTextAlign;
		}
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_verticalAlignment, vTextAlign);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_textHeight, text.getTextHeight());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_rotation, text.getOwnRotation());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_runDirection, text.getRunDirection(), RunDirectionEnum.LTR);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_markup, text.getOwnMarkup());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_lineSpacingFactor, text.getLineSpacingFactor(), 0f);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_leadingOffset, text.getLeadingOffset(), 0f);
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_averageCharWidth, text.getAverageCharWidth());

		JRHyperlinkProducerFactory hyperlinkProducerFactory = getCurrentItemConfiguration().getHyperlinkProducerFactory();
		if (hyperlinkProducerFactory == null)
		{
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkType, text.getLinkType());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkReference, text.getHyperlinkReference());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkAnchor, text.getHyperlinkAnchor());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkPage, text.getHyperlinkPage());
		}
		else
		{
			String reference = hyperlinkProducerFactory.produceHyperlink(text);
			if (reference != null)
			{
				xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkType, HyperlinkTypeEnum.REFERENCE);
				xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkReference, reference);
			}
		}
		
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTarget, text.getLinkTarget());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTooltip, text.getHyperlinkTooltip());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_anchorName, text.getAnchorName());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_bookmarkLevel, text.getBookmarkLevel(), JRAnchor.NO_BOOKMARK);
		
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_valueClass, text.getValueClassName());
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_pattern, text.getPattern());		
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_formatFactoryClass, text.getFormatFactoryClass());		
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_locale, text.getLocaleCode());		
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_timezone, text.getTimeZoneId());		
		
		exportReportElement(text);
		exportBox(text.getLineBox());

		exportFont(text);
		exportParagraph(text.getParagraph());

		if (text.getOriginalText() != null)
		{
			xmlWriter.writeCDATAElement(JRXmlConstants.ELEMENT_textContent, text.getOriginalText(),
					JRXmlConstants.ATTRIBUTE_truncateIndex, text.getTextTruncateIndex());
		}
		
		xmlWriter.writeCDATAElement(JRXmlConstants.ELEMENT_textTruncateSuffix, text.getTextTruncateSuffix());
		
		short[] lineBreakOffsets = text.getLineBreakOffsets();
		if (lineBreakOffsets != null)
		{
			StringBuilder offsetsString = formatTextLineBreakOffsets(lineBreakOffsets);
			xmlWriter.writeCDATAElement(JRXmlConstants.ELEMENT_lineBreakOffsets, 
					offsetsString.toString());
		}
		
		if (hyperlinkProducerFactory == null)
		{
			exportHyperlinkParameters(text);
		}

		xmlWriter.closeElement();
	}


	protected StringBuilder formatTextLineBreakOffsets(short[] lineBreakOffsets)
	{
		StringBuilder offsetsString = new StringBuilder();
		for (int i = 0; i < lineBreakOffsets.length; i++)
		{
			if (i > 0)
			{
				offsetsString.append(JRXmlConstants.LINE_BREAK_OFFSET_SEPARATOR);
			}
			offsetsString.append(lineBreakOffsets[i]);
		}
		return offsetsString;
	}


	/**
	 * @throws IOException 
	 *
	 */
	private void exportBox(JRLineBox box) throws IOException
	{
		if (box != null)
		{
			xmlWriter.startElement(JRXmlConstants.ELEMENT_box);

			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_padding, box.getOwnPadding());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_topPadding, box.getOwnTopPadding());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_leftPadding, box.getOwnLeftPadding());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_bottomPadding, box.getOwnBottomPadding());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_rightPadding, box.getOwnRightPadding());

			exportPen(JRXmlConstants.ELEMENT_pen, box.getPen());
			exportPen(JRXmlConstants.ELEMENT_topPen, box.getTopPen());
			exportPen(JRXmlConstants.ELEMENT_leftPen, box.getLeftPen());
			exportPen(JRXmlConstants.ELEMENT_bottomPen, box.getBottomPen());
			exportPen(JRXmlConstants.ELEMENT_rightPen, box.getRightPen());
			
			xmlWriter.closeElement(true);
		}
	}


	/**
	 * @throws IOException 
	 *
	 */
	private void exportParagraph(JRParagraph paragraph) throws IOException
	{
		if (paragraph != null)
		{
			xmlWriter.startElement(JRXmlConstants.ELEMENT_paragraph);

			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_lineSpacing, paragraph.getOwnLineSpacing());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_lineSpacingSize, paragraph.getOwnLineSpacingSize());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_firstLineIndent, paragraph.getOwnFirstLineIndent());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_leftIndent, paragraph.getOwnLeftIndent());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_rightIndent, paragraph.getOwnRightIndent());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_spacingBefore, paragraph.getOwnSpacingBefore());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_spacingAfter, paragraph.getOwnSpacingAfter());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_tabStopWidth, paragraph.getOwnTabStopWidth());
			
			/*   */
			TabStop[] tabStops = paragraph.getTabStops();
			if (tabStops != null && tabStops.length > 0)
			{
				for (int i = 0; i < tabStops.length; i++)
				{
					exportTabStop(tabStops[i]);
				}
			}

			xmlWriter.closeElement(true);
		}
	}

	
	/**
	 *
	 */
	public void exportTabStop(TabStop tabStop) throws IOException
	{
		if (tabStop != null)
		{
			xmlWriter.startElement(JRXmlConstants.ELEMENT_tabStop);
			
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_position, tabStop.getPosition());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_alignment, tabStop.getAlignment());

			xmlWriter.closeElement(true);
		}
	}
	

	/**
	 *
	 */
	protected void exportFont(JRFont font) throws IOException
	{
		if (font != null)
		{
			xmlWriter.startElement(JRXmlConstants.ELEMENT_font);
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_fontName, font.getOwnFontName());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_size, font.getOwnFontSize(), true);
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isBold, font.isOwnBold());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isItalic, font.isOwnItalic());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isUnderline, font.isOwnUnderline());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isStrikeThrough, font.isOwnStrikeThrough());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_pdfFontName, font.getOwnPdfFontName());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_pdfEncoding, font.getOwnPdfEncoding());
			xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_isPdfEmbedded, font.isOwnPdfEmbedded());
			xmlWriter.closeElement(true);
		}
	}
	
	
	protected void exportFrame(JRPrintFrame frame) throws IOException, JRException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_frame);
		
		setFrameElementsOffset(frame, true);
		try
		{
			exportReportElement(frame);
			exportBox(frame.getLineBox());
			exportElements(frame.getElements());

			xmlWriter.closeElement();
		}
		finally
		{
			restoreElementOffsets();
		}
	}
	
	
	/**
	 * 
	 */
	private static synchronized int getNextImageId(){
		return imageId++;
	}


	protected void exportHyperlinkParameters(JRPrintHyperlink hyperlink) throws IOException
	{
		JRPrintHyperlinkParameters hyperlinkParameters = hyperlink.getHyperlinkParameters();
		if (hyperlinkParameters != null)
		{
			for (JRPrintHyperlinkParameter parameter : hyperlinkParameters.getParameters())
			{
				exportHyperlinkParameter(parameter);
			}
		}
	}


	protected void exportHyperlinkParameter(JRPrintHyperlinkParameter parameter) throws IOException
	{
		xmlWriter.startElement(JRXmlConstants.ELEMENT_hyperlinkParameter);
		xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, parameter.getName());
		xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_class, parameter.getValueClass(), DEFAULT_OBJECT_TYPE);
		
		if (parameter.getValue() != null)
		{
			String data = JRValueStringUtils.serialize(parameter.getValueClass(), parameter.getValue());
			xmlWriter.writeCDATAElement(JRXmlConstants.ELEMENT_hyperlinkParameterValue, data);
		}
		
		xmlWriter.closeElement();
	}


	/**
	 *
	 */
	protected void exportGenericElement(JRGenericPrintElement element) throws IOException
	{
		GenericElementXmlHandler handler = (GenericElementXmlHandler) 
		GenericElementHandlerEnviroment.getInstance(jasperReportsContext).getElementHandler(
				element.getGenericType(), getExporterKey());

		if (handler != null)
		{
			handler.exportElement(exporterContext, element);
		}
		else
		{
			xmlWriter.startElement(JRXmlConstants.ELEMENT_genericElement);
			exportReportElement(element);
			
			JRGenericElementType genericType = element.getGenericType();
			xmlWriter.startElement(JRXmlConstants.ELEMENT_genericElementType);
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_namespace, 
					genericType.getNamespace());
			xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, 
					genericType.getName());
			xmlWriter.closeElement();//genericElementType
			
			Set<String> names = element.getParameterNames();
			for (String name : names)
			{
				Object value = element.getParameterValue(name);
				xmlWriter.startElement(JRXmlConstants.ELEMENT_genericElementParameter);
				xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_name, name);
				if (value != null)
				{
					String valueClass = value.getClass().getName();
					// check if there's a builtin serializer for the value
					boolean builtinSerialization = JRValueStringUtils.hasSerializer(valueClass);
					if (!builtinSerialization)
					{
						// try XML handlers, if none works then default back to the builtin serialization
						builtinSerialization = !XmlValueHandlerUtils.instance().writeToXml(value, this);
					}
					
					if (builtinSerialization)
					{
						String data = JRValueStringUtils.serialize(valueClass, value);
						xmlWriter.startElement(JRXmlConstants.ELEMENT_genericElementParameterValue);
						xmlWriter.addAttribute(JRXmlConstants.ATTRIBUTE_class, valueClass);
						xmlWriter.writeCDATA(data);
						xmlWriter.closeElement();//genericElementParameterValue
					}
				}
				xmlWriter.closeElement();//genericElementParameter
			}
			
			xmlWriter.closeElement();//genericElement
		}
	}


	@Override
	public String getExporterPropertiesPrefix()
	{
		return XML_EXPORTER_PROPERTIES_PREFIX;
	}

	
	@Override
	public String getExporterKey()
	{
		return XML_EXPORTER_KEY;
	}
	
	/**
	 * Returns the XML write helper used by this exporter.
	 * 
	 * The helper can be used to output XML elements and attributes.
	 * 
	 * @return the XML write helper used by this exporter
	 */
	public JRXmlWriteHelper getXmlWriteHelper()
	{
		return xmlWriter;
	}

	
	/**
	 *
	 */
	protected boolean isNewerVersionOrEqual(String oldVersion)
	{
		return versionComparator.compare(version, oldVersion) >= 0;
	}

	
	/**
	 *
	 */
	protected boolean isOlderVersionThan(String version)
	{
		return versionComparator.compare(this.version, version) < 0;
	}
}
