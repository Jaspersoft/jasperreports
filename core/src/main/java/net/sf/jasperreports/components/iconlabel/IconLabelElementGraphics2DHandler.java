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
package net.sf.jasperreports.components.iconlabel;

import java.awt.Graphics2D;

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.base.JRBasePrintFrame;
import net.sf.jasperreports.engine.export.GenericElementGraphics2DHandler;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporterContext;
import net.sf.jasperreports.engine.export.draw.FrameDrawer;
import net.sf.jasperreports.engine.export.draw.Offset;

/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public class IconLabelElementGraphics2DHandler implements GenericElementGraphics2DHandler
{
	private static final IconLabelElementGraphics2DHandler INSTANCE = new IconLabelElementGraphics2DHandler();
	
	public static IconLabelElementGraphics2DHandler getInstance()
	{
		return INSTANCE;
	}
	
	private IconLabelElementGraphics2DHandler()
	{
	}

	@Override
	public void exportElement(
			JRGraphics2DExporterContext exporterContext, 
			JRGenericPrintElement element, 
			Graphics2D grx, 
			Offset offset)
	{
		try
		{
			JRPrintText labelPrintText = (JRPrintText)element.getParameterValue(IconLabelElement.PARAMETER_LABEL_TEXT_ELEMENT);
			if (labelPrintText == null) //FIXMEINPUT deal with xml serialization
			{
				return;
			}
			
			JRBasePrintFrame frame = new JRBasePrintFrame(element.getDefaultStyleProvider());
			frame.setX(element.getX());
			frame.setY(element.getY());
			frame.setWidth(element.getWidth());
			frame.setHeight(element.getHeight());
			frame.setStyle(element.getStyle());
			frame.setBackcolor(element.getBackcolor());
			frame.setForecolor(element.getForecolor());
			frame.setMode(element.getMode());
			JRLineBox lineBox = (JRLineBox)element.getParameterValue(IconLabelElement.PARAMETER_LINE_BOX);
			if (lineBox != null)
			{
				frame.copyBox(lineBox);
			}
			
			frame.addElement(labelPrintText);
			
			JRPrintText iconPrintText = (JRPrintText)element.getParameterValue(IconLabelElement.PARAMETER_ICON_TEXT_ELEMENT);
			if (iconPrintText != null) //FIXMEINPUT deal with xml serialization
			{
				frame.addElement(iconPrintText);
			}

			JRGraphics2DExporter exporter = (JRGraphics2DExporter)exporterContext.getExporterRef();
			
			FrameDrawer frameDrawer = exporter.getDrawVisitor().getFrameDrawer();
			frameDrawer.draw(
				grx, 
				frame, 
				offset.getX(), 
				offset.getY()
				);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean toExport(JRGenericPrintElement element) 
	{
		return true;
	}

}
