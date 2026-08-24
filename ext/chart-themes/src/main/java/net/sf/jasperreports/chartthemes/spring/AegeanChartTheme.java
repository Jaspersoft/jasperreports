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
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DialShape;
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
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;

import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRPiePlot;
import net.sf.jasperreports.charts.JRThermometerPlot;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.charts.fill.DefaultChartTheme;
import net.sf.jasperreports.charts.type.MeterShapeEnum;
import net.sf.jasperreports.charts.type.ValueLocationEnum;
import net.sf.jasperreports.charts.util.ChartUtil;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class AegeanChartTheme extends GenericChartTheme
{
	public static final Color THERMOMETER_COLOR = Color.BLACK;
//	public static final Color MARKER_COLOR = new Color(210,210,210);

	/**
	 *
	 */
	public AegeanChartTheme()
	{
	}

	@Override
	protected void configureChart(JFreeChart jfreeChart, JRChartPlot jrPlot) throws JRException
	{

		super.configureChart(jfreeChart, jrPlot);
		TextTitle title = jfreeChart.getTitle();

		if (title != null)
		{
			
			RectangleInsets padding = title.getPadding();
			double bottomPadding = Math.max(padding.getBottom(), 15d);
			title.setPadding(padding.getTop(), padding.getLeft(), bottomPadding, padding.getRight());
		}
	}

	@Override
	protected void configurePlot(Plot plot, JRChartPlot jrPlot)
	{

		super.configurePlot(plot, jrPlot);

		if (plot instanceof CategoryPlot)
		{
			CategoryPlot categoryPlot = (CategoryPlot)plot;
			CategoryItemRenderer categoryRenderer = categoryPlot.getRenderer();
			CategoryDataset categoryDataset = categoryPlot.getDataset();
			if (categoryDataset != null)
			{
				for (int i = 0; i < categoryDataset.getRowCount(); i++)
				{
					categoryRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
				}
			}
			categoryPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_217);
			categoryPlot.setRangeGridlineStroke(new BasicStroke(0.5f));
			categoryPlot.setDomainGridlinesVisible(false);
			categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		}
		else if (plot instanceof XYPlot)
		{
			XYPlot xyPlot = (XYPlot)plot;
			XYItemRenderer xyItemRenderer = xyPlot.getRenderer();
			XYDataset xyDataset = xyPlot.getDataset();
			if (xyDataset != null)
			{
				for (int i = 0; i < xyDataset.getSeriesCount(); i++)
				{
					xyItemRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
				}
			}
			xyPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_217);
			xyPlot.setRangeGridlineStroke(new BasicStroke(0.5f));
			xyPlot.setDomainGridlinesVisible(false);
			xyPlot.setRangeZeroBaselineVisible(true);
		}
	}

	@Override
	protected JFreeChart createScatterChart() throws JRException
	{
		JFreeChart jfreeChart = super.createScatterChart();
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		xyPlot.setDomainGridlinesVisible(false);
		XYLineAndShapeRenderer plotRenderer = (XYLineAndShapeRenderer) ((XYPlot)jfreeChart.getPlot()).getRenderer();
		plotRenderer.setDefaultShapesFilled(false);
		plotRenderer.setDefaultStroke(new BasicStroke(1f));
		return jfreeChart;
	}

	@Override
	protected JFreeChart createPieChart() throws JRException
	{
		JFreeChart jfreeChart = super.createPieChart();

		PiePlot piePlot = (PiePlot)jfreeChart.getPlot();
		JRPiePlot jrPiePlot = (JRPiePlot)getPlot();
		boolean isShowLabels = jrPiePlot.getShowLabels() == null ? true : jrPiePlot.getShowLabels();

		if (isShowLabels && piePlot.getLabelGenerator() != null)
		{
			piePlot.setLabelBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
			piePlot.setLabelShadowPaint(ChartThemesConstants.TRANSPARENT_PAINT);
			piePlot.setLabelOutlinePaint(ChartThemesConstants.TRANSPARENT_PAINT);
		}
		piePlot.setShadowXOffset(0);
		piePlot.setShadowYOffset(0);
		PieDataset pieDataset = piePlot.getDataset();
		if (pieDataset != null)
		{
			for (int i = 0; i < pieDataset.getItemCount(); i++)
			{
				piePlot.setSectionOutlinePaint(pieDataset.getKey(i), ChartThemesConstants.TRANSPARENT_PAINT);
				
				//makes pie colors darker
				//piePlot.setSectionPaint(pieDataset.getKey(i), GRADIENT_PAINTS[i]);
			}
		}
		piePlot.setCircular(true);
		return jfreeChart;
	}


	@Override
	protected JFreeChart createPie3DChart() throws JRException
	{
		return createPieChart();
	}

	@Override
	protected JFreeChart createBar3DChart() throws JRException
	{
		return createBarChart();
	}


	@Override
	protected JFreeChart createStackedBar3DChart() throws JRException
	{
		return createStackedBarChart();
	}


	@Override
	protected JFreeChart createBubbleChart() throws JRException
	{
		JFreeChart jfreeChart = super.createBubbleChart();

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		XYBubbleRenderer bubbleRenderer = (XYBubbleRenderer)xyPlot.getRenderer();
		XYDataset xyDataset = xyPlot.getDataset();
		if (xyDataset != null)
		{
			for (int i = 0; i < xyDataset.getSeriesCount(); i++)
			{
				bubbleRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
			}
		}
		return jfreeChart;
	}

	@Override
	protected JFreeChart createGanttChart() throws JRException
	{

		JFreeChart jfreeChart = super.createGanttChart();
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		categoryPlot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
		categoryPlot.setDomainGridlinesVisible(true);
		categoryPlot.setDomainGridlinePosition(CategoryAnchor.END);
		categoryPlot.setDomainGridlineStroke(new BasicStroke(
				0.5f,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER,
				50,
				new float[] {1},
				0
				)
		);

		categoryPlot.setDomainGridlinePaint(ChartThemesConstants.GRAY_PAINT_217);

		categoryPlot.setRangeGridlinesVisible(true);
		categoryPlot.setRangeGridlineStroke(new BasicStroke(
				0.5f,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER,
				50,
				new float[] {1},
				0
				)
		);

		categoryPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_217);
