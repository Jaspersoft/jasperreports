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
 * Default {@link JRCompilationSourceCode JRCompilationSourceCode} implementation that can receive
 * a list of expressions corresponding to lines in the code.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDefaultCompilationSourceCode implements JRCompilationSourceCode
{
	
	private final String sourceCode;
	private final JRExpression[] lineExpressions;
	
	
	/**
	 * Constructor.
	 * 
	 * @param sourceCode the source code.
	 * @param lineExpressions an array of expressions corresponding to line numbers.
	 * 	Can be null.
	 */
	public JRDefaultCompilationSourceCode(String sourceCode, JRExpression[] lineExpressions)
	{
		this.sourceCode = sourceCode;
		this.lineExpressions = lineExpressions;
	}

	@Override
	public String getCode()
	{
		return sourceCode;
	}

	@Override
	public JRExpression getExpressionAtLine(int line)
	{
		if (lineExpressions == null || line <= 0 || line > lineExpressions.length)
		{
			return null;
		}
		
		return lineExpressions[line - 1];
	}

}
