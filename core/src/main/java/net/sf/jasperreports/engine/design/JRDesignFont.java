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
package net.sf.jasperreports.engine.design;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRStyleContainer;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignFont extends JRBaseFont
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_STYLE = "style";
	public static final String PROPERTY_STYLE_NAME_REFERENCE = "styleNameReference";
		

	@JsonCreator
	private JRDesignFont()
	{
		this(null); // this causes a style inheritance issue only for design view (mostly in charts), as base and fill factories provide proper style container reference; doesn't worth fixing 
	}

	
	/**
	 *
	 */
	public JRDesignFont(JRStyleContainer styleContainer)
	{
		super(styleContainer);
	}
		

	/**
	 *
	 */
	public void setStyle(JRStyle style)
	{
		Object old = this.style;
		this.style = style;
		getEventSupport().firePropertyChange(PROPERTY_STYLE, old, this.style);
	}

	
	/**
	 *
	 */
	public void setStyleNameReference(String styleNameReference)
	{
		Object old = this.styleNameReference;
		this.styleNameReference = styleNameReference;
		getEventSupport().firePropertyChange(PROPERTY_STYLE_NAME_REFERENCE, old, this.styleNameReference);
	}


	@JsonSetter(JRXmlConstants.ATTRIBUTE_style)
	private void setStyleName(String styleName)
	{
		if (styleName != null)
		{
			JasperDesign jasperDesign = JasperDesign.getThreadInstance();
			if (jasperDesign != null)
			{
				Map<String,JRStyle> stylesMap = jasperDesign.getStylesMap();

				if (stylesMap.containsKey(styleName))
				{
					JRStyle style = stylesMap.get(styleName);
					setStyle(style);
				}
				else
				{
					setStyleNameReference(styleName);
				}
			}
		}
	}

}
