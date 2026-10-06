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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import net.sf.jasperreports.components.items.ItemProperty;
import net.sf.jasperreports.components.items.StandardItemProperty;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ItemPropertyDeserializer extends StdDeserializer<ItemProperty>
{
	private static final long serialVersionUID = 1L;

	public ItemPropertyDeserializer()
	{
		this(null);
	}
	
	public ItemPropertyDeserializer(Class<?> vc)
	{
		super(vc);
	}

	@Override
	public ItemProperty deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException 
	{
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		
		ItemProperty itemProperty = getItemProperty();
		
		JsonNode nameNode = node.get("name");
		if (nameNode != null)
		{
			setName(itemProperty, nameNode.asText());
		}
		JsonNode valueNode = node.get("value");
		if (valueNode != null)
		{
			setValue(itemProperty, valueNode.asText());
		}
		
		JsonNode expressionNode = null;
		FromXmlParser xp = p instanceof FromXmlParser ? (FromXmlParser)p : null;
		if (xp == null)
		{
			expressionNode = node.get("expression");
		}
		else
		{
			expressionNode = node.getNodeType() == JsonNodeType.STRING ? node : node.get("");
		}
		
		if (expressionNode != null)
		{
			JRDesignExpression expression = new JRDesignExpression(expressionNode.asText());
			setValueExpression(itemProperty, expression);
		}
		
		customDeserialize(itemProperty, node);
		
		return itemProperty;
	}
	
	protected ItemProperty getItemProperty()
	{
		return new StandardItemProperty();
	}
	
	protected void setName(ItemProperty itemProperty, String name)
	{
		((StandardItemProperty)itemProperty).setName(name);
	}
	
	protected void setValue(ItemProperty itemProperty, String value)
	{
		((StandardItemProperty)itemProperty).setValue(value);
	}
	
	protected void setValueExpression(ItemProperty itemProperty, JRExpression expression)
	{
		((StandardItemProperty)itemProperty).setValueExpression(expression);
	}

	protected void customDeserialize(ItemProperty itemProperty, JsonNode node)
	{
		//nothing;
	}
}
