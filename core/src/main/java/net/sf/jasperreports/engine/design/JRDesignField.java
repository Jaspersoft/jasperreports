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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.base.JRBaseField;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignField extends JRBaseField
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_NAME = "name";
	
	public static final String PROPERTY_VALUE_CLASS_NAME = "valueClassName";
	
	public static final String PROPERTY_PROPERTY_EXPRESSIONS = "propertyExpressions";

	private List<JRPropertyExpression> propertyExpressions = new ArrayList<>();

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
	public void setValueClass(Class<?> clazz)
	{
		setValueClassName(clazz.getName());
	}
	
	/**
	 *
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_class)
	public void setValueClassName(String className)
	{
		Object old = this.valueClassName;
		valueClassName = className;
		valueClass = null;
		valueClassRealName = null;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_CLASS_NAME, old, this.valueClassName);
	}
	
	/**
	 * Add a dynamic/expression-based property.
	 * 
	 * @param propertyExpression the property to add
	 * @see #getPropertyExpressions()
	 */
	public void addPropertyExpression(JRPropertyExpression propertyExpression)
	{
		propertyExpressions.add(propertyExpression);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PROPERTY_EXPRESSIONS, 
				propertyExpression, propertyExpressions.size() - 1);
	}

	/**
	 * Remove a property expression.
	 * 
	 * @param propertyExpression the property expression to remove
	 * @see #addPropertyExpression(JRPropertyExpression)
	 */
	public void removePropertyExpression(JRPropertyExpression propertyExpression)
	{
		int idx = propertyExpressions.indexOf(propertyExpression);
		if (idx >= 0)
		{
			propertyExpressions.remove(idx);
			
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PROPERTY_EXPRESSIONS, 
					propertyExpression, idx);
		}
	}
	
	/**
	 * Remove a property expression.
	 * 
	 * @param name the name of the property to remove
	 * @return the removed property expression (if found)
	 */
	public JRPropertyExpression removePropertyExpression(String name)
	{
		JRPropertyExpression removed = null;
		for (ListIterator<JRPropertyExpression> it = propertyExpressions.listIterator(); it.hasNext();)
		{
			JRPropertyExpression prop = it.next();
			if (name.equals(prop.getName()))
			{
				removed = prop;
				int idx = it.previousIndex();
				
				it.remove();
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PROPERTY_EXPRESSIONS, 
						removed, idx);
				break;
			}
		}
		return removed;
	}
	
	/**
	 * Returns the list of property expressions.
	 * 
	 * @return the list of property expressions ({@link JRPropertyExpression} instances)
	 * @see #addPropertyExpression(JRPropertyExpression)
	 */
	@JsonIgnore
	public List<JRPropertyExpression> getPropertyExpressionsList()
	{
		return propertyExpressions;
	}
	
	@JsonSetter
	private void setPropertyExpressions(List<JRPropertyExpression> propertyExpressions)
	{
		if (propertyExpressions != null)
		{
			for (JRPropertyExpression propertyExpression : propertyExpressions)
			{
				addPropertyExpression(propertyExpression);
			}
		}
	}

	@Override
	public JRPropertyExpression[] getPropertyExpressions()
	{
		JRPropertyExpression[] props;
		if (propertyExpressions.isEmpty())
		{
			props = null;
		}
		else
		{
			props = propertyExpressions.toArray(new JRPropertyExpression[propertyExpressions.size()]);
		}
		return props;
	}

	@Override
	public Object clone()
	{
		JRDesignField clone = (JRDesignField) super.clone();
		clone.propertyExpressions = JRCloneUtils.cloneList(propertyExpressions);
		return clone;
	}

	private void readObject(ObjectInputStream stream)
		throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		
		if (propertyExpressions == null)
		{
			propertyExpressions = new ArrayList<>();
		}
	}
}
