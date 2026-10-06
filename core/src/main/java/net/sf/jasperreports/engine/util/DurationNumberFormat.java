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
package net.sf.jasperreports.engine.util;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

import net.sf.jasperreports.engine.JRConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DurationNumberFormat extends NumberFormat
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private final DecimalFormat twoDecimalsFormat = new DecimalFormat("00"); 
	
	@Override
	public StringBuffer format(
		double number, 
		StringBuffer toAppendTo,
		FieldPosition pos
		) 
	{
		return format((long)number, toAppendTo, pos);
	}

	@Override
	public StringBuffer format(
		long number, 
		StringBuffer toAppendTo,
		FieldPosition pos
		) 
	{
		
		long seconds = number;
		long hours = seconds / 3600;
		seconds -= hours * 3600;
		long minutes = seconds / 60;
		seconds -= minutes * 60;
		
		toAppendTo.append(twoDecimalsFormat.format(hours));
		toAppendTo.append(":");
		toAppendTo.append(twoDecimalsFormat.format(minutes));
		toAppendTo.append(":");
		toAppendTo.append(twoDecimalsFormat.format(seconds));
		
		return toAppendTo;
	}

	@Override
	public Number parse(String source, ParsePosition parsePosition) 
	{
		if (source != null && source.trim().length() > 0)
		{
			String[] tokens = source.split(":");
			if (tokens.length > 3)
			{
				throw new NumberFormatException();
			}
			else
			{
				long seconds = 3600 * Long.parseLong(tokens[0]);
				if (tokens.length > 1)
				{
					seconds += 60 * Long.parseLong(tokens[1]);
				}
				if (tokens.length > 2)
				{
					seconds += Long.parseLong(tokens[2]);
				}
				parsePosition.setIndex(source.length());
				return seconds;
			}
		}
		return null;
	}
}
