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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRConstants;

/**
 * <p>Implementation of {@link DateRange} for fixed, non relative date.</p>
 *
 * @author Sergey Prilukin
 */
public class FixedDate extends AbstractDateRange 
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String DATE_PATTERN = "yyyy-MM-dd";

	private String datePattern;
	private Date value;

	public FixedDate(String expression) {
		this(expression, null, null);
	}

	public FixedDate(String expression, TimeZone timeZone, String datePattern) {
		super(expression, timeZone);
		this.datePattern = datePattern;
		this.validateExpression(expression);
	}

	public FixedDate(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("NULL could not be used as a value");
		}

		this.value = date;
	}

	@Override
	protected void validateExpression(String expression) throws InvalidDateRangeExpressionException {
		super.validateExpression(expression);
		try {
			parse(expression);
		} catch (Exception pe) {
			throw new InvalidDateRangeExpressionException(expression);
		}
	}

	@Override
	public Date getStart() {
		if (value == null) {
			try {
				value = parse(expression);
			} catch (Exception e) {
				//Should not happen;
				throw new RuntimeException(e);
			}
		}

		return value;
	}

	@Override
	public Date getEnd() {
		return getStart();
	}

	private Date parse(String dateAsString, String pattern) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		dateFormat.setLenient(false);

		if (this.timeZone != null) {
			dateFormat.setTimeZone(this.timeZone);
		}

		return dateFormat.parse(dateAsString);
	}

	protected String getDefaultPattern() {
		return DATE_PATTERN;
	}

	private Date parse(String dateAsString) throws Exception {
		if (this.datePattern != null) {
			return parse(dateAsString, this.datePattern);
		}

		Date result = parse(dateAsString, getDefaultPattern());
		this.datePattern = getDefaultPattern();

		return result;
	}
}
