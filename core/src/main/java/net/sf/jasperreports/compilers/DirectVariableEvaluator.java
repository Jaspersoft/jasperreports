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
package net.sf.jasperreports.compilers;

import net.sf.jasperreports.engine.fill.JRFillVariable;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DirectVariableEvaluator implements DirectExpressionEvaluator
{

	private final JRFillVariable variable;
	private final DirectExpressionValueFilter valueFilter;
	
	public DirectVariableEvaluator(JRFillVariable variable, DirectExpressionValueFilter valueFilter)
	{
		this.variable = variable;
		this.valueFilter = valueFilter;
	}

	@Override
	public Object evaluate()
	{
		return valueFilter.filterValue(variable.getValue(), variable.getValueClass());
	}

	@Override
	public Object evaluateOld()
	{
		return valueFilter.filterValue(variable.getOldValue(), variable.getValueClass());
	}

	@Override
	public Object evaluateEstimated()
	{
		return valueFilter.filterValue(variable.getEstimatedValue(), variable.getValueClass());
	}
}