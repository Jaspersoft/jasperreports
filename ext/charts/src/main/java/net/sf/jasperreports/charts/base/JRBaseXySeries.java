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
package net.sf.jasperreports.charts.base;

import java.io.Serializable;

import net.sf.jasperreports.charts.JRXySeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBaseXySeries implements JRXySeries, Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	protected JRExpression seriesExpression;
	protected JRExpression xValueExpression;
	protected JRExpression yValueExpression;
	protected JRExpression labelExpression;
	protected JRHyperlink itemHyperlink;
	protected Boolean autoSort;

	
	/**
	 *
	 */
	protected JRBaseXySeries()
	{
	}
	
	
	/**
	 *
	 */
	public JRBaseXySeries(JRXySeries xySeries, ChartsBaseObjectFactory factory)
	{
		JRBaseObjectFactory parentFactory = factory.getParent();
		parentFactory.put(xySeries, this);

		seriesExpression = parentFactory.getExpression(xySeries.getSeriesExpression());
		xValueExpression = parentFactory.getExpression(xySeries.getXValueExpression());
		yValueExpression = parentFactory.getExpression(xySeries.getYValueExpression());
		labelExpression = parentFactory.getExpression(xySeries.getLabelExpression());
		itemHyperlink = parentFactory.getHyperlink(xySeries.getItemHyperlink());
		autoSort = xySeries.getAutoSort();
	}

	
	@Override
	public JRExpression getSeriesExpression()
	{
		return seriesExpression;
	}
		
	@Override
	public JRExpression getXValueExpression()
	{
		return xValueExpression;
	}
		
	@Override
	public JRExpression getYValueExpression()
	{
		return yValueExpression;
	}
		
	@Override
	public JRExpression getLabelExpression()
	{
		return labelExpression;
	}

	
	@Override
	public JRHyperlink getItemHyperlink()
	{
		return itemHyperlink;
	}
	
	@Override
	public Boolean getAutoSort()
	{
		return autoSort;
	}
	
	@Override
	public Object clone() 
	{
		JRBaseXySeries clone = null;
		
		try
		{
			clone = (JRBaseXySeries)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.seriesExpression = JRCloneUtils.nullSafeClone(seriesExpression);
		clone.xValueExpression = JRCloneUtils.nullSafeClone(xValueExpression);
		clone.yValueExpression = JRCloneUtils.nullSafeClone(yValueExpression);
		clone.labelExpression = JRCloneUtils.nullSafeClone(labelExpression);
		clone.itemHyperlink = JRCloneUtils.nullSafeClone(itemHyperlink);
		
		return clone;
	}
}
