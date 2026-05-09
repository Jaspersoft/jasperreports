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
package net.sf.jasperreports.components.spiderchart;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.charts.ChartsExtensionsRegistryFactory;
import net.sf.jasperreports.charts.base.ChartsBaseObjectFactory;
import net.sf.jasperreports.components.charts.ChartComponent;
import net.sf.jasperreports.components.charts.ChartDataset;
import net.sf.jasperreports.components.charts.ChartPlot;
import net.sf.jasperreports.components.charts.ChartSettings;
import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.BaseComponentContext;
import net.sf.jasperreports.engine.component.ComponentContext;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"evaluationTime",
	"evaluationGroup",
	"chartSettings",
	"dataset",
	"plot"
	})
@JsonTypeName(ChartsExtensionsRegistryFactory.SPIDERCHART_COMPONENT_NAME)
public class SpiderChartComponent implements ChartComponent, JRChangeEventsSupport, JRCloneable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_CHART_SETTINGS = "chartSettings";
	
	public static final String PROPERTY_DATASET = "dataset";
	
	public static final String PROPERTY_PLOT = "plot";
	
	public static final String PROPERTY_EVALUATION_TIME = "evaluationTime";
	
	public static final String PROPERTY_EVALUATION_GROUP = "evaluationGroup";
	
	private EvaluationTimeEnum evaluationTime;
	private String evaluationGroup;
	
	private ChartSettings chartSettings;
	private SpiderDataset dataset;
	private SpiderPlot plot;
	private ComponentContext context;
	
	public SpiderChartComponent()
	{
	}

	protected SpiderChartComponent(SpiderChartComponent chartComponent, JRBaseObjectFactory baseFactory)
	{
		this.evaluationTime = chartComponent.getEvaluationTime();
		this.evaluationGroup = chartComponent.getEvaluationGroup();
		this.context = new BaseComponentContext(chartComponent.getContext(), baseFactory);
		
		this.chartSettings = new StandardChartSettings(chartComponent.getChartSettings(), baseFactory);//FIXMENOW check use of constructor here
		this.dataset = new StandardSpiderDataset((SpiderDataset)chartComponent.getDataset(), new ChartsBaseObjectFactory(baseFactory));
		this.plot = new StandardSpiderPlot((SpiderPlot)chartComponent.getPlot(), baseFactory);
		
	}

	/**
	 * @return the chart
	 */
	@Override
	public ChartSettings getChartSettings() {
		return chartSettings;
	}

	/**
	 * @param chartSettings the chart to set
	 */
	public void setChartSettings(ChartSettings chartSettings) {
		Object old = this.chartSettings;
		this.chartSettings = chartSettings;
		getEventSupport().firePropertyChange(PROPERTY_CHART_SETTINGS, old, this.chartSettings);
	}

	/**
	 * @return the dataset
	 */
	@Override
	public ChartDataset getDataset() {
		return this.dataset;
	}


	/**
	 * @return the plot
	 */
	@Override
	public ChartPlot getPlot() {
		return this.plot;
	}

	@JsonDeserialize(as = StandardSpiderDataset.class)
	public void setDataset(ChartDataset dataset) {
		Object old = this.dataset;
		this.dataset = (SpiderDataset)dataset;
		getEventSupport().firePropertyChange(PROPERTY_DATASET, old, this.dataset);
	}

	@JsonDeserialize(as = StandardSpiderPlot.class)
	public void setPlot(ChartPlot plot) {
		Object old = this.plot;
		this.plot = (SpiderPlot)plot;
		getEventSupport().firePropertyChange(PROPERTY_PLOT, old, this.plot);
	}

	/**
	 * @return the evaluationTime
	 */
	@Override
	public EvaluationTimeEnum getEvaluationTime() {
		return evaluationTime;
	}

	/**
	 * @param evaluationTime the evaluationTime to set
	 */
	public void setEvaluationTime(EvaluationTimeEnum evaluationTime) {
		Object old = this.evaluationTime;
		this.evaluationTime = evaluationTime;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_TIME, old, this.evaluationTime);
	}

	/**
	 * @return the evaluationGroup
	 */
	@Override
	public String getEvaluationGroup() {
		return evaluationGroup;
	}

	/**
	 * @param evaluationGroup the evaluationGroup to set
	 */
	public void setEvaluationGroup(String evaluationGroup) {
		Object old = this.evaluationGroup;
		this.evaluationGroup = evaluationGroup;
		getEventSupport().firePropertyChange(PROPERTY_EVALUATION_GROUP, old, this.evaluationGroup);
	}

	@Override
	public Object clone()
	{
		SpiderChartComponent clone = null;
		
		try
		{
			clone = (SpiderChartComponent)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		clone.chartSettings = JRCloneUtils.nullSafeClone(chartSettings);
		clone.dataset = JRCloneUtils.nullSafeClone(dataset);
		clone.plot = JRCloneUtils.nullSafeClone(plot);
		clone.eventSupport = null;
		
		return clone;
	}
	
	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}
	
	@Override
	public ComponentContext getContext() {
		return context;
	}

	@Override
	public void setContext(ComponentContext context) {
		this.context = context;
	}

}
