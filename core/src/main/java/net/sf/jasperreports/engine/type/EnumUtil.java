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
package net.sf.jasperreports.engine.type;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class EnumUtil
{
	/**
	 *
	 */
	public static <T extends NamedValueEnum<?>> T getByValue(T[] values, Object value)
	{
		if (values != null && value != null)
		{
			for (T e:values)
			{
				if (value.equals(e.getValue()))
				{
					return e;
				}
			}
		}
		return null;
	}

	/**
	 *
	 */
	public static <T extends NamedEnum> T getEnumByName(T[] values, String name)
	{
		if (values != null && name != null)
		{
			for (T e:values)
			{
				if (name.equals(e.getName()))
				{
					return e;
				}
			}
		}
		return null;
	}

	/**
	 *
	 */
	public static <T extends Enum<?>> T getByConstantName(T[] values, String name)
	{
		if (values != null && name != null)
		{
			for (T e:values)
			{
				if (name.equals(e.name()))
				{
					return e;
				}
			}
		}
		return null;
	}
	
	/**
	 *
	 */
	private EnumUtil()
	{
	}
}
