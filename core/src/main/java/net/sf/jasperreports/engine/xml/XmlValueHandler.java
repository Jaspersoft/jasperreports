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
package net.sf.jasperreports.engine.xml;

import java.io.IOException;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXmlExporter;

/**
 * A handler that deals with arbitrary values being exported to XML and parsed back 
 * to {@link JasperPrint} objects.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface XmlValueHandler
{

	/**
	 * Returns the namespace of the elements generated on XML export.
	 * 
	 * @return the namespace of the elements generated on XML export
	 */
	XmlHandlerNamespace getNamespace();

	/**
	 * Outputs the XML representation of a value if the value is supported by
	 * this handler.
	 * 
	 * @param value the value
	 * @param exporter the XML exporter
	 * @return <code>true</code> iff the value is supported by this handler
	 * @throws IOException
	 */
	boolean writeToXml(Object value, JRXmlExporter exporter) throws IOException;
	
}
