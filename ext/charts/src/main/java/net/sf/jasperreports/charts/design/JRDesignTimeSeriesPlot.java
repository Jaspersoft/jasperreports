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
package net.sf.jasperreports.charts.design;

import java.awt.Color;

import com.fasterxml.jackson.annotation.JsonCreator;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.base.JRBaseTimeSeriesPlot;
import net.sf.jasperreports.charts.util.JRAxisFormat;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */

public class JRDesignTimeSeriesPlot extends JRBaseTimeSeriesPlot {

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_TIME_AXIS_LABEL_COLOR = "timeAxisLabelColor";
	
	public static final String PROPERTY_TIME_AXIS_LABEL_EXPRESSION = "timeAxisLabelExpression";
	
	public static final String PROPERTY_TIME_AXIS_LABEL_FONT = "timeAxisLabelFont";
	
	public static final String PROPERTY_TIME_AXIS_LINE_COLOR = "timeAxisLineColor";
	
	public static final String PROPERTY_TIME_AXIS_TICK_LABEL_COLOR = "timeAxisTickLabelColor";
	
	public static final String PROPERTY_TIME_AXIS_TICK_LABEL_FONT = "timeAxisTickLabelFont";
	
	public static final String PROPERTY_TIME_AXIS_TICK_LABEL_MASK = "timeAxisTickLabelMask";
	
	public static final String PROPERTY_TIME_AXIS_VERTICAL_TICK_LABELS = "timeAxisVerticalTickLabels";
	
	public static final String PROPERTY_VALUE_AXIS_LABEL_COLOR = "valueAxisLabelColor";
	
	public static final String PROPERTY_VALUE_AXIS_LABEL_EXPRESSION = "valueAxisLabelExpression";
	
	public static final String PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION = "domainAxisMinValueExpression";
	
	public static final String PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION = "domainAxisMaxValueExpression";
	
	public static final String PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION = "rangeAxisMinValueExpression";
	
	public static final String PROPERTY_RANGE_AXIS_MAXVALUE_EXPRESSION = "rangeAxisMaxValueExpression";
	
	public static final String PROPERTY_VALUE_AXIS_LABEL_FONT = "valueAxisLabelFont";
	
	public static final String PROPERTY_VALUE_AXIS_LINE_COLOR = "valueAxisLineColor";
	
	public static final String PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR = "valueAxisTickLabelColor";
	
	public static final String PROPERTY_VALUE_AXIS_TICK_LABEL_FONT = "valueAxisTickLabelFont";
	
	public static final String PROPERTY_VALUE_AXIS_TICK_LABEL_MASK = "valueAxisTickLabelMask";
	
	public static final String PROPERTY_VALUE_AXIS_VERTICAL_TICK_LABELS = "valueAxisVerticalTickLabels";
	
	@JsonCreator
	private JRDesignTimeSeriesPlot()
	{
		this(null, null);
	}
	
	/**
	 *
	 */
	public JRDesignTimeSeriesPlot(JRChartPlot plot, JRChart chart)
	{
		super(plot, chart);
	}
	
