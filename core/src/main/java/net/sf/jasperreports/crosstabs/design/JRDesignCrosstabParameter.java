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
package net.sf.jasperreports.crosstabs.design;

import net.sf.jasperreports.crosstabs.JRCrosstabParameter;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * Implementation of crosstab parameters to be used for report designing.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignCrosstabParameter extends JRDesignParameter implements JRCrosstabParameter
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_VALUE_EXPRESSION = "valueExpression";

	protected JRExpression valueExpression;

	
	/**
	 * Creates a crosstab parameter.
	 */
	public JRDesignCrosstabParameter()
	{
	}
	
	@Override
	public JRExpression getExpression()
	{
		return valueExpression;
	}

	
	/**
	 * Sets the parameter value expression.
	 * 
	 * @param expression the parameter value expression
	 */
	public void setExpression(JRExpression expression)
	{
		Object old = this.valueExpression;
		this.valueExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_EXPRESSION, old, this.valueExpression);
	}
	
	@Override
	public Object clone()
	{
		JRDesignCrosstabParameter clone = (JRDesignCrosstabParameter) super.clone();
		clone.valueExpression = JRCloneUtils.nullSafeClone(valueExpression);
		return clone;
	}
}
