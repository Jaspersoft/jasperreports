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

import net.sf.jasperreports.engine.JRFrame;

/**
 * Specifies the way the frame border is to be drawn when the frame element splits.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRFrame#getBorderSplitType()
 */
public enum BorderSplitType implements NamedEnum
{
	/**
	 * When the frame splits, the bottom border of the first fragment and the top border of the second fragment are not drawn.
	 */
	NO_BORDERS("NoBorders"),
	/**
	 * When the frame splits, both fragments are drawn with borders on all sides.
	 */
	DRAW_BORDERS("DrawBorders");
	
	private final String name;
	
	private BorderSplitType(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public static BorderSplitType byName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
