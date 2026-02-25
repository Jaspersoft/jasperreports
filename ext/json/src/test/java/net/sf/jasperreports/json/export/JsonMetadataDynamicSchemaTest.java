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
import net.sf.jasperreports.json.export.schema.*;
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
public class JsonMetadataDynamicSchemaTest {

	private static final Log log = LogFactory.getLog(JsonMetadataDynamicSchemaTest.class);

	private RepositoryUtil repoUtil;
	private ObjectMapper objectMapper;
	private String expectedJsonOutput;

	@BeforeClass
	public void setUp() {
		repoUtil = RepositoryUtil.getInstance(DefaultJasperReportsContext.getInstance());

		// Construct the same mapper that is used to read the JSON schema
		objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
	}

    @Test
    public void validateSchema() {
		JsonSchema jsonSchema = new JsonSchema();

		jsonSchema.addPathToSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.id");
		jsonSchema.addPathToSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.name");
		jsonSchema.addPathToSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.price");
		jsonSchema.addPathToSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.name");
		jsonSchema.addPathToSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.address");
		jsonSchema.addPathToSchema(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.address.street");

		if (log.isDebugEnabled()) {
			for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
				log.debug("pathToSchemaNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
			}
		}

		assert jsonSchema.getPathToSchemaNodeMap().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products");
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToSchemaNodeMap().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.name");
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products.name").getType().equals(NodeTypeEnum.VALUE);
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".products").getMembers().size() == 3;

		assert jsonSchema.getPathToSchemaNodeMap().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers");
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getType().equals(NodeTypeEnum.ARRAY);
		assert jsonSchema.getPathToSchemaNodeMap().containsKey(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.name");
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers.name").getType().equals(NodeTypeEnum.VALUE);
		assert jsonSchema.getPathToSchemaNodeMap().get(JsonSchema.JSON_SCHEMA_ROOT_NAME + ".customers").getMembers().size() == 2;
	}

	@BeforeMethod
	public void expectedResult(Method method) throws JRException {
		String methodName = method.getName();
		String pathPrefix = "net/sf/jasperreports/export/json/expectedResultFor_";
		String methodPrefix = "buildJSON_";
		if (methodName.startsWith(methodPrefix)) {
			String filePath = pathPrefix + methodName + ".json";
			Scanner scanner = new Scanner(repoUtil.getInputStreamFromLocation(filePath), StandardCharsets.UTF_8.name());

			expectedJsonOutput = scanner.useDelimiter("\\A").next();
		}
	}

	@Test
	public void buildJSON_1() throws IOException {
		StringWriter sw = new StringWriter();
		MetadataProcessor jsonProcessor = new JsonMetadataProcessor(sw);

		jsonProcessor.processElement(() -> "value_1", "a.b.c.d.e", true);
		jsonProcessor.processElement(() -> "value_2", "a.b.c.d.f", false);
		jsonProcessor.processElement(() -> "value_3", "a.b.c.d.g.h", false);
		jsonProcessor.processElement(() -> "value_4", "a.b.c.d.i", true);
		jsonProcessor.processElement(() -> "value_5", "a.b.c.d.g.h", false);
		jsonProcessor.processElement(() -> "value_6", "a.j.k", false);

		jsonProcessor.closeOpenNodes();
		boolean isValid;
		try {
			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));

			isValid = true;
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage());
			}
			isValid = false;
		}

		assert isValid;
	}

	@Test
	public void buildJSON_2() throws IOException {
		StringWriter sw = new StringWriter();
		MetadataProcessor jsonProcessor = new JsonMetadataProcessor(sw);

		jsonProcessor.processElement(() -> "value_1", "a.b.c.d.e", false);
		jsonProcessor.processElement(() -> "value_2", "a.b.c.d.f", true);
		jsonProcessor.processElement(() -> "value_3", "a.b.c.d.g.h", false);
		jsonProcessor.processElement(() -> "value_4", "a.b.c.d.i", false);
		jsonProcessor.processElement(() -> "value_5", "a.b.c.d.j", true);
		jsonProcessor.processElement(() -> "value_6", "a.b.c.d.g.h", false);
		jsonProcessor.processElement(() -> "value_7", "a.k.l", false);

		jsonProcessor.closeOpenNodes();
		boolean isValid;
		try {
			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));

			isValid = true;
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage());
			}
			isValid = false;
		}

		assert isValid;
	}

	@Test
	public void buildJSON_3() throws IOException {
		StringWriter sw = new StringWriter();
		MetadataProcessor jsonProcessor = new JsonMetadataProcessor(sw);

		jsonProcessor.processElement(() -> "value_1", "a.b.c.d.e", true);
		jsonProcessor.processElement(() -> "value_2", "a.b.c.d.f", false);
		jsonProcessor.processElement(() -> "value_3", "a.b.c.d.g.h", false);
		jsonProcessor.processElement(() -> "value_4", "a.b.c.d.i", true);
		jsonProcessor.processElement(() -> "value_5", "a.b.c.d.g.h", false);
		jsonProcessor.processElement(() -> "value_6", "a.j.k", false);
		jsonProcessor.processElement(() -> "value_7", "a.b.c.d.e", true);

		jsonProcessor.closeOpenNodes();
		boolean isValid;
		try {
			String generatedJson = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated JSON:\n" + generatedJson);

				log.debug("The Expected JSON:\n" + expectedJsonOutput);
			}

			assert objectMapper.readTree(generatedJson).equals(objectMapper.readTree(expectedJsonOutput));

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
