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
package net.sf.jasperreports.dataadapters;

import java.io.FilterInputStream;
import java.io.IOException;

import net.sf.jasperreports.engine.JRRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DataFileStream extends FilterInputStream
{
	
	private static final Log log = LogFactory.getLog(DataFileUtils.class);
	
	private final DataFileConnection connection;

	public DataFileStream(DataFileConnection connection)
	{
		// using FilterInputStream as decorator
		super(connection.getInputStream());
		
		this.connection = connection;
	}

	@Override
	public void close() throws IOException
	{
		boolean closed = false;
		try
		{
			super.close();
			closed = true;
		}
		finally
		{
			try
			{
				// attempt to close the connection no matter if the stream was closed successfully or not
				connection.dispose();
			}
			catch (RuntimeException e)
			{
				if (closed)
				{
					// if the stream closed successfully, propagate the connection exception
					throw new IOException(e);
				}
				else
				{
					log.warn("Failed to dispose connection for " + connection, e);
				}
			}
		}
	}

	public void dispose()
	{
		try
		{
			super.close();
		}
		catch (IOException e)
		{
			log.warn("Failed to dispose stream for " + connection, e);
		}
		
		try
		{
			connection.dispose();
		}
		catch (JRRuntimeException e)//catch RuntimeException?
		{
			log.warn("Failed to dispose connection for " + connection, e);
		}
	}

}
