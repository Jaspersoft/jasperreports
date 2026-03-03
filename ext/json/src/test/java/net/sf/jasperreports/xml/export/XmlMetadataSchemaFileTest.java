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
	private MetadataProcessor xmlProcessor;
	private boolean schemaIsValid;

    @BeforeClass
    public void setUp() throws JRException {
		repoUtil = RepositoryUtil.getInstance(DefaultJasperReportsContext.getInstance());
		documentBuilder = JRXmlUtils.createDocumentBuilder();
    }

	@BeforeMethod
	public void prepare(Method method) throws JRException {
		String methodName = method.getName();
		String methodPrefix = "validateXmlForSchema_";

		String expectedResultPathPrefix = "net/sf/jasperreports/export/xml/expectedResultFor_Schema";
		String schemaPathPrefix = "net/sf/jasperreports/export/xml/TestSchema";

		if (methodName.startsWith(methodPrefix)) {
			String suffix = methodName.substring(methodPrefix.length());

			String schemaPath = schemaPathPrefix + suffix + ".json";
			Scanner scanner = new Scanner(repoUtil.getInputStreamFromLocation(schemaPath), StandardCharsets.UTF_8.name());

			JsonSchema jsonSchema = new JsonSchema();
			schemaIsValid = true;
			try {
				jsonSchema.initialize(scanner.useDelimiter("\\A").next());

				if (log.isDebugEnabled()) {
					for (Map.Entry<String, SchemaNode> entry : jsonSchema.getPathToSchemaNodeMap().entrySet()) {
						log.debug("pathToSchemaNode: key: " + String.format("%-25s", entry.getKey()) + "; value: " + entry.getValue());
					}
				}

				StringWriter sw = new StringWriter();
				xmlProcessor = new XmlMetadataProcessor(jsonSchema, sw);
				xmlProcessor.getMetadataWriter().writeHeader();
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error(e.getMessage(), e);
				}
				schemaIsValid = false;
			}

			String expectedResultPath = expectedResultPathPrefix + suffix + ".xml";
			scanner = new Scanner(repoUtil.getInputStreamFromLocation(expectedResultPath), StandardCharsets.UTF_8.name());
			expectedXmlOutput = scanner.useDelimiter("\\A").next();
		}

	}

	private boolean isGeneratedXmlValid() {
		String generatedXml = xmlProcessor.getMetadataWriter().getWriter().toString();

		if (log.isDebugEnabled()) {
			log.debug("The generated XML:\n" + generatedXml);

			log.debug("The Expected XML:\n" + expectedXmlOutput);
		}

		try {
			Document generatedDoc = documentBuilder.parse(new ByteArrayInputStream(generatedXml.getBytes(StandardCharsets.UTF_8)));
			Document expectedDoc = documentBuilder.parse(new ByteArrayInputStream(expectedXmlOutput.getBytes(StandardCharsets.UTF_8)));

			return generatedDoc.isEqualNode(expectedDoc);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
			return false;
		}
	}

	@Test
	public void validateXmlForSchema_1() throws IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "1", "product.id", false);
		xmlProcessor.processElement(() -> "2", "product.id", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_2() throws IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
		xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
		xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_3() throws IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
		xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
		xmlProcessor.processElement(() -> "order_id_1", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_2", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_3", "products.orderId", false);
		xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
		xmlProcessor.processElement(() -> "order_id_4", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_5", "products.orderId", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_4() throws IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
		xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
		xmlProcessor.processElement(() -> "order_id_1", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_2", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_3", "products.orderId", false);
		xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
		xmlProcessor.processElement(() -> "order_id_4", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_5", "products.orderId", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_5() throws IOException {
		assert  schemaIsValid;

		xmlProcessor.processElement(() -> "id_1", "products.details.id", false);
		xmlProcessor.processElement(() -> "name_1", "products.details.name", false);
		xmlProcessor.processElement(() -> "order_id_1", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_2", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_3", "products.orderId", false);
		xmlProcessor.processElement(() -> "id_2", "products.details.id", false);
		xmlProcessor.processElement(() -> "order_id_4", "products.orderId", false);
		xmlProcessor.processElement(() -> "order_id_5", "products.orderId", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_6() throws JRException, IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "value_1", "a.d.e", true);
		xmlProcessor.processElement(() -> "value_2", "a.d.f", false);
		xmlProcessor.processElement(() -> "value_3", "a.d.g.h", false);
		xmlProcessor.processElement(() -> "value_4", "a.d.i", true);
		xmlProcessor.processElement(() -> "value_5", "a.d.g.h", false);
		xmlProcessor.processElement(() -> "value_6", "a.d.e", true);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_7() throws JRException, IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "1", "product.id", false);
		xmlProcessor.processElement(() -> "part_1", "product.parts.name", false);
		xmlProcessor.processElement(() -> "cat_1", "product.parts.category", false);
		xmlProcessor.processElement(() -> "name_1", "product.name", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_8() throws IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "city_1", "city", false);
		xmlProcessor.processElement(() -> "id_1", "products.id", false);
		xmlProcessor.processElement(() -> "name_1", "products.name", false);
		xmlProcessor.processElement(() -> "id_2", "products.id", false);
		xmlProcessor.processElement(() -> "city_2", "city", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}

	@Test
	public void validateXmlForSchema_9() throws JRException, IOException {
		assert schemaIsValid;

		xmlProcessor.processElement(() -> "value_1", "a.d.e", true);
		xmlProcessor.processElement(() -> "value_2", "a.d.f", false);
		xmlProcessor.processElement(() -> "value_3", "a.d.g", true);
		xmlProcessor.processElement(() -> "value_4", "a.d.e", true);
		xmlProcessor.processElement(() -> "value_5", "a.d.g", true);
		xmlProcessor.processElement(() -> "value_6", "a.d.f", false);
		xmlProcessor.processElement(() -> null, "a.d.e", false);
		xmlProcessor.closeOpenNodes();

		assert isGeneratedXmlValid();
	}
}
