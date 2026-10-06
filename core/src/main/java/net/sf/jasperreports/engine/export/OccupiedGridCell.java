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

import net.sf.jasperreports.engine.JRPrintElement;

	
	
/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class OccupiedGridCell extends JRExporterGridCell
{

	private static final GridCellSize SIZE = new GridCellSize(0, 0, 1, 1);

	/**
	 *
	 */
	private JRExporterGridCell occupier;

	/**
	 *
	 */
	public OccupiedGridCell(JRExporterGridCell occupier)
	{
		//FIXME do we need GridCellStyle for occupied cells?
		super();
		
		this.occupier = occupier;
	}

	@Override
	public GridCellSize getSize()
	{
		return SIZE;
	}

	public JRExporterGridCell getOccupier()
	{
		return occupier;
	}

	@Override
	public byte getType()
	{
		return TYPE_OCCUPIED_CELL;
	}

	@Override
	public JRPrintElement getElement()
	{
		return null;
	}

	@Override
	public String getElementAddress()
	{
		return null;
	}

	@Override
	public String getProperty(String propName)
	{
		return occupier.getProperty(propName);
	}

}
