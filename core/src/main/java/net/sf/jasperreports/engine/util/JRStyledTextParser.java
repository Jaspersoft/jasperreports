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
package net.sf.jasperreports.engine.util;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;

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

import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBasePrintHyperlink;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.util.JRStyledText.Run;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRStyledTextParser implements ErrorHandler
{
	private static final Log log = LogFactory.getLog(JRStyledTextParser.class);

	private static final Set<String> AVAILABLE_FONT_FACE_NAMES = new HashSet<>();
	static
	{
		//FIXME doing this in a static block obscures exceptions, move it to some other place
		try
		{
			//FIXMEFONT do some cache
			//FIXME these should be taken from the current JasperReportsContext
			List<FontFamily> families = ExtensionsEnvironment.getExtensionsRegistry().getExtensions(FontFamily.class);
			families.forEach(f -> AVAILABLE_FONT_FACE_NAMES.add(f.getName()));
			
			//FIXME use JRGraphEnvInitializer
			AVAILABLE_FONT_FACE_NAMES.addAll(
				Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
				);
		}
		catch (Exception e)
		{
			log.error("Error while loading available fonts", e);
			throw e;
		}
	}

	/**
	 *
	 */
	private static final String ROOT_START = "<st>";
	private static final String ROOT_END = "</st>";
	private static final String NODE_style = "style";
	private static final String NODE_bold = "b";
	private static final String NODE_italic = "i";
	private static final String NODE_underline = "u";
	private static final String NODE_sup = "sup";
	private static final String NODE_sub = "sub";
	private static final String NODE_font = "font";
	private static final String NODE_br = "br";
	private static final String NODE_ul = "ul";
	private static final String NODE_ol = "ol";
	private static final String NODE_li = "li";
	private static final String NODE_a = "a";
	private static final String NODE_param = "param";
	private static final String NODE_reference = "reference";
	private static final String NODE_note = "note";
	private static final String ATTRIBUTE_fontName = "fontName";
	private static final String ATTRIBUTE_fontFace = "face";
	private static final String ATTRIBUTE_color = "color";
	private static final String ATTRIBUTE_size = "size";
	private static final String ATTRIBUTE_isBold = "isBold";
	private static final String ATTRIBUTE_isItalic = "isItalic";
	private static final String ATTRIBUTE_isUnderline = "isUnderline";
	private static final String ATTRIBUTE_isStrikeThrough = "isStrikeThrough";
	private static final String ATTRIBUTE_forecolor = "forecolor";
	private static final String ATTRIBUTE_backcolor = "backcolor";
	private static final String ATTRIBUTE_pdfFontName = "pdfFontName";
	private static final String ATTRIBUTE_pdfEncoding = "pdfEncoding";
	private static final String ATTRIBUTE_isPdfEmbedded = "isPdfEmbedded";
	private static final String ATTRIBUTE_type = "type";
	private static final String ATTRIBUTE_href = "href";
	private static final String ATTRIBUTE_target = "target";
	private static final String ATTRIBUTE_name = "name";
	private static final String ATTRIBUTE_valueClass = "valueClass";
	private static final String ATTRIBUTE_start = "start";
	private static final String ATTRIBUTE_noBullet = "noBullet";

	private static final String SPACE = " ";
	private static final String EQUAL_QUOTE = "=\"";
	private static final String QUOTE = "\"";
	private static final String LESS = "<";
	private static final String LESS_SLASH = "</";
	private static final String GREATER = ">";
	
	/**
	 * Thread local soft cache of instances.
	 */
	private static final ThreadLocal<SoftReference<JRStyledTextParser>> threadInstances = new ThreadLocal<>();
	
	/**
	 * 
	 */
	private static final ThreadLocal<Locale> threadLocale = new ThreadLocal<>();
	
	/**
	 * Return a cached instance.
	 * 
	 * @return a cached instance
	 */
	public static JRStyledTextParser getInstance()
	{
		JRStyledTextParser instance = null;
		SoftReference<JRStyledTextParser> instanceRef = threadInstances.get();
		if (instanceRef != null)
		{
			instance =  instanceRef.get();
		}
		if (instance == null)
		{
			instance = new JRStyledTextParser();
			threadInstances.set(new SoftReference<>(instance));
		}
		return instance;
	}
	

	/**
	 * 
	 */
	public static void setLocale(Locale locale)
	{
		threadLocale.set(locale);
	}
	
	/**
	 * 
	 */
	public static Locale getLocale()
	{
		return threadLocale.get();
	}
	
	/**
	 *
	 */
	private DocumentBuilder documentBuilder;
	
	/**
	 *
	 */
	private JRBasePrintHyperlink hyperlink;
	
	/**
	 *
	 */
	private Stack<StyledTextListInfo> htmlListStack;
	private boolean insideLi;
	private boolean liStart;
	private StyledTextListInfo justClosedList;


	/**
	 *
	 */
	private JRStyledTextParser()
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
	 *
	 */
	public JRStyledText parse(Map<Attribute,Object> attributes, String text, Locale locale) throws SAXException
	{
		JRStyledText styledText = new JRStyledText(locale);
		
		Document document = null;

		try
		{
			document = documentBuilder.parse(new InputSource(new StringReader(ROOT_START + text + ROOT_END)));
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		
		hyperlink = null;
		htmlListStack = new Stack<>();
		
		parseStyle(styledText, document.getDocumentElement());
		
		styledText.setGlobalAttributes(attributes);
		
		return styledText;
	}

	/**
	 * Creates a styled text object by either parsing a styled text String or
	 * by wrapping an unstyled String.
	 * 
	 * @param parentAttributes the element-level styled text attributes
	 * @param text the (either styled or unstyled) text
	 * @param isStyledText flag indicating that the text is styled
	 * @param locale the locale for the text
	 * @return a styled text object
	 */
	public JRStyledText getStyledText(Map<Attribute,Object> parentAttributes, String text, boolean isStyledText, Locale locale)
	{
		JRStyledText styledText = null;
		if (
			isStyledText 
			&& text != null 
			&& (text.indexOf('<') >= 0 || text.indexOf('&') >= 0)
			)
		{
			try
			{
				styledText = parse(parentAttributes, text, locale);
			}
			catch (SAXException e)
			{
				//ignore if invalid styled text and treat like normal text
			}
		}
	
		if (styledText == null)
		{
			// using the original String object instead without creating a buffer and a String copy
			styledText = new JRStyledText(locale, text, parentAttributes);
		}
		
		return styledText;
	}
	
	/**
	 * Outputs a styled text String given a styled text instance.
	 * 
	 * @param styledText the styled text object
	 * @return the String styled text representation
	 */
	public String write(JRStyledText styledText)
	{
		return write(styledText.getGlobalAttributes(), 
				styledText.getAttributedString().getIterator(), 
				styledText.getText());
	}
	
	/**
	 * Outputs a styled text String given a set of element-level styled text
	 * attributes and a styled text in the form of a String text and an iterator
	 * of style attributes.
	 * 
	 * @param parentAttrs the element-level styled text attributes
	 * @param iterator iterator of styled text attributes
	 * @param text the text
	 * @return the String styled text representation
	 */
	public String write(Map<Attribute,Object> parentAttrs, AttributedCharacterIterator iterator, String text)
	{
		StyledTextWriteContext context = new StyledTextWriteContext();
		
		StringBuilder sb = new StringBuilder();
		XmlStyledTextListWriter xmlListWriter = new XmlStyledTextListWriter(sb);
		
		int runLimit = 0;

		while (runLimit < iterator.getEndIndex() && (runLimit = iterator.getRunLimit()) <= iterator.getEndIndex())
		{
			String chunk = text.substring(iterator.getIndex(), runLimit);
			Map<Attribute,Object> attrs = iterator.getAttributes();
			
			context.next(attrs);

			context.writeLists(xmlListWriter);

			writeChunk(context, sb, parentAttrs, attrs, chunk);

			iterator.setIndex(runLimit);
		}
		
		context.next(null);

		context.writeLists(xmlListWriter);
		
		return sb.toString();
	}

	/**
	 * Outputs the String representation of a styled text chunk.
	 * 
	 * @param styledText the styled text
	 * @param startIndex the start index
	 * @param endIndex the end index
	 * @return the String styled text representation of the chunk delimited by
	 * the start index and the end index
	 * @see #write(Map, AttributedCharacterIterator, String)
	 */
	public String write(JRStyledText styledText, 
			int startIndex, int endIndex)
	{
		AttributedCharacterIterator subIterator = new AttributedString(
				styledText.getAttributedString().getIterator(), 
				startIndex, endIndex).getIterator();
		String subText = styledText.getText().substring(startIndex, endIndex);
		return write(styledText.getGlobalAttributes(), subIterator, subText);
	}

	/**
	 *
	 */
	public void writeChunk(StyledTextWriteContext context, StringBuilder sb, Map<Attribute,Object> parentAttrs, Map<Attribute,Object> attrs, String chunk)
	{
		StringBuilder styleBuilder = writeStyleAttributes(parentAttrs, attrs);
		boolean isStyle = styleBuilder.length() > 0;
		if (isStyle)
		{
			sb.append(LESS);
			sb.append(NODE_style);
			sb.append(styleBuilder.toString());
			sb.append(GREATER);
		}

		Object value = attrs.get(TextAttribute.SUPERSCRIPT);
		Object oldValue = parentAttrs.get(TextAttribute.SUPERSCRIPT);

		boolean isSuper = false;
		boolean isSub = false;
		
		if (value != null && !value.equals(oldValue))
		{
			isSuper=TextAttribute.SUPERSCRIPT_SUPER.equals(value);
			isSub=TextAttribute.SUPERSCRIPT_SUB.equals(value);
		}

		String scriptNode = isSuper?NODE_sup:NODE_sub;

		if (isSuper || isSub)
		{
			sb.append(LESS);
			sb.append(scriptNode);
			sb.append(GREATER);
		}

		Boolean isReference = (Boolean)attrs.get(JRTextAttribute.REFERENCE);
		if (Boolean.TRUE.equals(isReference))
		{
			sb.append(LESS);
			sb.append(NODE_reference);
			sb.append(GREATER);
		}

		Boolean isNote = (Boolean)attrs.get(JRTextAttribute.REFERENCE);
		if (Boolean.TRUE.equals(isNote))
		{
			sb.append(LESS);
			sb.append(NODE_note);
			sb.append(GREATER);
		}

		JRPrintHyperlink hlink = (JRPrintHyperlink)attrs.get(JRTextAttribute.HYPERLINK);
		if (hlink != null)
		{
			sb.append(LESS);
			sb.append(NODE_a);

			String href = hlink.getHyperlinkReference();
			if (href != null && href.trim().length() > 0)
			{
				sb.append(SPACE);
				sb.append(ATTRIBUTE_href);
				sb.append(EQUAL_QUOTE);
				sb.append(JRStringUtil.htmlEncode(href));
				sb.append(QUOTE);
			}
			
			String type = hlink.getLinkType();
			if (type != null && type.trim().length() > 0)
			{
				sb.append(SPACE);
				sb.append(ATTRIBUTE_type);
				sb.append(EQUAL_QUOTE);
				sb.append(type);
				sb.append(QUOTE);
			}
			
			String target = hlink.getLinkTarget();
			if (target != null && target.trim().length() > 0)
			{
				sb.append(SPACE);
				sb.append(ATTRIBUTE_target);
				sb.append(EQUAL_QUOTE);
				sb.append(target);
				sb.append(QUOTE);
			}
			
			sb.append(GREATER);
			
			JRPrintHyperlinkParameters parameters = hlink.getHyperlinkParameters();
			if (parameters != null && parameters.getParameters() != null)
			{
				for (JRPrintHyperlinkParameter parameter : parameters.getParameters())
				{
					sb.append(LESS);
					sb.append(NODE_param);
					sb.append(SPACE);
					sb.append(ATTRIBUTE_name);
					sb.append(EQUAL_QUOTE);
					sb.append(parameter.getName());
					sb.append(QUOTE);
					sb.append(GREATER);
					
					if (parameter.getValue() != null)
					{
						String strValue = JRValueStringUtils.serialize(parameter.getValueClass(), parameter.getValue());
						sb.append(JRStringUtil.xmlEncode(strValue));
					}

					sb.append(LESS_SLASH);
					sb.append(NODE_param);
					sb.append(GREATER);
				}
			}
		}

		sb.append(JRStringUtil.xmlEncode(chunk));

		if (hlink != null)
		{
			sb.append(LESS_SLASH);
			sb.append(NODE_a);
			sb.append(GREATER);
		}

		if (Boolean.TRUE.equals(isNote))
		{
			sb.append(LESS_SLASH);
			sb.append(NODE_note);
			sb.append(GREATER);
		}
		
		if (Boolean.TRUE.equals(isReference))
		{
			sb.append(LESS_SLASH);
			sb.append(NODE_reference);
			sb.append(GREATER);
		}
		
		if (isSuper || isSub)
		{
			sb.append(LESS_SLASH);
			sb.append(scriptNode);
			sb.append(GREATER);
		}
		
		if (isStyle)
		{
			sb.append(LESS_SLASH);
			sb.append(NODE_style);
			sb.append(GREATER);
		}
	}

	/**
	 *
	 */
	private void parseStyle(JRStyledText styledText, Node parentNode) throws SAXException
	{
		NodeList nodeList = parentNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
			{
				liStart = false;
				justClosedList = null;

				styledText.append(node.getNodeValue());
			}
			else if (
				node.getNodeType() == Node.ELEMENT_NODE
				&& NODE_style.equals(node.getNodeName())
				)
			{
				NamedNodeMap nodeAttrs = node.getAttributes();

				Map<Attribute,Object> styleAttrs = new HashMap<>();

				if (nodeAttrs.getNamedItem(ATTRIBUTE_fontName) != null)
				{
					styleAttrs.put(
						TextAttribute.FAMILY,
						nodeAttrs.getNamedItem(ATTRIBUTE_fontName).getNodeValue()
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_isBold) != null)
				{
					styleAttrs.put(
						TextAttribute.WEIGHT,
						Boolean.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_isBold).getNodeValue())
						? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_isItalic) != null)
				{
					styleAttrs.put(
						TextAttribute.POSTURE,
						Boolean.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_isItalic).getNodeValue())
						? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_isUnderline) != null)
				{
					styleAttrs.put(
						TextAttribute.UNDERLINE,
						Boolean.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_isUnderline).getNodeValue())
						? TextAttribute.UNDERLINE_ON : null
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_isStrikeThrough) != null)
				{
					styleAttrs.put(
						TextAttribute.STRIKETHROUGH,
						Boolean.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_isStrikeThrough).getNodeValue())
						? TextAttribute.STRIKETHROUGH_ON : null
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_size) != null)
				{
					styleAttrs.put(
						TextAttribute.SIZE,
						Float.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_size).getNodeValue())
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_pdfFontName) != null)
				{
					styleAttrs.put(
						JRTextAttribute.PDF_FONT_NAME,
						nodeAttrs.getNamedItem(ATTRIBUTE_pdfFontName).getNodeValue()
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_pdfEncoding) != null)
				{
					styleAttrs.put(
						JRTextAttribute.PDF_ENCODING,
						nodeAttrs.getNamedItem(ATTRIBUTE_pdfEncoding).getNodeValue()
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_isPdfEmbedded) != null)
				{
					styleAttrs.put(
						JRTextAttribute.IS_PDF_EMBEDDED,
						Boolean.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_isPdfEmbedded).getNodeValue())
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_forecolor) != null)
				{
					Color color = 
						JRColorUtil.getColor(
							nodeAttrs.getNamedItem(ATTRIBUTE_forecolor).getNodeValue(),
							Color.black
							);
					styleAttrs.put(
						TextAttribute.FOREGROUND,
						color
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_backcolor) != null)
				{
					Color color = 
						JRColorUtil.getColor(
							nodeAttrs.getNamedItem(ATTRIBUTE_backcolor).getNodeValue(),
							Color.black
							);
					styleAttrs.put(
						TextAttribute.BACKGROUND,
						color
						);
				}

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_bold.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();
				styleAttrs.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_italic.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();
				styleAttrs.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_underline.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();
				styleAttrs.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_sup.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();
				styleAttrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_sub.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();
				styleAttrs.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_font.equalsIgnoreCase(node.getNodeName()))
			{
				NamedNodeMap nodeAttrs = node.getAttributes();

				Map<Attribute,Object> styleAttrs = new HashMap<>();

				if (nodeAttrs.getNamedItem(ATTRIBUTE_size) != null)
				{
					styleAttrs.put(
						TextAttribute.SIZE,
						Float.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_size).getNodeValue())
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_color) != null)
				{
					Color color = 
						JRColorUtil.getColor(
							nodeAttrs.getNamedItem(ATTRIBUTE_color).getNodeValue(),
							Color.black
							);
					styleAttrs.put(
						TextAttribute.FOREGROUND,
						color
						);
				}

				if (nodeAttrs.getNamedItem(ATTRIBUTE_fontFace) != null) 
				{
					String fontFaces = nodeAttrs.getNamedItem(ATTRIBUTE_fontFace).getNodeValue();

					StringTokenizer t = new StringTokenizer(fontFaces, ",");
					while (t.hasMoreTokens()) 
					{
						String face = t.nextToken().trim();
						if (AVAILABLE_FONT_FACE_NAMES.contains(face)) 
						{
							styleAttrs.put(TextAttribute.FAMILY, face);
							break;
						}
					}
				}
				
				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));

			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_br.equalsIgnoreCase(node.getNodeName()))
			{
				styledText.append("\n");

				int startIndex = styledText.length();
				resizeRuns(styledText.getRuns(), startIndex, 1);

				parseStyle(styledText, node);
				styledText.addRun(new JRStyledText.Run(new HashMap<>(), startIndex, styledText.length()));

				if (startIndex < styledText.length()) {
					styledText.append("\n");
					resizeRuns(styledText.getRuns(), startIndex, 1);
				}
			}
			else if (
				node.getNodeType() == Node.ELEMENT_NODE 
				&& (NODE_ul.equalsIgnoreCase(node.getNodeName()) || NODE_ol.equalsIgnoreCase(node.getNodeName()))
				)
			{
				boolean ordered = false;
				String type = null;
				Integer start = null;
				if (NODE_ol.equalsIgnoreCase(node.getNodeName()))
				{
					ordered = true;
					NamedNodeMap nodeAttrs = node.getAttributes();
					if (nodeAttrs != null)
					{
						Node typeNode = nodeAttrs.getNamedItem(ATTRIBUTE_type);
						if (typeNode != null)
						{
							type = typeNode.getNodeValue();
						}
						Node startNode = nodeAttrs.getNamedItem(ATTRIBUTE_start);
						if (startNode != null)
						{
							start = Integer.valueOf(startNode.getNodeValue());
						}
					}
				}
				
				StyledTextListInfo htmlList = 
					new StyledTextListInfo(
						ordered,
						type,
						start,
						insideLi
						);

				htmlList.setAtLiStart(liStart);
				
				htmlListStack.push(htmlList);
				
				insideLi = false;
				
				Map<Attribute,Object> styleAttrs = new HashMap<>();

				styleAttrs.put(JRTextAttribute.HTML_LIST, htmlListStack.toArray(new StyledTextListInfo[htmlListStack.size()]));
				styleAttrs.put(JRTextAttribute.HTML_LIST_ITEM, StyledTextListItemInfo.NO_LIST_ITEM_FILLER);
				
				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
				
				justClosedList = htmlListStack.pop();
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_li.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();

				StyledTextListInfo htmlList = null;
				
				boolean ulAdded = false;
				if (htmlListStack.size() == 0)
				{
					htmlList = new StyledTextListInfo(false, null, null, false);
					htmlListStack.push(htmlList);
					styleAttrs.put(JRTextAttribute.HTML_LIST, htmlListStack.toArray(new StyledTextListInfo[htmlListStack.size()]));
					styleAttrs.put(JRTextAttribute.HTML_LIST_ITEM, StyledTextListItemInfo.NO_LIST_ITEM_FILLER);
					ulAdded = true;
				}
				else
				{
					htmlList = htmlListStack.peek();
				}
				htmlList.setItemCount(htmlList.getItemCount() + 1);
				insideLi = true;
				liStart = true;
				justClosedList = null;
				
				StyledTextListItemInfo listItem = new StyledTextListItemInfo(htmlList.getItemCount() - 1);
				NamedNodeMap nodeAttrs = node.getAttributes();
				if (nodeAttrs.getNamedItem(ATTRIBUTE_noBullet) != null)
				{
					listItem.setNoBullet(Boolean.valueOf(nodeAttrs.getNamedItem(ATTRIBUTE_noBullet).getNodeValue()));
				}
				
				styleAttrs.put(JRTextAttribute.HTML_LIST_ITEM, listItem);
				
				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
				
				insideLi = false;
				liStart = false;
				if (justClosedList != null)
				{
					justClosedList.setAtLiEnd(true);
				}

				if (ulAdded)
				{
					htmlListStack.pop();
				}
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_a.equalsIgnoreCase(node.getNodeName()))
			{
				if (hyperlink == null)
				{
					NamedNodeMap nodeAttrs = node.getAttributes();

					Map<Attribute,Object> styleAttrs = new HashMap<>();

					hyperlink = new JRBasePrintHyperlink();
					hyperlink.setHyperlinkType(HyperlinkTypeEnum.REFERENCE);
					styleAttrs.put(JRTextAttribute.HYPERLINK, hyperlink);
					
					if (nodeAttrs.getNamedItem(ATTRIBUTE_href) != null)
					{
						hyperlink.setHyperlinkReference( nodeAttrs.getNamedItem(ATTRIBUTE_href).getNodeValue());
					}

					if (nodeAttrs.getNamedItem(ATTRIBUTE_type) != null)
					{
						hyperlink.setLinkType(nodeAttrs.getNamedItem(ATTRIBUTE_type).getNodeValue());
					}

					if (nodeAttrs.getNamedItem(ATTRIBUTE_target) != null)
					{
						hyperlink.setLinkTarget(nodeAttrs.getNamedItem(ATTRIBUTE_target).getNodeValue());
					}

					int startIndex = styledText.length();

					parseStyle(styledText, node);

					styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
					
					hyperlink = null;
				}
				else
				{
					throw new SAXException("Hyperlink <a> tags cannot be nested.");
				}
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_param.equalsIgnoreCase(node.getNodeName()))
			{
				if (hyperlink == null)
				{
					throw new SAXException("Hyperlink <param> tags must appear inside an <a> tag only.");
				}
				else
				{
					NamedNodeMap nodeAttrs = node.getAttributes();

					JRPrintHyperlinkParameter parameter = new JRPrintHyperlinkParameter();
					
					if (nodeAttrs.getNamedItem(ATTRIBUTE_name) != null)
					{
						parameter.setName(nodeAttrs.getNamedItem(ATTRIBUTE_name).getNodeValue());
					}

					if (nodeAttrs.getNamedItem(ATTRIBUTE_valueClass) != null)
					{
						parameter.setValueClass(nodeAttrs.getNamedItem(ATTRIBUTE_valueClass).getNodeValue());
					}

					String strValue = node.getTextContent();
					if (strValue != null)
					{
						Object value = JRValueStringUtils.deserialize(parameter.getValueClass(), strValue);
						parameter.setValue(value);
					}
						
					hyperlink.addHyperlinkParameter(parameter);
				}
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_reference.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();
				styleAttrs.put(JRTextAttribute.REFERENCE, Boolean.TRUE);

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE && NODE_note.equalsIgnoreCase(node.getNodeName()))
			{
				Map<Attribute,Object> styleAttrs = new HashMap<>();
				styleAttrs.put(JRTextAttribute.NOTE, Boolean.TRUE);

				int startIndex = styledText.length();

				parseStyle(styledText, node);

				styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
			}
			else if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				String nodeName = "<" + node.getNodeName() + ">";
				throw new SAXException("Tag " + nodeName + " is not a valid styled text tag.");
			}
		}
	}

	/**
	 *
	 */
	private void resizeRuns(List<Run> runs, int startIndex, int count)
	{
		for (JRStyledText.Run run : runs)
		{
			if (run.startIndex <= startIndex && run.endIndex > startIndex - count)
			{
				run.endIndex += count;
			}
		}
	}


	/**
	 *
	 */
	private StringBuilder writeStyleAttributes(Map<Attribute,Object> parentAttrs,  Map<Attribute,Object> attrs)
	{
		StringBuilder sb = new StringBuilder();
		
		Object value = attrs.get(TextAttribute.FAMILY);
		Object oldValue = parentAttrs.get(TextAttribute.FAMILY);
		
		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_fontName);
			sb.append(EQUAL_QUOTE);
			sb.append(value);
			sb.append(QUOTE);
		}

		value = attrs.get(TextAttribute.WEIGHT);
		oldValue = parentAttrs.get(TextAttribute.WEIGHT);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_isBold);
			sb.append(EQUAL_QUOTE);
			sb.append(value.equals(TextAttribute.WEIGHT_BOLD));
			sb.append(QUOTE);
		}

		value = attrs.get(TextAttribute.POSTURE);
		oldValue = parentAttrs.get(TextAttribute.POSTURE);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_isItalic);
			sb.append(EQUAL_QUOTE);
			sb.append(value.equals(TextAttribute.POSTURE_OBLIQUE));
			sb.append(QUOTE);
		}

		value = attrs.get(TextAttribute.UNDERLINE);
		oldValue = parentAttrs.get(TextAttribute.UNDERLINE);

		if (
			(value == null && oldValue != null)
			|| (value != null && !value.equals(oldValue))
			)
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_isUnderline);
			sb.append(EQUAL_QUOTE);
			sb.append(value != null);
			sb.append(QUOTE);
		}

		value = attrs.get(TextAttribute.STRIKETHROUGH);
		oldValue = parentAttrs.get(TextAttribute.STRIKETHROUGH);

		if (
			(value == null && oldValue != null)
			|| (value != null && !value.equals(oldValue))
			)
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_isStrikeThrough);
			sb.append(EQUAL_QUOTE);
			sb.append(value != null);
			sb.append(QUOTE);
		}

		value = attrs.get(TextAttribute.SIZE);
		oldValue = parentAttrs.get(TextAttribute.SIZE);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_size);
			sb.append(EQUAL_QUOTE);
			sb.append(value);
			sb.append(QUOTE);
		}

		value = attrs.get(JRTextAttribute.PDF_FONT_NAME);
		oldValue = parentAttrs.get(JRTextAttribute.PDF_FONT_NAME);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_pdfFontName);
			sb.append(EQUAL_QUOTE);
			sb.append(value);
			sb.append(QUOTE);
		}

		value = attrs.get(JRTextAttribute.PDF_ENCODING);
		oldValue = parentAttrs.get(JRTextAttribute.PDF_ENCODING);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_pdfEncoding);
			sb.append(EQUAL_QUOTE);
			sb.append(value);
			sb.append(QUOTE);
		}

		value = attrs.get(JRTextAttribute.IS_PDF_EMBEDDED);
		oldValue = parentAttrs.get(JRTextAttribute.IS_PDF_EMBEDDED);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_isPdfEmbedded);
			sb.append(EQUAL_QUOTE);
			sb.append(value);
			sb.append(QUOTE);
		}

		value = attrs.get(TextAttribute.FOREGROUND);
		oldValue = parentAttrs.get(TextAttribute.FOREGROUND);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_forecolor);
			sb.append(EQUAL_QUOTE);
			sb.append(JRColorUtil.getCssColor((Color)value));
			sb.append(QUOTE);
		}

		value = attrs.get(TextAttribute.BACKGROUND);
		oldValue = parentAttrs.get(TextAttribute.BACKGROUND);

		if (value != null && !value.equals(oldValue))
		{
			sb.append(SPACE);
			sb.append(ATTRIBUTE_backcolor);
			sb.append(EQUAL_QUOTE);
			sb.append(JRColorUtil.getCssColor((Color)value));
			sb.append(QUOTE);
		}
		
		return sb;
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


	protected class XmlStyledTextListWriter implements StyledTextListWriter
	{
		private StringBuilder sb;
		
		public XmlStyledTextListWriter(StringBuilder sb)
		{
			this.sb = sb;
		}
	
		@Override
		public void startUl() 
		{
			sb.append(LESS);
			sb.append(NODE_ul);
			sb.append(GREATER);
		}
	
		@Override
		public void endUl() 
		{
			sb.append(LESS_SLASH);
			sb.append(NODE_ul);
			sb.append(GREATER);
		}
	
		@Override
		public void startOl(String type, int cutStart) 
		{
			sb.append(LESS);
			sb.append(NODE_ol);
			if (type != null)
			{
				sb.append(" " + ATTRIBUTE_type + "=\"" + type + "\"");
			}
			if (cutStart > 1)
			{
				sb.append(" " + ATTRIBUTE_start + "=\"" + cutStart + "\"");
			}
			sb.append(GREATER);
		}
	
		@Override
		public void endOl() 
		{
			sb.append(LESS_SLASH);
			sb.append(NODE_ol);
			sb.append(GREATER);
		}
	
		@Override
		public void startLi(boolean noBullet) 
		{
			sb.append(LESS);
			sb.append(NODE_li);
			if (noBullet)
			{
				sb.append(" " + ATTRIBUTE_noBullet + "=\"true\"");
			}
			sb.append(GREATER);
		}
	
		@Override
		public void endLi() 
		{
			sb.append(LESS_SLASH);
			sb.append(NODE_li);
			sb.append(GREATER);
		}
	}
}

