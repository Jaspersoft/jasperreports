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

import net.sf.jasperreports.engine.JRHyperlink;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum HyperlinkTargetEnum implements NamedEnum
{
	/**
	 * Target not defined.
	 */
	NONE("None", null),

	/**
	 * Constant useful for specifying that the hyperlink will be opened in the same window.
	 */
	SELF("Self", "_self"),

	/**
	 * Constant useful for specifying that the hyperlink will be opened in a new window.
	 */
	BLANK("Blank", "_blank"),

	/**
	 * Constant useful for specifying that the hyperlink will be opened in the parent frame.
	 */
	PARENT("Parent", "_parent"),

	/**
	 * Constant useful for specifying that the hyperlink will be opened in the top frame.
	 */
	TOP("Top", "_top"),

	/**
	 * Custom hyperlink target name.
	 * <p>
	 * The specific target name is determined by {@link JRHyperlink#getLinkTarget() getLinkTarget()}.
	 * </p>
	 */
	CUSTOM("Custom", null);

	/**
	 *
	 */
	private final transient String name;
	private final transient String htmlValue;

	private HyperlinkTargetEnum(String name, String htmlValue)
	{
		this.name = name;
		this.htmlValue = htmlValue;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	/**
	 *
	 */
	public String getHtmlValue()
	{
		return htmlValue;
	}

	/**
	 *
	 */
	public static HyperlinkTargetEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
