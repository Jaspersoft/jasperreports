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
package net.sf.jasperreports.export.type;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum HtmlBorderCollapseEnum implements NamedEnum
{
	/**
	 * Borders are separated; each cell will display its own borders. 
	 */
	SEPARATE("separate"),

	/**
	 * Borders are collapsed into a single border when possible (border-spacing and empty-cells properties have no effect). 
	 */
	COLLAPSE("collapse"),

	/**
	 * Sets the border collapse property to its default value. 
	 */
	INITIAL("initial"),

	/**
	 * Inherits the border collapse property from its parent element. 
	 */
	INHERIT("inherit");
	
	/**
	 *
	 */
	private final transient String name;

	private HtmlBorderCollapseEnum(String name)
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
	public static HtmlBorderCollapseEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
