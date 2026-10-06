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
package net.sf.jasperreports.engine.data;

import java.util.Locale;
import java.util.TimeZone;

import net.sf.jasperreports.engine.JRCloneable;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRDataUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class TextDataSourceAttributes implements JRCloneable
{
	private Locale locale;
	private String datePattern;
	private String numberPattern;
	private TimeZone timeZone;

	public TextDataSourceAttributes()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public TextDataSourceAttributes clone()
	{
		try
		{
			return (TextDataSourceAttributes) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	public Locale getLocale()
	{
		return locale;
	}

	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	public void setLocale(String locale)
	{
		setLocale(JRDataUtils.getLocale(locale));
	}

	public String getDatePattern()
	{
		return datePattern;
	}

	public void setDatePattern(String datePattern)
	{
		this.datePattern = datePattern;
	}

	public String getNumberPattern()
	{
		return numberPattern;
	}

	public void setNumberPattern(String numberPattern)
	{
		this.numberPattern = numberPattern;
	}

	public TimeZone getTimeZone()
	{
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone)
	{
		this.timeZone = timeZone;
	}

	public void setTimeZone(String timeZoneId)
	{
		setTimeZone(JRDataUtils.getTimeZone(timeZoneId));
	}

}
