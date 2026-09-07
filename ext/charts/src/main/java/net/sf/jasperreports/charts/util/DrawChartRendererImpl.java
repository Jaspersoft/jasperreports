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
package net.sf.jasperreports.charts.util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.JFreeChart;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintImageAreaHyperlink;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.renderers.AbstractRenderToImageAwareRenderer;
import net.sf.jasperreports.renderers.AreaHyperlinksRenderable;
import net.sf.jasperreports.renderers.Graphics2DRenderable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DrawChartRendererImpl extends AbstractRenderToImageAwareRenderer implements AreaHyperlinksRenderable, Graphics2DRenderable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private JFreeChart chart;
	private ChartHyperlinkProvider chartHyperlinkProvider;
	private int reportDpi = JasperPrint.DEFAULT_REPORT_DPI;
	
	public DrawChartRendererImpl(JFreeChart chart, ChartHyperlinkProvider chartHyperlinkProvider)
	{
		this.chart = chart;
		this.chartHyperlinkProvider = chartHyperlinkProvider;
	}

	public DrawChartRendererImpl(JFreeChart chart, ChartHyperlinkProvider chartHyperlinkProvider, int reportDpi)
	{
		this(chart, chartHyperlinkProvider);
		this.reportDpi = reportDpi;
	}

	@Override
	public int getReportDpi()
	{
		return reportDpi;
	}

	@Override
	public void render(JasperReportsContext jasperReportsContext, Graphics2D grx, Rectangle2D rectangle) 
	{
		if (chart != null)
		{
			ChartUtil.drawChart(chart, grx, rectangle, reportDpi);
		}
	}
	
	@Override
	public List<JRPrintImageAreaHyperlink> getImageAreaHyperlinks(Rectangle2D renderingArea) throws JRException
	{
		double dpiScale = (double)reportDpi / JasperPrint.DEFAULT_REPORT_DPI;
		return ChartUtil.getImageAreaHyperlinks(
			chart, chartHyperlinkProvider, null, ChartUtil.toChartArea(renderingArea, dpiScale), dpiScale);
	}

	@Override
	public boolean hasImageAreaHyperlinks()
	{
		return chartHyperlinkProvider != null && chartHyperlinkProvider.hasHyperlinks();
	}
}
