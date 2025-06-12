/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
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
package net.sf.jasperreports.components.table;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum WhenNoDataTypeTableEnum implements NamedEnum
{
	/**
	 * Specifies that in case of empty datasources, the table output will be blank.
	 */
	BLANK("Blank"),

	/**
	 * Specifies that in case of empty datasources, the NoData cell of the table will be displayed.
	 */
	NO_DATA_CELL("NoDataCell"),

	/**
	 * Specifies that in case of empty datasources, all table sections except the detail will displayed.
	 */
	ALL_SECTIONS_NO_DETAIL("AllSectionsNoDetail");

	/**
	 *
	 */
	private final transient String name;

	private WhenNoDataTypeTableEnum(String name)
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
	public static WhenNoDataTypeTableEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
