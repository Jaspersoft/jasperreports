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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.beanutils2.locale.converters.DateLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Converter class dedicated for the java.util.Date type.
 * <p/>
 * In order to obtain a java.util.Date object from a given String, a JRJavaUtilDateConverter
 * object should be instantiated and it's inherited convert() method should be called. The 
 * final result is provided by the JRJavaUtilDateConverter's parse() invoked method.
 *  <p/>
 * If if any of constructor arguments is null, default values will be provided.
 *  <p/>
 * @see org.apache.commons.beanutils.locale.converters.DateLocaleConverter
 * @author szaharia
 */

public class JRDateLocaleConverter extends DateLocaleConverter<Date> 
{

	private static Log log = LogFactory.getLog(DateLocaleConverter.class);
	
	// holds the timezone's ID
	private TimeZone timeZone;


	/**
	 *
	 */
	public JRDateLocaleConverter(TimeZone timeZone) 
	{
		super(null, null, null, false, false, false);

		this.timeZone = timeZone;
	}

	@Override
	protected Date parse(Object value, String pattern) throws ParseException 
	{
		SimpleDateFormat formatter = getFormatter(pattern, locale);
		if (pattern != null)
		{
			if (localizedPattern) {
				formatter.applyLocalizedPattern(pattern);
			}
			else {
				formatter.applyPattern(pattern);
			}
		}
		return formatter.parse((String) value);
	}

	/**
	 *
	 */
	private SimpleDateFormat getFormatter(String pattern, Locale locale) 
	{
		if(pattern == null) {
			pattern = localizedPattern ? 
				new SimpleDateFormat().toLocalizedPattern() : new SimpleDateFormat().toPattern();
			log.warn("Null pattern was provided, defaulting to: " + pattern);
		}
		SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
		if(timeZone != null)
		{
			format.setTimeZone(timeZone);
		}
		format.setLenient(isLenient());
		return format;
	}

}
