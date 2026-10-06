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

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BoxLoader
{
	
	private static final BoxLoader INSTANCE = new BoxLoader();
	
	public static BoxLoader instance()
	{
		return INSTANCE;
	}

	public void loadBox(XmlLoader xmlLoader, JRLineBox box)
	{
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_padding, box::setPadding);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_topPadding, box::setTopPadding);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_leftPadding, box::setLeftPadding);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_bottomPadding, box::setBottomPadding);
		xmlLoader.setIntAttribute(JRXmlConstants.ATTRIBUTE_rightPadding, box::setRightPadding);
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_pen:
				PenLoader.instance().loadPen(xmlLoader, box.getPen());
				break;
			case JRXmlConstants.ELEMENT_topPen:
				PenLoader.instance().loadPen(xmlLoader, box.getTopPen());
				break;
			case JRXmlConstants.ELEMENT_leftPen:
				PenLoader.instance().loadPen(xmlLoader, box.getLeftPen());
				break;
			case JRXmlConstants.ELEMENT_bottomPen:
				PenLoader.instance().loadPen(xmlLoader, box.getBottomPen());
				break;
			case JRXmlConstants.ELEMENT_rightPen:
				PenLoader.instance().loadPen(xmlLoader, box.getRightPen());
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
	}
	
}
