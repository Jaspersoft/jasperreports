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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class RunLengthStore
{

	private static final Log log = LogFactory.getLog(RunLengthStore.class);
	
	protected static final int MAX_RUN_LENGTH = Short.MAX_VALUE - Short.MIN_VALUE;
	
	private static final int RUN_LENGTH_PENALTY = 32;// 32 bytes

	private final ArrayStore store;
	
	private int runStart;
	private int runCount;
	private int maxRunLength;
	
	public RunLengthStore(ArrayStore store)
	{
		this.store = store;
		
		if (log.isDebugEnabled())
		{
			log.debug("created " + this + " for " + store);
		}
	}
	
	public void reset()
	{
		this.runStart = -1;
		this.runCount = 0;
		this.maxRunLength = 1;
	}

	public void valueAdded()
	{
		int count = store.count();
		if (runStart >= 0 && store.valuesEqual(runStart, count - 1)
				&& (count - runStart) < MAX_RUN_LENGTH)
		{
			if (count - runStart > maxRunLength)
			{
				maxRunLength = count - runStart;
			}
		}
		else
		{
			runStart = count - 1;
			++runCount;
		}
	}

	public ColumnValues applyRunLengths(ValueLength valueLength)
	{
		ValueLength runLength = ValueLength.getNumberLength(maxRunLength - 1);
		
		ColumnValues runLengthValues = null;
		int originalCount = store.count();
		if (useRunLength(originalCount, valueLength, runLength))
		{
			if (log.isDebugEnabled())
			{
				log.debug(this + ": using run lengths count " + runCount 
						+ ", original count " + originalCount);
			}
			
			// go with run lengths
			long[] runLengths = new long[runCount];
			int runIdx = 0;
			runLengths[runIdx] = 0;// we start from 0 because we use 1 as offset
			for (int i = 1; i < originalCount; ++i)
			{
				if (store.valuesEqual(i, runIdx) && runLengths[runIdx] < RunLengthStore.MAX_RUN_LENGTH)
				{
					++runLengths[runIdx];
				}
				else
				{
					++runIdx;
					runLengths[runIdx] = 0;
					store.copyValue(runIdx, i);
				}
			}
			
			// update the values count
			store.updateCount(runCount);
			
			if (log.isDebugEnabled())
			{
				log.debug(this + ": creating run lengths of count " + runCount 
						+ ", value length " + runLength);
			}
			
			runLengthValues = NumberValuesUtils.instance().toValues(runCount, 
					runLengths, runLength, 1, 1);
		}
		return runLengthValues;
	}
	
	protected boolean useRunLength(int count, ValueLength valueLength, ValueLength runLength)
	{
		return count * valueLength.byteLength() > 
				runCount * (valueLength.byteLength() + runLength.byteLength()) + RUN_LENGTH_PENALTY;
	}
	
	public int getRunStart()
	{
		return runStart;
	}

	public int getRunCount()
	{
		return runCount;
	}

	public int getMaxRunLength()
	{
		return maxRunLength;
	}

	@Override
	public String toString()
	{
		return "RunLengthStore@" + hashCode();
	}
	
}
