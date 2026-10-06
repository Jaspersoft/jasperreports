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

import net.sf.jasperreports.engine.JRVariable;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum IncrementTypeEnum implements NamedEnum
{
	/**
	 * The variable never gets incremented during the report-filling process.
	 */
	REPORT("Report"),
	
	/**
	 * The variable is incremented with each new page.
	 */
	PAGE("Page"),
	
	/**
	 * The variable is incremented with each new column.
	 */
	COLUMN("Column"),
	
	/**
	 * The variable is incremented every time the group specified by the {@link JRVariable#getIncrementGroup()} method breaks.
	 */
	GROUP("Group"),
	
	/**
	 * The variable is incremented with every record during the iteration through the data source.
	 */
	NONE("None");

	/**
	 *
	 */
	private final transient String name;

	private IncrementTypeEnum(String name)
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
	public static IncrementTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static IncrementTypeEnum getValueOrDefault(IncrementTypeEnum value)
	{
		return value == null ? NONE : value;
	}
	
	@Override
	public IncrementTypeEnum getDefault()
	{
		return NONE;
	}
}
