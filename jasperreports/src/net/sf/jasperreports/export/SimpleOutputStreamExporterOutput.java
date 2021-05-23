/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.export;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleOutputStreamExporterOutput implements OutputStreamExporterOutput
{
	/**
	 * 
	 */
	private OutputStream outputStream;
	private boolean toClose;
	
	/**
	 * Creates a {@link OutputStreamExporterOutput} instance that puts the result into provided <tt>java.io.OutputStream</tt> object. 
	 * This is useful for sending the export result to an output stream, such as a <tt>ServletOutputStream</tt>.
	 */
	public SimpleOutputStreamExporterOutput(OutputStream outputStream)
	{
		this.outputStream = outputStream;
		toClose = false;
	}
	

	/**
	 * Creates a {@link OutputStreamExporterOutput} instance that puts the result into the provided <tt>java.io.File</tt> object. 
	 * This is useful when exporting to a file and the <tt>File</tt> instance is already there.
	 */
	public SimpleOutputStreamExporterOutput(File file)
	{
		if (file != null)
		{
			try
			{
				outputStream = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
			}
			catch (IOException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		toClose = true;
	}

	
	/**
	 * Creates a {@link OutputStreamExporterOutput} instance that puts the result into the file designated by the provided file name. 
	 * This is an alternative to the {@link #SimpleOutputStreamExporterOutput(File)}.
	 */
	public SimpleOutputStreamExporterOutput(String fileName)
	{
		this(fileName == null ? null : new File(fileName));
	}

	
	@Override
	public OutputStream getOutputStream()
	{
		return outputStream;
	}

	@Override
	public void close()
	{
		if (toClose)
		{
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
			}
		}
	}
}
