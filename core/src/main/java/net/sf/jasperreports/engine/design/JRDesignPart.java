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
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.base.JRBasePart;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartEvaluationTime;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignPart extends JRBasePart
{
	

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_PROPERTY_EXPRESSIONS = "propertyExpressions";
	public static final String PROPERTY_PRINT_WHEN_EXPRESSION = "printWhenExpression";
	public static final String PROPERTY_PART_NAME_EXPRESSION = "partNameExpression";
	public static final String PROPERTY_COMPONENT = "component";
	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";

	private List<JRPropertyExpression> propertyExpressions = new ArrayList<>();
	
	/**
	 * Creates an empty report part.
	 */
	public JRDesignPart()
	{
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
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

	/**
	 *
	 */
	public void setPrintWhenExpression(JRExpression expression)
	{
		Object old = this.printWhenExpression;
		this.printWhenExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_PRINT_WHEN_EXPRESSION, old, this.printWhenExpression);
	}
	
	public void setPartNameExpression(JRExpression expression)
	{
		Object old = this.partNameExpression;
		this.partNameExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_PART_NAME_EXPRESSION, old, this.partNameExpression);
	}


	/**
	 * Sets the component instance wrapped by this part.
	 * 
	 * @param component the component instance
	 * @see #getComponent()
	 */
	public void setComponent(PartComponent component)
	{
		Object old = this.component;
		this.component = component;
		getEventSupport().firePropertyChange(PROPERTY_COMPONENT, old, this.component);
	}
	
	public void setEvaluationTime(PartEvaluationTime evaluationTime)
	{
		Object old = this.evaluationTime;
		this.evaluationTime = evaluationTime;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME, old, this.evaluationTime);
	}

	@Override
	public Object clone()
	{
		JRDesignPart clone = (JRDesignPart) super.clone();
		clone.propertyExpressions = JRCloneUtils.cloneList(propertyExpressions);
		return clone;
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		
		if (propertyExpressions == null)
		{
			propertyExpressions = new ArrayList<>();
		}
	}

}
