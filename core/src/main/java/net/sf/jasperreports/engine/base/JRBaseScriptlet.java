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
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseScriptlet implements JRScriptlet, Serializable, JRChangeEventsSupport
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

	protected transient Class<?> valueClass;

	/**
	 *
	 */
	protected JRPropertiesMap propertiesMap;

	private JRPropertyExpression[] propertyExpressions;


	/**
	 *
	 */
	protected JRBaseScriptlet()
	{
		propertiesMap = new JRPropertiesMap();
	}
	
	
	/**
	 *
	 */
	protected JRBaseScriptlet(JRScriptlet scriptlet, JRBaseObjectFactory factory)
	{
		factory.put(scriptlet, this);
		
		name = scriptlet.getName();
		description = scriptlet.getDescription();
		valueClassName = scriptlet.getValueClassName();
		
		propertiesMap = scriptlet.getPropertiesMap().cloneProperties();
		propertyExpressions = factory.getPropertyExpressions(scriptlet.getPropertyExpressions());
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
					valueClass = JRClassLoader.loadClassForName(className);
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
	public boolean hasProperties()
	{
		return propertiesMap != null && propertiesMap.hasProperties();
	}


	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return propertiesMap;
	}

	
	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}


	@Override
	public JRPropertyExpression[] getPropertyExpressions()
	{
		return propertyExpressions;
	}

	
	@Override
	public Object clone() 
	{
		JRBaseScriptlet clone = null;

		try
		{
			clone = (JRBaseScriptlet)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		if (propertiesMap != null)
		{
			clone.propertiesMap = (JRPropertiesMap)propertiesMap.clone();
		}
		clone.propertyExpressions = JRCloneUtils.cloneArray(propertyExpressions);
		
		clone.eventSupport = null;

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
