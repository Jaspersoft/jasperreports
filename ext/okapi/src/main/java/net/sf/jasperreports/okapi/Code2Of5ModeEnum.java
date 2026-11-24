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
 * The Code 2 of 5 variant, other than Interleaved 2 of 5 and ITF-14, which have elements of their own.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum Code2Of5ModeEnum implements NamedEnum
{
	/**
	 * Code 2 of 5 Matrix, any number of digits.
	 */
	MATRIX("Matrix"),

	/**
	 * Industrial (Standard) Code 2 of 5, any number of digits.
	 */
	INDUSTRIAL("Industrial"),

	/**
	 * The IATA variant, any number of digits.
	 */
	IATA("IATA"),

	/**
	 * Code 2 of 5 Data Logic, any number of digits.
	 */
	DATA_LOGIC("DataLogic"),

	/**
	 * Deutsche Post Leitcode, 13 digits; the check digit is added.
	 */
	DP_LEITCODE("DPLeitcode"),

	/**
	 * Deutsche Post Identcode, 11 digits; the check digit is added.
	 */
	DP_IDENTCODE("DPIdentcode");

	/**
	 *
	 */
	private final transient String name;

	private Code2Of5ModeEnum(String name)
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
	public static Code2Of5ModeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
