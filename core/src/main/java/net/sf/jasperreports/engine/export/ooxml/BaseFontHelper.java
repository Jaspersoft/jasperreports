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
package net.sf.jasperreports.engine.export.ooxml;

import java.awt.font.TextAttribute;
import java.io.IOException;
import java.io.Writer;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.zip.FileBufferedZip;
import net.sf.jasperreports.engine.fonts.FontFace;
import net.sf.jasperreports.engine.fonts.FontInfo;
import net.sf.jasperreports.engine.fonts.FontSetInfo;
import net.sf.jasperreports.engine.util.JRDataUtils;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.repo.RepositoryUtil;

/**
 * @author Sanda Zaharia(shertage@users.sourceforge.net)
 */
public abstract class BaseFontHelper extends BaseHelper
{
	private static final String FONTS_DIR = "fonts/";
	public static final String WORD_FONTS_DIR = "word/" + FONTS_DIR;
	public static final String PPT_FONTS_DIR = "ppt/" + FONTS_DIR;
	
	protected Map<String, String> ooxmlFontsFirstLocales = new HashMap<>();// remember first locale for each font to prevent adding locale to font name if only one locale used
	protected Map<String, OoxmlFont> ooxmlFonts = new HashMap<>();
	protected Map<String, String> fontPaths = new HashMap<>();
	protected final Writer relsWriter;
	protected final FileBufferedZip zip;
	protected final boolean isEmbedFonts;

	/**
	 * 
	 */
	public BaseFontHelper(
		JasperReportsContext jasperReportsContext, 
		Writer writer, 
		Writer relsWriter,
		FileBufferedZip zip,
		boolean isEmbedFonts
		)
	{
		super(jasperReportsContext, writer);
		this.relsWriter = relsWriter;
		this.zip = zip;
		this.isEmbedFonts = isEmbedFonts;
	}

	public void exportFonts() throws IOException
	{
		for (OoxmlFont ooxmlFont : ooxmlFonts.values())
		{
			writer.write(getStartFontTag(ooxmlFont.getId()));
			
			if (ooxmlFont.getRegular() != null)
			{
				writer.write(getRegularEmbedding(ooxmlFont.getRegular()));
			}
			
			if (ooxmlFont.getBold() != null)
			{
				writer.write(getBoldEmbedding(ooxmlFont.getBold()));
			}
			
			if (ooxmlFont.getItalic() != null)
			{
				writer.write(getItalicEmbedding(ooxmlFont.getItalic()));
			}
			
			if (ooxmlFont.getBoldItalic() != null)
			{
				writer.write(getBoldItalicEmbedding(ooxmlFont.getBoldItalic()));
			}

			writer.write(getEndFontTag());
		}
	
		for (Entry<String, String> fontPath : fontPaths.entrySet())
		{
			String rIdf = fontPath.getValue();
			embedFont(rIdf, fontPath.getKey());
		}
	}
	
	
	private String getFontPathId(String fontPath)
	{
		return fontPaths.computeIfAbsent(fontPath, k -> "rIdf" + Integer.valueOf(fontPaths.size() + 1));
	}
			
