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


/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonSchema {

	private static final Log log = LogFactory.getLog(JsonSchema.class);

	protected static final String EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT = "export.json.invalid.json.object";
	protected static final String EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT_SEMANTIC = EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT + ".semantic";
	protected static final String EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT_ARRAY_FOUND = EXCEPTION_MESSAGE_KEY_INVALID_JSON_OBJECT + ".array.found";

	private static final String TYPE_KEY = "_type";
	private static final String CHILDREN_KEY = "_children";
	private static final String[] OBJECT_NODE_RESERVED_KEYS = new String[] { TYPE_KEY }; // FIXME: Use these reserved keys objects
	private static final String[] ARRAY_NODE_RESERVED_KEYS = new String[] { TYPE_KEY, CHILDREN_KEY };
	public static final String JSON_SCHEMA_ROOT_NAME = "___root";

	private final Map<String, SchemaNode> pathToSchemaNodeMap = new LinkedHashMap<>();
	private final Set<String> invalidSchemaPaths = new HashSet<>();

	private boolean isInitialized = false;

	public JsonSchema() {
	}

	public void initialize(String jsonSchema) throws JRException {
		ObjectMapper mapper = new ObjectMapper();

		// relax the JSON rules
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		try {
			JsonNode root = mapper.readTree(jsonSchema);
			if (root.isObject()) {
				if (!isValid((ObjectNode) root, JSON_SCHEMA_ROOT_NAME, JSON_SCHEMA_ROOT_NAME, null)) {
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

	private String getPaddedPrefix(String jsonPath) {
		int segmentCount = jsonPath.split("\\.").length - 1;
		return String.format(">>>%" + (segmentCount == 0 ? 1 : segmentCount * 4) + "s", "");
	}

	private boolean isValid(ObjectNode objectNode, String currentSchemaPath, String realJsonPath, SchemaNode parent) {
		if (log.isDebugEnabled()) {
			log.debug(getPaddedPrefix(realJsonPath) + "validating real JSON path: " + realJsonPath);
			log.debug(getPaddedPrefix(realJsonPath) + "objectNode: " + objectNode);
		}

		String nodeTypeValue = null;
		JsonNode typeNode = objectNode.path(TYPE_KEY);

		if (typeNode.isMissingNode()) {
			nodeTypeValue = "object";
		} else if (!typeNode.isTextual()) {
			return false; // FIXME: log reason why it's invalid
		}

		if (nodeTypeValue == null) {
			nodeTypeValue = typeNode.asText();
		}

		NodeTypeEnum nodeType = NodeTypeEnum.getByName(nodeTypeValue);

		// enforce type "object" or "array" with "_children" of type Object
		if (!(
				NodeTypeEnum.OBJECT.equals(nodeType) || (
						NodeTypeEnum.ARRAY.equals(nodeType) &&
								objectNode.has(CHILDREN_KEY) &&
								objectNode.path(CHILDREN_KEY).isObject()
				)
		)) {
			if (log.isErrorEnabled()) {
				log.error("Key _type could be either missing or 'object' or 'array' with '_children': {}");
			}
			return false;
		}

		boolean result = true;
		SchemaNode schemaNode;

		if (parent != null) {
			schemaNode = parent;
		} else {
			int level = currentSchemaPath.split("\\.").length;
			if (log.isDebugEnabled()) {
				log.debug(getPaddedPrefix(realJsonPath) + "creating node of type: " + nodeType + " for schema path: " + currentSchemaPath);
			}

			String parentPath = null;
			String currentKey = currentSchemaPath;
			if (level > 1) {
				parentPath = currentSchemaPath.substring(0, currentSchemaPath.lastIndexOf("."));
				currentKey = currentSchemaPath.substring(currentSchemaPath.lastIndexOf(".") + 1);
			}

			schemaNode = new SchemaNode(level - 1, currentSchemaPath, parentPath, nodeType, currentKey);
			pathToSchemaNodeMap.put(currentSchemaPath, schemaNode);
		}

		if (NodeTypeEnum.OBJECT.equals(nodeType)) {
			Iterator<String> it = objectNode.fieldNames();
			while (it.hasNext()) {
				String field = it.next();

				if (log.isDebugEnabled()) {
					log.debug(getPaddedPrefix(realJsonPath) + "found field: " + field);
				}

				// For object nodes consider only fields that don't start with _ // FIXME: consider allowing fields that start with _ and are different from _type and other internal reserved words
				if (!field.startsWith("_")) {
					JsonNode node = objectNode.path(field);
					String localPath = currentSchemaPath + "." + field;

					schemaNode.addMember(field);
					if (node.isTextual() && node.asText().equals("value")) {
						if (log.isDebugEnabled()) {
							log.debug(getPaddedPrefix(realJsonPath) + "adding value node for schema path: " + localPath);
						}
						pathToSchemaNodeMap.put(localPath, new SchemaNode(schemaNode.getLevel() + 1, localPath, currentSchemaPath, NodeTypeEnum.VALUE, field));
					} else if (node.isObject()) {
						if (log.isDebugEnabled()) {
							log.debug(getPaddedPrefix(realJsonPath) + "validating object node on real path: " + realJsonPath + "." + field);
						}
						if (!isValid((ObjectNode) node, localPath, realJsonPath + "." + field, null)) {
							result = false;
							break;
						}
					} else {
						result = false;
						break;
					}
				}
			}
		}

		// For array nodes process only the _children field which has already been validated
		if (NodeTypeEnum.ARRAY.equals(nodeType)) {
			if (log.isDebugEnabled()) {
				log.debug(getPaddedPrefix(realJsonPath) + "stepping into real path: " + realJsonPath + "." + CHILDREN_KEY);
			}

			if (!isValid((ObjectNode) objectNode.path(CHILDREN_KEY),
					currentSchemaPath, realJsonPath + "." + CHILDREN_KEY, schemaNode)) {
				result = false;
			}
		}

		return result;
	}

	public void addPathToSchema(String absoluteValuePath) {
		if (isInitialized ||
				pathToSchemaNodeMap.containsKey(absoluteValuePath) ||
				invalidSchemaPaths.contains(absoluteValuePath)) {
			// nothing to do
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Preparing schema for value node: " + absoluteValuePath);
		}

		String[] pathSegments = absoluteValuePath.split("\\.");
		SchemaNode previousNode = null;
		for (int i = 0; i < pathSegments.length; i++) {
			if (previousNode != null && previousNode.isValue()) {
				if (log.isWarnEnabled()) {
					log.warn("Stepping into a value node - " + previousNode.getKey() + " -  is not allowed. " +
							"Further keys will be dropped!");
				}
				invalidSchemaPaths.add(absoluteValuePath);
				break; // FIXME: should we just continue or throw an exception here?
			}

			StringBuilder sb = new StringBuilder(pathSegments[0]);
			for (int j = 1; j <= i; j++) {
				sb.append(".").append(pathSegments[j]);
			}

			String schemaPath = sb.toString();
			SchemaNode currenNode;
			if (!pathToSchemaNodeMap.containsKey(schemaPath)) {
				if (log.isDebugEnabled()) {
					log.debug("\t>>> Adding schema node for object path: " + schemaPath);
				}

				// last segment points to value node
				NodeTypeEnum schemaNodeType = (i == pathSegments.length -1) ? NodeTypeEnum.VALUE : NodeTypeEnum.ARRAY;

				String parentPath = null;
				if (i > 0) {
					parentPath = schemaPath.substring(0, schemaPath.lastIndexOf("."));
				}

				currenNode = new SchemaNode(i, schemaPath, parentPath, schemaNodeType, pathSegments[i]);
				pathToSchemaNodeMap.put(schemaPath, currenNode);
			} else {
				currenNode = pathToSchemaNodeMap.get(schemaPath);
			}

			// try to add members by looking ahead inside the pathSegments
			if (i < pathSegments.length - 1) {
				// Value nodes cannot have further members
				if (!currenNode.isValue()) {
					String member = pathSegments[i + 1];
					if (currenNode.getMember(member) == null) {
						if (log.isDebugEnabled()) {
							log.debug("\t\t>>> Adding member: " + member + " to path: " + schemaPath);
						}
						currenNode.addMember(member);
					}
				}
			}

			previousNode = currenNode;
		}
	}

	public SchemaNode getSchemaNode(String path) {
		return pathToSchemaNodeMap.get(path);
	}

	public Map<String, SchemaNode> getPathToSchemaNodeMap() {
		return pathToSchemaNodeMap;
	}

}
