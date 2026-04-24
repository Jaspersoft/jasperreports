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
package net.sf.jasperreports.charts.base;

import java.awt.Color;

import net.sf.jasperreports.charts.ChartVisitorFactory;
import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRBarPlot;
import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRCategoryDataset;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRChartPlot;
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
import net.sf.jasperreports.charts.type.ChartTypeEnum;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.base.JRBaseElement;
import net.sf.jasperreports.engine.base.JRBaseFont;
import net.sf.jasperreports.engine.base.JRBaseHyperlink;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseChart extends JRBaseElement implements JRChart
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String EXCEPTION_MESSAGE_KEY_CHART_TYPE_NOT_SUPPORTED = "charts.chart.type.not.supported";
	
	/*
	 * Chart properties
	 */
	
	public static final String PROPERTY_LEGEND_BACKGROUND_COLOR = "legendBackgroundColor";
	
	public static final String PROPERTY_LEGEND_COLOR = "legendColor";
	
	public static final String PROPERTY_LEGEND_POSITION = "legendPosition";
	
	public static final String PROPERTY_SHOW_LEGEND = "isShowLegend";
	
	public static final String PROPERTY_SUBTITLE_COLOR = "subtitleColor";
	
	public static final String PROPERTY_TITLE_COLOR = "titleColor";
	
	public static final String PROPERTY_TITLE_POSITION = "titlePosition";
	
	public static final String PROPERTY_RENDER_TYPE = "renderType";
	
	public static final String PROPERTY_THEME = "theme";
	
	/**
	 *
	 */
	protected ChartTypeEnum chartType;

	/**
	 *
	 */
	protected Boolean showLegend;
	protected EvaluationTimeEnum evaluationTime;
	protected String linkType;
	protected String linkTarget;
	private JRHyperlinkParameter[] hyperlinkParameters;
	
	protected Color titleColor;
	protected Color subtitleColor;
	protected Color legendColor;
	protected Color legendBackgroundColor;
	protected EdgeEnum legendPosition;
	protected EdgeEnum titlePosition;

	protected String renderType;
	protected String theme;

	/**
	 *
	 */
	protected JRLineBox lineBox;
	protected JRFont titleFont;
	protected JRFont subtitleFont;
	protected JRFont legendFont;

	protected String customizerClass;

	/**
	 *
	 */
	protected String evaluationGroup;
	protected JRExpression titleExpression;
	protected JRExpression subtitleExpression;
	protected JRExpression anchorNameExpression;
	protected JRExpression bookmarkLevelExpression;
	protected JRExpression hyperlinkReferenceExpression;
	protected JRExpression hyperlinkWhenExpression;
	protected JRExpression hyperlinkAnchorExpression;
	protected JRExpression hyperlinkPageExpression;
	private JRExpression hyperlinkTooltipExpression;

	protected JRChartDataset dataset;
	protected JRChartPlot plot;

	/**
	 * The bookmark level for the anchor associated with this chart.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity

	
	/**
	 *
	 */
	protected JRBaseChart(JRChart chart, ChartsBaseObjectFactory factory)
	{
		super(chart, factory.getParent());

		JRBaseObjectFactory parentFactory = factory.getParent();

		chartType = chart.getChartType();

		switch(chartType) {
			case AREA:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case BAR:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			case BAR3D:
			{
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				@SuppressWarnings("deprecation") 
				JRChartPlot depPlot = factory.getBar3DPlot((net.sf.jasperreports.charts.JRBar3DPlot) chart.getPlot());
				plot = depPlot;
				break;
			}
			case BUBBLE:
				dataset = factory.getXyzDataset((JRXyzDataset) chart.getDataset());
				plot = factory.getBubblePlot((JRBubblePlot) chart.getPlot());
				break;
			case CANDLESTICK:
				dataset = factory.getHighLowDataset((JRHighLowDataset) chart.getDataset());
				plot = factory.getCandlestickPlot((JRCandlestickPlot) chart.getPlot());
				break;
			case HIGHLOW:
				dataset = factory.getHighLowDataset((JRHighLowDataset) chart.getDataset());
				plot = factory.getHighLowPlot((JRHighLowPlot) chart.getPlot());
				break;
			case LINE:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getLinePlot((JRLinePlot) chart.getPlot());
				break;
			case METER:
				dataset = factory.getValueDataset((JRValueDataset) chart.getDataset());
				plot = factory.getMeterPlot((JRMeterPlot) chart.getPlot());
				break;
			case MULTI_AXIS:
				dataset = null;
				plot = factory.getMultiAxisPlot((JRMultiAxisPlot) chart.getPlot());
				break;
			case PIE:
				dataset = factory.getPieDataset((JRPieDataset) chart.getDataset());
				plot = factory.getPiePlot((JRPiePlot) chart.getPlot());
				break;
			case PIE3D:
			{
				dataset = factory.getPieDataset((JRPieDataset) chart.getDataset());
				@SuppressWarnings("deprecation") 
				JRChartPlot depPlot = factory.getPie3DPlot((net.sf.jasperreports.charts.JRPie3DPlot) chart.getPlot());
				plot = depPlot;
				break;
			}
			case SCATTER:
				dataset = factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getScatterPlot((JRScatterPlot) chart.getPlot());
				break;
			case STACKEDBAR:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			case STACKEDBAR3D:
			{
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				@SuppressWarnings("deprecation") 
				JRChartPlot depPlot = factory.getBar3DPlot((net.sf.jasperreports.charts.JRBar3DPlot) chart.getPlot());
				plot = depPlot;
				break;
			}
			case THERMOMETER:
				dataset = factory.getValueDataset((JRValueDataset) chart.getDataset());
				plot = factory.getThermometerPlot((JRThermometerPlot) chart.getPlot());
				break;
			case TIMESERIES:
				dataset = factory.getTimeSeriesDataset((JRTimeSeriesDataset)chart.getDataset());
				plot = factory.getTimeSeriesPlot( (JRTimeSeriesPlot)chart.getPlot() );
				break;
			case XYAREA:
				dataset = factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case XYBAR:
				
				switch (chart.getDataset().getDatasetType()){
					case JRChartDataset.TIMESERIES_DATASET:
						dataset = factory.getTimeSeriesDataset((JRTimeSeriesDataset) chart.getDataset());
						break;
					case JRChartDataset.TIMEPERIOD_DATASET:
						dataset = factory.getTimePeriodDataset((JRTimePeriodDataset) chart.getDataset() );
						break;
					case JRChartDataset.XY_DATASET:
						dataset = factory.getXyDataset( (JRXyDataset)chart.getDataset() );
						break;
					default:
				}
				plot = factory.getBarPlot((JRBarPlot)chart.getPlot());
				break;
			case XYLINE:
				dataset = factory.getXyDataset((JRXyDataset) chart.getDataset());
				plot = factory.getLinePlot((JRLinePlot) chart.getPlot());
				break;
			case STACKEDAREA:
				dataset = factory.getCategoryDataset((JRCategoryDataset) chart.getDataset());
				plot = factory.getAreaPlot((JRAreaPlot) chart.getPlot());
				break;
			case GANTT:
				dataset = factory.getGanttDataset((JRGanttDataset) chart.getDataset());
				plot = factory.getBarPlot((JRBarPlot) chart.getPlot());
				break;
			default:
				throw 
					new JRRuntimeException(
						JRBaseChart.EXCEPTION_MESSAGE_KEY_CHART_TYPE_NOT_SUPPORTED,  
						new Object[]{chartType} 
						);
		}

		showLegend = chart.getShowLegend();
		evaluationTime = chart.getEvaluationTime();
		linkType = chart.getLinkType();
		linkTarget = chart.getLinkTarget();
		titlePosition = chart.getTitlePosition();
		titleColor = chart.getOwnTitleColor();
		subtitleColor = chart.getOwnSubtitleColor();
		legendColor = chart.getOwnLegendColor();
		legendBackgroundColor = chart.getOwnLegendBackgroundColor();
		legendPosition = chart.getLegendPosition();
		renderType = chart.getRenderType();
		theme = chart.getTheme();
		
		titleFont = parentFactory.getFont(this, chart.getTitleFont());
		subtitleFont = parentFactory.getFont(this, chart.getSubtitleFont());
		legendFont =  parentFactory.getFont(this, chart.getLegendFont());

		evaluationGroup = chart.getEvaluationGroup();
		titleExpression = parentFactory.getExpression(chart.getTitleExpression());
		subtitleExpression = parentFactory.getExpression(chart.getSubtitleExpression());
		bookmarkLevelExpression = parentFactory.getExpression(chart.getBookmarkLevelExpression());
		anchorNameExpression = parentFactory.getExpression(chart.getAnchorNameExpression());
		hyperlinkReferenceExpression = parentFactory.getExpression(chart.getHyperlinkReferenceExpression());
		hyperlinkWhenExpression = parentFactory.getExpression(chart.getHyperlinkWhenExpression());
		hyperlinkAnchorExpression = parentFactory.getExpression(chart.getHyperlinkAnchorExpression());
		hyperlinkPageExpression = parentFactory.getExpression(chart.getHyperlinkPageExpression());
		hyperlinkTooltipExpression = parentFactory.getExpression(chart.getHyperlinkTooltipExpression());
		bookmarkLevel = chart.getBookmarkLevel();
		hyperlinkParameters = JRBaseHyperlink.copyHyperlinkParameters(chart, parentFactory);

		customizerClass = chart.getCustomizerClass();

		lineBox = chart.getLineBox().clone(this);
	}
		
	@Override
	public Boolean getShowLegend()
	{
		return this.showLegend;
	}

	@Override
	public void setShowLegend(Boolean isShowLegend)
	{
		Boolean old = this.showLegend;
		this.showLegend = isShowLegend;
		getEventSupport().firePropertyChange(PROPERTY_SHOW_LEGEND, old, this.showLegend);
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}
		
	@Override
	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}
		
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	@Override
	public JRFont getTitleFont()
	{
		return titleFont;
	}

	@Override
	public EdgeEnum getTitlePosition()
	{
		return titlePosition;
	}

	@Override
	public void setTitlePosition(EdgeEnum titlePosition)
	{
		EdgeEnum old = this.titlePosition;
		this.titlePosition = titlePosition;
		getEventSupport().firePropertyChange(PROPERTY_TITLE_POSITION, old, this.titlePosition);
	}

	@Override
	public Color getTitleColor()
	{
		return getChartsStyleResolver().getTitleColor(this);
	}

	@Override
	public Color getOwnTitleColor()
	{
		return titleColor;
	}

	@Override
	public void setTitleColor(Color titleColor)
	{
		Object old = this.titleColor;
		this.titleColor = titleColor;
		getEventSupport().firePropertyChange(PROPERTY_TITLE_COLOR, old, this.titleColor);
	}

	@Override
	public JRFont getSubtitleFont()
	{
		return subtitleFont;
	}

	@Override
	public Color getOwnSubtitleColor()
	{
		return subtitleColor;
	}

	@Override
	public Color getSubtitleColor()
	{
		return getChartsStyleResolver().getSubtitleColor(this);
	}

	@Override
	public void setSubtitleColor(Color subtitleColor)
	{
		Object old = this.subtitleColor;
		this.subtitleColor = subtitleColor;
		getEventSupport().firePropertyChange(PROPERTY_SUBTITLE_COLOR, old, this.subtitleColor);
	}

	@Override
	public Color getLegendBackgroundColor() {
		return getChartsStyleResolver().getLegendBackgroundColor(this);
	}

	@Override
	public Color getOwnLegendBackgroundColor() {
		return legendBackgroundColor;
	}


	@Override
	public Color getOwnLegendColor() {
		return legendColor;
	}

	@Override
	public Color getLegendColor() {
		return getChartsStyleResolver().getLegendColor(this);
	}

	@Override
	public JRFont getLegendFont() {
		return legendFont;
	}


	@Override
	public void setLegendBackgroundColor(Color legendBackgroundColor) {
		Object old = this.legendBackgroundColor;
		this.legendBackgroundColor = legendBackgroundColor;
		getEventSupport().firePropertyChange(PROPERTY_LEGEND_BACKGROUND_COLOR, old, this.legendBackgroundColor);
	}


	@Override
	public void setLegendColor(Color legendColor) {
		Object old = this.legendColor;
		this.legendColor = legendColor;
		getEventSupport().firePropertyChange(PROPERTY_LEGEND_COLOR, old, this.legendColor);
	}
	
	@Override
	public EdgeEnum getLegendPosition()
	{
		return legendPosition;
	}

	@Override
	public void setLegendPosition(EdgeEnum legendPosition)
	{
		EdgeEnum old = this.legendPosition;
		this.legendPosition = legendPosition;
		getEventSupport().firePropertyChange(PROPERTY_LEGEND_POSITION, old, this.legendPosition);
	}

	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(this);
	}
		
	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(this);
	}
		
	@Override
	public JRExpression getTitleExpression()
	{
		return titleExpression;
	}

	@Override
	public JRExpression getSubtitleExpression()
	{
		return subtitleExpression;
	}

	@Override
	public JRExpression getBookmarkLevelExpression()
	{
		return bookmarkLevelExpression;
	}
	
	@Override
	public JRExpression getAnchorNameExpression()
	{
		return anchorNameExpression;
	}

	@Override
	public JRExpression getHyperlinkReferenceExpression()
	{
		return hyperlinkReferenceExpression;
	}

	@Override
	public JRExpression getHyperlinkWhenExpression()
	{
		return hyperlinkWhenExpression;
	}

	@Override
	public JRExpression getHyperlinkAnchorExpression()
	{
		return hyperlinkAnchorExpression;
	}

	@Override
	public JRExpression getHyperlinkPageExpression()
	{
		return hyperlinkPageExpression;
	}
	
	@Override
	public JRChartDataset getDataset()
	{
		return chartType == ChartTypeEnum.MULTI_AXIS ? null : dataset; //we do this mostly for the jackson serialization
	}

	@Override
	public JRChartPlot getPlot()
	{
		return plot;
	}

	@Override
	public ChartTypeEnum getChartType()
	{
		return chartType;
	}

	@Override
	public String getRenderType()
	{
		return renderType;
	}

	@Override
	public void setRenderType(String renderType)
	{
		String old = this.renderType;
		this.renderType = renderType;
		getEventSupport().firePropertyChange(PROPERTY_RENDER_TYPE, old, this.renderType);
	}

	@Override
	public String getTheme()
	{
		return theme;
	}

	@Override
	public void setTheme(String theme)
	{
		String old = this.theme;
		this.theme = theme;
		getEventSupport().firePropertyChange(PROPERTY_THEME, old, this.theme);
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


	@Override
	public int getBookmarkLevel()
	{
		return bookmarkLevel;
	}


	@Override
	public String getCustomizerClass()
	{
		return customizerClass;
	}

	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}


	@Override
	public String getLinkType()
	{
		return linkType;
	}
	
	@Override
	public String getLinkTarget()
	{
		return linkTarget;
	}


	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters()
	{
		return hyperlinkParameters;
	}
	
	
	@Override
	public JRExpression getHyperlinkTooltipExpression()
	{
		return hyperlinkTooltipExpression;
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}
	

	@Override
	public Object clone() 
	{
		JRBaseChart clone = (JRBaseChart)super.clone();
		
		clone.lineBox = lineBox.clone(clone);
		clone.hyperlinkParameters = JRCloneUtils.cloneArray(hyperlinkParameters);
		clone.titleFont = JRCloneUtils.nullSafeClone((JRBaseFont)titleFont);
		clone.subtitleFont = JRCloneUtils.nullSafeClone((JRBaseFont)subtitleFont);
		clone.legendFont = JRCloneUtils.nullSafeClone((JRBaseFont)legendFont);

		clone.titleExpression = JRCloneUtils.nullSafeClone(titleExpression);
		clone.subtitleExpression = JRCloneUtils.nullSafeClone(subtitleExpression);
		clone.anchorNameExpression = JRCloneUtils.nullSafeClone(anchorNameExpression);
		clone.bookmarkLevelExpression = JRCloneUtils.nullSafeClone(bookmarkLevelExpression);
		clone.hyperlinkReferenceExpression = JRCloneUtils.nullSafeClone(hyperlinkReferenceExpression);
		clone.hyperlinkWhenExpression = JRCloneUtils.nullSafeClone(hyperlinkWhenExpression);
		clone.hyperlinkAnchorExpression = JRCloneUtils.nullSafeClone(hyperlinkAnchorExpression);
		clone.hyperlinkPageExpression = JRCloneUtils.nullSafeClone(hyperlinkPageExpression);
		clone.hyperlinkTooltipExpression = JRCloneUtils.nullSafeClone(hyperlinkTooltipExpression);
		clone.dataset = JRCloneUtils.nullSafeClone(dataset);
		clone.plot = plot == null ? null : (JRChartPlot) plot.clone(clone);

		return clone;
	}
}
