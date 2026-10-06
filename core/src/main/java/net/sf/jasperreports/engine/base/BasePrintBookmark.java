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


/*
 * Contributors:
 * John Bindel - jbindel@users.sourceforge.net 
 */

package net.sf.jasperreports.engine.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.PrintBookmark;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class BasePrintBookmark implements PrintBookmark, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private String label;
	private final int pageIndex;
	private String elementAddress;
	
	// we're keeping the level here to have it in BookmarkHelper.appendBookmarks
	// not adding to the PrintBookmark interface for now
	private int level;

	public List<PrintBookmark> bookmarks;

	public BasePrintBookmark(String label, int pageIndex, String elementAddress)
	{
		this(label, pageIndex, elementAddress, 0);
	}

	public BasePrintBookmark(String label, int pageIndex, String elementAddress,
			int level)
	{
		this.label = label;
		this.pageIndex = pageIndex;
		this.elementAddress = elementAddress;
		this.level = level;
	}
	
	@Override
	public String getLabel()
	{
		return label;
	}
	
	@Override
	public int getPageIndex()
	{
		return pageIndex;
	}
	
	@Override
	public String getElementAddress()
	{
		return elementAddress;
	}
	
	public void addBookmark(PrintBookmark child)
	{
		if (bookmarks == null)
		{
			bookmarks = new ArrayList<>();
		}
		bookmarks.add(child);
	}
	
	@Override
	public List<PrintBookmark> getBookmarks()
	{
		return bookmarks;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}

	public int getLevel()
	{
		return level;
	}
}
