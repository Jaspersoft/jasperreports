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
package net.sf.jasperreports.chartthemes.spring;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockFrame;
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
import org.jfree.chart.plot.PlotOrientation;
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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.ui.VerticalAlignment;
import org.jfree.chart.util.UnitType;
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

import net.sf.jasperreports.charts.ChartContext;
import net.sf.jasperreports.charts.ChartTheme;
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
import net.sf.jasperreports.charts.fill.DefaultChartTheme;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.charts.type.MeterShapeEnum;
import net.sf.jasperreports.charts.type.PlotOrientationEnum;
import net.sf.jasperreports.charts.type.ScaleTypeEnum;
import net.sf.jasperreports.charts.type.ValueLocationEnum;
import net.sf.jasperreports.charts.util.ChartUtil;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.ModeEnum;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class GenericChartTheme implements ChartTheme
{
	/**
	 *
	 */

	protected Map<String, ?> defaultChartPropertiesMap;
	protected Map<String, ?> defaultPlotPropertiesMap;
	protected Map<String, ?> defaultAxisPropertiesMap;
	protected Map<String, ?> defaultChartTypePropertiesMap;
	
	/**
	 *
	 */
	protected ThreadLocal<ChartContext> threadLocalChartContext = new ThreadLocal<>();
	protected ThreadLocal<FontUtil> threadLocalFontUtil = new ThreadLocal<>();
	

	/**
	 *
	 */
	protected GenericChartTheme()
	{
	}
	
	
	/**
	 *
	 */
	protected ChartContext getChartContext()
	{
		return threadLocalChartContext.get();
	}
	
	
	/**
	 *
	 */
	private void setChartContext(ChartContext chartContext)
	{
		this.threadLocalChartContext.set(chartContext);
		this.threadLocalFontUtil.set(FontUtil.getInstance(chartContext.getJasperReportsContext()));
	}

	
	/**
	 *
	 */
	private FontUtil getFontUtil()
	{
		return threadLocalFontUtil.get();
	}
	
	
	/**
	 *
	 */
	protected JRChart getChart()
	{
		return getChartContext().getChart();
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
		return getChartContext().getDataset();
	}
	
	
	/**
	 *
	 */
	protected Object getLabelGenerator()
	{
		return getChartContext().getLabelGenerator();
	}
	
	
	/**
	 *
	 */
	protected Locale getLocale()
	{
		return getChartContext().getLocale();
	}
	
	
	/**
	 *
	 */
	protected final Object evaluateExpression(JRExpression expression) throws JRException
	{
		return getChartContext().evaluateExpression(expression);
	}

	
	/**
	 *
	 */
	protected final String evaluateTextExpression(JRExpression expression) throws JRException
	{
		return getChartContext().evaluateTextExpression(expression);
	}

	@Override
	public JFreeChart createChart(ChartContext chartContext) throws JRException
	{
		setChartContext(chartContext);
		
		JFreeChart jfreeChart = null;
		
		switch (getChart().getChartType()) {
			case AREA:
				jfreeChart = createAreaChart();
				break;
			case BAR:
				jfreeChart = createBarChart();
				break;
			case BAR3D:
				jfreeChart = createBar3DChart();
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
				jfreeChart = createMeterChart();
				break;
			case MULTI_AXIS:
				//multi-axis charts are dealt with in JRChart
				break;
			case PIE:
				jfreeChart = createPieChart();
				break;
			case PIE3D:
				jfreeChart = createPie3DChart();
				break;
			case SCATTER:
				jfreeChart = createScatterChart();
				break;
			case STACKEDBAR:
				jfreeChart = createStackedBarChart();
				break;
			case STACKEDBAR3D:
				jfreeChart = createStackedBar3DChart();
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
				throw new JRRuntimeException("Chart type " + getChart().getChartType() + " not supported.");
		}

		return jfreeChart;
	}

	/**
	 *
	 */
	protected void configureChart(JFreeChart jfreeChart, JRChartPlot jrPlot) throws JRException
	{	
		Integer defaultBaseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
		
		setChartBackground(jfreeChart);
		setChartTitle(jfreeChart, defaultBaseFontSize);
		setChartSubtitles(jfreeChart, defaultBaseFontSize);
		setChartLegend(jfreeChart, defaultBaseFontSize);
		setChartBorder(jfreeChart);
		
		Boolean isAntiAlias = (Boolean)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.CHART_ANTI_ALIAS);
		if (isAntiAlias != null)
			jfreeChart.setAntiAlias(isAntiAlias);
		
		Double padding = (Double)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.CHART_PADDING);
		UnitType unitType = (UnitType)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.UNIT_TYPE);
		if (padding != null && unitType != null)
		{
			jfreeChart.setPadding(new RectangleInsets(unitType, padding, padding, padding, padding));
		}
		configurePlot(jfreeChart.getPlot(), jrPlot);
	}


	/**
	 *
	 */
	protected void configurePlot(Plot p, JRChartPlot jrPlot)
	{
		RectangleInsets defaultPlotInsets = (RectangleInsets)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_INSETS);
		Paint defaultPlotOutlinePaint = (Paint)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_OUTLINE_PAINT);
		Stroke defaultPlotOutlineStroke = (Stroke)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_OUTLINE_STROKE);
		Boolean defaultPlotOutlineVisible = (Boolean)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_OUTLINE_VISIBLE);
		if (defaultPlotInsets != null) 
			p.setInsets(defaultPlotInsets);

		if (defaultPlotOutlineVisible != null) 
		{
			if (defaultPlotOutlineVisible)
			{
				if (defaultPlotOutlinePaint != null)
					p.setOutlinePaint(defaultPlotOutlinePaint);
				
				if (defaultPlotOutlineStroke != null)
					p.setOutlineStroke(defaultPlotOutlineStroke);
				
				p.setOutlineVisible(true);
			}
			else
			{
				p.setOutlineVisible(false);
			}
		}
		
		setPlotBackground(p, jrPlot);
		if (p instanceof CategoryPlot)
		{
			handleCategoryPlotSettings((CategoryPlot)p, jrPlot);
		}

		setPlotDrawingDefaults(p, jrPlot);
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
	 * 						the mask should be <code>java.text.DecimalFormat</code> mask, and
	 * 						if it is a DateAxis then the mask should be a
	 * 						<code>java.text.SimpleDateFormat</code> mask.
	 * @param verticalTickLabels flag to draw tick labels at 90 degrees
	 * @param lineColor color to use when drawing the axis line and any tick marks
	 * @param isRangeAxis used to distinguish between range and domain axis type
	 */
	protected void configureAxis(
			Axis axis,
			JRFont labelFont,
			Color labelColor,
			JRFont tickLabelFont,
			Color tickLabelColor,
			String tickLabelMask,
			Boolean verticalTickLabels,
			Paint lineColor,
			boolean isRangeAxis,
			Comparable<?> axisMinValue,
			Comparable<?> axisMaxValue
			) throws JRException
	{
		Boolean axisVisible = (Boolean)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_VISIBLE);
		
		if (axisVisible != null && axisVisible)
		{
			setAxisLine(axis, lineColor);

			Double defaultFixedDimension = (Double)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_FIXED_DIMENSION);
			if (defaultFixedDimension != null)
			{
				axis.setFixedDimension(defaultFixedDimension);
			}
			
			Integer baseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
			setAxisLabel(axis, labelFont, labelColor, baseFontSize);
			setAxisTickLabels(axis, tickLabelFont, tickLabelColor, tickLabelMask, baseFontSize);
			setAxisTickMarks(axis, lineColor);
			String timePeriodUnit = isRangeAxis 
				? (String)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.RANGE_AXIS_TIME_PERIOD_UNIT)
				: (String)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.DOMAIN_AXIS_TIME_PERIOD_UNIT);
			setAxisBounds(axis, isRangeAxis, timePeriodUnit, axisMinValue, axisMaxValue);
			
			if (verticalTickLabels != null && axis instanceof ValueAxis)
			{
				((ValueAxis)axis).setVerticalTickLabels(verticalTickLabels);
			}
		}
		else
		{
			axis.setVisible(false);
		}
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

		configureChart(jfreeChart, getPlot());
		JRAreaPlot areaPlot = (JRAreaPlot)getPlot();
		// Handle the axis formating for the category axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getDomainAxis(), areaPlot.getCategoryAxisLabelFont(),
				areaPlot.getCategoryAxisLabelColor(), areaPlot.getCategoryAxisTickLabelFont(),
				areaPlot.getCategoryAxisTickLabelColor(), areaPlot.getCategoryAxisTickLabelMask(), areaPlot.getCategoryAxisVerticalTickLabels(),
				areaPlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getRangeAxis(), areaPlot.getValueAxisLabelFont(),
				areaPlot.getValueAxisLabelColor(), areaPlot.getValueAxisTickLabelFont(),
				areaPlot.getValueAxisTickLabelColor(), areaPlot.getValueAxisTickLabelMask(), areaPlot.getValueAxisVerticalTickLabels(),
				areaPlot.getOwnValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getRangeAxisMaxValueExpression()));
				
		return jfreeChart;
	}


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

		configureChart(jfreeChart, getPlot());

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
				barPlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMaxValueExpression()));

		((NumberAxis)categoryPlot.getRangeAxis()).setTickMarksVisible(isShowTickMarks);
		((NumberAxis)categoryPlot.getRangeAxis()).setTickLabelsVisible(isShowTickLabels);
		// Handle the axis formating for the value axis
		Comparable<?> rangeAxisMaxValue = (Comparable<?>)evaluateExpression(barPlot.getRangeAxisMaxValueExpression());
		configureAxis(categoryPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
				barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
				barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
				barPlot.getOwnValueAxisLineColor(), true,
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
								JRPropertiesUtil.getInstance(getChartContext().getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.horizontal.range.max.value.ratio", 1.25f
									);
						}
						else
						{
							rangeAxisMaxRatio = 
								JRPropertiesUtil.getInstance(getChartContext().getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.vertical.range.max.value.ratio", 1.1f
									);
						}
						
						((ValueAxis)axis).setUpperBound(((ValueAxis)axis).getUpperBound() * rangeAxisMaxRatio);
					}
				}
			}

			JRItemLabel itemLabel = barPlot.getItemLabel();
			Integer baseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
			JRFont font = itemLabel != null && itemLabel.getFont() != null ? itemLabel.getFont() : null;
			categoryRenderer.setDefaultItemLabelFont(getFont(new JRBaseFont(getChart(), null), font, baseFontSize));

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
		JFreeChart jfreeChart = 
			ChartFactory.createBubbleChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRBubblePlot)getPlot()).getXAxisLabelExpression()),
				evaluateTextExpression(((JRBubblePlot)getPlot()).getYAxisLabelExpression()),
				 (XYZDataset)getDataset(),
				 ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				 isShowLegend(),
				 true,
				 false);

		configureChart(jfreeChart, getPlot());

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		JRBubblePlot bubblePlot = (JRBubblePlot)getPlot();
		ScaleTypeEnum scaleType = bubblePlot.getScaleType() == null ? ScaleTypeEnum.ON_RANGE_AXIS : bubblePlot.getScaleType();
		XYBubbleRenderer bubbleRenderer = new XYBubbleRenderer( ChartUtil.getScaleType(scaleType) );
		xyPlot.setRenderer( bubbleRenderer );

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), bubblePlot.getXAxisLabelFont(),
				bubblePlot.getXAxisLabelColor(), bubblePlot.getXAxisTickLabelFont(),
				bubblePlot.getXAxisTickLabelColor(), bubblePlot.getXAxisTickLabelMask(), bubblePlot.getXAxisVerticalTickLabels(),
				bubblePlot.getOwnXAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(bubblePlot.getDomainAxisMinValueExpression()), 
				(Comparable<?>)evaluateExpression(bubblePlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), bubblePlot.getYAxisLabelFont(),
				bubblePlot.getYAxisLabelColor(), bubblePlot.getYAxisTickLabelFont(),
				bubblePlot.getYAxisTickLabelColor(), bubblePlot.getYAxisTickLabelMask(), bubblePlot.getYAxisVerticalTickLabels(),
				bubblePlot.getOwnYAxisLineColor(), true,
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

		configureChart(jfreeChart, getPlot());

		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		JRCandlestickPlot candlestickPlot = (JRCandlestickPlot)getPlot();
		CandlestickRenderer candlestickRenderer = (CandlestickRenderer) xyPlot.getRenderer();
		boolean isShowVolume = candlestickPlot.getShowVolume() == null ? true : candlestickPlot.getShowVolume();
		candlestickRenderer.setDrawVolume(isShowVolume);

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), candlestickPlot.getTimeAxisLabelFont(),
				candlestickPlot.getTimeAxisLabelColor(), candlestickPlot.getTimeAxisTickLabelFont(),
				candlestickPlot.getTimeAxisTickLabelColor(), candlestickPlot.getTimeAxisTickLabelMask(), candlestickPlot.getTimeAxisVerticalTickLabels(),
				candlestickPlot.getOwnTimeAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(candlestickPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(candlestickPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), candlestickPlot.getValueAxisLabelFont(),
				candlestickPlot.getValueAxisLabelColor(), candlestickPlot.getValueAxisTickLabelFont(),
				candlestickPlot.getValueAxisTickLabelColor(), candlestickPlot.getValueAxisTickLabelMask(), candlestickPlot.getValueAxisVerticalTickLabels(),
				candlestickPlot.getOwnValueAxisLineColor(), true,
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

		configureChart(jfreeChart, getPlot());
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
				highLowPlot.getOwnTimeAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(highLowPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(highLowPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), highLowPlot.getValueAxisLabelFont(),
				highLowPlot.getValueAxisLabelColor(), highLowPlot.getValueAxisTickLabelFont(),
				highLowPlot.getValueAxisTickLabelColor(), highLowPlot.getValueAxisTickLabelMask(), highLowPlot.getValueAxisVerticalTickLabels(),
				highLowPlot.getOwnValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(highLowPlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(highLowPlot.getRangeAxisMaxValueExpression()));
		
		return jfreeChart;
	}


	protected JFreeChart createLineChart() throws JRException 
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart freeChart = 
			ChartFactory.createLineChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression( ((JRLinePlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRLinePlot)getPlot()).getValueAxisLabelExpression()),
				(CategoryDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(freeChart, getPlot());

		CategoryPlot categoryPlot = (CategoryPlot)freeChart.getPlot();
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
				linePlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(categoryPlot.getRangeAxis(), linePlot.getValueAxisLabelFont(),
				linePlot.getValueAxisLabelColor(), linePlot.getValueAxisTickLabelFont(),
				linePlot.getValueAxisTickLabelColor(), linePlot.getValueAxisTickLabelMask(), linePlot.getValueAxisVerticalTickLabels(),
				linePlot.getOwnValueAxisLineColor(), true,
				(Comparable<?>)evaluateExpression(linePlot.getRangeAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(linePlot.getRangeAxisMaxValueExpression()));

		return freeChart;
	}


	/**
	 *
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

		configureChart(jfreeChart, getPlot());
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
			}
	//		else if (itemLabel != null && itemLabel.getMask() != null)
	//		{
	//			piePlot.setLabelGenerator(
	//					new StandardPieSectionLabelGenerator(itemLabel.getMask())
	//					);
	//
	//		}
	
			Integer baseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
			JRFont font = itemLabel != null && itemLabel.getFont() != null ? itemLabel.getFont() : null;
			
			piePlot.setLabelFont(getFont(new JRBaseFont(getChart(), null), font, baseFontSize));
	
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
		}
		
		return jfreeChart;
	}


	protected JFreeChart createScatterChart() throws JRException 
	{
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		JFreeChart jfreeChart = 
			ChartFactory.createScatterPlot(
				evaluateTextExpression(getChart().getTitleExpression()),
				evaluateTextExpression(((JRScatterPlot)getPlot()).getXAxisLabelExpression()),
				evaluateTextExpression(((JRScatterPlot)getPlot()).getYAxisLabelExpression()),
				(XYDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(jfreeChart, getPlot());
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
				scatterPlot.getOwnXAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(scatterPlot.getDomainAxisMinValueExpression()), 
				(Comparable<?>)evaluateExpression(scatterPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(jfreeChart.getXYPlot().getRangeAxis(), scatterPlot.getYAxisLabelFont(),
				scatterPlot.getYAxisLabelColor(), scatterPlot.getYAxisTickLabelFont(),
				scatterPlot.getYAxisTickLabelColor(), scatterPlot.getYAxisTickLabelMask(), scatterPlot.getYAxisVerticalTickLabels(),
				scatterPlot.getOwnYAxisLineColor(), true, 
				(Comparable<?>)evaluateExpression(scatterPlot.getRangeAxisMinValueExpression()), 
				(Comparable<?>)evaluateExpression(scatterPlot.getRangeAxisMaxValueExpression()));

		return jfreeChart;
	}


	/**
	 *
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

		configureChart(jfreeChart, getPlot());

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
								JRPropertiesUtil.getInstance(getChartContext().getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.horizontal.range.max.value.ratio", 1.25f
									);
						}
						else
						{
							rangeAxisMaxRatio = 
								JRPropertiesUtil.getInstance(getChartContext().getJasperReportsContext()).getFloatProperty(
									getChart(), "net.sf.jasperreports.chart.bar.vertical.range.max.value.ratio", 1.1f
									);
						}
						
						((ValueAxis)axis).setUpperBound(((ValueAxis)axis).getUpperBound() * rangeAxisMaxRatio);
					}
				}
			}

			JRItemLabel itemLabel = barPlot.getItemLabel();
			
			categoryRenderer.setDefaultItemLabelFont(
					getFontUtil().getAwtFont(
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
				barPlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(categoryPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
				barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
				barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
				barPlot.getOwnValueAxisLineColor(), true,
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

		configureChart(jfreeChart, getPlot());
		JRAreaPlot areaPlot = (JRAreaPlot)getPlot();

		// Handle the axis formating for the category axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getDomainAxis(), areaPlot.getCategoryAxisLabelFont(),
				areaPlot.getCategoryAxisLabelColor(), areaPlot.getCategoryAxisTickLabelFont(),
				areaPlot.getCategoryAxisTickLabelColor(), areaPlot.getCategoryAxisTickLabelMask(), areaPlot.getCategoryAxisVerticalTickLabels(),
				areaPlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(((CategoryPlot)jfreeChart.getPlot()).getRangeAxis(), areaPlot.getValueAxisLabelFont(),
				areaPlot.getValueAxisLabelColor(), areaPlot.getValueAxisTickLabelFont(),
				areaPlot.getValueAxisTickLabelColor(), areaPlot.getValueAxisTickLabelMask(), areaPlot.getValueAxisVerticalTickLabels(),
				areaPlot.getOwnValueAxisLineColor(), true,
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
				evaluateTextExpression(((JRAreaPlot)getPlot()).getCategoryAxisLabelExpression()),
				evaluateTextExpression(((JRAreaPlot)getPlot()).getValueAxisLabelExpression()),
				(XYDataset)getDataset(),
				ChartUtil.getPlotOrientation(getPlot().getOrientation()),
				isShowLegend(),
				true,
				false
				);

		configureChart(jfreeChart, getPlot());
		JRAreaPlot areaPlot = (JRAreaPlot)getPlot();

		// Handle the axis formating for the category axis
		configureAxis(jfreeChart.getXYPlot().getDomainAxis(), areaPlot.getCategoryAxisLabelFont(),
				areaPlot.getCategoryAxisLabelColor(), areaPlot.getCategoryAxisTickLabelFont(),
				areaPlot.getCategoryAxisTickLabelColor(), areaPlot.getCategoryAxisTickLabelMask(), areaPlot.getCategoryAxisVerticalTickLabels(),
				areaPlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(areaPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(jfreeChart.getXYPlot().getRangeAxis(), areaPlot.getValueAxisLabelFont(),
				areaPlot.getValueAxisLabelColor(), areaPlot.getValueAxisTickLabelFont(),
				areaPlot.getValueAxisTickLabelColor(), areaPlot.getValueAxisTickLabelMask(), areaPlot.getValueAxisVerticalTickLabels(),
				areaPlot.getOwnValueAxisLineColor(), true,
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
		if ( getChart().getDataset().getDatasetType() == JRChartDataset.XY_DATASET ){
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

		configureChart(jfreeChart, getPlot());

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		//plot.setNoDataMessage("No data to display");
//		((XYPlot)plot.getDomainAxis()).setTickMarksVisible(
//			((JRBarPlot)getPlot()).isShowTickMarks()
//			);
//		((CategoryAxis)plot.getDomainAxis()).setTickLabelsVisible(
//				((JRBarPlot)getPlot()).isShowTickLabels()
//				);
//		((NumberAxis)plot.getRangeAxis()).setTickMarksVisible(
//				((JRBarPlot)getPlot()).isShowTickMarks()
//				);
//		((NumberAxis)plot.getRangeAxis()).setTickLabelsVisible(
//				((JRBarPlot)getPlot()).isShowTickLabels()
//				);


		XYBarRenderer itemRenderer = (XYBarRenderer)xyPlot.getRenderer();
		itemRenderer.setDefaultItemLabelGenerator((XYItemLabelGenerator)getLabelGenerator());
		itemRenderer.setShadowVisible(false);

		JRBarPlot barPlot = (JRBarPlot)getPlot();
		boolean isShowLabels = barPlot.getShowLabels() == null ? false : barPlot.getShowLabels();
		
		itemRenderer.setDefaultItemLabelsVisible( isShowLabels );

		// Handle the axis formating for the category axis
		configureAxis(xyPlot.getDomainAxis(), barPlot.getCategoryAxisLabelFont(),
				barPlot.getCategoryAxisLabelColor(), barPlot.getCategoryAxisTickLabelFont(),
				barPlot.getCategoryAxisTickLabelColor(), barPlot.getCategoryAxisTickLabelMask(), barPlot.getCategoryAxisVerticalTickLabels(),
				barPlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(barPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
				barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
				barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
				barPlot.getOwnValueAxisLineColor(), true,
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
				evaluateTextExpression(linePlot.getValueAxisLabelExpression()),
				(XYDataset)getDataset(),
				ChartUtil.getPlotOrientation(linePlot.getOrientation()),
				isShowLegend(),
				true,
				false);

		configureChart(jfreeChart, getPlot());

		// Handle the axis formating for the category axis
		configureAxis(jfreeChart.getXYPlot().getDomainAxis(), linePlot.getCategoryAxisLabelFont(),
				linePlot.getCategoryAxisLabelColor(), linePlot.getCategoryAxisTickLabelFont(),
				linePlot.getCategoryAxisTickLabelColor(), linePlot.getCategoryAxisTickLabelMask(), linePlot.getCategoryAxisVerticalTickLabels(),
				linePlot.getOwnCategoryAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(linePlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(jfreeChart.getXYPlot().getRangeAxis(), linePlot.getValueAxisLabelFont(),
				linePlot.getValueAxisLabelColor(), linePlot.getValueAxisTickLabelFont(),
				linePlot.getValueAxisTickLabelColor(), linePlot.getValueAxisTickLabelMask(), linePlot.getValueAxisVerticalTickLabels(),
				linePlot.getOwnValueAxisLineColor(), true,
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
		JFreeChart jfreeChart = 
			ChartFactory.createTimeSeriesChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				timeAxisLabel,
				valueAxisLabel,
				(TimeSeriesCollection)getDataset(),
				isShowLegend(),
				true,
				false );

		configureChart(jfreeChart, getPlot());

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
				timeSeriesPlot.getOwnTimeAxisLineColor(), false,
				(Comparable<?>)evaluateExpression(timeSeriesPlot.getDomainAxisMinValueExpression()),
				(Comparable<?>)evaluateExpression(timeSeriesPlot.getDomainAxisMaxValueExpression()));

		// Handle the axis formating for the value axis
		configureAxis(xyPlot.getRangeAxis(), timeSeriesPlot.getValueAxisLabelFont(),
				timeSeriesPlot.getValueAxisLabelColor(), timeSeriesPlot.getValueAxisTickLabelFont(),
				timeSeriesPlot.getValueAxisTickLabelColor(), timeSeriesPlot.getValueAxisTickLabelMask(), timeSeriesPlot.getValueAxisVerticalTickLabels(),
				timeSeriesPlot.getOwnValueAxisLineColor(), true,
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

		configureChart(jfreeChart, getPlot());
		
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
			barPlot.getOwnCategoryAxisLineColor(), false, null, null
			);
		((DateAxis)categoryPlot.getRangeAxis()).setTickMarksVisible(isShowTickMarks);
		((DateAxis)categoryPlot.getRangeAxis()).setTickLabelsVisible(isShowTickLabels);
		// Handle the axis formating for the value axis
		configureAxis(
			categoryPlot.getRangeAxis(), barPlot.getValueAxisLabelFont(),
			barPlot.getValueAxisLabelColor(), barPlot.getValueAxisTickLabelFont(),
			barPlot.getValueAxisTickLabelColor(), barPlot.getValueAxisTickLabelMask(), barPlot.getValueAxisVerticalTickLabels(),
			barPlot.getOwnValueAxisLineColor(), true,
			(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMinValueExpression()),
			(Comparable<?>)evaluateExpression(barPlot.getRangeAxisMaxValueExpression()));

		BarRenderer categoryRenderer = (BarRenderer)categoryPlot.getRenderer();
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
			return null;

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
			label = "";

		Range range = convertRange(interval.getDataRange());

		Color color = interval.getBackgroundColor() != null ? interval.getBackgroundColor() : (Color)ChartThemesConstants.TRANSPARENT_PAINT;
		float[] components = color.getRGBColorComponents(null);

		float alpha = (float)(interval.getAlpha() == null ? JRMeterInterval.DEFAULT_TRANSPARENCY : interval.getAlpha());
		Color alphaColor = new Color(components[0], components[1], components[2], alpha);

		return new MeterInterval(label, range, alphaColor, null, alphaColor);
	}

	/**
	 * Build and configure a meter chart.
	 */
	protected JFreeChart createMeterChart() throws JRException 
	{
		// Start by creating the plot that will hold the meter
		MeterPlot chartPlot = new MeterPlot((ValueDataset)getDataset());
		JRMeterPlot jrPlot = (JRMeterPlot)getPlot();

		// Set the shape
		MeterShapeEnum shape = jrPlot.getShape() == null ? MeterShapeEnum.PIE : jrPlot.getShape();
		
		switch (shape)
		{
			case CHORD:
				chartPlot.setDialShape(DialShape.CHORD);
				break;
			case CIRCLE:
				chartPlot.setDialShape(DialShape.CIRCLE);
				break;
			case DIAL:
				return createDialChart();
			case PIE:
			default:
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
			chartPlot.setUnits(units);

		// Set the spacing between ticks.  I hate the name "tickSize" since to me it
		// implies I am changing the size of the tick, not the spacing between them.
		double tickInterval = jrPlot.getTickInterval() == null ? 10.0 : jrPlot.getTickInterval();
		chartPlot.setTickSize(tickInterval);

		// Set all the colors we support
		Color color = jrPlot.getMeterBackgroundColor();
		if (color != null)
			chartPlot.setDialBackgroundPaint(color);

		color = jrPlot.getNeedleColor();
		if (color != null)
			chartPlot.setNeedlePaint(color);

		JRFont tickLabelFont = jrPlot.getTickLabelFont();
		Integer defaultBaseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
		Font themeTickLabelFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_TICK_LABEL_FONT), tickLabelFont, defaultBaseFontSize);
		chartPlot.setTickLabelFont(themeTickLabelFont);
		
		// localizing the default format, can be overridden by display.getMask()
		chartPlot.setTickLabelFormat(NumberFormat.getInstance(getLocale()));
		
		JRValueDisplay display = jrPlot.getValueDisplay();
		JRFont displayFont = display.getFont();
		Font themeDisplayFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_DISPLAY_FONT), displayFont, defaultBaseFontSize);
		// Set how the value is displayed.
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
				chartPlot.setValueFont(themeDisplayFont);
			}

		}

		color = jrPlot.getTickColor();
		if (color != null)
			chartPlot.setTickPaint(color);

		// Now define all of the intervals, setting their range and color
		List<JRMeterInterval> intervals = jrPlot.getIntervals();
		if (intervals != null)
		{
			for (JRMeterInterval interval : intervals)
			{
				if (interval != null)
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
		configureChart(jfreeChart, getPlot());

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
		
		ChartUtil chartUtil = ChartUtil.getInstance(getChartContext().getJasperReportsContext());
		// setting localized range axis formatters
		chartPlot.getRangeAxis().setStandardTickUnits(chartUtil.createIntegerTickUnits(getLocale()));

		Range range = convertRange(jrPlot.getDataRange());

		// Set the boundary of the thermomoter
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
//				chartPlot.setValueFont(getFontUtil().getAwtFont(display.getFont()));
			}
		}

		// Set the location of where the value is displayed
		ValueLocationEnum valueLocation = jrPlot.getValueLocation();
		switch (valueLocation)
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
		configureChart(jfreeChart, getPlot());
		
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
		//double bound = Math.max(Math.abs(range.getUpperBound()), Math.abs(range.getLowerBound()));
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
		JRFont tickLabelFont = jrPlot.getTickLabelFont();
		Integer defaultBaseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
		Font themeTickLabelFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_TICK_LABEL_FONT), tickLabelFont, defaultBaseFontSize);
		scale.setTickLabelFont(themeTickLabelFont);
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
				colorStep = 255 / size;
			
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
			? getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_DIAL_VALUE_DISPLAY_VISIBLE) : "false";

		if (Boolean.parseBoolean(displayVisibility))
		{
			DialValueIndicator dvi = new DialValueIndicator(0);
			dvi.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
//			dvi.setFont(getFontUtil().getAwtFont(jrFont).deriveFont(10f).deriveFont(Font.BOLD));
			dvi.setOutlinePaint(ChartThemesConstants.TRANSPARENT_PAINT);
			dvi.setPaint(Color.WHITE);

			String pattern = display.getMask() != null ? display.getMask() : "#,##0.####";
			if (pattern != null)
				dvi.setNumberFormat( new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(getLocale())));
			dvi.setRadius(0.15);
			dvi.setValueAnchor(RectangleAnchor.CENTER);
			dvi.setTextAnchor(TextAnchor.CENTER);
			//dvi.setTemplateValue(Double.valueOf(getDialTickValue(dialPlot.getValue(0),dialUnitScale)));
			dialPlot.addLayer(dvi);
		}
		String label = getChart().hasProperties() ?
				getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_DIAL_LABEL) : null;
		
		if (label != null)
		{
			JRFont displayFont = jrPlot.getValueDisplay().getFont();
			Font themeDisplayFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_DISPLAY_FONT), displayFont, defaultBaseFontSize);
			String[] textLines = label.split("\\n");
			for (int i = 0; i < textLines.length; i++)
			{
				DialTextAnnotation dialAnnotation = new DialTextAnnotation(textLines[i]);
				dialAnnotation.setFont(themeDisplayFont);
				dialAnnotation.setPaint(jrPlot.getValueDisplay().getColor());
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
		configureChart(jfreeChart, getPlot());

		return jfreeChart;

	}

	/**
	 * Specifies the axis location.
	 * It has to be overridden for child themes with another default axis location
	 */
	protected AxisLocation getChartAxisLocation(JRChartAxis chartAxis)
	{
		if (chartAxis.getPosition() != null)
		{
			switch (chartAxis.getPosition())
			{
				case RIGHT_OR_BOTTOM :
					return AxisLocation.BOTTOM_OR_RIGHT;
				default:
					return AxisLocation.TOP_OR_LEFT;
			}
		}
		else
		{
			return (AxisLocation)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LOCATION);
		}
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

	protected void populateSeriesColors(Paint[] colors, Paint[] colorSequence)
	{
		if (colors != null)
		{
			int size = colorSequence != null ? colorSequence.length : 0;
			System.arraycopy(colorSequence, 0, colors, 0, colorSequence.length);
			System.arraycopy(DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE, 0, colors, size, DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length);
		}
	}

	protected void setChartBackground(JFreeChart jfreeChart)
	{
		Paint defaultBackgroundPaint = (Paint)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BACKGROUND_PAINT);
		Image defaultBackgroundImage = (Image)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BACKGROUND_IMAGE);
		Integer defaultBackgroundImageAlignment = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BACKGROUND_IMAGE_ALIGNMENT);
		Float defaultBackgroundImageAlpha = (Float)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BACKGROUND_IMAGE_ALPHA);

		if (getChart().getOwnMode() != null)
		{
			if (getChart().getOwnMode() == ModeEnum.OPAQUE)
			{
				if (getChart().getOwnBackcolor() == null && defaultBackgroundPaint != null)
				{
					jfreeChart.setBackgroundPaint(defaultBackgroundPaint);
				}
				else
				{
					jfreeChart.setBackgroundPaint(getChart().getBackcolor());
				}
				
				setChartBackgroundImage(jfreeChart, 
						defaultBackgroundImage,
						defaultBackgroundImageAlignment,
						defaultBackgroundImageAlpha);
			}
			else
			{
				jfreeChart.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
				setChartBackgroundImage(jfreeChart, 
						defaultBackgroundImage,
						defaultBackgroundImageAlignment,
						0f);
			}
		}
		else if (defaultBackgroundPaint != null)
		{
			jfreeChart.setBackgroundPaint(defaultBackgroundPaint);
		}
	}
	
	protected void setChartBackgroundImage(JFreeChart jfreeChart, 
			Image defaultBackgroundImage,
			Integer defaultBackgroundImageAlignment,
			Float defaultBackgroundImageAlpha)

	{
		
		if (defaultBackgroundImage != null)
		{
			jfreeChart.setBackgroundImage(defaultBackgroundImage);
			if (defaultBackgroundImageAlignment != null)
			{
				jfreeChart.setBackgroundImageAlignment(defaultBackgroundImageAlignment);
			}
			if (defaultBackgroundImageAlpha != null)
			{
				jfreeChart.setBackgroundImageAlpha(defaultBackgroundImageAlpha);
			}
		}
	}

	protected void setChartTitle(JFreeChart jfreeChart, Integer baseFontSize)
	{
		Boolean titleVisibility = (Boolean)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_VISIBLE);
		if (titleVisibility != null && titleVisibility)
		{
			TextTitle title = jfreeChart.getTitle();
			
			RectangleEdge titleEdge = null;
					
			if (title != null)
			{
				JRFont titleFont = getChart().getTitleFont();

				Font themeTitleFont = getFont((JRFont)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_FONT), titleFont, baseFontSize);
				title.setFont(themeTitleFont);
				
				HorizontalAlignment defaultTitleHAlignment = (HorizontalAlignment)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_HORIZONTAL_ALIGNMENT);
				if (defaultTitleHAlignment != null)
					title.setHorizontalAlignment(defaultTitleHAlignment);
				
				VerticalAlignment defaultTitleVAlignment = (VerticalAlignment)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_VERTICAL_ALIGNMENT);
				if (defaultTitleVAlignment != null)
					title.setVerticalAlignment(defaultTitleVAlignment);
				
				RectangleInsets defaultTitlePadding = (RectangleInsets)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_PADDING);
				RectangleInsets titlePadding = title.getPadding() != null ? title.getPadding() : defaultTitlePadding;
				if (titlePadding != null)
					title.setPadding(titlePadding);
				
				Color titleForecolor = getChart().getOwnTitleColor() != null ? 
						getChart().getOwnTitleColor() :
						(getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_FORECOLOR) != null ? 
								(Color)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_FORECOLOR) :
								getChart().getTitleColor());
				if (titleForecolor != null)
					title.setPaint(titleForecolor);
	
				Color titleBackcolor = (Color)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_BACKCOLOR);
				if (titleBackcolor != null)
					title.setBackgroundPaint(titleBackcolor);
				
				RectangleEdge defaultTitlePosition = (RectangleEdge)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.TITLE_POSITION);
				titleEdge = getEdge(getChart().getTitlePosition(), defaultTitlePosition);
				if (titleEdge != null)
					title.setPosition(titleEdge);
			}
		}
		else
		{
			TextTitle title = null;
			jfreeChart.setTitle(title);
		}
	}

	protected void setChartSubtitles(JFreeChart jfreeChart, Integer baseFontSize) throws JRException
	{			
		Boolean subtitleVisibility = (Boolean)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_VISIBLE);

		if (subtitleVisibility != null && subtitleVisibility)
		{
			String subtitleText = evaluateTextExpression(getChart().getSubtitleExpression());
			if (subtitleText != null)
			{
				TextTitle subtitle = new TextTitle(subtitleText);
				
				Font themeSubtitleFont = getFont((JRFont)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_FONT), getChart().getSubtitleFont(), baseFontSize);
				subtitle.setFont(themeSubtitleFont);
				
				HorizontalAlignment defaultSubtitleHAlignment = (HorizontalAlignment)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_HORIZONTAL_ALIGNMENT);
				if (defaultSubtitleHAlignment != null)
					subtitle.setHorizontalAlignment(defaultSubtitleHAlignment);

				VerticalAlignment defaultSubtitleVAlignment = (VerticalAlignment)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_VERTICAL_ALIGNMENT);
				if (defaultSubtitleVAlignment != null)
					subtitle.setVerticalAlignment(defaultSubtitleVAlignment);
				
				RectangleInsets defaultSubtitlePadding = (RectangleInsets)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_PADDING);
				RectangleInsets subtitlePadding = subtitle.getPadding() != null ? subtitle.getPadding() : defaultSubtitlePadding;
				if (subtitlePadding != null)
					subtitle.setPadding(subtitlePadding);

				Color subtitleForecolor = getChart().getOwnSubtitleColor() != null ? 
						getChart().getOwnSubtitleColor() :
						(getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_FORECOLOR) != null ? 
								(Color)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_FORECOLOR) :
								getChart().getSubtitleColor());
				if (subtitleForecolor != null)
					subtitle.setPaint(subtitleForecolor);
	
				Color subtitleBackcolor = getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_BACKCOLOR) != null ? 
						(Color)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_BACKCOLOR) :
						null;
				if (subtitleBackcolor != null)
					subtitle.setBackgroundPaint(subtitleBackcolor);
	
				RectangleEdge defaultSubtitlePosition = (RectangleEdge)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SUBTITLE_POSITION);
				//Subtitle has not its own position set, and by default this will be set the same as title position
				RectangleEdge subtitleEdge = null;
				if (defaultSubtitlePosition == null)
				{	
					subtitleEdge = jfreeChart.getTitle().getPosition();
				}
				else
				{
					subtitleEdge = defaultSubtitlePosition;
				}
				if (subtitleEdge != null)
					subtitle.setPosition(subtitleEdge);
				
				jfreeChart.addSubtitle(subtitle);
			}
		}
	}
	
	protected void setChartLegend(JFreeChart jfreeChart, Integer baseFontSize)
	{

		//The legend visibility is already taken into account in the jfreeChart object's constructor
		
		LegendTitle legend = jfreeChart.getLegend();
		if (legend != null)
		{
			Font themeLegendFont = getFont((JRFont)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_FONT), getChart().getLegendFont(), baseFontSize);
			legend.setItemFont(themeLegendFont);

			Color legendForecolor = getChart().getOwnLegendColor() != null ? 
					getChart().getOwnLegendColor() :
					(getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_FORECOLOR) != null ? 
							(Color)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_FORECOLOR) :
							getChart().getLegendColor());
			if (legendForecolor != null)
				legend.setItemPaint(legendForecolor);

			Color legendBackcolor = getChart().getOwnLegendBackgroundColor() != null ? 
					getChart().getOwnLegendBackgroundColor() :
					(getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_BACKCOLOR) != null ? 
							(Color)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_BACKCOLOR) :
							getChart().getLegendBackgroundColor());
			if (legendBackcolor != null)
				legend.setBackgroundPaint(legendBackcolor);
			
			BlockFrame frame = (BlockFrame)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_FRAME);
			if (frame != null)
				legend.setFrame(frame);
			
			HorizontalAlignment defaultLegendHAlignment = (HorizontalAlignment)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_HORIZONTAL_ALIGNMENT);
			if (defaultLegendHAlignment != null)
				legend.setHorizontalAlignment(defaultLegendHAlignment);
			
			VerticalAlignment defaultLegendVAlignment = (VerticalAlignment)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_VERTICAL_ALIGNMENT);
			if (defaultLegendVAlignment != null)
				legend.setVerticalAlignment(defaultLegendVAlignment);
			
			RectangleInsets defaultLegendPadding = (RectangleInsets)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_PADDING);
			RectangleInsets legendPadding = legend.getPadding() != null ? legend.getPadding() : defaultLegendPadding;
			if (legendPadding != null)
				legend.setPadding(legendPadding);

			RectangleEdge defaultLegendPosition = (RectangleEdge)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_POSITION);
			if (getEdge(getChart().getLegendPosition(), defaultLegendPosition) != null)
				legend.setPosition(getEdge(getChart().getLegendPosition(), defaultLegendPosition));
			
		}
	}
	
	protected void setChartBorder(JFreeChart jfreeChart)
	{
		JRLineBox lineBox = getChart().getLineBox();
		if (
			(lineBox.getLeftPen().getLineWidth() == null || lineBox.getLeftPen().getLineWidth() == 0)
			&& (lineBox.getBottomPen().getLineWidth() == null || lineBox.getBottomPen().getLineWidth() == 0)
			&& (lineBox.getRightPen().getLineWidth() == null || lineBox.getRightPen().getLineWidth() == 0)
			&& (lineBox.getTopPen().getLineWidth() == null || lineBox.getTopPen().getLineWidth() == 0)
			)
		{
			boolean isVisible = getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.CHART_BORDER_VISIBLE) == null ?
					false : 
					(Boolean)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.CHART_BORDER_VISIBLE);
			if (isVisible)
			{
				BasicStroke stroke = (BasicStroke)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.CHART_BORDER_STROKE);
				if (stroke != null)
					jfreeChart.setBorderStroke(stroke);
				Paint paint = (Paint)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.CHART_BORDER_PAINT);
				if (paint != null)
					jfreeChart.setBorderPaint(paint);
			}
				
			jfreeChart.setBorderVisible(isVisible);
		}
	}

	protected void setPlotBackground(Plot p, JRChartPlot jrPlot)
	{
		Paint defaultBackgroundPaint = (Paint)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_BACKGROUND_PAINT);
		Float defaultBackgroundAlpha = (Float)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_BACKGROUND_ALPHA);
		Float defaultForegroundAlpha = (Float)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_FOREGROUND_ALPHA);
		
		Image defaultBackgroundImage = (Image)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_BACKGROUND_IMAGE);
		Integer defaultBackgroundImageAlignment = (Integer)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_BACKGROUND_IMAGE_ALIGNMENT);
		Float defaultBackgroundImageAlpha = (Float)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_BACKGROUND_IMAGE_ALPHA);

		Paint backgroundPaint = jrPlot.getOwnBackcolor() != null ? jrPlot.getOwnBackcolor() : defaultBackgroundPaint;
		if (backgroundPaint != null)
		{
			p.setBackgroundPaint(backgroundPaint);
		}
		
		Float backgroundAlpha = jrPlot.getBackgroundAlpha() != null ? 
				jrPlot.getBackgroundAlpha() : 
				defaultBackgroundAlpha;
		if (backgroundAlpha != null)
			p.setBackgroundAlpha(backgroundAlpha);
		
		Float foregroundAlpha = jrPlot.getForegroundAlpha() != null ? 
				jrPlot.getForegroundAlpha() : 
				defaultForegroundAlpha;
		if (foregroundAlpha != null)
			p.setForegroundAlpha(foregroundAlpha);
		
		if (defaultBackgroundImage != null)
		{
			p.setBackgroundImage(defaultBackgroundImage);
			if (defaultBackgroundImageAlignment != null)
			{
				p.setBackgroundImageAlignment(defaultBackgroundImageAlignment);
			}
			if (defaultBackgroundImageAlpha != null)
			{
				p.setBackgroundImageAlpha(defaultBackgroundImageAlpha);
			}
		}
		
	}
	
	protected void handleCategoryPlotSettings(CategoryPlot p, JRChartPlot jrPlot)
	{
		Double defaultPlotLabelRotation = (Double)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_LABEL_ROTATION);
		PlotOrientation defaultPlotOrientation = (PlotOrientation)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_ORIENTATION);
		// Handle rotation of the category labels.
		CategoryAxis axis = p.getDomainAxis();
		boolean hasRotation = jrPlot.getLabelRotation() != null || defaultPlotLabelRotation != null;
		if (hasRotation)
		{
			double labelRotation = jrPlot.getLabelRotation() != null ? 
					jrPlot.getLabelRotation() :
					defaultPlotLabelRotation;
			
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
		
		if (defaultPlotOrientation != null)
		{
			p.setOrientation(defaultPlotOrientation);
		}
	}

	protected void setPlotDrawingDefaults(Plot p, JRChartPlot jrPlot)
	{
		@SuppressWarnings("unchecked")
		List<Paint> defaultSeriesColors = (List<Paint>)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SERIES_COLORS);
		Paint[] defaultPlotOutlinePaintSequence = 
			getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_OUTLINE_PAINT_SEQUENCE) != null ?
			(Paint[])getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_OUTLINE_PAINT_SEQUENCE) :
			DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE;
			
		Stroke[] defaultPlotStrokeSequence = 
			getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_STROKE_SEQUENCE) != null ?
			(Stroke[])getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_STROKE_SEQUENCE) :
			DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE;
			
		Stroke[] defaultPlotOutlineStrokeSequence = 
			getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_OUTLINE_STROKE_SEQUENCE) != null ?
			(Stroke[])getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_OUTLINE_STROKE_SEQUENCE) :
			DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE;
			
		Shape[] defaultPlotShapeSequence = 
			getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_SHAPE_SEQUENCE) != null ?
			(Shape[])getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_SHAPE_SEQUENCE) :
			DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE;
		// Set color series
		Paint[] colors = null;
		SortedSet<JRSeriesColor> seriesColors = jrPlot.getSeriesColors();
		Paint[] colorSequence = null;
		if (seriesColors != null && seriesColors.size() > 0)
		{
			int seriesColorsSize = seriesColors.size();
			
			colors = new Paint[DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length + seriesColorsSize];

			JRSeriesColor[] jrColorSequence = new JRSeriesColor[seriesColorsSize];
			seriesColors.toArray(jrColorSequence);
			colorSequence = new Paint[seriesColorsSize];
			
			for (int i = 0; i < seriesColorsSize; i++)
			{
				colorSequence[i] = jrColorSequence[i].getColor();
			}
			populateSeriesColors(colors, colorSequence);
		}
		else if (defaultSeriesColors != null && defaultSeriesColors.size() > 0)
		{
			colors = new Paint[DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE.length + defaultSeriesColors.size()];
			colorSequence = new Paint[defaultSeriesColors.size()];
			defaultSeriesColors.toArray(colorSequence);
			populateSeriesColors(colors, colorSequence);
		}
		else
		{
			colors = DefaultDrawingSupplier.DEFAULT_PAINT_SEQUENCE;
		}
		
		p.setDrawingSupplier(new DefaultDrawingSupplier(
				colors,
				defaultPlotOutlinePaintSequence,
				defaultPlotStrokeSequence,
				defaultPlotOutlineStrokeSequence,
				defaultPlotShapeSequence
				)
			);
		
	}
	
	protected void setAxisLine(Axis axis, Paint lineColor)
	{
		Boolean defaultAxisLineVisible = (Boolean)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LINE_VISIBLE);
		if (defaultAxisLineVisible != null && defaultAxisLineVisible)
		{
			Paint linePaint = lineColor != null ?
					lineColor :
					(Paint)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LINE_PAINT);
			
			if (linePaint != null)
			{
				axis.setAxisLinePaint(linePaint);
			}
			Stroke defaultAxisLineStroke = (Stroke)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LINE_STROKE);
			if (defaultAxisLineStroke != null)
				axis.setAxisLineStroke(defaultAxisLineStroke);
		}
	}
	
	protected void setAxisLabel(Axis axis, JRFont labelFont, Paint labelColor, Integer baseFontSize)
	{
		Boolean defaultAxisLabelVisible = (Boolean)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LABEL_VISIBLE);
		if (defaultAxisLabelVisible != null && defaultAxisLabelVisible)
		{
			if (axis.getLabel() == null)
				axis.setLabel((String)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LABEL));

			Double defaultLabelAngle = (Double)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LABEL_ANGLE);
			if (defaultLabelAngle != null)
				axis.setLabelAngle(defaultLabelAngle);
			Font themeLabelFont = getFont((JRFont)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LABEL_FONT), labelFont, baseFontSize);
			axis.setLabelFont(themeLabelFont);
			
			RectangleInsets defaultLabelInsets = (RectangleInsets)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LABEL_INSETS);
			if (defaultLabelInsets != null)
			{
				axis.setLabelInsets(defaultLabelInsets);
			}
			Paint labelPaint = labelColor != null ? 
					labelColor :
					(Paint)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_LABEL_PAINT);	
			if (labelPaint != null)
			{
				axis.setLabelPaint(labelPaint);
			}
		}
	}

	protected void setAxisTickLabels(Axis axis, JRFont tickLabelFont, Paint tickLabelColor, String tickLabelMask, Integer baseFontSize)
	{
		Boolean defaultAxisTickLabelsVisible = (Boolean)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_LABELS_VISIBLE);
		if (defaultAxisTickLabelsVisible != null && defaultAxisTickLabelsVisible)
		{
			Font themeTickLabelFont = getFont((JRFont)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_LABEL_FONT), tickLabelFont, baseFontSize);
			axis.setTickLabelFont(themeTickLabelFont);
			
			RectangleInsets defaultTickLabelInsets = (RectangleInsets)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_LABEL_INSETS);
			if (defaultTickLabelInsets != null)
			{
				axis.setTickLabelInsets(defaultTickLabelInsets);
			}
			Paint tickLabelPaint = tickLabelColor != null ? 
					tickLabelColor :
					(Paint)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_LABEL_PAINT);	
			if (tickLabelPaint != null)
			{
				axis.setTickLabelPaint(tickLabelPaint);
			}
			
			TimeZone timeZone = getChartContext().getTimeZone();
			if (axis instanceof DateAxis && timeZone != null)
			{
				// used when no mask is set
				((DateAxis) axis).setTimeZone(timeZone);
			}
			
			if (tickLabelMask != null)
			{
				if (axis instanceof NumberAxis)
				{
					NumberFormat fmt = NumberFormat.getInstance(getLocale());
					if (fmt instanceof DecimalFormat)
						((DecimalFormat) fmt).applyPattern(tickLabelMask);
					((NumberAxis)axis).setNumberFormatOverride(fmt);
				}
				else if (axis instanceof DateAxis)
				{
					DateFormat fmt;
					if (tickLabelMask.equals("SHORT") || tickLabelMask.equals("DateFormat.SHORT"))
						fmt = DateFormat.getDateInstance(DateFormat.SHORT, getLocale());
					else if (tickLabelMask.equals("MEDIUM") || tickLabelMask.equals("DateFormat.MEDIUM"))
						fmt = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
					else if (tickLabelMask.equals("LONG") || tickLabelMask.equals("DateFormat.LONG"))
						fmt = DateFormat.getDateInstance(DateFormat.LONG, getLocale());
					else if (tickLabelMask.equals("FULL") || tickLabelMask.equals("DateFormat.FULL"))
						fmt = DateFormat.getDateInstance(DateFormat.FULL, getLocale());
					else
						fmt = new SimpleDateFormat(tickLabelMask, getLocale());
					
					// FIXME fmt cannot be null
					if (fmt != null)
					{
						if (timeZone != null)
						{
							fmt.setTimeZone(timeZone);
						}
						
						((DateAxis)axis).setDateFormatOverride(fmt);
					}
					else
						((DateAxis)axis).setDateFormatOverride(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, getLocale()));
				}
				// ignore mask for other axis types.
			}
		}
	}
	
	protected void setAxisTickMarks(Axis axis, Paint lineColor)
	{
		Boolean defaultAxisTickMarksVisible = (Boolean)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_MARKS_VISIBLE);
		if (defaultAxisTickMarksVisible != null && defaultAxisTickMarksVisible)
		{
			Float defaultAxisTickMarksInsideLength = (Float)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_MARKS_INSIDE_LENGTH);
			if (defaultAxisTickMarksInsideLength != null)
				axis.setTickMarkInsideLength(defaultAxisTickMarksInsideLength);
			
			Float defaultAxisTickMarksOutsideLength = (Float)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_MARKS_OUTSIDE_LENGTH);
			if (defaultAxisTickMarksOutsideLength != null)
				axis.setTickMarkInsideLength(defaultAxisTickMarksOutsideLength);
			
			Paint tickMarkPaint = getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_MARKS_PAINT) != null ?
					(Paint)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_MARKS_PAINT) :
					lineColor;
			
			if (tickMarkPaint != null)
			{
				axis.setTickMarkPaint(tickMarkPaint);
			}
			Stroke defaultTickMarkStroke = (Stroke)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.AXIS_TICK_MARKS_STROKE);
			if (defaultTickMarkStroke != null)
				axis.setTickMarkStroke(defaultTickMarkStroke);
		}
	}
	
	protected void setAxisBounds(Axis axis, boolean isRangeAxis, String timePeriodUnit, Comparable<?> minValue, Comparable<?> maxValue) throws JRException
	{
		if (axis instanceof ValueAxis)
		{
			if (axis instanceof DateAxis)
			{
				if (minValue != null)
				{
					((DateAxis)axis).setMinimumDate((Date)minValue);
				}
				if (maxValue != null)
				{
					((DateAxis)axis).setMaximumDate((Date)maxValue);
				}
			}
			else
			{
				if (minValue != null)
				{
					((ValueAxis)axis).setLowerBound(((Number)minValue).doubleValue());
				}
				if (maxValue != null)
				{
					((ValueAxis)axis).setUpperBound(((Number)maxValue).doubleValue());
				}
			}

			calculateTickUnits(axis, isRangeAxis, timePeriodUnit);
		}
	}

	/**
	 * For a given axis, adjust the tick unit size, in order to 
	 * have a customizable number of ticks on that axis
	 */
	protected void calculateTickUnits(Axis axis, boolean isRangeAxis, String timePeriodUnit)
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
		else
		{
			if (isRangeAxis)
			{
				tickCount = (Integer)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.RANGE_AXIS_TICK_COUNT);
				tickInterval = (Number)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.RANGE_AXIS_TICK_INTERVAL);
				if (getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.RANGE_AXIS_INTEGER_UNIT) != null)
				{
					axisIntegerUnit = (Boolean)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.RANGE_AXIS_INTEGER_UNIT);
				}
			}
			else
			{
				tickCount = (Integer)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.DOMAIN_AXIS_TICK_COUNT);
				tickInterval = (Number)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.DOMAIN_AXIS_TICK_INTERVAL);
				if (getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.DOMAIN_AXIS_INTEGER_UNIT) != null)
				{
					axisIntegerUnit = (Boolean)getDefaultValue(defaultAxisPropertiesMap, ChartThemesConstants.DOMAIN_AXIS_INTEGER_UNIT);
				}
			}
		}
		
