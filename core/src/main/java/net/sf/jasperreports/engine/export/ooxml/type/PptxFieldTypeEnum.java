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
package net.sf.jasperreports.engine.export.ooxml.type;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum PptxFieldTypeEnum implements NamedEnum
{
	/**
	 *
	 */
	SLIDENUM("slidenum"),

	/**
	 *
	 */
	DATETIME("datetime"),

	DATETIME1("datetime1"),
	
	DATETIME2("datetime2"),
	
	DATETIME3("datetime3"),
	
	DATETIME4("datetime4"),
	
	DATETIME5("datetime5"),
	
	DATETIME6("datetime6"),
	
	DATETIME7("datetime7"),
	
	DATETIME8("datetime8"),
	
	DATETIME9("datetime9"),
	
	DATETIME10("datetime10"),
	
	DATETIME11("datetime11"),
	
	DATETIME12("datetime12"),
	
	DATETIME13("datetime13");

	/**
	 *
	 */
	private final transient String name;

	private PptxFieldTypeEnum(String name)
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
	public static PptxFieldTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
