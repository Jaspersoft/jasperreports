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
package net.sf.jasperreports.engine.xml;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRSimpleTemplate;
import net.sf.jasperreports.engine.JRTemplate;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.jackson.util.JacksonUtil;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JacksonReportLoader implements ReportLoader
{

	private static final Log log = LogFactory.getLog(JacksonReportLoader.class);

	private static final JacksonReportLoader INSTANCE = new JacksonReportLoader();
	
	public static JacksonReportLoader instance()
	{
		return INSTANCE;
	}
	
	@Override
	public Optional<JasperDesign> loadReport(JasperReportsContext context, byte[] data) throws JRException
	{
		boolean detectedReport = detectReportXML(data);
		if (detectedReport)
		{
			JasperDesign report = null;
 			ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
			JasperDesign.setThreadJasperReportsContext(context);
			try
			{
				report = JacksonUtil.getInstance(context).loadXml(dataStream, JasperDesign.class);
			}
			finally
			{
				JasperDesign.removeThreadJasperReportsContext();
				JasperDesign.removeThreadInstance();
			}
			return Optional.of(report);
		}
		return Optional.empty();
	}

	protected boolean detectReportXML(byte[] data)
	{
		return detectRootElement(data, JRXmlConstants.ELEMENT_jasperReport);
	}

	@Override
	public Optional<JRTemplate> loadTemplate(JasperReportsContext context, byte[] data)
	{
		boolean detectedReport = detectTemplateXML(data);
		if (detectedReport)
		{
			ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
			JRSimpleTemplate template = JacksonUtil.getInstance(context).loadXml(
					dataStream, JRSimpleTemplate.class);
			return Optional.of(template);
		}
		return Optional.empty();
	}

	private boolean detectTemplateXML(byte[] data)
	{
		return detectRootElement(data, JRXmlConstants.TEMPLATE_ELEMENT_ROOT);
	}

	protected boolean detectRootElement(byte[] data, String elementName)
	{
		ByteArrayInputStream dataStream = new ByteArrayInputStream(data);
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
		inputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
		inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		try
		{
			XMLStreamReader reader = inputFactory.createXMLStreamReader(dataStream);
			
			boolean foundRoot = false;
			boolean ended = false;
			read:
			while (!ended && reader.hasNext())
			{
				reader.next();
				switch (reader.getEventType())
				{
				case XMLEvent.START_ELEMENT:
					if (elementName.equals(reader.getLocalName())
							&& (reader.getNamespaceURI() == null || reader.getNamespaceURI().isEmpty()))
					{
						foundRoot = true;
					}
					break read;
				}
			}
			return foundRoot;
		}
		catch (XMLStreamException e)
		{
			log.debug("failed to load jrxml " + e);
			return false;
		}
	}

}
