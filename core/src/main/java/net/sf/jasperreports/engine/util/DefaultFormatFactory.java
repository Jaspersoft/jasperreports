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

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DefaultFormatFactory implements FormatFactory
{
	private static final Log log = LogFactory.getLog(DefaultFormatFactory.class);

	public static final String EXCEPTION_MESSAGE_KEY_FACTORY_INSTANCE_ERROR = "util.format.factory.instance.error";
	public static final String EXCEPTION_MESSAGE_KEY_FACTORY_LOADING_ERROR = "util.format.factory.loading.error";

	/**
	 * Used in the date pattern to specify the default style.
	 * @see java.text.DateFormat#DEFAULT
	 */
	public static final String STANDARD_DATE_FORMAT_DEFAULT = "default";

	/**
	 * Used in the date pattern to specify the short style.
	 * @see java.text.DateFormat#SHORT
	 */
	public static final String STANDARD_DATE_FORMAT_SHORT = "short";

	/**
	 * Used in the date pattern to specify the medium style.
	 * @see java.text.DateFormat#MEDIUM
	 */
	public static final String STANDARD_DATE_FORMAT_MEDIUM = "medium";

	/**
	 * Used in the date pattern to specify the long style.
	 * @see java.text.DateFormat#LONG
	 */
	public static final String STANDARD_DATE_FORMAT_LONG = "long";

	/**
	 * Used in the date pattern to specify the full style.
	 * @see java.text.DateFormat#FULL
	 */
	public static final String STANDARD_DATE_FORMAT_FULL = "full";

	/**
	 * Used in the date pattern to specify that the date or time should not be included.
	 */
	public static final String STANDARD_DATE_FORMAT_HIDE = "hide";

	/**
	 * Used in the date format pattern to separate the date and time styles.
	 */
	public static final String STANDARD_DATE_FORMAT_SEPARATOR = ",";

	/**
	 * Number pattern to show integer value as duration expressed in hours:minutes:seconds.
	 */
	public static final String STANDARD_NUMBER_FORMAT_DURATION = "[h]:mm:ss";


	@Override
	public DateFormat createDateFormat(String pattern, Locale locale, TimeZone tz)
	{
		int[] dateStyle = null;
		int[] timeStyle = null;
		if (pattern != null && pattern.trim().length() > 0)
		{			
			int sepIdx = pattern.indexOf(STANDARD_DATE_FORMAT_SEPARATOR);
			String dateTok = sepIdx < 0 ? pattern : pattern.substring(0, sepIdx);
			dateStyle = getDateStyle(dateTok);
			if (dateStyle != null)
			{
				if (sepIdx >= 0)
				{
					String timeTok = pattern.substring(sepIdx + STANDARD_DATE_FORMAT_SEPARATOR.length());
					timeStyle = getDateStyle(timeTok);
				}
				else
				{
					timeStyle = dateStyle;
				}
			}
		}
		
		DateFormat format;
		if (dateStyle != null && timeStyle != null)
		{
			format = getDateFormat(dateStyle, timeStyle, locale);
		}
		else
		{			
			if (locale == null)
			{
				format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
			}
			else
			{
				format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
			}
			if (
				pattern != null && pattern.trim().length() > 0
				&& format instanceof SimpleDateFormat
				)
			{
				((SimpleDateFormat) format).applyPattern(pattern);
			}
		}
		
		if (tz != null)
		{
			format.setTimeZone(tz);
		}

		return format;
	}


	protected static int[] getDateStyle(String pattern)
	{
		if (pattern.equalsIgnoreCase(STANDARD_DATE_FORMAT_DEFAULT))
		{
			return new int[]{DateFormat.DEFAULT};
		}
		else if (pattern.equalsIgnoreCase(STANDARD_DATE_FORMAT_SHORT))
		{
			return new int[]{DateFormat.SHORT};
		}
		else if (pattern.equalsIgnoreCase(STANDARD_DATE_FORMAT_MEDIUM))
		{
			return new int[]{DateFormat.MEDIUM};
		}
		else if (pattern.equalsIgnoreCase(STANDARD_DATE_FORMAT_LONG))
		{
			return new int[]{DateFormat.LONG};
		}
		else if (pattern.equalsIgnoreCase(STANDARD_DATE_FORMAT_FULL))
		{
			return new int[]{DateFormat.FULL};
		}
		else if (pattern.equalsIgnoreCase(STANDARD_DATE_FORMAT_HIDE))
		{
			return new int[0];
		}
		else
		{
			return null;
		}
	}

	
	protected static DateFormat getDateFormat(int[] dateStyle, int[] timeStyle, Locale locale)
	{
		if (dateStyle.length == 0)
		{
			if (timeStyle.length == 0)
			{
				return new SimpleDateFormat("");
			}

			return locale == null ? 
					DateFormat.getTimeInstance(timeStyle[0]) :
						DateFormat.getTimeInstance(timeStyle[0], locale);
		}

		if (timeStyle.length == 0)
		{
			return locale == null ? 
					DateFormat.getDateInstance(dateStyle[0]) :
					DateFormat.getDateInstance(dateStyle[0], locale);
		}

		return locale == null ? 
				DateFormat.getDateTimeInstance(dateStyle[0], timeStyle[0]) :
				DateFormat.getDateTimeInstance(dateStyle[0], timeStyle[0], locale);
	}
	
	
	@Override
	public NumberFormat createNumberFormat(String pattern, Locale locale)
	{
		NumberFormat format = null;
		if (pattern != null && pattern.trim().length() > 0)
		{
			if (STANDARD_NUMBER_FORMAT_DURATION.equals(pattern))
			{
				format = new DurationNumberFormat();
			}
			else
			{
				if (locale == null)
				{
					format = NumberFormat.getNumberInstance();
				}
				else
				{
					format = NumberFormat.getNumberInstance(locale);
				}
				
				if (format instanceof DecimalFormat)
				{
					if (log.isDebugEnabled())
					{
						log.debug("Applying pattern to DecimalFormat: " + pattern);
					}
					((DecimalFormat) format).applyPattern(pattern);
				}
			}
		}
		return format;
	}
	
	
	public static FormatFactory createFormatFactory(String formatFactoryClassName)
	{
		FormatFactory formatFactory = null;
		
		if (formatFactoryClassName != null)
		{
			try
			{
				Class<?> formatFactoryClass = JRClassLoader.loadClassForName(formatFactoryClassName);	
				formatFactory = (FormatFactory) formatFactoryClass.getDeclaredConstructor().newInstance();
			}
			catch (ClassNotFoundException e)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_FACTORY_LOADING_ERROR,
						new Object[]{formatFactoryClassName},
						e);
			}
			catch (NoSuchMethodException | InvocationTargetException 
				| IllegalAccessException | InstantiationException e)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_FACTORY_INSTANCE_ERROR,
						new Object[]{formatFactoryClassName},
						e);
			}
		}
		else
		{
			formatFactory = new DefaultFormatFactory();
		}
		
		return formatFactory;
	}


}
