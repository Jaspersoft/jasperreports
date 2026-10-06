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

import com.fasterxml.jackson.annotation.JsonSetter;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.base.JRBaseGenericElementParameter;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * A implementation of {@link JRBaseGenericElementParameter} that is to be used at report
 * design time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignGenericElementParameter extends JRBaseGenericElementParameter implements JRChangeEventsSupport
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_VALUE_EXPRESSION = "valueExpression";
	public static final String PROPERTY_SKIP_WHEN_EMPTY = "skipWhenEmpty";
	
	@Override
	public Object clone()
	{
		JRDesignGenericElementParameter clone = (JRDesignGenericElementParameter)super.clone();
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

	/**
	 * Sets the parameter name.
	 * 
	 * @param name the parameter name
	 * @see #getName()
	 */
	public void setName(String name)
	{
		Object old = this.name;
		this.name = name;
		getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
	}

	/**
	 * Sets the parameter's value expression.
	 * 
	 * @param valueExpression the value expression.
	 * @see #getValueExpression()
	 */
	@JsonSetter(JRXmlConstants.ELEMENT_expression)
	public void setValueExpression(JRExpression valueExpression)
	{
		Object old = this.valueExpression;
		this.valueExpression = valueExpression;
		getEventSupport().firePropertyChange(PROPERTY_VALUE_EXPRESSION, 
				old, this.valueExpression);
	}

	/**
	 * Sets the flag that determines whether the parameter is to be skipped
	 * when its value is <code>null</code>.
	 * 
	 * @param skipWhenEmpty whether parameter with <code>null</code> ar to be
	 * skipped
	 * @see #isSkipWhenEmpty()
	 */
	public void setSkipWhenEmpty(boolean skipWhenEmpty)
	{
		boolean old = this.skipWhenEmpty;
		this.skipWhenEmpty = skipWhenEmpty;
		getEventSupport().firePropertyChange(PROPERTY_SKIP_WHEN_EMPTY, 
				old, this.skipWhenEmpty);
	}

}
