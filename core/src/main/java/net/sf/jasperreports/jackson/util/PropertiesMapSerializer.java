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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.xml.ReportWriterConfiguration;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class PropertiesMapSerializer extends StdSerializer<JRPropertiesMap>
{
	private static final long serialVersionUID = 1L;
	private final ReportWriterConfiguration reportWriterConfig;

	public PropertiesMapSerializer(ReportWriterConfiguration reportWriterConfig)
	{
		super((Class<JRPropertiesMap>)null);
		
		this.reportWriterConfig = reportWriterConfig;
	}

	@Override
	public boolean isUnwrappingSerializer() 
	{
		return false;
	}
	
	@Override
	public void serialize(JRPropertiesMap propertiesMap, JsonGenerator jgen, SerializerProvider provider) throws IOException 
	{
		if (propertiesMap != null)
		{
			ToXmlGenerator xgen = jgen instanceof ToXmlGenerator ? (ToXmlGenerator)jgen : null;

			jgen.writeStartArray();
			for (String name : propertiesMap.getOwnPropertyNames())
			{
				if (reportWriterConfig.isPropertyToWrite(name))
				{
					jgen.writeStartObject();
					if (xgen != null)
					{
						xgen.setNextIsAttribute(true);
					}
					jgen.writeStringField("name", name);
					String value = propertiesMap.getProperty(name);
					if (value != null)
					{
						jgen.writeStringField("value", value);
					}
					jgen.writeEndObject();
				}
			}
			jgen.writeEndArray();
		}
	}
}
