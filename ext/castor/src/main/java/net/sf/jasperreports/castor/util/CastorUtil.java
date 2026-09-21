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
package net.sf.jasperreports.castor.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.xml.sax.InputSource;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.CompositeClassloader;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.VersionComparator;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import net.sf.jasperreports.util.CastorMapping;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @deprecated To be removed.
 */
public class CastorUtil
{
	private static final Log log = LogFactory.getLog(CastorUtil.class);
	
	private static final String EXCEPTION_MESSAGE_KEY_MAPPINGS_LOADING_ERROR = "util.castor.mappings.loading.error";
	
	/**
	 * 
	 */
	private static final String CASTOR_READ_XML_CONTEXT_KEY = "net.sf.jasperreports.castor.read.xml.context";
	private static final String CASTOR_WRITE_XML_CONTEXT_KEY = "net.sf.jasperreports.castor.write.xml.context";
	
	private static final Object CONTEXT_KEY_NULL = new Object();
	
	private JasperReportsContext jasperReportsContext;
	private VersionComparator versionComparator;


	/**
	 *
	 */
	private CastorUtil(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.versionComparator = new VersionComparator();
	}
	
	
	/**
	 *
	 */
	public static CastorUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return new CastorUtil(jasperReportsContext);
	}
	
	/**
	 * Convenience method called from main JasperReports library through reflection.
	 * @deprecated To be removed.
	 */
	public static Object read(JasperReportsContext jasperReportsContext, InputStream is)
	{
		return new CastorUtil(jasperReportsContext).read(is);
	}
	
	private XMLContext getReadXmlContext()
	{
		return getXmlContext(CASTOR_READ_XML_CONTEXT_KEY, null);//always reading with the last version mappings
	}
	
	private XMLContext getWriteXmlContext()
	{
		String targetVersion = JRPropertiesUtil.getInstance(jasperReportsContext).getProperty(
				JRXmlWriter.PROPERTY_REPORT_VERSION);
		if (log.isDebugEnabled())
		{
			log.debug("using write mappings for version " + targetVersion);
		}
		
		return getXmlContext(CASTOR_WRITE_XML_CONTEXT_KEY, targetVersion);
	}
	
	/**
	 *
	 */
	private XMLContext getXmlContext(String contextCacheKey, String version)
	{
		ClassLoader castorClassLoader = Mapping.class.getClassLoader();
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		
		Object cacheKey;
		ClassLoader contextClassLoader;
		if (threadClassLoader == null || threadClassLoader.equals(castorClassLoader))
		{
			cacheKey = CONTEXT_KEY_NULL;
			contextClassLoader = castorClassLoader;
		}
		else
		{
			cacheKey = threadClassLoader;
			contextClassLoader = new CompositeClassloader(castorClassLoader, threadClassLoader);
		}
		
		Map<Object, XMLContext> xmlContextCache = getXmlContextCache(contextCacheKey);
		XMLContext xmlContext = xmlContextCache.get(cacheKey);
		if (xmlContext == null)
		{
			xmlContext = new XMLContext();
			xmlContext.setClassLoader(contextClassLoader);

			Mapping mapping = new Mapping(contextClassLoader);
			
			List<CastorMapping> castorMappings = getMappings(version);
			for (CastorMapping castorMapping : castorMappings)
			{
				loadMapping(mapping, castorMapping.getPath());
			}
			
			try
			{
				xmlContext.addMapping(mapping);
			}
			catch (MappingException e)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_MAPPINGS_LOADING_ERROR,
						(Object[])null,
						e);
			}
			
			xmlContextCache.put(cacheKey, xmlContext);
		}
		return xmlContext;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<Object, XMLContext> getXmlContextCache(String contextCacheKey)
	{
		Map<Object, XMLContext> xmlContextCache = 
				(Map<Object, XMLContext>) jasperReportsContext.getOwnValue(contextCacheKey);
		if (xmlContextCache == null)
		{
			//TODO lucianc prevent double cache creation?
			xmlContextCache = Collections.synchronizedMap(
					new ReferenceMap<Object, XMLContext>(ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.SOFT));//using soft values is safer
			jasperReportsContext.setValue(contextCacheKey, xmlContextCache);
		}
		return xmlContextCache;
	}


	protected List<CastorMapping> getMappings(String version)
	{
		List<CastorMapping> castorMappings = jasperReportsContext.getExtensions(CastorMapping.class);
		Map<String, CastorMapping> keyMappings = new HashMap<>();
		for (CastorMapping mapping : castorMappings)
		{
			String key = mapping.getKey();
			if (key == null)
			{
				continue;
			}
			
			if (!isEligversionible(mapping, version))
			{
				continue;
			}
			
			CastorMapping existingMapping = keyMappings.get(key);
			if (existingMapping == null || newerThan(mapping, existingMapping))
			{
				keyMappings.put(key, mapping);
			}
		}
		
		List<CastorMapping> activeMappings = new ArrayList<>(castorMappings.size());
		for (CastorMapping mapping : castorMappings)
		{
			String key = mapping.getKey();
			if (key == null // mappings with no keys are always considered active
					// checking if it's the most recent eligible mapping
					|| keyMappings.get(key).equals(mapping))
			{
				activeMappings.add(mapping);
			}
		}
		return activeMappings;
	}

	protected boolean isEligversionible(CastorMapping castorMapping, String targetVersion)
	{
		String mappingVersion = getVersion(castorMapping);
		return versionComparator.compare(targetVersion, mappingVersion) >= 0;
	}
	
	private boolean newerThan(CastorMapping mapping, CastorMapping existingMapping)
	{
		String version = getVersion(mapping);
		String existingVersion = getVersion(existingMapping);
		return versionComparator.compare(version, existingVersion) > 0;
	}

	protected String getVersion(CastorMapping castorMapping)
	{
		String mappingVersion = castorMapping.getVersion();
		if (mappingVersion == null)
		{
			// if the mapping does not specify a version we consider it the initial mapping
			// using a min version to avoid null checks
			mappingVersion = VersionComparator.LOWEST_VERSION;
		}
		return mappingVersion;
	}
	
	/**
	 *
	 */
	private void loadMapping(Mapping mapping, String mappingFile)
	{
		try
		{
			byte[] mappingFileData = JRLoader.loadBytesFromResource(mappingFile);
			InputSource mappingSource = new InputSource(new ByteArrayInputStream(mappingFileData));

			mapping.loadMapping(mappingSource);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	
	/**
	 * 
	 */
	public Object read(InputStream is)
	{
		try
		{
			Unmarshaller unmarshaller = getReadXmlContext().createUnmarshaller();//FIXME initialization is not thread safe
			unmarshaller.setWhitespacePreserve(true);
			Object object = unmarshaller.unmarshal(new InputSource(is));
			return object;
		}
		catch (MarshalException | ValidationException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	public String writeToString(Object object)
	{
		StringWriter writer = new StringWriter();
		write(object, writer);
		return writer.toString();
	}
	
	public void writeToFile(Object object, String filename)
	{
		OutputStream output = null;
		boolean closed = false;
		try
		{
			output = new BufferedOutputStream(new FileOutputStream(filename));
			write(object, output);
			output.close();
			closed = true;
		}
		catch (IOException e)
		{
			throw new JRRuntimeException(e);
		}
		finally
		{
			if (output != null && !closed)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					//NOP
				}
			}
		}
	}
	
	public void write(Object object, OutputStream output)
	{
		Writer writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);//hardcoding utf8 instead of the default encoding
		write(object, writer);
	}
	
	public void write(Object object, Writer writer)
	{
		Marshaller marshaller = getWriteXmlContext().createMarshaller();
		try
		{
			marshaller.setWriter(writer);
			marshaller.setMarshalAsDocument(false);
			marshaller.marshal(object);
		}
		catch (IOException | MarshalException | ValidationException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	
}
