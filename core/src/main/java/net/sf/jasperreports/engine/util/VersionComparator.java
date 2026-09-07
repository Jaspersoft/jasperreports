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
package net.sf.jasperreports.engine.util;

import java.util.Comparator;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class VersionComparator implements Comparator<String>
{

	public static final String LOWEST_VERSION = "0";
	
	@Override
	public int compare(String currentVersion, String oldVersion) 
	{
		if (oldVersion == null)
		{
			throw new IllegalArgumentException("Reference version can't be null.");
		}
		
		if (currentVersion == null || currentVersion.startsWith(oldVersion)) 
		{
			return 1;
		}
		else 
		{
			String[] oldVersionChunks = oldVersion.split("\\.");			
			String[] currentVersionChunks = currentVersion.split("\\.");	
			int count = Math.min(oldVersionChunks.length, currentVersionChunks.length);
			for (int i = 0, old = 0, current = 0; i < count; i++)
			{
				try
				{
					//numeric comparison
					old = Integer.valueOf(oldVersionChunks[i]);
					current = Integer.valueOf(currentVersionChunks[i]);
					
					if (current != old)
					{
						return current - old;
					}
				} 
				catch (NumberFormatException e)
				{
					//string comparison
					if (currentVersionChunks[i].compareTo(oldVersionChunks[i]) != 0)
					{
						return currentVersionChunks[i].compareTo(oldVersionChunks[i]);
					}
				}
			}
			return currentVersionChunks.length - oldVersionChunks.length;
		}
	}
	
}