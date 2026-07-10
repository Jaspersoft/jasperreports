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
package net.sf.jasperreports.engine.fill;

import java.io.IOException;
import java.io.InputStream;

import net.sf.jasperreports.engine.util.DeserializationClassFilter;
import net.sf.jasperreports.engine.util.FilteredObjectInputStream;

/**
 * <code>java.io.ObjectInputStream</code> subclass used for deserializing report
 * data on virtualization.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class VirtualizationObjectInputStream extends FilteredObjectInputStream
{
	private final JRVirtualizationContext virtualizationContext;

	public VirtualizationObjectInputStream(InputStream in, 
			JRVirtualizationContext virtualizationContext) throws IOException
	{
		super(virtualizationContext.getJasperReportsContext(), in,
				new DeserializationClassFilter(virtualizationContext.getJasperReportsContext()));
		
		this.virtualizationContext = virtualizationContext;
		enableResolveObject(true);
	}

	@Override
	protected Object resolveObject(Object obj) throws IOException
	{
		return virtualizationContext.resolveSerializedObject(obj);
	}
}
