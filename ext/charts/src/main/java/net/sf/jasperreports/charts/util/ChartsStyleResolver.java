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
package net.sf.jasperreports.charts.util;

import java.awt.Color;

import net.sf.jasperreports.charts.JRCategoryAxisFormat;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRTimeAxisFormat;
import net.sf.jasperreports.charts.JRValueAxisFormat;
import net.sf.jasperreports.charts.JRXAxisFormat;
import net.sf.jasperreports.charts.JRYAxisFormat;
import net.sf.jasperreports.engine.util.StyleResolver;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ChartsStyleResolver // extends StyleResolver
{
	private final StyleResolver parent;


	/**
	 *
	 */
	public ChartsStyleResolver(StyleResolver parent)
	{
		this.parent = parent;
	}

	/**
	 *
	 */
	public Color getForecolor(JRChartPlot plot)
	{
		JRChart chart = plot.getChart();
		if (chart != null)
		{
			return parent.getForecolor(chart);
		}
		return Color.black;
	}

	/**
	 *
	 */
	public Color getBackcolor(JRChartPlot plot)
	{
		Color ownBackcolor = plot.getOwnBackcolor();
		if (ownBackcolor != null)
		{
			return ownBackcolor;
		}
		JRChart chart = plot.getChart();
		if (chart != null)
		{
			return parent.getBackcolor(chart);
		}
		return Color.white;
	}

	/**
	 *
	 */
	public Color getTitleColor(JRChart chart)
	{
		Color ownTitleColor = chart.getOwnTitleColor();
		if (ownTitleColor != null)
		{
			return ownTitleColor;
		}
		return parent.getForecolor(chart);
	}

	/**
	 *
	 */
	public Color getSubtitleColor(JRChart chart)
	{
		Color ownSubtitleColor = chart.getOwnSubtitleColor();
		if (ownSubtitleColor != null)
		{
			return ownSubtitleColor;
		}
		return parent.getForecolor(chart);
	}

	/**
	 *
	 */
	public Color getLegendColor(JRChart chart)
	{
		Color ownLegendColor = chart.getOwnLegendColor();
		if (ownLegendColor != null)
		{
			return ownLegendColor;
		}
		return parent.getForecolor(chart);
	}

	/**
	 *
	 */
	public Color getLegendBackgroundColor(JRChart chart)
	{
		Color ownLegendBackgroundColor = chart.getOwnLegendBackgroundColor();
		if (ownLegendBackgroundColor != null)
		{
			return ownLegendBackgroundColor;
		}
		return parent.getBackcolor(chart);
	}

	/**
	 *
	 */
	public Color getCategoryAxisLabelColor(JRCategoryAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownCategoryAxisLabelColor = axisFormat.getOwnCategoryAxisLabelColor();
		if (ownCategoryAxisLabelColor != null) 
		{
			return ownCategoryAxisLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getCategoryAxisTickLabelColor(JRCategoryAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownCategoryAxisTickLabelColor = axisFormat.getOwnCategoryAxisTickLabelColor();
		if (ownCategoryAxisTickLabelColor != null)
		{
			return ownCategoryAxisTickLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getCategoryAxisLineColor(JRCategoryAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownCategoryAxisLineColor = axisFormat.getOwnCategoryAxisLineColor();
		if (ownCategoryAxisLineColor != null)
		{
			return ownCategoryAxisLineColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getValueAxisLabelColor(JRValueAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownValueAxisLabelColor = axisFormat.getOwnValueAxisLabelColor();
		if (ownValueAxisLabelColor != null)
		{
			return ownValueAxisLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getValueAxisTickLabelColor(JRValueAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownValueAxisTickLabelColor = axisFormat.getOwnValueAxisTickLabelColor();
		if (ownValueAxisTickLabelColor != null) 
		{
			return ownValueAxisTickLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getValueAxisLineColor(JRValueAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownValueAxisLineColor = axisFormat.getOwnValueAxisLineColor();
		if (ownValueAxisLineColor != null) 
		{
			return ownValueAxisLineColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getXAxisLabelColor(JRXAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownXAxisLabelColor = axisFormat.getOwnXAxisLabelColor();
		if (ownXAxisLabelColor != null) 
		{
			return ownXAxisLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getXAxisTickLabelColor(JRXAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownXAxisTickLabelColor = axisFormat.getOwnXAxisTickLabelColor();
		if (ownXAxisTickLabelColor != null) 
		{
			return ownXAxisTickLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getXAxisLineColor(JRXAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownXAxisLineColor = axisFormat.getOwnXAxisLineColor();
		if (ownXAxisLineColor != null) 
		{
			return ownXAxisLineColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getYAxisLabelColor(JRYAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownYAxisLabelColor = axisFormat.getOwnYAxisLabelColor();
		if (ownYAxisLabelColor != null)
		{
			return ownYAxisLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getYAxisTickLabelColor(JRYAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownYAxisTickLabelColor = axisFormat.getOwnYAxisTickLabelColor();
		if (ownYAxisTickLabelColor != null) 
		{
			return ownYAxisTickLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getYAxisLineColor(JRYAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownYAxisLineColor = axisFormat.getOwnYAxisLineColor();
		if (ownYAxisLineColor != null) 
		{
			return ownYAxisLineColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getTimeAxisLabelColor(JRTimeAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownTimeAxisLabelColor = axisFormat.getOwnTimeAxisLabelColor();
		if (ownTimeAxisLabelColor != null) 
		{
			return ownTimeAxisLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getTimeAxisTickLabelColor(JRTimeAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownTimeAxisTickLabelColor = axisFormat.getOwnTimeAxisTickLabelColor();
		if (ownTimeAxisTickLabelColor != null) 
		{
			return ownTimeAxisTickLabelColor;
		}
		return getForecolor(plot);
	}

	/**
	 *
	 */
	public Color getTimeAxisLineColor(JRTimeAxisFormat axisFormat, JRChartPlot plot)
	{
		Color ownTimeAxisLineColor = axisFormat.getOwnTimeAxisLineColor();
		if (ownTimeAxisLineColor != null) 
		{
			return ownTimeAxisLineColor;
		}
		return getForecolor(plot);
	}
}
