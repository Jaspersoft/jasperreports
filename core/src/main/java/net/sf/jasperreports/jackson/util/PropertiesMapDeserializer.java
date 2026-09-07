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
package net.sf.jasperreports.jackson.util;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import net.sf.jasperreports.engine.JRPropertiesMap;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PropertiesMapDeserializer extends StdDeserializer<JRPropertiesMap>
{
	private static final long serialVersionUID = 1L;

	public PropertiesMapDeserializer()
	{
		this(null);
	}
	
	public PropertiesMapDeserializer(Class<?> vc)
	{
		super(vc);
	}

	@Override
	public JRPropertiesMap deserialize(JsonParser p, DeserializationContext ctxt, JRPropertiesMap intoValue) throws IOException 
	{
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		
		FromXmlParser xp = p instanceof FromXmlParser ? (FromXmlParser)p : null;
		if (xp == null)
		{
			for (Iterator<JsonNode> it = node.elements(); it.hasNext();)
			{
				JsonNode propertyNode = it.next();
				JsonNode propertyNameNode = propertyNode.get("name");
				JsonNode propertyValueNode = propertyNode.get("value");
				if (propertyNameNode != null)
				{
					intoValue.setProperty(propertyNameNode.asText(), propertyValueNode == null || propertyValueNode.isNull() ? null : propertyValueNode.asText());
				}
			}
		}
		else
		{
			JsonNode propertyNameNode = node.get("name");
			JsonNode propertyValueNode = node.get("value");
			if (propertyNameNode != null)
			{
				intoValue.setProperty(propertyNameNode.asText(), propertyValueNode == null || propertyValueNode.isNull() ? null : propertyValueNode.asText());
			}
		}
		
		return intoValue;
	}
	
	@Override
	public JRPropertiesMap deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException 
	{
		return deserialize(p, ctxt, new JRPropertiesMap());
	}
}
