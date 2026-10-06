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

import java.util.function.Consumer;

import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintFrame;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FrameLoader
{
	
	private static final FrameLoader INSTANCE = new FrameLoader();
	
	public static FrameLoader instance()
	{
		return INSTANCE;
	}

	public void loadFrame(XmlLoader xmlLoader, JasperPrint jasperPrint, Consumer<? super JRPrintFrame> consumer)
	{
		JRBasePrintFrame frame = new JRBasePrintFrame(jasperPrint.getDefaultStyleProvider());
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_reportElement:
				ReportElementLoader.instance().loadReportElement(xmlLoader, jasperPrint, frame);
				break;
			case JRXmlConstants.ELEMENT_box:
				BoxLoader.instance().loadBox(xmlLoader, frame.getLineBox());
				break;
			case JRXmlConstants.ELEMENT_line:
				LineLoader.instance().loadLine(xmlLoader, jasperPrint, frame::addElement);
				break;
			case JRXmlConstants.ELEMENT_rectangle:
				RectangleLoader.instance().loadRectangle(xmlLoader, jasperPrint, frame::addElement);
				break;
			case JRXmlConstants.ELEMENT_ellipse:
				EllipseLoader.instance().loadEllipse(xmlLoader, jasperPrint, frame::addElement);
				break;
			case JRXmlConstants.ELEMENT_image:
				ImageLoader.instance().loadImage(xmlLoader, jasperPrint, frame::addElement);
				break;
			case JRXmlConstants.ELEMENT_text:
				TextLoader.instance().loadText(xmlLoader, jasperPrint, frame::addElement);
				break;
			case JRXmlConstants.ELEMENT_frame:
				FrameLoader.instance().loadFrame(xmlLoader, jasperPrint, frame::addElement);
				break;
			case JRXmlConstants.ELEMENT_genericElement:
				GenericElementLoader.instance().loadGenericElement(xmlLoader, jasperPrint, frame::addElement);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		consumer.accept(frame);
	}
	
}
