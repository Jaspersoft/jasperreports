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
import net.sf.jasperreports.charts.JRXyzDataset;
import net.sf.jasperreports.charts.JRXyzSeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRDesignXyzDataset extends JRDesignChartDataset implements JRXyzDataset {
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_XYZ_SERIES = "xyzSeries";
	
	private List<JRXyzSeries> xyzSeriesList = new ArrayList<>();


	@JsonCreator
	private JRDesignXyzDataset()
	{
		this(null);
	}


	/**
	 *
	 */
	public JRDesignXyzDataset(JRChartDataset dataset)
	{
		super(dataset);
	}


	@Override
	public JRXyzSeries[] getSeries()
	{
		JRXyzSeries[] xyzSeriesArray = new JRXyzSeries[ xyzSeriesList.size() ];
		xyzSeriesList.toArray( xyzSeriesArray );
		
		return xyzSeriesArray;
	}
	
	/**
	 * 
	 */
	@JsonIgnore
	public List<JRXyzSeries> getSeriesList()
	{
		return xyzSeriesList;
	}

	@JsonSetter
	private void setSeries(List<JRXyzSeries> series)
	{
		if (series != null)
		{
			for (JRXyzSeries s : series)
			{
				addXyzSeries(s);
			}
		}
	}

	/**
	 * 
	 */
	public void addXyzSeries( JRXyzSeries xyzSeries ) 
	{
		xyzSeriesList.add( xyzSeries );
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_XYZ_SERIES, 
				xyzSeries, xyzSeriesList.size() - 1);
	}
	
	/**
	 * 
	 */
	public void addXyzSeries(int index, JRXyzSeries xyzSeries ) 
	{
		xyzSeriesList.add(index, xyzSeries );
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_XYZ_SERIES, 
				xyzSeries, index);
	}
	
	/**
	 * 
	 */
	public JRXyzSeries removeXyzSeries( JRXyzSeries xyzSeries ) 
	{
		if ( xyzSeries != null ){
			int idx = xyzSeriesList.indexOf(xyzSeries);
			if (idx >= 0) {
				xyzSeriesList.remove(idx);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_XYZ_SERIES, xyzSeries, idx);
			}
		}
		
		return xyzSeries;
	}
	
	@Override
	public byte getDatasetType() {
		return JRChartDataset.XYZ_DATASET;
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
		JRDesignXyzDataset clone = (JRDesignXyzDataset)super.clone();
		clone.xyzSeriesList = JRCloneUtils.cloneList(xyzSeriesList);
		return clone;
	}
}
