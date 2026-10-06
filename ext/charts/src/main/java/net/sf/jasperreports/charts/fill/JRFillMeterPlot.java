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
package net.sf.jasperreports.charts.fill;

import java.awt.Color;
import java.util.List;

import net.sf.jasperreports.charts.JRDataRange;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.charts.type.MeterShapeEnum;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRFont;

/**
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRFillMeterPlot extends JRFillChartPlot implements JRMeterPlot
{

	/**
	 *
	 */
	public JRFillMeterPlot(JRMeterPlot meterPlot, ChartsFillObjectFactory factory)
	{
		super(meterPlot, factory);
	}

	@Override
	public JRDataRange getDataRange()
	{
		return ((JRMeterPlot)parent).getDataRange();
	}

	@Override
	public JRValueDisplay getValueDisplay()
	{
		return ((JRMeterPlot)parent).getValueDisplay();
	}

	@Override
	public MeterShapeEnum getShape()
	{
		return ((JRMeterPlot)parent).getShape();
	}

	@Override
	public List<JRMeterInterval> getIntervals(){
		return ((JRMeterPlot)parent).getIntervals();
	}

	@Override
	public Integer getMeterAngle()
	{
		return ((JRMeterPlot)parent).getMeterAngle();
	}

	@Override
	public String getUnits()
	{
		return ((JRMeterPlot)parent).getUnits();
	}

	@Override
	public Double getTickInterval()
	{
		return ((JRMeterPlot)parent).getTickInterval();
	}

	@Override
	public Color getMeterBackgroundColor()
	{
		return ((JRMeterPlot)parent).getMeterBackgroundColor();
	}

	@Override
	public Color getNeedleColor()
	{
		return ((JRMeterPlot)parent).getNeedleColor();
	}

	@Override
	public Color getTickColor()
	{
		return ((JRMeterPlot)parent).getTickColor();
	}
	
	@Override
	public Integer getTickCount()
	{
		return ((JRMeterPlot)parent).getTickCount();
	}

	@Override
	public JRFont getTickLabelFont()
	{
		return ((JRMeterPlot)parent).getTickLabelFont();
	}
}
