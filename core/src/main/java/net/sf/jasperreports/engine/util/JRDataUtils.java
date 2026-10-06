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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRTextField;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JRDataUtils
{
	
	public static final double JULIAN_0000 = 1721424.5;
	public static final double JULIAN_1900 = 2415020.5;	
	public static final String ISO_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
	
	
	public static DateFormat getIsoDateFormat()
	{
		return new SimpleDateFormat(ISO_DATE_PATTERN);
	}
	

	public static String getLocaleCode(Locale locale)
	{
		return locale.toString();
	}

	
	public static Locale getLocale(String code)
	{
		String language;
		String country;
		String variant;
		
		int firstSep = code.indexOf('_');
		if (firstSep < 0) {
			language = code;
			country = variant = "";
		} else {
			language = code.substring(0, firstSep);
			
			int secondSep = code.indexOf('_', firstSep + 1);
			if (secondSep < 0) {
				country = code.substring(firstSep + 1);
				variant = "";
			} else {
				country = code.substring(firstSep + 1, secondSep);
				variant = code.substring(secondSep + 1);
			}
		}
		
		return new Locale(language, country, variant);
	}
	
	
	public static String getTimeZoneId(TimeZone tz)
	{
		return tz.getID();
	}
	
	
	public static TimeZone getTimeZone(String id)
	{
		return TimeZone.getTimeZone(id);
	}
	
	
	public static TimeZone resolveFormatTimeZone(String timeZoneId, TimeZone reportTimeZone)
	{
		if (timeZoneId == null || timeZoneId.isEmpty())
		{
			return null;
		}
		
		if (timeZoneId.equals(JRTextField.FORMAT_TIMEZONE_SYSTEM))
		{
			// using the default JVM timezone
			return TimeZone.getDefault();
		}
		
		if (timeZoneId.equals(JRParameter.REPORT_TIME_ZONE))
		{
			// using the report timezone
			return reportTimeZone;
		}
		
		// note that this returns GMT if the ID is unknown, leaving that as is
		return getTimeZone(timeZoneId);
	}
	

	public static double getExcelSerialDayNumber(Date date, Locale locale, TimeZone timeZone)
	{
		GregorianCalendar calendar = new GregorianCalendar(timeZone,locale);
		calendar.setTime(date);

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH); // starts from 0
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		int sec = calendar.get(Calendar.SECOND);
		int millis = calendar.get(Calendar.MILLISECOND);
		
		double result = getGregorianToJulianDay(year, month + 1, day) +
				(Math.floor(millis + 1000 * (sec + 60 * (min + 60 * hour)) + 0.5) / 86400000.0);	
		return (result - JULIAN_1900) + 1 + ((result > 2415078.5) ? 1 : 0);
	}
	

	public static double  getGregorianToJulianDay(int year, int month, int day)
	{
		return JULIAN_0000 +
				(365 * (year - 1)) +
				Math.floor((year - 1) / 4) -
				Math.floor((year - 1) / 100) +
				Math.floor((year - 1) / 400) +
				Math.floor((((367 * month) - 362) / 12) +
					(month <= 2 ? 0 : (isLeapYear(year) ? -1 : -2)) +
					day);
	}


	public static boolean isLeapYear(int year)
	{
		return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
	}
	
	/**
	 * Returns a translated date value that has the same fields in a specified
	 * timezone as the passed value in the default timezone.
	 * 
	 * @param value the value to translate
	 * @param tz the timezone to translate to
	 * @return the date translated to the specified timezone
	 */
	public static Date translateToTimezone(Date value, TimeZone tz)
	{
		if (tz == null)
		{
			return value;
		}
		
		TimeZone defaultTz = TimeZone.getDefault();
		if (defaultTz.hasSameRules(tz))
		{
			// nothing to do
			return value;
		}
		
		long time = value.getTime();
		Date adjustedDate = new Date(time + tz.getOffset(time) - defaultTz.getOffset(time));
		return adjustedDate;
	}
	

	private JRDataUtils()
	{
	}
	
}
