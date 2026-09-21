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
package net.sf.jasperreports.olap.mapping;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class Axis
{
	private static final String EXCEPTION_MESSAGE_KEY_OLAP_AXIS_NOT_FOUND = "data.olap.axis.not.found";
	
	public static final String AXIS0 = "Columns";
	public static final String AXIS1 = "Rows";
	public static final String AXIS2 = "Pages";
	public static final String AXIS3 = "Chapters";
	public static final String AXIS4 = "Sections";
	public static final String[] AXIS_NAMES = new String[]{AXIS0, AXIS1, AXIS2, AXIS3, AXIS4};
	
	public static final int getAxisIdx (String name)
	{
		for (int i = 0; i < AXIS_NAMES.length; i++)
		{
			if (AXIS_NAMES[i].equals(name))
			{
				return i;
			}
		}
		
		throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_OLAP_AXIS_NOT_FOUND,
				new Object[]{name});
	}
	
	private final int idx;
	
	public Axis (int idx)
	{
		this.idx = idx;
	}
	
	public Axis (String name)
	{
		this.idx = getAxisIdx(name);
	}
	
	public int getIdx ()
	{
		return idx;
	}

	@Override
	public String toString ()
	{
		return "Axis(" + idx +")";
	}
}
