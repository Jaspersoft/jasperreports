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
package net.sf.jasperreports.engine.base;

import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * Base implementation of {@link net.sf.jasperreports.engine.ExpressionReturnValue ExpressionReturnValue}.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class BaseExpressionReturnValue extends BaseCommonReturnValue implements ExpressionReturnValue
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	protected JRExpression expression;

	protected BaseExpressionReturnValue()
	{
	}

	
	protected BaseExpressionReturnValue(ExpressionReturnValue returnValue, JRBaseObjectFactory factory)
	{
		super(returnValue, factory);

		expression = factory.getExpression(returnValue.getExpression());
	}

	/**
	 * The expression producing the value to return.
	 */
	@Override
	public JRExpression getExpression()
	{
		return expression;
	}

	@Override
	public Object clone() 
	{
		BaseExpressionReturnValue clone = (BaseExpressionReturnValue)super.clone();
		clone.expression = JRCloneUtils.nullSafeClone(expression);
		return clone;
	}
}
