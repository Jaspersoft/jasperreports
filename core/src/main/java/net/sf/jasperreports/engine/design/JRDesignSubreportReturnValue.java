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
import net.sf.jasperreports.engine.base.JRBaseSubreportReturnValue;
import net.sf.jasperreports.engine.design.events.JRChangeEventsSupport;
import net.sf.jasperreports.engine.design.events.JRPropertyChangeSupport;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * Implementation of {@link net.sf.jasperreports.engine.JRSubreportReturnValue JRSubreportReturnValue}
 * to be used for report design purposes.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignSubreportReturnValue extends JRBaseSubreportReturnValue implements JRChangeEventsSupport // do not extend DesignCommonReturnValue to avoid deserialization field issues
{

	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_CALCULATION = "calculation";
	
	public static final String PROPERTY_INCREMENTER_FACTORY_CLASS_NAME = "incrementerFactoryClassName";
	
	public static final String PROPERTY_SUBREPORT_VARIABLE = "subreportVariable";
	
	public static final String PROPERTY_TO_VARIABLE = "toVariable";

	/**
	 * Sets the subreport variable name.
	 * 
	 * @param name the variable name
	 * @see net.sf.jasperreports.engine.JRSubreportReturnValue#getFromVariable()
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_subreportVariable)
	public void setSubreportVariable(String name)
	{
		Object old = this.subreportVariable;
		this.subreportVariable = name;
		getEventSupport().firePropertyChange(PROPERTY_SUBREPORT_VARIABLE, old, this.subreportVariable);
	}

	/**
	 * Sets the master variable name.
	 * 
	 * @param name the variable name
	 * @see net.sf.jasperreports.engine.JRSubreportReturnValue#getToVariable()
	 */
	public void setToVariable(String name)
	{
		Object old = this.toVariable;
		this.toVariable = name;
		getEventSupport().firePropertyChange(PROPERTY_TO_VARIABLE, old, this.toVariable);
	}

	/**
	 * Sets the calculation type.
	 * 
	 * @param calculation the calculation type
	 * @see net.sf.jasperreports.engine.JRSubreportReturnValue#getCalculation()
	 */
	public void setCalculation(CalculationEnum calculation)
	{
		CalculationEnum old = this.calculation;
		this.calculation = calculation;
		getEventSupport().firePropertyChange(PROPERTY_CALCULATION, old, this.calculation);
	}
	
	/**
	 * Sets the incrementer factory class name.
	 * 
	 * @param incrementerFactoryClassName the name of the incrementer factory class
	 * @see net.sf.jasperreports.engine.JRSubreportReturnValue#getIncrementerFactoryClassName()
	 */
	/*
	 * JACKSON-TIP
	 * Without this setter annotation here, the value of the field would not be serialized in xml unless the getter specifies a localName for the xml annotation.
	 * Note that this does not have anything to do with the fact that the corresponding field in the super class called incrementerFactoryClassName is marked with JsonIgnore or not.
	 * That field gets serialized in XML unless hidden by this named setter or by a JsonIgnore annotation attached to it.
	 * Similar cases exist across all object model, for example the valueClassName field in the JRVariable interface.
	 */
	@JsonSetter(JRXmlConstants.ATTRIBUTE_incrementerFactoryClass)
	public void setIncrementerFactoryClassName(String incrementerFactoryClassName)
	{
		Object old = this.incrementerFactoryClassName;
		this.incrementerFactoryClassName = incrementerFactoryClassName;
		getEventSupport().firePropertyChange(PROPERTY_INCREMENTER_FACTORY_CLASS_NAME, old, this.incrementerFactoryClassName);
	}
	
	@Override
	public Object clone()
	{
		JRDesignSubreportReturnValue clone = (JRDesignSubreportReturnValue)super.clone();
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
