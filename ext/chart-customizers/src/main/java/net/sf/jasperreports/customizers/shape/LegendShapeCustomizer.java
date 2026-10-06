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
package net.sf.jasperreports.customizers.shape;

import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.customizers.util.CategorySeriesNameProvider;
import net.sf.jasperreports.customizers.util.CustomizerUtil;
import net.sf.jasperreports.customizers.util.ItemsCounter;
import net.sf.jasperreports.customizers.util.SeriesNameProvider;

/**
 * Customizer to define the shape of a specific legend item, works for both 
 * XY and Category plot. The shape and other informations are encoded into a
 * JSON string.
 * 
 * @author Marco Orlandin (dejawho2@users.sourceforge.net)
 */
public class LegendShapeCustomizer extends AbstractShapeCustomizer 
{
	class LegendItemsCounter implements ItemsCounter
	{
		private final Plot plot;
		
		public LegendItemsCounter(Plot plot)
		{
			this.plot = plot;
		}
		
		@Override
		public int getCount() 
		{
			return plot.getLegendItems().getItemCount();
		}
	}

	protected class AbstractRendererLegendShapeSetter implements ShapeSetter
	{
		private final AbstractRenderer renderer;

		public AbstractRendererLegendShapeSetter(AbstractRenderer renderer)
		{
			this.renderer = renderer;
		}

		@Override
		public void setShape(int seriesIndex, Shape shape) 
		{
			renderer.setLegendShape(seriesIndex, shape);
		}
	}

	@Override
	public void customize(JFreeChart jfc, JRChart jrc) 
	{
		Plot plot = jfc.getPlot();

		ItemsCounter itemsCounter = new LegendItemsCounter(plot);
		SeriesNameProvider seriesNameProvider = null;
		Object renderer = null;

		if (plot instanceof XYPlot)
		{
			XYPlot xyPlot = jfc.getXYPlot();
			renderer = xyPlot.getRenderer();
			seriesNameProvider = new XYPlotSeriesNameProvider(xyPlot);
		}
		else if (plot instanceof CategoryPlot)
		{
			CategoryPlot categoryPlot = jfc.getCategoryPlot(); 
			renderer = categoryPlot.getRenderer();
			seriesNameProvider = new CategorySeriesNameProvider(categoryPlot);
		}

		Integer legendItemIndex = CustomizerUtil.resolveIndex(this, itemsCounter, seriesNameProvider);
		if (
			legendItemIndex != null
			&& renderer instanceof AbstractRenderer
			)
		{
			ShapeSetter shapeSetter = new AbstractRendererLegendShapeSetter((AbstractRenderer)renderer);
			if (legendItemIndex == -1)
			{
				updateItems(itemsCounter, shapeSetter);
			}
			else
			{
				updateItem(itemsCounter, shapeSetter, legendItemIndex);
			}
		}
	}

	@Override
	protected Point getOffset(Dimension2D size)
	{
		return new Point(0, 0);
	}

	@Override
	protected Point getOffset(Rectangle2D bounds)
	{
		return new Point(0, 0);
	}
}
