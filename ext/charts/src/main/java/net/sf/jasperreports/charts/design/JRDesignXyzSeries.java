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
package net.sf.jasperreports.charts.design;

import net.sf.jasperreports.charts.base.JRBaseXyzSeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;

/**
 * @author Flavius Sana (flavius_sana@users.sourceforge.net)
 */
public class JRDesignXyzSeries extends JRBaseXyzSeries implements JRChangeEventsSupport {
	
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_ITEM_HYPERLINK = "itemHyperlink";
	
	public static final String PROPERTY_SERIES_EXPRESSION = "seriesExpression";
	
	public static final String PROPERTY_X_VALUE_EXPRESSION = "xValueExpression";
	
	public static final String PROPERTY_Y_VALUE_EXPRESSION = "yValueExpression";
	
	public static final String PROPERTY_Z_VALUE_EXPRESSION = "zValueExpression";
	
	public void setSeriesExpression( JRExpression seriesExpression ){
		Object old = this.seriesExpression;
		this.seriesExpression = seriesExpression;
		getEventSupport().firePropertyChange(PROPERTY_SERIES_EXPRESSION, old, this.seriesExpression);
	}

	public void setXValueExpression( JRExpression xValueExpression ){
		Object old = this.xValueExpression;
		this.xValueExpression = xValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_X_VALUE_EXPRESSION, old, this.xValueExpression);
	}
	
	public void setYValueExpression( JRExpression yValueExpression ){
		Object old = this.yValueExpression;
		this.yValueExpression = yValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_Y_VALUE_EXPRESSION, old, this.yValueExpression);
	}
	
	public void setZValueExpression( JRExpression zValueExpression ){
		Object old = this.zValueExpression;
		this.zValueExpression = zValueExpression;
		getEventSupport().firePropertyChange(PROPERTY_Z_VALUE_EXPRESSION, old, this.zValueExpression);
	}

	/**
	 * Sets the hyperlink specification for chart items.
	 * 
	 * @param itemHyperlink the hyperlink specification
	 * @see #getItemHyperlink()
	 */
	public void setItemHyperlink(JRHyperlink itemHyperlink)
	{
		Object old = this.itemHyperlink;
		this.itemHyperlink = itemHyperlink;
		getEventSupport().firePropertyChange(PROPERTY_ITEM_HYPERLINK, old, this.itemHyperlink);
	}
	
	@Override
	public Object clone()
	{
		JRDesignXyzSeries clone = (JRDesignXyzSeries)super.clone();
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
