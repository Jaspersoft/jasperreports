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
package net.sf.jasperreports.crosstabs.design;

import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabCell;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;

/**
 * Implementation of {@link net.sf.jasperreports.crosstabs.JRCrosstabCell JRCrosstabCell} to be used
 * for report design.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignCrosstabCell extends JRBaseCrosstabCell implements JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_COLUMN_TOTAL_GROUP = "columnTotalGroup";

	public static final String PROPERTY_CONTENTS = "contents";

	public static final String PROPERTY_HEIGHT = "height";

	public static final String PROPERTY_ROW_TOTAL_GROUP = "rowTotalGroup";

	public static final String PROPERTY_WIDTH = "width";

	
	/**
	 * Creates a crosstab data cell.
	 */
	public JRDesignCrosstabCell()
	{
		contents = new JRDesignCellContents();
	}
	
	
	/**
	 * Indicates that the cell corresponds to a total column.
	 * 
	 * @param columnTotalGroup the corresponding column group
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabCell#getColumnTotalGroup()
	 */
	public void setColumnTotalGroup(String columnTotalGroup)
	{
		Object old = this.columnTotalGroup;
		this.columnTotalGroup = columnTotalGroup;
		getEventSupport().firePropertyChange(PROPERTY_COLUMN_TOTAL_GROUP, old, this.columnTotalGroup);
	}
	
	
	/**
	 * Sets the cell contents.
	 * 
	 * @param contents the contents
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabCell#getContents()
	 */
	public void setContents(JRDesignCellContents contents)
	{
		Object old = this.contents;
		if (contents == null)
		{
			contents = new JRDesignCellContents();
		}
		this.contents = contents;
		getEventSupport().firePropertyChange(PROPERTY_CONTENTS, old, this.contents);
	}

	
	
	/**
	 * Indicates that the cell corresponds to a total row.
	 * 
	 * @param rowTotalGroup the corresponding row group
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabCell#getRowTotalGroup()
	 */
	public void setRowTotalGroup(String rowTotalGroup)
	{
		Object old = this.rowTotalGroup;
		this.rowTotalGroup = rowTotalGroup;
		getEventSupport().firePropertyChange(PROPERTY_ROW_TOTAL_GROUP, old, this.rowTotalGroup);
	}

	
	/**
	 * Sets the cell width.
	 * <p>
	 * This is compulsory for base cells only.
	 * 
	 * @param width the width
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabCell#getWidth()
	 */
	public void setWidth(Integer width)
	{
		Object old = this.width;
		this.width = width;
		getEventSupport().firePropertyChange(PROPERTY_WIDTH, old, this.width);
	}

	
	/**
	 * Sets the cell height.
	 * <p>
	 * This is compulsory for base cells only.
	 * 
	 * @param height the height
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabCell#getHeight()
	 */
	public void setHeight(Integer height)
	{
		Object old = this.height;
		this.height = height;
		getEventSupport().firePropertyChange(PROPERTY_HEIGHT, old, this.height);
	}
	
	@Override
	public Object clone()
	{
		JRDesignCrosstabCell clone = (JRDesignCrosstabCell)super.clone();
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
