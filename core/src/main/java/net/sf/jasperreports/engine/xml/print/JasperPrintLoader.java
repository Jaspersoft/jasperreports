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
package net.sf.jasperreports.engine.xml.print;

import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintBookmark;
import net.sf.jasperreports.engine.SimplePrintPageFormat;
import net.sf.jasperreports.engine.SimplePrintPart;
import net.sf.jasperreports.engine.base.BasePrintBookmark;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JasperPrintLoader
{
	
	private static final JasperPrintLoader INSTANCE = new JasperPrintLoader();
	
	public static JasperPrintLoader instance()
	{
		return INSTANCE;
	}

	public JasperPrint load(XmlLoader xmlLoader)
	{
		JasperPrint jasperPrint = new JasperPrint();
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_name, jasperPrint::setName);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_dpi, jasperPrint::setDpi);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_pageWidth, jasperPrint::setPageWidth);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_pageHeight, jasperPrint::setPageHeight);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_topMargin, jasperPrint::setTopMargin);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_leftMargin, jasperPrint::setLeftMargin);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_bottomMargin, jasperPrint::setBottomMargin);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_rightMargin, jasperPrint::setRightMargin);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_orientation, OrientationEnum::getByName, jasperPrint::setOrientation);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_formatFactoryClass, jasperPrint::setFormatFactoryClass);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_locale, jasperPrint::setLocaleCode);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_timezone, jasperPrint::setTimeZoneId);
		
		xmlLoader.loadElements(element ->
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_property:
				PropertyLoader.instance().loadProperty(xmlLoader, jasperPrint);
				break;
			case JRXmlConstants.ELEMENT_origin:
				JROrigin origin = loadOrigin(xmlLoader);
				jasperPrint.addOrigin(origin);
				break;
			case JRXmlConstants.ELEMENT_style:
				StyleLoader.instance().loadStyle(xmlLoader, jasperPrint);
				break;
			case JRXmlConstants.ELEMENT_bookmark:
				PrintBookmark bookmark = loadBookmark(xmlLoader);
				jasperPrint.addBookmark(bookmark);
				break;
			case JRXmlConstants.ELEMENT_part:
				loadPart(xmlLoader, jasperPrint);
				break;
			case JRXmlConstants.ELEMENT_page:
				loadPage(xmlLoader, jasperPrint);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		return jasperPrint;
	}

	protected JROrigin loadOrigin(XmlLoader xmlLoader)
	{
		String report = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_report);
		String group = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_group);
		BandTypeEnum bandType = xmlLoader.getEnumAttribute(JRXmlConstants.ATTRIBUTE_band, BandTypeEnum::getByName);
		xmlLoader.endElement();
		return new JROrigin(report, group, bandType);
	}
	
	protected PrintBookmark loadBookmark(XmlLoader xmlLoader)
	{
		String label = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_label);
		Integer pageIndexAttr = xmlLoader.getIntAttribute(JRXmlConstants.ATTRIBUTE_pageIndex);
		int pageIndex = pageIndexAttr != null ? pageIndexAttr : 0;
		String elementAddress = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_elementAddress);
		BasePrintBookmark bookmark = new BasePrintBookmark(label, pageIndex, elementAddress);
		
		xmlLoader.loadElements(element ->
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_bookmark:
				PrintBookmark subBookmark = loadBookmark(xmlLoader);
				bookmark.addBookmark(subBookmark);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		return bookmark;
	}
	
	protected void loadPart(XmlLoader xmlLoader, JasperPrint jasperPrint)
	{
		SimplePrintPart part = new SimplePrintPart();
		Integer pageIndex = xmlLoader.getIntAttribute(JRXmlConstants.ATTRIBUTE_pageIndex);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_name, part::setName);
		
		SimplePrintPageFormat pageFormat = new SimplePrintPageFormat();
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_pageWidth, pageFormat::setPageWidth);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_pageHeight, pageFormat::setPageHeight);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_topMargin, pageFormat::setTopMargin);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_leftMargin, pageFormat::setLeftMargin);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_bottomMargin, pageFormat::setBottomMargin);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_rightMargin, pageFormat::setRightMargin);
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_orientation, OrientationEnum::getByName, pageFormat::setOrientation);
		part.setPageFormat(pageFormat);
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_property:
				PropertyLoader.instance().loadProperty(xmlLoader, part);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});

		jasperPrint.addPart(pageIndex, part);
	}

	protected void loadPage(XmlLoader xmlLoader, JasperPrint jasperPrint)
	{
		JRBasePrintPage page = new JRBasePrintPage();
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_line:
				LineLoader.instance().loadLine(xmlLoader, jasperPrint, page::addElement);
				break;
			case JRXmlConstants.ELEMENT_rectangle:
				RectangleLoader.instance().loadRectangle(xmlLoader, jasperPrint, page::addElement);
				break;
			case JRXmlConstants.ELEMENT_ellipse:
				EllipseLoader.instance().loadEllipse(xmlLoader, jasperPrint, page::addElement);
				break;
			case JRXmlConstants.ELEMENT_image:
				ImageLoader.instance().loadImage(xmlLoader, jasperPrint, page::addElement);
				break;
			case JRXmlConstants.ELEMENT_text:
				TextLoader.instance().loadText(xmlLoader, jasperPrint, page::addElement);
				break;
			case JRXmlConstants.ELEMENT_frame:
				FrameLoader.instance().loadFrame(xmlLoader, jasperPrint, page::addElement);
				break;
			case JRXmlConstants.ELEMENT_genericElement:
				GenericElementLoader.instance().loadGenericElement(xmlLoader, jasperPrint, page::addElement);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		jasperPrint.addPage(page);
	}
}
