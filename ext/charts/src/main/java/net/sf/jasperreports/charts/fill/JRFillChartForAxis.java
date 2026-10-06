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

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillChartForAxis extends JRFillChart
{
	/**
	 *
	 */
	protected JRChart parentChart;

	/**
	 *
	 */
	protected JRFillChartForAxis(
		JRFillChart fillChart,
		ChartsFillObjectFactory factory,
		JRChart parentChart
		)
	{
		super(fillChart.getFiller(), fillChart, factory);
		
		this.parentChart = parentChart;
	}

	@Override
	public Color getBackcolor()
	{
		return parentChart.getBackcolor();
	}

	@Override
	public Boolean getShowLegend()
	{
		return parentChart.getShowLegend();
	}

	@Override
	public String getRenderType()
	{
		return parentChart.getRenderType();
	}

	@Override
	public String getTheme()
	{
		return parentChart.getTheme();
	}

	@Override
	public JRFont getTitleFont()
	{
		return parentChart.getTitleFont();
	}

	@Override
	public EdgeEnum getTitlePosition()
	{
		return parentChart.getTitlePosition();
	}

	@Override
	public Color getTitleColor()
	{
		return parentChart.getTitleColor();
	}

	@Override
	public JRFont getSubtitleFont()
	{
		return parentChart.getSubtitleFont();
	}

	@Override
	public Color getSubtitleColor()
	{
		return parentChart.getSubtitleColor();
	}

	@Override
	public Color getLegendColor()
	{
		return parentChart.getLegendColor();
	}

	@Override
	public Color getLegendBackgroundColor()
	{
		return parentChart.getLegendBackgroundColor();
	}

	@Override
	public JRFont getLegendFont()
	{
		return parentChart.getLegendFont();
	}

	@Override
	public EdgeEnum getLegendPosition()
	{
		return parentChart.getLegendPosition();
	}

	@Override
	public JRExpression getTitleExpression()
	{
		return parentChart.getTitleExpression();
	}

	@Override
	public JRExpression getSubtitleExpression()
	{
		return parentChart.getSubtitleExpression();
	}
}
