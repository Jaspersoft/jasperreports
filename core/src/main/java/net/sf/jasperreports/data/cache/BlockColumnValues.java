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
package net.sf.jasperreports.data.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BlockColumnValues implements ColumnValues, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private int size;
	private ColumnValues[] blocks;
	
	public BlockColumnValues(List<ColumnValues> blocks)
	{
		ColumnValues[] blocksArray = new ColumnValues[blocks.size()];
		this.blocks = blocks.toArray(blocksArray);
		
		int totalSize = 0;
		for (ColumnValues values : this.blocks)
		{
			totalSize += values.size();
		}
		this.size = totalSize;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeInt(size);
		out.writeInt(blocks.length);
		for (ColumnValues block : blocks)
		{
			out.writeUnshared(block);
		}
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		size = in.readInt();
		int blockCount = in.readInt();
		blocks = new ColumnValues[blockCount];
		for (int i = 0; i < blockCount; i++)
		{
			blocks[i] = (ColumnValues) in.readUnshared();
		}
	}
	
	@Override
	public int size()
	{
		return size;
	}

	@Override
	public ColumnValuesIterator iterator()
	{
		return new ValuesIterator();
	}

	protected class ValuesIterator implements ColumnValuesIterator
	{
		private int blockIndex;
		private ColumnValuesIterator blockIterator;

		public ValuesIterator()
		{
			blockIndex = -1;
		}
		
		@Override
		public void moveFirst()
		{
			blockIndex = -1;
			blockIterator = null;
		}

		@Override
		public boolean next()
		{
			do
			{
				if (blockIterator != null)
				{
					if (blockIterator.next())
					{
						return true;
					}
					
					blockIterator = null;
				}
				
				if (blockIndex + 1 >= blocks.length)
				{
					return false;
				}
				
				++blockIndex;
				blockIterator = blocks[blockIndex].iterator();
			}
			while (true);// exit through one of the returns
		}

		@Override
		public Object get()
		{
			if (blockIterator == null)
			{
				throw new IllegalStateException();
			}
			
			return blockIterator.get();
		}
		
	}
}

  