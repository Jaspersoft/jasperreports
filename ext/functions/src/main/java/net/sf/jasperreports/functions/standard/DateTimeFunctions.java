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
package net.sf.jasperreports.functions.standard;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.zone.ZoneRules;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.functions.AbstractFunctionSupport;
import net.sf.jasperreports.functions.annotations.Function;
import net.sf.jasperreports.functions.annotations.FunctionCategories;
import net.sf.jasperreports.functions.annotations.FunctionParameter;
import net.sf.jasperreports.functions.annotations.FunctionParameters;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.DateRangeBuilder;


/**
 * This class should maintain all function methods that belongs to the DateTime category.
 * 
 * @author Massimo Rabbi (mrabbi@users.sourceforge.net)
 */
@FunctionCategories({DateTimeCategory.class})
public final class DateTimeFunctions extends AbstractFunctionSupport
{
	private static final Log log = LogFactory.getLog(DateTimeFunctions.class);
	
	// ===================== TODAY function ===================== //
	/**
	 * Returns the current date as date object.
	 */
	@Function("TODAY")
	public Date TODAY(){
		return Calendar.getInstance(getReportTimeZone(),getReportLocale()).getTime();
	}
	
	// ===================== NOW function ===================== //
	/**
	 * Returns the current instant as date object.
	 */
	@Function("NOW")
	public Date NOW(){
		return TODAY();
	}
	
	// ===================== YEAR function ===================== //
	/**
	 * Returns the year of a given date. Date object can be a String, long value (milliseconds) or Date instance itself.
	 */
	@Function("YEAR")
	@FunctionParameters({
		@FunctionParameter("dateObject")})
	public Integer YEAR(Object dateObject){
		return getCalendarFieldFromDate(dateObject,Calendar.YEAR);
	}
	
	// ===================== MONTH function ===================== //
	/**
	 * Returns the month of a given date. Date object can be a String, long value (milliseconds) or Date instance itself.
	 */
	@Function("MONTH")
	@FunctionParameters({
		@FunctionParameter("dateObject")})
	public Integer MONTH(Object dateObject){
		return getCalendarFieldFromDate(dateObject,Calendar.MONTH)+1;	// January is 0
	}
	
	// ===================== DAY function ===================== //
	/**
	 * 
	 * Returns the day of a given date. Date object can be a String, long value (milliseconds) or Date instance itself.
	 */
	@Function("DAY")
	@FunctionParameters({
		@FunctionParameter("dateObject")})
	public Integer DAY(Object dateObject){
		return getCalendarFieldFromDate(dateObject,Calendar.DAY_OF_MONTH);
	}
	
	// ===================== WEEKDAY function ===================== //
	/**
	 * Returns the day of the week for a given date. Date object can be a String, long value (milliseconds) or Date instance itself.
	 */
	@Function("WEEKDAY")
	@FunctionParameters({
		@FunctionParameter("dateObject"),
		@FunctionParameter("isSundayFirstDay")})
	public Integer WEEKDAY(Object dateObject){
		return WEEKDAY(dateObject, false);
	}
	
	public Integer WEEKDAY(Object dateObject, Boolean isSundayFirstDay){
		Integer dayOfWeek = getCalendarFieldFromDate(dateObject,Calendar.DAY_OF_WEEK);
		if (dayOfWeek==null) {
			if (log.isDebugEnabled()){
				log.debug("Unable to get the correct day of the week.");
			}
			return null;
		}
		if (isSundayFirstDay){
			// By default Sunday is considered first day in Java 
			// Calendar.SUNDAY should be a constant with value 1.
			// See the Calendar.DAY_OF_WEEK javadoc		
			return dayOfWeek;
		}
		else{
			// shift the days
			if (dayOfWeek==Calendar.SUNDAY){
				return 7;
			}
			else{
				return dayOfWeek-1;
			}
		}
	}
	
	// ===================== HOUR function ===================== //
	/**
	 * Returns the hour (0-23) of the day for a given date. Date object can be a String, long value (milliseconds) or Date instance itself.
	 */
	@Function("HOUR")
	@FunctionParameters({
		@FunctionParameter("dateObject")})
	public Integer HOUR(Object dateObject){
		return getCalendarFieldFromDate(dateObject,Calendar.HOUR_OF_DAY);
	}	

