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
package net.sf.jasperreports.crosstabs.design;

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.crosstabs.base.JRBaseCrosstabBucket;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.analytics.dataset.BucketOrder;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Implementation of {@link net.sf.jasperreports.crosstabs.JRCrosstabBucket crosstab group bucket}
 * to be used for report designing.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignCrosstabBucket extends JRBaseCrosstabBucket implements JRChangeEventsSupport
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_ORDER_BY_EXPRESSION = "orderByExpression";

	public static final String PROPERTY_COMPARATOR_EXPRESSION = "comparatorExpression";

	public static final String PROPERTY_EXPRESSION = "expression";

	public static final String PROPERTY_ORDER = "order";

	public static final String PROPERTY_VALUE_CLASS = "valueClassName";

	
	/**
	 * Creates a crosstab group bucket.
	 */
	public JRDesignCrosstabBucket()
	{
		super();
	}

	
	/**
	 * Sets the expression that provides order by values for group buckets.
	 * 
	 * @param orderByExpression the expression that provides order by values
	 * for group buckets
	 * @see #getOrderByExpression()
	 */
	public void setOrderByExpression(JRExpression orderByExpression)
	{
		Object old = this.orderByExpression;
		this.orderByExpression = orderByExpression;
		getEventSupport().firePropertyChange(PROPERTY_ORDER_BY_EXPRESSION, 
				old, this.orderByExpression);
	}

	
	/**
	 * Sets the comparator expression.
	 * <p>
	 * The expressions's type should be compatible with {@link java.util.Comparator java.util.Comparator}.
	 * 
	 * @param comparatorExpression the comparator expression
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabBucket#getComparatorExpression()
	 */
	public void setComparatorExpression(JRExpression comparatorExpression)
	{
		Object old = this.comparatorExpression;
		this.comparatorExpression = comparatorExpression;
		getEventSupport().firePropertyChange(PROPERTY_COMPARATOR_EXPRESSION, old, this.comparatorExpression);
	}

	
	/**
	 * Sets the grouping expression.
	 * 
	 * @param expression the grouping expression
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabBucket#getExpression()
	 */
	public void setExpression(JRDesignExpression expression)
	{
		Object old = this.expression;
		this.expression = expression;
		getEventSupport().firePropertyChange(PROPERTY_EXPRESSION, old, this.expression);
	}

	
	/**
	 * Sets the sorting type.
	 * 
	 * @param bucketOrder one of
	 * <ul>
	 * 	<li>{@link BucketOrder#ASCENDING BucketOrder.ASCENDING}</li>
	 * 	<li>{@link BucketOrder#DESCENDING BucketOrder.DESCENDING}</li>
	 * 	<li>{@link BucketOrder#NONE BucketOrder.NONE}</li>
	 * </ul>
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabBucket#getOrder()
	 */
	public void setOrder(BucketOrder bucketOrder)
	{
		Object old = this.bucketOrder;
		this.bucketOrder = bucketOrder;
		getEventSupport().firePropertyChange(PROPERTY_ORDER, old, this.bucketOrder);
	}
	

	/**
	 * Sets the bucket value class name.
	 * 
	 * @param valueClassName the bucket value class name
	 * @see net.sf.jasperreports.crosstabs.JRCrosstabBucket#getValueClassName()
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_class)
	public void setValueClassName(String valueClassName)
	{
		String old = this.valueClassName;
		
		this.valueClassName = valueClassName;
		this.valueClass = null;
		this.valueClassRealName = null;
		
		getEventSupport().firePropertyChange(PROPERTY_VALUE_CLASS, old,
				this.valueClassName);
	}
	
	@Override
	public Object clone()
	{
		JRDesignCrosstabBucket clone = (JRDesignCrosstabBucket)super.clone();
		clone.eventSupport = null;
		return clone;
	}
	
	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}
}
