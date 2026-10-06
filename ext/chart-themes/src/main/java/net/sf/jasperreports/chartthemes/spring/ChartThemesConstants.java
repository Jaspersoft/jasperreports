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
package net.sf.jasperreports.chartthemes.spring;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public interface ChartThemesConstants
{
	static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	//general properties
	public static final Paint TRANSPARENT_PAINT = new Color(0, 0, 0, 0);
	
	public static final Integer FONT_PLAIN_STYLE = Font.PLAIN;
	public static final Integer FONT_BOLD_STYLE = Font.BOLD;
	public static final Integer FONT_ITALIC_STYLE = Font.ITALIC;

	public static final Paint GRAY_PAINT_134 = new Color(134,134,134);
	public static final Paint GRAY_PAINT_196 = new Color(196,196,196);
	public static final Paint GRAY_PAINT_217 = new Color(217, 217, 217);
	
	@SuppressWarnings("serial")
	public static final List<Color> AEGEAN_INTERVAL_COLORS = new ArrayList<Color>(){{
		add(new Color(182, 0, 40));
		add(new Color(240, 205, 0));
		add(new Color(0,153,0));
		}};
	
	@SuppressWarnings("serial")
	public static final List<Color> EYE_CANDY_SIXTIES_COLORS =	new ArrayList<Color>(){{
			add(new Color(250, 97, 18));
			add(new Color(237, 38, 42));
			add(new Color(0, 111, 60));
			add(new Color(250, 223, 18));
			add(new Color(47, 137, 187));
			add(new Color(231, 133, 35));
			add(new Color(229, 1, 140));
			add(new Color(234, 171, 53));
			}};
	
	@SuppressWarnings("serial")
	public static final List<GradientPaint> EYE_CANDY_SIXTIES_GRADIENT_PAINTS = new ArrayList<GradientPaint>(){{
			add(new GradientPaint(0f, 0f, EYE_CANDY_SIXTIES_COLORS.get(0), 0f, 0f, (EYE_CANDY_SIXTIES_COLORS.get(0)).darker()));
			add(new GradientPaint(0f, 0f, EYE_CANDY_SIXTIES_COLORS.get(1), 0f, 0f, (EYE_CANDY_SIXTIES_COLORS.get(1)).darker()));
			add(new GradientPaint(0f, 0f, EYE_CANDY_SIXTIES_COLORS.get(2), 0f, 0f, (EYE_CANDY_SIXTIES_COLORS.get(2)).darker()));
			add(new GradientPaint(0f, 0f, EYE_CANDY_SIXTIES_COLORS.get(3), 0f, 0f, (EYE_CANDY_SIXTIES_COLORS.get(3)).darker()));
			add(new GradientPaint(0f, 0f, EYE_CANDY_SIXTIES_COLORS.get(4), 0f, 0f, (EYE_CANDY_SIXTIES_COLORS.get(4)).darker()));
			add(new GradientPaint(0f, 0f, EYE_CANDY_SIXTIES_COLORS.get(5), 0f, 0f, (EYE_CANDY_SIXTIES_COLORS.get(5)).darker()));
			add(new GradientPaint(0f, 0f, EYE_CANDY_SIXTIES_COLORS.get(6), 0f, 0f, (EYE_CANDY_SIXTIES_COLORS.get(6)).darker()));
	}};

	// generic chart default properties names
	public static final String BACKGROUND_PAINT = "backgroundPaint";
	public static final String BACKGROUND_IMAGE = "backgroundImage";
	public static final String BACKGROUND_IMAGE_ALIGNMENT = "backgroundImageAlignment";
	public static final String BACKGROUND_IMAGE_ALPHA = "backgroundImageAlpha";
	public static final String SERIES_COLORS = "seriesColors";
	public static final String BASEFONT_SIZE = "baseFontSize";
	public static final String SERIES_GRADIENT_PAINTS = "seriesGradientPaints";
	public static final String CHART_BORDER_PAINT = "chartBorderPaint";
	public static final String CHART_BORDER_STROKE = "chartBorderStroke";
	public static final String CHART_BORDER_VISIBLE = "chartBorderVisible";
	public static final String CHART_ANTI_ALIAS = "chartAntiAlias";
	public static final String TEXT_ANTI_ALIAS = "textAntiAlias";
	public static final String CHART_PADDING = "chartPadding";
	public static final String UNIT_TYPE = "unitType";
	public static final String RENDERING_HINTS = "renderingHints";
	public static final String TITLE = "title";

	// generic chart title  properties names
	public static final String TITLE_VISIBLE = "titleVisible";
	public static final String TITLE_POSITION = "titlePosition";
	public static final String TITLE_FONT = "titleFont";
	public static final String TITLE_FORECOLOR = "titleForecolor";
	public static final String TITLE_BACKCOLOR = "titleBackcolor";
	public static final String TITLE_HORIZONTAL_ALIGNMENT = "titleHorizontalAlignment";
	public static final String TITLE_VERTICAL_ALIGNMENT = "titleVerticalAlignment";
	public static final String TITLE_HEIGHT = "titleHeight";
	public static final String TITLE_WIDTH = "titleWidth";
	public static final String TITLE_BOUNDS = "titleBounds";
	public static final String TITLE_FRAME = "titleFrame";
	public static final String TITLE_MARGIN = "titleMargin";
	public static final String TITLE_PADDING = "titlePadding";
	public static final String TITLE_TEXT = "titleText";
	public static final String TITLE_TEXT_ALIGNMENT = "titleTextAlignment";
	public static final String TITLE_MAXIMUM_LINES_TO_DISPLAY = "titleMaximumLinesToDisplay";
	public static final String TITLE_TOOLTIP_TEXT = "titleTooltipText";
	public static final String TITLE_URL_TEXT = "titleURLText";
	public static final String TITLE_EXPAND_TO_FIT_SPACE = "titleExpandToFitSpace";
	
	// generic chart subtitle  properties names
	public static final String SUBTITLE_VISIBLE = "subtitleVisible";
	public static final String SUBTITLE_POSITION = "subtitlePosition";
	public static final String SUBTITLE_FONT = "subtitleFont";
	public static final String SUBTITLE_FORECOLOR = "subtitleForecolor";
	public static final String SUBTITLE_BACKCOLOR = "subtitleBackcolor";
	public static final String SUBTITLE_HORIZONTAL_ALIGNMENT = "subtitleHorizontalAlignment";
	public static final String SUBTITLE_VERTICAL_ALIGNMENT = "subtitleVerticalAlignment";
	public static final String SUBTITLE_HEIGHT = "subtitleHeight";
	public static final String SUBTITLE_WIDTH = "subtitleWidth";
	public static final String SUBTITLE_BOUNDS = "subtitleBounds";
	public static final String SUBTITLE_FRAME = "subtitleFrame";
	public static final String SUBTITLE_MARGIN = "subtitleMargin";
	public static final String SUBTITLE_PADDING = "subtitlePadding";
	public static final String SUBTITLE_TEXT = "subtitleText";
	public static final String SUBTITLE_TEXT_ALIGNMENT = "subtitleTextAlignment";
	public static final String SUBTITLE_MAXIMUM_LINES_TO_DISPLAY = "subtitleMaximumLinesToDisplay";
	public static final String SUBTITLE_TOOLTIP_TEXT = "subtitleTooltipText";
	public static final String SUBTITLE_URL_TEXT = "subtitleURLText";
	public static final String SUBTITLE_EXPAND_TO_FIT_SPACE = "subtitleExpandToFitSpace";
	
	// generic chart legend  properties names
	public static final String LEGEND_VISIBLE = "legendVisible";
	public static final String LEGEND_POSITION = "legendPosition";
	public static final String LEGEND_FONT = "legendFont";
	public static final String LEGEND_FORECOLOR = "legendForecolor";
	public static final String LEGEND_BACKCOLOR = "legendBackcolor";
	public static final String LEGEND_HORIZONTAL_ALIGNMENT = "legendHorizontalAlignment";
	public static final String LEGEND_FRAME = "legendFrame";
	public static final String LEGEND_VERTICAL_ALIGNMENT = "legendVerticalAlignment";
	public static final String LEGEND_HEIGHT = "legendHeight";
	public static final String LEGEND_WIDTH = "legendWidth";
	public static final String LEGEND_BOUNDS = "legendBounds";
	public static final String LEGEND_MARGIN = "legendMargin";
	public static final String LEGEND_PADDING = "legendPadding";
	public static final String LEGEND_ITEM_GRAPHIC_PADDING = "legendItemGraphicPadding";
	public static final String LEGEND_ITEM_GRAPHIC_LOCATION = "legendItemGraphicLocation";
	public static final String LEGEND_ITEM_GRAPHIC_EDGE = "legendItemGraphicEdge";
	public static final String LEGEND_ITEM_GRAPHIC_ANCHOR = "legendItemGraphicAnchor";
	public static final String LEGEND_ITEM_LABEL_PADDING = "legendItemLabelPadding";
	
	// generic plot  properties names
	public static final String PLOT_BACKGROUND_PAINT = "plotBackgroundPaint";
	public static final String PLOT_BACKGROUND_ALPHA = "plotBackgroundAlpha";
	public static final String PLOT_FOREGROUND_ALPHA = "plotForegroundAlpha";
	public static final String PLOT_BACKGROUND_IMAGE = "plotBackgroundImage";
	public static final String PLOT_BACKGROUND_IMAGE_ALIGNMENT = "plotBackgroundImageAlignment";
	public static final String PLOT_BACKGROUND_IMAGE_ALPHA = "plotBackgroundImageAlpha";
	public static final String PLOT_OUTLINE_PAINT_SEQUENCE = "plotOutlinePaintSequence";
	public static final String PLOT_STROKE_SEQUENCE = "plotStrokeSequence";
	public static final String PLOT_OUTLINE_STROKE_SEQUENCE = "plotOutlineStrokeSequence";
	public static final String PLOT_SHAPE_SEQUENCE = "plotShapeSequence";
	public static final String PLOT_LABEL_ROTATION = "plotLabelRotation";
	public static final String PLOT_ORIENTATION = "plotOrientation";
	public static final String PLOT_INSETS = "plotInsets";
	public static final String PLOT_OUTLINE_PAINT = "plotOutlinePaint";
	public static final String PLOT_OUTLINE_STROKE = "plotOutlineStroke";
	public static final String PLOT_OUTLINE_VISIBLE = "plotOutlineVisible";
	public static final String PLOT_TICK_LABEL_FONT = "plotTickLabelFont";
	public static final String PLOT_DISPLAY_FONT = "plotDisplayFont";
	
	// generic axis  properties names
	public static final String AXIS_VISIBLE = "axisVisible";
	public static final String AXIS_LOCATION = "axisLocation";
	public static final String AXIS_LINE_PAINT = "axisLinePaint";
	public static final String AXIS_LINE_STROKE = "axisLineStroke";
	public static final String AXIS_LINE_VISIBLE = "axisLineVisible";
	public static final String AXIS_FIXED_DIMENSION = "axisFixedDimension";
	public static final String AXIS_LABEL = "axisLabel";
	public static final String AXIS_LABEL_ANGLE = "axisLabelAngle";
	public static final String AXIS_LABEL_PAINT = "axisLabelPaint";
	public static final String AXIS_LABEL_FONT = "axisLabelFont";
	public static final String AXIS_LABEL_INSETS = "axisLabelInsets";
	public static final String AXIS_LABEL_VISIBLE = "axisLabelVisible";
	public static final String AXIS_TICK_LABEL_PAINT = "axisTickLabelPaint";
	public static final String AXIS_TICK_LABEL_FONT = "axisTickLabelFont";
	public static final String AXIS_TICK_LABEL_INSETS = "axisTickLabelInsets";
	public static final String AXIS_TICK_LABELS_VISIBLE = "axisTickLabelsVisible";
	public static final String AXIS_TICK_MARKS_INSIDE_LENGTH = "axisTickMarksInsideLength";
	public static final String AXIS_TICK_MARKS_OUTSIDE_LENGTH = "axisTickMarksOutsideLength";
	public static final String AXIS_TICK_MARKS_PAINT = "axisTickMarksPaint";
	public static final String AXIS_TICK_MARKS_STROKE = "axisTickMarksStroke";
	public static final String AXIS_TICK_MARKS_VISIBLE = "axisTickMarksVisible";
	public static final String RANGE_AXIS_MIN_VALUE = "rangeAxisMinValue";
	public static final String RANGE_AXIS_MAX_VALUE = "rangeAxisMaxValue";
	public static final String RANGE_AXIS_TICK_COUNT = "rangeAxisTickCount";
	public static final String RANGE_AXIS_TICK_INTERVAL = "rangeAxisTickInterval";
	public static final String RANGE_AXIS_INTEGER_UNIT = "rangeAxisIntegerUnit";
	public static final String RANGE_AXIS_TIME_PERIOD_UNIT = "rangeAxisTimePeriodUnit";
	public static final String DOMAIN_AXIS_MIN_VALUE = "domainAxisMinValue";
	public static final String DOMAIN_AXIS_MAX_VALUE = "domainAxisMaxValue";
	public static final String DOMAIN_AXIS_TICK_COUNT = "domainAxisTickCount";
	public static final String DOMAIN_AXIS_TICK_INTERVAL = "domainAxisTickInterval";
	public static final String DOMAIN_AXIS_INTEGER_UNIT = "domainAxisIntegerUnit";
	public static final String DOMAIN_AXIS_TIME_PERIOD_UNIT = "domainAxisTimePeriodUnit";
	
	// chart type names
	public static final String AREA_TYPE = "areaType";
	public static final String BAR_TYPE = "barType";
	public static final String BAR3D_TYPE = "bar3DType";
	public static final String BUBBLE_TYPE = "bubbleType";
	public static final String CANDLESTICK_TYPE = "candlestickType";
	public static final String HIGH_LOW_TYPE = "highLowType";
	public static final String LINE_TYPE = "lineType";
	public static final String METER_TYPE = "meterType";
	public static final String MULTI_AXIS_TYPE = "multiAxisType";
	public static final String PIE_TYPE = "pieType";
	public static final String PIE3D_TYPE = "pie3DType";
	public static final String SCATTER_TYPE = "scatterType";
	public static final String STACKED_BAR_TYPE = "stackedBarType";
	public static final String STACKED_BAR3D_TYPE = "stackedBar3DType";
	public static final String THERMOMETER_TYPE = "thermometerType";
	public static final String TIME_SERIES_TYPE = "timeSeriesType";
	public static final String XY_AREA_TYPE = "xyAreaType";
	public static final String XY_BAR_TYPE = "xyBarType";
	public static final String XY_LINE_TYPE = "xyLineType";
	public static final String STACKED_AREA_TYPE = "stackedAreaType";
	public static final String GANTT_TYPE = "ganttType";
}
