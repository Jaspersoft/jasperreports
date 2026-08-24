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
package net.sf.jasperreports.charts.design;

import net.sf.jasperreports.charts.ChartVisitor;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRGanttDataset;
import net.sf.jasperreports.charts.JRGanttSeries;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.charts.JRPieDataset;
import net.sf.jasperreports.charts.JRPieSeries;
import net.sf.jasperreports.charts.JRTimePeriodDataset;
import net.sf.jasperreports.charts.JRTimePeriodSeries;
import net.sf.jasperreports.charts.JRTimeSeries;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.JRValueDataset;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.charts.JRXyzDataset;
import net.sf.jasperreports.charts.JRXyzSeries;
import net.sf.jasperreports.charts.type.ChartTypeEnum;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.design.JRVerifierVisitor;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;


/**
 * A chart verifier.
 *
 * <p>
 * The verifier checks that a chart design meets certain rules in order to pass
 * report compilation.  
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ChartsVerifier implements ChartVisitor // extends JRVerifier
{

	private final JRVerifier parent;
	
	/**
	 * 
	 */
	public ChartsVerifier(JRVerifierVisitor visitor)
	{
		this.parent = visitor.getVerifier();
	}

	@Override
	public void visitChart(JRChart chart)
	{
		parent.verifyReportElement(chart);
		
		if (chart.getEvaluationTime() == EvaluationTimeEnum.AUTO)
		{
			parent.addBrokenRule("Charts do not support Auto evaluation time.", chart);
		}

		JRChartDataset dataset = chart.getDataset();
		if (dataset == null)
		{
			if (chart.getChartType() != ChartTypeEnum.MULTI_AXIS)
			{
				parent.addBrokenRule("Chart dataset missing.", chart);
			}
		}
		else
		{
			dataset.validate(this);
		}
	}


	public void verify(JRCategoryDataset dataset)
	{
		parent.verifyElementDataset(dataset);

		JRCategorySeries[] categorySeries = dataset.getSeries();
		if (categorySeries != null)
		{
			for (JRCategorySeries crtCategorySeries : categorySeries)
			{
				verify(crtCategorySeries);
			}
		}
	}


	protected void verify(JRCategorySeries series)
	{
		parent.verifyHyperlink(series.getItemHyperlink());
	}


	public void verify(JRPieDataset dataset)
	{
		parent.verifyElementDataset(dataset);
		
		JRPieSeries[] pieSeries = dataset.getSeries();
		if (pieSeries != null)
		{
			for (JRPieSeries crtPieSeries : pieSeries)
			{
				verify(crtPieSeries);
			}
		}

		parent.verifyHyperlink(dataset.getOtherSectionHyperlink());
	}

	
	protected void verify(JRPieSeries series)
	{
		parent.verifyHyperlink(series.getSectionHyperlink());
	}


	public void verify(JRHighLowDataset dataset)
	{
		parent.verifyElementDataset(dataset);
		parent.verifyHyperlink(dataset.getItemHyperlink());
	}


	public void verify(JRTimePeriodDataset dataset)
	{
		parent.verifyElementDataset(dataset);

		JRTimePeriodSeries[] timePeriodSeries = dataset.getSeries();
		if (timePeriodSeries != null)
		{
			for (JRTimePeriodSeries crtTimePeriodSeries : timePeriodSeries)
			{
				verify(crtTimePeriodSeries);
			}
		}
	}


	protected void verify(JRTimePeriodSeries series)
	{
		parent.verifyHyperlink(series.getItemHyperlink());
	}


	public void verify(JRTimeSeriesDataset dataset)
	{
		parent.verifyElementDataset(dataset);

		JRTimeSeries[] timeSeries = dataset.getSeries();
		if (timeSeries != null)
		{
			for (JRTimeSeries crtTimeSeries : timeSeries)
			{
				verify(crtTimeSeries);
			}
		}
	}


	protected void verify(JRTimeSeries series)
	{
		parent.verifyHyperlink(series.getItemHyperlink());
	}


	/**
	 * Verify the design of a value dataset.  Since value dataset's only
	 * contain a single value and do not support hyperlinks there is nothing
	 * to verify.
	 */
	public void verify(JRValueDataset dataset)
	{
	}

	public void verify(JRXyDataset dataset)
	{
		parent.verifyElementDataset(dataset);

		JRXySeries[] xySeries = dataset.getSeries();
		if (xySeries != null)
		{
			for (JRXySeries crtXySeries : xySeries)
			{
				verify(crtXySeries);
			}
		}
	}


	protected void verify(JRXySeries series)
	{
		parent.verifyHyperlink(series.getItemHyperlink());
	}


	protected void verify(JRGanttSeries series)
	{
		parent.verifyHyperlink(series.getItemHyperlink());
	}


	public void verify(JRXyzDataset dataset)
	{
		parent.verifyElementDataset(dataset);

		JRXyzSeries[] xyzSeries = dataset.getSeries();
		if (xyzSeries != null)
		{
			for (JRXyzSeries crtXyzSeries : xyzSeries)
			{
				verify(crtXyzSeries);
			}
		}
	}


	public void verify(JRGanttDataset dataset)
	{
		parent.verifyElementDataset(dataset);
		
		JRGanttSeries[] ganttSeries = dataset.getSeries();
		
		if (ganttSeries != null)
		{
			for (JRGanttSeries crtGanttSeries : ganttSeries)
			{
				verify(crtGanttSeries);
			}
		}
	}
	

	protected void verify(JRXyzSeries series)
	{
		parent.verifyHyperlink(series.getItemHyperlink());
	}
	
}
