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

import java.io.IOException;
import java.io.Writer;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public interface MetadataWriter {

	Writer getWriter();

	void writeHeader() throws IOException;

	void writeKeyValSeparator() throws IOException;

	void closeAndStartNewObject(SchemaNode node) throws IOException;

	void writeNodeKey(SchemaNode node, boolean isSameObject) throws IOException;

	void writeArrayStart(SchemaNode node) throws IOException;

	void writeArrayClosing(SchemaNode node) throws IOException;

	void writeObjectStart(SchemaNode node) throws IOException;

	void writeObjectClosing(SchemaNode node) throws IOException;

	void writeValueClosing(SchemaNode node, SchemaNode parent) throws IOException;

	void writeValue(String key, Object value, SchemaNode parent) throws IOException;

	void writePreviousMemberValue(SchemaNodeMember member, SchemaNode parent, boolean isSameObject) throws IOException;

}
