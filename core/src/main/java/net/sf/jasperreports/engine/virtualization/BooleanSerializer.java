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
package net.sf.jasperreports.engine.virtualization;

import java.io.IOException;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BooleanSerializer implements ObjectSerializer<Boolean>
{
	private static final String EXCEPTION_MESSAGE_KEY_UNEXPECTED_BOOLEAN_BYTE_VALUE = "engine.virtualization.unexpected.boolean.byte.value";

	@Override
	public int typeValue()
	{
		return SerializationConstants.OBJECT_TYPE_BOOLEAN;
	}

	@Override
	public ReferenceType defaultReferenceType()
	{
		return ReferenceType.OBJECT;
	}

	@Override
	public boolean defaultStoreReference()
	{
		// one byte, reference doesn't make sense
		return false;
	}

	@Override
	public void write(Boolean value, VirtualizationOutput out) throws IOException
	{
		//FIXME can be included in the type byte
		out.writeByte(value ? 1 : 0);
	}

	@Override
	public Boolean read(VirtualizationInput in) throws IOException
	{
		byte byteValue = in.readByte();
		if (byteValue != 0 && byteValue != 1)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNEXPECTED_BOOLEAN_BYTE_VALUE,
					new Object[]{byteValue});
		}
		return byteValue == 1;
	}
}