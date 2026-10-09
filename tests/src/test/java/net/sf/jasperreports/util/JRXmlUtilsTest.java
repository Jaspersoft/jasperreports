/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.util;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.jasperreports.engine.util.JRXmlUtils;

/**
 * Checks that the XML factories created by {@link JRXmlUtils} do not resolve external entities.
 */
public class JRXmlUtilsTest
{
	private static final String SECRET = "external-entity-content";

	@Test
	public void transformerFactoryDoesNotResolveExternalEntities() throws Exception
	{
		String xml = "<?xml version=\"1.0\"?>"
				+ "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"" + secretFile().toURI() + "\">]>"
				+ "<root>&xxe;</root>";

		StringWriter output = new StringWriter();
		try
		{
			JRXmlUtils.createTransformerFactory().newTransformer().transform(
					new StreamSource(new StringReader(xml)), new StreamResult(output));
		}
		catch (TransformerException e)
		{
			//the external entity was refused
			return;
		}

		assert !output.toString().contains(SECRET) : "external entity resolved: " + output;
	}

	@Test
	public void transformerFactoryTransformsDocument() throws Exception
	{
		Document document = JRXmlUtils.createDocumentBuilder().newDocument();
		document.appendChild(document.createElement("root"));

		StringWriter output = new StringWriter();
		JRXmlUtils.createTransformerFactory().newTransformer().transform(
				new DOMSource(document), new StreamResult(output));

		assert output.toString().contains("<root/>") : output;
	}

	@Test
	public void documentBuilderFactoryDoesNotResolveExternalEntities() throws Exception
	{
		String xml = "<?xml version=\"1.0\"?>"
				+ "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"" + secretFile().toURI() + "\">]>"
				+ "<root>&xxe;</root>";

		Document document;
		try
		{
			document = JRXmlUtils.createDocumentBuilderFactory().newDocumentBuilder().parse(
					new InputSource(new StringReader(xml)));
		}
		catch (SAXException e)
		{
			//the external entity was refused
			return;
		}

		String text = document.getDocumentElement().getTextContent();
		assert !text.contains(SECRET) : "external entity resolved: " + text;
	}

	@Test
	public void documentBuilderFactoryExpandsInternalEntities() throws Exception
	{
		String xml = "<?xml version=\"1.0\"?>"
				+ "<!DOCTYPE root [<!ENTITY value \"internal\">]>"
				+ "<root>&value;</root>";

		Document document = JRXmlUtils.createDocumentBuilderFactory().newDocumentBuilder().parse(
				new InputSource(new StringReader(xml)));

		assert "internal".equals(document.getDocumentElement().getTextContent()) : document.getDocumentElement().getTextContent();
	}

	@Test(expectedExceptions = SAXException.class)
	public void documentBuilderRejectsDoctype() throws Exception
	{
		JRXmlUtils.createDocumentBuilder().parse(new InputSource(new StringReader(
				"<?xml version=\"1.0\"?><!DOCTYPE root [<!ENTITY value \"internal\">]><root>&value;</root>")));
	}

	private static File secretFile() throws Exception
	{
		File secretFile = File.createTempFile("jr-xxe", ".txt");
		secretFile.deleteOnExit();
		Files.write(secretFile.toPath(), SECRET.getBytes(StandardCharsets.UTF_8));
		return secretFile;
	}
}
