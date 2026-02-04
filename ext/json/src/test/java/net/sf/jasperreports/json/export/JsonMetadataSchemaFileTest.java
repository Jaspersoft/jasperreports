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

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.json.export.schema.JsonMetadataProcessor;
import net.sf.jasperreports.json.export.schema.JsonSchema;
import net.sf.jasperreports.json.export.schema.NodeTypeEnum;
import net.sf.jasperreports.json.export.schema.SchemaNode;
import net.sf.jasperreports.repo.RepositoryUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonMetadataSchemaFileTest {

	private static final Log log = LogFactory.getLog(JsonMetadataSchemaFileTest.class);

    private RepositoryUtil repoUtil;

    @BeforeClass
    public void readJson() {
		repoUtil = RepositoryUtil.getInstance(DefaultJasperReportsContext.getInstance());
    }

    @Test
    public void validateSchema_1() throws JRException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema1.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor();
		JsonSchema jsonSchema = jsonProcessor.getJsonSchema();

		boolean isValid;
		try {
			jsonSchema.validateSchema(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToObjectNode().entrySet()) {
					log.debug("pathToObjectNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
				}

				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToValueNode().entrySet()) {
					log.debug("pathToValueNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			isValid = true;
		} catch (JRException e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage());
			}

			isValid = false;
		}

        assert isValid;
		assert jsonSchema.getPathToObjectNode().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".city");
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".city").getType().equals(NodeTypeEnum.OBJECT);

		assert jsonSchema.getPathToObjectNode().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products");
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getMembers().size() == 4;

		assert jsonSchema.getPathToObjectNode().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers");
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToObjectNode().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getMembers().size() == 2;
	}

	@Test
	public void validateSchema_2() throws JRException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema2.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor();

		boolean isValid = true;
		try {
			jsonProcessor.getJsonSchema().validateSchema(scanner.useDelimiter("\\A").next());
		} catch (JRException e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage());
			}
			
			isValid = false;
		}

		assert !isValid;
	}

}
