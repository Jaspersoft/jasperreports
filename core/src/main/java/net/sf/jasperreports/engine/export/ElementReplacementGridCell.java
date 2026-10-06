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

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementReplacementGridCell extends JRExporterGridCell
{
	
	private final ElementGridCell originalCell;
	private final JRPrintElement element;
	
	public ElementReplacementGridCell(ElementGridCell originalCell, JRPrintElement element)
	{
		super(originalCell.getStyle());
		
		this.originalCell = originalCell;
		this.element = element;
	}

	@Override
	public GridCellSize getSize()
	{
		return originalCell.getSize();
	}

	@Override
	public byte getType()
	{
		return JRExporterGridCell.TYPE_ELEMENT_CELL;
	}

	@Override
	public JRPrintElement getElement()
	{
		return element;
	}

	@Override
	public String getElementAddress()
	{
		return originalCell.getElementAddress();
	}

	@Override
	public String getProperty(String propName)
	{
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(propName))
		{
			return element.getPropertiesMap().getProperty(propName);
		}

		return originalCell.getProperty(propName);
	}

	@Override
	public void setBox(JRLineBox box)
	{
		GridCellStyle newStyle = originalCell.getContainer().cellStyle(getBackcolor(), getForecolor(), box);
		setStyle(newStyle);
	}

}
