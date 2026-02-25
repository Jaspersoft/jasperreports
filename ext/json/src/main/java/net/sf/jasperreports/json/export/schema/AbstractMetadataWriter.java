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
package net.sf.jasperreports.json.export.schema;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import net.sf.jasperreports.engine.util.JRDataUtils;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public abstract class AbstractMetadataWriter implements MetadataWriter {

	private int currentPadding = 0;
	private int spacesPerTab = 4;

	protected final DateFormat isoDateFormat = JRDataUtils.getIsoDateFormat();
	protected Writer writer;

	public AbstractMetadataWriter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public Writer getWriter() {
		return writer;
	}

	protected void incrementPadding() {
		currentPadding += spacesPerTab;
	}

	protected void decrementPadding() {
		currentPadding -= spacesPerTab;
	}

	protected StringBuilder getIndent() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < currentPadding; i++) {
			sb.append(" ");
		}
		return sb;
	}

}
