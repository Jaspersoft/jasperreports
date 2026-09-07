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
package net.sf.jasperreports.chartthemes.simple.handlers;

import java.io.IOException;

import org.jfree.chart.ui.Align;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ImageAlignmentDeserializer extends StdDeserializer<Integer>
{
	private static final long serialVersionUID = 1L;

	public ImageAlignmentDeserializer()
	{
		this(null);
	}
	
	public ImageAlignmentDeserializer(Class<?> vc)
	{
		super(vc);
	}

	@Override
	public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException 
	{
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		
		return convert(node.textValue());
	}
	
	public static Integer convert(String value)
	{
		if (value == null)
		{
			return null;
		}
		return 
			"Align.BOTTOM".equals(value) 
			? Align.BOTTOM
			: "Align.BOTTOM_LEFT".equals(value)
			? Align.BOTTOM_LEFT
			: "Align.BOTTOM_RIGHT".equals(value)
			? Align.BOTTOM_RIGHT
			: "Align.CENTER".equals(value)
			? Align.CENTER
			: "Align.FIT".equals(value)
			? Align.FIT
			: "Align.FIT_HORIZONTAL".equals(value)
			? Align.FIT_HORIZONTAL
			: "Align.FIT_VERTICAL".equals(value)
			? Align.FIT_VERTICAL
			: "Align.LEFT".equals(value)
			? Align.LEFT
			: "Align.RIGHT".equals(value)
			? Align.RIGHT
			: "Align.TOP".equals(value)
			? Align.TOP
			: "Align.TOP_LEFT".equals(value)
			? Align.TOP_LEFT
			: "Align.TOP_RIGHT".equals(value)
			? Align.TOP_RIGHT : null;
	}
}
