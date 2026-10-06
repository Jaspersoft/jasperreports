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

import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintLine;
import net.sf.jasperreports.engine.type.LineDirectionEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class LineLoader
{
	
	private static final LineLoader INSTANCE = new LineLoader();
	
	public static LineLoader instance()
	{
		return INSTANCE;
	}

	public void loadLine(XmlLoader xmlLoader, JasperPrint jasperPrint, Consumer<? super JRPrintLine> consumer)
	{
		JRBasePrintLine line = new JRBasePrintLine(jasperPrint.getDefaultStyleProvider());
		xmlLoader.setEnumAttribute(JRXmlConstants.ATTRIBUTE_direction, LineDirectionEnum::getByName, line::setDirection);
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_reportElement:
				ReportElementLoader.instance().loadReportElement(xmlLoader, jasperPrint, line);
				break;
			case JRXmlConstants.ELEMENT_graphicElement:
				ReportElementLoader.instance().loadGraphicElement(xmlLoader, line);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		consumer.accept(line);
	}
	
}
