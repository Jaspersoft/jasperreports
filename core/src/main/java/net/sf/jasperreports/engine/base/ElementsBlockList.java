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
package net.sf.jasperreports.engine.base;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementsBlockList implements ElementStore, Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(ElementsBlockList.class);
	
	private static final int INITIAL_SIZE = 3;
	private static final int MAX_INCREMENT = 200;
	
	private int blockCount;
	private int[] offsets;
	private ElementsBlock[] blocks;
	private int size;
	
	private transient int lastIndex = -1;
	
	public ElementsBlockList(ElementsBlock block)
	{
		blocks = new ElementsBlock[INITIAL_SIZE];
		offsets = new int[INITIAL_SIZE];
		
		blockCount = 1;
		blocks[0] = block;
		offsets[0] = 0;
		size = block.size();
	}

	@Override
	public void updatePage(JRVirtualPrintPage page)
	{
		for (ElementsBlock block : blocks)
		{
			if (block != null)
			{
				block.updatePage(page);
			}
		}
	}

	@Override
	public void updateContext(JRVirtualizationContext context, JRVirtualPrintPage page)
	{
		for (ElementsBlock block : blocks)
		{
			if (block != null)
			{
				block.updateContext(context, page);
			}
		}
	}

	public void addBlock(ElementsBlock block)
	{
		incrementBlocks();
		
		blocks[blockCount] = block;
		offsets[blockCount] = size;
		size += block.size();
		++blockCount;
	}
	
	protected void addBlock()
	{
		ElementsBlock block = new ElementsBlock(blocks[0].getContext(), blocks[0].getPage());
		addBlock(block);
		
		if (log.isDebugEnabled())
		{
			log.debug("created block " + block + " at offset " + size);
		}
	}

	protected void incrementBlocks()
	{
		if (blockCount == blocks.length)
		{
			int newSize = Math.min((blockCount * 3) / 2 + 1, blockCount + MAX_INCREMENT);
			
			ElementsBlock[] newBlocks = new ElementsBlock[newSize];
			System.arraycopy(blocks, 0, newBlocks, 0, blockCount);
			blocks = newBlocks;
			
			int[] newOffsets = new int[newSize];
			System.arraycopy(offsets, 0, newOffsets, 0, blockCount);
			offsets = newOffsets;
		}
	}

	public ElementsBlock[] getBlocks()
	{
		return Arrays.copyOf(blocks, blockCount);
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public int deepSize()
	{
		int deepSize = 0;
		for (int idx = 0; idx < blockCount; ++idx)
		{
			deepSize += blocks[idx].deepSize();
		}
		return deepSize;
	}

	@Override
	public JRPrintElement get(int index)
	{
		int blockIndex = blockIndex(index);
		return blocks[blockIndex].get(index - offsets[blockIndex]);
	}

	@Override
	public boolean add(JRPrintElement element)
	{
		// allow the last block to overflow.
		if (!blocks[blockCount - 1].add(element, false))
		{
			// the last block overflowed, create a new one
			addBlock();
			
			// add the element to the new block
			blocks[blockCount - 1].add(element, true);
		}

		++size;
		return true;
	}

	@Override
	public boolean add(int index, JRPrintElement element)
	{
		int blockIndex = blockIndex(index);
		
		// only allow the last block to overflow.
		// we're not moving elements across blocks when adding an element 
		// in the middle of a page.
		// this can lead to bigger blocks, but the case is exotic.
		boolean last = blockIndex == blockCount - 1;
		if (last)
		{
			if (!blocks[blockIndex].add(index - offsets[blockIndex], element, false))
			{
				// the last block overflowed, create a new one
				addBlock();
				
				// add the element to the new block
				blockIndex = blockCount - 1;
				blocks[blockIndex].add(index - offsets[blockIndex], element, true);
			}
		}
		else
		{
			blocks[blockIndex].add(index - offsets[blockIndex], element, true);
			
			// increment offsets of subsequent blocks
			for (int idx = blockIndex + 1; idx < blockCount; ++idx)
			{
				++offsets[blockIndex];
			}
		}

		++size;
		return true;
	}

	@Override
	public JRPrintElement set(int index, JRPrintElement element)
	{
		int blockIndex = blockIndex(index);
		return blocks[blockIndex].set(index - offsets[blockIndex], element);
	}

	@Override
	public JRPrintElement remove(int index)
	{
		// FIXME removing elements from an existing block without moving elements
		// across blocks.  this can lead to small blocks if many elements are
		// removed from a page, such as when we have a group with keep together.
		
		int blockIndex = blockIndex(index);
		ElementsBlock block = blocks[blockIndex];
		JRPrintElement element = block.remove(index - offsets[blockIndex]);
		
		// decrement offsets of subsequent blocks
		if (blockIndex + 1 < blockCount)
		{
			for (int idx = blockIndex + 1; idx < blockCount; ++idx)
			{
				--offsets[idx];
			}
		}
		
		// if the block was left empty and this is not the only block, get rid of it
		if (blockCount > 1 && block.isEmpty())
		{
			// shift the remaining blocks
			for (int idx = blockIndex + 1; idx < blockCount; ++idx)
			{
				blocks[idx - 1] = blocks[idx];
				offsets[idx - 1] = offsets[idx];
			}
			--blockCount;
			blocks[blockCount] = null;
			offsets[blockCount] = 0;
		}
		
		--size;
		return element;
	}

	@Override
	public void dispose()
	{
		for (int idx = 0; idx < blockCount; ++idx)
		{
			blocks[idx].dispose();
		}
	}
	
	protected int blockIndex(int index)
	{
		if (index < 0)
		{
			throw new IndexOutOfBoundsException("index: " + index);
		}

		// see if the index falls in the lastIndex block
		if (lastIndex >= 0 && lastIndex < blockCount)
		{
			if (index >= offsets[lastIndex]
					&& (lastIndex + 1 == blockCount
					|| index < offsets[lastIndex + 1]))
			{
				return lastIndex;
			}
		}
		
		int blockIndex = Arrays.binarySearch(offsets, 0, blockCount, index);
		if (blockIndex < 0)
		{
			blockIndex = -blockIndex - 2;
		}
		// caching last index for fast serial access
		lastIndex = blockIndex;
		return blockIndex;
	}

	@Override
	public JRVirtualPrintPage getPage()
	{
		return blocks[0].getPage();
	}

	@Override
	public void transferElements(Consumer<JRPrintElement> consumer)
	{
		for (int idx = 0; idx < blockCount; ++idx)
		{
			blocks[idx].transferElements(consumer);
		}

		//leaving only the first empty block
		for (int idx = 1; idx < blockCount; ++idx)
		{
			blocks[idx] = null;
		}
		blockCount = 1;
		size = 0;
	}
}