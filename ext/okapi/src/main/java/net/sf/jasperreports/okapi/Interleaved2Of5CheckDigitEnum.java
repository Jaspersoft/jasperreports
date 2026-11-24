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
 * The check digit of an Interleaved 2 of 5 barcode.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum Interleaved2Of5CheckDigitEnum implements NamedEnum
{
	/**
	 * No check digit.
	 */
	NONE("None"),

	/**
	 * A modulo 10 check digit with weights 3 and 1.
	 */
	MOD10("Mod10");

	/**
	 *
	 */
	private final transient String name;

	private Interleaved2Of5CheckDigitEnum(String name)
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
	public static Interleaved2Of5CheckDigitEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
