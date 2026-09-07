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
package net.sf.jasperreports.charts.fill;

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
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/** 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ChartsFillObjectFactory implements ChartsAbstractObjectFactory
{
	private final JRFillObjectFactory parent;
	
	public ChartsFillObjectFactory(JRFillObjectFactory parent)
	{
		this.parent = parent;
	}
	
	
	public JRFillObjectFactory getParent()
	{
		return parent;
	}


	@Override
	public void visitChart(JRChart chart)
	{
		JRFillChart fillChart = null;

		if (chart != null)
		{
			fillChart = (JRFillChart)parent.get(chart);
			if (fillChart == null)
			{
				fillChart = new JRFillChart(parent.getFiller(), chart, this);
			}
		}

		parent.setVisitResult(fillChart);
	}


	@Override
	public JRPieDataset getPieDataset(JRPieDataset pieDataset)
	{
		JRFillPieDataset fillPieDataset = null;

		if (pieDataset != null)
		{
			fillPieDataset = (JRFillPieDataset)parent.get(pieDataset);
			if (fillPieDataset == null)
			{
				fillPieDataset = new JRFillPieDataset(pieDataset, this);
				parent.registerElementDataset(fillPieDataset);
			}
		}

		return fillPieDataset;
	}


	@Override
	public JRPiePlot getPiePlot(JRPiePlot piePlot)
	{
		JRFillPiePlot fillPiePlot = null;

		if (piePlot != null)
		{
			fillPiePlot = (JRFillPiePlot)parent.get(piePlot);
			if (fillPiePlot == null)
			{
				fillPiePlot = new JRFillPiePlot(piePlot, this);
			}
		}

		return fillPiePlot;
	}

	/**
	 * @deprecated To be removed.
	 */
	@Override
	public net.sf.jasperreports.charts.JRPie3DPlot getPie3DPlot(net.sf.jasperreports.charts.JRPie3DPlot pie3DPlot)
	{
		JRFillPie3DPlot fillPie3DPlot = null;

		if (pie3DPlot != null)
		{
			fillPie3DPlot = (JRFillPie3DPlot)parent.get(pie3DPlot);
			if (fillPie3DPlot == null)
			{
				fillPie3DPlot = new JRFillPie3DPlot(pie3DPlot, this);
			}
		}

		return fillPie3DPlot;
	}


	@Override
	public JRCategoryDataset getCategoryDataset(JRCategoryDataset categoryDataset)
	{
		JRFillCategoryDataset fillCategoryDataset = null;

		if (categoryDataset != null)
		{
			fillCategoryDataset = (JRFillCategoryDataset)parent.get(categoryDataset);
			if (fillCategoryDataset == null)
			{
				fillCategoryDataset = new JRFillCategoryDataset(categoryDataset, this);
				parent.registerElementDataset(fillCategoryDataset);
			}
		}

		return fillCategoryDataset;
	}

	@Override
	public JRXyzDataset getXyzDataset( JRXyzDataset xyzDataset ){
		JRFillXyzDataset fillXyzDataset = null;
		if ( xyzDataset != null ){
			fillXyzDataset = (JRFillXyzDataset)parent.get( xyzDataset );
			if ( fillXyzDataset == null ){
				fillXyzDataset = new JRFillXyzDataset( xyzDataset, this );
				parent.registerElementDataset(fillXyzDataset);
			}
		}

		return fillXyzDataset;

	}


	/**
	 *
	 */
	public JRXyDataset getXyDataset(JRXyDataset xyDataset)
	{
		JRFillXyDataset fillXyDataset = null;

		if (xyDataset != null)
		{
			fillXyDataset = (JRFillXyDataset)parent.get(xyDataset);
			if (fillXyDataset == null)
			{
				fillXyDataset = new JRFillXyDataset(xyDataset, this);
				parent.registerElementDataset(fillXyDataset);
			}
		}

		return fillXyDataset;
	}


	@Override
	public JRTimeSeriesDataset getTimeSeriesDataset( JRTimeSeriesDataset timeSeriesDataset ){
		JRFillTimeSeriesDataset fillTimeSeriesDataset = null;

		if ( timeSeriesDataset != null ){

			fillTimeSeriesDataset = (JRFillTimeSeriesDataset)parent.get( timeSeriesDataset );

			if ( fillTimeSeriesDataset == null ){
				fillTimeSeriesDataset = new JRFillTimeSeriesDataset( timeSeriesDataset, this );
				parent.registerElementDataset(fillTimeSeriesDataset);
			}
		}

		return fillTimeSeriesDataset;
	}

	@Override
	public JRTimePeriodDataset getTimePeriodDataset( JRTimePeriodDataset timePeriodDataset ){
		JRFillTimePeriodDataset fillTimePeriodDataset = null;
		if ( timePeriodDataset != null ){
			fillTimePeriodDataset = (JRFillTimePeriodDataset)parent.get( timePeriodDataset );
			if ( fillTimePeriodDataset == null ){
				fillTimePeriodDataset = new JRFillTimePeriodDataset( timePeriodDataset, this );
				parent.registerElementDataset(fillTimePeriodDataset);
			}
		}
		return fillTimePeriodDataset;
	}
	
	/**
	 * 
	 */
	public JRGanttDataset getGanttDataset(JRGanttDataset ganttDataset)
	{
		JRFillGanttDataset fillGanttDataset = null;
		
		if (ganttDataset != null)
		{
			fillGanttDataset = (JRFillGanttDataset)parent.get(ganttDataset);
			if (fillGanttDataset == null)
			{
				fillGanttDataset = new JRFillGanttDataset(ganttDataset, this);
				parent.registerElementDataset(fillGanttDataset);
			}
		}
		
		return fillGanttDataset;
	}
	
	@Override
	public JRPieSeries getPieSeries(JRPieSeries pieSeries)
	{
		JRFillPieSeries fillPieSeries = null;

		if (pieSeries != null)
		{
			fillPieSeries = (JRFillPieSeries)parent.get(pieSeries);
			if (fillPieSeries == null)
			{
				fillPieSeries = new JRFillPieSeries(pieSeries, this);
			}
		}

		return fillPieSeries;
	}


	@Override
	public JRCategorySeries getCategorySeries(JRCategorySeries categorySeries)
	{
		JRFillCategorySeries fillCategorySeries = null;

		if (categorySeries != null)
		{
			fillCategorySeries = (JRFillCategorySeries)parent.get(categorySeries);
			if (fillCategorySeries == null)
			{
				fillCategorySeries = new JRFillCategorySeries(categorySeries, this);
			}
		}

		return fillCategorySeries;
	}


	@Override
	public JRXyzSeries getXyzSeries( JRXyzSeries xyzSeries ){
		JRFillXyzSeries fillXyzSeries = null;

		if ( xyzSeries != null ){
			fillXyzSeries = (JRFillXyzSeries)parent.get( xyzSeries );

			if ( fillXyzSeries == null ){
				fillXyzSeries = new JRFillXyzSeries( xyzSeries, this );
			}
		}

		return fillXyzSeries;
	}


	/**
	 *
	 */
	public JRXySeries getXySeries(JRXySeries xySeries)
	{
		JRFillXySeries fillXySeries = null;

		if (xySeries != null)
		{
			fillXySeries = (JRFillXySeries)parent.get(xySeries);
			if (fillXySeries == null)
			{
				fillXySeries = new JRFillXySeries(xySeries, this);
			}
		}

		return fillXySeries;
	}
	
	
	/**
	 * 
	 */
	public JRGanttSeries getGanttSeries(JRGanttSeries ganttSeries)
	{
		JRFillGanttSeries fillGanttSeries = null;
		
		if (ganttSeries != null)
		{
			fillGanttSeries = (JRFillGanttSeries)parent.get(ganttSeries);
			if (fillGanttSeries == null)
			{
				fillGanttSeries = new JRFillGanttSeries(ganttSeries, this);
			}
		}
		
		return fillGanttSeries;
	}
	
	
	@Override
	public JRBarPlot getBarPlot(JRBarPlot barPlot)
	{
		JRFillBarPlot fillBarPlot = null;

		if (barPlot != null)
		{
			fillBarPlot = (JRFillBarPlot)parent.get(barPlot);
			if (fillBarPlot == null)
			{
				fillBarPlot = new JRFillBarPlot(barPlot, this);
			}
		}

		return fillBarPlot;
	}


	@Override
	public JRTimeSeries getTimeSeries(JRTimeSeries timeSeries)
	{
		JRFillTimeSeries fillTimeSeries = null;

		if (timeSeries != null)
		{
			fillTimeSeries = (JRFillTimeSeries)parent.get(timeSeries);
			if (fillTimeSeries == null)
			{
				fillTimeSeries = new JRFillTimeSeries(timeSeries, this);
			}
		}

		return fillTimeSeries;
	}

	@Override
	public JRTimePeriodSeries getTimePeriodSeries( JRTimePeriodSeries timePeriodSeries ){
		JRFillTimePeriodSeries fillTimePeriodSeries = null;
		if ( timePeriodSeries != null ){
			fillTimePeriodSeries = (JRFillTimePeriodSeries)parent.get( timePeriodSeries );
			if ( fillTimePeriodSeries == null ){
				fillTimePeriodSeries = new JRFillTimePeriodSeries( timePeriodSeries, this );
			}
		}

		return fillTimePeriodSeries;
	}

	/**
	 * @deprecated To be removed.
	 */
	@Override
	public net.sf.jasperreports.charts.JRBar3DPlot getBar3DPlot(net.sf.jasperreports.charts.JRBar3DPlot barPlot) {
		JRFillBar3DPlot fillBarPlot = null;

		if (barPlot != null){
			fillBarPlot = (JRFillBar3DPlot)parent.get(barPlot);
			if (fillBarPlot == null){
				fillBarPlot = new JRFillBar3DPlot(barPlot, this);
			}
		}

		return fillBarPlot;
	}


	@Override
	public JRLinePlot getLinePlot(JRLinePlot linePlot) {
		JRFillLinePlot fillLinePlot = null;

		if (linePlot != null){
			fillLinePlot = (JRFillLinePlot)parent.get(linePlot);
			if (fillLinePlot == null){
				fillLinePlot = new JRFillLinePlot(linePlot, this);
			}
		}

		return fillLinePlot;
	}


	/**
	 *
	 */
	public JRScatterPlot getScatterPlot(JRScatterPlot scatterPlot) {
		JRFillScatterPlot fillScatterPlot = null;

		if (scatterPlot != null){
			fillScatterPlot = (JRFillScatterPlot)parent.get(scatterPlot);
			if (fillScatterPlot == null){
				fillScatterPlot = new JRFillScatterPlot(scatterPlot, this);
			}
		}

		return fillScatterPlot;
	}


	@Override
	public JRAreaPlot getAreaPlot(JRAreaPlot areaPlot) {
		JRFillAreaPlot fillAreaPlot = null;

		if (areaPlot != null)
		{
			fillAreaPlot = (JRFillAreaPlot)parent.get(areaPlot);
			if (fillAreaPlot == null)
			{
				fillAreaPlot = new JRFillAreaPlot(areaPlot, this);
			}
		}

		return fillAreaPlot;
	}


	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRAbstractObjectFactory#getBubblePlot(net.sf.jasperreports.charts.JRBubblePlot)
	 */
	@Override
	public JRBubblePlot getBubblePlot(JRBubblePlot bubblePlot) {
		JRFillBubblePlot fillBubblePlot = null;

		if (bubblePlot != null)
		{
			fillBubblePlot = (JRFillBubblePlot)parent.get(bubblePlot);
			if (fillBubblePlot == null)
			{
				fillBubblePlot = new JRFillBubblePlot(bubblePlot, this);
			}
		}

		return fillBubblePlot;
	}


	/**
	 *
	 */
	public JRHighLowDataset getHighLowDataset(JRHighLowDataset highLowDataset)
	{
		JRFillHighLowDataset fillHighLowDataset = null;

		if (highLowDataset != null)
		{
			fillHighLowDataset = (JRFillHighLowDataset)parent.get(highLowDataset);
			if (fillHighLowDataset == null)
			{
				fillHighLowDataset = new JRFillHighLowDataset(highLowDataset, this);
				parent.registerElementDataset(fillHighLowDataset);
			}
		}

		return fillHighLowDataset;
	}


	/**
	 *
	 */
	public JRHighLowPlot getHighLowPlot(JRHighLowPlot highLowPlot) {
		JRFillHighLowPlot fillHighLowPlot = null;

		if (highLowPlot != null){
			fillHighLowPlot = (JRFillHighLowPlot)parent.get(highLowPlot);
			if (fillHighLowPlot == null){
				fillHighLowPlot = new JRFillHighLowPlot(highLowPlot, this);
			}
		}

		return fillHighLowPlot;
	}


	@Override
	public JRCandlestickPlot getCandlestickPlot(JRCandlestickPlot candlestickPlot)
	{
		JRFillCandlestickPlot fillCandlestickPlot = null;

		if (candlestickPlot != null){
			fillCandlestickPlot = (JRFillCandlestickPlot)parent.get(candlestickPlot);
			if (fillCandlestickPlot == null){
				fillCandlestickPlot = new JRFillCandlestickPlot(candlestickPlot, this);
			}
		}

		return fillCandlestickPlot;
	}



	public JRTimeSeriesPlot getTimeSeriesPlot( JRTimeSeriesPlot plot ){
		JRFillTimeSeriesPlot fillPlot = null;
		if ( plot != null ){
			fillPlot = (JRFillTimeSeriesPlot)parent.get( plot );
			if ( fillPlot == null ){
				fillPlot = new JRFillTimeSeriesPlot( plot, this );
			}
		}

		return fillPlot;

	}

	/**
	 *
	 */
	public JRValueDataset getValueDataset(JRValueDataset valueDataset)
	{
		JRFillValueDataset fillValueDataset = null;

		if (valueDataset != null)
		{
			fillValueDataset = (JRFillValueDataset)parent.get(valueDataset);
			if (fillValueDataset == null)
			{
				fillValueDataset = new JRFillValueDataset(valueDataset, this);
				parent.registerElementDataset(fillValueDataset);
			}
		}

		return fillValueDataset;
	}

	/**
	 *
	 */
	public JRMeterPlot getMeterPlot(JRMeterPlot meterPlot)
	{
		JRFillMeterPlot fillMeterPlot = null;

		if (meterPlot != null)
		{
			fillMeterPlot = (JRFillMeterPlot)parent.get(meterPlot);
			if (fillMeterPlot == null)
			{
				fillMeterPlot = new JRFillMeterPlot(meterPlot, this);
			}
		}

		return fillMeterPlot;
	}

	/**
	 *
	 */
	public JRThermometerPlot getThermometerPlot(JRThermometerPlot thermometerPlot)
	{
		JRFillThermometerPlot fillThermometerPlot = null;

		if (thermometerPlot != null)
		{
			fillThermometerPlot = (JRFillThermometerPlot)parent.get(thermometerPlot);
			if (fillThermometerPlot == null)
			{
				fillThermometerPlot = new JRFillThermometerPlot(thermometerPlot, this);
			}
		}

		return fillThermometerPlot;
	}


	/**
	 *
	 */
	public JRMultiAxisPlot getMultiAxisPlot(JRMultiAxisPlot multiAxisPlot)
	{
		JRFillMultiAxisPlot fillMultiAxisPlot = null;

		if (multiAxisPlot != null)
		{
			fillMultiAxisPlot = (JRFillMultiAxisPlot)parent.get(multiAxisPlot);
			if (fillMultiAxisPlot == null)
			{
				fillMultiAxisPlot = new JRFillMultiAxisPlot(multiAxisPlot, this);
			}
		}

		return fillMultiAxisPlot;
	}


	public JRChartAxis getChartAxis(JRChartAxis axis)
	{
		JRFillChartAxis fillAxis = null;
		if (axis != null)
		{
			fillAxis = (JRFillChartAxis)parent.get(axis);
			if (fillAxis == null)
			{
				fillAxis = new JRFillChartAxis(axis, this);
			}
		}
		return fillAxis;
	}


}
