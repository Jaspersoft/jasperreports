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

import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.type.BreakTypeEnum;


/**
 * The actual implementation of a break element.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseBreak extends JRBaseElement implements JRBreak
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_TYPE = "type";

	protected BreakTypeEnum type;


	/**
	 * Initializes properties that are specific to break elements. Common properties are initialized by its
	 * parent constructors.
	 * @param breakElement an element whose properties are copied to this element. Usually it is a
	 * {@link net.sf.jasperreports.engine.design.JRDesignBreak} that must be transformed into an
	 * <tt>JRBaseBreak</tt> at compile time.
	 * @param factory a factory used in the compile process
	 */
	protected JRBaseBreak(JRBreak breakElement, JRBaseObjectFactory factory)
	{
		super(breakElement, factory);
		
		type = breakElement.getType();
	}
		

	@Override
	public int getX()
	{
		return 0;
	}

	@Override
	public int getHeight()
	{
		return 1;
	}

	@Override
	public BreakTypeEnum getType()
	{
		return this.type;
	}

	@Override
	public void setType(BreakTypeEnum type)
	{
		Object old = this.type;
		this.type = type;
		getEventSupport().firePropertyChange(PROPERTY_TYPE, old, this.type);
	}

	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		collector.collect(this);
	}

	@Override
	public void visit(JRVisitor visitor)
	{
		visitor.visitBreak(this);
	}
}
