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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.json.export.schema.JsonMetadataProcessor;
import net.sf.jasperreports.json.export.schema.JsonSchema;
import net.sf.jasperreports.json.export.schema.NodeTypeEnum;
import net.sf.jasperreports.json.export.schema.SchemaNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonMetadataDynamicSchemaTest {

	private static final Log log = LogFactory.getLog(JsonMetadataDynamicSchemaTest.class);

    @Test
    public void validateSchema() {
		JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor();
		JsonSchema jsonSchema = jsonProcessor.getJsonSchema();

		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.id");
		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.name");
		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.price");
		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.name");
		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.address");
		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.address.street");

		if (log.isDebugEnabled()) {
			for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToObjectNode().entrySet()) {
				log.debug("pathToObjectNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
			}

			for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToValueNode().entrySet()) {
				log.debug("pathToValueNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
			}
		}

		assert jsonSchema.getPathToObjectNode().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products");
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToValueNode().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.name");
		assert jsonSchema.getPathToValueNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.name").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getMembers().size() == 3;

		assert jsonSchema.getPathToObjectNode().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers");
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToValueNode().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.name");
		assert jsonSchema.getPathToValueNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.name").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getMembers().size() == 2;
	}

	@Test
	public void buildJSON() throws IOException {
		JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor();
		JsonSchema jsonSchema = jsonProcessor.getJsonSchema();

		StringWriter sw = new StringWriter();
		jsonProcessor.setWriter(sw);

		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.e");
		jsonProcessor.processElement("value_1", JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.e", true);

		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.f");
		jsonProcessor.processElement( "value_2", JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.f", false);

		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.g.h");
		jsonProcessor.processElement("value_3", JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.g.h", false);

		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.i");
		jsonProcessor.processElement("value_4", JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.i", true);

		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.g.h");
		jsonProcessor.processElement("value_5", JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.b.c.d.g.h", false);

		jsonSchema.prepareSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.j.k");
		jsonProcessor.processElement("value_6", JsonSchema.JSON_SCHEMA_ROOT_NAME + ".a.j.k", false);

		jsonProcessor.closeOpenNodes();

		// Construct same mapper that is used to read the JSON schema
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

		boolean isValid;
		try {
			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + sw);
			}
			mapper.readTree(generatedJson);
			isValid = true;
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage());
			}
			isValid = false;
		}

		assert isValid;
	}

}
