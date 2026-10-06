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
public enum FilterTypeNumericOperatorsEnum implements NamedEnum
{
	EQUALS("Equals"),
	
	DOES_NOT_EQUAL("Does Not Equal"),

	GREATER_THAN("Greater Than"),

	GREATER_THAN_EQUAL_TO("Greater Than or Equal to"),
	
	LESS_THAN("Less Than"),
	
	LESS_THAN_EQUAL_TO("Less Than or Equal to"),
	
	IS_BETWEEN("Is Between"),
	
	IS_NOT_BETWEEN("Is not between"),

	IS_NULL("Is Null"),

	IS_NOT_NULL("Is not Null");

	/**
	 *
	 */
	private final transient String name;

	private FilterTypeNumericOperatorsEnum(String name)
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
	public static FilterTypeNumericOperatorsEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static FilterTypeNumericOperatorsEnum getByEnumConstantName(String name)
	{
		return EnumUtil.getByConstantName(values(), name);
	}
}
