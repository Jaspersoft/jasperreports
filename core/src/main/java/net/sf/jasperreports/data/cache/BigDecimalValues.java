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
package net.sf.jasperreports.data.cache;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.jasperreports.engine.JRConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BigDecimalValues implements ColumnValues, Serializable
{

	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private ColumnValues unscaledValues;
	private ColumnValues scaleValues;

	public BigDecimalValues(ColumnValues unscaledValues,
			ColumnValues scaleValues)
	{
		if (unscaledValues.size() != scaleValues.size())
		{
			throw new IllegalArgumentException();
		}
		
		this.unscaledValues = unscaledValues;
		this.scaleValues = scaleValues;
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeUnshared(unscaledValues);
		out.writeUnshared(scaleValues);
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.unscaledValues = (ColumnValues) in.readUnshared();
		this.scaleValues = (ColumnValues) in.readUnshared();
	}

	@Override
	public int size()
	{
		return unscaledValues.size();
	}

	@Override
	public ColumnValuesIterator iterator()
	{
		ColumnValuesIterator unscaledIterator = unscaledValues.iterator();
		ColumnValuesIterator scaleIterator = scaleValues.iterator();
		return new BigDecimalValuesIterator(unscaledIterator, scaleIterator);
	}

}

class BigDecimalValuesIterator implements ColumnValuesIterator
{

	private final ColumnValuesIterator unscaledIterator;
	private final ColumnValuesIterator scaleIterator;

	public BigDecimalValuesIterator(ColumnValuesIterator unscaledIterator,
			ColumnValuesIterator scaleIterator)
	{
		this.unscaledIterator = unscaledIterator;
		this.scaleIterator = scaleIterator;
	}

	@Override
	public void moveFirst()
	{
		unscaledIterator.moveFirst();
		scaleIterator.moveFirst();
	}

	@Override
	public boolean next()
	{
		return unscaledIterator.next() && scaleIterator.next();
	}

	@Override
	public Object get()
	{
		BigInteger unscaled = (BigInteger) unscaledIterator.get();
		int scale = ((Number) scaleIterator.get()).intValue();
		return new BigDecimal(unscaled, scale);
	}

}