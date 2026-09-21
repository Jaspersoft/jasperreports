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
package net.sf.jasperreports.crosstabs.fill.calculation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.crosstabs.fill.BucketOrderer;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketDefinition.Bucket;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketValueOrderDecorator.OrderPosition;
import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition.MeasureValue;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CrosstabBucketingService extends BucketingService implements BucketingData
{
	
	private static final Log log = LogFactory.getLog(CrosstabBucketingService.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_DATA_NOT_PROCESSED = "crosstabs.calculation.data.not.processed";
	
	protected HeaderCell[][] colHeaders;
	protected HeaderCell[][] rowHeaders;
	protected CrosstabCell[][] cells;

	public CrosstabBucketingService(BucketingServiceContext serviceContext,
			List<BucketDefinition> rowBuckets,
			List<BucketDefinition> columnBuckets,
			List<MeasureDefinition> measures,
			boolean sorted, boolean[][] retrieveTotal)
	{
		super(serviceContext, rowBuckets, columnBuckets, measures, 
				sorted, retrieveTotal);
	}
	
	public void createCrosstab() throws JRException
	{
		if (!processed)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_DATA_NOT_PROCESSED,
					(Object[])null);
		}
		
		if (!hasData())
		{
			// nothing to do
			return;
		}
		
		CollectedList[] collectedHeaders = new CollectedList[BucketingService.DIMENSIONS];
		collectedHeaders[DIMENSION_ROW] = createHeadersList(DIMENSION_ROW, bucketValueMap, 0, false);
		
		BucketMap columnTotalsMap = null;
		if (allBuckets[0].computeTotal())
		{
			columnTotalsMap = bucketValueMap;
			for (int i = 0; i < rowBucketCount; ++i)
			{
				columnTotalsMap = (BucketMap) columnTotalsMap.getTotalEntry().getValue();
			}
		}
		
		collectedHeaders[DIMENSION_COLUMN] = createHeadersList(DIMENSION_COLUMN,
				// using column totals for sorting if needed
				columnTotalsMap == null ? columnBucketMap : columnTotalsMap, 
				0, false);
		
		int rowBuckets = collectedHeaders[BucketingService.DIMENSION_ROW].span;
		int colBuckets = collectedHeaders[BucketingService.DIMENSION_COLUMN].span;

		int bucketMeasureCount = rowBuckets * colBuckets * origMeasureCount;
		
		if (log.isDebugEnabled())
		{
			log.debug("crosstab has " + rowBuckets + " row buckets, " 
					+ colBuckets + " column buckets and " 
					+ origMeasureCount + " measures");
			log.debug("bucket measure count is " + bucketMeasureCount 
					+ ", limit " + bucketMeasureLimit);
		}
		
		checkBucketMeasureCount(bucketMeasureCount);
		
		colHeaders = createHeaders(BucketingService.DIMENSION_COLUMN, collectedHeaders, columnTotalsMap);
		rowHeaders = createHeaders(BucketingService.DIMENSION_ROW, collectedHeaders, bucketValueMap);
		
		cells = new CrosstabCell[rowBuckets][colBuckets];
		fillCells(collectedHeaders, bucketValueMap, 0, new int[]{0, 0}, new ArrayList<>(), new ArrayList<>());
	}

	protected HeaderCell[][] createHeaders(byte dimension, CollectedList[] headersLists, BucketMap totalsMap)
	{
		HeaderCell[][] headers = new HeaderCell[buckets[dimension].length][headersLists[dimension].span];
		
		List<Bucket> vals = new ArrayList<>();
		fillHeaders(dimension, headers, 0, 0, headersLists[dimension], vals, totalsMap);
		
		return headers;
	}
	
	
	protected CollectedList createHeadersList(byte dimension, BucketMap bucketMap, int level, boolean total) 
			throws JRException
	{
		BucketDefinition bucketDefinition = allBuckets[bucketMap.level];
		CrosstabTotalPositionEnum totalPosition = bucketDefinition.getTotalPosition();
		
		BucketOrderer bucketOrderer = bucketDefinition.getOrderer();
		CollectedList headers;
		if (bucketOrderer != null)
		{
			headers = new OrderedCollectedList(bucketDefinition);
			bucketOrderer.init(this);
		}
		else
		{
			headers = new SequentialCollectedList(totalPosition);
		}

		for (Iterator<Map.Entry<Bucket, Object>> it = bucketMap.entryIterator(); it.hasNext();)
		{
			Map.Entry<Bucket, Object> entry = it.next();
			Bucket bucketValue = entry.getKey();

			boolean totalBucket = bucketValue.isTotal();
			boolean createHeader = !totalBucket || total || totalPosition != CrosstabTotalPositionEnum.NONE;

			if (createHeader)
			{
				CollectedList nextHeaders;
				if (level + 1 < buckets[dimension].length)
				{
					BucketMap nextMap = (BucketMap) entry.getValue();
					nextHeaders = createHeadersList(dimension, nextMap, level + 1, total || totalBucket);
				}
				else
				{
					nextHeaders = new SequentialCollectedList(CrosstabTotalPositionEnum.NONE);
					nextHeaders.span = 1;
				}
				nextHeaders.key = bucketValue;
				if (bucketOrderer != null)
				{
					Object orderValue = bucketOrderer.getOrderValue(bucketMap, bucketValue);
					nextHeaders.orderValue = orderValue;
				}
				headers.add(nextHeaders);
			}
		}

		if (headers.span == 0)
		{
			headers.span = 1;
		}

		return headers;
	}

	
	@Override
	public MeasureValue[] getMeasureTotals(BucketMap bucketMap, Bucket bucket)
	{
		Object bucketValue = bucketMap.get(bucket);
		for (int idx = bucketMap.level + 1; idx < rowBucketCount + colBucketCount; ++idx)
		{
			bucketValue = ((BucketMap) bucketValue).getTotalEntry().getValue();
		}
		MeasureValue[] totals = (MeasureValue[]) bucketValue;
		
		MeasureValue[] userTotals = getUserMeasureValues(totals);
		return userTotals;
	}

	
	protected void fillHeaders(byte dimension, HeaderCell[][] headers, int level, int col, CollectedList list, 
			List<Bucket> vals, BucketMap totalsMap)
	{
		if (level == buckets[dimension].length)
		{
			return;
		}
		
		for (Iterator<CollectedList> it = list.iterator(); it.hasNext();)
		{
			CollectedList subList = it.next();
			
			vals.add(subList.key);
			
			if (!subList.key.isTotal())
			{
				fillHeaders(dimension, headers, level + 1, col, subList, vals, totalsMap);
			}
			
			int depthSpan = subList.key.isTotal() ? buckets[dimension].length - level : 1;
			Bucket[] values = new Bucket[buckets[dimension].length];
			vals.toArray(values);
			
			MeasureValue[][] totals = retrieveHeaderTotals(dimension, values, totalsMap);
			
			if (subList.key.isTotal() 
					|| buckets[dimension][level].isMergeHeaderCells()
					|| level >= buckets[dimension].length - 1)
			{
				// a single merged header
				headers[level][col] = new HeaderCell(values, subList.span, depthSpan, totals);
			}
			else
			{
				// creating one header for each header on the next level
				for (int c = col; c < col + subList.span; ++c)
				{
					HeaderCell subheader = headers[level + 1][c];
					if (subheader != null)
					{
						headers[level][c] = new HeaderCell(values, subheader.getLevelSpan(), depthSpan, totals);
					}
				}
			}
			
			col += subList.span;
			vals.remove(vals.size() - 1);
		}
	}


	protected MeasureValue[][] retrieveHeaderTotals(byte dimension, Bucket[] values, BucketMap totalsMap)
	{
		// an array to advance on bucket levels with values and totals
		int levelCount = buckets[dimension].length;
		Object[] levelBuckets = new Object[levelCount + 1];
		levelBuckets[0] = totalsMap;
		
		for (int idx = 0; idx < levelCount; ++idx)
		{
			// save this as it gets modified
			Object valueBucket = levelBuckets[idx];
			
			// advance with totals
			for (int lIdx = 0; lIdx <= idx; ++lIdx)
			{
				if (levelBuckets[lIdx] != null)
				{
					MapEntry entry = ((BucketMap) levelBuckets[lIdx]).getTotalEntry();
					levelBuckets[lIdx] = entry == null ? null : entry.getValue();
				}
			}
			
			// advance with value if it exists, or total otherwise
			if (valueBucket != null)
			{
				if (idx < values.length && values[idx] != null)
				{
					levelBuckets[idx + 1] = ((BucketMap) valueBucket).get(values[idx]);
				}
				else
				{
					// this is the total computed in the previous loop
					levelBuckets[idx + 1] = levelBuckets[idx];
				}
			}
		}
		
		if (dimension == DIMENSION_ROW)
		{
			// we need to advance through column totals
			for (int idx = 0; idx < colBucketCount; ++idx)
			{
				for (int lIdx = 0; lIdx <= levelCount; ++lIdx)
				{
					if (levelBuckets[lIdx] != null)
					{
						MapEntry entry = ((BucketMap) levelBuckets[lIdx]).getTotalEntry();
						levelBuckets[lIdx] = entry == null ? null : entry.getValue();
					}
				}
			}
		}
		
		MeasureValue[][] totals = new MeasureValue[levelCount + 1][];
		for (int lIdx = 0; lIdx <= levelCount; ++lIdx)
		{
			MeasureValue[] measureValues = (MeasureValue[]) levelBuckets[lIdx];
			if (measureValues != null)
			{
				totals[lIdx] = getUserMeasureValues(measureValues);
			}
		}
		return totals;
	}


	protected void fillCells(CollectedList[] collectedHeaders, BucketMap bucketMap, int level, int[] pos, List<Bucket> vals, List<BucketMap> bucketMaps)
	{
		bucketMaps.add(bucketMap);
		
		byte dimension = level < rowBucketCount ? DIMENSION_ROW : DIMENSION_COLUMN;
		boolean last = level == allBuckets.length - 1;

		CollectedList[] nextCollected = null;
		if (!last)
		{
			nextCollected = new CollectedList[DIMENSIONS];
			for (int d = 0; d < DIMENSIONS; ++d)
			{
				if (d != dimension)
				{
					nextCollected[d] = collectedHeaders[d];
				}
			}
		}
		
		boolean incrementRow = level == buckets[BucketingService.DIMENSION_ROW].length - 1;
				
		CollectedList collectedList = collectedHeaders[dimension];
		
		for (Iterator<CollectedList> it = collectedList.iterator(); it.hasNext();)
		{
			CollectedList list = it.next();
			Object bucketValue = bucketMap == null ? null : bucketMap.get(list.key);
			
			vals.add(list.key);
			if (last)
			{
				fillCell(pos, vals, bucketMaps, (MeasureValue[]) bucketValue);
			}
			else
			{				
				nextCollected[dimension] = list;
				BucketMap nextMap = bucketValue == null ? null : (BucketMap) bucketValue;
				
				fillCells(nextCollected, nextMap, level + 1, pos, vals, bucketMaps);
			}
			vals.remove(vals.size() - 1);
				
			if (incrementRow)
			{
				++pos[0];
				pos[1] = 0;
			}
		}
		
		bucketMaps.remove(bucketMaps.size() - 1);
	}


	protected void fillCell(int[] pos, List<Bucket> vals, List<BucketMap> bucketMaps, MeasureValue[] values)
	{
		Iterator<Bucket> valsIt = vals.iterator();
		Bucket[] rowValues = new Bucket[buckets[BucketingService.DIMENSION_ROW].length];
		for (int i = 0; i < rowValues.length; i++)
		{
			rowValues[i] = valsIt.next();
		}
		
		Bucket[] columnValues = new Bucket[buckets[BucketingService.DIMENSION_COLUMN].length];
		for (int i = 0; i < columnValues.length; i++)
		{
			columnValues[i] = valsIt.next();
		}
		
		MeasureValue[] measureVals = values == null ? zeroUserMeasureValues : getUserMeasureValues(values);
		MeasureValue[][][] totals = retrieveTotals(vals, bucketMaps);
		cells[pos[0]][pos[1]] = new CrosstabCell(rowValues, columnValues, measureVals, totals);
		++pos[1];
	}
	
	
	protected MeasureValue[][][] retrieveTotals(List<Bucket> vals, List<BucketMap> bucketMaps)
	{
		MeasureValue[][][] totals = new MeasureValue[rowBucketCount + 1][colBucketCount + 1][];
		
		for (int row = rowRetrTotalMax; row >= rowRetrTotalMin; --row)
		{
			if (!rowRetrTotals[row])
			{
				continue;
			}
			
			BucketMap rowMap = bucketMaps.get(row);
			for (int i = row; rowMap != null && i < rowBucketCount; ++i)
			{
				MapEntry totalEntry = rowMap.getTotalEntry();
				rowMap = totalEntry == null ? null : (BucketMap) totalEntry.getValue();
			}

			for (int col = 0; col <= rowRetrColMax[row]; ++col)
			{
				BucketMap colMap = rowMap;
				
				if (col < colBucketCount - 1)
				{
					if (row == rowBucketCount)
					{
						rowMap = bucketMaps.get(rowBucketCount + col + 1);
					}
					else if (rowMap != null)
					{
						rowMap = (BucketMap) rowMap.get(vals.get(rowBucketCount + col));
					}
				}
				
				if (!retrieveTotal[row][col])
				{
					continue;
				}
				
				for (int i = col + 1; colMap != null && i < colBucketCount; ++i)
				{
					colMap = (BucketMap) colMap.getTotalEntry().getValue();
				}
				
				if (colMap != null)
				{
					if (col == colBucketCount)
					{
						MeasureValue[] measureValues = (MeasureValue[]) colMap.get(vals.get(rowBucketCount + colBucketCount - 1));
						if (measureValues != null)
						{
							totals[row][col] = getUserMeasureValues(measureValues);
						}
					}
					else
					{
						MapEntry totalEntry = colMap.getTotalEntry();
						if (totalEntry != null)
						{
							MeasureValue[] totalValues = (MeasureValue[]) totalEntry.getValue();
							totals[row][col] = getUserMeasureValues(totalValues);
						}
					}
				}
				
				if (totals[row][col] == null)
				{
					totals[row][col] = zeroUserMeasureValues;
				}
			}
		}

		return totals;
	}
	
	
	/**
	 * Returns the crosstab column headers.
	 * <p>
	 * {@link #processData() processData()} has to be called before this.
	 * 
	 * @return the crosstab column headers
	 */
	public HeaderCell[][] getColumnHeaders()
	{
		return colHeaders;
	}
	
	
	/**
	 * Returns the crosstab row headers.
	 * <p>
	 * {@link #processData() processData()} has to be called before this.
	 * 
	 * @return the crosstab row headers
	 */
	public HeaderCell[][] getRowHeaders()
	{
		return rowHeaders;
	}
	
	
	/**
	 * Returns the crosstab data cells.
	 * <p>
	 * {@link #processData() processData()} has to be called before this.
	 * 
	 * @return the crosstab data cells
	 */
	public CrosstabCell[][] getCrosstabCells()
	{
		return cells;
	}
	
	protected static abstract class CollectedList
	{
		int span;
		Bucket key;
		Object orderValue;
		
		CollectedList()
		{
			span = 0;
		}

		public abstract Iterator<CollectedList> iterator();
		
		public void add(CollectedList sublist)
		{
			addSublist(sublist);
			incrementSpan(sublist);
		}

		protected abstract void addSublist(CollectedList sublist);

		private void incrementSpan(CollectedList sublist)
		{
			if (sublist != null)
			{
				span += sublist.span;
			}
			else
			{
				span += 1;
			}
		}
		
		@Override
		public String toString()
		{
			return key + "/" + span + ": " + super.toString();
		}
	}
	
	protected static class SequentialCollectedList extends CollectedList
	{
		final CrosstabTotalPositionEnum totalPosition;
		final LinkedList<CollectedList> list;
		
		SequentialCollectedList(CrosstabTotalPositionEnum totalPosition)
		{
			this.totalPosition = totalPosition;
			
			list = new LinkedList<>();
		}

		@Override
		public Iterator<CollectedList> iterator()
		{
			return list.iterator();
		}

		@Override
		protected void addSublist(CollectedList sublist)
		{
			if (sublist.key.isTotal() && totalPosition == CrosstabTotalPositionEnum.START)
			{
				list.addFirst(sublist);
			}
			else
			{
				list.add(sublist);
			}
		}
	}
	
	protected static class OrderedCollectedList extends CollectedList
	{
		final TreeSet<CollectedList> list;
		
		OrderedCollectedList(BucketDefinition bucketDefinition)
		{
			super();
			
			CollectedListComparator comparator = 
				new CollectedListComparator(bucketDefinition);
			list = new TreeSet<>(comparator);
		}

		@Override
		public Iterator<CollectedList> iterator()
		{
			return list.iterator();
		}

		@Override
		protected void addSublist(CollectedList sublist)
		{
			list.add(sublist);
		}
	}
	
	protected static class CollectedListComparator implements Comparator<CollectedList>
	{
		final String EXCEPTION_MESSAGE_KEY_TWO_TOTAL_KEYS_IN_SAME_LIST = "crosstabs.calculation.two.total.keys.in.same.list";
		final BucketOrderer bucketOrderer;
		final boolean totalFirst;
		
		CollectedListComparator(BucketDefinition bucketDefinition)
		{
			this.bucketOrderer = bucketDefinition.getOrderer();
			this.totalFirst = bucketDefinition.getTotalPosition() 
					== CrosstabTotalPositionEnum.START;
		}

		@Override
		public int compare(CollectedList l1, CollectedList l2)
		{
			if (l1 == l2)
			{
				return 0;
			}
			
			int order;
			if (l1.key.isTotal())
			{
				if (l2.key.isTotal())
				{
					// this should not happen
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_TWO_TOTAL_KEYS_IN_SAME_LIST,
							(Object[])null);
				}
				
				order = totalFirst ? -1 : 1;
			}
			else if (l2.key.isTotal())
			{
				order = totalFirst ? 1 : -1;
			}
			else
			{
				// check order positions
				OrderPosition orderPosition1 = l1.key.getOrderPosition();
				OrderPosition orderPosition2 = l2.key.getOrderPosition();
				if (orderPosition1 != orderPosition2)
				{
					order = orderPosition1.comparePosition(orderPosition2);
				}
				else
				{
					// compare the order values
					order = bucketOrderer.compareOrderValues(
							l1.orderValue, l2.orderValue);
					
					if (order == 0)
					{
						// if order values are equal, fallback to bucket value order
						order = l1.key.compareTo(l2.key);
					}
				}
			}
			
			return order;
		}		
	}

	@Override
	public BucketingServiceContext getServiceContext()
	{
		return serviceContext;
	}

	@Override
	public Bucket getColumnTotalBucket(int columnGroupIndex)
	{
		if (columnGroupIndex < 0 || columnGroupIndex >= colBucketCount)
		{
			throw new IndexOutOfBoundsException("Column group index " + columnGroupIndex 
					+ " out of range, column group count is " + colBucketCount);
		}
		
		return allBuckets[rowBucketCount + columnGroupIndex].VALUE_TOTAL;
	}

	@Override
	public Bucket getColumnBucket(int columnGroupIndex, Object value)
	{
		if (columnGroupIndex < 0 || columnGroupIndex >= colBucketCount)
		{
			throw new IndexOutOfBoundsException("Column group index " + columnGroupIndex 
					+ " out of range, column group count is " + colBucketCount);
		}
		
		BucketDefinition bucket = allBuckets[rowBucketCount + columnGroupIndex];
		return value == null ? bucket.VALUE_NULL : bucket.create(value);
	}

	@Override
	public MeasureValue[] getMeasureValues(BucketMap bucketMap, Bucket bucket, 
			List<Bucket> columnValues)
	{
		Object bucketValue = bucketMap.get(bucket);
		// advance to row totals
		for (int idx = bucketMap.level + 1; idx < rowBucketCount; ++idx)
		{
			bucketValue = ((BucketMap) bucketValue).getTotalEntry().getValue();
		}
		
		// advance to column values
		Iterator<Bucket> colValuesIt = columnValues.iterator();
		for (int idx = 0; idx < colBucketCount && bucketValue != null; ++idx)
		{
			if (colValuesIt.hasNext())
			{
				Bucket columnBucket = colValuesIt.next();
				bucketValue = ((BucketMap) bucketValue).get(columnBucket);
			}
			else
			{
				// if we don't have a value for the column group, use the total
				bucketValue = ((BucketMap) bucketValue).getTotalEntry().getValue();
			}
		}
		
		MeasureValue[] measureValues;
		if (bucketValue == null)
		{
			// the column values were not present for the row, using zero values
			measureValues = zeroUserMeasureValues;
		}
		else
		{
			measureValues = getUserMeasureValues((MeasureValue[]) bucketValue);
		}
		return measureValues;
	}

}
