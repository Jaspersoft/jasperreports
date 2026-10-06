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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.util.SortedIntList;

/**
 * Utility class used by grid exporters to create a grid for page layout.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class CutsInfo
{
	private final SortedIntList cutOffsets = new SortedIntList();
	private Cut[] cuts;
	
	private Map<String, Object> propertiesMap;
	
	public CutsInfo()
	{
		propertiesMap = new HashMap<>();
		addCutOffset(0);
	}
	
	public CutsInfo(Integer lastCutOffset)
	{
		this();
		addCutOffset(lastCutOffset);
	}
	
	public int size()
	{
		return cutOffsets.size();
	}
	
	public void use()//FIXMEXLS maybe remove this
	{
		if (cuts == null)
		{
			cuts = new Cut[cutOffsets.size()];
		}
	}
	
	public int getCutOffset(int index)
	{
		return cutOffsets.get(index);
	}
	
	public Cut getCut(int index)
	{
		Cut cut = cuts[index];
		if (cut == null)
		{
			cut = new Cut();
			cuts[index] = cut;
		}
		return cut;
	}
	
	public void addUsage(int index, int usage)
	{
		Cut cut = getCut(index);
		int tmpUsage = cut.getUsage();
		tmpUsage |= usage;
		cut.setUsage(tmpUsage);
	}
	
	public boolean addCutOffset(int cutOffset)
	{
		return cutOffsets.add(cutOffset);
	}

	public boolean removeCutOffset(int cutOffset)
	{
		return cutOffsets.remove(cutOffset);
	}
	
	public int indexOfCutOffset(Integer cutOffset)
	{
		return cutOffsets.indexOf(cutOffset);
	}

	public boolean hasCuts()
	{
		return !cutOffsets.isEmpty();
	}
	
	public int getFirstCutOffset()
	{
		return getCutOffset(0);
	}
	
	public int getLastCutOffset()
	{
		return getCutOffset(size() - 1);
	}
	
	public int getTotalLength()
	{
		return hasCuts() ? getLastCutOffset() - getFirstCutOffset() : 0;
	}

	public Map<String, Object> getPropertiesMap() 
	{
		return propertiesMap;
	}

}
