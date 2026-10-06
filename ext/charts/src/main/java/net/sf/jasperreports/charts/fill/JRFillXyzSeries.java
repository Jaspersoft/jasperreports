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

import net.sf.jasperreports.charts.JRXyzSeries;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRFillHyperlinkHelper;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRFillXyzSeries implements JRXyzSeries {
	
	JRXyzSeries parent;
	
	private Comparable<?> series;
	private Number xValue;
	private Number yValue;
	private Number zValue;
	private JRPrintHyperlink itemHyperlink;
	
	public JRFillXyzSeries( JRXyzSeries xyzSeries, ChartsFillObjectFactory factory ){
		factory.getParent().put( xyzSeries, this );
		parent = xyzSeries;
	}
	
	@Override
	public JRExpression getSeriesExpression(){
		return parent.getSeriesExpression();
	}
	
	@Override
	public JRExpression getXValueExpression(){
		return parent.getXValueExpression();
	}
	
	@Override
	public JRExpression getYValueExpression(){
		return parent.getYValueExpression();
	}
	
	@Override
	public JRExpression getZValueExpression(){
		return parent.getZValueExpression();
	}
	
	
	protected Comparable<?> getSeries(){
		return series;
	}
	
	protected Number getXValue(){
		return xValue;
	}
	
	protected Number getYValue(){
		return yValue;
	}
	
	protected Number getZValue(){
		return zValue;
	}
	
	protected JRPrintHyperlink getPrintItemHyperlink()
	{
		return itemHyperlink;
	}
	
	protected void evaluate( JRCalculator calculator ) throws JRExpressionEvalException {
		series = (Comparable<?>)calculator.evaluate( getSeriesExpression() );
		xValue = (Number)calculator.evaluate( getXValueExpression() );
		yValue = (Number)calculator.evaluate( getYValueExpression() );
		zValue = (Number)calculator.evaluate( getZValueExpression() );
		
		if (hasItemHyperlinks())
		{
			evaluateItemHyperlink(calculator);
		}
	}

	protected void evaluateItemHyperlink(JRCalculator calculator) throws JRExpressionEvalException
	{
		try
		{
			itemHyperlink = JRFillHyperlinkHelper.evaluateHyperlink(getItemHyperlink(), calculator, JRExpression.EVALUATION_DEFAULT);
		}
		catch (JRExpressionEvalException e)
		{
			throw e;
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	@Override
	public JRHyperlink getItemHyperlink()
	{
		return parent.getItemHyperlink();
	}
	
	public boolean hasItemHyperlinks()
	{
		return !JRHyperlinkHelper.isEmpty(getItemHyperlink()); 
	}

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
