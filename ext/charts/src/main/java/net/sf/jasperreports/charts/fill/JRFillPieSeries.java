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

import net.sf.jasperreports.charts.JRPieSeries;
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
public class JRFillPieSeries implements JRPieSeries
{

	/**
	 *
	 */
	protected JRPieSeries parent;

	private Comparable<?> key;
	private Number value;
	private String label;
	private JRPrintHyperlink sectionHyperlink;
	
	
	/**
	 *
	 */
	public JRFillPieSeries(JRPieSeries pieSeries, ChartsFillObjectFactory factory)
	{
		factory.getParent().put(pieSeries, this);

		parent = pieSeries;
	}


	@Override
	public JRExpression getKeyExpression()
	{
		return parent.getKeyExpression();
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
	protected Comparable<?> getKey()
	{
		return key;
	}
		
	/**
	 *
	 */
	protected Number getValue()
	{
		return value;
	}
		
	/**
	 *
	 */
	protected String getLabel()
	{
		return label;
	}
	
	/**
	 *
	 */
	protected JRPrintHyperlink getPrintSectionHyperlink()
	{
		return sectionHyperlink;
	}
	
	
	/**
	 *
	 */
	protected void evaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		key = (Comparable<?>)calculator.evaluate(getKeyExpression()); 
		value = (Number)calculator.evaluate(getValueExpression());
		label = (String)calculator.evaluate(getLabelExpression());
		
		if (hasSectionHyperlinks())
		{
			evaluateSectionHyperlink(calculator);
		}
	}


	protected void evaluateSectionHyperlink(JRCalculator calculator) throws JRExpressionEvalException
	{
		try
		{
			sectionHyperlink = JRFillHyperlinkHelper.evaluateHyperlink(getSectionHyperlink(), calculator, JRExpression.EVALUATION_DEFAULT);
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

	
	public boolean hasSectionHyperlinks()
	{
		return !JRHyperlinkHelper.isEmpty(getSectionHyperlink()); 
	}


	@Override
	public JRHyperlink getSectionHyperlink()
	{
		return parent.getSectionHyperlink();
	}

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
