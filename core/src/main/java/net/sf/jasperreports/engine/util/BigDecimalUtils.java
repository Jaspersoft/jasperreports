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
package net.sf.jasperreports.engine.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility methods for {@link BigDecimal} handling.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class BigDecimalUtils
{

	private static final Log log = LogFactory.getLog(BigDecimalUtils.class);
	
	private static final BigDecimalHandler handler;
	
	static
	{
		handler = loadHandler();
	}
	
	private static BigDecimalHandler loadHandler()
	{
		BigDecimalHandler handler;
		try
		{
			Class<?> handlerClass = Class.forName(
					"net.sf.jasperreports.engine.util.Java15BigDecimalHandler");
			handler = (BigDecimalHandler) handlerClass.getDeclaredConstructor().newInstance();
		}
		catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException 
			| IllegalAccessException | InstantiationException e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Failed to instantiate Java 1.5 BigDecimal handler");
			}
			
			//fallback to 1.4 handler
			handler = new Java14BigDecimalHandler();
		}
		return handler;
	}

	/**
	 * Divides two {@link BigDecimal} values.
	 * 
	 * <p>
	 * If running on Java 1.5 or newer, {@link Java15BigDecimalHandler} is used to divide
	 * the values.  Otherwise, the operation is delegated to {@link Java14BigDecimalHandler}. 
	 * 
	 * @param dividend the dividend
	 * @param divisor the divisor
	 * @return the division result computed by the {@link BigDecimalHandler} instance
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor)
	{
		return handler.divide(dividend, divisor);
	}
	

	private BigDecimalUtils()
	{
	}
}
