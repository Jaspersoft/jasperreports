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
package net.sf.jasperreports.engine;

import java.io.Serializable;

/**
 * An Id for a print element.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRPrintElement#getSourceElementId()
 * @see JRPrintElement#getPrintElementId()
 */
public class PrintElementId implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static PrintElementId forElement(JRPrintElement element)
	{
		return new PrintElementId(element.getSourceElementId(), element.getPrintElementId());
	}
	
	public static boolean matches(JRPrintElement element1, JRPrintElement element2)
	{
		return matches(element1.getSourceElementId(), element1.getPrintElementId(), 
				element2.getSourceElementId(), element2.getPrintElementId());
	}
	
	protected static boolean matches(int sourceElementId1, int printId1,
			int sourceElementId2, int printId2)
	{
		return sourceElementId1 != JRPrintElement.UNSET_SOURCE_ELEMENT_ID
				&& sourceElementId1 == sourceElementId2
				&& printId1 != JRPrintElement.UNSET_PRINT_ELEMENT_ID
				&& printId1 == printId2;
	}
	
	private final int sourceElementId;
	private final int printId;
	
	public PrintElementId(int sourceElementId, int printId)
	{
		this.sourceElementId = sourceElementId;
		this.printId = printId;
	}

	public int getSourceElementId()
	{
		return sourceElementId;
	}

	public int getPrintId()
	{
		return printId;
	}

	@Override
	public int hashCode()
	{
		return 2039 * sourceElementId + 1021 * printId;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PrintElementId))
		{
			return false;
		}
		
		PrintElementId id = (PrintElementId) obj;
		return sourceElementId == id.sourceElementId && printId == id.printId;
	}

	@Override
	public String toString()
	{
		return sourceElementId + "#" + printId;
	}
	
	public boolean matches(PrintElementId id)
	{
		return matches(sourceElementId, printId, id.sourceElementId, id.printId);
	}
	
	public boolean matches(JRPrintElement element)
	{
		return matches(sourceElementId, printId, element.getSourceElementId(), element.getPrintElementId());
	}
}
