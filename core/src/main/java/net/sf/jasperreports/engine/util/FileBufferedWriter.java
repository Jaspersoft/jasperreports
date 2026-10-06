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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import net.sf.jasperreports.engine.JRRuntimeException;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FileBufferedWriter extends Writer 
{
	private FileBufferedOutputStream fbos;
	private Writer osw;
	private boolean isEmpty = true;

	/**
	 * 
	 */
	public FileBufferedWriter()
	{
		fbos = new FileBufferedOutputStream();
		try
		{
			osw = new BufferedWriter(new OutputStreamWriter(fbos, "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean isEmpty()
	{
		return isEmpty;
	}
	
	/**
	 * 
	 */
	public void writeData(Writer writer)
	{
		Reader reader = null;

		try
		{
			close();
			
			reader = new InputStreamReader(fbos.getDataInputStream(), StandardCharsets.UTF_8);

			char[] chars = new char[10000];
			int ln = 0;
			while ((ln = reader.read(chars)) > 0)
			{
				writer.write(chars, 0, ln);
			}

			writer.flush();
		}
		catch(IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch(IOException e)
				{
				}
			}
		}
	}

	public void dispose()
	{
		fbos.dispose();
	}
	
	@Override
	public void close() throws IOException 
	{
		osw.close();
	}

	@Override
	public void flush() throws IOException 
	{
		osw.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException 
	{
		if (len > 0)
		{
			isEmpty = false;
		}
		osw.write(cbuf, off, len);
	}
	
}
