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
package themes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.Align;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.VerticalAlignment;
import org.jfree.chart.util.UnitType;

import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.chartthemes.simple.AxisSettings;
import net.sf.jasperreports.chartthemes.simple.ChartSettings;
import net.sf.jasperreports.chartthemes.simple.ChartThemeSettings;
import net.sf.jasperreports.chartthemes.simple.ColorProvider;
import net.sf.jasperreports.chartthemes.simple.FileImageProvider;
import net.sf.jasperreports.chartthemes.simple.GradientPaintProvider;
import net.sf.jasperreports.chartthemes.simple.LegendSettings;
import net.sf.jasperreports.chartthemes.simple.PaintProvider;
import net.sf.jasperreports.chartthemes.simple.PlotSettings;
import net.sf.jasperreports.chartthemes.simple.TitleSettings;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleSettingsFactory
{

	public static final Color COLOR_0 = new Color(250, 97, 18);
	public static final Color COLOR_1 = new Color(237, 38, 42);
	public static final Color COLOR_2 = new Color(0, 111, 60);
	public static final Color COLOR_3 = new Color(250, 223, 18);
	public static final Color COLOR_4 = new Color(47, 137, 187);
	public static final Color COLOR_5 = new Color(231, 133, 35);
	public static final Color COLOR_6 = new Color(229, 1, 140);
	public static final Color COLOR_7 = new Color(234, 171, 53);

	@SuppressWarnings("serial")
	public static final List<PaintProvider> COLORS = new ArrayList<PaintProvider>(){{
		add(new ColorProvider(COLOR_0));
		add(new ColorProvider(COLOR_1));
		add(new ColorProvider(COLOR_2));
		add(new ColorProvider(COLOR_3));
		add(new ColorProvider(COLOR_4));
		add(new ColorProvider(COLOR_5));
		add(new ColorProvider(COLOR_6));
		add(new ColorProvider(COLOR_7));
		}};
		
	@SuppressWarnings("serial")
	public static final List<PaintProvider> COLORS_DARKER = new ArrayList<PaintProvider>(){{
		add(new ColorProvider(COLOR_0.darker()));
		add(new ColorProvider(COLOR_1.darker()));
		add(new ColorProvider(COLOR_2.darker()));
		add(new ColorProvider(COLOR_3.darker()));
		add(new ColorProvider(COLOR_4.darker()));
		add(new ColorProvider(COLOR_5.darker()));
		add(new ColorProvider(COLOR_6.darker()));
		add(new ColorProvider(COLOR_7.darker()));
		}};
		
	@SuppressWarnings("serial")
	public static final List<GradientPaintProvider> GRADIENT_PAINTS = new ArrayList<GradientPaintProvider>(){{
		add(new GradientPaintProvider(COLOR_0, COLOR_0.darker()));
		add(new GradientPaintProvider(COLOR_1, COLOR_1.darker()));
		add(new GradientPaintProvider(COLOR_2, COLOR_2.darker()));
		add(new GradientPaintProvider(COLOR_3, COLOR_3.darker()));
		add(new GradientPaintProvider(COLOR_4, COLOR_4.darker()));
		add(new GradientPaintProvider(COLOR_5, COLOR_5.darker()));
		add(new GradientPaintProvider(COLOR_6, COLOR_6.darker()));
		add(new GradientPaintProvider(COLOR_7, COLOR_7.darker()));
	}};

	@SuppressWarnings("serial")
	public static final List<Stroke> STROKES = new ArrayList<Stroke>(){{
		add(new BasicStroke(2f));
		add(new BasicStroke(2f));
		add(new BasicStroke(2f));
		add(new BasicStroke(2f));
		add(new BasicStroke(2f));
		add(new BasicStroke(2f));
		add(new BasicStroke(2f));
		add(new BasicStroke(2f));
		}};
	
	@SuppressWarnings("serial")
	public static final List<Stroke> OUTLINE_STROKES = new ArrayList<Stroke>(){{
		add(new BasicStroke(3f));
		add(new BasicStroke(3f));
		add(new BasicStroke(3f));
		add(new BasicStroke(3f));
		add(new BasicStroke(3f));
		add(new BasicStroke(3f));
		add(new BasicStroke(3f));
		add(new BasicStroke(3f));
		}};
	

	/**
	 *
	 */
	public static final ChartThemeSettings createChartThemeSettings()
	{
		ChartThemeSettings settings = new ChartThemeSettings();
		
		ChartSettings chartSettings = settings.getChartSettings();
		chartSettings.setBackgroundPaint(new GradientPaintProvider(Color.green, Color.blue));
		chartSettings.setBackgroundImage(new FileImageProvider("net/sf/jasperreports/chartthemes/simple/jasperreports.png"));
		chartSettings.setBackgroundImageAlignment(Align.TOP_RIGHT);
		chartSettings.setBackgroundImageAlpha(1f);
		chartSettings.setBorderVisible(Boolean.TRUE);
		chartSettings.setBorderPaint(new ColorProvider(Color.GREEN));
		chartSettings.setBorderStroke(new BasicStroke(1f));
		chartSettings.setAntiAlias(Boolean.TRUE);
		chartSettings.setTextAntiAlias(Boolean.TRUE);
		chartSettings.setPadding(new RectangleInsets(UnitType.ABSOLUTE, 1.1, 2.2, 3.3, 4.4));

		TitleSettings titleSettings = settings.getTitleSettings();
		titleSettings.setShowTitle(Boolean.TRUE);
		titleSettings.setPosition(EdgeEnum.TOP);
		titleSettings.setForegroundPaint(new ColorProvider(Color.black));
		titleSettings.setBackgroundPaint(new GradientPaintProvider(Color.green, Color.blue));
		titleSettings.getFont().setBold(Boolean.TRUE);
		titleSettings.getFont().setFontSize(22f);
		titleSettings.setHorizontalAlignment(HorizontalAlignment.CENTER);
		titleSettings.setVerticalAlignment(VerticalAlignment.TOP);
		titleSettings.setPadding(new RectangleInsets(UnitType.ABSOLUTE, 1.1, 2.2, 3.3, 4.4));
		
		TitleSettings subtitleSettings = settings.getSubtitleSettings();
		subtitleSettings.setShowTitle(Boolean.TRUE);
		subtitleSettings.setPosition(EdgeEnum.TOP);
		subtitleSettings.setForegroundPaint(new ColorProvider(Color.red));
		subtitleSettings.setBackgroundPaint(new GradientPaintProvider(Color.green, Color.blue));
		subtitleSettings.getFont().setBold(Boolean.TRUE);
		subtitleSettings.setHorizontalAlignment(HorizontalAlignment.LEFT);
		subtitleSettings.setVerticalAlignment(VerticalAlignment.CENTER);
		subtitleSettings.setPadding(new RectangleInsets(UnitType.ABSOLUTE, 1.1, 2.2, 3.3, 4.4));

		LegendSettings legendSettings = settings.getLegendSettings();
		legendSettings.setShowLegend(Boolean.TRUE);
		legendSettings.setPosition(EdgeEnum.BOTTOM);
		legendSettings.setForegroundPaint(new ColorProvider(Color.black));
		legendSettings.setBackgroundPaint(new GradientPaintProvider(Color.green, Color.blue));
		legendSettings.getFont().setBold(Boolean.TRUE);
		legendSettings.setHorizontalAlignment(HorizontalAlignment.CENTER);
		legendSettings.setVerticalAlignment(VerticalAlignment.BOTTOM);
		//FIXMETHEME legendSettings.setBlockFrame();
		legendSettings.setPadding(new RectangleInsets(UnitType.ABSOLUTE, 1.1, 2.2, 3.3, 4.4));
		
		PlotSettings plotSettings = settings.getPlotSettings();
		plotSettings.setOrientation(PlotOrientation.VERTICAL);
//		plotSettings.setForegroundAlpha(0.5f);
		plotSettings.setBackgroundPaint(new GradientPaintProvider(Color.green, Color.blue));
//		plotSettings.setBackgroundAlpha(0.5f);
		plotSettings.setBackgroundImage(new FileImageProvider("net/sf/jasperreports/chartthemes/simple/jasperreports.png"));
		plotSettings.setBackgroundImageAlpha(0.5f);
		plotSettings.setBackgroundImageAlignment(Align.NORTH_WEST);
		plotSettings.setLabelRotation(0d);
		plotSettings.setPadding(new RectangleInsets(UnitType.ABSOLUTE, 1.1, 2.2, 3.3, 4.4));
		plotSettings.setOutlineVisible(Boolean.TRUE);
		plotSettings.setOutlinePaint(new ColorProvider(Color.red));
		plotSettings.setOutlineStroke(new BasicStroke(1f));
		plotSettings.setSeriesColorSequence(COLORS);
//		plotSettings.setSeriesGradientPaintSequence(GRADIENT_PAINTS);
		plotSettings.setSeriesOutlinePaintSequence(COLORS_DARKER);
		plotSettings.setSeriesStrokeSequence(STROKES);
		plotSettings.setSeriesOutlineStrokeSequence(OUTLINE_STROKES);
		plotSettings.setDomainGridlineVisible(Boolean.TRUE);
		plotSettings.setDomainGridlinePaint(new ColorProvider(Color.DARK_GRAY));
		plotSettings.setDomainGridlineStroke(new BasicStroke(0.5f));
		plotSettings.setRangeGridlineVisible(Boolean.TRUE);
		plotSettings.setRangeGridlinePaint(new ColorProvider(Color.BLACK));
		plotSettings.setRangeGridlineStroke(new BasicStroke(0.5f));
		plotSettings.getTickLabelFont().setFontName("Courier");
		plotSettings.getTickLabelFont().setBold(Boolean.TRUE);
		plotSettings.getTickLabelFont().setFontSize(10f);
		plotSettings.getDisplayFont().setFontName("Arial");
		plotSettings.getDisplayFont().setBold(Boolean.TRUE);
		plotSettings.getDisplayFont().setFontSize(12f);
		
		AxisSettings domainAxisSettings = settings.getDomainAxisSettings();
		domainAxisSettings.setVisible(Boolean.TRUE);
		domainAxisSettings.setLocation(AxisLocation.BOTTOM_OR_RIGHT);
		domainAxisSettings.setLinePaint(new ColorProvider(Color.green));
		domainAxisSettings.setLineStroke(new BasicStroke(1f));
		domainAxisSettings.setLineVisible(Boolean.TRUE);
//		domainAxisSettings.setLabel("Domain Axis");
		domainAxisSettings.setLabelAngle(0.0d);
		domainAxisSettings.setLabelPaint(new ColorProvider(Color.magenta));
		domainAxisSettings.getLabelFont().setBold(Boolean.TRUE);
		domainAxisSettings.getLabelFont().setItalic(Boolean.TRUE);
		domainAxisSettings.getLabelFont().setFontName("Comic Sans MS");
		domainAxisSettings.getLabelFont().setFontSize(12f);
		domainAxisSettings.setLabelInsets(new RectangleInsets(UnitType.ABSOLUTE, 0.5, 0.5, 1, 1));
		domainAxisSettings.setLabelVisible(Boolean.TRUE);
		domainAxisSettings.setTickLabelPaint(new ColorProvider(Color.cyan));
		domainAxisSettings.getTickLabelFont().setBold(Boolean.TRUE);
		domainAxisSettings.getTickLabelFont().setItalic(Boolean.FALSE);
		domainAxisSettings.getTickLabelFont().setFontName("Arial");
		domainAxisSettings.getTickLabelFont().setFontSize(10f);
		domainAxisSettings.setTickLabelInsets(new RectangleInsets(UnitType.ABSOLUTE, 0.5, 0.5, 0.5, 0.5));
		domainAxisSettings.setTickLabelsVisible(Boolean.TRUE);
		domainAxisSettings.setTickMarksInsideLength(0.1f);
		domainAxisSettings.setTickMarksOutsideLength(0.2f);
		domainAxisSettings.setTickMarksPaint(new ColorProvider(Color.ORANGE));
		domainAxisSettings.setTickMarksStroke(new BasicStroke(1f));
		domainAxisSettings.setTickMarksVisible(Boolean.TRUE);
		domainAxisSettings.setTickCount(5);

		
		AxisSettings rangeAxisSettings = settings.getRangeAxisSettings();
		rangeAxisSettings.setVisible(Boolean.TRUE);
		rangeAxisSettings.setLocation(AxisLocation.TOP_OR_RIGHT);
		rangeAxisSettings.setLinePaint(new ColorProvider(Color.yellow));
		rangeAxisSettings.setLineStroke(new BasicStroke(1f));
		rangeAxisSettings.setLineVisible(Boolean.TRUE);
//		rangeAxisSettings.setLabel("Range Axis");
		rangeAxisSettings.setLabelAngle(Math.PI/2.0d);
		rangeAxisSettings.setLabelPaint(new ColorProvider(Color.green));
		rangeAxisSettings.getLabelFont().setBold(Boolean.TRUE);
		rangeAxisSettings.getLabelFont().setItalic(Boolean.TRUE);
		rangeAxisSettings.getLabelFont().setFontName("Comic Sans MS");
		rangeAxisSettings.getLabelFont().setFontSize(12f);
		rangeAxisSettings.setLabelInsets(new RectangleInsets(UnitType.ABSOLUTE, 0.5, 0.5, 1, 1));
		rangeAxisSettings.setLabelVisible(Boolean.TRUE);
		rangeAxisSettings.setTickLabelPaint(new ColorProvider(Color.pink));
		rangeAxisSettings.getTickLabelFont().setBold(Boolean.FALSE);
		rangeAxisSettings.getTickLabelFont().setItalic(Boolean.TRUE);
		rangeAxisSettings.getTickLabelFont().setFontName("Arial");
		rangeAxisSettings.getTickLabelFont().setFontSize(10f);
		rangeAxisSettings.setTickLabelInsets(new RectangleInsets(UnitType.ABSOLUTE, 0.5, 0.5, 0.5, 0.5));
		rangeAxisSettings.setTickLabelsVisible(Boolean.TRUE);
		rangeAxisSettings.setTickMarksInsideLength(0.2f);
		rangeAxisSettings.setTickMarksOutsideLength(0.1f);
		rangeAxisSettings.setTickMarksPaint(new ColorProvider(Color.black));
		rangeAxisSettings.setTickMarksStroke(new BasicStroke(1f));
		rangeAxisSettings.setTickMarksVisible(Boolean.TRUE);
		rangeAxisSettings.setTickCount(6);
		
		return settings;
	}
}
