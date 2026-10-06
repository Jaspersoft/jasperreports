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
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.engine.util.LocalVirtualizationSerializer;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class LocalVirtualizationOutput extends VirtualizationOutput
{

	private final LocalVirtualizationSerializer virtualizationSerializer;

	public LocalVirtualizationOutput(OutputStream out, LocalVirtualizationSerializer serializer,
			JRVirtualizationContext virtualizationContext) throws IOException
	{
		super(out, virtualizationContext);
		
		this.virtualizationSerializer = serializer;
	}

	@Override
	protected void annotateClass(Class<?> clazz) throws IOException
	{
		super.annotateClass(clazz);

		// TODO lucianc investigate if we still need this
		int loaderIdx = virtualizationSerializer.getClassloaderIdx(clazz);
		writeShort(loaderIdx);
	}

	@Override
	protected void writeClassDescriptor(ObjectStreamClass desc)
			throws IOException
	{
		Class<?> clazz = desc.forClass();
		if (clazz == null)
		{
			throw new RuntimeException();
		}
		
		int classIdx = virtualizationSerializer.getClassDescriptorIdx(clazz);
		writeIntCompressed(classIdx);
	}

}
