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
package net.sf.jasperreports.components.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.FormatUtils;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class FieldNumberComparator extends AbstractFieldComparator<Number> {


	public FieldNumberComparator(String filterPattern, Locale locale) {
		formatter = getFormatFactory().createNumberFormat(filterPattern, locale);
	}
	
	@Override
	public void initValues() throws Exception {
		if (valueStart != null && valueStart.length() > 0) {
			compareStart = FormatUtils.getFormattedNumber((NumberFormat)formatter, valueStart, compareToClass);
		}
		if (valueEnd != null && valueEnd.length() > 0) {
			compareEnd = FormatUtils.getFormattedNumber((NumberFormat)formatter, valueEnd, compareToClass);
		}
	}

	@Override
	public boolean compare(String filterTypeOperator) {
		boolean defaultResult = true,
				result = defaultResult,
				resultPart1 = true, 
				resultPart2 = true;
		
		try {
			initValues();
		} catch (Exception e) {
			throw new JRRuntimeException(e);
		}
		
		Number formattedCompareTo = compareTo;
		if (compareTo != null) {
			try {
				formattedCompareTo =  FormatUtils.getFormattedNumber((NumberFormat)formatter, formatter.format(compareTo), compareToClass);
			} catch (ParseException e) {
				throw new JRRuntimeException(e);
			}
		}
		
		FilterTypeNumericOperatorsEnum numericEnum = FilterTypeNumericOperatorsEnum.getByEnumConstantName(filterTypeOperator);
		BigDecimal dbA = formattedCompareTo != null ? new BigDecimal(formattedCompareTo.toString()) : null;
		BigDecimal dbStart = compareStart != null ? new BigDecimal(compareStart.toString()) : null;
		BigDecimal dbEnd = compareEnd != null ? new BigDecimal(compareEnd.toString()) : null;
		
		boolean validComparison = dbStart != null && dbA != null;
		boolean validComparison2 = dbEnd != null && dbA != null;
					
		switch (numericEnum) {
			case DOES_NOT_EQUAL:
				result = validComparison ? dbA.compareTo(dbStart) != 0 : defaultResult;
				break;
			case EQUALS:
				result = validComparison ? dbA.compareTo(dbStart) == 0 : false;
				break;
			case GREATER_THAN:
				result = validComparison ? dbA.compareTo(dbStart) > 0 : false;
				break;
			case GREATER_THAN_EQUAL_TO:
				result = validComparison ? dbA.compareTo(dbStart) >= 0 : false;
				break;
			case IS_BETWEEN:
				resultPart1 = validComparison ? dbA.compareTo(dbStart) >= 0 : false;
				resultPart2 = validComparison2 ? dbA.compareTo(dbEnd) <= 0 : false;
				result = resultPart1 && resultPart2;
				break;
			case IS_NOT_BETWEEN:
				resultPart1 = validComparison ? dbA.compareTo(dbStart) >= 0 : false;
				resultPart2 = validComparison2 ? dbA.compareTo(dbEnd) <= 0 : false;
				result = !(resultPart1 && resultPart2);
				break;
			case LESS_THAN:
				result = validComparison ? dbA.compareTo(dbStart) < 0 : false;
				break;
			case LESS_THAN_EQUAL_TO:
				result = validComparison ? dbA.compareTo(dbStart) <= 0 : false;
				break;
			case IS_NULL:
				result = compareTo == null;
				break;
			case IS_NOT_NULL:
				result = compareTo != null;
				break;
		}
		
		return result;
	}

}
