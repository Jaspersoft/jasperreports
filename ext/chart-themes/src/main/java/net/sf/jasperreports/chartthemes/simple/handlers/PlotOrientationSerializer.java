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
package net.sf.jasperreports.chartthemes.simple.handlers;

import java.io.IOException;

import org.jfree.chart.plot.PlotOrientation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PlotOrientationSerializer extends StdSerializer<PlotOrientation>
{
	private static final long serialVersionUID = 1L;

	public PlotOrientationSerializer()
	{
		this(null);
	}

	public PlotOrientationSerializer(Class<PlotOrientation> vc)
	{
		super(vc);
	}

	@Override
	public void serialize(PlotOrientation value, JsonGenerator jgen, SerializerProvider provider) throws IOException 
	{
		String strValue = convert(value);
		if (strValue != null)
		{
			jgen.writeString(strValue);
		}
	}

	public static String convert(PlotOrientation value)
	{
		if (value == null)
		{
			return null;
		}
		return value.toString();
	}
}
