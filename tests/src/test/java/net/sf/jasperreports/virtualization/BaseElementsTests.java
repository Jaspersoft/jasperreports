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
package net.sf.jasperreports.virtualization;

import java.io.StringWriter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.fill.JRTemplatePrintElement;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseElementsTests extends BaseSerializationTests
{

	protected <T extends JRTemplatePrintElement> T compareSerialized(T element)
	{
		T readElement = passThroughElementSerialization(element);
		compareXml(element, readElement);
		return readElement;
	}

	protected <T extends JRTemplatePrintElement> T passThroughElementSerialization(T element)
	{
		JRVirtualizationContext virtualizationContext = createVirtualizationContext();
		T read = passThroughElementSerialization(virtualizationContext, element);
		assert read.getTemplate() == element.getTemplate();
		return read;
	}

	protected <T extends JRTemplatePrintElement> T passThroughElementSerialization(
			JRVirtualizationContext virtualizationContext, T element)
	{
		virtualizationContext.cacheTemplate(element);
		
		T readElement = passThroughSerialization(virtualizationContext, element);
		return readElement;
	}

	protected <T extends JRPrintElement> void compareXml(T element0, T element1)
	{
		String element0Xml = elementToXml(element0);
		String element1Xml = elementToXml(element1);
		assert element0Xml.equals(element1Xml);
	}
	
	protected String elementToXml(JRPrintElement element)
	{
		JRBasePrintPage page = new JRBasePrintPage();
		page.addElement(element);
		JasperPrint jasperPrint = new JasperPrint();
		jasperPrint.addPage(page);
		jasperPrint.setName("test");
		
		StringWriter writer = new StringWriter();
		JRXmlExporter exporter = new JRXmlExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		SimpleXmlExporterOutput output = new SimpleXmlExporterOutput(writer);
		output.setEmbeddingImages(true);
		exporter.setExporterOutput(output);
		try
		{
			exporter.exportReport();
		}
		catch (JRException e)
		{
			throw new RuntimeException(e);
		}
		return writer.toString();
	}

}
