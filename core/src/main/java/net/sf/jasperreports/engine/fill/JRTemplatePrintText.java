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

import java.io.IOException;

import net.sf.jasperreports.engine.JRAnchor;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyledTextAttributeSelector;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.HyperlinkTargetEnum;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.type.RotationEnum;
import net.sf.jasperreports.engine.type.RunDirectionEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextParser;
import net.sf.jasperreports.engine.virtualization.VirtualizationInput;
import net.sf.jasperreports.engine.virtualization.VirtualizationOutput;


/**
 * Implementation of {@link net.sf.jasperreports.engine.JRPrintText} that uses
 * a {@link net.sf.jasperreports.engine.fill.JRTemplateText} instance to
 * store common attributes. 
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRTemplatePrintText extends JRTemplatePrintElement implements JRPrintText
{
	/**
	 *
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	private static final int SERIALIZATION_FLAG_ANCHOR = 1;
	private static final int SERIALIZATION_FLAG_HYPERLINK = 1 << 1;
	private static final int SERIALIZATION_FLAG_RTL = 1 << 2;
	private static final int SERIALIZATION_FLAG_TRUNCATION = 1 << 3;
	private static final int SERIALIZATION_FLAG_LINE_BREAK_OFFSETS = 1 << 4;
	private static final int SERIALIZATION_FLAG_ZERO_LINE_BREAK_OFFSETS = 1 << 5;
	private static final int SERIALIZATION_FLAG_HAS_VALUE = 1 << 6;
	private static final int SERIALIZATION_FLAG_HYPERLINK_OMITTED = 1 << 7;

	/**
	 *
	 */
	private String text;
	private Integer textTruncateIndex;
	private String textTruncateSuffix;
	private short[] lineBreakOffsets;
	//private transient String truncatedText;
	private Object value;
	private float lineSpacingFactor;
	private float leadingOffset;
	private RunDirectionEnum runDirection;
	private Float averageCharWidth;
	private float textHeight;
	
	// we're no longer setting this at fill time, all format attributes are in the template.
	// keeping the field for old serialized JasperPrints.
	private TextFormat textFormat;
	
	private String anchorName;
	private boolean hyperlinkOmitted;
	private String hyperlinkReference;
	private String hyperlinkAnchor;
	private Integer hyperlinkPage;
	private String hyperlinkTooltip;
	private JRPrintHyperlinkParameters hyperlinkParameters;

	/**
	 * The bookmark level for the anchor associated with this field.
	 * @see JRAnchor#getBookmarkLevel()
	 */
	protected int bookmarkLevel = JRAnchor.NO_BOOKMARK; // primitive int field has zero as default value anyway, but it is explicitly initialized for clarity
	
	public JRTemplatePrintText()
	{
		
	}
	
	/**
	 * Creates a print text element.
	 * 
	 * @param text the template text that the element will use
	 * @param originator
	 */
	public JRTemplatePrintText(JRTemplateText text, PrintElementOriginator originator)
	{
		super(text, originator);
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
		return ((JRTemplateText)this.template).getHorizontalTextAlign();
	}
		
	@Override
	public HorizontalTextAlignEnum getOwnHorizontalTextAlign()
	{
		return ((JRTemplateText)this.template).getOwnHorizontalTextAlign();
	}
		
	@Override
	public void setHorizontalTextAlign(HorizontalTextAlignEnum horizontalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public VerticalTextAlignEnum getVerticalTextAlign()
	{
		return ((JRTemplateText)this.template).getVerticalTextAlign();
	}
		
	@Override
	public VerticalTextAlignEnum getOwnVerticalTextAlign()
	{
		return ((JRTemplateText)this.template).getOwnVerticalTextAlign();
	}
		
	@Override
	public void setVerticalTextAlign(VerticalTextAlignEnum verticalAlignment)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public RotationEnum getRotation()
	{
		return ((JRTemplateText)this.template).getRotation();
	}
		
	@Override
	public RotationEnum getOwnRotation()
	{
		return ((JRTemplateText)this.template).getOwnRotation();
	}
		
	@Override
	public void setRotation(RotationEnum rotation)
	{
		throw new UnsupportedOperationException();
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
		return ((JRTemplateText)template).getMarkup();
	}
		
	@Override
	public String getOwnMarkup()
	{
		return ((JRTemplateText)template).getOwnMarkup();
	}

	@Override
	public void setMarkup(String markup)
	{
		throw new UnsupportedOperationException();
	}
		
	@Override
	public JRLineBox getLineBox()
	{
		return ((JRTemplateText)template).getLineBox();
	}
		
	@Override
	public JRParagraph getParagraph()
	{
		return ((JRTemplateText)template).getParagraph();
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
	
	public void setHyperlinkOmitted(boolean hyperlinkOmitted)
	{
		this.hyperlinkOmitted = hyperlinkOmitted;
	}
		
	@Override
	public HyperlinkTypeEnum getHyperlinkType()
	{
		return hyperlinkOmitted ? HyperlinkTypeEnum.NONE : ((JRTemplateText)this.template).getHyperlinkType();
	}
		
	@Override
	public void setHyperlinkType(HyperlinkTypeEnum hyperlinkType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public HyperlinkTargetEnum getHyperlinkTarget()
	{
		return ((JRTemplateText)this.template).getHyperlinkTarget();
	}
		
	@Override
	public void setHyperlinkTarget(HyperlinkTargetEnum hyperlinkTarget)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String getLinkTarget()
	{
		return ((JRTemplateText)template).getLinkTarget();
	}
		
	@Override
	public void setLinkTarget(String linkTarget)
	{
	}
	/**
	 *
	 */
	public void setLinkTarget(byte hyperlinkTarget)
	{
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
		return ((JRTemplateText)template).getFontName();
	}

	@Override
	public String getOwnFontName()
	{
		return ((JRTemplateText)template).getOwnFontName();
	}

	@Override
	public void setFontName(String fontName)
	{
	}


	@Override
	public boolean isBold()
	{
		return ((JRTemplateText)template).isBold();
	}

	@Override
	public Boolean isOwnBold()
	{
		return ((JRTemplateText)template).isOwnBold();
	}

	/**
	 * Alternative setBold method which allows also to reset
	 * the "own" isBold property.
	 */
	@Override
	public void setBold(Boolean isBold)
	{
	}


	@Override
	public boolean isItalic()
	{
		return ((JRTemplateText)template).isItalic();
	}

	@Override
	public Boolean isOwnItalic()
	{
		return ((JRTemplateText)template).isOwnItalic();
	}

	/**
	 * Alternative setItalic method which allows also to reset
	 * the "own" isItalic property.
	 */
	@Override
	public void setItalic(Boolean isItalic)
	{
	}

	@Override
	public boolean isUnderline()
	{
		return ((JRTemplateText)template).isUnderline();
	}

	@Override
	public Boolean isOwnUnderline()
	{
		return ((JRTemplateText)template).isOwnUnderline();
	}

	/**
	 * Alternative setUnderline method which allows also to reset
	 * the "own" isUnderline property.
	 */
	@Override
	public void setUnderline(Boolean isUnderline)
	{
	}

	@Override
	public boolean isStrikeThrough()
	{
		return ((JRTemplateText)template).isStrikeThrough();
	}

	@Override
	public Boolean isOwnStrikeThrough()
	{
		return ((JRTemplateText)template).isOwnStrikeThrough();
	}

	/**
	 * Alternative setStrikeThrough method which allows also to reset
	 * the "own" isStrikeThrough property.
	 */
	@Override
	public void setStrikeThrough(Boolean isStrikeThrough)
	{
	}

	@Override
	public float getFontSize()
	{
		return ((JRTemplateText)template).getFontSize();
	}

	@Override
	public Float getOwnFontSize()
	{
		return ((JRTemplateText)template).getOwnFontSize();
	}

	/**
	 * Method which allows also to reset the "own" size property.
	 */
	@Override
	public void setFontSize(Float fontSize)
	{
	}

	@Override
	public String getPdfFontName()
	{
		return ((JRTemplateText)template).getPdfFontName();
	}

	@Override
	public String getOwnPdfFontName()
	{
		return ((JRTemplateText)template).getOwnPdfFontName();
	}

	@Override
	public void setPdfFontName(String pdfFontName)
	{
	}


	@Override
	public String getPdfEncoding()
	{
		return ((JRTemplateText)template).getPdfEncoding();
	}

	@Override
	public String getOwnPdfEncoding()
	{
		return ((JRTemplateText)template).getOwnPdfEncoding();
	}

	@Override
	public void setPdfEncoding(String pdfEncoding)
	{
	}


	@Override
	public boolean isPdfEmbedded()
	{
		return ((JRTemplateText)template).isPdfEmbedded();
	}

	@Override
	public Boolean isOwnPdfEmbedded()
	{
		return ((JRTemplateText)template).isOwnPdfEmbedded();
	}

	/**
	 * Alternative setPdfEmbedded method which allows also to reset
	 * the "own" isPdfEmbedded property.
	 */
	@Override
	public void setPdfEmbedded(Boolean isPdfEmbedded)
	{
	}


	@Override
	public String getValueClassName()
	{
		return  textFormat == null ? ((JRTemplateText) template).getValueClassName() : textFormat.getValueClassName();
	}

	@Override
	public String getPattern()
	{
		return textFormat == null ? ((JRTemplateText) template).getPattern() : textFormat.getPattern();
	}

	@Override
	public String getFormatFactoryClass()
	{
		return textFormat == null ? ((JRTemplateText) template).getFormatFactoryClass() : textFormat.getFormatFactoryClass();
	}

	@Override
	public String getLocaleCode()
	{
		return textFormat == null ? ((JRTemplateText) template).getLocaleCode() : textFormat.getLocaleCode();
	}

	@Override
	public String getTimeZoneId()
	{
		return textFormat == null ? ((JRTemplateText) template).getTimeZoneId() : textFormat.getTimeZoneId();
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

	@Override
	public String getLinkType()
	{
		return hyperlinkOmitted ? null : ((JRTemplateText) template).getLinkType();
	}

	@Override
	public void setLinkType(String type)
	{
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
	public <T> void accept(PrintElementVisitor<T> visitor, T arg)
	{
		visitor.visit(this, arg);
	}
	
	@Override
	public void writeVirtualized(VirtualizationOutput out) throws IOException
	{
		super.writeVirtualized(out);
		
		int flags = 0;
		boolean hasAnchor = anchorName != null || bookmarkLevel != JRAnchor.NO_BOOKMARK;
		boolean hasHyperlink = hyperlinkReference != null || hyperlinkAnchor != null
				|| hyperlinkPage != null || hyperlinkTooltip != null || hyperlinkParameters != null;
		boolean hasTrunc = textTruncateIndex != null || textTruncateSuffix != null;
		boolean hasLineBreakOffsets = lineBreakOffsets != null;
		boolean zeroLineBreakOffsets = lineBreakOffsets != null && lineBreakOffsets.length == 0;
		//FIXME add a flag for null value
		boolean hasValue = !(text == null ? value == null : (value instanceof String && text.equals(value)));
		
		if (hasAnchor)
		{
			flags |= SERIALIZATION_FLAG_ANCHOR;
		}
		if (hasHyperlink)
		{
			flags |= SERIALIZATION_FLAG_HYPERLINK;
		}
		if (hasTrunc)
		{
			flags |= SERIALIZATION_FLAG_TRUNCATION;
		}
		if (hasLineBreakOffsets)
		{
			flags |= SERIALIZATION_FLAG_LINE_BREAK_OFFSETS;
		}
		if (zeroLineBreakOffsets)
		{
			flags |= SERIALIZATION_FLAG_ZERO_LINE_BREAK_OFFSETS;
		}
		if (hasValue)
		{
			flags |= SERIALIZATION_FLAG_HAS_VALUE;
		}
		if (runDirection == RunDirectionEnum.RTL)
		{
			flags |= SERIALIZATION_FLAG_RTL;
		}
		if (hyperlinkOmitted)
		{
			flags |= SERIALIZATION_FLAG_HYPERLINK_OMITTED;
		}
		
		out.writeIntCompressed(flags);
		
		out.writeJRObject(text);
		if (hasValue)
		{
			out.writeJRObject(value);
		}
		
		//FIXME these usually repeat, keep in memory?
		out.writeFloat(lineSpacingFactor);
		out.writeFloat(leadingOffset);
		out.writeFloat(textHeight);

		if (hasTrunc)
		{
			out.writeJRObject(textTruncateIndex);
			out.writeJRObject(textTruncateSuffix);
		}
		
		if (hasLineBreakOffsets && !zeroLineBreakOffsets)
		{
			out.writeIntCompressed(lineBreakOffsets.length);
			for (short offset : lineBreakOffsets)
			{
				out.writeIntCompressed(offset);
			}
		}
		
		if (hasAnchor)
		{
			out.writeJRObject(anchorName);
			out.writeIntCompressed(bookmarkLevel);
		}

		if (hasHyperlink)
		{
			out.writeJRObject(hyperlinkReference);
			out.writeJRObject(hyperlinkAnchor);
			out.writeJRObject(hyperlinkPage);
			out.writeJRObject(hyperlinkTooltip);
			out.writeJRObject(hyperlinkParameters);
		}
	}

	@Override
	public void readVirtualized(VirtualizationInput in) throws IOException
	{
		super.readVirtualized(in);
		
		int flags = in.readIntCompressed();
		text = (String) in.readJRObject();
		
		if ((flags & SERIALIZATION_FLAG_HAS_VALUE) != 0)
		{
			value = in.readJRObject();
		}
		else
		{
			value = text;
		}
		
		lineSpacingFactor = in.readFloat();
		leadingOffset = in.readFloat();
		textHeight = in.readFloat();
		
		if ((flags & SERIALIZATION_FLAG_TRUNCATION) != 0)
		{
			textTruncateIndex = (Integer) in.readJRObject();
			textTruncateSuffix = (String) in.readJRObject();
		}
		
		if ((flags & SERIALIZATION_FLAG_LINE_BREAK_OFFSETS) != 0)
		{
			if ((flags & SERIALIZATION_FLAG_ZERO_LINE_BREAK_OFFSETS) != 0)
			{
				lineBreakOffsets = JRPrintText.ZERO_LINE_BREAK_OFFSETS;
			}
			else
			{
				int offsetCount = in.readIntCompressed();
				lineBreakOffsets = new short[offsetCount];
				for (int i = 0; i < offsetCount; i++)
				{
					lineBreakOffsets[i] = (short) in.readIntCompressed();
				}
			}
		}
		
		if ((flags & SERIALIZATION_FLAG_ANCHOR) != 0)
		{
			anchorName = (String) in.readJRObject();
			bookmarkLevel = in.readIntCompressed();
		}
		else
		{
			bookmarkLevel = JRAnchor.NO_BOOKMARK;
		}
		
		if ((flags & SERIALIZATION_FLAG_HYPERLINK_OMITTED) != 0)
		{
			hyperlinkOmitted = true;
		}

		if ((flags & SERIALIZATION_FLAG_HYPERLINK) != 0)
		{
			hyperlinkReference = (String) in.readJRObject();
			hyperlinkAnchor = (String) in.readJRObject();
			hyperlinkPage = (Integer) in.readJRObject();
			hyperlinkTooltip = (String) in.readJRObject();
			hyperlinkParameters = (JRPrintHyperlinkParameters) in.readJRObject();
		}
		
		runDirection = (flags & SERIALIZATION_FLAG_RTL) != 0 ? RunDirectionEnum.RTL : RunDirectionEnum.LTR;
		
		// keep this commented just in case we'll need it again sometime in the future
		//PSEUDO_SERIAL_VERSION_UID = JRConstants.PSEUDO_SERIAL_VERSION_UID;
	}
	
}
