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
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.charts.JRTimeSeriesPlot;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRBaseTimeSeriesPlot extends JRBaseChartPlot implements JRTimeSeriesPlot 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_SHOW_LINES = "isShowLines";
	
	public static final String PROPERTY_SHOW_SHAPES = "isShowShapes";
	
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
	
	Boolean showShapes;
	Boolean showLines;
	
	/**
	 * 
	 */
	protected JRBaseTimeSeriesPlot(JRChartPlot plot, JRChart chart)
	{
		super(plot, chart);
		
		JRTimeSeriesPlot timeSeriesPlot = plot instanceof JRTimeSeriesPlot ? (JRTimeSeriesPlot)plot : null;
		if (timeSeriesPlot != null)
		{//FIXMETHEME you could copy more things
			timeAxisLabelFont = timeSeriesPlot.getTimeAxisLabelFont();
			timeAxisTickLabelFont = timeSeriesPlot.getTimeAxisTickLabelFont();
			valueAxisLabelFont = timeSeriesPlot.getValueAxisLabelFont();
			valueAxisTickLabelFont = timeSeriesPlot.getValueAxisTickLabelFont();
		}
	}
	
	/**
	 * 
	 */
	public JRBaseTimeSeriesPlot(JRTimeSeriesPlot plot, ChartsBaseObjectFactory factory)
	{
		super(plot, factory);
		
		JRBaseObjectFactory parentFactory = factory.getParent();

		showLines = plot.getShowLines();
		showShapes = plot.getShowShapes();
		
		timeAxisLabelExpression = parentFactory.getExpression( plot.getTimeAxisLabelExpression() );
		timeAxisLabelFont = parentFactory.getFont(chart, plot.getTimeAxisLabelFont());//FIXMETHEME check this plot.getChart(); don't we get the design chart?
		timeAxisLabelColor = plot.getOwnTimeAxisLabelColor();
		timeAxisTickLabelFont = parentFactory.getFont(chart, plot.getTimeAxisTickLabelFont());
		timeAxisTickLabelColor = plot.getOwnTimeAxisTickLabelColor();
		timeAxisTickLabelMask = plot.getTimeAxisTickLabelMask();
		timeAxisVerticalTickLabels = plot.getTimeAxisVerticalTickLabels();
		timeAxisLineColor = plot.getOwnTimeAxisLineColor();
		
		valueAxisLabelExpression = parentFactory.getExpression( plot.getValueAxisLabelExpression() );
		domainAxisMinValueExpression = parentFactory.getExpression( plot.getDomainAxisMinValueExpression() );
		domainAxisMaxValueExpression = parentFactory.getExpression( plot.getDomainAxisMaxValueExpression() );
		rangeAxisMinValueExpression = parentFactory.getExpression( plot.getRangeAxisMinValueExpression() );
		rangeAxisMaxValueExpression = parentFactory.getExpression( plot.getRangeAxisMaxValueExpression() );
		valueAxisLabelFont = parentFactory.getFont(chart, plot.getValueAxisLabelFont());
		valueAxisLabelColor = plot.getOwnValueAxisLabelColor();
		valueAxisTickLabelFont = parentFactory.getFont(chart, plot.getValueAxisTickLabelFont());
		valueAxisTickLabelColor = plot.getOwnValueAxisTickLabelColor();
		valueAxisTickLabelMask = plot.getValueAxisTickLabelMask();
		valueAxisVerticalTickLabels = plot.getValueAxisVerticalTickLabels();
		valueAxisLineColor = plot.getOwnValueAxisTickLabelColor();
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
	public Boolean getShowLines(){
		return showLines;
	}
	
	@Override
	public Boolean getShowShapes(){
		return showShapes;
	}
	
	@Override
	public void setShowLines( Boolean val ){
		Boolean old = this.showLines;
		this.showLines = val;
		getEventSupport().firePropertyChange(PROPERTY_SHOW_LINES, old, this.showLines);
	}
	
	@Override
	public void setShowShapes( Boolean val ){
		Boolean old = this.showShapes;
		this.showShapes = val;
		getEventSupport().firePropertyChange(PROPERTY_SHOW_SHAPES, old, this.showShapes);
	}

	@Override
	public void collectExpressions(ChartsExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public Object clone(JRChart parentChart) 
	{
		JRBaseTimeSeriesPlot clone = (JRBaseTimeSeriesPlot)super.clone(parentChart);
		clone.timeAxisLabelExpression = JRCloneUtils.nullSafeClone(timeAxisLabelExpression);
		clone.valueAxisLabelExpression = JRCloneUtils.nullSafeClone(valueAxisLabelExpression);
		clone.domainAxisMinValueExpression = JRCloneUtils.nullSafeClone(domainAxisMinValueExpression);
		clone.domainAxisMaxValueExpression = JRCloneUtils.nullSafeClone(domainAxisMaxValueExpression);
		clone.rangeAxisMinValueExpression = JRCloneUtils.nullSafeClone(rangeAxisMinValueExpression);
		clone.rangeAxisMaxValueExpression = JRCloneUtils.nullSafeClone(rangeAxisMaxValueExpression);
		return clone;
	}
}
