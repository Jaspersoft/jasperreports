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

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;


/**
 * A report validation fault.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRVerifier
 */
public class JRValidationFault implements Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private String message;
	private Object source;
	
	/**
	 * Creates an empty fault.
	 */
	public JRValidationFault()
	{
	}

	/**
	 * Returns the fault message/description.
	 * 
	 * @return the fault description
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Sets the fault message/description.
	 * 
	 * @param message the description
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	/**
	 * Returns the fault source object.
	 * <p/>
	 * The source is the report object that dispalayed the fault.
	 * It can be, for instance, a (@link JRDesignVariable variable}, an {@link JRDesignExpression expression},
	 * or a {@link JRDesignElement report element}. 
	 * 
	 * @return the fault source
	 */
	public Object getSource()
	{
		return source;
	}
	
	/**
	 * Set the fault source.
	 * 
	 * @param source the fault source
	 */
	public void setSource(Object source)
	{
		this.source = source;
	}
	
}
