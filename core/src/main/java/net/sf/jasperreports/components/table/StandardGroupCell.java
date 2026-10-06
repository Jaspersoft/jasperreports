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
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardGroupCell implements GroupCell, Serializable,
		JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_GROUP_NAME = "groupName";
	public static final String PROPERTY_CELL = "cell";
	
	private String groupName;
	private Cell cell;
	
	public StandardGroupCell()
	{
	}
	
	public StandardGroupCell(String groupName, Cell cell)
	{
		this.groupName = groupName;
		this.cell = cell;
	}

	public StandardGroupCell(GroupCell groupCell, ColumnFactory columnFactory)
	{
		this.groupName = groupCell.getGroupName();
		this.cell = columnFactory.createCell(groupCell.getCell());
	}

	@Override
	public Cell getCell()
	{
		return cell;
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

	public void setCell(Cell cell)
	{
		Object old = this.cell;
		this.cell = cell;
		getEventSupport().firePropertyChange(PROPERTY_CELL, old, this.cell);
	}

	@Override
	public Object clone()
	{
		StandardGroupCell clone = null;
		try
		{
			clone = (StandardGroupCell) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		clone.cell = JRCloneUtils.nullSafeClone(cell);
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
