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
package net.sf.jasperreports.customizers.axis;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;

import net.sf.jasperreports.charts.JRAbstractChartCustomizer;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractAxisCustomizer extends JRAbstractChartCustomizer 
{
	public static final String PROPERTY_MIN_VALUE = "minValue";
	public static final String PROPERTY_MAX_VALUE = "maxValue";
	public static final String PROPERTY_TICK_UNIT = "tickUnit";


	protected void configValueAxis(
		ValueAxis valueAxis,
		String minProp, 
		String maxProp
		) 
	{
		if (valueAxis != null)
		{
			Double rangeMin = getDoubleProperty(minProp);
			Double rangeMax = getDoubleProperty(maxProp);
			if (
				rangeMin != null
				|| rangeMax != null
				)
			{
				valueAxis.setRange(
					rangeMin == null ? valueAxis.getRange().getLowerBound() : rangeMin, 
					rangeMax == null ? valueAxis.getRange().getUpperBound() : rangeMax
					);
			}
		}
	}

	protected void configNumberAxis(
		NumberAxis numberAxis,
		String tickUnitProp
		) 
	{
		if (numberAxis != null)
		{
			Double tickUnit = getDoubleProperty(tickUnitProp);
			if (tickUnit != null)
			{
				numberAxis.setTickUnit(new NumberTickUnit(tickUnit));
			}
		}
	}
}
