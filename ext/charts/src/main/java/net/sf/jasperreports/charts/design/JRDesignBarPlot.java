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
import net.sf.jasperreports.charts.JRItemLabel;
import net.sf.jasperreports.charts.base.JRBaseBarPlot;
import net.sf.jasperreports.charts.util.JRAxisFormat;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignBarPlot extends JRBaseBarPlot implements JRDesignCategoryPlot
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_CATEGORY_AXIS_LABEL_COLOR = "categoryAxisLabelColor";
	
	public static final String PROPERTY_CATEGORY_AXIS_LABEL_FONT = "categoryAxisLabelFont";
	
	public static final String PROPERTY_CATEGORY_AXIS_LINE_COLOR = "categoryAxisLineColor";
	
	public static final String PROPERTY_CATEGORY_AXIS_TICK_LABEL_COLOR = "categoryAxisTickLabelColor";
	
	public static final String PROPERTY_CATEGORY_AXIS_TICK_LABEL_FONT = "categoryAxisTickLabelFont";
	
	public static final String PROPERTY_CATEGORY_AXIS_TICK_LABEL_MASK = "categoryAxisTickLabelMask";
	
	public static final String PROPERTY_CATEGORY_AXIS_VERTICAL_TICK_LABELS = "categoryAxisVerticalTickLabels";
	
	public static final String PROPERTY_VALUE_AXIS_LABEL_COLOR = "valueAxisLabelColor";
	
	public static final String PROPERTY_VALUE_AXIS_LABEL_FONT = "valueAxisLabelFont";
	
	public static final String PROPERTY_VALUE_AXIS_LINE_COLOR = "valueAxisLineColor";
	
	public static final String PROPERTY_VALUE_AXIS_TICK_LABEL_COLOR = "valueAxisTickLabelColor";
	
	public static final String PROPERTY_VALUE_AXIS_TICK_LABEL_FONT = "valueAxisTickLabelFont";
	
	public static final String PROPERTY_VALUE_AXIS_TICK_LABEL_MASK = "valueAxisTickLabelMask";
	
	public static final String PROPERTY_VALUE_AXIS_VERTICAL_TICK_LABELS = "valueAxisVerticalTickLabels";

	public static final String PROPERTY_ITEM_LABEL = "itemLabel";

	
	@JsonCreator
	private JRDesignBarPlot()
	{
		this(null, null);
	}


	/**
	 *
	 */
	public JRDesignBarPlot(JRChartPlot plot, JRChart chart)
	{
		super(plot, chart, ChartCopyDesignObjectFactory.instance());
	}


	@Override
	public void setCategoryAxisLabelExpression(JRExpression categoryAxisLabelExpression)
	{
		Object old = this.categoryAxisLabelExpression;
		this.categoryAxisLabelExpression = categoryAxisLabelExpression;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_LABEL_EXPRESSION, old, this.categoryAxisLabelExpression);
	}

	/**
	 *
	 */
	public void setCategoryAxisLabelFont(JRFont categoryAxisLabelFont)
	{
		Object old = this.categoryAxisLabelFont;
		this.categoryAxisLabelFont = categoryAxisLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_LABEL_FONT, old, this.categoryAxisLabelFont);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setCategoryAxisLabelColor(Color categoryAxisLabelColor)
	{
		Object old = this.categoryAxisLabelColor;
		this.categoryAxisLabelColor = categoryAxisLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_LABEL_COLOR, old, this.categoryAxisLabelColor);
	}

	/**
	 *
	 */
	public void setCategoryAxisTickLabelFont(JRFont categoryAxisTickLabelFont)
	{
		Object old = this.categoryAxisTickLabelFont;
		this.categoryAxisTickLabelFont = categoryAxisTickLabelFont;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_TICK_LABEL_FONT, old, this.categoryAxisTickLabelFont);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setCategoryAxisTickLabelColor(Color categoryAxisTickLabelColor)
	{
		Object old = this.categoryAxisTickLabelColor;
		this.categoryAxisTickLabelColor = categoryAxisTickLabelColor;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_TICK_LABEL_COLOR, old, this.categoryAxisTickLabelColor);
	}

	/**
	 *
	 */
	public void setCategoryAxisTickLabelMask(String categoryAxisTickLabelMask)
	{
		Object old = this.categoryAxisTickLabelMask;
		this.categoryAxisTickLabelMask = categoryAxisTickLabelMask;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_TICK_LABEL_MASK, old, this.categoryAxisTickLabelMask);
	}

	/**
	 *
	 */
	public void setCategoryAxisVerticalTickLabels(Boolean categoryAxisVerticalTickLabels)
	{
		Object old = this.categoryAxisVerticalTickLabels;
		this.categoryAxisVerticalTickLabels = categoryAxisVerticalTickLabels;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_VERTICAL_TICK_LABELS, old, this.categoryAxisVerticalTickLabels);
	}

	/**
	 *
	 */
	@JsonSetter
	public void setCategoryAxisLineColor(Color categoryAxisLineColor)
	{
		Object old = this.categoryAxisLineColor;
		this.categoryAxisLineColor = categoryAxisLineColor;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_LINE_COLOR, old, this.categoryAxisLineColor);
	}

	@Override
	public void setValueAxisLabelExpression(JRExpression valueAxisLabelExpression)
	{
		Object old = this.valueAxisLabelExpression;
		this.valueAxisLabelExpression = valueAxisLabelExpression;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_LABEL_EXPRESSION, old, this.valueAxisLabelExpression);
	}

	@Override
	public void setDomainAxisMinValueExpression(JRExpression domainAxisMinValueExpression)
	{
		Object old = this.domainAxisMinValueExpression;
		this.domainAxisMinValueExpression = domainAxisMinValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_DOMAIN_AXIS_MINVALUE_EXPRESSION, old, this.domainAxisMinValueExpression);
	}

	@Override
	public void setDomainAxisMaxValueExpression(JRExpression domainAxisMaxValueExpression)
	{
		Object old = this.domainAxisMaxValueExpression;
		this.domainAxisMaxValueExpression = domainAxisMaxValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_DOMAIN_AXIS_MAXVALUE_EXPRESSION, old, this.domainAxisMaxValueExpression);
	}

	@Override
	public void setRangeAxisMinValueExpression(JRExpression rangeAxisMinValueExpression)
	{
		Object old = this.rangeAxisMinValueExpression;
		this.rangeAxisMinValueExpression = rangeAxisMinValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_RANGE_AXIS_MINVALUE_EXPRESSION, old, this.rangeAxisMinValueExpression);
	}

	@Override
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
	@JsonSetter
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
	@JsonSetter
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
	@JsonSetter
	public void setValueAxisLineColor(Color valueAxisLineColor)
	{
		Object old = this.valueAxisLineColor;
		this.valueAxisLineColor = valueAxisLineColor;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_AXIS_LINE_COLOR, old, this.valueAxisLineColor);
	}
	
	/**
	 *
	 */
	public void setItemLabel(JRItemLabel itemLabel)
	{
		Object old = this.itemLabel;
		this.itemLabel = itemLabel;
		getEventSupport().firePropertyChange(PROPERTY_ITEM_LABEL, old, this.itemLabel);
	}
	
	@Override
	public void setCategoryAxisFormat(JRAxisFormat axisFormat)
	{
		setCategoryAxisLabelFont(axisFormat.getLabelFont());
		setCategoryAxisLabelColor(axisFormat.getLabelColor());
		setCategoryAxisTickLabelFont(axisFormat.getTickLabelFont());
		setCategoryAxisTickLabelColor(axisFormat.getTickLabelColor());
		setCategoryAxisTickLabelMask(axisFormat.getTickLabelMask());
		setCategoryAxisVerticalTickLabels(axisFormat.getVerticalTickLabels());
		setCategoryAxisLineColor(axisFormat.getLineColor());
	}

	@Override
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
