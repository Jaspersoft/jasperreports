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
package net.sf.jasperreports.charts.design;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.util.JRCloneUtils;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignXyDataset extends JRDesignChartDataset implements JRXyDataset
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_XY_SERIES = "xySeries";

	private List<JRXySeries> xySeriesList = new ArrayList<>();


	@JsonCreator
	private JRDesignXyDataset()
	{
		this(null);
	}


	/**
	 *
	 */
	public JRDesignXyDataset(JRChartDataset dataset)
	{
		super(dataset);
	}


	@Override
	public JRXySeries[] getSeries()
	{
		JRXySeries[] xySeriesArray = new JRXySeries[xySeriesList.size()];
		
		xySeriesList.toArray(xySeriesArray);

		return xySeriesArray;
	}
	

	/**
	 * 
	 */
	@JsonIgnore
	public List<JRXySeries> getSeriesList()
	{
		return xySeriesList;
	}

	
	@JsonSetter
	private void setSeries(List<JRXySeries> series)
	{
		if (series != null)
		{
			for (JRXySeries s : series)
			{
				addXySeries(s);
			}
		}
	}

	
	/**
	 *
	 */
	public void addXySeries(JRXySeries xySeries)
	{
		xySeriesList.add(xySeries);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_XY_SERIES, 
				xySeries, xySeriesList.size() - 1);
	}
	
	/**
	 *
	 */
	public void addXySeries(int index, JRXySeries xySeries)
	{
		xySeriesList.add(index, xySeries);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_XY_SERIES, 
				xySeries, index);
	}
	

	/**
	 *
	 */
	public JRXySeries removeXySeries(JRXySeries xySeries)
	{
		if (xySeries != null)
		{
			int idx = xySeriesList.indexOf(xySeries);
			if (idx >= 0)
			{
				xySeriesList.remove(idx);
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_XY_SERIES, xySeries, idx);
			}
		}
		
		return xySeries;
	}


	@Override
	public byte getDatasetType() {
		return JRChartDataset.XY_DATASET;
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
		JRDesignXyDataset clone = (JRDesignXyDataset)super.clone();
		clone.xySeriesList = JRCloneUtils.cloneList(xySeriesList);
		return clone;
	}
}
