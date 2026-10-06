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
package net.sf.jasperreports.charts.base;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartAxis;
import net.sf.jasperreports.charts.JRMultiAxisPlot;
import net.sf.jasperreports.charts.type.AxisPositionEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * Base read-only implementation of {@link JRChartAxis JRChartAxis}.
 *
 * @author Barry Klawans (barry@users.sourceforge.net)
 */
public class JRBaseChartAxis implements JRChartAxis, Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * Where to position the axis.
	 */
	protected AxisPositionEnum position;

	protected JRMultiAxisPlot multiAxisPlot;

	/**
	 * The Chart object containing the dataset and plot to use with this axis.
	 */
	protected JRChart chart;

	/**
	 * Construct a new axis that will be added to the specified chart.
	 */
	public JRBaseChartAxis()
	{
	}

	public JRBaseChartAxis(JRChartAxis axis, ChartsBaseObjectFactory factory)
	{
		JRBaseObjectFactory parentFactory = factory.getParent();
		parentFactory.put(axis, this);

		this.position = axis.getPosition();
		this.multiAxisPlot = (JRMultiAxisPlot)factory.getMultiAxisPlot(axis.getMultiAxisPlot());
		this.chart = (JRChart)parentFactory.getVisitResult(axis.getChart());
	}

	/**
	 * Returns the position of this axis.
	 *
	 * @return the position of this axis
	 */
	@Override
	public AxisPositionEnum getPosition()
	{
		return position;
	}
	
	@Override
	public JRMultiAxisPlot getMultiAxisPlot()
	{
		return multiAxisPlot;
	}
	
	@JsonSetter
	private void setMultiAxisPlot(JRMultiAxisPlot multiAxisPlot)
	{
		this.multiAxisPlot = multiAxisPlot;
	}

	/**
	 * Returns the chart that contains the dataset and plot to use for this
	 * axis.  The plot is used to figure out how to render the dataset when
	 * adding to the multiple axis chart.
	 *
	 * @return the chart that contains the dataset and plot for this axis
	 */
	@Override
	public JRChart getChart()
	{
		return chart;
	}
	
	@Override
	public Object clone() 
	{
		JRBaseChartAxis clone = null;

		try
		{
			clone = (JRBaseChartAxis)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		clone.chart = JRCloneUtils.nullSafeClone(chart);
		
		return clone;
	}

	@Override
	public JRChartAxis clone(JRMultiAxisPlot multiAxisPlot)
	{
		JRBaseChartAxis clone = (JRBaseChartAxis) clone();
		clone.multiAxisPlot = multiAxisPlot;
		return clone;
	}
}
