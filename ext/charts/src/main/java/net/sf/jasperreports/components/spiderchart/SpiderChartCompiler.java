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

import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.components.charts.ChartSettings;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentCompiler;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class SpiderChartCompiler implements ComponentCompiler
{

	@Override
	public void collectExpressions(Component component, JRExpressionCollector collector)
	{
		SpiderChartComponent chart = (SpiderChartComponent) component;
		collectExpressions(chart.getChartSettings(), collector);
		collectExpressions((SpiderDataset)chart.getDataset(), collector);
		collectExpressions((SpiderPlot)chart.getPlot(), collector);
	}

	public static void collectExpressions(SpiderDataset dataset, JRExpressionCollector collector)
	{
		if (dataset != null)
		{
			collector.collect(dataset);
	
			JRCategorySeries[] categorySeries = dataset.getSeries();
			if (categorySeries != null && categorySeries.length > 0)
			{
				JRExpressionCollector seriesCollector = collector.getCollector(dataset);
				for (int j = 0; j < categorySeries.length; j++)
				{
					seriesCollector.addExpression(categorySeries[j].getSeriesExpression());
					seriesCollector.addExpression(categorySeries[j].getCategoryExpression());
					seriesCollector.addExpression(categorySeries[j].getValueExpression());
					seriesCollector.addExpression(categorySeries[j].getLabelExpression());
	
					seriesCollector.collectHyperlink(categorySeries[j].getItemHyperlink());
					
				}
			}
		}
	}

	public static void collectExpressions(SpiderPlot spiderPlot, JRExpressionCollector collector)
	{
		if (spiderPlot != null)
		{
			collector.addExpression(spiderPlot.getMaxValueExpression());
		}
	}

	public static void collectExpressions(ChartSettings chart, JRExpressionCollector collector)
	{
		if (chart != null)
		{
			collector.addExpression(chart.getTitleExpression());
			collector.addExpression(chart.getSubtitleExpression());
			collector.addExpression(chart.getAnchorNameExpression());
			collector.addExpression(chart.getBookmarkLevelExpression());
			collector.addExpression(chart.getHyperlinkReferenceExpression());
			collector.addExpression(chart.getHyperlinkWhenExpression());
			collector.addExpression(chart.getHyperlinkAnchorExpression());
			collector.addExpression(chart.getHyperlinkPageExpression());
			collector.addExpression(chart.getHyperlinkTooltipExpression());
	
			JRHyperlinkParameter[] hyperlinkParameters = chart.getHyperlinkParameters();
			if (hyperlinkParameters != null)
			{
				for (JRHyperlinkParameter parameter : hyperlinkParameters)
				{
					if (parameter != null)
					{
						collector.addExpression(parameter.getValueExpression());
					}
				}
			}
		}
	}

	@Override
	public void verify(Component component, JRVerifier verifier)
	{
		SpiderChartComponent chartComponent = (SpiderChartComponent) component;
		
		verifyEvaluation(verifier, chartComponent);
		
		ChartSettings chartSettings = chartComponent.getChartSettings();
		if (chartSettings == null)
		{
			verifier.addBrokenRule("No chart settings for spider chart", chartComponent);
		}
		
		SpiderDataset dataset = (SpiderDataset)chartComponent.getDataset();
		if (dataset == null)
		{
			verifier.addBrokenRule("No dataset for spider chart", chartComponent);
		}
		else
		{
			verify(verifier, dataset);
		}
		
		SpiderPlot spiderPlot = (SpiderPlot)chartComponent.getPlot();
		
		if (dataset == null)
		{
			verifier.addBrokenRule("No plot set for spider chart", chartComponent);
		}
		else
		{
			verify(verifier, spiderPlot);
		}
		
		
	}

	protected void verifyEvaluation(JRVerifier verifier,
			SpiderChartComponent chart)
	{
		EvaluationTimeEnum evaluationTime = chart.getEvaluationTime();
		if (evaluationTime == EvaluationTimeEnum.AUTO)
		{
			verifier.addBrokenRule("Spider chart evaluation time cannot be Auto", chart);
		}
		else if (evaluationTime == EvaluationTimeEnum.GROUP)
		{
			String groupName = chart.getEvaluationGroup();
			if (groupName == null)
			{
				verifier.addBrokenRule("Evaluation group not set for spider chart", chart);
			}
			else
			{
				JasperDesign report = verifier.getReportDesign();
				if (!report.getGroupsMap().containsKey(groupName))
				{
					verifier.addBrokenRule("Spider chart evaluation group " + groupName 
							+ " not found in report", chart);
				}
			}
		}
	}

	protected void verify(JRVerifier verifier, SpiderDataset dataset)
	{
		verifier.verifyElementDataset(dataset);
		
		JRCategorySeries[] series = dataset.getSeries();
		if (series != null)
		{
			for (int i = 0; i < series.length; i++)
			{
				verifier.verifyHyperlink(series[i].getItemHyperlink());
			}
		}
	}

	protected void verify(JRVerifier verifier, SpiderPlot spiderPlot)
	{
	}

	@Override
	public Component toCompiledComponent(Component component,
			JRBaseObjectFactory baseFactory)
	{
		SpiderChartComponent chart = (SpiderChartComponent) component;
		SpiderChartComponent compiledChart = new SpiderChartComponent(chart, baseFactory);
		return compiledChart;
	}
	
}
