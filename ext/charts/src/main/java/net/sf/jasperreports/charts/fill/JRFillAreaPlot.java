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

import net.sf.jasperreports.charts.JRAreaPlot;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRFillAreaPlot extends JRFillChartPlot implements JRAreaPlot 
{

	/**
	 *
	 */
	protected JRFont categoryAxisLabelFont;
	protected Color categoryAxisLabelColor;
	protected JRFont categoryAxisTickLabelFont;
	protected Color categoryAxisTickLabelColor;
	protected Color categoryAxisLineColor;

	protected JRFont valueAxisLabelFont;
	protected Color valueAxisLabelColor;
	protected JRFont valueAxisTickLabelFont;
	protected Color valueAxisTickLabelColor;
	protected Color valueAxisLineColor;

	
	/**
	 *
	 */
	public JRFillAreaPlot( JRAreaPlot areaPlot, ChartsFillObjectFactory factory )
	{
		super( areaPlot, factory ); 

		JRFillObjectFactory parentFactory = factory.getParent();
		
		//FIXME should the font use the fill chart?
		categoryAxisLabelFont = parentFactory.getFont(areaPlot.getChart(), areaPlot.getCategoryAxisLabelFont()); 
		categoryAxisLabelColor = areaPlot.getOwnCategoryAxisLabelColor();
		categoryAxisTickLabelFont = parentFactory.getFont(areaPlot.getChart(), areaPlot.getCategoryAxisTickLabelFont());
		categoryAxisTickLabelColor = areaPlot.getOwnCategoryAxisTickLabelColor();
		categoryAxisLineColor = areaPlot.getOwnCategoryAxisLineColor();
		
		valueAxisLabelFont = parentFactory.getFont(areaPlot.getChart(), areaPlot.getValueAxisLabelFont());
		valueAxisLabelColor = areaPlot.getOwnValueAxisLabelColor();
		valueAxisTickLabelFont = parentFactory.getFont(areaPlot.getChart(), areaPlot.getValueAxisTickLabelFont());
		valueAxisTickLabelColor = areaPlot.getOwnValueAxisTickLabelColor();
		valueAxisLineColor = areaPlot.getOwnValueAxisLineColor();
	}
	
	@Override
	public JRExpression getCategoryAxisLabelExpression()
	{
		return ((JRAreaPlot)parent).getCategoryAxisLabelExpression();
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

	/**
	 *
	 */
	public void setCategoryAxisLabelColor(Color color)
	{
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

	/**
	 *
	 */
	public void setCategoryAxisTickLabelColor(Color color)
	{
	}

	@Override
	public String getCategoryAxisTickLabelMask()
	{
		return ((JRAreaPlot)parent).getCategoryAxisTickLabelMask();
	}

	@Override
	public Boolean getCategoryAxisVerticalTickLabels()
	{
		return ((JRAreaPlot)parent).getCategoryAxisVerticalTickLabels();
	}

	@Override
	public Double getCategoryAxisTickLabelRotation()
	{
		return ((JRAreaPlot)parent).getCategoryAxisTickLabelRotation();
	}

	@Override
	public void setCategoryAxisTickLabelRotation(Double labelRotation)
	{
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

	/**
	 *
	 */
	public void setCategoryAxisLineColor(Color color)
	{
	}

	@Override
	public JRExpression getValueAxisLabelExpression()
	{
		return ((JRAreaPlot)parent).getValueAxisLabelExpression();
	}

	@Override
	public JRExpression getDomainAxisMinValueExpression()
	{
		return ((JRAreaPlot)parent).getDomainAxisMinValueExpression();
	}

	@Override
	public JRExpression getDomainAxisMaxValueExpression()
	{
		return ((JRAreaPlot)parent).getDomainAxisMaxValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMinValueExpression()
	{
		return ((JRAreaPlot)parent).getRangeAxisMinValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMaxValueExpression()
	{
		return ((JRAreaPlot)parent).getRangeAxisMaxValueExpression();
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

	/**
	 *
	 */
	public void setValueAxisLabelColor(Color color)
	{
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

	/**
	 *
	 */
	public void setValueAxisTickLabelColor(Color color)
	{
	}

	@Override
	public String getValueAxisTickLabelMask()
	{
		return ((JRAreaPlot)parent).getValueAxisTickLabelMask();
	}

	@Override
	public Boolean getValueAxisVerticalTickLabels()
	{
		return ((JRAreaPlot)parent).getValueAxisVerticalTickLabels();
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

	/**
	 *
	 */
	public void setValueAxisLineColor(Color color)
	{
	}
}
