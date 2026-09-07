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
package net.sf.jasperreports.json.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.DataAdapterServiceConstants;
import net.sf.jasperreports.engine.data.JRAbstractTextDataSource;
import net.sf.jasperreports.engine.data.JsonData;
import net.sf.jasperreports.engine.data.NoRecordAtIndexException;
import net.sf.jasperreports.engine.data.RandomAccessDataSource;
import net.sf.jasperreports.json.query.JsonQueryExecuterFactory;
import net.sf.jasperreports.json.util.JsonUtil;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;


/**
 * JSON data source implementation
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonDataSource extends JRAbstractTextDataSource implements JsonData<JsonDataSource>, RandomAccessDataSource {

	public static final String EXCEPTION_MESSAGE_KEY_JSON_FIELD_VALUE_NOT_RETRIEVED = "data.json.field.value.not.retrieved";
	public static final String EXCEPTION_MESSAGE_KEY_INVALID_ATTRIBUTE_SELECTION = "data.json.invalid.attribute.selection";
	public static final String EXCEPTION_MESSAGE_KEY_INVALID_EXPRESSION = "data.json.invalid.expression";
	public static final String EXCEPTION_MESSAGE_KEY_NO_DATA = "data.json.no.data";

	/**
	 * Property specifying the JSON expression for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {JsonQueryExecuterFactory.JSON_QUERY_EXECUTER_NAME,
					DataAdapterServiceConstants.JSON_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_FIELD_EXPRESSION = JRPropertiesUtil.PROPERTY_PREFIX + "json.field.expression";

	// the JSON select expression that gives the nodes to iterate
	private String selectExpression;

	private Map<String, String> fieldExpressions = new HashMap<>();

	private JsonNode dataNode;
	private Iterator<JsonNode> jsonNodesIterator;
	
	private int currentNodeIndex;

	// the current node
	private JsonNode currentJsonNode;

	private final String PROPERTY_SEPARATOR = ".";//FIXME static?

	private final String ARRAY_LEFT = "[";

	private final String ARRAY_RIGHT = "]";
	
	private final String ATTRIBUTE_LEFT = "(";
	
	private final String ATTRIBUTE_RIGHT = ")";
	
	// the JSON tree as it is obtained from the JSON source
	private JsonNode jsonTree;
	
	private ObjectMapper mapper;
	
	public JsonDataSource(InputStream stream) throws JRException {
		this(stream, null);
	}
	
	public JsonDataSource(InputStream jsonStream, String selectExpression) throws JRException {
		this(JsonUtil.parseJson(jsonStream), selectExpression);
	}
	
	protected JsonDataSource(JsonNode jsonTree, String selectExpression) throws JRException {
		this.mapper = JsonUtil.createObjectMapper();
		
		this.jsonTree = jsonTree;
		this.selectExpression = selectExpression;
		
		moveFirst();
	}


	public JsonDataSource(File file) throws FileNotFoundException, JRException {
		this(file, null);
	}
	

	public JsonDataSource(File file, String selectExpression) throws FileNotFoundException, JRException {
		this(JsonUtil.parseJson(file), selectExpression);
	}

	/**
	 * Creates a data source instance that reads JSON data from a given location
	 * @param jasperReportsContext the JasperReportsContext
	 * @param location a String representing JSON data source
	 * @param selectExpression a String representing the select expression
	 */
	public JsonDataSource(JasperReportsContext jasperReportsContext, String location, String selectExpression) throws JRException
	{
		this(SimpleRepositoryContext.of(jasperReportsContext), location, selectExpression);
	}
	
	public JsonDataSource(RepositoryContext repositoryContext, String location, String selectExpression) throws JRException 
	{
		this(JsonUtil.parseJson(repositoryContext, location), selectExpression);
	}

	/**
	 * @see #JsonDataSource(JasperReportsContext, String, String)
	 */
	public JsonDataSource(String location, String selectExpression) throws JRException 
	{
		this(DefaultJasperReportsContext.getInstance(), location, selectExpression);
	}

	/**
	 * Access the JDON tree that this data source is based on.
	 * 
	 * @return the JSON tree used by this data source
	 */
	public JsonNode getRootNode()
	{
		return jsonTree;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jasperreports.engine.JRRewindableDataSource#moveFirst()
	 */
	@Override
	public void moveFirst() throws JRException {
		if (jsonTree == null || jsonTree.isMissingNode()) {
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_NO_DATA,
					(Object[])null);
		}

		currentNodeIndex = -1;
		currentJsonNode = null;
		JsonNode result = getJsonData(jsonTree, selectExpression);
		if (result != null && result.isObject()) {
			dataNode = result;
			final List<JsonNode> list = new ArrayList<>();
			list.add(result);
			jsonNodesIterator = new Iterator<JsonNode>() {
				private int count = -1;
				@Override
				public void remove() {
					list.remove(count);
				}
				
				@Override
				public JsonNode next() {
					count ++;
					return list.get(count);
				}
				
				@Override
				public boolean hasNext() {
					return count < list.size()-1;
				}
			};
		} else if (result != null && result.isArray()) {
			dataNode = result;
			jsonNodesIterator = result.elements();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jasperreports.engine.JRDataSource#next()
	 */
	@Override
	public boolean next() {
		if (jsonNodesIterator == null || !jsonNodesIterator.hasNext()) {
			return false;
		}
		++currentNodeIndex;
		currentJsonNode = jsonNodesIterator.next();
		return true;
	}

	@Override
	public int recordCount() {
		int count;
		if (dataNode != null) {
			if (dataNode.isObject()) {
				count = 1;
			} else if (dataNode.isArray()) {
				count = dataNode.size();
			} else {
				//shouldn't happen
				throw new IllegalStateException();
			}
		} else {
			count = 0;
		}
		return count;
	}

	@Override
	public int currentIndex() {
		return currentNodeIndex;
	}

	@Override
	public void moveToRecord(int index) throws NoRecordAtIndexException {
		if (dataNode != null) {
			if (dataNode.isObject()) {
				if (index == 0) {
					currentNodeIndex = 0;
					currentJsonNode = dataNode;
				} else {
					throw new NoRecordAtIndexException(index);
				}
			} else if (dataNode.isArray()) {
				if (index >= 0 && index < dataNode.size()) {
					currentNodeIndex = index;
					currentJsonNode = dataNode.get(index);
				} else {
					throw new NoRecordAtIndexException(index);
				}
			}
		} else {
			throw new NoRecordAtIndexException(index);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.jasperreports.engine.JRDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
	 */
	@Override
	public Object getFieldValue(JRField jrField) throws JRException 
	{
		if (currentJsonNode == null) {
			return null;
		}
		
		String expression = null;
		if (fieldExpressions.containsKey(jrField.getName()))
		{
			expression = fieldExpressions.get(jrField.getName());
		}
		else
		{
			expression = getFieldExpression(jrField);
			fieldExpressions.put(jrField.getName(), expression);
		}
		if (expression == null || expression.length() == 0)
		{
			return null;
		}

		Object value = null;
		
		Class<?> valueClass = jrField.getValueClass();
		JsonNode selectedObject = getJsonData(currentJsonNode, expression);
		
		if (Object.class != valueClass) 
		{
			boolean hasValue = selectedObject != null 
					&& !selectedObject.isMissingNode() && !selectedObject.isNull();
			if (hasValue) 
			{
				try {
					if (valueClass.equals(String.class)) {
						if (selectedObject.isArray()) {
							value = selectedObject.toString();
						} else {
							value = selectedObject.asText();
						}

					} else if (valueClass.equals(Boolean.class)) {
						value = selectedObject.booleanValue();
						
					} else if (Number.class.isAssignableFrom(valueClass)) {
						if (selectedObject.isNumber()) {
							if (BigDecimal.class.equals(valueClass) && selectedObject.isBigDecimal()) {
								value = selectedObject.decimalValue();
							} else if (BigInteger.class.equals(valueClass) && selectedObject.isBigInteger()) {
								value = selectedObject.bigIntegerValue();
							} else if (Double.class.equals(valueClass) && selectedObject.isDouble()) {
								value = selectedObject.doubleValue();
							} else if (Integer.class.equals(valueClass) && selectedObject.isInt()) {
								value = selectedObject.intValue();
							} else {
								value = convertNumber(selectedObject.numberValue(), valueClass);
							}
						} else {
							value = convertStringValue(selectedObject.asText(), valueClass);
						}
					}
					else if (Date.class.isAssignableFrom(valueClass)) {
							value = convertStringValue(selectedObject.asText(), valueClass);
							
					} else {
						throw 
							new JRException(
								EXCEPTION_MESSAGE_KEY_CANNOT_CONVERT_FIELD_TYPE,
								new Object[]{jrField.getName(), valueClass.getName()});
					}
				} catch (Exception e) {
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_JSON_FIELD_VALUE_NOT_RETRIEVED,
							new Object[]{jrField.getName(), valueClass.getName()}, 
							e);
				}
			}
		}
		else
		{
			value = selectedObject;
		}
		
		return value;
	}
	
	/**
	 * Extracts the JSON nodes based on the query expression
	 * 
	 * @param rootNode
	 * @param jsonExpression
	 * @throws JRException
	 */
	protected JsonNode getJsonData(JsonNode rootNode, String jsonExpression) throws JRException {
		if (jsonExpression == null || jsonExpression.length() == 0) {
			return rootNode;
		}
		JsonNode tempNode = rootNode;
		StringTokenizer tokenizer = new StringTokenizer(jsonExpression, PROPERTY_SEPARATOR);
		
		while (tokenizer.hasMoreTokens()) {
			String currentToken = tokenizer.nextToken();
			int currentTokenLength = currentToken.length();
			int indexOfLeftSquareBracket = currentToken.indexOf(ARRAY_LEFT);

			// got Left Square Bracket - LSB
			if (indexOfLeftSquareBracket != -1) {
				// a Right Square Bracket must be the last character in the current token
				if (currentToken.lastIndexOf(ARRAY_RIGHT) != (currentTokenLength-1)) {
					throw 
						new JRException(
							EXCEPTION_MESSAGE_KEY_INVALID_EXPRESSION,
							new Object[]{jsonExpression, currentToken});
				}
				
				// LSB not first character
				if (indexOfLeftSquareBracket > 0) {
					// extract nodes at property
					String property = currentToken.substring(0, indexOfLeftSquareBracket);
					tempNode = goDownPathWithAttribute(tempNode, property);
				}

				String arrayOperators = currentToken.substring(indexOfLeftSquareBracket);
				StringTokenizer arrayOpsTokenizer = new StringTokenizer(arrayOperators,ARRAY_RIGHT);
				while (arrayOpsTokenizer.hasMoreTokens()) {
					if (tempNode == null || tempNode.isMissingNode() || !tempNode.isArray()) {
						return null;
					}

					String currentArrayOperator = arrayOpsTokenizer.nextToken();
					tempNode = tempNode.path(Integer.parseInt(currentArrayOperator.substring(1)));
				}
			} else {
				tempNode = goDownPathWithAttribute(tempNode, currentToken);
			}
		}
		
		return tempNode;
	}
	
	
	/**
	 * Extracts the JSON nodes that match the attribute expression
	 * 
	 * @param rootNode
	 * @param pathWithAttributeExpression : e.g. Orders(CustomerId == HILAA)
	 * @throws JRException
	 */
	protected JsonNode goDownPathWithAttribute(JsonNode rootNode, String pathWithAttributeExpression) throws JRException {
		// check if path has attribute selector
		int indexOfLeftRoundBracket = pathWithAttributeExpression.indexOf(ATTRIBUTE_LEFT); 
		if (indexOfLeftRoundBracket != -1) {
			
			// a Right Round Bracket must be the last character in the current pathWithAttribute
			if (pathWithAttributeExpression.indexOf(ATTRIBUTE_RIGHT) != (pathWithAttributeExpression.length() - 1)) {
				throw 
					new JRException(
						EXCEPTION_MESSAGE_KEY_INVALID_ATTRIBUTE_SELECTION,
						new Object[]{pathWithAttributeExpression});
			}
			
			if (rootNode != null && !rootNode.isMissingNode()) {
				
				String path = pathWithAttributeExpression.substring(0, indexOfLeftRoundBracket);
				
				// an expression in a form like: attribute==value
				String attributeExpression = pathWithAttributeExpression.substring(indexOfLeftRoundBracket + 1, pathWithAttributeExpression.length() - 1);
				
				JsonNode result = null;
				if (rootNode.isObject()) {
					// select only those nodes for which the attribute expression applies
					if (!rootNode.path(path).isMissingNode()) {
						if (rootNode.path(path).isObject()) {
							if (isValidExpression(rootNode.path(path), attributeExpression)) {
								result = rootNode.path(path);
							}
						} else if (rootNode.path(path).isArray()) {
							result = mapper.createArrayNode();
							for (JsonNode node: rootNode.path(path)) {
								if (isValidExpression(node, attributeExpression)) {
									((ArrayNode)result).add(node);
								} 
							}
						}
					}
				} else if (rootNode.isArray()) {
					result = mapper.createArrayNode();
					for (JsonNode node: rootNode) {
						JsonNode deeperNode = node.path(path);
						if (!deeperNode.isMissingNode()) {
							if (deeperNode.isArray()) {
								for (JsonNode arrayNode: deeperNode) {
									if (isValidExpression(arrayNode, attributeExpression)) {
										((ArrayNode)result).add(arrayNode);
									}
								}
							} else if (isValidExpression(deeperNode, attributeExpression)){
								((ArrayNode)result).add(deeperNode);
							}
						} 
					}
				}
				return result;
			} 
			
		} else { // path has no attribute selectors
			return goDownPath(rootNode, pathWithAttributeExpression);
		}
		return rootNode;
	}
	
	
	/**
	 * Extracts the JSON nodes under the simple path
	 * 
	 * @param rootNode
	 * @param simplePath - a simple field name, with no selection by attribute
	 */
	protected JsonNode goDownPath(JsonNode rootNode, String simplePath) {
		if (rootNode != null && !rootNode.isMissingNode()) {
			JsonNode result = null;
			if (rootNode.isObject()) {
				result = rootNode.path(simplePath);
			} else if (rootNode.isArray()) {
				result = mapper.createArrayNode();
				for (JsonNode node: rootNode) {
					JsonNode deeperNode = node.path(simplePath);
					if (!deeperNode.isMissingNode()) {
						if (deeperNode.isArray()) {
							for (JsonNode arrayNode: deeperNode) {
								((ArrayNode)result).add(arrayNode);
							}
						} else {
							((ArrayNode)result).add(deeperNode);
						}
					} 
				}
			}
			return result;
		} 
		return rootNode;
	}
	
	
	/**
	 * Validates an attribute expression on a JsonNode
	 * 
	 * @param operand
	 * @param attributeExpression
	 * @throws JRException
	 */
	protected boolean isValidExpression(JsonNode operand, String attributeExpression) throws JRException {
		return JsonUtil.evaluateJsonExpression(operand, attributeExpression);
	}


	/**
	 * Creates a sub data source using the current node as the base for its input stream.
	 * 
	 * @return the JSON sub data source
	 * @throws JRException
	 */
	@Override
	public JsonDataSource subDataSource() throws JRException {
		return subDataSource(null);
	}


	/**
	 * Creates a sub data source using the current node as the base for its input stream.
	 * An additional expression specifies the select criteria that will be applied to the
	 * JSON tree node. 
	 * 
	 * @param selectExpression
	 * @return the JSON sub data source
	 * @throws JRException
	 */
	@Override
	public JsonDataSource subDataSource(String selectExpression) throws JRException {
		if (currentJsonNode == null)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_NODE_NOT_AVAILABLE,
					(Object[])null);
		}

		JsonDataSource subDataSource = new JsonDataSource(currentJsonNode, selectExpression);
		subDataSource.setTextAttributes(this);

		return subDataSource;
	}


	protected String getFieldExpression(JRField field)
	{
		String fieldExpression = null;
		if (field.hasProperties())
		{
			fieldExpression = field.getPropertiesMap().getProperty(PROPERTY_FIELD_EXPRESSION);
		}
		if (fieldExpression == null)
		{
			fieldExpression = field.getDescription();
			if (fieldExpression == null || fieldExpression.length() == 0)
			{
				fieldExpression = field.getName();
			}
		}
		return fieldExpression;
	}
}
