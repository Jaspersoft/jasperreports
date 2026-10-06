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
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.type.SplitTypeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class CompiledRow implements Row, Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private JRExpression printWhenExpression;
	private SplitTypeEnum splitType;

	public CompiledRow()
	{
		super();
	}

	public CompiledRow(Row row, JRBaseObjectFactory factory)
	{
		this.printWhenExpression = factory.getExpression(row.getPrintWhenExpression());
		this.splitType = row.getSplitType();
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return printWhenExpression;
	}

	@Override
	public SplitTypeEnum getSplitType()
	{
		return splitType;
	}

	@Override
	public Object clone() 
	{
		CompiledRow clone = null;

		try
		{
			clone = (CompiledRow)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
		
		clone.printWhenExpression = JRCloneUtils.nullSafeClone(printWhenExpression);
		
		return clone;
	}

}
