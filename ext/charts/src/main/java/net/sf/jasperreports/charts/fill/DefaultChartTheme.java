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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.charts.ChartContext;
import net.sf.jasperreports.charts.ChartTheme;
import net.sf.jasperreports.charts.ChartThemeBundle;
import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRBarPlot;
import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartAxis;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRChartPlot.JRSeriesColor;
import net.sf.jasperreports.charts.JRDataRange;
import net.sf.jasperreports.charts.JRHighLowPlot;
import net.sf.jasperreports.charts.JRItemLabel;
import net.sf.jasperreports.charts.JRLinePlot;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRPiePlot;
import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRThermometerPlot;
import net.sf.jasperreports.charts.JRTimeSeriesPlot;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.charts.type.AxisPositionEnum;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.charts.type.MeterShapeEnum;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.charts.type.ScaleTypeEnum;
import net.sf.jasperreports.charts.util.ChartUtil;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @author Some enhancements by Barry Klawans (bklawans@users.sourceforge.net)
 */
public class DefaultChartTheme implements ChartTheme
{

	public static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_CHART_TYPE = "fill.chart.theme.unsupported.chart.type";
	
	/**
	 *
	 */
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			valueType = Boolean.class,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_5_0
			)
	public static final String PROPERTY_DIAL_VALUE_DISPLAY_VISIBLE = JRPropertiesUtil.PROPERTY_PREFIX + "chart.dial.value.display.visible";
	
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_5_0
			)
	public static final String PROPERTY_DIAL_LABEL = JRPropertiesUtil.PROPERTY_PREFIX + "chart.dial.label";
	
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			valueType = Integer.class,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_5_0
			)
	public static final String PROPERTY_RANGE_AXIS_TICK_COUNT = JRPropertiesUtil.PROPERTY_PREFIX + "chart.range.axis.tick.count";
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			valueType = Double.class,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_4
			)
	public static final String PROPERTY_RANGE_AXIS_TICK_INTERVAL = JRPropertiesUtil.PROPERTY_PREFIX + "chart.range.axis.tick.interval";
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			valueType = Boolean.class,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public static final String PROPERTY_RANGE_AXIS_INTEGER_UNIT = JRPropertiesUtil.PROPERTY_PREFIX + "chart.range.axis.integer.unit";
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			valueType = Integer.class,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_5_0
			)
	public static final String PROPERTY_DOMAIN_AXIS_TICK_COUNT = JRPropertiesUtil.PROPERTY_PREFIX + "chart.domain.axis.tick.count";
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			valueType = Double.class,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_3_1_4
			)
	public static final String PROPERTY_DOMAIN_AXIS_TICK_INTERVAL = JRPropertiesUtil.PROPERTY_PREFIX + "chart.domain.axis.tick.interval";
	@Property(
			category = PropertyConstants.CATEGORY_CHART_THEME,
			valueType = Boolean.class,
			scopes = {PropertyScope.CHART_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_6_0
			)
	public static final String PROPERTY_DOMAIN_AXIS_INTEGER_UNIT = JRPropertiesUtil.PROPERTY_PREFIX + "chart.domain.axis.integer.unit";

	/**
	 *
	 */
	// do not use unless actually necessary as it results in rasterized images for PostScript printing
	protected static final Color TRANSPARENT_PAINT = new Color(0, 0, 0, 0);
	
	/**
	 *
	 */
	private ChartContext chartContext;
	private FontUtil fontUtil;
	

	/**
	 *
	 */
	public DefaultChartTheme()
	{
	}
	
	
	/**
	 *
	 */
	protected JRChart getChart()
	{
		return chartContext.getChart();
	}
	
	
	/**
	 *
	 */
	protected JRChartPlot getPlot()
	{
		return getChart().getPlot();
	}
	
	
	/**
	 *
	 */
	protected Dataset getDataset()
	{
		return chartContext.getDataset();
	}
	
	
	/**
	 *
	 */
	protected Object getLabelGenerator()
	{
		return chartContext.getLabelGenerator();
	}
	
	
	/**
	 *
	 */
	protected Locale getLocale()
	{
		return chartContext.getLocale();
	}
	
	
	/**
	 *
	 */
	protected JRFont getFont(JRFont font)
	{
		if (font == null)
		{
			return new JRBaseFont(getChart());
		}
		return font;
	}
	
	
	/**
	 *
	 */
	protected final String evaluateTextExpression(JRExpression expression) throws JRException
	{
		return chartContext.evaluateTextExpression(expression);
	}

	
	/**
	 *
	 */
	protected final Object evaluateExpression(JRExpression expression) throws JRException
	{
		return chartContext.evaluateExpression(expression);
	}

	
	@Override
	public JFreeChart createChart(ChartContext chartContext) throws JRException
	{
		this.chartContext = chartContext;
		this.fontUtil = FontUtil.getInstance(chartContext.getJasperReportsContext());
		
		JFreeChart jfreeChart = null;
		
		switch (getChart().getChartType()) {
			case AREA:
				jfreeChart = createAreaChart();
				break;
			case BAR:
			case BAR3D:
				jfreeChart = createBarChart();
				break;
			case BUBBLE:
				jfreeChart = createBubbleChart();
				break;
			case CANDLESTICK:
				jfreeChart = createCandlestickChart();
				break;
			case HIGHLOW:
				jfreeChart = createHighLowChart();
				break;
			case LINE:
				jfreeChart = createLineChart();
				break;
			case METER:
				if (MeterShapeEnum.DIAL ==((JRMeterPlot)getPlot()).getShape())
				{
					jfreeChart = createDialChart();
				}
				else
				{
					jfreeChart = createMeterChart();
				}
				break;
			case MULTI_AXIS:
				//multi-axis charts are dealt with in JRFillChart
				break;
			case PIE:
			case PIE3D:
				jfreeChart = createPieChart();
				break;
			case SCATTER:
				jfreeChart = createScatterChart();
				break;
			case STACKEDBAR:
			case STACKEDBAR3D:
				jfreeChart = createStackedBarChart();
				break;
			case THERMOMETER:
				jfreeChart = createThermometerChart();
				break;
			case TIMESERIES:
				jfreeChart = createTimeSeriesChart();
				break;
			case XYAREA:
				jfreeChart = createXyAreaChart();
				break;
			case XYBAR:
				jfreeChart = createXYBarChart();
				break;
			case XYLINE:
				jfreeChart = createXyLineChart();
				break;
			case STACKEDAREA:
				jfreeChart = createStackedAreaChart();
				break;
			case GANTT:
				jfreeChart = createGanttChart();
				break;
			default:
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNSUPPORTED_CHART_TYPE,  
						new Object[]{getChart().getChartType()} 
						);
		}

		return jfreeChart;
	}


	/**
	 *
	 */
	protected void configureChart(JFreeChart jfreeChart) throws JRException
	{
		if (getChart().getMode() == ModeEnum.OPAQUE)
		{
			jfreeChart.setBackgroundPaint(getChart().getBackcolor());
		}
		else
		{
			jfreeChart.setBackgroundPaint(null);
		}
		
		RectangleEdge titleEdge = getEdge(getChart().getTitlePosition(), RectangleEdge.TOP);
		
		if (jfreeChart.getTitle() != null)
		{
			TextTitle title = jfreeChart.getTitle();
			title.setPaint(getChart().getTitleColor());

			title.setFont(fontUtil.getAwtFont(getFont(getChart().getTitleFont()), getLocale()));
			title.setPosition(titleEdge);
		}

		String subtitleText = evaluateTextExpression(getChart().getSubtitleExpression());
		if (subtitleText != null)
		{
			TextTitle subtitle = new TextTitle(subtitleText);
			subtitle.setPaint(getChart().getSubtitleColor());

			subtitle.setFont(fontUtil.getAwtFont(getFont(getChart().getSubtitleFont()), getLocale()));
			subtitle.setPosition(titleEdge);

			jfreeChart.addSubtitle(subtitle);
		}

		// Apply all of the legend formatting options
		LegendTitle legend = jfreeChart.getLegend();
		if (legend != null)
		{
			legend.setItemPaint(getChart().getLegendColor());

			if (getChart().getOwnLegendBackgroundColor() == null)// in a way, legend backcolor inheritance from chart is useless
			{
				legend.setBackgroundPaint(null);
			}
			else
			{
				legend.setBackgroundPaint(getChart().getLegendBackgroundColor());
			}

			legend.setItemFont(fontUtil.getAwtFont(getFont(getChart().getLegendFont()), getLocale()));
			legend.setPosition(getEdge(getChart().getLegendPosition(), RectangleEdge.BOTTOM));
		}
		
		configurePlot(jfreeChart.getPlot());
	}


	/**
	 *
	 */
	protected void configurePlot(Plot plot)
	{
		plot.setOutlinePaint(null);

		if (getPlot().getOwnBackcolor() == null)// in a way, plot backcolor inheritence from chart is useless
		{
			plot.setBackgroundPaint(null);
		}
		else
		{
			plot.setBackgroundPaint(getPlot().getBackcolor());
		}

		float backgroundAlpha = getPlot().getBackgroundAlpha() == null ? 1f : getPlot().getBackgroundAlpha();
		float foregroundAlpha = getPlot().getForegroundAlpha() == null ? 1f : getPlot().getForegroundAlpha();
		plot.setBackgroundAlpha(backgroundAlpha);
		plot.setForegroundAlpha(foregroundAlpha);

		if (plot instanceof CategoryPlot)
		{
			// Handle rotation of the category labels.
			CategoryAxis axis = ((CategoryPlot)plot).getDomainAxis();
			// it's OK to use deprecated method here; avoiding it means attempting cast operations  
			double labelRotation = getLabelRotation();
			if (labelRotation == 90)
			{
				axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
			}
			else if (labelRotation == -90) {
				axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
			}
			else if (labelRotation < 0)
			{
				axis.setCategoryLabelPositions(
						CategoryLabelPositions.createUpRotationLabelPositions( (-labelRotation / 180.0) * Math.PI));
			}
			else if (labelRotation > 0)
			{
				axis.setCategoryLabelPositions(
						CategoryLabelPositions.createDownRotationLabelPositions((labelRotation / 180.0) * Math.PI));
			}
		}


		// Set any color series
		SortedSet<JRSeriesColor> seriesColors = getPlot().getSeriesColors();
		if (seriesColors != null && seriesColors.size() > 0)
		{
			if (seriesColors.size() == 1)
			{
				// Add the single color to the beginning of the color cycle, using all the default
				// colors.  To replace the defaults you have to specify at least two colors.
				Paint[] colors = new Paint[DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length + 1];
				colors[0] = seriesColors.first().getColor();
				System.arraycopy(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, 0, colors, 1, DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length);
				plot.setDrawingSupplier(new DefaultDrawingSupplier(colors,
						DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
						DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
						DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
						DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
			}
			else if (seriesColors.size() > 1)
			{
				// Set up a custom drawing supplier that cycles through the user's colors
				// instead of the default colors.
				Color[] colors = new Color[seriesColors.size()];
				JRSeriesColor[] colorSequence = new JRSeriesColor[seriesColors.size()];
				seriesColors.toArray(colorSequence);
				for (int i = 0; i < colorSequence.length; i++)
				{
					colors[i] = colorSequence[i].getColor();
				}

				plot.setDrawingSupplier(new DefaultDrawingSupplier(colors,
											DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
											DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
											DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
											DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
			}
		}
	}

	/**
	 * Sets all the axis formatting options.  This includes the colors and fonts to use on
	 * the axis as well as the color to use when drawing the axis line.
	 *
	 * @param axis the axis to format
	 * @param labelFont the font to use for the axis label
	 * @param labelColor the color of the axis label
	 * @param tickLabelFont the font to use for each tick mark value label
	 * @param tickLabelColor the color of each tick mark value label
	 * @param tickLabelMask formatting mask for the label.  If the axis is a NumberAxis then
	 * 					    the mask should be <code>java.text.DecimalFormat</code> mask, and
	 * 						if it is a DateAxis then the mask should be a
	 * 						<code>java.text.SimpleDateFormat</code> mask.
	 * @param verticalTickLabels flag to draw tick labels at 90 degrees
	 * @param lineColor color to use when drawing the axis line and any tick marks
	 */
	protected void configureAxis(
		Axis axis,
		JRFont labelFont,
		Color labelColor,
		JRFont tickLabelFont,
		Color tickLabelColor,
		String tickLabelMask,
		Boolean verticalTickLabels,
		Color lineColor,
		boolean isRangeAxis,
		Comparable<?> axisMinValue,
		Comparable<?> axisMaxValue
		)
	{
		axis.setLabelFont(fontUtil.getAwtFont(getFont(labelFont), getLocale()));
		axis.setTickLabelFont(fontUtil.getAwtFont(getFont(tickLabelFont), getLocale()));
		if (labelColor != null)
		{
			axis.setLabelPaint(labelColor);
		}

		if (tickLabelColor != null)
		{
			axis.setTickLabelPaint(tickLabelColor);
		}

		if (lineColor != null)
		{
			axis.setAxisLinePaint(lineColor);
			axis.setTickMarkPaint(lineColor);
		}

		TimeZone timeZone = chartContext.getTimeZone();
		if (axis instanceof DateAxis && timeZone != null)
		{
			// used when no mask is set
			((DateAxis) axis).setTimeZone(timeZone);
		}
		
		// FIXME use locale for formats
		if (tickLabelMask != null)
		{
			if (axis instanceof NumberAxis)
			{
				NumberFormat fmt = NumberFormat.getInstance(getLocale());
				if (fmt instanceof DecimalFormat)
				{
					((DecimalFormat) fmt).applyPattern(tickLabelMask);
				}
				((NumberAxis)axis).setNumberFormatOverride(fmt);
			}
			else if (axis instanceof DateAxis)
			{
				DateFormat fmt;
				if (tickLabelMask.equals("SHORT") || tickLabelMask.equals("DateFormat.SHORT"))
				{
					fmt = DateFormat.getDateInstance(DateFormat.SHORT, getLocale());
				}
				else if (tickLabelMask.equals("MEDIUM") || tickLabelMask.equals("DateFormat.MEDIUM"))
				{
					fmt = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
				}
				else if (tickLabelMask.equals("LONG") || tickLabelMask.equals("DateFormat.LONG"))
				{
					fmt = DateFormat.getDateInstance(DateFormat.LONG, getLocale());
				}
				else if (tickLabelMask.equals("FULL") || tickLabelMask.equals("DateFormat.FULL"))
				{
					fmt = DateFormat.getDateInstance(DateFormat.FULL, getLocale());
				}
				else
				{
					fmt = new SimpleDateFormat(tickLabelMask, getLocale());
				}

				if (timeZone != null)
				{
					fmt.setTimeZone(timeZone);
				}
				
				((DateAxis)axis).setDateFormatOverride(fmt);
			}
			// ignore mask for other axis types.
		}

		if (verticalTickLabels != null && axis instanceof ValueAxis)
		{
			((ValueAxis)axis).setVerticalTickLabels(verticalTickLabels);
		}
		
		setAxisBounds(axis, isRangeAxis, axisMinValue, axisMaxValue);
	}

	/**
	 *
	 */
	protected JFreeChart createAreaChart() throws JRException 
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = 
			ChartFactory.createAreaChart( 
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRAreaPlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRAreaPlot)getPlot()).getValueAxisLabelExpression()),
				(CategoryDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(jfreeChart);
		JRAreaPlot areaPlot = (JRAreaPlot)getPlot();
		// Handle the axis formating for the category axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getDomainAxis(), areaPlot.getCategoryAxisLabelFont(),
				areaPlot.getCategoryAxisLabelColor(), areaPlot.getCategoryAxisTickLabelFont(),
				areaPlot.getCategoryAxisTickLabelColor(), areaPlot.getCategoryAxisTickLabelMask(), areaPlot.getCategoryAxisVerticalTickLabels(),
				areaPlot.getCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMaxValueExpression()));
		
		// Handle the axis formating for the value axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getRangeAxis(), areaPlot.getValueAxisLabelFont(),
				areaPlot.getValueAxisLabelColor(), areaPlot.getValueAxisTickLabelFont(),
				areaPlot.getValueAxisTickLabelColor(), areaPlot.getValueAxisTickLabelMask(), areaPlot.getValueAxisVerticalTickLabels(),
				areaPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMaxValueExpression()));
		return jfreeChart;
	}


	/**
	 * @deprecated To be removed.
	 */
	protected JFreeChart createBar3DChart() throws JRException 
	{
		return createBarChart();
	}


	/**
	 *
	 */
	protected JFreeChart createBarChart() throws JRException
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createBarChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRBarPlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRBarPlot)getPlot()).getValueAxisLabelExpression()),
				(CategoryDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false
				);

		configureChart(jfreeChart);

		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		//plot.setNoDataMessage("No data to display");
		
		JRBarPlot barPlot = (JRBarPlot)getPlot();
		boolean isShowTickMarks = barPlot.getShowTickMarks() == null ? true : barPlot.getShowTickMarks();
		boolean isShowTickLabels = barPlot.getShowTickLabels() == null ? true : barPlot.getShowTickLabels();

		categoryPlot.getDomainAxis().setTickMarksVisible(isShowTickMarks);
		categoryPlot.getDomainAxis().setTickLabelsVisible(isShowTickLabels);
		// Handle the axis formating for the category axis
		configureAxis(categoryPlot.getDomainAxis(), barPlot.getCategoryAxisLabelFont(),
				barPlot.getCategoryAxisLabelColor(), barPlot.getCategoryAxisTickLabelFont(),
				barPlot.getCategoryAxisTickLabelColor(), barPlot.getCategoryAxisTickLabelMask(), barPlot.getCategoryAxisVerticalTickLabels(),
				barPlot.getCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMaxValueExpression()));

		((NumberAxis)categoryPlot.getRangeAxis()).setTickMarksVisible(isShowTickMarks);
		((NumberAxis)categoryPlot.getRangeAxis()).setTickLabelsVisible(isShowTickLabels);
		// Handle the axis formating for the value axis
		Comparable<?> rangeAxisMaxValue = (Comparable<?>)evaluateExpression(barPlot.getRangeAxisMaxValueExpression());
		configureAxis(categoryPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
				barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
				barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
				barPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMinValueExpression()),
				rangeAxisMaxValue);

		BarRenderer categoryRenderer = (BarRenderer)categoryPlot.getRenderer();
		boolean isShowLabels = barPlot.getShowLabels() == null ? false : barPlot.getShowLabels();
		categoryRenderer.setDefaultItemLabelsVisible( isShowLabels );
		if (isShowLabels)
		{
			if (rangeAxisMaxValue == null)
			{
				//in case the bars are horizontal and there was no range max value specified, 
				//we try to make the axis a little longer for labels to fit on the plot
				Axis axis = categoryPlot.getRangeAxis();
				if (axis instanceof ValueAxis)
				{
					if (!(axis instanceof DateAxis))
					{
						float rangeAxisMaxRatio = 1f;
						
						if (barPlot.getOrientation() == PlotOrientationEnum.HORIZONTAL)
						{
							rangeAxisMaxRatio = 
								JRPropertiesUtil.getInstance(chartContext.getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.horizontal.range.max.value.ratio", 1.25f
									);
						}
						else
						{
							rangeAxisMaxRatio = 
								JRPropertiesUtil.getInstance(chartContext.getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.vertical.range.max.value.ratio", 1.1f
									);
						}
						
						((ValueAxis)axis).setUpperBound(((ValueAxis)axis).getUpperBound() * rangeAxisMaxRatio);
					}
				}
			}

			JRItemLabel itemLabel = barPlot.getItemLabel();
			
			categoryRenderer.setDefaultItemLabelFont(
				fontUtil.getAwtFont(
					getFont(itemLabel == null ? null : itemLabel.getFont()), 
					getLocale()
					)
				);
			
			if (itemLabel != null)
			{
				if (itemLabel.getColor() != null)
				{
					categoryRenderer.setDefaultItemLabelPaint(itemLabel.getColor());
				}
				else
				{
					categoryRenderer.setDefaultItemLabelPaint(getChart().getForecolor());
				}
//				categoryRenderer.setDefaultFillPaint(itemLabel.getBackgroundColor());
//				if (itemLabel.getMask() != null)
//				{
//					categoryRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
//							StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, 
//							new DecimalFormat(itemLabel.getMask())));
//				}
//				else
//				{
					categoryRenderer.setDefaultItemLabelGenerator((CategoryItemLabelGenerator)getLabelGenerator());
//				}
			}
			else
			{
				categoryRenderer.setDefaultItemLabelGenerator((CategoryItemLabelGenerator)getLabelGenerator());
				categoryRenderer.setDefaultItemLabelPaint(getChart().getForecolor());
			}
		}
		categoryRenderer.setShadowVisible(false);
		
		return jfreeChart;
	}


	protected JFreeChart createBubbleChart() throws JRException 
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = ChartFactory.createBubbleChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRBubblePlot)getPlot()).getXAxisLabelExpression()),
				evaluateTextExpression(((JRBubblePlot)getPlot()).getYAxisLabelExpression()),
				(XYZDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(jfreeChart);

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		JRBubblePlot bubblePlot = (JRBubblePlot)getPlot();
		ScaleTypeEnum scaleType = bubblePlot.getScaleType() == null ? ScaleTypeEnum.ON_RANGE_AXIS : bubblePlot.getScaleType();
		XYBubbleRenderer bubbleRenderer = new XYBubbleRenderer(ChartUtil.getScaleType(scaleType));
		xyPlot.setRenderer( bubbleRenderer );

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), bubblePlot.getXAxisLabelFont(),
				bubblePlot.getXAxisLabelColor(), bubblePlot.getXAxisTickLabelFont(),
				bubblePlot.getXAxisTickLabelColor(), bubblePlot.getXAxisTickLabelMask(), bubblePlot.getXAxisVerticalTickLabels(),
				bubblePlot.getXAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(bubblePlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(bubblePlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), bubblePlot.getYAxisLabelFont(),
				bubblePlot.getYAxisLabelColor(), bubblePlot.getYAxisTickLabelFont(),
				bubblePlot.getYAxisTickLabelColor(), bubblePlot.getYAxisTickLabelMask(), bubblePlot.getYAxisVerticalTickLabels(),
				bubblePlot.getYAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(bubblePlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(bubblePlot.getRangeAxisMaxValueExpression()));

		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createCandlestickChart() throws JRException
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createCandlestickChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRCandlestickPlot)getPlot()).getTimeAxisLabelExpression()),
				evaluateTextExpression(((JRCandlestickPlot)getPlot()).getValueAxisLabelExpression()),
				(DefaultHighLowDataset)getDataset(),
				isShowLegend()
				);

		configureChart(jfreeChart);

		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		JRCandlestickPlot candlestickPlot = (JRCandlestickPlot)getPlot();
		CandlestickRenderer candlestickRenderer = (CandlestickRenderer) xyPlot.getRenderer();
		boolean isShowVolume = candlestickPlot.getShowVolume() == null ? true : candlestickPlot.getShowVolume();
		candlestickRenderer.setDrawVolume(isShowVolume);

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), candlestickPlot.getTimeAxisLabelFont(),
				candlestickPlot.getTimeAxisLabelColor(), candlestickPlot.getTimeAxisTickLabelFont(),
				candlestickPlot.getTimeAxisTickLabelColor(), candlestickPlot.getTimeAxisTickLabelMask(), candlestickPlot.getTimeAxisVerticalTickLabels(),
				candlestickPlot.getTimeAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(candlestickPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(candlestickPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), candlestickPlot.getValueAxisLabelFont(),
				candlestickPlot.getValueAxisLabelColor(), candlestickPlot.getValueAxisTickLabelFont(),
				candlestickPlot.getValueAxisTickLabelColor(), candlestickPlot.getValueAxisTickLabelMask(), candlestickPlot.getValueAxisVerticalTickLabels(),
				candlestickPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(candlestickPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(candlestickPlot.getRangeAxisMaxValueExpression()));

		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createHighLowChart() throws JRException
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createHighLowChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRHighLowPlot)getPlot()).getTimeAxisLabelExpression()),
				evaluateTextExpression(((JRHighLowPlot)getPlot()).getValueAxisLabelExpression()),
				(DefaultHighLowDataset)getDataset(),
				isShowLegend()
				);

		configureChart(jfreeChart);

		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		JRHighLowPlot highLowPlot = (JRHighLowPlot)getPlot();
		HighLowRenderer hlRenderer = (HighLowRenderer) xyPlot.getRenderer();
		boolean isShowOpenTicks = highLowPlot.getShowOpenTicks() == null ? false : highLowPlot.getShowOpenTicks();
		boolean isShowCloseTicks = highLowPlot.getShowCloseTicks() == null ? false : highLowPlot.getShowCloseTicks();
		
		hlRenderer.setDrawOpenTicks(isShowOpenTicks);
		hlRenderer.setDrawCloseTicks(isShowCloseTicks);

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), highLowPlot.getTimeAxisLabelFont(),
				highLowPlot.getTimeAxisLabelColor(), highLowPlot.getTimeAxisTickLabelFont(),
				highLowPlot.getTimeAxisTickLabelColor(), highLowPlot.getTimeAxisTickLabelMask(), highLowPlot.getTimeAxisVerticalTickLabels(),
				highLowPlot.getTimeAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(highLowPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(highLowPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), highLowPlot.getValueAxisLabelFont(),
				highLowPlot.getValueAxisLabelColor(), highLowPlot.getValueAxisTickLabelFont(),
				highLowPlot.getValueAxisTickLabelColor(), highLowPlot.getValueAxisTickLabelMask(), highLowPlot.getValueAxisVerticalTickLabels(),
				highLowPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(highLowPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(highLowPlot.getRangeAxisMaxValueExpression()));
		
		return jfreeChart;
	}


	protected JFreeChart createLineChart() throws JRException 
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = ChartFactory.createLineChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression( ((JRLinePlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRLinePlot)getPlot()).getValueAxisLabelExpression()),
				(CategoryDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(jfreeChart);

		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		JRLinePlot linePlot = (JRLinePlot)getPlot();

		LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer)categoryPlot.getRenderer();
		boolean isShowShapes = linePlot.getShowShapes() == null ? true : linePlot.getShowShapes();
		boolean isShowLines = linePlot.getShowLines() == null ? true : linePlot.getShowLines();
		
		lineRenderer.setDefaultShapesVisible( isShowShapes );//FIXMECHART check this
		lineRenderer.setDefaultLinesVisible( isShowLines );
		
		//FIXME labels?

		// Handle the axis formating for the category axis
		configureAxis(categoryPlot.getDomainAxis(), linePlot.getCategoryAxisLabelFont(),
				linePlot.getCategoryAxisLabelColor(), linePlot.getCategoryAxisTickLabelFont(),
				linePlot.getCategoryAxisTickLabelColor(), linePlot.getCategoryAxisTickLabelMask(), linePlot.getCategoryAxisVerticalTickLabels(),
				linePlot.getCategoryAxisLineColor(),  false,
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(categoryPlot.getRangeAxis(), linePlot.getValueAxisLabelFont(),
				linePlot.getValueAxisLabelColor(), linePlot.getValueAxisTickLabelFont(),
				linePlot.getValueAxisTickLabelColor(), linePlot.getValueAxisTickLabelMask(), linePlot.getValueAxisVerticalTickLabels(),
				linePlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(linePlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(linePlot.getRangeAxisMaxValueExpression()));

		return jfreeChart;
	}


	/**
	 * @deprecated To be removed
	 */
	protected JFreeChart createPie3DChart() throws JRException
	{
		return createPieChart();
	}


	/**
	 *
	 */
	protected JFreeChart createPieChart() throws JRException
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createPieChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				(PieDataset)getDataset(),
				isShowLegend(),
				true,
				false
				);

		configureChart(jfreeChart);
		PiePlot piePlot = (PiePlot)jfreeChart.getPlot();
		//plot.setStartAngle(290);
		//plot.setDirection(Rotation.CLOCKWISE);
		//plot.setNoDataMessage("No data to display");
		JRPiePlot jrPiePlot = (JRPiePlot)getPlot();
		boolean isCircular = jrPiePlot.getCircular() == null ? true : jrPiePlot.getCircular();
		piePlot.setCircular(isCircular);

		boolean isShowLabels = jrPiePlot.getShowLabels() == null ? true : jrPiePlot.getShowLabels();
		
		if (isShowLabels)
		{
			PieSectionLabelGenerator labelGenerator = (PieSectionLabelGenerator)getLabelGenerator();
			JRItemLabel itemLabel = jrPiePlot.getItemLabel();
			if (labelGenerator != null)
			{
				piePlot.setLabelGenerator(labelGenerator);
			}
			else if (jrPiePlot.getLabelFormat() != null)
			{
				piePlot.setLabelGenerator(
					new StandardPieSectionLabelGenerator(jrPiePlot.getLabelFormat(), 
							NumberFormat.getNumberInstance(getLocale()),
			                NumberFormat.getPercentInstance(getLocale()))
					);
			}// the default section label is just the key, so there's no need to set localized number formats
	//		else if (itemLabel != null && itemLabel.getMask() != null)
	//		{
	//			piePlot.setLabelGenerator(
	//					new StandardPieSectionLabelGenerator(itemLabel.getMask())
	//					);
	//		}

			piePlot.setLabelFont(
				fontUtil.getAwtFont(
					getFont(itemLabel == null ? null : itemLabel.getFont()), 
					getLocale()
					)
				);
	
			if (itemLabel != null && itemLabel.getColor() != null)
			{
				piePlot.setLabelPaint(itemLabel.getColor());
			}
			else
			{
				piePlot.setLabelPaint(getChart().getForecolor());
			}
	
			if (itemLabel != null && itemLabel.getBackgroundColor() != null)
			{
				piePlot.setLabelBackgroundPaint(itemLabel.getBackgroundColor());
			}
			else
			{
				piePlot.setLabelBackgroundPaint(getChart().getBackcolor());
			}
		}
		else
		{
			piePlot.setLabelGenerator(null);
		}
		
		if (jrPiePlot.getLegendLabelFormat() != null)
		{
			piePlot.setLegendLabelGenerator(
				new StandardPieSectionLabelGenerator(jrPiePlot.getLegendLabelFormat(),
						NumberFormat.getNumberInstance(getLocale()),
						NumberFormat.getPercentInstance(getLocale()))
				);
		}// the default legend label is just the key, so there's no need to set localized number formats
		
		return jfreeChart;
	}


	protected JFreeChart createScatterChart() throws JRException 
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = ChartFactory.createScatterPlot(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRScatterPlot)getPlot()).getXAxisLabelExpression()),
				evaluateTextExpression(((JRScatterPlot)getPlot()).getYAxisLabelExpression() ),
				(XYDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(jfreeChart);
		XYLineAndShapeRenderer plotRenderer = (XYLineAndShapeRenderer) ((XYPlot)jfreeChart.getPlot()).getRenderer();

		JRScatterPlot scatterPlot = (JRScatterPlot) getPlot();
		boolean isShowLines = scatterPlot.getShowLines() == null ? true : scatterPlot.getShowLines();
		boolean isShowShapes = scatterPlot.getShowShapes() == null ? true : scatterPlot.getShowShapes();
		
		plotRenderer.setDefaultLinesVisible(isShowLines);
		plotRenderer.setDefaultShapesVisible(isShowShapes);

		// Handle the axis formating for the category axis
		configureAxis(jfreeChart.getXYPlot().getDomainAxis(), scatterPlot.getXAxisLabelFont(),
				scatterPlot.getXAxisLabelColor(), scatterPlot.getXAxisTickLabelFont(),
				scatterPlot.getXAxisTickLabelColor(), scatterPlot.getXAxisTickLabelMask(), scatterPlot.getXAxisVerticalTickLabels(),
				scatterPlot.getXAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(scatterPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(scatterPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(jfreeChart.getXYPlot().getRangeAxis(), scatterPlot.getYAxisLabelFont(),
				scatterPlot.getYAxisLabelColor(), scatterPlot.getYAxisTickLabelFont(),
				scatterPlot.getYAxisTickLabelColor(), scatterPlot.getYAxisTickLabelMask(), scatterPlot.getYAxisVerticalTickLabels(),
				scatterPlot.getYAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(scatterPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(scatterPlot.getRangeAxisMaxValueExpression()));

		return jfreeChart;
	}


	/**
	 * @deprecated To be removed
	 */
	protected JFreeChart createStackedBar3DChart() throws JRException
	{
		return createStackedBarChart();
	}


	/**
	 *
	 */
	protected JFreeChart createStackedBarChart() throws JRException
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createStackedBarChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRBarPlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRBarPlot)getPlot()).getValueAxisLabelExpression()),
				(CategoryDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false
				);

		configureChart(jfreeChart);

		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		JRBarPlot barPlot = (JRBarPlot)getPlot();
		//plot.setNoDataMessage("No data to display");
		boolean isShowTickMarks = barPlot.getShowTickMarks() == null ? true : barPlot.getShowTickMarks();
		boolean isShowTickLabels = barPlot.getShowTickLabels() == null ? true : barPlot.getShowTickLabels();
		boolean isShowLabels = barPlot.getShowLabels() == null ? false : barPlot.getShowLabels();
		
		categoryPlot.getDomainAxis().setTickMarksVisible(isShowTickMarks);
		categoryPlot.getDomainAxis().setTickLabelsVisible(isShowTickLabels);
		((NumberAxis)categoryPlot.getRangeAxis()).setTickMarksVisible(isShowTickMarks);
		((NumberAxis)categoryPlot.getRangeAxis()).setTickLabelsVisible(isShowTickLabels);

		BarRenderer categoryRenderer = (BarRenderer)categoryPlot.getRenderer();
		categoryRenderer.setDefaultItemLabelsVisible( isShowLabels );
		Comparable<?> rangeAxisMaxValue = (Comparable<?>)evaluateExpression(barPlot.getRangeAxisMaxValueExpression());
		if (isShowLabels)
		{
			if (rangeAxisMaxValue == null)
			{
				//in case the bars are horizontal and there was no range max value specified, 
				//we try to make the axis a little longer for labels to fit on the plot
				Axis axis = categoryPlot.getRangeAxis();
				if (axis instanceof ValueAxis)
				{
					if (!(axis instanceof DateAxis))
					{
						float rangeAxisMaxRatio = 1f;
						
						if (barPlot.getOrientation() == PlotOrientationEnum.HORIZONTAL)
						{
							rangeAxisMaxRatio = 
								JRPropertiesUtil.getInstance(chartContext.getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.horizontal.range.max.value.ratio", 1.25f
									);
						}
						else
						{
							rangeAxisMaxRatio = 
								JRPropertiesUtil.getInstance(chartContext.getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.vertical.range.max.value.ratio", 1.1f
									);
						}
						
						((ValueAxis)axis).setUpperBound(((ValueAxis)axis).getUpperBound() * rangeAxisMaxRatio);
					}
				}
			}

			JRItemLabel itemLabel = barPlot.getItemLabel();
			
			categoryRenderer.setDefaultItemLabelFont(
				fontUtil.getAwtFont(
					getFont(itemLabel == null ? null : itemLabel.getFont()), 
					getLocale()
					)
				);
			
			if (itemLabel != null)
			{
				if (itemLabel.getColor() != null)
				{
					categoryRenderer.setDefaultItemLabelPaint(itemLabel.getColor());
				}
				else
				{
					categoryRenderer.setDefaultItemLabelPaint(getChart().getForecolor());
				}
//				categoryRenderer.setDefaultFillPaint(itemLabel.getBackgroundColor());
//				if (itemLabel.getMask() != null)
//				{
//					categoryRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
//							StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, 
//							new DecimalFormat(itemLabel.getMask())));
//				}
//				else
//				{
					categoryRenderer.setDefaultItemLabelGenerator((CategoryItemLabelGenerator)getLabelGenerator());
//				}
			}
			else
			{
				categoryRenderer.setDefaultItemLabelGenerator((CategoryItemLabelGenerator)getLabelGenerator());
				categoryRenderer.setDefaultItemLabelPaint(getChart().getForecolor());
			}
		}
		categoryRenderer.setShadowVisible(false);

		// Handle the axis formating for the category axis
		configureAxis(categoryPlot.getDomainAxis(), barPlot.getCategoryAxisLabelFont(),
				barPlot.getCategoryAxisLabelColor(), barPlot.getCategoryAxisTickLabelFont(),
				barPlot.getCategoryAxisTickLabelColor(), barPlot.getCategoryAxisTickLabelMask(), barPlot.getCategoryAxisVerticalTickLabels(),
				barPlot.getCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(categoryPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
				barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
				barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
				barPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMinValueExpression()),
				rangeAxisMaxValue);

		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createStackedAreaChart() throws JRException
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createStackedAreaChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRAreaPlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRAreaPlot)getPlot()).getValueAxisLabelExpression()),
				(CategoryDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false
				);

		configureChart(jfreeChart);
		JRAreaPlot areaPlot = (JRAreaPlot)getPlot();

		// Handle the axis formating for the category axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getDomainAxis(), areaPlot.getCategoryAxisLabelFont(),
				areaPlot.getCategoryAxisLabelColor(), areaPlot.getCategoryAxisTickLabelFont(),
				areaPlot.getCategoryAxisTickLabelColor(), areaPlot.getCategoryAxisTickLabelMask(), areaPlot.getCategoryAxisVerticalTickLabels(),
				areaPlot.getCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getRangeAxis(), areaPlot.getValueAxisLabelFont(),
				areaPlot.getValueAxisLabelColor(), areaPlot.getValueAxisTickLabelFont(),
				areaPlot.getValueAxisTickLabelColor(), areaPlot.getValueAxisTickLabelMask(), areaPlot.getValueAxisVerticalTickLabels(),
				areaPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMaxValueExpression()));

		((CategoryPlot)jfreeChart.getPlot()).getDomainAxis().setCategoryMargin(0);
		
		return jfreeChart;
	}

	protected JFreeChart createXyAreaChart() throws JRException 
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = 
			ChartFactory.createXYAreaChart(
				evaluateTextExpression(getChart().getTitleExpression() ),
				evaluateTextExpression(((JRAreaPlot)getPlot()).getCategoryAxisLabelExpression() ),
				evaluateTextExpression(((JRAreaPlot)getPlot()).getValueAxisLabelExpression()),
				(XYDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false
				);

		configureChart(jfreeChart);
		JRAreaPlot areaPlot = (JRAreaPlot)getPlot();

		// Handle the axis formating for the category axis
		configureAxis(jfreeChart.getXYPlot().getDomainAxis(), areaPlot.getCategoryAxisLabelFont(),
				areaPlot.getCategoryAxisLabelColor(), areaPlot.getCategoryAxisTickLabelFont(),
				areaPlot.getCategoryAxisTickLabelColor(), areaPlot.getCategoryAxisTickLabelMask(), areaPlot.getCategoryAxisVerticalTickLabels(),
				areaPlot.getCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMaxValueExpression()));
		// Handle the axis formating for the value axis
		configureAxis(jfreeChart.getXYPlot().getRangeAxis(), areaPlot.getValueAxisLabelFont(),
				areaPlot.getValueAxisLabelColor(), areaPlot.getValueAxisTickLabelFont(),
				areaPlot.getValueAxisTickLabelColor(), areaPlot.getValueAxisTickLabelMask(), areaPlot.getValueAxisVerticalTickLabels(),
				areaPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMaxValueExpression()));

		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createXYBarChart() throws JRException
	{
		IntervalXYDataset tmpDataset = (IntervalXYDataset)getDataset();

		boolean isDate = true;
		if ( getChart().getDataset().getDatasetType() == JRChartDataset.XY_DATASET )
		{
			isDate = false;
		}

		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createXYBarChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRBarPlot)getPlot()).getCategoryAxisLabelExpression()),
				isDate,
				evaluateTextExpression(((JRBarPlot)getPlot()).getValueAxisLabelExpression()),
				tmpDataset,
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false
				);

		configureChart(jfreeChart);

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		//plot.setNoDataMessage("No data to display");
//		((XYPlot)plot.getDomainAxis()).setTickMarksVisible(
//			((JRFillBarPlot)getPlot()).isShowTickMarks()
//			);
//		((CategoryAxis)plot.getDomainAxis()).setTickLabelsVisible(
//				((JRFillBarPlot)getPlot()).isShowTickLabels()
//				);
//		((NumberAxis)plot.getRangeAxis()).setTickMarksVisible(
//				((JRFillBarPlot)getPlot()).isShowTickMarks()
//				);
//		((NumberAxis)plot.getRangeAxis()).setTickLabelsVisible(
//				((JRFillBarPlot)getPlot()).isShowTickLabels()
//				);


		XYBarRenderer itemRenderer = (XYBarRenderer)xyPlot.getRenderer();
		itemRenderer.setDefaultItemLabelGenerator((XYItemLabelGenerator)getLabelGenerator() );
		itemRenderer.setShadowVisible(false);
		

		JRBarPlot barPlot = (JRBarPlot)getPlot();
		boolean isShowLabels = barPlot.getShowLabels() == null ? false : barPlot.getShowLabels();
		
		itemRenderer.setDefaultItemLabelsVisible( isShowLabels );

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), barPlot.getCategoryAxisLabelFont(),
				barPlot.getCategoryAxisLabelColor(), barPlot.getCategoryAxisTickLabelFont(),
				barPlot.getCategoryAxisTickLabelColor(), barPlot.getCategoryAxisTickLabelMask(), barPlot.getCategoryAxisVerticalTickLabels(),
				barPlot.getCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
				barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
				barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
				barPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMaxValueExpression()));

		return jfreeChart;
	}


	protected JFreeChart createXyLineChart() throws JRException 
	{
		JRLinePlot linePlot = (JRLinePlot) getPlot();

		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = 
			ChartFactory.createXYLineChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(linePlot.getCategoryAxisLabelExpression()),
				evaluateTextExpression(linePlot.getValueAxisLabelExpression() ),
				(XYDataset)getDataset(),
				ChartUtil.getPlotOrientation(linePlot.getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(jfreeChart);

		// Handle the axis formating for the category axis
		configureAxis(jfreeChart.getXYPlot().getDomainAxis(), linePlot.getCategoryAxisLabelFont(),
				linePlot.getCategoryAxisLabelColor(), linePlot.getCategoryAxisTickLabelFont(),
				linePlot.getCategoryAxisTickLabelColor(), linePlot.getCategoryAxisTickLabelMask(), linePlot.getCategoryAxisVerticalTickLabels(),
				linePlot.getCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMaxValueExpression()));
		
		// Handle the axis formating for the value axis
		configureAxis(jfreeChart.getXYPlot().getRangeAxis(), linePlot.getValueAxisLabelFont(),
				linePlot.getValueAxisLabelColor(), linePlot.getValueAxisTickLabelFont(),
				linePlot.getValueAxisTickLabelColor(), linePlot.getValueAxisTickLabelMask(), linePlot.getValueAxisVerticalTickLabels(),
				linePlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(linePlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(linePlot.getRangeAxisMaxValueExpression()));

		XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer) jfreeChart.getXYPlot().getRenderer();
		boolean isShowShapes = linePlot.getShowShapes() == null ? true : linePlot.getShowShapes();
		boolean isShowLines = linePlot.getShowLines() == null ? true : linePlot.getShowLines();
		lineRenderer.setDefaultShapesVisible(isShowShapes);
		lineRenderer.setDefaultLinesVisible(isShowLines);

		return jfreeChart;
	}

	protected JFreeChart createTimeSeriesChart() throws JRException 
	{
		String timeAxisLabel = evaluateTextExpression(((JRTimeSeriesPlot)getPlot()).getTimeAxisLabelExpression());
		String valueAxisLabel = evaluateTextExpression(((JRTimeSeriesPlot)getPlot()).getValueAxisLabelExpression());

		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = ChartFactory.createTimeSeriesChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				timeAxisLabel,
				valueAxisLabel,
				(TimeSeriesCollection)getDataset(),
				isShowLegend(),
				true,
				false );

		configureChart(jfreeChart);

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		JRTimeSeriesPlot timeSeriesPlot = (JRTimeSeriesPlot)getPlot();

		XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
		
		boolean isShowShapes = timeSeriesPlot.getShowShapes() == null ? true : timeSeriesPlot.getShowShapes();
		boolean isShowLines = timeSeriesPlot.getShowLines() == null ? true : timeSeriesPlot.getShowLines();
		lineRenderer.setDefaultLinesVisible(isShowLines);
		lineRenderer.setDefaultShapesVisible(isShowShapes);

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), timeSeriesPlot.getTimeAxisLabelFont(),
				timeSeriesPlot.getTimeAxisLabelColor(), timeSeriesPlot.getTimeAxisTickLabelFont(),
				timeSeriesPlot.getTimeAxisTickLabelColor(), timeSeriesPlot.getTimeAxisTickLabelMask(), timeSeriesPlot.getTimeAxisVerticalTickLabels(),
				timeSeriesPlot.getTimeAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(timeSeriesPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(timeSeriesPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), timeSeriesPlot.getValueAxisLabelFont(),
				timeSeriesPlot.getValueAxisLabelColor(), timeSeriesPlot.getValueAxisTickLabelFont(),
				timeSeriesPlot.getValueAxisTickLabelColor(), timeSeriesPlot.getValueAxisTickLabelMask(), timeSeriesPlot.getValueAxisVerticalTickLabels(),
				timeSeriesPlot.getValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(timeSeriesPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(timeSeriesPlot.getRangeAxisMaxValueExpression()));
		return jfreeChart;
	}


	/**
	 *
	 */
	protected JFreeChart createGanttChart() throws JRException
	{
		//FIXMECHART legend/tooltip/url should come from plot?
		
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart =
			ChartFactory.createGanttChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRBarPlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRBarPlot)getPlot()).getValueAxisLabelExpression()),
				(GanttCategoryDataset)getDataset(),
				isShowLegend(),
				true,  //FIXMECHART tooltip: I guess BarPlot is not the best for gantt
				false
				);

		configureChart(jfreeChart);
		
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		//plot.setNoDataMessage("No data to display");
		
		JRBarPlot barPlot = (JRBarPlot)getPlot();
		boolean isShowTickMarks = barPlot.getShowTickMarks() == null ? true : barPlot.getShowTickMarks();
		boolean isShowTickLabels = barPlot.getShowTickLabels() == null ? true : barPlot.getShowTickLabels();
		boolean isShowLabels = barPlot.getShowLabels() == null ? false : barPlot.getShowLabels();
		
		categoryPlot.getDomainAxis().setTickMarksVisible(isShowTickMarks);
		categoryPlot.getDomainAxis().setTickLabelsVisible(isShowTickLabels);
		// Handle the axis formating for the category axis
		configureAxis(
			categoryPlot.getDomainAxis(), barPlot.getCategoryAxisLabelFont(),
			barPlot.getCategoryAxisLabelColor(), barPlot.getCategoryAxisTickLabelFont(),
			barPlot.getCategoryAxisTickLabelColor(), barPlot.getCategoryAxisTickLabelMask(), barPlot.getCategoryAxisVerticalTickLabels(),
			barPlot.getCategoryAxisLineColor(), false, null, null
			);
		((DateAxis)categoryPlot.getRangeAxis()).setTickMarksVisible(isShowTickMarks);
		((DateAxis)categoryPlot.getRangeAxis()).setTickLabelsVisible(isShowTickLabels);
		// Handle the axis formating for the value axis
		configureAxis(
			categoryPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
			barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
			barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
			barPlot.getValueAxisLineColor(), true,
			(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMinValueExpression()),
			(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMaxValueExpression()));

		GanttRenderer categoryRenderer = (GanttRenderer)categoryPlot.getRenderer();
		categoryRenderer.setDefaultItemLabelGenerator((CategoryItemLabelGenerator)getLabelGenerator());
		categoryRenderer.setDefaultItemLabelsVisible(isShowLabels);
		categoryRenderer.setShadowVisible(false);

		return jfreeChart;
	}


	/**
	 * Converts a JasperReport data range into one understood by JFreeChart.
	 *
	 * @param dataRange the JasperReport version of the range
	 * @return the JFreeChart version of the range
	 * @throws JRException thrown when the low value of the range is greater than the
	 * 						high value
	 */
	protected Range convertRange(JRDataRange dataRange) throws JRException
	{
		if (dataRange == null)
		{
			return null;
		}

		Number low = (Number)evaluateExpression(dataRange.getLowExpression());
		Number high = (Number)evaluateExpression(dataRange.getHighExpression());
		return new Range( low != null ? low.doubleValue() : 0.0,
								 high != null ? high.doubleValue() : 100.0);
	}

	/**
	 * Converts a JasperReports meter interval to one that JFreeChart understands.
	 *
	 * @param interval the JasperReports definition of an interval
	 * @return the JFreeChart version of the same interval
	 * @throws JRException thrown when the interval contains an invalid range
	 */
	protected MeterInterval convertInterval(JRMeterInterval interval) throws JRException
	{
		String label = interval.getLabel();
		if (label == null)
		{
			label = "";
		}

		Range range = convertRange(interval.getDataRange());

		Color color = interval.getBackgroundColor() == null ? getChart().getBackcolor() : interval.getBackgroundColor();//FIXMETHEME check this null protection
		float[] components = color.getRGBColorComponents(null);

		float alpha = (float)(interval.getAlpha() == null ? JRMeterInterval.DEFAULT_TRANSPARENCY : interval.getAlpha());
		Color alphaColor = new Color(components[0], components[1], components[2], alpha);

		return new MeterInterval(label, range, alphaColor, null, alphaColor);
	}

	/**
	 *
	 */
	protected JFreeChart createMeterChart() throws JRException 
	{
		JRMeterPlot jrPlot = (JRMeterPlot)getPlot();

		// Start by creating the plot that will hold the meter
		MeterPlot chartPlot = new MeterPlot((ValueDataset)getDataset());

		// Set the shape
		MeterShapeEnum shape = jrPlot.getShape() == null ? MeterShapeEnum.PIE : jrPlot.getShape();
		switch (shape)
		{
			case CHORD :
				chartPlot.setDialShape(DialShape.CHORD);
				break;
			case CIRCLE :
				chartPlot.setDialShape(DialShape.CIRCLE);
				break;
			case PIE :
			default :
				chartPlot.setDialShape(DialShape.PIE);
		}

		// Set the meter's range
		chartPlot.setRange(convertRange(jrPlot.getDataRange()));

		// Set the size of the meter
		int meterAngle = jrPlot.getMeterAngle() == null ? 180 : jrPlot.getMeterAngle();
		chartPlot.setMeterAngle(meterAngle);

		// Set the units - this is just a string that will be shown next to the
		// value
		String units = jrPlot.getUnits();
		if (units != null && units.length() > 0)
		{
			chartPlot.setUnits(units);
		}

		// Set the font used for tick labels
		if (jrPlot.getTickLabelFont() != null)
		{
			chartPlot.setTickLabelFont(fontUtil.getAwtFont(jrPlot.getTickLabelFont(), getLocale()));
		}
		
		// Set the spacing between ticks.  I hate the name "tickSize" since to me it
		// implies I am changing the size of the tick, not the spacing between them.
		double tickInterval = jrPlot.getTickInterval() == null ? 10.0 : jrPlot.getTickInterval();
		chartPlot.setTickSize(tickInterval);

		// Set all the colors we support
		Color color = jrPlot.getMeterBackgroundColor();
		if (color != null)
		{
			chartPlot.setDialBackgroundPaint(color);
		}

		color = jrPlot.getNeedleColor();
		if (color != null)
		{
			chartPlot.setNeedlePaint(color);
		}
		
		// localizing the default format, can be overridden by display.getMask()
		chartPlot.setTickLabelFormat(NumberFormat.getInstance(getLocale()));

		// Set how the value is displayed.
		JRValueDisplay display = jrPlot.getValueDisplay();
		if (display != null)
		{
			if (display.getColor() != null)
			{
				chartPlot.setValuePaint(display.getColor());
			}

			if (display.getMask() != null)
			{
				chartPlot.setTickLabelFormat(new DecimalFormat(display.getMask(), 
						DecimalFormatSymbols.getInstance(getLocale())));
			}
			
			if (display.getFont() != null)
			{
				chartPlot.setValueFont(fontUtil.getAwtFont(display.getFont(), getLocale()));
			}

		}

		color = jrPlot.getTickColor();
		if (color != null)
		{
			chartPlot.setTickPaint(color);
		}

		// Now define all of the intervals, setting their range and color
		List<JRMeterInterval> intervals = jrPlot.getIntervals();
		if (intervals != null)
		{
			Iterator<JRMeterInterval> iter = intervals.iterator();
			while (iter.hasNext())
			{
				JRMeterInterval interval = iter.next();
				chartPlot.addInterval(convertInterval(interval));
			}
		}

		// Actually create the chart around the plot
		JFreeChart jfreeChart = 
			new JFreeChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				null, 
				chartPlot, 
				isShowLegend()
				);

		// Set all the generic options
		configureChart(jfreeChart);

		return jfreeChart;
	}

	/**
	 * Build and run a thermometer chart.  JFreeChart thermometer charts have some
	 * limitations.  They always have a maximum of three ranges, and the colors of those
	 * ranges seems to be fixed.
	 */
	protected JFreeChart createThermometerChart() throws JRException 
	{
		JRThermometerPlot jrPlot = (JRThermometerPlot)getPlot();

		// Create the plot that will hold the thermometer.
		ThermometerPlot chartPlot = new ThermometerPlot((ValueDataset)getDataset());
		
		ChartUtil chartUtil = ChartUtil.getInstance(chartContext.getJasperReportsContext());
		// setting localized range axis formatters
		chartPlot.getRangeAxis().setStandardTickUnits(chartUtil.createIntegerTickUnits(getLocale()));

		Range range = convertRange(jrPlot.getDataRange());

		// Set the boundary of the thermometer
		chartPlot.setLowerBound(range.getLowerBound());
		chartPlot.setUpperBound(range.getUpperBound());

		// Units can only be Fahrenheit, Celsius or none, so turn off for now.
		chartPlot.setUnits(ThermometerPlot.UNITS_NONE);

		// Set the color of the mercury.  Only used when the value is outside of
		// any defined ranges.
		Color color = jrPlot.getMercuryColor();
		if (color != null)
		{
			chartPlot.setMercuryPaint(color);
			chartPlot.setUseSubrangePaint(false);
		}

		// localizing the default format, can be overridden by display.getMask()
		chartPlot.setValueFormat(NumberFormat.getNumberInstance(getLocale()));
		
		// Set the formatting of the value display
		JRValueDisplay display = jrPlot.getValueDisplay();
		if (display != null)
		{
			if (display.getColor() != null)
			{
				chartPlot.setValuePaint(display.getColor());
			}
			
			if (display.getMask() != null)
			{ 
				chartPlot.setValueFormat(new DecimalFormat(display.getMask(),
						DecimalFormatSymbols.getInstance(getLocale())));
			}
			
			if (display.getFont() != null)
			{
				chartPlot.setValueFont(fontUtil.getAwtFont(display.getFont(), getLocale()));
			}
		}

		// Set the location of where the value is displayed
		switch (jrPlot.getValueLocation())
		{
		  case NONE:
			 chartPlot.setValueLocation(ThermometerPlot.NONE);
			 break;
		  case LEFT:
			 chartPlot.setValueLocation(ThermometerPlot.LEFT);
			 break;
		  case RIGHT:
			 chartPlot.setValueLocation(ThermometerPlot.RIGHT);
			 break;
		  case BULB:
		  default:
			 chartPlot.setValueLocation(ThermometerPlot.BULB);
			 break;
		}

		// Define the three ranges
		range = convertRange(jrPlot.getLowRange());
		if (range != null)
		{
			chartPlot.setSubrangeInfo(2, range.getLowerBound(), range.getUpperBound());
		}

		range = convertRange(jrPlot.getMediumRange());
		if (range != null)
		{
			chartPlot.setSubrangeInfo(1, range.getLowerBound(), range.getUpperBound());
		}

		range = convertRange(jrPlot.getHighRange());
		if (range != null)
		{
			chartPlot.setSubrangeInfo(0, range.getLowerBound(), range.getUpperBound());
		}

		// Build a chart around this plot
		JFreeChart jfreeChart = 
				new JFreeChart(
					evaluateTextExpression(getChart().getTitleExpression()),
					null, 
					chartPlot, 
					isShowLegend()
					);

		// Set the generic options
		configureChart(jfreeChart);
		
		return jfreeChart;
	}

	/**
	 *
	 */
	protected JFreeChart createDialChart() throws JRException
	{

		JRMeterPlot jrPlot = (JRMeterPlot)getPlot();

		// get data for diagrams
		DialPlot dialPlot = new DialPlot();
		dialPlot.setDataset((ValueDataset)getDataset());
		StandardDialFrame dialFrame = new StandardDialFrame();
		dialPlot.setDialFrame(dialFrame);

		DialBackground db = new DialBackground(jrPlot.getBackcolor());
		dialPlot.setBackground(db);
		Range range = convertRange(jrPlot.getDataRange());
		int tickCount = jrPlot.getTickCount() != null && jrPlot.getTickCount() > 1 ? jrPlot.getTickCount() : 7;
		StandardDialScale scale =
			new StandardDialScale(
				range.getLowerBound(),
				range.getUpperBound(),
				225,
				-270,
				(range.getUpperBound() - range.getLowerBound())/(tickCount-1),
				15
				);
		scale.setTickRadius(0.9);
		scale.setTickLabelOffset(0.16);
		if (jrPlot.getTickLabelFont() != null)
		{
			scale.setTickLabelFont(fontUtil.getAwtFont(jrPlot.getTickLabelFont(), getLocale()));
		}
		scale.setMajorTickStroke(new BasicStroke(1f));
		scale.setMinorTickStroke(new BasicStroke(0.3f));
		scale.setMajorTickPaint(jrPlot.getTickColor());
		scale.setMinorTickPaint(jrPlot.getTickColor());

		scale.setTickLabelsVisible(true);
		scale.setFirstTickLabelVisible(true);
		
		// localizing the default tick label formatter
		scale.setTickLabelFormatter(new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(getLocale())));

		dialPlot.addScale(0, scale);
		List<JRMeterInterval> intervals = jrPlot.getIntervals();
		if (intervals != null && intervals.size() > 0)
		{
			int size = Math.min(3, intervals.size());
			int colorStep = 0;
			if (size > 0)
			{
				colorStep = 255 / size;
			}
			for (int i = 0; i < size; i++)
			{
				JRMeterInterval interval = intervals.get(i);
				Range intervalRange = convertRange(interval.getDataRange());

				Color color = new Color(255 - colorStep * i, 0 + colorStep * i, 0);
				
				StandardDialRange dialRange =
					new StandardDialRange(
						intervalRange.getLowerBound(),
						intervalRange.getUpperBound(),
						interval.getBackgroundColor() == null
							? color
							: interval.getBackgroundColor()
						);
				dialRange.setInnerRadius(0.41);
				dialRange.setOuterRadius(0.41);
				dialPlot.addLayer(dialRange);
			}
		}

		JRValueDisplay display = jrPlot.getValueDisplay();
		String displayVisibility = display != null && getChart().hasProperties() 
			? getChart().getPropertiesMap().getProperty(PROPERTY_DIAL_VALUE_DISPLAY_VISIBLE) : "false";
		if (Boolean.valueOf(displayVisibility))
		{
			DialValueIndicator dvi = new DialValueIndicator(0);
			// we need the transparent paint because null is not accepted
			dvi.setBackgroundPaint(TRANSPARENT_PAINT);
//			dvi.setFont(fontUtil.getAwtFont(jrFont).deriveFont(10f).deriveFont(Font.BOLD));
			dvi.setOutlinePaint(TRANSPARENT_PAINT);
			dvi.setPaint(Color.WHITE);

			String pattern = display.getMask() != null ? display.getMask() : "#,##0.####";
			if (pattern != null)
			{
				dvi.setNumberFormat( new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(getLocale())));
			}
			dvi.setRadius(0.15);
			dvi.setValueAnchor(RectangleAnchor.CENTER);
			dvi.setTextAnchor(TextAnchor.CENTER);
			//dvi.setTemplateValue(Double.valueOf(getDialTickValue(dialPlot.getValue(0),dialUnitScale)));
			dialPlot.addLayer(dvi);
		}
		String label = getChart().hasProperties() ?
				getChart().getPropertiesMap().getProperty(PROPERTY_DIAL_LABEL) : null;
		
		if (label != null)
		{
			JRFont displayFont = display == null ? null : display.getFont();
			
			String[] textLines = label.split("\\n");
			for (int i = 0; i < textLines.length; i++)
			{
				DialTextAnnotation dialAnnotation = new DialTextAnnotation(textLines[i]);
				if (displayFont != null)
				{
					dialAnnotation.setFont(fontUtil.getAwtFont(displayFont, getLocale()));
				}
				if (display != null && display.getColor() != null)
				{
					dialAnnotation.setPaint(jrPlot.getValueDisplay().getColor());
				}
				dialAnnotation.setRadius(Math.sin(Math.PI/4.0) + i/10.0);
				dialAnnotation.setAnchor(TextAnchor.CENTER);
				dialPlot.addLayer(dialAnnotation);
			}
		}

		DialPointer needle = new DialPointer.Pointer();

		needle.setVisible(true);
		needle.setRadius(0.91);
		dialPlot.addLayer(needle);

		DialCap cap = new DialCap();
		cap.setRadius(0.05);
		cap.setFillPaint(Color.DARK_GRAY);
		cap.setOutlinePaint(Color.GRAY);
		cap.setOutlineStroke(new BasicStroke(0.5f));
		dialPlot.setCap(cap);

		JFreeChart jfreeChart =
		new JFreeChart(
			evaluateTextExpression(getChart().getTitleExpression()),
			null,
			dialPlot,
			isShowLegend()
			);

		// Set all the generic options
		configureChart(jfreeChart);

		return jfreeChart;

	}
	/**
	 * Specifies the axis location.
	 * It has to be overridden for child themes with another default axis location
	 */
	protected AxisLocation getChartAxisLocation(JRChartAxis chartAxis)
	{
		return chartAxis.getPosition() != null && chartAxis.getPosition() == AxisPositionEnum.RIGHT_OR_BOTTOM
				? AxisLocation.BOTTOM_OR_RIGHT 
				: AxisLocation.TOP_OR_LEFT;
	}
	
	/**
	 *
	 */
	protected void setAxisBounds(
		Axis axis,
		boolean isRangeAxis,
		Comparable<?> axisMinValue,
		Comparable<?> axisMaxValue
		)
	{
		if (axis instanceof ValueAxis)
		{
			if (axis instanceof DateAxis)
			{
				if (axisMinValue != null)
				{
					((DateAxis)axis).setMinimumDate((Date)axisMinValue);
				}
				if (axisMaxValue != null)
				{
					((DateAxis)axis).setMaximumDate((Date)axisMaxValue);
				}
			}
			else
			{
				if (axisMinValue != null)
				{
					((ValueAxis)axis).setLowerBound(((Number)axisMinValue).doubleValue());
				}
				if (axisMaxValue != null)
				{
					((ValueAxis)axis).setUpperBound(((Number)axisMaxValue).doubleValue());
				}
			}
			
			calculateTickUnits(axis, isRangeAxis);
		}
	}

	/**
	 * For a given axis, adjust the tick unit size, in order to 
	 * have a customizable number of ticks on that axis
	 */
	protected void calculateTickUnits(Axis axis, boolean isRangeAxis)
	{
		Integer tickCount = null;
		Number tickInterval = null;
		boolean axisIntegerUnit = false;
		
		if (getChart().hasProperties())
		{
			String tickCountProperty = null;
			String tickIntervalProperty = null;
			String axisIntegerUnitProperty = null;
			
			if (isRangeAxis)
			{
				tickCountProperty = getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_RANGE_AXIS_TICK_COUNT);
				tickIntervalProperty = getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_RANGE_AXIS_TICK_INTERVAL);
				axisIntegerUnitProperty = getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_RANGE_AXIS_INTEGER_UNIT);
			}
			else
			{
				tickCountProperty = getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_DOMAIN_AXIS_TICK_COUNT);
				tickIntervalProperty = getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_DOMAIN_AXIS_TICK_INTERVAL);
				axisIntegerUnitProperty = getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_DOMAIN_AXIS_INTEGER_UNIT);
			}
			if (tickCountProperty != null && tickCountProperty.trim().length() > 0)
			{
				tickCount = Integer.valueOf(tickCountProperty);
			}
			if (tickIntervalProperty != null && tickIntervalProperty.trim().length() > 0)
			{
				tickInterval = Double.valueOf(tickIntervalProperty);
			}
			if (axisIntegerUnitProperty != null && axisIntegerUnitProperty.trim().length() > 0)
			{
				axisIntegerUnit = Boolean.valueOf(axisIntegerUnitProperty);
			}
		}
		
		if (axis instanceof NumberAxis)
		{
			NumberAxis numberAxis = (NumberAxis)axis;
			int axisRange = (int)numberAxis.getRange().getLength();
			
			if (axisIntegerUnit)
			{
				ChartUtil chartUtil = ChartUtil.getInstance(chartContext.getJasperReportsContext());
				numberAxis.setStandardTickUnits(chartUtil.createIntegerTickUnits(getLocale()));
				chartUtil.setAutoTickUnit(numberAxis);
			}
			else if (axisRange > 0)
			{
				if (tickInterval != null)
				{
					if (numberAxis.getNumberFormatOverride() != null)
					{
						numberAxis.setTickUnit(new NumberTickUnit(tickInterval.doubleValue(), numberAxis.getNumberFormatOverride()));
					}
					else
					{
						numberAxis.setTickUnit(new NumberTickUnit(tickInterval.doubleValue(), NumberFormat.getNumberInstance(getLocale())));
					}
				}
				else if (tickCount != null)
				{
					if (numberAxis.getNumberFormatOverride() != null)
					{
						numberAxis.setTickUnit(new NumberTickUnit(axisRange / tickCount, numberAxis.getNumberFormatOverride()));
					}
					else
					{
						numberAxis.setTickUnit(new NumberTickUnit(axisRange / tickCount, NumberFormat.getNumberInstance(getLocale())));
					}
				}
				else
				{
					ChartUtil chartUtil = ChartUtil.getInstance(chartContext.getJasperReportsContext());
					numberAxis.setStandardTickUnits(chartUtil.createStandardTickUnits(getLocale()));
					chartUtil.setAutoTickUnit(numberAxis);
				}
			}
		}
