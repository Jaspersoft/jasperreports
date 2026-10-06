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
public enum BandTypeEnum implements NamedEnum
{
	/**
	 *
	 */
	UNKNOWN("unknown"),

	/**
	 *
	 */
	BACKGROUND("background"),

	/**
	 *
	 */
	TITLE("title"),

	/**
	 *
	 */
	PAGE_HEADER("pageHeader"),

	/**
	 *
	 */
	COLUMN_HEADER("columnHeader"),

	/**
	 *
	 */
	GROUP_HEADER("groupHeader"),

	/**
	 *
	 */
	DETAIL("detail"),

	/**
	 *
	 */
	GROUP_FOOTER("groupFooter"),

	/**
	 *
	 */
	COLUMN_FOOTER("columnFooter"),

	/**
	 *
	 */
	PAGE_FOOTER("pageFooter"),

	/**
	 *
	 */
	LAST_PAGE_FOOTER("lastPageFooter"),

	/**
	 *
	 */
	SUMMARY("summary"),

	/**
	 *
	 */
	NO_DATA("noData");

	/**
	 *
	 */
	private final transient String name;

	private BandTypeEnum(String name)
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
	public static BandTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
