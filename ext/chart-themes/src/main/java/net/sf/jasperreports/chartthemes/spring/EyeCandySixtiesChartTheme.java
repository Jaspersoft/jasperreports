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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.chart.ui.GradientPaintTransformType;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.StandardGradientPaintTransformer;
import org.jfree.chart.ui.TextAnchor;

import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRPiePlot;
import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRThermometerPlot;
import net.sf.jasperreports.charts.JRValueDisplay;
import net.sf.jasperreports.charts.fill.DefaultChartTheme;
import net.sf.jasperreports.charts.type.MeterShapeEnum;
import net.sf.jasperreports.charts.type.ValueLocationEnum;
import net.sf.jasperreports.charts.util.ChartUtil;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class EyeCandySixtiesChartTheme extends GenericChartTheme
{

	public static final Color THERMOMETER_COLOR = Color.BLACK;
//	public static final Color MARKER_COLOR = new Color(210,210,210);
	public static final Color SCATTER_GRIDLINE_COLOR = new Color(196, 196, 196);

	/**
	 *
	 */
	public EyeCandySixtiesChartTheme()
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

		GradientPaint gp = (GradientPaint)getDefaultValue(defaultChartPropertiesMap, ChartThemesConstants.BACKGROUND_PAINT);

		jfreeChart.setBackgroundPaint(
			new GradientPaint(
				0f, 0f, gp.getColor1(), 
				0f, ChartUtil.toChartLength(getChart().getHeight() * 0.7f, getChartContext().getReportDpi()), gp.getColor2(), 
				false
				)
			);
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
			categoryPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);
			categoryPlot.setRangeGridlineStroke(new BasicStroke(1f));
			categoryPlot.setDomainGridlinesVisible(false);
			
		}
		else if (plot instanceof XYPlot)
		{
			XYPlot xyPlot = (XYPlot)plot;
			XYDataset xyDataset = xyPlot.getDataset();
			if (xyDataset != null)
			{
				XYItemRenderer xyItemRenderer = xyPlot.getRenderer();
				for (int i = 0; i < xyDataset.getSeriesCount(); i++)
				{
					xyItemRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
				}
			}
			xyPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);
			xyPlot.setRangeGridlineStroke(new BasicStroke(1f));
			xyPlot.setDomainGridlinesVisible(false);
			
			xyPlot.setRangeZeroBaselineVisible(true);

		}
	}

	@Override
	protected JFreeChart createXyAreaChart() throws JRException
	{
		JFreeChart jfreeChart = super.createXyAreaChart();

		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		SquareXYAreaRenderer squareXyAreaRenderer = new SquareXYAreaRenderer((XYAreaRenderer)xyPlot.getRenderer());
		xyPlot.setRenderer(squareXyAreaRenderer);

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
		piePlot.setShadowXOffset(5);
		piePlot.setShadowYOffset(10);
		int reportDpi = getChartContext().getReportDpi();
		piePlot.setShadowPaint(
			new GradientPaint(
				0, ChartUtil.toChartLength(getChart().getHeight() / 2f, reportDpi), new Color(41, 120, 162), 
				0, ChartUtil.toChartLength(getChart().getHeight(), reportDpi), Color.white
				)
			);
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
	protected JFreeChart createBarChart() throws JRException
	{
		JFreeChart jfreeChart = super.createBarChart();

		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		//categoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
		BarRenderer barRenderer = (BarRenderer)categoryPlot.getRenderer();
		barRenderer.setItemMargin(0);
		barRenderer.setGradientPaintTransformer(
			new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
			);
		CategoryDataset categoryDataset = categoryPlot.getDataset();
		if (categoryDataset != null)
		{
			for (int i = 0; i < categoryDataset.getRowCount(); i++)
			{
				barRenderer.setSeriesPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			}
		}
		return jfreeChart;
	}

	@Override
	protected JFreeChart createStackedBarChart() throws JRException
	{
		JFreeChart jfreeChart = super.createStackedBarChart();

		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		//categoryPlot.setOrientation(PlotOrientation.HORIZONTAL);
		BarRenderer barRenderer = (BarRenderer)categoryPlot.getRenderer();
		barRenderer.setItemMargin(0);
		barRenderer.setGradientPaintTransformer(
			new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
			);

		CategoryDataset categoryDataset = categoryPlot.getDataset();
		if (categoryDataset != null)
		{
			for (int i = 0; i < categoryDataset.getRowCount(); i++)
			{
				barRenderer.setSeriesPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			}
		}
		return jfreeChart;
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
		bubbleRenderer = new GradientXYBubbleRenderer(bubbleRenderer.getScaleType());
		xyPlot.setRenderer(bubbleRenderer);
		XYDataset xyDataset = xyPlot.getDataset();
		if (xyDataset != null)
		{
			for (int i = 0; i < xyDataset.getSeriesCount(); i++)
			{
				bubbleRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
				bubbleRenderer.setSeriesPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			}
		}
		return jfreeChart;
	}


	@Override
	protected JFreeChart createXYBarChart() throws JRException
	{
		JFreeChart jfreeChart = super.createXYBarChart();
		XYPlot xyPlot = (XYPlot)jfreeChart.getPlot();
		XYBarRenderer renderer = (XYBarRenderer)xyPlot.getRenderer();
		renderer.setMargin(0.1);
		renderer.setGradientPaintTransformer(
				new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
				);
		XYDataset xyDataset = xyPlot.getDataset();
		if (xyDataset != null)
		{
			for (int i = 0; i < xyDataset.getSeriesCount(); i++)
			{
				renderer.setSeriesPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			}
		}
		return jfreeChart;
	}

	@Override
	protected JFreeChart createScatterChart() throws JRException
	{
		JFreeChart jfreeChart = super.createScatterChart();
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();
		
		xyPlot.setRangeGridlinePaint(SCATTER_GRIDLINE_COLOR);
		xyPlot.setRangeGridlineStroke(new BasicStroke(0.75f));
		xyPlot.setDomainGridlinesVisible(true);
		xyPlot.setDomainGridlinePaint(SCATTER_GRIDLINE_COLOR);
		xyPlot.setDomainGridlineStroke(new BasicStroke(0.75f));
		xyPlot.setRangeZeroBaselinePaint(ChartThemesConstants.GRAY_PAINT_134);

		XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
		lineRenderer.setUseFillPaint(true);
		JRScatterPlot scatterPlot = (JRScatterPlot) getPlot();
		boolean isShowLines = scatterPlot.getShowLines() == null ? false : scatterPlot.getShowLines();
		lineRenderer.setDefaultLinesVisible(isShowLines);
		XYDataset xyDataset = xyPlot.getDataset();
		if (xyDataset != null)
		{
			for (int i = 0; i < xyDataset.getSeriesCount(); i++)
			{
				lineRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
				lineRenderer.setSeriesFillPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
				lineRenderer.setSeriesPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(i));
				//lineRenderer.setSeriesShape(i, new Ellipse2D.Double(-3, -3, 6, 6));
			}
		}
		return jfreeChart;
	}

	@Override
	protected JFreeChart createXyLineChart() throws JRException
	{
		JFreeChart jfreeChart = super.createXyLineChart();
		XYPlot xyPlot = (XYPlot) jfreeChart.getPlot();

		XYLineAndShapeRenderer lineRenderer = (XYLineAndShapeRenderer) jfreeChart.getXYPlot().getRenderer();
		XYLineAndShapeRenderer line3DRenderer = new XYLineAndShapeRenderer();


		line3DRenderer.setDefaultToolTipGenerator(lineRenderer.getDefaultToolTipGenerator());
		line3DRenderer.setURLGenerator(lineRenderer.getURLGenerator());
		line3DRenderer.setDefaultStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		line3DRenderer.setDefaultLinesVisible(lineRenderer.getDefaultLinesVisible());
		line3DRenderer.setDefaultShapesVisible(lineRenderer.getDefaultShapesVisible());
		Stroke stroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		XYDataset xyDataset = xyPlot.getDataset();
		if (xyDataset != null)
		{
			for (int i = 0; i < xyDataset.getSeriesCount(); i++)
			{
				line3DRenderer.setSeriesStroke(i, stroke);
				line3DRenderer.setSeriesLinesVisible(i, lineRenderer.getDefaultLinesVisible());
				line3DRenderer.setSeriesShapesVisible(i, lineRenderer.getDefaultShapesVisible());
			}
		}
//		line3DRenderer.setXOffset(2);
//		line3DRenderer.setYOffset(2);
//		line3DRenderer.setWallPaint(ChartThemesConstants.GRAY_PAINT_134);

		xyPlot.setRenderer(line3DRenderer);
		return jfreeChart;
	}

	@Override
	protected JFreeChart createLineChart() throws JRException
	{
		JFreeChart jfreeChart = super.createLineChart();
		CategoryPlot categoryPlot = (CategoryPlot)jfreeChart.getPlot();
		LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer)categoryPlot.getRenderer();
		lineRenderer.setDefaultStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//		Stroke stroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		for (int i = 0; i < lineRenderer.getRowCount(); i++)
		{
			lineRenderer.setSeriesOutlinePaint(i, ChartThemesConstants.TRANSPARENT_PAINT);
			lineRenderer.setSeriesFillPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			lineRenderer.setSeriesPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(i));
			lineRenderer.setSeriesShapesVisible(i,true);
			//it isn't applied at the moment
			//lineRenderer.setSeriesStroke(i,stroke);
			
			//line3DRenderer.setSeriesLinesVisible(i,lineRenderer.getSeriesVisible(i));
		}