	// ===================== MINUTE function ===================== //
	/**
	 * Returns the minute (0-59) of the hour for a given date. Date object can be a String, long value (milliseconds) or Date instance itself.
	 */
	@Function("MINUTE")
	@FunctionParameters({
		@FunctionParameter("dateObject")})
	public Integer MINUTE(Object dateObject){
		return getCalendarFieldFromDate(dateObject,Calendar.MINUTE);
	}
	
	// ===================== SECOND function ===================== //
	/**
	 * Returns the second (0-59) of the minute for a given date. Date object can be a String, long value (milliseconds) or Date instance itself.
	 */
	@Function("SECOND")
	@FunctionParameters({
		@FunctionParameter("dateObject")})
	public Integer SECOND(Object dateObject){
		return getCalendarFieldFromDate(dateObject,Calendar.SECOND);
	}
	
	// ===================== DATE function ===================== //
	/**
	 *Creates a date object using the specified information on day, month and year.
	 */
	@Function("DATE")
	@FunctionParameters({
		@FunctionParameter("year"),
		@FunctionParameter("month"),
		@FunctionParameter("dayOfMonth")})
	public Date DATE(Integer year, Integer month, Integer dayOfMonth){
		if (year==null || month==null || dayOfMonth==null) {
			if (log.isDebugEnabled()){
				log.debug("None of the arguments can be null.");
			}
			return null;
		}
		LocalDateTime ldt = LocalDateTime.of(year, month, dayOfMonth, 0, 0);
		ZonedDateTime zdt = ZonedDateTime.of(ldt, getReportTimeZone().toZoneId());
		return Date.from(zdt.toInstant());
	}
	
	// ===================== DATEVALUE function ===================== //
	/**
	 * Gives the corresponding numeric value (long milliseconds) for a specified date object.
	 */
	@Function("DATEVALUE")
	@FunctionParameters({
		@FunctionParameter("dateObject")})
	public Long DATEVALUE(Object dateObject){
		Date convertedDate = convertDateObject(dateObject);
		if (convertedDate!=null){
			return convertedDate.getTime(); 
		}
		else {
			logCannotConvertToDate();
			return null;
		}
	}
	
	// ===================== TIME function ===================== //
	/**
	 * Returns a text string representing a time value (hours, seconds and minutes). If no specific pattern is specified a default formatter is used.
	 */
	@Function("TIME")
	@FunctionParameters({
		@FunctionParameter("hours"),
		@FunctionParameter("minutes"),
		@FunctionParameter("seconds"),
		@FunctionParameter("timePattern")})
	public String TIME(Integer hours, Integer minutes, Integer seconds){
		return TIME(hours, minutes, seconds, null);
	}
	
