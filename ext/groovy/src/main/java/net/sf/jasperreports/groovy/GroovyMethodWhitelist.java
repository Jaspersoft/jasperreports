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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.GeneratedMetaMethod;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.metaclass.ReflectionMetaMethod;

import groovy.lang.GroovyObject;
import groovy.lang.MetaMethod;
import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.StandardClassWhitelist;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * Whitelist of Groovy methods that are not plain methods of the receiver's class, applied
 * to report expressions when the report class filter is enabled.
 *
 * <p>
 * Groovy extension methods (e.g. {@link DefaultGroovyMethods} and other extension modules)
 * are implemented as static methods in classes other than the receiver's class, so checking
 * the receiver class against the report class whitelist does not reveal what is actually
 * invoked (e.g. <code>"cmd".execute()</code> calls <code>ProcessGroovyMethods.execute(String)</code>).
 * The same goes for the {@link GroovyObject} meta methods (<code>invokeMethod</code>,
 * <code>getProperty</code>, etc.), which dispatch dynamically to arbitrary methods.
 * Such methods are only allowed if listed in the whitelist as
 * <code>implementingClassName.methodName</code>.
 * </p>
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GroovyMethodWhitelist
{

	@Property(
			category = PropertyConstants.CATEGORY_SECURITY,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_1_2,
			name = "net.sf.jasperreports.report.groovy.method.whitelist.{arbitrary_name}"
			)
	public static final String PROPERTY_PREFIX_METHOD_WHITELIST =
			JRPropertiesUtil.PROPERTY_PREFIX + "report.groovy.method.whitelist.";

	public static final String EXCEPTION_MESSAGE_KEY_METHOD_NOT_ALLOWED = "compilers.groovy.method.not.allowed";

	private static final Set<String> GROOVY_OBJECT_METHODS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			"invokeMethod", "getProperty", "setProperty", "getMetaClass", "setMetaClass")));

	private static final String METHOD_SEPARATOR = ".";
	
	private static final Set<Class<?>> ADDITIONAL_METHOD_CLASSES = Collections.unmodifiableSet(
			new HashSet<>(Arrays.<Class<?>>asList(DefaultGroovyMethods.ADDITIONAL_CLASSES)));

	private final StandardClassWhitelist whitelist;

	private final Map<String, Boolean> allowedCache = new ConcurrentHashMap<>();

	//only caching precompiled DGM methods, they are Groovy runtime singletons
	private final Map<MetaMethod, String> generatedMethodKeys = new ConcurrentHashMap<>();

	public GroovyMethodWhitelist(JasperReportsContext jasperReportsContext)
	{
		whitelist = new StandardClassWhitelist();

		List<PropertySuffix> properties = JRPropertiesUtil.getInstance(jasperReportsContext).getProperties(
				PROPERTY_PREFIX_METHOD_WHITELIST);
		for (PropertySuffix property : properties)
		{
			whitelist.addWhitelist(property.getValue());
		}
	}

	/**
	 * Checks whether a resolved method can be called from report expressions.
	 *
	 * @param metaMethod the method resolved by Groovy for a call
	 * @throws JRRuntimeException if the method is subject to the whitelist and not whitelisted
	 */
	public void checkMethod(MetaMethod metaMethod) throws JRRuntimeException
	{
		String key = methodKey(metaMethod);
		if (key != null && !isAllowed(key))
		{
			throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_METHOD_NOT_ALLOWED, new Object[] {key});
		}
	}

	protected boolean isAllowed(String key)
	{
		return allowedCache.computeIfAbsent(key, whitelist::includesClass);
	}

	/**
	 * Returns the whitelist key of a method, as <code>implementingClassName.methodName</code>,
	 * or <code>null</code> if the method is a plain method of the receiver's class and thus only
	 * subject to the report class whitelist.
	 */
	protected String methodKey(MetaMethod metaMethod)
	{
		String name = metaMethod.getName();
		if (metaMethod instanceof CachedMethod)
		{
			Class<?> declaringClass = metaMethod.getDeclaringClass().getTheClass();
			if (GROOVY_OBJECT_METHODS.contains(name) && GroovyObject.class.isAssignableFrom(declaringClass))
			{
				return GroovyObject.class.getName() + METHOD_SEPARATOR + name;
			}
			return null;
		}

		if (metaMethod instanceof ReflectionMetaMethod)
		{
			//NewInstanceMetaMethod/NewStaticMetaMethod, i.e. extension modules and static DGM methods
			CachedMethod cachedMethod = (CachedMethod) ((ReflectionMetaMethod) metaMethod).getCachedMethod();
			return cachedMethod.getDeclaringClass().getName() + METHOD_SEPARATOR + name;
		}

		if (metaMethod instanceof GeneratedMetaMethod)
		{
			return generatedMethodKeys.computeIfAbsent(metaMethod, this::generatedMethodKey);
		}
		
		if (ADDITIONAL_METHOD_CLASSES.contains(metaMethod.getClass()))
		{
			//DGM methods implemented directly as meta method classes (number arithmetic, array access)
			return metaMethod.getClass().getName() + METHOD_SEPARATOR + name;
		}

		//any other kind of meta method (expando, mixin, etc.) is subject to the whitelist
		return metaMethod.getDeclaringClass().getName() + METHOD_SEPARATOR + name;
	}

	protected String generatedMethodKey(MetaMethod metaMethod)
	{
		//precompiled DGM methods (dgm$NNN) report the receiver type as declaring class
		//and do not expose the implementing class, looking for it among the DGM-like classes
		Class<?>[] parameterTypes = metaMethod.getNativeParameterTypes();
		Class<?>[] staticParameterTypes = new Class<?>[parameterTypes.length + 1];
		staticParameterTypes[0] = metaMethod.getDeclaringClass().getTheClass();
		System.arraycopy(parameterTypes, 0, staticParameterTypes, 1, parameterTypes.length);

		for (Class<?> dgmClass : DefaultGroovyMethods.DGM_LIKE_CLASSES)
		{
			try
			{
				Method method = dgmClass.getMethod(metaMethod.getName(), staticParameterTypes);
				if (Modifier.isStatic(method.getModifiers()))
				{
					return dgmClass.getName() + METHOD_SEPARATOR + metaMethod.getName();
				}
			}
			catch (NoSuchMethodException e)
			{
				//not in this class
			}
		}

		//the implementing class was not found, using a key that is not going to be whitelisted
		return metaMethod.getClass().getName() + METHOD_SEPARATOR + metaMethod.getName();
	}

}
