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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketDefinition.Bucket;
import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition.MeasureValue;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRCalculable;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Bidimensional bucketing engine.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class BucketingService
{
	
	public static final String EXCEPTION_MESSAGE_KEY_BUCKET_MEASURE_LIMIT = "crosstabs.bucket.measure.limit";
	public static final String EXCEPTION_MESSAGE_KEY_BUCKET_DATA_PROCESSED = "crosstabs.bucket.data.processed";
	
	@Property(
			category = PropertyConstants.CATEGORY_CROSSTAB,
			valueType = Integer.class,
			defaultValue = "0",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_1_3_4
			)
	public static final String PROPERTY_BUCKET_MEASURE_LIMIT = JRPropertiesUtil.PROPERTY_PREFIX + "crosstab.bucket.measure.limit";
	
	protected static final byte DIMENSION_ROW = 0;

	protected static final byte DIMENSION_COLUMN = 1;

	protected static final int DIMENSIONS = 2;

	protected final BucketingServiceContext serviceContext;

	protected final BucketDefinition[] allBuckets;
	protected final BucketDefinition[][] buckets;

	protected final int rowBucketCount;
	protected final int colBucketCount;

	protected final boolean[][] retrieveTotal;
	protected boolean[] rowRetrTotals;
	protected int rowRetrTotalMin;
	protected int rowRetrTotalMax;
	protected int[] rowRetrColMax;

	protected final MeasureDefinition[] measures;
	protected final int origMeasureCount;
	protected final int[] measureIndexes;

	protected final boolean sorted;

	protected final BucketMap bucketValueMap;
	protected final BucketMap columnBucketMap;
	protected long dataCount;
	protected boolean processed;

	protected final MeasureValue[] zeroMeasureValues;
	protected final MeasureValue[] zeroUserMeasureValues;

	protected final int bucketMeasureLimit;
	private int runningBucketMeasureCount;
	
	/**
	 * Creates a crosstab bucketing engine.
	 * 
	 * @param serviceContext the bucketing service context
	 * @param rowBuckets the row bucket definitions
	 * @param columnBuckets the column bucket definitions
	 * @param measures the measure definitions
	 * @param sorted whether the data is presorted
	 * @param retrieveTotal totals to retrieve along with the cell values
	 */
	public BucketingService(
			BucketingServiceContext serviceContext, 
			List<BucketDefinition> rowBuckets, 
			List<BucketDefinition> columnBuckets, 
			List<MeasureDefinition> measures, 
			boolean sorted, 
			boolean[][] retrieveTotal
			)
	{
		this.serviceContext = serviceContext;
		
		this.sorted = sorted;

		buckets = new BucketDefinition[DIMENSIONS][];
		
		rowBucketCount = rowBuckets.size();
		buckets[DIMENSION_ROW] = new BucketDefinition[rowBucketCount];
		rowBuckets.toArray(buckets[DIMENSION_ROW]);
		
		colBucketCount = columnBuckets.size();
		buckets[DIMENSION_COLUMN] = new BucketDefinition[colBucketCount];
		columnBuckets.toArray(buckets[DIMENSION_COLUMN]);

		allBuckets = new BucketDefinition[rowBucketCount + colBucketCount];
		System.arraycopy(buckets[DIMENSION_ROW], 0, allBuckets, 0, rowBucketCount);
		System.arraycopy(buckets[DIMENSION_COLUMN], 0, allBuckets, rowBucketCount, colBucketCount);

		origMeasureCount = measures.size();
		List<MeasureDefinition> measuresList = new ArrayList<>(measures.size() * 2);
		List<Integer> measureIndexList = new ArrayList<>(measures.size() * 2);
		for (int i = 0; i < measures.size(); ++i)
		{
			MeasureDefinition measure =  measures.get(i);
			addMeasure(measure, i, measuresList, measureIndexList);
		}
		this.measures = new MeasureDefinition[measuresList.size()];
		measuresList.toArray(this.measures);
		this.measureIndexes = new int[measureIndexList.size()];
		for (int i = 0; i < measureIndexes.length; ++i)
		{
			measureIndexes[i] = measureIndexList.get(i);
		}

		this.retrieveTotal = retrieveTotal;
		checkTotals();
		
		bucketValueMap = createBucketMap(0);
		columnBucketMap = createBucketMapMap(rowBucketCount);
		
		zeroMeasureValues = initMeasureValues();
		zeroUserMeasureValues = initUserMeasureValues();
		
		bucketMeasureLimit = JRPropertiesUtil.getInstance(serviceContext.getJasperReportsContext()).getIntegerProperty(PROPERTY_BUCKET_MEASURE_LIMIT, 0);
	}


	protected void checkTotals()
	{
		rowRetrTotalMin = rowBucketCount + 1;
		rowRetrTotalMax = -1;
		rowRetrTotals = new boolean[rowBucketCount + 1];
		rowRetrColMax = new int[rowBucketCount + 1];
		for (int row = 0; row <= rowBucketCount; ++row)
		{
			rowRetrColMax[row] = -1;
			boolean total = false;
			for (int col = 0; col <= colBucketCount; ++col)
			{
				if (retrieveTotal[row][col])
				{
					total = true;
					rowRetrColMax[row] = col;
				}
			}
			
			rowRetrTotals[row] = total;
			if (total)
			{
				if (row < rowRetrTotalMin)
				{
					rowRetrTotalMin = row;
				}
				rowRetrTotalMax = row;
				
				if (row < rowBucketCount)
				{
					allBuckets[row].setComputeTotal();
				}
			}
		}
		
		for (int col = 0; col < colBucketCount; ++col)
		{
			BucketDefinition colBucket = allBuckets[rowBucketCount + col];
			if (!colBucket.computeTotal())
			{
				boolean total = false;
				for (int row = 0; !total && row <= rowBucketCount; ++row)
				{
					total = retrieveTotal[row][col];
				}
				
				if (total)
				{
					colBucket.setComputeTotal();
				}
			}
		}
		
		for (int d = 0; d < DIMENSIONS; ++d)
		{
			boolean dTotal = false;
			
			for (int i = 0; i < buckets[d].length; ++i)
			{
				if (dTotal)
				{
					buckets[d][i].setComputeTotal();
				}
				else
				{
					dTotal = buckets[d][i].computeTotal();
				}
			}
		}
	}


	/**
	 * Clears all the accumulated and computed data.
	 */
	public void clear()
	{
		bucketValueMap.clear();
		columnBucketMap.clear();
		processed = false;
		dataCount = 0;
		runningBucketMeasureCount = 0;
	}
	
	protected BucketMap createBucketMap(int level)
	{
		BucketMap map;
		if (sorted)
		{
			map = new BucketListMap(level);
		}
		else
		{
			map = createBucketMapMap(level);
		}
		return map;
	}
	
	protected BucketMapMap createBucketMapMap(int level)
	{
		boolean sortedMap = !sorted && allBuckets[level].isSorted();
		return new BucketMapMap(level, sortedMap);
	}
	
	protected BucketMapMap createRowTotalsBucketMap()
	{
		BucketMapMap totalsBucketMap = new BucketMapMap(rowBucketCount, false);
		totalsBucketMap.copyEntries(columnBucketMap);
		return totalsBucketMap;
	}

	protected void addMeasure(
			MeasureDefinition measure, 
			int index, 
			List<MeasureDefinition> measuresList, 
			List<Integer> measureIndexList
			)
	{
		switch (measure.getCalculation())
		{
			case AVERAGE:
			case VARIANCE:
			{
				MeasureDefinition sumMeasure = MeasureDefinition.createHelperMeasure(measure, CalculationEnum.SUM);
				addMeasure(sumMeasure, index, measuresList, measureIndexList);
				MeasureDefinition countMeasure = MeasureDefinition.createHelperMeasure(measure, CalculationEnum.COUNT);
				addMeasure(countMeasure, index, measuresList, measureIndexList);
				break;
			}
			case STANDARD_DEVIATION:
			{
				MeasureDefinition varianceMeasure = MeasureDefinition.createHelperMeasure(measure, CalculationEnum.VARIANCE);
				addMeasure(varianceMeasure, index, measuresList, measureIndexList);
				break;
			}
			case DISTINCT_COUNT:
			{
				MeasureDefinition countMeasure = MeasureDefinition.createDistinctCountHelperMeasure(measure);
				addMeasure(countMeasure, index, measuresList, measureIndexList);
				break;
			}
			default:
		}

		measuresList.add(measure);
		measureIndexList.add(index);
	}

	
	/**
	 * Feeds data to the engine.
	 *  
	 * @param bucketValues the bucket values
	 * @param measureValues the measure values
	 * @throws JRException
	 */
	public void addData(Object[] bucketValues, Object[] measureValues) throws JRException
	{
		if (processed)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_BUCKET_DATA_PROCESSED,  
					(Object[])null 
					);
		}
		
		++dataCount;
		
		Bucket[] bucketVals = getBucketValues(bucketValues);

		MeasureValue[] values = bucketValueMap.insertMeasureValues(bucketVals, true, 0);
		for (int i = 0; i < measures.length; ++i)
		{
			Object measureValue = measureValues[measureIndexes[i]];
			values[i].addValue(measureValue);
		}
		
		// collect column bucket values
		columnBucketMap.insertMeasureValues(bucketVals, false, rowBucketCount);
	}
	
	protected void bucketMeasuresCreated()
	{
		runningBucketMeasureCount += origMeasureCount;
		
		checkBucketMeasureCount(runningBucketMeasureCount);
	}

	protected Bucket[] getBucketValues(Object[] bucketValues)
	{
		Bucket[] bucketVals = new Bucket[allBuckets.length];

		for (int i = 0; i < allBuckets.length; ++i)
		{
			BucketDefinition bucket = allBuckets[i];
			Object value = bucketValues[i];
			bucketVals[i] = bucket.create(value);
		}
		
		return bucketVals;
	}

	protected MeasureValue[] initMeasureValues()
	{
		MeasureValue[] values;
		values = new MeasureValue[measures.length];

		for (int i = 0; i < measures.length; ++i)
		{
			MeasureDefinition measure = measures[i];
			values[i] = measure.new MeasureValue();

			switch (measure.getCalculation())
			{
				case AVERAGE:
				case VARIANCE:
				{
					values[i].setHelper(values[i - 2], JRCalculable.HELPER_SUM);
					values[i].setHelper(values[i - 1], JRCalculable.HELPER_COUNT);
					break;
				}
				case STANDARD_DEVIATION:
				{
					values[i].setHelper(values[i - 1], JRCalculable.HELPER_VARIANCE);
					break;
				}
				case DISTINCT_COUNT:
				{
					values[i].setHelper(values[i - 1], JRCalculable.HELPER_COUNT);
					break;
				}
				default:
			}
		}
		return values;
	}

	protected MeasureValue[] initUserMeasureValues()
	{
		MeasureValue[] vals = new MeasureValue[origMeasureCount];
		
		for (int c = 0, i = 0; i < measures.length; ++i)
		{
			if (!measures[i].isSystemDefined())
			{
				vals[c] = measures[i].new MeasureValue();
				++c;
			}
		}
		
		return vals;
	}

	
	/**
	 * Processes the data which was fed to the engine.
	 * <p>
	 * This method should be called after the data has been exhausted.
	 * The processing consists of total calculations and crosstab table creation.
	 * 
	 * @throws JRException
	 */
	public void processData() throws JRException
	{
		if (!processed)
		{
			if (dataCount > 0)
			{
				if (allBuckets[rowBucketCount - 1].computeTotal() || allBuckets[allBuckets.length - 1].computeTotal())
				{
					//FIXME doing this just to insert total entries, we don't actually need to sum anything
					computeTotals(columnBucketMap);
					
					computeTotals(bucketValueMap);
				}
			}
			
			processed = true;
		}
	}

	
	/**
	 * Checks whether there is any data accumulated by the engine.
	 * 
	 * @return <code>true</code> if and only if the engine has any accumulated data
	 */
	public boolean hasData()
	{
		return dataCount > 0;
	}
	
	
	/**
	 * Returns the measure values for a set of bucket values.
	 * 
	 * @param bucketValues the bucket values
	 * @return the measure values corresponding to the bucket values
	 */
	public MeasureValue[] getMeasureValues(Bucket[] bucketValues)
	{
		BucketMap map = bucketValueMap;
		
		for (int i = 0; map != null && i < allBuckets.length - 1; ++i)
		{
			map = (BucketMap) map.get(bucketValues[i]);
		}
		
		return map == null ? null : (MeasureValue[]) map.get(bucketValues[allBuckets.length - 1]);
	}

	public MeasureValue[] getUserMeasureValues(MeasureValue[] values)
	{
		MeasureValue[] vals = new MeasureValue[origMeasureCount];
		
		for (int c = 0, i = 0; i < measures.length; ++i)
		{
			if (!measures[i].isSystemDefined())
			{
				vals[c] = values[i];
				++c;
			}
		}
		
		return vals;
	}

	public MeasureValue[] getZeroUserMeasureValues()
	{
		return zeroUserMeasureValues;
	}
	
	/**
	 * Returns the grand total measure values.
	 * 
	 * @return the grand total measure values
	 */
	public MeasureValue[] getGrandTotals()
	{
		BucketMap map = bucketValueMap;
		
		for (int i = 0; map != null && i < allBuckets.length - 1; ++i)
		{
			map = (BucketMap) map.getTotalEntry().getValue();
		}
		
		return map == null ? null : (MeasureValue[]) map.getTotalEntry().getValue();
	}

	
	protected void computeTotals(BucketMap bucketMap) throws JRException
	{
		byte dimension = bucketMap.level < rowBucketCount ? DIMENSION_ROW : DIMENSION_COLUMN;
		
		if (dimension == DIMENSION_COLUMN && !allBuckets[allBuckets.length - 1].computeTotal())
		{
			return;
		}
		
		if (!bucketMap.last)
		{
			for (Iterator<Map.Entry<Bucket, Object>> it = bucketMap.entryIterator(); it.hasNext();)
			{
				Map.Entry<Bucket, Object> entry = it.next();

				computeTotals((BucketMap) entry.getValue());
			}
		}
		
		if (allBuckets[bucketMap.level].computeTotal())
		{
			if (dimension == DIMENSION_COLUMN)
			{
				computeColumnTotal(bucketMap);
			}
			else
			{
				computeRowTotals(bucketMap);
			}
		}
	}


	protected void sumVals(MeasureValue[] totals, MeasureValue[] vals) throws JRException
	{
		for (int i = 0; i < measures.length; i++)
		{
			totals[i].addValue(vals[i]);
		}
	}
	
	protected void computeColumnTotal(BucketMap bucketMap) throws JRException
	{
		MeasureValue[] totals = initMeasureValues();
		
		for (Iterator<Map.Entry<Bucket, Object>> it = bucketMap.entryIterator(); it.hasNext();)
		{
			Map.Entry<Bucket, Object> entry = it.next();
			
			for (int i = bucketMap.level + 1; i < allBuckets.length; ++i)
			{
				entry = ((BucketMap) entry.getValue()).getTotalEntry();
			}
			
			sumVals(totals, (MeasureValue[]) entry.getValue());
		}
				
		for (int i = bucketMap.level + 1; i < allBuckets.length; ++i)
		{
			bucketMap = bucketMap.addTotalNextMap();
		}
		
		bucketMap.addTotalEntry(totals);
	}


	protected void computeRowTotals(BucketMap bucketMap) throws JRException
	{
		BucketMapMap totals = createRowTotalsBucketMap();
		
		for (Iterator<Map.Entry<Bucket, Object>> it = bucketMap.entryIterator(); it.hasNext();)
		{
			Map.Entry<Bucket, Object> entry = it.next();
			
			for (int i = bucketMap.level + 1; i < rowBucketCount; ++i)
			{
				entry = ((BucketMap) entry.getValue()).getTotalEntry();
			}
			
			totals.sumValues((BucketMap) entry.getValue());
		}
		
		BucketMap totalBucketMap = bucketMap;
		for (int i = bucketMap.level + 1; i < rowBucketCount; ++i)
		{
			totalBucketMap = totalBucketMap.addTotalNextMap();
		}
		
		totalBucketMap.addTotalEntry(totals);
	}

	
	static protected class MapEntry implements Map.Entry<Bucket, Object>, Comparable<MapEntry>
	{
		final Bucket key;

		final Object value;
		
		MapEntry(Bucket key, Object value)
		{
			this.key = key;
			this.value = value;
		}

		@Override
		public Bucket getKey()
		{
			return key;
		}

		@Override
		public Object getValue()
		{
			return value;
		}

		@Override
		public Object setValue(Object value)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(MapEntry o)
		{
			return key.compareTo(o.key);
		}
		
		@Override
		public String toString()
		{
			return key + "=" + value;
		}
	}
	
	public abstract class BucketMap
	{
		final int level;
		final boolean last;
		final Bucket totalKey;

		BucketMap(int level)
		{
			this.level = level;
			this.last = level == allBuckets.length - 1;
			totalKey = allBuckets[level].VALUE_TOTAL;
		}

		BucketMap addTotalNextMap()
		{
			BucketMap nextMap = createBucketMap(level + 1);
			addTotalEntry(nextMap);
			return nextMap;
		}

		abstract void clear();

		public abstract Iterator<Map.Entry<Bucket, Object>> entryIterator();

		public abstract Object get(Bucket key);
		
		abstract MeasureValue[] insertMeasureValues(Bucket[] bucketValues, boolean createValues, int offset);

/*		abstract void fillKeys(Collection collectedKeys);*/

		abstract void addTotalEntry(Object val);

		public abstract int size();
		
		public abstract Object getTotal();
		
		public abstract MapEntry getTotalEntry();
	}
	
	protected class BucketMapMap extends BucketMap
	{
		Map<Bucket, Object> map;
		
		BucketMapMap(int level, boolean sortedMap)
		{
			super(level);
			
			this.map = sortedMap ? new TreeMap<>() : new LinkedHashMap<>();
		}
		
		@Override
		void clear()
		{
			map.clear();
		}

		@Override
		public Iterator<Map.Entry<Bucket, Object>> entryIterator()
		{
			return map.entrySet().iterator();
		}

		@Override
		public Object get(Bucket key)
		{
			return map.get(key);
		}

		@Override
		MeasureValue[] insertMeasureValues(Bucket[] bucketValues, boolean createValues, int offset)
		{
			BucketMapMap levelMap = this;
			for (int i = offset; i < bucketValues.length - 1; i++)
			{
				BucketMapMap nextMap = (BucketMapMap) levelMap.get(bucketValues[i]);
				if (nextMap == null)
				{
					nextMap = createBucketMapMap(i + 1);
					levelMap.map.put(bucketValues[i], nextMap);
				}

				levelMap = nextMap;
			}

			MeasureValue[] values = (MeasureValue[]) levelMap.get(bucketValues[bucketValues.length - 1]);
			if (values == null)
			{
				if (createValues)
				{
					values = initMeasureValues();
					bucketMeasuresCreated();
				}
				else
				{
					values = zeroMeasureValues;
				}
				
				levelMap.map.put(bucketValues[bucketValues.length - 1], values);
			}

			return values;
		}

		@Override
		public int size()
		{
			return map.size();
		}

		@Override
		void addTotalEntry(Object value)
		{
			map.put(totalKey, value);
		}
		
		@Override
		public Object getTotal()
		{
			return get(totalKey);
		}
		
		@Override
		public MapEntry getTotalEntry()
		{
			Object value = get(totalKey);
			return value == null ? null : new MapEntry(totalKey, value);
		}

		void copyEntries(BucketMap bucketMap)
		{
			for (Iterator<Entry<Bucket, Object>> bucketIterator = bucketMap.entryIterator(); bucketIterator.hasNext();)
			{
				Entry<Bucket, Object> bucketEntry = bucketIterator.next();
				Bucket bucketKey = bucketEntry.getKey();
				
				Object copyBucketValue;
				if (bucketMap.last)
				{
					copyBucketValue = initMeasureValues();
				}
				else
				{
					BucketMap bucketSubMap = (BucketMap) bucketEntry.getValue();
					BucketMapMap copyBucketSubMap = new BucketMapMap(level + 1, false);
					copyBucketSubMap.copyEntries(bucketSubMap);
					copyBucketValue = copyBucketSubMap;
				}
				
				map.put(bucketKey, copyBucketValue);
			}
		}

		void sumValues(BucketMap bucketMap) throws JRException
		{
			for (Iterator<Map.Entry<Bucket, Object>> it = bucketMap.entryIterator(); it.hasNext();)
			{
				Map.Entry<Bucket, Object> entry = it.next();
				
				// find the total entry that matches the map entry.
				// the total map is should contain all collected entries
				//FIXME optimize this for sorted maps where we can assume that the order is the same
				Object value = get(entry.getKey());
				if (last)
				{
					// last level, sum the values
					sumVals((MeasureValue[]) value, (MeasureValue[]) entry.getValue());
				}
				else
				{
					// go to the next level
					((BucketMapMap) value).sumValues((BucketMap) entry.getValue());
				}
			}
		}
		
		@Override
		public String toString()
		{
			return map.toString();
		}
	}

	protected class BucketListMap extends BucketMap
	{
		List<Map.Entry<Bucket, Object>> entries;
		// we maintain a map as well in order to have fast search by key
		// TODO implement this in a single structure
		Map<Bucket, Object> entryMap;

		BucketListMap(int level)
		{
			super(level);

			entries = new ArrayList<>();
			entryMap = new HashMap<>();
		}

		@Override
		void clear()
		{
			entries.clear();
			entryMap.clear();
		}
		
		@Override
		public Iterator<Map.Entry<Bucket, Object>> entryIterator()
		{
			return entries.iterator();
		}

		private void add(Bucket key, Object value)
		{
			entries.add(new MapEntry(key, value));
			entryMap.put(key, value);
		}

		@Override
		public Object get(Bucket key)
		{
			return entryMap.get(key);
		}

		@Override
		MeasureValue[] insertMeasureValues(Bucket[] bucketValues, boolean createValues, int offset)
		{
			int i = offset;
			Object levelObj = this;
			BucketListMap map = null;
			while (i < allBuckets.length)
			{
				map = (BucketListMap) levelObj;
				int size = map.entries.size();
				if (size == 0)
				{
					break;
				}

				MapEntry lastEntry = (MapEntry)map.entries.get(size - 1);
				if (!lastEntry.key.equals(bucketValues[i]))
				{
					break;
				}
				
				++i;
				levelObj = lastEntry.value;
			}

			if (i == allBuckets.length)
			{
				return (MeasureValue[]) levelObj;
			}

			while (i < allBuckets.length - 1)
			{
				BucketListMap nextMap = new BucketListMap(i + 1);
				map.add(bucketValues[i], nextMap);
				map = nextMap;
				++i;
			}

			MeasureValue[] values;
			if (createValues)
			{
				values = initMeasureValues();
				bucketMeasuresCreated();
			}
			else
			{
				values = zeroMeasureValues;
			}
			
			map.add(bucketValues[i], values);
			return values;
		}

		@Override
		public int size()
		{
			return entries.size();
		}

		@Override
		void addTotalEntry(Object value)
		{
			add(totalKey, value);
		}

		@Override
		public Object getTotal()
		{
			MapEntry totalEntry = getTotalEntry();
			return totalEntry == null ? null : totalEntry.getValue();
		}
		
		@Override
		public MapEntry getTotalEntry()
		{
			MapEntry lastEntry = (MapEntry)entries.get(entries.size() - 1);
			if (lastEntry.key.isTotal())
			{
				return lastEntry;
			}
			
			return null;
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			sb.append(entries.stream().map(Object::toString).collect(Collectors.joining(", ")));
			sb.append('}');
			return sb.toString();
		}
	}

	
	protected void checkBucketMeasureCount(int bucketMeasureCount)
	{
		if (bucketMeasureLimit > 0 && bucketMeasureCount > bucketMeasureLimit)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_BUCKET_MEASURE_LIMIT,  
					new Object[]{bucketMeasureLimit} 
					);
		}
	}
	
	public BucketDefinition[] getRowBuckets()
	{
		return buckets[DIMENSION_ROW];
	}
}
