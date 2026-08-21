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
package net.sf.jasperreports.engine.fonts;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.JRFontNotFoundException;
import net.sf.jasperreports.engine.util.JRGraphEnvInitializer;
import net.sf.jasperreports.engine.util.JRTextAttribute;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class FontUtil
{
	private static final Log log = LogFactory.getLog(FontUtil.class);
	public static final String EXCEPTION_MESSAGE_KEY_NULL_FONT = "engine.fonts.null.font";
	public static final String EXCEPTION_MESSAGE_KEY_FONT_SET_FAMILY_NOT_FOUND = "util.font.set.family.not.found";

	private static final Object CACHE_NULL_VALUE = new Object();
	
	private JasperReportsContext jasperReportsContext;
	private ConcurrentHashMap<FontInfoKey, Object> fontInfoCache;
	private ConcurrentHashMap<FontSetInfoKey, Object> fontSetInfoCache;


	/**
	 *
	 */
	private FontUtil(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.fontInfoCache = new ConcurrentHashMap<>();
		this.fontSetInfoCache = new ConcurrentHashMap<>();
	}
	
	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}
	
	/**
	 *
	 */
	public static FontUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return new FontUtil(jasperReportsContext);
	}
	
	
	/**
	 *.
	 */ //FIXMECONTEXT this should no longer be a thread local
	private static final InheritableThreadLocal<Set<String>> threadMissingFontsCache = new InheritableThreadLocal<Set<String>>()
	{
		@Override
		protected Set<String> initialValue() {
			return new HashSet<>();
		}
	};
	
	/**
	 *
	 */
	public static void copyNonNullOwnProperties(JRFont srcFont, JRFont destFont)
	{
		if(srcFont != null && destFont != null)
		{
			if (srcFont.getOwnFontName() != null)
			{
				destFont.setFontName(srcFont.getOwnFontName());
			}
			if (srcFont.isOwnBold() != null)
			{
				destFont.setBold(srcFont.isOwnBold());
			}
			if (srcFont.isOwnItalic() != null)
			{
				destFont.setItalic(srcFont.isOwnItalic());
			}
			if (srcFont.isOwnUnderline() != null)
			{
				destFont.setUnderline(srcFont.isOwnUnderline());
			}
			if (srcFont.isOwnStrikeThrough() != null)
			{
				destFont.setStrikeThrough(srcFont.isOwnStrikeThrough());
			}
			if (srcFont.getOwnFontSize() != null)
			{
				destFont.setFontSize(srcFont.getOwnFontSize());
			}
			if (srcFont.getOwnPdfFontName() != null)
			{
				destFont.setPdfFontName(srcFont.getOwnPdfFontName());
			}
			if (srcFont.getOwnPdfEncoding() != null)
			{
				destFont.setPdfEncoding(srcFont.getOwnPdfEncoding());
			}
			if (srcFont.isOwnPdfEmbedded() != null)
			{
				destFont.setPdfEmbedded(srcFont.isOwnPdfEmbedded());
			}
		}
	}
	

	/**
	 *
	 */
	public Map<Attribute,Object> getAttributesWithoutAwtFont(Map<Attribute,Object> attributes, JRFont font)
	{
		attributes.put(TextAttribute.FAMILY, font.getFontName());

		attributes.put(TextAttribute.SIZE, font.getFontSize());

		if (font.isBold())
		{
			attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		}
		if (font.isItalic())
		{
			attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		}
		if (font.isUnderline())
		{
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		if (font.isStrikeThrough())
		{
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		}
		
		attributes.put(JRTextAttribute.PDF_FONT_NAME, font.getPdfFontName());
		attributes.put(JRTextAttribute.PDF_ENCODING, font.getPdfEncoding());

		if (font.isPdfEmbedded())
		{
			attributes.put(JRTextAttribute.IS_PDF_EMBEDDED, Boolean.TRUE);
		}

		return attributes;
	}


	/**
	 * Returns font information containing the font family, font face and font style.
	 * 
	 * @param name the font family or font face name
	 * @param ignoreCase the flag to specify if family names or face names are searched by ignoring case or not
	 * @param locale the locale
	 * @return a font info object
	 */
	public FontInfo getFontInfo(String name, boolean ignoreCase, Locale locale)
	{
		FontInfoKey key = new FontInfoKey(name, ignoreCase, locale);
		Object cachedFontInfo = fontInfoCache.get(key);
		if (cachedFontInfo == null)
		{
			FontInfo fontInfo = searchFontInfo(name, ignoreCase, locale);
			if (log.isDebugEnabled())
			{
				log.debug("looking for font " + name + ", ignore case " + ignoreCase
						+ ", locale " + locale
						+ " found " + fontInfo);
			}
			
			cachedFontInfo = fontInfo == null ? CACHE_NULL_VALUE : fontInfo;
			fontInfoCache.put(key, cachedFontInfo);
		}
		return cachedFontInfo == CACHE_NULL_VALUE ? null : (FontInfo) cachedFontInfo;
	}
	
	protected FontInfo searchFontInfo(String name, boolean ignoreCase, Locale locale)
	{
		FontInfo awtFamilyMatchFontInfo = null;

		List<FontFamily> families = jasperReportsContext.getExtensions(FontFamily.class);
		for (Iterator<FontFamily> itf = families.iterator(); itf.hasNext();)
		{
			FontFamily family = itf.next();
			if (locale == null || family.supportsLocale(locale))
			{
				if (equals(name, family.getName(), ignoreCase))
				{
					return new FontInfo(family, null, Font.PLAIN);
				}
				
				FontFace face = family.getNormalFace();
				if (face != null)
				{
					if (equals(name, face.getName(), ignoreCase))
					{
						return new FontInfo(family, face, Font.PLAIN);
					}
					else if (
						awtFamilyMatchFontInfo == null
						&& face.getFont() != null
						&& equals(name, face.getFont().getFamily(), ignoreCase)
						)
					{
						awtFamilyMatchFontInfo = new FontInfo(family, face, Font.PLAIN);
					}
				}

				face = family.getBoldFace();
				if (face != null)
				{
					if (equals(name, face.getName(), ignoreCase))
					{
						return new FontInfo(family, face, Font.BOLD);
					}
					else if (
						awtFamilyMatchFontInfo == null
						&& face.getFont() != null
						&& equals(name, face.getFont().getFamily(), ignoreCase)
						)
					{
						awtFamilyMatchFontInfo = new FontInfo(family, face, Font.BOLD);
					}
				}

				face = family.getItalicFace();
				if (face != null)
				{
					if (equals(name, face.getName(), ignoreCase))
					{
						return new FontInfo(family, face, Font.ITALIC);
					}
					else if (
						awtFamilyMatchFontInfo == null
						&& face.getFont() != null
						&& equals(name, face.getFont().getFamily(), ignoreCase)
						)
					{
						awtFamilyMatchFontInfo = new FontInfo(family, face, Font.ITALIC);
					}
				}

				face = family.getBoldItalicFace();
				if (face != null)
				{
					if (equals(name, face.getName(), ignoreCase))
					{
						return new FontInfo(family, face, Font.BOLD | Font.ITALIC);
					}
					else if (
						awtFamilyMatchFontInfo == null
						&& face.getFont() != null
						&& equals(name, face.getFont().getFamily(), ignoreCase)
						)
					{
						awtFamilyMatchFontInfo = new FontInfo(family, face, Font.BOLD | Font.ITALIC);
					}
				}
			}
		}
		
		return awtFamilyMatchFontInfo;
	}


	private static boolean equals(String value1, String value2, boolean ignoreCase)
	{
		return ignoreCase ? value1.equalsIgnoreCase(value2) : value1.equals(value2);
	}
	
	
	/**
	 * Returns font information containing the font family, font face and font style, searching for names case sensitive.
	 * 
	 * @param name the font family or font face name
	 * @param locale the locale
	 * @return a font info object
	 */
	public FontInfo getFontInfo(String name, Locale locale)
	{
		return getFontInfo(name, false, locale);
	}


	public FontSetInfo getFontSetInfo(String name, Locale locale, boolean ignoreMissingFonts)
	{
		FontSetInfoKey key = new FontSetInfoKey(name, locale, ignoreMissingFonts);
		Object cachedFontSetInfo = fontSetInfoCache.get(key);
		if (cachedFontSetInfo == null)
		{
			FontSetInfo fontSetInfo = searchFontSetInfo(name, locale, ignoreMissingFonts);
			if (log.isDebugEnabled())
			{
				log.debug("looking for font set " + name + ", ignore missing " + ignoreMissingFonts
						+ ", locale " + locale
						+ " found " + fontSetInfo);
			}
			
			cachedFontSetInfo = fontSetInfo == null ? CACHE_NULL_VALUE : fontSetInfo;
			fontSetInfoCache.put(key, cachedFontSetInfo);
		}
		return cachedFontSetInfo == CACHE_NULL_VALUE ? null : (FontSetInfo) cachedFontSetInfo;		
	}

	protected FontSetInfo searchFontSetInfo(String name, Locale locale, boolean ignoreMissingFonts)
	{
		List<FontFamily> allFontFamilies = jasperReportsContext.getExtensions(FontFamily.class);
		HashMap<String, FontFamily> fontFamilies = new HashMap<>(allFontFamilies.size() * 4 / 3, .75f);
		for (FontFamily family : allFontFamilies)
		{
			if (family.getName() != null
					&& (locale == null || family.supportsLocale(locale))
					&& !fontFamilies.containsKey(family.getName()))
			{
				fontFamilies.put(family.getName(), family);
			}
		}
		
		Map<String, FontSetFamilyInfo> setFamilyInfos = new LinkedHashMap<>();
		List<FontSet> allSets = jasperReportsContext.getExtensions(FontSet.class);
		FontSet foundFontSet = null;
		FontSetFamilyInfo primaryFamily = null;
		for (FontSet fontSet : allSets)
		{
			if (name.equals(fontSet.getName()))
			{
				foundFontSet = fontSet;
				
				List<FontSetFamily> setFamilies = fontSet.getFamilies();
				for (FontSetFamily fontSetFamily : setFamilies)
				{
					FontFamily fontFamily = fontFamilies.get(fontSetFamily.getFamilyName());
					if (fontFamily != null)
					{
						FontSetFamilyInfo familyInfo = new FontSetFamilyInfo(fontSetFamily, fontFamily);
						setFamilyInfos.put(fontSetFamily.getFamilyName(), familyInfo);
						
						if (fontSetFamily.isPrimary())
						{
							primaryFamily = familyInfo;
						}
					}
					else 
					{
						if (ignoreMissingFonts)
						{
							if (log.isWarnEnabled())
							{
								log.warn("Font family " + fontSetFamily.getFamilyName()
										+ " was not found for font set " + name);
							}
						}
						else
						{
							throw new JRRuntimeException(EXCEPTION_MESSAGE_KEY_FONT_SET_FAMILY_NOT_FOUND,
									new Object[]{fontSetFamily.getFamilyName(), name});
						}
					}
				}
			}
		}
		
		if (foundFontSet == null)
		{
			return null;
		}
		
		//TODO lucianc handle sets with no families
		List<FontSetFamilyInfo> familyInfoList = new ArrayList<>(setFamilyInfos.values());
		if (primaryFamily == null && !familyInfoList.isEmpty())
		{
			primaryFamily = familyInfoList.get(0);
		}
		return new FontSetInfo(foundFontSet, primaryFamily, familyInfoList);
	}
	
	public String getExportFontFamily(String name, Locale locale, String exporterKey)
	{
		FontInfo fontInfo = getFontInfo(name, locale);
		if (fontInfo != null)
		{
			FontFamily family = fontInfo.getFontFamily();
			String exportFont = family.getExportFont(exporterKey);
			return exportFont == null ? name : exportFont;
		}
		
		FontSetInfo fontSetInfo = getFontSetInfo(name, locale, true);
		if (fontSetInfo != null)
		{
			String exportFont = fontSetInfo.getFontSet().getExportFont(exporterKey);
			//TODO also look at the primary family?
			return exportFont == null ? name : exportFont;
		}
		
		return name;
	}

	/**
	 * Returns the font family names available through extensions, in alphabetical order.
	 */
	public Collection<String> getFontFamilyNames()
	{
		TreeSet<String> familyNames = new TreeSet<>();//FIXMEFONT use collator for order?
		//FIXMEFONT do some cache
		collectFontFamilyNames(familyNames);
		return familyNames;
	}

	protected void collectFontFamilyNames(Collection<String> names)
	{
		List<FontFamily> families = jasperReportsContext.getExtensions(FontFamily.class);
		for (Iterator<FontFamily> itf = families.iterator(); itf.hasNext();)
		{
			FontFamily family = itf.next();
			if (family.isVisible())
			{
				names.add(family.getName());
			}
		}
	}

	/**
	 * Returns the font names available through extensions, in alphabetical order.
	 * 
	 * @return the list of font names provided by extensions
	 */
	public Collection<String> getFontNames()
	{
		TreeSet<String> fontNames = new TreeSet<>();//FIXMEFONT use collator for order?
		//FIXMEFONT do some cache
		collectFontFamilyNames(fontNames);
		collectFontSetNames(fontNames);
		return fontNames;
	}

	protected void collectFontSetNames(Collection<String> names)
	{
		List<FontSet> fontSets = jasperReportsContext.getExtensions(FontSet.class);
		for (Iterator<FontSet> itf = fontSets.iterator(); itf.hasNext();)
		{
			FontSet fontSet = itf.next();
			names.add(fontSet.getName());
		}
	}


	/**
	 * Calls {@link #getAwtFontFromBundles(boolean, String, int, float, Locale, boolean)} with the ignoreCase parameter set to false.
	 */
	public Font getAwtFontFromBundles(String name, int style, float size, Locale locale, boolean ignoreMissingFont)
	{
		return getAwtFontFromBundles(false, name, style, size, locale, ignoreMissingFont);
	}


	/**
	 *
	 */
	public Font getAwtFontFromBundles(boolean ignoreCase, String name, int style, float size, Locale locale, boolean ignoreMissingFont)
	{
		Font awtFont = null;
		FontInfo fontInfo = ignoreCase ? getFontInfo(name, true, locale) : getFontInfo(name, locale);
		
		if (fontInfo != null)
		{
			awtFont = getAwtFont(fontInfo, style, size, ignoreMissingFont);
		}
		
		return awtFont;
	}


	protected Font getAwtFont(FontInfo fontInfo, int style, float size, boolean ignoreMissingFont)
	{
		@SuppressWarnings("unused")
		int faceStyle = Font.PLAIN;
		FontFamily family = fontInfo.getFontFamily();
		FontFace face = fontInfo.getFontFace();
		if (face == null)
		{
			if (((style & Font.BOLD) > 0) && ((style & Font.ITALIC) > 0))
			{
				face = family.getBoldItalicFace();
				faceStyle = Font.BOLD | Font.ITALIC;
			}
			
			if ((face == null || face.getFont() == null) && ((style & Font.BOLD) > 0))
			{
				face = family.getBoldFace();
				faceStyle = Font.BOLD;
			}
			
			if ((face == null || face.getFont() == null) && ((style & Font.ITALIC) > 0))
			{
				face = family.getItalicFace();
				faceStyle = Font.ITALIC;
			}
			
			if (face == null || face.getFont() == null)
			{
				face = family.getNormalFace();
				faceStyle = Font.PLAIN;
			}
				
//			if (face == null)
//			{
//				throw new JRRuntimeException("Font family '" + family.getName() + "' does not have the normal font face.");
//			}
		}
		else
		{
			faceStyle = fontInfo.getStyle();
		}

		Font awtFont;
		if (face == null || face.getFont() == null)
		{
			// None of the family's font faces was found to match, neither by name, nor by style and the font family does not even specify a normal face font.
			// In such case, we take the family name and consider it as JVM available font name.
			checkAwtFont(family.getName(), ignoreMissingFont);
			
			awtFont = new Font(family.getName(), style, (int)size);
			awtFont = awtFont.deriveFont(size);
		}
		else
		{
			awtFont = face.getFont();
			if (awtFont == null)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_NULL_FONT,
						new Object[]{face.getName(), family.getName()});
			}

			//deriving with style and size in one call, because deriving with size and then style loses the float size
			awtFont = awtFont.deriveFont(style, size);// & ~faceStyle);
		}
		return awtFont;
	}


	public Font getAwtFontFromBundles(AwtFontAttribute fontAttribute, int style, float size, Locale locale, boolean ignoreMissingFont)
	{
		FontInfo fontInfo = fontAttribute.getFontInfo();
		if (fontInfo == null)
		{
			fontInfo = getFontInfo(fontAttribute.getFamily(), locale);
		}
		
		Font awtFont = null;
		if (fontInfo != null)
		{
			awtFont = getAwtFont(fontInfo, style, size, ignoreMissingFont);
		}
		return awtFont;
	}

	
	/**
	 *
	 */ //FIXMECONTEXT check how to make this cache effective again
	public void resetThreadMissingFontsCache()
	{
		threadMissingFontsCache.set(new HashSet<>());
	}
	
	
	/**
	 *
	 */
	public void checkAwtFont(String name, boolean ignoreMissingFont)
	{
		if (!JRGraphEnvInitializer.isAwtFontAvailable(name))
		{
			if (ignoreMissingFont)
			{
				Set<String> missingFontNames = threadMissingFontsCache.get();
				if (!missingFontNames.contains(name))
				{
					missingFontNames.add(name);
					if (log.isWarnEnabled())
					{
						log.warn("Font '" + name + "' is not available to the JVM. For more details, see http://jasperreports.sourceforge.net/api/net/sf/jasperreports/engine/util/JRFontNotFoundException.html");
					}
				}
			}
			else
			{
				throw new JRFontNotFoundException(name);
			}
		}
	}

	
	/**
	 * Returns a java.awt.Font instance by converting a JRFont instance.
	 * Mostly used in combination with third-party visualization packages such as JFreeChart (for chart themes).
	 * Unless the font parameter is null, this method always returns a non-null AWT font, regardless whether it was
	 * found in the font extensions or not. This is because we do need a font to draw with and there is no point
	 * in raising a font missing exception here, as it is not JasperReports who does the drawing. 
	 */
	public Font getAwtFont(JRFont font, Locale locale)
	{
		if (font == null)
		{
			return null;
		}
		
		// ignoring missing font as explained in the Javadoc
		Font awtFont = 
			getAwtFontFromBundles(
				font.getFontName(), 
				((font.isBold()?Font.BOLD:Font.PLAIN)|(font.isItalic()?Font.ITALIC:Font.PLAIN)), 
				font.getFontSize(),
				locale,
				true
				);
		
		if (awtFont == null)
		{
			awtFont = new Font(getAttributesWithoutAwtFont(new HashMap<>(), font));
		}
		else
		{
			// add underline and strikethrough attributes since these are set at
			// style/font level
			Map<Attribute, Object> attributes = new HashMap<>();
			if (font.isUnderline())
			{
				attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			}
			if (font.isStrikeThrough())
			{
				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
			}
			
			if (!attributes.isEmpty())
			{
				awtFont = awtFont.deriveFont(attributes);
			}
		}
		
		return awtFont;
	}
	
	public Font resolveDeserializedFont(Font font)
	{
		// We use the font.getName() method here because the name field in the java.awt.Font class is the only font name related information that gets serialized,
		// along with the size and style. The font.getFontName() and font.getFamily() both return runtime calculated values, which are not accurate in case of AWT fonts
		// created at runtime through font extensions (both seem to return 'Dialog').
		// For AWT fonts created from font extensions using the Font.createFont(int, InputStream), the name field is set to the same value as the font.getFontName(),
		// which is the recommended method to get the name of an AWT font.
		String fontName = font.getName();
		// We load an instance of an AWT font, even if the specified font name is not available (ignoreMissingFont=true),
		// because only third-party visualization packages such as JFreeChart (chart themes) store serialized java.awt.Font objects,
		// and they are responsible for the drawing as well.
		// Here we rely on the utility method ability to find a font by face name, not only family name. This is because font.getName() above returns an AWT font name,
		// not a font family name.
		Font newFont = getAwtFontFromBundles(fontName, font.getStyle(), font.getSize2D(), null, true);
		if (newFont != null)
		{
			return newFont.deriveFont(font.getAttributes());
		}

		return font;
	}
	
	private FontUtil()
	{
	}
}
