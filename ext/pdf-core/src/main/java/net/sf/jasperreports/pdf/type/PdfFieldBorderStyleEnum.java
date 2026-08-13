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


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum PdfFieldBorderStyleEnum implements NamedEnum
{
	/**
	 * Constant useful for specifying the solid border for the input field in PDF form.
	 */
	SOLID("Solid"),

	/**
	 * Constant useful for specifying the dashed border for the input field in PDF form.
	 */
	DASHED("Dashed"),

	/**
	 * Constant useful for specifying the beveled border for the input field in PDF form.
	 */
	BEVELED("Beveled"),

	/**
	 * Constant useful for specifying the inset border for the input field in PDF form.
	 */
	INSET("Inset"),

	/**
	 * Constant useful for specifying the underline border for the input field in PDF form.
	 */
	UNDERLINE("Underline");
	
	
	/**
	 *
	 */
	private final transient String name;

	private PdfFieldBorderStyleEnum(String name)
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
	public static PdfFieldBorderStyleEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
