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
package net.sf.jasperreports.engine;

import java.io.IOException;
import java.io.Serializable;

import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;
import net.sf.jasperreports.engine.virtualization.VirtualizationSerializable;


/**
 * A parameter of the hyperlink associated to a print element.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRPrintHyperlinkParameter implements Serializable, VirtualizationSerializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String DEFAULT_VALUE_CLASS = java.lang.String.class.getName();
	
	private String name;
	private String valueClass = DEFAULT_VALUE_CLASS;
	private Object value;


	/**
	 * Creates a blank parameter.
	 */
	public JRPrintHyperlinkParameter()
	{
	}


	/**
	 * Creates a parameter and initializes its properties. 
	 * 
	 * @param name the parameter name
	 * @param valueClass the parameter value class
	 * @param value the parameter value
	 */
	public JRPrintHyperlinkParameter(final String name, final String valueClass, final Object value)
	{
		this.name = name;
		this.valueClass = valueClass;
		this.value = value;
	}
	
	
	/**
	 * Returns the parameter name.
	 * 
	 * @return the parameter name
	 * @see #setName(String)
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Returns the parameter value class name.
	 * 
	 * @return the parameter value class
	 * @see #setValueClass(String)
	 */
	public String getValueClass()
	{
		return valueClass;
	}	

	
	/**
	 * Returns the parameter value.
	 * 
	 * @return the parameter value
	 * @see #setValue(Object)
	 */
	public Object getValue()
	{
		return value;
	}

	
	/**
	 * Sets the parameter name.
	 * 
	 * @param name the name
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	
	/**
	 * Sets the parameter value class.
	 * 
	 * @param valueClass the value class name
	 */
	public void setValueClass(final String valueClass)
	{
		this.valueClass = valueClass;
	}

	
	/**
	 * Sets the parameter value.
	 * 
	 * @param value the value
	 */
	public void setValue(final Object value)
	{
		this.value = value;
	}


	@Override
	public void writeVirtualized(VirtualizationOutput out) throws IOException
	{
		out.writeJRObject(name);
		out.writeJRObject(valueClass);
		out.writeJRObject(value);
	}


	@Override
	public void readVirtualized(VirtualizationInput in) throws IOException
	{
		name = (String) in.readJRObject();
		valueClass = (String) in.readJRObject();
		value = in.readJRObject();
	}
	
}
