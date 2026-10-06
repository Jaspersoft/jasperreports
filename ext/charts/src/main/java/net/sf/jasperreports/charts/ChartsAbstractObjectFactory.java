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
package net.sf.jasperreports.charts;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface ChartsAbstractObjectFactory extends ChartVisitor
{
	/**
	 *
	 */
	public abstract JRPieDataset getPieDataset(JRPieDataset pieDataset);

	/**
	 *
	 */
	public abstract JRPiePlot getPiePlot(JRPiePlot piePlot);


	/**
	 * @deprecated To be removed.
	 */
	public abstract JRPie3DPlot getPie3DPlot(JRPie3DPlot pie3DPlot);


	/**
	 *
	 */
	public abstract JRCategoryDataset getCategoryDataset(JRCategoryDataset categoryDataset);


	/**
	 * 
	 */
	public abstract JRTimeSeriesDataset getTimeSeriesDataset( JRTimeSeriesDataset timeSeriesDataset );

	/**
	 * 
	 */
	public abstract JRTimePeriodDataset getTimePeriodDataset( JRTimePeriodDataset timePeriodDataset );

	/**
	 * 
	 */
	public abstract JRTimePeriodSeries getTimePeriodSeries( JRTimePeriodSeries timePeriodSeries );

	/**
	 * 
	 */
	public abstract JRTimeSeries getTimeSeries( JRTimeSeries timeSeries );

	/**
	 *
	 */
	public abstract JRPieSeries getPieSeries(JRPieSeries pieSeries);

	/**
	 *
	 */
	public abstract JRCategorySeries getCategorySeries(JRCategorySeries categorySeries);

	/**
	 *
	 */
	public abstract JRXyzDataset getXyzDataset( JRXyzDataset xyzDataset );

	/**
	 *
	 */
	public abstract JRXyzSeries getXyzSeries( JRXyzSeries xyzSeries );


	/**
	 *
	 */
	public abstract JRBarPlot getBarPlot(JRBarPlot barPlot);

	/**
	 * @deprecated To be removed.
	 */
	public abstract JRBar3DPlot getBar3DPlot( JRBar3DPlot barPlot );


	/**
	 *
	 */
	public abstract JRLinePlot getLinePlot( JRLinePlot linePlot );


	/**
	 *
	 */
	public abstract JRAreaPlot getAreaPlot( JRAreaPlot areaPlot );


	/**
	 *
	 */
	public abstract JRBubblePlot getBubblePlot( JRBubblePlot bubblePlot );


	/**
	 *
	 */
	public abstract JRCandlestickPlot getCandlestickPlot(JRCandlestickPlot candlestickPlot);
}
