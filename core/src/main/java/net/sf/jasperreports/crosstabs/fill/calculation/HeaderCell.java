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

import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition.MeasureValue;

/**
 * Crosstab header cell produced by the crosstab bucketing engine.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class HeaderCell
{
	private final BucketDefinition.Bucket[] bucketValues;
	private final int levelSpan;
	private final int depthSpan;
	private final boolean isTotal;
	private final MeasureValue[][] totals;


	/**
	 * Creates a crosstab header cell.
	 * 
	 * @param bucketValues the bucket values for the cell
	 * @param levelSpan the span across cells on the same level (bucket)
	 * @param depthSpan the span across cells on subsequent levels (buckets) 
	 */
	public HeaderCell(
			BucketDefinition.Bucket[] bucketValues, 
			int levelSpan,
			int depthSpan,
			MeasureValue[][] totals)
	{
		this.bucketValues = bucketValues;
		this.levelSpan = levelSpan;
		this.depthSpan = depthSpan;
		this.totals = totals;
		
		boolean foundTotal = false;
		for (BucketDefinition.Bucket bucketValue : bucketValues)
		{
			if (bucketValue != null && bucketValue.isTotal())
			{
				foundTotal = true;
				break;
			}
		}
		
		isTotal = foundTotal;
	}


	/**
	 * Returns the bucket values for this cell.
	 * 
	 * @return the bucket values for this cell
	 */
	public BucketDefinition.Bucket[] getBucketValues()
	{
		return bucketValues;
	}


	/**
	 * Returns the span across cells on the same level (bucket).
	 * <p>
	 * This is used for headers of buckets having sub-buckets.
	 * 
	 * @return the span across cells on the same level (bucket)
	 */
	public int getLevelSpan()
	{
		return levelSpan;
	}


	/**
	 * Returns the span across cells on subsequent levels (buckets).
	 * <p>
	 * This is used for total headers.
	 * 
	 * @return the span across cells on subsequent levels (buckets)
	 */
	public int getDepthSpan()
	{
		return depthSpan;
	}
	
	
	/**
	 * Returns whether this header is a total header.
	 * 
	 * @return whether this header is a total header
	 */
	public boolean isTotal()
	{
		return isTotal;
	}
	
	public MeasureValue[][] getTotals()
	{
		return totals;
	}
	
	public static HeaderCell createLevelSpanCopy(HeaderCell cell, int newLevelSpan)
	{
		return new HeaderCell(cell.bucketValues, newLevelSpan, cell.getDepthSpan(), cell.totals);
	}
}
