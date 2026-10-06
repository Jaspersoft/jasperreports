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
package net.sf.jasperreports.engine.export.zip;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.util.JRLoader;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class FileBufferedZip extends AbstractZip
{
	/**
	 * 
	 */
	private final Integer memoryThreshold;

	/**
	 * 
	 */
	public FileBufferedZip()
	{
		this(null);
	}
	
	/**
	 * 
	 */
	public FileBufferedZip(Integer memoryThreshold)
	{
		this.memoryThreshold = memoryThreshold;
	}
	
	@Override
	public ExportZipEntry createEntry(String name)
	{
		ExportZipEntry entry = memoryThreshold == null ? new FileBufferedZipEntry(name) : new FileBufferedZipEntry(name, memoryThreshold);

		addEntry(entry);
		
		return entry;
	}
	
	/**
	 *
	 */
	public void addEntry(String name, String resource)
	{
		byte[] bytes = null;

		try
		{
			bytes = JRLoader.loadBytesFromResource(resource);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
		
		addEntry(new FileBufferedZipEntry(name, bytes));
	}

	/**
	 *
	 */
	public void addEntry(String name, byte[] bytes)
	{
		addEntry(new FileBufferedZipEntry(name, bytes));
	}
	
}
