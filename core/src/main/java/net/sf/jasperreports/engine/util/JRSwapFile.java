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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;


/**
 * Swap file implementation that can be used as a disk cache for arbitrary binary data.
 * <p>
 * Fixed-size blocks are allocated inside the swap file when a caller wants to write data.
 * The caller receives a handle to the allocated area based on which it can read the data
 * or free the area.
 * <p>
 * The implementation is thread-safe.  I/O operations are performed in synchronized blocks,
 * only one thread would do a read or write at one moment.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRSwapFile
{
	private static final Log log = LogFactory.getLog(JRSwapFile.class);
	
	/**
	 * Property that instructs whether {@link File#deleteOnExit() deleteOnExit} is to be requested
	 * for swap files.
	 * 
	 * Swap files are explicitly deleted on {@link #dispose() dispose()} and garbage collection.
	 */
	public static final String PROPERTY_DELETE_ON_EXIT = JRFileVirtualizer.PROPERTY_TEMP_FILES_SET_DELETE_ON_EXIT;

	protected final File swapFile;
	protected final RandomAccessFile file;
	private final int blockSize;
	private final int minGrowCount;
	private final LongQueue freeBlocks;
	
	
	/**
	 * Creates a swap file.
	 * 
	 * The file name is generated automatically.
	 * 
	 * @param directory the directory where the file should be created.
	 * @param blockSize the size of the blocks allocated by the swap file
	 * @param minGrowCount the minimum number of blocks by which the swap file grows when full
	 */
	public JRSwapFile(String directory, int blockSize, int minGrowCount)
	{
		this(DefaultJasperReportsContext.getInstance(), directory, blockSize, minGrowCount);
	}
	
	/**
	 * Creates a swap file.
	 * 
	 * The file name is generated automatically.
	 * 
	 * @param jasperReportsContext the JasperReportsContext to read configuration from.
	 * @param directory the directory where the file should be created.
	 * @param blockSize the size of the blocks allocated by the swap file
	 * @param minGrowCount the minimum number of blocks by which the swap file grows when full
	 */
	public JRSwapFile(JasperReportsContext jasperReportsContext, String directory, int blockSize, int minGrowCount)
	{
		try
		{
			String filename = "swap_" + System.identityHashCode(this) + "_" + System.currentTimeMillis();
			swapFile = new File(directory, filename);
			if (log.isDebugEnabled())
			{
				log.debug("Creating swap file " + swapFile.getPath());
			}
			boolean fileExists = swapFile.exists();
			
			boolean deleteOnExit = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(PROPERTY_DELETE_ON_EXIT);
			if (deleteOnExit)
			{
				swapFile.deleteOnExit();
			}
			
			file = new RandomAccessFile(swapFile, "rw");//FIXME to this lazily?

			this.blockSize = blockSize;
			this.minGrowCount = minGrowCount;
			freeBlocks = new LongQueue(minGrowCount);
			
			if (fileExists)
			{
				file.setLength(0);
				if (log.isDebugEnabled())
				{
					log.debug("Swap file " + swapFile.getPath() + " exists, truncating");
				}
			}
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	@Override
	public String toString()
	{
		return "JRSwapFile " + swapFile.getAbsolutePath();
	}

	/**
	 * Allocates an area in the swap file and writes data in it.
	 * 
	 * @param data the data for which to allocate an area in the file
	 * @return a handle to the allocated area
	 * @throws IOException
	 */
	public SwapHandle write(byte[] data) throws IOException
	{
		int blockCount = (data.length - 1) / blockSize + 1;
		long[] offsets = reserveFreeBlocks(blockCount);
		int lastBlockSize = (data.length - 1) % blockSize + 1;
		SwapHandle handle = new SwapHandle(offsets, lastBlockSize);
		for (int i = 0; i < blockCount; ++i)
		{
			int dataSize = i < blockCount - 1 ? blockSize : lastBlockSize;
			int dataOffset = i * blockSize;			
			write(data, dataSize, dataOffset, offsets[i]);
		}
		
		return handle;
	}


	protected void write(byte[] data, int dataSize, int dataOffset, long fileOffset) throws IOException
	{
		synchronized (this)
		{
			file.seek(fileOffset);
			file.write(data, dataOffset, dataSize);
		}
	}

	
	/**
	 * Reads all the data from an allocated area.
	 * 
	 * @param handle the allocated area handle
	 * @param free whether to free the area after reading
	 * @return the whole data saved in an allocated area
	 * @throws IOException
	 */
	public byte[] read(SwapHandle handle, boolean free) throws IOException
	{
		long[] offsets = handle.getOffsets();
		int totalLength = (offsets.length - 1) * blockSize + handle.getLastSize();
		byte[] data = new byte[totalLength];
		
		for (int i = 0; i < offsets.length; ++i)
		{
			int dataOffset = i * blockSize;
			int dataLength = i < offsets.length - 1 ? blockSize : handle.getLastSize();
			read(data, dataOffset, dataLength, offsets[i]);
		}
		
		if (free)
		{
			freeBlocks(offsets);
		}
		
		return data;
	}


	protected void read(byte[] data, int dataOffset, int dataLength, long fileOffset) throws IOException
	{
		synchronized (this)
		{
			file.seek(fileOffset);
			file.readFully(data, dataOffset, dataLength);
		}
	}
	
	
	/**
	 * Frees an allocated area.
	 * 
	 * @param handle the allocated area handle
	 */
	public void free(SwapHandle handle)
	{
		freeBlocks(handle.getOffsets());
	}
	
	
	/**
	 * Closes and deletes the swap file.
	 */
	public void dispose()
	{
		synchronized (this)
		{
			if (swapFile.exists())
			{
				if (log.isDebugEnabled())
				{
					log.debug("Disposing swap file " + swapFile.getPath());
				}

				try
				{
					file.close();
				}
				catch (IOException e)
				{
					log.warn("Not able to close swap file " + swapFile.getPath());
				}

				if (!swapFile.delete())
				{
					log.warn("Not able to delete swap file " + swapFile.getPath());
				}
			}
		}
	}


	@Override
	protected void finalize() throws Throwable //NOSONAR
	{
		dispose();
		super.finalize();
	}


	protected synchronized long[] reserveFreeBlocks(int blockCount) throws IOException
	{
		int growCount = blockCount - freeBlocks.size();
		if (growCount > 0)
		{
			if (growCount < minGrowCount)
			{
				growCount = minGrowCount;
			}
			
			long length = file.length();
			long newLength = length + growCount * blockSize;
			if (log.isDebugEnabled())
			{
				log.debug("Growing swap file " + swapFile.getPath() + " with " + growCount + " blocks x " + blockSize + " bytes to size " + newLength);
			}
			file.setLength(newLength);

			for (int i = 0; i < growCount; ++i)
			{
				freeBlocks.addLast(length + i * blockSize);
			}
		}
		
		long[] offsets = new long[blockCount];
		for (int i = 0; i < blockCount; i++)
		{
			offsets[i] = freeBlocks.popFirst();
		}
		return offsets;
	}


	protected synchronized void freeBlocks(long []offsets)
	{
		for (int i = offsets.length - 1; i >= 0; --i)
		{
			freeBlocks.addFirst(offsets[i]);
		}
	}
	
	
	protected static class LongQueue
	{
		private static final String EXCEPTION_MESSAGE_KEY_QUEUE_UNDERFLOW = "util.long.queue.underflow";
		
		private final int minGrowCount;
		private long[] vals;
		private int size;
		private int first;
		private int last;
		
		public LongQueue(int minGrowCount)
		{
			this.minGrowCount = minGrowCount;
			vals = new long[minGrowCount];
			size = 0;
			first = 0;
			last = 0;
		}

		public void addFirst(long val)
		{
			growIfFull();
			
			--first;
			if (first == -1)
			{
				first = vals.length - 1;
			}
			vals[first] = val;
			++size;
		}

		public void addLast(long val)
		{
			growIfFull();
			
			vals[last] = val;
			++size;
			++last;
			if (last == vals.length)
			{
				last = 0;
			}
		}

		public long popFirst()
		{
			if (size == 0)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_QUEUE_UNDERFLOW,
						(Object[])null);
			}

			long val = vals[first];
			++first;
			if (first == vals.length)
			{
				first = 0;
			}
			--size;

			return val;
		}

		protected void growIfFull()
		{
			int valsLength = vals.length;
			if (size == valsLength)
			{
				int newLength = (valsLength * 3) / 2 + 1;
				if (newLength - valsLength < minGrowCount)
				{
					newLength = valsLength + minGrowCount;
				}
				
				long[] newVals = new long[newLength];
				System.arraycopy(vals, first, newVals, 0, valsLength - first);
				if (last > 0)
				{
					System.arraycopy(vals, 0, newVals, valsLength - first, last);
				}
				
				vals = newVals;
				first = 0;
				last = valsLength;
			}
		}

		public int size()
		{
			return size;
		}
	}
	
	public static class SwapHandle
	{
		private final long[] offsets;
		private final int lastSize;
		
		public SwapHandle(long[] offsets, int lastSize)
		{
			this.offsets = offsets;
			this.lastSize = lastSize;
		}

		public long[] getOffsets()
		{
			return offsets;
		}
		
		public int getLastSize()
		{
			return lastSize;
		}
	}
}
