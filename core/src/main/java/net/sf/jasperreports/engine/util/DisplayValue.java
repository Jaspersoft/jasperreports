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
package net.sf.jasperreports.engine.util;

import java.io.Serializable;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DisplayValue<T> implements Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	protected final T key;
	protected final String label;
	
	public DisplayValue(T key, String label)
	{
		this.key = key;
		this.label = label;
	}

	@Override
	public int hashCode()
	{
		return key == null ? 0 : key.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof DisplayValue))
		{
			return false;
		}
		
		Object otherKey = ((DisplayValue<?>) obj).key;
		return key == null ? otherKey == null
				: (otherKey != null && key.equals(otherKey));
	}

	@Override
	public String toString()
	{
		return label;
	}

	public T key()
	{
		return key;
	}

	public String label()
	{
		return label;
	}
}
