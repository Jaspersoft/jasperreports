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
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum PenEnum implements NamedEnum
{
	/**
	 * Constant useful for specifying that the element border will not be drawn.
	 */
	NONE("None"),

	/**
	 * Constant useful for specifying that an element border of 1 pixel.
	 */
	ONE_POINT("1Point"),
	
	/**
	 * Constant useful for specifying that an element border of 2 pixels.
	 */
	TWO_POINT("2Point"),
	
	/**
	 * Constant useful for specifying that an element border of 4 pixels.
	 */
	FOUR_POINT("4Point"),
	
	/**
	 * Constant useful for specifying that an element has a dotted border.
	 */
	DOTTED("Dotted"),
	
	/**
	 * Constant useful for specifying that an element has a thin border (0.5 pixels)
	 */
	THIN("Thin");

	/**
	 *
	 */
	private final transient String name;

	private PenEnum(String name)
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
	public static PenEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
