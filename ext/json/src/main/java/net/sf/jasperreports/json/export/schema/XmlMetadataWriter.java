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

import java.io.IOException;
import java.io.Writer;
import java.util.Date;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class XmlMetadataWriter extends AbstractMetadataWriter {

	private static final String ARRAY_ITEM_SUFFIX = "_item";

	public XmlMetadataWriter(Writer writer) {
		super(writer);
	}

	@Override
	public void writeHeader() throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	}

	@Override
	public void writeKeyValSeparator() {
		// do nothing
	}

	@Override
	public void closeAndStartNewObject(SchemaNode arrayNode) throws IOException {
		writer.write("\n");
		decrementPadding();
		writer.write(
				getIndent()
						.append("</")
						.append(getChildrenWrapper(arrayNode))
						.append(">\n").toString());

		writer.write(
				getIndent()
						.append("<")
						.append(getChildrenWrapper(arrayNode))
						.append(">")
						.toString());

		incrementPadding();
	}

	@Override
	public void writeKeyWithVal(String key, Object value) throws IOException {
		writeValue(key, value);
		writer.write("</");
		writer.write(key);
		writer.write(">");
	}

	@Override
	public void writeKey(String key, boolean isSameObject) throws IOException {
		// do nothing
	}

	private String getChildrenWrapper(SchemaNode arrayNode) {
		if (arrayNode.getChildrenKey() != null) {
			return arrayNode.getChildrenKey();
		} else {
			return arrayNode.getKey() + ARRAY_ITEM_SUFFIX;
		}
	}

	@Override
	public void writeArrayStart(SchemaNode node) throws IOException {
		if (node.getLevel() > 0) {
			writer.write("\n");
		}
		writer.write(
				getIndent()
						.append("<")
						.append(node.getKey())
						.append(">\n").toString());

		incrementPadding();

		writer.write(
				getIndent()
						.append("<")
						.append(getChildrenWrapper(node))
						.append(">").toString());

		incrementPadding();
	}

	@Override
	public void writeArrayClosing(SchemaNode node) throws IOException {
		writer.write("\n");
		decrementPadding();

		writer.write(
				getIndent()
						.append("</")
						.append(getChildrenWrapper(node))
						.append(">\n").toString());

		decrementPadding();

		writer.write(
				getIndent()
						.append("</")
						.append(node.getKey())
						.append(">").toString());
	}

	@Override
	public void writeObjectStart(SchemaNode node) throws IOException {
		if (node.getLevel() > 0) {
			writer.write("\n");
		}
		writer.write(
				getIndent()
						.append("<")
						.append(node.getKey())
						.append(">").toString());

		incrementPadding();
	}

	@Override
	public void writeObjectClosing(SchemaNode node) throws IOException {
		writer.write("\n");
		decrementPadding();

		writer.write(
				getIndent()
						.append("</")
						.append(node.getKey())
						.append(">").toString());
	}

	@Override
	public void writeValueClosing(SchemaNode node) throws IOException {
		writer.write("</");
		writer.write(node.getKey());
		writer.write(">");
	}

	@Override
	public void writeValue(String key, Object value)throws IOException {
		writer.write("\n");

		writer.write(
				getIndent()
						.append("<")
						.append(key)
						.append(">").toString());

		if (value != null) {
			if (value instanceof Number || value instanceof Boolean) {
				writer.write(value.toString());
			} else if (value instanceof Date) {
				writer.write(isoDateFormat.format((Date)value));
			} else {
				writer.write(JsonStringEncoder.getInstance().quoteAsString(value.toString())); // FIXME: Encode for XML
			}
		} else {
			writer.write("null");  // FIXME: how to treat null values?
		}
	}
}
