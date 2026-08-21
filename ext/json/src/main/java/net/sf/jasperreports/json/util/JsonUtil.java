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
package net.sf.jasperreports.json.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.type.JsonOperatorEnum;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.SimpleRepositoryContext;


/**
 * 
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonUtil {
	
	private static final Log log = LogFactory.getLog(JsonUtil.class);
	
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_OPERATOR = "util.json.unknown.operator";
	
	public static boolean evaluateJsonExpression(JsonNode contextNode, String attributeExpression) throws JRException {
		
		if (attributeExpression == null) {
			return true;
		}
		
		String attribute = null;
		JsonOperatorEnum operator = null;
		String value = null;
		boolean result = false;
		
		for (JsonOperatorEnum joe: JsonOperatorEnum.values()) {
			int indexOfOperator = attributeExpression.indexOf(joe.getValue());
			if (indexOfOperator != -1) {
				operator = joe;
				attribute = attributeExpression.substring(0, indexOfOperator).trim();
				value = attributeExpression.substring(indexOfOperator + joe.getValue().length()).trim();
				break;
			}
		}
		
		if (operator == null) {
			StringBuilder possibleOperations = new StringBuilder();
			for (JsonOperatorEnum op: JsonOperatorEnum.values()) {
				possibleOperations.append(op.getValue()).append(",");
			}
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_UNKNOWN_OPERATOR,
					new Object[]{attributeExpression, possibleOperations});
		}
		
		if (attribute != null && operator != null && value != null) {
			// going down the path of the attribute must return a value node 
			if (!contextNode.path(attribute).isValueNode()) {
				result = false;	
			} else {
				String contextValue = contextNode.path(attribute).asText();
				switch (operator) {
				case LT:
					try {
						result = Double.parseDouble(contextValue) < Double.parseDouble(value);
					} catch (NumberFormatException nfe) {
						result = false;
					}
					break;
				case LE:
					try {
						result = Double.parseDouble(contextValue) <= Double.parseDouble(value);
					} catch (NumberFormatException nfe) {
						result = false;
					}
					break;
				case GT:
					try {
						result = Double.parseDouble(contextValue) > Double.parseDouble(value);
					} catch (NumberFormatException nfe) {
						result = false;
					}
					break;
				case GE:
					try {
						result = Double.parseDouble(contextValue) >= Double.parseDouble(value);
					} catch (NumberFormatException nfe) {
						result = false;
					}
					break;
				case EQ:
					result = contextValue.equals(value);
					break;
				case NE:
					result = !contextValue.equals(value);
					break;
				default:
				}
			}
		}
		
		return result;
	}
	
	public static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		return mapper;
	}
	
	public static JsonNode parseJson(File file) throws JRException {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			return parseJson(fileInputStream);
		} catch (IOException e) {
			throw new JRException(e);
		}
	}
	
	public static JsonNode parseJson(JasperReportsContext jasperReportsContext, String location) throws JRException {
		return parseJson(SimpleRepositoryContext.of(jasperReportsContext), location);
	}
	
	public static JsonNode parseJson(RepositoryContext repositoryContext, String location) throws JRException {
		RepositoryUtil repository = RepositoryUtil.getInstance(repositoryContext);
		InputStream stream = repository.getInputStreamFromLocation(location);
		try {
			return parseJson(stream);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					if (log.isWarnEnabled()) {
						log.warn("Failed to close input stream for location " + location, e);
					}
				}
			}			
		}
	}
	
	public static JsonNode parseJson(InputStream jsonStream) throws JRException {
		ObjectMapper mapper = createObjectMapper();
		JsonNode jsonTree;
		try {
			jsonTree = mapper.readTree(jsonStream);
		} catch (IOException e) {
			throw new JRException(e);
		}
		return jsonTree;
	}
}
