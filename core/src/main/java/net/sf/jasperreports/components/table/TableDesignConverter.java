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
package net.sf.jasperreports.components.table;

import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.components.table.util.TableUtil;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintFrame;
import net.sf.jasperreports.engine.component.ComponentDesignConverter;
import net.sf.jasperreports.engine.convert.ConvertVisitor;
import net.sf.jasperreports.engine.convert.ReportConverter;

/**
 * Table preview converter.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class TableDesignConverter implements ComponentDesignConverter
{

	@Override
	public JRPrintElement convert(ReportConverter reportConverter,
			JRComponentElement element)
	{
		TableComponent table = (TableComponent) element.getComponent();
		if (table == null)// || list.getContents() == null)
		{
			return null;
		}
		
		JRBasePrintFrame frame = new JRBasePrintFrame(
				reportConverter.getDefaultStyleProvider());
		reportConverter.copyBaseAttributes(element, frame);
		
		TableUtil tableManager = 
			new TableUtil(
				table, 
				reportConverter.getReport()
				);
		
		Map<BaseCell, Rectangle> cellBounds = tableManager.getCellBounds();
		
		for (Map.Entry<BaseCell, Rectangle> pair : cellBounds.entrySet()) 
		{
			BaseCell cell = pair.getKey();
			Rectangle rectangle = pair.getValue(); 
			
			JRBasePrintFrame cellFrame = 
				new JRBasePrintFrame(
					reportConverter.getDefaultStyleProvider()
					);
			cellFrame.setX((int)rectangle.getX());
			cellFrame.setY((int)rectangle.getY());
			cellFrame.setWidth((int)rectangle.getWidth());			
			cellFrame.setHeight((int)rectangle.getHeight());
			cellFrame.setStyle(reportConverter.resolveStyle(cell));
			
			if (cell.getLineBox() != null)
			{
				cellFrame.copyBox(cell.getLineBox());
			}

			List<JRChild> children = cell.getChildren();
			if (children != null && children.size() > 0)
			{
				ConvertVisitor convertVisitor = new ConvertVisitor(reportConverter, cellFrame);
				for (JRChild child : children)
				{
					child.visit(convertVisitor);
				}
			}

			frame.addElement(cellFrame);
		}
		
		return frame;
	}

}
