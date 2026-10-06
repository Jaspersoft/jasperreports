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

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public enum ValueLength
{
	BYTE(1), SHORT(2), INT(4), LONG(8),
	FLOAT(4), DOUBLE(8),
	REFERENCE(4);// FIXME lucianc using 4 bytes for references, but this actually varies
	
	private final int byteLength;
	
	private ValueLength(int byteLength)
	{
		this.byteLength = byteLength;
	}
	
	public int byteLength()
	{
		return byteLength;
	}

	public static ValueLength getNumberLength(long value)
	{
		ValueLength valueLength;
		if ((value & 0xffffffffffffff00L) == 0)
		{
			// byte values
			valueLength = ValueLength.BYTE;
		}
		else if ((value & 0xffffffffffff0000L) == 0)
		{
			// short values
			valueLength = ValueLength.SHORT;
		}
		else if ((value & 0xffffffff00000000L) == 0)
		{
			// int values
			valueLength = ValueLength.INT;
		}
		else
		{
			valueLength = ValueLength.LONG;
		}
		return valueLength;
	}
}
