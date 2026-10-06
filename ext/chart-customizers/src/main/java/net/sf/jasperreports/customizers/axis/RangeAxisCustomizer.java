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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;

import net.sf.jasperreports.charts.JRChart;

/**
 * Customizer to define the minimum and maximum value of the range axis, works for both 
 * XY and Category plot
 * 
 * @author Marco Orlandin (dejawho2@users.sourceforge.net)
 */
public class RangeAxisCustomizer extends AbstractAxisCustomizer 
{
	@Override
	public void customize(JFreeChart jfc, JRChart jrc) 
	{
		ValueAxis valueAxis = null;

		if ((jfc.getPlot() instanceof XYPlot))
		{
			valueAxis = jfc.getXYPlot().getRangeAxis();
		}
		else if (jfc.getPlot() instanceof CategoryPlot)
		{
			valueAxis = jfc.getCategoryPlot().getRangeAxis();
		}

		if (valueAxis != null)
		{
			configValueAxis(valueAxis, PROPERTY_MIN_VALUE, PROPERTY_MAX_VALUE);

			if (valueAxis instanceof NumberAxis)
			{
				configNumberAxis((NumberAxis)valueAxis, PROPERTY_TICK_UNIT);
			}
		}
	}
}
