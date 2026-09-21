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
package net.sf.jasperreports.engine.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * {@link net.sf.jasperreports.engine.util.JRSwapFile JRSwapFile} derived class that uses 
 * a {@link java.nio.channels.FileChannel FileChannel} to perform concurrent I/O on the
 * swap file.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRConcurrentSwapFile extends JRSwapFile
{
	private static final String EXCEPTION_MESSAGE_KEY_INSUFFICIENT_DATA = "util.concurrent.swap.file.insufficient.data";
	
	private final FileChannel fileChannel;

	/**
	 * Creates a swap file.
	 * 
	 * The file name is generated automatically.
	 * 
	 * @param directory the directory where the file should be created.
	 * @param blockSize the size of the blocks allocated by the swap file
	 * @param minGrowCount the minimum number of blocks by which the swap file grows when full
	 */
	public JRConcurrentSwapFile(String directory, int blockSize, int minGrowCount)
	{
		super(directory, blockSize, minGrowCount);

		fileChannel = file.getChannel();
	}
	
	@Override
	public String toString()
	{
		return "JRConcurrentSwapFile " + swapFile.getAbsolutePath();
	}

	@Override
	protected void write(byte[] data, int dataSize, int dataOffset, long fileOffset) throws IOException
	{
		fileChannel.write(ByteBuffer.wrap(data, dataOffset, dataSize), fileOffset);
	}

	@Override
	protected void read(byte[] data, int dataOffset, int dataLength, long fileOffset) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.wrap(data, dataOffset, dataLength);
		int read, totalRead = 0;
		do
		{
			read = fileChannel.read(buffer, fileOffset + totalRead);
			if (read < 0)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INSUFFICIENT_DATA,
						(Object[])null);
			}
			totalRead += read;
		}
		while (totalRead < dataLength);
	}
	
}
