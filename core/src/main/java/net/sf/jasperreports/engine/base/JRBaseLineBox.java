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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.Deduplicable;
import net.sf.jasperreports.engine.JRBoxContainer;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.ObjectUtils;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * This is useful for drawing borders around text elements and images. Boxes can have borders and paddings, which can
 * have different width and colour on each side of the element.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseLineBox implements JRLineBox, Serializable, Cloneable, JRChangeEventsSupport, Deduplicable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_PADDING = "padding";
	
	public static final String PROPERTY_TOP_PADDING = "topPadding";
	
	public static final String PROPERTY_LEFT_PADDING = "leftPadding";
	
	public static final String PROPERTY_BOTTOM_PADDING = "bottomPadding";
	
	public static final String PROPERTY_RIGHT_PADDING = "rightPadding";
	

	protected JRBoxContainer boxContainer;

	/**
	 *
	 */
	protected JRBoxPen pen; 
	protected JRBoxPen topPen;
	protected JRBoxPen leftPen;
	protected JRBoxPen bottomPen;
	protected JRBoxPen rightPen;

	protected Integer padding;
	protected Integer topPadding;
	protected Integer leftPadding;
	protected Integer bottomPadding;
	protected Integer rightPadding;

	
	/**
	 *
	 */
	public JRBaseLineBox(JRBoxContainer boxContainer)
	{
		this.boxContainer = boxContainer;

		pen = new JRBaseBoxPen(this);
		topPen = new JRBaseBoxTopPen(this);
		leftPen = new JRBaseBoxLeftPen(this);
		bottomPen = new JRBaseBoxBottomPen(this);
		rightPen = new JRBaseBoxRightPen(this);
	}
	
	
	
	@JsonCreator
	private JRBaseLineBox()
	{
		this(null);
	}
	
	
	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider() 
	{
		if (boxContainer != null)
		{
			return boxContainer.getDefaultStyleProvider();
		}
		return null;
	}

	/**
	 *
	 */
	protected StyleResolver getStyleResolver() 
	{
		if (getDefaultStyleProvider() != null)
		{
			return getDefaultStyleProvider().getStyleResolver();
		}
		return StyleResolver.getInstance();
	}

	@Override
	public JRStyle getStyle() 
	{
		if (boxContainer != null)
		{
			return boxContainer.getStyle();
		}
		return null;
	}

	@Override
	public String getStyleNameReference()
	{
		if (boxContainer != null)
		{
			return boxContainer.getStyleNameReference();
		}
		return null;
	}

	@Override
	public JRBoxContainer getBoxContainer()
	{
		return boxContainer;
	}

	@Override
	public Color getDefaultLineColor()
	{
		if (boxContainer != null)
		{
			return boxContainer.getDefaultLineColor();
		}
		return Color.black;
	}

	@Override
	public JRBoxPen getPen()
	{
		return pen;
	}

	@Override
	@JsonSetter(JRXmlConstants.ELEMENT_pen)
	public void copyPen(JRBoxPen pen)
	{
		this.pen = pen.clone(this);
	}

	@Override
	public JRBoxPen getTopPen()
	{
		return topPen;
	}

	@Override
	@JsonSetter(JRXmlConstants.ELEMENT_topPen)
	public void copyTopPen(JRBoxPen topPen)
	{
		this.topPen = topPen.clone(this);
	}

	@Override
	public JRBoxPen getLeftPen()
	{
		return leftPen;
	}

	@Override
	@JsonSetter(JRXmlConstants.ELEMENT_leftPen)
	public void copyLeftPen(JRBoxPen leftPen)
	{
		this.leftPen = leftPen.clone(this);
	}

	@Override
	public JRBoxPen getBottomPen()
	{
		return bottomPen;
	}

	@Override
	@JsonSetter(JRXmlConstants.ELEMENT_bottomPen)
	public void copyBottomPen(JRBoxPen bottomPen)
	{
		this.bottomPen = bottomPen.clone(this);
	}

	@Override
	public JRBoxPen getRightPen()
	{
		return rightPen;
	}

	@Override
	@JsonSetter(JRXmlConstants.ELEMENT_rightPen)
	public void copyRightPen(JRBoxPen rightPen)
	{
		this.rightPen = rightPen.clone(this);
	}

	@Override
	public Integer getPadding()
	{
		return getStyleResolver().getPadding(this);
	}

	@Override
	public Integer getOwnPadding()
	{
		return padding;
	}
	
	@Override
	public void setPadding(Integer padding)
	{
		Object old = this.padding;
		this.padding = padding;
		getEventSupport().firePropertyChange(PROPERTY_PADDING, old, this.padding);
	}

	@Override
	public Integer getTopPadding()
	{
		return getStyleResolver().getTopPadding(this);
	}

	@Override
	public Integer getOwnTopPadding()
	{
		return topPadding;
	}

	@Override
	public void setTopPadding(Integer topPadding)
	{
		Object old = this.topPadding;
		this.topPadding = topPadding;
		getEventSupport().firePropertyChange(PROPERTY_TOP_PADDING, old, this.topPadding);
	}

	@Override
	public Integer getLeftPadding()
	{
		return getStyleResolver().getLeftPadding(this);
	}

	@Override
	public Integer getOwnLeftPadding()
	{
		return leftPadding;
	}

	@Override
	public void setLeftPadding(Integer leftPadding)
	{
		Object old = this.leftPadding;
		this.leftPadding = leftPadding;
		getEventSupport().firePropertyChange(PROPERTY_LEFT_PADDING, old, this.leftPadding);
	}

	@Override
	public Integer getBottomPadding()
	{
		return getStyleResolver().getBottomPadding(this);
	}

	@Override
	public Integer getOwnBottomPadding()
	{
		return bottomPadding;
	}

	@Override
	public void setBottomPadding(Integer bottomPadding)
	{
		Object old = this.bottomPadding;
		this.bottomPadding = bottomPadding;
		getEventSupport().firePropertyChange(PROPERTY_BOTTOM_PADDING, old, this.bottomPadding);
	}

	@Override
	public Integer getRightPadding()
	{
		return getStyleResolver().getRightPadding(this);
	}

	@Override
	public Integer getOwnRightPadding()
	{
		return rightPadding;
	}

	@Override
	public void setRightPadding(Integer rightPadding)
	{
		Object old = this.rightPadding;
		this.rightPadding = rightPadding;
		getEventSupport().firePropertyChange(PROPERTY_RIGHT_PADDING, old, this.rightPadding);
	}


	@Override
	public JRLineBox clone(JRBoxContainer boxContainer)
	{
		JRBaseLineBox clone = null;
		
		try
		{
			clone = (JRBaseLineBox)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		clone.boxContainer = boxContainer;
		
		clone.pen = pen.clone(clone);
		clone.topPen = topPen.clone(clone);
		clone.leftPen = leftPen.clone(clone);
		clone.bottomPen = bottomPen.clone(clone);
		clone.rightPen = rightPen.clone(clone);
		
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
		hash.addIdentical(pen);
		hash.addIdentical(topPen);
		hash.addIdentical(leftPen);
		hash.addIdentical(bottomPen);
		hash.addIdentical(rightPen);
		hash.add(padding);
		hash.add(topPadding);
		hash.add(leftPadding);
		hash.add(bottomPadding);
		hash.add(rightPadding);
		return hash.getHashCode();
	}

	@Override
	public boolean isIdentical(Object object)
	{
		if (this == object)
		{
			return true;
		}
		
		if (!(object instanceof JRBaseLineBox))
		{
			return false;
		}
		
		JRBaseLineBox box = (JRBaseLineBox) object;

		return 
				ObjectUtils.identical(pen, box.pen)
				&& ObjectUtils.identical(topPen, box.topPen)
				&& ObjectUtils.identical(leftPen, box.leftPen)
				&& ObjectUtils.identical(bottomPen, box.bottomPen)
				&& ObjectUtils.identical(rightPen, box.rightPen)
				&& ObjectUtils.equals(padding, box.padding)
				&& ObjectUtils.equals(topPadding, box.topPadding)
				&& ObjectUtils.equals(leftPadding, box.leftPadding)
				&& ObjectUtils.equals(bottomPadding, box.bottomPadding)
				&& ObjectUtils.equals(rightPadding, box.rightPadding);
	}


	@Override
	public void populateStyle()
	{
		if (topPen != null)
		{
			topPen.populateStyle();
		}
		if (leftPen != null)
		{
			leftPen.populateStyle();
		}
		if (bottomPen != null)
		{
			bottomPen.populateStyle();
		}
		if (rightPen != null)
		{
			rightPen.populateStyle();
		}
		if (pen != null)
		{
			pen.populateStyle();
		}
		topPadding = getTopPadding();
		leftPadding = getLeftPadding();
		bottomPadding = getBottomPadding();
		rightPadding = getRightPadding();
		padding = getPadding();
	}

}
