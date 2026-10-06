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
import net.sf.jasperreports.engine.ReturnValue;

/**
 * Implementation of {@link net.sf.jasperreports.engine.ReturnValue ReturnValue}
 * to be used for report design purposes.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DesignReturnValue extends DesignCommonReturnValue implements ReturnValue
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String PROPERTY_FROM_VARIABLE = "fromVariable";

	/**
	 * The name of the variable to be copied.
	 */
	protected String fromVariable;

	/**
	 * Returns the name of the variable whose value should be copied.
	 * 
	 * @return the name of the variable whose value should be copied.
	 */
	@Override
	public String getFromVariable()
	{
		return this.fromVariable;
	}

	/**
	 * Sets the source variable name.
	 * 
	 * @param name the variable name
	 * @see net.sf.jasperreports.engine.ReturnValue#getFromVariable()
	 */
	public void setFromVariable(String name)
	{
		Object old = this.fromVariable;
		this.fromVariable = name;
		getEventSupport().firePropertyChange(PROPERTY_FROM_VARIABLE, old, this.fromVariable);
	}
	
	@Override
	public Object clone()
	{
		return super.clone();
	}
}
