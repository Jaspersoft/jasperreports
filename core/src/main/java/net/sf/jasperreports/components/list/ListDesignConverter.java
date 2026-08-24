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
package net.sf.jasperreports.components.list;

import java.util.List;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintFrame;
import net.sf.jasperreports.engine.component.ComponentDesignConverter;
import net.sf.jasperreports.engine.convert.ConvertVisitor;
import net.sf.jasperreports.engine.convert.ReportConverter;
import net.sf.jasperreports.engine.type.ModeEnum;

/**
 * List preview converter.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ListDesignConverter implements ComponentDesignConverter
{

	@Override
	public JRPrintElement convert(ReportConverter reportConverter,
			JRComponentElement element)
	{
		ListComponent list = (ListComponent) element.getComponent();
		if (list == null || list.getContents() == null)
		{
			return null;
		}
		
		JRBasePrintFrame frame = new JRBasePrintFrame(
				reportConverter.getDefaultStyleProvider());
		reportConverter.copyBaseAttributes(element, frame);
		
		ListContents contents = list.getContents();
		if (contents.getHeight() > 0)
		{
			JRBasePrintFrame contentsFrame = new JRBasePrintFrame(
					reportConverter.getDefaultStyleProvider());
			contentsFrame.setUUID(element.getUUID());
			contentsFrame.setX(0);
			contentsFrame.setY(0);
			contentsFrame.setWidth(element.getWidth());			
			contentsFrame.setHeight(contents.getHeight());
			contentsFrame.setMode(ModeEnum.TRANSPARENT);
			
			List<JRChild> children = contents.getChildren();
			if (children != null)
			{
				ConvertVisitor contentsVisitor =
					new ConvertVisitor(reportConverter, contentsFrame);
				for (JRChild child : children)
				{
					child.visit(contentsVisitor);
				}
			}
			
			frame.addElement(contentsFrame);

			/*
			Integer width = contents.getWidth();
			int contentsWidth = width == null ? element.getWidth() 
					: width;
			
			if (contents.getHeight() < element.getHeight() 
					|| contentsWidth < element.getWidth())
			{
				// add a grey rectangle to highlight the contents height
				JRBasePrintImage image = new JRBasePrintImage(
						reportConverter.getDefaultStyleProvider());
				image.setX(0);
				image.setWidth(element.getWidth());
				image.setY(0);
				image.setHeight(element.getHeight());
				image.setMode(ModeEnum.TRANSPARENT);
				
				// clip out the list contents area
				Area clip = new Area(new Rectangle(
						0, 0, element.getWidth(), element.getHeight()));
				clip.subtract(new Area(new Rectangle(
						0, 0, contentsWidth, contents.getHeight())));
				
				image.setRenderer(new UnusedSpaceImageRenderer(clip));
				frame.addElement(image);
			}
			*/
		}
		
		return frame;
	}

}
