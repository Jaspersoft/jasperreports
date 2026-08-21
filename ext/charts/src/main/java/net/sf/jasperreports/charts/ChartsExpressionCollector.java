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
package net.sf.jasperreports.charts;

import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.charts.type.ChartTypeEnum;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRExpressionCollector;


/**
 * An expression collector traverses a report and collects report expressions
 * out of it.
 * 
 * <p>
 * The expressions are then included into evaluator classes which are compiled
 * and used at report fill time to evaluate expressions.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ChartsExpressionCollector// extends JRExpressionCollector
{
	private final JRExpressionCollector parent;
	
	public ChartsExpressionCollector(JRExpressionCollector parent)
	{
		this.parent = parent;
	}


	/**
	 *
	 */
	public void collect(JRChart chart)
	{
		parent.collectElement(chart);
		parent.collectAnchor(chart);
		parent.collectHyperlink(chart);

		parent.addExpression(chart.getTitleExpression());
		parent.addExpression(chart.getSubtitleExpression());

		if (chart.getChartType() != ChartTypeEnum.MULTI_AXIS) // multiaxis chart returns null dataset
		{
			chart.getDataset().collectExpressions(this);
		}
		chart.getPlot().collectExpressions(this);
	}

	/**
	 *
	 */
	public void collect(JRPieDataset pieDataset)
	{
		parent.collect(pieDataset);

		JRExpressionCollector datasetCollector = parent.getCollector(pieDataset);
		ChartsExpressionCollector datasetChartsCollector = new ChartsExpressionCollector(datasetCollector);

		JRPieSeries[] pieSeries = pieDataset.getSeries();
		if (pieSeries != null && pieSeries.length > 0)
		{
			for (int j = 0; j < pieSeries.length; j++)
			{
				datasetChartsCollector.collect(pieSeries[j]);
			}
		}

		datasetCollector.addExpression(pieDataset.getOtherKeyExpression());
		datasetCollector.addExpression(pieDataset.getOtherLabelExpression());
		datasetCollector.collectHyperlink(pieDataset.getOtherSectionHyperlink());
	}

	/**
	 *
	 */
	public void collect(JRCategoryDataset categoryDataset)
	{
		parent.collect(categoryDataset);

		JRCategorySeries[] categorySeries = categoryDataset.getSeries();
		if (categorySeries != null && categorySeries.length > 0)
		{
			JRExpressionCollector datasetCollector = parent.getCollector(categoryDataset);
			ChartsExpressionCollector datasetChartsCollector = new ChartsExpressionCollector(datasetCollector);
			for (int j = 0; j < categorySeries.length; j++)
			{
				datasetChartsCollector.collect(categorySeries[j]);
			}
		}
	}

	/**
	 *
	 */
	public void collect(JRXyDataset xyDataset)
	{
		parent.collect(xyDataset);

		JRXySeries[] xySeries = xyDataset.getSeries();
		if (xySeries != null && xySeries.length > 0)
		{
			JRExpressionCollector datasetCollector = parent.getCollector(xyDataset);
			ChartsExpressionCollector datasetChartsCollector = new ChartsExpressionCollector(datasetCollector);
			for (int j = 0; j < xySeries.length; j++)
			{
				datasetChartsCollector.collect(xySeries[j]);
			}
		}
	}

	/**
	 *
	 */
	public void collect( JRTimeSeriesDataset timeSeriesDataset ){
		parent.collect(timeSeriesDataset);

		JRTimeSeries[] timeSeries = timeSeriesDataset.getSeries();
		if ( timeSeries != null && timeSeries.length > 0 ){
			JRExpressionCollector datasetCollector = parent.getCollector(timeSeriesDataset);
			ChartsExpressionCollector datasetChartsCollector = new ChartsExpressionCollector(datasetCollector);
			for ( int i = 0; i <  timeSeries.length; i++ ){
				datasetChartsCollector.collect(timeSeries[i]);
			}
		}
	}

	/**
	 *
	 */
	public void collect( JRTimePeriodDataset timePeriodDataset ){
		parent.collect(timePeriodDataset);

		JRTimePeriodSeries[] timePeriodSeries = timePeriodDataset.getSeries();
		if ( timePeriodSeries != null && timePeriodSeries.length > 0 ){
			JRExpressionCollector datasetCollector = parent.getCollector(timePeriodDataset);
			ChartsExpressionCollector datasetChartsCollector = new ChartsExpressionCollector(datasetCollector);
			for ( int i = 0; i < timePeriodSeries.length; i++ ){
				datasetChartsCollector.collect(timePeriodSeries[i]);
			}
		}
	}

	/**
	 *
	 */
	public void collect(JRGanttDataset ganttDataset)
	{
		parent.collect(ganttDataset);
		
		JRGanttSeries[] ganttSeries = ganttDataset.getSeries();
		if (ganttSeries != null && ganttSeries.length > 0)
		{
			JRExpressionCollector datasetCollector = parent.getCollector(ganttDataset);
			ChartsExpressionCollector datasetChartsCollector = new ChartsExpressionCollector(datasetCollector);
			for (int j = 0; j < ganttSeries.length; j++)
			{
				datasetChartsCollector.collect(ganttSeries[j]);
			}
		}
	}

	/**
	 *
	 */
	public void collect( JRValueDataset valueDataset ){
		parent.collect(valueDataset);

		JRExpressionCollector datasetCollector = parent.getCollector(valueDataset);
		datasetCollector.addExpression(valueDataset.getValueExpression());
	}

	/**
	 *
	 */
	private void collect(JRXySeries xySeries)
	{
		parent.addExpression(xySeries.getSeriesExpression());
		parent.addExpression(xySeries.getXValueExpression());
		parent.addExpression(xySeries.getYValueExpression());
		parent.addExpression(xySeries.getLabelExpression());

		parent.collectHyperlink(xySeries.getItemHyperlink());
	}

	/**
	 *
	 */
	private void collect(JRPieSeries pieSeries)
	{
		parent.addExpression(pieSeries.getKeyExpression());
		parent.addExpression(pieSeries.getValueExpression());
		parent.addExpression(pieSeries.getLabelExpression());

		parent.collectHyperlink(pieSeries.getSectionHyperlink());
	}

	/**
	 *
	 */
	private void collect(JRCategorySeries categorySeries)
	{
		parent.addExpression(categorySeries.getSeriesExpression());
		parent.addExpression(categorySeries.getCategoryExpression());
		parent.addExpression(categorySeries.getValueExpression());
		parent.addExpression(categorySeries.getLabelExpression());

		parent.collectHyperlink(categorySeries.getItemHyperlink());
	}

	/**
	 * 
	 */
	private void collect(JRGanttSeries ganttSeries)
	{
		parent.addExpression(ganttSeries.getSeriesExpression());
		parent.addExpression(ganttSeries.getTaskExpression());
		parent.addExpression(ganttSeries.getSubtaskExpression());
		parent.addExpression(ganttSeries.getStartDateExpression());
		parent.addExpression(ganttSeries.getEndDateExpression());
		parent.addExpression(ganttSeries.getPercentExpression());
		parent.addExpression(ganttSeries.getLabelExpression());

		parent.collectHyperlink(ganttSeries.getItemHyperlink());
	}

	/**
	 *
	 */
	public void collect(JRBarPlot barPlot)
	{
		parent.addExpression(barPlot.getCategoryAxisLabelExpression());
		parent.addExpression(barPlot.getValueAxisLabelExpression());
		parent.addExpression(barPlot.getDomainAxisMinValueExpression());
		parent.addExpression(barPlot.getDomainAxisMaxValueExpression());
		parent.addExpression(barPlot.getRangeAxisMinValueExpression());
		parent.addExpression(barPlot.getRangeAxisMaxValueExpression());
	}

	/**
	 * @deprecated To be removed.
	 */
	public void collect(JRBar3DPlot barPlot)
	{
		parent.addExpression(barPlot.getCategoryAxisLabelExpression());
		parent.addExpression(barPlot.getValueAxisLabelExpression());
		parent.addExpression(barPlot.getRangeAxisMinValueExpression());
		parent.addExpression(barPlot.getRangeAxisMaxValueExpression());
	}

	/**
	 *
	 */
	public void collect( JRLinePlot linePlot ){
		parent.addExpression( linePlot.getCategoryAxisLabelExpression() );
		parent.addExpression( linePlot.getValueAxisLabelExpression() );
		parent.addExpression(linePlot.getDomainAxisMinValueExpression());
		parent.addExpression(linePlot.getDomainAxisMaxValueExpression());
		parent.addExpression(linePlot.getRangeAxisMinValueExpression());
		parent.addExpression(linePlot.getRangeAxisMaxValueExpression());
	}

	/**
	 *
	 */
	public void collect( JRTimeSeriesPlot timeSeriesPlot ){
		parent.addExpression( timeSeriesPlot.getTimeAxisLabelExpression() );
		parent.addExpression( timeSeriesPlot.getValueAxisLabelExpression() );
		parent.addExpression(timeSeriesPlot.getDomainAxisMinValueExpression());
		parent.addExpression(timeSeriesPlot.getDomainAxisMaxValueExpression());
		parent.addExpression(timeSeriesPlot.getRangeAxisMinValueExpression());
		parent.addExpression(timeSeriesPlot.getRangeAxisMaxValueExpression());
	}

	/**
	 *
	 */
	public void collect( JRScatterPlot scatterPlot ){
		parent.addExpression( scatterPlot.getXAxisLabelExpression() );
		parent.addExpression( scatterPlot.getYAxisLabelExpression() );
		parent.addExpression(scatterPlot.getDomainAxisMinValueExpression());
		parent.addExpression(scatterPlot.getDomainAxisMaxValueExpression());
		parent.addExpression(scatterPlot.getRangeAxisMinValueExpression());
		parent.addExpression(scatterPlot.getRangeAxisMaxValueExpression());
	}

	/**
	 *
	 */
	public void collect( JRAreaPlot areaPlot ){
		parent.addExpression( areaPlot.getCategoryAxisLabelExpression() );
		parent.addExpression( areaPlot.getValueAxisLabelExpression() );
		parent.addExpression(areaPlot.getDomainAxisMinValueExpression());
		parent.addExpression(areaPlot.getDomainAxisMaxValueExpression());
		parent.addExpression(areaPlot.getRangeAxisMinValueExpression());
		parent.addExpression(areaPlot.getRangeAxisMaxValueExpression());
	}

	/**
	 *
	 */
	private void collect(JRTimeSeries timeSeries)
	{
		parent.addExpression(timeSeries.getSeriesExpression());
		parent.addExpression(timeSeries.getTimePeriodExpression());
		parent.addExpression(timeSeries.getValueExpression());
		parent.addExpression(timeSeries.getLabelExpression());

		parent.collectHyperlink(timeSeries.getItemHyperlink());
	}

	/**
	 *
	 */
	private void collect(JRTimePeriodSeries timePeriodSeries ){
		parent.addExpression(timePeriodSeries.getSeriesExpression());
		parent.addExpression(timePeriodSeries.getStartDateExpression());
		parent.addExpression(timePeriodSeries.getEndDateExpression());
		parent.addExpression(timePeriodSeries.getValueExpression());
		parent.addExpression(timePeriodSeries.getLabelExpression());
		parent.collectHyperlink(timePeriodSeries.getItemHyperlink());
	}

	/**
	 *
	 */
	public void collect(JRXyzDataset xyzDataset) {
		parent.collect(xyzDataset);

		JRXyzSeries[] xyzSeries = xyzDataset.getSeries();
		if (xyzSeries != null && xyzSeries.length > 0)
		{
			JRExpressionCollector datasetCollector = parent.getCollector(xyzDataset);
			ChartsExpressionCollector datasetChartsCollector = new ChartsExpressionCollector(datasetCollector);
			for (int j = 0; j < xyzSeries.length; j++)
			{
				datasetChartsCollector.collect(xyzSeries[j]);
			}
		}

	}

	/**
	 *
	 */
	private void collect(JRXyzSeries xyzSeries) {
		parent.addExpression(xyzSeries.getSeriesExpression());
		parent.addExpression(xyzSeries.getXValueExpression());
		parent.addExpression(xyzSeries.getYValueExpression());
		parent.addExpression(xyzSeries.getZValueExpression());
		parent.collectHyperlink(xyzSeries.getItemHyperlink());
	}

	/**
	 *
	 */
	public void collect(JRBubblePlot bubblePlot) {
		parent.addExpression(bubblePlot.getXAxisLabelExpression());
		parent.addExpression(bubblePlot.getYAxisLabelExpression());
		parent.addExpression(bubblePlot.getDomainAxisMinValueExpression());
		parent.addExpression(bubblePlot.getDomainAxisMaxValueExpression());
		parent.addExpression(bubblePlot.getRangeAxisMinValueExpression());
		parent.addExpression(bubblePlot.getRangeAxisMaxValueExpression());
	}

	/**
	 *
	 */
	public void collect(JRHighLowPlot highLowPlot)
	{
		parent.addExpression(highLowPlot.getTimeAxisLabelExpression());
		parent.addExpression(highLowPlot.getValueAxisLabelExpression());
		parent.addExpression(highLowPlot.getDomainAxisMinValueExpression());
		parent.addExpression(highLowPlot.getDomainAxisMaxValueExpression());
		parent.addExpression(highLowPlot.getRangeAxisMinValueExpression());
		parent.addExpression(highLowPlot.getRangeAxisMaxValueExpression());
	}

	/**
	 *
	 */
	public void collect(JRDataRange dataRange)
	{
		if (dataRange != null)
		{
			parent.addExpression(dataRange.getLowExpression());
			parent.addExpression(dataRange.getHighExpression());
		}
	}

	/**
	 *
	 */
	public void collect(JRMeterPlot meterPlot)
	{
		List<JRMeterInterval> intervals = meterPlot.getIntervals();
		if (intervals != null)
		{
			Iterator<JRMeterInterval> iter = intervals.iterator();
			while (iter.hasNext())
			{
				JRMeterInterval interval = iter.next();
				collect(interval.getDataRange());
			}
		}
		collect(meterPlot.getDataRange());
	}

	/**
	 *
	 */
	public void collect(JRThermometerPlot thermometerPlot)
	{
		collect(thermometerPlot.getDataRange());
		collect(thermometerPlot.getLowRange());
		collect(thermometerPlot.getMediumRange());
		collect(thermometerPlot.getHighRange());
	}


	/**
	 *
	 */
	public void collect(JRHighLowDataset highLowDataset)
	{
		parent.collect(highLowDataset);

		JRExpressionCollector datasetCollector = parent.getCollector(highLowDataset);
		datasetCollector.addExpression(highLowDataset.getSeriesExpression());
		datasetCollector.addExpression(highLowDataset.getDateExpression());
		datasetCollector.addExpression(highLowDataset.getHighExpression());
		datasetCollector.addExpression(highLowDataset.getLowExpression());
		datasetCollector.addExpression(highLowDataset.getOpenExpression());
		datasetCollector.addExpression(highLowDataset.getCloseExpression());
		datasetCollector.addExpression(highLowDataset.getVolumeExpression());

		datasetCollector.collectHyperlink(highLowDataset.getItemHyperlink());
	}

	/**
	 *
	 */
	public void collect(JRCandlestickPlot candlestickPlot)
	{
		parent.addExpression(candlestickPlot.getTimeAxisLabelExpression());
		parent.addExpression(candlestickPlot.getValueAxisLabelExpression());
		parent.addExpression(candlestickPlot.getDomainAxisMinValueExpression());
		parent.addExpression(candlestickPlot.getDomainAxisMaxValueExpression());
		parent.addExpression(candlestickPlot.getRangeAxisMinValueExpression());
		parent.addExpression(candlestickPlot.getRangeAxisMaxValueExpression());
	}

}
