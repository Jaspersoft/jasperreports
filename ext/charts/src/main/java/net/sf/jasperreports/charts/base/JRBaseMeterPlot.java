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
package net.sf.jasperreports.charts.base;

import java.awt.Color;
import java.util.List;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRDataRange;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.charts.type.MeterShapeEnum;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * An immutable representation of the layout of a Meter chart.
 *
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRBaseMeterPlot extends JRBaseChartPlot implements JRMeterPlot
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * The range displayed by the Meter.
	 */
	protected JRDataRange dataRange;

	/**
	 * Formatting information for the textual display of the value.
	 */
	protected JRValueDisplay valueDisplay;

	/**
	 * The shape to use when drawing the Meter. Only applied if the meter is
	 * over 180 degrees wide and less than a full circle.
	 */
	protected MeterShapeEnum shape;


	/**
	 * The defined intervals for the Meter.  Each interval indicates a
	 * subsection of the meter and a color to use for that section.
	 */
	protected List<JRMeterInterval> intervals = new java.util.ArrayList<>();

	/**
	 * The extend of the meter face in degrees.  It will always be centered
	 * around the straight up position.
	 */
	protected Integer meterAngle;

	/**
	 * Optional description of what the meter is displaying.  It will be
	 * appended to the textual representation of the value.
	 */
	protected String units;

	/**
	 * How often to draw ticks around the face of the meter.  The interval
	 * is relative to the meter range - if the meter displays 100 to 200 and
	 * the tickInterval is 20, there will be 4 ticks at 120, 140, 160 and 180.
	 */
	protected Double tickInterval;

	/**
	 * The color to use for the face of the meter.
	 */
	protected Color meterBackgroundColor;

	/**
	 * The color to use for the pointer on the meter.
	 */
	protected Color needleColor;

	/**
	 * The color to use for each tick on the face of the meter.
	 */
	protected Color tickColor;
	
	/**
	 * The number of major ticks on the meter scale.
	 */
	protected Integer tickCount;

	/**
	 * The font to use when writing tick labels.
	 */
	protected JRFont tickLabelFont;


	/**
	 * Constructs a copy of an existing meter.
	 *
	 * @param plot the plot to copy
	 */
	public JRBaseMeterPlot(JRChartPlot plot, JRChart chart)
	{
		super(plot, chart);
		
		JRMeterPlot meterPlot = plot instanceof JRMeterPlot ? (JRMeterPlot)plot : null;
		if (meterPlot == null)
		{
			valueDisplay = new JRBaseValueDisplay(null, chart);
		}
		else
		{
			valueDisplay = new JRBaseValueDisplay(meterPlot.getValueDisplay(), chart);
			tickLabelFont = meterPlot.getTickLabelFont();
		}
	}

	/**
	 * Constructs a copy of an existing meter and registers all expressions
	 * maintained by the meter plot with a factory.
	 *
	 * @param meterPlot the meter to copy
	 * @param factory the factory to register expressions with
	 */
	public JRBaseMeterPlot(JRMeterPlot meterPlot, ChartsBaseObjectFactory factory)
	{
		super(meterPlot, factory);

		JRBaseObjectFactory parentFactory = factory.getParent();

		dataRange = new JRBaseDataRange(meterPlot.getDataRange(), factory);
		valueDisplay = new JRBaseValueDisplay(meterPlot.getValueDisplay(), factory);
		shape = meterPlot.getShape();
		List<JRMeterInterval> origIntervals = meterPlot.getIntervals();
		intervals.clear();
		if (origIntervals != null)
		{
			for (JRMeterInterval interval : origIntervals)
			{
				intervals.add(new JRMeterInterval(interval, factory));
			}
		}

		meterAngle = meterPlot.getMeterAngle();
		units = meterPlot.getUnits();
		tickInterval = meterPlot.getTickInterval();
		tickCount = meterPlot.getTickCount();
		meterBackgroundColor = meterPlot.getMeterBackgroundColor();
		needleColor = meterPlot.getNeedleColor();
		tickColor = meterPlot.getTickColor();
		tickLabelFont = parentFactory.getFont(this.getChart(), meterPlot.getTickLabelFont());
	}

	@Override
	public JRDataRange getDataRange()
	{
		return dataRange;
	}

	@Override
	public JRValueDisplay getValueDisplay()
	{
		return valueDisplay;
	}

	@Override
	public MeterShapeEnum getShape()
	{
		return shape;
	}

	@Override
	public List<JRMeterInterval> getIntervals(){
		return intervals;
	}

	@Override
	public Integer getMeterAngle()
	{
		return meterAngle;
	}

	@Override
	public String getUnits()
	{
		return units;
	}

	@Override
	public Double getTickInterval()
	{
		return tickInterval;
	}

	@Override
	public Color getMeterBackgroundColor()
	{
		return meterBackgroundColor;
	}

	@Override
	public Color getNeedleColor()
	{
		return needleColor;
	}

	@Override
	public Color getTickColor()
	{
		return tickColor;
	}

	@Override
	public Integer getTickCount()
	{
		return tickCount;
	}

	@Override
	public JRFont getTickLabelFont()
	{
		return tickLabelFont;
	}
	
	/**
	 * Adds all the expression used by this plot with the specified collector.
	 * All collected expression that are also registered with a factory will
	 * be included with the report is compiled.
	 *
	 * @param collector the expression collector to use
	 */
	@Override
	public void collectExpressions(ChartsExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public Object clone(JRChart parentChart) 
	{
		JRBaseMeterPlot clone = (JRBaseMeterPlot)super.clone(parentChart);
		clone.dataRange = JRCloneUtils.nullSafeClone(dataRange);
		clone.valueDisplay = valueDisplay == null ? null : valueDisplay.clone(parentChart);
		clone.intervals = JRCloneUtils.cloneList(intervals);
		return clone;
	}
}
