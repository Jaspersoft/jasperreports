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
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.data.DataAdapterServiceConstants;
import net.sf.jasperreports.engine.data.JRAbstractTextDataSource;
import net.sf.jasperreports.engine.data.JsonData;
import net.sf.jasperreports.engine.data.NoRecordAtIndexException;
import net.sf.jasperreports.engine.data.RandomAccessDataSource;
import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.query.JsonQLQueryExecuterFactory;
import net.sf.jasperreports.json.util.DefaultJsonQLExecuter;
import net.sf.jasperreports.json.util.JsonQLExecuter;
import net.sf.jasperreports.json.util.JsonUtil;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQLDataSource extends JRAbstractTextDataSource implements JsonData<JsonQLDataSource>, RandomAccessDataSource {
	private static final Log log = LogFactory.getLog(JsonQLDataSource.class);

	public static final String EXCEPTION_MESSAGE_KEY_NO_DATA = "data.json.no.data";
	public static final String EXCEPTION_MESSAGE_KEY_JSON_FIELD_VALUE_NOT_RETRIEVED = "data.json.field.value.not.retrieved";

	/**
	 * Property specifying the JSONQL expression for the dataset field.
	 */
	@Property (
			category = PropertyConstants.CATEGORY_DATA_SOURCE,
			scopes = {PropertyScope.FIELD},
			scopeQualifications = {JsonQLQueryExecuterFactory.JSONQL_QUERY_EXECUTER_NAME,
					DataAdapterServiceConstants.JSONQL_DESIGNATION},
			sinceVersion = PropertyConstants.VERSION_6_3_1
	)
	public static final String PROPERTY_FIELD_EXPRESSION = JRPropertiesUtil.PROPERTY_PREFIX + "jsonql.field.expression";

	private JRJsonNode root;
	private String selectExpression;

	private JRJsonNode currentJsonNode;
	private List<JRJsonNode> nodes;
	private int currentNodeIndex = - 1;

	private JsonQLExecuter jsonQLExecuter;

	private Map<String, String> fieldExpressions = new HashMap<>();


	public JsonQLDataSource(File file, String selectExpression) throws JRException {
		this(JsonUtil.parseJson(file), selectExpression);
	}

	public JsonQLDataSource(File file) throws JRException {
		this(file, null);
	}

	public JsonQLDataSource(InputStream jsonInputStream, String selectExpression) throws JRException {
		this(JsonUtil.parseJson(jsonInputStream), selectExpression);
	}

	public JsonQLDataSource(InputStream jsonInputStream) throws JRException {
		this(jsonInputStream, null);
	}

	public JsonQLDataSource(JasperReportsContext jasperReportsContext, String jsonSource, String selectExpression) throws JRException {
		this(SimpleRepositoryContext.of(jasperReportsContext), jsonSource, selectExpression);
	}

	public JsonQLDataSource(RepositoryContext repositoryContext, String jsonSource, String selectExpression) throws JRException {
		this(JsonUtil.parseJson(repositoryContext, jsonSource), selectExpression);
	}

	protected JsonQLDataSource(JsonNode jacksonJsonTree, String selectExpression) throws JRException {
		this(new JRJsonNode(null, jacksonJsonTree), selectExpression);
	}

	protected JsonQLDataSource(JRJsonNode root, String selectExpression) throws JRException {
		this.root = root;
		this.selectExpression = selectExpression;
		this.jsonQLExecuter = new DefaultJsonQLExecuter();

		if (log.isDebugEnabled()) {
			log.debug("The JsonQL expression is: " + selectExpression);
		}

		moveFirst();
	}

	@Override
	public void moveFirst() throws JRException {
		if (root.getDataNode() == null || root.getDataNode().isMissingNode()) {
			throw new JRException(EXCEPTION_MESSAGE_KEY_NO_DATA, (Object[]) null);
		}

		currentJsonNode = null;
		nodes = jsonQLExecuter.selectNodes(root, selectExpression);
		currentNodeIndex = -1;
	}

	@Override
	public boolean next() throws JRException {
		if (nodes != null && currentNodeIndex < nodes.size() - 1) {
			currentJsonNode = nodes.get(++currentNodeIndex);

			return true;
		}

		return false;
	}

	@Override
	public int recordCount() {
		return nodes == null ? 0 : nodes.size();
	}

	@Override
	public int currentIndex() {
		return currentNodeIndex;
	}

	@Override
	public void moveToRecord(int index) throws NoRecordAtIndexException {
		if (nodes != null && index >= 0 && index < nodes.size()) {
			 currentNodeIndex = index;
			 currentJsonNode = nodes.get(index);
		} else {
			throw new NoRecordAtIndexException(index);
		}
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		if (currentJsonNode == null) {
			return null;
		}

		String expression;
		if (fieldExpressions.containsKey(jrField.getName())) {
			expression = fieldExpressions.get(jrField.getName());
		} else {
			expression = getFieldExpression(jrField);
			fieldExpressions.put(jrField.getName(), expression);
		}

		if (expression == null || expression.length() == 0) {
			return null;
		}

		JRJsonNode selectedNode = jsonQLExecuter.selectNode(currentJsonNode, root, expression);
		if (selectedNode != null) {
			return getConvertedValue(selectedNode, jrField);
		}

		return null;
	}

	@Override
	public JsonQLDataSource subDataSource() throws JRException {
		return subDataSource(null);
	}

	@Override
	public JsonQLDataSource subDataSource(String selectExpression) throws JRException {
		if (currentJsonNode == null) {
			throw new JRException(EXCEPTION_MESSAGE_KEY_NO_DATA, (Object[])null);
		}

		JsonQLDataSource subDataSource = new JsonQLDataSource(currentJsonNode, selectExpression);
		subDataSource.setTextAttributes(this);

		return subDataSource;
	}

	protected Object getConvertedValue(JRJsonNode node, JRField jrField) throws JRException {
		JsonNode dataNode = node.getDataNode();
		Class<?> valueClass = jrField.getValueClass();

		if (log.isDebugEnabled()) {
			log.debug("attempting to convert: " + dataNode + " to class: " + valueClass);
		}

		if (Object.class.equals(valueClass)) {
			return dataNode;
		}

		Object result = null;

		if (!dataNode.isNull())  {
			try {
				if (Boolean.class.equals(valueClass) && dataNode.isBoolean()) {
					result = dataNode.booleanValue();

				} else if (BigDecimal.class.equals(valueClass) && dataNode.isBigDecimal()) {
					result = dataNode.decimalValue();

				} else if (BigInteger.class.equals(valueClass) && dataNode.isBigInteger()) {
					result = dataNode.bigIntegerValue();

				} else if (Double.class.equals(valueClass) && dataNode.isDouble()) {
					result = dataNode.doubleValue();

				} else if (Integer.class.equals(valueClass) && dataNode.isInt()) {
					result = dataNode.intValue();

				} else if (Number.class.isAssignableFrom(valueClass) && dataNode.isNumber()) {
					result = convertNumber(dataNode.numberValue(), valueClass);

				} else {
					result = convertStringValue(dataNode.asText(), valueClass);
				}

				if (result == null) {
					throw new JRException(EXCEPTION_MESSAGE_KEY_CANNOT_CONVERT_FIELD_TYPE,
							new Object[]{jrField.getName(), valueClass.getName()});
				}

			} catch (Exception e) {
				throw new JRException(EXCEPTION_MESSAGE_KEY_JSON_FIELD_VALUE_NOT_RETRIEVED,
						new Object[]{jrField.getName(), valueClass.getName()}, e);
			}
		}

		return result;
	}

	protected String getFieldExpression(JRField field) {
		String fieldExpression = null;

		if (field.hasProperties()) {
			fieldExpression = field.getPropertiesMap().getProperty(PROPERTY_FIELD_EXPRESSION);
		}

		if (fieldExpression == null) {
			fieldExpression = field.getDescription();

			if (fieldExpression == null || fieldExpression.length() == 0) {
				fieldExpression = field.getName();
			}
		}

		return fieldExpression;
	}

}

