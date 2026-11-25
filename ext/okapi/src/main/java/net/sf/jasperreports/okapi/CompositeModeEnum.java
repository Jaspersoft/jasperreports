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
 * The 2D component of a GS1 Composite barcode. A larger one is used when the data doesn't fit.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum CompositeModeEnum implements NamedEnum
{
	/**
	 * CC-A, the smallest.
	 */
	CC_A("CCA"),

	/**
	 * CC-B.
	 */
	CC_B("CCB"),

	/**
	 * CC-C, only with a {@link CompositeSymbologyEnum#CODE128 GS1-128} linear component.
	 */
	CC_C("CCC");

	/**
	 *
	 */
	private final transient String name;

	private CompositeModeEnum(String name)
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
	public static CompositeModeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
