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
import net.sf.jasperreports.charts.JRItemLabel;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;

/**
 * An immutable representation of the formatting options for showing the
 * item labels in a category plot.
 *
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class JRBaseItemLabel implements JRItemLabel, Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected JRChart chart;

	/**
	 * The item label text color.
	 */
	protected Color color;

	/**
	 * The item label background color.
	 */
	protected Color backgroundColor;

	/**
	 * The formatting mask to use when writing the value.  Must a pattern
	 * that is accepted by a code>java.text.DecimalFormat</code> object.
	 */
//	protected String mask;

	/**
	 * The item label text font.
	 */
	protected JRFont font;


	/**
	 * Constructs a copy of an existing item label specification.
	 *
	 * @param itemLabel the item label object to copy
	 * @param chart the parent chart
	 */
	public JRBaseItemLabel(JRItemLabel itemLabel, JRChart chart)
	{
		this.chart = chart;
		
		if (itemLabel != null)
		{
			color = itemLabel.getColor();
			backgroundColor = itemLabel.getBackgroundColor();
//			mask = itemLabel.getMask();
			font = itemLabel.getFont();
		}
	}

	/**
	 * Constructs a copy of an existing item label specification and registers
	 * any expression in the new copy with the specified factory.
	 *
	 * @param itemLabel the item label object to copy
	 * @param factory the factory object to register expressions with
	 */
	public JRBaseItemLabel(JRItemLabel itemLabel, ChartsBaseObjectFactory factory)
	{
		JRBaseObjectFactory parentFactory = factory.getParent();

		parentFactory.put(itemLabel, this);

		chart = (JRChart)parentFactory.getVisitResult(itemLabel.getChart());

		color = itemLabel.getColor();
		backgroundColor = itemLabel.getBackgroundColor();
//		mask = itemLabel.getMask();
		font = parentFactory.getFont(chart, itemLabel.getFont());
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
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}
	/**
	 *
	 */
//	public String getMask()
//	{
//		return mask;
//	}
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
	public JRItemLabel clone(JRChart parentChart)
	{
		JRBaseItemLabel clone = (JRBaseItemLabel) clone();
		clone.chart = parentChart;
		return clone;
	}
}
