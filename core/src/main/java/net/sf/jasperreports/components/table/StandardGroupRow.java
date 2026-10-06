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
package net.sf.jasperreports.components.table;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StandardGroupRow implements GroupRow, Serializable, JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_GROUP_NAME = "groupName";
	public static final String PROPERTY_ROW = "row";
	
	private String groupName;
	private Row row;
	
	public StandardGroupRow()
	{
	}
	
	public StandardGroupRow(String groupName, Row row)
	{
		this.groupName = groupName;
		this.row = row;
	}

	public StandardGroupRow(GroupRow groupRow, RowFactory rowFactory)
	{
		this.groupName = groupRow.getGroupName();
		this.row = rowFactory.createRow(groupRow.getRow());
	}

	@Override
	public Row getRow()
	{
		return row;
	}

	@Override
	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		Object old = this.groupName;
		this.groupName = groupName;
		getEventSupport().firePropertyChange(PROPERTY_GROUP_NAME, 
				old, this.groupName);
	}

	public void setRow(Row row)
	{
		Object old = this.row;
		this.row = row;
		getEventSupport().firePropertyChange(PROPERTY_ROW, old, this.row);
	}

	@Override
	public Object clone()
	{
		StandardGroupRow clone = null;
		try
		{
			clone = (StandardGroupRow) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		clone.row = JRCloneUtils.nullSafeClone(row);
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
