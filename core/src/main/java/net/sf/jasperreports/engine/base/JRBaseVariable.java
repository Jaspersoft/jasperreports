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

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.util.CloneStore;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.util.StoreCloneable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseVariable implements JRVariable, Serializable, StoreCloneable<JRBaseVariable>, JRChangeEventsSupport
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_DESCRIPTION = "description";

	/**
	 *
	 */
	protected String name;
	protected String description;
	protected String valueClassName = java.lang.String.class.getName();
	protected String valueClassRealName;
	protected String incrementerFactoryClassName;
	protected String incrementerFactoryClassRealName;
	protected ResetTypeEnum resetType;
	protected IncrementTypeEnum incrementType;
	protected CalculationEnum calculation;
	protected boolean isSystemDefined;

	protected transient Class<?> valueClass;
	protected transient Class<?> incrementerFactoryClass;

	/**
	 *
	 */
	protected JRExpression expression;
	protected JRExpression initialValueExpression;
	protected String resetGroup;
	protected String incrementGroup;


	/**
	 *
	 */
	protected JRBaseVariable()
	{
	}
	
	
	/**
	 *
	 */
	protected JRBaseVariable(JRVariable variable, JRBaseObjectFactory factory)
	{
		factory.put(variable, this);
		
		name = variable.getName();
		description = variable.getDescription();
		valueClassName = variable.getValueClassName();
		incrementerFactoryClassName = variable.getIncrementerFactoryClassName();
		resetType = variable.getResetType();
		incrementType = variable.getIncrementType();
		calculation = variable.getCalculation();
		isSystemDefined = variable.isSystemDefined();
		
		expression = factory.getExpression(variable.getExpression());
		initialValueExpression = factory.getExpression(variable.getInitialValueExpression());

		resetGroup = variable.getResetGroup();
		incrementGroup = variable.getIncrementGroup();
	}
		

	@Override
	public String getName()
	{
		return this.name;
	}
		
	@Override
	public String getDescription()
	{
		return this.description;
	}
		
	@Override
	public void setDescription(String description)
	{
		Object old = this.description;
		this.description = description;
		getEventSupport().firePropertyChange(PROPERTY_DESCRIPTION, old, this.description);
	}
	
	@Override
	public Class<?> getValueClass()
	{
		if (valueClass == null)
		{
			String className = getValueClassRealName();
			if (className != null)
			{
				try
				{
					valueClass = JRClassLoader.resolveClassForName(className);
				}
				catch(ClassNotFoundException e)
				{
					throw new JRRuntimeException(e);
				}
			}
		}
		
		return valueClass;
	}
		
	@Override
	public String getValueClassName()
	{
		return valueClassName;
	}

	/**
	 *
	 */
	private String getValueClassRealName()
	{
		if (valueClassRealName == null)
		{
			valueClassRealName = JRClassLoader.getClassRealName(valueClassName);
		}
		
		return valueClassRealName;
	}

	@Override
	public Class<?> getIncrementerFactoryClass()
	{
		if (incrementerFactoryClass == null)
		{
			String className = getIncrementerFactoryClassRealName();
			if (className != null)
			{
				try
				{
					incrementerFactoryClass = JRClassLoader.resolveClassForName(className);
				}
				catch(ClassNotFoundException e)
				{
					throw new JRRuntimeException(e);
				}
			}
		}
		
		return incrementerFactoryClass;
	}
		
	@Override
	public String getIncrementerFactoryClassName()
	{
		return incrementerFactoryClassName;
	}

	/**
	 *
	 */
	private String getIncrementerFactoryClassRealName()
	{
		if (incrementerFactoryClassRealName == null)
		{
			incrementerFactoryClassRealName = JRClassLoader.getClassRealName(incrementerFactoryClassName);
		}
		
		return incrementerFactoryClassRealName;
	}

	@Override
	public ResetTypeEnum getResetType()
	{
		return this.resetType;
	}
		
	@Override
	public IncrementTypeEnum getIncrementType()
	{
		return this.incrementType;
	}
		
	@Override
	public CalculationEnum getCalculation()
	{
		return this.calculation;
	}

	@Override
	public boolean isSystemDefined()
	{
		return this.isSystemDefined;
	}

	@Override
	public JRExpression getExpression()
	{
		return this.expression;
	}
		
	@Override
	public JRExpression getInitialValueExpression()
	{
		return this.initialValueExpression;
	}
		
	@Override
	public String getResetGroup()
	{
		return this.resetGroup;
	}
		
	@Override
	public String getIncrementGroup()
	{
		return this.incrementGroup;
	}

	@Override
	public Object clone() 
	{
		JRBaseVariable clone = null;
		
		try
		{
			clone = (JRBaseVariable)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.expression = JRCloneUtils.nullSafeClone(expression);
		clone.initialValueExpression = JRCloneUtils.nullSafeClone(initialValueExpression);
		clone.eventSupport = null;
		
		return clone;
	}

	@Override
	public JRBaseVariable clone(CloneStore cloneStore)
	{
		JRBaseVariable clone = (JRBaseVariable) clone();
		//early store for circular dependencies
		cloneStore.store(this, clone);
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
}
