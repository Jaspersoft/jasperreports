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
package net.sf.jasperreports.engine.design;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRReportTemplate;
import net.sf.jasperreports.engine.base.JRBaseReportTemplate;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;


/**
 * {@link JRReportTemplate} implementation to be used at report design time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignReportTemplate extends JRBaseReportTemplate implements JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_SOURCE_EXPRESSION = "sourceExpression";

	/**
	 * Creates an empty report template.
	 */
	public JRDesignReportTemplate()
	{
		super();
	}

	/**
	 * Creates a report template for a template source expression.
	 */
	public JRDesignReportTemplate(JRExpression sourceExpression)
	{
		super();

		this.sourceExpression = sourceExpression;
	}
	
	/**
	 * Sets the template source expression.
	 * 
	 * @param sourceExpression the template source expression
	 * @see #getSourceExpression()
	 */
	public void setSourceExpression(JRExpression sourceExpression)
	{
		Object old = this.sourceExpression;
		this.sourceExpression = sourceExpression;
		getEventSupport().firePropertyChange(PROPERTY_SOURCE_EXPRESSION, old, this.sourceExpression);
	}
	
	@Override
	public Object clone()
	{
		JRDesignReportTemplate clone = (JRDesignReportTemplate)super.clone();
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
