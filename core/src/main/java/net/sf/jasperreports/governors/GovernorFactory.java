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
package net.sf.jasperreports.governors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.scriptlets.DefaultScriptletFactory;
import net.sf.jasperreports.engine.scriptlets.ScriptletFactory;
import net.sf.jasperreports.engine.scriptlets.ScriptletFactoryContext;
import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class GovernorFactory implements ScriptletFactory
{

	private static final GovernorFactory INSTANCE = new GovernorFactory();
	
	private GovernorFactory()
	{
	}
	
	/**
	 * 
	 */
	public static GovernorFactory getInstance()
	{
		return INSTANCE;
	}

	@Override
	public List<JRAbstractScriptlet> getScriplets(ScriptletFactoryContext context) throws JRException
	{
		List<JRAbstractScriptlet> scriptlets = new ArrayList<>();

		boolean maxPagesEnabled = JRPropertiesUtil.getInstance(context.getJasperReportsContext()).getBooleanProperty(context.getDataset(), MaxPagesGovernor.PROPERTY_MAX_PAGES_ENABLED, true);
		if (maxPagesEnabled)
		{
			int maxPages = JRPropertiesUtil.getInstance(context.getJasperReportsContext()).getIntegerProperty(context.getDataset(), MaxPagesGovernor.PROPERTY_MAX_PAGES, 0);
			if (maxPages > 0)
			{
				scriptlets.add(new MaxPagesGovernor(maxPages));
			}
		}
		
		boolean timeoutEnabled = JRPropertiesUtil.getInstance(context.getJasperReportsContext()).getBooleanProperty(context.getDataset(), TimeoutGovernor.PROPERTY_TIMEOUT_ENABLED, true);
		if (timeoutEnabled)
		{
			long timeout = JRPropertiesUtil.getInstance(context.getJasperReportsContext()).getLongProperty(context.getDataset(), TimeoutGovernor.PROPERTY_TIMEOUT, 0l);
			if (timeout > 0)
			{
				scriptlets.add(new TimeoutGovernor(timeout));
			}
		}
		
		return scriptlets;
	}
	
	/**
	 *
	 */
	protected JRAbstractScriptlet getScriptlet(String scriptletClassName) throws JRException
	{
		JRAbstractScriptlet scriptlet = null;

		try
		{
			Class<?> scriptletClass = JRClassLoader.resolveClassForName(scriptletClassName);
			scriptlet = scriptletClass.asSubclass(JRAbstractScriptlet.class).getDeclaredConstructor().newInstance();
		}
		catch (ClassNotFoundException e)
		{
			throw 
				new JRException(
					DefaultScriptletFactory.EXCEPTION_MESSAGE_KEY_CLASS_LOADING_ERROR,
					new Object[]{scriptletClassName}, 
					e);
		}
		catch (NoSuchMethodException | InvocationTargetException 
			| IllegalAccessException | InstantiationException e)
		{
			throw 
				new JRException(
					DefaultScriptletFactory.EXCEPTION_MESSAGE_KEY_INSTANCE_LOADING_ERROR,
					new Object[]{scriptletClassName}, 
					e);
		}
		
		return scriptlet;
	}
	
}
