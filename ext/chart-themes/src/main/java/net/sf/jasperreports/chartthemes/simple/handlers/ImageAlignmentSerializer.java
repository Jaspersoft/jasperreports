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

import org.jfree.chart.ui.Align;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ImageAlignmentSerializer extends StdSerializer<Integer>
{
	private static final long serialVersionUID = 1L;

	public ImageAlignmentSerializer()
	{
		this(null);
	}

	public ImageAlignmentSerializer(Class<Integer> vc)
	{
		super(vc);
	}

	@Override
	public void serialize(Integer value, JsonGenerator jgen, SerializerProvider provider) throws IOException 
	{
		String strValue = convert(value);
		if (strValue != null)
		{
			jgen.writeString(strValue);
		}
	}

	public static String convert(Integer value)
	{
		if (value == null)
		{
			return null;
		}
		return 
			value.equals(Align.BOTTOM) 
			? "Align.BOTTOM"
			: value.equals(Align.BOTTOM_LEFT)
			? "Align.BOTTOM_LEFT"
			: value.equals(Align.BOTTOM_RIGHT)
			? "Align.BOTTOM_RIGHT"
			: value.equals(Align.CENTER)
			? "Align.CENTER"
			: value.equals(Align.FIT)
			? "Align.FIT"
			: value.equals(Align.FIT_HORIZONTAL)
			? "Align.FIT_HORIZONTAL"
			: value.equals(Align.FIT_VERTICAL)
			? "Align.FIT_VERTICAL"
			: value.equals(Align.LEFT)
			? "Align.LEFT"
			: value.equals(Align.RIGHT)
			? "Align.RIGHT"
			: value.equals(Align.TOP)
			? "Align.TOP"
			: value.equals(Align.TOP_LEFT)
			? "Align.TOP_LEFT"
			: value.equals(Align.TOP_RIGHT)
			? "Align.TOP_RIGHT" : null;
	}
}
