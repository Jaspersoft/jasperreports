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
package net.sf.jasperreports.charts.design;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.charts.ChartVisitorFactory;
import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartDataset;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.base.JRBaseChart;
import net.sf.jasperreports.charts.type.ChartTypeEnum;
import net.sf.jasperreports.charts.type.EdgeEnum;
import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignHyperlink;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignChart extends JRDesignElement implements JRChart
{
	public static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_CHART_TYPE = "charts.chart.type.unsupported";

	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/*
	 * Chart properties
	 */
	
	public static final String PROPERTY_ANCHOR_NAME_EXPRESSION = "anchorNameExpression";
	
	public static final String PROPERTY_BOOKMARK_LEVEL = "bookmarkLevel";
	
	public static final String PROPERTY_BOOKMARK_LEVEL_EXPRESSION = "bookmarkLevelExpression";
	
	public static final String PROPERTY_EVALUATION_GROUP = "evaluationGroup";
	
	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";
	
	public static final String PROPERTY_CHART_TYPE = "chartType";
	
	public static final String PROPERTY_CUSTOMIZER_CLASS = "customizerClass";
	
	public static final String PROPERTY_DATASET = "dataset";
	
	public static final String PROPERTY_LEGEND_FONT = "legendFont";
	
	public static final String PROPERTY_SUBTITLE_EXPRESSION = "subtitleExpression";
	
	public static final String PROPERTY_SUBTITLE_FONT = "subtitleFont";
	
	public static final String PROPERTY_TITLE_EXPRESSION = "titleExpression";
	
	public static final String PROPERTY_TITLE_FONT = "titleFont";
	
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
	protected Color titleColor;
	protected Color subtitleColor;
	protected Color legendColor;
	protected Color legendBackgroundColor;
	protected String renderType;
	protected String theme;

	/**
	 *
	 */
	protected JRFont titleFont;
	protected JRFont subtitleFont;
	protected JRFont legendFont;
	protected EdgeEnum legendPosition;
	protected EdgeEnum titlePosition;

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
	private List<JRHyperlinkParameter> hyperlinkParameters;

	protected JRChartDataset dataset;
	@JsonMerge
	protected JRChartPlot plot;

	/**
	 *
	 */
	protected JRLineBox lineBox;
	
	/**
	 * The bookmark level for the anchor associated with this chart.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity


	@JsonCreator
	private JRDesignChart(@JsonProperty("chartType") ChartTypeEnum chartType)
	{
		this(JasperDesign.getThreadInstance(), chartType);
	}

	/**
	 *
	 */
	public JRDesignChart(JRDefaultStyleProvider defaultStyleProvider, ChartTypeEnum chartType)
	{
		super(defaultStyleProvider);
		
		setChartType(chartType);
		
		hyperlinkParameters = new ArrayList<>();
		
		lineBox = new JRBaseLineBox(this);
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
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_SHOW_LEGEND, old, this.showLegend);
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
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_RENDER_TYPE, old, this.renderType);
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
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_THEME, old, this.theme);
	}

	@Override
	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}
		
	/**
	 *
	 */
	public void setEvaluationTime(EvaluationTimeEnum evaluationTime)
	{
		Object old = this.evaluationTime;
		this.evaluationTime = evaluationTime;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME, old, this.evaluationTime);
	}
		
	@Override
	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}
		
	/**
	 *
	 */
	public void setEvaluationGroup(String group)
	{
		Object old = this.evaluationGroup;
		this.evaluationGroup = group;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_GROUP, old, this.evaluationGroup);
	}
		
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	@JsonSetter(JRXmlConstants.ELEMENT_box)
	private void setLineBox(JRLineBox lineBox)
	{
		this.lineBox = lineBox == null ? null : lineBox.clone(this);
	}
	
	@Override
	public JRFont getTitleFont()
	{
		return titleFont;
	}

	/**
	 *
	 */
	public void setTitleFont(JRFont font)//FIXMEFONT embedded fonts should never be null so these font setting methods should be deprecated; check iR impact
	{
		Object old = this.titleFont;
		this.titleFont = font;
		getEventSupport().firePropertyChange(PROPERTY_TITLE_FONT, old, this.titleFont);
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
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_TITLE_POSITION, old, this.titlePosition);
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
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_TITLE_COLOR, old, this.titleColor);
	}

	@Override
	public JRFont getSubtitleFont()
	{
		return subtitleFont;
	}

	/**
	 *
	 */
	public void setSubtitleFont(JRFont font)
	{
		Object old = this.subtitleFont;
		this.subtitleFont = font;
		getEventSupport().firePropertyChange(PROPERTY_SUBTITLE_FONT, old, this.subtitleFont);
	}
	
	@Override
	public Color getSubtitleColor()
	{
		return getChartsStyleResolver().getSubtitleColor(this);
	}

	@Override
	public Color getOwnSubtitleColor()
	{
		return subtitleColor;
	}

	@Override
	public void setSubtitleColor(Color subtitleColor)
	{
		Object old = this.subtitleColor;
		this.subtitleColor = subtitleColor;
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_SUBTITLE_COLOR, old, this.subtitleColor);
	}

	@Override
	public Color getOwnLegendColor()
	{
		return legendColor;
	}

	@Override
	public Color getLegendColor()
	{
		return getChartsStyleResolver().getLegendColor(this);
	}

	@Override
	public void setLegendColor(Color legendColor)
	{
		Object old = this.legendColor;
		this.legendColor = legendColor;
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_LEGEND_COLOR, old, this.legendColor);
	}
	
	@Override
	public Color getOwnLegendBackgroundColor()
	{
		return legendBackgroundColor;
	}

	@Override
	public Color getLegendBackgroundColor()
	{
		return getChartsStyleResolver().getLegendBackgroundColor(this);
	}

	@Override
	public void setLegendBackgroundColor(Color legendBackgroundColor)
	{
		Object old = this.legendBackgroundColor;
		this.legendBackgroundColor = legendBackgroundColor;
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_LEGEND_BACKGROUND_COLOR, old, this.legendBackgroundColor);
	}
	
	@Override
	public JRFont getLegendFont()
	{
		return legendFont;
	}

	/**
	 *
	 */
	public void setLegendFont(JRFont legendFont)
	{
		Object old = this.legendFont;
		this.legendFont = legendFont;
		getEventSupport().firePropertyChange(PROPERTY_LEGEND_FONT, old, this.legendFont);
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
		getEventSupport().firePropertyChange(JRBaseChart.PROPERTY_LEGEND_POSITION, old, this.legendPosition);
	}

	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(this);
	}
		
	/**
	 * Sets the link type as a built-in hyperlink type.
	 * 
	 * @param hyperlinkType the built-in hyperlink type
	 * @see #getLinkType()
	 */
	public void setHyperlinkType(HyperlinkTypeEnum hyperlinkType)
	{
		setLinkType(JRHyperlinkHelper.getLinkType(hyperlinkType));
	}
		
	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(this);
	}
		
	/**
	 *
	 */
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		setLinkTarget(JRHyperlinkHelper.getLinkTarget(hyperlinkTarget));
	}
		
	@Override
	public JRExpression getTitleExpression()
	{
		return titleExpression;
	}

	/**
	 *
	 */
	public void setTitleExpression(JRExpression expression)
	{
		Object old = this.titleExpression;
		this.titleExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_TITLE_EXPRESSION, old, this.titleExpression);
	}

	@Override
	public JRExpression getSubtitleExpression()
	{
		return subtitleExpression;
	}

	/**
	 *
	 */
	public void setSubtitleExpression(JRExpression expression)
	{
		Object old = this.subtitleExpression;
		this.subtitleExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_SUBTITLE_EXPRESSION, old, this.subtitleExpression);
	}

	@Override
	public JRExpression getAnchorNameExpression()
	{
		return anchorNameExpression;
	}

	/**
	 *
	 */
	public void setAnchorNameExpression(JRExpression anchorNameExpression)
	{
		Object old = this.anchorNameExpression;
		this.anchorNameExpression = anchorNameExpression;
		getEventSupport().firePropertyChange(PROPERTY_ANCHOR_NAME_EXPRESSION, old, this.anchorNameExpression);
	}

	@Override
	public JRExpression getBookmarkLevelExpression()
	{
		return bookmarkLevelExpression;
	}

	/**
	 *
	 */
	public void setBookmarkLevelExpression(JRExpression bookmarkLevelExpression)
	{
		Object old = this.bookmarkLevelExpression;
		this.bookmarkLevelExpression = bookmarkLevelExpression;
		getEventSupport().firePropertyChange(PROPERTY_BOOKMARK_LEVEL_EXPRESSION, old, this.bookmarkLevelExpression);
	}

	@Override
	public JRExpression getHyperlinkReferenceExpression()
	{
		return hyperlinkReferenceExpression;
	}

	/**
	 *
	 */
	public void setHyperlinkReferenceExpression(JRExpression hyperlinkReferenceExpression)
	{
		Object old = this.hyperlinkReferenceExpression;
		this.hyperlinkReferenceExpression = hyperlinkReferenceExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_REFERENCE_EXPRESSION, old, this.hyperlinkReferenceExpression);
	}

	@Override
	public JRExpression getHyperlinkWhenExpression()
	{
		return hyperlinkWhenExpression;
	}

	/**
	 *
	 */
	public void setHyperlinkWhenExpression(JRExpression hyperlinkWhenExpression)
	{
		Object old = this.hyperlinkWhenExpression;
		this.hyperlinkWhenExpression = hyperlinkWhenExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_WHEN_EXPRESSION, old, this.hyperlinkWhenExpression);
	}

	@Override
	public JRExpression getHyperlinkAnchorExpression()
	{
		return hyperlinkAnchorExpression;
	}

	/**
	 *
	 */
	public void setHyperlinkAnchorExpression(JRExpression hyperlinkAnchorExpression)
	{
		Object old = this.hyperlinkAnchorExpression;
		this.hyperlinkAnchorExpression = hyperlinkAnchorExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_ANCHOR_EXPRESSION, old, this.hyperlinkAnchorExpression);
	}

	@Override
	public JRExpression getHyperlinkPageExpression()
	{
		return hyperlinkPageExpression;
	}
	
	/**
	 *
	 */
	public void setHyperlinkPageExpression(JRExpression hyperlinkPageExpression)
	{
		Object old = this.hyperlinkPageExpression;
		this.hyperlinkPageExpression = hyperlinkPageExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_PAGE_EXPRESSION, old, this.hyperlinkPageExpression);
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


	/**
	 *
	 */
	public void setChartType(ChartTypeEnum chartType)
	{
		ChartTypeEnum old = this.chartType;
		this.chartType = chartType;
		
		switch(chartType) {
			case AREA:
				dataset = new JRDesignCategoryDataset(dataset);
				plot = new JRDesignAreaPlot(plot, this);
				break;
			case BAR:
				dataset = new JRDesignCategoryDataset(dataset);
				plot = new JRDesignBarPlot(plot, this);
				break;
			case BAR3D:
			{
				dataset = new JRDesignCategoryDataset(dataset);
				@SuppressWarnings("deprecation")
				JRChartPlot depPlot = new JRDesignBar3DPlot(plot, this);
				plot = depPlot;
				break;
			}
			case BUBBLE:
				dataset = new JRDesignXyzDataset(dataset);
				plot = new JRDesignBubblePlot(plot, this);
				break;
			case CANDLESTICK:
				dataset = new JRDesignHighLowDataset(dataset);
				plot = new JRDesignCandlestickPlot(plot, this);
				break;
			case HIGHLOW:
				dataset = new JRDesignHighLowDataset(dataset);
				plot = new JRDesignHighLowPlot(plot, this);
				break;
			case LINE:
				dataset = new JRDesignCategoryDataset(dataset);
				plot = new JRDesignLinePlot(plot, this);
				break;
			case METER:
				dataset = new JRDesignValueDataset(dataset);
				plot = new JRDesignMeterPlot(plot, this);
				break;
			case MULTI_AXIS:
				dataset = null;
				plot = new JRDesignMultiAxisPlot(plot, this);
				break;
			case PIE:
				dataset = new JRDesignPieDataset(dataset);
				plot = new JRDesignPiePlot(plot, this);
				break;
			case PIE3D:
			{
				dataset = new JRDesignPieDataset(dataset);
				@SuppressWarnings("deprecation")
				JRChartPlot depPlot = new JRDesignPie3DPlot(plot, this);
				plot = depPlot;
				break;
			}
			case SCATTER:
				dataset = new JRDesignXyDataset(dataset);
				plot = new JRDesignScatterPlot(plot, this);
				break;
			case STACKEDBAR:
				dataset = new JRDesignCategoryDataset(dataset);
				plot = new JRDesignBarPlot(plot, this);
				break;
			case STACKEDBAR3D:
			{
				dataset = new JRDesignCategoryDataset(dataset);
				@SuppressWarnings("deprecation")
				JRChartPlot depPlot = new JRDesignBar3DPlot(plot, this);
				plot = depPlot;
				break;
			}
			case THERMOMETER:
				dataset = new JRDesignValueDataset(dataset);
				plot = new JRDesignThermometerPlot(plot, this);
				break;
			case TIMESERIES:
				dataset = new JRDesignTimeSeriesDataset(dataset);//other datasets could be supported
				plot = new JRDesignTimeSeriesPlot(plot, this);
				break;
			case XYAREA:
				dataset = new JRDesignXyDataset(dataset);
				plot = new JRDesignAreaPlot(plot, this);
				break;
			case XYBAR:
				plot = new JRDesignBarPlot(plot, this);
				break;
			case XYLINE:
				dataset = new JRDesignXyDataset(dataset);
				plot = new JRDesignLinePlot(plot, this);
				break;
			case STACKEDAREA:
				dataset = new JRDesignCategoryDataset(dataset);
				plot = new JRDesignAreaPlot(plot, this);
				break;
			case GANTT:
				dataset = new JRDesignGanttDataset(dataset);
				plot = new JRDesignBarPlot(plot, this);
				break;
			default:
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNSUPPORTED_CHART_TYPE,
						(Object[])null);
		}

		getEventSupport().firePropertyChange(PROPERTY_CHART_TYPE, old, this.chartType);
	}


	public void setDataset(JRChartDataset ds)
	{
		if (chartType != ChartTypeEnum.MULTI_AXIS) //not really needed, but just in case
		{
			Object old = this.dataset;
			dataset = ds;
			getEventSupport().firePropertyChange(PROPERTY_DATASET, old, this.dataset);		
		}
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


	/**
	 * Sets the boomark level for the anchor associated with this chart.
	 * 
	 * @param bookmarkLevel the bookmark level (starting from 1)
	 * or {@link JRAnchor#NO_BOOKMARK NO_BOOKMARK} if no bookmark should be created
	 */
	public void setBookmarkLevel(int bookmarkLevel)
	{
		int old = this.bookmarkLevel;
		this.bookmarkLevel = bookmarkLevel;
		getEventSupport().firePropertyChange(PROPERTY_BOOKMARK_LEVEL, old, this.bookmarkLevel);
	}

	@Override
	public String getCustomizerClass()
	{
		return customizerClass;
	}

	/**
	 * Sets a user specified chart customizer class name.
	 * @see net.sf.jasperreports.charts.JRChartCustomizer
 	 */
	public void setCustomizerClass(String customizerClass)
	{
		Object old = this.customizerClass;
		this.customizerClass = customizerClass;
		getEventSupport().firePropertyChange(PROPERTY_CUSTOMIZER_CLASS, old, this.customizerClass);
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

	/**
	 * Sets the hyperlink type.
	 * <p>
	 * The type can be one of the built-in types
	 * (Reference, LocalAnchor, LocalPage, RemoteAnchor, RemotePage),
	 * or can be an arbitrary type.
	 * </p>
	 * @param type the hyperlink type
	 */
	public void setLinkType(String type)
	{
		Object old = this.linkType;
		this.linkType = type;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_LINK_TYPE, old, this.linkType);
	}

	@Override
	public String getLinkTarget()
	{
		return linkTarget;
	}


	/**
	 * Sets the hyperlink target.
	 * <p>
	 * The target can be one of the built-in target names
	 * (Self, Blank, Top, Parent),
	 * or can be an arbitrary target name.
	 * </p>
	 * @param target the hyperlink target
	 */
	public void setLinkTarget(String target)
	{
		Object old = this.linkTarget;
		this.linkTarget = target;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_LINK_TARGET, old, this.linkTarget);
	}


	@Override
	public JRHyperlinkParameter[] getHyperlinkParameters()
	{
		JRHyperlinkParameter[] parameters;
		if (hyperlinkParameters.isEmpty())
		{
			parameters = null;
		}
		else
		{
			parameters = new JRHyperlinkParameter[hyperlinkParameters.size()];
			hyperlinkParameters.toArray(parameters);
		}
		return parameters;
	}
	
	
	@JsonSetter
	private void setHyperlinkParameters(List<JRHyperlinkParameter> parameters)
	{
		if (parameters != null)
		{
			for (JRHyperlinkParameter parameter : parameters)
			{
				addHyperlinkParameter(parameter);
			}
		}
	}
	
	
	/**
	 * Returns the list of custom hyperlink parameters.
	 * 
	 * @return the list of custom hyperlink parameters
	 */
	@JsonIgnore
	public List<JRHyperlinkParameter> getHyperlinkParametersList()
	{
		return hyperlinkParameters;
	}
	
	
	/**
	 * Adds a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to add
	 */
	public void addHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		hyperlinkParameters.add(parameter);
		getEventSupport().fireCollectionElementAddedEvent(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS, 
				parameter, hyperlinkParameters.size() - 1);
	}
	

	/**
	 * Removes a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to remove
	 */
	public void removeHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		int idx = hyperlinkParameters.indexOf(parameter);
		if (idx >= 0)
		{
			hyperlinkParameters.remove(idx);
			getEventSupport().fireCollectionElementRemovedEvent(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS, 
					parameter, idx);
		}
	}
	
	
	/**
	 * Removes a custom hyperlink parameter.
	 * <p>
	 * If multiple parameters having the specified name exist, all of them
	 * will be removed
	 * </p>
	 * 
	 * @param parameterName the parameter name
	 */
	public void removeHyperlinkParameter(String parameterName)
	{
		for (ListIterator<JRHyperlinkParameter> it = hyperlinkParameters.listIterator(); it.hasNext();)
		{
			JRHyperlinkParameter parameter = it.next();
			if (parameter.getName() != null && parameter.getName().equals(parameterName))
			{
				it.remove();
				getEventSupport().fireCollectionElementRemovedEvent(JRDesignHyperlink.PROPERTY_HYPERLINK_PARAMETERS, 
						parameter, it.nextIndex());
			}
		}
	}
	
	
	@Override
	public JRExpression getHyperlinkTooltipExpression()
	{
		return hyperlinkTooltipExpression;
	}

	
	/**
	 * Sets the expression which will be used to generate the hyperlink tooltip.
	 * 
	 * @param hyperlinkTooltipExpression the expression which will be used to generate the hyperlink tooltip
	 * @see #getHyperlinkTooltipExpression()
	 */
	public void setHyperlinkTooltipExpression(JRExpression hyperlinkTooltipExpression)
	{
		Object old = this.hyperlinkTooltipExpression;
		this.hyperlinkTooltipExpression = hyperlinkTooltipExpression;
		getEventSupport().firePropertyChange(JRDesignHyperlink.PROPERTY_HYPERLINK_TOOLTIP_EXPRESSION, old, this.hyperlinkTooltipExpression);
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}

	
	@Override
	public Object clone() 
	{
		JRDesignChart clone = (JRDesignChart)super.clone();
		clone.lineBox = lineBox.clone(clone);
		clone.hyperlinkParameters = JRCloneUtils.cloneList(hyperlinkParameters);
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
