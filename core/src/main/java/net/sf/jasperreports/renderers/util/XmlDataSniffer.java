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
package net.sf.jasperreports.renderers.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Utility class that checks if provided byte data is a valid XML document.
 * It does this by attempting to parse the data using a SAX parser.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XmlDataSniffer
{
	
	private static final Log log = LogFactory.getLog(XmlDataSniffer.class);
	
	private static final String SAX_EXCEPTION_MESSAGE_VALID_XML = "something unique";
	
	private static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
	private static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
	private static final String FEATURE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	private static class ValidXmlSAXException extends SAXException
	{
		private static final long serialVersionUID = 1L;

		ValidXmlSAXException()
		{
			super(SAX_EXCEPTION_MESSAGE_VALID_XML);
		}

		@Override
		public synchronized Throwable fillInStackTrace()
		{
			// stacktrace is not needed, easing the impact using exceptions
			return this;
		}
	}
	
	/**
	 * 
	 */
	public static boolean isXmlData(byte[] data)
	{
		XmlSniffResult sniffResult = sniffXml(data);
		return sniffResult != null;
	}
	
	public static XmlSniffResult sniffXml(byte[] data)
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		factory.setXIncludeAware(false);
		setParserFeature(factory, FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
		setParserFeature(factory, FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
		setParserFeature(factory, FEATURE_LOAD_EXTERNAL_DTD, false);
		
		SaxHandler handler = new SaxHandler();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);

		try 
		{
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(bais, handler);
			return new XmlSniffResult(handler.rootElementName);
		}
		catch (ValidXmlSAXException e)
		{
			return new XmlSniffResult(handler.rootElementName);
		}
		catch (SAXException | ParserConfigurationException | IOException e)
		{
			return null;
		}
	}

	protected static void setParserFeature(SAXParserFactory factory, String feature, boolean value)
	{
		try
		{
			factory.setFeature(feature, value);
		}
		catch (SAXNotRecognizedException | SAXNotSupportedException | ParserConfigurationException e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Failed to set parser feature " + feature + ", error " + e);
			}
		}
	}
	
	private static class SaxHandler extends DefaultHandler
	{
		private String rootElementName;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			this.rootElementName = (qName != null && !qName.isEmpty()) ? qName
					: ((localName != null && !localName.isEmpty()) ? localName : null);
			throw new ValidXmlSAXException();
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException
		{
			//stop any attempt to load entities
			throw new ValidXmlSAXException();
		}
	}
	
	public static class XmlSniffResult
	{
		private final String rootElementName;
		
		public XmlSniffResult(String rootElementName)
		{
			this.rootElementName = rootElementName;
		}

		public String getRootElementName()
		{
			return rootElementName;
		}
	}
}
