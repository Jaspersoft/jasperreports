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
import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.JFreeChart;

import net.sf.jasperreports.components.charts.AbstractChartCustomizer;
import net.sf.jasperreports.components.charts.ChartComponent;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class SpiderChartCustomizer extends AbstractChartCustomizer
{
	@Override
	public void customize(JFreeChart chart, ChartComponent chartComponent)
	{
		chart.getPlot().setOutlineVisible(true);
		chart.getPlot().setOutlinePaint(new Color(0,0,255));
		chart.getPlot().setOutlineStroke(new BasicStroke(1f));
	}
}