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
package net.sf.jasperreports.customizers.shape;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.plot.XYPlot;

import net.sf.jasperreports.charts.JRAbstractChartCustomizer;
import net.sf.jasperreports.customizers.util.ItemsCounter;
import net.sf.jasperreports.customizers.util.SeriesNameProvider;

/**
 * Abstract customizer that provide the utility methods to work with shapes.
 * 
 * @author Marco Orlandin (dejawho2@users.sourceforge.net)
 */
public abstract class AbstractShapeCustomizer extends JRAbstractChartCustomizer
{
	public static final String PROPERTY_SHAPE_WIDTH = "shapeWidth";
	public static final String PROPERTY_SHAPE_HEIGHT = "shapeHeight";
	public static final String PROPERTY_SHAPE_TYPE = "shapeType";
	public static final String PROPERTY_SHAPE_POINTS = "shapePoints";

	protected interface ShapeSetter
	{
		public void setShape(int seriesIndex, Shape shape);
	}

	class XYPlotSeriesNameProvider implements SeriesNameProvider
	{
		private final XYPlot xyPlot;

		public XYPlotSeriesNameProvider(XYPlot xyPlot)
		{
			this.xyPlot = xyPlot;
		}

		@Override
		public String getSeriesName(int index) 
		{
			Comparable<?> key = xyPlot.getDataset().getSeriesKey(index);
			return key == null ? null : String.valueOf(key);
		}
	}
	
	/**
	 * Returns the width of the shape.
	 * 
	 * @return the width of the shape or null if it is not specified
	 */
	protected Integer getWidth() 
	{
		return getIntegerProperty(PROPERTY_SHAPE_WIDTH);
	}

	/**
	 * Returns the height of the shape. 
	 * 
	 * @return the height of the shape or null if it is not specified
	 */
	protected Integer getHeight() 
	{
		return getIntegerProperty(PROPERTY_SHAPE_HEIGHT);
	}

	/**
	 * Builds the shape from the type and the points read from the respective configuration properties.
	 * 
	 * @return the Shape or null if the configuration properties are not valid
	 */
	protected Shape buildShape()
	{
		Shape shape = null;

		ShapeTypeEnum shapeType = ShapeTypeEnum.getByName(getProperty(PROPERTY_SHAPE_TYPE));
		if (shapeType != null)
		{
			switch (shapeType)
			{
				case ELLIPSE :
				{
					shape = buildEllipse();
					break;
				}
				case RECTANGLE :
				{
					shape = buildRectangle();
					break;
				}
				case POLYLINE :
				{
					String shapePoints = getProperty(PROPERTY_SHAPE_POINTS);
					if (shapePoints != null)
					{
						shape = buildPolyline(ShapePoints.decode(shapePoints));
					}
					break;
				}
				case POLYGON :
				{
					String shapePoints = getProperty(PROPERTY_SHAPE_POINTS);
					if (shapePoints != null)
					{
						shape = buildPolygon(ShapePoints.decode(shapePoints));
					}
					break;
				}
			}
		}

		return shape;
	}

	/**
	 * Returns the width and height properties defined, if only one is defined it value will be used 
	 * also for the other, if they are both undefined it will return null.
	 * 
	 * @return a size or null if it is undefined
	 */
	protected Dimension2D getSize()
	{
		Integer width = getWidth();
		Integer height = getHeight();
		if (width == null && height == null) {
			return null;
		} else if (width == null) {
			width = height;
		} else if (height == null) {
			height = width;
		}
		return new Dimension(width, height);
	}

	/**
	 *
	 */
	protected Rectangle2D getBounds(ShapePoints shape)
	{
		Rectangle2D bounds = null;

		List<Point> points = shape.getPoints();
		if (points != null && !points.isEmpty())
		{
			Integer top = null;
			Integer bottom = null;
			Integer left = null;
			Integer right = null;

			for (int i = 0; i < points.size(); i++)
			{
				Point point = points.get(i);

				if (top == null || point.getY() < top)
				{
					top = point.getY();
				}

				if (bottom == null || point.getY() > bottom)
				{
					bottom = point.getY();
				}

				if (left == null || point.getX() < left)
				{
					left = point.getX();
				}

				if (right == null || point.getX() > right)
				{
					right = point.getX();
				}
			}

			bounds = new Rectangle(left, top, right - left, bottom - top);
		}

		return bounds;
	}

	/**
	 *
	 */
	protected abstract Point getOffset(Dimension2D size);

	/**
	 *
	 */
	protected abstract Point getOffset(Rectangle2D bounds);

