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
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum TextAdjustEnum implements NamedEnum
{
	/**
	 * A constant value specifying that if the text content of the element does not fit the element size, it is cut
	 * and only partially displayed.
	 */
	CUT_TEXT("CutText"),

	/**
	 * A constant value specifying that if the text content of the element does not fit the element size, the height of the
	 * element is increased so that the entire content fits the element, unless a page/column breaks occurs.
	 */
	STRETCH_HEIGHT("StretchHeight"),
	
	/**
	 * A constant value specifying that if the text content of the element does not fit the element size, the font size
	 * of the content is decreased so that it fits the element entirely.
	 */
	SCALE_FONT("ScaleFont");
	
	/**
	 *
	 */
	private final transient String name;

	private TextAdjustEnum(String name)
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
	public static TextAdjustEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	@Override
	public TextAdjustEnum getDefault()
	{
		return CUT_TEXT;
	}
}
