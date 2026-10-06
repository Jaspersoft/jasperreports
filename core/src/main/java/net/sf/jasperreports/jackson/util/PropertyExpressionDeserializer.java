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
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertyExpression;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignPropertyExpression;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PropertyExpressionDeserializer extends StdDeserializer<JRPropertyExpression>
{
	private static final long serialVersionUID = 1L;

	public PropertyExpressionDeserializer()
	{
		this(null);
	}
	
	public PropertyExpressionDeserializer(Class<?> vc)
	{
		super(vc);
	}

	@Override
	public JRPropertyExpression deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException 
	{
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		
		JRPropertyExpression propertyExpression = null;
		
		FromXmlParser xp = p instanceof FromXmlParser ? (FromXmlParser)p : null;
		if (xp == null)
		{
			propertyExpression = getPropertyExpression();
			propertyExpression.setName(node.get("name").asText());
			
			JRDesignExpression expression = new JRDesignExpression(node.get("text").asText());
			JsonNode typeNode = node.get("type");
			if (typeNode != null)
			{
				expression.setType(ExpressionTypeEnum.getByName(typeNode.asText()));
			}

			setValueExpression(propertyExpression, expression);
		}
		else
		{
			propertyExpression = getPropertyExpression();
			propertyExpression.setName(node.get("name").asText());

			JRDesignExpression expression = new JRDesignExpression(node.get("").asText());
			JsonNode typeNode = node.get("type");
			if (typeNode != null)
			{
				expression.setType(ExpressionTypeEnum.getByName(typeNode.asText()));
			}
			
			setValueExpression(propertyExpression, expression);
		}
		
		return propertyExpression;
	}
	
	protected JRPropertyExpression getPropertyExpression()
	{
		return new JRDesignPropertyExpression();
	}
	
	protected void setValueExpression(JRPropertyExpression propetyExpression, JRExpression expression)
	{
		((JRDesignPropertyExpression)propetyExpression).setValueExpression(expression);
	}
}
