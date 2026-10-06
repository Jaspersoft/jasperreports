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
package net.sf.jasperreports.crosstabs.base;

import java.io.Serializable;

import net.sf.jasperreports.crosstabs.CrosstabColumnCell;
import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.type.CrosstabColumnPositionEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseCrosstabColumnCell implements CrosstabColumnCell, Serializable
{
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	protected int height;
	protected CrosstabColumnPositionEnum contentsPosition;
	protected JRCellContents cellContents;

	// used by the design implementation
	protected BaseCrosstabColumnCell()
	{
	}

	public BaseCrosstabColumnCell(CrosstabColumnCell cell, JRBaseObjectFactory factory)
	{
		factory.put(cell, this);
		
		height = cell.getHeight();
		contentsPosition = cell.getContentsPosition();
		cellContents = factory.getCell(cell.getCellContents());
	}
	
	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public CrosstabColumnPositionEnum getContentsPosition()
	{
		return contentsPosition;
	}

	@Override
	public JRCellContents getCellContents()
	{
		return cellContents;
	}
	
	@Override
	public Object clone()
	{
		try
		{
			BaseCrosstabColumnCell clone = (BaseCrosstabColumnCell) super.clone();
			clone.cellContents = JRCloneUtils.nullSafeClone(cellContents);
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
	}

}
