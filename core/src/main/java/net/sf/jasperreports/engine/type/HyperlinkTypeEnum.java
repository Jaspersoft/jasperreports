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
public enum HyperlinkTypeEnum implements NamedEnum
{
	/**
	 * Not set hyperlink type.
	 */
	NULL("Null"),
	
	/**
	 * Constant useful for specifying that the element does not contain a hyperlink. This is the default value
	 * for a hyperlink type.
	 */
	NONE("None"),

	/**
	 * Constant useful for specifying that the hyperlink points to an external resource specified by the
	 * hyperlink reference expression.
	 * @see JRHyperlink#getHyperlinkReferenceExpression()
	 */
	REFERENCE("Reference"),

	/**
	 * Constant useful for specifying that the hyperlink points to a local anchor, specified by the hyperlink
	 * anchor expression.
	 * @see JRHyperlink#getHyperlinkAnchorExpression()
	 */
	LOCAL_ANCHOR("LocalAnchor"),

	/**
	 * Constant useful for specifying that the hyperlink points to a 1 based page index within the current document.
	 */
	LOCAL_PAGE("LocalPage"),

	/**
	 * Constant useful for specifying that the hyperlink points to a remote anchor (specified by the hyperlink
	 * anchor expression) within an external document (specified by the hyperlink reference expression).
	 * @see JRHyperlink#getHyperlinkAnchorExpression()
	 * @see JRHyperlink#getHyperlinkReferenceExpression()
	 */
	REMOTE_ANCHOR("RemoteAnchor"),

	/**
	 * Constant useful for specifying that the hyperlink points to a 1 based page index within an external document
	 * (specified by the hyperlink reference expression).
	 */
	REMOTE_PAGE("RemotePage"),
	
	/**
	 * Custom hyperlink type.
	 * <p>
	 * The specific type is determined by {@link JRHyperlink#getLinkType() getLinkType()}.
	 */
	CUSTOM("Custom");

	/**
	 *
	 */
	private final transient String name;

	private HyperlinkTypeEnum(String name)
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
	public static HyperlinkTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
