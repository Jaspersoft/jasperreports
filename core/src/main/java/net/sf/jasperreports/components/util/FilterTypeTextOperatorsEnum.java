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
package net.sf.jasperreports.components.util;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum FilterTypeTextOperatorsEnum implements NamedEnum
{
	EQUALS("Equals"),
	
	IS_NOT_EQUAL_TO("Is not Equal to"),

	CONTAINS("Contains"),

	DOES_NOT_CONTAIN("Does not Contain"),
	
	STARTS_WITH("Starts With"),
	
	DOES_NOT_START_WITH("Does not Start With"),
	
	ENDS_WITH("Ends With"),
	
	DOES_NOT_END_WITH("Does not End With"),

	IS_NULL("Is Null"),

	IS_NOT_NULL("Is not Null");

	/**
	 *
	 */
	private final transient String name;

	private FilterTypeTextOperatorsEnum(String name)
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
	public static FilterTypeTextOperatorsEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static FilterTypeTextOperatorsEnum getByEnumConstantName(String name)
	{
		return EnumUtil.getByConstantName(values(), name);
	}
}
