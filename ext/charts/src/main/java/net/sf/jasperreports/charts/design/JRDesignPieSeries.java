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

import net.sf.jasperreports.charts.base.JRBasePieSeries;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRHyperlink;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRDesignPieSeries extends JRBasePieSeries implements JRChangeEventsSupport
{


	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_KEY_EXPRESSION = "keyExpression";
	
	public static final String PROPERTY_SECTION_HYPERLINK = "sectionHyperlink";
	
	public static final String PROPERTY_LABEL_EXPRESSION = "labelExpression";
	
	public static final String PROPERTY_VALUE_EXPRESSION = "valueExpression";

	
	/**
	 *
	 */
	public void setKeyExpression(JRExpression keyExpression)
	{
		Object old = this.keyExpression;
		this.keyExpression = keyExpression;
		getEventSupport().firePropertyChange(PROPERTY_KEY_EXPRESSION, old, this.keyExpression);
	}

	/**
	 *
	 */
	public void setValueExpression(JRExpression valueExpression)
	{
		Object old = this.valueExpression;
		this.valueExpression = valueExpression;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_EXPRESSION, old, this.valueExpression);
	}

	/**
	 *
	 */
	public void setLabelExpression(JRExpression labelExpression)
	{
		Object old = this.labelExpression;
		this.labelExpression = labelExpression;
		getEventSupport().firePropertyChange(PROPERTY_LABEL_EXPRESSION, old, this.labelExpression);
	}


	/**
	 * Sets the hyperlink specification for pie chart sections.
	 * 
	 * @param sectionHyperlink the hyperlink specification
	 * @see #getSectionHyperlink()
	 */
	public void setSectionHyperlink(JRHyperlink sectionHyperlink)
	{
		Object old = this.sectionHyperlink;
		this.sectionHyperlink = sectionHyperlink;
		getEventSupport().firePropertyChange(PROPERTY_SECTION_HYPERLINK, old, this.sectionHyperlink);
	}

	@Override
	public Object clone()
	{
		JRDesignPieSeries clone = (JRDesignPieSeries)super.clone();
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
