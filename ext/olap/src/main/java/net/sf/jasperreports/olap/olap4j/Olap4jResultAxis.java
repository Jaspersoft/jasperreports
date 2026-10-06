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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.olap.result.JROlapHierarchy;
import net.sf.jasperreports.olap.result.JROlapMemberTuple;
import net.sf.jasperreports.olap.result.JROlapResultAxis;

import org.olap4j.CellSetAxis;
import org.olap4j.Position;
import org.olap4j.metadata.Hierarchy;


/**
 * @author swood
 */
public class Olap4jResultAxis implements JROlapResultAxis
{
	
	private List<Olap4jTuple> tuples;
	private List<Olap4jHierarchy> hierarchies;
	private Olap4jHierarchy[] hierarchyArray = null;
	
	public Olap4jResultAxis(CellSetAxis axis,
			List<Hierarchy> axisHierarchies,
			Olap4jFactory factory)
	{
		List<Position> positions = axis.getPositions();
		this.tuples = new ArrayList<>(positions.size());
		for (Iterator<Position> it = positions.iterator(); it.hasNext(); )
		{
			tuples.add(new Olap4jTuple(it.next(), factory));
		}
		
		this.hierarchies = new ArrayList<>(axisHierarchies.size());
		for (Iterator<Hierarchy> it = axisHierarchies.iterator(); it.hasNext(); )
		{
			hierarchies.add(new Olap4jHierarchy(it.next()));
		}
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

	protected Olap4jHierarchy[] ensureHierarchyArray()
	{
		if (hierarchyArray == null)
		{
			hierarchyArray = new Olap4jHierarchy[hierarchies.size()];
			hierarchyArray = hierarchies.toArray(hierarchyArray);
		}
		return hierarchyArray;
	}
}
