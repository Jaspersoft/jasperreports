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
public class JsonMetadataWriter extends AbstractMetadataWriter {

	private boolean escapeMembers;

	public JsonMetadataWriter(Writer writer) {
		super(writer);
	}

	public void setEscapeMembers(boolean escapeMembers) {
		this.escapeMembers = escapeMembers;
	}

	private String getIndentedKey(String key) {
		StringBuilder sb = getIndent();
		if (escapeMembers) sb.append("\"");
		sb.append(key); // FIXME: should we also escape the key string?
		if (escapeMembers) sb.append("\"");

		return sb.toString();
	}

	@Override
	public void writeHeader() {
		// do nothing for now // FIXME maybe add JasperReports version and a date?
	}

	@Override
	public void writeKeyValSeparator() throws IOException {
		writer.write(",");
	}

	@Override
	public void closeAndStartNewObject(SchemaNode node) throws IOException {
		writer.write("\n");
		decrementPadding();
		writer.write(getIndent() + "}, {");
		incrementPadding();
	}

	@Override
	public void writeKeyWithVal(String key, Object value) throws IOException {
		writer.write("\n");
		String paddedKey = getIndentedKey(key);
		writer.write(paddedKey + ": ");
		writeValue(key, value);
		writer.write(",");
	}

	@Override
	public void writeKey(String key, boolean isSameObject) throws IOException {
		writer.write("\n");
		if (!isSameObject) incrementPadding();
		String paddedKey = getIndentedKey(key);
		writer.write(paddedKey + ": ");
	}

	@Override
	public void writeArrayStart(SchemaNode node) throws IOException {
		writer.write("[{");
	}

	@Override
	public void writeArrayClosing(SchemaNode node) throws IOException {
		writer.write("\n");
		decrementPadding();
		writer.write(getIndent() + "}]");
	}

	@Override
	public void writeObjectStart(SchemaNode node) throws IOException {
		writer.write("{");
	}

	@Override
	public void writeObjectClosing(SchemaNode node) throws IOException {
		writer.write("\n");
		decrementPadding();
		writer.write(getIndent() + "}");
	}

	@Override
	public void writeValueClosing(SchemaNode node) throws IOException {
		// do nothing
	}

	@Override
	public void writeValue(String key, Object value)throws IOException {
		if (value != null) {
			if (value instanceof Number || value instanceof Boolean) {
				writer.write(value.toString());
			} else if (value instanceof Date) {
				writer.write("\"");
				writer.write(isoDateFormat.format((Date)value));
				writer.write("\"");
			} else {
				writer.write("\"");
				writer.write(JsonStringEncoder.getInstance().quoteAsString(value.toString()));
				writer.write("\"");
			}
		} else {
			writer.write("null");  // FIXMEJSONMETA: how to treat null values?
		}
	}
}
