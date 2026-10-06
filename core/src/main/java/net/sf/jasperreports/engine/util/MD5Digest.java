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
package net.sf.jasperreports.engine.util;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class MD5Digest implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private final long low;
	private final long high;
	
	public MD5Digest(long low, long high)
	{
		this.low = low;
		this.high = high;
	}
	
	public long getLow()
	{
		return low;
	}
	
	public long getHigh()
	{
		return high;
	}

	@Override
	public int hashCode()
	{
		return (int) (low + 47 * high);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof MD5Digest))
		{
			return false;
		}
		
		MD5Digest d = (MD5Digest) obj;
		return low == d.low && high == d.high;
	}

	@Override
	public String toString()
	{
		return String.format("%016x", low) + String.format("%016x", high);
	}
}
