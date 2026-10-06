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
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum OrientationEnum implements NamedEnum
{
	/**
	 * Specifies a portrait orientation. This is used mostly to inform printers of page layouts.
	 */
	PORTRAIT("Portrait"),

	/**
	 * Specifies a landscape orientation. This is used mostly to inform printers of page layouts.
	 */
	LANDSCAPE("Landscape");
	
	/**
	 *
	 */
	private final transient String name;

	private OrientationEnum(String name)
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
	public static OrientationEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static OrientationEnum getValueOrDefault(OrientationEnum value)
	{
		return value == null ? PORTRAIT : value;
	}
	
	@Override
	public OrientationEnum getDefault()
	{
		return PORTRAIT;
	}
}
