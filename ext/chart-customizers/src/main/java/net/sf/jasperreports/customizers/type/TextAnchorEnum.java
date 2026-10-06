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
package net.sf.jasperreports.customizers.type;

import org.jfree.chart.ui.TextAnchor;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum TextAnchorEnum implements NamedEnum
{
	/**
	 *
	 */
	TOP_CENTER(TextAnchor.TOP_CENTER, "topCenter"),

	/**
	 *
	 */
	TOP_LEFT(TextAnchor.TOP_LEFT, "topLeft"),

	/**
	 *
	 */
	TOP_RIGHT(TextAnchor.TOP_RIGHT, "topRight"),

	/**
	 *
	 */
	HALF_ASCENT_CENTER(TextAnchor.HALF_ASCENT_CENTER, "halfAscentCenter"),

	/**
	 *
	 */
	HALF_ASCENT_LEFT(TextAnchor.HALF_ASCENT_LEFT, "halfAscentLeft"),

	/**
	 *
	 */
	HALF_ASCENT_RIGTH(TextAnchor.HALF_ASCENT_RIGHT, "halfAscentRight"),

	/**
	 *
	 */
	CENTER(TextAnchor.CENTER, "center"),

	/**
	 *
	 */
	CENTER_LEFT(TextAnchor.CENTER_LEFT, "centerLeft"),

	/**
	 *
	 */
	CENTER_RIGHT(TextAnchor.CENTER_RIGHT, "centerRight"),

	/**
	 *
	 */
	BOTTOM_CENTER(TextAnchor.BOTTOM_CENTER, "bottomCenter"),

	/**
	 *
	 */
	BASELINE_CENTER(TextAnchor.BASELINE_CENTER, "baselineCenter"),

	/**
	 *
	 */
	BASELINE_LEFT(TextAnchor.BASELINE_LEFT, "baselineLeft"),

	/**
	 *
	 */
	BASELINE_RIGHT(TextAnchor.BASELINE_RIGHT, "baselineRight"),

	/**
	 *
	 */
	BOTTOM_LEFT(TextAnchor.BOTTOM_LEFT, "bottomLeft"),

	/**
	 *
	 */
	BOTTOM_RIGHT(TextAnchor.BOTTOM_RIGHT, "bottomRigth");


	/**
	 *
	 */
	private final transient TextAnchor value;
	private final transient String name;

	private TextAnchorEnum(TextAnchor textAnchor, String name)
	{
		this.value = textAnchor;
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
	public final TextAnchor getTextAnchor()
	{
		return this.value;
	}

	/**
	 *
	 */
	public static TextAnchorEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
