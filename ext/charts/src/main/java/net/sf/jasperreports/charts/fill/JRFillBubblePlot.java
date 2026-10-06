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

import net.sf.jasperreports.charts.JRBubblePlot;
import net.sf.jasperreports.charts.type.ScaleTypeEnum;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRFillBubblePlot extends JRFillChartPlot implements JRBubblePlot 
{

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
	public JRFillBubblePlot( JRBubblePlot bubblePlot, ChartsFillObjectFactory factory )
	{
		super( bubblePlot, factory );
		
		JRFillObjectFactory parentFactory = factory.getParent();

		xAxisLabelFont = parentFactory.getFont(bubblePlot.getChart(), bubblePlot.getXAxisLabelFont());
		xAxisLabelColor = bubblePlot.getOwnXAxisLabelColor();
		xAxisTickLabelFont = parentFactory.getFont(bubblePlot.getChart(), bubblePlot.getXAxisTickLabelFont());
		xAxisTickLabelColor = bubblePlot.getOwnXAxisTickLabelColor();
		xAxisLineColor = bubblePlot.getOwnXAxisLineColor();
		
		yAxisLabelFont = parentFactory.getFont(bubblePlot.getChart(), bubblePlot.getYAxisLabelFont());
		yAxisLabelColor = bubblePlot.getOwnYAxisLabelColor();
		yAxisTickLabelFont = parentFactory.getFont(bubblePlot.getChart(), bubblePlot.getYAxisTickLabelFont());
		yAxisTickLabelColor = bubblePlot.getOwnYAxisTickLabelColor();
		yAxisLineColor = bubblePlot.getOwnYAxisLineColor();
	}
	
	@Override
	public JRExpression getXAxisLabelExpression()
	{
		return ((JRBubblePlot)parent).getXAxisLabelExpression();
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
		return ((JRBubblePlot)parent).getXAxisTickLabelMask();
	}

	@Override
	public Boolean getXAxisVerticalTickLabels()
	{
		return ((JRBubblePlot)parent).getXAxisVerticalTickLabels();
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
		return ((JRBubblePlot)parent).getYAxisLabelExpression();
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
		return ((JRBubblePlot)parent).getYAxisTickLabelMask();
	}

	@Override
	public Boolean getYAxisVerticalTickLabels()
	{
		return ((JRBubblePlot)parent).getYAxisVerticalTickLabels();
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
	public ScaleTypeEnum getScaleType()
	{
		return ((JRBubblePlot)parent).getScaleType();
	}
	
	@Override
	public void setScaleType( ScaleTypeEnum scaleType )
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public JRExpression getDomainAxisMinValueExpression()
	{
		return ((JRBubblePlot)parent).getDomainAxisMinValueExpression();
	}

	@Override
	public JRExpression getDomainAxisMaxValueExpression()
	{
		return ((JRBubblePlot)parent).getDomainAxisMaxValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMinValueExpression()
	{
		return ((JRBubblePlot)parent).getRangeAxisMinValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMaxValueExpression()
	{
		return ((JRBubblePlot)parent).getRangeAxisMaxValueExpression();
	}

}
