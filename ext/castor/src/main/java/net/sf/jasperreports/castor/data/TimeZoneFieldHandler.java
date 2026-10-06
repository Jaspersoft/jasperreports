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
package net.sf.jasperreports.castor.data;

import java.util.TimeZone;

import net.sf.jasperreports.engine.util.JRDataUtils;

import org.exolab.castor.mapping.GeneralizedFieldHandler;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated To be removed.
 */
public class TimeZoneFieldHandler extends GeneralizedFieldHandler
{
	/**
	 *
	 */
	public TimeZoneFieldHandler()
	{
		super();
	}
	
	@Override
	public Object convertUponGet(Object value)
	{
		if (value == null)
		{
			return null;
		}
		return JRDataUtils.getTimeZoneId((TimeZone)value);
	}

	@Override
	public Object convertUponSet(Object value)
	{
		if (value == null)
		{
			return null;
		}
		return JRDataUtils.getTimeZone((String)value);
	}
	
	@Override
	public Class<?> getFieldType()
	{
		return TimeZone.class;//FIXMECONTEXT is this correct?
	}

	@Override
	public Object newInstance(Object parent) throws IllegalStateException
	{
		return null;
	}
}
