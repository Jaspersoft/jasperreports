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
package net.sf.jasperreports.olap.xmla;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.olap.result.JROlapHierarchy;
import net.sf.jasperreports.olap.result.JROlapHierarchyLevel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRXmlaHierarchy implements JROlapHierarchy
{
	
	private final static Log log = LogFactory.getLog(JRXmlaHierarchy.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_XMLA_INVALID_DIMENSION_HIERARCHY = "data.olap.xmla.invalid.dimension.hierarchy";

	private static final Pattern DIMENSION_HIERARCHY_PATTERN = Pattern.compile("\\[.*\\]\\.\\[.*\\]");

	private final String dimensionName;
	private final String uniqueName;
	private final List<JRXmlaHierarchyLevel> levels;
	private JRXmlaHierarchyLevel[] levelArray;

	public JRXmlaHierarchy(String dimensionName)
	{
		this.dimensionName = dimensionName;
		// Dimension name could be of the form [Dimension].[Hierarchy]
		// in that case, just put in the Hierarchy as uniqueName
		this.uniqueName = parseUniqueName(dimensionName);
		this.levels = new ArrayList<>();
	}

	@Override
	public String getDimensionName()
	{
		return dimensionName;
	}

	@Override
	public JROlapHierarchyLevel[] getLevels()
	{
		return ensureLevelArray();
	}

	public void setLevel(String levelName, int depth)
	{
		int levelCount = levels.size();
		if (depth >= levelCount)
		{
			for (int i = levelCount; i <= depth; ++i)
			{
				levels.add(null);
			}
		}
		
		JRXmlaHierarchyLevel level = levels.get(depth);
		if (level == null)
		{
			level = new JRXmlaHierarchyLevel(levelName, depth);
			levels.set(depth, level);
		}
		else if (!levelName.equals(level.getName()))
		{
			if (log.isWarnEnabled())
			{
				log.warn("Different level name \"" + levelName + "\" found for level \"" + level.getName() + "\" at depth " + depth);
			}
		}
		
		resetLevelArray();
	}
	
	// MPenningroth 21-April-2009 deal with case when dimension is <dimension>.<hierarchy> form
	@Override
	public String getHierarchyUniqueName()
	{
		return uniqueName;
	}

	protected JRXmlaHierarchyLevel[] ensureLevelArray()
	{
		if (levelArray == null)
		{
			levelArray = new JRXmlaHierarchyLevel[levels.size()];
			levelArray = levels.toArray(levelArray);
		}
		return levelArray;
	}

	protected void resetLevelArray()
	{
		levelArray = null;
	}
	
	protected String parseUniqueName(String originalDimensionName)
	{
		Matcher m = DIMENSION_HIERARCHY_PATTERN.matcher(originalDimensionName);
		Boolean gotMatch = m.matches();
		if (gotMatch)
		{
			int startIndex = originalDimensionName.lastIndexOf(".[");
			int endIndex = originalDimensionName.lastIndexOf(']'); 
			if (startIndex == -1 || endIndex == -1 || startIndex + 3 >= endIndex)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_XMLA_INVALID_DIMENSION_HIERARCHY,
						new Object[]{originalDimensionName});
			}
			else
			{
				return originalDimensionName.substring(startIndex + 2, endIndex);
			}
		}
		return originalDimensionName;
	}
}
