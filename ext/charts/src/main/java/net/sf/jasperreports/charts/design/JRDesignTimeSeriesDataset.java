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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRTimeSeries;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.type.TimePeriodEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRDesignTimeSeriesDataset extends JRDesignChartDataset implements JRTimeSeriesDataset {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_TIME_PERIOD = "timePeriod";
	
	public static final String PROPERTY_TIME_SERIES = "timeSeries";
	
	private List<JRTimeSeries> timeSeriesList = new ArrayList<>();
	private TimePeriodEnum timePeriod;
	

	@JsonCreator
	private JRDesignTimeSeriesDataset()
	{
		this(null);
	}

	/**
	 * 
	 */
	public JRDesignTimeSeriesDataset( JRChartDataset dataset )
	{
		super( dataset );
	}

	@Override
	public JRTimeSeries[] getSeries()
	{
		JRTimeSeries[] timeSeriesArray = new JRTimeSeries[ timeSeriesList.size() ];
		timeSeriesList.toArray( timeSeriesArray );
		
		return timeSeriesArray;
	}
	
	/**
	 * 
	 */
	@JsonIgnore
	public List<JRTimeSeries> getSeriesList()
	{
		return timeSeriesList;
	}

	@JsonSetter
	private void setSeries(List<JRTimeSeries> series)
	{
		if (series != null)
		{
			for (JRTimeSeries s : series)
			{
				addTimeSeries(s);
			}
		}
	}

	/**
	 * 
	 */
	public void addTimeSeries( JRTimeSeries timeSeries ) 
	{
		timeSeriesList.add( timeSeries );
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_TIME_SERIES, 
				timeSeries, timeSeriesList.size() - 1);
	}
	
	/**
	 * 
	 */
	public void addTimeSeries(int index, JRTimeSeries timeSeries ) 
	{
		timeSeriesList.add(index, timeSeries );
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_TIME_SERIES, 
				timeSeries, index);
	}
	
	/**
	 * 
	 */
	public JRTimeSeries removeTimeSeries( JRTimeSeries timeSeries ) 
	{
		if ( timeSeries != null)
		{
			int idx = timeSeriesList.indexOf(timeSeries);
			if (idx >= 0)
			{
				timeSeriesList.remove(idx);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_TIME_SERIES, timeSeries, idx);
			}
		}
		
		return timeSeries;
	}

	@Override
	public TimePeriodEnum getTimePeriod() 
	{
		return timePeriod;
	}
	
	@Override
	public void setTimePeriod(TimePeriodEnum timePeriod)
	{
		Object old = this.timePeriod;
		this.timePeriod = timePeriod;
		getEventSupport().firePropertyChange(PROPERTY_TIME_PERIOD, old, this.timePeriod);
	}

	@Override
	public byte getDatasetType() 
	{
		return JRChartDataset.TIMESERIES_DATASET;
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


	@Override
	public void validate(ChartsVerifier verifier)
	{
		verifier.verify(this);
	}

	@Override
	public Object clone() 
	{
		JRDesignTimeSeriesDataset clone = (JRDesignTimeSeriesDataset)super.clone();
		clone.timeSeriesList = JRCloneUtils.cloneList(timeSeriesList);
		return clone;
	}
}
