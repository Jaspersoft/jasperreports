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

import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.util.JRValueStringUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class HyperlinkLoader
{
	
	private static final HyperlinkLoader INSTANCE = new HyperlinkLoader();
	
	public static HyperlinkLoader instance()
	{
		return INSTANCE;
	}

	public void loadHyperlinkParameter(XmlLoader xmlLoader, Consumer<JRPrintHyperlinkParameter> consumer)
	{
		JRPrintHyperlinkParameter parameter = new JRPrintHyperlinkParameter();
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_name, parameter::setName);
		xmlLoader.setAttribute(JRXmlConstants.ATTRIBUTE_class, parameter::setValueClass);
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_hyperlinkParameterValue:
				String data = xmlLoader.loadText(true);
				Object value = JRValueStringUtils.deserialize(parameter.getValueClass(), data);
				parameter.setValue(value);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		consumer.accept(parameter);
	}
	
}
