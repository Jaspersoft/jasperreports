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

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRStringUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PptxSlideRelsHelper extends BaseHelper
{

	private Map<String,Integer> linkCache = new HashMap<>();
	private Set<String> imageNames = new HashSet<>();
	
	/**
	 * 
	 */
	public PptxSlideRelsHelper(JasperReportsContext jasperReportsContext, Writer writer)
	{
		super(jasperReportsContext, writer);
	}

	/**
	 * 
	 */
	public void exportHeader(boolean isSlideMaster)
	{
		write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		write("<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n");
		if (isSlideMaster)
		{
			write("<Relationship Id=\"rIdTh\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme\" Target=\"../theme/theme1.xml\"/>\n");
		}
		write("<Relationship Id=\"rIdSl\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slideLayout\" Target=\"../slideLayouts/slideLayout1.xml\"/>\n");
	}
	
	/**
	 * 
	 */
	public void exportImage(String imageName)
	{
		if (!imageNames.contains(imageName))
		{
			write(" <Relationship Id=\"" + imageName + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" Target=\"../media/" + imageName + "\"/>\n");
			imageNames.add(imageName);
		}
	}
	
	/**
	 *
	 */
	public int getHyperlink(String href)
	{
		Integer linkIndex = linkCache.get(href);
		if (linkIndex == null)
		{
			linkIndex = linkCache.size();
			exportHyperlink(linkIndex, href);
			linkCache.put(href, linkIndex);
		}
		return linkIndex;
	}

	/**
	 * 
	 */
	private void exportHyperlink(int index, String href)
	{
		write(" <Relationship Id=\"rIdLnk" 
			+ index + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink\" Target=\"" 
			+ JRStringUtil.xmlEncode(href) + "\" TargetMode=\"External\"/>\n");
	}
	
	/**
	 * 
	 */
	public void exportFooter()
	{
		write("</Relationships>\n");
	}
	
}
