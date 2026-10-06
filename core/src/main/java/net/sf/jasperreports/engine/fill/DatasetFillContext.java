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

import java.util.Locale;

import net.sf.jasperreports.engine.EvaluationType;

/**
 * Context information related to a fill time dataset.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface DatasetFillContext
{

	/**
	 * Returns the value of a parameter.
	 * 
	 * @param parameter the parameter name
	 * @return the parameter value
	 */
	Object getParameterValue(String parameter);
	
	/**
	 * Returns the value of a field.
	 * 
	 * @param field the field name
	 * @param evaluation the evaluation type
	 * @return the field value
	 */
	Object getFieldValue(String field, EvaluationType evaluation);
	
	/**
	 * Returns the value of a variable.
	 * 
	 * @param variable the variable name
	 * @param evaluation the evaluation type
	 * @return the variable value
	 */
	Object getVariableValue(String variable, EvaluationType evaluation);

	/**
	 * Returns the locale used by the dataset.
	 */
	Locale getLocale();
}
