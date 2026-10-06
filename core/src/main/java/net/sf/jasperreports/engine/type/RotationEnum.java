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
public enum RotationEnum implements NamedEnum
{
	/**
	 * Constant useful for displaying the text or image without rotating it.
	 */
	NONE("None"),

	/**
	 * Constant useful for rotating the text or image 90 degrees counter clockwise.
	 */
	LEFT("Left"),
	
	/**
	 * Constant useful for rotating the text or image 90 degrees clockwise.
	 */
	RIGHT("Right"),
	
	/**
	 * Constant useful for rotating the text or image 180 degrees.
	 */
	UPSIDE_DOWN("UpsideDown");
	
	/**
	 *
	 */
	private final transient String name;

	private RotationEnum(String name)
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
	public static RotationEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
