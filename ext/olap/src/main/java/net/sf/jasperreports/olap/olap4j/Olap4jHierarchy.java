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
package net.sf.jasperreports.olap.olap4j;

import net.sf.jasperreports.olap.result.JROlapHierarchy;
import net.sf.jasperreports.olap.result.JROlapHierarchyLevel;

import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.NamedList;


/**
 * @author swood
 */
public class Olap4jHierarchy implements JROlapHierarchy
{
	
	private final Hierarchy hierarchy;
	private final Olap4jLevel[] levels;
	private String uniqueName = null;//FIXME 
	
	public Olap4jHierarchy(Hierarchy hierarchy)
	{
		this.hierarchy = hierarchy;
		
		if (hierarchy == null)
		{
			levels = new Olap4jLevel[0];
		}
		else
		{
			NamedList<org.olap4j.metadata.Level> hierarchyLevels = hierarchy.getLevels();
			levels = new Olap4jLevel[hierarchyLevels.size()];
			for (int i = 0; i < hierarchyLevels.size(); i++)
			{
				levels[i] = new Olap4jLevel(hierarchyLevels.get(i));
			}
		}
	}

	@Override
	public String getDimensionName()
	{
		if (hierarchy != null)
		{
			return hierarchy.getDimension().getName();
		} 
		else
		{
			return uniqueName;
		}
	}

	@Override
	public JROlapHierarchyLevel[] getLevels()
	{
		return levels;
	}

	// MPenningroth 21-April-2009 deal with case when dimension is <dimension>.<hierarchy> form
	@Override
	public String getHierarchyUniqueName()
	{
		if (hierarchy != null) 
		{
			return hierarchy.getUniqueName();
		} 
		else 
		{
			return uniqueName;
		}
	}
}
