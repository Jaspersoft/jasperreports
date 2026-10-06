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
package net.sf.jasperreports.engine.fill;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRConstants;

/**
 * Position that uniquely identifies a dataset instantiation as part of a fill process.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillDatasetPosition implements Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private final FillDatasetPosition parent;
	private final Map<String, Serializable> attributes;
	
	public FillDatasetPosition(FillDatasetPosition parent)
	{
		this.parent = parent;
		this.attributes = new LinkedHashMap<>();
	}
	
	public void addAttribute(String key, Serializable value)
	{
		attributes.put(key, value == null ? null : value);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof FillDatasetPosition))
		{
			return false;
		}
		
		FillDatasetPosition p = (FillDatasetPosition) o;
		return (parent == null ? p.parent == null : (p.parent != null && parent.equals(p.parent)))
				&& attributes.equals(p.attributes);
	}

	@Override
	public int hashCode()
	{
		int hash = 47;
		hash = 29 * hash + (parent == null ? 0 : parent.hashCode());
		hash = 29 * hash + attributes.hashCode();		
		return hash;
	}

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();
		string.append('[');
		attributesToString(string);
		string.append(']');
		return string.toString();
	}

	protected void attributesToString(StringBuilder string)
	{
		if (parent != null)
		{
			parent.attributesToString(string);
			string.append(',');
		}
		
		string.append('{');
		for (Map.Entry<String, Serializable> entry : attributes.entrySet())
		{
			string.append(entry.getKey());
			string.append(':');
			string.append(entry.getValue());
			string.append(',');
		}
		string.append('}');
	}
	
}
