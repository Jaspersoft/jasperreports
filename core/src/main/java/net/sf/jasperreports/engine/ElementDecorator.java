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
package net.sf.jasperreports.engine;

import java.awt.Color;
import java.util.UUID;

import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class ElementDecorator implements JRElement
{
	
	private final JRElement element;

	public ElementDecorator(JRElement decorated)
	{
		this.element = decorated;
	}
	
	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		} 
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
	}
	
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		element.collectExpressions(collector);
	}

	@Override
	public JRElementGroup getElementGroup()
	{
		return element.getElementGroup();
	}

	@Override
	public UUID getUUID()
	{
		return element.getUUID();
	}

	@Override
	public String getKey()
	{
		return element.getKey();
	}

	@Override
	public PositionTypeEnum getPositionType()
	{
		return element.getPositionType();
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return element.getPrintWhenExpression();
	}

	@Override
	public String getPrintWhenGroupChanges()
	{
		return element.getPrintWhenGroupChanges();
	}

	@Override
	public JRPropertyExpression[] getPropertyExpressions()
	{
		return element.getPropertyExpressions();
	}

	@Override
	public StretchTypeEnum getStretchType()
	{
		return element.getStretchType();
	}

	@Override
	public int getX()
	{
		return element.getX();
	}

	@Override
	public int getY()
	{
		return element.getY();
	}

	@Override
	public boolean isPrintInFirstWholeBand()
	{
		return element.isPrintInFirstWholeBand();
	}

	@Override
	public boolean isPrintRepeatedValues()
	{
		return element.isPrintRepeatedValues();
	}

	@Override
	public boolean isPrintWhenDetailOverflows()
	{
		return element.isPrintWhenDetailOverflows();
	}

	@Override
	public boolean isRemoveLineWhenBlank()
	{
		return element.isRemoveLineWhenBlank();
	}

	@Override
	public void setPositionType(PositionTypeEnum positionType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrintInFirstWholeBand(boolean isPrintInFirstWholeBand)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrintRepeatedValues(boolean isPrintRepeatedValues)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrintWhenDetailOverflows(boolean isPrintWhenDetailOverflows)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setRemoveLineWhenBlank(boolean isRemoveLineWhenBlank)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStretchType(StretchTypeEnum stretchTypeEnum)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setWidth(int width)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setX(int x)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object clone(JRElementGroup parentGroup)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRElement clone(JRElementGroup parentGroup, int y)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		element.visit(visitor);
	}

	@Override
	public Color getBackcolor()
	{
		return element.getBackcolor();
	}

	@Override
	public Color getForecolor()
	{
		return element.getForecolor();
	}

	@Override
	public int getHeight()
	{
		return element.getHeight();
	}

	@Override
	public ModeEnum getMode()
	{
		return element.getMode();
	}

	@Override
	public Color getOwnBackcolor()
	{
		return element.getOwnBackcolor();
	}

	@Override
	public Color getOwnForecolor()
	{
		return element.getOwnForecolor();
	}

	@Override
	public ModeEnum getOwnMode()
	{
		return element.getOwnMode();
	}

	@Override
	public int getWidth()
	{
		return element.getWidth();
	}

	@Override
	public void setBackcolor(Color backcolor)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setForecolor(Color forecolor)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMode(ModeEnum mode)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return element.getDefaultStyleProvider();
	}

	@Override
	public JRStyle getStyle()
	{
		return element.getStyle();
	}

	@Override
	public String getStyleNameReference()
	{
		return element.getStyleNameReference();
	}

	@Override
	public JRExpression getStyleExpression()
	{
		return element.getStyleExpression();
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return element.getParentProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return element.getPropertiesMap();
	}

	@Override
	public boolean hasProperties()
	{
		return element.hasProperties();
	}

}
