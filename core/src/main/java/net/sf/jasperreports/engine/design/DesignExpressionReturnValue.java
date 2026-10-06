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
package net.sf.jasperreports.engine.design;

import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * Implementation of {@link net.sf.jasperreports.engine.ExpressionReturnValue ExpressionReturnValue}
 * to be used for report design purposes.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DesignExpressionReturnValue extends DesignCommonReturnValue implements ExpressionReturnValue
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_EXPRESSION = "expression";

	/**
	 * The expression producing the value to copy.
	 */
	protected JRExpression expression;

	/**
	 * Returns the expression whose value should be copied.
	 * 
	 * @return the expression whose value should be copied.
	 */
	@Override
	public JRExpression getExpression()
	{
		return expression;
	}

	/**
	 * Sets the expression.
	 * 
	 * @param expression the expression
	 * @see net.sf.jasperreports.engine.ExpressionReturnValue#getExpression()
	 */
	public void setExpression(JRExpression expression)
	{
		Object old = this.expression;
		this.expression = expression;
		getEventSupport().firePropertyChange(PROPERTY_EXPRESSION, old, this.expression);
	}
	
	@Override
	public Object clone()
	{
		DesignExpressionReturnValue clone = (DesignExpressionReturnValue)super.clone();
		clone.expression = JRCloneUtils.nullSafeClone(expression);
		return clone;
	}
}
