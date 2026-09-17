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
import java.io.Serializable;

import net.sf.jasperreports.engine.Deduplicable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRPenContainer;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.LineStyleEnum;
import net.sf.jasperreports.engine.util.ObjectUtils;
import net.sf.jasperreports.engine.util.StyleResolver;


/**
 * This is useful for drawing borders around text elements and images. Boxes can have borders and paddings, which can
 * have different width and colour on each side of the element.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBasePen implements JRPen, Serializable, Cloneable, JRChangeEventsSupport, Deduplicable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_LINE_WIDTH = "lineWidth";
	
	public static final String PROPERTY_LINE_STYLE = "lineStyle";
	
	public static final String PROPERTY_LINE_COLOR = "lineColor";
	

	protected JRPenContainer penContainer;

	/**
	 *
	 */
	protected Float lineWidth;
	protected LineStyleEnum lineStyle;
	protected Color lineColor;

	
	/**
	 *
	 */
	public JRBasePen()
	{
		this(null);
	}
	
	
	/**
	 *
	 */
	public JRBasePen(JRPenContainer penContainer)
	{
		this.penContainer = penContainer;
	}
	
	
	@Override
	public JRPenContainer getPenContainer()
	{
		return penContainer;
	}

	/**
	 *
	 */
	protected StyleResolver getStyleResolver() 
	{
		if (getPenContainer().getDefaultStyleProvider() != null)
		{
			return getPenContainer().getDefaultStyleProvider().getStyleResolver();
		}
		return StyleResolver.getInstance();
	}
	
	@Override
	public Float getLineWidth()
	{
		return getStyleResolver().getLineWidth(this);
	}

	@Override
	public Float getOwnLineWidth()
	{
		return lineWidth;
	}

	@Override
	public void setLineWidth(Float lineWidth)
	{
		Object old = this.lineWidth;
		this.lineWidth = lineWidth;
		getEventSupport().firePropertyChange(PROPERTY_LINE_WIDTH, old, this.lineWidth);
	}

	@Override
	public LineStyleEnum getLineStyle()
	{
		return getStyleResolver().getLineStyle(this);
	}

	@Override
	public LineStyleEnum getOwnLineStyle()
	{
		return lineStyle;
	}

	@Override
	public void setLineStyle(LineStyleEnum lineStyle)
	{
		Object old = this.lineStyle;
		this.lineStyle = lineStyle;
		getEventSupport().firePropertyChange(PROPERTY_LINE_STYLE, old, this.lineStyle);
	}

	@Override
	public Color getLineColor()
	{
		return getStyleResolver().getLineColor(this, penContainer.getDefaultLineColor());
	}

	@Override
	public Color getOwnLineColor()
	{
		return lineColor;
	}

	@Override
	public void setLineColor(Color lineColor)
	{
		Object old = this.lineColor;
		this.lineColor = lineColor;
		getEventSupport().firePropertyChange(PROPERTY_LINE_COLOR, old, this.lineColor);
	}


	public String getStyleNameReference()
	{
		return null;
	}

	@Override
	public JRPen clone(JRPenContainer penContainer)
	{
		JRBasePen clone = null;
		try
		{
			clone = (JRBasePen)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		clone.penContainer = penContainer;
		clone.eventSupport = null;
		
		return clone;
	}
	
	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}

	
	@Override
	public int getHashCode()
	{
		ObjectUtils.HashCode hash = ObjectUtils.hash();
		hash.add(lineWidth);
		hash.add(lineStyle);
		hash.add(lineColor);
		return hash.getHashCode();
	}


	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRBasePen))
		{
			return false;
		}
		
		JRBasePen pen = (JRBasePen) object;

		return 
				ObjectUtils.equals(lineWidth, pen.lineWidth)
				&& ObjectUtils.equals(lineStyle, pen.lineStyle)
				&& ObjectUtils.equals(lineColor, pen.lineColor);
	}


	@Override
	public void populateStyle()
	{
		lineWidth = getLineWidth();
		lineStyle = getLineStyle();
		lineColor = getLineColor();
	}
}
