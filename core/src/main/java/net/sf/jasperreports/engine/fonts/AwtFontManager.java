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

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryUtil;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class AwtFontManager
{

	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			defaultValue = "false",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_7_0_3,
			valueType = Boolean.class
			)
	public static final String PROPERTY_USE_OWN_TEMP_FILE = JRPropertiesUtil.PROPERTY_PREFIX
			+ "font.use.own.temp.file";
	
	private static final Log log = LogFactory.getLog(AwtFontManager.class);

	private static final AwtFontManager INSTANCE = new AwtFontManager();
	
	public static AwtFontManager instance()
	{
		return INSTANCE;
	}
	
	private Map<FontFileReference, Object> fontFileReferences;
	private ReferenceQueue<Font> fontFilesQueue;
	
	public AwtFontManager()
	{
		fontFileReferences = new ConcurrentHashMap<>();
		fontFilesQueue = new ReferenceQueue<>();
	}
	
	public Font getAwtFont(JasperReportsContext jasperReportsContext, String ttfLocation)
	{
		boolean useOwnFile = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(
				PROPERTY_USE_OWN_TEMP_FILE, false);
		
		purgeFontFiles();
		
		InputStream is = null;
		Path fontFile;
		try
		{
			is = RepositoryUtil.getInstance(jasperReportsContext).getInputStreamFromLocation(ttfLocation);
			
			Font font;
			if (useOwnFile)
			{
				fontFile = Files.createTempFile("jr-font", ".ttf");
				fontFile.toFile().deleteOnExit();
				
				if (log.isDebugEnabled())
				{
					log.debug("created font file " + fontFile + " for " + ttfLocation);
				}
				
				Files.copy(is, fontFile, StandardCopyOption.REPLACE_EXISTING);
				
				font = Font.createFont(Font.TRUETYPE_FONT, fontFile.toFile());
				
				FontFileReference fontFileReference = new FontFileReference(font, fontFilesQueue, fontFile);
				fontFileReferences.put(fontFileReference, Boolean.TRUE);
			}
			else
			{
				if (log.isDebugEnabled())
				{
					log.debug("created font for " + ttfLocation);
				}

				font = Font.createFont(Font.TRUETYPE_FONT, is);
			}
			
			return font;
		}
		catch (JRException | IOException | FontFormatException e)
		{
			throw new InvalidFontException(ttfLocation, e);
		}
		finally
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
			}
			catch (IOException e)
			{
			}
		}
	}
	
	public void purgeFontFiles()
	{
		FontFileReference fontFileRef;
		while ((fontFileRef = (FontFileReference) fontFilesQueue.poll()) != null)
		{
			fontFileReferences.remove(fontFileRef);
			
			try
			{
				Files.deleteIfExists(fontFileRef.fontFile);
			}
			catch (IOException e)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Failed to delete font file " + fontFileRef.fontFile);
				}
			}
		}
	}
	
	private static class FontFileReference extends WeakReference<Font>
	{
		Path fontFile;
		
		FontFileReference(Font font, ReferenceQueue<Font> queue, Path fontFile)
		{
			super(font, queue);
			
			this.fontFile = fontFile;
		}
	}
	
}
