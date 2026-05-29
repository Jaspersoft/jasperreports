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
package net.sf.jasperreports.engine.fill;

import java.awt.Font;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.fonts.AwtFontAttribute;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledText.Run;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.engine.util.TextUtils;
import net.sf.jasperreports.engine.util.text.TextLayoutUtils;
import net.sf.jasperreports.properties.PropertyConstants;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleTextLineWrapper implements TextLineWrapper
{
	
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_4_7_1
			)
	public static final String PROPERTY_MEASURE_EXACT = 
			JRPropertiesUtil.PROPERTY_PREFIX + "measure.simple.text.exact";
	
	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "2000",
			scopes = {PropertyScope.CONTEXT},
			sinceVersion = PropertyConstants.VERSION_4_7_1,
			valueType = Integer.class
			)
	public static final String PROPERTY_ELEMENT_CACHE_SIZE = 
			JRPropertiesUtil.PROPERTY_PREFIX + "measure.simple.text.element.cache.size";

	public static final String MEASURE_EXACT_ALWAYS = "always";
	public static final String MEASURE_EXACT_MULTILINE = "multiline";

	private static final Log log = LogFactory.getLog(SimpleTextLineWrapper.class);

	protected static final int FONT_MIN_COUNT = 10;
	protected static final double FONT_SIZE_MIN_FACTOR = 0.1;
	protected static final double FONT_WIDTH_CHECK_FACTOR = 1.2;
	
	protected static final int NEXT_BREAK_INDEX_THRESHOLD = 3;
	
	protected static final String FILL_CACHE_KEY_ELEMENT_FONT_INFOS = 
			SimpleTextLineWrapper.class.getName() + "#elementFontInfos";
	
	protected static final String FILL_CACHE_KEY_GENERAL_FONT_INFOS = 
			SimpleTextLineWrapper.class.getName() + "#generalFontInfos";
	
	// storing per instance to avoid too many calls (and to allow runtime level changes)
	private final boolean logTrace = log.isTraceEnabled();
	
	private TextMeasureContext context;
	private boolean measureSimpleTexts;
	private boolean measureExact;
	private boolean measureExactMultiline;
	private Map<FontKey, ElementFontInfo> fontInfos;
	
	private String wholeText;
	private FontKey fontKey;
	private ElementFontInfo fontInfo;

	private String paragraphText;
	private boolean paragraphTruncateAtChar;
	private boolean paragraphLeftToRight;
	private boolean paragraphMeasureExact;
	private int paragraphOffset;
	private int paragraphPosition;
	private BreakIterator paragraphBreakIterator;
	private LineBreakMeasurer exactBreakMeasurer;
	private int exactBreakMeasurerStart;

	public SimpleTextLineWrapper()
	{
	}

	public SimpleTextLineWrapper(SimpleTextLineWrapper parent)
	{
		this.context = parent.context;
		this.measureSimpleTexts = parent.measureSimpleTexts;
		this.measureExact = parent.measureExact;
		this.measureExactMultiline = parent.measureExactMultiline;
		this.fontInfos = parent.fontInfos;
		
		this.wholeText = parent.wholeText;
		this.fontKey = parent.fontKey;
		this.fontInfo = parent.fontInfo;
	}
	
	@Override
	public void init(TextMeasureContext context)
	{
		this.context = context;
		
		JRPropertiesUtil properties = JRPropertiesUtil.getInstance(context.getJasperReportsContext());
		measureSimpleTexts = properties.getBooleanProperty(context.getPropertiesHolder(), 
				TextMeasurer.PROPERTY_MEASURE_SIMPLE_TEXTS, true);
		if (measureSimpleTexts)
		{
			String exactProp = properties.getProperty(context.getPropertiesHolder(), PROPERTY_MEASURE_EXACT);
			if (exactProp != null)
			{
				if (MEASURE_EXACT_ALWAYS.equals(exactProp))
				{
					measureExact = true;
				}
				else if (MEASURE_EXACT_MULTILINE.equals(exactProp))
				{
					measureExactMultiline = true;
				}
			}

			fontInfos = new HashMap<>();
		}
	}

	@Override
	public boolean start(JRStyledText styledText)
	{
		if (!measureSimpleTexts)
		{
			return false;
		}
		
		List<Run> runs = styledText.getRuns();
		if (runs.size() != 1)
		{
			// multiple styles
			return false;
		}
		
		wholeText = styledText.getText();
		if (wholeText.indexOf('\t') >= 0)
		{
			// supporting tabs is more difficult because we'd need
			// measureParagraphFragment to include the white space advance.
			return false;
		}
		
		Run run = styledText.getRuns().get(0);
		if (run.attributes.get(TextAttribute.SUPERSCRIPT) != null)
		{
			// not handling this case, see JRStyledText.getAwtAttributedString
			return false;
		}

		AwtFontAttribute fontAttribute = AwtFontAttribute.fromAttributes(run.attributes);
		Number size = (Number) run.attributes.get(TextAttribute.SIZE);
		if (!fontAttribute.hasAttribute() || size == null)
		{
			// this should not happen, but still checking
			return false;
		}
		
		int style = 0;
		Number posture = (Number) run.attributes.get(TextAttribute.POSTURE);
		if (posture != null && !TextAttribute.POSTURE_REGULAR.equals(posture))
		{
			if (TextAttribute.POSTURE_OBLIQUE.equals(posture))
			{
				style |= Font.ITALIC;
			}
			else
			{
				// non standard posture
				return false;
			}
		}
		
		Number weight = (Number) run.attributes.get(TextAttribute.WEIGHT);
		if (weight != null && !TextAttribute.WEIGHT_REGULAR.equals(weight))
		{
			if (TextAttribute.WEIGHT_BOLD.equals(weight))
			{
				style |= Font.BOLD;
			}
			else
			{
				// non standard weight
				return false;
			}
		}
		
		fontKey = new FontKey(fontAttribute, size.floatValue() * context.getFontSizeScale(), style, styledText.getLocale());
		createFontInfo(run.attributes);
		
		return true;
	}

	protected void createFontInfo(Map<Attribute, Object> textAttributes)
	{
		fontInfo = fontInfos.get(fontKey);
		if (fontInfo != null)
		{
			// found in local cache
			return;
		}
		
		Map<Pair<UUID, FontKey>, ElementFontInfo> elementFontInfos = null;
		Pair<UUID, FontKey> elementFontKey = null;
		
		// look in the fill cache
		if (context.getElement() instanceof JRFillElement)
		{
			JRFillElement fillElement = (JRFillElement) context.getElement();
			JRFillContext fillContext = fillElement.getFiller().getFillContext();
			elementFontKey = new Pair<>(fillElement.getUUID(), fontKey);
			
			elementFontInfos = fillContext.getFillCache(FILL_CACHE_KEY_ELEMENT_FONT_INFOS,
					this::createElementFontInfosFillCache);

			fontInfo = elementFontInfos.get(elementFontKey);
		}
		
		if (fontInfo == null)
		{
			// did not find in the general cache, create the font info
			// we first need the general font info
			FontInfo generalFontInfo = getGeneralFontInfo(textAttributes);
			
			if (logTrace)
			{
				log.trace("creating element font info for " + fontKey
						+ (elementFontKey == null ? "" : (" and element " + elementFontKey.first())));
			}
			
			fontInfo = new ElementFontInfo(generalFontInfo);
			fontInfos.put(fontKey, fontInfo);
			
			if (elementFontInfos != null && elementFontKey.first() != null)//UUID should not be null but check to be sure
			{
				elementFontInfos.put(elementFontKey, fontInfo);
			}
		}
	}

	protected HashMap<Pair<UUID, FontKey>, ElementFontInfo> createElementFontInfosFillCache()
	{
		final int cacheSize = JRPropertiesUtil.getInstance(context.getJasperReportsContext()).getIntegerProperty(
				PROPERTY_ELEMENT_CACHE_SIZE, 2000);//hardcoded default
		if (log.isDebugEnabled())
		{
			log.debug("creating element font infos cache of size " + cacheSize);
		}
		
		// creating a LRU map
		return new LinkedHashMap<Pair<UUID,FontKey>, SimpleTextLineWrapper.ElementFontInfo>(64, 0.75f, true)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean removeEldestEntry(Entry<Pair<UUID, FontKey>, ElementFontInfo> eldest)
			{
				return size() > cacheSize;
			}
		};
	}
	
	protected FontInfo getGeneralFontInfo(Map<Attribute, Object> textAttributes)
	{
		Map<FontKey, FontInfo> generalFontInfos = null;
		FontInfo generalFontInfo = null;
		// look in the fill cache
		if (context.getElement() instanceof JRFillElement)
		{
			JRFillElement fillElement = (JRFillElement) context.getElement();
			JRFillContext fillContext = fillElement.getFiller().getFillContext();
			
			generalFontInfos = fillContext.getFillCache(FILL_CACHE_KEY_GENERAL_FONT_INFOS,
					HashMap<FontKey, FontInfo>::new);
			
			generalFontInfo = generalFontInfos.get(fontKey);			
		}
		
		if (generalFontInfo == null)
		{
			Font font = loadFont(textAttributes);
			boolean complexLayout = determineComplexLayout(font);
			// computing the leading a single time, assuming that it doesn't change with text
			//FIXME verify if computing leading for each line is needed
			float leading = determineLeading(font);
			if (logTrace)
			{
				log.trace("font " + font + " has complex layout " + complexLayout
						+ ", leading " + leading);
			}

			generalFontInfo = new FontInfo(font, complexLayout, leading);
			
			if (generalFontInfos != null)
			{
				generalFontInfos.put(fontKey, generalFontInfo);
			}
		}
		
		return generalFontInfo;
	}

	protected Font loadFont(Map<Attribute, Object> textAttributes)
	{
		// check bundled fonts
		FontUtil fontUtil = context.getFontUtil();
		Font font = fontUtil.getAwtFontFromBundles(fontKey.fontAttribute, fontKey.style, fontKey.size, fontKey.locale, false);
		if (font == null)
		{
			// checking AWT font
			fontUtil.checkAwtFont(fontKey.fontAttribute.getFamily(), context.isIgnoreMissingFont());
			// creating AWT font
			// FIXME using the current text attributes might be slightly dangerous as we are sharing font metrics
			font = Font.getFont(textAttributes);
		}
		return font;
	}

	protected boolean determineComplexLayout(Font font)
	{
		// this tries to emulate the tests in Font.getStringBounds()
		//FIXME use font.hasLayoutAttributes() instead of this?
		Map<TextAttribute, ?> fontAttributes = font.getAttributes();
		Object kerning = fontAttributes.get(TextAttribute.KERNING);
		Object ligatures = fontAttributes.get(TextAttribute.LIGATURES);
		return (kerning != null && TextAttribute.KERNING_ON.equals(kerning))
				|| (ligatures != null && TextAttribute.LIGATURES_ON.equals(ligatures))
				|| font.isTransformed();
	}
	
	protected float determineLeading(Font font)
	{
		LineMetrics lineMetrics = font.getLineMetrics(" ", context.getFontRenderContext());
		return lineMetrics.getLeading();
	}

	@Override
	public void startParagraph(int paragraphStart, int paragraphEnd,
			boolean truncateAtChar)
	{
		String text = wholeText.substring(paragraphStart, paragraphEnd);
		startParagraph(text, paragraphStart, truncateAtChar);
	}

	@Override
	public void startEmptyParagraph(int paragraphStart)
	{
		startParagraph(" ", paragraphStart, false);
	}
	
	protected void startParagraph(String text, int start, boolean truncateAtChar)
	{
		paragraphText = text;
		paragraphTruncateAtChar = truncateAtChar;
		
		char[] textChars = text.toCharArray();
		// direction is per paragraph
		paragraphLeftToRight = isLeftToRight(textChars);
		paragraphMeasureExact = isParagraphMeasureExact(textChars);
		
		if (logTrace)
		{
			log.trace("paragraph start at " + start
					+ ", truncate at char " + truncateAtChar
					+ ", LTR " + paragraphLeftToRight
					+ ", exact measure " + paragraphMeasureExact);
		}
		
		paragraphOffset = start;
		paragraphPosition = 0;
		
		paragraphBreakIterator = truncateAtChar ? BreakIterator.getCharacterInstance()
				: BreakIterator.getLineInstance();
		paragraphBreakIterator.setText(paragraphText);

		exactBreakMeasurer = null;
	}

	protected boolean isLeftToRight(char[] chars)
	{
		return TextUtils.isLeftToRight(chars);
	}

	protected boolean isParagraphMeasureExact(char[] chars)
	{
		// when we have complex text layout or truncating at char,
		// perform exact break measurement as estimating/guessing could be slower
		if (measureExact
				|| fontInfo.fontInfo.complexLayout
				|| paragraphTruncateAtChar)
		{
			return true;
		}
		
		return hasComplexLayout(chars);
	}

	protected boolean hasComplexLayout(char[] chars)
	{
		return TextLayoutUtils.textLayoutAssessor().hasComplexLayout(chars);
	}

	@Override
	public int paragraphPosition()
	{
		return paragraphPosition;
	}

	@Override
	public int paragraphEnd()
	{
		return paragraphText.length();
	}
	
	@Override
	public TextLine nextLine(float width, int endLimit, boolean requireWord)
	{
		if (logTrace)
		{
			log.trace("simple line measurement at " + (paragraphOffset + paragraphPosition)
					+ " to " + (paragraphOffset + endLimit)
					+ " in width " + width
					+ " with font " + fontInfo);
		}
		
		// the result
		TextLine textLine;
		if (useExactLineMeasurement())
		{
			textLine = measureExactLine(width, endLimit, requireWord);
		}
		else
		{
			textLine = measureLine(width, requireWord, endLimit);
		}
		return textLine;
	}

	protected boolean useExactLineMeasurement()
	{
		// when missing a character width estimate perform one exact measurement
		return paragraphMeasureExact
				|| !fontInfo.hasCharWidthEstimate();
	}
	
	protected TextLine measureExactLine(float width, int endLimit, boolean requireWord)
	{
		int breakIndex = measureExactLineBreakIndex(width, endLimit, requireWord);
		if (breakIndex <= paragraphPosition)
		{
			// nothing fit
			return null;
		}
		
		Rectangle2D lineBounds = measureParagraphFragment(breakIndex);
		return toTextLine(breakIndex, lineBounds);
	}
	
	protected int measureExactLineBreakIndex(float width, int endLimit, boolean requireWord)
	{		
		if (exactBreakMeasurer == null)
		{
			Map<Attribute, Object> attributes = new HashMap<>();
			// we only need the font as it includes the size and style
			attributes.put(TextAttribute.FONT, fontInfo.fontInfo.font);
			
			String textLine = paragraphText.substring(paragraphPosition, endLimit);
			AttributedString attributedLine = new AttributedString(textLine, attributes);

			// we need a fresh iterator for the line
			BreakIterator breakIterator = paragraphTruncateAtChar ? BreakIterator.getCharacterInstance()
					: BreakIterator.getLineInstance();
			exactBreakMeasurer = new LineBreakMeasurer(attributedLine.getIterator(), 
					breakIterator, context.getFontRenderContext());
			exactBreakMeasurerStart = paragraphPosition;
		}
		else
		{
			exactBreakMeasurer.setPosition(paragraphPosition - exactBreakMeasurerStart);
		}

		int breakIndex = exactBreakMeasurer.nextOffset(width, endLimit - exactBreakMeasurerStart, requireWord) 
				+ exactBreakMeasurerStart;
		if (logTrace)
		{
			log.trace("exact line break index measured at " + (paragraphOffset + breakIndex));
		}
		
		return breakIndex;
	}

	protected TextLine measureLine(float width, boolean requireWord, int endLimit)
	{
		// try to guess how much of the text would fit based on the average char width
		int measureIndex = estimateBreakIndex(width, endLimit);
		
		// if estimating that there's more than a line, check measureExactMultiline
		if (measureIndex < endLimit && measureExactMultiline)
		{
			return measureExactLine(width, endLimit, requireWord);
		}
		
		// measure the text
		Rectangle2D bounds = measureParagraphFragment(measureIndex);
		//FIXME fast exit when the height is exceeded
		
		Rectangle2D measuredBounds = bounds;
		if (bounds.getWidth() <= width)
		{
			// see if there's more that could fit
			boolean done = false;
			do
			{
				int nextBreakIndex = measureIndex < endLimit? paragraphBreakIterator.following(measureIndex) 
						: BreakIterator.DONE;
				if (nextBreakIndex == BreakIterator.DONE || nextBreakIndex > endLimit)
				{
					// the next break is after the limit, we're done
					done = true;
				}
				else
				{
					// measure to the next break
					Rectangle2D nextBounds = measureParagraphFragment(nextBreakIndex);
					if (nextBounds.getWidth() <= width)
					{
						measuredBounds = nextBounds;
						measureIndex = nextBreakIndex;
						// loop
					}
					else
					{
						done = true;
					}
				}
			} while (!done);
		}
		else
		{
			// didn't fit, try shorter texts
			boolean done = false;
			do
			{
				int previousBreakIndex = measureIndex > paragraphPosition ? paragraphBreakIterator.preceding(measureIndex) 
						: BreakIterator.DONE;
				if (previousBreakIndex == BreakIterator.DONE || previousBreakIndex <= paragraphPosition)
				{
					if (requireWord)
					{
						// no full word fits, returning empty
						measureIndex = paragraphPosition;
					}
					else
					{
						// we need to break inside the word.
						// measuring the exact break index as estimating/guessing might be slower.
						measureIndex = measureExactLineBreakIndex(width, endLimit, requireWord);
						measuredBounds = measureParagraphFragment(measureIndex);
					}
					done = true;
				}
				else
				{
					measureIndex = previousBreakIndex;
					Rectangle2D prevBounds = measureParagraphFragment(measureIndex);
					if (prevBounds.getWidth() <= width)
					{
						// fitted, we're done
						measuredBounds = prevBounds;
						done = true;
					}
				}
			} while (!done);
		}
		
		if (measureIndex <= paragraphPosition)
		{
			// nothing fit
			return null;
		}
		return toTextLine(measureIndex, measuredBounds);
	}

	protected int estimateBreakIndex(float width, int endLimit)
	{
		double avgCharWidth = fontInfo.charWidthEstimate();
		if ((endLimit - paragraphPosition) * avgCharWidth <= width * FONT_WIDTH_CHECK_FACTOR)
		{
			// there are chances that the entire text would fit, let's be optimistic
			return endLimit;
		}
		
		// estimate how many characters would fit
		int charCountEstimate =  (int) Math.ceil(width / avgCharWidth);
		int estimateFitPosition = paragraphPosition + charCountEstimate;
		if (estimateFitPosition > endLimit)
		{
			// estimated that everything would fit
			return endLimit;
		}
		
		// find the break after the estimate
		int breakAfterEstimatePosition = paragraphBreakIterator.following(estimateFitPosition);
		if (breakAfterEstimatePosition == BreakIterator.DONE || breakAfterEstimatePosition > endLimit)
		{
			breakAfterEstimatePosition = endLimit;
		}
		
		int estimateIndex = breakAfterEstimatePosition;
		// if the after break is too far way from the estimate, see if the break before is closer
		if (breakAfterEstimatePosition > estimateFitPosition + NEXT_BREAK_INDEX_THRESHOLD)
		{
			int breakBeforeEstimatePosition = paragraphBreakIterator.previous();
			// if the break before is closer than the break after, use the break before
			if (breakBeforeEstimatePosition == BreakIterator.DONE
					&& breakBeforeEstimatePosition > paragraphPosition
					&& estimateFitPosition - breakBeforeEstimatePosition < breakAfterEstimatePosition - estimateFitPosition)
			{
				estimateIndex = breakBeforeEstimatePosition;
			}
		}
		return estimateIndex;
	}

	protected Rectangle2D measureParagraphFragment(int measureIndex)
	{
		int endIndex = measureIndex;
		if (endIndex > paragraphPosition + 1) {
			char lastMeasureChar = paragraphText.charAt(endIndex - 1);
			if (Character.isWhitespace(lastMeasureChar)) {
				// exclude trailing white space from the text to measure.
				// use the previous break as limit, but always keep at least one character to measure.
				int preceding = paragraphBreakIterator.preceding(endIndex);
				if (preceding == BreakIterator.DONE || preceding <= paragraphPosition) {
					preceding = paragraphPosition + 1;
				}

				do {
					--endIndex;
					lastMeasureChar = paragraphText.charAt(endIndex - 1);
				} while (endIndex > preceding 
						&& Character.isWhitespace(lastMeasureChar));
			}
		}

		// note that trailing white space will not be included in the advance
		Rectangle2D bounds = fontInfo.fontInfo.font.getStringBounds(paragraphText, paragraphPosition, endIndex, 
				context.getFontRenderContext());
		
		// adding the measurement to the font info statistics
		fontInfo.recordMeasurement(bounds.getWidth() / (endIndex - paragraphPosition));
		
		if (logTrace)
		{
			log.trace("measured to index " + (endIndex + paragraphOffset) + " at width " + bounds.getWidth());
		}
		
		return bounds;
	}

	protected TextLine toTextLine(int measureIndex,
			Rectangle2D measuredBounds)
	{
		SimpleTextLine textLine = new SimpleTextLine();
		textLine.setAscent((float) -measuredBounds.getY());
		textLine.setDescent((float) (measuredBounds.getMaxY() - fontInfo.fontInfo.leading));
		textLine.setLeading(fontInfo.fontInfo.leading); 
		textLine.setCharacterCount(measureIndex - paragraphPosition);
		textLine.setAdvance((float) measuredBounds.getWidth());
		textLine.setLeftToRight(paragraphLeftToRight);
		
		// update the paragraph position
		paragraphPosition = measureIndex;
		
		return textLine;
	}

	@Override
	public TextLine baseTextLine(int index)
	{
		// this should only be called when the text is tabbed, which is not supported 
		throw new UnsupportedOperationException();
	}

	@Override
	public float maxFontsize(int start, int end)
	{
		return fontKey.size;
	}

	@Override
	public String getLineText(int start, int end)
	{
		int newLineIdx = wholeText.indexOf('\n', start);
		int endIdx = (newLineIdx >= 0 && newLineIdx < end) ? newLineIdx : end;
		return wholeText.substring(start, endIdx);
	}

	@Override
	public char charAt(int index)
	{
		return wholeText.charAt(index);
	}

	@Override
	public TextLineWrapper lastLineWrapper(String lineText, int start, int textLength, 
			boolean truncateAtChar)
	{
		if (logTrace)
		{
			log.trace("last line wrapper at " + start + ", textLength " + textLength);
		}
		
		SimpleTextLineWrapper lastLineWrapper = new SimpleTextLineWrapper(this);
		lastLineWrapper.startParagraph(lineText, start, truncateAtChar);
		return lastLineWrapper;
	}
	
	protected static class FontKey
	{
		AwtFontAttribute fontAttribute;
		float size;
		int style;
		Locale locale;
		
		public FontKey(AwtFontAttribute fontAttribute, float size, int style, Locale locale)
		{
			super();
			this.fontAttribute = fontAttribute;
			this.size = size;
			this.style = style;
			this.locale = locale;
		}
		
		@Override
		public int hashCode()
		{
			int hash = 43;
			hash = hash*29 + fontAttribute.hashCode();
			hash = hash*29 + Float.floatToIntBits(size);
			hash = hash*29 + style;
			hash = hash*29 + (locale == null ? 0 : locale.hashCode());
			return hash;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			FontKey info = (FontKey) obj;
			return fontAttribute.equals(info.fontAttribute) && size == info.size && style == info.style
					&& ((locale == null) ? (info.locale == null) : (info.locale != null && locale.equals(info.locale)));
		}
		
		@Override
		public String toString()
		{
			return "{font: " + fontAttribute
					+ ", size: " + size
					+ ", style: " + style
					+ "}";
		}
	}
	
	protected static class FontInfo
	{
		final Font font;
		final boolean complexLayout;
		final float leading;
		final FontStatistics fontStatistics;
		
		public FontInfo(Font font, boolean complexLayout, float leading)
		{
			this.font = font;
			this.complexLayout = complexLayout;
			this.leading = leading;
			this.fontStatistics = new FontStatistics();
		}
		
		@Override
		public String toString()
		{
			return font.toString();
		}
	}
	
	protected static class FontStatistics
	{
		int measurementsCount;
		double characterWidthSum;
		
		public void recordMeasurement(double avgWidth)
		{
			++measurementsCount;
			characterWidthSum += avgWidth;
		}
	}
	
	protected static class ElementFontInfo
	{
		final FontInfo fontInfo;
		final FontStatistics fontStatistics;
		
		public ElementFontInfo(FontInfo fontInfo)
		{
			this.fontInfo = fontInfo;
			this.fontStatistics = new FontStatistics();
		}
		
		public boolean hasCharWidthEstimate()
		{
			return fontStatistics.measurementsCount > 0
					|| fontInfo.fontStatistics.measurementsCount > 0;
		}

		public double charWidthEstimate()
		{
			double avgCharWidth;
			if (fontStatistics.measurementsCount > 0)
			{
				avgCharWidth = fontStatistics.characterWidthSum / fontStatistics.measurementsCount;
			}
			else if (fontInfo.fontStatistics.measurementsCount > 0)
			{
				avgCharWidth = fontInfo.fontStatistics.characterWidthSum / fontInfo.fontStatistics.measurementsCount;
			}
			else
			{
				throw new IllegalStateException("No measurement available for char width estimate");
			}
			
			return avgCharWidth;
		}
		
		public void recordMeasurement(double avgWidth)
		{
			fontStatistics.recordMeasurement(avgWidth);
			fontInfo.fontStatistics.recordMeasurement(avgWidth);
		}
		
		@Override
		public String toString()
		{
			return fontInfo.font.toString();
		}
	}

}
