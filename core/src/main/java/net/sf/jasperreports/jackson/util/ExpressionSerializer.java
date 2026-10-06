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
package net.sf.jasperreports.jackson.util;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ExpressionSerializer extends StdSerializer<JRExpression>
{
	private static final long serialVersionUID = 1L;

	public ExpressionSerializer()
	{
		this(null);
	}

	public ExpressionSerializer(Class<JRExpression> vc)
	{
		super(vc);
	}

	@Override
	public void serialize(JRExpression value, JsonGenerator jgen, SerializerProvider provider) throws IOException 
	{
		ToXmlGenerator xgen = jgen instanceof ToXmlGenerator ? (ToXmlGenerator)jgen : null;

		if (value.getType() == ExpressionTypeEnum.SIMPLE_TEXT)
		{
			if (xgen == null)
			{
				jgen.writeStartObject();
				jgen.writeStringField("type", ExpressionTypeEnum.SIMPLE_TEXT.getName());
				jgen.writeStringField("text", value.getText());
				jgen.writeEndObject();
			}
			else
			{
				jgen.writeStartObject();
				xgen.setNextIsAttribute(true);
				jgen.writeStringField("type", ExpressionTypeEnum.SIMPLE_TEXT.getName());
				xgen.setNextIsAttribute(false);
				jgen.writeRaw(value.getText());
				jgen.writeEndObject();
			}
		}
		else
		{
			if (xgen == null)
			{
				jgen.writeString(value.getText());
			}
			else
			{
				jgen.writeStartObject();
				try
				{
					xgen.getStaxWriter().writeCData(value.getText());
				}
				catch (XMLStreamException e)
				{
					throw new IOException(e);
				}
				jgen.writeEndObject();
			}
		}
	}
}
