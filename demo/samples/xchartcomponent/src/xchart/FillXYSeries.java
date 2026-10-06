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
package xchart;

import java.awt.Color;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.util.JRColorUtil;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FillXYSeries implements XYSeries
{

	/**
	 *
	 */
	protected XYSeries parent;

	private Comparable<?> series;
	private Number xValue;
	private Number yValue;
	private Color color;
	
	/**
	 *
	 */
	public FillXYSeries(
		XYSeries xySeries, 
		JRFillObjectFactory factory
		)
	{
		factory.put(xySeries, this);

		parent = xySeries;
	}


	@Override
	public JRExpression getSeriesExpression()
	{
		return parent.getSeriesExpression();
	}
		
	@Override
	public JRExpression getXValueExpression()
	{
		return parent.getXValueExpression();
	}
		
	@Override
	public JRExpression getYValueExpression()
	{
		return parent.getYValueExpression();
	}
	
	@Override
	public JRExpression getColorExpression()
	{
		return parent.getColorExpression();
	}
		
	
	/**
	 *
	 */
	protected Comparable<?> getSeries()
	{
		return series;
	}
		
	/**
	 *
	 */
	protected Number getXValue()
	{
		return xValue;
	}
		
	/**
	 *
	 */
	protected Number getYValue()
	{
		return yValue;
	}
	
	/**
	 *
	 */
	protected Color getColor()
	{
		return color;
	}
		
	
	/**
	 *
	 */
	protected void evaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		series = (Comparable<?>)calculator.evaluate(getSeriesExpression()); 
		xValue = (Number)calculator.evaluate(getXValueExpression()); 
		yValue = (Number)calculator.evaluate(getYValueExpression());
		color = JRColorUtil.getColor((String)calculator.evaluate(getColorExpression()), null);
	}

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
