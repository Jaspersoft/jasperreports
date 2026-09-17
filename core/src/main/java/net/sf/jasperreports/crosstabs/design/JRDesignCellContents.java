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
package net.sf.jasperreports.crosstabs.design;

import java.awt.Color;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.design.JRDesignElementGroup;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * Implementation of {@link net.sf.jasperreports.crosstabs.JRCellContents JRCellContents} used for
 * report design.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignCellContents extends JRDesignElementGroup implements JRCellContents
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_BOX = "box";
	
	public static final String PROPERTY_STYLE = "style";
	
	public static final String PROPERTY_STYLE_NAME_REFERENCE = "styleNameReference";

	protected JRDefaultStyleProvider defaultStyleProvider;
	protected JRStyle style;
	protected String styleNameReference;
	
	protected ModeEnum mode;
	private Color backcolor;
	private JRLineBox lineBox;
	private int width = JRCellContents.NOT_CALCULATED;
	private int height = JRCellContents.NOT_CALCULATED;

	private JRCrosstabOrigin origin;
	
	private JRPropertiesMap propertiesMap;
	
	/**
	 * Creates an empty cell contents.
	 */
	public JRDesignCellContents()
	{
		super();
		
		defaultStyleProvider = JasperDesign.getThreadInstance();
		lineBox = new JRBaseLineBox(this);
	}
	
	@Override
	public Color getBackcolor()
	{
		return backcolor;
	}
	
	
	/**
	 * Sets the cell background color.
	 * 
	 * @param color the background color
	 * @see JRCellContents#getBackcolor()
	 */
	public void setBackcolor(Color color)
	{
		Object old = this.backcolor;
		backcolor = color;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_BACKCOLOR, old, this.backcolor);
	}

	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}
	
	@JsonSetter(JRXmlConstants.ELEMENT_box)
	private void setLineBox(JRLineBox lineBox)
	{
		this.lineBox = lineBox == null ? null : lineBox.clone(this);
	}
	
	@Override
	public int getHeight()
	{
		return height;
	}

	
	/**
	 * Sets the computed cell height.
	 * 
	 * <p>
	 * The method should NOT be called by external code.
	 * </p>
	 * 
	 * @param height the cell height
	 * @see JRCellContents#getHeight()
	 */
	public void setHeight(int height)
	{
		this.height = height;
	}

	@Override
	public int getWidth()
	{
		return width;
	}


	/**
	 * Sets the computed cell width.
	 * 
	 * @param width the cell width
	 * @see JRCellContents#getWidth()
	 */
	protected void setWidth(int width)
	{
		this.width = width;
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
	
	
	/**
	 * Sets the style used by this cell.
	 * <p/>
	 * The style is only used for cell background and borders and is not inherited by
	 * elements inside the cell.
	 * 
	 * @param style the style to be used
	 */
	public void setStyle(JRStyle style)
	{
		Object old = this.style;
		this.style = style;
		getEventSupport().firePropertyChange(PROPERTY_STYLE, old, this.style);
	}

	@Override
	public ModeEnum getMode()
	{
		return mode;
	}
	
	/**
	 * Sets the cell transparency mode.
	 * 
	 * @param mode the transparency mode
	 * @see JRCellContents#getMode()
	 */
	public void setMode(ModeEnum mode)
	{
		Object old = this.mode;
		this.mode = mode;
		getEventSupport().firePropertyChange(JRBaseStyle.PROPERTY_MODE, old, this.mode);
	}

	@Override
	public String getStyleNameReference()
	{
		return styleNameReference;
	}

	
	/**
	 * Set the name of the external style to be used for this cell.
	 * <p/>
	 * An external style is only effective when there is no internal style set for this cell,
	 * i.e. {@link #getStyle() getStyle()} returns <code>null</code>
	 * The external style will be resolved at fill time from the templates used in the report.
	 * 
	 * @param styleName the name of the external style
	 * @see #getStyleNameReference()
	 */
	public void setStyleNameReference(String styleName)
	{
		Object old = this.styleNameReference;
		this.styleNameReference = styleName;
		getEventSupport().firePropertyChange(PROPERTY_STYLE_NAME_REFERENCE, old, this.styleNameReference);
	}

	@JsonSetter(JRXmlConstants.ELEMENT_style)
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
	
	@JsonIgnore
	public JRCrosstabOrigin getOrigin()
	{
		return origin;
	}
	
	@JsonIgnore
	public void setOrigin(JRCrosstabOrigin origin)
	{
		this.origin = origin;
	}
	
	@Override
	public Color getDefaultLineColor() 
	{
		return Color.black;
	}

	@Override
	public Object clone() 
	{
		JRDesignCellContents clone = (JRDesignCellContents) super.clone();
		clone.lineBox = lineBox == null ? null : (JRLineBox) lineBox.clone(clone);
		clone.propertiesMap = JRPropertiesMap.getPropertiesClone(this);
		return clone;
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
}
