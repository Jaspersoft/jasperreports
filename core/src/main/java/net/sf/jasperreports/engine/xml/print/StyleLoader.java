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

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.type.FillEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StyleLoader
{
	
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_REPORT_STYLE = "xml.base.factory.unknown.report.style";
	
	private static final StyleLoader INSTANCE = new StyleLoader();
	
	public static StyleLoader instance()
	{
		return INSTANCE;
	}
	
	public void loadStyle(XmlLoader xmlLoader, JasperPrint jasperPrint)
	{
		JRDesignStyle style = new JRDesignStyle(jasperPrint.getDefaultStyleProvider());

		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_name, style::setName);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isDefault, style::setDefault);

		String parentStyle = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_style);
		if (parentStyle != null)
		{
			Map<String, JRStyle> stylesMap = jasperPrint.getStylesMap();
			JRStyle parent = stylesMap.get(parentStyle);
			if (parent == null)
			{
				throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_UNKNOWN_REPORT_STYLE,
						new Object[]{parentStyle});
			}
			style.setParentStyle(parent);
		}

		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_mode, ModeEnum::getByName, style::setMode);
		xmlLoader.setColorAttribute(JRXmlConstants.ATTRIBUTE_forecolor, style::setForecolor);
		xmlLoader.setColorAttribute(JRXmlConstants.ATTRIBUTE_backcolor, style::setBackcolor);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_fill, FillEnum::getByName, style::setFill);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_radius, style::setRadius);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_scaleImage, ScaleImageEnum::getByName, style::setScaleImage);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_hTextAlign, HorizontalTextAlignEnum::getByName, style::setHorizontalTextAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_vTextAlign, VerticalTextAlignEnum::getByName, style::setVerticalTextAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_hImageAlign, HorizontalImageAlignEnum::getByName, style::setHorizontalImageAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_vImageAlign, VerticalImageAlignEnum::getByName, style::setVerticalImageAlign);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_rotation, RotationEnum::getByName, style::setRotation);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_markup, style::setMarkup);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_pattern, style::setPattern);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isBlankWhenNull, style::setBlankWhenNull);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_fontName, style::setFontName);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isBold, style::setBold);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isItalic, style::setItalic);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isUnderline, style::setUnderline);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isStrikeThrough, style::setStrikeThrough);
		xmlLoader.setFloatAttribute(JRXmlConstants.ATTRIBUTE_fontSize, style::setFontSize);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_pdfFontName, style::setPdfFontName);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_pdfEncoding, style::setPdfEncoding);
		xmlLoader.setBooleanAttribute(JRXmlConstants.ATTRIBUTE_isPdfEmbedded, style::setPdfEmbedded);

		xmlLoader.loadElements(element ->
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_pen:
				PenLoader.instance().loadPen(xmlLoader, style.getLinePen());
				break;
			case JRXmlConstants.ELEMENT_box:
				BoxLoader.instance().loadBox(xmlLoader, style.getLineBox());
				break;
			case JRXmlConstants.ELEMENT_paragraph:
				ParagraphsLoader.instance().loadParagraph(xmlLoader, style.getParagraph());
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		try
		{
			jasperPrint.addStyle(style);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
}
