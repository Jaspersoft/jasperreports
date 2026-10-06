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

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * {@link BigDecimalHandler} implementation used on Java 1.4.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see BigDecimalUtils#divide(BigDecimal, BigDecimal)
 */
public class Java14BigDecimalHandler implements BigDecimalHandler
{

	/**
	 * Divides the values by using the dividend scale as result scale
	 * and {@link RoundingMode#HALF_UP} as round mode.
	 */
	@Override
	public BigDecimal divide(BigDecimal dividend, BigDecimal divisor)
	{
		return dividend.divide(divisor, RoundingMode.HALF_UP);
	}

}
