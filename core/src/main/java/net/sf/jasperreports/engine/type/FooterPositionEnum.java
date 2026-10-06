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
public enum FooterPositionEnum implements NamedEnum
{
	/**
	 * The group footer section is rendered at normal position, just right after the last detail 
	 * or right after the nested group footer section.
	 */
	NORMAL("Normal"),

	/**
	 * The group footer section is rendered at bottom of the current page, provided that an inner group 
	 * having this value would force outer group footers to stack at the bottom of the current page, 
	 * regardless of the outer group footer setting.
	 */
	STACK_AT_BOTTOM("StackAtBottom"),

	/**
	 * The group footer section is rendered at bottom of the current page, provided that an inner group 
	 * having this value would render its footer right at the bottom of the page, forcing the outer group 
	 * footers to render on the next page.
	 */
	FORCE_AT_BOTTOM("ForceAtBottom"),

	/**
	 * The group footer section is rendered at bottom of the current page, provided that the outer footers
	 * have a similar footer display option to render at the page bottom as well, because otherwise, they cannot
	 * be forced to change their behavior in any way.
	 */
	COLLATE_AT_BOTTOM("CollateAtBottom");

	/**
	 *
	 */
	private final transient String name;

	private FooterPositionEnum(String name)
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
	public static FooterPositionEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static FooterPositionEnum getValueOrDefault(FooterPositionEnum value)
	{
		return value == null ? NORMAL : value;
	}
	
	@Override
	public FooterPositionEnum getDefault()
	{
		return NORMAL;
	}
}
