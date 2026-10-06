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
public enum LineDirectionEnum implements NamedEnum
{
	/**
	 * Constant used for specifying that the line starts from the top and goes towards the bottom.
	 */
	TOP_DOWN("TopDown"),

	/**
	 * Constant used for specifying that the line starts from the bottom and goes towards the top.
	 */
	BOTTOM_UP("BottomUp");
	
	/**
	 *
	 */
	private final transient String name;

	private LineDirectionEnum(String name)
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
	public static LineDirectionEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	@Override
	public LineDirectionEnum getDefault()
	{
		return TOP_DOWN;
	}
}
