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
package net.sf.jasperreports.engine.analytics.dataset;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.analytics.data.Axis;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DesignDataAxis extends BaseDataAxis implements JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_AXIS = "axis";
	public static final String PROPERTY_LEVELS = "levels";

	public DesignDataAxis()
	{
	}

	public void setAxis(Axis axis)
	{
		Object old = this.axis;
		this.axis = axis;
		getEventSupport().firePropertyChange(PROPERTY_AXIS, old, this.axis);
	}

	public void addLevel(DataAxisLevel level)
	{
		levels.add(level);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_LEVELS, level, levels.size() - 1);
	}
	
	public void removeLevel(DataAxisLevel level)
	{
		int idx = levels.indexOf(level);
		if (idx >= 0)
		{
			levels.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_LEVELS, levels, idx);
		}
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
