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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonMetadataSchemaFileTest {

	private static final Log log = LogFactory.getLog(JsonMetadataSchemaFileTest.class);

    private RepositoryUtil repoUtil;
	private ObjectMapper objectMapper;
	private String expectedJsonOutput;

    @BeforeClass
    public void setUp () {
		repoUtil = RepositoryUtil.getInstance(DefaultJasperReportsContext.getInstance());

		// Construct the same mapper that is used to read the JSON schema
		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
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
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			isValid = true;
		} catch (JRException e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}

			isValid = false;
		}

        assert isValid;
		assert jsonSchema.getPathToSchemaNodeMap().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".city");
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".city").getType().equals(NodeTypeEnum.OBJECT);

		assert jsonSchema.getPathToSchemaNodeMap().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products");
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getMembers().size() == 4;

		assert jsonSchema.getPathToSchemaNodeMap().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers");
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getMembers().size() == 2;
	}

	@Test
	public void validateSchema_2() throws JRException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema2.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();

		boolean isValid = true;
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());
		} catch (JRException e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage());
			}

			isValid = false;
		}

		assert !isValid;
	}

	@BeforeMethod
	public void expectedResult(Method method) throws JRException {
		String methodName = method.getName();
		String pathPrefix = "net/sf/jasperreports/export/json/expectedResultFor_Schema";
		String methodPrefix = "validateJsonForSchema_";
		if (methodName.startsWith(methodPrefix)) {
			String filePath = pathPrefix + methodName.substring(methodPrefix.length())+ ".json";
			Scanner scanner = new Scanner(repoUtil.getInputStreamFromLocation(filePath), StandardCharsets.UTF_8.name());

			expectedJsonOutput = scanner.useDelimiter("\\A").next();
		}
	}

	@Test
	public void validateJsonForSchema_3() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema3.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor(jsonSchema);
			StringWriter sw = new StringWriter();
			jsonProcessor.setWriter(sw);

			jsonProcessor.processElement(() -> "1", "product.id", false);
			jsonProcessor.processElement(() -> "2", "product.id", false);
			jsonProcessor.closeOpenNodes();

			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Test
	public void validateJsonForSchema_4() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema4.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor(jsonSchema);
			StringWriter sw = new StringWriter();
			jsonProcessor.setWriter(sw);

			jsonProcessor.processElement(() -> "id_1", "products.details.id", false);
			jsonProcessor.processElement(() -> "name_1", "products.details.name", false);
			jsonProcessor.processElement(() -> "id_2", "products.details.id", false);
			jsonProcessor.closeOpenNodes();

			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Test
	public void validateJsonForSchema_5() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema5.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor(jsonSchema);
			StringWriter sw = new StringWriter();
			jsonProcessor.setWriter(sw);

			jsonProcessor.processElement(() -> "id_1", "products.details.id", false);
			jsonProcessor.processElement(() -> "name_1", "products.details.name", false);
			jsonProcessor.processElement(() -> "order_id_1", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_2", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_3", "products.orderId", false);
			jsonProcessor.processElement(() -> "id_2", "products.details.id", false);
			jsonProcessor.processElement(() -> "order_id_4", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_5", "products.orderId", false);
			jsonProcessor.closeOpenNodes();

			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Test
	public void validateJsonForSchema_6() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema6.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor(jsonSchema);
			StringWriter sw = new StringWriter();
			jsonProcessor.setWriter(sw);

			jsonProcessor.processElement(() -> "id_1", "products.details.id", false);
			jsonProcessor.processElement(() -> "name_1", "products.details.name", false);
			jsonProcessor.processElement(() -> "order_id_1", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_2", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_3", "products.orderId", false);
			jsonProcessor.processElement(() -> "id_2", "products.details.id", false);
			jsonProcessor.processElement(() -> "order_id_4", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_5", "products.orderId", false);
			jsonProcessor.closeOpenNodes();

			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Test
	public void validateJsonForSchema_7() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/json/TestSchema7.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			JsonMetadataProcessor jsonProcessor = new JsonMetadataProcessor(jsonSchema);
			jsonProcessor.setEscapeMembers(true);
			StringWriter sw = new StringWriter();
			jsonProcessor.setWriter(sw);

			jsonProcessor.processElement(() -> "id_1", "products.details.id", false);
			jsonProcessor.processElement(() -> "name_1", "products.details.name", false);
			jsonProcessor.processElement(() -> "order_id_1", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_2", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_3", "products.orderId", false);
			jsonProcessor.processElement(() -> "id_2", "products.details.id", false);
			jsonProcessor.processElement(() -> "order_id_4", "products.orderId", false);
			jsonProcessor.processElement(() -> "order_id_5", "products.orderId", false);
			jsonProcessor.closeOpenNodes();

			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
