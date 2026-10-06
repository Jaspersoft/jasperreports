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
package net.sf.jasperreports.crosstabs.fill;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.engine.fill.JRFillCellContents;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillCrosstabCell implements JRCrosstabCell
{
	private JRCrosstabCell parentCell;
	protected JRFillCellContents contents;
	
	public JRFillCrosstabCell(JRCrosstabCell cell, JRFillCrosstabObjectFactory factory)
	{
		factory.put(cell, this);
		
		parentCell = cell;
		
		contents = factory.getCell(cell.getContents(), JRCellContents.TYPE_DATA);
	}

	@Override
	public String getRowTotalGroup()
	{
		return parentCell.getRowTotalGroup();
	}

	@Override
	public String getColumnTotalGroup()
	{
		return parentCell.getColumnTotalGroup();
	}

	@Override
	public JRCellContents getContents()
	{
		return contents;
	}

	public JRFillCellContents getFillContents()
	{
		return contents;
	}

	@Override
	public Integer getWidth()
	{
		return parentCell.getWidth();
	}

	@Override
	public Integer getHeight()
	{
		return parentCell.getHeight();
	}
	
	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
