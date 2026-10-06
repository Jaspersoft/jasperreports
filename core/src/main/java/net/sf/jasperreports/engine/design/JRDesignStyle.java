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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConditionalStyle;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRDesignStyle extends JRBaseStyle
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_DEFAULT = "isDefault";
	
	public static final String PROPERTY_NAME = "name";
	
	public static final String PROPERTY_PARENT_STYLE = "parentStyle";
	
	public static final String PROPERTY_PARENT_STYLE_NAME_REFERENCE = "parentStyleNameReference";
	
	public static final String PROPERTY_CONDITIONAL_STYLES = "conditionalStyles";

	private List<JRConditionalStyle> conditionalStylesList = new ArrayList<>();


	@JsonCreator // not actually needed; used for clarity
	public JRDesignStyle()
	{
		this(JasperDesign.getThreadInstance());
	}

	/**
	 *
	 */
	public JRDesignStyle(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);
	}

	/**
	 *
	 */
	public void setName(String name)
	{
		Object old = this.name;
		this.name = name;
		getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
	}

	/**
	 *
	 */
	public void setDefault(boolean isDefault)
	{
		boolean old = this.isDefault;
		this.isDefault = isDefault;
		getEventSupport().firePropertyChange(PROPERTY_DEFAULT, old, this.isDefault);
	}

	@Override
	public void setParentStyle(JRStyle parentStyle)
	{
		Object old = getStyle();
		super.setParentStyle(parentStyle);
		getEventSupport().firePropertyChange(PROPERTY_PARENT_STYLE, old, getStyle());
	}

	/**
	 *
	 */
	public void addConditionalStyle(JRConditionalStyle conditionalStyle)
	{
		addConditionalStyle(conditionalStylesList.size(), conditionalStyle);
	}

	/**
	 *
	 */
	public void addConditionalStyle(int index, JRConditionalStyle conditionalStyle)
	{
		conditionalStylesList.add(index, conditionalStyle);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_CONDITIONAL_STYLES, 
				conditionalStyle, index);
	}

	/**
	 *
	 */
	public boolean removeConditionalStyle(JRConditionalStyle conditionalStyle)
	{
		int idx = conditionalStylesList.indexOf(conditionalStyle);
		if (idx >= 0)
		{
			conditionalStylesList.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_CONDITIONAL_STYLES, 
					conditionalStyle, idx);
			return true;
		}
		return false;
	}

	@Override
	public JRConditionalStyle[] getConditionalStyles()
	{
		return conditionalStylesList.toArray(new JRDesignConditionalStyle[conditionalStylesList.size()]);
	}

	/**
	 *
	 */
	@JsonIgnore
	public List<JRConditionalStyle> getConditionalStyleList()
	{
		return conditionalStylesList;
	}

	/**
	 *
	 */
	@JsonSetter
	private void setConditionalStyles(List<JRConditionalStyle> conditionalStyles)
	{
		if (conditionalStyles != null)
		{
			for (JRConditionalStyle conditonalStyle : conditionalStyles)
			{
				addConditionalStyle(conditonalStyle);
			}
		}
	}

	/**
	 * Set the name of the external style to be used as parent style.
	 * <p/>
	 * An external style is only effective when there is no internal parent style set,
	 * i.e. {@link #getStyle() getStyle()} returns <code>null</code>
	 * The external style will be resolved at fill time from the templates used in the report.
	 * 
	 * @param styleName the name of the external style
	 * @see #getStyleNameReference()
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_style)
	public void setParentStyleNameReference(String styleName)
	{
		Object old = this.parentStyleNameReference;
		this.parentStyleNameReference = styleName;
		getEventSupport().firePropertyChange(PROPERTY_PARENT_STYLE_NAME_REFERENCE, old, this.parentStyleNameReference);
	}

	@Override
	public Object clone()
	{
		JRDesignStyle clone = (JRDesignStyle) super.clone();
		clone.conditionalStylesList = JRCloneUtils.cloneList(conditionalStylesList);
		return clone;
	}
}
