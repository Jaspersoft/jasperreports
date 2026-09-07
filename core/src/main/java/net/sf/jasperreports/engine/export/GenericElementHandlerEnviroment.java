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
package net.sf.jasperreports.engine.export;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRGenericElementType;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.extensions.ExtensionsEnvironment;

/**
 * A class that provides access to 
 * {@link GenericElementHandler generic element handlers}.
 * 
 * <p>
 * Generic element handler bundles are registered as JasperReports extensions
 * of type {@link GenericElementHandlerBundle} via the central extension
 * framework (see {@link ExtensionsEnvironment}). 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class GenericElementHandlerEnviroment
{

	private static final Log log = LogFactory.getLog(
			GenericElementHandlerEnviroment.class);
	public static final String EXCEPTION_MESSAGE_KEY_HANDLERS_NOT_FOUND_FOR_NAMESPACE = 
			"export.common.handlers.not.found.for.namespace";
	
	private final ReferenceMap<Object, List<GenericElementHandlerBundle>> handlersCache = 
		new ReferenceMap<>(
			ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
			);
	
	private JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	private GenericElementHandlerEnviroment(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	public static GenericElementHandlerEnviroment getInstance(JasperReportsContext jasperReportsContext)
	{
		return new GenericElementHandlerEnviroment(jasperReportsContext);
	}
	
	
	/**
	 * Returns a handler for a generic print element type and an exporter
	 * key.
	 *
	 * <p>
	 * The method first locates a 
	 * {@link GenericElementHandlerBundle handler bundle} that matches the type
	 * namespace, and then uses 
	 * {@link GenericElementHandlerBundle#getHandler(String, String)} to
	 * resolve an export handler.
	 * 
	 * @param type the generic element type
	 * @param exporterKey the exporter key
	 * @return a generic print element handler
	 * @throws JRRuntimeException if a handler does not exist for the 
	 * combination of element type and exporter key
	 */
	public GenericElementHandler getElementHandler(JRGenericElementType type,
			String exporterKey)
	{
		String namespace = type.getNamespace();
		String name = type.getName();
		List<GenericElementHandlerBundle> handlerBundles = getBundles();
		List<GenericElementHandler> handlers = handlerBundles.stream()
				.filter(bundle -> bundle.getNamespace().equals(namespace))
				.map(bundle -> bundle.getHandler(name, exporterKey))
				.filter(handler -> handler != null)
				.collect(Collectors.toList());

		if (handlers.isEmpty())
		{
			return null;
		}
		
		if (handlers.size() > 1)
		{
			log.warn("Found " + handlers.size() + " handlers for name " + type.getName()
					+ ", namespace " + namespace + ", exporter " + exporterKey);
		}
		return handlers.get(0);
	}

	protected List<GenericElementHandlerBundle> getBundles()
	{
		Object cacheKey = ExtensionsEnvironment.getExtensionsCacheKey();
		synchronized (handlersCache)
		{
			return handlersCache.computeIfAbsent(cacheKey, k -> loadBundles());
		}
	}

	protected List<GenericElementHandlerBundle> loadBundles()
	{
		return jasperReportsContext.getExtensions(GenericElementHandlerBundle.class);
	}
}
