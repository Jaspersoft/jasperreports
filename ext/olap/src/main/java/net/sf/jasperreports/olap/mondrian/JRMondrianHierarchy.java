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
package net.sf.jasperreports.olap.mondrian;

import mondrian.olap.Hierarchy;
import mondrian.olap.Level;
import net.sf.jasperreports.olap.result.JROlapHierarchy;
import net.sf.jasperreports.olap.result.JROlapHierarchyLevel;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRMondrianHierarchy implements JROlapHierarchy
{
	
	private final Hierarchy hierarchy;
	private final JRMondrianLevel[] levels;
	
	public JRMondrianHierarchy(Hierarchy hierarchy)
	{
		this.hierarchy = hierarchy;
		
		if (hierarchy == null)
		{
			levels = new JRMondrianLevel[0];
		}
		else
		{
			Level[] hierarchyLevels = hierarchy.getLevels();
			levels = new JRMondrianLevel[hierarchyLevels.length];
			for (int i = 0; i < hierarchyLevels.length; i++)
			{
				levels[i] = new JRMondrianLevel(hierarchyLevels[i]);
			}
		}
	}

	@Override
	public String getDimensionName()
	{
		return hierarchy == null ? null : hierarchy.getDimension().getName();
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
		return hierarchy == null ? null : hierarchy.getUniqueName();
	}

}
