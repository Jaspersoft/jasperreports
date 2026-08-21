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
package net.sf.jasperreports.engine.analytics.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.analytics.data.Axis;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseMultiAxisData implements MultiAxisData, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	public static final String EXCEPTION_MESSAGE_KEY_DATA_AXIS_LEVEL_NOT_SPECIFIED = "engine.analytics.dataset.data.axis.level.not.specified";
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_AXIS = "engine.analytics.dataset.duplicate.axis";

	protected MultiAxisDataset dataset;
	private DataAxis[] axes = new DataAxis[Axis.axisCount()];
	private List<DataAxis> axisList;
	protected List<DataMeasure> measures;
	
	public BaseMultiAxisData()
	{
		this.axisList = new ArrayList<>();
		this.measures = new ArrayList<>();
	}
	
	public BaseMultiAxisData(MultiAxisData data, JRBaseObjectFactory factory)
	{
		factory.put(data, this);
		
		this.dataset = factory.getMultiAxisDataset(data.getDataset());
		
		List<DataAxis> dataAxes = data.getDataAxisList();
		this.axisList = new ArrayList<>(dataAxes.size());
		for (DataAxis dataAxis : dataAxes)
		{
			DataAxis axis = factory.getDataAxis(dataAxis);
			this.axisList.add(axis);
			this.axes[axis.getAxis().ordinal()] = axis;
		}
		
		List<DataMeasure> dataMeasures = data.getMeasures();
		this.measures = 
			dataMeasures.stream().map(factory::getDataMeasure)
				.collect(Collectors.toCollection(() -> new ArrayList<>(dataMeasures.size())));
	}
	
	protected void addDataAxis(DataAxis axis)
	{
		if (axis.getAxis() == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_DATA_AXIS_LEVEL_NOT_SPECIFIED,
					(Object[])null);
		}
		
		int axisIndex = axis.getAxis().ordinal();
		DataAxis existingAxis = axes[axisIndex];
		if (existingAxis == null)
		{
			axes[axisIndex] = axis;
			axisList.add(axis);
		}
		else if (existingAxis != axis)// testing for object identity
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_AXIS,
					new Object[]{axis.getAxis()});
		}
	}
	
	protected int removeDataAxis(Axis axis)
	{
		int axisIndex = axis.ordinal();
		if (axes[axisIndex] == null)
		{
			return -1;
		}
		
		int listIndex = axisList.indexOf(axes[axisIndex]);
		if (listIndex >= 0)
		{
			axisList.remove(listIndex);
		}
		
		axes[axisIndex] = null;
		
		return listIndex;
	}
	
	@Override
	public MultiAxisDataset getDataset()
	{
		return dataset;
	}

	@Override
	public List<DataAxis> getDataAxisList()
	{
		return axisList;
	}
	
	@JsonSetter
	private void setDataAxisList(List<DataAxis> axisList)
	{
		if (axisList != null)
		{
			for (DataAxis axis : axisList)
			{
				addDataAxis(axis);
			}
		}
	}
	
	@Override
	public DataAxis getDataAxis(Axis axis)
	{
		return axes[axis.ordinal()];
	}

	@Override
	public List<DataMeasure> getMeasures()
	{
		return measures;
	}
	
	@Override
	public Object clone() 
	{
		BaseMultiAxisData clone = null;
		try
		{
			clone = (BaseMultiAxisData) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		
		clone.dataset = JRCloneUtils.nullSafeClone(dataset);
		
		clone.axisList = JRCloneUtils.cloneList(axisList);
		clone.axes = new DataAxis[Axis.axisCount()];
		for (DataAxis axis : clone.axisList)
		{
			clone.axes[axis.getAxis().ordinal()] = axis;
		}
		
		clone.measures = JRCloneUtils.cloneList(measures);
		return clone;
	}
	
}
