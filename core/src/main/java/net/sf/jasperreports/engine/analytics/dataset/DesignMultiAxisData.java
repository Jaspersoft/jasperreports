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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DesignMultiAxisData extends BaseMultiAxisData implements JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_DATASET = "dataset";
	public static final String PROPERTY_AXIS_LIST = "axisList";
	public static final String PROPERTY_MEASURES = "measures";

	public DesignMultiAxisData()
	{
	}
	
	public void setDataset(MultiAxisDataset dataset)
	{
		Object old = this.dataset;
		this.dataset = dataset;
		getEventSupport().firePropertyChange(PROPERTY_DATASET, old, this.dataset);
	}
	
	@Override
	public void addDataAxis(DataAxis axis)
	{
		super.addDataAxis(axis);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_AXIS_LIST, axis, getDataAxisList().size() - 1);
	}
	
	public void removeDataAxis(DataAxis axis)
	{
		int idx = removeDataAxis(axis.getAxis());
		if (idx >= 0)
		{
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_AXIS_LIST, getDataAxisList(), idx);
		}
	}
	
	@JsonSetter
	private void setMeasures(List<DataMeasure> measures)
	{
		if (measures != null)
		{
			for (DataMeasure measure : measures)
			{
				addMeasure(measure);
			}
		}
	}
	public void addMeasure(DataMeasure measure)
	{
		measures.add(measure);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_MEASURES, measure, measures.size() - 1);
	}
	
	public void removeMeasure(DataMeasure measure)
	{
		int idx = measures.indexOf(measure);
		if (idx >= 0)
		{
			measures.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_MEASURES, measure, idx);
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
