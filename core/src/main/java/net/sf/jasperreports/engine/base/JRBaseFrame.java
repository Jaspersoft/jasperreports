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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.type.BorderSplitType;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.ElementsVisitorUtils;

/**
 * Base read-only implementation of {@link net.sf.jasperreports.engine.JRFrame JRFrame}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRBaseFrame extends JRBaseElement implements JRFrame
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	protected List<JRChild> children;

	protected JRLineBox lineBox;
	
	private BorderSplitType borderSplitType;


	public JRBaseFrame(JRFrame frame, JRBaseObjectFactory factory)
	{
		super(frame, factory);

		List<JRChild> frameChildren = frame.getChildren();
		if (frameChildren != null)
		{
			children = new ArrayList<>(frameChildren.size());
			for (JRChild child : frameChildren)
			{
				children.add((JRChild)factory.getVisitResult(child));
			}
		}
		
		lineBox = frame.getLineBox().clone(this);
		this.borderSplitType = frame.getBorderSplitType();
	}

	@Override
	public JRElement[] getElements()
	{
		return JRBaseElementGroup.getElements(children);
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

	
	@Override
	public Object clone()
	{
		JRBaseFrame clone = (JRBaseFrame) super.clone();
		
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
}
