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
package net.sf.jasperreports.charts.fill;

import java.awt.Color;

import net.sf.jasperreports.charts.JRScatterPlot;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillScatterPlot extends JRFillChartPlot implements JRScatterPlot {
	
	/**
	 *
	 */
	protected JRFont xAxisLabelFont;
	protected Color xAxisLabelColor;
	protected JRFont xAxisTickLabelFont;
	protected Color xAxisTickLabelColor;
	protected Color xAxisLineColor;

	protected JRFont yAxisLabelFont;
	protected Color yAxisLabelColor;
	protected JRFont yAxisTickLabelFont;
	protected Color yAxisTickLabelColor;
	protected Color yAxisLineColor;

	
	/**
	 *
	 */
	public JRFillScatterPlot( JRScatterPlot plot, ChartsFillObjectFactory factory )
	{
		super( plot, factory );
		
		JRFillObjectFactory parentFactory = factory.getParent();

		xAxisLabelFont = parentFactory.getFont(plot.getChart(), plot.getXAxisLabelFont());
		xAxisLabelColor = plot.getOwnXAxisLabelColor();
		xAxisTickLabelFont = parentFactory.getFont(plot.getChart(), plot.getXAxisTickLabelFont());
		xAxisTickLabelColor = plot.getOwnXAxisTickLabelColor();
		xAxisLineColor = plot.getOwnXAxisLineColor();
		
		yAxisLabelFont = parentFactory.getFont(plot.getChart(), plot.getYAxisLabelFont());
		yAxisLabelColor = plot.getOwnYAxisLabelColor();
		yAxisTickLabelFont = parentFactory.getFont(plot.getChart(), plot.getYAxisTickLabelFont());
		yAxisTickLabelColor = plot.getOwnYAxisTickLabelColor();
		yAxisLineColor = plot.getOwnYAxisLineColor();
	}
	
	@Override
	public JRExpression getXAxisLabelExpression()
	{
		return ((JRScatterPlot)parent).getXAxisLabelExpression();
	}

	@Override
	public JRFont getXAxisLabelFont()
	{
		return xAxisLabelFont;
	}
	
	@Override
	public Color getXAxisLabelColor()
	{
		return getStyleResolver().getXAxisLabelColor(this, this);
	}
		
	@Override
	public Color getOwnXAxisLabelColor()
	{
		return xAxisLabelColor;
	}
		
	@Override
	public JRFont getXAxisTickLabelFont()
	{
		return xAxisTickLabelFont;
	}
	
	@Override
	public Color getXAxisTickLabelColor()
	{
		return getStyleResolver().getXAxisTickLabelColor(this, this);
	}

	@Override
	public Color getOwnXAxisTickLabelColor()
	{
		return xAxisTickLabelColor;
	}

	@Override
	public String getXAxisTickLabelMask()
	{
		return ((JRScatterPlot)parent).getXAxisTickLabelMask();
	}

	@Override
	public Boolean getXAxisVerticalTickLabels()
	{
		return ((JRScatterPlot)parent).getXAxisVerticalTickLabels();
	}

	@Override
	public Color getXAxisLineColor()
	{
		return getStyleResolver().getXAxisLineColor(this, this);
	}

	@Override
	public Color getOwnXAxisLineColor()
	{
		return xAxisLineColor;
	}

	@Override
	public JRExpression getYAxisLabelExpression()
	{
		return ((JRScatterPlot)parent).getYAxisLabelExpression();
	}

	@Override
	public JRFont getYAxisLabelFont()
	{
		return yAxisLabelFont;
	}
	
	@Override
	public Color getYAxisLabelColor()
	{
		return getStyleResolver().getYAxisLabelColor(this, this);
	}
	
	@Override
	public Color getOwnYAxisLabelColor()
	{
		return yAxisLabelColor;
	}
	
	@Override
	public JRFont getYAxisTickLabelFont()
	{
		return yAxisTickLabelFont;
	}
	
	@Override
	public Color getYAxisTickLabelColor()
	{
		return getStyleResolver().getYAxisTickLabelColor(this, this);
	}
	
	@Override
	public Color getOwnYAxisTickLabelColor()
	{
		return yAxisTickLabelColor;
	}
	
	@Override
	public String getYAxisTickLabelMask()
	{
		return ((JRScatterPlot)parent).getYAxisTickLabelMask();
	}

	@Override
	public Boolean getYAxisVerticalTickLabels()
	{
		return ((JRScatterPlot)parent).getYAxisVerticalTickLabels();
	}

	@Override
	public Color getYAxisLineColor()
	{
		return getStyleResolver().getYAxisLineColor(this, this);
	}
	
	@Override
	public Color getOwnYAxisLineColor()
	{
		return yAxisLineColor;
	}
	
	@Override
	public Boolean getShowShapes(){
		return ((JRScatterPlot)parent).getShowShapes();
	}
	
	@Override
	public void setShowShapes( Boolean value ){
	}
	
	@Override
	public Boolean getShowLines(){
		return ((JRScatterPlot)parent).getShowLines();
	}
	
	@Override
	public void setShowLines( Boolean value ){
	}
	
	@Override
	public JRExpression getDomainAxisMinValueExpression()
	{
		return ((JRScatterPlot)parent).getDomainAxisMinValueExpression();
	}

	@Override
	public JRExpression getDomainAxisMaxValueExpression()
	{
		return ((JRScatterPlot)parent).getDomainAxisMaxValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMinValueExpression()
	{
		return ((JRScatterPlot)parent).getRangeAxisMinValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMaxValueExpression()
	{
		return ((JRScatterPlot)parent).getRangeAxisMaxValueExpression();
	}

}
