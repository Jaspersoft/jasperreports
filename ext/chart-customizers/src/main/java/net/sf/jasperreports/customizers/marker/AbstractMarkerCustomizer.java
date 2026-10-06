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
package net.sf.jasperreports.customizers.marker;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleInsets;

import net.sf.jasperreports.charts.JRAbstractChartCustomizer;
import net.sf.jasperreports.customizers.type.LayerEnum;
import net.sf.jasperreports.customizers.type.RectangleAnchorEnum;
import net.sf.jasperreports.customizers.type.StrokeStyleEnum;
import net.sf.jasperreports.customizers.type.TextAnchorEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractMarkerCustomizer extends JRAbstractChartCustomizer 
{
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_LABEL_ANCHOR = "labelAnchor";
	public static final String PROPERTY_LABEL_OFFSET_TOP = "labelOffsetTop";
	public static final String PROPERTY_LABEL_OFFSET_LEFT = "labelOffsetLeft";
	public static final String PROPERTY_LABEL_OFFSET_BOTTOM = "labelOffsetBottom";
	public static final String PROPERTY_LABEL_OFFSET_RIGHT = "labelOffsetRight";
	public static final String PROPERTY_LABEL_TEXT_ANCHOR = "labelTextAnchor";

	public static final String PROPERTY_COLOR = "color";
	public static final String PROPERTY_ALPHA = "alpha";

	public static final String PROPERTY_STROKE_WIDTH = "strokeWidth";
	public static final String PROPERTY_STROKE_STYLE = "strokeStyle";

	public static final String PROPERTY_LAYER = "layer";


	protected void configureMarker(Marker marker)
	{
		String label = getProperty(PROPERTY_LABEL);
		if (label != null && label.length() > 0)
		{
			marker.setLabel(label);
		}

		RectangleAnchorEnum labelAnchor = RectangleAnchorEnum.getByName(getProperty(PROPERTY_LABEL_ANCHOR));
		if (labelAnchor != null)
		{
			marker.setLabelAnchor(labelAnchor.getRectangleAnchor());
		}

		Double offsetTop = getDoubleProperty(PROPERTY_LABEL_OFFSET_TOP);
		Double offsetLeft = getDoubleProperty(PROPERTY_LABEL_OFFSET_LEFT);
		Double offsetBottom = getDoubleProperty(PROPERTY_LABEL_OFFSET_BOTTOM);
		Double offsetRight = getDoubleProperty(PROPERTY_LABEL_OFFSET_RIGHT);
		if (
			offsetTop != null
			|| offsetLeft != null
			|| offsetBottom != null
			|| offsetRight != null
			)
		{
			RectangleInsets currentOffset = marker.getLabelOffset();
			marker.setLabelOffset(
				new RectangleInsets(
					offsetTop == null ? currentOffset.getTop() : offsetTop, 
					offsetLeft == null ? currentOffset.getLeft() : offsetLeft, 
					offsetBottom == null ? currentOffset.getBottom() : offsetBottom, 
					offsetRight == null ? currentOffset.getRight() : offsetRight
					)
				);
		}

		TextAnchorEnum labelTextAnchor = TextAnchorEnum.getByName(getProperty(PROPERTY_LABEL_TEXT_ANCHOR));
		if (labelTextAnchor != null) 
		{
			marker.setLabelTextAnchor(labelTextAnchor.getTextAnchor());
		}

		Color color = JRColorUtil.getColor(getProperty(PROPERTY_COLOR), null);
		if (color != null)
		{
			marker.setPaint(color);
		}

		Float alpha = getFloatProperty(PROPERTY_ALPHA);
		if (alpha != null)
		{
			marker.setAlpha(alpha);
		}
	}


	protected void configureStroke(Marker marker)
	{
		Float strokeWidth = getFloatProperty(PROPERTY_STROKE_WIDTH);
		if (strokeWidth == null)
		{
			strokeWidth = 1f;
		}

		BasicStroke basicStroke = getStroke(strokeWidth);

		marker.setStroke(basicStroke);
	}


	protected BasicStroke getStroke(Float strokeWidth)
	{
		BasicStroke basicStroke = new BasicStroke(strokeWidth);

		StrokeStyleEnum strokeStyle = StrokeStyleEnum.getByName(getProperty(PROPERTY_STROKE_STYLE));
		if (strokeStyle != null)
		{
			switch (strokeStyle)
			{
				case SOLID : 
				{
					//do nothing; already created stroke is good
					break;
				}
				case DOTTED : 
				{
					basicStroke = 
						new BasicStroke(
							basicStroke.getLineWidth(), 
							basicStroke.getEndCap(),
							basicStroke.getLineJoin(), 
							basicStroke.getMiterLimit(), 
							new float[] { 1.0f, 1.0f },
							basicStroke.getDashPhase()
							);
					break;
				}
				case DASHED : 
				{
					basicStroke = 
						new BasicStroke(
							basicStroke.getLineWidth(), 
							basicStroke.getEndCap(),
							basicStroke.getLineJoin(), 
							basicStroke.getMiterLimit(), 
							new float[] { 10.0f, 10.0f },
							basicStroke.getDashPhase()
							);
					break;
				}
			}
		}
		
		return basicStroke;
	}


	protected abstract void addMarker(Plot plot, Marker marker);


	protected Layer getLayer() 
	{
		LayerEnum layer = LayerEnum.getByName(getProperty(PROPERTY_LAYER));
		return layer == null ? LayerEnum.BACKGROUND.getLayer() : layer.getLayer();
	}
}
