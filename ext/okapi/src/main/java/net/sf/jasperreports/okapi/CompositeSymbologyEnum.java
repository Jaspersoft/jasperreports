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
 * The linear component of a GS1 Composite barcode.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum CompositeSymbologyEnum implements NamedEnum
{
	/**
	 * UPC-A.
	 */
	UPCA("UPCA"),

	/**
	 * UPC-E.
	 */
	UPCE("UPCE"),

	/**
	 * EAN-13, or EAN-8 for 7 or 8 digit data.
	 */
	EAN("EAN"),

	/**
	 * GS1-128, the only one that takes the CC-C 2D component.
	 */
	CODE128("Code128"),

	/**
	 * GS1 DataBar Omnidirectional, one row.
	 */
	DATABAR("DataBar"),

	/**
	 * GS1 DataBar Stacked, two low rows of a fixed height.
	 */
	DATABAR_STACKED("DataBarStacked"),

	/**
	 * GS1 DataBar Stacked Omnidirectional, two full-height rows.
	 */
	DATABAR_OMNI("DataBarOmni"),

	/**
	 * GS1 DataBar Limited.
	 */
	DATABAR_LIMITED("DataBarLimited"),

	/**
	 * GS1 DataBar Expanded, one row.
	 */
	DATABAR_EXPANDED("DataBarExpanded"),

	/**
	 * GS1 DataBar Expanded Stacked.
	 */
	DATABAR_EXPANDED_STACKED("DataBarExpandedStacked");

	/**
	 *
	 */
	private final transient String name;

	private CompositeSymbologyEnum(String name)
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
	public static CompositeSymbologyEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
