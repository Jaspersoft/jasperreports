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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZip;
import net.sf.jasperreports.repo.RepositoryUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsxZip extends FileBufferedZip
{
	private static final String EXCEPTION_MESSAGE_KEY_MACRO_TEMPLATE_NOT_FOUND = "export.xlsx.macro.template.not.found";
	
	private final RepositoryUtil repository;

	/**
	 * 
	 */
	private ExportZipEntry workbookEntry;
	private ExportZipEntry stylesEntry;
	private ExportZipEntry sharedStringsEntry;
	private ExportZipEntry relsEntry;
	private ExportZipEntry contentTypesEntry;
	private ExportZipEntry appEntry;
	private ExportZipEntry coreEntry;
	
	/**
	 * 
	 */
	public XlsxZip(JasperReportsContext jasperReportsContext) throws IOException
	{
		this(jasperReportsContext, null);
	}
	
	/**
	 * 
	 */
	public XlsxZip(JasperReportsContext jasperReportsContext, Integer memoryThreshold) throws IOException
	{
		this(jasperReportsContext, RepositoryUtil.getInstance(jasperReportsContext), memoryThreshold);
	}
	
	public XlsxZip(JasperReportsContext jasperReportsContext, RepositoryUtil repository, Integer memoryThreshold) throws IOException
	{
		super(memoryThreshold);

		this.repository = repository;
		
		workbookEntry = createEntry("xl/workbook.xml");
		
		stylesEntry = createEntry("xl/styles.xml");
		
		sharedStringsEntry = createEntry("xl/sharedStrings.xml");

		relsEntry = createEntry("xl/_rels/workbook.xml.rels");
		
		contentTypesEntry = createEntry("[Content_Types].xml");
		
		appEntry = createEntry("docProps/app.xml");

		coreEntry = createEntry("docProps/core.xml");

		addEntry("_rels/.rels", "net/sf/jasperreports/engine/export/ooxml/xlsx/_rels/xml.rels");
	}
	
	/**
	 *
	 */
	public ExportZipEntry getWorkbookEntry()
	{
		return workbookEntry;
	}
	
	/**
	 *
	 */
	public ExportZipEntry getStylesEntry()
	{
		return stylesEntry;
	}
	
	/**
	 *
	 */
	public ExportZipEntry getSharedStringsEntry()
	{
		return sharedStringsEntry;
	}
	
	/**
	 *
	 */
	public ExportZipEntry getRelsEntry()
	{
		return relsEntry;
	}
	
	/**
	 *
	 */
	public ExportZipEntry getContentTypesEntry()
	{
		return contentTypesEntry;
	}
	
	/**
	 *
	 */
	public ExportZipEntry getAppEntry()
	{
		return appEntry;
	}
	
	/**
	 *
	 */
	public ExportZipEntry getCoreEntry()
	{
		return coreEntry;
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addSheet(int index)
	{
		return createEntry("xl/worksheets/sheet" + index + ".xml");
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addSheetRels(int index)
	{
		return createEntry("xl/worksheets/_rels/sheet" + index + ".xml.rels");
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addDrawing(int index)
	{
		return createEntry("xl/drawings/drawing" + index + ".xml");
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addDrawingRels(int index)
	{
		return createEntry("xl/drawings/_rels/drawing" + index + ".xml.rels");
	}

	/**
	 * 
	 */
	public void addMacro(String template)
	{
		InputStream templateIs = null;
		ZipInputStream templateZipIs = null;
		try
		{
			templateIs = repository.getInputStreamFromLocation(template);//TODO
			if (templateIs == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_MACRO_TEMPLATE_NOT_FOUND,
						new Object[]{template});
			}
			else
			{
				templateZipIs = new ZipInputStream(templateIs);
				
				ZipEntry entry = null;
				while ((entry = templateZipIs.getNextEntry()) != null)
				{
					if ("xl/vbaProject.bin".equals(entry.getName()))
					{
						break;
					}
				}
				
				if (entry != null)
				{
					ExportZipEntry macroEntry = createEntry("xl/vbaProject.bin");
					OutputStream entryOs = macroEntry.getOutputStream();

					long entryLength = entry.getSize();
					
					byte[] bytes = new byte[10000];
					int ln = 0;
					long readBytesLength = 0;
					while (readBytesLength < entryLength && (ln = templateZipIs.read(bytes)) >= 0)
					{
						readBytesLength += ln;
						entryOs.write(bytes, 0, ln);
					}
				}
			}
		}
		catch (JRException | IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (templateZipIs != null)
			{
				try
				{
					templateZipIs.close();
				}
				catch (IOException e)
				{
				}
			}

			if (templateIs != null)
			{
				try
				{
					templateIs.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}
	
}
