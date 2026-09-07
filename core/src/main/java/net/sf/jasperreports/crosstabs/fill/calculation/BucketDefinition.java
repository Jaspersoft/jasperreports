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

import java.util.Comparator;

import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.ReverseComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.crosstabs.fill.BucketOrderer;
import net.sf.jasperreports.crosstabs.fill.calculation.BucketValueOrderDecorator.OrderPosition;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.analytics.dataset.BucketOrder;

/**
 * Bucket definition.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BucketDefinition
{
	
	private static final Log log = LogFactory.getLog(BucketDefinition.class);
	public static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_ORDER_TYPE = "crosstabs.calculation.unsupported.order.type";
	
	/**
	 * Value type used for non-null values.
	 */
	protected static final byte VALUE_TYPE_VALUE = 0;
	
	/**
	 * Value type used for null bucket values.
	 */
	protected static final byte VALUE_TYPE_NULL = 1;
	
	/**
	 * Value type used for total buckets.
	 */
	protected static final byte VALUE_TYPE_TOTAL = 2;

	/**
	 * The total value for this bucket.
	 */
	protected final Bucket VALUE_TOTAL = new Bucket(VALUE_TYPE_TOTAL);
	
	/**
	 * The null bucket.
	 */
	protected final Bucket VALUE_NULL = new Bucket(VALUE_TYPE_NULL);
	
	protected final Comparator<Object> bucketValueComparator;

	protected final BucketOrderer orderer;
	//FIXME totalPosition and mergeHeaderCells only apply to crosstabs
	private final CrosstabTotalPositionEnum totalPosition;
	private boolean mergeHeaderCells;

	private final BucketOrder order;
	
	private boolean computeTotal;
	
	/**
	 * Creates a bucket.
	 * 
	 * @param valueClass the class of the bucket values
	 * @param orderer bucket entries orderer
	 * @param comparator the comparator to use for bucket sorting
	 * @param order the order type, {@link BucketOrder#ASCENDING}, {@link BucketOrder#DESCENDING} or {@link BucketOrder#NONE}
	 * @param totalPosition the position of the total bucket
	 * @throws JRException
	 */
	public BucketDefinition(Class<?> valueClass, 
			BucketOrderer orderer, Comparator<Object> comparator, BucketOrder order, 
			CrosstabTotalPositionEnum totalPosition) throws JRException
	{
		this.orderer = orderer;
		this.order = order;
		
		if (orderer == null)
		{
			// we don't have a bucket orderer
			if (order == BucketOrder.NONE)
			{
				// no ordering, values are inserted in the order in which they come
				this.bucketValueComparator = null;
			}
			else
			{
				// the buckets are ordered using the bucket values
				// if there's no comparator, we're assuming that the values are Comparable
				this.bucketValueComparator = createOrderComparator(comparator, order);
			}
		}
		else
		{
			// we have an order by expression
			// we only need an internal ordering for bucket values
			if (Comparable.class.isAssignableFrom(valueClass))
			{
				// using natural order
				this.bucketValueComparator = ComparableComparator.INSTANCE;
			}
			else
			{
				// using an arbitrary rank comparator
				// TODO lucianc couldn't we just set here bucketValueComparator to null?
				if (log.isDebugEnabled())
				{
					log.debug("Using arbitrary rank comparator for bucket");
				}
				
				this.bucketValueComparator = new ArbitraryRankComparator();
			}
		}
		
		this.totalPosition = totalPosition;
		computeTotal = totalPosition != CrosstabTotalPositionEnum.NONE || orderer != null;
	}

	
	public static Comparator<Object> createOrderComparator(Comparator<Object> comparator, BucketOrder order)
	{
		Comparator<Object> orderComparator;
		switch (order)
		{
			case DESCENDING:
			{
				if (comparator == null)
				{
					orderComparator = new ReverseComparator<>();
				}
				else
				{
					orderComparator = new ReverseComparator<>(comparator);
				}
				break;
			}
			case ASCENDING:				
			{
				if (comparator == null)
				{
					orderComparator = ComparableComparator.INSTANCE;
				}
				else
				{
					orderComparator = comparator;
				}
				break;
			}
			case NONE:
			default:
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNSUPPORTED_ORDER_TYPE,
						new Object[]{order});
		}
		return orderComparator;
	}
	
	public boolean isSorted()
	{
		return bucketValueComparator != null;
	}
	
	/**
	 * Whether this bucket needs total calculation.
	 * 
	 * @return this bucket needs total calculation
	 */
	public boolean computeTotal()
	{
		return computeTotal;
	}

	
	/**
	 * Instructs that the bucket will need total bucket calculation.
	 * 
	 * @see #computeTotal()
	 */
	public void setComputeTotal()
	{
		computeTotal = true;
	}

	
	/**
	 * Returns the total bucket position.
	 * 
	 * @return the total bucket position
	 */
	public CrosstabTotalPositionEnum getTotalPosition()
	{
		return totalPosition;
	}
	
	
	public BucketOrderer getOrderer()
	{
		return orderer;
	}
	
	public BucketOrder getOrder()
	{
		return order;
	}

	public boolean isMergeHeaderCells()
	{
		return mergeHeaderCells;
	}

	public void setMergeHeaderCells(boolean mergeHeaderCells)
	{
		this.mergeHeaderCells = mergeHeaderCells;
	}


	/**
	 * Creates a {@link Bucket BucketValue} object for a given value.
	 * 
	 * @param value the value
	 * @return the corresponding {@link Bucket BucketValue} object
	 */
	public Bucket create(Object value)
	{
		if (value == null)
		{
			return VALUE_NULL;
		}
		
		if (value instanceof BucketValueOrderDecorator)
		{
			// create only when orderPosition != normal?
			return new OrderDecoratorBucket((BucketValueOrderDecorator<?>) value);
		}
		
		return new Bucket(value);
	}
	
	
	/**
	 * Bucket value class.
	 * 
	 * @author Lucian Chirita (lucianc@users.sourceforge.net)
	 */
	public class Bucket implements Comparable<Object>
	{
		private final Object value;
		private final byte type;
		
		
		/**
		 * Creates a bucket for a value type.
		 * 
		 * @param type the value type
		 */
		protected Bucket(byte type)
		{
			this.value = null;
			this.type = type;
		}
		
		
		/**
		 * Creates a bucket for a value.
		 * 
		 * @param value the value
		 */
		protected Bucket(Object value)
		{
			this.value = value;
			this.type = VALUE_TYPE_VALUE;
		}
		
		public Bucket(Object value, byte type)
		{
			this.value = value;
			this.type = type;
		}
		
		
		/**
		 * Returns the bucket value.
		 * 
		 * @return the bucket value
		 */
		public Object getValue()
		{
			return value;
		}
		
		@Override
		public boolean equals (Object o)
		{
			if (o == null || !(o instanceof Bucket))
			{
				return false;
			}
			
			if (o == this)
			{
				return true;
			}
			
			Bucket v = (Bucket) o;

			if (type != VALUE_TYPE_VALUE)
			{
				return type == v.type;
			}
			
			return v.type == VALUE_TYPE_VALUE && value.equals(v.value);
		}
		
		@Override
		public int hashCode()
		{
			int hash = type;
			
			if (type == VALUE_TYPE_VALUE)
			{
				hash = 37*hash + value.hashCode();
			}
			
			return hash;
		}
		
		@Override
		public String toString()
		{
			switch (type)
			{
				case VALUE_TYPE_NULL:
					return "NULL";
				case VALUE_TYPE_TOTAL:
					return "TOTAL";
				case VALUE_TYPE_VALUE:
				default:
					return String.valueOf(value);
			}
		}

		@Override
		public int compareTo(Object o)
		{
			Bucket val = (Bucket) o;
			if (type != val.type)
			{
				return type - val.type;
			}
			
			if (type != VALUE_TYPE_VALUE)
			{
				return 0;
			}

			OrderPosition orderPosition = getOrderPosition();
			OrderPosition otherOrderPosition = val.getOrderPosition();
			if (orderPosition != otherOrderPosition)
			{
				return orderPosition.comparePosition(otherOrderPosition);
			}
			
			return bucketValueComparator.compare(value, val.value);
		}
		
		/**
		 * Decides whether this is a total bucket.
		 * 
		 * @return whether this is a total bucket
		 */
		public boolean isTotal()
		{
			return type == VALUE_TYPE_TOTAL;
		}
		
		public OrderPosition getOrderPosition()
		{
			return OrderPosition.NORMAL;
		}
	}
	
	public class OrderDecoratorBucket extends Bucket
	{
		private OrderPosition orderPosition;

		protected OrderDecoratorBucket(BucketValueOrderDecorator<?> value)
		{
			super(value.getValue());
			
			orderPosition = value.getOrderPosition();
		}

		@Override
		public OrderPosition getOrderPosition()
		{
			return orderPosition;
		}
	}
}
