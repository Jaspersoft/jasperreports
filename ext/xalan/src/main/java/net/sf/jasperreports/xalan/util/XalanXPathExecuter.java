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
package net.sf.jasperreports.xalan.util;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.xml.JRXPathExecuter;


/**
 * XPath executer implementation that uses <a href="http://xml.apache.org/xalan-j/" target="_blank">Apache Xalan</a>.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class XalanXPathExecuter implements JRXPathExecuter {

	protected static final String EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE = "util.xml.xalan.xpath.selection.failure";

	// XPath API facade
	private CachedXPathAPI xpathAPI = new CachedXPathAPI();

	/**
	 * Default constructor.
	 */
	public XalanXPathExecuter() {
	}
	
	@Override
	public NodeList selectNodeList(Node contextNode, String expression) throws JRException
	{
		try {
			return xpathAPI.selectNodeList(contextNode, expression);
		} catch (TransformerException e) {
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE,
					new Object[]{expression},
					e);
		}
	}

	@Override
	public Object selectObject(Node contextNode, String expression) throws JRException {
		try {
			Object value;
			XObject object = xpathAPI.eval(contextNode, expression);
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
	
}