	public String TIME(Integer hours, Integer minutes, Integer seconds, String timePattern){
		if (hours==null || minutes==null || seconds==null) {
			if (log.isDebugEnabled()){
				log.debug("None of the arguments can be null.");
			}
			return null;
		}
		
		LocalTime lt = LocalTime.of(hours, minutes, seconds);
		DateTimeFormatter fallbackFormatter = 
				DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG).withLocale(getReportLocale()).withZone(getReportTimeZone().toZoneId());
		if (timePattern==null) {
			return fallbackFormatter.format(lt);
		}
		else {
			try {
				// Try to convert to a pattern
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timePattern, getReportLocale()).withZone(getReportTimeZone().toZoneId());
				return dtf.format(lt);
			} catch (IllegalArgumentException e) {
				// Fallback to the default solution
				return fallbackFormatter.format(lt);	
			}
		}
	}
	
	// ===================== EDATE function ===================== //
	/**
	 * Returns a date a number of months away.
	 */
	@Function("EDATE")
	@FunctionParameters({
		@FunctionParameter("dateObject"),
		@FunctionParameter("months")})
	public Date EDATE(Object dateObject, Integer months){
		Date convertedDate = convertDateObject(dateObject);
		if (convertedDate==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			Instant instant = Instant.ofEpochMilli(convertedDate.getTime());
			ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, getReportTimeZone().toZoneId());
			ZonedDateTime newZdt = zdt.plusMonths(months);
			return Date.from(newZdt.toInstant());
		}
	}
	
	// ===================== WORKDAY function ===================== //
	/**
	 * Returns a date a number of workdays away. Saturday and Sundays are not considered working days.
	 */
	@Function("WORKDAY")
	@FunctionParameters({
		@FunctionParameter("dateObject"),
		@FunctionParameter("workdays")})
	public Date WORKDAY(Object dateObject, Integer workdays){
		Date convertedDate = convertDateObject(dateObject);
		if (convertedDate==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(convertedDate.getTime()), getReportTimeZone().toZoneId());
			boolean lookBack = workdays < 0;
			int remainingDays = Math.abs(workdays);
			while (remainingDays > 0) {
				DayOfWeek dayOfWeek = zdt.getDayOfWeek();
				if (!(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY)) {
					// Decrement remaining days only when it is not Saturday or Sunday
					remainingDays--;
				}
				if (!lookBack) {
					zdt = dayOfWeek == DayOfWeek.FRIDAY ? zdt.plusDays(3) : zdt.plusDays(1);
				} else {
					zdt = dayOfWeek == DayOfWeek.MONDAY ? zdt.minusDays(3) : zdt.minusDays(1);
				}
			}
			return Date.from(zdt.toInstant());
		}
	}
	
	// ===================== NETWORKDAYS function ===================== //
	/**
	 * Returns the number of working days between two dates (inclusive). Saturday and Sunday are not considered working days.
	 */
	@Function("NETWORKDAYS")
	@FunctionParameters({
		@FunctionParameter("startDate"),
		@FunctionParameter("endDate")})
	public Integer NETWORKDAYS(Object startDate, Object endDate){
		Date startDateObj = convertDateObject(startDate);
		if (startDateObj==null) {
			logCannotConvertToDate();
			return null;
		}
		Date endDateObj = convertDateObject(endDate);
		if (endDateObj==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			ZoneId reportZoneID = getReportTimeZone().toZoneId();
			ZonedDateTime zdtCursor = ZonedDateTime.ofInstant(Instant.ofEpochMilli(startDateObj.getTime()), reportZoneID);
			ZonedDateTime zdtEnd = ZonedDateTime.ofInstant(Instant.ofEpochMilli(endDateObj.getTime()), reportZoneID);
			
			int workingDays = 0;
			if (zdtCursor.isAfter(zdtEnd)) {
				// Swap date information
				ZonedDateTime tmp = zdtCursor;
				zdtCursor = zdtEnd;
				zdtEnd = tmp;
			}

			LocalDate cursorLocalDate = zdtCursor.toLocalDate();
			LocalDate endLocalDate = zdtEnd.toLocalDate();
			while (ChronoUnit.DAYS.between(cursorLocalDate, endLocalDate)>0) {
				DayOfWeek dayOfWeek = cursorLocalDate.getDayOfWeek();
				if (!isWeekendDay(dayOfWeek)) {
					workingDays++;
				}
				cursorLocalDate = cursorLocalDate.plusDays(1);
			}
			
			return workingDays;
		}
	}
	
	// ===================== DAYS function ===================== //
	/**
	 * Returns the number of days between two dates.
	 */
	@Function("DAYS")
	@FunctionParameters({
		@FunctionParameter("startDate"),
		@FunctionParameter("endDate")})
	public Integer DAYS(Object startDate, Object endDate){
		Date startDateObj = convertDateObject(startDate);
		if (startDateObj==null) {
			logCannotConvertToDate();
			return null;
		}
		Date endDateObj = convertDateObject(endDate);
		if (endDateObj==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			ZoneId reportZoneID = getReportTimeZone().toZoneId();
			LocalDate startLocalDate = ofInstant(Instant.ofEpochMilli(startDateObj.getTime()), reportZoneID);
			LocalDate endLocalDate = ofInstant(Instant.ofEpochMilli(endDateObj.getTime()), reportZoneID);
			return (int) ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
		}
	}
	
	// ===================== DAYSINMONTH function ===================== //
	/**
	 * Returns the number of days in a month.
	 */
	@Function("DAYSINMONTH")
	@FunctionParameters({
		@FunctionParameter("dateObj")})
	public Integer DAYSINMONTH(Object dateObj){
		Date date = convertDateObject(dateObj);
		if (date==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			LocalDate ld = ofInstant(Instant.ofEpochMilli(date.getTime()), getReportTimeZone().toZoneId());
			return YearMonth.from(ld).lengthOfMonth();
		}
	}
	
	// ===================== DAYSINYEAR function ===================== //
	/**
	 * Returns the number of days in a year.
	 */
	@Function("DAYSINYEAR")
	@FunctionParameters({
		@FunctionParameter("dateObj")})
	public Integer DAYSINYEAR(Object dateObj){
		Date date = convertDateObject(dateObj);
		if (date==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			LocalDate ld = ofInstant(Instant.ofEpochMilli(date.getTime()), getReportTimeZone().toZoneId());
			return YearMonth.from(ld).lengthOfYear();
		}
	}
	
	// ===================== WEEKS function ===================== //
	/**
	 * Returns the number of weeks between two dates.
	 */
	@Function("WEEKS")
	@FunctionParameters({
		@FunctionParameter("startDate"),
		@FunctionParameter("endDate")})
	public Integer WEEKS(Object startDate, Object endDate){
		Date startDateObj = convertDateObject(startDate);
		if (startDateObj==null) {
			logCannotConvertToDate();
			return null;
		}
		Date endDateObj = convertDateObject(endDate);
		if (endDateObj==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			ZoneId reportZoneID = getReportTimeZone().toZoneId();
			LocalDate startLocalDate = ofInstant(Instant.ofEpochMilli(startDateObj.getTime()), reportZoneID);
			LocalDate endLocalDate = ofInstant(Instant.ofEpochMilli(endDateObj.getTime()), reportZoneID);
			return (int) ChronoUnit.WEEKS.between(startLocalDate, endLocalDate);
		}
	}
	
	// ===================== WEEKSINYEAR function ===================== //
	/**
	 * Returns the number of weeks in a year.
	 */
	@Function("WEEKSINYEAR")
	@FunctionParameters({
		@FunctionParameter("dateObj")})
	public Integer WEEKSINYEAR(Object dateObj){
		Date date = convertDateObject(dateObj);
		if (date==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			// Rules:
			// 	- 53 weeks:
			//		> if the 1 January is a Thursday
			//		> if the 1 January is a Wednesday in a leap year
			// 	- 52 weeks:
			//		> all other cases
			LocalDate ld = ofInstant(Instant.ofEpochMilli(date.getTime()), getReportTimeZone().toZoneId());
			LocalDate firstDayOfYear = LocalDate.of(ld.getYear(), 1, 1);
			if (firstDayOfYear.getDayOfWeek() == DayOfWeek.THURSDAY
					|| (firstDayOfYear.getDayOfWeek() == DayOfWeek.WEDNESDAY && firstDayOfYear.isLeapYear())) {
				return 53;
			}
			return 52;
		}
	}
	
	// ===================== WEEKNUM function ===================== //
	/**
	 * Returns the week number of a given date.
	 */
	@Function("WEEKNUM")
	@FunctionParameters({
		@FunctionParameter("dateObj")})
	public Integer WEEKNUM(Object dateObj){
		Date date = convertDateObject(dateObj);
		if (date==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			LocalDate ld = ofInstant(Instant.ofEpochMilli(date.getTime()), getReportTimeZone().toZoneId());
			return ld.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
		}
	}
	
	// ===================== MONTHS function ===================== //
	/**
	 * Returns the number of months between two dates.
	 */
	@Function("MONTHS")
	@FunctionParameters({
		@FunctionParameter("startDate"),
		@FunctionParameter("endDate")})
	public Integer MONTHS(Object startDate, Object endDate){
		Date startDateObj = convertDateObject(startDate);
		if (startDateObj==null) {
			logCannotConvertToDate();
			return null;
		}
		Date endDateObj = convertDateObject(endDate);
		if (endDateObj==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			ZoneId reportZoneID = getReportTimeZone().toZoneId();
			LocalDate startLocalDate = ofInstant(Instant.ofEpochMilli(startDateObj.getTime()), reportZoneID);
			LocalDate endLocalDate = ofInstant(Instant.ofEpochMilli(endDateObj.getTime()), reportZoneID);
			return (int) ChronoUnit.MONTHS.between(startLocalDate, endLocalDate);
		}
	}
	
	// ===================== YEARS function ===================== //
	/**
	 * Returns the number of years between two dates.
	 */
	@Function("YEARS")
	@FunctionParameters({
		@FunctionParameter("startDate"),
		@FunctionParameter("endDate")})
	public Integer YEARS(Object startDate, Object endDate){
		Date startDateObj = convertDateObject(startDate);
		if (startDateObj==null) {
			logCannotConvertToDate();
			return null;
		}
		Date endDateObj = convertDateObject(endDate);
		if (endDateObj==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			ZoneId reportZoneID = getReportTimeZone().toZoneId();
			LocalDate startLocalDate = ofInstant(Instant.ofEpochMilli(startDateObj.getTime()), reportZoneID);
			LocalDate endLocalDate = ofInstant(Instant.ofEpochMilli(endDateObj.getTime()), reportZoneID);
			return (int) ChronoUnit.YEARS.between(startLocalDate, endLocalDate);
		}
	}
	
	// ===================== ISLEAPYEAR function ===================== //
	/**
	 * Checks if the given date occurs in a leap year.
	 */
	@Function("ISLEAPYEAR")
	@FunctionParameters({
		@FunctionParameter("dateObj")})
	public Boolean ISLEAPYEAR(Object dateObj){
		Date date = convertDateObject(dateObj);
		if (date==null){
			logCannotConvertToDate();
			return null;
		}
		else{
			LocalDate ld = ofInstant(Instant.ofEpochMilli(date.getTime()), getReportTimeZone().toZoneId());
			return ld.isLeapYear();
		}
	}
	
	// ===================== FORMAT function ===================== //
	/**
	 * Format the specified date object using the chosen format pattern.
	 */
	@Function("DATEFORMAT")
	@FunctionParameters({
		@FunctionParameter("dateObj"),
		@FunctionParameter("formatPattern")})
	public String DATEFORMAT(Date dateObj, String formatPattern){
		if (dateObj==null){
			return null;
		}
		else{
			ZoneId zoneId = getReportTimeZone().toZoneId();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern, getReportLocale()).withZone(zoneId);
			ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateObj.getTime()), zoneId);
			return formatter.format(zdt);
		}
	}
	
	// ===================== DATERANGE function ===================== //
	/**
	 * Allows to create a JasperReports DateRange instance starting from either a String expression or a Date instance.
	 */
	@Function("DATERANGE")
	@FunctionParameters({
		@FunctionParameter("dateExprObj")})
	public DateRange DATERANGE(Object dateExprObj){
		DateRangeBuilder dateRangeBuilder = null;
		if (dateExprObj instanceof String){
			dateRangeBuilder = new DateRangeBuilder((String)dateExprObj);
		}
		else if (dateExprObj instanceof Date) {
			dateRangeBuilder = new DateRangeBuilder((Date)dateExprObj);
		}
		else {
			throw new IllegalArgumentException(
					"The input parameter for DATERANGE function can be only a String or a Date object");
		}
		
		dateRangeBuilder.set(getReportLocale());
		dateRangeBuilder.set(getReportTimeZone());
		
		return dateRangeBuilder.toDateRange();
	}
	
	/* Internal private methods */
	
	/**
	 * This methods tries to convert a generic object into a java.util.Date instance.
	 * Supported types are for now String, Long values (time millis) and Date subtypes
	 * like for example java.sql.Date.
	 */
	private Date convertDateObject(Object dateObject){
		if (dateObject==null){
			if (log.isDebugEnabled()){
				log.debug("The date object can not be null.");
			}
			return null;
		}
		else if (dateObject instanceof String){
			// Try to convert using the different style for pattern.
			// We use MEDIUM as the first one because it is the DEFAULT
			int formatTypes[] = new int[]{DateFormat.MEDIUM, DateFormat.SHORT, DateFormat.LONG, DateFormat.FULL};
			for (int formatType : formatTypes) {
				try {
					DateFormat df = DateFormat.getDateInstance(formatType, getReportLocale());
					df.setTimeZone(getReportTimeZone());
					return df.parse((String)dateObject);
				} catch (ParseException e) {
					if (log.isDebugEnabled()){
						log.debug("Unable to parse the string as Date using the standard SimpleDateFormat.");
					}
				}
			}
			return null;
		}
		else if (dateObject instanceof Long){
			Calendar cal = Calendar.getInstance(getReportTimeZone(), getReportLocale());
			cal.setTimeInMillis((Long)dateObject);
			return cal.getTime();
		}
		else if (dateObject instanceof Date){
			return (Date)dateObject;
		}
		if (log.isDebugEnabled()){
			log.debug("The specified object is not among the allowed types for Date conversion.");
		}
		return null;
	}

	/*
	 * Tries to recover a specific detail (given by Calendar field type) 
	 * from an input date object.
	 */
	private Integer getCalendarFieldFromDate(Object dateObject,int field){
		Date convertedDate = convertDateObject(dateObject);
		if (convertedDate==null) {
			logCannotConvertToDate();
			return null;
		}
		else{
			Calendar cal = Calendar.getInstance(getReportTimeZone(),getReportLocale());
			cal.setTime(convertedDate);
			return cal.get(field);			
		}
	}
	
	private static void logCannotConvertToDate() {
		if (log.isDebugEnabled()){
			log.debug("Unable to convert to a valid Date instance.");
		}
	}
	
	/*
	 * Tries to retrieve the {@link TimeZone} to be used in the report, 
	 * using the parameter {@link JRParameter#REPORT_TIME_ZONE}. 
	 * If not available it will default the {@link TimeZone#getDefault()} value.
	 * 
	 * @return the {@link TimeZone} instance to be used
	 */
	private TimeZone getReportTimeZone() {
		TimeZone reportTimeZone = TimeZone.getDefault(); 
		if (getContext()!=null) {
			reportTimeZone = (TimeZone) getContext().getParameterValue(JRParameter.REPORT_TIME_ZONE);
		}
		return reportTimeZone;
	}
	
	/*
	 * Tries to retrieve the {@link Locale} to be used in the report, 
	 * using the parameter {@link JRParameter#REPORT_LOCALE}. 
	 * If not available it will default the {@link Locale#getDefault()} value.
	 * 
	 * @return the {@link Locale} instance to be used
	 */
	private Locale getReportLocale() {
		Locale reportLocale = Locale.getDefault(); 
		if (getContext()!=null) {
			reportLocale = (Locale) getContext().getParameterValue(JRParameter.REPORT_LOCALE);
		}
		return reportLocale;
	}
	
	/*
	 * Checks if the specified day is a weekend one.
	 */
	private static boolean isWeekendDay(DayOfWeek day) {
		return DayOfWeek.SATURDAY.equals(day) || DayOfWeek.SUNDAY.equals(day);
	}

	/**
	 * Copied from Java 9.
	 * @deprecated To be removed.
	 */
	private static LocalDate ofInstant(Instant instant, ZoneId zone)
	{
		Objects.requireNonNull(instant, "instant");
		Objects.requireNonNull(zone, "zone");
		ZoneRules rules = zone.getRules();
		ZoneOffset offset = rules.getOffset(instant);
		long localSecond = instant.getEpochSecond() + offset.getTotalSeconds();
		long localEpochDay = Math.floorDiv(localSecond, 86400L);//LocalDate.SECONDS_PER_DAY
		return LocalDate.ofEpochDay(localEpochDay);
	}
}
