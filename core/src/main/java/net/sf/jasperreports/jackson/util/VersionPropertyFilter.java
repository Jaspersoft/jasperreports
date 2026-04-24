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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import net.sf.jasperreports.engine.util.VersionComparator;


/**
 * A Jackson property filter that excludes properties annotated with
 * {@link JRXmlSince} when the target version (passed via serialization attributes)
 * is older than the version in which the property was introduced.
 *
 * <p>The target version is read from the serialization attribute
 * {@link #ATTRIBUTE_TARGET_VERSION}. When no target version is set,
 * all properties are serialized (current version behavior).</p>
 *
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class VersionPropertyFilter extends SimpleBeanPropertyFilter
{
	/**
	 * The serialization attribute key used to pass the target JRXML version
	 * to the filter via {@code ObjectWriter.withAttribute()}.
	 */
	public static final String ATTRIBUTE_TARGET_VERSION = "net.sf.jasperreports.jrxml.target.version";

	/**
	 * The filter ID used with Jackson's {@code @JsonFilter} and
	 * {@code SimpleFilterProvider}.
	 */
	public static final String FILTER_ID = "jrxmlVersionFilter";

	private final VersionComparator versionComparator = new VersionComparator();

	@Override
	public void serializeAsField(
		Object pojo,
		JsonGenerator gen,
		SerializerProvider provider,
		PropertyWriter writer
	) throws Exception
	{
		if (includeProperty(provider, writer))
		{
			writer.serializeAsField(pojo, gen, provider);
		}
		else if (!gen.canOmitFields())
		{
			writer.serializeAsOmittedField(pojo, gen, provider);
		}
	}

	private boolean includeProperty(SerializerProvider provider, PropertyWriter writer)
	{
		String targetVersion = (String) provider.getAttribute(ATTRIBUTE_TARGET_VERSION);
		if (targetVersion == null)
		{
			// no target version specified, write everything (current version)
			return true;
		}

		JRXmlSince since = writer.getAnnotation(JRXmlSince.class);
		if (since == null)
		{
			// no version annotation, always include
			return true;
		}

		// include only if targetVersion >= sinceVersion
		return versionComparator.compare(targetVersion, since.value()) >= 0;
	}
}
