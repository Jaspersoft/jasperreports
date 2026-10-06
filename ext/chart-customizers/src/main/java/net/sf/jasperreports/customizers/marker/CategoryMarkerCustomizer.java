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

import java.awt.Font;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.Plot;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.engine.fonts.FontUtil;

/**
 * This customizer plots a line chart as spline.
 * 
 * @author Giulio Toffoli (gt78@users.sourceforge.net)
 */
public class CategoryMarkerCustomizer extends AbstractMarkerCustomizer
{
	public static final String PROPERTY_CATEGORY = "category";
	public static final String PROPERTY_FONT_NAME = "fontName";
	public static final String PROPERTY_FONT_SIZE = "fontSize";
	public static final String PROPERTY_FONT_BOLD = "isBold";
	public static final String PROPERTY_FONT_ITALIC = "isItalic";

	public static String PROPERTY_DRAW_AS_LINE = "drawAsLine";

	@Override
	public void customize(JFreeChart jfc, JRChart jrc) 
	{
		Plot plot = jfc.getPlot();
		if (plot instanceof CategoryPlot && ((CategoryPlot) plot).getDataset() != null)
		{
			Marker marker = createMarker(jrc);
			if (marker != null)
			{
				addMarker(plot, marker);
			}
		}
	}

	protected CategoryMarker createMarker(JRChart jrc) 
	{
		Comparable<?> value = getProperty(PROPERTY_CATEGORY);

		if (value == null)
		{
			return null;
		}

		CategoryMarker marker = new CategoryMarker(value);

		configureMarker(marker);

		configureStroke(marker);

		Boolean drawAsLine = getBooleanProperty(PROPERTY_DRAW_AS_LINE);
		if (drawAsLine != null) {
			marker.setDrawAsLine(drawAsLine);
		}

		//Setup the font
		Font font = marker.getLabelFont();

		String fontName = getProperty(PROPERTY_FONT_NAME);
		if (fontName == null) {
			fontName = font.getName();
		}

		Float fontSize = getFloatProperty(PROPERTY_FONT_SIZE);
		if (fontSize == null) {
			fontSize = Float.valueOf(font.getSize());
		}

		int fontStyle = Font.PLAIN;
		Boolean isBold = getBooleanProperty(PROPERTY_FONT_BOLD);
		if (isBold != null) {
			fontStyle = fontStyle | Font.BOLD;
		}
		Boolean isItalic = getBooleanProperty(PROPERTY_FONT_ITALIC);
		if (isItalic != null) {
			fontStyle = fontStyle | Font.ITALIC;
		}

		marker.setLabelFont(
			FontUtil.getInstance(filler.getJasperReportsContext()).getAwtFontFromBundles(
				fontName, 
				fontStyle, 
				fontSize, 
				filler.getFillContext().getMasterLocale(), 
				true
				)
			);

		return marker;
	}

	@Override
	protected void addMarker(Plot plot, Marker marker) 
	{
		((CategoryPlot)plot).addDomainMarker(
			(CategoryMarker)marker, 
			getLayer()
			);
	}
}
