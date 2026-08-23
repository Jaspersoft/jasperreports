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
package net.sf.jasperreports.components.spiderchart;

import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.DefaultCategoryDataset;

import net.sf.jasperreports.charts.JRCategorySeries;
import net.sf.jasperreports.charts.fill.ChartsFillObjectFactory;
import net.sf.jasperreports.charts.fill.JRFillCategorySeries;
import net.sf.jasperreports.charts.util.CategoryLabelGenerator;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRFillElementDataset;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FillSpiderDataset extends JRFillElementDataset implements SpiderDataset
{

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String EXCEPTION_MESSAGE_KEY_SERIES_NULL_NAME = "components.spiderchart.category.dataset.series.null.name";
	/**
	 *
	 */
	protected JRFillCategorySeries[] categorySeries;

	private DefaultCategoryDataset dataset;
	private Map<Comparable<?>, Map<Comparable<?>, String>> labelsMap;
	
	private Map<Comparable<?>,Map<Comparable<?>,JRPrintHyperlink>> itemHyperlinks;

	
	/**
	 *
	 */
	public FillSpiderDataset(SpiderDataset spiderDataset, ChartsFillObjectFactory factory)
	{
		super(spiderDataset, factory.getParent());

		/*   */
		JRCategorySeries[] srcCategorySeries = spiderDataset.getSeries();
		if (srcCategorySeries != null && srcCategorySeries.length > 0)
		{
			categorySeries = new JRFillCategorySeries[srcCategorySeries.length];
			for (int i = 0; i < srcCategorySeries.length; i++)
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
			
			for (JRFillCategorySeries crtCategorySeries : categorySeries)
			{
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
					labelsMap.computeIfAbsent(
						seriesName,
						k -> new HashMap<>()
						).put(crtCategorySeries.getCategory(), crtCategorySeries.getLabel());
				}
				
				if (crtCategorySeries.hasItemHyperlinks())
				{
					itemHyperlinks.computeIfAbsent(
						seriesName,
						k -> new HashMap<>()
						).put(crtCategorySeries.getCategory(), crtCategorySeries.getPrintItemHyperlink());
				}
			}
		}
	}

	/**
	 *
	 */
	public DefaultCategoryDataset getCustomDataset()
	{
		return dataset;
	}

	/**
	 *
	 */
	public StandardCategoryItemLabelGenerator getLabelGenerator()
	{
		//do we need to use the fill locale here?  apparently not.
		return labelsMap != null ? new CategoryLabelGenerator(labelsMap) : new StandardCategoryItemLabelGenerator();
	}


	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		SpiderChartCompiler.collectExpressions(this, collector);
	}

	
	public Map<Comparable<?>,Map<Comparable<?>,JRPrintHyperlink>> getItemHyperlinks()
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

	public void finishDataset()
	{
		//one last increment is required in certain cases
		increment();
	}

	
}
