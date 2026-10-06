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

import java.awt.Color;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum ColorEnum implements NamedValueEnum<Color>
{
	/**
	 *
	 */
	BLACK(Color.black, "black"),

	/**
	 *
	 */
	BLUE(Color.blue, "blue"),

	/**
	 *
	 */
	CYAN(Color.cyan, "cyan"),

	/**
	 *
	 */
	DARK_GRAY(Color.darkGray, "darkGray"),

	/**
	 *
	 */
	GRAY(Color.gray, "gray"),

	/**
	 *
	 */
	GREEN(Color.green, "green"),

	/**
	 *
	 */
	LIGHT_GRAY(Color.lightGray, "lightGray"),

	/**
	 *
	 */
	MAGENTA(Color.magenta, "magenta"),

	/**
	 *
	 */
	ORANGE(Color.orange, "orange"),

	/**
	 *
	 */
	PINK(Color.pink, "pink"),

	/**
	 *
	 */
	RED(Color.red, "red"),

	/**
	 *
	 */
	YELLOW(Color.yellow, "yellow"),

	/**
	 *
	 */
	WHITE(Color.white, "white");

	/**
	 *
	 */
	private final transient Color color;
	private final transient String name;

	private ColorEnum(Color color, String name)
	{
		this.color = color;
		this.name = name;
	}

	/**
	 *
	 */
	public final Color getColor()
	{
		return color;
	}
	
	@Override
	public final Color getValue()
	{
		return color;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 *
	 */
	public static ColorEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static ColorEnum getByColor(Color color)
	{
		return EnumUtil.getByValue(values(), color);
	}
}
