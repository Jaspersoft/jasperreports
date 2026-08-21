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
package net.sf.jasperreports.engine.export;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jasperreports.engine.JRParagraph;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.TabStop;
import net.sf.jasperreports.engine.fonts.FontUtil;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.util.JRStringUtil;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledTextUtil;
import net.sf.jasperreports.engine.util.JRTextAttribute;
import net.sf.jasperreports.engine.util.ParagraphUtil;
import net.sf.jasperreports.engine.util.StyledTextListWriter;
import net.sf.jasperreports.engine.util.StyledTextWriteContext;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public abstract class AbstractTextRenderer
{
	public static final FontRenderContext LINE_BREAK_FONT_RENDER_CONTEXT = new FontRenderContext(null, true, true);

	protected final JasperReportsContext jasperReportsContext;
	protected final JRPropertiesUtil propUtil;
	protected final FontUtil fontUtil;
	protected JRPrintText text;
	protected JRStyledText styledText;
	protected String allText;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int topPadding;
	protected int leftPadding;
	protected int bottomPadding;
	protected int rightPadding;
	
	//protected float formatWidth;
	protected float verticalAlignOffset;
	protected float drawPosY;
	protected float drawPosX;
	protected float lineHeight;
	protected boolean isMaxHeightReached;
	protected boolean isFirstParagraph;
	protected boolean isLastParagraph;
	protected List<TabSegment> segments;
	protected int segmentIndex;
	protected boolean indentFirstLine;
	protected boolean justifyLastLine;

	protected int htmlListIndent;
	protected String bulletText;
	protected AttributedString bulletChunk;
	
	/**
	 * 
	 *
	private MaxFontSizeFinder maxFontSizeFinder;
	
	/**
	 * 
	 */
	private final boolean isMinimizePrinterJobSize;
	protected final boolean ignoreMissingFont;
	private final boolean defaultIndentFirstLine;
	private final boolean defaultJustifyLastLine;

	
	/**
	 * 
	 */
	public AbstractTextRenderer(
		JasperReportsContext jasperReportsContext,
		boolean isMinimizePrinterJobSize,
		boolean ignoreMissingFont,
		boolean defaultIndentFirstLine,
		boolean defaultJustifyLastLine
		)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.propUtil = JRPropertiesUtil.getInstance(jasperReportsContext);
		this.fontUtil = FontUtil.getInstance(jasperReportsContext);
		this.isMinimizePrinterJobSize = isMinimizePrinterJobSize;
		this.ignoreMissingFont = ignoreMissingFont;
		this.defaultIndentFirstLine = defaultIndentFirstLine;
		this.defaultJustifyLastLine = defaultJustifyLastLine;
	}
	
	
	/**
	 *
	 */
	public int getX()
	{
		return x;
	}
	
	
	/**
	 *
	 */
	public int getY()
	{
		return y;
	}
	
	
	/**
	 *
	 */
	public int getWidth()
	{
		return width;
	}
	
	
	/**
	 *
	 */
	public int getHeight()
	{
		return height;
	}
	
	
	/**
	 *
	 */
	public int getTopPadding()
	{
		return topPadding;
	}
	
	
	/**
	 *
	 */
	public int getLeftPadding()
	{
		return leftPadding;
	}
	
	
	/**
	 *
	 */
	public int getBottomPadding()
	{
		return bottomPadding;
	}
	
	
	/**
	 *
	 */
	public int getRightPadding()
	{
		return rightPadding;
	}
	
	
	/**
	 *
	 */
	public JRStyledText getStyledText()
	{
		return styledText;
	}
	
	
	/**
	 *
	 */
	public String getPlainText()
	{
		return allText;
	}
	
	
	/**
	 * 
	 */
	public void initialize(JRPrintText text, JRStyledText styledText, int offsetX, int offsetY)
	{
		this.styledText = styledText;
		allText = styledText.getText();
		
		x = text.getX() + offsetX;
		y = text.getY() + offsetY;
		width = text.getWidth();
		height = text.getHeight();
		topPadding = text.getLineBox().getTopPadding();
		leftPadding = text.getLineBox().getLeftPadding();
		bottomPadding = text.getLineBox().getBottomPadding();
		rightPadding = text.getLineBox().getRightPadding();
		
		switch (text.getRotation())
		{
			case LEFT :
			{
				y = text.getY() + offsetY + text.getHeight();
				width = text.getHeight();
				height = text.getWidth();
				int tmpPadding = topPadding;
				topPadding = leftPadding;
				leftPadding = bottomPadding;
				bottomPadding = rightPadding;
				rightPadding = tmpPadding;
				break;
			}
			case RIGHT :
			{
				x = text.getX() + offsetX + text.getWidth();
				width = text.getHeight();
				height = text.getWidth();
				int tmpPadding = topPadding;
				topPadding = rightPadding;
				rightPadding = bottomPadding;
				bottomPadding = leftPadding;
				leftPadding = tmpPadding;
				break;
			}
			case UPSIDE_DOWN :
			{
				int tmpPadding = topPadding;
				x = text.getX() + offsetX + text.getWidth();
				y = text.getY() + offsetY + text.getHeight();
				topPadding = bottomPadding;
				bottomPadding = tmpPadding;
				tmpPadding = leftPadding;
				leftPadding = rightPadding;
				rightPadding = tmpPadding;
				break;
			}
			case NONE :
			default :
			{
			}
		}
		
		this.text = text;

		verticalAlignOffset = 0f;
		switch (text.getVerticalTextAlign())
		{
			case BOTTOM :
			{
				verticalAlignOffset = height - topPadding - bottomPadding - text.getTextHeight();
				break;
			}
			case MIDDLE :
			{
				verticalAlignOffset = (height - topPadding - bottomPadding - text.getTextHeight()) / 2f;
				break;
			}
			case TOP :
			case JUSTIFIED :
			default :
			{
				verticalAlignOffset = 0f;
			}
		}
		
		indentFirstLine = defaultIndentFirstLine;
		if (text.getPropertiesMap().containsProperty(JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE))
		{
			indentFirstLine = propUtil.getBooleanProperty(text, JRPrintText.PROPERTY_AWT_INDENT_FIRST_LINE, defaultIndentFirstLine);
		}

		justifyLastLine = defaultJustifyLastLine;
		if (text.getPropertiesMap().containsProperty(JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE))
		{
			justifyLastLine = propUtil.getBooleanProperty(text, JRPrintText.PROPERTY_AWT_JUSTIFY_LAST_LINE, defaultJustifyLastLine);
		}

//		formatWidth = width - leftPadding - rightPadding;
//		formatWidth = formatWidth < 0 ? 0 : formatWidth;

		drawPosY = 0;
		drawPosX = 0;
	
		isMaxHeightReached = false;
		
		//maxFontSizeFinder = MaxFontSizeFinder.getInstance(!JRCommonText.MARKUP_NONE.equals(text.getMarkup()));
	}
	

	/**
	 * 
	 */
	public void render()
	{
		StyledTextWriteContext context = new StyledTextWriteContext();
		
		StyledTextListWriter listWriter = getListWriter();
		
		AttributedCharacterIterator allParagraphs = getAttributedString().getIterator(); 

		isFirstParagraph = true;
		isLastParagraph = false;
		
		int runLimit = 0; //first value does not matter; will be assigned a proper value in the while statement below
		int runStart = 0; 

		while (!isMaxHeightReached && runStart < allParagraphs.getEndIndex() && (runLimit = allParagraphs.getRunLimit(JRTextAttribute.HTML_LIST_ATTRIBUTES)) <= allParagraphs.getEndIndex())
		{
			Map<Attribute,Object> attributes = allParagraphs.getAttributes();

			context.next(attributes);
			
			context.writeLists(listWriter);

			prepareBullet(context, attributes);

			int paragraphStart = 0;
			boolean lastTokenWasNewline = false;

			String runText = allText.substring(runStart, runLimit);
			StringTokenizer tkzer = new StringTokenizer(runText, "\n", true);

			// text is split into paragraphs, using the newline character as delimiter
			while (tkzer.hasMoreTokens() && !isMaxHeightReached) 
			{
				String paragraphText = tkzer.nextToken();

				isLastParagraph = !tkzer.hasMoreTokens();

				if ("\n".equals(paragraphText))
				{
					if (lastTokenWasNewline) // the previous newline becomes itself a paragraph when followed by another newline
					{
						renderParagraph(
							allParagraphs, 
							runStart + paragraphStart - 1, 
							null // null paragraphText is the way to render newlines
							);
					}

					if (
						paragraphStart == 0 // this newline is the first character in the first paragraph  
						|| paragraphStart == allParagraphs.getEndIndex() - 1 // this newline is the last character in the last paragraph
						)
					{
						renderParagraph(
							allParagraphs, 
							runStart + paragraphStart, 
							null // null paragraphText is the way to render newlines
							);
					}

					lastTokenWasNewline = true;
				}
				else
				{
					renderParagraph(
						allParagraphs, 
						runStart + paragraphStart, 
						paragraphText
						);

					lastTokenWasNewline = false;
				}

				paragraphStart += paragraphText.length();
				isFirstParagraph = false;
			}
			
			runStart = runLimit;
			allParagraphs.setIndex(runStart);
		}
		
		context.next(null);
		
		context.writeLists(listWriter);
	}


	protected StyledTextListWriter getListWriter()
	{
		return null;
	}


	/**
	 * 
	 */
	protected void renderParagraph(
		AttributedCharacterIterator allParagraphs,
		int paragraphStart,
		String paragraphText
		)
	{
		AttributedCharacterIterator paragraph = null;
		
		if (paragraphText == null)
		{
			paragraphText = " ";
			paragraph = 
				new AttributedString(
					paragraphText,
					new AttributedString(
						allParagraphs, 
						paragraphStart, 
						paragraphStart + paragraphText.length()
						).getIterator().getAttributes()
					).getIterator();
		}
		else
		{
			paragraph = 
				new AttributedString(
					allParagraphs, 
					paragraphStart, 
					paragraphStart + paragraphText.length()
					).getIterator();
		}

		List<Integer> tabIndexes = JRStringUtil.getTabIndexes(paragraphText);
		
		int currentTab = 0;
		int lines = 0;
		float endX = 0;
		
		TabStop nextTabStop = null;
		boolean requireNextWord = false;
	
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, getFontRenderContext());//grx.getFontRenderContext()

		// the paragraph is rendered one line at a time
		while (lineMeasurer.getPosition() < paragraph.getEndIndex() && !isMaxHeightReached)
		{
			boolean lineComplete = false;

			float maxAscent = 0;
			float maxDescent = 0;
			float maxLeading = 0;
			
			// each line is split into segments, using the tab character as delimiter
			segments = new ArrayList<>(1);

			TabSegment oldSegment = null;
			TabSegment crtSegment = null;

			// splitting the current line into tab segments
			while (!lineComplete)
			{
				// the current segment limit is either the next tab character or the paragraph end 
				int tabIndexOrEndIndex = (tabIndexes == null || currentTab >= tabIndexes.size() ? paragraph.getEndIndex() : tabIndexes.get(currentTab) + 1); // this +1 here means
				// that each segment would contain its terminal tab character, except the last segment which ends where the paragraph ends;
				// the tab character at the end of the segment, although it is not actually rendered, it still causes the layout.getAdvance() to equal layout.getVisibleAdvance()
				// meaning that any white spaces before the tab are not considered trailing spaces, so they contribute to segment width and thus impact segment text alignment
				
				int firstLineIndent = lineMeasurer.getPosition() == 0 ? text.getParagraph().getFirstLineIndent() : 0;
				
				if (
					firstLineIndent != 0
					&& (isFirstParagraph && !indentFirstLine)
					)
				{
					firstLineIndent = 0;
				}
				
				float startX = htmlListIndent + firstLineIndent + leftPadding;
				endX = width - text.getParagraph().getRightIndent() - rightPadding;
				endX = endX < startX ? startX : endX;
				//formatWidth = endX - startX;
				//formatWidth = endX;
				
				int startIndex = lineMeasurer.getPosition();

				float rightX = 0;

				if (segments.size() == 0)
				{
					rightX = startX;
					//nextTabStop = nextTabStop;
				}
				else
				{
					rightX = oldSegment.rightX;
					nextTabStop = ParagraphUtil.getNextTabStop(text.getParagraph(), endX, rightX);
				}

				//float availableWidth = formatWidth - ParagraphUtil.getSegmentOffset(nextTabStop, rightX); // nextTabStop can be null here; and that's OK
				float availableWidth = endX - text.getParagraph().getLeftIndent() - ParagraphUtil.getSegmentOffset(nextTabStop, rightX); // nextTabStop can be null here; and that's OK
				
				// creating a text layout object for each tab segment 
				TextLayout layout = 
					lineMeasurer.nextLayout(
						availableWidth,
						tabIndexOrEndIndex,
						requireNextWord
						);
				
				if (layout != null)
				{
		 			AttributedString tmpText = 
						new AttributedString(
							paragraph, 
							startIndex, 
							startIndex + layout.getCharacterCount()
							);
		 			
					if (isMinimizePrinterJobSize)
					{
						//eugene fix - start
						layout = new TextLayout(tmpText.getIterator(), getFontRenderContext());
						//eugene fix - end
					}
		
					if (
						text.getHorizontalTextAlign() == HorizontalTextAlignEnum.JUSTIFIED
						&& (lineMeasurer.getPosition() < paragraph.getEndIndex() || (isLastParagraph && justifyLastLine))
						)
					{
						layout = layout.getJustifiedLayout(availableWidth);
					}
					
					maxAscent = Math.max(maxAscent, layout.getAscent());
					maxDescent = Math.max(maxDescent, layout.getDescent());
					maxLeading = Math.max(maxLeading, layout.getLeading());

					//creating the current segment
					crtSegment = new TabSegment();
					crtSegment.layout = layout;
					crtSegment.as = tmpText;
					crtSegment.text = paragraphText.substring(startIndex, startIndex + layout.getCharacterCount());
					crtSegment.isLastLine = lineMeasurer.getPosition() == paragraph.getEndIndex();

					// using layout.getVisibleAdvance() here means trailing white space characters at the end of the line do not contribute to line width,
					// which is important when aligning the line of text, to match how text alignment works in PDF, DOCX and other formats;
					// unlike entire lines of text which might end up with white space characters and are thus considered trailing spaces, 
					// segments separated by tab character contain the tab character as last character and any white space character preceding the tab are not 
					// considered trailing spaces; they contribute to the segment width and impact segment alignment because layout.getAvance() equals layout.getVisibleAdvance()
					// in their case
					
					float advance = layout.getVisibleAdvance();
					//float advance = layout.getAdvance();
					float leftX = ParagraphUtil.getLeftX(nextTabStop, advance); // nextTabStop can be null here; and that's OK
					if (rightX > leftX)
					{
						crtSegment.leftX = rightX;
						crtSegment.rightX = rightX + advance;
					}
					else
					{
						crtSegment.leftX = leftX;
						// we need this special tab stop based utility call because adding the advance to leftX causes rounding issues
						crtSegment.rightX = ParagraphUtil.getRightX(nextTabStop, advance); // nextTabStop can be null here; and that's OK
					}

					segments.add(crtSegment);
				}
				
				requireNextWord = true;

				if (lineMeasurer.getPosition() == tabIndexOrEndIndex)
				{
					// the segment limit was a tab; going to the next tab
					currentTab++;
				}

				if (lineMeasurer.getPosition() == paragraph.getEndIndex())
				{
					// the segment limit was the paragraph end; line completed and next line should start at normal zero x offset
					lineComplete = true;
					nextTabStop = null;
				}
				else
				{
					// there is paragraph text remaining 
					if (lineMeasurer.getPosition() == tabIndexOrEndIndex)
					{
						// the segment limit was a tab
						if (crtSegment.rightX >= ParagraphUtil.getLastTabStop(text.getParagraph(), endX).getPosition())
						{
							// current segment stretches out beyond the last tab stop; line complete
							lineComplete = true;
							// next line should should start at first tab stop indent
							nextTabStop = ParagraphUtil.getFirstTabStop(text.getParagraph(), endX);
						}
//						else
//						{
//							//nothing; this leaves lineComplete=false
//						}
					}
					else
					{
						// the segment did not fit entirely
						lineComplete = true;
						if (layout == null)
						{
							// nothing fitted; next line should start at first tab stop indent
							if (nextTabStop.getPosition() == ParagraphUtil.getFirstTabStop(text.getParagraph(), endX).getPosition())//FIXMETAB check based on segments.size()
							{
								// at second attempt we give up to avoid infinite loop
								nextTabStop = null;
								requireNextWord = false;
								
								//provide dummy maxFontSize because it is used for the line height of this empty line when attempting drawing below
					 			AttributedString tmpText = 
									new AttributedString(
										paragraph, 
										startIndex, 
										startIndex + 1
										);
					 			LineBreakMeasurer lbm = new LineBreakMeasurer(tmpText.getIterator(), getFontRenderContext());
					 			TextLayout tlyt = lbm.nextLayout(100);
								maxAscent = tlyt.getAscent();
								maxDescent = tlyt.getDescent();
								maxLeading = tlyt.getLeading();
							}
							else
							{
								nextTabStop = ParagraphUtil.getFirstTabStop(text.getParagraph(), endX);
							}
						}
						else
						{
							// something fitted
							nextTabStop = null;
							requireNextWord = false;
						}
					}
				}

				oldSegment = crtSegment;
			}

			lineHeight = getLineHeight(paragraphStart == 0 && lines == 0, text.getParagraph(), maxLeading, maxAscent);// + maxDescent;
			
			if (paragraphStart == 0 && lines == 0)
			//if (lines == 0) //FIXMEPARA
			{
				lineHeight +=  text.getParagraph().getSpacingBefore();
			}

			if (drawPosY + lineHeight <= text.getTextHeight())
			{
				lines++;
				
				drawPosY += lineHeight;
				
				float lastRightX = (segments == null || segments.size() == 0 ? 0 : segments.get(segments.size() - 1).rightX);
				
				// now iterate through segments and draw their layouts
				for (segmentIndex = 0; segmentIndex < segments.size(); segmentIndex++)
				{
					TabSegment segment = segments.get(segmentIndex);
					TextLayout layout = segment.layout;

					switch (text.getHorizontalTextAlign())
					{
						case JUSTIFIED :
						{
							if (layout.isLeftToRight())
							{
								drawPosX = text.getParagraph().getLeftIndent() + segment.leftX;
							}
							else
							{
								drawPosX = (endX - lastRightX + segment.leftX);
							}
	
							break;
						}
						case RIGHT ://FIXMETAB RTL writings
						{
							drawPosX = (endX - lastRightX + segment.leftX);
							break;
						}
						case CENTER :
						{
							drawPosX = ((endX - lastRightX) / 2) + segment.leftX; 
							break;
						}
						case LEFT :
						default :
						{
							drawPosX = text.getParagraph().getLeftIndent() + segment.leftX;
						}
					}

					/*   */
					draw();
					
					bulletText = null;
					bulletChunk = null;
				}
				
				drawPosY += maxDescent;
				
//				if (lineMeasurer.getPosition() == paragraph.getEndIndex()) //FIXMEPARA
//				{
//					drawPosY += text.getParagraph().getSpacingAfter();
//				}
			}
			else
			{
				isMaxHeightReached = true;
			}
		}
	}
	
	
	private void prepareBullet(
		StyledTextWriteContext context, 
		Map<Attribute,Object> attributes
		)
	{
		htmlListIndent = context.getDepth() * 50; //FIXMEBULLET always 50?

		bulletText = JRStyledTextUtil.getBulletText(context);
		
		if (bulletText == null)
		{
			bulletChunk = null;
		}
		else
		{
			bulletChunk = 
				new AttributedString(
					bulletText,
					attributes
					);
		}
	}
	
	/**
	 * 
	 */
	protected AttributedString getAttributedString()
	{
		return styledText.getAwtAttributedString(fontUtil, ignoreMissingFont);
	}

	/**
	 * 
	 */
	public abstract void draw();

	/**
	 * 
	 */
	public static float getLineHeight(boolean isFirstLine, JRParagraph paragraph, float maxLeading, float maxAscent)
	{
		float lineHeight = 0;

		switch (paragraph.getLineSpacing())
		{
			case ONE_AND_HALF:
			{
				if (isFirstLine)
				{
					lineHeight = maxLeading + 1f * maxAscent;
				}
				else
				{
					lineHeight = maxLeading + 1.5f * maxAscent;
				}
				break;
			}
			case DOUBLE:
			{
				if (isFirstLine)
				{
					lineHeight = maxLeading + 1f * maxAscent;
				}
				else
				{
					lineHeight = maxLeading + 2f * maxAscent;
				}
				break;
			}
			case PROPORTIONAL:
			{
				if (isFirstLine)
				{
					lineHeight = maxLeading + 1f * maxAscent;
				}
				else
				{
					lineHeight = maxLeading + paragraph.getLineSpacingSize() * maxAscent;
				}
				break;
			}
			case AT_LEAST:
			{
				if (isFirstLine)
				{
					lineHeight = maxLeading + 1f * maxAscent;
				}
				else
				{
					lineHeight = Math.max(maxLeading + 1f * maxAscent, paragraph.getLineSpacingSize());
				}
				break;
			}
			case FIXED:
			{
				if (isFirstLine)
				{
					lineHeight = maxLeading + 1f * maxAscent;
				}
				else
				{
					lineHeight = paragraph.getLineSpacingSize();
				}
				break;
			}
			case SINGLE:
			default :
			{
				lineHeight = maxLeading + 1f * maxAscent;
				break;
			}
		}
		
		return lineHeight;
	}

	/**
	 * 
	 *
	public static float getLineHeight(JRParagraph paragraph, float lineSpacingFactor, int maxFontSize)
	{
		float lineHeight = 0;

		switch (paragraph.getLineSpacing())
		{
			case SINGLE:
			case ONE_AND_HALF:
			case DOUBLE:
			case PROPORTIONAL:
			{
				lineHeight = lineSpacingFactor * maxFontSize;
				break;
			}
			case AT_LEAST:
			{
				lineHeight = Math.max(lineSpacingFactor * maxFontSize, paragraph.getLineSpacingSize());
				break;
			}
			case FIXED:
			{
				lineHeight = paragraph.getLineSpacingSize();
				break;
			}
			default :
			{
				throw new JRRuntimeException("Invalid line space type: " + paragraph.getLineSpacing());
			}
		}
		
		return lineHeight;
	}


	/**
	 * 
	 */
	public FontRenderContext getFontRenderContext()
	{
		return LINE_BREAK_FONT_RENDER_CONTEXT;
	}


	/**
	 * 
	 */
	public static class TabSegment
	{
		public TextLayout layout;
		public AttributedString as;//FIXMETAB rename these
		public String text;
		public float leftX;
		public float rightX;
		public boolean isLastLine;
	}
}
