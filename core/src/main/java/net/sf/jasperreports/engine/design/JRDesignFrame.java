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
package net.sf.jasperreports.engine.design;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.base.JRBaseElementGroup;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.type.BorderSplitType;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Implementation of {@link net.sf.jasperreports.engine.JRFrame JRFrame} to be used at design time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignFrame extends JRDesignElement implements JRFrame
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/*
	 * Frame properties
	 */
	
	public static final String PROPERTY_CHILDREN = "children";
	
	public static final String PROPERTY_BORDER_SPLIT_TYPE = "borderSplitType";
	
	private List<JRChild> children;

	private JRLineBox lineBox;

	private BorderSplitType borderSplitType;
	
	/**
	 * Creates a new frame object.
	 * 
	 * @param defaultStyleProvider default style provider instance
	 */
	public JRDesignFrame(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);
		
		children = new ArrayList<>();
		
		lineBox = new JRBaseLineBox(this);
	}

	
	/**
	 * Creates a new frame object.
	 */
	@JsonCreator // not actually needed; used for clarity
	public JRDesignFrame()
	{
		this(JasperDesign.getThreadInstance());
	}

	
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitFrame(this);
		
		if (ElementsVisitorUtils.visitDeepElements(visitor))
		{
			ElementsVisitorUtils.visitElements(visitor, children);
		}
	}

	@Override
	public JRElement[] getElements()
	{
		return JRBaseElementGroup.getElements(children);
	}

	
	/**
	 *
	 */
	@JsonSetter
	private void setChildren(List<JRChild> children)
	{
		if (children != null)
		{
			for (JRChild child : children)
			{
				if (child instanceof JRDesignElement)
				{
					addElement((JRDesignElement)child);
				}
				else
				{
					addElementGroup((JRDesignElementGroup)child);
				}
			}
		}
	}


	/**
	 * Adds a sub element to the frame.
	 * 
	 * @param element the element to add
	 */
	public void addElement(JRElement element)
	{
		addElement(children.size(), element);
	}
	
	
	/**
	 * Inserts a sub element at specified position into the frame.
	 * 
	 * @param index the element position
	 * @param element the element to add
	 */
	public void addElement(int index, JRElement element)
	{
		if (element instanceof JRDesignElement)
		{
			((JRDesignElement) element).setElementGroup(this);
		}

		children.add(index, element);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_CHILDREN, element, index);
	}
	
	
	/**
	 * Removes a sub element from the frame.
	 * 
	 * @param element the element to remove
	 * @return <tt>true</tt> if this frame contained the specified element
	 */
	public boolean removeElement(JRElement element)
	{
		if (element instanceof JRDesignElement)
		{
			((JRDesignElement) element).setElementGroup(null);
		}

		int idx = children.indexOf(element);
		if (idx >= 0)
		{
			children.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_CHILDREN, element, idx);
			return true;
		}
		return false;
	}

	
	/**
	 * Adds an element group to the frame.
	 * 
	 * @param group the element group to add
	 */
	public void addElementGroup(JRElementGroup group)
	{
		addElementGroup(children.size(), group);
	}
	
	
	/**
	 * Inserts an element group at specified position into the frame.
	 * 
	 * @param index the element group position
	 * @param group the element group to add
	 */
	public void addElementGroup(int index, JRElementGroup group)
	{
		if (group instanceof JRDesignElementGroup)
		{
			((JRDesignElementGroup) group).setElementGroup(this);
		}
		
		children.add(index, group);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_CHILDREN, group, index);
	}
	
	
	/**
	 * Removes a group element from the frame.
	 * 
	 * @param group the group to remove
	 * @return <tt>true</tt> if this frame contained the specified group
	 */
	public boolean removeElementGroup(JRElementGroup group)
	{
		if (group instanceof JRDesignElementGroup)
		{
			((JRDesignElementGroup) group).setElementGroup(null);
		}
		
		int idx = children.indexOf(group);
		if (idx >= 0)
		{
			children.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_CHILDREN, group, idx);
			return true;
		}
		return false;
	}

	
	@Override
	public List<JRChild> getChildren()
	{
		return children;
	}

	@Override
	public JRElement getElementByKey(String elementKey)
	{
		return JRBaseElementGroup.getElementByKey(getElements(), elementKey);
	}
	
	
	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}
	
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ELEMENT_box)
	public void copyBox(JRLineBox lineBox)
	{
		this.lineBox = lineBox.clone(this);
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}

	@Override
	public BorderSplitType getBorderSplitType()
	{
		return borderSplitType;
	}

	/**
	 * Sets the border split type for the frame.
	 * 
	 * @param borderSplitType the border split type
	 * @see JRFrame#getBorderSplitType()
	 */
	public void setBorderSplitType(BorderSplitType borderSplitType)
	{
		Object old = this.borderSplitType;
		this.borderSplitType = borderSplitType;
		getEventSupport().firePropertyChange(PROPERTY_BORDER_SPLIT_TYPE, old, this.borderSplitType);
	}
	
	@Override
	public Object clone() 
	{
		JRDesignFrame clone = (JRDesignFrame)super.clone();
		
		if (children != null)
		{
			clone.children = new ArrayList<>(children.size());
			for (int i = 0; i < children.size(); i++)
			{
				clone.children.add((JRChild)(children.get(i).clone(clone)));
			}
		}

		clone.lineBox = lineBox.clone(clone);

		return clone;
	}
}
