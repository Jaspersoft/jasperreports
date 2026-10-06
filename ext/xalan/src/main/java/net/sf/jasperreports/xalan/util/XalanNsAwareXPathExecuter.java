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
package net.sf.jasperreports.xalan.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.jasperreports.engine.JRException;

import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XPath executer implementation that uses <a href="http://xml.apache.org/xalan-j/" target="_blank">Apache Xalan</a>.
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class XalanNsAwareXPathExecuter extends XalanXPathExecuter {

	private CachedXPathAPI xpathAPI = new CachedXPathAPI();

	private XPathFactory xpathFact = XPathFactory.newInstance();

	private Map<String, String> xmlNamespaceMap;

	private Element namespaceElement;

	boolean detectXmlNamespaces;

	/**
	 * Default constructor.
	 */
	public XalanNsAwareXPathExecuter() {
	}

	public Map<String, String> getXmlNamespaceMap() {
		return xmlNamespaceMap;
	}

	public void setXmlNamespaceMap(Map<String, String> xmlNamespaceMap)
			throws JRException {
		this.xmlNamespaceMap = xmlNamespaceMap;
	}

	public boolean getDetectXmlNamespaces() {
		return detectXmlNamespaces;
	}

	public void setDetectXmlNamespaces(boolean detectXmlNamespaces) {
		this.detectXmlNamespaces = detectXmlNamespaces;
	}


	protected void createNamespaceElement(Node contextNode, String expression) throws JRException {
		if (xmlNamespaceMap == null && detectXmlNamespaces && containsPrefixes(expression) && namespaceElement == null) {
			xmlNamespaceMap = extractXmlNamespaces(contextNode);
		}
		
		if (xmlNamespaceMap != null && xmlNamespaceMap.size() > 0) {
			if (namespaceElement == null) {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = null;
				try {
					builder = factory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					throw new JRException(e);
				}	
				DOMImplementation impl = builder.getDOMImplementation();
				
				Set<String> nsSet = xmlNamespaceMap.keySet();
				Iterator<String> it = nsSet.iterator();
				String prefix = it.next(); 
				Document namespaceHolder = impl.createDocument(xmlNamespaceMap.get(prefix), prefix + ":namespaceMapping", null);
				
				namespaceElement = namespaceHolder.getDocumentElement();
				while (it.hasNext()) {
					prefix = it.next();
					namespaceElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, xmlNamespaceMap.get(prefix));
				}
			}
		}
	}

	@Override
	public NodeList selectNodeList(Node contextNode, String expression)
			throws JRException {
		try {
			createNamespaceElement(contextNode, expression);
			if (namespaceElement != null) {
				return xpathAPI.selectNodeList(contextNode, expression,	namespaceElement);
			} else {
				return xpathAPI.selectNodeList(contextNode, expression);
			}
		} catch (TransformerException e) {
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE,
					new Object[]{expression},
					e);
		}
	}

	@Override
	public Object selectObject(Node contextNode, String expression)
			throws JRException {
		try {
			createNamespaceElement(contextNode, expression);
			Object value;
			XObject object = null; 
			if (namespaceElement != null) {	
				object = xpathAPI.eval(contextNode, expression, namespaceElement);
			} else {
				object = xpathAPI.eval(contextNode, expression);
			}
			switch (object.getType()) {
			case XObject.CLASS_NODESET:
				value = object.nodeset().nextNode();
				break;
			case XObject.CLASS_BOOLEAN:
				value = object.bool();
				break;
			case XObject.CLASS_NUMBER:
				value = object.num();
				break;
			default:
				value = object.str();
				break;
			}
			return value;
		} catch (TransformerException e) {
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE,
					new Object[]{expression},
					e);
		}
	}


	protected boolean containsPrefixes(String expression) {
		String[] tokens = expression.split("::");
		for (String token : tokens) {
			if (token.indexOf(":") != -1) {
				return true;
			}
		}
		return false;
	}


	public Map<String, String> extractXmlNamespaces(Node contextNode)
			throws JRException {
		Map<String, String> namespaces = new HashMap<>();
		XPath namespaceXPath = xpathFact.newXPath();
		String namespaceXPathString = "//namespace::*";
		

		try {
			NodeList nlist = (NodeList) namespaceXPath.evaluate(
					namespaceXPathString, contextNode, XPathConstants.NODESET);
			for (int i = 0; i < nlist.getLength(); i++) {
				Node node = nlist.item(i);
				namespaces.put(node.getNodeName().substring("xmlns:".length()),
						node.getNodeValue());
			}
		} catch (XPathExpressionException ex) {
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE,
					new Object[]{namespaceXPathString},
					ex);
		}

		return namespaces;
	}
}
