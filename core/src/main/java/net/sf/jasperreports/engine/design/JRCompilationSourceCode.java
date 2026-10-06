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

import net.sf.jasperreports.engine.JRExpression;


/**
 * A source code unit to be compiled by a report compiler.
 * <p/>
 * In addition to the source code itself, the unit offers the possibility of determining whether
 * a line of code corresponds to a report expression.
 *  
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRCompilationSourceCode
{
	
	/**
	 * Returns the source code.
	 * 
	 * @return the source code
	 */
	String getCode();
	
	/**
	 * Determines whether a line of code corresponds to a report expression.
	 * 
	 * @param line the line number
	 * @return the expression to which the line of code corresponds if any and null otherwise
	 */
	JRExpression getExpressionAtLine(int line);
	
}
