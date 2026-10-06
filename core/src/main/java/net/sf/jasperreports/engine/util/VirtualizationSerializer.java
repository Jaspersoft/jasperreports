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
package net.sf.jasperreports.engine.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jasperreports.engine.JRVirtualizable;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class VirtualizationSerializer
{
	
	public final void writeData(JRVirtualizable o, OutputStream out) throws IOException
	{
		Object virtualData = o.getVirtualData();
		JRVirtualizationContext context = o.getContext();
		writeData(virtualData, context, out);
	}

	public final void writeData(Object virtualData, JRVirtualizationContext context, OutputStream out) throws IOException
	{
		VirtualizationOutput oos = createOutput(context, out);
		oos.writeJRObject(virtualData);
		oos.flush();
	}

	protected abstract VirtualizationOutput createOutput(JRVirtualizationContext context, OutputStream out) 
			throws IOException;
	
	public final void readData(JRVirtualizable o, InputStream in) throws IOException
	{
		Object virtualData = readData(o.getContext(), in);
		o.setVirtualData(virtualData);
	}
	
	public final Object readData(JRVirtualizationContext context, InputStream in) throws IOException
	{
		VirtualizationInput ois = createInput(context, in);
		Object readObject = ois.readJRObject();
		return readObject;
	}

	protected abstract VirtualizationInput createInput(JRVirtualizationContext context, InputStream in) 
			throws IOException;

}
