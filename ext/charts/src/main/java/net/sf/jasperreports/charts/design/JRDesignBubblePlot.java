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
import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.base.JRBaseBubblePlot;
import net.sf.jasperreports.charts.util.JRAxisFormat;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRDesignBubblePlot extends JRBaseBubblePlot {
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_X_AXIS_LABEL_COLOR = "xAxisLabelColor";
	
	public static final String PROPERTY_X_AXIS_LABEL_EXPRESSION = "xAxisLabelExpression";
	
	public static final String PROPERTY_X_AXIS_LABEL_FONT = "xAxisLabelFont";
	
	public static final String PROPERTY_X_AXIS_LINE_COLOR = "xAxisLineColor";
	
	public static final String PROPERTY_X_AXIS_TICK_LABEL_COLOR = "xAxisTickLabelColor";
	
	public static final String PROPERTY_X_AXIS_TICK_LABEL_FONT = "xAxisTickLabelFont";
	
	public static final String PROPERTY_X_AXIS_TICK_LABEL_MASK = "xAxisTickLabelMask";
	
	public static final String PROPERTY_X_AXIS_VERTICAL_TICK_LABELS = "xAxisVerticalTickLabels";
	
	public static final String PROPERTY_Y_AXIS_LABEL_COLOR = "yAxisLabelColor";
	
	public static final String PROPERTY_Y_AXIS_LABEL_EXPRESSION = "yAxisLabelExpression";
	
	public static final String PROPERTY_Y_AXIS_LABEL_FONT = "yAxisLabelFont";
	
	public static final String PROPERTY_Y_AXIS_LINE_COLOR = "yAxisLineColor";
	
	public static final String PROPERTY_Y_AXIS_TICK_LABEL_COLOR = "yAxisTickLabelColor";
	
	public static final String PROPERTY_Y_AXIS_TICK_LABEL_FONT = "yAxisTickLabelFont";
	
	public static final String PROPERTY_Y_AXIS_TICK_LABEL_MASK = "yAxisTickLabelMask";

	public static final String PROPERTY_Y_AXIS_VERTICAL_TICK_LABELS = "yAxisVerticalTickLabels";
	
	public static final String PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION = "domainAxisMinValueExpression";
	
	public static final String PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION = "domainAxisMaxValueExpression";
	
	public static final String PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION = "rangeAxisMinValueExpression";
	
	public static final String PROPERTY_RANGE_AXIS_MAXVALUE_EXPRESSION = "rangeAxisMaxValueExpression";
	

	@JsonCreator
	private JRDesignBubblePlot()
	{
		this(null, null);
	}


	/**
	 *
	 */
	public JRDesignBubblePlot(JRChartPlot plot, JRChart chart)
	{
		super(plot, chart);
	}


	/**
	 *
	 */
	public void setXAxisLabelExpression( JRExpression xAxisLabelExpression ){
		Object old = this.xAxisLabelExpression;
		this.xAxisLabelExpression = xAxisLabelExpression;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_LABEL_EXPRESSION, old, this.xAxisLabelExpression);
	}

	/**
	 *
	 */
	public void setXAxisLabelFont(JRFont xAxisLabelFont)
	{
		Object old = this.xAxisLabelFont;
		this.xAxisLabelFont = xAxisLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_LABEL_FONT, old, this.xAxisLabelFont);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setXAxisLabelColor(Color xAxisLabelColor)
	{
		Object old = this.xAxisLabelColor;
		this.xAxisLabelColor = xAxisLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_LABEL_COLOR, old, this.xAxisLabelColor);
	}
	
	/**
	 *
	 */
	public void setXAxisTickLabelFont(JRFont xAxisTickLabelFont)
	{
		Object old = this.xAxisTickLabelFont;
		this.xAxisTickLabelFont = xAxisTickLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_TICK_LABEL_FONT, old, this.xAxisTickLabelFont);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setXAxisTickLabelColor(Color xAxisTickLabelColor)
	{
		Object old = this.xAxisTickLabelColor;
		this.xAxisTickLabelColor = xAxisTickLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_TICK_LABEL_COLOR, old, this.xAxisTickLabelColor);
	}

	/**
	 *
	 */
	public void setXAxisTickLabelMask(String xAxisTickLabelMask)
	{
		Object old = this.xAxisTickLabelMask;
		this.xAxisTickLabelMask = xAxisTickLabelMask;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_TICK_LABEL_MASK, old, this.xAxisTickLabelMask);
	}

	/**
	 *
	 */
	public void setXAxisVerticalTickLabels(Boolean xAxisVerticalTickLabels)
	{
		Object old = this.xAxisVerticalTickLabels;
		this.xAxisVerticalTickLabels = xAxisVerticalTickLabels;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_VERTICAL_TICK_LABELS, old, this.xAxisVerticalTickLabels);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setXAxisLineColor(Color xAxisLineColor)
	{
		Object old = this.xAxisLineColor;
		this.xAxisLineColor = xAxisLineColor;
		getEventSupport().firePropertyChange(PROPERTY_X_AXIS_LINE_COLOR, old, this.xAxisLineColor);
	}

	/**
	 *
	 */
	public void setYAxisLabelExpression( JRExpression yAxisLabelExpression ){
		Object old = this.yAxisLabelExpression;
		this.yAxisLabelExpression = yAxisLabelExpression;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_LABEL_EXPRESSION, old, this.yAxisLabelExpression);
	}

	/**
	 *
	 */
	public void setYAxisLabelFont(JRFont yAxisLabelFont)
	{
		Object old = this.yAxisLabelFont;
		this.yAxisLabelFont = yAxisLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_LABEL_FONT, old, this.yAxisLabelFont);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setYAxisLabelColor(Color yAxisLabelColor)
	{
		Object old = this.yAxisLabelColor;
		this.yAxisLabelColor = yAxisLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_LABEL_COLOR, old, this.yAxisLabelColor);
	}
	
	/**
	 *
	 */
	public void setYAxisTickLabelFont(JRFont yAxisTickLabelFont)
	{
		Object old = this.yAxisTickLabelFont;
		this.yAxisTickLabelFont = yAxisTickLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_TICK_LABEL_FONT, old, this.yAxisTickLabelFont);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setYAxisTickLabelColor(Color yAxisTickLabelColor)
	{
		Object old = this.yAxisTickLabelColor;
		this.yAxisTickLabelColor = yAxisTickLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_TICK_LABEL_COLOR, old, this.yAxisTickLabelColor);
	}

	/**
	 *
	 */
	public void setYAxisTickLabelMask(String yAxisTickLabelMask)
	{
		Object old = this.yAxisTickLabelMask;
		this.yAxisTickLabelMask = yAxisTickLabelMask;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_TICK_LABEL_MASK, old, this.yAxisTickLabelMask);
	}

	/**
	 *
	 */
	public void setYAxisVerticalTickLabels(Boolean yAxisVerticalTickLabels)
	{
		Object old = this.yAxisVerticalTickLabels;
		this.yAxisVerticalTickLabels = yAxisVerticalTickLabels;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_VERTICAL_TICK_LABELS, old, this.yAxisVerticalTickLabels);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setYAxisLineColor(Color yAxisLineColor)
	{
		Object old = this.yAxisLineColor;
		this.yAxisLineColor = yAxisLineColor;
		getEventSupport().firePropertyChange(PROPERTY_Y_AXIS_LINE_COLOR, old, this.yAxisLineColor);
	}
	
	/**
	 * 
	 */
	public void setXAxisFormat(JRAxisFormat axisFormat)
	{
		setXAxisLabelColor(axisFormat.getLabelColor());
		setXAxisLabelFont(axisFormat.getLabelFont());
		setXAxisTickLabelFont(axisFormat.getTickLabelFont());
		setXAxisTickLabelColor(axisFormat.getTickLabelColor());
		setXAxisTickLabelMask(axisFormat.getTickLabelMask());
		setXAxisVerticalTickLabels(axisFormat.getVerticalTickLabels());
		setXAxisLineColor(axisFormat.getLineColor());
	}

	/**
	 * 
	 */
	public void setYAxisFormat(JRAxisFormat axisFormat)
	{
		setYAxisLabelColor(axisFormat.getLabelColor());
		setYAxisLabelFont(axisFormat.getLabelFont());
		setYAxisTickLabelFont(axisFormat.getTickLabelFont());
		setYAxisTickLabelColor(axisFormat.getTickLabelColor());
		setYAxisTickLabelMask(axisFormat.getTickLabelMask());
		setYAxisVerticalTickLabels(axisFormat.getVerticalTickLabels());
		setYAxisLineColor(axisFormat.getLineColor());
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

}
