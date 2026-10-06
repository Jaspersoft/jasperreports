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
package net.sf.jasperreports.charts.type;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum ValueLocationEnum implements NamedEnum
{
	/**
	 * The value should not be displayed.
	 */
	NONE("none"),
	
	/**
	 * The value should be displayed to the left of the thermometer.
	 */
	LEFT("left"),
	
	/**
	 * The value should be displayed to the right of the thermometer.
	 */
	RIGHT("right"),
	
	/**
	 * The value should be displayed in the bulb of the thermometer.  When
	 * using this option make sure the font is small enough or the value short
	 * enough so the value fits in the bulb.
	 */
	BULB("bulb");
	

	/**
	 *
	 */
	private final transient String name;

	private ValueLocationEnum(String name)
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
	public static ValueLocationEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
