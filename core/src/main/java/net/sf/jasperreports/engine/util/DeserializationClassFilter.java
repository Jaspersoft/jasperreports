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
package net.sf.jasperreports.engine.util;

import java.util.List;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class DeserializationClassFilter extends AbstractClassFilter
{
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			defaultValue = "true",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_4,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CLASS_FILTER_ENABLED = 
			JRPropertiesUtil.PROPERTY_PREFIX + "deserialization.class.filter.enabled";
	
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_4,
			name = "net.sf.jasperreports.deserialization.class.whitelist.{arbitrary_name}"
			)
	public static final String PROPERTY_PREFIX_CLASS_WHITELIST = 
			JRPropertiesUtil.PROPERTY_PREFIX + "deserialization.class.whitelist.";
	
	public static final String EXCEPTION_MESSAGE_KEY_CLASS_NOT_VISIBLE = "deserialization.class.not.visible";
	
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

	public DeserializationClassFilter(JasperReportsContext jasperReportsContext)
	{
		super(jasperReportsContext);
	}

	@Override
	protected void addExtraWhitelists(JasperReportsContext jasperReportsContext,
			List<ClassWhitelist> whitelists)
	{
		StandardClassWhitelist whitelist = new StandardClassWhitelist();
		whitelist.addClass("B");
		//whitelist.addClass("C");
		whitelist.addClass("D");
		whitelist.addClass("F");
		whitelist.addClass("I");
		whitelist.addClass("J");
		whitelist.addClass("S");
		whitelist.addClass("Z");
		whitelist.addClass("java.lang.Boolean");
		whitelist.addClass("java.lang.Byte");
		whitelist.addClass("java.lang.Character");
		whitelist.addClass("java.lang.Double");
		whitelist.addClass("java.lang.Enum");
		whitelist.addClass("java.lang.Float");
		whitelist.addClass("java.lang.Integer");
		whitelist.addClass("java.lang.Long");
		whitelist.addClass("java.lang.Number");
		whitelist.addClass("java.lang.Object");
		whitelist.addClass("java.lang.Short");
		whitelist.addClass("java.lang.String");
		whitelists.add(whitelist);

		List<DeserializationClassWhitelist> extensionWhitelists = jasperReportsContext.getExtensions(
				DeserializationClassWhitelist.class);
		whitelists.addAll(extensionWhitelists);
	}
}
