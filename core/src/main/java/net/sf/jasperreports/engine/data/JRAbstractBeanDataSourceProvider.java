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

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import net.sf.jasperreports.engine.JRDataSourceProvider;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignField;


/**
 * The base implementation for JRBeanXXXDataSource providers. It provides a common
 * implementation for bean properties introspection.
 * 
 * @author Peter Severin (peter_s@sourceforge.net, contact@jasperassistant.com)
 */
public abstract class JRAbstractBeanDataSourceProvider implements JRDataSourceProvider 
{
	public static final String EXCEPTION_MESSAGE_KEY_NULL_BEAN_CLASS = "data.bean.constructor.argument.cannot.be.null";

	/** The introspected bean class */
	private Class<?> beanClass;

	/**
	 * Creates the provider. Superclasses must pass a valid class that will be
	 * used to introspect the available bean fields.
	 * @param beanClass the bean class to be introspected.
	 */
	public JRAbstractBeanDataSourceProvider(Class<?> beanClass) {
		if (beanClass == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_NULL_BEAN_CLASS,
					new Object[]{"beanClass"});
		}

		this.beanClass = beanClass;
	}

	/**
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#supportsGetFieldsOperation()
	 */
	@Override
	public boolean supportsGetFieldsOperation() {
		return true;
	}
	
	/**
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#getFields(net.sf.jasperreports.engine.JasperReport)
	 */
	@Override
	public JRField[] getFields(JasperReport report) throws JRException {
		BeanInfo beanInfo = null;

		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			throw new JRException(e);
		}

		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		if (descriptors != null) 
		{
			ArrayList<JRField> fields = new ArrayList<>(descriptors.length);
			
			for (int i = 0; i < descriptors.length; i++) {
				PropertyDescriptor descriptor = descriptors[i];
				
				if (!(descriptor instanceof IndexedPropertyDescriptor) && descriptor.getReadMethod() != null) 
				{
					JRDesignField field = new JRDesignField();
					field.setValueClassName(normalizeClass(descriptor.getPropertyType()).getCanonicalName());
					field.setName(descriptor.getName());
					
					fields.add(field);
				}
			}
	
			return fields.toArray(new JRField[fields.size()]);
		}

		return new JRField[0];
	}

	/**
	 * Converts a primitive class to its object counterpart
	 */
	private static Class<?> normalizeClass(Class<?> clazz) {
		if (clazz.isPrimitive()) 
		{
			if (clazz == boolean.class)
			{
				return Boolean.class;
			}
			if (clazz == byte.class)
			{
				return Byte.class;
			}
			if (clazz == char.class)
			{
				return Character.class;
			}
			if (clazz == short.class)
			{
				return Short.class;
			}
			if (clazz == int.class)
			{
				return Integer.class;
			}
			if (clazz == long.class)
			{
				return Long.class;
			}
			if (clazz == float.class)
			{
				return Float.class;
			}
			if (clazz == double.class)
			{
				return Double.class;
			}
		}
		
		return clazz;
	}
}
