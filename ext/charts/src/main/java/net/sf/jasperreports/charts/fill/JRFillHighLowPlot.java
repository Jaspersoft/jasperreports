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

import net.sf.jasperreports.charts.JRHighLowPlot;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;


/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
public class JRFillHighLowPlot extends JRFillChartPlot implements JRHighLowPlot
{

	/**
	 *
	 */
	protected JRFont timeAxisLabelFont;
	protected Color timeAxisLabelColor;
	protected JRFont timeAxisTickLabelFont;
	protected Color timeAxisTickLabelColor;
	protected Color timeAxisLineColor;

	protected JRFont valueAxisLabelFont;
	protected Color valueAxisLabelColor;
	protected JRFont valueAxisTickLabelFont;
	protected Color valueAxisTickLabelColor;
	protected Color valueAxisLineColor;

	
	/**
	 *
	 */
	public JRFillHighLowPlot(JRHighLowPlot highLowPlot, ChartsFillObjectFactory factory)
	{
		super(highLowPlot, factory);

		JRFillObjectFactory parentFactory = factory.getParent();

		timeAxisLabelFont = parentFactory.getFont(highLowPlot.getChart(), highLowPlot.getTimeAxisLabelFont());
		timeAxisLabelColor = highLowPlot.getOwnTimeAxisLabelColor();
		timeAxisTickLabelFont = parentFactory.getFont(highLowPlot.getChart(), highLowPlot.getTimeAxisTickLabelFont());
		timeAxisTickLabelColor = highLowPlot.getOwnTimeAxisTickLabelColor();
		timeAxisLineColor = highLowPlot.getOwnTimeAxisLineColor();
		
		valueAxisLabelFont = parentFactory.getFont(highLowPlot.getChart(), highLowPlot.getValueAxisLabelFont());
		valueAxisLabelColor = highLowPlot.getOwnValueAxisLabelColor();
		valueAxisTickLabelFont = parentFactory.getFont(highLowPlot.getChart(), highLowPlot.getValueAxisTickLabelFont());
		valueAxisTickLabelColor = highLowPlot.getOwnValueAxisTickLabelColor();
		valueAxisLineColor = highLowPlot.getOwnValueAxisTickLabelColor();
	}

	@Override
	public JRExpression getTimeAxisLabelExpression()
	{
		return ((JRHighLowPlot)parent).getTimeAxisLabelExpression();
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

	/**
	 *
	 */
	public void setTimeAxisLabelColor(Color color)
	{
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

	/**
	 *
	 */
	public void setTimeAxisTickLabelColor(Color color)
	{
	}

	@Override
	public String getTimeAxisTickLabelMask()
	{
		return ((JRHighLowPlot)parent).getTimeAxisTickLabelMask();
	}

	@Override
	public Boolean getTimeAxisVerticalTickLabels()
	{
		return ((JRHighLowPlot)parent).getTimeAxisVerticalTickLabels();
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

	/**
	 *
	 */
	public void setTimeAxisLineColor(Color color)
	{
	}

	@Override
	public JRExpression getValueAxisLabelExpression()
	{
		return ((JRHighLowPlot)parent).getValueAxisLabelExpression();
	}

	@Override
	public JRExpression getDomainAxisMinValueExpression()
	{
		return ((JRHighLowPlot)parent).getDomainAxisMinValueExpression();
	}

	@Override
	public JRExpression getDomainAxisMaxValueExpression()
	{
		return ((JRHighLowPlot)parent).getDomainAxisMaxValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMinValueExpression()
	{
		return ((JRHighLowPlot)parent).getRangeAxisMinValueExpression();
	}

	@Override
	public JRExpression getRangeAxisMaxValueExpression()
	{
		return ((JRHighLowPlot)parent).getRangeAxisMaxValueExpression();
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
		return ((JRHighLowPlot)parent).getValueAxisTickLabelMask();
	}

	@Override
	public Boolean getValueAxisVerticalTickLabels()
	{
		return ((JRHighLowPlot)parent).getValueAxisVerticalTickLabels();
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
	
	@Override
	public Boolean getShowOpenTicks()
	{
		return ((JRHighLowPlot)parent).getShowOpenTicks();
	}

	@Override
	public Boolean getShowCloseTicks()
	{
		return ((JRHighLowPlot)parent).getShowCloseTicks();
	}
}
