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
package net.sf.jasperreports.extensions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.util.ClassLoaderResource;
import net.sf.jasperreports.engine.util.ClassUtils;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.ObjectUtils;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * The default {@link ExtensionsRegistry extension registry} implementation.
 *
 * <p>
 * The implementation builds an extension registry by scanning the context
 * classloader for resources named <code>jasperreports_extension.properties</code>.
 * 
 * <p>
 * Each such resource is loaded as a properties file, and properties that start
 * with {@link #PROPERTY_REGISTRY_FACTORY_PREFIX net.sf.jasperreports.extension.registry.factory.} are identified.
 * 
 * <p>
 * Each such property should have as value the name of a 
 * {@link ExtensionsRegistryFactory} implementation.  The registry factory class is
 * instantiated, and
 * {@link ExtensionsRegistryFactory#createRegistry(String, JRPropertiesMap)}
 * is called on it, using the property suffix as registry ID and passing the
 * properties map.  The registry factory can collect properties that apply to the
 * specific registry by using a property prefix obtain by appending the registry ID
 * to "{@link #PROPERTY_REGISTRY_PREFIX net.sf.jasperreports.extension.}".
 * 
 * <p>
 * If instantiating an extension registry results in an exception, the registry
 * is skipped and an error message is logged.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DefaultExtensionsRegistry implements ExtensionsRegistry
{
	
	private final Log log = LogFactory.getLog(DefaultExtensionsRegistry.class);
	
	/**
	 * The name of property file resources that are used to load JasperReports 
	 * extensions.
	 */
	public final static String EXTENSION_RESOURCE_NAME = 
		"jasperreports_extension.properties";
	
	/**
	 * The property prefix of extension registry factories.
	 */
	@Property(
			name = "net.sf.jasperreports.extension.registry.factory.{arbitrary_name}",
			category = PropertyConstants.CATEGORY_EXTENSIONS,
			scopes = {PropertyScope.EXTENSION},
			sinceVersion = PropertyConstants.VERSION_3_1_0
			)
	public final static String PROPERTY_REGISTRY_FACTORY_PREFIX = 
			JRPropertiesUtil.PROPERTY_PREFIX + "extension.registry.factory.";
	
	/**
	 * A prefix that can be used to provide registry-specific properties,
	 * by appending the registry ID and a fixed property suffix to it. 
	 */
	@Property(
			name = "net.sf.jasperreports.extension.{registry_id}.{property_suffix}",
			category = PropertyConstants.CATEGORY_EXTENSIONS,
			scopes = {PropertyScope.EXTENSION},
			sinceVersion = PropertyConstants.VERSION_3_1_0
			)
	public static final String PROPERTY_REGISTRY_PREFIX = 
			JRPropertiesUtil.PROPERTY_PREFIX + "extension.";
	
	/**
	 * A numerical weight to use for extensions created by a registry.
	 * 
	 * Extensions with bigger weights take precedence.
	 */
	@Property(
			name = "net.sf.jasperreports.extension.registry.weight.{registry_id}",
			category = PropertyConstants.CATEGORY_EXTENSIONS,
			scopes = {PropertyScope.EXTENSION},
			sinceVersion = PropertyConstants.VERSION_7_0_2,
			valueType = Integer.class,
			defaultValue = "1000"
			)
	public static final String PROPERTY_REGISTRY_WEIGHT_PREFIX = 
			JRPropertiesUtil.PROPERTY_PREFIX + "extension.registry.weight.";
	
	/**
	 * Default extension weight.
	 * 
	 * @see DefaultExtensionsRegistry#PROPERTY_REGISTRY_WEIGHT_PREFIX
	 */
	public static final int DEFAULT_EXTENSION_WEIGHT = 1000;

	private final ReferenceMap<Object, List<ExtensionsRegistry>> registrySetCache = 
		new ReferenceMap<>(
			ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
			);
	
	private final ReferenceMap<ClassLoader, Map<URL, URLRegistries>> registryCache = 
		new ReferenceMap<>(
			ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
			);

	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		List<ExtensionsRegistry> registries = getRegistries();
		List<T> extensions = new ArrayList<>(registries.size());
		for (Iterator<ExtensionsRegistry> it = registries.iterator(); it.hasNext();)
		{
			ExtensionsRegistry registry = it.next();
			List<T> registryExtensions = registry.getExtensions(extensionType);
			if (registryExtensions != null && !registryExtensions.isEmpty())
			{
				extensions.addAll(registryExtensions);
			}
		}
		return extensions;
	}
	
	protected List<ExtensionsRegistry> getRegistries()
	{
		List<ExtensionsRegistry> registries;
		Object cacheKey = ExtensionsEnvironment.getExtensionsCacheKey();
		synchronized (registrySetCache)
		{
			registries = registrySetCache.get(cacheKey);
			if (registries == null)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Loading registries for cache key " + cacheKey);
				}
				
				registries = loadRegistries();
				registrySetCache.put(cacheKey, registries);
			}
		}
		return registries;
	}
	
	protected List<ExtensionsRegistry> loadRegistries()
	{
		//there is no identity linked hash map/set, using separate map and list
		IdentityHashMap<ExtensionsRegistry, Object> registrySet = new IdentityHashMap<>();
		List<LoadedRegistry> allRegistries = new ArrayList<>();
		
		List<ClassLoaderResource> extensionResources = loadExtensionPropertyResources();
		for (ClassLoaderResource extensionResource : extensionResources)
		{
			ClassLoader classLoader = extensionResource.getClassLoader();
			Map<URL, URLRegistries> classLoaderRegistries = getClassLoaderRegistries(classLoader);
			
			URL url = extensionResource.getUrl();
			List<LoadedRegistry> registries;
			Map<String, Exception> registryExceptions = new LinkedHashMap<>();
			synchronized (classLoaderRegistries)
			{
				URLRegistries urlRegistries = classLoaderRegistries.get(url);
				if (urlRegistries == null)
				{
					if (log.isDebugEnabled())
					{
						log.debug("Loading JasperReports extension properties resource " 
								+ url);
					}
					
					JRPropertiesMap properties = JRPropertiesMap.loadProperties(url);
					URL duplicateURL = detectDuplicate(properties, classLoaderRegistries);//search across classloaders?
					if (duplicateURL == null)
					{
						registries = loadRegistries(properties, registryExceptions);
					}
					else
					{
						log.warn("Extension resource " + url + " was found to be a duplicate of "
								+ duplicateURL + " in classloader " + classLoader);
						registries = Collections.emptyList();
					}
					
					classLoaderRegistries.put(url, new URLRegistries(properties, registries));
				}
				else
				{
					registries = urlRegistries.registries;
				}
			}
			
			for (Map.Entry<String, Exception> entry : registryExceptions.entrySet())
			{
				log.error("Error instantiating extensions registry for " 
						+ entry.getKey() + " from " + url, entry.getValue());
			}
			
			for (LoadedRegistry loadedRegistry : registries)
			{
				//detecting identity duplicates
				ExtensionsRegistry extensionsRegistry = loadedRegistry.getRegistry();
				boolean added = registrySet.put(extensionsRegistry, Boolean.FALSE) == null;
				if (added)
				{
					allRegistries.add(loadedRegistry);
				}
				else if (log.isDebugEnabled())
				{
					log.debug("Found duplicate extension registry " + extensionsRegistry);
				}
			}
		}
		
		List<ExtensionsRegistry> registries = allRegistries.stream().sorted(
				Comparator.comparing(LoadedRegistry::getWeight).reversed()).map(
						LoadedRegistry::getRegistry).collect(Collectors.toList());
		return registries;
	}

	protected List<ClassLoaderResource> loadExtensionPropertyResources()
	{
		return JRLoader.getClassLoaderResources(
				EXTENSION_RESOURCE_NAME);
	}

	protected Map<URL, URLRegistries> getClassLoaderRegistries(ClassLoader classLoader)
	{
		synchronized (registryCache)
		{
			return registryCache.computeIfAbsent(classLoader, k -> new HashMap<>());
		}
	}
	
	protected List<LoadedRegistry> loadRegistries(JRPropertiesMap properties, 
			Map<String, Exception> registryExceptions)
	{
		List<LoadedRegistry> registries = new ArrayList<>();
		List<PropertySuffix> factoryProps = JRPropertiesUtil.getProperties(properties, 
				PROPERTY_REGISTRY_FACTORY_PREFIX);
		for (Iterator<PropertySuffix> it = factoryProps.iterator(); it.hasNext();)
		{
			PropertySuffix factoryProp = it.next();
			String registryId = factoryProp.getSuffix();
			String factoryClass = factoryProp.getValue();
			
			String weightProp = properties.getProperty(PROPERTY_REGISTRY_WEIGHT_PREFIX + registryId);
			int weight = weightProp == null || weightProp.isEmpty() ? DEFAULT_EXTENSION_WEIGHT
					: JRPropertiesUtil.asInteger(weightProp);
			
			if (log.isDebugEnabled())
			{
				log.debug("Instantiating registry of type " + factoryClass 
						+ " for property " + factoryProp.getKey()
						+ " with weight " + weight);
			}
			
			try
			{
				ExtensionsRegistry registry = instantiateRegistry(
						properties, registryId, factoryClass);
				LoadedRegistry loadedRegistry = new LoadedRegistry(registry, weight);
				registries.add(loadedRegistry);
			}
			catch (Exception e)
			{
				//skip this registry
				//error logging is deferred after the registries are cached to avoid a loop from JRRuntimeException.resolveMessage
				registryExceptions.put(registryId, e);
			}
		}
		return registries;
	}

	protected ExtensionsRegistry instantiateRegistry(
			JRPropertiesMap props, String registryId, String factoryClass)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Instantiating extensions registry for " + registryId
					+ " using factory class " + factoryClass);
		}
		
		ExtensionsRegistryFactory factory = (ExtensionsRegistryFactory) 
				ClassUtils.instantiateClass(factoryClass, ExtensionsRegistryFactory.class);
		return factory.createRegistry(registryId, props);
	}
	
	protected URL detectDuplicate(JRPropertiesMap properties, Map<URL, URLRegistries> registries)
	{
		URL duplicateURL = null;
		for (Entry<URL, URLRegistries> registryEntry : registries.entrySet())
		{
			JRPropertiesMap entryProperties = registryEntry.getValue().properties;
			if (ObjectUtils.equals(properties, entryProperties))
			{
				duplicateURL = registryEntry.getKey();
				break;
			}
		}
		return duplicateURL;
	}
	
	protected static class URLRegistries
	{
		JRPropertiesMap properties;
		List<LoadedRegistry> registries;
		
		public URLRegistries(JRPropertiesMap properties, List<LoadedRegistry> registries)
		{
			this.properties = properties;
			this.registries = registries;
		}
	}

}
