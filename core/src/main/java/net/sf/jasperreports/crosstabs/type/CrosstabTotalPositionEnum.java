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
package net.sf.jasperreports.crosstabs.type;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum CrosstabTotalPositionEnum implements NamedEnum
{
	/**
	 * Constant indicating that total are not required for this bucket.
	 */
	NONE("None"),
	
	/**
	 * Constants indicating that totals are to be positioned before the other buckets.
	 */
	START("Start"),
	
	/**
	 * Constants indicating that totals are to be positioned at the end of the other buckets.
	 */
	END("End");

	/**
	 *
	 */
	private final transient String name;

	private CrosstabTotalPositionEnum(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 *
	 */
	public static CrosstabTotalPositionEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static CrosstabTotalPositionEnum getValueOrDefault(CrosstabTotalPositionEnum value)
	{
		return value == null ? NONE : value;
	}
	
	@Override
	public CrosstabTotalPositionEnum getDefault()
	{
		return NONE;
	}
}
