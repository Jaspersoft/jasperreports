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
package net.sf.jasperreports.engine.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRXmlUtils;

/**
 * Produces a <code>org.w3c.dom.Document</code> based on a <code>java.io.File</code>, <code>java.io.InputStream</code> or a <code>java.lang.String</code> uri
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JRXmlDocumentProducer {
	
	private static final String EXCEPTION_MESSAGE_KEY_DOCUMENT_BUILDER_CREATION_FAILURE = "util.xml.document.builder.creation.failure";

	private File file;
	
	private InputStream inputStream;
	
	private String uri;
	
	private DocumentBuilderFactory documentBuilderFactory;
	
	
	public JRXmlDocumentProducer() {
	}

	public JRXmlDocumentProducer(File file) {
		this.file = file;
	}

	public JRXmlDocumentProducer(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public JRXmlDocumentProducer(String uri) {
		this.uri = uri;
	}
	
	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return documentBuilderFactory;
	}
	
	
	public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
		this.documentBuilderFactory = documentBuilderFactory;
	}
	
	
	public void setFile(File file) {
		this.file = file;
	}
	
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
	public Document getDocument() throws JRException  {
		try {
			if (file != null) {
				return getDocumentBuilder().parse(file);
			} else if (inputStream != null ) {
				return getDocumentBuilder().parse(inputStream);
			} else if (uri != null) {
				return getDocumentBuilder().parse(uri);
			}
		} catch (SAXException | IOException e) {
			throw 
				new JRException(
					JRXmlUtils.EXCEPTION_MESSAGE_KEY_DOCUMENT_PARSING_FAILURE, 
					null,
					e);
		}
		return null;
	}
	
	
	public Document getDocument(Node sourceNode) throws JRException {
		Document doc = getDocumentBuilder().newDocument();

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
	
	
	protected DocumentBuilder getDocumentBuilder() throws JRException {
		try{
			if (documentBuilderFactory != null) {
				return documentBuilderFactory.newDocumentBuilder();
			} else {
				return JRXmlUtils.createDocumentBuilder();
			}
		} catch (ParserConfigurationException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DOCUMENT_BUILDER_CREATION_FAILURE, 
					null,
					e);
		}
	}
	
}
