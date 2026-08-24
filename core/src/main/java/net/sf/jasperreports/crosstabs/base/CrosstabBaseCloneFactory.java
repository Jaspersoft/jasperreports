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
package net.sf.jasperreports.crosstabs.base;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRVariable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CrosstabBaseCloneFactory//FIXME use CloneStore instead
{
	private Map<JRVariable, JRVariable> clonedVariables = new HashMap<>();
	
	public JRVariable clone(JRVariable variable)
	{
		return clonedVariables.computeIfAbsent(variable, k -> (JRVariable) variable.clone());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] cloneCrosstabObjects(T[] groups)
	{
		if (groups == null)
		{
			return null;
		}
		
		T[] clones = (T[]) Array.newInstance(groups.getClass().getComponentType(), groups.length);
		for (int i = 0; i < groups.length; i++)
		{
			// assuming CrosstabBaseCloneable
			clones[i] = (T) ((CrosstabBaseCloneable) groups[i]).clone(this);
		}
		return clones;
	}
}
