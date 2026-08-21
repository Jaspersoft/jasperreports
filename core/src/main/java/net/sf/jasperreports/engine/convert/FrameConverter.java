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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.convert;

import java.util.List;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.base.JRBasePrintFrame;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FrameConverter extends ElementConverter
{

	/**
	 *
	 */
	private final static FrameConverter INSTANCE = new FrameConverter();
	
	/**
	 *
	 */
	protected FrameConverter()
	{
	}

	/**
	 *
	 */
	public static ElementConverter getInstance()
	{
		return INSTANCE;
	}
	
	@Override
	public JRPrintElement convert(ReportConverter reportConverter, JRElement element)
	{
		JRBasePrintFrame printFrame = new JRBasePrintFrame(reportConverter.getDefaultStyleProvider());
		JRFrame frame = (JRFrame)element; 
		
		copyElement(reportConverter, frame, printFrame);
		
		printFrame.copyBox(frame.getLineBox());
		
		List<JRChild> children = frame.getChildren();
		if (children != null && children.size() > 0)
		{
			ConvertVisitor convertVisitor = new ConvertVisitor(reportConverter, printFrame);
			for (int i = 0; i < children.size(); i++)
			{
				children.get(i).visit(convertVisitor);
			}
		}
		
		return printFrame;
	}

}
