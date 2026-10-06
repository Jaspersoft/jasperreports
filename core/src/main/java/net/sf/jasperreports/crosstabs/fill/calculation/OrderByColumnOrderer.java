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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.crosstabs.fill.BucketOrderer;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketDefinition.Bucket;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketingService.BucketMap;
import net.sf.jasperreports.crosstabs.fill.calculation.MeasureDefinition.MeasureValue;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.type.SortOrderEnum;
import net.sf.jasperreports.engine.util.JRValueStringUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class OrderByColumnOrderer implements BucketOrderer
{
	
	private static final Log log = LogFactory.getLog(OrderByColumnOrderer.class);
	
	private final OrderByColumnInfo orderInfo;
	private BucketingData bucketingData;
	private List<Bucket> bucketValues;

	public OrderByColumnOrderer(OrderByColumnInfo orderInfo)
	{
		this.orderInfo = orderInfo;
	}

	@Override
	public void init(BucketingData bucketingData)
	{
		this.bucketingData = bucketingData;
		
		initBucketValues();
	}

	private void initBucketValues()
	{
		List<ColumnValueInfo> columnValues = orderInfo.getColumnValues();
		bucketValues = new ArrayList<>(columnValues.size());
		
		// TODO lucianc handle cases when the values no longer match the groups
		for (ListIterator<ColumnValueInfo> it = columnValues.listIterator(); it.hasNext();)
		{
			Bucket bucketValue;
			ColumnValueInfo columnValue = it.next();
			if (columnValue.isTotal())
			{
				bucketValue = bucketingData.getColumnTotalBucket(it.previousIndex());
			}
			else
			{
				Object value;
				if (columnValue.getValue() == null)
				{
					value = null;
				}
				else
				{
					value = JRValueStringUtils.deserialize(columnValue.getValueType(), columnValue.getValue());
				}
				
				// TODO lucianc check against column group value type?
				bucketValue = bucketingData.getColumnBucket(it.previousIndex(), value);
			}
			
			bucketValues.add(bucketValue);
		}
	}

	@Override
	public Object getOrderValue(BucketMap bucketMap, Bucket bucketValue) throws JRException
	{
		MeasureValue[] measureValues = bucketingData.getMeasureValues(bucketMap, bucketValue, bucketValues);
		return measureValues[orderInfo.getMeasureIndex()].getValue();
	}

	@Override
	public int compareOrderValues(Object orderValue, Object orderValue2)
	{
		int order;
		if (orderValue == null)
		{
			if (orderValue2 == null)
			{
				order = 0;
			}
			else
			{
				// by convention nulls come before
				order = -1;
			}
		}
		else
		{
			if (orderValue2 == null)
			{
				order = 1;
			}
			else
			{
				// assuming natural ordering
				order = ((Comparable) orderValue).compareTo(orderValue2);
			}
		}
		return orderInfo.getOrder() == SortOrderEnum.DESCENDING ? -order : order;
	}

	public List<Bucket> getBucketValues()
	{
		return bucketValues;
	}

}
