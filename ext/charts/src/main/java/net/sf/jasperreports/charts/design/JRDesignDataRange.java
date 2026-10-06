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

import com.fasterxml.jackson.annotation.JsonCreator;

import net.sf.jasperreports.charts.JRDataRange;
import net.sf.jasperreports.charts.base.JRBaseDataRange;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;

/**
 * Contains a range of values.  Used to specify the set of acceptable values
 * for a meter or thermometer, and to divide those charts up into subsections.
 *
 * @author Barry Klawans (bklawans@users.sourceforge.net)
 */
public class JRDesignDataRange extends JRBaseDataRange implements JRChangeEventsSupport
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_HIGH_EXPRESSION = "highExpression";
	
	public static final String PROPERTY_LOW_EXPRESSION = "lowExpression";


	@JsonCreator
	private JRDesignDataRange()
	{
		this(null);
	}

	/**
	 * Constructs a new data range that is a copy of an existing one.
	 *
	 * @param dataRange the range to copy
	 */
	public JRDesignDataRange(JRDataRange dataRange)
	{
		super(dataRange);
	}

	/**
	 * Sets the low expression of the range.  The low expression is
	 * evaluted to get the lower bound of the range.
	 *
	 * @param lowExpression used to get the lower bound of the range
	 */
	public void setLowExpression(JRExpression lowExpression)
	{
		Object old = this.lowExpression;
		this.lowExpression = lowExpression;
		getEventSupport().firePropertyChange(PROPERTY_LOW_EXPRESSION, old, this.lowExpression);
	}

	/**
	 * Sets the high expression of the range.  The high expression is
	 * used to get the upper bound of the range.
	 *
	 * @param highExpression used to get the upper bound of the range
	 */
	public void setHighExpression(JRExpression highExpression)
	{
		Object old = this.highExpression;
		this.highExpression = highExpression;
		getEventSupport().firePropertyChange(PROPERTY_HIGH_EXPRESSION, old, this.highExpression);
	}
	
	@Override
	public Object clone() 
	{
		JRDesignDataRange clone = (JRDesignDataRange)super.clone();
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
