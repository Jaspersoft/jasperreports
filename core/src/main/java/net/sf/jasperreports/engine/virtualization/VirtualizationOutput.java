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
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class VirtualizationOutput extends ObjectOutputStream
{
	private static final String EXCEPTION_MESSAGE_KEY_UNSUPPORTED_REFERENCE_TYPE = "engine.virtualization.output.unsupported.reference.type";
	
	private final JRVirtualizationContext virtualizationContext;
	
	private final SerializerRegistry serializerRegistry = DefaultSerializerRegistry.getInstance();
	
	@SuppressWarnings("unchecked")
	private final Map<Object, Integer>[] writtenObjects = new Map[SerializationConstants.OBJECT_TYPE_COUNT];

	public VirtualizationOutput(OutputStream out,
			JRVirtualizationContext virtualizationContext) throws IOException
	{
		super(out);
		
		this.virtualizationContext = virtualizationContext;
	}

	public JRVirtualizationContext getVirtualizationContext()
	{
		return virtualizationContext;
	}
	
	public void writeIntCompressed(int value) throws IOException
	{
		SerializationUtils.writeIntCompressed(this, value);
	}
	
	// didn't find another name
	public void writeJRObject(Object o) throws IOException
	{
		writeJRObject(o, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public void writeJRObject(Object o, Boolean lookupReference, Boolean storeReference) throws IOException
	{
		if (o == null)
		{
			writeByte(SerializationConstants.OBJECT_NULL);
			return;
		}
		
		@SuppressWarnings("rawtypes")
		ObjectSerializer serializer = serializerRegistry.getSerializer(o.getClass());
		if (serializer == null)
		{
			writeByte(SerializationConstants.OBJECT_ARBITRARY);
			writeObject(o);
			return;
		}
		
		int typeValue = serializer.typeValue();
		boolean typeStoreReference = serializer.defaultStoreReference();
		if (lookupReference == null ? typeStoreReference : lookupReference)
		{
			Integer objectIndex = findReference(typeValue, o);
			if (objectIndex != null)
			{
				writeByte(SerializationConstants.OBJECT_REF_MASK | typeValue);
				writeIntCompressed(objectIndex);
				return;
			}
		}
		
		if (storeReference == null ? typeStoreReference : storeReference)
		{
			ReferenceType referenceType = serializer.defaultReferenceType();
			putReference(typeValue, referenceType, o);
		}
		
		writeByte(typeValue);
		serializer.write(o, this);
	}
	
	protected Integer findReference(int typeValue, Object value)
	{
		Map<Object, Integer> objectsMap = writtenObjects[typeValue - SerializationConstants.OBJECT_TYPE_OFFSET];
		return objectsMap == null ? null : objectsMap.get(value);
	}
	
	protected void putReference(int typeValue, ReferenceType referenceType, Object value)
	{
		Map<Object, Integer> objectsMap = writtenObjects[typeValue - SerializationConstants.OBJECT_TYPE_OFFSET];
		if (objectsMap == null)
		{
			switch (referenceType)
			{
			case OBJECT:
				objectsMap = new HashMap<>();
				break;
			case IDENTITY:
				objectsMap = new IdentityHashMap<>();
				break;
			default:
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNSUPPORTED_REFERENCE_TYPE,
						new Object[]{referenceType});
			}
			
			writtenObjects[typeValue - SerializationConstants.OBJECT_TYPE_OFFSET] = objectsMap;
		}
		
		int objectIndex = objectsMap.size();
		objectsMap.put(value, objectIndex);
	}
}
