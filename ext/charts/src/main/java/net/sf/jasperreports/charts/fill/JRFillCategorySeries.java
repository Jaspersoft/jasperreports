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

import net.sf.jasperreports.charts.JRCategorySeries;
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
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillCategorySeries implements JRCategorySeries
{

	/**
	 *
	 */
	protected JRCategorySeries parent;

	private Comparable<?> series;
	private Comparable<?> category;
	private Number value;
	private String label;
	private JRPrintHyperlink itemHyperlink;
	
	
	/**
	 *
	 */
	public JRFillCategorySeries(JRCategorySeries categorySeries, ChartsFillObjectFactory factory)
	{
		factory.getParent().put(categorySeries, this);

		parent = categorySeries;
	}


	@Override
	public JRExpression getSeriesExpression()
	{
		return parent.getSeriesExpression();
	}
		
	@Override
	public JRExpression getCategoryExpression()
	{
		return parent.getCategoryExpression();
	}
		
	@Override
	public JRExpression getValueExpression()
	{
		return parent.getValueExpression();
	}
		
	@Override
	public JRExpression getLabelExpression()
	{
		return parent.getLabelExpression();
	}
	
	
	/**
	 *
	 */
	public Comparable<?> getSeries()
	{
		return series;
	}
		
	/**
	 *
	 */
	public Comparable<?> getCategory()
	{
		return category;
	}
		
	/**
	 *
	 */
	public Number getValue()
	{
		return value;
	}
		
	/**
	 *
	 */
	public String getLabel()
	{
		return label;
	}
	
	public JRPrintHyperlink getPrintItemHyperlink()
	{
		return itemHyperlink;
	}
	
	
	/**
	 *
	 */
	public void evaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		series = (Comparable<?>)calculator.evaluate(getSeriesExpression()); 
		category = (Comparable<?>)calculator.evaluate(getCategoryExpression()); 
		value = (Number)calculator.evaluate(getValueExpression());
		label = (String)calculator.evaluate(getLabelExpression());
		
		if (hasItemHyperlinks())
		{
			evaluateItemHyperlink(calculator);
		}
	}


	public void evaluateItemHyperlink(JRCalculator calculator) throws JRExpressionEvalException
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

	
	public boolean hasItemHyperlinks()
	{
		return !JRHyperlinkHelper.isEmpty(getItemHyperlink()); 
	}


	@Override
	public JRHyperlink getItemHyperlink()
	{
		return parent.getItemHyperlink();
	}

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
