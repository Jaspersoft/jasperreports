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

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRElementDataset;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionCollector;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseElementDataset implements JRElementDataset, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected DatasetResetTypeEnum datasetResetType;
	protected IncrementTypeEnum incrementType;
	protected String resetGroup;
	protected String incrementGroup;
	
	protected JRDatasetRun datasetRun;
	protected JRExpression incrementWhenExpression;

	
	protected JRBaseElementDataset()
	{
	}

	
	/**
	 *
	 */
	protected JRBaseElementDataset(JRElementDataset dataset)
	{
		if (dataset != null) {
			datasetResetType = dataset.getDatasetResetType();
			incrementType = dataset.getIncrementType();
			resetGroup = dataset.getResetGroup();
			incrementGroup = dataset.getIncrementGroup();
			datasetRun = dataset.getDatasetRun();
			incrementWhenExpression = dataset.getIncrementWhenExpression();
		}
	}


	/**
	 *
	 */
	protected JRBaseElementDataset(JRElementDataset dataset, JRBaseObjectFactory factory)
	{
		factory.put(dataset, this);

		datasetResetType = dataset.getDatasetResetType();
		incrementType = dataset.getIncrementType();
		resetGroup = dataset.getResetGroup();
		incrementGroup = dataset.getIncrementGroup();
		
		datasetRun = factory.getDatasetRun(dataset.getDatasetRun());
		incrementWhenExpression = factory.getExpression(dataset.getIncrementWhenExpression());
	}

	
	@Override
	public DatasetResetTypeEnum getDatasetResetType()
	{
		return this.datasetResetType;
	}
		
	@Override
	public IncrementTypeEnum getIncrementType()
	{
		return this.incrementType;
	}
		
	@Override
	public String getResetGroup()
	{
		return resetGroup;
	}
		
	@Override
	public String getIncrementGroup()
	{
		return incrementGroup;
	}


	@Override
	public JRDatasetRun getDatasetRun()
	{
		return datasetRun;
	}


	@Override
	public JRExpression getIncrementWhenExpression()
	{
		return incrementWhenExpression;
	}

	
	@Override
	public void collectExpressions(JRExpressionCollector collector)
	{
		// expressions are collected in JRExpressionCollector directly
	}

	
	@Override
	public Object clone() 
	{
		JRBaseElementDataset clone = null;

		try
		{
			clone = (JRBaseElementDataset)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.incrementWhenExpression = JRCloneUtils.nullSafeClone(incrementWhenExpression);
		clone.datasetRun = JRCloneUtils.nullSafeClone(datasetRun);
		
		return clone;
	}
}
