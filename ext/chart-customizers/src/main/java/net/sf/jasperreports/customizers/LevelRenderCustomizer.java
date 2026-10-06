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
package net.sf.jasperreports.customizers;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LevelRenderer;

import net.sf.jasperreports.charts.JRAbstractChartCustomizer;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.customizers.util.CategoryCounter;
import net.sf.jasperreports.customizers.util.CategorySeriesNameProvider;
import net.sf.jasperreports.customizers.util.CustomizerUtil;
import net.sf.jasperreports.customizers.util.ItemsCounter;

/**
 * @author Marco Orlandin (dejawho2@users.sourceforge.net)
 */
public class LevelRenderCustomizer extends JRAbstractChartCustomizer
{
	public static final String PROPERTY_ITEM_MARIGN = "itemMargin";
	public static final String PROPERTY_MAX_ITEM_WIDTH = "maxItemWidth";


	@Override
	public void customize(JFreeChart jfc, JRChart jrc) 
	{
		if (jfc.getPlot() instanceof CategoryPlot)
		{
			CategoryPlot plot = (CategoryPlot)jfc.getPlot();

			ItemsCounter itemsCounter = new CategoryCounter(plot);
			
			Integer seriesIndex = 
				CustomizerUtil.resolveIndex(
					this, 
					itemsCounter,
					new CategorySeriesNameProvider(plot)
					);
			if (seriesIndex != null)
			{
				LevelRenderer levelRenderer = new LevelRenderer();

				Double itemMargin = getDoubleProperty(PROPERTY_ITEM_MARIGN);
				if (itemMargin != null)
				{
					levelRenderer.setItemMargin(itemMargin);
				}
				Double maxItemWidth = getDoubleProperty(PROPERTY_MAX_ITEM_WIDTH);
				if (maxItemWidth != null)
				{
					levelRenderer.setMaximumItemWidth(maxItemWidth);
				}

				if (seriesIndex == -1)
				{
					for (int i = 0; i < itemsCounter.getCount(); i++)
					{
						plot.setRenderer(i, levelRenderer);
					}
				}
				else
				{
					plot.setRenderer(seriesIndex, levelRenderer);
				}
			}
		}
	}
}
