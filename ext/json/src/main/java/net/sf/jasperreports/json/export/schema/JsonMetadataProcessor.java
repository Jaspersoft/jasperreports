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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.util.*;

public class JsonMetadataProcessor {
	private static final Log log = LogFactory.getLog(JsonMetadataProcessor.class);

	private final JsonSchema jsonSchema;
	private Writer writer;
	private boolean escapeMembers;
	protected final DateFormat isoDateFormat = JRDataUtils.getIsoDateFormat();

	private final Map<SchemaNode, ArrayList<String>> visitedMembers = new HashMap<>();
	private final ArrayList<SchemaNode> openedSchemaNodes = new ArrayList<>();

	private String previousPath;

	public JsonMetadataProcessor() {
		this.jsonSchema = new JsonSchema();
	}

	public JsonMetadataProcessor(JsonSchema jsonSchema) {
		this.jsonSchema = jsonSchema;
	}

	public JsonSchema getJsonSchema() {
		return jsonSchema;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public void setEscapeMembers(boolean escapeMembers) {
		this.escapeMembers = escapeMembers;
	}

	public void processElement(Object value, String absolutePath, boolean repeatValue) throws IOException {
		if (openedSchemaNodes.size() == 0) {
			// initialize the json for the first time
			initJson(absolutePath, value, repeatValue);
		} else {
			String valueProperty = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);

			String[] curSegments = absolutePath.substring(0, absolutePath.lastIndexOf(".")).split("\\.");
			String[] prevSegments = previousPath.substring(0, previousPath.lastIndexOf(".")).split("\\.");

			int ln = Math.min(curSegments.length, prevSegments.length);
			int lastCommonIndex = -1;

			for (int i = 0; i < ln; i++) {
				if (curSegments[i].equals(prevSegments[i])) {
					lastCommonIndex = i;
				} else {
					break;
				}
			}

			int commonSegmentsNo = lastCommonIndex + 1;

			// compared to previous path, we have different path with common segments
			if (commonSegmentsNo < prevSegments.length) {
				if (log.isDebugEnabled()) {
					log.debug("\tgot different path with common segments");
				}

				// close the extra path segments of the previous path
				closeExtraPathSegments(prevSegments, lastCommonIndex);

				// open new path segments for the current path
				openPathSegments(curSegments, lastCommonIndex + 1);
			}
			// we have a longer path that extends previous path
			else if (commonSegmentsNo == prevSegments.length && curSegments.length > prevSegments.length) {
				if (log.isDebugEnabled()) {
					log.debug("\tgot longer path than previous one");
				}

				// open new paths
				openPathSegments(curSegments, lastCommonIndex + 1);
			}

			SchemaNode currentNode = jsonSchema.getPathToValueNode().get(absolutePath);

			if (log.isDebugEnabled()) {
				log.debug("\tcurrent node is: " + currentNode.getType().getName());
			}

			if (currentNode.isArray()) {
				writePathProperty(currentNode, valueProperty, value, repeatValue);
			}
			// just write the value for property, no repeat
			else {
				writePathProperty(currentNode, valueProperty, value, false);
			}
		}

		previousPath = absolutePath;
	}

