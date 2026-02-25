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
package net.sf.jasperreports.json.export;


import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.json.export.schema.MetadataProcessor;
import net.sf.jasperreports.json.export.schema.XmlMetadataProcessor;

import java.io.Writer;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class XmlMetadataExporter extends AbstractMetadataExporter {

	public XmlMetadataExporter() {
		super();
	}

	public XmlMetadataExporter(JasperReportsContext jasperReportsContext) {
		super(jasperReportsContext);
	}

	@Override
	protected MetadataProcessor createMetadataProcessor(Writer writer) {
		return new XmlMetadataProcessor(writer);
	}

	@Override
	protected void passConfigToProcessor(MetadataProcessor processor, JsonMetadataReportConfiguration configuration) {
		// do nothing for now
	}

}
