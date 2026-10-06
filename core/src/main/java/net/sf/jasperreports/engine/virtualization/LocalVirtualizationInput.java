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
package net.sf.jasperreports.engine.virtualization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.engine.util.LocalVirtualizationSerializer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class LocalVirtualizationInput extends VirtualizationInput
{
	
	private final LocalVirtualizationSerializer serializer;

	public LocalVirtualizationInput(InputStream in, LocalVirtualizationSerializer serializer,
			JRVirtualizationContext virtualizationContext) throws IOException
	{
		super(in, virtualizationContext);
		
		this.serializer = serializer;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
	{
		Class<?> clazz;
		try
		{
			clazz = super.resolveClass(desc);
			readShort();
		}
		catch (ClassNotFoundException e)
		{
			int loaderIdx = readShort();
			clazz = serializer.resolveClass(desc, loaderIdx);
			if (clazz == null)
			{
				throw e;
			}
		}

		return clazz;
	}

	@Override
	protected ObjectStreamClass readClassDescriptor() throws IOException,
			ClassNotFoundException
	{
		int classIdx = readIntCompressed();
		Class<?> clazz = serializer.getClassForDescriptorIdx(classIdx);
		ObjectStreamClass descriptor = ObjectStreamClass.lookupAny(clazz);
		return descriptor;
	}

}
