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
package net.sf.jasperreports.components.table;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StandardRow implements Row, Serializable, JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	public static final String PROPERTY_PRINT_WHEN_EXPRESSION = "printWhenExpression";
	public static final String PROPERTY_splitType = "splitType";
	
	private JRExpression printWhenExpression;
	private SplitTypeEnum splitType;

	public StandardRow()
	{
	}

	public StandardRow(BaseColumn column, ColumnFactory factory)
	{
		this.printWhenExpression = factory.getBaseObjectFactory().getExpression(
				column.getPrintWhenExpression());
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return printWhenExpression;
	}

	public void setPrintWhenExpression(JRExpression printWhenExpression)
	{
		Object old = this.printWhenExpression;
		this.printWhenExpression = printWhenExpression;
		getEventSupport().firePropertyChange(PROPERTY_PRINT_WHEN_EXPRESSION, 
				old, this.printWhenExpression);
	}
	
	@Override
	public SplitTypeEnum getSplitType()
	{
		return splitType;
	}

	public void setSplitType(SplitTypeEnum splitType)
	{
		SplitTypeEnum old = this.splitType;
		this.splitType = splitType;
		getEventSupport().firePropertyChange(PROPERTY_splitType, old, this.splitType);
	}

	@Override
	public Object clone()
	{
		StandardRow clone = null;
		try
		{
			clone = (StandardRow) super.clone();
		} 
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.printWhenExpression = JRCloneUtils.nullSafeClone(printWhenExpression);
		clone.eventSupport = null;
		return clone;
	}

	private transient JRPropertyChangeSupport eventSupport;
	
	@Override
	public JRPropertyChangeSupport getEventSupport()
	{
		synchronized (this)
		{
			if (eventSupport == null)
			{
				eventSupport = new JRPropertyChangeSupport(this);
			}
		}
		
		return eventSupport;
	}
}
