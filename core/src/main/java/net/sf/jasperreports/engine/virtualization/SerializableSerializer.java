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
package net.sf.jasperreports.engine.virtualization;

import java.io.IOException;
import java.lang.reflect.Constructor;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SerializableSerializer<T extends VirtualizationSerializable> implements ObjectSerializer<T>
{
	private static final String EXCEPTION_MESSAGE_KEY_CLASS_INSTANCE_ERROR = "engine.virtualization.serializable.serializer.class.instance.error";
	private static final String EXCEPTION_MESSAGE_KEY_INITIALIZATION_FAILED = "engine.virtualization.serializable.serializer.initialization.failed";
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_VALUE_TYPE = "engine.virtualization.serializable.serializer.unexpected.value.type";
	
	private static final Class<?>[] NO_ARGS_TYPES = new Class<?>[0];
	private static final Object[] NO_ARGS = new Object[0];
	
	private final int typeValue;
	private final Class<T> type;
	
	private final Constructor<T> constructor;
	
	public SerializableSerializer(int typeValue, Class<T> type)
	{
		this.typeValue = typeValue;
		this.type = type;
		
		this.constructor = initConstructor();
	}

	private Constructor<T> initConstructor()
	{
		try
		{
			return type.getConstructor(NO_ARGS_TYPES);
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INITIALIZATION_FAILED,
					new Object[]{type.getName()},
					e);
		}
	}

	@Override
	public int typeValue()
	{
		return typeValue;
	}

	@Override
	public ReferenceType defaultReferenceType()
	{
		return ReferenceType.IDENTITY;
	}

	@Override
	public boolean defaultStoreReference()
	{
		// assuming that instances are not shared, can be parametrized if required
		return false;
	}

	@Override
	public void write(T value, VirtualizationOutput out)
			throws IOException
	{
		if (!type.isInstance(value))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNEXPECTED_VALUE_TYPE,
					new Object[]{value,  value.getClass().getName(), type.getName()});
		}
		
		value.writeVirtualized(out);
	}

	@Override
	public T read(VirtualizationInput in) throws IOException
	{
		T object;
		try
		{
			object = constructor.newInstance(NO_ARGS);
		} 
		catch (Exception e)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_CLASS_INSTANCE_ERROR,
					new Object[]{type.getName()},
					e);
		} 
		
		object.readVirtualized(in);
		return object;
	}

}
