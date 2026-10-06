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

import java.util.Arrays;
import java.util.BitSet;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CharPredicateCache
{
	public static enum Result
	{
		TRUE,
		FALSE,
		NOT_FOUND,
		NOT_CACHEABLE
	}

	//storing 2^11 chars per block/bitset
	private static final int BLOCK_SIZE_EXP = 11;
	
	//we need 2 bits per char
	private static final int BITSET_SIZE = 1 << (BLOCK_SIZE_EXP + 1);
	
	//how many bits to allocate in advance
	private static final int ALLOCATE_IN_ADVANCE_SIZE = 127;
	
	//the mask to use for determining the bit index
	private static final int BIT_INDEX_MASK = (1 << BLOCK_SIZE_EXP) - 1;
	
	//the number of bitsets needed for BMP
	private static final int BMP_BITSET_COUNT = 0x10000 >>> BLOCK_SIZE_EXP;
	
	//the highest codepoint
	private static final int MAX_CODEPOINT = 0x2FFFF;
	
	private BitSet[] bitSets;
	
	public CharPredicateCache()
	{
		bitSets = new BitSet[BMP_BITSET_COUNT];
	}

	private boolean cacheable(int codepoint)
	{
		return codepoint >= 0 && codepoint <= MAX_CODEPOINT;
	}
	
	private int setIndex(int codepoint)
	{
		return codepoint >>> BLOCK_SIZE_EXP;
	}

	private int bitIndex(int codepoint)
	{
		return (codepoint & BIT_INDEX_MASK) << 1;
	}
	
	public Result getCached(int codepoint)
	{
		if (!cacheable(codepoint))
		{
			return Result.NOT_CACHEABLE;
		}
		
		int setIndex = setIndex(codepoint);
		if (setIndex >= bitSets.length || bitSets[setIndex] == null)
		{
			return Result.NOT_FOUND;
		}
		
		int bitIndex = bitIndex(codepoint);
		if (bitSets[setIndex].get(bitIndex))
		{
			return Result.TRUE;
		}
		if (bitSets[setIndex].get(bitIndex + 1))
		{
			return Result.FALSE;
		}
		return Result.NOT_FOUND; 
	}
	
	public void set(int codepoint, boolean result)
	{
		if (!cacheable(codepoint))
		{
			throw new IllegalArgumentException("Codepoint " + codepoint + " not cacheable");
		}
		
		int setIndex = setIndex(codepoint);
		if (setIndex >= bitSets.length)
		{
			//outside BMP, allocate up to max codepoint
			int maxSetIndex = setIndex(MAX_CODEPOINT);
			bitSets = Arrays.copyOf(bitSets, maxSetIndex + 1);
		}
		
		int bitIndex = bitIndex(codepoint);
		BitSet bitSet = bitSets[setIndex];
		if (bitSet == null)
		{
			int allocateSize = Math.min(bitIndex + ALLOCATE_IN_ADVANCE_SIZE, BITSET_SIZE);
			bitSet = bitSets[setIndex] = new BitSet(allocateSize);
		}
		
		bitSet.set(result ? bitIndex : (bitIndex + 1));
		//we could skip this if we would assume that the cache is immutable
		bitSet.clear(result ? (bitIndex + 1) : bitIndex);
	}
}
