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
package net.sf.jasperreports.engine.fonts;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.engine.util.JRXmlWriteHelper;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.util.StringBuilderWriter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class SimpleFontExtensionHelper implements ErrorHandler
{
	private static final Log log = LogFactory.getLog(SimpleFontExtensionHelper.class);
	public static final String EXCEPTION_MESSAGE_KEY_FILE_WRITER_ERROR = "fonts.file.writer.error";
	public static final String EXCEPTION_MESSAGE_KEY_OUTPUT_STREAM_WRITER_ERROR = "fonts.output.stream.writer.error";
	

	/**
	 *
	 */
	private static final String NODE_fontFamily = "fontFamily";
	private static final String NODE_normal = "normal";
	private static final String NODE_bold = "bold";
	private static final String NODE_italic = "italic";
	private static final String NODE_boldItalic = "boldItalic";
	private static final String NODE_ttf = "ttf";
	private static final String NODE_pdf = "pdf";
	private static final String NODE_eot = "eot";
	private static final String NODE_svg = "svg";
	private static final String NODE_woff = "woff";
	/**
	 * @deprecated Replaced by {@link #NODE_pdf}.
	 */
	private static final String NODE_normalPdfFont = "normalPdfFont";
	/**
	 * @deprecated Replaced by {@link #NODE_pdf}.
	 */
	private static final String NODE_boldPdfFont = "boldPdfFont";
	/**
	 * @deprecated Replaced by {@link #NODE_pdf}.
	 */
	private static final String NODE_italicPdfFont = "italicPdfFont";
	/**
	 * @deprecated Replaced by {@link #NODE_pdf}.
	 */
	private static final String NODE_boldItalicPdfFont = "boldItalicPdfFont";
	private static final String NODE_pdfEncoding = "pdfEncoding";
	private static final String NODE_pdfEmbedded = "pdfEmbedded";
	private static final String NODE_exportFonts = "exportFonts";
	private static final String NODE_export = "export";
	private static final String NODE_locales = "locales";
	private static final String NODE_locale = "locale";
	private static final String NODE_includedScript = "includedScript";
	private static final String NODE_excludedScript = "excludedScript";
	private static final String NODE_fontSet = "fontSet";
	private static final String NODE_family = "family";
	private static final String ATTRIBUTE_name = "name";
	private static final String ATTRIBUTE_visible = "visible";
	private static final String ATTRIBUTE_key = "key";
	private static final String ATTRIBUTE_familyName = "familyName";
	private static final String ATTRIBUTE_primary = "primary";
	
	/**
	 * Return a new instance.
	 * 
	 * @return a new instance
	 */
	public static SimpleFontExtensionHelper getInstance()
	{
		SimpleFontExtensionHelper instance = new SimpleFontExtensionHelper();
		return instance;
	}
	
	/**
	 *
	 */
	private DocumentBuilder documentBuilder;


	/**
	 *
	 */
	private SimpleFontExtensionHelper()
	{
		try
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(JRXmlUtils.FEATURE_DISALLOW_DOCTYPE, true);
			
			documentBuilder = factory.newDocumentBuilder();
			documentBuilder.setErrorHandler(this);
		}
		catch (ParserConfigurationException e)
		{
			throw new JRRuntimeException(e);
		}
	}


	/**
	 * @see #loadFontFamilies(JasperReportsContext, String)
	 */
	public List<FontFamily> loadFontFamilies(String file)
	{
		return loadFontFamilies(DefaultJasperReportsContext.getInstance(), file);
	}
	
	/**
	 *
	 */
	public List<FontFamily> loadFontFamilies(JasperReportsContext jasperReportsContext, String file)
	{
		FontExtensionsCollector collector = new FontExtensionsCollector();
		loadFontExtensions(jasperReportsContext, file, collector);
		return collector.getFontFamilies();
	}
	
	public void loadFontExtensions(JasperReportsContext jasperReportsContext, String file,
			FontExtensionsReceiver receiver)
	{
		InputStream is = null; 
		
		try
		{
			is = RepositoryUtil.getInstance(jasperReportsContext).getInputStreamFromLocation(file);
			loadFontExtensions(jasperReportsContext, is, receiver, true);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}
	
	/**
	 *
	 */
	public List<FontFamily> loadFontFamilies(JasperReportsContext jasperReportsContext, InputStream is)
	{
		FontExtensionsCollector collector = new FontExtensionsCollector();
		loadFontExtensions(jasperReportsContext, is, collector);
		return collector.getFontFamilies();
	}

	public void loadFontExtensions(JasperReportsContext jasperReportsContext, InputStream is,
			FontExtensionsReceiver receiver)
	{
		loadFontExtensions(jasperReportsContext, is, receiver, true);
	}

	public void loadFontExtensions(JasperReportsContext jasperReportsContext, InputStream is,
			FontExtensionsReceiver receiver, boolean loadFonts)
	{
		try
		{
			Document document = documentBuilder.parse(new InputSource(new InputStreamReader(is, StandardCharsets.UTF_8)));
			parseFontExtensions(jasperReportsContext, document.getDocumentElement(), receiver, loadFonts);
		}
		catch (SAXException | IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	/**
	 *
	 */
	private void parseFontExtensions(JasperReportsContext jasperReportsContext, Node fontFamiliesNode,
			FontExtensionsReceiver receiver, boolean loadFonts) throws SAXException
	{
		NodeList nodeList = fontFamiliesNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				if (NODE_fontFamily.equals(node.getNodeName()))
				{
					try
					{
						SimpleFontFamily fontFamily = parseFontFamily(jasperReportsContext, node, loadFonts);
						receiver.acceptFontFamily(fontFamily);
					}
					catch (InvalidFontException e)//only catching the specific InvalidFontException for now
					{
						log.error("Error loading font family", e);
						//discarding the whole family
					}
				}
				else if (NODE_fontSet.equals(node.getNodeName()))
				{
					SimpleFontSet fontSet = parseFontSet(node);
					receiver.acceptFontSet(fontSet);
				}
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	private SimpleFontFamily parseFontFamily(JasperReportsContext jasperReportsContext, Node fontFamilyNode,
			boolean loadFonts) throws SAXException
	{
		SimpleFontFamily fontFamily = new SimpleFontFamily(jasperReportsContext);
		
		NamedNodeMap nodeAttrs = fontFamilyNode.getAttributes();
		
		if (nodeAttrs.getNamedItem(ATTRIBUTE_name) != null)
		{
			fontFamily.setName(nodeAttrs.getNamedItem(ATTRIBUTE_name).getNodeValue());
			if (log.isDebugEnabled())
			{
				log.debug("Parsing font family " + fontFamily.getName());
			}
		}
		if (nodeAttrs.getNamedItem(ATTRIBUTE_visible) != null)
		{
			fontFamily.setVisible(
				Boolean.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_visible).getNodeValue())
				);
		}

		NodeList nodeList = fontFamilyNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				if (NODE_normal.equals(node.getNodeName()))
				{
					fontFamily.setNormalFace(parseFontFace(jasperReportsContext, node, loadFonts));
				}
				else if (NODE_bold.equals(node.getNodeName()))
				{
					fontFamily.setBoldFace(parseFontFace(jasperReportsContext, node, loadFonts));
				}
				else if (NODE_italic.equals(node.getNodeName()))
				{
					fontFamily.setItalicFace(parseFontFace(jasperReportsContext, node, loadFonts));
				}
				else if (NODE_boldItalic.equals(node.getNodeName()))
				{
					fontFamily.setBoldItalicFace(parseFontFace(jasperReportsContext, node, loadFonts));
				}
				else if (NODE_normalPdfFont.equals(node.getNodeName()))
				{
					fontFamily.setNormalPdfFont(node.getTextContent());
				}
				else if (NODE_boldPdfFont.equals(node.getNodeName()))
				{
					fontFamily.setBoldPdfFont(node.getTextContent());
				}
				else if (NODE_italicPdfFont.equals(node.getNodeName()))
				{
					fontFamily.setItalicPdfFont(node.getTextContent());
				}
				else if (NODE_boldItalicPdfFont.equals(node.getNodeName()))
				{
					fontFamily.setBoldItalicPdfFont(node.getTextContent());
				}
				else if (NODE_pdfEncoding.equals(node.getNodeName()))
				{
					fontFamily.setPdfEncoding(node.getTextContent());
				}
				else if (NODE_pdfEmbedded.equals(node.getNodeName()))
				{
					fontFamily.setPdfEmbedded(Boolean.valueOf(node.getTextContent()));
				}
				else if (NODE_exportFonts.equals(node.getNodeName()))
				{
					fontFamily.setExportFonts(parseExportFonts(node));
				}
				else if (NODE_locales.equals(node.getNodeName()))
				{
					fontFamily.setLocales(parseLocales(node));
				}
			}
		}
		
		return fontFamily;
	}

	/**
	 *
	 */
	private SimpleFontFace parseFontFace(JasperReportsContext jasperReportsContext, Node fontFaceNode,
			boolean loadFonts) throws SAXException
	{
		SimpleFontFace fontFace = new SimpleFontFace(jasperReportsContext);
		
		NodeList nodeList = fontFaceNode.getChildNodes();

		if (
			nodeList.getLength() == 1 
			&& (fontFaceNode.getFirstChild().getNodeType() == Node.TEXT_NODE
			|| fontFaceNode.getFirstChild().getNodeType() == Node.CDATA_SECTION_NODE)
			)
		{
			fontFace.setTtf(fontFaceNode.getFirstChild().getTextContent(), loadFonts);
		}
		else
		{
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					if (NODE_ttf.equals(node.getNodeName()))
					{
						fontFace.setTtf(node.getTextContent(), loadFonts);
					}
					else if (NODE_pdf.equals(node.getNodeName()))
					{
						fontFace.setPdf(node.getTextContent());
					}
					else if (NODE_eot.equals(node.getNodeName()))
					{
						fontFace.setEot(node.getTextContent());
					}
					else if (NODE_svg.equals(node.getNodeName()))
					{
						fontFace.setSvg(node.getTextContent());
					}
					else if (NODE_woff.equals(node.getNodeName()))
					{
						fontFace.setWoff(node.getTextContent());
					}
				}
			}
		}
		
		return fontFace;
	}

	/**
	 *
	 */
	private Map<String,String> parseExportFonts(Node exportFontsNode)
	{
		Map<String,String> exportFonts = new HashMap<>();
		
		NodeList nodeList = exportFontsNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (
				node.getNodeType() == Node.ELEMENT_NODE
				&& NODE_export.equals(node.getNodeName())
				)
			{
				NamedNodeMap nodeAttrs = node.getAttributes();
				
				if (nodeAttrs.getNamedItem(ATTRIBUTE_key) != null)
				{
					exportFonts.put(nodeAttrs.getNamedItem(ATTRIBUTE_key).getNodeValue(), node.getTextContent());
				}
			}
		}
		
		return exportFonts;
	}
	
	private SimpleFontSet parseFontSet(Node fontSetNode)
	{
		SimpleFontSet fontSet = new SimpleFontSet();
		
		NamedNodeMap nodeAttrs = fontSetNode.getAttributes();
		if (nodeAttrs.getNamedItem(ATTRIBUTE_name) != null)
		{
			fontSet.setName(nodeAttrs.getNamedItem(ATTRIBUTE_name).getNodeValue());
		}

		NodeList nodeList = fontSetNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				if (NODE_family.equals(node.getNodeName()))
				{
					SimpleFontSetFamily family = parsetFontSetFamily(node);
					fontSet.addFamily(family);
				}
				else if (NODE_exportFonts.equals(node.getNodeName()))
				{
					fontSet.setExportFonts(parseExportFonts(node));
				}
			}
		}
		
		return fontSet;
	}
	
	private SimpleFontSetFamily parsetFontSetFamily(Node familyNode)
	{
		SimpleFontSetFamily family = new SimpleFontSetFamily();
		
		NamedNodeMap nodeAttrs = familyNode.getAttributes();
		if (nodeAttrs.getNamedItem(ATTRIBUTE_familyName) != null)
		{
			family.setFamilyName(nodeAttrs.getNamedItem(ATTRIBUTE_familyName).getNodeValue());
		}
		if (nodeAttrs.getNamedItem(ATTRIBUTE_primary) != null)
		{
			family.setPrimary(Boolean.parseBoolean(nodeAttrs.getNamedItem(ATTRIBUTE_primary).getNodeValue()));
		}

		NodeList nodeList = familyNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				if (NODE_includedScript.equals(node.getNodeName()))
				{
					family.addIncludedScript(node.getTextContent());
				}
				else if (NODE_excludedScript.equals(node.getNodeName()))
				{
					family.addExcludedScript(node.getTextContent());
				}
			}
		}
		
		return family;
	}

	/**
	 *
	 */
	public static String getFontsXml(List<FontFamily> fontFamilies)
	{
		if (fontFamilies != null)
		{
			FontExtensionsContainer extensions = new SimpleFontExtensionsContainer(fontFamilies, null);
			StringBuilder sb = new StringBuilder();
			StringBuilderWriter writer = new StringBuilderWriter(sb);
			try
			{
				writeFontExtensions(writer, extensions);
			}
			catch (IOException e)
			{
				//should not happen
				throw new JRRuntimeException(e);
			}
			return sb.toString();
		}
		else
		{
			log.error("There are no font families in the list.");
			return null;
		}
	}

	protected static void writeFontExtensions(Writer out, FontExtensionsContainer extensions) throws IOException
	{
		JRXmlWriteHelper writer = new JRXmlWriteHelper(out);
		writer.writeProlog(StandardCharsets.UTF_8.name());
		
		writer.startElement("fontFamilies");
		
		List<? extends FontFamily> fontFamilies = extensions.getFontFamilies();
		if (fontFamilies != null)
		{
			for (FontFamily fontFamily : fontFamilies)
			{
				writeFontFamily(writer, fontFamily);
			}
		}
		
		List<? extends FontSet> fontSets = extensions.getFontSets();
		if (fontSets != null)
		{
			for (FontSet fontSet : fontSets)
			{
				writeFontSet(writer, fontSet);
			}
		}

		writer.closeElement();
	}

	/**
	 * @throws IOException 
	 *
	 */
	private static void writeFontFamily(JRXmlWriteHelper writer, FontFamily fontFamily) throws IOException 
	{
		if (fontFamily != null)
		{
			if (fontFamily.getName() == null)
			{
				log.error("Font family name is required.");
				return;
			}
			
			writer.startElement(NODE_fontFamily);
			writer.addAttribute(ATTRIBUTE_name, fontFamily.getName());
			writer.addAttribute(ATTRIBUTE_visible, fontFamily.isVisible(), true);
			
			writeFontFace(writer, fontFamily.getNormalFace(), NODE_normal);
			writeFontFace(writer, fontFamily.getBoldFace(), NODE_bold);
			writeFontFace(writer, fontFamily.getItalicFace(), NODE_italic);
			writeFontFace(writer, fontFamily.getBoldItalicFace(), NODE_boldItalic);

			if (fontFamily.getPdfEncoding() != null)
			{
				writer.writeCDATAElement(NODE_pdfEncoding, fontFamily.getPdfEncoding());
				
			}
			if (fontFamily.isPdfEmbedded() != null)
			{
				//TODO lucianc we can do without CDATA here
				writer.writeCDATAElement(NODE_pdfEmbedded, Boolean.toString(fontFamily.isPdfEmbedded()));
			}
			
			if (fontFamily instanceof SimpleFontFamily)
			{
				SimpleFontFamily simpleFontFamily = (SimpleFontFamily)fontFamily;
				
				Map<String, String> exportFonts = simpleFontFamily.getExportFonts();
				writeExportFonts(writer, exportFonts);
				
				Set<String> locales = simpleFontFamily.getLocales();
				
				if (locales != null)
				{
					writer.startElement(NODE_locales);
					for (String locale : locales)
					{
						writer.writeCDATAElement(NODE_locale, locale);
					}
					writer.closeElement();
				}
			}
			writer.closeElement();
		}		
		else
		{
			log.info("Null font family encountered.");
		}
	}


	protected static void writeExportFonts(JRXmlWriteHelper writer, 
			Map<String, String> exportFonts) throws IOException
	{
		if (exportFonts != null)
		{
			writer.startElement(NODE_exportFonts);
			for (Entry<String, String> exportFont : exportFonts.entrySet())
			{
				writer.writeCDATAElement(NODE_export, exportFont.getValue(), ATTRIBUTE_key, exportFont.getKey());
			}
			writer.closeElement();
		}
	}
	


	/**
	 * @throws IOException 
	 *
	 */
	private static void writeFontFace(JRXmlWriteHelper writer, FontFace fontFace, String faceTypeName) throws IOException 
	{
		if (fontFace != null)
		{
			if (
				fontFace.getPdf() == null
				&& fontFace.getEot() == null
				&& fontFace.getSvg() == null
				&& fontFace.getWoff() == null
				)
			{
				if (fontFace.getTtf() != null)
				{
					writer.writeCDATAElement(faceTypeName, fontFace.getTtf());
				}
			}
			else
			{
				writer.startElement(faceTypeName);
				if (fontFace.getTtf() != null)
				{
					writer.writeCDATAElement("ttf", fontFace.getTtf());
				}
				if (fontFace.getPdf() != null)
				{
					writer.writeCDATAElement("pdf", fontFace.getPdf());
				}
				if (fontFace.getEot() != null)
				{
					writer.writeCDATAElement("eot", fontFace.getEot());
				}
				if (fontFace.getSvg() != null)
				{
					writer.writeCDATAElement("svg", fontFace.getSvg());
				}
				if (fontFace.getWoff() != null)
				{
					writer.writeCDATAElement("woff", fontFace.getWoff());
				}
				writer.closeElement();
			}
		}
	}
	
	private static void writeFontSet(JRXmlWriteHelper writer, FontSet fontSet) throws IOException
	{
		if (fontSet == null)
		{
			log.info("Null font set encountered.");
			return;
		}
		
		if (fontSet.getName() == null)
		{
			log.error("Font set name is required.");
			return;
		}
			
		writer.startElement(NODE_fontSet);
		writer.addAttribute(ATTRIBUTE_name, fontSet.getName());
			
		if (fontSet instanceof SimpleFontSet)
		{
			Map<String, String> exportFonts = ((SimpleFontSet) fontSet).getExportFonts();
			writeExportFonts(writer, exportFonts);
		}
			
		List<FontSetFamily> families = fontSet.getFamilies();
		if (families != null)
		{
			for (FontSetFamily family : families)
			{
				writeFontSetFamily(writer, family);
			}
		}
		
		writer.closeElement();
	}

	private static void writeFontSetFamily(JRXmlWriteHelper writer, FontSetFamily family) throws IOException
	{
		if (family == null)
		{
			log.info("Null font set family encountered.");
			return;
		}
		
		if (family.getFamilyName() == null)
		{
			log.error("Font set name is required.");
			return;
		}
		
		writer.startElement(NODE_family);
		writer.addAttribute(ATTRIBUTE_familyName, family.getFamilyName());
		writer.addAttribute(ATTRIBUTE_primary, family.isPrimary(), false);
		
		List<String> includedScripts = family.getIncludedScripts();
		if (includedScripts != null)
		{
			for (String script : includedScripts)
			{
				writer.writeCDATAElement(NODE_includedScript, script);
			}
		}
		
		List<String> excludedScripts = family.getExcludedScripts();
		if (excludedScripts != null)
		{
			for (String script : excludedScripts)
			{
				writer.writeCDATAElement(NODE_excludedScript, script);
			}
		}
		
		writer.closeElement();
	}


	/**
	 *
	 */
	public static void writeFontsXml(
		String destFileName,
		List<FontFamily> fontFamilies
		) throws JRException
	{
		FontExtensionsContainer extensions = new SimpleFontExtensionsContainer(fontFamilies, null);
		writeFontExtensionsXml(destFileName, extensions);
	}

	public static void writeFontExtensionsXml(
		String destFileName,
		FontExtensionsContainer extensions
		) throws JRException
	{
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(destFileName)))
		{
			writeFontExtensionsXml(os, extensions);
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_FILE_WRITER_ERROR,
					new Object[]{destFileName},
					e);
		}
	}


	/**
	 *
	 */
	public static void writeFontsXml(
		OutputStream outputStream,
		List<FontFamily> fontFamilies
		) throws JRException
	{
		FontExtensionsContainer extensions = new SimpleFontExtensionsContainer(fontFamilies, null);
		writeFontExtensionsXml(outputStream, extensions);
	}

	public static void writeFontExtensionsXml(
		OutputStream outputStream,
		FontExtensionsContainer extensions
		) throws JRException
	{
		Writer out = null;
		try
		{
			out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
			writeFontExtensions(out, extensions);
			out.flush();
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_OUTPUT_STREAM_WRITER_ERROR, 
					null, 
					e);
		}
	}


	/**
	 *
	 */
	public static void writeFontExtensionsProperties(String fontsXmlLocation, String destFileName) throws JRException
	{
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream(destFileName, false)))
		{
			writeFontExtensionsProperties(fontsXmlLocation, os);
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_FILE_WRITER_ERROR,
					new Object[]{destFileName},
					e);
		}
	}


	/**
	 *
	 */
	public static void writeFontExtensionsProperties(String fontFamiliesPropertyValue, OutputStream outputStream) throws JRException
	{
		writeFontExtensionsProperties(
				SimpleFontExtensionsRegistryFactory.PROPERTY_SIMPLE_FONT_FAMILIES_REGISTRY_FACTORY,
				SimpleFontExtensionsRegistryFactory.class.getName(),
				SimpleFontExtensionsRegistryFactory.SIMPLE_FONT_FAMILIES_PROPERTY_PREFIX +	"location", 
				fontFamiliesPropertyValue,
				outputStream);
	}


	/**
	 *
	 */
	public static void writeFontExtensionsProperties(
			String fontRegistryFactoryPropertyName, 
			String fontRegistryFactoryPropertyValue, 
			String fontFamiliesPropertyName, 
			String fontFamiliesPropertyValue, 
			OutputStream outputStream
			) throws JRException
	{
		try (Writer out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
		{
			out.write(
					fontRegistryFactoryPropertyName + 
					"=" + 
					fontRegistryFactoryPropertyValue + 
					"\n"
					);
			out.write(
					fontFamiliesPropertyName +
					"=" + 
					fontFamiliesPropertyValue + 
					"\n"
					);
		}
		catch (IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_OUTPUT_STREAM_WRITER_ERROR, 
					null, 
					e);
		}
	}


	/**
	 *
	 */
	private Set<String> parseLocales(Node localesNode) throws SAXException
	{
		Set<String> locales = new HashSet<>();
		
		NodeList nodeList = localesNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (
				node.getNodeType() == Node.ELEMENT_NODE
				&& NODE_locale.equals(node.getNodeName())
				)
			{
				locales.add(node.getTextContent());
			}
		}
		
		return locales;
	}


	@Override
	public void error(SAXParseException e) {
		if (log.isErrorEnabled())
		{
			log.error("Error parsing styled text.", e);
		}
	}

	@Override
	public void fatalError(SAXParseException e) {
		if (log.isFatalEnabled())
		{
			log.fatal("Error parsing styled text.", e);
		}
	}

	@Override
	public void warning(SAXParseException e) {
		if (log.isWarnEnabled())
		{
			log.warn("Error parsing styled text.", e);
		}
	}

}
