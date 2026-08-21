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
package xchart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRFillElementDataset;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FillXYDataset extends JRFillElementDataset implements XYDataset
{

	private final XYDataset dataset;
	
	private FillXYSeries[] xySeries;
	
	private Map<Comparable<?>, XYSeriesData> xySeriesMap;
	private List<Comparable<?>> xySeriesNames;
	
	public FillXYDataset(XYDataset dataset,	JRFillObjectFactory factory)
	{
		super(dataset, factory);
		
		XYSeries[] srcXySeries = dataset.getSeries();
		if (srcXySeries != null && srcXySeries.length > 0)
		{
			xySeries = new FillXYSeries[srcXySeries.length];
			for (int i = 0; i < xySeries.length; i++)
			{
				xySeries[i] = new FillXYSeries(srcXySeries[i], factory);
			}
		}
		this.dataset = dataset;
	}

	@Override
	protected void customEvaluate(JRCalculator calculator)
			throws JRExpressionEvalException
	{
		if (xySeries != null && xySeries.length > 0)
		{
			for (int i = 0; i < xySeries.length; i++)
			{
				xySeries[i].evaluate(calculator);
			}
		}
	}

	@Override
	protected void customIncrement()
	{
		if (xySeries != null && xySeries.length > 0)
		{
			if (xySeriesMap == null)
			{
				xySeriesMap = new HashMap<Comparable<?>, XYSeriesData>();
				xySeriesNames = new ArrayList<Comparable<?>>();
			}

			for (int i = 0; i < xySeries.length; i++)
			{
				FillXYSeries crtXySeries = xySeries[i];

				Comparable<?> seriesName = crtXySeries.getSeries();
				XYSeriesData xySeriesData = xySeriesMap.get(seriesName);
				if (xySeriesData == null)
				{
					xySeriesData =  new XYSeriesData();
					xySeriesMap.put(seriesName, xySeriesData);
					xySeriesNames.add(seriesName);
				}
				
				xySeriesData.getXData().add(crtXySeries.getXValue());
				xySeriesData.getYData().add(crtXySeries.getYValue());
				if (crtXySeries.getColor() != null)
				{
					xySeriesData.setColor(crtXySeries.getColor());
				}
			}
		}
	}

	@Override
	protected void customInitialize()
	{
		xySeriesMap = null;
		xySeriesNames = null;
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		XYChartCompiler.collectExpressions(dataset, collector);
	}

	@Override
	public XYSeries[] getSeries()
	{
		return xySeries;
	}

	protected void finishDataset()
	{
		//one last increment is required in certain cases
		increment();
	}
	
	public Map<Comparable<?>, XYSeriesData> getXYSeriesMap()
	{
		return xySeriesMap;
	}
	
	public List<Comparable<?>> getXYSeriesNames()
	{
		return xySeriesNames;
	}
}
