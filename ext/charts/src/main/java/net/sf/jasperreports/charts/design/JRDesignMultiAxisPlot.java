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
package net.sf.jasperreports.charts.design;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartAxis;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.base.JRBaseMultiAxisPlot;
import net.sf.jasperreports.engine.JRConstants;

/**
 * Contains information on how a multiple axis chart should be
 * displayed.  This information overrides the display information
 * for all the charts sharing the single domain axis in the multiple
 * axis chart.
 *
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRDesignMultiAxisPlot extends JRBaseMultiAxisPlot
{
	
	public static final String PROPERTY_CHART = "chart";
	
	public static final String PROPERTY_AXES = "axes";
	 
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;


	@JsonCreator
	private JRDesignMultiAxisPlot()
	{
		this(null, null);
	}

	/**
	 * Constructs a new plot by copying an existing one.
	 *
	 * @param multiAxisPlot the plot to copy
	 */
	public JRDesignMultiAxisPlot(JRChartPlot multiAxisPlot, JRChart chart)
	{
		super(multiAxisPlot, chart);
	}

	@JsonSetter
	private void setAxes(List<JRChartAxis> axes)
	{
		if (axes != null)
		{
			for (JRChartAxis axis : axes)
			{
				addAxis(axis);
			}
		}
	}

	/**
	 * Adds an axis to the plot.  The axis contains the complete information on
	 * the data and rendering to use as well as where to draw the axis.
	 *
	 * @param axis the axis to add to the plot
	 */
	public void addAxis(JRChartAxis axis)
	{
		axes.add(axis);
		if (axes.size() == 1)
		{
			((JRDesignChart) getChart()).setDataset(axis.getChart().getDataset());
		}
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_AXES, axis, axes.size() - 1);
	}
	
	/**
	 * Adds an axis to the plot.  The axis contains the complete information on
	 * the data and rendering to use as well as where to draw the axis.
	 *
	 * @param axis the axis to add to the plot
	 */
	public void addAxis(int index, JRChartAxis axis)
	{
		axes.add(index, axis);
		if (axes.size() == 1)
		{
			((JRDesignChart) getChart()).setDataset(axis.getChart().getDataset());
		}
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_AXES, axis, index);
	}

	/**
	 *
	 */
	public JRChartAxis removeAxis(JRChartAxis axis)
	{
		if (axis != null)
		{
			int idx = axes.indexOf(axis);
			if (idx >= 0)
			{
				axes.remove(idx);
				((JRDesignChart) getChart()).setDataset(axis.getChart().getDataset());
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_AXES, axis, idx);
			}
		}
		
		return axis;
	}
	/**
	 * Removes all the axes from the plot.
	 */
	public void clearAxes()
	{
		List<JRChartAxis> tmpList =  new ArrayList<>(axes);
		for (Iterator<JRChartAxis> it = tmpList.iterator(); it.hasNext();){
			removeAxis(it.next());
		}
		((JRDesignChart) getChart()).setDataset(null);
	}
//
//	/**
//	 * Returns the definition of the multiple axis chart.  This is separate
//	 * from and distinct that the definition of the nested charts.
//	 *
//	 * @return the chart object for this plot
//	 */
//	public JRChart getChart()
//	{
//			return chart;
//	}

	/**
	 * Sets the chart object that this plot belongs to.  The chart object defines
	 * all the information about the multiple axis chart.
	 *
	 * @param chart the chart that this plot belongs to
	 */
	public void setChart(JRDesignChart chart)
	{
		Object old = this.chart;
		this.chart = chart;
		getEventSupport().firePropertyChange(PROPERTY_CHART, old, this.chart);
	}
}
