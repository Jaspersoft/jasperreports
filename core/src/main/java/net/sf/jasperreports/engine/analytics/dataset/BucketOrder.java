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
package net.sf.jasperreports.engine.analytics.dataset;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.NamedEnum;
import net.sf.jasperreports.engine.type.SortOrderEnum;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public enum BucketOrder implements NamedEnum
{
	/**
	 * Ascending order constant.
	 */ 
	ASCENDING("Ascending"),
	
	/**
	 * Descending order constant.
	 */ 
	DESCENDING("Descending"),
	
	/**
	 * No sorting.
	 */ 
	NONE("None");

	private final transient String name;
	public static final String EXCEPTION_MESSAGE_KEY_CANNOT_TRANSLATE_NONE = "engine.analytics.dataset.cannot.translate.none";
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_ORDER_ENUM = "engine.analytics.dataset.unknown.order.enum";

	private BucketOrder(String name)
	{
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public static SortOrderEnum toSortOrderEnum(BucketOrder order)
	{
		SortOrderEnum sortOrder;
		if (order == null)
		{
			sortOrder = null;
		}
		else
		{
			switch (order)
			{
			case ASCENDING:
				sortOrder = SortOrderEnum.ASCENDING;
				break;
			case DESCENDING:
				sortOrder = SortOrderEnum.DESCENDING;
				break;
			case NONE:
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_CANNOT_TRANSLATE_NONE,
						(Object[])null);
			default:
				// should not happen
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_ORDER_ENUM,
						new Object[]{order});
			}
		}
		return sortOrder;
	}
	
	public static BucketOrder fromSortOrderEnum(SortOrderEnum orderValue)
	{
		BucketOrder order;
		if (orderValue == null)
		{
			order = null;
		}
		else
		{
			switch (orderValue)
			{
			case ASCENDING:
				order = BucketOrder.ASCENDING;
				break;
			case DESCENDING:
				order = BucketOrder.DESCENDING;
				break;
			default:
				// should not happen
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_ORDER_ENUM,
						new Object[]{orderValue});
			}
		}
		return order;
	}
	
	/**
	 *
	 */
	public static BucketOrder getValueOrDefault(BucketOrder value)
	{
		return value == null ? ASCENDING : value;
	}
	
	@Override
	public BucketOrder getDefault()
	{
		return ASCENDING;
	}
}
