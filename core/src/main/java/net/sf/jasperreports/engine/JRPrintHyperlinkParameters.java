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
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;
import net.sf.jasperreports.engine.virtualization.VirtualizationSerializable;


/**
 * A set of parameters associated with a print element.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRPrintHyperlinkParameters implements Serializable, VirtualizationSerializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private List<JRPrintHyperlinkParameter> parameters;
	
	
	/**
	 * Creates an empty set of parameters.
	 */
	public JRPrintHyperlinkParameters()
	{
		parameters = new ArrayList<>();
	}


	/**
	 * Returns the list of {@link JRPrintHyperlinkParameter parameters}.
	 * 
	 * @return the list of parameters
	 */
	public List<JRPrintHyperlinkParameter> getParameters()
	{
		return parameters;
	}
	
	
	/**
	 * Adds a new parameter to the set.
	 * 
	 * @param parameter the parameter to add
	 */
	public void addParameter(JRPrintHyperlinkParameter parameter)
	{
		parameters.add(parameter);
	}

	@Override
	public void writeVirtualized(VirtualizationOutput out) throws IOException
	{
		//FIXME optimize
		out.writeIntCompressed(parameters.size());
		for (JRPrintHyperlinkParameter parameter : parameters)
		{
			out.writeJRObject(parameter);
		}
	}

	@Override
	public void readVirtualized(VirtualizationInput in) throws IOException
	{
		int size = in.readIntCompressed();
		parameters = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
		{
			JRPrintHyperlinkParameter param = (JRPrintHyperlinkParameter) in.readJRObject();
			parameters.add(param);
		}
	}
}
