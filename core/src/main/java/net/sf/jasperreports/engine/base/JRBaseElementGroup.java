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
package net.sf.jasperreports.engine.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseElementGroup implements JRElementGroup, Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	protected List<JRChild> children = new ArrayList<>();
	protected JRElementGroup elementGroup;


	/**
	 *
	 */
	protected JRBaseElementGroup()
	{
	}
	
	
	/**
	 *
	 */
	protected JRBaseElementGroup(JRElementGroup elementGrp, JRBaseObjectFactory factory)
	{
		factory.put(elementGrp, this);
		
		/*   */
		List<JRChild> list = elementGrp.getChildren();
		if (list != null && list.size() > 0)
		{
			for (int i = 0; i < list.size(); i++)
			{
				JRChild child = list.get(i);
				child = (JRChild)factory.getVisitResult(child);
				children.add(child);
			}
		}

		this.elementGroup = (JRElementGroup)factory.getVisitResult(elementGrp.getElementGroup());
	}
		

	@Override
	public List<JRChild> getChildren()
	{
		return this.children;
	}


	@Override
	public JRElementGroup getElementGroup()
	{
		return this.elementGroup;
	}


	/**
	 *
	 */
	public static JRElement[] getElements(List<JRChild> children)
	{
		JRElement[] elements = null;
		
		if (children != null)
		{
			List<JRElement> allElements = new ArrayList<>();
			Object child = null;
			JRElement[] childElementArray = null;
			for (int i = 0; i < children.size(); i++)
			{
				child = children.get(i);
				if (child instanceof JRElement)
				{
					allElements.add((JRElement)child);
				}
				else if (child instanceof JRElementGroup)
				{
					childElementArray = ((JRElementGroup)child).getElements();
					if (childElementArray != null)
					{
						allElements.addAll( Arrays.asList(childElementArray) );
					}
				}
			}
			
			elements = new JRElement[allElements.size()];
			allElements.toArray(elements);
		}
		
		return elements;
	}

	
	@Override
	public JRElement[] getElements()
	{
		return getElements(children);
	}
	

	/**
	 *
	 */
	public static JRElement getElementByKey(JRElement[] elements, String key)
	{
		JRElement element = null;
		
		if (key != null)
		{
			if (elements != null)
			{
				int i = 0;
				while (element == null && i < elements.length)
				{
					JRElement elem = elements[i];
					if (key.equals(elem.getKey()))
					{
						element = elem;
					}
					else if (elem instanceof JRFrame)
					{
						element = ((JRFrame) elem).getElementByKey(key);
					}
					else if (elem instanceof JRCrosstab)
					{
						element = ((JRCrosstab) elem).getElementByKey(key);
					}
					i++;
				}
			}
		}
		
		return element;
	}

	
	@Override
	public JRElement getElementByKey(String key)
	{
		return getElementByKey(getElements(), key);
	}

	
	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitElementGroup(this);
		
		if (ElementsVisitorUtils.visitDeepElements(visitor))
		{
			ElementsVisitorUtils.visitElements(visitor, children);
		}
	}


	@Override
	public Object clone() 
	{
		JRBaseElementGroup clone = null;
		
		try
		{
			clone = (JRBaseElementGroup)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		if (children != null)
		{
			clone.children = new ArrayList<>(children.size());
			for (int i = 0; i < children.size(); i++)
			{
				clone.children.add((JRChild)(children.get(i).clone(clone)));
			}
		}

		return clone;
	}

	@Override
	public Object clone(JRElementGroup parentGroup) 
	{
		JRBaseElementGroup clone = (JRBaseElementGroup)this.clone();
		
		clone.elementGroup = parentGroup;
		
		return clone;
	}

}
