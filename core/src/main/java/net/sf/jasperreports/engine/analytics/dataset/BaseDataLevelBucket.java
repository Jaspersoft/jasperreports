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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseDataLevelBucket implements DataLevelBucket, Serializable
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String EXCEPTION_MESSAGE_KEY_BUCKET_LOAD_ERROR = "engine.analytics.dataset.bucket.load.error";
	
	protected String valueClassName;
	protected String valueClassRealName;
	protected Class<?> valueClass;
	
	protected BucketOrder order = BucketOrder.ASCENDING;
	protected JRExpression expression;
	protected JRExpression labelExpression;
	protected JRExpression comparatorExpression;
	
	protected List<DataLevelBucketProperty> bucketProperties;

	protected BaseDataLevelBucket()
	{
		this.bucketProperties = new ArrayList<>();
	}
	
	public BaseDataLevelBucket(DataLevelBucket bucket, JRBaseObjectFactory factory)
	{
		factory.put(bucket, this);
		
		this.valueClassName = bucket.getValueClassName();
		this.order = bucket.getOrder();
		this.expression = factory.getExpression(bucket.getExpression());
		this.labelExpression = factory.getExpression(bucket.getLabelExpression());
		this.comparatorExpression = factory.getExpression(bucket.getComparatorExpression());
		
		List<DataLevelBucketProperty> properties = bucket.getBucketProperties();
		this.bucketProperties = new ArrayList<>(properties.size());
		for (DataLevelBucketProperty property : properties)
		{
			this.bucketProperties.add(factory.getDataLevelBucketProperty(property));
		}
	}

	@Override
	public String getValueClassName()
	{
		return valueClassName;
	}

	@Override
	public BucketOrder getOrder()
	{
		return order;
	}

	@Override
	public JRExpression getExpression()
	{
		return expression;
	}

	@Override
	public JRExpression getLabelExpression()
	{
		return labelExpression;
	}

	@Override
	public JRExpression getComparatorExpression()
	{
		return comparatorExpression;
	}
	
	@Override
	public Class<?> getValueClass()
	{
		if (valueClass == null)
		{
			String className = getValueClassRealName();
			if (className != null)
			{
				try
				{
					valueClass = JRClassLoader.resolveClassForName(className);
				}
				catch (ClassNotFoundException e)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_BUCKET_LOAD_ERROR,
							(Object[])null,
							e);
				}
			}
		}
		
		return valueClass;
	}

	private String getValueClassRealName()
	{
		if (valueClassRealName == null)
		{
			valueClassRealName = JRClassLoader.getClassRealName(valueClassName);
		}
		
		return valueClassRealName;
	}

	@Override
	public List<DataLevelBucketProperty> getBucketProperties()
	{
		// TODO lucianc unmodifiable?
		return bucketProperties;
	}

	@Override
	public Object clone()
	{
		BaseDataLevelBucket clone = null;
		try
		{
			clone = (BaseDataLevelBucket) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.expression = JRCloneUtils.nullSafeClone(expression);
		clone.labelExpression = JRCloneUtils.nullSafeClone(labelExpression);
		clone.comparatorExpression = JRCloneUtils.nullSafeClone(comparatorExpression);
		clone.bucketProperties = JRCloneUtils.cloneList(bucketProperties);
		return clone;
	}
}
