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
package net.sf.jasperreports.engine.xml.print;

import java.util.StringTokenizer;
import java.util.function.Consumer;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TextLoader
{
	
	private static final TextLoader INSTANCE = new TextLoader();
	
	public static TextLoader instance()
	{
		return INSTANCE;
	}

	public void loadText(XmlLoader xmlLoader, JasperPrint jasperPrint, Consumer<? super JRPrintText> consumer)
	{
		JRBasePrintText text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_textAlignment, HorizontalTextAlignEnum::getByName, text::setHorizontalTextAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_verticalAlignment, VerticalTextAlignEnum::getByName, text::setVerticalTextAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_rotation, RotationEnum::getByName, text::setRotation);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_runDirection, RunDirectionEnum::getByName, text::setRunDirection);
		xmlLoader.setFloatAttribute(JRXmlConstants.ATTRIBUTE_textHeight, text::setTextHeight);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_markup, text::setMarkup);
		xmlLoader.setFloatAttribute(JRXmlConstants.ATTRIBUTE_lineSpacingFactor, text::setLineSpacingFactor);
		xmlLoader.setFloatAttribute(JRXmlConstants.ATTRIBUTE_leadingOffset, text::setLeadingOffset);
		xmlLoader.setFloatAttribute(JRXmlConstants.ATTRIBUTE_averageCharWidth, text::setAverageCharWidth);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkType, text::setLinkType);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTarget, text::setLinkTarget);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_anchorName, text::setAnchorName);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkReference, text::setHyperlinkReference);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkAnchor, text::setHyperlinkAnchor);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkPage, text::setHyperlinkPage);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_hyperlinkTooltip, text::setHyperlinkTooltip);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_bookmarkLevel, text::setBookmarkLevel);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_valueClass, text::setValueClassName);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_pattern, text::setPattern);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_formatFactoryClass, text::setFormatFactoryClass);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_locale, text::setLocaleCode);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_timezone, text::setTimeZoneId);
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_reportElement:
				ReportElementLoader.instance().loadReportElement(xmlLoader, jasperPrint, text);
				break;
			case JRXmlConstants.ELEMENT_box:
				BoxLoader.instance().loadBox(xmlLoader, text.getLineBox());
				break;
			case JRXmlConstants.ELEMENT_font:
				loadFont(xmlLoader, text);
				break;
			case JRXmlConstants.ELEMENT_paragraph:
				ParagraphsLoader.instance().loadParagraph(xmlLoader, text.getParagraph());
				break;
			case JRXmlConstants.ELEMENT_textContent:
				xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_truncateIndex, text::setTextTruncateIndex);
				String textContent = xmlLoader.loadText(false);
				text.setText(textContent);
				break;
			case JRXmlConstants.ELEMENT_textTruncateSuffix:
				String textTruncateSuffix = xmlLoader.loadText(false);
				text.setTextTruncateSuffix(textTruncateSuffix);
				break;
			case JRXmlConstants.ELEMENT_lineBreakOffsets:
				loadLineBreakOffsets(xmlLoader, text);
				break;
			case JRXmlConstants.ELEMENT_hyperlinkParameter:
				HyperlinkLoader.instance().loadHyperlinkParameter(xmlLoader, text::addHyperlinkParameter);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		consumer.accept(text);
	}

	protected void loadLineBreakOffsets(XmlLoader xmlLoader, JRBasePrintText text)
	{
		String lineBreakOffsets = xmlLoader.loadText(true);
		if (lineBreakOffsets != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(lineBreakOffsets, 
					JRXmlConstants.LINE_BREAK_OFFSET_SEPARATOR);
			int tokenCount = tokenizer.countTokens();
			short[] offsets;
			if (tokenCount == 0)
			{
				//use the zero length array singleton
				offsets = JRPrintText.ZERO_LINE_BREAK_OFFSETS;
			}
			else
			{
				offsets = new short[tokenCount];
				for (int i = 0; i < offsets.length; i++)
				{
					String token = tokenizer.nextToken();
					offsets[i] = Short.parseShort(token);
				}
			}
			
			text.setLineBreakOffsets(offsets);
		}
	}
	
	protected void loadFont(XmlLoader xmlLoader, JRBasePrintText text)
	{
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_fontName, text::setFontName);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isBold, text::setBold);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isItalic, text::setItalic);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isUnderline, text::setUnderline);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isStrikeThrough, text::setStrikeThrough);
		xmlLoader.setFloatAttribute(JRXmlConstants.ATTRIBUTE_size, text::setFontSize);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_pdfFontName, text::setPdfFontName);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_pdfEncoding, text::setPdfEncoding);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isPdfEmbedded, text::setPdfEmbedded);
		xmlLoader.endElement();
	}
	
}
