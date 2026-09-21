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
package net.sf.jasperreports.javascript;

import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.engine.fill.JasperReportsContextAware;
import net.sf.jasperreports.functions.FunctionsUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * JavaScript expression evaluator that compiles expressions at fill time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JavaScriptCompiler
 */
public class JavaScriptEvaluator extends JREvaluator implements JasperReportsContextAware
{
	
	/**
	 * Property that determines the optimization level used when compiling expressions.
	 * 
	 * See <a href="http://www-archive.mozilla.org/rhino/apidocs/org/mozilla/javascript/Context.html#setOptimizationLevel%28int%29"/>
	 */
	@Property(
			category = PropertyConstants.CATEGORY_COMPILE,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_7_0,
			valueType = Integer.class
			)
	public static final String PROPERTY_OPTIMIZATION_LEVEL = JRPropertiesUtil.PROPERTY_PREFIX 
			+ "javascript.evaluator.optimization.level";
	
	private static final String EXCEPTION_MESSAGE_KEY_EVALUATOR_LOAD_ERROR = "compilers.javascript.evaluator.load.error";
	
	private final JasperReportsContext jrContext;
	private final JavaScriptCompileData compileData;
	private FunctionsUtil functionsUtil;
	private JavaScriptEvaluatorScope evaluatorScope;

	/**
	 * Create a JavaScript expression evaluator.
	 * 
	 * @param compileData the report compile data
	 */
	public JavaScriptEvaluator(JasperReportsContext jrContext, JavaScriptCompileData compileData)
	{
		this.jrContext = jrContext;
		this.compileData = compileData;
	}
	
	@Override
	public void setJasperReportsContext(JasperReportsContext context)
	{
		this.functionsUtil = FunctionsUtil.getInstance(context);
	}

	@Override
	protected void customizedInit(
			Map<String, JRFillParameter> parametersMap, 
			Map<String, JRFillField> fieldsMap,
			Map<String, JRFillVariable> variablesMap
			) throws JRException
	{
		evaluatorScope = new JavaScriptEvaluatorScope(jrContext, this, functionsUtil);
		evaluatorScope.init(parametersMap, fieldsMap, variablesMap);
	}
	
	@Override
	protected Object evaluate(int id) throws Throwable //NOSONAR
	{
		JavaScriptCompileData.Expression expression = getExpression(id);
		return evaluateExpression(expression.getDefaultExpression());
	}

	@Override
	protected Object evaluateEstimated(int id) throws Throwable //NOSONAR
	{
		JavaScriptCompileData.Expression expression = getExpression(id);
		return evaluateExpression(expression.getEstimatedExpression());
	}

	@Override
	protected Object evaluateOld(int id) throws Throwable //NOSONAR
	{
		JavaScriptCompileData.Expression expression = getExpression(id);
		return evaluateExpression(expression.getOldExpression());
	}

	protected JavaScriptCompileData.Expression getExpression(int id)
	{
		return compileData.getExpression(id);
	}
	
	protected Object evaluateExpression(String expression)
	{
		return evaluatorScope.evaluateExpression(expression);
	}
	
}
