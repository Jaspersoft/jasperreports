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

import java.util.HashMap;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.design.ChartsVerifier;
import net.sf.jasperreports.charts.util.CategoryLabelGenerator;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillCategoryDataset extends JRFillChartDataset implements JRCategoryDataset
{

	public static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_NAME = "charts.category.dataset.series.null.name";
	
	/**
	 *
	 */
	protected JRFillCategorySeries[] categorySeries;

	private DefaultCategoryDataset dataset;
	private Map<Comparable<?>, Map<Comparable<?>, String>> labelsMap;
	
	private Map<Comparable<?>, Map<Comparable<?>, JRPrintHyperlink>> itemHyperlinks;

	
	/**
	 *
	 */
	public JRFillCategoryDataset(JRCategoryDataset categoryDataset, ChartsFillObjectFactory factory)
	{
		super(categoryDataset, factory.getParent());

		/*   */
		JRCategorySeries[] srcCategorySeries = categoryDataset.getSeries();
		if (srcCategorySeries != null && srcCategorySeries.length > 0)
		{
			categorySeries = new JRFillCategorySeries[srcCategorySeries.length];
			for (int i = 0; i < categorySeries.length; i++)
			{
				categorySeries[i] = (JRFillCategorySeries)factory.getCategorySeries(srcCategorySeries[i]);
			}
		}
	}
	
	
	@Override
	public JRCategorySeries[] getSeries()
	{
		return categorySeries;
	}


	@Override
	protected void customInitialize()
	{
		dataset = null;
		labelsMap = null;
		itemHyperlinks = null;
	}

	@Override
	protected void customEvaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		if (categorySeries != null && categorySeries.length > 0)
		{
			for (int i = 0; i < categorySeries.length; i++)
			{
				categorySeries[i].evaluate(calculator);
			}
		}
	}

	@Override
	protected void customIncrement()
	{
		if (categorySeries != null && categorySeries.length > 0)
		{
			if (dataset == null)
			{
				dataset = new DefaultCategoryDataset();
				labelsMap = new HashMap<>();
				itemHyperlinks = new HashMap<>();
			}
			
			for (int i = 0; i < categorySeries.length; i++)
			{
				JRFillCategorySeries crtCategorySeries = categorySeries[i];
				
				Comparable<?> seriesName = crtCategorySeries.getSeries();
				if (seriesName == null)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_SERIES_NULL_NAME,  
							(Object[])null 
							);
				}

				dataset.addValue(
					crtCategorySeries.getValue(), 
					crtCategorySeries.getSeries(), 
					crtCategorySeries.getCategory()
					);

				if (crtCategorySeries.getLabelExpression() != null)
				{
					labelsMap.computeIfAbsent(seriesName, k -> new HashMap<>()).put(crtCategorySeries.getCategory(), crtCategorySeries.getLabel());
				}
				
				if (crtCategorySeries.hasItemHyperlinks())
				{
					itemHyperlinks.computeIfAbsent(seriesName, k -> new HashMap<>()).put(crtCategorySeries.getCategory(), crtCategorySeries.getPrintItemHyperlink());
				}
			}
		}
	}

	@Override
	public Dataset getCustomDataset()
	{
		//TODO create an empty dataset when there was no data?
		return dataset;
	}


	@Override
	public byte getDatasetType() {
		return JRChartDataset.CATEGORY_DATASET;
	}

	
	@Override
	public Object getLabelGenerator()
	{
		return new CategoryLabelGenerator(labelsMap, getLocale());
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

	
	public Map<Comparable<?>, Map<Comparable<?>, JRPrintHyperlink>> getItemHyperlinks()
	{
		return itemHyperlinks;
	}
	
	
	public boolean hasItemHyperlinks()
	{
		boolean foundLinks = false;
		if (categorySeries != null && categorySeries.length > 0)
		{
			for (int i = 0; i < categorySeries.length && !foundLinks; i++)
			{
				JRFillCategorySeries serie = categorySeries[i];
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
