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
package net.sf.jasperreports.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;

/**
 * Properties map of an JR element.
 * <p/>
 * The order of the properties (obtained by {@link #getPropertyNames() getPropertyNames()}
 * is the same as the order in which the properties were added.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRPropertiesMap implements Serializable, Cloneable, JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(JRPropertiesMap.class);
	
	private Map<String, String> propertiesMap;
	private List<String> propertiesList;
	
	private JRPropertiesMap base;
	
	/**
	 * Creates a properties map.
	 */
	public JRPropertiesMap()
	{
	}
	
	/**
	 * Clones a properties map.
	 * 
	 * @param propertiesMap the original properties map
	 */
	public JRPropertiesMap(JRPropertiesMap propertiesMap)
	{
		this();
		
		this.base = propertiesMap.base;
		
		//this copies all properties from base to this instance
		//FIXME in some cases we might want to keep the properties in base
		copyFrom(propertiesMap);
	}

	private void copyFrom(JRPropertiesMap propertiesMap)
	{
		String[] propertyNames = propertiesMap.getPropertyNames();
		if (propertyNames != null && propertyNames.length > 0)
		{
			for (int i = 0; i < propertyNames.length; i++)
			{
				setProperty(propertyNames[i], propertiesMap.getProperty(propertyNames[i]));
			}
		}
	}

	protected synchronized void ensureInit()
	{
		if (propertiesMap == null)
		{
			init();
		}
	}

	private void init()
	{
		// start with small collections
		propertiesMap = new HashMap<>(4, 0.75f);
		propertiesList = new ArrayList<>(2);
	}

	
	/**
	 * Returns the names of the properties.
	 *  
	 * @return the names of the properties
	 */
	public String[] getPropertyNames()
	{
		String[] names;
		if (hasOwnProperties())
		{
			if (base == null)
			{
				names = propertiesList.toArray(new String[propertiesList.size()]);
			}
			else
			{
				LinkedHashSet<String> namesSet = new LinkedHashSet<>();
				collectPropertyNames(namesSet);
				names = namesSet.toArray(new String[namesSet.size()]);
			}
		}
		else if (base != null)
		{
			names = base.getPropertyNames();
		}
		else
		{
			names = new String[0];
		}
		return names;
	}
	
	public String[] getOwnPropertyNames()
	{
		String[] names;
		if (hasOwnProperties())
		{
			names = propertiesList.toArray(new String[propertiesList.size()]);
		}
		else
		{
			names = new String[0];
		}
		return names;
	}

	
	protected void collectPropertyNames(Collection<String> names)
	{
		if (base != null)
		{
			base.collectPropertyNames(names);
		}
		
		if (propertiesList != null)
		{
			names.addAll(propertiesList);
		}
	}


	/**
	 * Returns the value of a property.
	 * 
	 * @param propName the name of the property
	 * @return the value
	 */
	public String getProperty(String propName)
	{
		String val;
		if (hasOwnProperty(propName))
		{
			val = getOwnProperty(propName);
		}
		else if (base != null)
		{
			val = base.getProperty(propName);
		}
		else
		{
			val = null;
		}
		return val;
	}
	
	
	/**
	 * Decides whether the map contains a specified property.
	 * 
	 * The method returns true even if the property value is null.
	 * 
	 * @param propName the property name
	 * @return <code>true</code> if and only if the map contains the property
	 */
	public boolean containsProperty(String propName)
	{
		return hasOwnProperty(propName) 
				|| base != null && base.containsProperty(propName);
	}


	protected boolean hasOwnProperty(String propName)
	{
		return propertiesMap != null && propertiesMap.containsKey(propName);
	}


	protected String getOwnProperty(String propName)
	{
		return propertiesMap != null ? (String) propertiesMap.get(propName) : null;
	}

	
	/**
	 * Adds/sets a property value.
	 * 
	 * @param propName the name of the property
	 * @param value the value of the property
	 */
	public void setProperty(String propName, String value)
	{
		Object old = getOwnProperty(propName);
		
		ensureInit();
		
		if (!hasOwnProperty(propName))
		{
			propertiesList.add(propName);
		}
		propertiesMap.put(propName, value);

		if (hasEventSupport())
		{
			getEventSupport().firePropertyChange(propName, old, value);
		}
	}
	
	
	/**
	 * Removes a property.
	 * 
	 * @param propName the property name
	 */	
	public void removeProperty(String propName)
	{
		//FIXME base properties?
		if (hasOwnProperty(propName))
		{
			String old = getOwnProperty(propName);
			propertiesList.remove(propName);
			propertiesMap.remove(propName);

			if (hasEventSupport())
			{
				getEventSupport().firePropertyRemove(propName, old);
			}
		}
	}
	
	
	/**
	 * Clones this property map.
	 * 
	 * @return a clone of this property map
	 */
	public JRPropertiesMap cloneProperties()
	{
		return new JRPropertiesMap(this);
	}
	
	
	@Override
	public Object clone()
	{
		return this.cloneProperties();
	}
	
	/**
	 * Copies own properties of a different map into this object.
	 * 
	 * @param propertiesMap the map to copy properties from
	 */
	public void copyOwnProperties(JRPropertiesMap propertiesMap)
	{
		if (propertiesMap.propertiesList != null)
		{
			for (String prop : propertiesMap.propertiesList)
			{
				setProperty(prop, propertiesMap.getOwnProperty(prop));
			}
		}
	}
	
	/**
	 * Copies properties of a different map into this object.
	 * 
	 * @param propertiesMap the map to copy properties from
	 */
	public void copyProperties(JRPropertiesMap propertiesMap)
	{
		copyFrom(propertiesMap);
	}
	
	@Override
	public String toString()
	{
		return propertiesMap == null ? "" : propertiesMap.toString();
	}
	
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		if (propertiesList == null && propertiesMap != null)// an instance from an old version has been deserialized
		{
			//recreate the properties list and map
			propertiesList = new ArrayList<>(propertiesMap.keySet());
			propertiesMap = new HashMap<>(propertiesMap);
		}
	}
	
	
	/**
	 * Checks whether there are any properties.
	 * 
	 * @return whether there are any properties
	 */
	public boolean hasProperties()
	{
		return hasOwnProperties()
				|| base != null && base.hasProperties();
	}

	public boolean isEmpty()
	{
		// only checking base for null and not whether base has any properties
		return !hasOwnProperties() && base == null;
	}

	/**
	 * Checks whether this object has properties of its own
	 * (i.e. not inherited from the base properties).
	 * 
	 * @return whether this object has properties of its own
	 * @see #setBaseProperties(JRPropertiesMap)
	 */
	public boolean hasOwnProperties()
	{
		return propertiesList != null && !propertiesList.isEmpty();
	}
	
	
	/**
	 * Clones the properties map of a properties holder.
	 * If the holder does not have any properties, null is returned.
	 * 
	 * @param propertiesHolder the properties holder
	 * @return a clone of the holder's properties map, or <code>null</code>
	 * if the holder does not have any properties
	 */
	public static JRPropertiesMap getPropertiesClone(JRPropertiesHolder propertiesHolder)
	{
		JRPropertiesMap clone;
		if (propertiesHolder.hasProperties())
		{
			clone = propertiesHolder.getPropertiesMap().cloneProperties();
		}
		else
		{
			clone = null;
		}
		return clone;
	}


	/**
	 * Returns the base properties map, if any.
	 * 
	 * @return the base properties map
	 * @see #setBaseProperties(JRPropertiesMap)
	 */
	public JRPropertiesMap getBaseProperties()
	{
		return base;
	}


	/**
	 * Sets the base properties map.
	 * 
	 * <p>
	 * The base properties map are used as base/default properties for this
	 * instance.  All of the {@link #containsProperty(String)}, 
	 * {@link #getProperty(String)}, {@link #getPropertyNames()} and 
	 * {@link #hasProperties()} methods include base properties as well.
	 * </p>
	 * 
	 * @param base the base properties map
	 */
	public void setBaseProperties(JRPropertiesMap base)
	{
		this.base = base;
	}
	
	/**
	 * Loads a properties file from a location.
	 * 
	 * @param location the properties file URL
	 * @return the properties file loaded as a in-memory properties map
	 */
	public static JRPropertiesMap loadProperties(URL location)
	{
		boolean close = true;
		InputStream stream = null;
		try
		{
			stream = location.openStream();
			
			Properties props = new Properties();
			props.load(stream);
			
			close = false;
			stream.close();
			
			JRPropertiesMap properties = new JRPropertiesMap();
			for (Enumeration<?> names = props.propertyNames(); names.hasMoreElements(); )
			{
				String name = (String) names.nextElement();
				String value = props.getProperty(name);
				properties.setProperty(name, value);
			}
			return properties;
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (close && stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					if (log.isWarnEnabled())
					{
						log.warn("Error closing stream for " + location, e);
					}
				}
			}
		}
	}

	
	private transient JRPropertyChangeSupport eventSupport;
	
	protected boolean hasEventSupport()
	{
		return eventSupport != null;
	}
	
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
