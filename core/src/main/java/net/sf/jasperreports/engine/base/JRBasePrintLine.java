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
package net.sf.jasperreports.engine.base;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.type.LineDirectionEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBasePrintLine extends JRBasePrintGraphicElement implements JRPrintLine
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	protected LineDirectionEnum direction;


	/**
	 *
	 */
	public JRBasePrintLine(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);
	}


	@Override
	public void setWidth(int width)
	{
		if (width == 0)
		{
			width = 1;
		}
		
		super.setWidth(width);
	}

	@Override
	public void setHeight(int height)
	{
		if (height == 0)
		{
			height = 1;
		}
		
		super.setHeight(height);
	}

	@Override
	public LineDirectionEnum getDirection()
	{
		return this.direction;
	}

	@Override
	public void setDirection(LineDirectionEnum direction)
	{
		this.direction = direction;
	}

	@Override
	public <T> void accept(PrintElementVisitor<T> visitor, T arg)
	{
		visitor.visit(this, arg);
	}
}
