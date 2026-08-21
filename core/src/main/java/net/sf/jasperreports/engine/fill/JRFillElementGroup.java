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
package net.sf.jasperreports.engine.fill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillElementGroup implements JRElementGroup, JRFillCloneable
{


	/**
	 *
	 */
	protected List<JRChild> children = new ArrayList<>();
	protected JRElementGroup elementGroup;

	/**
	 *
	 */
	protected JRFillElement[] elements;

	/**
	 *
	 */
	protected JRElement topElementInGroup;
	protected JRElement bottomElementInGroup;
	private Integer stretchHeightDiff;


	/**
	 *
	 */
	protected JRFillElementGroup(
		JRElementGroup elementGrp, 
		JRFillObjectFactory factory
		)
	{
		if (elementGrp != null)
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
	
			/*   */
			getElements();
	
			elementGroup = (JRElementGroup)factory.getVisitResult(elementGrp.getElementGroup());
		}
	}

	
	protected JRFillElementGroup(JRFillElementGroup elementGrp, JRFillCloneFactory factory)
	{
		factory.put(elementGrp, this);

		List<JRChild> list = elementGrp.getChildren();
		if (list != null)
		{
			for (int i = 0; i < list.size(); i++)
			{
				JRFillCloneable child = (JRFillCloneable) list.get(i);
				JRFillCloneable clone = child.createClone(factory);
				children.add((JRChild)clone);
			}
		}

		getElements();

		elementGroup = (JRFillElementGroup) factory.getClone((JRFillElementGroup) elementGrp.getElementGroup());
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


	@Override
	public JRElement[] getElements()
	{
		if (this.elements == null)
		{
			if (this.children != null)
			{
				List<JRElement> allElements = new ArrayList<>();
				Object child = null;
				JRElement[] childElementArray = null;
				for (int i = 0; i < this.children.size(); i++)
				{
					child = this.children.get(i);
					if (child instanceof JRFillElement)
					{
						allElements.add((JRElement)child);
					}
					else if (child instanceof JRFillElementGroup)
					{
						childElementArray = ((JRFillElementGroup)child).getElements();
						if (childElementArray != null)
						{
							allElements.addAll( Arrays.asList(childElementArray) );
						}
					}
				}
				
				this.elements = new JRFillElement[allElements.size()];
				allElements.toArray(this.elements);
			}
		}
		
		return this.elements;
	}


	@Override
	public JRElement getElementByKey(String key)
	{
		return null;
	}


	/**
	 *
	 */
	protected void reset()
	{
		stretchHeightDiff = null;
	}


	/**
	 *
	 */
	protected int getStretchHeightDiff()
	{
		if (stretchHeightDiff == null)
		{
			stretchHeightDiff = 0;
			
			setTopBottomElements();

			JRElement[] allElements = getElements();

			if (allElements != null && allElements.length > 0)
			{
				JRFillElement topElem = null;
				JRFillElement bottomElem = null;

				for (int i = 0; i < allElements.length; i++)
				{
					JRFillElement element = (JRFillElement)allElements[i];
					//if (element != this && element.isToPrint())
					if (element.isToPrint())
					{
						if (
							topElem == null ||
							(
							element.getRelativeY() + element.getStretchHeight() <
							topElem.getRelativeY() + topElem.getStretchHeight())
							)
						{
							topElem = element;
						}

						if (
							bottomElem == null ||
							(
							element.getRelativeY() + element.getStretchHeight() >
							bottomElem.getRelativeY() + bottomElem.getStretchHeight())
							)
						{
							bottomElem = element;
						}
					}
				}

				if (topElem != null)
				{
					stretchHeightDiff = 
						bottomElem.getRelativeY() + bottomElem.getStretchHeight() - topElem.getRelativeY() -
						(bottomElementInGroup.getY() + bottomElementInGroup.getHeight() - topElementInGroup.getY());
				}

				if (stretchHeightDiff < 0)
				{
					stretchHeightDiff = 0;
				}
			}
		}
		
		return stretchHeightDiff;
	}


	/**
	 *
	 */
	private void setTopBottomElements()
	{
		if (topElementInGroup == null) // some element groups such as JRFillElementContainer already set this during their initElements
		{
			JRElement[] allElements = getElements();
			
			if (allElements != null && allElements.length > 0)
			{
				for (JRElement element : allElements)
				{
					if (
						topElementInGroup == null ||
						(
						element.getY() + element.getHeight() <
						topElementInGroup.getY() + topElementInGroup.getHeight())
						)
					{
						topElementInGroup = element;
					}

					if (
						bottomElementInGroup == null ||
						(
						element.getY() + element.getHeight() >
						bottomElementInGroup.getY() + bottomElementInGroup.getHeight())
						)
					{
						bottomElementInGroup = element;
					}
				}
			}
		}
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
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new JRFillElementGroup(this, factory);
	}

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object clone(JRElementGroup parentGroup) 
	{
		throw new UnsupportedOperationException();
	}
}
