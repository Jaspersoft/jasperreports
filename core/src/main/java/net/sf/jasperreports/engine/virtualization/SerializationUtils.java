/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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
package net.sf.jasperreports.engine.virtualization;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SerializationUtils
{
	private static final String EXCEPTION_MESSAGE_KEY_INVALID_INT_BYTE_READ = "engine.virtualization.utils.invalid.int.byte.read";

	public static void writeIntCompressed(ObjectOutput out, int value)
			throws IOException
	{
		if ((value & 0xFFFFFF80) == 0)
		{
			out.writeByte(value);
			return;
		}

		if ((value & 0xFFFFC000) == 0)
		{
			out.writeByte((value >>> 8) | 0x80);
			out.writeByte(value);
			return;
		}

		if ((value & 0xFFE00000) == 0)
		{
			out.writeByte((value >>> 16) | 0xC0);
			out.writeByte(value >>> 8);
			out.writeByte(value);
			return;
		}

		if ((value & 0xF0000000) == 0)
		{
			out.writeByte((value >>> 24) | 0xE0);
			out.writeByte(value >>> 16);
			out.writeByte(value >>> 8);
			out.writeByte(value);
			return;
		}

		out.writeByte(0xF0);
		out.writeByte(value >>> 24);
		out.writeByte(value >>> 16);
		out.writeByte(value >>> 8);
		out.writeByte(value);
	}

	public static int readIntCompressed(ObjectInput in) throws IOException
	{
		int b1 = in.readUnsignedByte();
		if ((b1 & 0x80) == 0)
		{
			return b1;
		}
		
		if ((b1 & 0xC0) == 0x80)
		{
			int b2 = in.readUnsignedByte();
			return ((b1 & 0x3F) << 8) | b2;
		}
		
		if ((b1 & 0xE0) == 0xC0)
		{
			int b2 = in.readUnsignedByte();
			int b3 = in.readUnsignedByte();
			return ((b1 & 0x1F) << 16) | (b2 << 8) | b3;
		}
		
		if ((b1 & 0xF0) == 0xE0)
		{
			int b2 = in.readUnsignedByte();
			int b3 = in.readUnsignedByte();
			int b4 = in.readUnsignedByte();
			return ((b1 & 0x0F) << 24) | (b2 << 16) | (b3 << 8) | b4;
		}
		
		if (b1 != 0xF0)
		{
			// should not happen
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INVALID_INT_BYTE_READ,
					new Object[]{Integer.toHexString(b1)});
		}
		
		int b2 = in.readUnsignedByte();
		int b3 = in.readUnsignedByte();
		int b4 = in.readUnsignedByte();
		int b5 = in.readUnsignedByte();
		return (b2 << 24) | (b3 << 16) | (b4 << 8) | b5;
	}

}
