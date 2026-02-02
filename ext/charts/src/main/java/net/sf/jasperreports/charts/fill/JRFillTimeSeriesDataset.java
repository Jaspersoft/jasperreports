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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.general.Dataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRTimeSeries;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.charts.type.TimePeriodEnum;
import net.sf.jasperreports.charts.util.ChartUtil;
import net.sf.jasperreports.charts.util.TimeSeriesLabelGenerator;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRFillTimeSeriesDataset extends JRFillChartDataset implements JRTimeSeriesDataset 
{

	public static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_NAME = "charts.time.series.dataset.series.null.name";
	
	/**
	 * 
	 */
	protected JRFillTimeSeries[] timeSeries;
	
	private List<Comparable<?>> seriesNames;
	private Map<Comparable<?>, TimeSeries> seriesMap;
	private Map<Comparable<?>, Map<RegularTimePeriod, String>> labelsMap;
	private Map<Comparable<?>, Map<RegularTimePeriod, JRPrintHyperlink>> itemHyperlinks;
	
	
	public JRFillTimeSeriesDataset(JRTimeSeriesDataset timeSeriesDataset, ChartsFillObjectFactory factory)
	{
		super( timeSeriesDataset, factory.getParent() );
		
		JRTimeSeries[] srcTimeSeries = timeSeriesDataset.getSeries();
		if( srcTimeSeries != null && srcTimeSeries.length > 0)
		{
			timeSeries = new JRFillTimeSeries[srcTimeSeries.length];
			for (int i = 0; i < timeSeries.length; i++)
			{
				timeSeries[i] = (JRFillTimeSeries)factory.getTimeSeries(srcTimeSeries[i]);
			}
		}
	}
	
	@Override
	public JRTimeSeries[] getSeries()
	{
		return timeSeries;
	}
	
	@Override
	protected void customInitialize()
	{
		seriesNames = null;
		seriesMap = null;
		labelsMap = null;
		itemHyperlinks = null;
	}
	
	@Override
	protected void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException 
	{
		if(timeSeries != null && timeSeries.length > 0)
		{
			for (int i = 0; i < timeSeries.length; i++)
			{
				timeSeries[i].evaluate( calculator );
			}
		}
	}
	
	
	@Override
	protected void customIncrement()
	{
		if (timeSeries != null && timeSeries.length > 0)
		{
			if (seriesNames == null)
			{
				seriesNames = new ArrayList<>();
				seriesMap = new HashMap<>();
				labelsMap = new HashMap<>();
				itemHyperlinks = new HashMap<>();
			}

			for (int i = 0; i < timeSeries.length; i++)
			{
				JRFillTimeSeries crtTimeSeries = timeSeries[i];
				
				Comparable<?> seriesName = crtTimeSeries.getSeries();
				if (seriesName == null)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_SERIES_NULL_NAME,  
							(Object[])null 
							);
				}

				TimeSeries series = seriesMap.get(seriesName);
				if(series == null)
				{
					series = new TimeSeries(seriesName.toString());
					seriesNames.add(seriesName);
					seriesMap.put(seriesName, series);
				}
				
				RegularTimePeriod tp = 
					RegularTimePeriod.createInstance(
						ChartUtil.getTimePeriod(getTimePeriod()), 
						crtTimeSeries.getTimePeriod(), 
						getTimeZone(),
						getLocale()
						);

				series.addOrUpdate(tp, crtTimeSeries.getValue());

				if (crtTimeSeries.getLabelExpression() != null)
				{
					Map<RegularTimePeriod, String> seriesLabels = labelsMap.get(seriesName);
					if (seriesLabels == null)
					{
						seriesLabels = new HashMap<>();
						labelsMap.put(seriesName, seriesLabels);
					}
					
					seriesLabels.put(tp, crtTimeSeries.getLabel());
				}
				
				if (crtTimeSeries.hasItemHyperlink())
				{
					Map<RegularTimePeriod, JRPrintHyperlink> seriesLinks = itemHyperlinks.get(seriesName);
					if (seriesLinks == null)
					{
						seriesLinks = new HashMap<>();
						itemHyperlinks.put(seriesName, seriesLinks);
					}
					seriesLinks.put(tp, crtTimeSeries.getPrintItemHyperlink());
				}
			}
		}
	}
	
	@Override
	public Dataset getCustomDataset()
	{
		TimeSeriesCollection dataset = new TimeSeriesCollection(getTimeZone());
		if (seriesNames != null)
		{
			for(int i = 0; i < seriesNames.size(); i++)
			{
				Comparable<?> seriesName = seriesNames.get(i);
				dataset.addSeries(seriesMap.get(seriesName));
			}
		}
		return dataset;
	}


	@Override
	public TimePeriodEnum getTimePeriod() {
		return TimePeriodEnum.getValueOrDefault(((JRTimeSeriesDataset)parent).getTimePeriod());
	}

	@Override
	public void setTimePeriod(TimePeriodEnum timePeriod) {	
	}

	@Override
	public byte getDatasetType() {
		return JRChartDataset.TIMESERIES_DATASET;
	}
	
	
	@Override
	public Object getLabelGenerator(){
		return new TimeSeriesLabelGenerator(labelsMap, getLocale());//FIXMETHEME this and other similar implementations should be able to return null and chart themes should be protected agains null;
	}
	
	
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}


	@Override
	public void collectExpressions(ChartsExpressionCollector collector)
	{
		collector.collect(this);
	}

	
	public Map<Comparable<?>, Map<RegularTimePeriod, JRPrintHyperlink>> getItemHyperlinks()
	{
		return itemHyperlinks;
	}
	
	
	public boolean hasItemHyperlinks()
	{
		boolean foundLinks = false;
		if (timeSeries != null && timeSeries.length > 0)
		{
			for (int i = 0; i < timeSeries.length && !foundLinks; i++)
			{
				foundLinks = timeSeries[i].hasItemHyperlink();
			}
		}
		return foundLinks;
	}


	@Override
	public void validate(ChartsVerifier verifier)
	{
		verifier.verify(this);
	}


}
