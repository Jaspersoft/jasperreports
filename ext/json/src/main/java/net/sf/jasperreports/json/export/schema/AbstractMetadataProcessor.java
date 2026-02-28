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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public abstract class AbstractMetadataProcessor implements MetadataProcessor {
	private static final Log log = LogFactory.getLog(AbstractMetadataProcessor.class);

	private final JsonSchema jsonSchema;
	protected final MetadataWriter metadataWriter;

	private final Map<String, ArrayList<String>> pathToVisitedMembers = new HashMap<>();
	private final ArrayList<SchemaNode> openedSchemaNodes = new ArrayList<>();

	protected AbstractMetadataProcessor(JsonSchema jsonSchema, MetadataWriter metadataWriter) {
		this.jsonSchema = jsonSchema;
		this.metadataWriter = metadataWriter;
	}

	@Override
	public JsonSchema getJsonSchema() {
		return jsonSchema;
	}

	public void processElement(Supplier<Object> valueSupplier, String valuePath, boolean repeatValue) throws IOException {
		// convert valuePath to absolute valuePath for internal reference
		String absoluteValuePath = JsonSchema.JSON_SCHEMA_ROOT_NAME + "." + valuePath;

		if (log.isDebugEnabled()) {
			log.debug("processElement absoluteValuePath: " + absoluteValuePath);
		}

		// try to add valuePath to schema; it may not be added
		jsonSchema.addPathToSchema(absoluteValuePath);

		// current valuePath must point to a SchemaNode with type Value
		SchemaNode valueNode = jsonSchema.getSchemaNode(absoluteValuePath);
		if (valueNode == null || !valueNode.isValue()) {
			if (log.isWarnEnabled()) {
				if (valueNode == null) {
					log.warn("\tNo schema node for path: " + valuePath + ". Skipping!");
				} else {
					log.warn("\tSupplied path does not point to a value node: " + valuePath + ". Skipping!");
				}
			}

			// nothing to do
			return; // FIXME: should we just continue or throw an exception here?
		}

		Object value = valueSupplier.get();

		List<SchemaNode> toClose = new ArrayList<>();
		List<SchemaNode> toOpen = new ArrayList<>();

		// always add the top most value node to the toOpen list
		toOpen.add(valueNode);

		SchemaNode currentNode = valueNode;
		String parentPath = currentNode.getParentPath();
		if (log.isDebugEnabled()) {
			log.debug("\tStart processing from parentPath: "  + parentPath);
		}

		boolean isSafeToWriteCurrentValue = true;
		while (parentPath != null) {
			SchemaNode parentNode = jsonSchema.getSchemaNode(parentPath);
			if (openedSchemaNodes.contains(parentNode)) {
				// when it's not safe to open a new object for a non-array parent try to find a better parent instead of
				// dropping the current value for valuePath
				if (isSafeToWriteKeyToNode(currentNode.getKey(), parentNode)) {
					int level = parentNode.getLevel();

					// mark for closing all opened nodes after our parentNode
					if (openedSchemaNodes.size() > level + 1) {
						for (int i = level + 1; i < openedSchemaNodes.size(); i++) {
							toClose.add(openedSchemaNodes.get(i));
						}
					}

					isSafeToWriteCurrentValue = true;

					// we're done searching
					break;
				} else {
					isSafeToWriteCurrentValue = false;
				}
			}

			// the parentNode is not opened yet; mark it for opening
			toOpen.add(parentNode);

			currentNode = parentNode;
			parentPath = currentNode.getParentPath();
		}

		if (!isSafeToWriteCurrentValue) {
			if (log.isWarnEnabled()) {
				log.warn("Not safe to write value for path: " + valuePath + ". Skipping!");
			}
			// no point continuing
			return;
		}

		// start closing from the deepest node
		Collections.reverse(toClose);
		for (SchemaNode node2close: toClose) {
			if (log.isDebugEnabled()) {
				log.debug("\tclosing node: " + node2close.getPath());
			}

			closeNode(node2close);

			openedSchemaNodes.remove(node2close);
		}

		// start opening from the top most node
		Collections.reverse(toOpen);
		for (SchemaNode node2open: toOpen) {
			if (log.isDebugEnabled()) {
				log.debug("\topening node: " + node2open.getPath());
			}

			openNode(node2open, value);

			openedSchemaNodes.add(node2open);
		}

		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Opened nodes: [ ");
			for (SchemaNode openedNode : openedSchemaNodes) {
				sb.append(openedNode.getPath()).append(", ");
			}
			sb.append("]");

			log.debug(sb.toString());
		}

		// set repeat/previous value for parent node's current key member
		SchemaNode parent = jsonSchema.getSchemaNode(valueNode.getParentPath());
		SchemaNodeMember member = parent.getMember(valueNode.getKey());
		member.setRepeatValue(repeatValue);
		member.setPreviousValue(value);
	}

	private boolean isSafeToWriteKeyToNode(String key, SchemaNode node) {
		List<String> visitedMembers = pathToVisitedMembers.get(node.getPath());
		if (visitedMembers != null) {
			int keyIndex = node.indexOfMember(key);
			String lastVisited = visitedMembers.get(visitedMembers.size() - 1);
			int lastVisitedIndex = node.indexOfMember(lastVisited);

			// we are trying to write to same object
			if (keyIndex > lastVisitedIndex) {
				return true;
			}
			// we should be opening a new object only if node is of type Array
			else {
				return node.isArray();
			}
		}

		return true;
	}

	private void openNode(SchemaNode node, Object value) throws IOException {
		String currentKey = node.getKey();
		String parentPath = node.getParentPath();
		boolean isSameObject = false;
		if (parentPath != null) {
			if (pathToVisitedMembers.containsKey(parentPath)) {
				isSameObject = true;
				SchemaNode parent = jsonSchema.getSchemaNode(parentPath);
				int currentKeyIndex = parent.indexOfMember(currentKey);

				List<String> visitedMembers = pathToVisitedMembers.get(parentPath);
				String lastVisited = visitedMembers.get(visitedMembers.size() - 1); // FIXME: visitedMembers should be just the last visited member, not an array
				int lastVisitedIndex = parent.indexOfMember(lastVisited);

				// we'll be adding to the same object
				if (currentKeyIndex > lastVisitedIndex) {
					metadataWriter.writeKeyValSeparator();
				}
				// we'll be adding to a new object
				else {
					// only for a parent array it makes sense to open a new object
					if (!parent.isArray()) {
						return; // FIXME: should we just continue or throw an exception here?
					}

					// before closing and starting a new object search for repeated values
					int allMembersSize = parent.getMembers().size();
					if (lastVisitedIndex < allMembersSize - 1) {
						// try to find repeated values since last visited index
						writePreviousValues(lastVisitedIndex + 1, allMembersSize, parent, true, false);
					}

					metadataWriter.closeAndStartNewObject(parent);

					// try to find repeated values up until current index
					writePreviousValues(0, currentKeyIndex, parent, false, true);
				}
			}
		}

		metadataWriter.writeNodeKey(node, isSameObject);

		if (node.isArray()) {
			metadataWriter.writeArrayStart(node);
		} else if (node.isObject()){
			metadataWriter.writeObjectStart(node);
		} else { // isValue
			metadataWriter.writeValue(node.getKey(), value, jsonSchema.getSchemaNode(parentPath)); // parent should not be null for value nodes
		}

		// mark visited for current node's parent
		ArrayList<String> visitedMembers;
		if (pathToVisitedMembers.containsKey(parentPath)) {
			visitedMembers = pathToVisitedMembers.get(parentPath);
		} else {
			visitedMembers = new ArrayList<>();
			pathToVisitedMembers.put(parentPath, visitedMembers);
		}
		visitedMembers.add(currentKey);
	}

	private void writePreviousValues(int from, int to, SchemaNode parent, boolean startWithSeparator, boolean endWithSeparator) throws IOException {
		List<SchemaNodeMember> membersToRepeat = new ArrayList<>();
		for (int i = from; i < to; i++) {
			SchemaNodeMember schemaMember = parent.getMember(i);
			if (schemaMember.isRepeatValue()) {
				membersToRepeat.add(schemaMember);
			}
		}

		if (!membersToRepeat.isEmpty()) {
			if (startWithSeparator) {
				metadataWriter.writeKeyValSeparator();
			}

			for (int i = 0; i < membersToRepeat.size(); i++) {
				SchemaNodeMember memberToRepeat = membersToRepeat.get(i);
				metadataWriter.writePreviousMemberValue(memberToRepeat, parent, true);
				if (endWithSeparator || i < membersToRepeat.size() - 1) {
					metadataWriter.writeKeyValSeparator();
				}
			}
		}
	}

	private void closeNode(SchemaNode node) throws IOException {
		if (!node.isValue()) {
			if (pathToVisitedMembers.containsKey(node.getPath())) {
				List<String> visitedMembers = pathToVisitedMembers.get(node.getPath());
				String lastVisited = visitedMembers.get(visitedMembers.size() - 1);
				int lastVisitedIndex = node.indexOfMember(lastVisited);
				int allMembersSize = node.getMembers().size();

				// if last visited member is not the last node member
				if (lastVisitedIndex < allMembersSize - 1) {
					// try to find the remaining repeated values
					writePreviousValues(lastVisitedIndex + 1, allMembersSize, node, true, false);
				}
			}
		}

		if (node.isArray()) {
			metadataWriter.writeArrayClosing(node);
		} else if (node.isObject()){
			metadataWriter.writeObjectClosing(node);
		} else {
			metadataWriter.writeValueClosing(node, jsonSchema.getSchemaNode(node.getParentPath()));
		}

		if (!node.isValue()) {
			pathToVisitedMembers.remove(node.getPath());
			// FIXME: for array nodes we should also clear the repeated values for each member
		}
	}

	public void closeOpenNodes() throws IOException {
		Collections.reverse(openedSchemaNodes);
		for (SchemaNode openedNode : openedSchemaNodes) {
			closeNode(openedNode);
		}

		// last line break after all nodes have been closed
		this.metadataWriter.getWriter().write("\n");
	}

}
