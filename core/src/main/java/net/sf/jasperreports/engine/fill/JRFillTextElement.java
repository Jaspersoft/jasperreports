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

import java.awt.Color;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.base.JRBaseStyle;
import net.sf.jasperreports.engine.export.ExcelAbstractExporter;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledText.Run;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.engine.util.JRTextMeasurerUtil;
import net.sf.jasperreports.engine.util.StyleUtil;
import net.sf.jasperreports.engine.util.StyledTextListItemInfo;
import net.sf.jasperreports.properties.PropertyConstants;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class JRFillTextElement extends JRFillElement implements JRTextElement
{
	
	public static final String EXCEPTION_MESSAGE_KEY_INVALID_START_INDEX = "fill.text.element.invalid.start.index";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_TRUE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_3_1,
			valueType = Boolean.class
			)
	public static final String PROPERTY_CONSUME_SPACE_ON_OVERFLOW = 
			JRPropertiesUtil.PROPERTY_PREFIX + "consume.space.on.overflow";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = "0.5",
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_11_0,
			valueType = Float.class
			)
	public static final String PROPERTY_SCALE_FONT_STEP_LIMIT = 
			JRPropertiesUtil.PROPERTY_PREFIX + "scale.font.step.limit";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			scopes = {PropertyScope.TEXT_ELEMENT},
			sinceVersion = PropertyConstants.VERSION_6_20_6,
			valueType = Integer.class
			)
	public static final String PROPERTY_CUT_TEXT_MAX_HEIGHT = 
			JRPropertiesUtil.PROPERTY_PREFIX + "cut.text.max.height";

	@Property(
			category = PropertyConstants.CATEGORY_FILL,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_20_5,
			valueType = Boolean.class
			)
	public static final String PROPERTY_LEGACY_TEXT_MEASURING = 
		JRPropertiesUtil.PROPERTY_PREFIX + "legacy.text.measuring";

	/**
	 *
	 */
	private boolean isLeftToRight = true;
	private JRTextMeasurer textMeasurer;
	private float lineSpacingFactor;
	private float leadingOffset;
	private float textWidth;
	private float averageCharWidth;
	private float textHeight;
	//private int elementStretchHeightDelta;
	private int textStart;
	private int textEnd;
	private boolean isCutParagraphOverflow;
	private boolean isCutParagraph;
	private short[] lineBreakOffsets;
	private String textTruncateSuffix;
	private String oldRawText;
	private String rawText;
	private JRStyledText styledText;
	private JRStyledText processedStyledText;
	private Map<JRStyle,Map<Attribute,Object>> styledTextAttributesMap = new HashMap<>();
	
	protected final JRLineBox initLineBox;
	protected final JRParagraph initParagraph;
	protected JRLineBox lineBox;
	protected JRParagraph paragraph;

	private JRStyle currentFillStyle;
	private FillStyleObjects fillStyleObjects;
	private Map<JRStyle, FillStyleObjects> fillStyleObjectsMap;
	
	private Boolean defaultConsumeSpaceOnOverflow;
	private boolean dynamicConsumeSpaceOnOverflow;
	private Boolean defaultKeepFullText;
	private boolean dynamicKeepFullText;
	private Float defaultScaleFontStepLimit;
	private boolean dynamicScaleFontStepLimit;
	private Integer defaultCutTextMaxHeight;
	private boolean dynamicCutTextMaxHeight;
	
	protected final boolean keepAverageCharWidth;

	/**
	 *
	 */
	protected JRFillTextElement(
		JRBaseFiller filler,
		JRTextElement textElement, 
		JRFillObjectFactory factory
		)
	{
		super(filler, textElement, factory);

		initLineBox = textElement.getLineBox().clone(this);
		initParagraph = textElement.getParagraph().clone(this);

		this.dynamicConsumeSpaceOnOverflow = hasDynamicProperty(PROPERTY_CONSUME_SPACE_ON_OVERFLOW);
		this.dynamicKeepFullText = hasDynamicProperty(JRTextElement.PROPERTY_PRINT_KEEP_FULL_TEXT);
		this.dynamicScaleFontStepLimit = hasDynamicProperty(PROPERTY_SCALE_FONT_STEP_LIMIT);
		
		this.keepAverageCharWidth = filler.getPropertiesUtil().getBooleanProperty(parent, ExcelAbstractExporter.PROPERTY_AUTO_FIT_COLUMN, false);
		
		this.fillStyleObjectsMap = new HashMap<>();
	}
	

	protected JRFillTextElement(JRFillTextElement textElement, JRFillCloneFactory factory)
	{
		super(textElement, factory);
		
		initLineBox = textElement.getLineBox().clone(this);
		initParagraph = textElement.getParagraph().clone(this);

		this.defaultConsumeSpaceOnOverflow = textElement.defaultConsumeSpaceOnOverflow;
		this.dynamicConsumeSpaceOnOverflow = textElement.dynamicConsumeSpaceOnOverflow;
		this.defaultKeepFullText = textElement.defaultKeepFullText;
		this.dynamicKeepFullText = textElement.dynamicKeepFullText;
		this.defaultScaleFontStepLimit = textElement.defaultScaleFontStepLimit;
		this.dynamicScaleFontStepLimit = textElement.dynamicScaleFontStepLimit;
		this.defaultCutTextMaxHeight = textElement.defaultCutTextMaxHeight;
		this.dynamicCutTextMaxHeight = textElement.dynamicCutTextMaxHeight;

		this.keepAverageCharWidth = textElement.keepAverageCharWidth;

		this.fillStyleObjectsMap = textElement.fillStyleObjectsMap;
	}


	private void createTextMeasurer()
	{
		textMeasurer = JRTextMeasurerUtil.getInstance(filler.getFillContext().getFontUtil()).createTextMeasurer(this);
	}

	protected void ensureTextMeasurer()
	{
		if (textMeasurer == null)
		{
			createTextMeasurer();
		}
	}


	@Override
	protected void evaluateStyle(
		byte evaluation
		) throws JRException
	{
		super.evaluateStyle(evaluation);

		if (providerStyle == null)
		{
			lineBox = null;
			paragraph = null;
			
			setFillStyleObjects();
		}
		else
		{
			lineBox = initLineBox.clone(this);
			paragraph = initParagraph.clone(this);
			StyleUtil.appendBox(lineBox, providerStyle.getLineBox());
			StyleUtil.appendParagraph(paragraph, providerStyle.getParagraph());
			
			fillStyleObjects = null;
		}
	}

	private void setFillStyleObjects()
	{
		JRStyle evaluatedStyle = getStyle();
		// quick check for fast exit (avoid map lookup) when the style has not changed
		//FIXME keep two previous styles to catch common alternating row styles?
		if (fillStyleObjects != null && currentFillStyle == evaluatedStyle)
		{
			return;
		}
		
		// update current style
		currentFillStyle = evaluatedStyle;
		
		// search cached per style
		fillStyleObjects = fillStyleObjectsMap.get(evaluatedStyle);
		if (fillStyleObjects == null)
		{
			// create fill style objects
			CachingLineBox cachedLineBox = new CachingLineBox(initLineBox);
			CachingParagraph cachedParagraph = new CachingParagraph(initParagraph);
			fillStyleObjects = new FillStyleObjects(cachedLineBox, cachedParagraph);
			
			fillStyleObjectsMap.put(evaluatedStyle, fillStyleObjects);
		}
	}


	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}

	@Override
	public HorizontalTextAlignEnum getHorizontalTextAlign()
	{
		return getStyleResolver().getHorizontalTextAlign(this);
	}
		
	@Override
	public HorizontalTextAlignEnum getOwnHorizontalTextAlign()
	{
		return providerStyle == null || providerStyle.getOwnHorizontalTextAlign() == null ? ((JRTextElement)this.parent).getOwnHorizontalTextAlign() : providerStyle.getOwnHorizontalTextAlign();
	}

	@Override
	public void setHorizontalTextAlign(HorizontalTextAlignEnum horizontalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public VerticalTextAlignEnum getVerticalTextAlign()
	{
		return getStyleResolver().getVerticalTextAlign(this);
	}
		
	@Override
	public VerticalTextAlignEnum getOwnVerticalTextAlign()
	{
		return providerStyle == null || providerStyle.getOwnVerticalTextAlign() == null ? ((JRTextElement)this.parent).getOwnVerticalTextAlign() : providerStyle.getOwnVerticalTextAlign();
	}

	@Override
	public void setVerticalTextAlign(VerticalTextAlignEnum verticalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public RotationEnum getRotation()
	{
		return getStyleResolver().getRotation(this);
	}
		
	@Override
	public RotationEnum getOwnRotation()
	{
		return providerStyle == null || providerStyle.getOwnRotation() == null ? ((JRTextElement)this.parent).getOwnRotation() : providerStyle.getOwnRotation();
	}

	@Override
	public void setRotation(RotationEnum rotation)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getMarkup()
	{
		return getStyleResolver().getMarkup(this);
	}
		
	@Override
	public String getOwnMarkup()
	{
		return providerStyle == null || providerStyle.getOwnMarkup() == null ? ((JRTextElement)parent).getOwnMarkup() : providerStyle.getOwnMarkup();
	}

	@Override
	public void setMarkup(String markup)
	{
		throw new UnsupportedOperationException();
	}

	protected JRLineBox getPrintLineBox()
	{
		return lineBox == null ? initLineBox : lineBox;
	}
	
	@Override
	public JRLineBox getLineBox()
	{
		return lineBox == null 
				? (fillStyleObjects == null ? initLineBox : fillStyleObjects.lineBox) 
				: lineBox;
	}

	protected JRParagraph getPrintParagraph()
	{
		return paragraph == null ? initParagraph : paragraph;
	}

	@Override
	public JRParagraph getParagraph()
	{
		return paragraph == null 
				? (fillStyleObjects == null ? initParagraph : fillStyleObjects.paragraph) 
				: paragraph;
	}

	
	/**
	 *
	 */
	protected Map<Attribute,Object> getStyledTextAttributes()
	{
		JRStyle style = getStyle();
		Map<Attribute,Object> styledTextAttributes = styledTextAttributesMap.get(style);
		if (styledTextAttributes == null)
		{
			styledTextAttributes = new HashMap<>(); 
			//JRFontUtil.getAttributes(styledTextAttributes, this, filler.getLocale());
			FontUtil.getInstance(filler.getJasperReportsContext()).getAttributesWithoutAwtFont(styledTextAttributes, this);
			styledTextAttributes.put(TextAttribute.FOREGROUND, getForecolor());
			if (getMode() == ModeEnum.OPAQUE)
			{
				styledTextAttributes.put(TextAttribute.BACKGROUND, getBackcolor());
			}
			styledTextAttributesMap.put(style, styledTextAttributes);
		}
		
		return styledTextAttributes;
	}

	/**
	 *
	 */
	protected float getLineSpacingFactor()
	{
		return lineSpacingFactor;
	}
		
	/**
	 *
	 */
	protected void setLineSpacingFactor(float lineSpacingFactor)
	{
		this.lineSpacingFactor = lineSpacingFactor;
	}

	/**
	 *
	 */
	protected float getLeadingOffset()
	{
		return leadingOffset;
	}
		
	/**
	 *
	 */
	protected void setLeadingOffset(float leadingOffset)
	{
		this.leadingOffset = leadingOffset;
	}

	/**
	 *
	 */
	public RunDirectionEnum getRunDirection()
	{
		return isLeftToRight ? RunDirectionEnum.LTR : RunDirectionEnum.RTL;
	}
		
	/**
	 *
	 */
	public float getTextWidth()
	{
		return textWidth;
	}
		
	/**
	 *
	 */
	protected void setTextWidth(float textWidth)
	{
		this.textWidth = textWidth;
	}
		
	/**
	 *
	 */
	public float getAverageCharWidth()
	{
		return averageCharWidth;
	}
		
	/**
	 *
	 */
	protected void setAverageCharWidth(float averageCharWidth)
	{
		this.averageCharWidth = averageCharWidth;
	}
	
	/**
	 *
	 */
	protected float getTextHeight()
	{
		return textHeight;
	}
		
	/**
	 *
	 */
	protected void setTextHeight(float textHeight)
	{
		this.textHeight = textHeight;
	}

	/**
	 *
	 */
	protected int getTextStart()
	{
		return textStart;
	}
		
	/**
	 *
	 */
	protected void setTextStart(int textStart)
	{
		this.textStart = textStart;
	}

	/**
	 *
	 */
	protected int getTextEnd()
	{
		return textEnd;
	}
		
	/**
	 *
	 */
	protected void setTextEnd(int textEnd)
	{
		this.textEnd = textEnd;
	}

	/**
	 *
	 */
	protected boolean isCutParagraph()
	{
		return isCutParagraph;
	}
		
	/**
	 *
	 */
	protected void setCutParagraph(boolean isCutParagraph)
	{
		this.isCutParagraph = isCutParagraph;
	}
	
	protected short[] getLineBreakOffsets()
	{
		return lineBreakOffsets;
	}

	protected void setLineBreakOffsets(short[] lineBreakOffsets)
	{
		this.lineBreakOffsets = lineBreakOffsets;
	}

	protected void resetTextChunk()
	{
		textStart = 0;
		textEnd = 0;
		textTruncateSuffix = null;
		lineBreakOffsets = null;
		//elementStretchHeightDelta = 0;
	}
	
	/**
	 *
	 */
	protected String getRawText()
	{
		return rawText;
	}

	/**
	 *
	 */
	protected void setRawText(String rawText)
	{
		this.oldRawText = this.rawText;
		this.rawText = rawText;
		styledText = null;
		processedStyledText = null;
	}


	@Override
	public void reset()
	{
		super.reset();
		
		isLeftToRight = true;
		lineSpacingFactor = 0;
		leadingOffset = 0;
		textHeight = 0;
		//elementStretchHeightDelta = 0;
	}


	@Override
	public void rewind()
	{
		this.rawText = this.oldRawText;

		resetTextChunk();
	}


	/**
	 *
	 */
	protected JRStyledText getStyledText()
	{
		if (styledText == null)
		{
			String text = getRawText();
			if (text != null)
			{
				styledText = 
					filler.getStyledTextParser().getStyledText(
						getStyledTextAttributes(), 
						text, 
						!JRCommonText.MARKUP_NONE.equals(getMarkup()),
						filler.getLocale()
						);
			}
			processedStyledText = null;
		}
		
		return styledText;
	}
	
	protected JRStyledText getProcessedStyledText()
	{
		if (processedStyledText == null)
		{
			JRStyledText text = getStyledText();
			if (text != null)
			{
				processedStyledText = filler.getStyledTextUtil().resolveFonts(text, filler.getLocale());
			}
		}
		return processedStyledText;
	}

	/**
	 *
	 */
	public String getTextString()
	{
		JRStyledText tmpStyledText = getStyledText();

		if (tmpStyledText == null)
		{
			return null;
		}

		return tmpStyledText.getText();
	}
	

	@Override
	protected boolean prepare(
		int availableHeight,
		boolean isOverflow,
		boolean isOverflowAllowed
		) throws JRException
	{
		isCutParagraphOverflow = canOverflow() && isCutParagraph;
		
		return super.prepare(availableHeight, isOverflow, isOverflowAllowed);
	}


	/**
	 *
	 */
	protected void chopTextElement(
		int availableStretchHeight
		)
	{
		ensureTextMeasurer();
		
		JRStyledText tmpStyledText = getStyledText();

		if (tmpStyledText == null)
		{
			return;
		}

		int fullTextLength = tmpStyledText.getText().length();
		if (getTextEnd() == fullTextLength)
		{
			return;
		}

		boolean canOverflow = canOverflow();
		JRStyledText processedText = getProcessedStyledText();
		JRMeasuredText measuredText = textMeasurer.measure(
			processedText,
			getTextEnd(),
			availableStretchHeight,
			!isCutParagraphOverflow, // indentFirstLine
			canOverflow
			);
		
		if (
			scaleFontToFit()
			&& measuredText.getTextOffset() < fullTextLength
			)
		{
			// work with a clone of processed styled text so that we don't
			// damage the cached global style attributes that are going to be reused
			// by subsequent print text elements issued from this fill element
			JRStyledText tmpProcessedText = processedText.cloneText();
			
			JRMeasuredText tmpMeasuredText = measuredText;
			JRMeasuredText lastGoodMeasuredText = measuredText;
			JRMeasuredText lastBadMeasuredText = measuredText;

			float factor = 1f;
			float lastGoodFactor = 1f;
			float lastBadFactor = 1f;
			float delta = 1f;
			int deltaSign = -1;
			int oldDeltaSign = -1;
			float oldFontSizeMaxDiff = 0;
			float scaleFontStepLimit = scaleFontStepLimit();

			boolean keepMeasuring = true;

			while (keepMeasuring)
			{
				delta = 0.5f * delta;
				
				if (tmpMeasuredText.getTextOffset() < fullTextLength)
				{
					lastBadMeasuredText = tmpMeasuredText;
					lastBadFactor = factor;

					deltaSign = -1;
					factor = factor - delta;
				}
				else
				{
					lastGoodMeasuredText = tmpMeasuredText;
					lastGoodFactor = factor;

					deltaSign = 1;
					factor = factor + delta;
				}

				float newFontSizeMaxDiff = alterFontSizes(tmpProcessedText, factor, scaleFontStepLimit);
				keepMeasuring = 
					newFontSizeMaxDiff != 0
					&& (newFontSizeMaxDiff != scaleFontStepLimit || deltaSign * newFontSizeMaxDiff != - oldDeltaSign * oldFontSizeMaxDiff);
				if (keepMeasuring)
				{
					tmpMeasuredText = textMeasurer.measure(
						tmpProcessedText,
						getTextEnd(),
						availableStretchHeight,
						!isCutParagraphOverflow, // indentFirstLine
						canOverflow
						);
				}
				
				oldDeltaSign = deltaSign;
				oldFontSizeMaxDiff = newFontSizeMaxDiff;
			}

			if (lastGoodFactor == 1f)
			{
				lastGoodFactor = lastBadFactor; // fit as much text as we can, if cannot fit all
				measuredText = lastBadMeasuredText;
			}
			else
			{
				measuredText = lastGoodMeasuredText;
			}
			
			if (!JRCommonText.MARKUP_NONE.equals(getMarkup()))
			{
				// scale font sizes in styled text according to calculated factor, but on a clone of the styled text;
				// not doing it on existing styled text instance as it would damage the global style text attributes used
				// by the last run, which are cached and reused for print text elements issued from this fill element
				styledText = styledText.cloneText();
				alterFontSizes(styledText, lastGoodFactor, scaleFontStepLimit);
			}

			if (providerStyle == null)
			{
				providerStyle = new JRBaseStyle();
			}
			providerStyle.setFontSize(scaleFontSize(getFontSize(), lastGoodFactor, scaleFontStepLimit));
		}
		
		isLeftToRight = measuredText.isLeftToRight();
		setTextWidth(measuredText.getTextWidth());
		setAverageCharWidth(measuredText.getAverageCharWidth());
		setTextHeight(measuredText.getTextHeight());
		
		//elementStretchHeightDelta = 0;
		if (getRotation().equals(RotationEnum.NONE))
		{
			@SuppressWarnings("deprecation")
			int intTextHeight =  (int) (filler.isLegacyTextMeasuring() ? getTextHeight() : Math.ceil(getTextHeight()));
			int elementTextHeight = intTextHeight + getLineBox().getTopPadding() + getLineBox().getBottomPadding();
			if (
				measuredText.getTextOffset() >= fullTextLength //text ended 
				|| !canOverflow 
				|| !isConsumeSpaceOnOverflow()
				)
			{
				setPrepareHeight(elementTextHeight);
			}
			else
			{
				// occupy all remaining space so that no other element renders there
				setPrepareHeight(getHeight() + availableStretchHeight);
				
				// store the difference between the consumed stretch height and the text stretch height.
				// this will be used in fill() to set the print element height, 
				// which doesn't take into account the consumed empty space;
				
				// gave up on storing this delta because it was not consistent with other elements stretching to container height
				//int textStretchHeight = elementTextHeight > getHeight() ? elementTextHeight : getHeight();
				//elementStretchHeightDelta = getStretchHeight() - textStretchHeight;
			}
		}
		else
		{
			setPrepareHeight(getHeight());
		}
		
		setTextStart(getTextEnd());
		setTextEnd(measuredText.getTextOffset());
		setCutParagraph(measuredText.isParagraphCut());
		setLineBreakOffsets(measuredText.getLineBreakOffsets());
		setTextTruncateSuffix(measuredText.getTextSuffix());
		setLineSpacingFactor(measuredText.getLineSpacingFactor());
		setLeadingOffset(measuredText.getLeadingOffset());
	}
	
	private static float scaleFontSize(float fontSize, float factor, float scaleFontStepLimit)
	{
		float newFontSize = (int)(100 * (Math.round(factor * fontSize / scaleFontStepLimit) * scaleFontStepLimit)) / 100f; // we round to step limit and round to 2 decimals
		newFontSize = newFontSize < scaleFontStepLimit ? scaleFontStepLimit : newFontSize;
		return newFontSize;
	}
	
	private static float alterFontSizes(JRStyledText styledText, float factor, float scaleFontStepLimit)
	{
		float fontSizeMaxDiff = 0;
		if (styledText != null && styledText.length() != 0)
		{
			fontSizeMaxDiff = alterFontSize(styledText.getGlobalAttributes(), factor, scaleFontStepLimit, fontSizeMaxDiff);
			
			List<Run> runs = styledText.getRuns();
			for (Run run : runs)
			{
				fontSizeMaxDiff = alterFontSize(run.attributes, factor, scaleFontStepLimit, fontSizeMaxDiff);
			}
			styledText.append(""); // just to reset internal caches
		}
		return fontSizeMaxDiff;
	}

	private static float alterFontSize(Map<Attribute, Object> attributes, float factor, float scaleFontStepLimit, float fontSizeMaxDiff)
	{
		Float originalFontSize = (Float)attributes.get(JRTextAttribute.FONT_SIZE);
		if (originalFontSize == null)
		{
			originalFontSize = (Float)attributes.get(TextAttribute.SIZE);
			if (originalFontSize != null)
			{
				//keep the original font size, if present
				attributes.put(JRTextAttribute.FONT_SIZE, originalFontSize);
			}
		}
		if (originalFontSize != null)
		{
			Float newFontSize = scaleFontSize(originalFontSize, factor, scaleFontStepLimit);
			Float oldFontSize = (Float)attributes.get(TextAttribute.SIZE);
			fontSizeMaxDiff = Math.max(Math.abs(newFontSize - oldFontSize), fontSizeMaxDiff);
			attributes.put(TextAttribute.SIZE, newFontSize);
		}
		return fontSizeMaxDiff;
	}

	protected boolean isConsumeSpaceOnOverflow()
	{
		if (defaultConsumeSpaceOnOverflow == null)
		{
			defaultConsumeSpaceOnOverflow = filler.getPropertiesUtil().getBooleanProperty( 
					PROPERTY_CONSUME_SPACE_ON_OVERFLOW, true,
					// manually falling back to report properties as getParentProperties() is null for textElement
					parent, filler.getMainDataset());//TODO
		}
		
		boolean consumeSpaceOnOverflow = defaultConsumeSpaceOnOverflow;
		if (dynamicConsumeSpaceOnOverflow)
		{
			String consumeSpaceOnOverflowProp = getDynamicProperties().getProperty(PROPERTY_CONSUME_SPACE_ON_OVERFLOW);
			if (consumeSpaceOnOverflowProp != null)
			{
				consumeSpaceOnOverflow = JRPropertiesUtil.asBoolean(consumeSpaceOnOverflowProp);
			}
		}
		return consumeSpaceOnOverflow;
	}
	
//	public int getPrintElementHeight()
//	{
//		return getStretchHeight() - elementStretchHeightDelta;
//	}
	
	protected abstract boolean canOverflow();

	protected abstract boolean scaleFontToFit();


	@Override
	public String getFontName()
	{
		return getStyleResolver().getFontName(this);
	}

	@Override
	public String getOwnFontName()
	{
		return providerStyle == null || providerStyle.getOwnFontName() == null ? ((JRFont)parent).getOwnFontName() : providerStyle.getOwnFontName();
	}

	@Override
	public void setFontName(String fontName)
	{
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isBold()
	{
		return getStyleResolver().isBold(this);
	}

	@Override
	public Boolean isOwnBold()
	{
		return providerStyle == null || providerStyle.isOwnBold() == null ? ((JRFont)parent).isOwnBold() : providerStyle.isOwnBold();
	}

	/**
	 * Alternative setBold method which allows also to reset
	 * the "own" isBold property.
	 */
	@Override
	public void setBold(Boolean isBold)
	{
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isItalic()
	{
		return getStyleResolver().isItalic(this);
	}

	@Override
	public Boolean isOwnItalic()
	{
		return providerStyle == null || providerStyle.isOwnItalic() == null ? ((JRFont)parent).isOwnItalic() : providerStyle.isOwnItalic();
	}

	/**
	 * Alternative setItalic method which allows also to reset
	 * the "own" isItalic property.
	 */
	@Override
	public void setItalic(Boolean isItalic)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUnderline()
	{
		return getStyleResolver().isUnderline(this);
	}

	@Override
	public Boolean isOwnUnderline()
	{
		return providerStyle == null || providerStyle.isOwnUnderline() == null ? ((JRFont)parent).isOwnUnderline() : providerStyle.isOwnUnderline();
	}

	/**
	 * Alternative setUnderline method which allows also to reset
	 * the "own" isUnderline property.
	 */
	@Override
	public void setUnderline(Boolean isUnderline)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isStrikeThrough()
	{
		return getStyleResolver().isStrikeThrough(this);
	}

	@Override
	public Boolean isOwnStrikeThrough()
	{
		return providerStyle == null || providerStyle.isOwnStrikeThrough() == null ? ((JRFont)parent).isOwnStrikeThrough() : providerStyle.isOwnStrikeThrough();
	}

	/**
	 * Alternative setStrikeThrough method which allows also to reset
	 * the "own" isStrikeThrough property.
	 */
	@Override
	public void setStrikeThrough(Boolean isStrikeThrough)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public float getFontSize()
	{
		return getStyleResolver().getFontSize(this);
	}

	@Override
	public Float getOwnFontSize()
	{
		return providerStyle == null || providerStyle.getOwnFontSize() == null ? ((JRFont)parent).getOwnFontSize() : providerStyle.getOwnFontSize();
	}

	@Override
	public void setFontSize(Float size)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPdfFontName()
	{
		return getStyleResolver().getPdfFontName(this);
	}

	@Override
	public String getOwnPdfFontName()
	{
		return providerStyle == null || providerStyle.getOwnPdfFontName() == null ? ((JRFont)parent).getOwnPdfFontName() : providerStyle.getOwnPdfFontName();
	}

	@Override
	public void setPdfFontName(String pdfFontName)
	{
		throw new UnsupportedOperationException();
	}


	@Override
	public String getPdfEncoding()
	{
		return getStyleResolver().getPdfEncoding(this);
	}

	@Override
	public String getOwnPdfEncoding()
	{
		return providerStyle == null || providerStyle.getOwnPdfEncoding() == null ? ((JRFont)parent).getOwnPdfEncoding() : providerStyle.getOwnPdfEncoding();
	}

	@Override
	public void setPdfEncoding(String pdfEncoding)
	{
		throw new UnsupportedOperationException();
	}


	@Override
	public boolean isPdfEmbedded()
	{
		return getStyleResolver().isPdfEmbedded(this);
	}

	@Override
	public Boolean isOwnPdfEmbedded()
	{
		return providerStyle == null || providerStyle.isOwnPdfEmbedded() == null ? ((JRFont)parent).isOwnPdfEmbedded() : providerStyle.isOwnPdfEmbedded();
	}

	/**
	 * Alternative setPdfEmbedded method which allows also to reset
	 * the "own" isPdfEmbedded property.
	 */
	@Override
	public void setPdfEmbedded(Boolean isPdfEmbedded)
	{
		throw new UnsupportedOperationException();
	}

	
	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}

	
	@Override
	public void setHeight(int height)
	{
		super.setHeight(height);
		
		createTextMeasurer();
	}


	@Override
	public void setWidth(int width)
	{
		super.setWidth(width);
		
		createTextMeasurer();
	}

	protected String processMarkupText(String text)
	{
		text = JRStringUtil.replaceCRwithLF(text);
		
		if (text != null)
		{
			String markup = getMarkup();
			if (
				!JRCommonText.MARKUP_NONE.equals(markup)
				&& !JRCommonText.MARKUP_STYLED_TEXT.equals(markup)
				)
			{
				text = filler.getFillContext().getMarkupProcessor(markup).convert(text);
			}
		}
		
		return text;
	}

	protected void setPrintText(JRPrintText printText)
	{
		int startIndex = getTextStart();
		int endIndex = getTextEnd();
		JRStyledText fullStyledText = getStyledText();
		String fullText = fullStyledText.getText();
		
		boolean keepAllText = !canOverflow() && keepFullText();
		if (keepAllText)
		{
			//assert getTextStart() == 0
			if (startIndex != 0)
			{
				throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_INVALID_START_INDEX,  
					(Object[])null 
					);
			}
			
			if (JRCommonText.MARKUP_NONE.equals(getMarkup()))
			{
				setPrintText(printText, fullText);
			}
			else
			{
				//rewrite as styled text
				String styledText = filler.getStyledTextParser().write(fullStyledText);
				setPrintText(printText, styledText);
			}
			
			if (endIndex < fullText.length())
			{
				printText.setTextTruncateIndex(endIndex);
			}
		}
		else
		{
			if (JRCommonText.MARKUP_NONE.equals(getMarkup()))
			{
				// relying on substring to return the same String object when whole substring
				setPrintText(printText, fullText.substring(startIndex, endIndex));
			}
			else
			{
				if (startIndex > 0) // if this is an overflow
				{
					// preparing bulleted list cuts is a shared responsibility between the TextMeasurer and the JRFillTextElement here;
					// the TextMeasurer has the ability to count how many items have been rendered from each nested list so far and sets their itemIndex and cutStart,
					// while here in the JRFillTextElement we are able to see where does the actual cut go, either cutting through items or in between them and thus
					// decide if a bullet should be rendered
//					StyledTextListInfo[] cutListStack = null; // was used to adjust cutStart by 1 below

					List<Run> runs = fullStyledText.getRuns();
					for (int i = runs.size() - 1; i >= 0; i--)
					{
						Run run = runs.get(i);
						if (run.startIndex <= startIndex && startIndex < run.endIndex)
						{
//							StyledTextListInfo[] listStack = (StyledTextListInfo[])run.attributes.get(JRTextAttribute.HTML_LIST);
//							if (listStack != null)
//							{
//								cutListStack = listStack;
//							}

							StyledTextListItemInfo listItem = (StyledTextListItemInfo)run.attributes.get(JRTextAttribute.HTML_LIST_ITEM);
							if (listItem != null)
							{
								listItem.setNoBullet(run.startIndex < startIndex);
							}
						}
					}
					
//					if (cutListStack != null && cutListStack.length > 0)
//					{
//						for (int i = cutListStack.length - 1; i > 0; i--)
//						{
//							StyledTextListInfo list = cutListStack[i];
//							if (!list.hasParentLi())
//							{
//								StyledTextListInfo parentList = cutListStack[i - 1];
//								parentList.setCutStart(parentList.getCutStart() + 1);
//							}
//						}
//					}
				}
				String styledText = filler.getStyledTextParser().write(fullStyledText, startIndex, endIndex);
				setPrintText(printText, styledText);
			}
		}
		
		printText.setTextTruncateSuffix(getTextTruncateSuffix());
		printText.setLineBreakOffsets(getLineBreakOffsets());
		
		if (
			isCutParagraphOverflow
			&& getParagraph().getFirstLineIndent() != 0
			)
		{
			printText.getPropertiesMap().setProperty(JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE, Boolean.FALSE.toString());
		}
		
		if (
			fullText != null
			&& endIndex < fullText.length()
			&& HorizontalTextAlignEnum.JUSTIFIED == getHorizontalTextAlign()
			&& isCutParagraph
			)
		{
			printText.getPropertiesMap().setProperty(JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE, Boolean.TRUE.toString());
		}
	}
	
	protected boolean keepFullText()
	{
		if (defaultKeepFullText == null)
		{
			defaultKeepFullText = filler.getPropertiesUtil().getBooleanProperty( 
					JRTextElement.PROPERTY_PRINT_KEEP_FULL_TEXT, false,
					// manually falling back to report properties as getParentProperties() is null for textElement
					parent, filler.getMainDataset());//TODO
		}
		
		boolean keepFullText = defaultKeepFullText;
		if (dynamicKeepFullText)
		{
			String keepFullTextProp = getDynamicProperties().getProperty(
					JRTextElement.PROPERTY_PRINT_KEEP_FULL_TEXT);
			if (keepFullTextProp != null)
			{
				keepFullText = JRPropertiesUtil.asBoolean(keepFullTextProp);
			}
		}
		return keepFullText;
	}
	
	protected float scaleFontStepLimit()
	{
		if (defaultScaleFontStepLimit == null)
		{
			defaultScaleFontStepLimit = filler.getPropertiesUtil().getFloatProperty(
					PROPERTY_SCALE_FONT_STEP_LIMIT, 0.5f,
					// manually falling back to report properties as getParentProperties() is null for textElement
					parent, filler.getMainDataset());//TODO
		}
		
		float scaleFontStepLimit = defaultScaleFontStepLimit;
		if (dynamicScaleFontStepLimit)
		{
			String scaleFontStepLimitProp = getDynamicProperties().getProperty(
					PROPERTY_SCALE_FONT_STEP_LIMIT);
			if (scaleFontStepLimitProp != null)
			{
				scaleFontStepLimit = JRPropertiesUtil.asFloat(scaleFontStepLimitProp);
			}
		}
		return scaleFontStepLimit;
	}
	
	protected int cutTextMaxHeight()
	{
		if (defaultCutTextMaxHeight == null)
		{
			String cutTextMaxHeightProp = getParent().getPropertiesMap().getProperty(PROPERTY_CUT_TEXT_MAX_HEIGHT);
			if (cutTextMaxHeightProp == null)
			{
				defaultCutTextMaxHeight = 0;
			}
			else
			{
				defaultCutTextMaxHeight = JRPropertiesUtil.asInteger(cutTextMaxHeightProp);
			}
		}
		
		int cutTextMaxHeight = defaultCutTextMaxHeight;
		if (dynamicCutTextMaxHeight)
		{
			String cutTextMaxHeightProp = getDynamicProperties().getProperty(PROPERTY_CUT_TEXT_MAX_HEIGHT);
			if (cutTextMaxHeightProp != null)
			{
				cutTextMaxHeight = JRPropertiesUtil.asInteger(cutTextMaxHeightProp);
			}
		}
		return cutTextMaxHeight;
	}
	
	protected void setPrintText(JRPrintText printText, String text)
	{
		printText.setText(text);
	}

	protected String getTextTruncateSuffix()
	{
		return textTruncateSuffix;
	}

	protected void setTextTruncateSuffix(String textTruncateSuffix)
	{
		this.textTruncateSuffix = textTruncateSuffix;
	}

	private static class FillStyleObjects
	{
		private final JRLineBox lineBox;
		private final JRParagraph paragraph;
		
		public FillStyleObjects(JRLineBox lineBox, JRParagraph paragraph)
		{
			this.lineBox = lineBox;
			this.paragraph = paragraph;
		}
	}
}