//		JRBarPlot barPlot = (BarPlot)categoryPlot;
//		categoryPlot.getDomainAxis().setTickLabelsVisible(
//				categoryPlot.getShowTickLabels() == null ? true : barPlot.getShowTickLabels().
//				true
//				);
		CategoryItemRenderer categoryRenderer = categoryPlot.getRenderer();
		categoryRenderer.setDefaultItemLabelsVisible(true);
		BarRenderer barRenderer = (BarRenderer)categoryRenderer;
		@SuppressWarnings("unchecked")
		List<Paint> seriesPaints = (List<Paint>)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SERIES_COLORS);
		barRenderer.setSeriesPaint(0, seriesPaints.get(3));
		barRenderer.setSeriesPaint(1, seriesPaints.get(0));
		CategoryDataset categoryDataset = categoryPlot.getDataset();
		if (categoryDataset != null)
		{
			for (int i = 0; i < categoryDataset.getRowCount(); i++)
			{
				barRenderer.setSeriesItemLabelFont(i, categoryPlot.getDomainAxis().getTickLabelFont());
				barRenderer.setSeriesItemLabelsVisible(i, true);
	//			barRenderer.setSeriesPaint(i, GRADIENT_PAINTS[i]);
	//			CategoryMarker categoryMarker = new CategoryMarker(categoryDataset.getColumnKey(i),MARKER_COLOR, new BasicStroke(1f));
	//			categoryMarker.setAlpha(0.5f);
	//			categoryPlot.addDomainMarker(categoryMarker, Layer.BACKGROUND);
			}
		}
		categoryPlot.setOutlinePaint(Color.DARK_GRAY);
		categoryPlot.setOutlineStroke(new BasicStroke(1.5f));
		categoryPlot.setOutlineVisible(true);
		return jfreeChart;
	}

	@Override
	protected JFreeChart createMeterChart() throws JRException
	{
		// Start by creating the plot that will hold the meter
		MeterPlot chartPlot = new MeterPlot((ValueDataset)getDataset());
		JRMeterPlot jrPlot = (JRMeterPlot)getPlot();

		// Set the shape
		MeterShapeEnum shape = jrPlot.getShape() == null ? MeterShapeEnum.DIAL : jrPlot.getShape();
		
		switch (shape)
		{
			case CHORD:
				chartPlot.setDialShape(DialShape.CHORD);
				break;
			case PIE:
				chartPlot.setDialShape(DialShape.PIE);
				break;
			case CIRCLE:
				chartPlot.setDialShape(DialShape.CIRCLE);
				break;
			case DIAL:
			default:
				return createDialChart();
		}

		chartPlot.setDialOutlinePaint(Color.BLACK);
		int meterAngle = jrPlot.getMeterAngle() == null ? 180 : jrPlot.getMeterAngle();
		// Set the size of the meter
		chartPlot.setMeterAngle(meterAngle);

		// Set the spacing between ticks.  I hate the name "tickSize" since to me it
		// implies I am changing the size of the tick, not the spacing between them.
		double tickInterval = jrPlot.getTickInterval() == null ? 10.0 : jrPlot.getTickInterval();
		chartPlot.setTickSize(tickInterval);
		
		JRFont tickLabelFont = jrPlot.getTickLabelFont();
		Integer defaultBaseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
		Font themeTickLabelFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_TICK_LABEL_FONT), tickLabelFont, defaultBaseFontSize);
		chartPlot.setTickLabelFont(themeTickLabelFont);
		
		// localizing the default format, can be overridden by display.getMask()
		chartPlot.setTickLabelFormat(NumberFormat.getInstance(getLocale()));
		
		Color tickColor = jrPlot.getTickColor() == null ? Color.BLACK : jrPlot.getTickColor();
		chartPlot.setTickPaint(tickColor);
		int dialUnitScale = 1;
		Range range = convertRange(jrPlot.getDataRange());
		if (range != null)
		{
			// Set the meter's range
			chartPlot.setRange(range);
			double bound = Math.max(Math.abs(range.getUpperBound()), Math.abs(range.getLowerBound()));
			dialUnitScale = ChartThemesUtilities.getScale(bound);
			if ((range.getLowerBound() == (int)range.getLowerBound() &&
					range.getUpperBound() == (int)range.getUpperBound() &&
					tickInterval == (int)tickInterval) ||
					dialUnitScale > 1
					)
			{
				chartPlot.setTickLabelFormat(new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(getLocale())));
			}
			else if (dialUnitScale == 1)
			{
				chartPlot.setTickLabelFormat(new DecimalFormat("#,##0.0", DecimalFormatSymbols.getInstance(getLocale())));
			}
			else if (dialUnitScale <= 0)
			{
				chartPlot.setTickLabelFormat(new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(getLocale())));
			}
		}
		chartPlot.setTickLabelsVisible(true);

		// Set all the colors we support
		Paint backgroundPaint = jrPlot.getOwnBackcolor() == null ? ChartThemesConstants.TRANSPARENT_PAINT : jrPlot.getOwnBackcolor();
		chartPlot.setBackgroundPaint(backgroundPaint);

		GradientPaint gp =
			new GradientPaint(
				new Point(), Color.LIGHT_GRAY,
				new Point(), Color.BLACK,
				false
				);
		
		if (jrPlot.getMeterBackgroundColor() != null)
		{
			chartPlot.setDialBackgroundPaint(jrPlot.getMeterBackgroundColor());
		}
		else
		{
			chartPlot.setDialBackgroundPaint(gp);
		}
		//chartPlot.setForegroundAlpha(1f);
		Paint needlePaint = jrPlot.getNeedleColor() == null ? new Color(191, 48, 0) : jrPlot.getNeedleColor();
		chartPlot.setNeedlePaint(needlePaint);

		JRValueDisplay display = jrPlot.getValueDisplay();
		if (display != null)
		{
			Color valueColor = display.getColor() == null ? Color.BLACK : display.getColor();
			chartPlot.setValuePaint(valueColor);
			String pattern = display.getMask() != null ? display.getMask() : "#,##0.####";
			if (pattern != null)
				chartPlot.setTickLabelFormat( new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(getLocale())));
			JRFont displayFont = display.getFont();
			Font themeDisplayFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_DISPLAY_FONT), displayFont, defaultBaseFontSize);
	
			if (themeDisplayFont != null)
			{
				chartPlot.setValueFont(themeDisplayFont);
			}
		}
		String label = getChart().hasProperties() ?
				getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_DIAL_LABEL) : null;
		
		if (label != null)
		{ 
			if (dialUnitScale < 0)
				label = new MessageFormat(label).format(new Object[]{String.valueOf(Math.pow(10, dialUnitScale))});
			else if (dialUnitScale < 3)
				label = new MessageFormat(label).format(new Object[]{"1"});
			else
				label = new MessageFormat(label).format(new Object[]{String.valueOf((int)Math.pow(10, dialUnitScale-2))});
		
		}
		
		// Set the units - this is just a string that will be shown next to the
		// value
		String units = jrPlot.getUnits() == null ? label : jrPlot.getUnits();
		if (units != null && units.length() > 0)
			chartPlot.setUnits(units);

		chartPlot.setTickPaint(Color.BLACK);

		// Now define all of the intervals, setting their range and color
		List<JRMeterInterval> intervals = jrPlot.getIntervals();
		if (intervals != null  && intervals.size() > 0)
		{
			int size = Math.min(3, intervals.size());
			
			int colorStep = 0;
			if (size > 3)
				colorStep = 255 / (size - 3);
			
			for (int i = 0; i < size; i++)
			{
				JRMeterInterval interval = intervals.get(i);
				Color color = i < 3 
					? (Color)ChartThemesConstants.AEGEAN_INTERVAL_COLORS.get(i)
					: new Color(255 - colorStep * (i - 3), 0 + colorStep * (i - 3), 0);
				
				interval.setBackgroundColor(color);
				interval.setAlpha(1.0d);
				chartPlot.addInterval(convertInterval(interval));
			}
		}

		// Actually create the chart around the plot
		JFreeChart jfreeChart =
			new JFreeChart(
				evaluateTextExpression(getChart().getTitleExpression()),
				null,
				chartPlot,
				getChart().getShowLegend() == null ? false : getChart().getShowLegend()
				);

		// Set all the generic options
		configureChart(jfreeChart, getPlot());

		return jfreeChart;
		
	}

	@Override
	protected JFreeChart createThermometerChart() throws JRException
	{
		JRThermometerPlot jrPlot = (JRThermometerPlot)getPlot();

		// Create the plot that will hold the thermometer.
		ThermometerPlot chartPlot = new ThermometerPlot((ValueDataset)getDataset());
		
		ChartUtil chartUtil = ChartUtil.getInstance(getChartContext().getJasperReportsContext());
		// setting localized range axis formatters
		chartPlot.getRangeAxis().setStandardTickUnits(chartUtil.createIntegerTickUnits(getLocale()));

		// Build a chart around this plot
		JFreeChart jfreeChart = 
				new JFreeChart(
					evaluateTextExpression(getChart().getTitleExpression()),
					null, 
					chartPlot, 
					getChart().getShowLegend() == null ? false : getChart().getShowLegend()
					);

		// Set the generic options
		configureChart(jfreeChart, getPlot());
		jfreeChart.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		jfreeChart.setBorderVisible(false);

		Range range = convertRange(jrPlot.getDataRange());

		if (range != null)
		{
			// Set the boundary of the thermomoter
			chartPlot.setLowerBound(range.getLowerBound());
			chartPlot.setUpperBound(range.getUpperBound());
		}
		chartPlot.setGap(0);

		// Units can only be Fahrenheit, Celsius or none, so turn off for now.
		chartPlot.setUnits(ThermometerPlot.UNITS_NONE);

		// Set the color of the mercury.  Only used when the value is outside of
		// any defined ranges.
		@SuppressWarnings("unchecked")
		List<Paint> seriesPaints = (List<Paint>)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SERIES_COLORS);
		
		Paint paint = jrPlot.getMercuryColor();
		if (paint != null)
		{
			chartPlot.setUseSubrangePaint(false);
		}
		else
		{
			//it has no effect, but is kept for backward compatibility reasons
			paint = seriesPaints.get(0);
		}
		
		chartPlot.setMercuryPaint(paint);

		chartPlot.setThermometerPaint(THERMOMETER_COLOR);
		chartPlot.setThermometerStroke(new BasicStroke(2f));
		chartPlot.setOutlineVisible(false);
		chartPlot.setValueFont(chartPlot.getValueFont().deriveFont(Font.BOLD));

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
//				chartPlot.setValueFont(JRFontUtil.getAwtFont(display.getFont()).deriveFont(Font.BOLD));
			}
		}

		// Set the location of where the value is displayed
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

		return jfreeChart;
	}

	@Override
	protected JFreeChart createDialChart() throws JRException
	{

		JRMeterPlot jrPlot = (JRMeterPlot)getPlot();

		// get data for diagrams
		DialPlot dialPlot = new DialPlot();
		if (getDataset() != null)
		{
			dialPlot.setDataset((ValueDataset)getDataset());
		}
		StandardDialFrame dialFrame = new StandardDialFrame();
		dialFrame.setForegroundPaint(Color.BLACK);
		dialFrame.setBackgroundPaint(Color.BLACK);
		dialFrame.setStroke(new BasicStroke(1f));
		dialPlot.setDialFrame(dialFrame);

		DialBackground db = new DialBackground(ChartThemesConstants.TRANSPARENT_PAINT);
		dialPlot.setBackground(db);
		ScaledDialScale scale = null;
		int dialUnitScale = 1;
		Range range = convertRange(jrPlot.getDataRange());
		if (range != null)
		{
			double bound = Math.max(Math.abs(range.getUpperBound()), Math.abs(range.getLowerBound()));
			dialUnitScale = ChartThemesUtilities.getScale(bound);
	
			double lowerBound = ChartThemesUtilities.getTruncatedValue(range.getLowerBound(), dialUnitScale);
			double upperBound = ChartThemesUtilities.getTruncatedValue(range.getUpperBound(), dialUnitScale);
			int tickCount = jrPlot.getTickCount() != null && jrPlot.getTickCount() > 1 ? jrPlot.getTickCount() : 7;
			scale =
				new ScaledDialScale(
					lowerBound,
					upperBound,
					210,
					-240,
					(upperBound - lowerBound)/(tickCount-1),
					1
					);
			if ((lowerBound == (int)lowerBound &&
					upperBound == (int)upperBound &&
					scale.getMajorTickIncrement() == (int)scale.getMajorTickIncrement()) ||
					dialUnitScale > 1
					)
			{
				scale.setTickLabelFormatter(new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(getLocale())));
			}
			else if (dialUnitScale == 1)
			{

				scale.setTickLabelFormatter(new DecimalFormat("#,##0.0", DecimalFormatSymbols.getInstance(getLocale())));
			}
			else if (dialUnitScale <= 0)
			{
				scale.setTickLabelFormatter(new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(getLocale())));
			}
			else
			{
				// localizing the default tick label formatter
				scale.setTickLabelFormatter(new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(getLocale())));
			}
		}
		else
		{
			scale = new ScaledDialScale();
			
			// localizing the default tick label formatter
			scale.setTickLabelFormatter(new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(getLocale())));
		}
		scale.setTickRadius(0.9);
		scale.setTickLabelOffset(0.16);
		JRFont tickLabelFont = jrPlot.getTickLabelFont();
		Integer defaultBaseFontSize = (Integer)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BASEFONT_SIZE);
		Font themeTickLabelFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_TICK_LABEL_FONT), tickLabelFont, defaultBaseFontSize);
		scale.setTickLabelFont(themeTickLabelFont);
		scale.setMajorTickStroke(new BasicStroke(1f));
		scale.setMinorTickStroke(new BasicStroke(0.7f));
		scale.setMajorTickPaint(Color.BLACK);
		scale.setMinorTickPaint(Color.BLACK);
		scale.setTickLabelsVisible(true);
		scale.setFirstTickLabelVisible(true);

		dialPlot.addScale(0, scale);
		
		
		List<JRMeterInterval> intervals = jrPlot.getIntervals();
		if (intervals != null && intervals.size() > 0)
		{
			int size = Math.min(3, intervals.size());
			
			int colorStep = 0;
			if (size > 3)
				colorStep = 255 / (size - 3);
			
			for (int i = 0; i < size; i++)
			{
				JRMeterInterval interval = intervals.get(i);
				Range intervalRange = convertRange(interval.getDataRange());
				double intervalLowerBound = ChartThemesUtilities.getTruncatedValue(intervalRange.getLowerBound(), dialUnitScale);
				double intervalUpperBound = ChartThemesUtilities.getTruncatedValue(intervalRange.getUpperBound(), dialUnitScale);

				Color color = i < 3 
					? (Color)ChartThemesConstants.AEGEAN_INTERVAL_COLORS.get(i)
					: new Color(255 - colorStep * (i - 3), 0 + colorStep * (i - 3), 0);
					ScaledDialRange dialRange =
						new ScaledDialRange(
							intervalLowerBound,
							intervalUpperBound,
							interval.getBackgroundColor() == null
								? color
								: interval.getBackgroundColor(),
							15f
							);
				dialRange.setInnerRadius(0.5);
				dialRange.setOuterRadius(0.5);
				dialPlot.addLayer(dialRange);
			}
			
		}
		JRValueDisplay display = jrPlot.getValueDisplay();

		String displayVisibility = display != null && getChart().hasProperties() ? 
				getChart().getPropertiesMap().getProperty(DefaultChartTheme.PROPERTY_DIAL_VALUE_DISPLAY_VISIBLE) : "false";

		if (Boolean.valueOf(displayVisibility))
		{
			ScaledDialValueIndicator dvi = new ScaledDialValueIndicator(0, dialUnitScale);
			dvi.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
//			dvi.setFont(JRFontUtil.getAwtFont(jrFont).deriveFont(10f).deriveFont(Font.BOLD));
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
			if (dialUnitScale < 0)
				label = new MessageFormat(label).format(new Object[]{String.valueOf(Math.pow(10, dialUnitScale))});
			else if (dialUnitScale < 3)
				label = new MessageFormat(label).format(new Object[]{"1"});
			else
				label = new MessageFormat(label).format(new Object[]{String.valueOf((int)Math.pow(10, dialUnitScale-2))});

			JRFont displayFont = jrPlot.getValueDisplay().getFont();
			Font themeDisplayFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_DISPLAY_FONT), displayFont, defaultBaseFontSize);
			
			String[] textLines = label.split("\\n");
			for (int i = 0; i < textLines.length; i++)
			{
				DialTextAnnotation dialAnnotation = new DialTextAnnotation(textLines[i]);
				dialAnnotation.setFont(themeDisplayFont);
				dialAnnotation.setPaint(Color.BLACK);
				dialAnnotation.setRadius(Math.sin(Math.PI/6.0) + i/10.0);
				dialAnnotation.setAnchor(TextAnchor.CENTER);
				dialPlot.addLayer(dialAnnotation);
			}
		}

		DialPointer needle = new ScaledDialPointer(dialUnitScale, 0.047);

		needle.setVisible(true);
		needle.setRadius(0.7);
		dialPlot.addLayer(needle);

		DialCap cap = new DialCap();
		cap.setRadius(0.05);
		cap.setFillPaint(Color.BLACK);
		cap.setOutlinePaint(ChartThemesConstants.TRANSPARENT_PAINT);
		dialPlot.setCap(cap);

		JFreeChart jfreeChart =
		new JFreeChart(
			evaluateTextExpression(getChart().getTitleExpression()),
			null,
			dialPlot,
			getChart().getShowLegend() == null ? false : getChart().getShowLegend()
			);

		// Set all the generic options
		configureChart(jfreeChart, getPlot());

		jfreeChart.setBackgroundPaint(ChartThemesConstants.TRANSPARENT_PAINT);
		jfreeChart.setBorderVisible(false);

		return jfreeChart;

	}

	@Override
	protected JFreeChart createCandlestickChart() throws JRException
	{
		JFreeChart jfreeChart = super.createCandlestickChart();
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		CandlestickRenderer renderer = (CandlestickRenderer)xyPlot.getRenderer();
		DefaultHighLowDataset dataset = (DefaultHighLowDataset)xyPlot.getDataset();
		@SuppressWarnings("unchecked")
		List<Paint> seriesPaints = (List<Paint>)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.SERIES_COLORS);

		for (int i = 0; i < dataset.getSeriesCount(); i++)
		{
			
			renderer.setSeriesFillPaint(i, seriesPaints.get(i));
			renderer.setSeriesPaint(i, Color.DARK_GRAY);
		}
		return jfreeChart;
	}
	
}
