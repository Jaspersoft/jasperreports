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
package xchart;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * 
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
@JsonTypeName("XYChart")
public class XYChartComponent implements Component, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private EvaluationTimeEnum evaluationTime;
	private String evaluationGroup;
	
	private JRExpression chartTitleExpression;
	private JRExpression xAxisTitleExpression;
	private JRExpression yAxisTitleExpression;
	private XYDataset dataset;

	public XYChartComponent()
	{
	}

	protected XYChartComponent(XYChartComponent chart, JRBaseObjectFactory baseFactory)
	{
		this.evaluationTime = chart.getEvaluationTime();
		this.evaluationGroup = chart.getEvaluationGroup();
		
		this.chartTitleExpression = baseFactory.getExpression(chart.getChartTitleExpression());
		this.xAxisTitleExpression = baseFactory.getExpression(chart.getXAxisTitleExpression());
		this.yAxisTitleExpression = baseFactory.getExpression(chart.getYAxisTitleExpression());
		this.dataset = new CompiledXYDataset(chart.getDataset(), baseFactory);
	}
	
	public XYDataset getDataset()
	{
		return dataset;
	}

	public void setDataset(XYDataset dataset)
	{
		this.dataset = dataset;
	}
	
	@JacksonXmlProperty(isAttribute = true)
	public EvaluationTimeEnum getEvaluationTime()
	{
		return evaluationTime;
	}

	public void setEvaluationTime(EvaluationTimeEnum evaluationTime)
	{
		this.evaluationTime = evaluationTime;
	}

	@JacksonXmlProperty(isAttribute = true)
	public String getEvaluationGroup()
	{
		return evaluationGroup;
	}

	public void setEvaluationGroup(String evaluationGroup)
	{
		this.evaluationGroup = evaluationGroup;
	}
	
	public JRExpression getChartTitleExpression()
	{
		return chartTitleExpression;
	}
	
	public void setChartTitleExpression(JRExpression chartTitleExpression)
	{
		this.chartTitleExpression = chartTitleExpression;
	}
	
	public JRExpression getXAxisTitleExpression()
	{
		return xAxisTitleExpression;
	}
	
	public void setXAxisTitleExpression(JRExpression xAxisTitleExpression)
	{
		this.xAxisTitleExpression = xAxisTitleExpression;
	}
	
	public JRExpression getYAxisTitleExpression()
	{
		return yAxisTitleExpression;
	}

	public void setYAxisTitleExpression(JRExpression yAxisTitleExpression)
	{
		this.yAxisTitleExpression = yAxisTitleExpression;
	}

}
