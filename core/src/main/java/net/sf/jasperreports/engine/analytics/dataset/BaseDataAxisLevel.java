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
package net.sf.jasperreports.engine.analytics.dataset;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseDataAxisLevel implements DataAxisLevel, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	protected String name;
	protected JRExpression labelExpression;
	protected DataLevelBucket bucket;
	
	public BaseDataAxisLevel()
	{
	}
	
	public BaseDataAxisLevel(DataAxisLevel level, JRBaseObjectFactory factory)
	{
		factory.put(level, this);
		
		this.name = level.getName();
		this.labelExpression = factory.getExpression(level.getLabelExpression());
		this.bucket = factory.getDataLevelBucket(level.getBucket());
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public JRExpression getLabelExpression()
	{
		return labelExpression;
	}

	@Override
	public DataLevelBucket getBucket()
	{
		return bucket;
	}
	
	@Override
	public Object clone() 
	{
		BaseDataAxisLevel clone = null;
		try
		{
			clone = (BaseDataAxisLevel) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// never
			throw new JRRuntimeException(e);
		}
		
		clone.labelExpression = JRCloneUtils.nullSafeClone(labelExpression);
		clone.bucket = JRCloneUtils.nullSafeClone(bucket);
		return clone;
	}

}
