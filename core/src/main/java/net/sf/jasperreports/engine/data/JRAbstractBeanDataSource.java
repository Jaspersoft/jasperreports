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
package net.sf.jasperreports.engine.data;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils2.NestedNullException;
import org.apache.commons.beanutils2.PropertyUtils;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.query.EjbqlConstants;
import net.sf.jasperreports.engine.query.HibernateConstants;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRAbstractBeanDataSource implements JRRewindableDataSource
{
	
	public static final String EXCEPTION_MESSAGE_KEY_BEAN_FIELD_VALUE_NOT_RETRIEVED = "data.bean.field.value.not.retrieved";

	/**
	 * Property specifying the JavaBean property name for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {HibernateConstants.QUERY_EXECUTER_NAME_HQL, EjbqlConstants.QUERY_EXECUTER_NAME_EJBQL},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_JAVABEAN_FIELD_PROPERTY = JRPropertiesUtil.PROPERTY_PREFIX + "javabean.field.property";
	
	/**
	 * Field mapping that produces the current bean.
	 * <p/>
	 * If the field name/description matches this constant (the case is important),
	 * the data source will return the current bean as the field value.
	 */
	public static final String CURRENT_BEAN_MAPPING = "_THIS";

	/**
	 *
	 */
	protected PropertyNameProvider propertyNameProvider;

	public static class DefaultPropertyNameProvider implements PropertyNameProvider
	{
		private boolean isUseFieldDescription;
		private Map<String, String> fieldPropertyNames = new HashMap<>();
		
		public DefaultPropertyNameProvider(boolean isUseFieldDescription)
		{
			this.isUseFieldDescription = isUseFieldDescription;
		}
		
		@Override
		public String getPropertyName(JRField field) 
		{
			String fieldPropertyName = null;

			if (fieldPropertyNames.containsKey(field.getName()))
			{
				fieldPropertyName = fieldPropertyNames.get(field.getName());
			}
			else
			{
				if (field.hasProperties())
				{
					fieldPropertyName = field.getPropertiesMap().getProperty(PROPERTY_JAVABEAN_FIELD_PROPERTY);
				}
				
				if (fieldPropertyName == null)
				{
					if (isUseFieldDescription && field.getDescription() != null)
					{
						fieldPropertyName = field.getDescription();
					}
					else
					{
						fieldPropertyName = field.getName();
					}
				}
				
				fieldPropertyNames.put(field.getName(), fieldPropertyName);
			}
			
			return fieldPropertyName;
		}
	}
		
	/**
	 *
	 */
	public JRAbstractBeanDataSource(boolean isUseFieldDescription)
	{
		propertyNameProvider = new DefaultPropertyNameProvider(isUseFieldDescription);
	}
	

	/**
	 *
	 */
	public interface PropertyNameProvider
	{
		public String getPropertyName(JRField field);
	}

	protected Object getFieldValue(Object bean, JRField field) throws JRException
	{
		return getBeanProperty(bean, getPropertyName(field));
	}
	
	public static Object getBeanProperty(Object bean, String propertyName) throws JRException
	{
		Object value = null;
		
		if (isCurrentBeanMapping(propertyName))
		{
			value = bean;
		}
		else if (bean != null)
		{
			try
			{
				value = PropertyUtils.getProperty(bean, propertyName);
			}
			catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_BEAN_FIELD_VALUE_NOT_RETRIEVED,
						new Object[]{propertyName}, 
						e);
			}
			catch (NestedNullException e)
			{
				// deliberately to be ignored
			}
		}

		return value;
	}

	protected static boolean isCurrentBeanMapping(String propertyName)
	{
		return CURRENT_BEAN_MAPPING.equals(propertyName);
	}

	protected String getPropertyName(JRField field)
	{
		return propertyNameProvider.getPropertyName(field);
	}
}
