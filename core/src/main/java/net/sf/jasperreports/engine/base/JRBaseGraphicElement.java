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

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRGraphicElement;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.type.FillEnum;


/**
 * This class provides functionality common to graphic elements. It provides implementation for the methods described
 * in <tt>JRTextElement</tt>.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRBaseGraphicElement extends JRBaseElement implements JRGraphicElement
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	
	/**
	 *
	 */
	protected JRPen linePen;
	protected FillEnum fill;


	/**
	 * Constructs an empty graphic element. By default graphic elements are opaque.
	 *
	protected JRBaseGraphicElement()
	{
		super();
	}
		

	/**
	 * Initializes properties that are specific to graphic elements. Common properties are initialized by its
	 * parent constructor.
	 * @param graphicElement an element whose properties are copied to this element. Usually it is a
	 * {@link net.sf.jasperreports.engine.design.JRDesignGraphicElement} that must be transformed into an
	 * <tt>JRBaseGraphicElement</tt> at compile time.
	 * @param factory a factory used in the compile process
	 */
	protected JRBaseGraphicElement(JRGraphicElement graphicElement, JRBaseObjectFactory factory)
	{
		super(graphicElement, factory);
		
		linePen = graphicElement.getLinePen().clone(this);
		
		fill = graphicElement.getOwnFill();
	}
		

	@Override
	public JRPen getLinePen()
	{
		return linePen;
	}

	@Override
	public FillEnum getFill()
	{
		return getStyleResolver().getFill(this);
	}

	@Override
	public FillEnum getOwnFill()
	{
		return this.fill;
	}
	
	@Override
	public void setFill(FillEnum fill)
	{
		FillEnum old = this.fill;
		this.fill = fill;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_FILL, old, this.fill);
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}
		

	@Override
	public Object clone() 
	{
		JRBaseGraphicElement clone = (JRBaseGraphicElement)super.clone();
		
		clone.linePen = linePen.clone(clone);
		
		return clone;
	}
}
