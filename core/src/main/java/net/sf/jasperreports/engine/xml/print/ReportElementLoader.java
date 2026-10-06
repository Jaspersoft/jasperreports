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
import java.util.UUID;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintGraphicElement;
import net.sf.jasperreports.engine.type.FillEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReportElementLoader
{
	
	private static final ReportElementLoader INSTANCE = new ReportElementLoader();
	
	public static ReportElementLoader instance()
	{
		return INSTANCE;
	}

	public void loadReportElement(XmlLoader xmlLoader, JasperPrint jasperPrint, JRBasePrintElement printElement)
	{
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_key, printElement::setKey);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_mode, ModeEnum::getByName, printElement::setMode);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_uuid, UUID::fromString, printElement::setUUID);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_x, printElement::setX);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_y, printElement::setY);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_width, printElement::setWidth);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_height, printElement::setHeight);
		xmlLoader.setColorAttribute(JRXmlConstants.ATTRIBUTE_forecolor, printElement::setForecolor);
		xmlLoader.setColorAttribute(JRXmlConstants.ATTRIBUTE_backcolor, printElement::setBackcolor);
		
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_style, (styleName -> 
		{
			Map<String,JRStyle> stylesMap = jasperPrint.getStylesMap();
			JRStyle style = stylesMap.get(styleName);
			if (style == null)
			{
				throw new JRRuntimeException(StyleLoader.EXCEPTION_MESSAGE_KEY_UNKNOWN_REPORT_STYLE,
						new Object[]{styleName});
			}
			return style;
		}), printElement::setStyle);
		
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_origin, 
				originAttr -> jasperPrint.getOriginsList().get(Integer.parseInt(originAttr)),
				printElement::setOrigin);
		
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_srcId, printElement::setSourceElementId);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_printId, printElement::setPrintElementId);
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_property:
				PropertyLoader.instance().loadProperty(xmlLoader, printElement);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
	}
	
	public void loadGraphicElement(XmlLoader xmlLoader, JRBasePrintGraphicElement printElement)
	{
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_fill, FillEnum::getByName, printElement::setFill);
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_pen:
				PenLoader.instance().loadPen(xmlLoader, printElement.getLinePen());
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
	}
	
}
