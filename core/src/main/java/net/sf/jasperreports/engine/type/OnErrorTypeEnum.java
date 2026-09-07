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
package net.sf.jasperreports.engine.type;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum OnErrorTypeEnum implements NamedEnum
{
	/**
	 * A constant used for specifying that the engine should raise an exception if the image is not found.
	 */
	ERROR("Error"),

	/**
	 * A constant used for specifying that the engine should display blank space if the image is not found.
	 */
	BLANK("Blank"),
	
	/**
	 * A constant used for specifying that the engine should display a replacement icon if the image is not found.
	 */
	ICON("Icon");
	
	
	/**
	 *
	 */
	private final transient String name;

	private OnErrorTypeEnum(String name)
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
	public static OnErrorTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static OnErrorTypeEnum getValueOrDefault(OnErrorTypeEnum value)
	{
		return value == null ? ERROR : value;
	}
	
	@Override
	public OnErrorTypeEnum getDefault()
	{
		return ERROR;
	}
}