	/**
	 *
	 */
	public void setTimeAxisLabelExpression( JRExpression timeAxisLabelExpression ){
		Object old = this.timeAxisLabelExpression;
		this.timeAxisLabelExpression = timeAxisLabelExpression;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_LABEL_EXPRESSION, old, this.timeAxisLabelExpression);
		
	}

	/**
	 *
	 */
	public void setTimeAxisLabelFont(JRFont timeAxisLabelFont)
	{
		Object old = this.timeAxisLabelFont;
		this.timeAxisLabelFont = timeAxisLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_LABEL_FONT, old, this.timeAxisLabelFont);
	}

	/**
	 *
	 */
	public void setTimeAxisLabelColor(Color timeAxisLabelColor)
	{
		Object old = this.timeAxisLabelColor;
		this.timeAxisLabelColor = timeAxisLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_LABEL_COLOR, old, this.timeAxisLabelColor);
	}

	/**
	 *
	 */
	public void setTimeAxisTickLabelFont(JRFont timeAxisTickLabelFont)
	{
		Object old = this.timeAxisTickLabelFont;
		this.timeAxisTickLabelFont = timeAxisTickLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_TICK_LABEL_FONT, old, this.timeAxisTickLabelFont);
	}

	/**
	 *
	 */
	public void setTimeAxisTickLabelColor(Color timeAxisTickLabelColor)
	{
		Object old = this.timeAxisTickLabelColor;
		this.timeAxisTickLabelColor = timeAxisTickLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_TICK_LABEL_COLOR, old, this.timeAxisTickLabelColor);
	}

	/**
	 *
	 */
	public void setTimeAxisTickLabelMask(String timeAxisTickLabelMask)
	{
		Object old = this.timeAxisTickLabelMask;
		this.timeAxisTickLabelMask = timeAxisTickLabelMask;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_TICK_LABEL_MASK, old, this.timeAxisTickLabelMask);
	}

	/**
	 *
	 */
	public void setTimeAxisVerticalTickLabels(Boolean timeAxisVerticalTickLabels)
	{
		Object old = this.timeAxisVerticalTickLabels;
		this.timeAxisVerticalTickLabels = timeAxisVerticalTickLabels;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_VERTICAL_TICK_LABELS, old, this.timeAxisVerticalTickLabels);
	}

	/**
	 *
	 */
	public void setTimeAxisLineColor(Color timeAxisLineColor)
	{
		Object old = this.timeAxisLineColor;
		this.timeAxisLineColor = timeAxisLineColor;
		getEventSupport().firePropertyChange(PROPERTY_TIME_AXIS_LINE_COLOR, old, this.timeAxisLineColor);
	}

	/**
	 *
	 */
	public void setValueAxisLabelExpression(JRExpression valueAxisLabelExpression)
	{
		Object old = this.valueAxisLabelExpression;
		this.valueAxisLabelExpression = valueAxisLabelExpression;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_LABEL_EXPRESSION, old, this.valueAxisLabelExpression);
	}

	/**
	 *
	 */
	public void setDomainAxisMinValueExpression(JRExpression domainAxisMinValueExpression)
	{
		Object old = this.domainAxisMinValueExpression;
		this.domainAxisMinValueExpression = domainAxisMinValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION, old, this.domainAxisMinValueExpression);
	}

	/**
	 *
	 */
	public void setDomainAxisMaxValueExpression(JRExpression domainAxisMaxValueExpression)
	{
		Object old = this.domainAxisMaxValueExpression;
		this.domainAxisMaxValueExpression = domainAxisMaxValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION, old, this.domainAxisMaxValueExpression);
	}

	/**
	 *
	 */
	public void setRangeAxisMinValueExpression(JRExpression rangeAxisMinValueExpression)
	{
		Object old = this.rangeAxisMinValueExpression;
		this.rangeAxisMinValueExpression = rangeAxisMinValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION, old, this.rangeAxisMinValueExpression);
	}

	/**
	 *
	 */
	public void setRangeAxisMaxValueExpression(JRExpression rangeAxisMaxValueExpression)
	{
		Object old = this.rangeAxisMaxValueExpression;
		this.rangeAxisMaxValueExpression = rangeAxisMaxValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_RANGE_AXIS_MAXVALUE_EXPRESSION, old, this.rangeAxisMaxValueExpression);
	}

	/**
	 *
	 */
	public void setValueAxisLabelFont(JRFont valueAxisLabelFont)
	{
		Object old = this.valueAxisLabelFont;
		this.valueAxisLabelFont = valueAxisLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_LABEL_FONT, old, this.valueAxisLabelFont);
	}

	/**
	 *
	 */
	public void setValueAxisLabelColor(Color valueAxisLabelColor)
	{
		Object old = this.valueAxisLabelColor;
		this.valueAxisLabelColor = valueAxisLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_LABEL_COLOR, old, this.valueAxisLabelColor);
	}
	
	/**
	 *
	 */
	public void setValueAxisTickLabelFont(JRFont valueAxisTickLabelFont)
	{
		Object old = this.valueAxisTickLabelFont;
		this.valueAxisTickLabelFont = valueAxisTickLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_TICK_LABEL_FONT, old, this.valueAxisTickLabelFont);
	}

	/**
	 *
	 */
	public void setValueAxisTickLabelColor(Color valueAxisTickLabelColor)
	{
		Object old = this.valueAxisTickLabelColor;
		this.valueAxisTickLabelColor = valueAxisTickLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR, old, this.valueAxisTickLabelColor);
	}

	/**
	 *
	 */
	public void setValueAxisTickLabelMask(String valueAxisTickLabelMask)
	{
		Object old = this.valueAxisTickLabelMask;
		this.valueAxisTickLabelMask = valueAxisTickLabelMask;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_TICK_LABEL_MASK, old, this.valueAxisTickLabelMask);
	}

	/**
	 *
	 */
	public void setValueAxisVerticalTickLabels(Boolean valueAxisVerticalTickLabels)
	{
		Object old = this.valueAxisVerticalTickLabels;
		this.valueAxisVerticalTickLabels = valueAxisVerticalTickLabels;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_VERTICAL_TICK_LABELS, old, this.valueAxisVerticalTickLabels);
	}

	/**
	 *
	 */
	public void setValueAxisLineColor(Color valueAxisLineColor)
	{
		Object old = this.valueAxisLineColor;
		this.valueAxisLineColor = valueAxisLineColor;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_LINE_COLOR, old, this.valueAxisLineColor);
	}

	/**
	 * 
	 */
	public void setTimeAxisFormat(JRAxisFormat axisFormat)
	{
		setTimeAxisLabelFont(axisFormat.getLabelFont());
		setTimeAxisLabelColor(axisFormat.getLabelColor());
		setTimeAxisTickLabelFont(axisFormat.getTickLabelFont());
		setTimeAxisTickLabelColor(axisFormat.getTickLabelColor());
		setTimeAxisTickLabelMask(axisFormat.getTickLabelMask());
		setTimeAxisVerticalTickLabels(axisFormat.getVerticalTickLabels());
		setTimeAxisLineColor(axisFormat.getLineColor());
	}

	/**
	 * 
	 */
	public void setValueAxisFormat(JRAxisFormat axisFormat)
	{
		setValueAxisLabelFont(axisFormat.getLabelFont());
		setValueAxisLabelColor(axisFormat.getLabelColor());
		setValueAxisTickLabelFont(axisFormat.getTickLabelFont());
		setValueAxisTickLabelColor(axisFormat.getTickLabelColor());
		setValueAxisTickLabelMask(axisFormat.getTickLabelMask());
		setValueAxisVerticalTickLabels(axisFormat.getVerticalTickLabels());
		setValueAxisLineColor(axisFormat.getLineColor());
	}
}
