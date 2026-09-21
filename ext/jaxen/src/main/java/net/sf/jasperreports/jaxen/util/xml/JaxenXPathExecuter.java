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
package net.sf.jasperreports.jaxen.util.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ReferenceMap;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.xml.JRXPathExecuter;


/**
 * XPath executer implementation that uses <a href="http://jaxen.org/" target="_blank">Jaxen</a>.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JaxenXPathExecuter implements JRXPathExecuter
{

	protected static final String EXCEPTION_MESSAGE_KEY_XPATH_COMPILATION_FAILURE = "util.xml.jaxen.xpath.compilation.failure";
	protected static final String EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE = "util.xml.jaxen.xpath.selection.failure";
	
	private final Map<String,XPath> cachedXPaths = new ReferenceMap<>();//soft cache
	
	public JaxenXPathExecuter()
	{
	}
	
	protected XPath getXPath(String expression) throws JRException
	{
		XPath xPath = cachedXPaths.get(expression);
		if (xPath == null)
		{
			try
			{
				xPath = new DOMXPath(expression);
			}
			catch (JaxenException e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_XPATH_COMPILATION_FAILURE,
						new Object[]{expression},
						e);
			}
			cachedXPaths.put(expression, xPath);
		}
		return xPath;
	}
	
	@Override
	public NodeList selectNodeList(Node contextNode, String expression) throws JRException
	{
		try
		{
			XPath xpath = getXPath(expression);
			Object object = xpath.evaluate(contextNode);
			List<Object> nodes;
			if (object instanceof List<?>)
			{
				nodes = (List<Object>) object;
			}
			else
			{
				nodes = new ArrayList<>();
				nodes.add(object);
			}
			return new NodeListWrapper(nodes);
		}
		catch (JaxenException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE,
					new Object[]{expression},
					e);
		}		
	}

	@Override
	public Object selectObject(Node contextNode, String expression) throws JRException
	{
		try
		{
			XPath xpath = new DOMXPath(expression);
			Object object = xpath.evaluate(contextNode);
			Object value;
			if (object instanceof List<?>)
			{
				List<?> list = (List<?>) object;
				if (list.isEmpty())
				{
					value = null;
				}
				else
				{
					value = list.get(0);
				}
			}
			else if (object instanceof Number || object instanceof Boolean)
			{
				value = object;
			}
			else
			{
				value = object.toString();
			}
			return value;
		}
		catch (JaxenException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_XPATH_SELECTION_FAILURE,
					new Object[]{expression},
					e);
		}
	}

	protected static final class NodeListWrapper implements NodeList
	{

		private final List<?> nodes;
		
		public NodeListWrapper(List<?> nodes)
		{
			this.nodes = nodes;
		}
		
		@Override
		public int getLength()
		{
			return nodes.size();
		}

		@Override
		public Node item(int index)
		{
			return (Node)nodes.get(index);
		}
		
	}
	
}
