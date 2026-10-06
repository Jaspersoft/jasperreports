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
import java.io.OutputStream;
import java.io.Writer;

import net.sf.jasperreports.engine.export.FileXmlResourceHandler;
import net.sf.jasperreports.engine.export.XmlResourceHandler;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class SimpleXmlExporterOutput extends SimpleWriterExporterOutput implements XmlExporterOutput
{
	private static final String XML_FILES_SUFFIX = "_files";

	/**
	 * 
	 */
	private XmlResourceHandler imageHandler;
	private boolean imageHandlerSet;

	private Boolean isEmbeddingImages;
	
	private final File destFile;

	
	/**
	 * 
	 */
	public SimpleXmlExporterOutput(StringBuffer sbuffer)
	{
		super(sbuffer);
		
		destFile = null;
	}

	
	/**
	 * 
	 */
	public SimpleXmlExporterOutput(StringBuilder sbuilder)
	{
		super(sbuilder);
		
		destFile = null;
	}

	
	/**
	 * 
	 */
	public SimpleXmlExporterOutput(Writer writer)
	{
		super(writer);
		
		destFile = null;
	}
	

	/**
	 * 
	 */
	public SimpleXmlExporterOutput(OutputStream outputStream)
	{
		super(outputStream);
		
		destFile = null;
	}

	
	/**
	 * 
	 */
	public SimpleXmlExporterOutput(OutputStream outputStream, String encoding)
	{
		super(outputStream, encoding);
		
		destFile = null;
	}
	

	/**
	 * 
	 */
	public SimpleXmlExporterOutput(File file)
	{
		super(file);
		
		destFile = file;
	}

	
	/**
	 * 
	 */
	public SimpleXmlExporterOutput(File file, String encoding)
	{
		super(file, encoding);
		
		destFile = file;
	}

	
	/**
	 * 
	 */
	public SimpleXmlExporterOutput(String fileName)
	{
		super(fileName);
		
		destFile = fileName == null ? null : new File(fileName);
	}

	
	/**
	 * 
	 */
	public SimpleXmlExporterOutput(String fileName, String encoding)
	{
		super(fileName, encoding);
		
		destFile = fileName == null ? null : new File(fileName);
	}

	
	
	@Override
	public XmlResourceHandler getImageHandler() 
	{
		if (
			!imageHandlerSet
			&& imageHandler == null
			&& destFile != null
			)
		{
			File imagesFolder = new File(destFile.getParent(), destFile.getName() + XML_FILES_SUFFIX);
			imageHandler = new FileXmlResourceHandler(imagesFolder, imagesFolder.getName() + "/{0}");
		}
		
		return imageHandler;
	}


	/**
	 * 
	 */
	public void setImageHandler(XmlResourceHandler imageHandler)
	{
		this.imageHandler = imageHandler;
		this.imageHandlerSet = true;
	}


	@Override
	public Boolean isEmbeddingImages()
	{
		return isEmbeddingImages;
	}

	
	/**
	 * 
	 */
	public void setEmbeddingImages(Boolean isEmbeddingImages)
	{
		this.isEmbeddingImages = isEmbeddingImages;
	}
}
