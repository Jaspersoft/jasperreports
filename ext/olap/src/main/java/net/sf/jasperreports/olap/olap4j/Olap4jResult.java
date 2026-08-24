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
package net.sf.jasperreports.olap.olap4j;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.olap.result.JROlapCell;
import net.sf.jasperreports.olap.result.JROlapResult;
import net.sf.jasperreports.olap.result.JROlapResultAxis;

import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;


/**
 * @author swood
 */
public class Olap4jResult implements JROlapResult
{
	
	private CellSet cellSet = null;
	private Olap4jResultAxis[] axes;

	public Olap4jResult(CellSet cSet)
	{
		this.cellSet = cSet;
		
		Olap4jFactory factory = new Olap4jFactory();

		List<CellSetAxis> resultAxes = cellSet.getAxes();
		axes = new Olap4jResultAxis[resultAxes.size()];
		for (int i = 0; i < resultAxes.size(); i++)
		{
			// AxisOrdinal ordinal = AxisOrdinal.StandardAxisOrdinal.forLogicalOrdinal(i);
			axes[i] = new Olap4jResultAxis(cellSet.getAxes().get(i),
					cellSet.getAxes().get(i).getAxisMetaData().getHierarchies(),
					factory);
		}
	}

	@Override
	public JROlapResultAxis[] getAxes()
	{
		return axes;
	}

	@Override
	public JROlapCell getCell(int[] axisPositions)
	{
		List<Integer> positions = new ArrayList<>(axisPositions.length);
		for (int axisPosition : axisPositions)
		{
			positions.add(axisPosition);
		} 
		Cell dataCell = cellSet.getCell(positions);
		return new Olap4jCell(dataCell);
	}
}
