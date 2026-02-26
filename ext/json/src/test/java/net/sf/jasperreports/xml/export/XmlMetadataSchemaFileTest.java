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
package net.sf.jasperreports.xml.export;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import net.sf.jasperreports.json.export.schema.*;
import net.sf.jasperreports.repo.RepositoryUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class XmlMetadataSchemaFileTest {

	private static final Log log = LogFactory.getLog(XmlMetadataSchemaFileTest.class);

    private RepositoryUtil repoUtil;
	private String expectedXmlOutput;
	private DocumentBuilder documentBuilder;

    @BeforeClass
    public void setUp() throws JRException {
		repoUtil = RepositoryUtil.getInstance(DefaultJasperReportsContext.getInstance());
		documentBuilder = JRXmlUtils.createDocumentBuilder();
    }

	@BeforeMethod
	public void expectedResult(Method method) throws JRException {
		String methodName = method.getName();
		String pathPrefix = "net/sf/jasperreports/export/xml/expectedResultFor_Schema";
		String methodPrefix = "validateJsonForSchema_";
		if (methodName.startsWith(methodPrefix)) {
			String filePath = pathPrefix + methodName.substring(methodPrefix.length())+ ".xml";
			Scanner scanner = new Scanner(repoUtil.getInputStreamFromLocation(filePath), StandardCharsets.UTF_8.name());

			expectedXmlOutput = scanner.useDelimiter("\\A").next();
		}
	}

	@Test
	public void validateJsonForSchema_1() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/xml/TestSchema1.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		boolean isValid;
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			StringWriter sw = new StringWriter();
			MetadataProcessor xmlProcessor = new XmlMetadataProcessor(jsonSchema, sw);
			xmlProcessor.getMetadataWriter().writeHeader();

			xmlProcessor.processElement(() -> "1", "product.id", false);
			xmlProcessor.processElement(() -> "2", "product.id", false);
			xmlProcessor.closeOpenNodes();

			String generatedXml = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated XML:\n" + generatedXml);

				log.debug("The Expected XML:\n" + expectedXmlOutput);
			}

			Document generatedDoc = documentBuilder.parse(new ByteArrayInputStream(generatedXml.getBytes(StandardCharsets.UTF_8)));
			Document expectedDoc = documentBuilder.parse(new ByteArrayInputStream(expectedXmlOutput.getBytes(StandardCharsets.UTF_8)));

			isValid = generatedDoc.isEqualNode(expectedDoc);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
			isValid = false;
		}

		assert isValid;
	}

	@Test
	public void validateJsonForSchema_2() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/xml/TestSchema2.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		boolean isValid;
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			StringWriter sw = new StringWriter();
			MetadataProcessor xmlProcessor = new XmlMetadataProcessor(jsonSchema, sw);
			xmlProcessor.getMetadataWriter().writeHeader();

			xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
			xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
			xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
			xmlProcessor.closeOpenNodes();

			String generatedXml = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated XML:\n" + generatedXml);

				log.debug("The Expected XML:\n" + expectedXmlOutput);
			}

			Document generatedDoc = documentBuilder.parse(new ByteArrayInputStream(generatedXml.getBytes(StandardCharsets.UTF_8)));
			Document expectedDoc = documentBuilder.parse(new ByteArrayInputStream(expectedXmlOutput.getBytes(StandardCharsets.UTF_8)));

			isValid = generatedDoc.isEqualNode(expectedDoc);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
			isValid = false;
		}

		assert isValid;
	}

	@Test
	public void validateJsonForSchema_3() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/xml/TestSchema3.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		boolean isValid;
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			StringWriter sw = new StringWriter();
			MetadataProcessor xmlProcessor = new XmlMetadataProcessor(jsonSchema, sw);
			xmlProcessor.getMetadataWriter().writeHeader();

			xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
			xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
			xmlProcessor.processElement(() -> "order_id_1", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_2", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_3", "products.orderId", false);
			xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
			xmlProcessor.processElement(() -> "order_id_4", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_5", "products.orderId", false);
			xmlProcessor.closeOpenNodes();

			String generatedXml = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated XML:\n" + generatedXml);

				log.debug("The Expected XML:\n" + expectedXmlOutput);
			}

			Document generatedDoc = documentBuilder.parse(new ByteArrayInputStream(generatedXml.getBytes(StandardCharsets.UTF_8)));
			Document expectedDoc = documentBuilder.parse(new ByteArrayInputStream(expectedXmlOutput.getBytes(StandardCharsets.UTF_8)));

			isValid = generatedDoc.isEqualNode(expectedDoc);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
			isValid = false;
		}

		assert isValid;
	}

	@Test
	public void validateJsonForSchema_4() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/xml/TestSchema4.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		boolean isValid;
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			StringWriter sw = new StringWriter();
			MetadataProcessor xmlProcessor = new XmlMetadataProcessor(jsonSchema, sw);
			xmlProcessor.getMetadataWriter().writeHeader();

			xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
			xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
			xmlProcessor.processElement(() -> "order_id_1", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_2", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_3", "products.orderId", false);
			xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
			xmlProcessor.processElement(() -> "order_id_4", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_5", "products.orderId", false);
			xmlProcessor.closeOpenNodes();

			String generatedXml = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated XML:\n" + generatedXml);

				log.debug("The Expected XML:\n" + expectedXmlOutput);
			}

			Document generatedDoc = documentBuilder.parse(new ByteArrayInputStream(generatedXml.getBytes(StandardCharsets.UTF_8)));
			Document expectedDoc = documentBuilder.parse(new ByteArrayInputStream(expectedXmlOutput.getBytes(StandardCharsets.UTF_8)));

			isValid = generatedDoc.isEqualNode(expectedDoc);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
			isValid = false;
		}

		assert isValid;
	}

	@Test
	public void validateJsonForSchema_5() throws JRException, IOException {
		Scanner scanner =
				new Scanner(
						repoUtil.getInputStreamFromLocation("net/sf/jasperreports/export/xml/TestSchema5.json"),
						StandardCharsets.UTF_8.name()
				);

		JsonSchema jsonSchema = new JsonSchema();
		boolean isValid;
		try {
			jsonSchema.initialize(scanner.useDelimiter("\\A").next());

			if (log.isDebugEnabled()) {
				for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
					log.debug("pathToSchemaNode: key: " + String.format("%-20s", entry.getKey()) + "; value: " + entry.getValue());
				}
			}

			StringWriter sw = new StringWriter();
			MetadataProcessor xmlProcessor = new XmlMetadataProcessor(jsonSchema, sw);
			xmlProcessor.getMetadataWriter().writeHeader();

			xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
			xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
			xmlProcessor.processElement(() -> "order_id_1", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_2", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_3", "products.orderId", false);
			xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
			xmlProcessor.processElement(() -> "order_id_4", "products.orderId", false);
			xmlProcessor.processElement(() -> "order_id_5", "products.orderId", false);
			xmlProcessor.closeOpenNodes();

			String generatedXml = sw.toString();
			if (log.isDebugEnabled()) {
				log.debug("The generated XML:\n" + generatedXml);

				log.debug("The Expected XML:\n" + expectedXmlOutput);
			}

			Document generatedDoc = documentBuilder.parse(new ByteArrayInputStream(generatedXml.getBytes(StandardCharsets.UTF_8)));
			Document expectedDoc = documentBuilder.parse(new ByteArrayInputStream(expectedXmlOutput.getBytes(StandardCharsets.UTF_8)));

			isValid = generatedDoc.isEqualNode(expectedDoc);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
			isValid = false;
		}

		assert isValid;
	}
}
