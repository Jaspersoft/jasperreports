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

import net.sf.jasperreports.charts.JRXyzSeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBaseObjectFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRBaseXyzSeries implements JRXyzSeries, Serializable {

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	protected JRExpression seriesExpression;
	protected JRExpression xValueExpression;
	protected JRExpression yValueExpression;
	protected JRExpression zValueExpression;
	protected JRHyperlink itemHyperlink;
	
	public JRBaseXyzSeries(){
	}
	
	public JRBaseXyzSeries( JRXyzSeries xyzSeries, ChartsBaseObjectFactory factory ){
		JRBaseObjectFactory parentFactory = factory.getParent();
		parentFactory.put( xyzSeries, this );
		
		seriesExpression = parentFactory.getExpression( xyzSeries.getSeriesExpression() );
		xValueExpression = parentFactory.getExpression( xyzSeries.getXValueExpression() );
		yValueExpression = parentFactory.getExpression( xyzSeries.getYValueExpression() );
		zValueExpression = parentFactory.getExpression( xyzSeries.getZValueExpression() );
		itemHyperlink = parentFactory.getHyperlink(xyzSeries.getItemHyperlink());
	}
	
	
	@Override
	public JRExpression getSeriesExpression(){
		return seriesExpression;
	}
	
	@Override
	public JRExpression getXValueExpression(){
		return xValueExpression;
	}
	
	@Override
	public JRExpression getYValueExpression(){
		return yValueExpression;
	}
	
	@Override
	public JRExpression getZValueExpression(){
		return zValueExpression;
	}
	
	@Override
	public JRHyperlink getItemHyperlink()
	{
		return itemHyperlink;
	}
	
	@Override
	public Object clone() 
	{
		JRBaseXyzSeries clone = null;
		
		try
		{
			clone = (JRBaseXyzSeries)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}

		clone.seriesExpression = JRCloneUtils.nullSafeClone(seriesExpression);
		clone.xValueExpression = JRCloneUtils.nullSafeClone(xValueExpression);
		clone.yValueExpression = JRCloneUtils.nullSafeClone(yValueExpression);
		clone.zValueExpression = JRCloneUtils.nullSafeClone(zValueExpression);
		clone.itemHyperlink = JRCloneUtils.nullSafeClone(itemHyperlink);
		
		return clone;
	}
}
