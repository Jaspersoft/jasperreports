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

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StringSerializer implements ObjectSerializer<String>
{
	private static final String EXCEPTION_MESSAGE_KEY_CHUNK_UNEXPECTED_LENGTH = "virtualization.chunk.unexpected.length";
	private static final String EXCEPTION_MESSAGE_KEY_CHUNK_UNEXPECTED_LENGTH_REMAINING = "virtualization.chunk.unexpected.length.remaining";

	// smaller than 65535/3, see writeUTF
	private static final int CHUNK_SIZE = 20000;
	
	@Override
	public int typeValue()
	{
		return SerializationConstants.OBJECT_TYPE_STRING;
	}

	@Override
	public ReferenceType defaultReferenceType()
	{
		return ReferenceType.OBJECT;
	}

	@Override
	public boolean defaultStoreReference()
	{
		return true;
	}

	@Override
	public void write(String value, VirtualizationOutput out) throws IOException
	{
		int length = value.length();
		if (length < CHUNK_SIZE)
		{
			out.writeUTF(value);
		}
		else
		{
			out.writeUTF(value.substring(0, CHUNK_SIZE));
			// writing the length so that we know when to stop in read()
			out.writeInt(length);
			
			for (int index = CHUNK_SIZE; index < length; index += CHUNK_SIZE)
			{
				out.writeUTF(value.substring(index, Math.min(length, index + CHUNK_SIZE)));
			}
		}
	}

	@Override
	public String read(VirtualizationInput in) throws IOException
	{
		//TODO write some unit tests
		String chunk = in.readUTF();
		int chunkLength = chunk.length();
		if (chunkLength < CHUNK_SIZE)
		{
			// single chunk
			return chunk;
		}
		
		// should be of exact max chunk size
		if (chunkLength != CHUNK_SIZE)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CHUNK_UNEXPECTED_LENGTH, 
					new Object[]{chunkLength});
		}
		
		// we have several chunks, read the length
		int length = in.readInt();
		if (length < CHUNK_SIZE) 
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CHUNK_UNEXPECTED_LENGTH, 
					new Object[]{length});
		}
		
		//FIXME check against an upper limit to prevent memory being allocated when the data is corrupted?
		char[] chars = new char[length];
		chunk.getChars(0, chunkLength, chars, 0);
		
		int remaining = length - chunkLength;
		while (remaining > 0)
		{
			chunk = in.readUTF();
			chunkLength = chunk.length();
			
			if (chunkLength < CHUNK_SIZE)
			{
				// we should be on the last chunk
				if (chunkLength != remaining)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_CHUNK_UNEXPECTED_LENGTH_REMAINING, 
							new Object[]{chunkLength, remaining});
				}
			}
			else if (chunkLength != CHUNK_SIZE)
			{
				// should be of exact max chunk size
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_CHUNK_UNEXPECTED_LENGTH, 
						new Object[]{chunkLength});
			}
			
			chunk.getChars(0, chunkLength, chars, length - remaining);
			remaining -= chunkLength;
		}
		
		return new String(chars);
	}

}