//		else if (axis instanceof DateAxis)
//		{
//			DateAxis dateAxis = (DateAxis)axis;
//			int axisRange = (int)dateAxis.getRange().getLength();
//			if (dateAxis.getDateFormatOverride() != null)
//			{
//				dateAxis.setTickUnit(new DateTickUnit(timePeriodUnit, axisRange/tickCount, dateAxis.getDateFormatOverride()));
//			}
//			else
//			{
//				dateAxis.setTickUnit(new DateTickUnit(timePeriodUnit, axisRange/tickCount));
//			}
//		}
	}
	

	/**
	 *
	 */
	@SuppressWarnings("deprecation")
	private double getLabelRotation()
	{
		return getPlot().getLabelRotation() == null ? 0d : getPlot().getLabelRotation();
	}

	
	/**
	 *
	 */
	private static RectangleEdge getEdge(EdgeEnum position, RectangleEdge defaultPosition)
	{
		RectangleEdge edge = defaultPosition;
		if (position != null)
		{
			switch (position)
			{
				case TOP :
				{
					edge = RectangleEdge.TOP;
					break;
				}
				case BOTTOM :
				{
					edge = RectangleEdge.BOTTOM;
					break;
				}
				case LEFT :
				{
					edge = RectangleEdge.LEFT;
					break;
				}
				case RIGHT :
				{
					edge = RectangleEdge.RIGHT;
					break;
				}
			}
		}
		return edge;
	}

	
	public static final ChartThemeBundle BUNDLE = 
		new ChartThemeBundle()
		{
			private static final String NAME = "default";

			@Override
			public String[] getChartThemeNames() 
			{
				return new String[]{NAME};
			}
		
			@Override
			public ChartTheme getChartTheme(String themeName) 
			{
				if (NAME.equals(themeName))
				{
					return new DefaultChartTheme(); 
				}
				return null;
			}
		};
		
	/**
	 * Specifies whether a chart legend should be visible or no by default.
	 * It has to be overridden for child themes which don't show chart legends
	 */
	protected boolean isShowLegend()
	{
		return getChart().getShowLegend() == null ? true : getChart().getShowLegend();
	}

}
