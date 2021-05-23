/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2019 TIBCO Software Inc. All rights reserved.
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
package net.sf.jasperreports.engine.util;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRStyledTextAttributeSelector;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.fonts.FontFace;
import net.sf.jasperreports.engine.fonts.FontFamily;
import net.sf.jasperreports.engine.fonts.FontInfo;
import net.sf.jasperreports.engine.fonts.FontSetFamilyInfo;
import net.sf.jasperreports.engine.fonts.FontSetInfo;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.util.CharPredicateCache.Result;
import net.sf.jasperreports.engine.util.JRStyledText.Run;



/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRStyledTextUtil
{
	//private final JasperReportsContext jasperReportsContext;
	private final JRStyledTextAttributeSelector allSelector;
	private final FontUtil fontUtil;
	private final boolean ignoreMissingFonts;
	
	private final Map<Pair<String, Locale>, FamilyFonts> familyFonts = 
			new ConcurrentHashMap<Pair<String, Locale>, FamilyFonts>();
	
	/**
	 *
	 */
	private JRStyledTextUtil(JasperReportsContext jasperReportsContext)
	{
		//this.jasperReportsContext = jasperReportsContext;
		this.allSelector = JRStyledTextAttributeSelector.getAllSelector(jasperReportsContext);
		fontUtil = FontUtil.getInstance(jasperReportsContext);
		//FIXME read from report/element
		ignoreMissingFonts = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(
				JRStyledText.PROPERTY_AWT_IGNORE_MISSING_FONT);
	}
	
	/**
	 *
	 */
	public static JRStyledTextUtil getInstance(JasperReportsContext jasperReportsContext)
	{
		return new JRStyledTextUtil(jasperReportsContext);
	}
	
	/**
	 *
	 */
	public String getTruncatedText(JRPrintText printText)
	{
		String truncatedText = null;
		String originalText = printText.getOriginalText();
		if (originalText != null)
		{
			if (printText.getTextTruncateIndex() == null)
			{
				truncatedText = originalText;
			}
			else
			{
				if (!JRCommonText.MARKUP_NONE.equals(printText.getMarkup()))
				{
					truncatedText = JRStyledTextParser.getInstance().write(
							printText.getFullStyledText(allSelector), 
							0, printText.getTextTruncateIndex());
				}
				else
				{
					truncatedText = originalText.substring(0, printText.getTextTruncateIndex());
				}
			}
			
			String textTruncateSuffix = printText.getTextTruncateSuffix();
			if (textTruncateSuffix != null)
			{
				truncatedText += textTruncateSuffix;
			}
		}
		return truncatedText;
	}
	
	/**
	 *
	 */
	public JRStyledText getStyledText(JRPrintText printText, JRStyledTextAttributeSelector attributeSelector)
	{
		String truncatedText = getTruncatedText(printText);
		if (truncatedText == null)
		{
			return null;
		}
		
		Locale locale = JRStyledTextAttributeSelector.getTextLocale(printText);
		JRStyledText styledText = getStyledText(printText, truncatedText, attributeSelector, locale);
		return styledText;
	}

	protected JRStyledText getStyledText(JRPrintText printText, String text,
			JRStyledTextAttributeSelector attributeSelector, Locale locale)
	{
		return JRStyledTextParser.getInstance().getStyledText(
			attributeSelector.getStyledTextAttributes(printText), 
			text, 
			!JRCommonText.MARKUP_NONE.equals(printText.getMarkup()),
			locale
			);
	}
	
	public JRStyledText getProcessedStyledText(JRPrintText printText, JRStyledTextAttributeSelector attributeSelector,
			String exporterKey)
	{
		String truncatedText = getTruncatedText(printText);
		if (truncatedText == null)
		{
			return null;
		}
		
		Locale locale = JRStyledTextAttributeSelector.getTextLocale(printText);
		JRStyledText styledText = getStyledText(printText, truncatedText, attributeSelector, locale);
		JRStyledText processedStyledText = resolveFonts(styledText, locale, exporterKey);
		return processedStyledText;
	}
	
	public JRStyledText resolveFonts(JRStyledText styledText, Locale locale)
	{
		return resolveFonts(styledText, locale, null);
	}
	
	protected JRStyledText resolveFonts(JRStyledText styledText, Locale locale, String exporterKey)
	{
		if (styledText == null || styledText.length() == 0)
		{
			return styledText;
		}
		
		//TODO introduce an option to modify the existing object
		//TODO lucianc trace logging
		String text = styledText.getText();
		List<Run> runs = styledText.getRuns();
		List<Run> newRuns = null;
		
		if (runs.size() == 1)
		{
			//treating separately to avoid styledText.getAttributedString() because it's slow
			Map<Attribute, Object> attributes = runs.get(0).attributes;
			FamilyFonts families = getFamilyFonts(attributes, locale);
			if (families.needsToResolveFonts(exporterKey))//TODO lucianc check for single family
			{
				newRuns = new ArrayList<Run>(runs.size() + 2);
				matchFonts(text, 0, styledText.length(), attributes, families, newRuns);
			}
		}
		else
		{
			//quick test to avoid styledText.getAttributedString() when not needed
			boolean needsFontMatching = false;
			for (Run run : runs)
			{
				FamilyFonts families = getFamilyFonts(run.attributes, locale);
				if (families.needsToResolveFonts(exporterKey))
				{
					needsFontMatching = true;
					break;
				}			
			}
			
			if (needsFontMatching)
			{
				newRuns = new ArrayList<Run>(runs.size() + 2);
				AttributedCharacterIterator attributesIt = styledText.getAttributedString().getIterator();
				int index = 0;
				while (index < styledText.length())
				{
					int runEndIndex = attributesIt.getRunLimit();
					Map<Attribute, Object> runAttributes = attributesIt.getAttributes();
					FamilyFonts familyFonts = getFamilyFonts(runAttributes, locale);
					if (familyFonts.needsToResolveFonts(exporterKey))
					{
						matchFonts(text, index, runEndIndex, runAttributes, familyFonts, newRuns);
					}
					else
					{
						//not a font set, copying the run
						copyRun(newRuns, runAttributes, index, runEndIndex);
					}
					
					index = runEndIndex;
					attributesIt.setIndex(index);
				}
			}
		}

		if (newRuns == null)
		{
			//no changes
			return styledText;
		}
		
		JRStyledText processedText = createProcessedStyledText(styledText, text, newRuns);
		return processedText;
	}

	protected JRStyledText createProcessedStyledText(JRStyledText styledText, String text, List<Run> newRuns)
	{
		Map<Attribute,Object> globalAttributes = null;
		JRStyledText processedText = new JRStyledText(styledText.getLocale(), text);
		for (Run newRun : newRuns)
		{
			if (newRun.startIndex == 0 && newRun.endIndex == text.length() && globalAttributes == null)
			{
				globalAttributes = newRun.attributes;
			}
			else
			{
				processedText.addRun(newRun);
			}
		}
		processedText.setGlobalAttributes(globalAttributes == null ? styledText.getGlobalAttributes() 
				: globalAttributes);
		return processedText;
	}

	protected void matchFonts(String text, int startIndex, int endIndex, 
			Map<Attribute, Object> attributes, FamilyFonts familyFonts, 
			List<Run> newRuns)
	{
		Number posture = (Number) attributes.get(TextAttribute.POSTURE);
		boolean italic = posture != null && !TextAttribute.POSTURE_REGULAR.equals(posture);
		
		Number weight = (Number) attributes.get(TextAttribute.WEIGHT);
		boolean bold = weight != null && !TextAttribute.WEIGHT_REGULAR.equals(weight);
		
		boolean hadUnmatched = false;
		int index = startIndex;
		do
		{
			FontMatch fontMatch = null;
			
			if (bold && italic)
			{
				fontMatch = fontMatchRun(text, index, endIndex, familyFonts.boldItalicFonts);
			}
			
			if (bold && (fontMatch == null || fontMatch.fontInfo == null))
			{
				fontMatch = fontMatchRun(text, index, endIndex, familyFonts.boldFonts);
			}
			
			if (italic && (fontMatch == null || fontMatch.fontInfo == null))
			{
				fontMatch = fontMatchRun(text, index, endIndex, familyFonts.italicFonts);
			}
			
			if (fontMatch == null || fontMatch.fontInfo == null)
			{
				fontMatch = fontMatchRun(text, index, endIndex, familyFonts.normalFonts);
			}
			
			if (fontMatch.fontInfo != null)
			{
				//we have a font that matched a part of the text
				addFontRun(newRuns, attributes, index, fontMatch.endIndex, fontMatch.fontInfo);
			}
			else
			{
				//we stopped at the first character
				hadUnmatched = true;
			}
			index = fontMatch.endIndex;
		}
		while(index < endIndex);
		
		if (hadUnmatched)
		{
			//we have unmatched characters, adding a run with the primary font for the entire chunk.
			//we're relying on the JRStyledText to apply the runs in the reverse order.
			addFallbackRun(newRuns, attributes, startIndex, endIndex, familyFonts);
		}
	}
	
	protected void copyRun(List<Run> newRuns, Map<Attribute, Object> attributes,  
			int startIndex, int endIndex)
	{
		Map<Attribute, Object> newAttributes = Collections.unmodifiableMap(attributes);
		Run newRun = new Run(newAttributes, startIndex, endIndex);
		newRuns.add(newRun);
	}
	
	protected void addFallbackRun(List<Run> newRuns, Map<Attribute, Object> attributes,  
			int startIndex, int endIndex, FamilyFonts familyFonts)
	{
		Map<Attribute, Object> newAttributes;
		if (familyFonts.fontSet.getPrimaryFamily() != null)
		{
			//using the primary font as fallback for characters that are not found in any fonts
			//TODO lucianc enhance AdditionalEntryMap to support overwriting an entry
			newAttributes = new HashMap<Attribute, Object>(attributes);
			String primaryFamilyName = familyFonts.fontSet.getPrimaryFamily().getFontFamily().getName();
			newAttributes.put(TextAttribute.FAMILY, primaryFamilyName);
		}
		else
		{
			//not a normal case, leaving the font family as is
			newAttributes = Collections.unmodifiableMap(attributes);
		}
		Run newRun = new Run(newAttributes, startIndex, endIndex);
		newRuns.add(newRun);
	}
	
	protected void addFontRun(List<Run> newRuns, Map<Attribute, Object> attributes,  
			int startIndex, int endIndex, FontInfo fontInfo)
	{
		//directly putting the FontInfo as an attribute
		Map<Attribute, Object> newAttributes = new AdditionalEntryMap<Attribute, Object>(
				attributes, JRTextAttribute.FONT_INFO, fontInfo);
		Run newRun = new Run(newAttributes, startIndex, endIndex);
		newRuns.add(newRun);
	}
	
	protected static class FontMatch
	{
		FontInfo fontInfo;
		int endIndex;
	}
	
	protected FontMatch fontMatchRun(String text, int startIndex, int endIndex, List<Face> fonts)
	{
		LinkedList<Face> validFonts = new LinkedList<Face>(fonts);
		Face lastValid = null;
		int charIndex = startIndex;
		int nextCharIndex = charIndex;
		while (charIndex < endIndex)
		{
			char textChar = text.charAt(charIndex);
			nextCharIndex = charIndex + 1;
			
			int codePoint;
			if (Character.isHighSurrogate(textChar))
			{
				if (charIndex + 1 >= endIndex)
				{
					//isolated high surrogate, not attempting to match fonts
					break;
				}
				
				char nextChar = text.charAt(charIndex + 1);
				if (!Character.isLowSurrogate(nextChar))
				{
					//unpaired high surrogate, not attempting to match fonts
					break;
				}
				codePoint = Character.toCodePoint(textChar, nextChar);
				++nextCharIndex;
			}
			else
			{
				codePoint = textChar;
			}
			
			boolean stopSearching = false;

			for (ListIterator<Face> fontIt = validFonts.listIterator(); fontIt.hasNext();)
			{
				Face face = fontIt.next();
				
				if (!face.supports(codePoint))
				{
					if (lastValid != null) {
						stopSearching = true;
						break;
					}
					fontIt.remove();
				}
			}
			if (stopSearching) {
				break;
			}
			
			if (validFonts.isEmpty())
			{
				break;
			}
			
			lastValid = validFonts.getFirst();
			charIndex = nextCharIndex;
		}
		
		FontMatch fontMatch = new FontMatch();
		fontMatch.endIndex = lastValid == null ? nextCharIndex : charIndex;
		fontMatch.fontInfo = lastValid == null ? null : lastValid.fontInfo;
		return fontMatch;
	}
	
	private FamilyFonts getFamilyFonts(Map<Attribute, Object> attributes, Locale locale)
	{
		String family = (String) attributes.get(TextAttribute.FAMILY);
		return getFamilyFonts(family, locale);
	}
	
	protected FamilyFonts getFamilyFonts(String name, Locale locale)
	{
		Pair<String, Locale> key = new Pair<String, Locale>(name, locale);
		FamilyFonts fonts = familyFonts.get(key);
		if (fonts == null)
		{
			fonts = loadFamilyFonts(name, locale);
			familyFonts.put(key, fonts);
		}
		return fonts;
	}
	
	protected FamilyFonts loadFamilyFonts(String name, Locale locale)
	{
		if (name == null)
		{
			return NULL_FAMILY_FONTS;
		}
		
		FontInfo fontInfo = fontUtil.getFontInfo(name, locale);
		if (fontInfo != null)
		{
			//we found a font, not looking for font sets
			return NULL_FAMILY_FONTS;
		}
		
		FontSetInfo fontSetInfo = fontUtil.getFontSetInfo(name, locale, ignoreMissingFonts);
		if (fontSetInfo == null)
		{
			return NULL_FAMILY_FONTS;
		}
		
		return new FamilyFonts(fontSetInfo);
	}

	private static FamilyFonts NULL_FAMILY_FONTS = new FamilyFonts(null);
	
	private static class FamilyFonts
	{
		FontSetInfo fontSet;
		List<Face> normalFonts;
		List<Face> boldFonts;
		List<Face> italicFonts;
		List<Face> boldItalicFonts;
		
		public FamilyFonts(FontSetInfo fontSet)
		{
			this.fontSet = fontSet;
			
			init();
		}

		private void init()
		{
			if (fontSet == null)
			{
				return;
			}
			
			List<FontSetFamilyInfo> families = fontSet.getFamilies();
			this.normalFonts = new ArrayList<Face>(families.size());
			this.boldFonts = new ArrayList<Face>(families.size());
			this.italicFonts = new ArrayList<Face>(families.size());
			this.boldItalicFonts = new ArrayList<Face>(families.size());
			
			for (FontSetFamilyInfo fontSetFamily : families)
			{
				Family family = new Family(fontSetFamily);
				
				FontFamily fontFamily = fontSetFamily.getFontFamily();
				if (fontFamily.getNormalFace() != null && fontFamily.getNormalFace().getFont() != null)
				{
					normalFonts.add(new Face(family, fontFamily.getNormalFace(), Font.PLAIN));
				}
				if (fontFamily.getBoldFace() != null && fontFamily.getBoldFace().getFont() != null)
				{
					boldFonts.add(new Face(family, fontFamily.getBoldFace(), Font.BOLD));
				}
				if (fontFamily.getItalicFace() != null && fontFamily.getItalicFace().getFont() != null)
				{
					italicFonts.add(new Face(family, fontFamily.getItalicFace(), Font.ITALIC));
				}
				if (fontFamily.getBoldItalicFace() != null && fontFamily.getBoldItalicFace().getFont() != null)
				{
					boldItalicFonts.add(new Face(family, fontFamily.getBoldItalicFace(), Font.BOLD | Font.ITALIC));
				}
			}
		}
		
		public boolean needsToResolveFonts(String exporterKey)
		{
			return fontSet != null && (exporterKey == null 
					|| fontSet.getFontSet().getExportFont(exporterKey) == null);
		}
	}
	
	private static class Family
	{
		final FontSetFamilyInfo fontFamily;
		CharScriptsSet scriptsSet;
		
		public Family(FontSetFamilyInfo fontSetFamily)
		{
			this.fontFamily = fontSetFamily;
			initScripts();
		}

		private void initScripts()
		{
			List<String> includedScripts = fontFamily.getFontSetFamily().getIncludedScripts();
			List<String> excludedScripts = fontFamily.getFontSetFamily().getExcludedScripts();
			if ((includedScripts != null && !includedScripts.isEmpty())
					|| (excludedScripts != null && !excludedScripts.isEmpty()))
			{
				scriptsSet = new CharScriptsSet(includedScripts, excludedScripts);
			}
		}
		
		public boolean includesCharacter(int codePoint)
		{
			return scriptsSet == null || scriptsSet.includesCharacter(codePoint);
		}
	}
	
	private static class Face
	{
		final Family family;
		final FontInfo fontInfo;
		//TODO share caches across fills/exports
		final CharPredicateCache cache;
		
		public Face(Family family, FontFace fontFace, int style)
		{
			this.family = family;
			this.fontInfo = new FontInfo(family.fontFamily.getFontFamily(), fontFace, style);
			this.cache = new CharPredicateCache();
		}
		
		public boolean supports(int code)
		{
			Result cacheResult = cache.getCached(code);
			boolean supports;
			switch (cacheResult)
			{
			case TRUE:
				supports = true;
				break;
			case FALSE:
				supports = false;
				break;
			case NOT_FOUND:
				supports = supported(code);
				cache.set(code, supports);
				break;
			case NOT_CACHEABLE:
			default:
				supports = supported(code);
				break;
			}
			return supports;			
		}
		
		protected boolean supported(int code)
		{
			return family.includesCharacter(code)
					&& fontInfo.getFontFace().getFont().canDisplay(code);			
		}
	}
}
