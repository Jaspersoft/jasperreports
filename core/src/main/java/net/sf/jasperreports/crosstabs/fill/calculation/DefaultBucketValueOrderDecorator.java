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

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DefaultBucketValueOrderDecorator<T extends Comparable<T>> 
		implements BucketValueOrderDecorator<Comparable<T>>, Comparable<DefaultBucketValueOrderDecorator<T>>
{

	public static final <T extends Comparable<T>> DefaultBucketValueOrderDecorator<T> wrap(T value, OrderPosition position)
	{
		// null values are left untouched
		return value == null ? null : new DefaultBucketValueOrderDecorator<>(value, 
				position == null ? OrderPosition.NORMAL : position);
	}
	
	private T value;
	private OrderPosition position;

	protected DefaultBucketValueOrderDecorator()
	{
	}
	
	protected DefaultBucketValueOrderDecorator(T value, OrderPosition position)
	{
		this.value = value;
		this.position = position;
	}

	@Override
	public OrderPosition getOrderPosition()
	{
		return position;
	}

	@Override
	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value.toString();
	}

	@Override
	public int compareTo(DefaultBucketValueOrderDecorator<T> o)
	{
		// this not actually used, the ordering is explicitly performed by the bucketing service
		return value.compareTo(o.value);
	}

}
