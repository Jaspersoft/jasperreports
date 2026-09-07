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
package net.sf.jasperreports.ohloh;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.export.DefaultElementHandlerBundle;
import net.sf.jasperreports.engine.export.GenericElementHandler;
import net.sf.jasperreports.engine.export.GenericElementHandlerBundle;
import net.sf.jasperreports.extensions.ExtensionsRegistry;
import net.sf.jasperreports.extensions.ExtensionsRegistryFactory;
import net.sf.jasperreports.extensions.ListExtensionsRegistry;
import net.sf.jasperreports.extensions.SingletonExtensionRegistry;

/**
 * Ohloh extensions registry factory.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class OhlohExtensions implements ExtensionsRegistryFactory
{

	@Override
	public ExtensionsRegistry createRegistry(String registryId, JRPropertiesMap properties)
	{
		Map<String, Map<String, GenericElementHandler>> elementHandlers = new HashMap<>();
		OhlohWidgetHtmlHandler languagesHandler = new OhlohWidgetHtmlHandler();
		languagesHandler.setProjectIDParameter("ProjectID");
		languagesHandler.setWidgetName("project_languages");
		elementHandlers.put("languages", Collections.singletonMap(
				"net.sf.jasperreports.html", languagesHandler));

		OhlohWidgetHtmlHandler statsHandler = new OhlohWidgetHtmlHandler();
		statsHandler.setProjectIDParameter("ProjectID");
		statsHandler.setWidgetName("project_basic_stats");
		elementHandlers.put("stats", Collections.singletonMap(
				"net.sf.jasperreports.html", statsHandler));

		DefaultElementHandlerBundle bundle = new DefaultElementHandlerBundle();
		bundle.setNamespace("http://jasperreports.sourceforge.net/jasperreports/ohloh");
		bundle.setElementHandlers(elementHandlers);
		
		return new SingletonExtensionRegistry<GenericElementHandlerBundle>(
				GenericElementHandlerBundle.class, bundle);
	}

}
