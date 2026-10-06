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
package net.sf.jasperreports.engine.fill;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;

/**
 * {@link MessageFormat} does not work with classes of the java.time package such as {@link LocalDate} This is by design and won't be fixed:
 * https://bugs.openjdk.org/browse/JDK-8016743
 * 
 * This class wraps a {@link MessageFormat} and converts classes of the java.time package into {@link Date} objects
 * 
 * @author Guillaume Toison
 */
public class MessageFormatWrapper
{

	/**
	 * @param pattern The message pattern, for instance "since {0, date, dd-MM-yy}"
	 * @param locale The locale, must be non-null
	 * @param arguments the message parameters
	 * @return the constructed message
	 * 
	 * @see MessageFormat#format(java.lang.Object[],java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public static String format(String pattern, Locale locale, Object... arguments)
	{
		MessageFormat messageFormat = new MessageFormat("");
		messageFormat.setLocale(locale);
		messageFormat.applyPattern(pattern);

		Format[] formats = messageFormat.getFormats();
		for (int i = 0; i < formats.length; i++)
		{
			Format format = formats[i];
			if (format instanceof DateFormat)
			{
				messageFormat.setFormat(i, new DateFormatWrapper(format));
			}
		}

		return messageFormat.format(arguments);
	}

	private static class DateFormatWrapper extends Format
	{
		private static final long serialVersionUID = 1L;

		private final Format format;

		private DateFormatWrapper(Format format)
		{
			this.format = format;
		}

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,	FieldPosition pos)
		{
			if (obj instanceof TemporalAccessor)
			{
				TemporalAccessor ta = (TemporalAccessor) obj;

				final Instant instant;
				if (obj instanceof LocalDate)
				{
					LocalDate date = (LocalDate) obj;

					instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
				} 
				else if (obj instanceof LocalDateTime)
				{
					LocalDateTime dateTime = (LocalDateTime) obj;

					instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
				}
				else if (obj instanceof ZonedDateTime)
				{
					ZonedDateTime dateTime = (ZonedDateTime) obj;

					instant = dateTime.toInstant();
				}
				else
				{
					instant = Instant.from(ta);
				}
				obj = Date.from(instant);
			}
			return format.format(obj, toAppendTo, pos);
		}

		@Override
		public Object parseObject(String source, ParsePosition pos)
		{
			return format.parseObject(source, pos);
		}
	}
}