	/**
	 * Builds an ellipse shape.
	 * 
	 * @return the ellipse or null if its size is not specified
	 */
	protected Shape buildEllipse()
	{
		Ellipse2D ellipse = null;
		Dimension2D size = getSize();
		if (size != null)
		{
			Point offset = getOffset(size);
			ellipse = 
				new Ellipse2D.Float(
					-offset.getX(), 
					-offset.getY(), 
					(float)size.getWidth(), 
					(float)size.getHeight()
					);
		}
		return ellipse;
	}

	/**
	 * Builds a rectangle shape.
	 * 
	 * @return the rectangle or null if its size is not specified
	 */
	protected Shape buildRectangle()
	{
		Rectangle2D rectangle = null;
		Dimension2D size = getSize();
		if (size != null)
		{
			Point offset = getOffset(size);
			rectangle = 
				new Rectangle2D.Float(
					-offset.getX(), 
					-offset.getY(), 
					(float)size.getWidth(), 
					(float)size.getHeight()
					);
		}
		return rectangle;
	}

	/**
	 * Builds a polygon shape.
	 * 
	 * @param shapePoints the points of the polygon
	 * @return the polygon or null if it can't be build from the current configuration
	 */
	protected Shape buildPolygon(ShapePoints shapePoints)
	{
		Polygon polygon = null;
		List<Point> points = shapePoints.getPoints();
		if (points != null && !points.isEmpty())
		{
			float scaleFactorX = 1.0f;
			float scaleFactorY = 1.0f;

			Rectangle2D bounds = getBounds(shapePoints);
			Integer width = getWidth();
			Integer height = getHeight();
			if (width != null) {
				scaleFactorX = (float)width / (float)bounds.getWidth();
			}
			if (height != null) {
				scaleFactorY = (float)height / (float)bounds.getHeight();
			}

			Point offset = getOffset(bounds);

			int[] pointsX = new int[points.size()];
			int[] pointsY = new int[points.size()];

			for (int i = 0; i < points.size(); i++)
			{
				Point point = points.get(i);
				pointsX[i] = Math.round((point.getX() - offset.getX()) * scaleFactorX);
				pointsY[i] = Math.round((point.getY() - offset.getY()) * scaleFactorY);
			}

			polygon = new Polygon(pointsX, pointsY, points.size());
		}
		return polygon;
	}

	/**
	 * Uses the points to build a polyline
	 * 
	 * @param baseShape the points of the polyline
	 * @return a polyline shape or null if it can't be build from the current configuration
	 */
	protected Shape buildPolyline(ShapePoints baseShape)
	{
		Path2D path = null;
		List<Point> points = baseShape.getPoints();
		if (points != null && !points.isEmpty())
		{
			float scaleFactorX = 1.0f;
			float scaleFactorY = 1.0f;

			Rectangle2D bounds = getBounds(baseShape);
			Integer width = getWidth();
			Integer height = getHeight();
			if (width != null) {
				scaleFactorX = (float)(width / bounds.getWidth());
			}
			if (height != null) {
				scaleFactorY = (float)(height / bounds.getHeight());
			}

			path = new Path2D.Double();

			if (points.size() > 1)
			{
				Point offset = getOffset(bounds);
				Point point = points.get(0);

				path.moveTo(
					(point.getX() - offset.getX()) * scaleFactorX, 
					(point.getY() - offset.getY()) * scaleFactorY
					);
				for (int i = 1; i < points.size(); i++)
				{
					point = points.get(i);
					path.lineTo(
						(point.getX() - offset.getX()) * scaleFactorX, 
						(point.getY() - offset.getY()) * scaleFactorY
						);
				}
			}
		}
		return path;
	}

	/**
	 * Apply the shaped defined in the configuration to the ShapeSetter in the specified
	 * index of the passed parameter
	 */
	protected void updateItem(ItemsCounter itemsCounter, ShapeSetter shapeSetter, int index)
	{
		if (
			index >= 0
			&& index < itemsCounter.getCount()
			)
		{
			Shape shape = buildShape(); 
			if (shape != null)
			{
				shapeSetter.setShape(index, shape);
			}
		}	
	}

	/**
	 * Update all the items in the collection
	 */
	protected void updateItems(ItemsCounter itemsCounter, ShapeSetter shapeSetter)
	{
		Shape shape = buildShape(); 
		if (shape != null)
		{
			for (int i= 0; i < itemsCounter.getCount(); i++)
			{
				shapeSetter.setShape(i, shape);
			}	
		}
	}
}
