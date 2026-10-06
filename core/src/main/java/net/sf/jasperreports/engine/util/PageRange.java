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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PageRange
{
	private Integer startPageIndex;
	private Integer endPageIndex;
	
	/**
	 * 
	 */
	public PageRange(Integer startPageIndex, Integer endPageIndex)
	{
		this.startPageIndex = startPageIndex;
		this.endPageIndex = endPageIndex;
	}
	
	/**
	 * 
	 */
	public Integer getStartPageIndex()
	{
		return startPageIndex;
	}

	/**
	 * 
	 */
	public Integer getEndPageIndex()
	{
		return endPageIndex;
	}
	
	/**
	 * 
	 */
	public static PageRange[] parse(String strRanges)
	{
		List<PageRange> ranges = null;
		
		if (strRanges != null && strRanges.trim().length() > 0)
		{
			ranges = new ArrayList<>();

			String[] rangeTokens = strRanges.split(",");
			for (String rangeToken : rangeTokens)
			{
				PageRange pageRange = null;

				int hyphenPos = rangeToken.indexOf("-");
				if (hyphenPos > 0 && hyphenPos < rangeToken.length() - 1)
				{
					pageRange = 
						new PageRange(
							Integer.valueOf(rangeToken.substring(0, hyphenPos).trim()), 
							Integer.valueOf(rangeToken.substring(hyphenPos + 1).trim()) 
							);
				}
				else
				{
					int pageIndex = Integer.valueOf(rangeToken.trim());
					pageRange = new PageRange(pageIndex, pageIndex);
				}
				
				ranges.add(pageRange);
			}
		}
		
		return ranges == null ? null : (PageRange[]) ranges.toArray(new PageRange[ranges.size()]);
	}
	
	/**
	 * 
	 */
	public static boolean isPageInRanges(int pageIndex, PageRange[] ranges)
	{
		boolean isInRanges = false;
		
		if (ranges != null)
		{
			for (PageRange range : ranges)
			{
				if (range.startPageIndex <= pageIndex && pageIndex <= range.endPageIndex)
				{
					isInRanges = true;
					break;
				}
			}
		}
		
		return isInRanges;
	}
}
