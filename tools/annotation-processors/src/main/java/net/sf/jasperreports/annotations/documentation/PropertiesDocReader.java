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
package net.sf.jasperreports.annotations.documentation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.metadata.properties.CompiledPropertiesMetadata;
import net.sf.jasperreports.metadata.properties.CompiledPropertyMetadata;
import net.sf.jasperreports.metadata.properties.PropertyMetadataConstants;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class PropertiesDocReader
{
	
	private static final String ELEMENT_ROOT = "configReference";
	private static final String ELEMENT_CATEGORY = "category";
	private static final String ATTR_CATEGORY_KEY = "key";
	private static final String ELEMENT_CATEGORY_NAME = "name";
	private static final String ELEMENT_CATEGORY_CONTENT = "content";
	private static final String ELEMENT_CATEGORY_PROPERTY = "property";
	private static final String ATTR_CATEGORY_PROPERTY_REF = "ref";
	private static final String ELEMENT_CONFIG_PROP = "configProperty";
	private static final String ATTR_CONFIG_PROP_NAME = "name";
	private static final String ATTR_CONSTANT_DECLARATION_CLASS_NAME = "constantDeclarationClassName";
	private static final String ELEMENT_DESCRIPTION = "description";
	private static final String ELEMENT_API = "api";
	private static final String ELEMENT_DEFAULT = "default";
	private static final String ELEMENT_SCOPE = "scope";
	private static final String ELEMENT_CONTEXT_UNAWARE = "contextUnaware";
	private static final String ELEMENT_SINCE = "since";
	private static final String ELEMENT_DEPRECATED = "deprecated";
	
	 
	// This array is used for binary searches, maintain it sorted

	private static final String[] SORTED_DUPLICATE_CLASSES = {"JRJpaQueryExecuter"};
			
	private ProcessingEnvironment environment;
	private CompiledPropertiesMetadata properties;
	private DocumentBuilder documentBuilder;
	private Properties propertyMessages;
	
	private Map<String, CategoryDoc> categories = new LinkedHashMap<>();
	private Map<String, Element> propertyDocNodes = new LinkedHashMap<>();

	public PropertiesDocReader(ProcessingEnvironment environment, CompiledPropertiesMetadata properties)
	{
		this.environment = environment;
		this.properties = properties;
		
		try
		{
			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new RuntimeException(e);
		}
		
		readMessages();
	}
	
	private void readMessages()
	{
		try
		{
			FileObject resource = environment.getFiler().getResource(StandardLocation.SOURCE_PATH, 
					"", properties.getMessagesName() + ".properties");
			
			Properties messages = new Properties();
			try (InputStream in = resource.openInputStream())
			{
				messages.load(in);
			}
			
			propertyMessages = messages;
		}
		catch (IOException e)
		{
			environment.getMessager().printMessage(Kind.WARNING, "Failed to read source of " 
					+ properties.getMessagesName() + ".properties: " + e.getMessage());
		}
	}

	public void readPropertiesDoc(String docFile)
	{
		try
		{
			Document doc = documentBuilder.parse(new File(docFile));
			Element rootElement = doc.getDocumentElement();
			
			readCategories(rootElement);
			readPropertyDocs(rootElement);
		}
		catch (SAXException | IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	protected void readCategories(Element rootElement)
	{
		NodeList categoryElements = rootElement.getElementsByTagName(ELEMENT_CATEGORY);
		for (int i = 0; i < categoryElements.getLength(); i++)
		{
			Element categoryElement = (Element) categoryElements.item(i);
			String key = categoryElement.getAttribute(ATTR_CATEGORY_KEY);
			if (key == null || key.isEmpty())
			{
				environment.getMessager().printMessage(Kind.WARNING, "No category key at index " + i);
				continue;
			}
			
			CategoryDoc category = new CategoryDoc(key);
			categories.put(key, category);
			
			NodeList nameElems = categoryElement.getElementsByTagName(ELEMENT_CATEGORY_NAME);
			if (nameElems.getLength() != 1)
			{
				environment.getMessager().printMessage(Kind.WARNING, "Unexpected name for category node " + key);
			}
			else
			{
				Element nameElem = (Element) nameElems.item(0);
				category.setNameElement(nameElem);
			}
		}
	}

	protected void readPropertyDocs(Element rootElement)
	{
		NodeList docPropElements = rootElement.getElementsByTagName(ELEMENT_CONFIG_PROP);
		for (int i = 0; i < docPropElements.getLength(); i++)
		{
			Element docPropElement = (Element) docPropElements.item(i);
			String propName = docPropElement.getAttribute(ATTR_CONFIG_PROP_NAME);
			if (propName == null || propName.isEmpty())
			{
				environment.getMessager().printMessage(Kind.WARNING, "No name attribute in node at index " + i);
				continue;
			}
			
			NodeList descriptionElems = docPropElement.getElementsByTagName(ELEMENT_DESCRIPTION);
			if (descriptionElems.getLength() != 1)
			{
				environment.getMessager().printMessage(Kind.WARNING, "Unexpected description for node " + propName);
				continue;
			}
			
			Element descriptionElem = (Element) descriptionElems.item(0);
			String constantDeclarationClassName = docPropElement.getAttribute(ATTR_CONSTANT_DECLARATION_CLASS_NAME);
			if(Arrays.binarySearch(SORTED_DUPLICATE_CLASSES, constantDeclarationClassName.substring(constantDeclarationClassName.lastIndexOf('.')+1)) >= 0)
			{
				descriptionElem.setAttribute(ATTR_CONSTANT_DECLARATION_CLASS_NAME, constantDeclarationClassName);
			}
			propertyDocNodes.put(propName, descriptionElem);
		}
	}
	
	private static final Pattern PATTERN_LEADING_WHITE_SPACE = Pattern.compile("^\\s+");
	
	private static final Pattern PATTERN_TRAILING_WHITE_SPACE = Pattern.compile("\\s+$");
	
	public void writeDefaultMessages()
	{
		Properties defaultMessages = new Properties();
		BreakIterator sentenceBreaks = BreakIterator.getSentenceInstance(Locale.US);
		for (CompiledPropertyMetadata prop : properties.getProperties())
		{
			String descriptionMessage = PropertyMetadataConstants.PROPERTY_DESCRIPTION_PREFIX + prop.getName();
			if (propertyMessages == null || !propertyMessages.containsKey(descriptionMessage))
			{
				Element docNode = propertyDocNodes.get(prop.getName());
				if (docNode != null)
				{
					String docText = docNode.getTextContent();
					sentenceBreaks.setText(docText);
					int first = sentenceBreaks.first();
					int next = sentenceBreaks.next();
					
					String firstSentence = docText.substring(first, next);
					firstSentence = PATTERN_LEADING_WHITE_SPACE.matcher(firstSentence).replaceAll("");
					firstSentence = PATTERN_TRAILING_WHITE_SPACE.matcher(firstSentence).replaceAll("");
					
					defaultMessages.setProperty(descriptionMessage, firstSentence);
				}
			}
		}
		
		if (!defaultMessages.isEmpty())
		{
			try
			{
				FileObject res = environment.getFiler().createResource(StandardLocation.CLASS_OUTPUT, 
						"", properties.getMessagesName() + PropertyMetadataConstants.MESSAGES_DEFAULTS_SUFFIX, 
						(javax.lang.model.element.Element[]) null);
				try (OutputStream out = res.openOutputStream())
				{
					//TODO lucianc preserve order
					defaultMessages.store(out, null);
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	public void writeConfigReference(String refFile)
	{
		collectCategoryProps();
		
		Document refDoc = documentBuilder.newDocument();
		Element refRoot = refDoc.createElement(ELEMENT_ROOT);
		
		for (CategoryDoc categoryDoc : categories.values())
		{
			categoryDoc.sortProperties();
			Element categoryRef = createCategoryRef(refDoc, categoryDoc);
			refRoot.appendChild(categoryRef);
		}
		
		for (CategoryDoc categoryDoc : categories.values())
		{
			for (PropertyDoc prop : categoryDoc.getProperties())
			{
				Element refProp = createPropRef(refDoc, prop);
				refRoot.appendChild(refProp);
			}
		}
		
		refDoc.appendChild(refRoot);
		writeRefDoc(refFile, refDoc);
	}

	protected void collectCategoryProps()
	{
		for (CompiledPropertyMetadata prop : properties.getProperties())
		{
			String category = prop.getCategory();
			CategoryDoc categoryDoc = categories.get(category);
			if (categoryDoc == null)
			{
				environment.getMessager().printMessage(Kind.WARNING, "No category doc found for " + category);
				
				categoryDoc = new CategoryDoc(category);
				categories.put(category, categoryDoc);
			}
			
			PropertyDoc propertyDoc = new PropertyDoc(prop);
			Element docNode = propertyDocNodes.get(prop.getName());
			if (docNode == null)
			{
				environment.getMessager().printMessage(Kind.WARNING, "No description found for " + prop.getName());
			}
			else
			{
				propertyDoc.setDocElement(docNode);
			}
			
			categoryDoc.addProperty(propertyDoc);
		}
	}


	protected Element createCategoryRef(Document refDoc, CategoryDoc category)
	{
		Element refCategory = refDoc.createElement(ELEMENT_CATEGORY);
		
		Element nameElement = category.getNameElement();
		if (nameElement != null)
		{
			Node nameClone = refDoc.importNode(nameElement, true);
			refCategory.appendChild(nameClone);
		}
		
		Element content = refDoc.createElement(ELEMENT_CATEGORY_CONTENT);
		for (PropertyDoc prop : category.getProperties())
		{
			Element propElem = refDoc.createElement(ELEMENT_CATEGORY_PROPERTY);
			CompiledPropertyMetadata propMetadata = prop.getPropertyMetadata();
			propElem.setAttribute(ATTR_CATEGORY_PROPERTY_REF, propMetadata.getName());
			String constantDeclarationClassName=propMetadata.getConstantDeclarationClass().trim();
			
			if(Arrays.binarySearch(SORTED_DUPLICATE_CLASSES, constantDeclarationClassName.substring(constantDeclarationClassName.lastIndexOf('.')+1)) >= 0)
			{
				propElem.setAttribute(ATTR_CONSTANT_DECLARATION_CLASS_NAME,constantDeclarationClassName);
			}
			content.appendChild(propElem);
		}
		refCategory.appendChild(content);
		
		return refCategory;
	}
	
	protected Element createPropRef(Document refDoc, PropertyDoc property)
	{
		CompiledPropertyMetadata propertyMetadata = property.getPropertyMetadata();
		String propName = propertyMetadata.getName();
		
		Element refProp = refDoc.createElement(ELEMENT_CONFIG_PROP);
		refProp.setAttribute(ATTR_CONFIG_PROP_NAME, propName);
		
		String constantDeclarationClass = propertyMetadata.getConstantDeclarationClass();
		if(Arrays.binarySearch(SORTED_DUPLICATE_CLASSES, constantDeclarationClass.substring(constantDeclarationClass.lastIndexOf('.')+1)) >= 0)
		{
			refProp.setAttribute(ATTR_CONSTANT_DECLARATION_CLASS_NAME, constantDeclarationClass.trim());
		}
		
		Element docNode = propertyDocNodes.get(propName);
		if (docNode != null)
		{
			Node docClone = refDoc.importNode(docNode, true);
			refProp.appendChild(docClone);
		}
		
		Element apiElem = refDoc.createElement(ELEMENT_API);
		String apiRef = getApiRef(propertyMetadata);
		apiElem.setTextContent(apiRef);
		refProp.appendChild(apiElem);
		
		Element defaultElem = refDoc.createElement(ELEMENT_DEFAULT);
		defaultElem.setTextContent(propertyMetadata.getDefaultValue());
		refProp.appendChild(defaultElem);
		
		List<PropertyScope> scopes = propertyMetadata.getScopes();
		Element scopeElem = refDoc.createElement(ELEMENT_SCOPE);
		String scopesText = getScopesText(scopes);
		scopeElem.setTextContent(scopesText);
		refProp.appendChild(scopeElem);
		
		if (scopes.contains(PropertyScope.GLOBAL) && !scopes.contains(PropertyScope.CONTEXT))
		{
			Element contextUnawareElem = refDoc.createElement(ELEMENT_CONTEXT_UNAWARE);
			refProp.appendChild(contextUnawareElem);
		}

		if (!propertyMetadata.getSinceVersion().isEmpty())
		{
			Element sinceElem = refDoc.createElement(ELEMENT_SINCE);
			sinceElem.setTextContent(propertyMetadata.getSinceVersion());
			refProp.appendChild(sinceElem);
		}
		
		if (propertyMetadata.isDeprecated())
		{
			Element deprecatedElem = refDoc.createElement(ELEMENT_DEPRECATED);
			deprecatedElem.setTextContent(Boolean.TRUE.toString());
			refProp.appendChild(deprecatedElem);
		}
		
		return refProp;
	}

	protected String getApiRef(CompiledPropertyMetadata prop)
	{
		String apiRef = prop.getConstantDeclarationClass().replace('.', '/') 
				+ ".html#" + prop.getConstantFieldName();
		return apiRef;
	}

	protected String getScopesText(List<PropertyScope> scopes)
	{
		return scopes.stream().map(PropertyScope::toString).collect(Collectors.joining(" | "));
	}

	protected void writeRefDoc(String refFile, Document refDoc)
	{
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(new DOMSource(refDoc), new StreamResult(new File(refFile)));
		}
		catch (TransformerFactoryConfigurationError | TransformerException e)
		{
			throw new RuntimeException(e);
		}
	}

}
