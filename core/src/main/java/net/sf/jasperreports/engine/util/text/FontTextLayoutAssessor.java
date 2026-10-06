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
package net.sf.jasperreports.engine.util.text;

import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * Complex text layout check that uses Java 9 {@link Font}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FontTextLayoutAssessor implements TextLayoutAssessor
{

	private static final Log log = LogFactory.getLog(FontTextLayoutAssessor.class);

	private final Method fontLayoutMethod;
	
	public FontTextLayoutAssessor()
	{
		Method method;
		try
		{
			method = Font.class.getMethod("textRequiresLayout", char[].class, int.class, int.class);
		}
		catch (NoSuchMethodException e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("java.awt.Font.textRequiresLayout method not found: " + e.getMessage());
			}
			method = null;
		}
		fontLayoutMethod = method;
	}

	public boolean available()
	{
		return fontLayoutMethod != null;
	}

	@Override
	public boolean hasComplexLayout(char[] chars)
	{
		try
		{
			return (boolean) fontLayoutMethod.invoke(null, chars, 0, chars.length);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new JRRuntimeException(e);
		}
	}

}
