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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.charts.convert;

import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;

import net.sf.jasperreports.charts.ChartContext;
import net.sf.jasperreports.charts.ChartTheme;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.util.ChartUtil;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.convert.ElementConverter;
import net.sf.jasperreports.engine.convert.ReportConverter;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRExpressionUtil;
import net.sf.jasperreports.renderers.Renderable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class ChartConverter extends ElementConverter
{

	/**
	 *
	 */
	private final static ChartConverter INSTANCE = new ChartConverter();
	
	/**
	 *
	 */
	private ChartConverter()
	{
	}

	/**
	 *
	 */
	public static ChartConverter getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public JRPrintElement convert(ReportConverter reportConverter, JRElement element)
	{
		JRBasePrintImage printImage = new JRBasePrintImage(reportConverter.getDefaultStyleProvider());
		JRChart chart = (JRChart)element;

		copyElement(reportConverter, chart, printImage);
		
		printImage.copyBox(chart.getLineBox());
		
		printImage.setAnchorName(JRExpressionUtil.getExpressionText(chart.getAnchorNameExpression()));
		printImage.setBookmarkLevel(chart.getBookmarkLevel());
		printImage.setLinkType(chart.getLinkType());
		printImage.setOnErrorType(OnErrorTypeEnum.ICON);
		printImage.setRenderer(getRenderer(reportConverter, chart));
		printImage.setScaleImage(ScaleImageEnum.CLIP);
		printImage.setRotation(RotationEnum.NONE);
		
		return printImage;
	}

	/**
	 * 
	 */
	private Renderable getRenderer(ReportConverter reportConverter, JRChart chart)
	{
		String renderType = chart.getRenderType();//FIXMETHEME try reuse this sequence
		if (renderType == null)
		{
			renderType = JRPropertiesUtil.getInstance(reportConverter.getJasperReportsContext()).getProperty(reportConverter.getReport(), JRChart.PROPERTY_CHART_RENDER_TYPE);
		}
		
		String themeName = chart.getTheme();
		if (themeName == null)
		{
			themeName = JRPropertiesUtil.getInstance(reportConverter.getJasperReportsContext()).getProperty(reportConverter.getReport(), JRChart.PROPERTY_CHART_THEME);
		}
		
		ChartTheme theme = ChartUtil.getInstance(reportConverter.getJasperReportsContext()).getTheme(themeName);
		
		ChartContext chartContext = new ConvertChartContext(reportConverter, chart);
		
		JFreeChart jfreeChart = null;
		try
		{
			jfreeChart = theme.createChart(chartContext);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		
		Rectangle2D rectangle = new Rectangle2D.Double(0, 0, chart.getWidth(), chart.getHeight());

		return 
			ChartUtil.getInstance(reportConverter.getJasperReportsContext()).getChartRenderableFactory(renderType).getRenderable(
				reportConverter.getJasperReportsContext(),
				jfreeChart, 
				null,
				rectangle,
				reportConverter.getReport().getDpi()
				);
	}
}
