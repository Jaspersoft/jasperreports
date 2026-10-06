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
package net.sf.jasperreports.engine.fill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillField implements JRField
{


	/**
	 *
	 */
	protected JRField parent;
	protected JRFillExpressionEvaluator expressionEvaluator;
	protected List<JRPropertyExpression> propertyExpressions;
	protected JRPropertiesMap staticProperties;
	protected JRPropertiesMap mergedProperties;

	/**
	 *
	 */
	private Object previousOldValue;
	private Object oldValue;
	private Object value;
	private Object savedValue;


	/**
	 *
	 */
	protected JRFillField(
		JRField field, 
		JRFillObjectFactory factory
		)
	{
		factory.put(field, this);

		parent = field;

		expressionEvaluator = factory.getExpressionEvaluator();

		staticProperties = field.hasProperties() ? field.getPropertiesMap().cloneProperties() : null;
		mergedProperties = staticProperties;

		JRPropertyExpression[] fieldPropertyExpressions = field.getPropertyExpressions();
		propertyExpressions = 
			fieldPropertyExpressions == null 
			? new ArrayList<>(0)
			: new ArrayList<>(Arrays.asList(fieldPropertyExpressions));
	}


	@Override
	public String getName()
	{
		return parent.getName();
	}
		
	@Override
	public String getDescription()
	{
		return parent.getDescription();
	}
		
	@Override
	public void setDescription(String description)
	{
	}
	
	@Override
	public Class<?> getValueClass()
	{
		return parent.getValueClass();
	}
	
	@Override
	public String getValueClassName()
	{
		return parent.getValueClassName();
	}
	
	/**
	 *
	 */
	public Object getOldValue()
	{
		return oldValue;
	}
		
	/**
	 *
	 */
	public void setOldValue(Object oldValue)
	{
		this.oldValue = oldValue;
	}

	/**
	 *
	 */
	public Object getValue()
	{
		return value;
	}
		
	/**
	 *
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
		
	public Object getValue(byte evaluation)
	{
		Object returnValue;
		switch (evaluation)
		{
			case JRExpression.EVALUATION_OLD:
				returnValue = oldValue;
				break;
			default:
				returnValue = value;
				break;
		}
		return returnValue;
	}
	
	public void overwriteValue(Object newValue, byte evaluation)
	{
		switch (evaluation)
		{
			case JRExpression.EVALUATION_OLD:
				savedValue = oldValue;
				oldValue = newValue;
				break;
			default:
				savedValue = value;
				value = newValue;
				break;
		}
	}
	
	public void restoreValue(byte evaluation)
	{
		switch (evaluation)
		{
			case JRExpression.EVALUATION_OLD:
				oldValue = savedValue;
				break;
			default:
				value = savedValue;
				break;
		}
		savedValue = null;
	}


	
	public Object getPreviousOldValue()
	{
		return previousOldValue;
	}


	
	public void setPreviousOldValue(Object previousOldValue)
	{
		this.previousOldValue = previousOldValue;
	}

	
	@Override
	public boolean hasProperties()
	{
		return mergedProperties != null && mergedProperties.hasProperties();
	}


	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return mergedProperties;
	}

	
	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

	
	@Override
	public JRPropertyExpression[] getPropertyExpressions()
	{
		return propertyExpressions.toArray(new JRPropertyExpression[propertyExpressions.size()]);
	}


	/**
	 *
	 */
	protected void evaluateProperties() throws JRException
	{
		if (propertyExpressions.isEmpty())
		{
			mergedProperties = staticProperties;
		}
		else
		{
			JRPropertiesMap dynamicProperties = new JRPropertiesMap();
			
			for (JRPropertyExpression prop : propertyExpressions)
			{
				String value = (String) evaluateExpression(prop.getValueExpression());
				//if (value != null) //is the null value significant for some field properties?
				{
					dynamicProperties.setProperty(prop.getName(), value);
				}
			}
			
			mergedProperties = dynamicProperties.cloneProperties();
			mergedProperties.setBaseProperties(staticProperties);
		}
	}

	/**
	 *
	 */
	protected final Object evaluateExpression(JRExpression expression) throws JRException
	{
		return expressionEvaluator.evaluate(expression, JRExpression.EVALUATION_DEFAULT);
	}

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
