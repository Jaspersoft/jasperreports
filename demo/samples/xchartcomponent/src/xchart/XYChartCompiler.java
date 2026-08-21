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
package xchart;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
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
public class XYChartCompiler implements ComponentCompiler
{
	@Override
	public void collectExpressions(Component component, JRExpressionCollector collector)
	{
		XYChartComponent chart = (XYChartComponent) component;
		collector.addExpression(chart.getChartTitleExpression());
		collector.addExpression(chart.getXAxisTitleExpression());
		collector.addExpression(chart.getYAxisTitleExpression());
		collectExpressions(chart.getDataset(), collector);
	}

	public static void collectExpressions(XYDataset dataset, JRExpressionCollector collector)
	{
		collector.collect(dataset);
		
		XYSeries[] xySeries = dataset.getSeries();
		if (xySeries != null && xySeries.length > 0)
		{
			JRExpressionCollector seriesCollector = collector.getCollector(dataset);
			for (int i = 0; i < xySeries.length; i++)
			{
				seriesCollector.addExpression(xySeries[i].getSeriesExpression());
				seriesCollector.addExpression(xySeries[i].getXValueExpression());
				seriesCollector.addExpression(xySeries[i].getYValueExpression());
				seriesCollector.addExpression(xySeries[i].getColorExpression());
			}
		}

	}

	@Override
	public void verify(Component component, JRVerifier verifier)
	{
		XYChartComponent chart = (XYChartComponent) component;
		
		verifyEvaluation(verifier, chart);
		
		XYDataset dataset = chart.getDataset();
		if (dataset == null)
		{
			verifier.addBrokenRule("No dataset for axis chart", chart);
		}
		else
		{
			verify(verifier, dataset);
		}
	}

	protected void verifyEvaluation(JRVerifier verifier,
			XYChartComponent chart)
	{
		EvaluationTimeEnum evaluationTime = chart.getEvaluationTime();
		if (evaluationTime == EvaluationTimeEnum.AUTO)
		{
			verifier.addBrokenRule("Axis chart evaluation time cannot be Auto", chart);
		}
		else if (evaluationTime == EvaluationTimeEnum.GROUP)
		{
			String groupName = chart.getEvaluationGroup();
			if (groupName == null)
			{
				verifier.addBrokenRule("Evaluation group not set for axis chart", chart);
			}
			else
			{
				JasperDesign report = verifier.getReportDesign();
				if (!report.getGroupsMap().containsKey(groupName))
				{
					verifier.addBrokenRule("Axis chart evaluation group " + groupName 
							+ " not found in report", chart);
				}
			}
		}
	}

	protected void verify(JRVerifier verifier, XYDataset dataset)
	{
		verifier.verifyElementDataset(dataset);
		
		XYSeries[] xySeries = dataset.getSeries();
		if (xySeries != null && xySeries.length > 0)
		{
			for (XYSeries series : xySeries)
			{
				JRExpression seriesExpression = series.getSeriesExpression();
				if (seriesExpression == null)
				{
					verifier.addBrokenRule("No series expression for XY series", dataset);
				}
				JRExpression xValueExpression = series.getXValueExpression();
				if (xValueExpression == null)
				{
					verifier.addBrokenRule("No X value expression for XY series", dataset);
				}
				JRExpression yValueExpression = series.getYValueExpression();
				if (yValueExpression == null)
				{
					verifier.addBrokenRule("No Y value expression for XY series", dataset);
				}
			}
		}
	}

	@Override
	public Component toCompiledComponent(Component component,
			JRBaseObjectFactory baseFactory)
	{
		XYChartComponent chart = (XYChartComponent) component;
		XYChartComponent compiledChart = new XYChartComponent(chart, baseFactory);
		return compiledChart;
	}
	
}
