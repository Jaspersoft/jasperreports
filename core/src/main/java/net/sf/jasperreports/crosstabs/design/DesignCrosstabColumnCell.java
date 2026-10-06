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

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.sf.jasperreports.crosstabs.base.BaseCrosstabColumnCell;
import net.sf.jasperreports.crosstabs.type.CrosstabColumnPositionEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DesignCrosstabColumnCell extends BaseCrosstabColumnCell implements JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_HEIGHT = "height";

	public static final String PROPERTY_CONTENTS_POSITION = "contentsPosition";

	public static final String PROPERTY_CELL_CONTENTS = "cellContents";
	
	public DesignCrosstabColumnCell()
	{
		cellContents = new JRDesignCellContents();
	}

	@JsonIgnore
	public JRDesignCellContents getDesignCellContents()
	{
		return (JRDesignCellContents) getCellContents();
	}
	
	public void setHeight(int height)
	{
		int old = this.height;
		this.height = height;
		getEventSupport().firePropertyChange(PROPERTY_HEIGHT, old, this.height);
	}

	public void setContentsPosition(CrosstabColumnPositionEnum contentsPosition)
	{
		Object old = this.contentsPosition;
		this.contentsPosition = contentsPosition;
		getEventSupport().firePropertyChange(PROPERTY_CONTENTS_POSITION, old, this.contentsPosition);
	}

	public void setCellContents(JRDesignCellContents cellContents)
	{
		Object old = this.cellContents;
		this.cellContents = cellContents;
		getEventSupport().firePropertyChange(PROPERTY_CELL_CONTENTS, old, this.cellContents);
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
