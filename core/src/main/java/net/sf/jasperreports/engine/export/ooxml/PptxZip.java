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
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PptxZip extends FileBufferedZip
{

	/**
	 * 
	 */
	private ExportZipEntry presentationEntry;
//	private ExportZipEntry stylesEntry;
	private ExportZipEntry relsEntry;
	private ExportZipEntry contentTypesEntry;
	private ExportZipEntry appEntry;
	private ExportZipEntry coreEntry;
	
	/**
	 * 
	 */
	public PptxZip() throws IOException
	{
		presentationEntry = createEntry("ppt/presentation.xml");
		
//		stylesEntry = createEntry("xl/styles.xml");
		
		relsEntry = createEntry("ppt/_rels/presentation.xml.rels");

		contentTypesEntry = createEntry("[Content_Types].xml");
		
		appEntry = createEntry("docProps/app.xml");

		coreEntry = createEntry("docProps/core.xml");

		addEntry("_rels/.rels", "net/sf/jasperreports/engine/export/ooxml/pptx/_rels/xml.rels");
		addEntry("ppt/slideLayouts/_rels/slideLayout1.xml.rels", "net/sf/jasperreports/engine/export/ooxml/pptx/ppt/slideLayouts/_rels/slideLayout1.xml.rels");
		addEntry("ppt/slideLayouts/slideLayout1.xml", "net/sf/jasperreports/engine/export/ooxml/pptx/ppt/slideLayouts/slideLayout1.xml");
		addEntry("ppt/theme/theme1.xml", "net/sf/jasperreports/engine/export/ooxml/pptx/ppt/theme/theme1.xml");
		addEntry("ppt/tableStyles.xml", "net/sf/jasperreports/engine/export/ooxml/pptx/ppt/tableStyles.xml");
	}
	
	/**
	 *
	 */
	public ExportZipEntry getPresentationEntry()
	{
		return presentationEntry;
	}
	
//	/**
//	 *
//	 */
//	public ExportZipEntry getStylesEntry()
//	{
//		return stylesEntry;
//	}
	
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
	public ExportZipEntry addSlideMaster()
	{
		return createEntry("ppt/slideMasters/slideMaster1.xml");
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addSlide(int index)
	{
		return createEntry("ppt/slides/slide" + index + ".xml");
	}
	
	/**
	 * 
	 */
	public ExportZipEntry addSlideMasterRels()
	{
		return createEntry("ppt/slideMasters/_rels/slideMaster1.xml.rels");
	}
	/**
	 * 
	 */
	public ExportZipEntry addSlideRels(int index)
	{
		return createEntry("ppt/slides/_rels/slide" + index + ".xml.rels");
	}
	
}
