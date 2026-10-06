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

import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBaseGenericPrintElement;
import net.sf.jasperreports.engine.util.JRValueStringUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GenericElementLoader
{
	
	private static final GenericElementLoader INSTANCE = new GenericElementLoader();
	
	public static GenericElementLoader instance()
	{
		return INSTANCE;
	}

	public void loadGenericElement(XmlLoader xmlLoader, JasperPrint jasperPrint, Consumer<? super JRGenericPrintElement> consumer)
	{
		JRBaseGenericPrintElement genericElement = new JRBaseGenericPrintElement(jasperPrint.getDefaultStyleProvider());
		
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_reportElement:
				ReportElementLoader.instance().loadReportElement(xmlLoader, jasperPrint, genericElement);
				break;
			case JRXmlConstants.ELEMENT_genericElementType:
				loadType(xmlLoader, genericElement);
				break;
			case JRXmlConstants.ELEMENT_genericElementParameter:
				loadParameter(xmlLoader, genericElement);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
		
		consumer.accept(genericElement);
	}

	protected void loadType(XmlLoader xmlLoader, JRBaseGenericPrintElement genericElement)
	{
		String namespace = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_namespace);
		String name = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_name);
		xmlLoader.endElement();
		JRGenericElementType type = new JRGenericElementType(namespace, name);
		genericElement.setGenericType(type);
	}

	private void loadParameter(XmlLoader xmlLoader, JRBaseGenericPrintElement genericElement)
	{
		String name = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_name);
		xmlLoader.loadElements(element -> 
		{
			switch (element)
			{
			case JRXmlConstants.ELEMENT_genericElementParameterValue:
				String valueClass = xmlLoader.getAttribute(JRXmlConstants.ATTRIBUTE_class);
				String valueText = xmlLoader.loadText(true);
				Object value = JRValueStringUtils.deserialize(valueClass, valueText);
				genericElement.setParameterValue(name, value);
				break;
			default:
				xmlLoader.unexpectedElement(element);
				break;
			}
		});
	}
	
}
