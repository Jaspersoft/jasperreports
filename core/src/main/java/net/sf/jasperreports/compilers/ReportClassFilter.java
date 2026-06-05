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
package net.sf.jasperreports.compilers;

import java.util.List;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.AbstractClassFilter;
import net.sf.jasperreports.engine.util.ClassWhitelist;
import net.sf.jasperreports.engine.util.StandardClassWhitelist;
import net.sf.jasperreports.functions.FunctionsBundle;
import net.sf.jasperreports.functions.FunctionsUtil;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReportClassFilter extends AbstractClassFilter
{
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "false",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_13_0,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CLASS_FILTER_ENABLED = 
			JRPropertiesUtil.PROPERTY_PREFIX + "report.class.filter.enabled";
	
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_6_13_0,
			name = "net.sf.jasperreports.report.class.whitelist.{arbitrary_name}"
			)
	public static final String PROPERTY_PREFIX_CLASS_WHITELIST = 
			JRPropertiesUtil.PROPERTY_PREFIX + "report.class.whitelist.";
	
	public static final String EXCEPTION_MESSAGE_KEY_CLASS_NOT_VISIBLE = "compilers.class.not.visible";
	
	@Override
	protected String getClassFilterEnabledPropertyName()
	{
		return PROPERTY_CLASS_FILTER_ENABLED;
	}
	
	@Override
	protected String getClassWhitelistPropertyPrefix()
	{
		return PROPERTY_PREFIX_CLASS_WHITELIST;
	}
	
	@Override
	protected String getClassNotVisibleExceptionMessageKey()
	{
		return EXCEPTION_MESSAGE_KEY_CLASS_NOT_VISIBLE;
	}

	public ReportClassFilter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}

	@Override
	protected void addExtraWhitelists(JasperReportsContext jasperReportsContext,
			List<ClassWhitelist> whitelists)
	{
		StandardClassWhitelist whitelist = new StandardClassWhitelist();
		whitelist.addClass("java.lang.Boolean");
		whitelist.addClass("java.lang.String");
		whitelist.addClass("java.lang.StringBuffer");
		whitelist.addClass("java.lang.StringBuilder");
		whitelist.addClass("java.lang.Character");
		whitelist.addClass("java.lang.Byte");
		whitelist.addClass("java.lang.Short");
		whitelist.addClass("java.lang.Integer");
		whitelist.addClass("java.lang.Long");
		whitelist.addClass("java.lang.Float");
		whitelist.addClass("java.lang.Double");
		whitelist.addClass("java.lang.Math");
		whitelists.add(whitelist);

		StandardClassWhitelist functionsWhitelist = new StandardClassWhitelist();
		FunctionsUtil functionsUtil = FunctionsUtil.getInstance(jasperReportsContext);
		List<FunctionsBundle> functionBundles = functionsUtil.getAllFunctionBundles();
		for (FunctionsBundle functionsBundle : functionBundles)
		{
			List<Class<?>> functionClasses = functionsBundle.getFunctionClasses();
			for (Class<?> functionClass : functionClasses)
			{
				functionsWhitelist.addClass(functionClass.getName());
			}
		}
		whitelists.add(functionsWhitelist);
	}

}
