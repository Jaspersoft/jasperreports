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
package net.sf.jasperreports.pdf.classic.producer;

import java.awt.font.TextAttribute;
import java.lang.Character.UnicodeBlock;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil.PropertySuffix;
import net.sf.jasperreports.engine.fonts.AwtFontAttribute;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.pdf.AbstractPdfTextRenderer;
import net.sf.jasperreports.pdf.PdfReportConfiguration;
import net.sf.jasperreports.pdf.classic.PdfGlyphRenderer;
import net.sf.jasperreports.pdf.common.PdfProducerContext;
import net.sf.jasperreports.pdf.common.PdfTextRendererContext;
import net.sf.jasperreports.pdf.type.PdfVersionEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GlyphRendering
{
	
	private static final Log log = LogFactory.getLog(GlyphRendering.class);
	
	private ClassicPdfProducer pdfProducer;
	private Set<UnicodeBlock> glyphRendererBlocks;
	private boolean glyphRendererAddActualText;
	private Map<FontKey, Boolean> glyphRendererFonts;

	public GlyphRendering(ClassicPdfProducer pdfProducer)
	{
		this.pdfProducer = pdfProducer;
		this.glyphRendererFonts = new HashMap<>();
		
		PdfProducerContext context = pdfProducer.getContext();
		this.glyphRendererAddActualText = context.getProperties().getBooleanProperty( 
				PdfReportConfiguration.PROPERTY_GLYPH_RENDERER_ADD_ACTUAL_TEXT, false);
		if (glyphRendererAddActualText && !context.isTagged() && PdfGlyphRenderer.supported())
		{
			context.setMinimalVersion(PdfVersionEnum.VERSION_1_5);
		}
	}

	protected void initGlyphRenderer() 
	{
		glyphRendererBlocks = new HashSet<>();
		PdfProducerContext context = pdfProducer.getContext();
		List<PropertySuffix> props = context.getProperties().getAllProperties(
				context.getCurrentJasperPrint(), 
				PdfReportConfiguration.PROPERTY_PREFIX_GLYPH_RENDERER_BLOCKS);
		for (PropertySuffix prop : props)
		{
			String blocks = prop.getValue();
			for (String blockToken : blocks.split(","))
			{
				UnicodeBlock block = resolveUnicodeBlock(blockToken);
				if (block != null)
				{
					if (log.isDebugEnabled())
					{
						log.debug("glyph renderer block " + block);
					}
					glyphRendererBlocks.add(block);
				}
			}
		}
	}
	
	protected UnicodeBlock resolveUnicodeBlock(String name)
	{
		if (name.trim().isEmpty())
		{
			return null;
		}
		
		try 
		{
			return UnicodeBlock.forName(name.trim());
		} 
		catch (IllegalArgumentException e) 
		{
			log.warn("Could not resolve \"" + name + "\" to a Unicode block");
			return null;
		} 
	}
	
	public AbstractPdfTextRenderer getGlyphTextRenderer(PdfTextRendererContext context)
	{
		if (toUseGlyphRenderer(context.getPrintText())
			&& PdfGlyphRenderer.supported()
			&& canUseGlyphRendering(context.getPrintText(), context.getStyledText(), context.getTextLocale(), context.getAwtIgnoreMissingFont()))
		{
			PdfProducerContext producerContext = pdfProducer.getContext();
			PdfGlyphRenderer textRenderer = 
				new PdfGlyphRenderer(
					producerContext.getJasperReportsContext(), 
					context.getAwtIgnoreMissingFont(),
					context.getIndentFirstLine(),
					context.getJustifyLastLine(),
					glyphRendererAddActualText
					);
			return textRenderer;
		}
		
		return null;
	}
	
	/**
	 * @deprecated Replaced by {@link #getGlyphTextRenderer(PdfTextRendererContext)}.
	 */
	public AbstractPdfTextRenderer getGlyphTextRenderer(
			JRPrintText text, JRStyledText styledText, Locale textLocale, 
			boolean awtIgnoreMissingFont, boolean defaultIndentFirstLine, boolean defaultJustifyLastLine)
	{
		if (toUseGlyphRenderer(text)
				&& PdfGlyphRenderer.supported()
				&& canUseGlyphRendering(text, styledText, textLocale, awtIgnoreMissingFont))
		{
			PdfProducerContext context = pdfProducer.getContext();
			PdfGlyphRenderer textRenderer = 
					new PdfGlyphRenderer(
						context.getJasperReportsContext(), 
						awtIgnoreMissingFont,
						defaultIndentFirstLine,
						defaultJustifyLastLine,
						glyphRendererAddActualText
						);
			return textRenderer;
		}
		
		return null;
	}
	
	protected boolean canUseGlyphRendering(JRPrintText text, JRStyledText styledText, Locale locale, 
			boolean awtIgnoreMissingFont)
	{
		AttributedCharacterIterator attributesIterator = styledText.getAttributedString().getIterator();
		int index = 0;
		while (index < styledText.length())
		{
			FontKey fontKey = extractFontKey(attributesIterator.getAttributes(), locale);
			if (!fontKey.fontAttribute.hasAttribute())
			{
				return false;
			}
			
			Boolean canUse = glyphRendererFonts.get(fontKey);
			if (canUse == null)
			{
				canUse = canUseGlyphRendering(fontKey, awtIgnoreMissingFont);
				glyphRendererFonts.put(fontKey, canUse);
			}
			
			if (!canUse)
			{
				return false;
			}
			
			index = attributesIterator.getRunLimit();
			attributesIterator.setIndex(index);
		}
		return true;
	}

	protected FontKey extractFontKey(Map<Attribute, Object> attributes, Locale locale)
	{
		AwtFontAttribute fontAttribute = AwtFontAttribute.fromAttributes(attributes);
		
		Number posture = (Number) attributes.get(TextAttribute.POSTURE);
		boolean italic = TextAttribute.POSTURE_OBLIQUE.equals(posture);//FIXME check for non standard posture

		Number weight = (Number) attributes.get(TextAttribute.WEIGHT);
		boolean bold = TextAttribute.WEIGHT_BOLD.equals(weight);
		
		return new FontKey(fontAttribute, italic, bold, locale);
	}
	
	protected boolean canUseGlyphRendering(FontKey fontKey, boolean awtIgnoreMissingFont) 
	{
		Map<Attribute, Object> fontAttributes = new HashMap<>();
		fontKey.fontAttribute.putAttributes(fontAttributes);
		fontAttributes.put(TextAttribute.SIZE, 10f);

		int style = 0;
		if (fontKey.italic)
		{
			style |= java.awt.Font.ITALIC;
			fontAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		}
		if (fontKey.bold)
		{
			style |= java.awt.Font.BOLD;
			fontAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		}
		
		Font pdfFont = pdfProducer.getFont(fontAttributes, fontKey.locale);
		BaseFont baseFont = pdfFont.getBaseFont();
		if (baseFont.getFontType() != BaseFont.FONT_TYPE_TTUNI
				|| baseFont.isFontSpecific())
		{
			if (log.isDebugEnabled())
			{
				log.debug("pdf font for " + fontKey + " has type " + baseFont.getFontType()
						+ ", symbol " + baseFont.isFontSpecific()
						+ ", cannot use glyph rendering");
			}
			return false;
		}
		
		java.awt.Font awtFont = pdfProducer.getContext().getFontUtil().getAwtFontFromBundles(fontKey.fontAttribute, style,
				10f, fontKey.locale, awtIgnoreMissingFont);
		if (awtFont == null)
		{
			awtFont = new java.awt.Font(fontAttributes);
		}
		String awtFontName = awtFont.getFontName();
		
		if (log.isDebugEnabled())
		{
			log.debug(fontKey + " resolved to awt font " + awtFontName);
		}
		
		// we need the fonts to be identical.
		// it would be safer to only allow fonts from extensions, 
		// but for now we are just checking the font names.
		// we need to compare full names because we can't get the base name from awt.
		String[][] pdfFontNames = baseFont.getFullFontName();
		boolean nameMatch = false;
		for (String[] nameArray : pdfFontNames)
		{
			if (nameArray.length >= 4)
			{
				if (log.isDebugEnabled())
				{
					log.debug(fontKey + " resolved to pdf font " + nameArray[3]);
				}
				
				if (awtFontName.equals(nameArray[3]))
				{
					nameMatch = true;
					break;
				}
			}
		}
		
		return nameMatch;
	}
	
	public boolean toUseGlyphRenderer(JRPrintText text)
	{
		String value = pdfProducer.getContext().getStyledTextUtil().getTruncatedText(text);
		if (value == null)
		{
			return false;
		}
		
		if (glyphRendererBlocks.isEmpty())
		{
			return false;
		}
		
		int charCount = value.length();
		char[] chars = new char[charCount];
		value.getChars(0, charCount, chars, 0);
		for (char c : chars)
		{
			UnicodeBlock block = UnicodeBlock.of(c);
			if (glyphRendererBlocks.contains(block))
			{
				if (log.isTraceEnabled())
				{
					log.trace("found character in block " + block + ", using the glyph renderer");
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	protected static class FontKey
	{
		AwtFontAttribute fontAttribute;
		boolean italic;
		boolean bold;
		Locale locale;
		
		public FontKey(AwtFontAttribute fontAttribute, boolean italic, boolean bold, Locale locale)
		{
			this.fontAttribute = fontAttribute;
			this.italic = italic;
			this.bold = bold;
			this.locale = locale;
		}
		
		@Override
		public int hashCode()
		{
			int hash = 43;
			hash = hash*29 + fontAttribute.hashCode();
			hash = hash*29 + (italic ? 1231 : 1237);
			hash = hash*29 + (bold ? 1231 : 1237);
			hash = hash*29 + (locale == null ? 0 : locale.hashCode());
			return hash;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			FontKey key = (FontKey) obj;
			return fontAttribute.equals(key.fontAttribute) && italic == key.italic && bold == key.bold
					&& ((locale == null) ? (key.locale == null) : (key.locale != null && locale.equals(key.locale)));
		}
		
		@Override
		public String toString()
		{
			return "{font: " + fontAttribute
					+ ", italic: " + italic
					+ ", bold: " + bold
					+ "}";
		}
	}

}
