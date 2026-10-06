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
package net.sf.jasperreports.components.table;

import java.awt.Color;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseElementGroup;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CompiledBaseCell extends JRBaseElementGroup implements BaseCell
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected JRDefaultStyleProvider defaultStyleProvider;
	protected JRStyle style;
	protected String styleNameReference;
	protected JRLineBox box;
	protected Integer height;
	
	protected JRPropertiesMap propertiesMap;
	
	public CompiledBaseCell()
	{
		super();
		
		box = new JRBaseLineBox(this);
	}

	public CompiledBaseCell(BaseCell cell, JRBaseObjectFactory factory)
	{
		super(cell, factory);
		
		this.defaultStyleProvider = factory.getDefaultStyleProvider();
		this.style = factory.getStyle(cell.getStyle());
		this.styleNameReference = cell.getStyleNameReference();
		this.box = cell.getLineBox().clone(this);
		this.height = cell.getHeight();
		
		this.propertiesMap = JRPropertiesMap.getPropertiesClone(cell);
	}

	@Override
	public Integer getHeight()
	{
		return height;
	}

	@Override
	public Color getDefaultLineColor()
	{
		return Color.BLACK;
	}

	@Override
	public JRLineBox getLineBox()
	{
		return box;
	}

	@Override
	public JRDefaultStyleProvider getDefaultStyleProvider()
	{
		return defaultStyleProvider;
	}

	@Override
	public JRStyle getStyle()
	{
		return style;
	}

	@Override
	public String getStyleNameReference()
	{
		return styleNameReference;
	}

	@Override
	public boolean hasProperties()
	{
		return propertiesMap != null && propertiesMap.hasProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		if (propertiesMap == null)
		{
			propertiesMap = new JRPropertiesMap();
		}
		return propertiesMap;
	}

	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}
	
	@Override
	public Object clone() 
	{
		CompiledBaseCell clone = (CompiledBaseCell) super.clone();
		clone.propertiesMap = JRPropertiesMap.getPropertiesClone(this);
		return clone;
	}

}
