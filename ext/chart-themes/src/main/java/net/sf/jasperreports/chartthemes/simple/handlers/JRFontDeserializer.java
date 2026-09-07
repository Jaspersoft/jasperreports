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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.base.JRBaseFont;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFontDeserializer extends StdDeserializer<JRFont>
{
	private static final long serialVersionUID = 1L;

	public JRFontDeserializer()
	{
		this(null);
	}
	
	public JRFontDeserializer(Class<?> vc)
	{
		super(vc);
	}

	@Override
	public JRFont deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException 
	{
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		
		JRBaseFont font = new JRBaseFont();
		
		JsonNode fontNameNode = node.get("font-name");
		if (fontNameNode != null)
		{
			font.setFontName(fontNameNode.asText());
		}
		
		JsonNode fontSizeNode = node.get("font-size");
		if (fontSizeNode != null)
		{
			font.setFontSize((float)fontSizeNode.asDouble());
		}
		
		JsonNode boldNode = node.get("bold");
		if (boldNode != null)
		{
			font.setBold(boldNode.asBoolean());
		}
		
		JsonNode italicNode = node.get("italic");
		if (italicNode != null)
		{
			font.setItalic(italicNode.asBoolean());
		}
		
		JsonNode underlineNode = node.get("underline");
		if (underlineNode != null)
		{
			font.setUnderline(underlineNode.asBoolean());
		}
		
		JsonNode strikeThroughNode = node.get("strike-through");
		if (strikeThroughNode != null)
		{
			font.setStrikeThrough(strikeThroughNode.asBoolean());
		}
		
		return font;
	}
}
