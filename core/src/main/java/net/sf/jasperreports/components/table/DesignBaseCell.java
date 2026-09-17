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
package net.sf.jasperreports.components.table;

import java.awt.Color;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.design.DesignStyleContainer;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DesignBaseCell extends JRDesignElementGroup implements BaseCell, DesignStyleContainer
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_DEFAULT_STYLE_PROVIDER = "defaultStyleProvider";
	public static final String PROPERTY_STYLE = "style";
	public static final String PROPERTY_STYLE_NAME_REFERENCE = "styleNameReference";
	public static final String PROPERTY_HEIGHT = "height";
	
	protected JRDefaultStyleProvider defaultStyleProvider;
	protected JRStyle style;
	protected String styleNameReference;
	protected JRLineBox box;
	protected Integer height;
	
	protected JRPropertiesMap propertiesMap;

	public DesignBaseCell()
	{
		this.defaultStyleProvider = JasperDesign.getThreadInstance();
		this.box = new JRBaseLineBox(this);
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

	@JsonSetter(JRXmlConstants.ELEMENT_box)
	private void setLineBox(JRLineBox box)
	{
		this.box = box == null ? null : box.clone(this);
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
	public void setDefaultStyleProvider(
			JRDefaultStyleProvider defaultStyleProvider)
	{
		Object old = this.defaultStyleProvider;
		this.defaultStyleProvider = defaultStyleProvider;
		getEventSupport().firePropertyChange(PROPERTY_DEFAULT_STYLE_PROVIDER, 
				old, this.defaultStyleProvider);
	}

	@Override
	public void setStyle(JRStyle style)
	{
		Object old = this.style;
		this.style = style;
		getEventSupport().firePropertyChange(PROPERTY_STYLE, 
				old, this.style);
	}

	@Override
	public void setStyleNameReference(String styleName)
	{
		Object old = this.styleNameReference;
		this.styleNameReference = styleName;
		getEventSupport().firePropertyChange(PROPERTY_STYLE_NAME_REFERENCE, 
				old, this.styleNameReference);
	}

	@JsonSetter(JRXmlConstants.ATTRIBUTE_style)
	private void setStyleName(String styleName)
	{
		if (styleName != null)
		{
			JasperDesign jasperDesign = JasperDesign.getThreadInstance();
			Map<String,JRStyle> stylesMap = jasperDesign == null ? null : jasperDesign.getStylesMap();

			if (stylesMap != null && stylesMap.containsKey(styleName))
			{
				setStyle(stylesMap.get(styleName));
			}
			else
			{
				setStyleNameReference(styleName);
			}
		}
	}

	public void setHeight(Integer height)
	{
		Object old = this.height;
		this.height = height;
		getEventSupport().firePropertyChange(PROPERTY_HEIGHT, 
				old, this.height);
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
		DesignBaseCell clone = (DesignBaseCell) super.clone();
		clone.propertiesMap = JRPropertiesMap.getPropertiesClone(this);
		return clone;
	}

}
