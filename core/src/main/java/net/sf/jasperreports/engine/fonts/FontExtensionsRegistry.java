/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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
package net.sf.jasperreports.engine.fonts;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.extensions.ExtensionsRegistry;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class FontExtensionsRegistry implements ExtensionsRegistry
{
	
	private static final Log log = LogFactory.getLog(FontExtensionsRegistry.class);

	private final List<String> fontFamiliesLocations;
	private List<FontFamily> fontFamilies;
	private List<FontSet> fontSets;
	
	public FontExtensionsRegistry(List<String> fontFamiliesLocations)
	{
		this.fontFamiliesLocations = fontFamiliesLocations;
	}
	
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		if (FontFamily.class.equals(extensionType)) 
		{
			ensureFontExtensions();
			
			@SuppressWarnings("unchecked")
			List<T> extensions = (List<T>) fontFamilies;
			return extensions;
		}
		
		if (FontSet.class.equals(extensionType)) 
		{
			ensureFontExtensions();
			
			@SuppressWarnings("unchecked")
			List<T> extensions = (List<T>) fontSets;
			return extensions;
		}
		
		return null;
	}

	protected void ensureFontExtensions()
	{
		if ((fontFamilies == null || fontSets == null) && fontFamiliesLocations != null)
		{
			SimpleFontExtensionHelper fontExtensionHelper = SimpleFontExtensionHelper.getInstance();
			DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
			
			FontExtensionsCollector extensionsCollector = new FontExtensionsCollector();
			for (String location : fontFamiliesLocations)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Loading font extensions from " + location);
				}
				
				try
				{
					fontExtensionHelper.loadFontExtensions(context, location, extensionsCollector);
				}
				catch (JRRuntimeException e)//only catching JRRuntimeException for now
				{
					log.error("Error loading font extensions from " + location, e);
					//keeping any font extensions collected so far, though it's a little weird
				}
			}
			
			fontFamilies = extensionsCollector.getFontFamilies();
			fontSets = extensionsCollector.getFontSets();
		}
	}

}
