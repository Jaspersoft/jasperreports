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
package net.sf.jasperreports.engine.design;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CompilationUnits
{

	private JRCompilationUnit[] reportUnits;
	private JRCompilationUnit[] sourceUnits;
	private int[] sourceUnitIndexes;

	public CompilationUnits(JRCompilationUnit[] reportUnits)
	{
		this.reportUnits = reportUnits;
		initSourceUnits();
	}

	private void initSourceUnits()
	{
		sourceUnitIndexes = new int[reportUnits.length];
		List<JRCompilationUnit> sourceUnitList = new ArrayList<>(reportUnits.length);
		for (int i = 0; i < reportUnits.length; i++)
		{
			JRCompilationUnit unit = reportUnits[i];
			if (unit.hasSource())
			{
				sourceUnitIndexes[i] = sourceUnitList.size();
				sourceUnitList.add(unit);
			}
			else
			{
				sourceUnitIndexes[i] = -1;
			}
		}
		sourceUnits = sourceUnitList.toArray(new JRCompilationUnit[sourceUnitList.size()]);
	}
	
	public JRCompilationUnit[] getSourceUnits()
	{
		return sourceUnits;
	}
	
	public JRCompilationUnit getCompiledUnit(int index)
	{
		//Java compilers update the source unit array when builtin functions are used
		//return from the source unit array when found
		int sourceUnitIndex = sourceUnitIndexes[index];
		return sourceUnitIndex >= 0 ? sourceUnits[sourceUnitIndex] : reportUnits[index];
	}

}
