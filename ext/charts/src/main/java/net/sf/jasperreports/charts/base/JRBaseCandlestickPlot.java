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
package net.sf.jasperreports.charts.base;

import java.awt.Color;

import net.sf.jasperreports.charts.ChartsExpressionCollector;
import net.sf.jasperreports.charts.JRCandlestickPlot;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRBaseCandlestickPlot extends JRBaseChartPlot implements JRCandlestickPlot
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_SHOW_VOLUME = "isShowVolume";

	protected JRExpression timeAxisLabelExpression;
	protected JRFont timeAxisLabelFont;
	protected Color timeAxisLabelColor;
	protected JRFont timeAxisTickLabelFont;
	protected Color timeAxisTickLabelColor;
	protected String timeAxisTickLabelMask;
	protected Boolean timeAxisVerticalTickLabels;
	protected Color timeAxisLineColor;

	protected JRExpression valueAxisLabelExpression;
	protected JRExpression rangeAxisMinValueExpression;
	protected JRExpression rangeAxisMaxValueExpression;
	protected JRExpression domainAxisMinValueExpression;
	protected JRExpression domainAxisMaxValueExpression;
	protected JRFont valueAxisLabelFont;
	protected Color valueAxisLabelColor;
	protected JRFont valueAxisTickLabelFont;
	protected Color valueAxisTickLabelColor;
	protected String valueAxisTickLabelMask;
	protected Boolean valueAxisVerticalTickLabels;
	protected Color valueAxisLineColor;

	protected Boolean showVolume;

	/**
	 *
	 */
	protected JRBaseCandlestickPlot(JRChartPlot plot, JRChart chart)
	{
		super(plot, chart);
		
		JRCandlestickPlot candlestickPlot = plot instanceof JRCandlestickPlot ? (JRCandlestickPlot)plot : null;
		if (candlestickPlot != null)
		{
			timeAxisLabelFont = candlestickPlot.getTimeAxisLabelFont();
			timeAxisTickLabelFont = candlestickPlot.getTimeAxisTickLabelFont();
			valueAxisLabelFont = candlestickPlot.getValueAxisLabelFont();
			valueAxisTickLabelFont = candlestickPlot.getValueAxisTickLabelFont();
		}
	}


	/**
	 *
	 */
	public JRBaseCandlestickPlot(JRCandlestickPlot candlestickPlot, ChartsBaseObjectFactory factory)
	{
		super(candlestickPlot, factory);

		JRBaseObjectFactory parentFactory = factory.getParent();

		showVolume = candlestickPlot.getShowVolume();

		timeAxisLabelExpression = parentFactory.getExpression( candlestickPlot.getTimeAxisLabelExpression() );
		timeAxisLabelFont = parentFactory.getFont(chart, candlestickPlot.getTimeAxisLabelFont());
		timeAxisLabelColor = candlestickPlot.getOwnTimeAxisLabelColor();
		timeAxisTickLabelFont = parentFactory.getFont(chart, candlestickPlot.getTimeAxisTickLabelFont());
		timeAxisTickLabelColor = candlestickPlot.getOwnTimeAxisTickLabelColor();
		timeAxisTickLabelMask = candlestickPlot.getTimeAxisTickLabelMask();
		timeAxisVerticalTickLabels = candlestickPlot.getTimeAxisVerticalTickLabels();
		timeAxisLineColor = candlestickPlot.getOwnTimeAxisLineColor();
		
		valueAxisLabelExpression = parentFactory.getExpression( candlestickPlot.getValueAxisLabelExpression() );
		domainAxisMinValueExpression = parentFactory.getExpression( candlestickPlot.getDomainAxisMinValueExpression() );
		domainAxisMaxValueExpression = parentFactory.getExpression( candlestickPlot.getDomainAxisMaxValueExpression() );
		rangeAxisMinValueExpression = parentFactory.getExpression( candlestickPlot.getRangeAxisMinValueExpression() );
		rangeAxisMaxValueExpression = parentFactory.getExpression( candlestickPlot.getRangeAxisMaxValueExpression() );
		valueAxisLabelFont = parentFactory.getFont(chart, candlestickPlot.getValueAxisLabelFont());
		valueAxisLabelColor = candlestickPlot.getOwnValueAxisLabelColor();
		valueAxisTickLabelFont = parentFactory.getFont(chart, candlestickPlot.getValueAxisTickLabelFont());
		valueAxisTickLabelColor = candlestickPlot.getOwnValueAxisTickLabelColor();
		valueAxisTickLabelMask = candlestickPlot.getValueAxisTickLabelMask();
		valueAxisVerticalTickLabels = candlestickPlot.getValueAxisVerticalTickLabels();
		valueAxisLineColor = candlestickPlot.getOwnValueAxisTickLabelColor();
	}


	@Override
	public JRExpression getTimeAxisLabelExpression(){
		return timeAxisLabelExpression;
	}
	
	@Override
	public JRFont getTimeAxisLabelFont()
	{
		return timeAxisLabelFont;
	}
	
	@Override
	public Color getTimeAxisLabelColor()
	{
		return getStyleResolver().getTimeAxisLabelColor(this, this);
	}
	
	@Override
	public Color getOwnTimeAxisLabelColor()
	{
		return timeAxisLabelColor;
	}
	
	@Override
	public JRFont getTimeAxisTickLabelFont()
	{
		return timeAxisTickLabelFont;
	}
	
	@Override
	public Color getTimeAxisTickLabelColor()
	{
		return getStyleResolver().getTimeAxisTickLabelColor(this, this);
	}

	@Override
	public Color getOwnTimeAxisTickLabelColor()
	{
		return timeAxisTickLabelColor;
	}

	@Override
	public String getTimeAxisTickLabelMask()
	{
		return timeAxisTickLabelMask;
	}

	@Override
	public Boolean getTimeAxisVerticalTickLabels()
	{
		return timeAxisVerticalTickLabels;
	}

	@Override
	public Color getTimeAxisLineColor()
	{
		return getStyleResolver().getTimeAxisLineColor(this, this);
	}

	@Override
	public Color getOwnTimeAxisLineColor()
	{
		return timeAxisLineColor;
	}

	@Override
	public JRExpression getValueAxisLabelExpression(){
		return valueAxisLabelExpression;
	}

	@Override
	public JRExpression getDomainAxisMinValueExpression(){
		return domainAxisMinValueExpression;
	}

	@Override
	public JRExpression getDomainAxisMaxValueExpression(){
		return domainAxisMaxValueExpression;
	}

	@Override
	public JRExpression getRangeAxisMinValueExpression(){
		return rangeAxisMinValueExpression;
	}

	@Override
	public JRExpression getRangeAxisMaxValueExpression(){
		return rangeAxisMaxValueExpression;
	}

	@Override
	public JRFont getValueAxisLabelFont()
	{
		return valueAxisLabelFont;
	}
	
	@Override
	public Color getValueAxisLabelColor()
	{
		return getStyleResolver().getValueAxisLabelColor(this, this);
	}
	
	@Override
	public Color getOwnValueAxisLabelColor()
	{
		return valueAxisLabelColor;
	}
	
	@Override
	public JRFont getValueAxisTickLabelFont()
	{
		return valueAxisTickLabelFont;
	}
	
	@Override
	public Color getValueAxisTickLabelColor()
	{
		return getStyleResolver().getValueAxisTickLabelColor(this, this);
	}

	@Override
	public Color getOwnValueAxisTickLabelColor()
	{
		return valueAxisTickLabelColor;
	}

	@Override
	public String getValueAxisTickLabelMask()
	{
		return valueAxisTickLabelMask;
	}

	@Override
	public Boolean getValueAxisVerticalTickLabels()
	{
		return valueAxisVerticalTickLabels;
	}

	@Override
	public Color getValueAxisLineColor()
	{
		return getStyleResolver().getValueAxisLineColor(this, this);
	}
	
	@Override
	public Color getOwnValueAxisLineColor()
	{
		return valueAxisLineColor;
	}
	
	@Override
	public Boolean getShowVolume()
	{
		return showVolume;
	}


	/**
	 * 
	 */
	public void setShowVolume(Boolean showVolume)
	{
		Boolean old = this.showVolume;
		this.showVolume = showVolume;
		getEventSupport().firePropertyChange(PROPERTY_SHOW_VOLUME, old, this.showVolume);
	}

	@Override
	public void collectExpressions(ChartsExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public Object clone(JRChart parentChart) 
	{
		JRBaseCandlestickPlot clone = (JRBaseCandlestickPlot)super.clone(parentChart);
		clone.timeAxisLabelExpression = JRCloneUtils.nullSafeClone(timeAxisLabelExpression);
		clone.valueAxisLabelExpression = JRCloneUtils.nullSafeClone(valueAxisLabelExpression);
		clone.domainAxisMinValueExpression = JRCloneUtils.nullSafeClone(domainAxisMinValueExpression);
		clone.domainAxisMaxValueExpression = JRCloneUtils.nullSafeClone(domainAxisMaxValueExpression);
		clone.rangeAxisMinValueExpression = JRCloneUtils.nullSafeClone(rangeAxisMinValueExpression);
		clone.rangeAxisMaxValueExpression = JRCloneUtils.nullSafeClone(rangeAxisMaxValueExpression);
		return clone;
	}
}
