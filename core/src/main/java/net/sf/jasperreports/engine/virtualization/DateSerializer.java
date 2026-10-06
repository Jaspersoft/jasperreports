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
package net.sf.jasperreports.engine.virtualization;

import java.io.IOException;
import java.util.Date;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DateSerializer implements ObjectSerializer<Date>
{
	@Override
	public int typeValue()
	{
		return SerializationConstants.OBJECT_TYPE_DATE;
	}

	@Override
	public ReferenceType defaultReferenceType()
	{
		return ReferenceType.OBJECT;
	}

	@Override
	public boolean defaultStoreReference()
	{
		return true;
	}

	@Override
	public void write(Date value, VirtualizationOutput out) throws IOException
	{
		//FIXME check if milliseconds/seconds/etc is zero and write a smaller value
		out.writeLong(value.getTime());
	}

	@Override
	public Date read(VirtualizationInput in) throws IOException
	{
		long time = in.readLong();
		return new Date(time);
	}

}
