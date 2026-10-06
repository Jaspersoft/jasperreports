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
package net.sf.jasperreports.json.export;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import net.sf.jasperreports.engine.export.FileHtmlResourceHandler;
import net.sf.jasperreports.engine.export.HtmlResourceHandler;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleJsonExporterOutput extends SimpleWriterExporterOutput implements JsonExporterOutput
{
	/**
	 * 
	 */
	private HtmlResourceHandler fontHandler;
	private HtmlResourceHandler resourceHandler;
	private HtmlResourceHandler internalResourceHandler;

	
	/**
	 * 
	 */
	public SimpleJsonExporterOutput(StringBuffer sbuffer)
	{
		super(sbuffer);
	}

	
	/**
	 * 
	 */
	public SimpleJsonExporterOutput(StringBuilder sbuilder)
	{
		super(sbuilder);
	}

	
	/**
	 * 
	 */
	public SimpleJsonExporterOutput(Writer writer)
	{
		super(writer);
	}
	

	/**
	 * 
	 */
	public SimpleJsonExporterOutput(OutputStream outputStream)
	{
		super(outputStream);
	}

	
	/**
	 * 
	 */
	public SimpleJsonExporterOutput(OutputStream outputStream, String encoding)
	{
		super(outputStream, encoding);
	}
	

	/**
	 * 
	 */
	public SimpleJsonExporterOutput(File file)
	{
		super(file);
		
		setFileHandlers(file);
	}

	
	/**
	 * 
	 */
	public SimpleJsonExporterOutput(File file, String encoding)
	{
		super(file, encoding);
		
		setFileHandlers(file);
	}

	
	/**
	 * 
	 */
	public SimpleJsonExporterOutput(String fileName)
	{
		super(fileName);
		
		setFileHandlers(new File(fileName));
	}

	
	/**
	 * 
	 */
	public SimpleJsonExporterOutput(String fileName, String encoding)
	{
		super(fileName, encoding);
		
		setFileHandlers(new File(fileName));
	}
	
	@Override
	public HtmlResourceHandler getFontHandler() 
	{
		return fontHandler;
	}

	/**
	 * 
	 */
	public void setFontHandler(HtmlResourceHandler fontHandler)
	{
		this.fontHandler = fontHandler;
	}
	
	@Override
	public HtmlResourceHandler getResourceHandler() 
	{
		return resourceHandler;
	}

	/**
	 * 
	 */
	public void setResourceHandler(HtmlResourceHandler resourceHandler)
	{
		this.resourceHandler = resourceHandler;
	}

	@Override
	public HtmlResourceHandler getInternalResourceHandler()
	{
		return internalResourceHandler;
	}

	/**
	 *
	 */
	public void setInternalResourceHandler(HtmlResourceHandler internalResourceHandler)
	{
		this.internalResourceHandler = internalResourceHandler;
	}
	
	/**
	 * 
	 */
	private void setFileHandlers(File destFile)
	{
		File resourcesDir = new File(destFile.getParent(), destFile.getName() + "_files");
		String pathPattern = resourcesDir.getName() + "/{0}";
		fontHandler = new FileHtmlResourceHandler(resourcesDir, pathPattern);
	}
}
