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
package net.sf.jasperreports.jaxen.data;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.AbstractXmlDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.data.NoRecordAtIndexException;
import net.sf.jasperreports.engine.util.xml.JRXmlDocumentProducer;
import net.sf.jasperreports.jaxen.util.xml.JaxenNsAwareXPathExecuter;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JaxenXmlDataSource extends AbstractXmlDataSource<JaxenXmlDataSource> 
{

	// the xml document
	private Document document;

	// the XPath select expression that gives the nodes to iterate
	private String selectExpression;

	// the node list
	private NodeList nodeList;

	// the node list length
	private int nodeListLength;
	
	// the current node
	private Node currentNode;

	// current node index
	private int currentNodeIndex = - 1;

	private final JaxenNsAwareXPathExecuter xPathExecuter = new JaxenNsAwareXPathExecuter();
	
	private final JRXmlDocumentProducer documentProducer;
	
	private boolean mustBeMovedFirst;
	
	
	// -----------------------------------------------------------------
	// Constructors

	/**
	 * Creates the data source by parsing the xml document from the given file.
	 * The data source will contain exactly one record consisting of the document node itself.
	 * 
	 * @param document the document
	 * @throws JRException if the data source cannot be created
	 */
	public JaxenXmlDataSource(Document document) throws JRException {
		this(document, ".");
	}

	/**
	 * Creates the data source by parsing the xml document from the given file.
	 * An additional XPath expression specifies the select criteria that produces the 
	 * nodes (records) for the data source.
	 * 
	 * @param document the document
	 * @param selectExpression the XPath select expression
	 * @throws JRException if the data source cannot be created
	 */
	public JaxenXmlDataSource(Document document, String selectExpression)
			throws JRException {
		this.document = document;
		this.selectExpression = selectExpression;
		this.documentProducer = new JRXmlDocumentProducer();
		
		mustBeMovedFirst = true;
	}


	/**
	 * Creates the data source by parsing the xml document from the given input stream.
	 *  
	 * @param in the input stream
	 * @see JRXmlDataSource#JRXmlDataSource(Document) 
	 */
	public JaxenXmlDataSource(InputStream in) throws JRException {
		this(in, ".");
	}

	/**
	 * Creates the data source by parsing the xml document from the given input stream.
	 * 
	 * @see JRXmlDataSource#JRXmlDataSource(InputStream) 
	 * @see JRXmlDataSource#JRXmlDataSource(Document, String) 
	 */
	public JaxenXmlDataSource(InputStream in, String selectExpression)
			throws JRException {
		this.selectExpression = selectExpression;
		this.documentProducer = new JRXmlDocumentProducer(in);
		
		mustBeMovedFirst = true;
	}

	/**
	 * Creates the data source by parsing the xml document from the given system identifier (URI).
	 * <p>If the system identifier is a URL, it must be full resolved.</p>
	 * 
	 * @param uri the system identifier
	 * @see JRXmlDataSource#JRXmlDataSource(Document) 
	 */
	public JaxenXmlDataSource(String uri) throws JRException {
		this(uri, ".");
	}

	/**
	 * Creates the data source by parsing the xml document from the given system identifier (URI).
	 * 
	 * @see JRXmlDataSource#JRXmlDataSource(String) 
	 * @see JRXmlDataSource#JRXmlDataSource(Document, String) 
	 */
	public JaxenXmlDataSource(String uri, String selectExpression)
			throws JRException {
		this.selectExpression = selectExpression;
		this.documentProducer = new JRXmlDocumentProducer(uri);
		
		mustBeMovedFirst = true;
	}

	/**
	 * Creates the data source by parsing the xml document from the given file.
	 * 
	 * @param file the file
	 * @see JRXmlDataSource#JRXmlDataSource(Document) 
	 */
	public JaxenXmlDataSource(File file) throws JRException {
		this(file, ".");
	}

	/**
	 * Creates the data source by parsing the xml document from the given file.
	 * 
	 * @see JRXmlDataSource#JRXmlDataSource(File) 
	 * @see JRXmlDataSource#JRXmlDataSource(Document, String) 
	 */
	public JaxenXmlDataSource(File file, String selectExpression)
			throws JRException {
		this.selectExpression = selectExpression;
		this.documentProducer = new JRXmlDocumentProducer(file);
		
		mustBeMovedFirst = true;
	}

	@Override
	public Document getDocument()
	{
		return document;
	}
	
	// -----------------------------------------------------------------
	// Implementation
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jasperreports.engine.JRRewindableDataSource#moveFirst()
	 */
	@Override
	public void moveFirst() throws JRException {
		if (document == null)
		{
			document = documentProducer.getDocument();
			if (document == null)
			{	
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_NULL_DOCUMENT,
						(Object[])null);
			}
		}
		if (selectExpression == null)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_NULL_SELECT_EXPRESSION,
					(Object[])null);
		}

		currentNode = null;
		currentNodeIndex = -1;
		nodeListLength = 0;
		nodeList = xPathExecuter.selectNodeList(document,
				selectExpression);
		nodeListLength = nodeList.getLength();
	}

	protected void checkMoveFirst() throws JRException
	{
		if (mustBeMovedFirst) 
		{
			moveFirst();
			mustBeMovedFirst = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	@Override
	public boolean next() throws JRException 
	{
		checkMoveFirst();
		if (currentNodeIndex == nodeListLength - 1)
		{
			return false;
		}
		currentNode = nodeList.item(++ currentNodeIndex);
		return true;
	}

	@Override
	public int recordCount() throws JRException
	{
		checkMoveFirst();
		return nodeListLength;
	}

	@Override
	public int currentIndex()
	{
		return currentNodeIndex;
	}

	@Override
	public void moveToRecord(int index) throws NoRecordAtIndexException
	{
		if (index >= 0 && index < nodeListLength)
		{
			currentNodeIndex = index;
			currentNode = nodeList.item(index);
		}
		else
		{
			throw new NoRecordAtIndexException(index);
		}
	}

	
	@Override
	public Document subDocument() throws JRException
	{
		if (currentNode == null)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_NODE_NOT_AVAILABLE,
					(Object[])null);
		}
		
		// create a new document from the current node
		return documentProducer.getDocument(currentNode);
	}
	
	
	public void setXmlNamespaceMap(Map<String, String> xmlNamespaceMap) throws JRException
	{
		this.xPathExecuter.setXmlNamespaceMap(xmlNamespaceMap);
	}
	
	
	public void setDetectXmlNamespaces(boolean detectXmlNamespaces)
	{
		this.xPathExecuter.setDetectXmlNamespaces(detectXmlNamespaces);
	}
	
	
	public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory)
	{
		this.documentProducer.setDocumentBuilderFactory(documentBuilderFactory);
	}
	
	
	@Override
	public Node getCurrentNode() {
		return currentNode;
	}

	@Override
	public Object getSelectObject(Node currentNode, String expression) throws JRException {
		return xPathExecuter.selectObject(currentNode, expression);
	}

	@Override
	public JaxenXmlDataSource subDataSource(String selectExpr)
			throws JRException {
		Document doc = subDocument();
		JaxenXmlDataSource subDataSource = new JaxenXmlDataSource(doc, selectExpr);
		subDataSource.setTextAttributes(this);
		
		subDataSource.setXmlNamespaceMap(xPathExecuter.getXmlNamespaceMap());
		subDataSource.setDetectXmlNamespaces(xPathExecuter.getDetectXmlNamespaces());
		subDataSource.setDocumentBuilderFactory(documentProducer.getDocumentBuilderFactory());
		
		return subDataSource;
	}

	@Override
	public JaxenXmlDataSource dataSource(String selectExpr)
			throws JRException {
		JaxenXmlDataSource subDataSource = new JaxenXmlDataSource(document, selectExpr);
		subDataSource.setTextAttributes(this);
		
		subDataSource.setXmlNamespaceMap(xPathExecuter.getXmlNamespaceMap());
		subDataSource.setDetectXmlNamespaces(xPathExecuter.getDetectXmlNamespaces());
		subDataSource.setDocumentBuilderFactory(documentProducer.getDocumentBuilderFactory());
		
		return subDataSource;
	}
	
}
