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
package net.sf.jasperreports.engine.export.tabulator;

import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DimensionEntries<T extends DimensionEntry>
{
	private static final Log log = LogFactory.getLog(DimensionEntries.class);
	private static final String EXCEPTION_MESSAGE_KEY_END_OUT_OF_RANGE = "export.tabulator.dimension.end.out.of.range";
	private static final String EXCEPTION_MESSAGE_KEY_START_OUT_OF_RANGE = "export.tabulator.dimension.start.out.of.range";
	
	private DimensionControl<T> control;
	private TreeSet<T> entries;
	
	public DimensionEntries(DimensionControl<T> control)
	{
		this.control = control;
		this.entries = new TreeSet<>();
		
		// TODO lucianc no index for column
		T univEntry = control.createEntry(DimensionEntry.MINUS_INF, DimensionEntry.PLUS_INF);
		this.entries.add(univEntry);
	}
	
	@Override
	public String toString()
	{
		return "DimensionEntries " + logId();
	}
	
	protected String logId()
	{
		return Integer.toHexString(hashCode());
	}
	
	public DimensionRange<T> getRange(int start, int end)
	{
		if (start > end)
		{
			throw new IllegalArgumentException(start + " > " + end);
		}
		if (start <= DimensionEntry.MINUS_INF)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_START_OUT_OF_RANGE,
					new Object[]{start});
		}
		if (end >= DimensionEntry.PLUS_INF)
		{
			throw 
			new JRRuntimeException(
				EXCEPTION_MESSAGE_KEY_END_OUT_OF_RANGE,
				new Object[]{end});
		}

		T startKey = control.entryKey(start);
		T floor = entries.floor(startKey);
		assert floor != null;
		
		NavigableSet<T> tailSet = entries.tailSet(floor, true);
		
		T endKey = control.entryKey(end);
		T ceiling = tailSet.ceiling(endKey);
		
		NavigableSet<T> rangeSet;
		if (ceiling == null)
		{
			rangeSet = tailSet;
		}
		else
		{
			rangeSet = tailSet.headSet(ceiling, false);
		}
		
		return new DimensionRange<>(start, end, floor, ceiling, rangeSet);
	}
	
	public DimensionRange<T> addEntries(DimensionRange<T> range)
	{
		T resultStart = addStartEntry(range);
		assert resultStart != null && resultStart.startCoord == range.start;
		
		T resultEnd = addEndEntry(range);
		assert resultEnd != null && resultEnd.startCoord == range.end;

		// check if the final range is the same as the initial range
		if (resultStart.startCoord == range.floor.startCoord
				&& (range.ceiling != null && resultEnd.startCoord == range.ceiling.startCoord))
		{
			return range;
		}
		
		// not the same, create a new range 
		NavigableSet<T> resultRange = range.rangeSet.subSet(resultStart, true, resultEnd, false);
		return new DimensionRange<>(range.start, range.end, resultStart, resultEnd, resultRange);
	}

	protected T addStartEntry(DimensionRange<T> range)
	{
		T resultStart;
		if (range.floor.startCoord < range.start)
		{
			int entryEnd;
			T splitEntry = null;
			if (range.floor.endCoord > range.start)
			{
				splitEntry = range.floor;
				entryEnd = splitEntry.endCoord;
			}
			else
			{
				assert range.floor == entries.last();
				if (range.floor.endCoord < range.start)
				{
					addEntry(range.floor.endCoord, range.start, null);
				}
				entryEnd = range.end;
			}
			
			resultStart = addEntry(range.start, entryEnd, splitEntry);
		}
		else
		{
			resultStart = range.floor;
		}
		return resultStart;
	}

	protected T addEndEntry(DimensionRange<T> range)
	{

		T resultEnd;
		if (range.ceiling != null && range.ceiling.startCoord == range.end)
		{
			resultEnd = range.ceiling;
		}
		else
		{
			T last = range.rangeSet.last();
			// we just added the start entry, so rangeSet can't be empty
			assert last != null;
			
			if (range.ceiling == null)
			{
				assert last.endCoord > range.end;
				resultEnd = addEntry(range.end, last.endCoord, last);
			}
			else //range.ceiling.startCoord > range.end
			{
				resultEnd = addEntry(range.end, range.ceiling.startCoord, last);
			}
		}
		return resultEnd;
	}
	
	protected T addEntry(int startCoord)
	{
		T entryKey = control.entryKey(startCoord);
		T floorEntry = entries.floor(entryKey);
		assert floorEntry != null;
		
		T startEntry;
		if (floorEntry.startCoord == startCoord)
		{
			startEntry = floorEntry;
		}
		else //floorEntry.startCoord < startCoord
		{
			startEntry = addEntry(startCoord, floorEntry.endCoord, floorEntry);
		}
		
		return startEntry;
	}
	
	protected T addEntry(int startCoord, int endCoord)
	{
		assert startCoord < endCoord;
		T entryKey = control.entryKey(startCoord);
		T floorEntry = entries.floor(entryKey);
		assert floorEntry != null;
		assert endCoord <= floorEntry.endCoord;
		
		T startEntry;
		if (floorEntry.startCoord == startCoord)
		{
			startEntry = floorEntry;
		}
		else //floorEntry.startCoord < startCoord
		{
			startEntry = addEntry(startCoord, floorEntry.endCoord, floorEntry);
		}
		
		if (endCoord < startEntry.endCoord)
		{
			addEntry(endCoord, startEntry.endCoord, startEntry);
		}
		return startEntry;
	}
	
	protected T addEntry(int startCoord, int endCoord, T splitEntry)
	{
		assert startCoord < endCoord;
		T entry = control.createEntry(startCoord, endCoord);
		boolean added = entries.add(entry);
		assert added;
		
		if (splitEntry == null)
		{
			assert (entry == entries.first() && (entries.size() == 1 || endCoord == entries.higher(entry).startCoord)) 
					|| (entry == entries.last() && (entries.size() == 1 || startCoord == entries.lower(entry).endCoord));
		}
		else
		{
			assert splitEntry.startCoord < startCoord;
			assert splitEntry.endCoord == endCoord;

			splitEntry.endCoord = startCoord;
			control.entrySplit(splitEntry, entry);
		}
		
		if (log.isTraceEnabled())
		{
			log.trace(logId() + ": added entry " + entry + ", split " + splitEntry);
		}
		
		return entry;
	}
	
	public void addMargins(int extent)
	{
		if (log.isTraceEnabled())
		{
			log.trace("add margins to extent " + extent);
		}
		
		T firstEntry = entries.higher(entries.first());
		int marginStart = (firstEntry == null || firstEntry.startCoord > 0) ? 0 : firstEntry.startCoord;
		
		T lastEntry = entries.lower(entries.last());
		int marginEnd = (lastEntry == null || lastEntry.endCoord < extent) ? extent : lastEntry.endCoord;

		DimensionRange<T> range = getRange(marginStart, marginEnd);
		addEntries(range);
	}

	public void removeEntry(T entry, T prevEntry)
	{
		assert prevEntry.endCoord == entry.startCoord;
		boolean removed = entries.remove(entry);
		assert removed;
		prevEntry.endCoord = entry.endCoord;
		
		if (log.isTraceEnabled())
		{
			log.trace(logId() + ": removed entry at " + entry.startCoord);
		}
	}
	
	public NavigableSet<T> getEntries()
	{
		return entries;
	}
	
	public SortedSet<T> getUserEntries()
	{
		return entries.subSet(entries.first(), false, entries.last(), false);
	}

	public DimensionControl<T> getControl()
	{
		return control;
	}
}
