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
package net.sf.jasperreports.groovy;

import org.kohsuke.groovy.sandbox.GroovyInterceptor;

import net.sf.jasperreports.compilers.ReportClassFilter;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class GroovySandboxEvaluator extends GroovyEvaluator
{
	
	private GroovyInterceptor interceptor;

	public void setReportClassFilter(ReportClassFilter reportClassFilter)
	{
		interceptor = new GroovyClassFilter(reportClassFilter, this);
	}
	
	@Override
	public Object evaluate(JRExpression expression) throws JRExpressionEvalException
	{
		startEvaluate();
		try
		{
			return super.evaluate(expression);
		}
		finally
		{
			endEvaluate();
		}
	}

	@Override
	public Object evaluateOld(JRExpression expression) throws JRExpressionEvalException
	{
		startEvaluate();
		try
		{
			return super.evaluateOld(expression);
		}
		finally
		{
			endEvaluate();
		}
	}

	@Override
	public Object evaluateEstimated(JRExpression expression) throws JRExpressionEvalException
	{
		startEvaluate();
		try
		{
			return super.evaluateEstimated(expression);
		}
		finally
		{
			endEvaluate();
		}
	}

	private void startEvaluate()
	{
		//TODO avoid doing this on each expression
		if (interceptor != null)
		{
			interceptor.register();
		}
	}
	
	private void endEvaluate()
	{
		if (interceptor != null)
		{
			interceptor.unregister();
		}
	}	
	
}