//		if (axis instanceof NumberAxis)
//		{
//			NumberAxis numberAxis = (NumberAxis)axis;
//			int axisRange = (int)numberAxis.getRange().getLength();
//			if (numberAxis.getNumberFormatOverride() != null)
//			{
//				if (tickInterval != null && tickInterval.length() > 0)
//					numberAxis.setTickUnit(new NumberTickUnit(Double.valueOf(tickInterval), numberAxis.getNumberFormatOverride()));
//				else
//					numberAxis.setTickUnit(new NumberTickUnit( axisRange/tickCount, numberAxis.getNumberFormatOverride()));
//			}
//			else
//			{
//				if (tickInterval != null && tickInterval.length() > 0)
//					numberAxis.setTickUnit(new NumberTickUnit(Double.valueOf(tickInterval)));
//				else
//					numberAxis.setTickUnit(new NumberTickUnit(axisRange/tickCount));
//			}
//		}
		if (axis instanceof NumberAxis)
		{
			NumberAxis numberAxis = (NumberAxis)axis;
			int axisRange = (int)numberAxis.getRange().getLength();
			
			if (axisIntegerUnit)
			{
				ChartUtil chartUtil = ChartUtil.getInstance(getChartContext().getJasperReportsContext());
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
					int newTickUnitSize = axisRange / tickCount;
					if (newTickUnitSize > numberAxis.getTickUnit().getSize())
					{
						int tickUnitSize = newTickUnitSize;
			
						//preferably multiple of 5 values should be used as tick units lengths:
						int i = 1;
						while (tickUnitSize > 9)
						{
							tickUnitSize /= 10;
							i *= 10;
						}
						tickUnitSize *= i;
						newTickUnitSize = tickUnitSize + i/2;
			
						if (newTickUnitSize > 0 && axisRange / newTickUnitSize > tickCount)
						{
							newTickUnitSize += i / 2;
						}
						if (numberAxis.getNumberFormatOverride() != null)
						{
							numberAxis.setTickUnit(new NumberTickUnit(newTickUnitSize, numberAxis.getNumberFormatOverride()));
						}
						else
						{
							numberAxis.setTickUnit(new NumberTickUnit(newTickUnitSize, NumberFormat.getNumberInstance(getLocale())));
						}
					}
				}
				else
				{
					ChartUtil chartUtil = ChartUtil.getInstance(getChartContext().getJasperReportsContext());
					numberAxis.setStandardTickUnits(chartUtil.createStandardTickUnits(getLocale()));
					chartUtil.setAutoTickUnit(numberAxis);
				}
			}
		}
