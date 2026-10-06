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
package net.sf.jasperreports.customizers.type;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum StrokeStyleEnum implements NamedEnum
{
	/**
	 * Constant useful for specifying solid line style.
	 */
	SOLID("solid"),

	/**
	 * Constant useful for specifying dashed line style.
	 */
	DASHED("dashed"),
	
	/**
	 * Constant useful for specifying dotted line style.
	 */
	DOTTED("dotted");
	
	/**
	 *
	 */
	private final transient String name;

	private StrokeStyleEnum(String name)
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
	public static StrokeStyleEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
