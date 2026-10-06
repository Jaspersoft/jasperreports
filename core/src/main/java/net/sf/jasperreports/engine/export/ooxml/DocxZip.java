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
package net.sf.jasperreports.engine.export.ooxml;

import java.io.IOException;

import net.sf.jasperreports.engine.export.zip.ExportZipEntry;
import net.sf.jasperreports.engine.export.zip.FileBufferedZip;


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class DocxZip extends FileBufferedZip
{

	/**
	 * 
	 */
	private ExportZipEntry documentEntry;
	private ExportZipEntry stylesEntry;
	private ExportZipEntry settingsEntry;
	private ExportZipEntry relsEntry;
	private ExportZipEntry contentTypesEntry;
	private ExportZipEntry appEntry;
	private ExportZipEntry coreEntry;
	private ExportZipEntry fontTableEntry;
	private ExportZipEntry fontTableRelsEntry;
	
	/**
	 * 
	 */
	public DocxZip() throws IOException
	{
		documentEntry = createEntry("word/document.xml");
		
		stylesEntry = createEntry("word/styles.xml");
		
		settingsEntry = createEntry("word/settings.xml");
		
		fontTableEntry = createEntry("word/fontTable.xml");
		
		fontTableRelsEntry = createEntry("word/_rels/fontTable.xml.rels");
		
		relsEntry = createEntry("word/_rels/document.xml.rels");
		
		contentTypesEntry = createEntry("[Content_Types].xml");
		
		appEntry = createEntry("docProps/app.xml");

		coreEntry = createEntry("docProps/core.xml");

		addEntry("_rels/.rels", "net/sf/jasperreports/engine/export/ooxml/docx/_rels/xml.rels");
	}
	
	/**
	 *
	 */
	public ExportZipEntry getDocumentEntry()
	{
		return documentEntry;
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addHeader(int index)
	{
		return createEntry("word/header" + index + ".xml");
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addHeaderRels(int index)
	{
		return createEntry("word/_rels/header" + index + ".xml.rels");
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
	public ExportZipEntry getSettingsEntry()
	{
		return settingsEntry;
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
	public ExportZipEntry getFontTableEntry()
	{
		return fontTableEntry;
	}
	
	/**
	 *
	 */
	public ExportZipEntry getFontTableRelsEntry()
	{
		return fontTableRelsEntry;
	}
}