//		else if (axis instanceof DateAxis)
//		{
//			DateAxis dateAxis = (DateAxis)axis;
//			int axisRange = (int)dateAxis.getRange().getLength();
//			int timeUnit = getTimePeriodUnit(timePeriodUnit);
//			
//			if (dateAxis.getDateFormatOverride() != null)
//			{
//				if (tickInterval != null && tickInterval.length() > 0)
//					dateAxis.setTickUnit(new DateTickUnit(timeUnit, Integer.valueOf(tickInterval), dateAxis.getDateFormatOverride()));
//				else
//					dateAxis.setTickUnit(new DateTickUnit(timeUnit, axisRange/tickCount, dateAxis.getDateFormatOverride()));
//			}
//			else
//			{
//				if (tickInterval != null && tickInterval.length() > 0)
//					dateAxis.setTickUnit(new DateTickUnit(timeUnit, Integer.valueOf(tickInterval)));
//				else
//					dateAxis.setTickUnit(new DateTickUnit(timeUnit, axisRange/tickCount));
//			}
//		}
	}
	
	/**
	 * Specifies whether a chart legend should be visible or no by default.
	 */
	protected boolean isShowLegend()
	{
		Boolean legendVisibility = 
			getChart().getShowLegend() == null 
				?(Boolean)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.LEGEND_VISIBLE)
				: getChart().getShowLegend();

		return legendVisibility == null ? false : legendVisibility;
	}

	/**
	 * Returns the specific org.jfree.chart.axis.DateTickUnit time unit constant
	 * related to the String value passed as argument
	 * 
	 * @param timePeriodUnit - a String represented by one of the following
	 * accepted values: ["Year", "Month", "Day", "Hour", "Minute", "Second", "Millisecond"]
	 * @return the specific org.jfree.chart.axis.DateTickUnit time unit constant
	 */
	protected DateTickUnitType getTimePeriodUnit(String timePeriodUnit)
	{
		if (timePeriodUnit == null)
			return DateTickUnitType.DAY;
		return timePeriodUnit.equals("Year")
			? DateTickUnitType.YEAR
			: timePeriodUnit.equals("Month")
			? DateTickUnitType.MONTH
			: timePeriodUnit.equals("Hour")
			? DateTickUnitType.HOUR
			: timePeriodUnit.equals("Minute")
			? DateTickUnitType.MINUTE
			: timePeriodUnit.equals("Second")
			? DateTickUnitType.SECOND
			: timePeriodUnit.equals("Millisecond")
			? DateTickUnitType.MILLISECOND
			: DateTickUnitType.DAY;
	}

	protected Object getDefaultValue(Map<String, ?> map, Object key)
	{
		return map == null ? null : map.get(key);
	}


	protected Font getFont(JRFont themeFont, JRFont ownFont, Integer baseFontSize)
	{
		JRBaseFont font = new JRBaseFont();
		if (themeFont != null && themeFont.getFontSize() <= 0 && baseFontSize != null && baseFontSize > 0)
		{
			themeFont.setFontSize((float)baseFontSize);
		}
		FontUtil.copyNonNullOwnProperties(themeFont, font);
		FontUtil.copyNonNullOwnProperties(ownFont, font);
		font = new JRBaseFont(getChart(), font);
		return getFontUtil().getAwtFont(font, getLocale());
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
	 * @return the defaultChartPropertiesMap
	 */
	public Map<String, ?> getDefaultChartPropertiesMap()
	{
		return defaultChartPropertiesMap;
	}


	/**
	 * @param defaultChartPropertiesMap the defaultChartPropertiesMap to set
	 */
	public void setDefaultChartPropertiesMap(Map<String, ?> defaultChartPropertiesMap)
	{
		this.defaultChartPropertiesMap = defaultChartPropertiesMap;
	}


	/**
	 * @return the defaultPlotPropertiesMap
	 */
	public Map<String, ?> getDefaultPlotPropertiesMap()
	{
		return defaultPlotPropertiesMap;
	}


	/**
	 * @param defaultPlotPropertiesMap the defaultPlotPropertiesMap to set
	 */
	public void setDefaultPlotPropertiesMap(Map<String, ?> defaultPlotPropertiesMap)
	{
		this.defaultPlotPropertiesMap = defaultPlotPropertiesMap;
	}


	/**
	 * @return the defaultAxisPropertiesMap
	 */
	public Map<String, ?> getDefaultAxisPropertiesMap()
	{
		return defaultAxisPropertiesMap;
	}


	/**
	 * @param defaultAxisPropertiesMap the defaultAxisPropertiesMap to set
	 */
	public void setDefaultAxisPropertiesMap(Map<String, ?> defaultAxisPropertiesMap)
	{
		this.defaultAxisPropertiesMap = defaultAxisPropertiesMap;
	}


	/**
	 * @return the defaultChartTypePropertiesMap
	 */
	public Map<String, ?> getDefaultChartTypePropertiesMap()
	{
		return defaultChartTypePropertiesMap;
	}


	/**
	 * @param defaultChartTypePropertiesMap the defaultChartTypePropertiesMap to set
	 */
	public void setDefaultChartTypePropertiesMap(Map<String, ?> defaultChartTypePropertiesMap)
	{
		this.defaultChartTypePropertiesMap = defaultChartTypePropertiesMap;
	}
		
}
