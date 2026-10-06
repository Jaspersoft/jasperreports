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
package net.sf.jasperreports.engine.export;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PrintElementIndex
{
	private final PrintElementIndex parentIndex;
	private final int index;
	
	public PrintElementIndex(PrintElementIndex parentIndex, int index)
	{
		this.parentIndex = parentIndex;
		this.index = index;
	}

	public PrintElementIndex getParentIndex()
	{
		return parentIndex;
	}

	public int getIndex()
	{
		return index;
	}
	
	public static String asAddress(PrintElementIndex parentIndex, int elementIndex)
	{
		StringBuilder address = new StringBuilder();
		writeElementAddress(address, parentIndex, elementIndex);
		return address.toString();
	}

	protected static void writeElementAddress(StringBuilder output, PrintElementIndex parentIndex, int elementIndex)
	{
		if (parentIndex != null)
		{
			writeElementAddress(output, parentIndex.getParentIndex(), parentIndex.getIndex());
			output.append('_');
		}
		output.append(elementIndex);
	}
}
