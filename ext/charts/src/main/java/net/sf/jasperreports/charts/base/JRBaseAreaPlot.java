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
import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.charts.JRChart;
import net.sf.jasperreports.charts.JRChartPlot;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRBaseAreaPlot extends JRBaseChartPlot implements JRAreaPlot 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	protected JRExpression categoryAxisLabelExpression;
	protected JRFont categoryAxisLabelFont;
	protected Color categoryAxisLabelColor;
	protected JRFont categoryAxisTickLabelFont;
	protected Color categoryAxisTickLabelColor;
	protected String categoryAxisTickLabelMask;
	protected Boolean categoryAxisVerticalTickLabels;
	protected Color categoryAxisLineColor;

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
	

	/**
	 * 
	 */
	public JRBaseAreaPlot(JRChartPlot plot, JRChart chart)
	{
		super(plot, chart);
		
		JRAreaPlot areaPlot = plot instanceof JRAreaPlot ? (JRAreaPlot)plot : null;
		if (areaPlot != null)//FIXMECHART make a common interface and try copy props that are common to different plots
		{
			categoryAxisLabelFont = areaPlot.getCategoryAxisLabelFont(); 
			categoryAxisTickLabelFont = areaPlot.getCategoryAxisTickLabelFont();
			valueAxisLabelFont = areaPlot.getValueAxisLabelFont();
			valueAxisTickLabelFont = areaPlot.getValueAxisTickLabelFont();
		}
	}


	/**
	 * 
	 */
	public JRBaseAreaPlot( JRAreaPlot areaPlot, ChartsBaseObjectFactory factory )
	{
		super( areaPlot, factory );
		
		JRBaseObjectFactory parentFactory = factory.getParent();

		categoryAxisLabelExpression = parentFactory.getExpression( areaPlot.getCategoryAxisLabelExpression() );
		categoryAxisLabelFont = parentFactory.getFont(chart, areaPlot.getCategoryAxisLabelFont()); 
		categoryAxisLabelColor = areaPlot.getOwnCategoryAxisLabelColor();
		categoryAxisTickLabelFont = parentFactory.getFont(chart, areaPlot.getCategoryAxisTickLabelFont());
		categoryAxisTickLabelColor = areaPlot.getOwnCategoryAxisTickLabelColor();
		categoryAxisTickLabelMask = areaPlot.getCategoryAxisTickLabelMask();
		categoryAxisVerticalTickLabels = areaPlot.getCategoryAxisVerticalTickLabels();
		categoryAxisLineColor = areaPlot.getOwnCategoryAxisLineColor();
		labelRotation = areaPlot.getCategoryAxisTickLabelRotation();
		
		valueAxisLabelExpression = parentFactory.getExpression( areaPlot.getValueAxisLabelExpression() );
		domainAxisMinValueExpression = parentFactory.getExpression( areaPlot.getDomainAxisMinValueExpression() );
		domainAxisMaxValueExpression = parentFactory.getExpression( areaPlot.getDomainAxisMaxValueExpression() );
		rangeAxisMinValueExpression = parentFactory.getExpression( areaPlot.getRangeAxisMinValueExpression() );
		rangeAxisMaxValueExpression = parentFactory.getExpression( areaPlot.getRangeAxisMaxValueExpression() );
		valueAxisLabelFont = parentFactory.getFont(chart, areaPlot.getValueAxisLabelFont());
		valueAxisLabelColor = areaPlot.getOwnValueAxisLabelColor();
		valueAxisTickLabelFont = parentFactory.getFont(chart, areaPlot.getValueAxisTickLabelFont());
		valueAxisTickLabelColor = areaPlot.getOwnValueAxisTickLabelColor();
		valueAxisTickLabelMask = areaPlot.getValueAxisTickLabelMask();
		valueAxisVerticalTickLabels = areaPlot.getValueAxisVerticalTickLabels();
		valueAxisLineColor = areaPlot.getOwnValueAxisLineColor();
	}
	
	@Override
	public JRExpression getCategoryAxisLabelExpression(){
		return categoryAxisLabelExpression;
	}
	
	@Override
	public JRFont getCategoryAxisLabelFont()
	{
		return categoryAxisLabelFont;
	}
	
	@Override
	public Color getCategoryAxisLabelColor()
	{
		return getStyleResolver().getCategoryAxisLabelColor(this, this);
	}
	
	@Override
	public Color getOwnCategoryAxisLabelColor()
	{
		return categoryAxisLabelColor;
	}
	
	@Override
	public JRFont getCategoryAxisTickLabelFont()
	{
		return categoryAxisTickLabelFont;
	}
	
	@Override
	public Color getCategoryAxisTickLabelColor()
	{
		return getStyleResolver().getCategoryAxisTickLabelColor(this, this);
	}

	@Override
	public Color getOwnCategoryAxisTickLabelColor()
	{
		return categoryAxisTickLabelColor;
	}

	@Override
	public String getCategoryAxisTickLabelMask()
	{
		return categoryAxisTickLabelMask;
	}

	@Override
	public Boolean getCategoryAxisVerticalTickLabels()
	{
		return categoryAxisVerticalTickLabels;
	}

	@Override
	public Double getCategoryAxisTickLabelRotation()
	{
		return labelRotation;
	}

	@Override
	public void setCategoryAxisTickLabelRotation(Double labelRotation)
	{
		Object old = this.labelRotation;
		this.labelRotation = labelRotation;
		getEventSupport().firePropertyChange(PROPERTY_CATEGORY_AXIS_TICK_LABEL_ROTATION, old, this.labelRotation);
	}

	@Override
	public Color getCategoryAxisLineColor()
	{
		return getStyleResolver().getCategoryAxisLineColor(this, this);
	}
		
	@Override
	public Color getOwnCategoryAxisLineColor()
	{
		return categoryAxisLineColor;
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
	public void collectExpressions(ChartsExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public Object clone(JRChart parentChart) 
	{
		JRBaseAreaPlot clone = (JRBaseAreaPlot)super.clone(parentChart);
		clone.categoryAxisLabelExpression = JRCloneUtils.nullSafeClone(categoryAxisLabelExpression);
		clone.valueAxisLabelExpression = JRCloneUtils.nullSafeClone(valueAxisLabelExpression);
		clone.domainAxisMinValueExpression = JRCloneUtils.nullSafeClone(domainAxisMinValueExpression);
		clone.domainAxisMaxValueExpression = JRCloneUtils.nullSafeClone(domainAxisMaxValueExpression);
		clone.rangeAxisMinValueExpression = JRCloneUtils.nullSafeClone(rangeAxisMinValueExpression);
		clone.rangeAxisMaxValueExpression = JRCloneUtils.nullSafeClone(rangeAxisMaxValueExpression);
		return clone;
	}
}
