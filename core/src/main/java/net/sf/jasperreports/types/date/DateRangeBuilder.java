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
package net.sf.jasperreports.types.date;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>Builder which allows to build proper instance of {@link net.sf.jasperreports.types.date.DateRange}</p>
 * <p>Usage example:</p>
 * <code>
 *     DateRange range = new DateRangeBuilder("DAY").set(TimeZone.getTimeZone("GMT")).setValueClass(Timestamp.class).toDateRange()
 * </code>
 *
 * @author Sergey Prilukin
 */
public class DateRangeBuilder 
{

	private Date dateValue;
	private String expression;
	private String datePattern;
	private Class<? extends Date> valueClass;
	private TimeZone timeZone;
	private Integer weekStartDay;

	/**
	 * <p>Constructor which allows to create DateRange instance from passed date instance.</p>
	 *
	 * <p>No additional configuration via <code>set</code> methods is necessary</p>
	 * @param dateValue date instance to create DateRange instance
	 */
	public DateRangeBuilder(Date dateValue) {
		this.dateValue = dateValue;
	}

	/**
	 * <p>Constructor which allows to create DateRange instance from passed string expression.</p>
	 *
	 * <p>It is usually necessary to pass additional configuration through <code>set</code> methods</p>
	 * @param expression string expression which will be parsed in order to construct instance of DateRange
	 */
	public DateRangeBuilder(String expression) {
		this.expression = expression;
	}

	/**
	 * <p>Configuration method which sets date pattern which will be used to parse expression</p>
	 * <p>
	 *     NOTE: this method only will take effect if expression is in fact formatted date like "1970-01-01".
	 * </p>
	 * @param datePattern date pattern which will be used to parse date
	 * @return this instance of DateRangeBuilder
	 *
	 * @see java.text.SimpleDateFormat
	 */
	public DateRangeBuilder set(String datePattern) {
		this.datePattern = datePattern;
		return this;
	}

	/**
	 * <p>
	 *     Configuration method which sets value class,
	 *     to indicate desired class of DateRange.getStart and DateRange.getEnd methods.
	 * </p>
	 * <p>
	 *     NOTE: this method will not take effect if {@link #DateRangeBuilder(java.util.Date)} constructor was used.
	 * </p>
	 *
	 * @param valueClass class instance of which will be returned by DateRange.getStart and DateRange.getEnd methods
	 * @return this instance of DateRangeBuilder
	 */
	public DateRangeBuilder set(Class<? extends Date> valueClass) {
		this.valueClass = valueClass;
		return this;
	}

	/**
	 * <p>
	 *     Configuration method which sets desired time zone which will be used to calculate DateRange.getStart and DateRange.getEnd.
	 * </p>
	 * <p>
	 *     NOTE: this method will not take effect if {@link #DateRangeBuilder(java.util.Date)} constructor was used.
	 * </p>
	 *
	 * @param timeZone time zone which will be used to calculate DateRange.getStart and DateRange.getStart.
	 * @return this instance of DateRangeBuilder
	 */
	public DateRangeBuilder set(TimeZone timeZone) {
		this.timeZone = timeZone;
		return this;
	}

	/**
	 * <p>
	 *     Configuration method which set start of a week
	 *     which will be used during DateRange.getStart and DateRange.getEnd calculations.
	 * </p>
	 * <p>
	 *     Should be one of:
	 *     {@link java.util.Calendar#SUNDAY}, {@link java.util.Calendar#MONDAY}
	 * </p>
	 * <p>
	 *     NOTE: this method only will take effect if expression is in fact date range expression like "WEEK".
	 * </p>
	 * @param weekStartDay week start day to calculate DateRange.getStart and DateRange.getEnd for expressions like "WEEK"
	 * @return this instance of DateRangeBuilder
	 */
	public DateRangeBuilder set(Integer weekStartDay) {
		this.weekStartDay = weekStartDay;
		return this;
	}

	/**
	 * Returns the week start day used for <code>WEEK</code> expressions.
	 * 
	 * @return the week start day
	 * @see #set(Integer)
	 */
	public Integer getWeekStartDay()
	{
		return weekStartDay;
	}

	/**
	 * <p>
	 *     Configuration method which set start of a week
	 *     which will be used during DateRange.getStart and DateRange.getEnd calculations
	 *     from passed locale instance
	 * </p>
	 * <p>
	 *     NOTE: This method and {@link #set(Integer)} will overwrite each other
	 *     so configuration which was set later will be used.
	 * </p>
	 * <p>
	 *     NOTE: this method only will take effect if expression is in fact date range expression like "WEEK".
	 * </p>
	 * @param locale locale to calculate DateRange.getStart and DateRange.getEnd for expressions like "WEEK"
	 * @return this instance of DateRangeBuilder
	 */
	public DateRangeBuilder set(Locale locale) {
		Calendar cal = Calendar.getInstance(locale);
		this.weekStartDay = cal.getFirstDayOfWeek();

		return this;
	}

	/**
	 * <code>Returns instance of {@link DateRange} based on which configuration methods was called</code>
	 * @return instance of {@link DateRange}
	 * @throws InvalidDateRangeExpressionException if could not instantiate {@code DateRange} using passed expression
	 */
	public DateRange toDateRange() throws InvalidDateRangeExpressionException {

		//If dateValue was sat we have only two possibilities - FixedDate or FixedTimestamp
		if (dateValue != null) {
			if (valueClass == null) {
				if (Timestamp.class.equals(dateValue.getClass())) {
					return new FixedTimestamp((Timestamp)dateValue);
				} else {
					return new FixedDate(dateValue);
				}
			} else if (Timestamp.class.equals(valueClass)) {
				return new FixedTimestamp(new Timestamp(dateValue.getTime()));
			} else {
				return new FixedDate(dateValue);
			}
		}

		//Try to get instance using FixedDate firstly and RelativeDateRange if first attempt failed
		if (valueClass != null) {
			if (Timestamp.class.equals(valueClass)) {
				try {
					return new FixedTimestamp(expression, timeZone, datePattern);
				} catch (Exception e) {
					return new RelativeTimestampRange(expression, timeZone, weekStartDay);
				}
			} else {
				try {
					return new FixedDate(expression, timeZone, datePattern);
				} catch (Exception e) {
					return new RelativeDateRange(expression, timeZone, weekStartDay);
				}
			}
		}

		//Ok need to use heuristic methods below
		if (datePattern != null) {
			try {
				if (isLikeTimestampPattern(datePattern)) {
					return new FixedTimestamp(expression, timeZone, datePattern);
				} else {
					return new FixedDate(expression, timeZone, datePattern);
				}
			} catch (Exception e) {/*do nothing*/}
		}

		//Worst case scenario: need to try one by one
		try {
			return new FixedTimestamp(expression, timeZone, datePattern);
		} catch (Exception e) {/*do nothing*/}

		try {
			return new FixedDate(expression, timeZone, datePattern);
		} catch (Exception e) {/*do nothing*/}

		//Last resort
		return new RelativeDateRange(expression, timeZone, weekStartDay);
	}

	private boolean isLikeTimestampPattern(String pattern) {
		return pattern.matches(".*[hHmsS]+.*");
	}
}
