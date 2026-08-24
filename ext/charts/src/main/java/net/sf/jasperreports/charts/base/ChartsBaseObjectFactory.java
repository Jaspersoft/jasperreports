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
package net.sf.jasperreports.charts.base;

import net.sf.jasperreports.charts.ChartsAbstractObjectFactory;
import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRBarPlot;
import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartAxis;
import net.sf.jasperreports.charts.JRGanttDataset;
import net.sf.jasperreports.charts.JRGanttSeries;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.charts.JRHighLowPlot;
import net.sf.jasperreports.charts.JRLinePlot;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRMultiAxisPlot;
import net.sf.jasperreports.charts.JRPieDataset;
import net.sf.jasperreports.charts.JRPiePlot;
import net.sf.jasperreports.charts.JRPieSeries;
import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRThermometerPlot;
import net.sf.jasperreports.charts.JRTimePeriodDataset;
import net.sf.jasperreports.charts.JRTimePeriodSeries;
import net.sf.jasperreports.charts.JRTimeSeries;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.JRTimeSeriesPlot;
import net.sf.jasperreports.charts.JRValueDataset;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.charts.JRXyzDataset;
import net.sf.jasperreports.charts.JRXyzSeries;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;


/**
 * Factory of objects used in compiled reports.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ChartsBaseObjectFactory implements ChartsAbstractObjectFactory
{
	private final JRBaseObjectFactory parent;
	
	public ChartsBaseObjectFactory(JRBaseObjectFactory parent)
	{
		this.parent = parent;
	}
	
	
	public JRBaseObjectFactory getParent()
	{
		return parent;
	}
	
	@Override
	public JRPieDataset getPieDataset(JRPieDataset pieDataset)
	{
		JRBasePieDataset basePieDataset = null;

		if (pieDataset != null)
		{
			basePieDataset = (JRBasePieDataset)parent.get(pieDataset);
			if (basePieDataset == null)
			{
				basePieDataset = new JRBasePieDataset(pieDataset, this);
			}
		}

		return basePieDataset;
	}


	@Override
	public JRPiePlot getPiePlot(JRPiePlot piePlot)
	{
		JRBasePiePlot basePiePlot = null;

		if (piePlot != null)
		{
			basePiePlot = (JRBasePiePlot)parent.get(piePlot);
			if (basePiePlot == null)
			{
				basePiePlot = new JRBasePiePlot(piePlot, this);
			}
		}

		return basePiePlot;
	}

	/**
	 * @deprecated To be removed.
	 */
	@Override
	public net.sf.jasperreports.charts.JRPie3DPlot getPie3DPlot(net.sf.jasperreports.charts.JRPie3DPlot pie3DPlot)
	{
		JRBasePie3DPlot basePie3DPlot = null;

		if (pie3DPlot != null)
		{
			basePie3DPlot = (JRBasePie3DPlot)parent.get(pie3DPlot);
			if (basePie3DPlot == null)
			{
				basePie3DPlot = new JRBasePie3DPlot(pie3DPlot, this);
			}
		}

		return basePie3DPlot;
	}


	@Override
	public JRCategoryDataset getCategoryDataset(JRCategoryDataset categoryDataset)
	{
		JRBaseCategoryDataset baseCategoryDataset = null;

		if (categoryDataset != null)
		{
			baseCategoryDataset = (JRBaseCategoryDataset)parent.get(categoryDataset);
			if (baseCategoryDataset == null)
			{
				baseCategoryDataset = new JRBaseCategoryDataset(categoryDataset, this);
			}
		}

		return baseCategoryDataset;
	}

	@Override
	public JRTimeSeriesDataset getTimeSeriesDataset( JRTimeSeriesDataset timeSeriesDataset ){
		JRBaseTimeSeriesDataset baseTimeSeriesDataset = null;
		if ( timeSeriesDataset != null ){
			baseTimeSeriesDataset = (JRBaseTimeSeriesDataset)parent.get( timeSeriesDataset );
			if ( baseTimeSeriesDataset == null ){
				baseTimeSeriesDataset = new JRBaseTimeSeriesDataset( timeSeriesDataset, this );
			}
		}

		return baseTimeSeriesDataset;
	}


	@Override
	public JRTimePeriodDataset getTimePeriodDataset( JRTimePeriodDataset timePeriodDataset ){
		JRBaseTimePeriodDataset baseTimePeriodDataset = null;
		if ( timePeriodDataset != null ){
			baseTimePeriodDataset = (JRBaseTimePeriodDataset)parent.get( timePeriodDataset );
			if ( baseTimePeriodDataset == null ){
				baseTimePeriodDataset = new JRBaseTimePeriodDataset( timePeriodDataset, this );
			}
		}
		return baseTimePeriodDataset;
	}

	/*
	 * 
	 */
	public JRGanttDataset getGanttDataset(JRGanttDataset ganttDataset)
	{
		JRBaseGanttDataset baseGanttDataset = null;
		
		if (ganttDataset != null)
		{
			baseGanttDataset = (JRBaseGanttDataset)parent.get(ganttDataset);
			if (baseGanttDataset == null)
			{
				baseGanttDataset = new JRBaseGanttDataset(ganttDataset, this);
			}
		}
		
		return baseGanttDataset;
	}

	
	@Override
	public JRPieSeries getPieSeries(JRPieSeries pieSeries)
	{
		JRBasePieSeries basePieSeries = null;

		if (pieSeries != null)
		{
			basePieSeries = (JRBasePieSeries)parent.get(pieSeries);
			if (basePieSeries == null)
			{
				basePieSeries = new JRBasePieSeries(pieSeries, this);
			}
		}

		return basePieSeries;
	}
	
	
	@Override
	public JRCategorySeries getCategorySeries(JRCategorySeries categorySeries)
	{
		JRBaseCategorySeries baseCategorySeries = null;

		if (categorySeries != null)
		{
			baseCategorySeries = (JRBaseCategorySeries)parent.get(categorySeries);
			if (baseCategorySeries == null)
			{
				baseCategorySeries = new JRBaseCategorySeries(categorySeries, this);
			}
		}

		return baseCategorySeries;
	}


	/**
	 *
	 */
	public JRXySeries getXySeries(JRXySeries xySeries)
	{
		JRBaseXySeries baseXySeries = null;

		if (xySeries != null)
		{
			baseXySeries = (JRBaseXySeries)parent.get(xySeries);
			if (baseXySeries == null)
			{
				baseXySeries = new JRBaseXySeries(xySeries, this);
			}
		}

		return baseXySeries;
	}


	@Override
	public JRTimeSeries getTimeSeries(JRTimeSeries timeSeries)
	{
		JRBaseTimeSeries baseTimeSeries = null;

		if (timeSeries != null)
		{
			baseTimeSeries = (JRBaseTimeSeries)parent.get(timeSeries);
			if (baseTimeSeries == null)
			{
				baseTimeSeries = new JRBaseTimeSeries(timeSeries, this);
			}
		}

		return baseTimeSeries;
	}

	@Override
	public JRTimePeriodSeries getTimePeriodSeries( JRTimePeriodSeries timePeriodSeries ){
		JRBaseTimePeriodSeries baseTimePeriodSeries = null;
		if ( timePeriodSeries != null ){
			baseTimePeriodSeries = (JRBaseTimePeriodSeries)parent.get( timePeriodSeries );
			if ( baseTimePeriodSeries == null ){
				baseTimePeriodSeries = new JRBaseTimePeriodSeries( timePeriodSeries, this );
			}
		}

		return baseTimePeriodSeries;
	}


	/**
	 * 
	 */
	public JRGanttSeries getGanttSeries(JRGanttSeries ganttSeries)
	{
		JRBaseGanttSeries baseGanttSeries = null;
		
		if (ganttSeries != null)
		{
			baseGanttSeries = (JRBaseGanttSeries)parent.get(ganttSeries);
			if (baseGanttSeries == null)
			{
				baseGanttSeries = new JRBaseGanttSeries(ganttSeries, this);
			}
		}
		
		return baseGanttSeries;
	}


	@Override
	public JRBarPlot getBarPlot(JRBarPlot barPlot)
	{
		JRBaseBarPlot baseBarPlot = null;

		if (barPlot != null)
		{
			baseBarPlot = (JRBaseBarPlot)parent.get(barPlot);
			if (baseBarPlot == null)
			{
				baseBarPlot = new JRBaseBarPlot(barPlot, this);
			}
		}

		return baseBarPlot;
	}


	/**
	 * @deprecated To be removed.
	 */
	@Override
	public net.sf.jasperreports.charts.JRBar3DPlot getBar3DPlot(net.sf.jasperreports.charts.JRBar3DPlot barPlot) {
		JRBaseBar3DPlot baseBarPlot = null;

		if (barPlot != null)
		{
			baseBarPlot = (JRBaseBar3DPlot)parent.get(barPlot);
			if (baseBarPlot == null)
			{
				baseBarPlot = new JRBaseBar3DPlot(barPlot, this);
			}
		}

		return baseBarPlot;
	}


	@Override
	public JRLinePlot getLinePlot(JRLinePlot linePlot) {
		JRBaseLinePlot baseLinePlot = null;

		if (linePlot != null)
		{
			baseLinePlot = (JRBaseLinePlot)parent.get(linePlot);
			if (baseLinePlot == null)
			{
				baseLinePlot = new JRBaseLinePlot(linePlot, this);
			}
		}

		return baseLinePlot;
	}


	@Override
	public JRAreaPlot getAreaPlot(JRAreaPlot areaPlot) {
		JRBaseAreaPlot baseAreaPlot = null;

		if (areaPlot != null)
		{
			baseAreaPlot = (JRBaseAreaPlot)parent.get(areaPlot);
			if (baseAreaPlot == null)
			{
				baseAreaPlot = new JRBaseAreaPlot(areaPlot, this);
			}
		}

		return baseAreaPlot;
	}


	@Override
	public JRXyzDataset getXyzDataset(JRXyzDataset xyzDataset) {
		JRBaseXyzDataset baseXyzDataset = null;

		if (xyzDataset != null)
		{
			baseXyzDataset = (JRBaseXyzDataset)parent.get(xyzDataset);
			if (baseXyzDataset == null)
			{
				baseXyzDataset = new JRBaseXyzDataset(xyzDataset, this);
			}
		}

		return baseXyzDataset;
	}


	/*
	 *
	 */
	public JRXyDataset getXyDataset(JRXyDataset xyDataset) {
		JRBaseXyDataset baseXyDataset = null;

		if (xyDataset != null)
		{
			baseXyDataset = (JRBaseXyDataset)parent.get(xyDataset);
			if (baseXyDataset == null)
			{
				baseXyDataset = new JRBaseXyDataset(xyDataset, this);
			}
		}

		return baseXyDataset;
	}

	/*
	 *
	 */
	public JRHighLowDataset getHighLowDataset(JRHighLowDataset highLowDataset) {
		JRBaseHighLowDataset baseHighLowDataset = null;

		if (highLowDataset != null)
		{
			baseHighLowDataset = (JRBaseHighLowDataset)parent.get(highLowDataset);
			if (baseHighLowDataset == null)
			{
				baseHighLowDataset = new JRBaseHighLowDataset(highLowDataset, this);
			}
		}

		return baseHighLowDataset;
	}


	@Override
	public JRXyzSeries getXyzSeries(JRXyzSeries xyzSeries) {
		JRBaseXyzSeries baseXyzSeries = null;

		if (xyzSeries != null)
		{
			baseXyzSeries = (JRBaseXyzSeries)parent.get(xyzSeries);
			if (baseXyzSeries == null)
			{
				baseXyzSeries = new JRBaseXyzSeries(xyzSeries, this);
			}
		}

		return baseXyzSeries;
	}


	@Override
	public JRBubblePlot getBubblePlot(JRBubblePlot bubblePlot) {
		JRBaseBubblePlot baseBubblePlot = null;

		if (bubblePlot != null)
		{
			baseBubblePlot = (JRBaseBubblePlot)parent.get(bubblePlot);
			if (baseBubblePlot == null)
			{
				baseBubblePlot = new JRBaseBubblePlot(bubblePlot, this);
			}
		}

		return baseBubblePlot;
	}


	@Override
	public JRCandlestickPlot getCandlestickPlot(JRCandlestickPlot candlestickPlot)
	{
		JRBaseCandlestickPlot baseCandlestickPlot = null;

		if (candlestickPlot != null)
		{
			baseCandlestickPlot = (JRBaseCandlestickPlot)parent.get(candlestickPlot);
			if (baseCandlestickPlot == null)
			{
				baseCandlestickPlot = new JRBaseCandlestickPlot(candlestickPlot, this);
			}
		}

		return baseCandlestickPlot;
	}


	/**
	 *
	 */
	public JRHighLowPlot getHighLowPlot(JRHighLowPlot highLowPlot)
	{
		JRBaseHighLowPlot baseHighLowPlot = null;

		if (highLowPlot != null)
		{
			baseHighLowPlot = (JRBaseHighLowPlot)parent.get(highLowPlot);
			if (baseHighLowPlot == null)
			{
				baseHighLowPlot = new JRBaseHighLowPlot(highLowPlot, this);
			}
		}

		return baseHighLowPlot;
	}


	/**
	 *
	 */
	public JRScatterPlot getScatterPlot(JRScatterPlot scatterPlot)
	{
		JRBaseScatterPlot baseScatterPlot = null;

		if (scatterPlot != null)
		{
			baseScatterPlot = (JRBaseScatterPlot)parent.get(scatterPlot);
			if (baseScatterPlot == null)
			{
				baseScatterPlot = new JRBaseScatterPlot(scatterPlot, this);
			}
		}

		return baseScatterPlot;
	}



	public JRTimeSeriesPlot getTimeSeriesPlot( JRTimeSeriesPlot plot ){
		JRBaseTimeSeriesPlot basePlot = null;
		if ( plot != null ){
			basePlot = (JRBaseTimeSeriesPlot)parent.get( plot );
			if ( basePlot == null ){
				basePlot = new JRBaseTimeSeriesPlot( plot, this );
			}
		}

		return basePlot;
	}


	/**
	 *
	 */
	public JRValueDataset getValueDataset(JRValueDataset valueDataset)
	{
		JRBaseValueDataset baseValueDataset = null;

		if (valueDataset != null)
		{
			baseValueDataset = (JRBaseValueDataset)parent.get(valueDataset);
			if (baseValueDataset == null)
			{
				baseValueDataset = new JRBaseValueDataset(valueDataset, this);
			}
		}

		return baseValueDataset;
	}


	/**
	 *
	 */
	public JRMeterPlot getMeterPlot(JRMeterPlot meterPlot)
	{
		JRBaseMeterPlot baseMeterPlot = null;

		if (meterPlot != null)
		{
			baseMeterPlot = (JRBaseMeterPlot)parent.get(meterPlot);
			if (baseMeterPlot == null)
			{
				baseMeterPlot = new JRBaseMeterPlot(meterPlot, this);
			}
		}

		return baseMeterPlot;
	}


	/**
	 *
	 */
	public JRThermometerPlot getThermometerPlot(JRThermometerPlot thermometerPlot)
	{
		JRBaseThermometerPlot baseThermometerPlot = null;

		if (thermometerPlot != null)
		{
			baseThermometerPlot = (JRBaseThermometerPlot)parent.get(thermometerPlot);
			if (baseThermometerPlot == null)
			{
				baseThermometerPlot = new JRBaseThermometerPlot(thermometerPlot, this);
			}
		}

		return baseThermometerPlot;
	}


	/**
	 *
	 */
	public JRMultiAxisPlot getMultiAxisPlot(JRMultiAxisPlot multiAxisPlot)
	{
		JRBaseMultiAxisPlot baseMultiAxisPlot = null;

		if (multiAxisPlot != null)
		{
			baseMultiAxisPlot = (JRBaseMultiAxisPlot)parent.get(multiAxisPlot);
			if (baseMultiAxisPlot == null)
			{
				baseMultiAxisPlot = new JRBaseMultiAxisPlot(multiAxisPlot, this);
			}
		}

		return baseMultiAxisPlot;
	}


	@Override
	public void visitChart(JRChart chart)
	{
		JRBaseChart baseChart = null;

		if (chart != null)
		{
			baseChart = (JRBaseChart)parent.get(chart);
			if (baseChart == null)
			{
				baseChart = new JRBaseChart(chart, this);
			}
		}

		parent.setVisitResult(baseChart);
	}

	public JRChartAxis getChartAxis(JRChartAxis axis)
	{
		JRChartAxis baseAxis = null;
		if (axis != null)
		{
			baseAxis = (JRChartAxis)parent.get(axis);
			if (baseAxis == null)
			{
				baseAxis = new JRBaseChartAxis(axis, this);
			}
		}
		return baseAxis;
	}
}
