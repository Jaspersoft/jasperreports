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

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * <p>Implementation of {@link DateRange} for relative range of dates.</p>
 *
 * @author Sergey Prilukin
 */
public class RelativeDateRange extends AbstractDateRange implements DateRangeExpression 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;


	public static final String DATE_RANGE_REGEXP = "^(DAY|WEEK|MONTH|QUARTER|SEMI|YEAR)([\\+|-][\\d]{1,9})?$";
	public static final int DEFAULT_WEEK_START_DAY = Calendar.MONDAY;
	public static final String WEEK_START_DAY_KEY = "week.start.day";
	public static final String PROPERTIES_FILE_NAME = "relativedate.properties";

	@Property (
			category = PropertyConstants.CATEGORY_OTHER,
			scopes = {PropertyScope.GLOBAL},
			sinceVersion = PropertyConstants.VERSION_6_4_3,
			valueType = Integer.class
	)
	public static final String PROPERTY_WEEK_START_DAY = JRPropertiesUtil.PROPERTY_PREFIX + "week.start.day";
	private static final Pattern PATTERN = Pattern.compile(DATE_RANGE_REGEXP);

	private static Properties props;

	private CalendarUnit calendarUnit;
	private Integer number;
	private Integer weekStart;

	public RelativeDateRange(String expression) {
		this(expression, null, null);
	}

	public RelativeDateRange(String expression, TimeZone timeZone, Integer weekStart) {
		super(expression, timeZone);
		this.weekStart = weekStart;
		this.validateExpression(expression);
		this.parse();
	}

	protected Pattern getPattern() {
		return PATTERN;
	}

	@Override
	protected void validateExpression(String expression) throws InvalidDateRangeExpressionException {
		super.validateExpression(expression);

		Matcher matcher = getPattern().matcher(expression);
		if (!matcher.matches()) {
			throw new InvalidDateRangeExpressionException(expression);
		}
	}

	@Override
	public String getExpression() {
		return expression;
	}

	private void parse() {
		Matcher matcher = getPattern().matcher(getExpression());

		if (matcher.find()) {
			this.calendarUnit = CalendarUnit.fromValue(matcher.group(1));
			String numberAsString = matcher.group(2);

			if (numberAsString != null) {
				this.number = Integer.parseInt(numberAsString.replace("+", ""));
			} else {
				this.number = 0;
			}
		} else {
			//Should never happen since expression was already validated by validateExpression
			throw new InvalidDateRangeExpressionException(getExpression());
		}
	}

	protected int getWeekStart() {
		if (weekStart == null) {

			if (getProperties() == null) {
				synchronized (RelativeDateRange.class) {
					//double-check pattern is used
					if (getProperties() == null) {
						InputStream is = Thread.currentThread().getContextClassLoader()
								.getResourceAsStream(getPropertiesFileName());
						if (is != null) {
							setProperties(new Properties());
							try {
								getProperties().load(is);
							} catch (Exception e) {
								//Ignore exception
							}
						}
					}
				}
			}

			if (getProperties() != null) {
				try {
					this.weekStart = Integer.parseInt(getProperties().getProperty(WEEK_START_DAY_KEY));
				} catch (Exception e) {
					//ignore exception
				}
			}

			if (weekStart == null) {
				//FIXME: set an appropriate context
				this.weekStart = JRPropertiesUtil
						.getInstance(DefaultJasperReportsContext.getInstance())
						.getIntegerProperty(PROPERTY_WEEK_START_DAY, DEFAULT_WEEK_START_DAY);
			}
		}

		return weekStart;
	}

	protected String getPropertiesFileName() {
		return PROPERTIES_FILE_NAME;
	}

	protected Date getCurrentDate() {
		return new Date();
	}

	protected void setProperties(Properties props) {
		RelativeDateRange.props = props;
	}

	protected Properties getProperties() {
		return RelativeDateRange.props;
	}

	@Override
	public Date getStart() {
		Calendar calendar = getCalendar();

		switch (this.calendarUnit) {
			case DAY:
				calendar.add(Calendar.DAY_OF_YEAR, this.number);
				break;
			case WEEK:
				calendar.add(Calendar.DAY_OF_YEAR, -1 * getDaysToWeekStart(calendar));
				calendar.add(Calendar.WEEK_OF_YEAR, this.number);
				break;
			case MONTH:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.MONTH, this.number);
				break;
			case QUARTER:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.MONTH, -1 * getMonthesToQuarterStart(calendar));
				calendar.add(Calendar.MONTH, this.number * 3);
				break;
			case SEMI:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.MONTH, -1 * getMonthesToSemiStart(calendar));
				calendar.add(Calendar.MONTH, this.number * 6);
				break;
			case YEAR:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, 0);
				calendar.add(Calendar.YEAR, this.number);
				break;
			default:
				throw new IllegalArgumentException();
		}

		return calendar.getTime();
	}

	@Override
	public Date getEnd() {
		Calendar calendar = getCalendar();

		switch (this.calendarUnit) {
			case DAY:
				calendar.add(Calendar.DAY_OF_YEAR, this.number);
				break;
			case WEEK:
				calendar.add(Calendar.DAY_OF_YEAR, getDaysToWeekEnd(calendar));
				calendar.add(Calendar.WEEK_OF_YEAR, this.number);
				break;
			case MONTH:
				calendar.add(Calendar.MONTH, this.number);
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				break;
			case QUARTER:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.MONTH, getMonthesToQuarterEnd(calendar));
				calendar.add(Calendar.MONTH, this.number * 3);
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				break;
			case SEMI:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.add(Calendar.MONTH, getMonthesToSemiEnd(calendar));
				calendar.add(Calendar.MONTH, this.number * 6);
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				break;
			case YEAR:
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				calendar.set(Calendar.MONTH, 0);
				calendar.add(Calendar.YEAR, this.number);
				calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
				calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				break;
			default:
				throw new IllegalArgumentException();
		}

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTime();
	}

	protected Calendar getCalendar() {
		//setting the timezone as early as possible
		//if the timezone is set after setTime(), set(HOUR_OF_DAY) might change the day
		Calendar calendar = timeZone == null ? new GregorianCalendar() : new GregorianCalendar(timeZone);
		calendar.setTime(getCurrentDate());

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar;
	}

	private int getDaysToWeekStart(Calendar cal) {
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == Calendar.SUNDAY && getWeekStart() == Calendar.MONDAY) {
			return 6;
		}

		return dayOfWeek - getWeekStart();
	}

	private int getDaysToWeekEnd(Calendar cal) {
		return 7 - (getDaysToWeekStart(cal) + 1);
	}

	private int getMonthesToQuarterStart(Calendar cal) {
		return cal.get(Calendar.MONTH) % 3;
	}

	private int getMonthesToQuarterEnd(Calendar cal) {
		return 3 - (getMonthesToQuarterStart(cal) + 1);
	}

	private int getMonthesToSemiStart(Calendar cal) {
		return cal.get(Calendar.MONTH) % 6;
	}

	private int getMonthesToSemiEnd(Calendar cal) {
		return 6 - (getMonthesToSemiStart(cal) + 1);
	}

}
