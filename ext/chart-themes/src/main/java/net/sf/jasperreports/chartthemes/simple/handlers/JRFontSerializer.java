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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import net.sf.jasperreports.engine.JRFont;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFontSerializer extends StdSerializer<JRFont>
{
	private static final long serialVersionUID = 1L;

	public JRFontSerializer()
	{
		this(null);
	}

	public JRFontSerializer(Class<JRFont> vc)
	{
		super(vc);
	}

	@Override
	public void serialize(JRFont value, JsonGenerator jgen, SerializerProvider provider) throws IOException 
	{
		if (jgen instanceof ToXmlGenerator)
		{
			((ToXmlGenerator)jgen).setNextIsAttribute(true);
		}
		
		jgen.writeStartObject();
		
		String fontName = value.getOwnFontName();
		if (fontName != null)
		{
			jgen.writeStringField("font-name", fontName);
		}

		Float fontSize = value.getOwnFontSize();
		if (fontSize != null)
		{
			jgen.writeNumberField("font-size", fontSize);
		}

		Boolean bold = value.isOwnBold();
		if (bold != null)
		{
			jgen.writeBooleanField("bold", bold);
		}

		Boolean italic = value.isOwnItalic();
		if (italic != null)
		{
			jgen.writeBooleanField("italic", italic);
		}

		Boolean underline = value.isOwnUnderline();
		if (underline != null)
		{
			jgen.writeBooleanField("underline", underline);
		}

		Boolean strikeThrough = value.isOwnStrikeThrough();
		if (strikeThrough != null)
		{
			jgen.writeBooleanField("strike-through", strikeThrough);
		}
		
		jgen.writeEndObject();
	}
}