	private void writePathProperty(SchemaNode node, String valueProperty, Object value, boolean repeatValue) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("\twriting property: " + valueProperty);
		}
		ArrayList<String> vizMembers = visitedMembers.get(node);
		String lastProp = null;
		int lastPropIdx = -1;
		int valPropIdx = node.indexOfMember(valueProperty);

		if (vizMembers != null && vizMembers.size() > 0) {
			lastProp = vizMembers.get(vizMembers.size() - 1);
			lastPropIdx = node.indexOfMember(lastProp);
		} else {
			vizMembers = new ArrayList<>();
			visitedMembers.put(node, vizMembers);
		}

		boolean foundPreviousRepeated = false;

		// if property of the same object
		if (lastProp == null || valPropIdx > lastPropIdx) {
			if (log.isDebugEnabled()) {
				log.debug("\tgot property of the same object");
			}

			// check for repeated values, if any, before writing current
			if (lastProp != null) {
				foundPreviousRepeated = writeReapeatedValues(node, lastPropIdx + 1, valPropIdx);
			} else {
				foundPreviousRepeated = writeReapeatedValues(node, 0, valPropIdx);
			}

			if (foundPreviousRepeated || vizMembers.size() > 0) {
				writer.write(",\n");
			}

			writeEscaped(node, valueProperty, value, repeatValue);

			// mark visited property for current node
			visitedMembers.get(node).add(valueProperty);
		}
		// create new object
		else {
			if (log.isDebugEnabled()) {
				log.debug("\tgot property of a new object");
			}
			// before closing current object, write the repeated values, if any, from last accessed property until the end is reached
			writeReapeatedValues(node, lastPropIdx + 1, node.getMembers().size());

			// close existing object
			writer.write("},\n{");

			// check for repeated values, if any, before writing current
			foundPreviousRepeated = writeReapeatedValues(node, 0, valPropIdx);

			if (foundPreviousRepeated) {
				writer.write(",");
			}

			writeEscaped(node, valueProperty, value, repeatValue);

			// mark visited property for current node
			visitedMembers.get(node).clear();
			visitedMembers.get(node).add(valueProperty);
		}
	}

	private boolean writeReapeatedValues(SchemaNode node, int from, int to) throws IOException {
		return writeReapeatedValues(node, from, to, true);
	}

	private boolean writeReapeatedValues(SchemaNode node, int from, int to, boolean startWithComma) throws IOException {
		boolean found = false;
		SchemaNodeMember member;

		for (int i = from; i < to; i++) {
			member = node.getMember(i);
			if (member.isRepeatValue() && member.getPreviousValue() != null) {
				found = true;
				if (i != 0 && startWithComma) {
					writer.write(",");
				}
				if (escapeMembers) {
					writer.write("\"" + member.getName() + "\":");
				} else {
					writer.write(member.getName() + ":");
				}

				writeValue(member.getPreviousValue());

				if (log.isDebugEnabled()) {
					log.debug("\t\twriting repeated value for member: " + member.getName());
				}
			}
		}

		return found;
	}

	private void writeEscaped(SchemaNode node, String valueProperty, Object value, boolean repeatValue) throws IOException {
		// write current value
		if (escapeMembers) {
			writer.write("\"" + valueProperty + "\":");
		} else {
			writer.write(valueProperty + ":");
		}

		writeValue(value);

		// mark repeated value
		if (repeatValue) {
			SchemaNodeMember nodeMember = node.getMember(valueProperty);
			nodeMember.setRepeatValue(true);
			nodeMember.setPreviousValue(value);
		}
	}

	private void closeExtraPathSegments(String[] prevSegments, int lastCommonIndex) throws IOException {
		for (int i = prevSegments.length - 1; i > lastCommonIndex; i--) {
			StringBuilder sb = new StringBuilder(prevSegments[0]);
			for (int j=1; j <= i; j++) {
				sb.append(".").append(prevSegments[j]);
			}

			SchemaNode toClose = jsonSchema.getPathToObjectNode().get(sb.toString());

			if (openedSchemaNodes.get(openedSchemaNodes.size() - 1).equals(toClose)) {
				openedSchemaNodes.remove(openedSchemaNodes.size() - 1);
			} else if (log.isWarnEnabled()) {
				log.warn("unexpected");
			}

			// write previous repeated before closing
			if (toClose.isArray()) {
				List<String> vizMembers = visitedMembers.get(toClose);
				String lastProp = vizMembers.get(vizMembers.size() - 1);
				int lastPropIdx = toClose.indexOfMember(lastProp);
				writeReapeatedValues(toClose, lastPropIdx + 1, toClose.getMembers().size());

				// clear visited member cache for closed node
				vizMembers.clear();
			}

			if (toClose.isObject()) {
				writer.write("}\n");
			} else {
				writer.write("}]\n");
			}

			if (log.isDebugEnabled()) {
				log.debug("\t\tclosing " + toClose.getType().getName() + " path: " + sb.toString());
			}
		}
	}

	private void openPathSegments(String[] pathSegments, int from) throws IOException {
		for (int i = from; i < pathSegments.length; i++) {
			StringBuilder sb = new StringBuilder(pathSegments[0]);
			StringBuilder parentPath = new StringBuilder(pathSegments[0]);
			for (int j=1; j <= i; j++) {
				sb.append(".").append(pathSegments[j]);
				if (j < i) {
					parentPath.append(".").append(pathSegments[j]);
				}
			}

			SchemaNode parent = jsonSchema.getPathToObjectNode().get(parentPath.toString());
			String currentProperty = pathSegments[i];
			boolean foundPreviousRepeated = false;

			ArrayList<String> vizMembers = visitedMembers.get(parent);
			String lastVisitedProp = null;
			int lastVisitedPropIdx = -1;
			int currentPropIdx = parent.indexOfMember(currentProperty);

			if (vizMembers != null && vizMembers.size() > 0) {
				lastVisitedProp = vizMembers.get(vizMembers.size() - 1);
				lastVisitedPropIdx = parent.indexOfMember(lastVisitedProp);
			}

			// before opening new path, check if previous has repeated values to be written
			if (parent.isArray()) {
				if (lastVisitedProp != null) {
					foundPreviousRepeated = writeReapeatedValues(parent, lastVisitedPropIdx + 1, currentPropIdx, false);
				} else {
					vizMembers = new ArrayList<>();
					visitedMembers.put(parent, vizMembers);
				}

				vizMembers.add(currentProperty);
			}

			if (foundPreviousRepeated ||
					// got another property of the same object
					(lastVisitedPropIdx != -1 && currentPropIdx > lastVisitedPropIdx)) {
				writer.write(",");
			}

			if (escapeMembers) {
				writer.write("\"" + currentProperty + "\":");
			} else {
				writer.write(currentProperty + ":");
			}

			SchemaNode toOpen = jsonSchema.getPathToObjectNode().get(sb.toString());

			openedSchemaNodes.add(toOpen);

			if (toOpen.isObject()) {
				writer.write("{");
			} else {
				writer.write("[{");
			}

			if (log.isDebugEnabled()) {
				log.debug("\t\topening " + toOpen.getType().getName() + " path: " + sb.toString());
			}
		}
	}

	public void closeOpenNodes() throws IOException {
		if (openedSchemaNodes.size() == 0) {
			return;
		}

		SchemaNode toClose;
		for (int i = openedSchemaNodes.size() - 1; i >= 0; i--) {
			toClose = openedSchemaNodes.get(i);
			if (toClose.isArray()) {
				// write previous repeated before closing
				List<String> vizMembers = visitedMembers.get(toClose);

				String lastProp = vizMembers.get(vizMembers.size() - 1);
				int lastPropIdx = toClose.indexOfMember(lastProp);
				writeReapeatedValues(toClose, lastPropIdx + 1, toClose.getMembers().size());

				// clear visited member cache for closed node
				vizMembers.clear();

				writer.write("}]");
			} else {
				writer.write("}");
			}

			if (log.isDebugEnabled()) {
				log.debug("closing " + toClose.getType().getName() + " path: " + (toClose.getPath().length() > 0 ? toClose.getPath() + "." : "") + toClose.getName());
			}
		}
	}

	private void initJson(String firstPath, Object firstValue, boolean repeatValue) throws IOException {
		if (log.isDebugEnabled()) {
			log.debug("Initializing JSON with first absolute path: " + firstPath);
		}
		String[] segments = firstPath.split("\\.");

		String currentPath = "";
		SchemaNode schemaNode = null;
		int i;

		for (i=0; i < segments.length - 1; i++) {
			currentPath = currentPath.length() > 0 ? currentPath + "." + segments[i] : segments[i];
			schemaNode = jsonSchema.getPathToObjectNode().get(currentPath);

			openedSchemaNodes.add(schemaNode);

			if (i == 0) { // got root node
				if (schemaNode.isObject()) {
					writer.write("{");
				} else {
					writer.write("[{");
				}
			} else {
				String parentPath = currentPath.substring(0, currentPath.lastIndexOf("."));
				SchemaNode parent = jsonSchema.getPathToObjectNode().get(parentPath);
				String currentProperty = segments[i];

				ArrayList<String> vizMembers = new ArrayList<>();
				vizMembers.add(currentProperty);
				visitedMembers.put(parent, vizMembers);

				if (schemaNode.isObject()) {
					if (escapeMembers) {
						writer.write("\"" + currentProperty + "\": {");
					} else {
						writer.write(currentProperty + ": {");
					}
				} else {
					if (escapeMembers) {
						writer.write("\"" + currentProperty + "\": [{");
					} else {
						writer.write(currentProperty + ": [{");
					}
				}
			}
		}

		if (escapeMembers) {
			writer.write("\"" + segments[i] + "\": ");
		} else {
			writer.write(segments[i] + ": ");
		}
		writeValue(firstValue);

		// mark repeated value
		if (schemaNode != null && repeatValue) {
			SchemaNodeMember nodeMember = schemaNode.getMember(segments[i]);
			nodeMember.setRepeatValue(true);
			nodeMember.setPreviousValue(firstValue);
		}

		// mark visited property for current node
		ArrayList<String> members = new ArrayList<>();
		members.add(segments[i]);
		visitedMembers.put(schemaNode, members);
	}

	private void writeValue(Object value)throws IOException {
		if (value != null) {
			if (
					value instanceof Number
							|| value instanceof Boolean
			)
			{
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
