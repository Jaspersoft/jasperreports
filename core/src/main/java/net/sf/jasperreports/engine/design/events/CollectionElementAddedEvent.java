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

import java.beans.PropertyChangeEvent;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CollectionElementAddedEvent extends PropertyChangeEvent
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private final int addedIndex;
	
	public CollectionElementAddedEvent(Object source, String propertyName,  
			Object addedValue, int addedIndex)
	{
		super(source, propertyName, null, addedValue);
		
		this.addedIndex = addedIndex;
	}

	public Object getAddedValue()
	{
		return getNewValue();
	}
	
	public int getAddedIndex()
	{
		return addedIndex;
	}
	
}
