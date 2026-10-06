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
package net.sf.jasperreports.barcode4j;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedValueEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum OrientationEnum implements NamedValueEnum<Integer>
{
	/**
	 * 
	 */
	UP(0, "up"),

	/**
	 * 
	 */
	LEFT(90, "left"),

	/**
	 * 
	 */
	DOWN(180, "down"),

	/**
	 * 
	 */
	RIGHT(270, "right");

	/**
	 *
	 */
	private final transient int value;
	private final transient String name;

	private OrientationEnum(int value, String name)
	{
		this.value = value;
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public Integer getValue()
	{
		return value;
	}
	
	/**
	 *
	 */
	public static OrientationEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static OrientationEnum getByValue(Integer value)
	{
		return EnumUtil.getByValue(values(), value);
	}
	
	/**
	 *
	 */
	public static OrientationEnum getValueOrDefault(OrientationEnum value)
	{
		return value == null ? UP : value;
	}
	
	@Override
	public OrientationEnum getDefault()
	{
		return UP;
	}
}
