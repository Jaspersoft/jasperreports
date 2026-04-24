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
package net.sf.jasperreports.engine.base;

import java.awt.Color;

import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDefaultStyleProvider;
import net.sf.jasperreports.engine.JRFont;
import net.sf.jasperreports.engine.JRHyperlinkHelper;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyledTextAttributeSelector;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.fill.TextFormat;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextParser;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRBasePrintText extends JRBasePrintElement implements JRPrintText
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 *
	 */
	protected String text;
	protected Integer textTruncateIndex;
	protected short[] lineBreakOffsets;
	protected String textTruncateSuffix;
	//protected transient String truncatedText;
	protected Object value;
	protected float lineSpacingFactor;
	protected float leadingOffset;
	protected HorizontalTextAlignEnum horizontalTextAlign;
	protected VerticalTextAlignEnum verticalTextAlign;
	protected RotationEnum rotation;
	protected RunDirectionEnum runDirection;
	protected float textHeight;
	protected Float averageCharWidth;
	protected String markup;
	protected TextFormat textFormat;
	protected String anchorName;
	protected String linkType;
	protected String linkTarget;
	protected String hyperlinkReference;
	protected String hyperlinkAnchor;
	protected Integer hyperlinkPage;
	protected String hyperlinkTooltip;
	protected JRPrintHyperlinkParameters hyperlinkParameters;

	/**
	 * The bookmark level for the anchor associated with this field.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity

	/**
	 *
	 */
	protected JRLineBox lineBox;
	protected JRParagraph paragraph;

	protected String fontName;
	protected Boolean isBold;
	protected Boolean isItalic;
	protected Boolean isUnderline;
	protected Boolean isStrikeThrough;
	protected Float fontSize;
	protected String pdfFontName;
	protected String pdfEncoding;
	protected Boolean isPdfEmbedded;

	protected String valueClassName;
	protected String pattern;
	protected String formatFactoryClass;
	protected String localeCode;
	protected String timeZoneId;
	
	/**
	 *
	 */
	public JRBasePrintText(JRDefaultStyleProvider defaultStyleProvider)
	{
		super(defaultStyleProvider);
		
		lineBox = new JRBaseLineBox(this);
		paragraph = new JRBaseParagraph(this);
	}


	@Override
	public ModeEnum getMode()
	{
		return getStyleResolver().getMode(this, ModeEnum.TRANSPARENT);
	}
		
	@Override
	public void setText(String text)
	{
		this.text = text;
		//this.truncatedText = null;
	}

	@Override
	public Integer getTextTruncateIndex()
	{
		return textTruncateIndex;
	}

	@Override
	public void setTextTruncateIndex(Integer textTruncateIndex)
	{
		this.textTruncateIndex = textTruncateIndex;
		//this.truncatedText = null;
	}

	@Override
	public String getTextTruncateSuffix()
	{
		return textTruncateSuffix;
	}

	@Override
	public void setTextTruncateSuffix(String textTruncateSuffix)
	{
		this.textTruncateSuffix = textTruncateSuffix;
		//this.truncatedText = null;
	}
	
	@Override
	public short[] getLineBreakOffsets()
	{
		return lineBreakOffsets;
	}

	@Override
	public void setLineBreakOffsets(short[] lineBreakOffsets)
	{
		this.lineBreakOffsets = lineBreakOffsets;
	}

	@Override
	public String getFullText()
	{
		String fullText = this.text;
		if (textTruncateIndex == null && textTruncateSuffix != null)
		{
			fullText = (fullText == null ? "" : fullText) + textTruncateSuffix;
		}
		return fullText;
	}

	@Override
	public String getOriginalText()
	{
		return text;
	}

	@Override
	public JRStyledText getFullStyledText(JRStyledTextAttributeSelector attributeSelector)
	{
		if (getFullText() == null)
		{
			return null;
		}

		return 
			JRStyledTextParser.getInstance().getStyledText(
				attributeSelector.getStyledTextAttributes(this), 
				getFullText(), 
				!JRCommonText.MARKUP_NONE.equals(getMarkup()),
				JRStyledTextAttributeSelector.getTextLocale(this)
				);
	}

	@Override
	public Object getValue()
	{
		return value;
	}

	@Override
	public void setValue(Object value)
	{
		this.value = value;
	}

	@Override
	public float getLineSpacingFactor()
	{
		return lineSpacingFactor;
	}
		
	@Override
	public void setLineSpacingFactor(float lineSpacingFactor)
	{
		this.lineSpacingFactor = lineSpacingFactor;
	}

	@Override
	public float getLeadingOffset()
	{
		return leadingOffset;
	}
		
	@Override
	public void setLeadingOffset(float leadingOffset)
	{
		this.leadingOffset = leadingOffset;
	}

	@Override
	public HorizontalTextAlignEnum getHorizontalTextAlign()
	{
		return getStyleResolver().getHorizontalTextAlign(this);
	}
		
	@Override
	public HorizontalTextAlignEnum getOwnHorizontalTextAlign()
	{
		return horizontalTextAlign;
	}
		
	@Override
	public void setHorizontalTextAlign(HorizontalTextAlignEnum horizontalTextAlign)
	{
		this.horizontalTextAlign = horizontalTextAlign;
	}

	@Override
	public VerticalTextAlignEnum getVerticalTextAlign()
	{
		return getStyleResolver().getVerticalTextAlign(this);
	}
		
	@Override
	public VerticalTextAlignEnum getOwnVerticalTextAlign()
	{
		return verticalTextAlign;
	}
		
	@Override
	public void setVerticalTextAlign(VerticalTextAlignEnum verticalTextAlign)
	{
		this.verticalTextAlign = verticalTextAlign;
	}

	@Override
	public RotationEnum getRotation()
	{
		return getStyleResolver().getRotation(this);
	}
		
	@Override
	public RotationEnum getOwnRotation()
	{
		return rotation;
	}

	@Override
	public void setRotation(RotationEnum rotation)
	{
		this.rotation = rotation;
	}

	@Override
	public RunDirectionEnum getRunDirection()
	{
		return this.runDirection;
	}

	@Override
	public void setRunDirection(RunDirectionEnum runDirection)
	{
		this.runDirection = runDirection;
	}

	@Override
	public Float getAverageCharWidth()
	{
		return averageCharWidth;
	}

	@Override
	public void setAverageCharWidth(Float averageCharWidth)
	{
		this.averageCharWidth = averageCharWidth;
	}

	@Override
	public float getTextHeight()
	{
		return textHeight;
	}
		
	@Override
	public void setTextHeight(float textHeight)
	{
		this.textHeight = textHeight;
	}

	@Override
	public String getMarkup()
	{
		return getStyleResolver().getMarkup(this);
	}
		
	@Override
	public String getOwnMarkup()
	{
		return markup;
	}

	@Override
	public void setMarkup(String markup)
	{
		this.markup = markup;
	}

	@Override
	public JRLineBox getLineBox()
	{
		return lineBox;
	}

	@Override
	public JRParagraph getParagraph()
	{
		return paragraph;
	}

	/**
	 *
	 */
	public void copyBox(JRLineBox lineBox)
	{
		this.lineBox = lineBox.clone(this);
	}

	/**
	 *
	 */
	public void copyParagraph(JRParagraph paragraph)
	{
		this.paragraph = paragraph.clone(this);
	}

	/**
	 *
	 */
	public void setFont(JRFont font)
	{
		fontName = font.getOwnFontName();
		isBold = font.isOwnBold();
		isItalic = font.isOwnItalic();
		isUnderline = font.isOwnUnderline();
		isStrikeThrough = font.isOwnStrikeThrough();
		fontSize = font.getOwnFontSize();
		pdfFontName = font.getOwnPdfFontName();
		pdfEncoding = font.getOwnPdfEncoding();
		isPdfEmbedded = font.isOwnPdfEmbedded();
	}

	@Override
	public void setTextFormat(TextFormat textFormat)
	{
		this.textFormat = textFormat;
	}
		
	@Override
	public String getAnchorName()
	{
		return anchorName;
	}
		
	@Override
	public void setAnchorName(String anchorName)
	{
		this.anchorName = anchorName;
	}
		
	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return JRHyperlinkHelper.getHyperlinkType(getLinkType());
	}
		
	@Override
	public void setHyperlinkType(HyperlinkTypeEnum hyperlinkType)
	{
		setLinkType(JRHyperlinkHelper.getLinkType(hyperlinkType));
	}

	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return JRHyperlinkHelper.getHyperlinkTarget(getLinkTarget());
	}
		
	@Override
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		setLinkTarget(JRHyperlinkHelper.getLinkTarget(hyperlinkTarget));
	}

	@Override
	public String getHyperlinkReference()
	{
		return hyperlinkReference;
	}
		
	@Override
	public void setHyperlinkReference(String hyperlinkReference)
	{
		this.hyperlinkReference = hyperlinkReference;
	}
		
	@Override
	public String getHyperlinkAnchor()
	{
		return hyperlinkAnchor;
	}
		
	@Override
	public void setHyperlinkAnchor(String hyperlinkAnchor)
	{
		this.hyperlinkAnchor = hyperlinkAnchor;
	}
		
	@Override
	public Integer getHyperlinkPage()
	{
		return hyperlinkPage;
	}
		
	@Override
	public void setHyperlinkPage(Integer hyperlinkPage)
	{
		this.hyperlinkPage = hyperlinkPage;
	}
		
	/**
	 *
	 */
	public void setHyperlinkPage(String hyperlinkPage)
	{
		this.hyperlinkPage = Integer.valueOf(hyperlinkPage);
	}


	@Override
	public int getBookmarkLevel()
	{
		return bookmarkLevel;
	}


	@Override
	public void setBookmarkLevel(int bookmarkLevel)
	{
		this.bookmarkLevel = bookmarkLevel;
	}

	@Override
	public String getFontName()
	{
		return getStyleResolver().getFontName(this);
	}

	@Override
	public String getOwnFontName()
	{
		return fontName;
	}

	@Override
	public void setFontName(String fontName)
	{
		this.fontName = fontName;
	}


	@Override
	public boolean isBold()
	{
		return getStyleResolver().isBold(this);
	}

	@Override
	public Boolean isOwnBold()
	{
		return isBold;
	}

	/**
	 * Alternative setBold method which allows also to reset
	 * the "own" isBold property.
	 */
	@Override
	public void setBold(Boolean isBold)
	{
		this.isBold = isBold;
	}


	@Override
	public boolean isItalic()
	{
		return getStyleResolver().isItalic(this);
	}

	@Override
	public Boolean isOwnItalic()
	{
		return isItalic;
	}

	/**
	 * Alternative setItalic method which allows also to reset
	 * the "own" isItalic property.
	 */
	@Override
	public void setItalic(Boolean isItalic)
	{
		this.isItalic = isItalic;
	}

	@Override
	public boolean isUnderline()
	{
		return getStyleResolver().isUnderline(this);
	}

	@Override
	public Boolean isOwnUnderline()
	{
		return isUnderline;
	}

	/**
	 * Alternative setUnderline method which allows also to reset
	 * the "own" isUnderline property.
	 */
	@Override
	public void setUnderline(Boolean isUnderline)
	{
		this.isUnderline = isUnderline;
	}

	@Override
	public boolean isStrikeThrough()
	{
		return getStyleResolver().isStrikeThrough(this);
	}

	@Override
	public Boolean isOwnStrikeThrough()
	{
		return isStrikeThrough;
	}

	/**
	 * Alternative setStrikeThrough method which allows also to reset
	 * the "own" isStrikeThrough property.
	 */
	@Override
	public void setStrikeThrough(Boolean isStrikeThrough)
	{
		this.isStrikeThrough = isStrikeThrough;
	}

	@Override
	public float getFontSize()
	{
		return getStyleResolver().getFontSize(this);
	}

	@Override
	public Float getOwnFontSize()
	{
		return fontSize;
	}

	/**
	 * Method which allows also to reset the "own" size property.
	 */
	@Override
	public void setFontSize(Float fontSize)
	{
		this.fontSize = fontSize;
	}

	@Override
	public String getPdfFontName()
	{
		return getStyleResolver().getPdfFontName(this);
	}

	@Override
	public String getOwnPdfFontName()
	{
		return pdfFontName;
	}

	@Override
	public void setPdfFontName(String pdfFontName)
	{
		this.pdfFontName = pdfFontName;
	}


	@Override
	public String getPdfEncoding()
	{
		return getStyleResolver().getPdfEncoding(this);
	}

	@Override
	public String getOwnPdfEncoding()
	{
		return pdfEncoding;
	}

	@Override
	public void setPdfEncoding(String pdfEncoding)
	{
		this.pdfEncoding = pdfEncoding;
	}


	@Override
	public boolean isPdfEmbedded()
	{
		return getStyleResolver().isPdfEmbedded(this);
	}

	@Override
	public Boolean isOwnPdfEmbedded()
	{
		return isPdfEmbedded;
	}

	/**
	 * Alternative setPdfEmbedded method which allows also to reset
	 * the "own" isPdfEmbedded property.
	 */
	@Override
	public void setPdfEmbedded(Boolean isPdfEmbedded)
	{
		this.isPdfEmbedded = isPdfEmbedded;
	}

	
	@Override
	public String getPattern()
	{
		return pattern;
	}

	
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	
	@Override
	public String getValueClassName()
	{
		return valueClassName;
	}

	
	public void setValueClassName(String valueClassName)
	{
		this.valueClassName = valueClassName;
	}

	
	@Override
	public String getFormatFactoryClass()
	{
		return formatFactoryClass;
	}

	
	public void setFormatFactoryClass(String formatFactoryClass)
	{
		this.formatFactoryClass = formatFactoryClass;
	}

	
	@Override
	public String getLocaleCode()
	{
		return localeCode;
	}

	
	public void setLocaleCode(String localeCode)
	{
		this.localeCode = localeCode;
	}

	
	@Override
	public String getTimeZoneId()
	{
		return timeZoneId;
	}

	
	public void setTimeZoneId(String timeZoneId)
	{
		this.timeZoneId = timeZoneId;
	}

	
	@Override
	public JRPrintHyperlinkParameters getHyperlinkParameters()
	{
		return hyperlinkParameters;
	}

	
	@Override
	public void setHyperlinkParameters(JRPrintHyperlinkParameters hyperlinkParameters)
	{
		this.hyperlinkParameters = hyperlinkParameters;
	}

	
	/**
	 * Adds a custom hyperlink parameter.
	 * 
	 * @param parameter the parameter to add
	 * @see #getHyperlinkParameters()
	 * @see JRPrintHyperlinkParameters#addParameter(JRPrintHyperlinkParameter)
	 */
	public void addHyperlinkParameter(JRPrintHyperlinkParameter parameter)
	{
		if (hyperlinkParameters == null)
		{
			hyperlinkParameters = new JRPrintHyperlinkParameters();
		}
		hyperlinkParameters.addParameter(parameter);
	}
	
	
	@Override
	public String getLinkType()
	{
		return linkType;
	}


	
	@Override
	public void setLinkType(String linkType)
	{
		this.linkType = linkType;
	}
	
	@Override
	public String getLinkTarget()
	{
		return linkTarget;
	}


	
	@Override
	public void setLinkTarget(String linkTarget)
	{
		this.linkTarget = linkTarget;
	}
	
	
	@Override
	public String getHyperlinkTooltip()
	{
		return hyperlinkTooltip;
	}


	
	@Override
	public void setHyperlinkTooltip(String hyperlinkTooltip)
	{
		this.hyperlinkTooltip = hyperlinkTooltip;
	}

	@Override
	public Color getDefaultLineColor() 
	{
		return getForecolor();
	}

	@Override
	public <T> void accept(PrintElementVisitor<T> visitor, T arg)
	{
		visitor.visit(this, arg);
	}
}
