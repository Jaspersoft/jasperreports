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

import groovy.lang.MetaBeanProperty;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.metaclass.MultipleSetterProperty;
import org.kohsuke.groovy.sandbox.GroovyValueFilter;

import net.sf.jasperreports.compilers.ReportClassFilter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GroovyClassFilter extends GroovyValueFilter
{
	
	private ReportClassFilter classFilter;
	private GroovyMethodWhitelist methodWhitelist;
	private GroovySandboxEvaluator groovyEvaluator;
	
	public GroovyClassFilter(ReportClassFilter classFilter, GroovyMethodWhitelist methodWhitelist,
			GroovySandboxEvaluator groovyEvaluator)
	{
		this.classFilter = classFilter;
		this.methodWhitelist = methodWhitelist;
		this.groovyEvaluator = groovyEvaluator;
	}

	@Override
	public Object onMethodCall(Invoker invoker, Object receiver, 
			String method, Object... args) throws Throwable
	{
		checkMethodCall(receiver, method, args);

		return super.onMethodCall(invoker, receiver, method, args);
	}

	protected void checkMethodCall(Object receiver, String method, Object[] args)
	{
		if (receiver == null)
		{
			return;
		}

		MetaMethod metaMethod = InvokerHelper.getMetaClass(receiver).pickMethod(method,
				MetaClassHelper.convertToTypeArray(args));
		if (metaMethod != null)
		{
			checkResolvedMethod(metaMethod);
		}
	}

	@Override
	public Object onStaticCall(Invoker invoker, @SuppressWarnings("rawtypes") Class receiver, 
			String method, Object... args) throws Throwable
	{
		checkStaticMethodCall(receiver, method, args);

		return super.onStaticCall(invoker, receiver, method, args);
	}

	protected void checkStaticMethodCall(Class<?> receiver, String method, Object[] args)
	{
		MetaClass metaClass = InvokerHelper.getMetaClass(receiver);
		if (metaClass instanceof MetaClassImpl)
		{
			MetaMethod metaMethod = ((MetaClassImpl) metaClass).retrieveStaticMethod(method, args);
			if (metaMethod != null)
			{
				checkResolvedMethod(metaMethod);
			}
		}
	}

	@Override
	public Object onSuperCall(Invoker invoker, @SuppressWarnings("rawtypes") Class senderType, 
			Object receiver, String method, Object... args) throws Throwable
	{
		checkMethodCall(receiver, method, args);

		Object filteredReceiver = filterReceiver(receiver);
		Object[] filteredArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++)
		{
			filteredArgs[i] = filterArgument(args[i]);
		}

		return filterReturnValue(super.onSuperCall(invoker, senderType, 
				filteredReceiver, method, filteredArgs));
	}

	@Override
	public Object onGetProperty(Invoker invoker, Object receiver, String property) throws Throwable
	{
		checkPropertyGet(receiver, property);

		return super.onGetProperty(invoker, receiver, property);
	}

	@Override
	public Object onGetAttribute(Invoker invoker, Object receiver, String attribute) throws Throwable
	{
		//attributes are accessed directly as fields, no getter involved
		MetaProperty metaProperty = resolveProperty(receiver, attribute);
		if (metaProperty != null)
		{
			checkResolvedReturnType(metaProperty.getType());
		}

		return super.onGetAttribute(invoker, receiver, attribute);
	}

	protected void checkPropertyGet(Object receiver, String property)
	{
		MetaProperty metaProperty = resolveProperty(receiver, property);
		if (metaProperty != null)
		{
			MetaMethod getter = propertyGetter(metaProperty);
			if (getter != null)
			{
				methodWhitelist.checkMethod(getter);
			}
			
			checkResolvedReturnType(metaProperty.getType());
		}
	}

	@Override
	public Object onSetProperty(Invoker invoker, Object receiver, String property, Object value) throws Throwable
	{
		checkPropertySet(receiver, property);

		return super.onSetProperty(invoker, receiver, property, value);
	}

	protected void checkPropertySet(Object receiver, String property)
	{
		MetaProperty metaProperty = resolveProperty(receiver, property);
		if (metaProperty instanceof MetaBeanProperty)
		{
			MetaMethod setter = ((MetaBeanProperty) metaProperty).getSetter();
			if (setter != null)
			{
				methodWhitelist.checkMethod(setter);
			}
		}
	}

	protected MetaProperty resolveProperty(Object receiver, String property)
	{
		return receiver == null ? null : InvokerHelper.getMetaClass(receiver).getMetaProperty(property);
	}

	protected MetaMethod propertyGetter(MetaProperty metaProperty)
	{
		if (metaProperty instanceof MetaBeanProperty)
		{
			return ((MetaBeanProperty) metaProperty).getGetter();
		}
		if (metaProperty instanceof MultipleSetterProperty)
		{
			return ((MultipleSetterProperty) metaProperty).getGetter();
		}
		return null;
	}

	protected void checkResolvedMethod(MetaMethod metaMethod)
	{
		methodWhitelist.checkMethod(metaMethod);
		
		checkResolvedReturnType(metaMethod.getReturnType());
	}

	protected void checkResolvedReturnType(Class<?> returnType)
	{
		if (returnType != null && !returnType.isPrimitive())
		{
			classFilter.checkClassVisibility(returnType.getName());
		}
	}

	@Override
	public Object filter(Object o)
	{
		if (o != null)
		{
			Class<?> type = (o instanceof Class<?>) ? (Class<?>) o : o.getClass();
			if (type != groovyEvaluator.getClass())
			{
				classFilter.checkClassVisibility(type.getName());
			}
		}
		
		return super.filter(o);
	}

}
