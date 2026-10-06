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
package net.sf.jasperreports.javascript;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.fill.JREvaluator;
import net.sf.jasperreports.functions.FunctionSupport;
import net.sf.jasperreports.functions.FunctionsUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JavaScriptFunctionsObject implements Scriptable
{
	
	private static final Log log = LogFactory.getLog(JavaScriptFunctionsObject.class);

	private final Context context;
	private final FunctionsUtil functionsUtil;
	private final JREvaluator evaluator;
	private final Map<String, Object> functions;
	
	private Scriptable prototype;
	private Scriptable parentScope;
	
	public JavaScriptFunctionsObject(Context context, FunctionsUtil functionsUtil, JREvaluator evaluator)
	{
		this.context = context;
		this.functionsUtil = functionsUtil;
		this.evaluator = evaluator;
		this.functions = new HashMap<>();
	}
	
	@Override
	public String getClassName()
	{
		return "JRFunctions";
	}

	@Override
	public Object get(String name, Scriptable start)
	{
		Object function = resolveFunction(name, start);
		return function;
	}
	
	protected Object resolveFunction(String name, Scriptable start)
	{
		Object function = functions.get(name);
		if (function != null)
		{
			return function;
		}
		
		// remembering not found functions as well so that we don't look them up several times
		function = NOT_FOUND;
		
		Method method = functionsUtil.getMethod4Function(name);
		if (method != null)
		{
			Class<?> functionClass = method.getDeclaringClass();
			if (FunctionSupport.class.isAssignableFrom(functionClass))
			{
				// instance method, create the instance
				@SuppressWarnings("unchecked")
				FunctionSupport functionSupport = evaluator.getFunctionSupport(
						(Class<? extends FunctionSupport>) functionClass);
				
				// wrap the function support as a JavaScript object
				final Scriptable functionScriptable = context.getWrapFactory().wrapAsJavaObject(
						context, start, functionSupport, functionClass);
				// get the JavaScript method
				Object functionCall = functionScriptable.get(name, start);
				if (functionCall instanceof Callable)//this should be the case normally
				{
					Callable functionCallable = (Callable) functionCall;
					// wrap the method so that it uses functionScriptable as this object
					function = new JavaScriptCallableThisDecorator(functionCallable, functionScriptable);
				}
			}
			else
			{
				// static method
				// wrap the class
				Scriptable functionScriptable = context.getWrapFactory().wrapJavaClass(context, start, functionClass);
				// get the static JavaScript method
				Object functionCall = functionScriptable.get(name, start);
				if (functionCall instanceof Callable)//this should be the case normally
				{
					function = functionCall;
				}
			}
			
			if (function == NOT_FOUND && log.isDebugEnabled())
			{
				// should not happen normally
				log.debug("did not find JavaScript function " + name + " in class " + functionClass.getName());
			}
		}
		
		functions.put(name, function);
		return function;
	}

	@Override
	public Object get(int index, Scriptable start)
	{
		return NOT_FOUND;
	}

	@Override
	public boolean has(String name, Scriptable start)
	{
		Object function = resolveFunction(name, start);
		return function != NOT_FOUND;
	}

	@Override
	public boolean has(int index, Scriptable start)
	{
		return false;
	}

	@Override
	public void put(String name, Scriptable start, Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(int index, Scriptable start, Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String name)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(int index)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Scriptable getPrototype()
	{
		return prototype;
	}

	@Override
	public void setPrototype(Scriptable prototype)
	{
		this.prototype = prototype;
	}

	@Override
	public Scriptable getParentScope()
	{
		return parentScope;
	}

	@Override
	public void setParentScope(Scriptable parent)
	{
		this.parentScope = parent;
	}

	@Override
	public Object[] getIds()
	{
		return new Object[0];
	}

	@Override
	public Object getDefaultValue(Class<?> hint)
	{
		return "[object JRFunctions]";
	}

	@Override
	public boolean hasInstance(Scriptable instance)
	{
		return false;
	}

}
