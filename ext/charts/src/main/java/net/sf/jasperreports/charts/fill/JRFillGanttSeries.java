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

import java.util.Date;

import net.sf.jasperreports.charts.JRGanttSeries;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRCalculator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JRFillHyperlinkHelper;

/**
 * @author Peter Risko (peter@risko.hu)
 */
public class JRFillGanttSeries implements JRGanttSeries {

	/**
	 *
	 */
	protected JRGanttSeries parent;

	private Comparable<?> series;
	private String task;
	private String subtask;
	private Date startDate;
	private Date endDate;
	private Number percent;
	private String label;
	private JRPrintHyperlink itemHyperlink;


	/**
	 *
	 */
	public JRFillGanttSeries(JRGanttSeries ganttSeries, ChartsFillObjectFactory factory)
	{
		factory.getParent().put(ganttSeries, this);

		parent = ganttSeries;
	}


	@Override
	public JRExpression getSeriesExpression()
	{
		return parent.getSeriesExpression();
	}

	@Override
	public JRExpression getStartDateExpression(){
		return parent.getStartDateExpression();
	}

	@Override
	public JRExpression getEndDateExpression(){
		return parent.getEndDateExpression();
	}

	@Override
	public JRExpression getTaskExpression()
	{
		return parent.getTaskExpression();
	}

	@Override
	public JRExpression getSubtaskExpression()
	{
		return parent.getSubtaskExpression();
	}

	@Override
	public JRExpression getPercentExpression()
	{
		return parent.getPercentExpression();
	}

	@Override
	public JRExpression getLabelExpression()
	{
		return parent.getLabelExpression();
	}


	/**
	 *
	 */
	protected Comparable<?> getSeries()
	{
		return series;
	}

	protected Date getStartDate(){
		return startDate;
	}

	protected Date getEndDate(){
		return endDate;
	}

	/**
	 *
	 */
	protected String getTask()
	{
		return task;
	}

	/**
	 *
	 */
	protected String getSubtask()
	{
		return subtask;
	}

	/**
	 *
	 */
	protected Number getPercent()
	{
		return percent;
	}

	/**
	 *
	 */
	protected String getLabel()
	{
		return label;
	}

	protected JRPrintHyperlink getPrintItemHyperlink()
	{
		return itemHyperlink;
	}


	/**
	 *
	 */
	protected void evaluate(JRCalculator calculator) throws JRExpressionEvalException
	{
		series = (Comparable<?>)calculator.evaluate(getSeriesExpression());
		startDate = (Date)calculator.evaluate( getStartDateExpression() );
		endDate = (Date)calculator.evaluate( getEndDateExpression() );
		task = (String)calculator.evaluate(getTaskExpression());
		subtask = (String)calculator.evaluate(getSubtaskExpression());
		percent = (Number)calculator.evaluate(getPercentExpression());
		label = (String)calculator.evaluate(getLabelExpression());

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
		return getItemHyperlink() != null;
	}



}
