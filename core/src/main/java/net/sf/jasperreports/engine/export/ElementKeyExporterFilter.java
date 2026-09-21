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
package net.sf.jasperreports.engine.export;

import java.util.Set;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * An exporter filter that excludes elements based on their keys.
 *
 * The filter uses a fixed set of elements keys to exclude.
 * <p/>
 * Element keys are set at report design time and are propagated into generated reports.
 * Each element in a filled report has the same key as the element from the report template
 * that generated it.
 * <p/>
 * To trigger an element key filter, the report designer needs to define one or more report
 * properties that start with <code>&lt;exporter_property_prefix&gt;.exclude.key</code>. Each such
 * property matches a single element key which is to be excluded by the filter. The element
 * key is given by the property value, or if no value is set for the property, by the property
 * suffix.
 * <p/>
 * The following example shows how to specify element keys which are to be excluded
 * from specific export outputs:
 * <pre>
 * &lt;jasperReport ...&gt;
 *   &lt;!-- exclude elements with keys Image1 and Text4 from HTML export --&gt;
 *   &lt;property name="net.sf.jasperreports.export.html.exclude.key.Image1"/&gt;
 *   &lt;property name="net.sf.jasperreports.export.html.exclude.key.Text4"/&gt;
 *   &lt;!-- exclude elements with keys Image5 from PDF export --&gt;
 *   &lt;property name="net.sf.jasperreports.export.pdf.exclude.key.the.image" value="Image5"/&gt;
 *   ...
 * &lt;/jasperReport&gt;
 * </pre>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementKeyExporterFilter implements ExporterFilter
{
	private static final String EXCEPTION_MESSAGE_KEY_EXCLUDED_NULL_KEYS_SET = "export.filter.excluded.null.keys.set";
	
	private final Set<String> excludedKeys;
	
	/**
	 * Creates a filter instance.
	 * 
	 * @param excludedKeys the set of keys to exclude
	 */
	public ElementKeyExporterFilter(Set<String> excludedKeys)
	{
		if (excludedKeys == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_EXCLUDED_NULL_KEYS_SET,
					(Object[])null);
		}
		
		this.excludedKeys = excludedKeys;
	}

	/**
	 * Excludes elements whose keys match any of the excluded keys.
	 * 
	 * Elements with no keys are <u>not</u> excluded.
	 */
	@Override
	public boolean isToExport(JRPrintElement element)
	{
		return element.getKey() == null
				|| !excludedKeys.contains(element.getKey());
	}

}
