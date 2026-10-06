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

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.util.ObjectUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleTextFormat implements TextFormat, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	private String valueClassName;
	private String pattern;
	private String formatFactoryClass;
	private String localeCode;
	private String timeZoneId;

	@Override
	public int hashCode()
	{
		int hash = 13;
		hash = 67 * hash + (valueClassName == null ? 0 : valueClassName.hashCode());
		hash = 67 * hash + (pattern == null ? 0 : pattern.hashCode());
		hash = 67 * hash + (formatFactoryClass == null ? 0 : formatFactoryClass.hashCode());
		hash = 67 * hash + (localeCode == null ? 0 : localeCode.hashCode());
		hash = 67 * hash + (timeZoneId == null ? 0 : timeZoneId.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SimpleTextFormat))
		{
			return false;
		}
		
		SimpleTextFormat f = (SimpleTextFormat) obj;
		return ObjectUtils.equals(valueClassName, f.valueClassName)
				&& ObjectUtils.equals(pattern, f.pattern)
				&& ObjectUtils.equals(formatFactoryClass, f.formatFactoryClass)
				&& ObjectUtils.equals(localeCode, f.localeCode)
				&& ObjectUtils.equals(timeZoneId, f.timeZoneId);
	}

	@Override
	public String toString()
	{
		return "{" + valueClassName + ", "
				+ pattern + ","
				+ formatFactoryClass + ","
				+ localeCode + ","
				+ timeZoneId + "}";
	}
	
	@Override
	public String getValueClassName()
	{
		return valueClassName;
	}

	/**
	 *
	 */
	public void setValueClassName(String valueClassName)
	{
		this.valueClassName = valueClassName;
	}

	@Override
	public String getPattern()
	{
		return pattern;
	}

	/**
	 *
	 */
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	@Override
	public String getFormatFactoryClass()
	{
		return formatFactoryClass;
	}

	/**
	 *
	 */
	public void setFormatFactoryClass(String formatFactoryClass)
	{
		this.formatFactoryClass = formatFactoryClass;
	}

	@Override
	public String getLocaleCode()
	{
		return localeCode;
	}

	/**
	 *
	 */
	public void setLocaleCode(String localeCode)
	{
		this.localeCode = localeCode;
	}

	@Override
	public String getTimeZoneId()
	{
		return timeZoneId;
	}

	/**
	 *
	 */
	public void setTimeZoneId(String timeZoneId)
	{
		this.timeZoneId = timeZoneId;
	}
}
