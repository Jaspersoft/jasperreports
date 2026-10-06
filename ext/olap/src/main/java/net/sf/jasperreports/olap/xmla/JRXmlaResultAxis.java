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
package net.sf.jasperreports.olap.xmla;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.olap.result.JROlapHierarchy;
import net.sf.jasperreports.olap.result.JROlapMemberTuple;
import net.sf.jasperreports.olap.result.JROlapResultAxis;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRXmlaResultAxis implements JROlapResultAxis
{

	private final String axisName;
	private final List<JRXmlaHierarchy> hierarchyList;
	private JRXmlaHierarchy[] hierarchies;
	private final List<JRXmlaMemberTuple> tuples;
	
	public JRXmlaResultAxis(String axisName)
	{
		this.axisName = axisName;
		this.hierarchyList = new ArrayList<>();
		this.tuples = new ArrayList<>();
	}
	
	public String getAxisName()
	{
		return axisName;
	}

	@Override
	public JROlapHierarchy[] getHierarchiesOnAxis()
	{
		return ensureHierarchyArray();
	}

	@Override
	public JROlapMemberTuple getTuple(int index)
	{
		if (index < 0 || index >= tuples.size())
		{
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + tuples.size());
		}
		
		return tuples.get(index);
	}

	@Override
	public int getTupleCount()
	{
		return tuples.size();
	}

	public void addHierarchy(JRXmlaHierarchy hierarchy)
	{
		hierarchyList.add(hierarchy);
		resetHierarchyArray();
	}
	
	public void addTuple(JRXmlaMemberTuple tuple)
	{
		tuples.add(tuple);
		
		copyLevelInfo(tuple);
	}

	protected void copyLevelInfo(JRXmlaMemberTuple tuple)
	{
		JRXmlaMember[] members = tuple.getXmlaMembers();
		int idx = 0;
		for (Iterator<JRXmlaHierarchy> it = hierarchyList.iterator(); it.hasNext() && idx < members.length; ++idx)
		{
			JRXmlaHierarchy hierarchy = it.next();
			JRXmlaMember member = members[idx];
			if (hierarchy.getDimensionName().equals(member.getDimensionName()))
			{
				hierarchy.setLevel(member.getLevelName(), member.getDepth());
			}
		}
		
	}

	protected JRXmlaHierarchy[] ensureHierarchyArray()
	{
		if (hierarchies == null)
		{
			hierarchies = new JRXmlaHierarchy[hierarchyList.size()];
			hierarchies = hierarchyList.toArray(hierarchies);
		}
		return hierarchies;
	}

	protected void resetHierarchyArray()
	{
		hierarchies = null;
	}

}
