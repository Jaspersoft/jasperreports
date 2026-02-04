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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.*;

public class JsonSchema {

	private static final Log log = LogFactory.getLog(JsonSchema.class);

	protected static final String EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT = "export.json.invalid.json.object";
	protected static final String EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT_SEMANTIC = EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT + ".semantic";
	protected static final String EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT_ARRAY_FOUND = EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT + ".array.found";

	public static final String JSON_SCHEMA_ROOT_NAME = "___root";

	private final Map<String, SchemaNode> pathToValueNode = new HashMap<>();
	private final Map<String, SchemaNode> pathToObjectNode = new HashMap<>();

	private boolean isInitialized = false;

	public void validateSchema(String jsonSchema) throws JRException {
		ObjectMapper mapper = new ObjectMapper();

		// relax the JSON rules
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		try {
			JsonNode root = mapper.readTree(jsonSchema);
			if (root.isObject()) {
				if (!isValid((ObjectNode) root, JSON_SCHEMA_ROOT_NAME, "", null)) {
					throw
							new JRException(
									EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT_SEMANTIC,
									(Object[])null
							);
				}
				isInitialized = true;
			} else {
				throw
						new JRException(
								EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT_ARRAY_FOUND,
								(Object[])null
						);
			}

		} catch (IOException e) {
			throw
					new JRException(
							EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT,
							(Object[])null
					);
		}
	}

	private boolean isValid(ObjectNode objectNode, String objectName, String currentPath, SchemaNode parent) {
		String nodeTypeValue = null;
		JsonNode typeNode = objectNode.path("_type");

		if (typeNode.isMissingNode()) {
			nodeTypeValue = "object";
		} else if (!typeNode.isTextual()) {
			return false;
		}

		if (nodeTypeValue == null) {
			nodeTypeValue = typeNode.asText();
		}

		NodeTypeEnum nodeType = NodeTypeEnum.getByName(nodeTypeValue);

		// enforce type "object" or "array" with "_children"
		if (!(NodeTypeEnum.OBJECT.equals(nodeType) || (NodeTypeEnum.ARRAY.equals(nodeType) && objectNode.has("_children")))) {
			return false;
		}

		// enforce "_children" of type Object when typeNode is "array"
		if (NodeTypeEnum.ARRAY.equals(nodeType) && !objectNode.path("_children").isObject()) {
			return false;
		}

		boolean result = true;
		String availablePath = currentPath;
		SchemaNode schemaNode;

		availablePath = availablePath.length() > 0 ? (availablePath.endsWith(".") ? availablePath : availablePath + ".") + objectName : objectName;

		// _children properties are passed to the parent array
		if (parent != null) {
			schemaNode = parent;
		} else {

			int level;

			if (JSON_SCHEMA_ROOT_NAME.equals(objectName)) {
				level = 0;
			} else if (availablePath.length() > 0 && availablePath.indexOf(".") > 0) {
				level = availablePath.split("\\.").length - 1;
			} else {
				level = 1;
			}

			schemaNode = new SchemaNode(level, objectName, nodeType, currentPath.endsWith(".") ? currentPath.substring(0, currentPath.length() - 2) : currentPath);
			pathToObjectNode.put(availablePath, schemaNode);
		}

		Iterator<String> it = objectNode.fieldNames();
		while (it.hasNext()) {
			String field = it.next();
			JsonNode node = objectNode.path(field);
			String localPath = availablePath;

			if (!field.startsWith("_")) {
				schemaNode.addMember(field);
				if (node.isTextual() && node.asText().equals("value")) {
					localPath = localPath.length() > 0 ? (localPath.endsWith(".") ? localPath : localPath + ".") + field : field;
					pathToValueNode.put(localPath, schemaNode);
				} else if ((node.isObject() && !isValid((ObjectNode) node, field, availablePath, null)) || !node.isObject()) {
					result = false;
					break;
				}
			} else if (field.equals("_children") && !isValid((ObjectNode) node, "", availablePath, schemaNode)) {
				result = false;
				break;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("object is valid: " + objectNode);
			log.debug("objectName: " + objectName);
			log.debug("currentPath: " + currentPath);
		}

		return result;
	}

	public void prepareSchema(String absolutePath) {
		if (!pathToValueNode.containsKey(absolutePath)) {
			String valueProperty = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
			String[] objectPathSegments = absolutePath.substring(0, absolutePath.lastIndexOf(".")).split("\\.");
			SchemaNode node = null;

			for (int i = 0; i < objectPathSegments.length; i++) {
				StringBuilder objectPath = new StringBuilder(objectPathSegments[0]);
				for (int j = 1; j <= i; j++) {
					objectPath.append(".").append(objectPathSegments[j]);
				}

				if (!pathToObjectNode.containsKey(objectPath.toString())) {
					String schemaNodePath = "";

					for (int k = 0; k < i; k++) {
						schemaNodePath += schemaNodePath.length() > 0 ? "." + objectPathSegments[k] : objectPathSegments[k];
					}

					node = new SchemaNode(i, objectPathSegments[i], NodeTypeEnum.ARRAY, schemaNodePath);


					pathToObjectNode.put(objectPath.toString(), node);
				} else {
					node = pathToObjectNode.get(objectPath.toString());
				}

				if (i < objectPathSegments.length - 1 && node.getMember(objectPathSegments[i+1]) == null) {
					node.addMember(objectPathSegments[i+1]);
				}
			}

			node.addMember(valueProperty);
			pathToValueNode.put(absolutePath, node);
		}
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public Map<String, SchemaNode> getPathToValueNode() {
		return pathToValueNode;
	}

	public Map<String, SchemaNode> getPathToObjectNode() {
		return pathToObjectNode;
	}

}
