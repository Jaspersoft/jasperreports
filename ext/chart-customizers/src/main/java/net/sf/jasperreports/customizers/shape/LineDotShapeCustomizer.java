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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.customizers.util.CategoryCounter;
import net.sf.jasperreports.customizers.util.CategorySeriesNameProvider;
import net.sf.jasperreports.customizers.util.CustomizerUtil;
import net.sf.jasperreports.customizers.util.ItemsCounter;
import net.sf.jasperreports.customizers.util.SeriesNameProvider;

/**
 * Chart customizer to define the shape and the size of the symbol along the line.
 * It works for {@link LineAndShapeRenderer} and {@link XYLineAndShapeRenderer}
 * 
 * @author Marco Orlandin (dejawho2@users.sourceforge.net)
 */
public class LineDotShapeCustomizer extends AbstractShapeCustomizer 
{
	protected class XYPlotSeriesCounter implements ItemsCounter
	{
		private final XYPlot xyPlot;

		public XYPlotSeriesCounter(XYPlot xyPlot)
		{
			this.xyPlot = xyPlot;
		}

		@Override
		public int getCount() 
		{
			return xyPlot.getSeriesCount();
		}
	}

	protected class AbstractRendererSeriesShapeSetter implements ShapeSetter
	{
		private final AbstractRenderer renderer;

		public AbstractRendererSeriesShapeSetter(AbstractRenderer renderer)
		{
			this.renderer = renderer;
		}

		@Override
		public void setShape(int seriesIndex, Shape shape) 
		{
			renderer.setSeriesShape(seriesIndex, shape);
		}
	}

	@Override
	public void customize(JFreeChart jfc, JRChart jrc) 
	{
		Plot plot = jfc.getPlot();

		ItemsCounter itemsCounter = null;
		SeriesNameProvider seriesNameProvider = null;
		Object renderer = null;

		if (plot instanceof XYPlot)
		{
			XYPlot xyPlot = jfc.getXYPlot();
			renderer = xyPlot.getRenderer();
			itemsCounter = new XYPlotSeriesCounter(xyPlot);
			seriesNameProvider = new XYPlotSeriesNameProvider(xyPlot);
		}
		else if (plot instanceof CategoryPlot)
		{
			CategoryPlot categoryPlot = jfc.getCategoryPlot(); 
			renderer = categoryPlot.getRenderer();
			itemsCounter = new CategoryCounter(categoryPlot);
			seriesNameProvider = new CategorySeriesNameProvider(categoryPlot);
		}

		Integer seriesItemIndex = CustomizerUtil.resolveIndex(this, itemsCounter, seriesNameProvider);
		if (
			seriesItemIndex != null
			&& renderer instanceof AbstractRenderer
			)
		{
			ShapeSetter shapeSetter = new AbstractRendererSeriesShapeSetter((AbstractRenderer)renderer);
			if (seriesItemIndex == -1)
			{
				updateItems(itemsCounter, shapeSetter);
			}
			else
			{
				updateItem(itemsCounter, shapeSetter, seriesItemIndex);	
			}
		}
	}

	@Override
	protected Point getOffset(Dimension2D size)
	{
		return
			new Point(
				(int)(size.getWidth() / 2),
				(int)(size.getHeight() / 2)
				);
	}

	@Override
	protected Point getOffset(Rectangle2D bounds)
	{
		return
			new Point(
				(int)(bounds.getWidth() / 2 + bounds.getX()),
				(int)(bounds.getHeight() / 2 + bounds.getY())
				);
	}
}
