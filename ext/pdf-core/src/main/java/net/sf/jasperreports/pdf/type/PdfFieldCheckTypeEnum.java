/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.pdf.type;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;
import net.sf.jasperreports.engine.type.NamedValueEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum PdfFieldCheckTypeEnum implements NamedEnum
{
	/**
	 * Constant useful for specifying the checkbox type for the check input field in PDF form.
	 */
	CHECK("Check"),

	/**
	 * Constant useful for specifying the circle type for the check input field in PDF form.
	 */
	CIRCLE("Circle"),

	/**
	 * Constant useful for specifying the cross type for the check input field in PDF form.
	 */
	CROSS("Cross"),

	/**
	 * Constant useful for specifying the diamond type for the check input field in PDF form.
	 */
	DIAMOND("Diamond"),

	/**
	 * Constant useful for specifying the square type for the check input field in PDF form.
	 */
	SQUARE("Square"),

	/**
	 * Constant useful for specifying the star type for the check input field in PDF form.
	 */
	STAR("Star");
	
	
	/**
	 *
	 */
	private final transient String name;

	private PdfFieldCheckTypeEnum(String name)
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
	public static PdfFieldCheckTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
