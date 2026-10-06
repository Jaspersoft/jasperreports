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
package net.sf.jasperreports.jackson.type;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

import net.sf.jasperreports.engine.type.EnumUtil;
import net.sf.jasperreports.engine.type.NamedEnum;

/**
 * A {@link NamedEnum} deserializer that accepts both {@link NamedEnum#getName() enum names}
 * and enum constants (for backward compatibility).
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class NamedEnumConstantDeserializer extends StdScalarDeserializer<Object>
{

	private static final long serialVersionUID = 1L;

	public NamedEnumConstantDeserializer(JavaType type)
	{
		super(type);
		
		if (_valueClass == null || !NamedEnum.class.isAssignableFrom(_valueClass)
				|| !Enum.class.isAssignableFrom(_valueClass))
		{
			throw new IllegalArgumentException(type + " needs to be a NamedEnum");
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes"})
	@Override
	public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException
	{
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		String textValue = node.textValue();
		Object value = EnumUtil.getEnumByName(((Class<? extends NamedEnum>) _valueClass).getEnumConstants(), textValue);
		if (value == null)
		{
			try
			{
				value = Enum.valueOf((Class<? extends Enum>) _valueClass, textValue);
			}
			catch (IllegalArgumentException e)
			{
		        return ctxt.handleWeirdStringValue(_valueClass, textValue,
		                "not one of the values accepted for enum %s", _valueClass);			
			}
		}
		return value;
	}

}
