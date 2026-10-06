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
public enum ResetTypeEnum implements NamedEnum
{
	/**
	 * The variable is initialized only once, at the beginning of the report filling process, with the value returned by
	 * the variable's initial value expression.
	 */
	REPORT("Report"),
	
	/**
	 * The variable is reinitialized at the beginning of each new page.
	 */
	PAGE("Page"),
	
	/**
	 * The variable is reinitialized at the beginning of each new column.
	 */
	COLUMN("Column"),
	
	/**
	 * The variable is reinitialized every time the group specified by the {@link JRVariable#getResetGroup()} method breaks.
	 */
	GROUP("Group"),
	
	/**
	 * The variable will never be initialized using its initial value expression and will only contain values obtained by
	 * evaluating the variable's expression.
	 */
	NONE("None"),
	
	/**
	 * Used internally by the master report page variables to allow the variables to be used in
	 * text fields with {@link EvaluationTimeEnum#AUTO Auto} evaluation time.
	 * 
	 * @see JRVariable#MASTER_CURRENT_PAGE
	 * @see JRVariable#MASTER_TOTAL_PAGES
	 */
	MASTER("Master");

	/**
	 *
	 */
	private final transient String name;

	private ResetTypeEnum(String name)
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
	public static ResetTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static ResetTypeEnum getValueOrDefault(ResetTypeEnum value)
	{
		return value == null ? REPORT : value;
	}
	
	@Override
	public ResetTypeEnum getDefault()
	{
		return REPORT;
	}
}
