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
 * The check digits of an MSI Plessey barcode.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum MSIPlesseyCheckDigitEnum implements NamedEnum
{
	/**
	 * No check digit.
	 */
	NONE("None"),

	/**
	 * A modulo 10 check digit.
	 */
	MOD10("Mod10"),

	/**
	 * Two modulo 10 check digits.
	 */
	MOD10_MOD10("Mod10Mod10"),

	/**
	 * A modulo 11 check digit.
	 */
	MOD11("Mod11"),

	/**
	 * A modulo 11 and a modulo 10 check digit.
	 */
	MOD11_MOD10("Mod11Mod10");

	/**
	 *
	 */
	private final transient String name;

	private MSIPlesseyCheckDigitEnum(String name)
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
	public static MSIPlesseyCheckDigitEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
