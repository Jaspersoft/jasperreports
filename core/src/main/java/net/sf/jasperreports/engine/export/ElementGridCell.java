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
package net.sf.jasperreports.engine.export;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRRuntimeException;

	
	
/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ElementGridCell extends JRExporterGridCell
{
	private static final String EXCEPTION_MESSAGE_KEY_NOT_FRAME_ELEMENT = "export.common.grid.cell.not.frame.element";
	
	private GridCellSize size;

	// TODO lucianc do not keep a reference to the container here but require exporters to use the cell with the container
	private JRGridLayout container;

	private PrintElementIndex parentIndex;
	private int elementIndex;

	// used only by XLSX exporter for now
	private Integer styleIndex;

	/**
	 *
	 */
	public ElementGridCell(
		JRGridLayout container,
		PrintElementIndex parentIndex,
		int elementIndex,
		GridCellSize size
		)
	{
		this.size = size;
		
		this.container = container;
		this.parentIndex = parentIndex;
		this.elementIndex = elementIndex;
	}

	@Override
	public GridCellSize getSize()
	{
		return size;
	}

	@Override
	public byte getType()
	{
		return TYPE_ELEMENT_CELL;
	}
	
	@Override
	public JRPrintElement getElement()
	{
		return container.getElement(parentIndex, elementIndex);
	}
	
	@Override
	public String getProperty(String propName)
	{
		JRPrintElement element = getElement();
		if (element.hasProperties()
				&& element.getPropertiesMap().containsProperty(propName))
		{
			return element.getPropertiesMap().getProperty(propName);
		}

		PrintElementIndex ancestorIndex = parentIndex;
		while (ancestorIndex != null)
		{
			JRPrintElement ancestor = container.getElement(
					ancestorIndex.getParentIndex(), ancestorIndex.getIndex());
			if (ancestor.hasProperties()
					&& ancestor.getPropertiesMap().containsProperty(propName))
			{
				return ancestor.getPropertiesMap().getProperty(propName);
			}
			
			ancestorIndex = ancestorIndex.getParentIndex();
		}
			
		return null;
	}

	public PrintElementIndex getParentIndex()
	{
		return parentIndex;
	}

	public int getElementIndex()
	{
		return elementIndex;
	}

	@Override
	public String getElementAddress()
	{
		return PrintElementIndex.asAddress(parentIndex, elementIndex);
	}

	public JRGridLayout getLayout()
	{
		JRPrintElement element = getElement();
		if (!(element instanceof JRPrintFrame))
		{
			// should not happen
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_NOT_FRAME_ELEMENT,
					new Object[]{getElementAddress()});
		}
		
		JRPrintFrame frame = (JRPrintFrame) element;
		PrintElementIndex frameIndex = new PrintElementIndex(getParentIndex(), getElementIndex());
		return new JRGridLayout(
				container, 
				frame.getElements(),
				frame.getWidth(), 
				frame.getHeight(),
				0, //offsetX
				0, //offsetY
				frameIndex
		);
	}

	@Override
	public void setBox(JRLineBox box)
	{
		GridCellStyle newStyle = container.cellStyle(getBackcolor(), getForecolor(), box);
		setStyle(newStyle);
	}

	protected JRGridLayout getContainer()
	{
		return container;
	}

	/**
	 * 	Used only by XLSX exporter for now.
	 */
	public void setStyleIndex(Integer styleIndex)
	{
		this.styleIndex = styleIndex;
	}

	/**
	 * 	Used only by XLSX exporter for now.
	 */
	public Integer getStyleIndex()
	{
		return styleIndex;
	}
}
