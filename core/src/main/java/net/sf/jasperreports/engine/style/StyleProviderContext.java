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
package net.sf.jasperreports.engine.style;

import java.util.Locale;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JasperReportsContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface StyleProviderContext
{

	/**
	 *
	 */
	public JasperReportsContext getJasperReportsContext();

	/**
	 *
	 */
	public JRElement getElement();

	/**
	 *
	 */
	public Object evaluateExpression(JRExpression expression, byte evaluation);

	/**
	 *
	 */
	public Object getFieldValue(String fieldName, byte evaluation);

	/**
	 *
	 */
	public Object getVariableValue(String variableName, byte evaluation);

	/**
	 *
	 */
	public Locale getLocale();

	/**
	 *
	 */
	public TimeZone getTimeZone();

}
