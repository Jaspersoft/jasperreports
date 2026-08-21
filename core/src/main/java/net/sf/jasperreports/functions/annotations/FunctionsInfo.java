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
package net.sf.jasperreports.functions.annotations;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.MessageProvider;
import net.sf.jasperreports.engine.util.MessageUtil;
import net.sf.jasperreports.functions.FunctionsBundle;
import net.sf.jasperreports.functions.FunctionsRegistryFactory;
import net.sf.jasperreports.functions.FunctionsUtil;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FunctionsInfo 
{
	/**
	 * 
	 */
	public static final String DEFAULT_MESSAGES_BUNDLE = "jasperreports_messages";
	public static final String PROPERTY_SUFFIX_NAME = "name";
	public static final String PROPERTY_SUFFIX_DESCRIPTION = "description";
	public static final String PROPERTY_SUFFIX_TYPE = "type";
	public static final String PROPERTY_PARAMETER_REQUIRED = FunctionsRegistryFactory.FUNCTIONS_CLASSES_PROPERTY_PREFIX +"parameter.required";
	public static final String PROPERTY_PARAMETER_OPTIONAL = FunctionsRegistryFactory.FUNCTIONS_CLASSES_PROPERTY_PREFIX +"parameter.optional";

	/**
	 * 
	 */
	private final JasperReportsContext jasperReportsContext;
	private final Locale locale;
	private final MessageUtil messageUtil;
	
	private final Map<String, FunctionCategoryBean> categories = new HashMap<>();
	
	/**
	 * 
	 */
	private FunctionsInfo(JasperReportsContext jasperReportsContext, Locale locale) 
	{
		this.jasperReportsContext = jasperReportsContext;
		this.locale = locale;
		this.messageUtil = MessageUtil.getInstance(jasperReportsContext);
		
		addFunctions();
	}
	
	/**
	 * 
	 */
	public static FunctionsInfo getInstance(JasperReportsContext jasperReportsContext, Locale locale)
	{
		return new FunctionsInfo(jasperReportsContext, locale);
	}
	
	/**
	 * 
	 */
	public Collection<FunctionCategoryBean> getCategories()
	{
		return categories.values();
	}
	
	/**
	 * 
	 */
	protected void addFunctions()
	{
		List<FunctionsBundle> bundles = FunctionsUtil.getInstance(jasperReportsContext).getAllFunctionBundles();
		for (FunctionsBundle bundle : bundles) 
		{
			List<Class<?>> functionClasses = bundle.getFunctionClasses();
			if (functionClasses != null && !functionClasses.isEmpty()) 
			{
				for (Class<?> functionClass : functionClasses) 
				{
					addFunctionClass(functionClass);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	protected void addFunctionClass(Class<?> functionClass)
	{
		FunctionCategories categories = functionClass.getAnnotation(FunctionCategories.class);
		Set<String> defaultCategories = addCategories(categories);
		
		MessageProvider provider = getMessageProvider(functionClass);
		
		Method[] methods = functionClass.getMethods();
		if (methods != null && methods.length > 0) 
		{
			for (Method method : methods) 
			{
				addFunction(method, provider, defaultCategories, getBoundaryMethods(method, methods));
			}
		}
	}
	
	/**
	 * 
	 */
	protected Set<String> addCategories(FunctionCategories categories)
	{
		Set<String> categoryIds = null;
		if (categories != null) 
		{
			categoryIds = new HashSet<>();
			for (Class<?> categoryClass : categories.value()) 
			{
				String categoryId = addCategoryClass(categoryClass);
				categoryIds.add(categoryId);
			}
		}
		return categoryIds;
	}
	
	/**
	 * 
	 */
	protected void addFunction(Method functionMethod, MessageProvider provider, Set<String> categoryIds, Method[] boundaryMethods)
	{
		Function function = functionMethod.getAnnotation(Function.class);
		if (function != null) 
		{
			String functionId = functionMethod.getDeclaringClass().getName() + "." + function.value();
			
			FunctionBean functionBean = 
				new FunctionBean(
					functionId,
					getName(functionId, provider),
					getDescription(functionId, provider),
					functionMethod.getReturnType()
					);

			FunctionParameters functionParameters = functionMethod.getAnnotation(FunctionParameters.class);
			Class<?>[] minParameters = boundaryMethods[0].getParameterTypes();
			Class<?>[] maxParameters = boundaryMethods[1].getParameterTypes();
			
			int params = 0, requiredParams = minParameters.length;
			if (functionParameters != null && functionParameters.value().length > 0) 
			{
				for (FunctionParameter functionParameter : functionParameters.value()) 
				{
					addFunctionParameter(functionBean, functionParameter, provider, maxParameters[params], params < requiredParams);
					++params;
				}
			} 

			FunctionParameter functionParameter = functionMethod.getAnnotation(FunctionParameter.class);
			if (functionParameter != null) 
			{
				addFunctionParameter(functionBean, functionParameter, provider, maxParameters[params], params < requiredParams);
			}

			FunctionCategories functionCategories = functionMethod.getAnnotation(FunctionCategories.class);
			Set<String> functionCategoryIds = addCategories(functionCategories);
			if (functionCategoryIds != null && functionCategoryIds.size() > 0)
			{
				categoryIds = functionCategoryIds;
			}

			for (String categoryId : categoryIds) 
			{
				FunctionCategoryBean categoryBean = categories.get(categoryId);
				categoryBean.addFunction(functionBean);
			}
		}
	}

	/**
	 * 
	 */
	protected void addFunctionParameter(FunctionBean functionBean, FunctionParameter functionParameter, MessageProvider provider, Class<?> type, boolean isRequired)
	{
		String parameterId = functionBean.getId() + "." + functionParameter.value();
		
		FunctionParameterBean functionParameterBean = 
			new FunctionParameterBean(
				parameterId,
				getName(parameterId, provider),
				getDescription(parameterId, provider),
				type,
				isRequired
				);
		
		functionBean.addParameter(functionParameterBean);
	}

	/**
	 * 
	 */
	protected String addCategoryClass(Class<?> categoryClass)
	{
		MessageProvider provider = getMessageProvider(categoryClass);
		
		String categoryId = categoryClass.getName();
		FunctionCategory category = categoryClass.getAnnotation(FunctionCategory.class);
		if (category != null && category.value() != null && category.value().trim().length() > 0) 
		{
			categoryId = category.value();
		}
		
		FunctionCategoryBean categoryBean = new FunctionCategoryBean(
				categoryId, 
				getName(categoryId, provider), 
				getDescription(categoryId, provider));
		
		categories.put(categoryId, categoryBean);
		
		return categoryId;
	}
	
	/**
	 * 
	 */
	private MessageProvider getMessageProvider(Class<?> clazz)
	{
		FunctionMessagesBundle functionMessagesBundle = clazz.getAnnotation(FunctionMessagesBundle.class);

		String messagesBundle = 
			functionMessagesBundle == null 
				? getDefaultMessageBundle(clazz)
				: functionMessagesBundle.value();

		return messageUtil.getMessageProvider(messagesBundle);//FIXME cache them
	}
		
	/**
	 * 
	 */
	private String getDefaultMessageBundle(Class<?> clazz) 
	{
		return clazz.getPackage().getName() + "." + DEFAULT_MESSAGES_BUNDLE;
	}
	
	/**
	 * 
	 */
	private String getName(String key, MessageProvider provider) 
	{
		return provider.getMessage(key + "." + PROPERTY_SUFFIX_NAME, null, locale);
	}
	
	/**
	 * 
	 */
	private String getDescription(String key, MessageProvider provider) 
	{
		return provider.getMessage(key + "." + PROPERTY_SUFFIX_DESCRIPTION, null, locale);
	}
	
	/**
	 * Returns an array of 2 overloaded methods: the first one provides the minimum number of arguments 
	 * and the second one provides the maximum number of arguments
	 */
	private Method[] getBoundaryMethods(Method method, Method[] methods) {
		Method minMethod = method, maxMethod = method;
		int minParams = method.getParameterTypes().length;
		int maxParams = method.getParameterTypes().length;
		String name = method.getName();
		for (Method m : methods) {
			if (name.equals(m.getName())) {
				int params = m.getParameterTypes().length;
				if (params < minParams) {
					minParams = params;
					minMethod = m;
				} else if (params > maxParams){
					maxParams = params;
					maxMethod = m;
				}
			}
		}
		return new Method[] {minMethod, maxMethod};
	}
}
