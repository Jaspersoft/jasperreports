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
package net.sf.jasperreports.engine.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * XML parsing utilities.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JRXmlUtils
{
	private static final Log log = LogFactory.getLog(JRXmlUtils.class);
	public static final String EXCEPTION_MESSAGE_KEY_DOCUMENT_BUILDER_FACTORY_CREATION_FAILURE = "util.xml.document.builder.factory.creation.failure";
	public static final String EXCEPTION_MESSAGE_KEY_DOCUMENT_PARSING_FAILURE = "util.xml.document.parsing.failure";
	
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_1_2,
			valueType = Boolean.class
			)
	public static final String PROPERTY_ALLOW_DOCTYPE = JRPropertiesUtil.PROPERTY_PREFIX + "xml.allow.doctype";
	
	public static final String FEATURE_DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
	
	public static Document parse(InputSource is) throws JRException
	{
		return parse(is, false);
	}


	/**
	 * Parses an input source into a document.
	 * 
	 * @param is the input source
	 * @return the parsed document
	 * @throws JRException
	 */
	public static Document parse(InputSource is, boolean isNamespaceAware) throws JRException
	{
		try
		{
			return createDocumentBuilder(isNamespaceAware).parse(is);
		}
		catch (SAXException | IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DOCUMENT_PARSING_FAILURE,
					null,
					e);
		}
	}
	
	
	public static Document parse(String uri) throws JRException
	{
		return parse(uri, false);
	}


	/**
	 * Parses a document specified by an URI.
	 * 
	 * @param uri the URI
	 * @return the parsed document
	 * @throws JRException
	 */
	public static Document parse(String uri,  boolean isNamespaceAware) throws JRException
	{
		return parse(new InputSource(uri), isNamespaceAware);
	}

	
	public static Document parse(File file) throws JRException
	{
		return parse(file, false);
	}


	/**
	 * Parses a file into a document.
	 * 
	 * @param file the XML file
	 * @return the document
	 * @throws JRException
	 */
	public static Document parse(File file,  boolean isNamespaceAware) throws JRException
	{
		try
		{
			return createDocumentBuilder(isNamespaceAware).parse(file);
		}
		catch (SAXException | IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DOCUMENT_PARSING_FAILURE,
					null,
					e);
		}
	}


	public static Document parse(InputStream is) throws JRException
	{
		return parse(is, false);
	}


	/**
	 * Parses an input stream into a XML document.
	 * 
	 * @param is the input stream
	 * @return the document
	 * @throws JRException
	 */
	public static Document parse(InputStream is, boolean isNamespaceAware) throws JRException
	{
		return parse(new InputSource(is), isNamespaceAware);
	}


	public static Document parse(URL url) throws JRException
	{
		return parse(url, false);
	}


	/**
	 * Parses an URL stream as a XML document.
	 * 
	 * @param url the URL
	 * @return the document
	 * @throws JRException
	 */
	public static Document parse(URL url, boolean isNamespaceAware) throws JRException
	{
		InputStream is = null;
		try
		{
			is = url.openStream();
			return createDocumentBuilder(isNamespaceAware).parse(is);
		}
		catch (SAXException | IOException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DOCUMENT_PARSING_FAILURE,
					null,
					e);
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					log.warn("Error closing stream of URL " + url, e);
				}
			}
		}
	}


	public static DocumentBuilder createDocumentBuilder() throws JRException
	{
		return createDocumentBuilder(false);
	}


	/**
	 * Creates a XML document builder.
	 * 
	 * @return a XML document builder
	 * @throws JRException
	 */
	public static DocumentBuilder createDocumentBuilder(boolean isNamespaceAware) throws JRException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setIgnoringComments(true);
		dbf.setNamespaceAware(isNamespaceAware);
		try
		{
			if (!allowDoctype())
			{
				dbf.setFeature(FEATURE_DISALLOW_DOCTYPE, true);
			}
			
			return dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw 
			new JRException(
				EXCEPTION_MESSAGE_KEY_DOCUMENT_BUILDER_FACTORY_CREATION_FAILURE,
				null,
				e);
		}
	}

	protected static boolean allowDoctype()
	{
		//FIXME use a context?
		return JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).getBooleanProperty(
				PROPERTY_ALLOW_DOCTYPE, false);
	}
	
	public static Document createDocument(Node sourceNode) throws JRException
	{
		return createDocument(sourceNode, false);
	}


	/**
	 * Creates a document having a node as root.
	 * 
	 * @param sourceNode the node
	 * @return a document having the specified node as root
	 * @throws JRException
	 */
	public static Document createDocument(Node sourceNode, boolean isNamespaceAware) throws JRException
	{
		Document doc = JRXmlUtils.createDocumentBuilder(isNamespaceAware).newDocument();
		Node source;
		if (sourceNode.getNodeType() == Node.DOCUMENT_NODE) {
			source = ((Document) sourceNode).getDocumentElement();
		} else {
			source = sourceNode;
		}

		Node node = doc.importNode(source, true);
		doc.appendChild(node);
		
		return doc;
	}
	
	
	private JRXmlUtils()
	{
	}
}
