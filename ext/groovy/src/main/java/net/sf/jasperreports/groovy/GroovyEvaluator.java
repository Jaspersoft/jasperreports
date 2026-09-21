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
package net.sf.jasperreports.groovy;

import groovy.lang.ExpandoMetaClass;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MissingMethodException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.engine.fill.JRExpressionEvalException;
import net.sf.jasperreports.engine.fill.JasperReportsContextAware;
import net.sf.jasperreports.functions.FunctionSupport;
import net.sf.jasperreports.functions.FunctionsUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod;

/**
 * Groovy expression evaluator that compiles expressions at fill time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRGroovyCompiler
 */
public abstract class GroovyEvaluator extends JREvaluator implements JasperReportsContextAware
{

	private static final Log log = LogFactory.getLog(GroovyEvaluator.class);
	private static final String EXCEPTION_MESSAGE_KEY_FUNCTION_NOT_FOUND = "compilers.groovy.function.not.found";
	
	//copied from groovy.lang.MetaClassImpl.chooseMostSpecificParams()
	private static final Pattern GROOVY_EXCEPTION_PATTERN_AMBIGUOUS_NULL = 
			Pattern.compile("Ambiguous method overloading for method.*Cannot resolve which method to invoke for \\[null\\] due to overlapping prototypes between.*",
					Pattern.DOTALL);
	
	private FunctionsUtil functionsUtil;
	
	private List<ClosureMetaMethod> functionMethods = new ArrayList<>();
	
	@Override
	public void setJasperReportsContext(JasperReportsContext context)
	{
		this.functionsUtil = FunctionsUtil.getInstance(context);
	}

	@Override
	protected Object handleEvaluationException(JRExpression expression, Throwable e) throws JRExpressionEvalException
	{
		if (ignoreNPE && e instanceof GroovyRuntimeException && e.getMessage() != null)
		{
			//in Groovy 2.0.1, 1 + null (and other e.g. BigDecimal * null) was throwing NPE
			//in 2.4.3, it fails with "Ambiguous method overloading..."
			//we're catching this exception (by message) and treating it like a NPE
			Matcher matcher = GROOVY_EXCEPTION_PATTERN_AMBIGUOUS_NULL.matcher(e.getMessage());
			if (matcher.matches())
			{
				//evaluating the expression to null to match Groovy 2.0.1 behaviour
				return null;
			}
		}
		
		//throw the exception
		return super.handleEvaluationException(expression, e);
	}

	protected Object functionCall(String methodName, Object[] args)
	{
		Method functionMethod = functionsUtil.getMethod4Function(methodName);
		if (functionMethod == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_FUNCTION_NOT_FOUND,
					new Object[]{methodName});
		}

		// we're relying on this, if we'll ever resolve functions to methods 
		// that have different names we need to adapt the code
		assert functionMethod.getName().equals(methodName);
		
		Class<?> functionClass = functionMethod.getDeclaringClass();
		MetaClass functionMetaClass = DefaultGroovyMethods.getMetaClass(functionClass);
		
		MethodClosure functionMethodClosure = null;
		if (FunctionSupport.class.isAssignableFrom(functionClass))
		{
			// search for an instance method that applies to the arguments
			MetaMethod metaMethod = functionMetaClass.getMetaMethod(methodName, args);
			if (metaMethod != null && metaMethod.isPublic())
			{
				@SuppressWarnings("unchecked")
				FunctionSupport functionObject = getFunctionSupport((Class<? extends FunctionSupport>) functionClass);
				functionMethodClosure = new MethodClosure(functionObject, methodName);
				
				if (log.isDebugEnabled())
				{
					log.debug("found public instance method " + metaMethod + " in class " + functionClass);
				}
			}
		}
		
		if (functionMethodClosure == null)
		{
			// searching for a static method that applies to the arguments
			MetaMethod metaMethod = functionMetaClass.getStaticMetaMethod(methodName, args);
			if (metaMethod != null && metaMethod.isPublic())
			{
				// creating a static method closure
				functionMethodClosure = new MethodClosure(functionClass, methodName);
				
				if (log.isDebugEnabled())
				{
					log.debug("found public static method " + metaMethod + " in class " + functionClass);
				}
			}
		}

		if (functionMethodClosure == null)
		{
			// we didn't find a public instance/static method that applies to the arguments
			throw new MissingMethodException(methodName, functionMetaClass.getTheClass(), args);
		}
		
		// adding the function methods for the name to the list of registered methods.
		// we need to add all the methods with the same name in the beginning because once we add one method 
		// methodMissing might no longer be called.
		// we need to reregister all methods on each new method since we create ExpandoMetaClass from scratch.
		addFunctionClosureMethods(functionMethodClosure, methodName);
		
		// adding the methods to the evaluator MetaClass so that it doesn't go again into methodMissing
		ExpandoMetaClass extendedMetaClass = new ExpandoMetaClass(getClass(), false);
		registerMethods(extendedMetaClass);
		extendedMetaClass.initialize();
		DefaultGroovyMethods.setMetaClass((GroovyObject) this, extendedMetaClass);
		
		// returning what we have to return
		return functionMethodClosure.call(args);
	}
	
	protected void addFunctionClosureMethods(MethodClosure methodClosure, String functionName)
	{
		// calling registerInstanceMethod(String, Closure) would register all methods, but we only want public methods
		List<MetaMethod> closureMethods = ClosureMetaMethod.createMethodList(functionName, getClass(), methodClosure);
		for (MetaMethod metaMethod : closureMethods)
		{
			if (!(metaMethod instanceof ClosureMetaMethod))
			{
				// should not happen
				log.warn("Got unexpected closure method " + metaMethod + " of type " + metaMethod.getClass().getName());
				continue;
			}
			
			ClosureMetaMethod closureMethod = (ClosureMetaMethod) metaMethod;
			if (!closureMethod.getDoCall().isPublic())
			{
				if (log.isDebugEnabled())
				{
					log.debug("method " + closureMethod.getDoCall() + " is not public, not registering");
				}
				continue;
			}
			
			if (log.isDebugEnabled())
			{
				log.debug("creating closure method for " + closureMethod.getDoCall());
			}
			
			functionMethods.add(closureMethod);
		}
	}

	protected void registerMethods(ExpandoMetaClass extendedMetaClass)
	{
		for (ClosureMetaMethod closureMethod : functionMethods)
		{
			extendedMetaClass.registerInstanceMethod(closureMethod);
		}
	}
}
