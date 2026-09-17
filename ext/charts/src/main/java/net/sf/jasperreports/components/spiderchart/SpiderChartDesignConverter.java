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
package net.sf.jasperreports.components.spiderchart;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.components.charts.ChartSettings;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.base.JRBasePrintImage;
import net.sf.jasperreports.engine.component.ComponentDesignConverter;
import net.sf.jasperreports.engine.convert.ReportConverter;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.util.JRExpressionUtil;

/**
 * Spider Chart preview converter.
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class SpiderChartDesignConverter implements ComponentDesignConverter
{

	@Override
	public JRPrintElement convert(ReportConverter reportConverter, JRComponentElement element)
	{
		SpiderChartComponent chartComponent = (SpiderChartComponent) element.getComponent();
		if (chartComponent == null)
		{
			return null;
		}
		JRBasePrintImage printImage = new JRBasePrintImage(reportConverter.getDefaultStyleProvider());
		ChartSettings chartSettings = chartComponent.getChartSettings();

		reportConverter.copyBaseAttributes(element, printImage);
		
		//TODO: spiderchart box
//		printImage.copyBox(element.getLineBox());
		
		printImage.setAnchorName(JRExpressionUtil.getExpressionText(chartSettings.getAnchorNameExpression()));
		printImage.setBookmarkLevel(chartSettings.getBookmarkLevel());
		printImage.setLinkType(chartSettings.getLinkType());
		printImage.setOnErrorType(OnErrorTypeEnum.ICON);
		printImage.setScaleImage(ScaleImageEnum.CLIP);
		SpiderChartSharedBean spiderchartBean = 
			new SpiderChartSharedBean(
				chartSettings.getRenderType(),
				SpiderChartRendererEvaluator.SAMPLE_MAXVALUE,
				JRExpressionUtil.getExpressionText(chartSettings.getTitleExpression()),
				JRExpressionUtil.getExpressionText(chartSettings.getSubtitleExpression()),
				null,
				null
				);
		
		printImage.setRenderer(
			SpiderChartRendererEvaluator.evaluateRenderable(
				reportConverter.getJasperReportsContext(),
				element,
				spiderchartBean,
				null,
				JRPropertiesUtil.getInstance(reportConverter.getJasperReportsContext()).getProperty(reportConverter.getReport(), JRChart.PROPERTY_CHART_RENDER_TYPE),
				SpiderChartRendererEvaluator.SAMPLE_DATASET,
				reportConverter.getReport().getDpi())
				);
		
		return printImage;
	}
}
