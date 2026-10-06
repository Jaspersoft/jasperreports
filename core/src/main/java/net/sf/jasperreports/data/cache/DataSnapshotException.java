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
package net.sf.jasperreports.data.cache;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DataSnapshotException extends JRException
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public DataSnapshotException(String message)
	{
		super(message);
	}

	public DataSnapshotException(Throwable t)
	{
		super(t);
	}

	public DataSnapshotException(String message, Throwable t)
	{
		super(message, t);
	}
	
	public DataSnapshotException(String messageKey, Object[] args, Throwable t)
	{
		super(messageKey, args, t);
	}

	public DataSnapshotException(String messageKey, Object[] args)
	{
		super(messageKey, args);
	}
}
