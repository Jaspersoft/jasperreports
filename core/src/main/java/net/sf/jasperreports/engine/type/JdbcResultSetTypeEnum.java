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

import java.sql.ResultSet;


/**
 * Values for the {@link java.sql.ResultSet} type used by the JDBC query executer.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public enum JdbcResultSetTypeEnum implements NamedEnum
{
	/**
	 * The cursor may move only forward.
	 * 
	 * @see ResultSet#TYPE_FORWARD_ONLY
	 */
	FORWARD_ONLY("forwardOnly", ResultSet.TYPE_FORWARD_ONLY),

	/**
	 * The result set is scrollable but generally not sensitive to changes made by others.
	 * 
	 * @see ResultSet#TYPE_SCROLL_INSENSITIVE
	 */
	SCROLL_INSENSITIVE("scrollInsensitive", ResultSet.TYPE_SCROLL_INSENSITIVE),

	/**
	 * The result set is scrollable and generally sensitive to changes made by others.
	 * 
	 * @see ResultSet#TYPE_SCROLL_SENSITIVE
	 */
	SCROLL_SENSITIVE("scrollSensitive", ResultSet.TYPE_SCROLL_SENSITIVE);
	
	private final transient String name;
	private final transient int jdbcValue;

	private JdbcResultSetTypeEnum(String name, int jdbcValue)
	{
		this.name = name;
		this.jdbcValue = jdbcValue;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the corresponding {@link ResultSet} constant.
	 */
	public int getJdbcValue()
	{
		return jdbcValue;
	}
	
	public static JdbcResultSetTypeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
}
