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
package net.sf.jasperreports.export;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.util.JRLoader;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleExporterInput implements ExporterInput
{
	private List<ExporterInputItem> items;


	/**
	 * Creates an ExportInput object containing the list of {@link JasperPrint} objects to be exported. 
	 * If you need to concatenate several reports into the same document, you can use this constructor, 
	 * provided that you don't need to specify a different export configuration for each item. 
	 * Otherwise, consider using {@link #SimpleExporterInput(List)} instead.
	 */
	public static SimpleExporterInput getInstance(List<JasperPrint> jasperPrintList)
	{
		return new SimpleExporterInput(getItems(jasperPrintList));
	}


	/**
	 * Creates an {@link ExporterInput} object with a single item wrapping the {@link JasperPrint} object that will be exported. 
	 * If you already have a JasperPrint object, you can pass it to the exporter using this type of input.
	 */
	public SimpleExporterInput(JasperPrint jasperPrint)
	{
		if (jasperPrint != null)
		{
			this.items = new ArrayList<>();
			items.add(new SimpleExporterInputItem(jasperPrint));
		}
	}


	/**
	 * Creates an {@link ExporterInput} object with a single {@link JasperPrint} item read from the provided input stream. 
	 * If you want to read the JasperPrint object from an input stream (like a web location), you can pass the stream to this constructor.
	 */
	public SimpleExporterInput(InputStream inputStream)
	{
		if (inputStream != null)
		{
			JasperPrint jasperPrint = null;
			try
			{
				jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			this.items = new ArrayList<>();
			items.add(new SimpleExporterInputItem(jasperPrint));
		}
	}


	/**
	 * Creates an {@link ExporterInput} object with a single {@link JasperPrint} item read from the provided URL. 
	 * If the JasperPrint object is available as a web resource, you can use this constructor, instead of opening 
	 * a HTTP connection and read from the input stream.
	 */
	public SimpleExporterInput(URL url)
	{
		if (url != null)
		{
			JasperPrint jasperPrint = null;
			try
			{
				jasperPrint = (JasperPrint)JRLoader.loadObject(url);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			this.items = new ArrayList<>();
			items.add(new SimpleExporterInputItem(jasperPrint));
		}
	}


	/**
	 * Creates an {@link ExporterInput} object with a single {@link JasperPrint} item read from the provided <tt>java.io.File</tt>. 
	 * This is useful if the JasperPrint object is representing a file on disk.
	 */
	public SimpleExporterInput(File file)
	{
		if (file != null)
		{
			JasperPrint jasperPrint = null;
			try
			{
				jasperPrint = (JasperPrint)JRLoader.loadObject(file);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			this.items = new ArrayList<>();
			items.add(new SimpleExporterInputItem(jasperPrint));
		}
	}

	
	/**
	 * Creates an {@link ExporterInput} object with a single {@link JasperPrint} item read from the provided file. 
	 * This is useful if the JasperPrint object is representing a file on disk.
	 */
	public SimpleExporterInput(String fileName)
	{
		if (fileName != null)
		{
			JasperPrint jasperPrint = null;
			try
			{
				jasperPrint = (JasperPrint)JRLoader.loadObjectFromFile(fileName);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			this.items = new ArrayList<>();
			items.add(new SimpleExporterInputItem(jasperPrint));
		}
	}

	
	/**
	 * Creates an {@link ExporterInput} object with the provided export items.
	 */
	public SimpleExporterInput(List<ExporterInputItem> items)
	{
		this.items = items;
	}


	@Override
	public List<ExporterInputItem> getItems()
	{
		return items;
	}

	
	/**
	 * 
	 */
	protected static List<ExporterInputItem> getItems(List<JasperPrint> jasperPrintList)
	{
		List<ExporterInputItem> items = null;
		
		if (jasperPrintList != null)
		{
			items = new ArrayList<>(jasperPrintList.size());
			for (JasperPrint jasperPrint : jasperPrintList)
			{
				items.add(new SimpleExporterInputItem(jasperPrint));
			}
		}

		return items;
	}
}
