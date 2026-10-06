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
package net.sf.jasperreports.virtualization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.engine.util.LocalVirtualizationSerializer;
import net.sf.jasperreports.engine.virtualization.LocalVirtualizationInput;
import net.sf.jasperreports.engine.virtualization.LocalVirtualizationOutput;
import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseSerializationTests
{

	protected <T> T passThroughSerialization(T object)
	{
		JRVirtualizationContext virtualizationContext = createVirtualizationContext();
		return passThroughSerialization(virtualizationContext, object);
	}
	
	protected <T> T passThroughSerialization(JRVirtualizationContext virtualizationContext, T object)
	{
		assert object != null;
		
		Object read = passThroughSerializationNoChecks(virtualizationContext, object);
		assert read != null;
		assert read.getClass().equals(object.getClass());
		
		@SuppressWarnings("unchecked")
		T castRead = (T) read;
		return castRead;
	}

	protected Object passThroughSerializationNoChecks(Object object)
	{
		JRVirtualizationContext virtualizationContext = createVirtualizationContext();
		return passThroughSerializationNoChecks(virtualizationContext, object);
	}

	protected JRVirtualizationContext createVirtualizationContext()
	{
		return new JRVirtualizationContext(DefaultJasperReportsContext.getInstance());
	}

	protected Object passThroughSerializationNoChecks(JRVirtualizationContext virtualizationContext, Object object)
	{
		try
		{
			SerializationJob job = new SerializationJob(virtualizationContext);
			job.out().writeJRObject(object);
			
			Object read = job.in().readJRObject();
			return read;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected SerializationJob serializationJob()
	{
		JRVirtualizationContext virtualizationContext = createVirtualizationContext();
		return new SerializationJob(virtualizationContext);
	}

	protected SerializationJob serializationJob(JRVirtualizationContext virtualizationContext)
	{
		return new SerializationJob(virtualizationContext);
	}
	
	protected static class SerializationJob
	{
		protected final JRVirtualizationContext virtualizationContext;
		protected final LocalVirtualizationSerializer serializer;
		
		private ByteArrayOutputStream bout;
		private VirtualizationOutput out;
		private VirtualizationInput in;
		
		public SerializationJob(JRVirtualizationContext virtualizationContext)
		{
			this.virtualizationContext = virtualizationContext;
			this.serializer = new LocalVirtualizationSerializer();
		}
		
		public VirtualizationOutput out() throws IOException
		{
			if (out == null)
			{
				bout = new ByteArrayOutputStream();
				out = new LocalVirtualizationOutput(bout, serializer, virtualizationContext);
			}
			
			return out;
		}
		
		public VirtualizationInput in() throws IOException
		{
			if (in == null)
			{
				out.close();
				
				ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
				in = new LocalVirtualizationInput(bin, serializer, virtualizationContext);
			}
			
			return in;
		}
	}
	
}
