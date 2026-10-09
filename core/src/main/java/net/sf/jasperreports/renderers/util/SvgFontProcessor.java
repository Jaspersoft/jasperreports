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
package net.sf.jasperreports.renderers.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.HtmlFontFamily;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.renderers.BatikUserAgent;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class SvgFontProcessor
{
	public static final String SVG_ELEMENT_svg = "svg";
	public static final String SVG_ELEMENT_g = "g";
	public static final String SVG_ELEMENT_text = "text";
	public static final String SVG_ATTRIBUTE_style = "style";
	public static final String SVG_ATTRIBUTE_fontFamily = "font-family";
	
	private final SVGDocumentFactory documentFactory;
	private final Locale locale;
	
	/**
	 * 
	 */
	protected SvgFontProcessor(
		JasperReportsContext jasperReportsContext,
		Locale locale
		)
	{
		UserAgent userAgent = new BatikUserAgent(jasperReportsContext);
		documentFactory =
			new SAXSVGDocumentFactory(userAgent.getXMLParserClassName(), true);
		documentFactory.setValidating(userAgent.isXMLParserValidating());

		this.locale = locale;
	}
	
	/**
	 * 
	 */
	public byte[] process(byte[] svgData)
	{
		try
		{
			SVGDocument document = 
				documentFactory.createSVGDocument(
					null,
					new ByteArrayInputStream(svgData)
					);
			
			process(document);
			
			TransformerFactory tFactory =
				JRXmlUtils.createTransformerFactory();
			Transformer transformer = 
				tFactory.newTransformer();
			
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			DOMSource source = new DOMSource(document);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(baos);
			transformer.transform(source, result);
			
			return baos.toByteArray();
		}
		catch (TransformerException | IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	
	private void process(Node element)
	{
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (Node.ELEMENT_NODE == child.getNodeType())
			{
				if (
					SVG_ELEMENT_svg.equals(child.getNodeName())
					|| SVG_ELEMENT_g.equals(child.getNodeName())
					|| SVG_ELEMENT_text.equals(child.getNodeName())
					)
				{
					processFontAttributes((Element)child);
				}
				
				process(child);
			}
		}
	}

	
	private void processFontAttributes(Element styleElement)
	{
		String fontFamilyAttrValue = null;

		NamedNodeMap attributes = styleElement.getAttributes();
		
		Node fontFamilyAttrNode = attributes.getNamedItem(SVG_ATTRIBUTE_fontFamily);
		if (fontFamilyAttrNode != null)
		{
			fontFamilyAttrValue = fontFamilyAttrNode.getNodeValue();
		}
		
		// values from style property takes precedence over values from attributes
		
		Node styleAttrNode = attributes.getNamedItem(SVG_ATTRIBUTE_style);
		if (styleAttrNode != null)
		{
			String styleFontFamily = getFontFamily(styleAttrNode.getNodeValue());
			if (styleFontFamily != null)
			{
				fontFamilyAttrValue = styleFontFamily;
			}
		}
		
		if (fontFamilyAttrValue != null)
		{
			StringBuilder newFamilyNameAttrValue = new StringBuilder();
			
			boolean firstToken = true;
			String[] fontFamilyTokens = fontFamilyAttrValue.split(",");
			for (String fontFamilyToken : fontFamilyTokens)
			{
				if (!firstToken)
				{
					newFamilyNameAttrValue.append(",");
				}
				
				String fontFamily = fontFamilyToken.trim();
				
				if (
					fontFamily.startsWith("'")
					&& fontFamily.endsWith("'") 
					)
				{
					fontFamily = fontFamily.substring(1, fontFamily.length() - 1);
				}
				
				// svg font-family could have locale suffix because it is needed in svg measured by phantomjs
				int localeSeparatorPos = fontFamily.lastIndexOf(HtmlFontFamily.LOCALE_SEPARATOR);
				if (localeSeparatorPos > 0)
				{
					fontFamily = fontFamily.substring(0, localeSeparatorPos);
				}

				fontFamily = 
					getFontFamily(
						fontFamily, 
						locale //FIXMEBATIK could use locale from svg above
						);
						
				newFamilyNameAttrValue.append(" " + fontFamily);

				firstToken = false;
			}

			if (styleAttrNode == null)
			{
				// do not put single quotes around family name here because the value might already contain quotes, 
				// especially if it is coming from font extension export configuration
				styleElement.setAttribute(SVG_ATTRIBUTE_fontFamily, newFamilyNameAttrValue.toString());
			}
			else
			{
				String newStyleAttr = replaceOrAddFontFamilyInStyle(styleAttrNode.getNodeValue(), newFamilyNameAttrValue.toString());
				styleElement.setAttribute(SVG_ATTRIBUTE_style, newStyleAttr);
			}
		}
	}

	
	private String getFontFamily(String style)
	{
		String fontFamily = null;
		{
			int fontFamilyStart = style.indexOf(SVG_ATTRIBUTE_fontFamily + ":");
			if (fontFamilyStart >= 0)
			{
				fontFamilyStart = fontFamilyStart + SVG_ATTRIBUTE_fontFamily.length() + 1;
				int fontFamilyEnd = style.indexOf(";", fontFamilyStart);
				if (fontFamilyEnd >= 0)
				{
					fontFamily = style.substring(fontFamilyStart, fontFamilyEnd).trim();
				}
				else
				{
					fontFamily = style.substring(fontFamilyStart).trim();
				}
			}
		}

		return fontFamily;
	}


	private String replaceOrAddFontFamilyInStyle(String style, String fontFamily)
	{
		StringBuilder sb = new StringBuilder();
		
		int fontFamilyStart = style.indexOf(SVG_ATTRIBUTE_fontFamily + ":");
		if (fontFamilyStart >= 0)
		{
			fontFamilyStart = fontFamilyStart + SVG_ATTRIBUTE_fontFamily.length() + 1;
			sb.append(style.substring(0, fontFamilyStart));
			// do not put single quotes around family name here because the value might already contain quotes, 
			// especially if it is coming from font extension export configuration
			sb.append(fontFamily);
			int fontFamilyEnd = style.indexOf(";", fontFamilyStart);
			if (fontFamilyEnd >= 0)
			{
				sb.append(style.substring(fontFamilyEnd));
			}
		}
		else
		{
			sb.append(style);
			if (!style.trim().endsWith(";"))
			{
				sb.append(";");
			}
			// do not put single quotes around family name here because the value might already contain quotes, 
			// especially if it is coming from font extension export configuration
			sb.append(SVG_ATTRIBUTE_fontFamily + ": " + fontFamily + ";");
		}
		
		return sb.toString();
	}


	/**
	 * 
	 */
	public abstract String getFontFamily(String fontFamily, Locale locale);
}
