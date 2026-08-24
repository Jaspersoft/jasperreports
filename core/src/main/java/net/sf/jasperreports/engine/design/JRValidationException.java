/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRException;


/**
 * An exception that contains a list of {@link JRValidationFault report validation faults}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRVerifier
 */
public class JRValidationException extends JRException
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private final Collection<JRValidationFault> faults;

	/**
	 * Creates a validation exception containing a single fault.
	 * 
	 * @param message the validation message
	 * @param source the validation source
	 * @see JRValidationFault
	 */
	public JRValidationException(String message, Object source)
	{
		this(createFault(message, source));
	}
	
	private static JRValidationFault createFault(String message, Object source)
	{
		JRValidationFault fault = new JRValidationFault();
		fault.setMessage(message);
		fault.setSource(source);
		return fault;
	}

	/**
	 * Creates a validation exception containing a single fault.
	 * 
	 * @param fault the fault
	 */
	public JRValidationException(JRValidationFault fault)
	{
		this(Arrays.asList(new JRValidationFault[]{fault}));
	}
	
	/**
	 * Create an exception.
	 * 
	 * @param faults a list of {@link JRValidationFault validation faults}
	 */
	public JRValidationException(Collection<JRValidationFault> faults)
	{
		this(null, faults);
	}
	
	/**
	 * Create a validation exception.
	 * 
	 * @param message the message to be used as header for the exception message
	 * @param faults a list of {@link JRValidationFault validation faults}
	 */
	public JRValidationException(String message, Collection<JRValidationFault> faults)
	{
		super(appendMessages(message, faults));
		
		this.faults = faults;
	}
	
	/**
	 * Returns the list of {@link JRValidationFault validation faults}.
	 * 
	 * @return the list of {@link JRValidationFault JRValidationFault} instances.
	 */
	public Collection<JRValidationFault> getFaults()
	{
		return faults;
	}
	
	protected static String appendMessages(String header, Collection<JRValidationFault> faults)
	{
		if (header == null)
		{
			header = "Report design not valid : ";
		}

		StringBuilder sb = new StringBuilder();
		sb.append(header);
		int i = 1;
		for (Iterator<JRValidationFault> it = faults.iterator(); it.hasNext(); i++)
		{
			JRValidationFault fault = it.next();
			sb.append("\n\t " + i + ". " + fault.getMessage());
		}
		return sb.toString();
	}
}
