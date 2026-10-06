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

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BlockColumnStore implements ColumnStore
{
	
	private static final Log log = LogFactory.getLog(BlockColumnStore.class);
	
	private final BufferColumnStore bufferStore;
	private LinkedList<ColumnValues> previousBlocks;
	
	public BlockColumnStore(BufferColumnStore bufferStore)
	{
		this.bufferStore = bufferStore;
	}

	@Override
	public Class<?> getBaseValuesType()
	{
		return bufferStore.getBaseValuesType();
	}

	protected void preAdd()
	{
		if (bufferStore.full())
		{
			if (previousBlocks == null)
			{
				previousBlocks = new LinkedList<>();
			}
			
			ColumnValues currentValues = bufferStore.createValues();
			previousBlocks.add(currentValues);
			
			bufferStore.resetValues();
		}
	}
	
	@Override
	public void addValue(Object value)
	{
		preAdd();
		bufferStore.addValue(value);
	}

	@Override
	public ColumnValues createValues()
	{
		ColumnValues currentValues = bufferStore.createValues();
		if (previousBlocks == null)
		{
			return currentValues;
		}
		
		previousBlocks.add(currentValues);
		BlockColumnValues blockColumnValues = new BlockColumnValues(previousBlocks);
		
		if (log.isDebugEnabled())
		{
			log.debug(this + ": created block values of size " + blockColumnValues.size() + " by " + bufferStore);
		}
		
		return blockColumnValues;
	}

	@Override
	public String toString()
	{
		return "BlockColumnStore@" + hashCode();
	}
	
}