//		configureChart(jfreeChart, getPlot());
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

		categoryPlot.setDomainGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);

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

		categoryPlot.setRangeGridlinePaint(ChartThemesConstants.GRAY_PAINT_134);
		categoryPlot.getDomainAxis().setTickLabelsVisible(
				//barPlot.isShowTickLabels()
				true
				);
		CategoryItemRenderer categoryRenderer = categoryPlot.getRenderer();
		categoryRenderer.setDefaultItemLabelsVisible(true);
		BarRenderer barRenderer = (BarRenderer)categoryRenderer;
		barRenderer.setSeriesPaint(0, ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(3));
		barRenderer.setSeriesPaint(1, ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(0));
		CategoryDataset categoryDataset = categoryPlot.getDataset();
		if (categoryDataset != null)
		{
			for (int i = 0; i < categoryDataset.getRowCount(); i++)
			{
				barRenderer.setSeriesItemLabelFont(i, categoryPlot.getDomainAxis().getTickLabelFont());
				barRenderer.setSeriesItemLabelsVisible(i, true);
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
		// Set the meter's range
		if (range != null)
		{
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
		Paint paint = jrPlot.getMercuryColor();
		if (paint != null)
		{
			chartPlot.setUseSubrangePaint(false);
		}
		else
		{
			//it has no effect, but is kept for backward compatibility reasons
			paint = ChartThemesConstants.EYE_CANDY_SIXTIES_GRADIENT_PAINTS.get(0);
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
				chartPlot.setValueFormat(new DecimalFormat(display.getMask(), DecimalFormatSymbols.getInstance(getLocale())));
			}
			if (display.getFont() != null)
			{
//				chartPlot.setValueFont(JRFontUtil.getAwtFont(display.getFont()).deriveFont(Font.BOLD));
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

		return jfreeChart;

	}

	@Override
	protected JFreeChart createDialChart() throws JRException
	{

		JRMeterPlot jrPlot = (JRMeterPlot)getPlot();

		GradientPaint gp =
			new GradientPaint(
				new Point(), Color.LIGHT_GRAY,
				new Point(), Color.BLACK,
				false
				);

		GradientPaint gp2 =
			new GradientPaint(
				new Point(), Color.GRAY,
				new Point(), Color.BLACK
				);

		// get data for diagrams
		DialPlot dialPlot = new DialPlot();
		//dialPlot.setView(0.0, 0.0, 1.0, 1.0);
		if (getDataset() != null)
		{
			dialPlot.setDataset((ValueDataset)getDataset());
		}
		StandardDialFrame dialFrame = new StandardDialFrame();
		//dialFrame.setRadius(0.60);
		//dialFrame.setBackgroundPaint(gp2);
		dialFrame.setForegroundPaint(gp2);
		dialPlot.setDialFrame(dialFrame);

		DialBackground db = new DialBackground(gp);
		db.setGradientPaintTransformer(new StandardGradientPaintTransformer(
				GradientPaintTransformType.VERTICAL));
		dialPlot.setBackground(db);
		StandardDialScale scale = null;
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
				new StandardDialScale(
					lowerBound,
					upperBound,
					225,
					-270,
					(upperBound - lowerBound)/(tickCount-1),
					15
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
			scale = new StandardDialScale();
			
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
		scale.setMinorTickStroke(new BasicStroke(0.3f));
		scale.setMajorTickPaint(Color.WHITE);
		scale.setMinorTickPaint(Color.WHITE);
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
						12f
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
			JRFont displayFont = jrPlot.getValueDisplay().getFont();
			Font themeDisplayFont = getFont((JRFont)getDefaultValue(defaultPlotPropertiesMap, ChartThemesConstants.PLOT_DISPLAY_FONT), displayFont, defaultBaseFontSize);
			if (dialUnitScale < 0)
				label = new MessageFormat(label).format(new Object[]{String.valueOf(Math.pow(10, dialUnitScale))});
			else if (dialUnitScale < 3)
				label = new MessageFormat(label).format(new Object[]{"1"});
			else
				label = new MessageFormat(label).format(new Object[]{String.valueOf((int)Math.pow(10, dialUnitScale-2))});
		
			String[] textLines = label.split("\\n");
			for (int i = 0; i < textLines.length; i++)
			{
				DialTextAnnotation dialAnnotation = new DialTextAnnotation(textLines[i]);
				dialAnnotation.setFont(themeDisplayFont);
				dialAnnotation.setPaint(Color.WHITE);
				dialAnnotation.setRadius(Math.sin(Math.PI/4.0) + i/10.0);
				dialAnnotation.setAnchor(TextAnchor.CENTER);
				dialPlot.addLayer(dialAnnotation);
			}
		}

		//DialPointer needle = new DialPointer.Pointer();
		Paint paint = new Color(191, 48, 0);
		DialPointer needle = new ScaledDialPointer(dialUnitScale, paint, paint);

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
		if (dataset != null)
		{
			for (int i = 0; i < dataset.getSeriesCount(); i++)
			{
				renderer.setSeriesFillPaint(i, ChartThemesConstants.EYE_CANDY_SIXTIES_COLORS.get(i));
				renderer.setSeriesPaint(i, Color.DARK_GRAY);
			}
		}
		return jfreeChart;
	}

}

class SquareXYAreaRenderer extends XYAreaRenderer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public SquareXYAreaRenderer(XYAreaRenderer parent)
	{
		super(XYAreaRenderer.AREA, parent.getDefaultToolTipGenerator(), parent.getURLGenerator());
	}

	@Override
	public LegendItem getLegendItem(int datasetIndex, int series)
	{
		if (datasetIndex > -1 && series > -1)
		{
			setLegendArea(new Rectangle2D.Double(-3, -3, 6, 6));
			return super.getLegendItem(datasetIndex, series);
		}
		return null;
	}
}


class GradientXYBubbleRenderer extends XYBubbleRenderer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public GradientXYBubbleRenderer(int scaleType)
	{
		super(scaleType);
	}

	@Override
	public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass)
	{
		// return straight away if the item is not visible
		if (!getItemVisible(series, item)) {
			return;
		}

		PlotOrientation orientation = plot.getOrientation();

		// get the data point...
		double x = dataset.getXValue(series, item);
		double y = dataset.getYValue(series, item);
		double z = Double.NaN;
		if (dataset instanceof XYZDataset) {
			XYZDataset xyzData = (XYZDataset) dataset;
			z = xyzData.getZValue(series, item);
		}
		if (!Double.isNaN(z)) {
			RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
			RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
			double transX = domainAxis.valueToJava2D(x, dataArea,
					domainAxisLocation);
			double transY = rangeAxis.valueToJava2D(y, dataArea,
					rangeAxisLocation);

			double transDomain = 0.0;
			double transRange = 0.0;
			double zero;

			switch (getScaleType()) {
				case SCALE_ON_DOMAIN_AXIS:
					zero = domainAxis.valueToJava2D(0.0, dataArea,
							domainAxisLocation);
					transDomain = domainAxis.valueToJava2D(z, dataArea,
							domainAxisLocation) - zero;
					transRange = transDomain;
					break;
				case SCALE_ON_RANGE_AXIS:
					zero = rangeAxis.valueToJava2D(0.0, dataArea,
							rangeAxisLocation);
					transRange = zero - rangeAxis.valueToJava2D(z, dataArea,
							rangeAxisLocation);
					transDomain = transRange;
					break;
				default:
					double zero1 = domainAxis.valueToJava2D(0.0, dataArea,
							domainAxisLocation);
					double zero2 = rangeAxis.valueToJava2D(0.0, dataArea,
							rangeAxisLocation);
					transDomain = domainAxis.valueToJava2D(z, dataArea,
							domainAxisLocation) - zero1;
					transRange = zero2 - rangeAxis.valueToJava2D(z, dataArea,
							rangeAxisLocation);
			}
			transDomain = Math.abs(transDomain);
			transRange = Math.abs(transRange);
			Ellipse2D circle = null;
			if (orientation == PlotOrientation.VERTICAL) {
				circle = new Ellipse2D.Double(transX - transDomain / 2.0,
						transY - transRange / 2.0, transDomain, transRange);
			}
			else if (orientation == PlotOrientation.HORIZONTAL) {
				circle = new Ellipse2D.Double(transY - transRange / 2.0,
						transX - transDomain / 2.0, transRange, transDomain);
			}

			Paint paint = getItemPaint(series, item);
			if (paint instanceof GradientPaint)
			{
				paint = new StandardGradientPaintTransformer().transform((GradientPaint)paint, circle);
			}
			g2.setPaint(paint);
			g2.fill(circle);
			g2.setStroke(getItemOutlineStroke(series, item));
			g2.setPaint(getItemOutlinePaint(series, item));
			g2.draw(circle);

			if (isItemLabelVisible(series, item)) {
				if (orientation == PlotOrientation.VERTICAL) {
					drawItemLabel(g2, orientation, dataset, series, item,
							transX, transY, false);
				}
				else if (orientation == PlotOrientation.HORIZONTAL) {
					drawItemLabel(g2, orientation, dataset, series, item,
							transY, transX, false);
				}
			}

			// add an entity if this info is being collected
			EntityCollection entities = null;
			if (info != null) {
				entities = info.getOwner().getEntityCollection();
				if (entities != null && circle.intersects(dataArea)) {
					addEntity(entities, circle, dataset, series, item,
							circle.getCenterX(), circle.getCenterY());
				}
			}

			int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
			int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
			// FIXME: use updateCrosshairValues with the appropriate parameters
			/*
		 	updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
					rangeAxisIndex, transX, transY, orientation);
			*/
		}
	}
}

