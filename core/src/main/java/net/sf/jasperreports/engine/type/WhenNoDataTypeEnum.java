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
package net.sf.jasperreports.engine.type;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum WhenNoDataTypeEnum implements NamedEnum
{
	/**
	 * Specifies that in case of empty datasources, there will be an empty report.
	 */
	NO_PAGES("NoPages"),

	/**
	 * Specifies that in case of empty datasources, there will be a report with just one blank page.
	 */
	BLANK_PAGE("BlankPage"),

	/**
	 * Specifies that in case of empty datasources, all sections except detail will displayed.
	 */
	ALL_SECTIONS_NO_DETAIL("AllSectionsNoDetail"),

	/**
	 * Specifies that in case of empty datasources, the NoData section will be displayed.
	 */
	NO_DATA_SECTION("NoDataSection");

	/**
	 *
	 */
	private final transient String name;

	private WhenNoDataTypeEnum(String name)
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
	public static WhenNoDataTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
