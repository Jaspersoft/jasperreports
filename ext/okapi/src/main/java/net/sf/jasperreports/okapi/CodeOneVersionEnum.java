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
package net.sf.jasperreports.okapi;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * The Code One symbol version; sizes are in rows x columns.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum CodeOneVersionEnum implements NamedEnum
{
	/**
	 * Version A, 16 x 18 modules.
	 */
	A("A"),

	/**
	 * Version B, 22 x 22 modules.
	 */
	B("B"),

	/**
	 * Version C, 28 x 32 modules.
	 */
	C("C"),

	/**
	 * Version D, 40 x 42 modules.
	 */
	D("D"),

	/**
	 * Version E, 52 x 54 modules.
	 */
	E("E"),

	/**
	 * Version F, 70 x 76 modules.
	 */
	F("F"),

	/**
	 * Version G, 104 x 98 modules.
	 */
	G("G"),

	/**
	 * Version H, 148 x 134 modules.
	 */
	H("H"),

	/**
	 * Version S, 8 modules high, for digits only; its width follows the data.
	 */
	S("S"),

	/**
	 * Version T, 16 modules high; its width follows the data.
	 */
	T("T");

	/**
	 *
	 */
	private final transient String name;

	private CodeOneVersionEnum(String name)
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
	public static CodeOneVersionEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
