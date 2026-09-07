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

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.general.Dataset;

import net.sf.jasperreports.charts.ChartContext;
import net.sf.jasperreports.charts.ChartTheme;
import net.sf.jasperreports.charts.ChartVisitorFactory;
import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRAbstractChartCustomizer;
import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRBarPlot;
import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartAxis;
import net.sf.jasperreports.charts.JRChartCustomizer;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRChartPlot.JRSeriesColor;
import net.sf.jasperreports.charts.JRDataRange;
import net.sf.jasperreports.charts.JRGanttDataset;
import net.sf.jasperreports.charts.JRHighLowDataset;
import net.sf.jasperreports.charts.JRHighLowPlot;
import net.sf.jasperreports.charts.JRLinePlot;
import net.sf.jasperreports.charts.JRMeterPlot;
import net.sf.jasperreports.charts.JRMultiAxisPlot;
import net.sf.jasperreports.charts.JRPieDataset;
import net.sf.jasperreports.charts.JRPiePlot;
import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.charts.JRThermometerPlot;
import net.sf.jasperreports.charts.JRTimePeriodDataset;
import net.sf.jasperreports.charts.JRTimeSeriesDataset;
import net.sf.jasperreports.charts.JRTimeSeriesPlot;
import net.sf.jasperreports.charts.JRValueDataset;
import net.sf.jasperreports.charts.JRXyDataset;
import net.sf.jasperreports.charts.JRXyzDataset;
import net.sf.jasperreports.charts.NamedChartCustomizer;
import net.sf.jasperreports.charts.base.JRBaseChart;
import net.sf.jasperreports.charts.type.AxisPositionEnum;
import net.sf.jasperreports.charts.type.ChartTypeEnum;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.charts.util.CategoryChartHyperlinkProvider;
import net.sf.jasperreports.charts.util.ChartHyperlinkProvider;
import net.sf.jasperreports.charts.util.ChartUtil;
import net.sf.jasperreports.charts.util.HighLowChartHyperlinkProvider;
import net.sf.jasperreports.charts.util.JRMeterInterval;
import net.sf.jasperreports.charts.util.MultiAxisChartHyperlinkProvider;
import net.sf.jasperreports.charts.util.PieChartHyperlinkProvider;
import net.sf.jasperreports.charts.util.TimePeriodChartHyperlinkProvider;
import net.sf.jasperreports.charts.util.TimeSeriesChartHyperlinkProvider;
import net.sf.jasperreports.charts.util.XYChartHyperlinkProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JRBaseFiller;
import net.sf.jasperreports.engine.fill.JRFillBand;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillCloneable;
import net.sf.jasperreports.engine.fill.JRFillElement;
import net.sf.jasperreports.engine.fill.JRFillHyperlinkHelper;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRTemplateElement;
import net.sf.jasperreports.engine.fill.JRTemplateImage;
import net.sf.jasperreports.engine.fill.JRTemplatePrintImage;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.StyleResolver;
import net.sf.jasperreports.engine.util.StyleUtil;
import net.sf.jasperreports.export.HtmlReportConfiguration;
import net.sf.jasperreports.renderers.Renderable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @author Some enhancements by Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRFillChart extends JRFillElement implements JRChart
{
	public static final String EXCEPTION_MESSAGE_KEY_CUSTOMIZER_INSTANCE_ERROR = "charts.customizer.instance.error";
	public static final String EXCEPTION_MESSAGE_KEY_MULTIAXIS_PLOT_TYPES_MIX_NOT_ALLOWED = "charts.multiaxis.plot.types.mix.not.allowed";
	public static final String EXCEPTION_MESSAGE_KEY_MULTIAXIS_PLOT_NOT_SUPPORTED = "charts.multiaxis.plot.not.supported";

	/**
	 *
	 */
	protected ChartTypeEnum chartType;

	/**
	 *
	 */
	protected JRFont titleFont;
	protected JRFont subtitleFont;
	protected JRFont legendFont;

	protected final JRLineBox initLineBox;
	protected JRLineBox lineBox;

	protected JRFillChartDataset dataset;
	protected JRChartPlot plot;

	protected Renderable renderer;
	private String anchorName;
	private Integer bookmarkLevel;
	private String hyperlinkReference;
	private Boolean hyperlinkWhen;
	private String hyperlinkAnchor;
	private Integer hyperlinkPage;
	private String hyperlinkTooltip;
	private JRPrintHyperlinkParameters hyperlinkParameters;

	protected List<JRChartCustomizer> chartCustomizers;
	
	protected String renderType;
	protected ChartTheme theme;

	protected JFreeChart jfreeChart;
	protected ChartHyperlinkProvider chartHyperlinkProvider;

	/**
	 *
	 */
	protected JRFillChart(
		JRBaseFiller filler,
		JRChart chart,
		ChartsFillObjectFactory factory
		)
	{
		super(filler, chart, factory.getParent());

		/*   */
		chartType = chart.getChartType();

		switch (chartType) {
			case AREA:
				dataset = (JRFillChartDataset) factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case BAR:
			case BAR3D:
				dataset = (JRFillChartDataset) factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			case BUBBLE:
				dataset = (JRFillChartDataset) factory.getXyzDataset((JRXyzDataset) chart.getDataset());
				plot = factory.getBubblePlot((JRBubblePlot) chart.getPlot());
				break;
			case CANDLESTICK:
				dataset = (JRFillChartDataset) factory.getHighLowDataset((JRHighLowDataset) chart.getDataset());
				plot = factory.getCandlestickPlot((JRCandlestickPlot) chart.getPlot());
				break;
			case HIGHLOW:
				dataset = (JRFillChartDataset) factory.getHighLowDataset((JRHighLowDataset) chart.getDataset());
				plot = factory.getHighLowPlot((JRHighLowPlot) chart.getPlot());
				break;
			case LINE:
				dataset = (JRFillChartDataset) factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getLinePlot((JRLinePlot) chart.getPlot());
				break;
			case METER:
				dataset = (JRFillChartDataset) factory.getValueDataset((JRValueDataset) chart.getDataset());
				plot = factory.getMeterPlot((JRMeterPlot) chart.getPlot());
				break;
			case MULTI_AXIS:
				plot = factory.getMultiAxisPlot((JRMultiAxisPlot) chart.getPlot());
				dataset = ((JRFillMultiAxisPlot)plot).getMainDataset();
				break;
			case PIE:
			case PIE3D:
				dataset = (JRFillChartDataset) factory.getPieDataset((JRPieDataset) chart.getDataset());
				plot = factory.getPiePlot((JRPiePlot) chart.getPlot());
				break;
			case SCATTER:
				dataset = (JRFillChartDataset) factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getScatterPlot((JRScatterPlot) chart.getPlot());
				break;
			case STACKEDBAR:
			case STACKEDBAR3D:
				dataset = (JRFillChartDataset) factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			case THERMOMETER:
				dataset = (JRFillChartDataset) factory.getValueDataset((JRValueDataset) chart.getDataset());
				plot = factory.getThermometerPlot((JRThermometerPlot) chart.getPlot());
				break;
			case TIMESERIES:
				dataset = (JRFillChartDataset) factory.getTimeSeriesDataset((JRTimeSeriesDataset)chart.getDataset());
				plot = factory.getTimeSeriesPlot((JRTimeSeriesPlot)chart.getPlot());
				break;
			case XYAREA:
				dataset = (JRFillChartDataset) factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case XYBAR:
				switch (chart.getDataset().getDatasetType()){
					case JRChartDataset.TIMESERIES_DATASET:
						dataset = (JRFillChartDataset) factory.getTimeSeriesDataset( (JRTimeSeriesDataset)chart.getDataset() );
						break;
					case JRChartDataset.TIMEPERIOD_DATASET:
						dataset = (JRFillChartDataset) factory.getTimePeriodDataset((JRTimePeriodDataset) chart.getDataset() );
						break;
					case JRChartDataset.XY_DATASET:
						dataset = (JRFillChartDataset) factory.getXyDataset( (JRXyDataset)chart.getDataset() );
						break;
					default:
				}

				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			case XYLINE:
				dataset = (JRFillChartDataset) factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getLinePlot((JRLinePlot) chart.getPlot());
				break;
			case STACKEDAREA:
				dataset = (JRFillChartDataset) factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case GANTT:
				dataset = (JRFillChartDataset) factory.getGanttDataset((JRGanttDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			default:
				throw 
					new JRRuntimeException(
						JRBaseChart.EXCEPTION_MESSAGE_KEY_CHART_TYPE_NOT_SUPPORTED,  
						new Object[]{getChartType()} 
						);
		}

		JRFillObjectFactory parentFactory = factory.getParent();

		titleFont = parentFactory.getFont(chart, chart.getTitleFont());
		subtitleFont = parentFactory.getFont(chart, chart.getSubtitleFont());
		//FIXME this is inconsistent with the lines above
		legendFont =  parentFactory.getFont(this, chart.getLegendFont());
		
		initLineBox = chart.getLineBox().clone(this);

		chartCustomizers = new ArrayList<>();
		JRChartCustomizer chartCustomizer = createAndInitCustomizer(chart.getCustomizerClass(), null);
		if (chartCustomizer != null)
		{
			chartCustomizers.add(chartCustomizer);
		}

		List<JRPropertiesUtil.PropertySuffix> properties = 
			JRPropertiesUtil.getProperties(
				chart.getPropertiesMap(), 
				NamedChartCustomizer.CUSTOMIZER_CLASS_PROPERTY_PREFIX
				);
		for (JRPropertiesUtil.PropertySuffix prop : properties) 
		{
			chartCustomizer = createAndInitCustomizer(prop.getValue(), prop.getSuffix());
			if (chartCustomizer != null)
			{
				chartCustomizers.add(chartCustomizer);
			}
		}
		
		renderType = chart.getRenderType();
		if (renderType == null)
		{
			renderType = filler.getPropertiesUtil().getProperty(getParentProperties(), JRChart.PROPERTY_CHART_RENDER_TYPE);
		}
	}

	@Override
	protected StyleResolver getStyleResolver() 
	{
		return getDefaultStyleProvider().getStyleResolver();
	}

	/**
	 *
	 */
	protected JRChartCustomizer createAndInitCustomizer(
		String customizerClassName,
		String customizerName
		)
	{
		JRChartCustomizer customizer = null;

		if (customizerClassName != null && customizerClassName.length() > 0) 
		{
			try 
			{
				Class<?> customizerClass = JRClassLoader.loadClassForName(customizerClassName);
				customizer = (JRChartCustomizer) customizerClass.getDeclaredConstructor().newInstance();
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException 
				| NoSuchMethodException | InvocationTargetException e) 
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_CUSTOMIZER_INSTANCE_ERROR,
						(Object[])null,
						e);
			}

			if (customizer instanceof JRAbstractChartCustomizer)
			{
				((JRAbstractChartCustomizer) customizer).init(this);
			}

			if (customizer instanceof NamedChartCustomizer)
			{
				((NamedChartCustomizer) customizer).setName(customizerName);
			}
		}
		
		return customizer;
	}

	@Override
	public void setBand(JRFillBand band)
	{
		super.setBand(band);
		
		dataset.setBand(band);
		((JRFillChartPlot) plot).setBand(band);
	}


	@Override
	protected void evaluateStyle(
		byte evaluation
		) throws JRException
	{
		super.evaluateStyle(evaluation);

		lineBox = null;
		
		if (providerStyle != null)
		{
			lineBox = initLineBox.clone(this);
			StyleUtil.appendBox(lineBox, providerStyle.getLineBox());
		}
	}


	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}

	@Override
	public Boolean getShowLegend()
	{
		return ((JRChart)parent).getShowLegend();
	}

	@Override
	public void setShowLegend(Boolean isShowLegend)
	{
	}

	@Override
	public String getRenderType()
	{
		return renderType;
	}

	@Override
	public void setRenderType(String renderType)
	{
	}

	@Override
	public String getTheme()
	{
		return ((JRChart)parent).getTheme();
	}

	@Override
	public void setTheme(String theme)
	{
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return EvaluationTimeEnum.getValueOrDefault(((JRChart)this.parent).getEvaluationTime());
	}

	@Override
	public String getEvaluationGroup()
	{
		return ((JRChart)parent).getEvaluationGroup();
	}

	@Override
	public JRLineBox getLineBox()
	{
		return lineBox == null ? initLineBox : lineBox;
	}

	@Override
	public JRFont getTitleFont()
	{
		return titleFont;
	}

	@Override
	public EdgeEnum getTitlePosition()
	{
		return ((JRChart)parent).getTitlePosition();
	}

	@Override
	public void setTitlePosition(EdgeEnum titlePosition)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Color getTitleColor()
	{
		return getChartsStyleResolver().getTitleColor(this);
	}

	@Override
	public Color getOwnTitleColor()
	{
		return ((JRChart)parent).getOwnTitleColor();
	}

	@Override
	public void setTitleColor(Color titleColor)
	{
	}

	@Override
	public JRFont getSubtitleFont()
	{
		return subtitleFont;
	}

	@Override
	public Color getOwnSubtitleColor()
	{
		return ((JRChart)parent).getOwnSubtitleColor();
	}

	@Override
	public Color getSubtitleColor()
	{
		return getChartsStyleResolver().getSubtitleColor(this);
	}

	@Override
	public void setSubtitleColor(Color subtitleColor)
	{
	}

	/**
	 * Returns the color to use for text in the legend.
	 *
	 * @return the color to use for text in the legend
	 */
	@Override
	public Color getOwnLegendColor()
	{
		return ((JRChart)parent).getOwnLegendColor();
	}

	/**
	 * Returns the inherited color to use for text in the legend.
	 *
	 * @return the color to use for text in the legend
	 */
	@Override
	public Color getLegendColor()
	{
		return getChartsStyleResolver().getLegendColor(this);
	}

	/**
	 * Sets the color to use for text in the legend.
	 *
	 * @param legendColor the color to use for text in the legend
	 */
	@Override
	public void setLegendColor(Color legendColor)
	{
	}

	/**
	 * Returns the color to use as the background of the legend.
	 *
	 * @return the color to use as the background of the legend
	 */
	@Override
	public Color getOwnLegendBackgroundColor()
	{
		return ((JRChart)parent).getOwnLegendBackgroundColor();
	}

	/**
	 * Returns the color to use as the background of the legend.
	 *
	 * @return the color to use as the background of the legend
	 */
	@Override
	public Color getLegendBackgroundColor()
	{
		return getChartsStyleResolver().getLegendBackgroundColor(this);
	}

	/**
	 * Sets the color to use for the background of the legend.
	 *
	 * @param legendBackgroundColor the color to use for the background of the legend
	 */
	@Override
	public void setLegendBackgroundColor(Color legendBackgroundColor)
	{
	}

	/**
	 * Returns the font to use in the legend.
	 *
	 * @return the font to use in the legend
	 */
	@Override
	public JRFont getLegendFont()
	{
		return legendFont;
	}

	@Override
	public EdgeEnum getLegendPosition()
	{
		return ((JRChart)parent).getLegendPosition();
	}

	@Override
	public void setLegendPosition(EdgeEnum legendPosition)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRExpression getTitleExpression()
	{
		return ((JRChart)parent).getTitleExpression();
	}

	@Override
	public JRExpression getSubtitleExpression()
	{
		return ((JRChart)parent).getSubtitleExpression();
	}

	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return ((JRChart)parent).getHyperlinkType();
	}

	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return ((JRChart)parent).getHyperlinkTarget();
	}

	@Override
	public JRExpression getAnchorNameExpression()
	{
		return ((JRChart)parent).getAnchorNameExpression();
	}
	
	@Override
	public JRExpression getBookmarkLevelExpression()
	{
		return ((JRChart)parent).getBookmarkLevelExpression();
	}

	@Override
	public JRExpression getHyperlinkReferenceExpression()
	{
		return ((JRChart)parent).getHyperlinkReferenceExpression();
	}

	@Override
	public JRExpression getHyperlinkWhenExpression()
	{
		return ((JRChart)parent).getHyperlinkWhenExpression();
	}

	@Override
	public JRExpression getHyperlinkAnchorExpression()
	{
		return ((JRChart)parent).getHyperlinkAnchorExpression();
	}

	@Override
	public JRExpression getHyperlinkPageExpression()
	{
		return ((JRChart)parent).getHyperlinkPageExpression();
	}


	/**
	 *
	 */
	public Locale getLocale()
	{
		return filler.getLocale();
	}

	@Override
	public TimeZone getTimeZone()
	{
		return super.getTimeZone();
	}

	@Override
	public JRChartDataset getDataset()
	{
		return dataset;
	}

	/**
	 *
	 */
	public void setDataset(JRFillChartDataset dataset)
	{
		this.dataset = dataset;
	}

	@Override
	public JRChartPlot getPlot()
	{
		return plot;
	}

	/**
	 *
	 */
	protected Renderable getRenderable()
	{
		return renderer;
	}

	/**
	 *
	 */
	protected String getAnchorName()
	{
		return anchorName;
	}

	/**
	 *
	 */
	protected String getHyperlinkReference()
	{
		return hyperlinkReference;
	}

	/**
	 *
	 */
	protected String getHyperlinkAnchor()
	{
		return hyperlinkAnchor;
	}

	/**
	 *
	 */
	protected Integer getHyperlinkPage()
	{
		return hyperlinkPage;
	}

	protected String getHyperlinkTooltip()
	{
		return hyperlinkTooltip;
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}


	/**
	 *
	 */
	protected JRTemplateImage getJRTemplateImage()
	{
		return (JRTemplateImage) getElementTemplate();
	}

	@Override
	protected JRTemplateElement createElementTemplate()
	{
		ChartTemplateImage templateImage = new ChartTemplateImage(getElementOrigin(), 
				filler.getJasperPrint().getDefaultStyleProvider(), this);
		templateImage.setUsingCache(false);
		return templateImage;
	}


	@Override
	protected void rewind()
	{
	}


	@Override
	protected void evaluate(byte evaluation) throws JRException
	{
		reset();

		evaluatePrintWhenExpression(evaluation);

		if (isPrintWhenExpressionNull() || isPrintWhenTrue())
		{
			bookmarkLevel = getBookmarkLevel(evaluateExpression(getBookmarkLevelExpression(), evaluation));

			if (getEvaluationTime() == EvaluationTimeEnum.NOW)
			{
				evaluateProperties(evaluation);
				evaluateStyle(evaluation);
				
				evaluateRenderer(evaluation);
			}
		}
	}


	/**
	 *
	 */
	protected void evaluateRenderer(byte evaluation) throws JRException
	{
		JFreeChart chart = evaluateChart(evaluation);
		
		Rectangle2D rectangle = new Rectangle2D.Double(0,0,getWidth(),getHeight());

		renderer = 
			ChartUtil.getInstance(filler.getJasperReportsContext()).getChartRenderableFactory(getRenderType()).getRenderable(
				filler.getJasperReportsContext(),
				chart, 
				chartHyperlinkProvider,
				rectangle,
				filler.getDpi()
				);
	}
	
	protected ChartHyperlinkProvider getHyperlinkProvider()
	{
		return chartHyperlinkProvider;
	}
	
	/**
	 *
	 */
	protected JFreeChart evaluateChart(byte evaluation) throws JRException
	{
		evaluateDatasetRun(evaluation);

		// needs to be lazy loaded here because in the JRFillChart constructor above, the parent properties could return null,
		// as the filler main dataset is not yet set, if the current band is a group band
		if (theme == null)
		{
			String themeName = getTheme();
			if (themeName == null)
			{
				themeName = filler.getPropertiesUtil().getProperty(getParentProperties(), JRChart.PROPERTY_CHART_THEME);
			}

			theme = ChartUtil.getInstance(filler.getJasperReportsContext()).getTheme(themeName);
		}
		
		if (getChartType() == ChartTypeEnum.MULTI_AXIS)
		{
			//FIXMECHARTTHEME multi axis charts do not support themes
			createMultiAxisChart(evaluation);
		}
		else
		{
			jfreeChart = theme.createChart(new FillChartContext(evaluation));

			chartHyperlinkProvider = createChartHyperlinkProvider();
		}

		for (JRChartCustomizer chartCustomizer : chartCustomizers)
		{
			chartCustomizer.customize(jfreeChart, this);
		}

		anchorName = (String) evaluateExpression(getAnchorNameExpression(), evaluation);
		hyperlinkReference = (String) evaluateExpression(getHyperlinkReferenceExpression(), evaluation);
		hyperlinkWhen = (Boolean) evaluateExpression(getHyperlinkWhenExpression(), evaluation);
		hyperlinkAnchor = (String) evaluateExpression(getHyperlinkAnchorExpression(), evaluation);
		hyperlinkPage = (Integer) evaluateExpression(getHyperlinkPageExpression(), evaluation);
		hyperlinkTooltip = (String) evaluateExpression(getHyperlinkTooltipExpression(), evaluation);
		hyperlinkParameters = JRFillHyperlinkHelper.evaluateHyperlinkParameters(this, expressionEvaluator, evaluation);

		return jfreeChart;
	}


	@Override
	protected boolean prepare(
		int availableHeight,
		boolean isOverflow,
		boolean isOverflowAllowed
		)
	{
		boolean willOverflow = false;

		if (
			isPrintWhenExpressionNull() ||
			( !isPrintWhenExpressionNull() &&
			isPrintWhenTrue() )
			)
		{
			setToPrint(true);
		}
		else
		{
			setToPrint(false);
		}

		if (!isToPrint())
		{
			return willOverflow;
		}

		boolean isToPrint = true;
		boolean isReprinted = false;

		if (getEvaluationTime() == EvaluationTimeEnum.NOW)
		{
			if (isOverflow && isAlreadyPrinted() && !isPrintWhenDetailOverflows())
			{
				isToPrint = false;
			}

			if (
				isToPrint &&
				availableHeight < getRelativeY() + getHeight()
				)
			{
				isToPrint = false;
				willOverflow = true;
			}

			if (
				isToPrint &&
				isOverflow &&
				//(this.isAlreadyPrinted() || !this.isPrintRepeatedValues())
				(isPrintWhenDetailOverflows() && (isAlreadyPrinted() || (!isAlreadyPrinted() && !isPrintRepeatedValues())))
				)
			{
				isReprinted = true;
			}

			if (
				isToPrint &&
				isRemoveLineWhenBlank() &&
				getRenderable() == null
				)
			{
				isToPrint = false;
			}
		}
		else
		{
			if (isOverflow && isAlreadyPrinted() && !isPrintWhenDetailOverflows())
			{
				isToPrint = false;
			}

			if (
				isToPrint &&
				availableHeight < getRelativeY() + getHeight()
				)
			{
				isToPrint = false;
				willOverflow = true;
			}

			if (
				isToPrint &&
				isOverflow &&
				//(this.isAlreadyPrinted() || !this.isPrintRepeatedValues())
				(isPrintWhenDetailOverflows() && (isAlreadyPrinted() || (!isAlreadyPrinted() && !isPrintRepeatedValues())))
				)
			{
				isReprinted = true;
			}
		}

		setToPrint(isToPrint);
		setReprinted(isReprinted);

		return willOverflow;
	}


	@Override
	protected JRPrintElement fill()
	{
		JRTemplatePrintImage printImage = new JRTemplatePrintImage(getJRTemplateImage(), printElementOriginator);

		printImage.setUUID(getUUID());
		printImage.setX(getX());
		printImage.setY(getRelativeY());
		printImage.setWidth(getWidth());
		printImage.setHeight(getStretchHeight());
		printImage.setBookmarkLevel(getBookmarkLevel());
		HtmlReportConfiguration.forceEmbedImage(filler.getPropertiesUtil(), this, printImage);

		EvaluationTimeEnum evaluationTime = getEvaluationTime();
		if (evaluationTime == EvaluationTimeEnum.NOW)
		{
			copy(printImage);
		}
		else
		{
			filler.addBoundElement(this, printImage, evaluationTime, getEvaluationGroup(), band);
		}

		return printImage;
	}


	/**
	 *
	 */
	protected void copy(JRPrintImage printImage)
	{
		printImage.setRenderer(getRenderable());
		printImage.setAnchorName(getAnchorName());
		if (getHyperlinkWhenExpression() == null || Boolean.TRUE.equals(hyperlinkWhen))
		{
			printImage.setHyperlinkReference(getHyperlinkReference());
			printImage.setHyperlinkAnchor(getHyperlinkAnchor());
			printImage.setHyperlinkPage(getHyperlinkPage());
			printImage.setHyperlinkTooltip(getHyperlinkTooltip());
			printImage.setHyperlinkParameters(hyperlinkParameters);
		}
		else
		{
			if (printImage instanceof JRTemplatePrintImage)//this is normally the case
			{
				((JRTemplatePrintImage) printImage).setHyperlinkOmitted(true);
			}
			
			printImage.setHyperlinkReference(null);
		}
		transferProperties(printImage);
	}

	@Override
	public ChartTypeEnum getChartType()
	{
		return chartType;
	}


	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		new ChartsExpressionCollector(collector).collect(this);
	}


	@Override
	public void visit(JRVisitor visitor)
	{
		ChartVisitorFactory.getInstance().getChartVisitor(visitor).visitChart(this);
	}

	
	/**
	 *
	 */
	protected ChartHyperlinkProvider createChartHyperlinkProvider()
	{
		ChartHyperlinkProvider chartHyperlinkProvider = null;
		
		switch (getChartType()) 
		{
			case AREA:
			case BAR:
			case BAR3D:
			case LINE:
			case STACKEDBAR3D:
			case STACKEDBAR:
			case STACKEDAREA:
				chartHyperlinkProvider = new CategoryChartHyperlinkProvider(((JRFillCategoryDataset)getDataset()).getItemHyperlinks());
				break;
			case BUBBLE:
				chartHyperlinkProvider = new XYChartHyperlinkProvider(((JRFillXyzDataset)getDataset()).getItemHyperlinks());
				break;
			case SCATTER:
			case XYAREA:
			case XYLINE:
				chartHyperlinkProvider = new XYChartHyperlinkProvider(((JRFillXyDataset)getDataset()).getItemHyperlinks());
				break;
			case CANDLESTICK:
			case HIGHLOW:
				chartHyperlinkProvider = new HighLowChartHyperlinkProvider(((JRFillHighLowDataset)getDataset()).getItemHyperlinks());
				break;
			case MULTI_AXIS:
				//multi-axis charts are dealt with in createMultiAxisChart
				break;
			case PIE:
			case PIE3D:
				chartHyperlinkProvider = new PieChartHyperlinkProvider(((JRFillPieDataset)getDataset()).getSectionHyperlinks());
				break;
			case TIMESERIES:
				chartHyperlinkProvider = new TimeSeriesChartHyperlinkProvider(((JRFillTimeSeriesDataset)getDataset()).getItemHyperlinks());
				break;
			case XYBAR:
				switch (getDataset().getDatasetType())
				{
					case JRChartDataset.TIMESERIES_DATASET :
						chartHyperlinkProvider = new TimeSeriesChartHyperlinkProvider(((JRFillTimeSeriesDataset)getDataset()).getItemHyperlinks());
						break;
					case JRChartDataset.TIMEPERIOD_DATASET :
						chartHyperlinkProvider = new TimePeriodChartHyperlinkProvider(((JRFillTimePeriodDataset)getDataset()).getItemHyperlinks());
						break;
					case JRChartDataset.XY_DATASET :
						chartHyperlinkProvider = new XYChartHyperlinkProvider(((JRFillXyDataset)getDataset()).getItemHyperlinks());
						break;
					default:
						break;
				}
				break;
			case GANTT:
				chartHyperlinkProvider = new XYChartHyperlinkProvider(((JRFillGanttDataset)getDataset()).getItemHyperlinks());
				break;
			case METER:
			case THERMOMETER:
				//no item hyperlinks
				break;
			default:
				throw 
					new JRRuntimeException(
						JRBaseChart.EXCEPTION_MESSAGE_KEY_CHART_TYPE_NOT_SUPPORTED,  
						new Object[]{getChartType()} 
						);
		}

		return chartHyperlinkProvider;
	}



	/**
	 * Build and configure a multiple axis chart.  A multiple axis chart support more than
	 * one range axis.  Multiple datasets using different ranges can be displayed as long as
	 * they share a common domain axis.  Each dataset can be rendered differently, so one chart
	 * can contain (for example) two line charts, a bar chart and an area chart.
	 * <br><br>
	 * Multiple axis charts are handled differently than the other chart types.  They do not
	 * have a dataset, as each chart that is added to the multiple axis chart has its own
	 * dataset.  For simplicity, each dataset is treated as its own chart, and in fact we
	 * reuse the design of all the chart types and let JFreeChart actually run them.  Then
	 * we pull out the bits we need and add it to the common chart.  All the plot and chart
	 * options on the nested charts is ignored, and all formatting is controlled by the plot
	 * attached to the multiAxisChart.  The one exception is seriesColor, which can be used in
	 * a nested report to specify a color for a specific series in that report.
	 *
	 * @param evaluation current expression evaluation phase
	 * @throws JRException
	 */
	protected void createMultiAxisChart(byte evaluation) throws JRException
	{
		// A multi axis chart has to have at least one axis and chart specified.
		// Create the first axis as the base plot, and then go ahead and create the
		// charts for any additional axes.  Just take the renderer and data series
		// from those charts and add them to the first one.
		Plot mainPlot = null;

		JRFillMultiAxisPlot jrPlot = (JRFillMultiAxisPlot)getPlot();
		
		// create a multi axis hyperlink provider
		MultiAxisChartHyperlinkProvider multiHyperlinkProvider = new MultiAxisChartHyperlinkProvider();
		
		// Generate the main plot from the first axes specified.
		Iterator<JRChartAxis> iter = jrPlot.getAxes().iterator();
		if (iter.hasNext())
		{
			JRFillChartAxis axis = (JRFillChartAxis)iter.next();
			JRFillChart fillChart = axis.getFillChart();
			
			//a JFreeChart object should be obtained first; the rendering type should be always "vector"
			

			jfreeChart = fillChart.evaluateChart(evaluation);
			//FIXME honor printWhenExpression
			// Override the plot from the first axis with the plot for the multi-axis
			// chart.
			//FIXME is the above comment true? 
			//configureChart(jfreeChart, getPlot(), evaluation);
			mainPlot = jfreeChart.getPlot();
			ChartHyperlinkProvider axisHyperlinkProvider = fillChart.getHyperlinkProvider();

			if (mainPlot instanceof CategoryPlot)
			{
				CategoryPlot categoryPlot = (CategoryPlot) mainPlot;
				categoryPlot.setRangeAxisLocation(0, getChartAxisLocation(axis));
				if (axisHyperlinkProvider != null)
				{
					multiHyperlinkProvider.addHyperlinkProvider(categoryPlot.getDataset(), 
							axisHyperlinkProvider);
				}
			}
			else if (mainPlot instanceof XYPlot)
			{
				XYPlot xyPlot = (XYPlot) mainPlot;
				xyPlot.setRangeAxisLocation(0, getChartAxisLocation(axis));
				if (axisHyperlinkProvider != null)
				{
					multiHyperlinkProvider.addHyperlinkProvider(xyPlot.getDataset(), axisHyperlinkProvider);
				}
			}
		 }

		// Now handle all the extra axes, if any.
		int axisNumber = 0;
		while (iter.hasNext())
		{			
			JRFillChartAxis chartAxis = (JRFillChartAxis)iter.next();
			JRFillChart fillChart = chartAxis.getFillChart();

			fillChart.evaluatePrintWhenExpression(evaluation);
			if (!(fillChart.isPrintWhenExpressionNull() || fillChart.isPrintWhenTrue()))
			{
				continue;
			}

			axisNumber++;
			JFreeChart axisChart = fillChart.evaluateChart(evaluation);
			ChartHyperlinkProvider axisHyperlinkProvider = fillChart.getHyperlinkProvider();

			// In JFreeChart to add a second chart type to an existing chart
			// you need to add an axis, a data series and a renderer.  To
			// leverage existing code we simply create a new chart for the
			// axis and then pull out the bits we need and add them to the multi
			// chart.  Currently JFree only supports category plots and xy plots
			// in a multi-axis chart, and you can not mix the two.
			if (mainPlot instanceof CategoryPlot)
			{
				CategoryPlot mainCatPlot = (CategoryPlot)mainPlot;
				if (!(axisChart.getPlot() instanceof CategoryPlot))
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_MULTIAXIS_PLOT_TYPES_MIX_NOT_ALLOWED,  
							(Object[])null 
							);
				}

				// Get the axis and add it to the multi axis chart plot
				CategoryPlot axisPlot = (CategoryPlot)axisChart.getPlot();
				mainCatPlot.setRangeAxis(axisNumber, axisPlot.getRangeAxis());
				mainCatPlot.setRangeAxisLocation(axisNumber, getChartAxisLocation(chartAxis));

				// Add the data set and map it to the recently added axis
				mainCatPlot.setDataset(axisNumber, axisPlot.getDataset());
				mainCatPlot.mapDatasetToRangeAxis(axisNumber, axisNumber);

				// Set the renderer to use to draw the dataset.
				mainCatPlot.setRenderer(axisNumber, axisPlot.getRenderer());

				// Handle any color series for this chart
				configureAxisSeriesColors(axisPlot.getRenderer(), fillChart.getPlot());
				
				if (axisHyperlinkProvider != null)
				{
					multiHyperlinkProvider.addHyperlinkProvider(axisPlot.getDataset(), 
							axisHyperlinkProvider);
				}
			}
			else if (mainPlot instanceof XYPlot)
			{
				XYPlot mainXyPlot = (XYPlot)mainPlot;
				if (!(axisChart.getPlot() instanceof XYPlot))
				{
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_MULTIAXIS_PLOT_TYPES_MIX_NOT_ALLOWED,  
							(Object[])null 
							);
				}

				// Get the axis and add it to the multi axis chart plot
				XYPlot axisPlot = (XYPlot)axisChart.getPlot();
				mainXyPlot.setRangeAxis(axisNumber, axisPlot.getRangeAxis());
				mainXyPlot.setRangeAxisLocation(axisNumber, getChartAxisLocation(chartAxis));

				// Add the data set and map it to the recently added axis
				mainXyPlot.setDataset(axisNumber, axisPlot.getDataset());
				mainXyPlot.mapDatasetToRangeAxis(axisNumber, axisNumber);

				// Set the renderer to use to draw the dataset.
				mainXyPlot.setRenderer(axisNumber, axisPlot.getRenderer());

				// Handle any color series for this chart
				configureAxisSeriesColors(axisPlot.getRenderer(), fillChart.getPlot());
				
				if (axisHyperlinkProvider != null)
				{
					multiHyperlinkProvider.addHyperlinkProvider(axisPlot.getDataset(), 
							axisHyperlinkProvider);
				}
			}
			else
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_MULTIAXIS_PLOT_NOT_SUPPORTED,  
						(Object[])null 
						);
			}
		}

		//set the multi hyperlink provider
		chartHyperlinkProvider = multiHyperlinkProvider;
	}


	/**
	 * The series colors set in the main plot of a multiple axis chart are used for
	 * all the rendered charts in the plot.  This is a problem with multiple line
	 * charts, using different scales and thus different axis.  All the lines will
	 * be drawn using the first series color (since they are the first series for that
	 * rendered) and it will be impossible to tell them apart.
	 * <br><br>
	 * For this reason we interpret series colors for charts included in a multiple
	 * axis chart as specify absolute series colors for that renderer.
	 *
	 * @param renderer the renderer of the chart being created
	 * @param jrPlot the Jasper view of that plot
	 */
	private void configureAxisSeriesColors(CategoryItemRenderer renderer, JRChartPlot jrPlot)
	{
		SortedSet<JRSeriesColor> seriesColors = jrPlot.getSeriesColors();

		if (seriesColors != null)
		{
			for (JRSeriesColor seriesColor : seriesColors)
			{
				renderer.setSeriesPaint(seriesColor.getSeriesOrder(), seriesColor.getColor());
			}
		}
	}

	/**
	 * The series colors set in the main plot of a multiple axis chart are used for
	 * all the rendered charts in the plot.  This is a problem with multiple line
	 * charts, using different scales and thus different axis.  All the lines will
	 * be drawn using the first series color (since they are the first series for that
	 * rendered) and it will be impossible to tell them apart.
	 * <br>
	 * For this reason we interpret series colors for charts included in a multiple
	 * axis chart as specify absolute series colors for that renderer.
	 *
	 * @param renderer the renderer of the chart being created
	 * @param jrPlot the Jasper view of that plot
	 */
	private void configureAxisSeriesColors(XYItemRenderer renderer, JRChartPlot jrPlot)
	{
		SortedSet<JRSeriesColor> seriesColors = jrPlot.getSeriesColors();

		if (seriesColors != null)
		{
			for (JRSeriesColor seriesColor : seriesColors)
			{
				renderer.setSeriesPaint(seriesColor.getSeriesOrder(), seriesColor.getColor());
			}
		}
	}

	/**
	 * Converts a JasperReport data range into one understood by JFreeChart.
	 *
	 * @param dataRange the JasperReport version of the range
	 * @param evaluation current expression evaluation phase
	 * @return the JFreeChart version of the range
	 * @throws JRException thrown when the low value of the range is greater than the
	 * 						high value
	 */
	protected Range convertRange(JRDataRange dataRange, byte evaluation) throws JRException
	{
		if (dataRange == null)
		{
			return null;
		}
		Number low = (Number)evaluateExpression(dataRange.getLowExpression(), evaluation);
		Number high = (Number)evaluateExpression(dataRange.getHighExpression(), evaluation);
		return new Range( low != null ? low.doubleValue() : 0.0,
								 high != null ? high.doubleValue() : 100.0);
	}

	/**
	 * Converts a JasperReports meter interval to one that JFreeChart understands.
	 *
	 * @param interval the JasperReports definition of an interval
	 * @param evaluation current evaluation time
	 * @return the JFreeChart version of the same interval
	 * @throws JRException thrown when the interval contains an invalid range
	 */
	protected MeterInterval convertInterval(JRMeterInterval interval, byte evaluation) throws JRException
	{
		String label = interval.getLabel();
		if (label == null)
		{
			label = "";
		}

		Range range = convertRange(interval.getDataRange(), evaluation);

		Color color = interval.getBackgroundColor();
		float[] components = color.getRGBColorComponents(null);
		float alpha = (float)(interval.getAlpha() == null ? JRMeterInterval.DEFAULT_TRANSPARENCY : interval.getAlpha());

		Color alphaColor = new Color(components[0], components[1], components[2], alpha);

		return new MeterInterval(label, range, alphaColor, null, alphaColor);
	}

	protected AxisLocation getChartAxisLocation(JRFillChartAxis chartAxis)
	{
		return chartAxis.getPosition() != null && chartAxis.getPosition() == AxisPositionEnum.RIGHT_OR_BOTTOM
				? AxisLocation.BOTTOM_OR_RIGHT 
				: AxisLocation.TOP_OR_LEFT;
	}
	
	@Override
	protected void resolveElement(JRPrintElement element, byte evaluation) throws JRException
	{
		evaluateRenderer(evaluation);

		copy((JRPrintImage) element);
		filler.updateBookmark(element);
	}


	@Override
	public int getBookmarkLevel()
	{
		return this.bookmarkLevel == null ? ((JRChart)parent).getBookmarkLevel() : this.bookmarkLevel;
	}

	@Override
	public String getCustomizerClass()
	{
		return ((JRChart)parent).getCustomizerClass();
	}


	private void evaluateDatasetRun(byte evaluation) throws JRException
	{
		dataset.evaluateDatasetRun(evaluation);
	}


	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		//not needed
		return null;
	}


	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters()
	{
		return ((JRChart) parent).getHyperlinkParameters();
	}


	@Override
	public String getLinkType()
	{
		return ((JRChart) parent).getLinkType();
	}

	@Override
	public String getLinkTarget()
	{
		return ((JRChart) parent).getLinkTarget();
	}


	@Override
	public JRExpression getHyperlinkTooltipExpression()
	{
		return ((JRChart) parent).getHyperlinkTooltipExpression();
	}

	class FillChartContext implements ChartContext
	{
		@Override
		public int getReportDpi()
		{
			return filler.getDpi();
		}

		private final byte evaluation; 
		
		protected FillChartContext(byte evaluation)
		{
			this.evaluation = evaluation;
			
			JRFillChart.this.filler.getJasperReportsContext();
		}
		
		@Override
		public JasperReportsContext getJasperReportsContext()
		{
			return JRFillChart.this.filler.getJasperReportsContext();
		}
		
		@Override
		public String evaluateTextExpression(JRExpression expression) throws JRException {
			return JRStringUtil.getString(JRFillChart.this.evaluateExpression(expression, evaluation));
		}

		@Override
		public Object evaluateExpression(JRExpression expression) throws JRException {
			return JRFillChart.this.evaluateExpression(expression, evaluation);
		}

		@Override
		public JRChart getChart() {
			return JRFillChart.this;
		}

		@Override
		public Dataset getDataset() {
			return ((JRFillChartDataset)JRFillChart.this.getDataset()).getDataset();
		}

		@Override
		public Object getLabelGenerator() {
			return ((JRFillChartDataset)JRFillChart.this.getDataset()).getLabelGenerator();
		}

		@Override
		public Locale getLocale() {
			return JRFillChart.this.getLocale();
		}

		@Override
		public TimeZone getTimeZone() {
			return JRFillChart.this.getTimeZone();
		}
	}
}
