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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.charts.util.XYDatasetLabelGenerator;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.util.Pair;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillXyDataset extends JRFillChartDataset implements JRXyDataset
{

	/**
	 *
	 */
	protected JRFillXySeries[] xySeries;

	private List<Comparable<?>> seriesNames;
	private Map<Comparable<?>, XYSeries> seriesMap;
	private Map<Comparable<?>, Map<Number, String>> labelsMap;
	
	private Map<Comparable<?>, Map<Pair, JRPrintHyperlink>> itemHyperlinks;
	
	
	/**
	 *
	 */
	public JRFillXyDataset(JRXyDataset xyDataset, ChartsFillObjectFactory factory)
	{
		super(xyDataset, factory.getParent());

		/*   */
		JRXySeries[] srcXySeries = xyDataset.getSeries();
		if (srcXySeries != null && srcXySeries.length > 0)
		{
			xySeries = new JRFillXySeries[srcXySeries.length];
			for (int i = 0; i < xySeries.length; i++)
			{
				xySeries[i] = (JRFillXySeries)factory.getXySeries(srcXySeries[i]);
			}
		}
	}
	
	
	@Override
	public JRXySeries[] getSeries()
	{
		return xySeries;
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
		if (xySeries != null && xySeries.length > 0)
		{
			for (JRFillXySeries crtXySeries : xySeries)
			{
				crtXySeries.evaluate(calculator);
			}
		}
	}

	
	@Override
	protected void customIncrement()
	{
		if (xySeries != null && xySeries.length > 0)
		{
			if (seriesNames == null)
			{
				seriesNames = new ArrayList<>();
				seriesMap = new HashMap<>();
				labelsMap = new HashMap<>();
				itemHyperlinks = new HashMap<>();
			}

			for (JRFillXySeries crtXySeries : xySeries)
			{
				Comparable<?> seriesName = crtXySeries.getSeries();
				XYSeries xySrs = seriesMap.get(seriesName);
				if (xySrs == null)
				{
					boolean autoSort = crtXySeries.getAutoSort() == null ? true : crtXySeries.getAutoSort();
					xySrs =  new XYSeries(seriesName, autoSort);
					seriesNames.add(seriesName);
					seriesMap.put(seriesName, xySrs);
				}
				
				xySrs.addOrUpdate(
					crtXySeries.getXValue(), 
					crtXySeries.getYValue()
					);
				
				if (crtXySeries.getLabelExpression() != null)
				{
					labelsMap.computeIfAbsent(seriesName, k -> new HashMap<>()).put(crtXySeries.getXValue(), crtXySeries.getLabel());
				}
				
				if (crtXySeries.hasItemHyperlinks())
				{
					itemHyperlinks.computeIfAbsent(seriesName, k -> new HashMap<>())
						.put(new Pair<>(crtXySeries.getXValue(), crtXySeries.getYValue()), crtXySeries.getPrintItemHyperlink());
				}
			}
		}
	}

	
	@Override
	public Dataset getCustomDataset()
	{
		XYSeriesCollection dataset = new XYSeriesCollection();
		if (seriesNames != null)
		{
			for (Comparable<?> seriesName : seriesNames)
			{
				dataset.addSeries(seriesMap.get(seriesName));
			}
		}
		return dataset;
	}

	
	@Override
	public byte getDatasetType() {
		return JRChartDataset.XY_DATASET;
	}
	
	
	@Override
	public Object getLabelGenerator(){
		return new XYDatasetLabelGenerator(labelsMap, getLocale());
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

	
	public Map<Comparable<?>, Map<Pair, JRPrintHyperlink>> getItemHyperlinks()
	{
		return itemHyperlinks;
	}
	
	
	public boolean hasItemHyperlinks()
	{
		boolean foundLinks = false;
		if (xySeries != null && xySeries.length > 0)
		{
			for (int i = 0; i < xySeries.length && !foundLinks; i++)
			{
				JRFillXySeries serie = xySeries[i];
				foundLinks = serie.hasItemHyperlinks();
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
