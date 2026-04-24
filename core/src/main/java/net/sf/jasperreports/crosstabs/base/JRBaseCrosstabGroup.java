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
package net.sf.jasperreports.crosstabs.base;

import java.io.Serializable;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstabBucket;
import net.sf.jasperreports.crosstabs.JRCrosstabGroup;
import net.sf.jasperreports.crosstabs.type.CrosstabTotalPositionEnum;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * Base read-only implementation for crosstab row and column groups.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRBaseCrosstabGroup implements JRCrosstabGroup, Serializable, CrosstabBaseCloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	protected String name;
	protected CrosstabTotalPositionEnum totalPosition;
	protected JRCrosstabBucket bucket;
	
	protected JRCellContents header;
	protected JRCellContents totalHeader;
	protected Boolean mergeHeaderCells;

	protected JRVariable variable;
	
	protected JRBaseCrosstabGroup()
	{
	}

	public JRBaseCrosstabGroup(JRCrosstabGroup group, JRBaseObjectFactory factory)
	{
		factory.put(group, this);
		
		this.name = group.getName();
		this.totalPosition = group.getTotalPosition();
		this.bucket = factory.getCrosstabBucket(group.getBucket());
		
		this.header = factory.getCell(group.getHeader());
		this.totalHeader = factory.getCell(group.getTotalHeader());
		this.mergeHeaderCells = group.getMergeHeaderCells();
		
		this.variable = factory.getVariable(group.getVariable());
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public JRCrosstabBucket getBucket()
	{
		return bucket;
	}

	@Override
	public CrosstabTotalPositionEnum getTotalPosition()
	{
		return totalPosition;
	}

	@Override
	public boolean hasTotal()
	{
		return CrosstabTotalPositionEnum.getValueOrDefault(totalPosition) != CrosstabTotalPositionEnum.NONE;
	}

	@Override
	public JRCellContents getHeader()
	{
		return header;
	}

	@Override
	public JRCellContents getTotalHeader()
	{
		return totalHeader;
	}

	@Override
	public JRVariable getVariable()
	{
		return variable;
	}

	@Override
	public Boolean getMergeHeaderCells()
	{
		return mergeHeaderCells;
	}

	@Override
	public Object clone() 
	{
		JRBaseCrosstabGroup clone = null;
		try
		{
			clone = (JRBaseCrosstabGroup) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		clone.bucket = JRCloneUtils.nullSafeClone(bucket);
		clone.header = JRCloneUtils.nullSafeClone(header);
		clone.totalHeader = JRCloneUtils.nullSafeClone(totalHeader);
		clone.variable = JRCloneUtils.nullSafeClone(variable);
		return clone;
	}

	@Override
	public JRCrosstabGroup clone(CrosstabBaseCloneFactory cloneFactory)
	{
		JRBaseCrosstabGroup clone = (JRBaseCrosstabGroup) clone();
		clone.variable = cloneFactory.clone(variable);
		return clone;
	}
}
