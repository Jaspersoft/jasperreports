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

import java.awt.Color;
import java.io.Serializable;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;



/**
 * An immutable representation of the formatting options for showing the
 * value of a value dataset.  Used by charts that display a single value,
 * such as a Meter or Thermometer.
 *
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRBaseValueDisplay implements JRValueDisplay, Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected JRChart chart;

	/**
	 * The color to use when writing the value.
	 */
	protected Color color;

	/**
	 * The formatting mask to use when writing the value.  Must a pattern
	 * that is accepted by a <code>java.text.DecimalFormat</code> object.
	 */
	protected String mask;

	/**
	 * The font to use when writing the value.
	 */
	protected JRFont font;


	/**
	 * Constructs a copy of an existing value format specification.
	 *
	 * @param valueDisplay the value formatting object to copy
	 * @param chart the parent chart
	 */
	public JRBaseValueDisplay(JRValueDisplay valueDisplay, JRChart chart)//FIXMECHART these two parameters are no longer used; first one is always null
	{
		this.chart = chart;
		
		if (valueDisplay != null)
		{
			color = valueDisplay.getColor();
			mask = valueDisplay.getMask();
			font = valueDisplay.getFont();
		}
	}

	/**
	 * Constructs a copy of an existing value format specification and registers
	 * any expression in the new copy with the specified factory.
	 *
	 * @param valueDisplay the value formatting object to copy
	 * @param factory the factory object to register expressions with
	 */
	public JRBaseValueDisplay(JRValueDisplay valueDisplay, ChartsBaseObjectFactory factory)
	{
		JRBaseObjectFactory parentFactory = factory.getParent();

		parentFactory.put(valueDisplay, this);

		chart = (JRChart)parentFactory.getVisitResult(valueDisplay.getChart());

		color = valueDisplay.getColor();
		mask = valueDisplay.getMask();
		font = parentFactory.getFont(chart, valueDisplay.getFont());
	}


	@Override
	public JRChart getChart()
	{
		return chart;
	}

	@Override
	public Color getColor()
	{
		return color;
	}
	@Override
	public String getMask()
	{
		return mask;
	}
	@Override
	public JRFont getFont()
	{
		return font;
	}

	/**
	 * Adds all the expression used by this plot with the specified collector.
	 * All collected expression that are also registered with a factory will
	 * be included with the report is compiled.
	 *
	 * @param collector the expression collector to use
	 */
	public void collectExpressions(JRExpressionCollector collector)
	{
	}

	@Override
	public Object clone() 
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public JRBaseValueDisplay clone(JRChart parentChart)
	{
		JRBaseValueDisplay clone = (JRBaseValueDisplay) clone();
		clone.chart = parentChart;
		return clone;
	}
}
