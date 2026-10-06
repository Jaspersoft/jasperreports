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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.sf.jasperreports.jackson.util.PropertyExpressionDeserializer;
import net.sf.jasperreports.jackson.util.PropertyExpressionSerializer;

/**
 * Report property with a value based on an expression.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRElement#getPropertyExpressions()
 */
@JsonSerialize(using = PropertyExpressionSerializer.class)
@JsonDeserialize(using = PropertyExpressionDeserializer.class)
public interface JRPropertyExpression extends JRCloneable
{
	
	/**
	 * Return the property name.
	 * 
	 * @return the property name
	 */
	String getName();
	
	/**
	 * Set the property name.
	 * 
	 * @param name the property name
	 */
	void setName(String name);
	
	/**
	 * Return the property value expression.
	 * 
	 * @return the property value expression
	 */
	JRExpression getValueExpression();

}
