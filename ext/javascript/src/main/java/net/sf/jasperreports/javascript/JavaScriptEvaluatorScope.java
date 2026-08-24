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

import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillParameter;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.engine.util.JRClassLoader;
import net.sf.jasperreports.engine.util.ProtectionDomainFactory;
import net.sf.jasperreports.functions.FunctionsUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Context.ClassShutterSetter;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.optimizer.Codegen;
import org.mozilla.javascript.tools.shell.JavaPolicySecurity;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JavaScriptEvaluatorScope
{
	private static final Log log = LogFactory.getLog(JavaScriptEvaluatorScope.class);
	
	protected static final String EVALUATOR_VAR = "_jreval";
	
	/**
	 * Base JavaScript value class.
	 */
	public abstract static class JSValue
	{
		private final ScriptableObject scope;

		protected JSValue(ScriptableObject scope)
		{
			this.scope = scope;
		}
		
		protected final Object toJSValue(Object value)
		{
			return Context.javaToJS(value, scope);
		}
	}
	
	/**
	 * Parameter class used in JavaScript expressions.
	 */
	public static class JSParameter extends JSValue
	{
		private final JRFillParameter parameter;
		
		public JSParameter(JRFillParameter parameter, ScriptableObject scope)
		{
			super(scope);
			this.parameter = parameter;
		}
		
		public Object getValue()
		{
			return toJSValue(parameter.getValue());
		}
	}
	
	/**
	 * Field class used in JavaScript expressions.
	 */
	public static class JSField extends JSValue
	{
		private final JRFillField field;
		
		public JSField(JRFillField field, ScriptableObject scope)
		{
			super(scope);
			this.field = field;
		}
		
		public Object getValue()
		{
			return toJSValue(field.getValue());
		}
		
		public Object getOldValue()
		{
			return toJSValue(field.getOldValue());
		}
	}
	
	/**
	 * Variable class used in JavaScript expressions.
	 */
	public static class JSVariable extends JSValue
	{
		private final JRFillVariable variable;
		
		public JSVariable(JRFillVariable variable, ScriptableObject scope)
		{
			super(scope);
			this.variable = variable;
		}
		
		public Object getValue()
		{
			return toJSValue(variable.getValue());
		}
		
		public Object getOldValue()
		{
			return toJSValue(variable.getOldValue());
		}
		
		public Object getEstimatedValue()
		{
			return toJSValue(variable.getEstimatedValue());
		}
	}
	
	//TODO find a way to tell whether a Context is our own by only looking at it
	private static Map<Context, Void> ownContexts = Collections.synchronizedMap(new WeakHashMap<>());

	private ReportClassShutter classShutter;
	private Context context;
	private ScriptableObject scope;
	private volatile ProtectionDomain protectionDomain;
	private Map<String, Script> compiledExpressions = new HashMap<>();

	public JavaScriptEvaluatorScope(JasperReportsContext jrContext, JREvaluator evaluator, FunctionsUtil functionsUtil)
	{
		classShutter = new ReportClassShutter(jrContext);
		context = enter(null);
		ownContexts.put(context, null);
		
		int optimizationLevel = JRPropertiesUtil.getInstance(jrContext).getIntegerProperty(JavaScriptEvaluator.PROPERTY_OPTIMIZATION_LEVEL);
		if (log.isDebugEnabled())
		{
			log.debug("optimization level " + optimizationLevel);
		}
		context.setOptimizationLevel(optimizationLevel);
		
		context.getWrapFactory().setJavaPrimitiveWrap(false);
		
		//using a protection domain in getCompiledExpression
		context.setSecurityController(new JavaPolicySecurity());
		
		JavaScriptFunctionsObject functionsObject = new JavaScriptFunctionsObject(context, functionsUtil, evaluator);
		this.scope = context.initStandardObjects();
		// is this OK?  the original prototype set by initStandardObjects is lost, and functionsObject has no prototype.
		// seems to be fine for now, if not we could try setting the Object prototype to functionsObject.
		this.scope.setPrototype(functionsObject);
		
		this.scope.put(EVALUATOR_VAR, this.scope, evaluator);
		
		// exiting for now because we will enter later in ensureContext(), possibly on other thread
		Context.exit();
	}
	
	public void init(Map<String, JRFillParameter> parametersMap, 
			Map<String, JRFillField> fieldsMap,
			Map<String, JRFillVariable> variablesMap)
	{
		for (Map.Entry<String, JRFillParameter> entry : parametersMap.entrySet())
		{
			String name = entry.getKey();
			JRFillParameter param = entry.getValue();
			JSParameter jsParam = new JSParameter(param, scope);
			scope.put(JavaScriptCompiler.getParameterVar(name), scope, jsParam);
		}

		for (Map.Entry<String, JRFillVariable> entry : variablesMap.entrySet())
		{
			String name = entry.getKey();
			JRFillVariable var = entry.getValue();
			JSVariable jsVar = new JSVariable(var, scope);
			scope.put(JavaScriptCompiler.getVariableVar(name), scope, jsVar);
		}

		if (fieldsMap != null)
		{
			for (Map.Entry<String, JRFillField> entry : fieldsMap.entrySet())
			{
				String name = entry.getKey();
				JRFillField field = entry.getValue();
				JSField jsField = new JSField(field, scope);
				scope.put(JavaScriptCompiler.getFieldVar(name), scope, jsField);
			}
		}

	}

	protected void ensureContext()
	{
		enter(context);
	}
	
	public Object evaluateExpression(Script expression)
	{
		ensureContext();
		
		Object value = expression.exec(context, scope);
		
		Object javaValue;
		// not converting Number objects because the generic conversion call below
		// always converts to Double
		if (value == null || value instanceof Number)
		{
			javaValue = value;
		}
		else
		{
			try
			{
				javaValue = Context.jsToJava(value, Object.class);
			}
			catch (EvaluatorException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		return javaValue;
	}
	
	public Object evaluateExpression(String expression)
	{
		Script compiledExpression = getCompiledExpression(expression);
		return evaluateExpression(compiledExpression);
	}
	
	public void setScopeVariable(String name, Object value)
	{
		scope.put(name, scope, value);
	}
	
	//TODO move expression compilation to a separate class
	protected Script getCompiledExpression(String expression)
	{
		Script compiledExpression = compiledExpressions.get(expression);
		if (compiledExpression == null)
		{
			if (log.isTraceEnabled())
			{
				log.trace("compiling expression " + expression);
			}
			
			ensureContext();
			
			compiledExpression = context.compileString(expression, "expression", 0, getProtectionDomain());
			compiledExpressions.put(expression, compiledExpression);
		}
		return compiledExpression;
	}
	
	protected ProtectionDomain getProtectionDomain()
	{
		ProtectionDomain domain = protectionDomain;
		if (domain == null)
		{
			synchronized (this)
			{
				domain = protectionDomain;
				if (domain == null)
				{
					ProtectionDomainFactory protectionDomainFactory = JRClassLoader.getProtectionDomainFactory();
					domain = protectionDomain = protectionDomainFactory.getProtectionDomain(
							Codegen.class.getClassLoader());
				}
			}
		}
		return domain;
	}
	
	// enter a precreated context, or a new one if null is passed
	protected Context enter(Context context)
	{
		Context currentContext = Context.getCurrentContext();
		if (context != null && context == currentContext)
		{
			// already the current context
			return currentContext;
		}
		
		// exit the current context if any
		if (currentContext != null && ownContexts.containsKey(currentContext))
		{
			Context.exit();
		}
		
		Context newContext = ContextFactory.getGlobal().enterContext(context);
		ClassShutterSetter classShutterSetter = newContext.getClassShutterSetter();
		if (classShutterSetter != null)
		{
			classShutterSetter.setClassShutter(classShutter);
		}
		
		if (log.isDebugEnabled())
		{
			log.debug("entered context " + newContext + ", requested " + context);
		}
		
		return newContext;
	}
}
