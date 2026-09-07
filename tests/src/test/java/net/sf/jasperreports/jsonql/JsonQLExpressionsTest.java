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
package net.sf.jasperreports.jsonql;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.json.JRJsonNode;
import net.sf.jasperreports.json.JsonNodeContainer;
import net.sf.jasperreports.json.util.DefaultJsonQLExecuter;
import net.sf.jasperreports.json.util.JsonUtil;

/**
 * @author Narcis Marcu (narcism@users.sourceforge.net)
 */
public class JsonQLExpressionsTest {

	private JRJsonNode jrJsonNode;
	private JsonNode expectedResult;
	private DefaultJsonQLExecuter jsonQLExecuter;

	@BeforeClass
	public void readJson() throws JRException {
		JsonNode tree = JsonUtil.parseJson(DefaultJasperReportsContext.getInstance(), "net/sf/jasperreports/jsonql/orders.json");

		jrJsonNode = new JRJsonNode(null, tree);
		jsonQLExecuter = new DefaultJsonQLExecuter();
	}

	@BeforeMethod
	public void expectedResult(Method method) throws JRException {
		expectedResult = JsonUtil.parseJson(DefaultJasperReportsContext.getInstance(),
				"net/sf/jasperreports/jsonql/" + method.getName() + "_result.json");
	}

	@Test
	public void customerXorders() throws JRException {
		String jsonQL_1 = "customerXorders";
		JsonNodeContainer jsonQL_1_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_1);

		// the customerXorders key points to an array
		assert jsonQL_1_result.getNodes().size() == 1;
		assert jsonQL_1_result.getFirst().getDataNode().isArray();
		assert jsonQL_1_result.getFirst().getDataNode().equals(expectedResult);

		String jsonQL_2 = "customerXorders.*";
		JsonNodeContainer jsonQL_2_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_2);

		// customerXorders has 2 object children
		assert jsonQL_2_result.getNodes().size() == 2;
		assert jsonQL_2_result.getNodes().get(0).getDataNode().isObject();
		assert jsonQL_2_result.getNodes().get(1).getDataNode().isObject();
		assert toArrayNode(jsonQL_2_result).equals(expectedResult);
	}

	@Test
	public void allOrders() throws JRException {
		String jsonQL_1 = ".*.*.*";
		JsonNodeContainer jsonQL_1_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_1);

		assert toArrayNode(jsonQL_1_result).equals(expectedResult);

		String jsonQL_2 = "..*(orderId != null)";
		JsonNodeContainer jsonQL_2_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_2);

		assert toArrayNode(jsonQL_2_result).equals(expectedResult);

		String jsonQL_3 = "..[orderId, orderDate, shipped, shippedOn, products]";
		JsonNodeContainer jsonQL_3_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_3);

		assert toArrayNode(jsonQL_3_result).equals(expectedResult);
	}

	@Test
	public void customerXproducts() throws JRException {
		String jsonQL_1 = "customerXorders.products.*.*";
		JsonNodeContainer jsonQL_1_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_1);

		assert toArrayNode(jsonQL_1_result).equals(expectedResult);

		String jsonQL_2 = "customerXorders..[prodId, prodQty]";
		JsonNodeContainer jsonQL_2_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_2);

		assert toArrayNode(jsonQL_2_result).equals(expectedResult);

		String jsonQL_3 = "customerXorders.products..[prodId, prodQty]";
		JsonNodeContainer jsonQL_3_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_3);

		assert toArrayNode(jsonQL_3_result).equals(expectedResult);

		String jsonQL_4 = "..products[0,1].*.*";
		JsonNodeContainer jsonQL_4_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_4);

		assert toArrayNode(jsonQL_4_result).equals(expectedResult);

		String jsonQL_5 = "..*(prodId != null && (^{2}.orderId == 1001 || ^^.orderId == 1002))";
		JsonNodeContainer jsonQL_5_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_5);

		assert toArrayNode(jsonQL_5_result).equals(expectedResult);

		String jsonQL_6 = "*[0]..[prodId, prodQty]";
		JsonNodeContainer jsonQL_6_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_6);

		assert toArrayNode(jsonQL_6_result).equals(expectedResult);
	}

	@Test
	public void allProducts() throws JRException {
		String jsonQL_1 = "..[prodId, prodQty]";
		JsonNodeContainer jsonQL_1_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_1);

		assert toArrayNode(jsonQL_1_result).equals(expectedResult);

		String jsonQL_2 = "..*(prodId != null)";
		JsonNodeContainer jsonQL_2_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_2);

		assert toArrayNode(jsonQL_2_result).equals(expectedResult);

		String jsonQL_3 = "..prodId(@isNotNull)^";
		JsonNodeContainer jsonQL_3_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_3);

		assert toArrayNode(jsonQL_3_result).equals(expectedResult);

		String jsonQL_4 = "..prodId(!@isNull)^";
		JsonNodeContainer jsonQL_4_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_4);

		assert toArrayNode(jsonQL_4_result).equals(expectedResult);

		String jsonQL_5 = "..prodId(@val != null)^";
		JsonNodeContainer jsonQL_5_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_5);

		assert toArrayNode(jsonQL_5_result).equals(expectedResult);
	}

	@Test
	public void productsOfShippedOrders() throws JRException {
		String jsonQL_1 = "..[prodId, prodQty](^^^.shipped == null)";
		JsonNodeContainer jsonQL_1_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_1);

		assert toArrayNode(jsonQL_1_result).equals(expectedResult);

		String jsonQL_2 = "..*(prodId != null && ^^.shippedOn != null)";
		JsonNodeContainer jsonQL_2_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_2);

		assert toArrayNode(jsonQL_2_result).equals(expectedResult);

		String jsonQL_3 = "..shippedOn(@val != null)^..[prodId, prodQty]";
		JsonNodeContainer jsonQL_3_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_3);

		assert toArrayNode(jsonQL_3_result).equals(expectedResult);

		String jsonQL_4 = "..products(^.shippedOn != null)..[prodId, prodQty]";
		JsonNodeContainer jsonQL_4_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_4);

		assert toArrayNode(jsonQL_4_result).equals(expectedResult);
	}

	@Test
	public void productsOfOrdersWithAtLeast2products() throws JRException {
		String jsonQL_1 = "..products(*@size >=2)..*(prodId != null)";
		JsonNodeContainer jsonQL_1_result = jsonQLExecuter.evaluateExpression(jrJsonNode, jsonQL_1);

		assert toArrayNode(jsonQL_1_result).equals(expectedResult);
	}

	private ArrayNode toArrayNode(JsonNodeContainer container) {
		ArrayNode result = jsonQLExecuter.getEvaluator().getEvaluationContext().getObjectMapper().createArrayNode();

		for (JRJsonNode node: container.getContainerNodes()) {
			result.add(node.getDataNode());
		}

		return result;
	}

}