class GradientBarRenderer3D extends BarRenderer
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public GradientBarRenderer3D(BarRenderer barRenderer3D)
	{
		setDefaultItemLabelGenerator(barRenderer3D.getDefaultItemLabelGenerator());
		setDefaultItemLabelsVisible(barRenderer3D.getDefaultItemLabelsVisible());
	}

	@Override
	public void drawItem(Graphics2D g2,
		CategoryItemRendererState state,
		Rectangle2D dataArea,
		CategoryPlot plot,
		CategoryAxis domainAxis,
		ValueAxis rangeAxis,
		CategoryDataset dataset,
		int row,
		int column,
		int pass)
	{

		// check the value we are plotting...
		Number dataValue = dataset.getValue(row, column);
		if (dataValue == null) {
			return;
		}

		double value = dataValue.doubleValue();

		Rectangle2D adjusted = new Rectangle2D.Double(dataArea.getX(),
			dataArea.getY(),
			dataArea.getWidth(),
			dataArea.getHeight());

		PlotOrientation orientation = plot.getOrientation();

		double barW0 = calculateBarW0(plot, orientation, adjusted, domainAxis,
			state, row, column);
		double[] barL0L1 = calculateBarL0L1(value);
		if (barL0L1 == null) {
			return;  // the bar is not visible
		}

		RectangleEdge edge = plot.getRangeAxisEdge();
		double transL0 = rangeAxis.valueToJava2D(barL0L1[0], adjusted, edge);
		double transL1 = rangeAxis.valueToJava2D(barL0L1[1], adjusted, edge);
		double barL0 = Math.min(transL0, transL1);
		double barLength = Math.abs(transL1 - transL0);

		// draw the bar...
		Rectangle2D bar = null;
		if (orientation == PlotOrientation.HORIZONTAL) {
			bar = new Rectangle2D.Double(barL0, barW0, barLength, state.getBarWidth());
		}
		else {
			bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(), barLength);
		}
		Paint itemPaint = getItemPaint(row, column);
		if (itemPaint instanceof GradientPaint)
		{
			itemPaint = getGradientPaintTransformer().transform((GradientPaint)itemPaint, bar);
		}
		g2.setPaint(itemPaint);
		g2.fill(bar);

		double x0 = bar.getMinX();
		double x1 = x0;
		double x2 = bar.getMaxX();
		double x3 = x2;

		double y0 = bar.getMinY();
		double y1 = bar.getMinY();
		double y2 = bar.getMaxY();
		double y3 = bar.getMaxY();

		GeneralPath bar3dRight = null;
		GeneralPath bar3dTop = null;
		if (barLength > 0.0) {
			bar3dRight = new GeneralPath();
			bar3dRight.moveTo((float) x2, (float) y3);
			bar3dRight.lineTo((float) x2, (float) y1);
			bar3dRight.lineTo((float) x3, (float) y0);
			bar3dRight.lineTo((float) x3, (float) y2);
			bar3dRight.closePath();

			if (itemPaint instanceof Color) {
				g2.setPaint(((Color) itemPaint).darker());
			}
			else if (itemPaint instanceof GradientPaint)
			{
				GradientPaint gp = (GradientPaint)itemPaint;
				g2.setPaint(
					new StandardGradientPaintTransformer().transform(
						new GradientPaint(gp.getPoint1(), gp.getColor1().darker(), gp.getPoint2(), gp.getColor2().darker(), gp.isCyclic()),
						bar3dRight
						)
					);
			}
			g2.fill(bar3dRight);
		}

		bar3dTop = new GeneralPath();
		bar3dTop.moveTo((float) x0, (float) y1);
		bar3dTop.lineTo((float) x1, (float) y0);
		bar3dTop.lineTo((float) x3, (float) y0);
		bar3dTop.lineTo((float) x2, (float) y1);
		bar3dTop.closePath();
		g2.fill(bar3dTop);

		if (isDrawBarOutline()
			&& state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) 
		{
			g2.setStroke(getItemOutlineStroke(row, column));
			g2.setPaint(getItemOutlinePaint(row, column));
			g2.draw(bar);
			if (bar3dRight != null) {
				g2.draw(bar3dRight);
			}
			if (bar3dTop != null) {
				g2.draw(bar3dTop);
			}
		}

		CategoryItemLabelGenerator generator
		= getItemLabelGenerator(row, column);
		if (generator != null && isItemLabelVisible(row, column)) {
			drawItemLabel(g2, dataset, row, column, plot, generator, bar, (value < 0.0));
		}

		// add an item entity, if this information is being collected
		EntityCollection entities = state.getEntityCollection();
		if (entities != null) {
			GeneralPath barOutline = new GeneralPath();
			barOutline.moveTo((float) x0, (float) y3);
			barOutline.lineTo((float) x0, (float) y1);
			barOutline.lineTo((float) x1, (float) y0);
			barOutline.lineTo((float) x3, (float) y0);
			barOutline.lineTo((float) x3, (float) y2);
			barOutline.lineTo((float) x2, (float) y3);
			barOutline.closePath();
			addItemEntity(entities, dataset, row, column, barOutline);
		}
	}

}
