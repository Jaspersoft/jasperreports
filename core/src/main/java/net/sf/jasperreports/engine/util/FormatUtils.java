/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class FormatUtils {
	private static final boolean isIcu4jAvailable = isIcu4jAvailable();
	
	/**
	 * Creates a number from a string value
	 * 
	 * @param numberFormat
	 * @param fieldValue
	 * @param valueClass
	 * @return the number as parsed from the string
	 * @throws ParseException
	 */
	public static Number getFormattedNumber(NumberFormat numberFormat, String fieldValue, Class<?> valueClass) throws ParseException {
		
		if (valueClass.equals(Byte.class)) 
		{
			return numberFormat.parse(fieldValue).byteValue();
		}
		else if (valueClass.equals(Integer.class)) 
		{
			return numberFormat.parse(fieldValue).intValue();
		}
		else if (valueClass.equals(Long.class)) 
		{
			return numberFormat.parse(fieldValue).longValue();
		}
		else if (valueClass.equals(Short.class)) 
		{
			return numberFormat.parse(fieldValue).shortValue();
		}
		else if (valueClass.equals(Double.class)) 
		{
			return numberFormat.parse(fieldValue).doubleValue();
		}
		else if (valueClass.equals(Float.class)) 
		{
			return numberFormat.parse(fieldValue).floatValue();
		}
		else if (valueClass.equals(BigDecimal.class)) 
		{
			return new BigDecimal(numberFormat.parse(fieldValue).toString());
		}
		else if (valueClass.equals(BigInteger.class)) 
		{
			return new BigInteger(String.valueOf(numberFormat.parse(fieldValue).longValue()));
		}
		else if (valueClass.equals(java.lang.Number.class)) 
		{
			return numberFormat.parse(fieldValue);
		}
		return null;
	}
	
	/**
	 * Creates a date from a string value
	 * 
	 * @param dateFormat
	 * @param fieldValue
	 * @param valueClass
	 * @return the date as parsed from the string
	 * @throws ParseException
	 */
	public static Date getFormattedDate(DateFormat dateFormat, String fieldValue, Class<?> valueClass) throws ParseException {
		
		if (valueClass.equals(java.util.Date.class)) 
		{
			return dateFormat.parse(fieldValue);
		}
		else if (valueClass.equals(java.sql.Date.class)) 
		{
			return new java.sql.Date(dateFormat.parse(fieldValue).getTime());
		}
		else if (valueClass.equals(java.sql.Timestamp.class)) 
		{
			return new java.sql.Timestamp(dateFormat.parse(fieldValue).getTime());
		}
		else if (valueClass.equals(java.sql.Time.class)) 
		{
			return new java.sql.Time(dateFormat.parse(fieldValue).getTime());
		}
		return null;
	}

	/**
	 * Returns an ICU4j NumberFormat instance if ICU4j is available and the pattern has variable grouping.
	 * If ICU4j is not available or the pattern does not have variable grouping, returns an empty Optional.
	 *
	 * @param pattern the number format pattern
	 * @param locale the locale for the number format
	 * @return an Optional containing the ICU4j NumberFormat if available and applicable, otherwise empty
	 */
	public static Optional<Format> getIcu4jNumberFormat(String pattern, Locale locale) {
		if (pattern != null && isIcu4jAvailable && hasVariableGrouping(pattern)) {
			com.ibm.icu.text.NumberFormat icuNumberFormat = com.ibm.icu.text.NumberFormat.getInstance(locale);

			if (icuNumberFormat instanceof com.ibm.icu.text.DecimalFormat) {
				((com.ibm.icu.text.DecimalFormat)icuNumberFormat).applyPattern(pattern);
			}

			return Optional.of(icuNumberFormat);
		}

		return Optional.empty();
	}

	/**
	 * Checks if the given pattern has variable grouping, which is a feature of ICU4j NumberFormat.
	 * Variable grouping means that the number of digits in each group can vary, which is not supported
	 * by the standard Java NumberFormat. ICU4j looks only at the last two groups of digits, while Java
	 * looks only at the last group when formatting numbers with grouping.
	 *
	 * @param pattern the number format pattern to check
	 * @return true if the pattern has variable grouping, false otherwise
	 */
	private static boolean hasVariableGrouping(String pattern) {
		String[] parts = pattern.split(";");
		String pozPart = parts[0];
		if (pozPart.indexOf(".") > 0) {
			pozPart = pozPart.substring(0, pozPart.indexOf("."));
		}

		if (!pozPart.contains(",")) {
			return false; // no grouping in pattern
		}

		char[] chars = pozPart.toCharArray();

		/*
         Although ICU4j only looks at the last two groups, we consider the second group(from right to left)
         to be closed when a second comma is found.
        */
		int[] groupSizes = new int[2];

		int groupIndex = 0;
		boolean secondGroupClosed = false;

		// Iterate backwards through the characters to find the last two groups
		for (int i = chars.length - 1; i >= 0; i--) {
			char currentChar = chars[i];
			// Looking only for 0s and #s without validating their position
			if (currentChar == '#' || currentChar == '0') {
				groupSizes[groupIndex] += 1;
			} else if (currentChar == ',') {
				groupIndex++;

				if (groupIndex > 1) {
					secondGroupClosed = true;
					break; // look no further
				}
			}
		}

		// Second group must be greater than zero to have variable groping with ICU4j
		return secondGroupClosed && groupSizes[1] > 0 && groupSizes[0] != groupSizes[1];
	}

	private static boolean isIcu4jAvailable() {
		try {
			// Check for ICU4j class from the icu4j jar
			Class.forName("com.ibm.icu.text.NumberFormat");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
