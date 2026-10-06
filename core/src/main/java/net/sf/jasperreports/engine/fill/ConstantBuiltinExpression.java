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
package net.sf.jasperreports.engine.fill;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;

/**
 * Builtin expression that evaluates to a constant value.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ConstantBuiltinExpression implements BuiltinExpressionEvaluator
{

	private final Object value;

	/**
	 * Creates an expression evaluator for a specified value.
	 * 
	 * @param value the value
	 */
	public ConstantBuiltinExpression(Object value)
	{
		this.value = value;
	}
	
	@Override
	public void init(Map<String, JRFillParameter> parametersMap,
			Map<String, JRFillField> fieldsMap, 
			Map<String, JRFillVariable> variablesMap,
			WhenResourceMissingTypeEnum resourceMissingType) throws JRException
	{
		// NOP
	}

	@Override
	public Object evaluate(DatasetExpressionEvaluator evaluator) throws JRExpressionEvalException
	{
		return value;
	}

	@Override
	public Object evaluateOld(DatasetExpressionEvaluator evaluator) throws JRExpressionEvalException
	{
		return value;
	}

	@Override
	public Object evaluateEstimated(DatasetExpressionEvaluator evaluator) throws JRExpressionEvalException
	{
		return value;
	}

}
