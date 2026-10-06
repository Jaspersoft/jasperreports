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
package net.sf.jasperreports.crosstabs.fill.calculation;

import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition.MeasureValue;

/**
 * Crosstab cell produced by the crosstab bucketing engine.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CrosstabCell
{
	private final BucketDefinition.Bucket[] rowBucketValues;
	private final int rowTotalGroupIndex;
	private final BucketDefinition.Bucket[] columnBucketValues;
	private final int columnTotalGroupIndex;
	private final MeasureValue[] mesureValues;
	private final MeasureValue[][][] totals;
	
	
	/**
	 * Create a crosstab cell.
	 * 
	 * @param rowBucketValues the row bucket values corresponding to the cell
	 * @param columnBucketValues the column bucket values corresponding to the cell
	 * @param mesureValues the measure values
	 * @param totals totals corresponding to the cell
	 */
	public CrosstabCell(
			BucketDefinition.Bucket[] rowBucketValues, 
			BucketDefinition.Bucket[] columnBucketValues, 
			MeasureValue[] mesureValues,
			MeasureValue[][][] totals)
	{
		this.rowBucketValues = rowBucketValues;
		rowTotalGroupIndex = getTotalIndex(rowBucketValues);
		this.columnBucketValues = columnBucketValues;
		columnTotalGroupIndex = getTotalIndex(columnBucketValues);
		this.mesureValues = mesureValues;
		this.totals = totals;
	}

	
	private static int getTotalIndex(BucketDefinition.Bucket[] values)
	{
		int i = 0;
		while (i < values.length && !values[i].isTotal())
		{
			++i;
		}
		
		return i;
	}

	
	/**
	 * Returns the measure values for this cell.
	 * 
	 * @return the measure values for this cell
	 */
	public MeasureValue[] getMesureValues()
	{
		return mesureValues;
	}


	/**
	 * Returns the column bucket values corresponding to this cell.
	 * 
	 * @return the column bucket values corresponding to this cell
	 */
	public BucketDefinition.Bucket[] getColumnBucketValues()
	{
		return columnBucketValues;
	}


	/**
	 * Returns the row bucket values corresponding to this cell.
	 * 
	 * @return the row bucket values corresponding to this cell
	 */
	public BucketDefinition.Bucket[] getRowBucketValues()
	{
		return rowBucketValues;
	}


	/**
	 * Returns the index of the column total bucket this cell corresponds to.
	 * <p>
	 * If this cell corresponds to a column total bucket, this method returns the index of the
	 * bucket.  Otherwise it returns the number of column buckets.
	 * 
	 * @return the index of the column total bucket this cell corresponds to 
	 */
	public int getColumnTotalGroupIndex()
	{
		return columnTotalGroupIndex;
	}


	/**
	 * Returns the index of the row total bucket this cell corresponds to.
	 * <p>
	 * If this cell corresponds to a row total bucket, this method returns the index of the
	 * bucket.  Otherwise it returns the number of row buckets.
	 * 
	 * @return the index of the row total bucket this cell corresponds to 
	 */
	public int getRowTotalGroupIndex()
	{
		return rowTotalGroupIndex;
	}
	
	
	/**
	 * Returns measure totals corresponding to the cell.
	 * 
	 * @return measure totals corresponding to the cell
	 */
	public MeasureValue[][][] getTotals()
	{
		return totals;
	}
}