	private void embedFont(String id, String path) 
	{
		String entryPath = path.replace(' ', '_');
		
		try
		{
			relsWriter.write(" <Relationship Id=\"" + id + "\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/font\" Target=\"" + FONTS_DIR + entryPath + "\"/>\n");
			relsWriter.flush();
			entryPath = getFontsDir() + entryPath;
			byte[] bytes = RepositoryUtil.getInstance(jasperReportsContext).getBytesFromLocation(path);
			zip.addEntry(entryPath, bytes);
		}
		catch (IOException | JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	public String resolveFontFamily(Map<Attribute, Object> attributes, Locale locale)
	{
		String fontFamilyAttr = (String)attributes.get(TextAttribute.FAMILY);
		FontInfo fontInfo = (FontInfo) attributes.get(JRTextAttribute.FONT_INFO);
		
		String defaultFontFamily;
		if (fontInfo == null) // if export font mapping was found at the time of styled text processing, then no FONT_INFO is set, so we need to look for it again
		{
			//no resolved font, using the family
			defaultFontFamily = fontFamilyAttr;
			//check if the family is an extension font family
			fontInfo = fontUtil.getFontInfo(fontFamilyAttr, locale);
		}
		else
		{
			//we have an already resolved font, using it
			defaultFontFamily = fontInfo.getFontFamily().getName();
		}
		
		String exportFont = null;
		if (fontInfo == null)
		{
			//we don't have a resolved font family, check if it's a font set
			FontSetInfo fontSetInfo = fontUtil.getFontSetInfo(fontFamilyAttr, locale, true);
			if (fontSetInfo != null)
			{
				//it's a font set, check the font mapping
				exportFont = fontSetInfo.getFontSet().getExportFont(getExporterKey());
			}
		}
		else
		{
			//it's a font family, check the font mapping
			exportFont = fontInfo.getFontFamily().getExportFont(getExporterKey());
		}

		//by default the font family is used
		String fontFamily = defaultFontFamily;
		if (exportFont != null)
		{
			//we have a font mapping
			fontFamily = exportFont;
		}
		else if (isEmbedFonts && fontInfo != null)
		{
			fontFamily = 
				handleOoxmlFont(
					fontFamily,
					locale,
					fontInfo, 
					TextAttribute.WEIGHT_BOLD.equals(attributes.get(TextAttribute.WEIGHT)),
					TextAttribute.POSTURE_OBLIQUE.equals(attributes.get(TextAttribute.POSTURE))
					);
		}
		return fontFamily;
	}
	
	private String handleOoxmlFont(String name, Locale locale, FontInfo fontInfo, boolean isBold, boolean isItalic)
	{
		FontFace fontFace = fontInfo.getFontFace();

		if (isBold)
		{
			if (isItalic)
			{
				FontFace boldItalicFontFace = null;
				
				if (fontFace == null)
				{
					boldItalicFontFace = fontInfo.getFontFamily().getBoldItalicFace();
					if (boldItalicFontFace == null)
					{
						boldItalicFontFace = fontInfo.getFontFamily().getBoldFace();
					}
					if (boldItalicFontFace == null)
					{
						boldItalicFontFace = fontInfo.getFontFamily().getItalicFace();
					}
					if (boldItalicFontFace == null)
					{
						boldItalicFontFace = fontInfo.getFontFamily().getNormalFace();
					}
				}
				else
				{
					 // font was found by AWT name or AWT family name, not as extension family name
					boldItalicFontFace = fontFace;
				}
				
				String fontPath = getFontPath(boldItalicFontFace);
				if (fontPath != null && !fontPath.trim().isEmpty())
				{
					OoxmlFont ooxmlFont = getOoxmlFont(name, locale);
					String rIdf = getFontPathId(fontPath);
					ooxmlFont.setBoldItalic(rIdf);
					return ooxmlFont.getId();
				}
			}
			else
			{
				FontFace boldFontFace = null;
				
				if (fontFace == null)
				{
					boldFontFace = fontInfo.getFontFamily().getBoldFace();
					if (boldFontFace == null)
					{
						boldFontFace = fontInfo.getFontFamily().getNormalFace();
					}
				}
				else
				{
					 // font was found by AWT name or AWT family name, not as extension family name
					boldFontFace = fontFace;
				}
				
				String fontPath = getFontPath(boldFontFace);
				if (fontPath != null && !fontPath.trim().isEmpty())
				{
					OoxmlFont ooxmlFont = getOoxmlFont(name, locale);
					String rIdf = getFontPathId(fontPath);
					ooxmlFont.setBold(rIdf);
					return ooxmlFont.getId();
				}
			}
		}
		else if (isItalic)
		{
			FontFace italicFontFace = null;
			
			if (fontFace == null)
			{
				italicFontFace = fontInfo.getFontFamily().getItalicFace();
				if (italicFontFace == null)
				{
					italicFontFace = fontInfo.getFontFamily().getNormalFace();
				}
			}
			else
			{
				 // font was found by AWT name or AWT family name, not as extension family name
				italicFontFace = fontFace;
			}
			
			String fontPath = getFontPath(italicFontFace);
			if (fontPath != null && !fontPath.trim().isEmpty())
			{
				OoxmlFont ooxmlFont = getOoxmlFont(name, locale);
				String rIdf = getFontPathId(fontPath);
				ooxmlFont.setItalic(rIdf);
				return ooxmlFont.getId();
			}
		}
		else
		{
			FontFace regularFontFace = null;
			
			if (fontFace == null)
			{
				regularFontFace = fontInfo.getFontFamily().getNormalFace();
			}
			else
			{
				 // font was found by AWT name or AWT family name, not as extension family name
				regularFontFace = fontFace;
			}
			
			String fontPath = getFontPath(regularFontFace);
			if (fontPath != null && !fontPath.trim().isEmpty())
			{
				OoxmlFont ooxmlFont = getOoxmlFont(name, locale);
				String rIdf = getFontPathId(fontPath);
				ooxmlFont.setRegular(rIdf);
				return ooxmlFont.getId();
			}
		}
		
		return name;
	}

	private OoxmlFont getOoxmlFont(String name, Locale locale)
	{
		boolean isFirstLocale = true;
		String localeCode = JRDataUtils.getLocaleCode(locale);
		String firstLocale = ooxmlFontsFirstLocales.get(name);
		if (firstLocale == null)
		{
			ooxmlFontsFirstLocales.put(name, localeCode);
		}
		else
		{
			isFirstLocale = firstLocale.equals(localeCode);
		}
		
		return 
			ooxmlFonts.computeIfAbsent(
				name + (isFirstLocale ? "" : (" " + localeCode)),
				key -> OoxmlFont.getInstance(key)
				);
	}

	protected abstract String getExporterKey();
	
	protected abstract String getStartFontTag(String fontName);
	
	protected abstract String getEndFontTag();
	
	protected abstract String getFontsDir();
	
	protected abstract String getRegularEmbedding(String id);
	
	protected abstract String getBoldEmbedding(String id);
	
	protected abstract String getItalicEmbedding(String id);
	
	protected abstract String getBoldItalicEmbedding(String id);

	protected abstract String getFontPath(FontFace fontFace);
}
