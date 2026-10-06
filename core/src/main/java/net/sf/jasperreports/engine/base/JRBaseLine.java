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
package net.sf.jasperreports.engine.base;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.type.LineDirectionEnum;


/**
 * The actual implementation of a graphic element representing a straight line.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseLine extends JRBaseGraphicElement implements JRLine
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_DIRECTION = "direction";

	/**
	 *
	 */
	protected LineDirectionEnum direction;

	/**
	 * Initializes properties that are specific to lines. Common properties are initialized by its
	 * parent constructors.
	 * @param line an element whose properties are copied to this element. Usually it is a
	 * {@link net.sf.jasperreports.engine.design.JRDesignLine} that must be transformed into an
	 * <tt>JRBaseLine</tt> at compile time.
	 * @param factory a factory used in the compile process
	 */
	protected JRBaseLine(JRLine line, JRBaseObjectFactory factory)
	{
		super(line, factory);
		
		direction = line.getDirection();
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
	public LineDirectionEnum getDirection()
	{
		return direction;
	}

	@Override
	public void setDirection(LineDirectionEnum direction)
	{
		LineDirectionEnum old = this.direction;
		this.direction = direction;
		getEventSupport().firePropertyChange(PROPERTY_DIRECTION, old, this.direction);
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitLine(this);
	}
}
