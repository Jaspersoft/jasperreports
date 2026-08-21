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

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;

import net.sf.jasperreports.charts.ChartContext;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.convert.ReportConverter;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ConvertChartContext implements ChartContext
{
	private final ReportConverter reportConverter;
	private final JRChart chart;
	
	protected ConvertChartContext(ReportConverter reportConverter, JRChart chart)
	{
		this.reportConverter = reportConverter;
		this.chart = chart;
	}
	
	@Override
	public JasperReportsContext getJasperReportsContext()
	{
		return DefaultJasperReportsContext.getInstance();//FIXMECONTEXT
	}
	
	@Override
	public String evaluateTextExpression(JRExpression expression) throws JRException 
	{
		if (expression != null)
		{
			return expression.getText();
		}
		return null;
	}

	@Override
	public Object evaluateExpression(JRExpression expression) throws JRException 
	{
		return null;
	}

	@Override
	public JRChart getChart() {
		return chart;
	}

	@Override
	public Dataset getDataset()
	{//FIXMETHEME make different datasets
		Dataset dataset = null;
		switch (chart.getDataset().getDatasetType()){
		case JRChartDataset.CATEGORY_DATASET:
			dataset = createCategoryDataset();
			break;
		case JRChartDataset.GANTT_DATASET:
			dataset = createGanttDataset();
			break;
		case JRChartDataset.HIGHLOW_DATASET:
			dataset = createHighLowDataset();
			break;
		case JRChartDataset.PIE_DATASET:
			dataset = createPieDataset();
			break;
		case JRChartDataset.TIMEPERIOD_DATASET:
			dataset = createTimePeriodDataset();
			break;
		case JRChartDataset.TIMESERIES_DATASET:
			dataset = createTimeSeriesDataset();
			break;
		case JRChartDataset.VALUE_DATASET:
			dataset = new DefaultValueDataset(50);
			break;
		case JRChartDataset.XY_DATASET:
			dataset = createXyDataset();
			break;
		case JRChartDataset.XYZ_DATASET:
			dataset = createXyzDataset();
			break;
		default:
		}
		return dataset;
	}

	@Override
	public Object getLabelGenerator() {
		return null;
	}

	@Override
	public Locale getLocale() {
		return reportConverter.getLocale();
	}

	@Override
	public TimeZone getTimeZone() {
		return reportConverter.getTimeZone();
	}
	
	private static DefaultCategoryDataset sampleCategoryDataset;
	
	private static CategoryDataset createCategoryDataset()
	{
		if (sampleCategoryDataset == null)
		{
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			dataset.addValue(1.0, "First", "One");
			dataset.addValue(4.0, "First", "Two");
			dataset.addValue(3.0, "First", "Three");
			dataset.addValue(5.0, "First", "Four");
			dataset.addValue(5.0, "First", "Five");
			dataset.addValue(7.0, "First", "Six");
			dataset.addValue(7.0, "First", "Seven");
			dataset.addValue(8.0, "First", "Eight");
			dataset.addValue(5.0, "Second", "One");
			dataset.addValue(7.0, "Second", "Two");
			dataset.addValue(6.0, "Second", "Three");
			dataset.addValue(8.0, "Second", "Four");
			dataset.addValue(4.0, "Second", "Five");
			dataset.addValue(4.0, "Second", "Six");
			dataset.addValue(2.0, "Second", "Seven");
			dataset.addValue(1.0, "Second", "Eight");
			dataset.addValue(4.0, "Third", "One");
			dataset.addValue(3.0, "Third", "Two");
			dataset.addValue(2.0, "Third", "Three");
			dataset.addValue(3.0, "Third", "Four");
			dataset.addValue(6.0, "Third", "Five");
			dataset.addValue(3.0, "Third", "Six");
			dataset.addValue(4.0, "Third", "Seven");
			dataset.addValue(3.0, "Third", "Eight");
			
			sampleCategoryDataset = dataset;
		}
		
		return sampleCategoryDataset;
	}
	
	private static PieDataset samplePieDataset;
	
	private static PieDataset createPieDataset()
	{
		if (samplePieDataset == null)
		{
			DefaultPieDataset dataset = new DefaultPieDataset();
			dataset.setValue("First", 45d);
			dataset.setValue("Second", 10d);
			dataset.setValue("Third", 15d);
			dataset.setValue("Fourth", 25d);
			dataset.setValue("Fifth", 5d);
			
			samplePieDataset = dataset;
		}
		
		return samplePieDataset;
	}
	
	private static XYSeriesCollection sampleXyDataset;
	
	private static XYDataset createXyDataset()
	{
		if (sampleXyDataset == null)
		{
			XYSeriesCollection dataset = new XYSeriesCollection();

			XYSeries series = new XYSeries("First");
			series.add(1.0, 500.2);
			series.add(5.0, 694.1);
			series.add(4.0, 100.0);
			series.add(12.5, 734.4);
			series.add(17.3, 453.2);
			series.add(21.2, 500.2);
			series.add(21.9, null);
			series.add(25.6, 734.4);
			series.add(30.0, 453.2);
			dataset.addSeries(series);

			series = new XYSeries("Second");
			series.add(2.0, 400.2);
			series.add(6.0, 594.1);
			series.add(5.0, 90.0);
			series.add(13.5, 634.4);
			series.add(18.3, 353.2);
			series.add(22.2, 400.2);
			series.add(22.9, null);
			series.add(26.6, 634.4);
			series.add(31.0, 353.2);
			dataset.addSeries(series);

			series = new XYSeries("Third");
			series.add(0.0, 600.2);
			series.add(4.0, 794.1);
			series.add(3.0, 200.0);
			series.add(11.5, 834.4);
			series.add(16.3, 553.2);
			series.add(20.2, 600.2);
			series.add(20.9, null);
			series.add(24.6, 834.4);
			series.add(29.0, 553.2);
			dataset.addSeries(series);
			
			sampleXyDataset = dataset;
		}

		return sampleXyDataset;
	}
	
	private static TimeSeriesCollection sampleTimeSeriesDataset;
	
	private static TimeSeriesCollection createTimeSeriesDataset()
	{
//		TimeSeries series1 = new TimeSeries("Series 1", Day.class);
//		series1.add(new Day(1, 1, 2003), 54.3);
//		series1.add(new Day(2, 1, 2003), 20.3);
//		series1.add(new Day(3, 1, 2003), 43.4);
//		series1.add(new Day(4, 1, 2003), -12.0);
//		
//		TimeSeries series2 = new TimeSeries("Series 2", Day.class);
//		series2.add(new Day(1, 1, 2003), 8.0);
//		series2.add(new Day(2, 1, 2003), 16.0);
//		series2.add(new Day(3, 1, 2003), 21.0);
//		series2.add(new Day(4, 1, 2003), 5.0);
//		
//		TimeSeriesCollection dataset = new TimeSeriesCollection();
//		dataset.addSeries(series1);
//		dataset.addSeries(series2);
//		return dataset;
		
		if (sampleTimeSeriesDataset == null)
		{
			TimeSeriesCollection dataset = new TimeSeriesCollection();
			
			TimeSeries series = new TimeSeries("First", "Year", "Count");

			series.add(new Year(1976), 0);
			series.add(new Year(1977), 1);
			series.add(new Year(1978), 0);
			series.add(new Year(1979), 2);
			series.add(new Year(1980), 0);
			series.add(new Year(1981), 1);
			series.add(new Year(1982), 2);
			series.add(new Year(1983), 5);
			series.add(new Year(1984), 21);
			series.add(new Year(1985), 18);
			series.add(new Year(1986), 18);
			series.add(new Year(1987), 25);
			series.add(new Year(1988), 11);
			series.add(new Year(1989), 16);
			series.add(new Year(1990), 23);
			series.add(new Year(1991), 14);
			series.add(new Year(1992), 31);
			series.add(new Year(1993), 38);
			series.add(new Year(1994), 31);
			series.add(new Year(1995), 56);
			series.add(new Year(1996), 45);
			series.add(new Year(1997), 74);
			series.add(new Year(1998), 68);
			series.add(new Year(1999), 98);
			series.add(new Year(2000), 85);
			series.add(new Year(2001), 66);
			series.add(new Year(2002), 71);
			series.add(new Year(2003), 65);
			series.add(new Year(2004), 59);
			series.add(new Year(2005), 60);
			
			dataset.addSeries(series);
			
			sampleTimeSeriesDataset = dataset;
		}
		
		return sampleTimeSeriesDataset;
	}
	
	private static DefaultXYZDataset sampleXyzDataset;
	
	private static XYZDataset createXyzDataset()
	{
		if (sampleXyzDataset == null)
		{
			DefaultXYZDataset dataset = new DefaultXYZDataset(); 
			
			dataset.addSeries(
				"First", 
				new double[][]{ 
					{2.1, 2.3, 2.3, 2.2, 2.2, 1.8, 1.8, 1.9, 2.3, 3.8}, 
					{14.1, 11.1, 10.0, 8.8, 8.7, 8.4, 5.4, 4.1, 4.1, 25}, 
					{2.4, 2.7, 2.7, 2.2, 2.2, 2.2, 2.1, 2.2, 1.6, 4}
					}
				);
			
			sampleXyzDataset = dataset;
		}
		
		return sampleXyzDataset;
	}

	private static TaskSeriesCollection sampleGanttDataset;
	
	private static IntervalCategoryDataset createGanttDataset() 
	{
		if (sampleGanttDataset == null)
		{
			TaskSeriesCollection dataset = new TaskSeriesCollection();

			TaskSeries series = new TaskSeries("Scheduled");
			series.add(new Task("First", new SimpleTimePeriod(date(1, Calendar.APRIL, 2008), date(5, Calendar.APRIL, 2008))));
			series.add(new Task("Second", new SimpleTimePeriod(date(9, Calendar.APRIL, 2008), date(9, Calendar.APRIL, 2008))));
			series.add(new Task("Third", new SimpleTimePeriod(date(10, Calendar.APRIL, 2008), date(5, Calendar.MAY, 2008))));
			series.add(new Task("Fourth", new SimpleTimePeriod(date(6, Calendar.MAY, 2008), date(30, Calendar.MAY, 2008))));
			series.add(new Task("Fifth", new SimpleTimePeriod(date(2, Calendar.JUNE, 2008), date(2, Calendar.JUNE, 2008))));
			dataset.add(series);
			
			series = new TaskSeries("Actual");
			series.add(new Task("First", new SimpleTimePeriod(date(1, Calendar.APRIL, 2008), date(5, Calendar.APRIL, 2008))));
			series.add(new Task("Second", new SimpleTimePeriod(date(9, Calendar.APRIL, 2008), date(9, Calendar.APRIL, 2008))));
			series.add(new Task("Third", new SimpleTimePeriod(date(10, Calendar.APRIL, 2008), date(15, Calendar.MAY, 2008))));
			series.add(new Task("Fourth", new SimpleTimePeriod(date(15, Calendar.MAY, 2008), date(17, Calendar.JUNE, 2008))));
			series.add(new Task("Fifth", new SimpleTimePeriod(date(30, Calendar.JUNE, 2008), date(30, Calendar.JUNE, 2008))));
			dataset.add(series);

			sampleGanttDataset = dataset;
		}
		
		return sampleGanttDataset;
	}
	
	private static Date date(int day, int month, int year)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		return calendar.getTime();
	}

	private static DefaultHighLowDataset sampleHighLowDataset;
	
	private static OHLCDataset createHighLowDataset() 
	{
		if (sampleHighLowDataset == null)
		{
			Date[] date = new Date[47];
			double[] high = new double[47];
			double[] low = new double[47];
			double[] open = new double[47];
			double[] close = new double[47];
			double[] volume = new double[47];
			
			int jan = 1;
			int feb = 2;
			
			date[0]  = getDate(2001, jan, 4, 12, 0, 0);
			high[0]  = 47.0;
			low[0]   = 33.0;
			open[0]  = 35.0;
			close[0] = 33.0;
			volume[0] = 100.0;
			
			date[1]  = getDate(2001, jan, 5, 12, 0, 0);
			high[1]  = 47.0;
			low[1]   = 32.0;
			open[1]  = 41.0;
			close[1] = 37.0;
			volume[1] = 150.0;
			
			date[2]  = getDate(2001, jan, 6, 12, 0, 0);
			high[2]  = 49.0;
			low[2]   = 43.0;
			open[2]  = 46.0;
			close[2] = 48.0;
			volume[2] = 70.0;
			
			date[3]  = getDate(2001, jan, 7, 12, 0, 0);
			high[3]  = 51.0;
			low[3]   = 39.0;
			open[3]  = 40.0;
			close[3] = 47.0;
			volume[3] = 200.0;
			
			date[4]  = getDate(2001, jan, 8, 12, 0, 0);
			high[4]  = 60.0;
			low[4]   = 40.0;
			open[4]  = 46.0;
			close[4] = 53.0;
			volume[4] = 120.0;
			
			date[5]  = getDate(2001, jan, 9, 12, 0, 0);
			high[5]  = 62.0;
			low[5]   = 55.0;
			open[5]  = 57.0;
			close[5] = 61.0;
			volume[5] = 110.0;
			
			date[6]  = getDate(2001, jan, 10, 12, 0, 0);
			high[6]  = 65.0;
			low[6]   = 56.0;
			open[6]  = 62.0;
			close[6] = 59.0;
			volume[6] = 70.0;
			
			date[7]  = getDate(2001, jan, 11, 12, 0, 0);
			high[7]  = 55.0;
			low[7]   = 43.0;
			open[7]  = 45.0;
			close[7] = 47.0;
			volume[7] = 20.0;
			
			date[8]  = getDate(2001, jan, 12, 12, 0, 0);
			high[8]  = 54.0;
			low[8]   = 33.0;
			open[8]  = 40.0;
			close[8] = 51.0;
			volume[8] = 30.0;
			
			date[9]  = getDate(2001, jan, 13, 12, 0, 0);
			high[9]  = 47.0;
			low[9]   = 33.0;
			open[9]  = 35.0;
			close[9] = 33.0;
			volume[9] = 100.0;
			
			date[10]  = getDate(2001, jan, 14, 12, 0, 0);
			high[10]  = 54.0;
			low[10]   = 38.0;
			open[10]  = 43.0;
			close[10] = 52.0;
			volume[10] = 50.0;
			
			date[11]  = getDate(2001, jan, 15, 12, 0, 0);
			high[11]  = 48.0;
			low[11]   = 41.0;
			open[11]  = 44.0;
			close[11] = 41.0;
			volume[11] = 80.0;
			
			date[12]  = getDate(2001, jan, 17, 12, 0, 0);
			high[12]  = 60.0;
			low[12]   = 30.0;
			open[12]  = 34.0;
			close[12] = 44.0;
			volume[12] = 90.0;
			
			date[13]  = getDate(2001, jan, 18, 12, 0, 0);
			high[13]  = 58.0;
			low[13]   = 44.0;
			open[13]  = 54.0;
			close[13] = 56.0;
			volume[13] = 20.0;
			
			date[14]  = getDate(2001, jan, 19, 12, 0, 0);
			high[14]  = 54.0;
			low[14]   = 32.0;
			open[14]  = 42.0;
			close[14] = 53.0;
			volume[14] = 70.0;
			
			date[15]  = getDate(2001, jan, 20, 12, 0, 0);
			high[15]  = 53.0;
			low[15]   = 39.0;
			open[15]  = 50.0;
			close[15] = 49.0;
			volume[15] = 60.0;
			
			date[16]  = getDate(2001, jan, 21, 12, 0, 0);
			high[16]  = 47.0;
			low[16]   = 33.0;
			open[16]  = 41.0;
			close[16] = 40.0;
			volume[16] = 30.0;
			
			date[17]  = getDate(2001, jan, 22, 12, 0, 0);
			high[17]  = 55.0;
			low[17]   = 37.0;
			open[17]  = 43.0;
			close[17] = 45.0;
			volume[17] = 90.0;
			
			date[18]  = getDate(2001, jan, 23, 12, 0, 0);
			high[18]  = 54.0;
			low[18]   = 42.0;
			open[18]  = 50.0;
			close[18] = 42.0;
			volume[18] = 150.0;
			
			date[19]  = getDate(2001, jan, 24, 12, 0, 0);
			high[19]  = 48.0;
			low[19]   = 37.0;
			open[19]  = 37.0;
			close[19] = 47.0;
			volume[19] = 120.0;
			
			date[20]  = getDate(2001, jan, 25, 12, 0, 0);
			high[20]  = 58.0;
			low[20]   = 33.0;
			open[20]  = 39.0;
			close[20] = 41.0;
			volume[20] = 80.0;
			
			date[21]  = getDate(2001, jan, 26, 12, 0, 0);
			high[21]  = 47.0;
			low[21]   = 31.0;
			open[21]  = 36.0;
			close[21] = 41.0;
			volume[21] = 40.0;
			
			date[22]  = getDate(2001, jan, 27, 12, 0, 0);
			high[22]  = 58.0;
			low[22]   = 44.0;
			open[22]  = 49.0;
			close[22] = 44.0;
			volume[22] = 20.0;
			
			date[23]  = getDate(2001, jan, 28, 12, 0, 0);
			high[23]  = 46.0;
			low[23]   = 41.0;
			open[23]  = 43.0;
			close[23] = 44.0;
			volume[23] = 60.0;
			
			date[24]  = getDate(2001, jan, 29, 12, 0, 0);
			high[24]  = 56.0;
			low[24]   = 39.0;
			open[24]  = 39.0;
			close[24] = 51.0;
			volume[24] = 40.0;
			
			date[25]  = getDate(2001, jan, 30, 12, 0, 0);
			high[25]  = 56.0;
			low[25]   = 39.0;
			open[25]  = 47.0;
			close[25] = 49.0;
			volume[25] = 70.0;
			
			date[26]  = getDate(2001, jan, 31, 12, 0, 0);
			high[26]  = 53.0;
			low[26]   = 39.0;
			open[26]  = 52.0;
			close[26] = 47.0;
			volume[26] = 60.0;
			
			date[27]  = getDate(2001, feb, 1, 12, 0, 0);
			high[27]  = 51.0;
			low[27]   = 30.0;
			open[27]  = 45.0;
			close[27] = 47.0;
			volume[27] = 90.0;
			
			date[28]  = getDate(2001, feb, 2, 12, 0, 0);
			high[28]  = 47.0;
			low[28]   = 30.0;
			open[28]  = 34.0;
			close[28] = 46.0;
			volume[28] = 100.0;
			
			date[29]  = getDate(2001, feb, 3, 12, 0, 0);
			high[29]  = 57.0;
			low[29]   = 37.0;
			open[29]  = 44.0;
			close[29] = 56.0;
			volume[29] = 20.0;
			
			date[30]  = getDate(2001, feb, 4, 12, 0, 0);
			high[30]  = 49.0;
			low[30]   = 40.0;
			open[30]  = 47.0;
			close[30] = 44.0;
			volume[30] = 50.0;
			
			date[31]  = getDate(2001, feb, 5, 12, 0, 0);
			high[31]  = 46.0;
			low[31]   = 38.0;
			open[31]  = 43.0;
			close[31] = 40.0;
			volume[31] = 70.0;
			
			date[32]  = getDate(2001, feb, 6, 12, 0, 0);
			high[32]  = 55.0;
			low[32]   = 38.0;
			open[32]  = 39.0;
			close[32] = 53.0;
			volume[32] = 120.0;
			
			date[33]  = getDate(2001, feb, 7, 12, 0, 0);
			high[33]  = 50.0;
			low[33]   = 33.0;
			open[33]  = 37.0;
			close[33] = 37.0;
			volume[33] = 140.0;
			
			date[34]  = getDate(2001, feb, 8, 12, 0, 0);
			high[34]  = 59.0;
			low[34]   = 34.0;
			open[34]  = 57.0;
			close[34] = 43.0;
			volume[34] = 70.0;
			
			date[35]  = getDate(2001, feb, 9, 12, 0, 0);
			high[35]  = 48.0;
			low[35]   = 39.0;
			open[35]  = 46.0;
			close[35] = 47.0;
			volume[35] = 70.0;
			
			date[36]  = getDate(2001, feb, 10, 12, 0, 0);
			high[36]  = 55.0;
			low[36]   = 30.0;
			open[36]  = 37.0;
			close[36] = 30.0;
			volume[36] = 30.0;
			
			date[37]  = getDate(2001, feb, 11, 12, 0, 0);
			high[37]  = 60.0;
			low[37]   = 32.0;
			open[37]  = 56.0;
			close[37] = 36.0;
			volume[37] = 70.0;
			
			date[38]  = getDate(2001, feb, 12, 12, 0, 0);
			high[38]  = 56.0;
			low[38]   = 42.0;
			open[38]  = 53.0;
			close[38] = 54.0;
			volume[38] = 40.0;
			
			date[39]  = getDate(2001, feb, 13, 12, 0, 0);
			high[39]  = 49.0;
			low[39]   = 42.0;
			open[39]  = 45.0;
			close[39] = 42.0;
			volume[39] = 90.0;
			
			date[40]  = getDate(2001, feb, 14, 12, 0, 0);
			high[40]  = 55.0;
			low[40]   = 42.0;
			open[40]  = 47.0;
			close[40] = 54.0;
			volume[40] = 70.0;
			
			date[41]  = getDate(2001, feb, 15, 12, 0, 0);
			high[41]  = 49.0;
			low[41]   = 35.0;
			open[41]  = 38.0;
			close[41] = 35.0;
			volume[41] = 20.0;
			
			date[42]  = getDate(2001, feb, 16, 12, 0, 0);
			high[42]  = 47.0;
			low[42]   = 38.0;
			open[42]  = 43.0;
			close[42] = 42.0;
			volume[42] = 10.0;
			
			date[43]  = getDate(2001, feb, 17, 12, 0, 0);
			high[43]  = 53.0;
			low[43]   = 42.0;
			open[43]  = 47.0;
			close[43] = 48.0;
			volume[43] = 20.0;
			
			date[44]  = getDate(2001, feb, 18, 12, 0, 0);
			high[44]  = 47.0;
			low[44]   = 44.0;
			open[44]  = 46.0;
			close[44] = 44.0;
			volume[44] = 30.0;
			
			date[45]  = getDate(2001, feb, 19, 12, 0, 0);
			high[45]  = 46.0;
			low[45]   = 40.0;
			open[45]  = 43.0;
			close[45] = 44.0;
			volume[45] = 50.0;
			
			date[46]  = getDate(2001, feb, 20, 12, 0, 0);
			high[46]  = 48.0;
			low[46]   = 41.0;
			open[46]  = 46.0;
			close[46] = 41.0;
			volume[46] = 100.0;
			
			sampleHighLowDataset = new DefaultHighLowDataset("First", date, high, low, open, close, volume);
		}
		
		return sampleHighLowDataset;
	}
	
	private static TimePeriodValuesCollection sampleTimePeriodDataset;
	
	private static XYDataset createTimePeriodDataset()
	{
		if (sampleTimePeriodDataset == null)
		{
			TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();

			Day today = new Day();
			TimePeriodValues series1 = new TimePeriodValues("First");
			TimePeriodValues series2 = new TimePeriodValues("Second");
			
			for (int i = 0; i < 24; i++) 
			{
				Minute m0 = new Minute(0, new Hour(i, today));
				Minute m1 = new Minute(15, new Hour(i, today));
				Minute m2 = new Minute(30, new Hour(i, today));
				Minute m3 = new Minute(45, new Hour(i, today));
				Minute m4 = new Minute(0, new Hour(i + 1, today));
				series1.add(new SimpleTimePeriod(m0.getStart(), m1.getStart()), Math.random());
				series2.add(new SimpleTimePeriod(m1.getStart(), m2.getStart()), Math.random());
				series1.add(new SimpleTimePeriod(m2.getStart(), m3.getStart()), Math.random());
				series2.add(new SimpleTimePeriod(m3.getStart(), m4.getStart()), Math.random());
			}

			dataset.addSeries(series1);
			dataset.addSeries(series2);
			
			sampleTimePeriodDataset = dataset;
		}

		return sampleTimePeriodDataset;
	}
	
	private static Date getDate(int year, int month, int day, int hour, int minute, int second) {
	   	Calendar calendar = Calendar.getInstance();
	   	calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTime();

	}
	
}
