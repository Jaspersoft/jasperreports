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

import net.sf.jasperreports.components.items.ItemProperty;
import net.sf.jasperreports.engine.JRExpression;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ItemPropertySerializer extends StdSerializer<ItemProperty>
{
	private static final long serialVersionUID = 1L;

	public ItemPropertySerializer()
	{
		this(null);
	}

	public ItemPropertySerializer(Class<ItemProperty> vc)
	{
		super(vc);
	}

	@Override
	public void serialize(ItemProperty value, JsonGenerator jgen, SerializerProvider provider) throws IOException 
	{
		ToXmlGenerator xgen = jgen instanceof ToXmlGenerator ? (ToXmlGenerator)jgen : null;

		if (xgen == null)
		{
			jgen.writeStartObject();
			jgen.writeStringField("name", value.getName());
			if (value.getValue() != null)
			{
				jgen.writeStringField("value", value.getValue());
			}
			JRExpression expression = value.getValueExpression();
			if (expression != null)
			{
				jgen.writeStringField("expression", expression.getText());
			}
			jgen.writeEndObject();
		}
		else
		{
			jgen.writeStartObject();
			xgen.setNextIsAttribute(true);
			jgen.writeStringField("name", value.getName());
			if (value.getValue() != null) // apparently, null test is not needed, at least for xml, but anyway...
			{
				jgen.writeStringField("value", value.getValue());
			}
			JRExpression expression = value.getValueExpression();
			if (expression != null)
			{
				xgen.setNextIsAttribute(false);
				try
				{
					xgen.getStaxWriter().writeCData(expression.getText());
				}
				catch (XMLStreamException e)
				{
					throw new IOException(e);
				}
			}
			jgen.writeEndObject();
		}
	}
}
