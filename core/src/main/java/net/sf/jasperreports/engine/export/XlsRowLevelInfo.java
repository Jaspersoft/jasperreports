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
 * Greg Hilton
 */

package net.sf.jasperreports.engine.export;

import java.util.SortedMap;
import java.util.TreeMap;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class XlsRowLevelInfo
{
	private SortedMap<String, Integer> levelMap = new TreeMap<>();
	private Integer endIndex;
	
	public XlsRowLevelInfo()
	{			
	}
	
//	public XlsRowLevelRange(Integer startIndex)
//	{		
//		this(startIndex, null);
//	}
	
//	public XlsRowLevelRange(Integer startIndex,Integer endIndex)
//	{		
//		this.startIndex = startIndex;
//		this.endIndex = endIndex;
//	}

	public SortedMap<String, Integer> getLevelMap()
	{
		return this.levelMap;
	}

//	public void setStartIndex(Integer startIndex)
//	{
//		this.startIndex = startIndex;
//	}

	public Integer getEndIndex()
	{
		return this.endIndex;
	}

	public void setEndIndex(Integer endIndex)
	{
		this.endIndex = endIndex;
	}
	
//	public void increaseStartIndex()
//	{
//		this.startIndex++;
//	}
//	
//	public void increaseEndIndex()
//	{
//		this.endIndex++;
//	}
//	
//	public void decreaseStartIndex()
//	{
//		this.startIndex--;
//	}
//	
//	public void decreaseEndIndex()
//	{
//		this.endIndex--;
//	}
//	
//	public boolean equals(Object obj)
//	{
//		if (obj == this)
//		{
//			return true;
//		}
//
//		IntegerRange range = (IntegerRange) obj;
//
//		return (startIndex == null ? range.startIndex == null : startIndex.equals(range.startIndex)) 
//			&& (endIndex == null ? range.endIndex == null : endIndex.equals(range.endIndex));
//	}
//
//	public int hashCode()
//	{
//		ObjectUtils.HashCode hash = ObjectUtils.hash();
//		hash.add(startIndex);
//		hash.add(endIndex);
//		return hash.getHashCode();
//	}
	
}

