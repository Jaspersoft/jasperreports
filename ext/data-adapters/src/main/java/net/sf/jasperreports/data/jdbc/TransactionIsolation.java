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
package net.sf.jasperreports.data.jdbc;

import net.sf.jasperreports.engine.type.NamedEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public enum TransactionIsolation implements NamedEnum
{

	NONE(java.sql.Connection.TRANSACTION_NONE),
	READ_UNCOMMITTED(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED),
	READ_COMMITTED(java.sql.Connection.TRANSACTION_READ_COMMITTED),
	REPEATABLE_READ(java.sql.Connection.TRANSACTION_REPEATABLE_READ),
	SERIALIZABLE(java.sql.Connection.TRANSACTION_SERIALIZABLE);
	
	private final transient int level;
	
	private TransactionIsolation(int level)
	{
		this.level = level;
	}
	
	public int getLevel()
	{
		return level;
	}

	@Override
	public String getName()
	{
		return name();
	}
	
}
