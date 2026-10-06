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
package net.sf.jasperreports.engine.design.events;

import java.beans.PropertyChangeSupport;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRPropertyChangeSupport extends PropertyChangeSupport
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private Object sourceBean;
	
	public JRPropertyChangeSupport(Object sourceBean)
	{
		super(sourceBean);
		
		this.sourceBean = sourceBean;
	}

	public void fireCollectionElementAddedEvent(String propertyName, Object addedValue, int addedIndex)
	{
		firePropertyChange(new CollectionElementAddedEvent(sourceBean, propertyName, 
				addedValue, addedIndex));
	}

	public void fireCollectionElementRemovedEvent(String propertyName, Object removedValue, int removedIndex)
	{
		firePropertyChange(new CollectionElementRemovedEvent(sourceBean, propertyName, 
				removedValue, removedIndex));
	}
	
	public void firePropertyChange(String propertyName, float oldValue, float newValue)
	{
		if (oldValue == newValue)
		{
			return;
		}
		
		firePropertyChange(propertyName, (Float)oldValue, (Float)newValue);
	}
	
	public void firePropertyChange(String propertyName, double oldValue, double newValue)
	{
		if (oldValue == newValue)
		{
			return;
		}
		
		firePropertyChange(propertyName, (Double)oldValue, (Double)newValue);
	}
	
	public void firePropertyRemove(String propertyName, String oldValue)
	{
		firePropertyChange(new PropertyRemoveEvent(sourceBean, propertyName, oldValue));
	}
	
}
