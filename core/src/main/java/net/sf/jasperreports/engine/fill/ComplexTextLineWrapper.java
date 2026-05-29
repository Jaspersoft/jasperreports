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

import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.util.Iterator;
import java.util.Map;

import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.MaxFontSizeFinder;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class ComplexTextLineWrapper implements TextLineWrapper
{

	private TextMeasureContext context;
	private MaxFontSizeFinder maxFontSizeFinder;
	
	private AttributedCharacterIterator allParagraphs;
	private Map<Attribute,Object> globalAttributes;
	
	private AttributedCharacterIterator paragraph;
	private LineBreakMeasurer lineMeasurer;
	
	public ComplexTextLineWrapper()
	{
	}
	
	protected ComplexTextLineWrapper(ComplexTextLineWrapper parent)
	{
		this.context = parent.context;
		this.maxFontSizeFinder = parent.maxFontSizeFinder;
		
		this.allParagraphs = parent.allParagraphs;
		this.globalAttributes = parent.globalAttributes;
	}

	@Override
	public void init(TextMeasureContext context)
	{
		this.context = context;
		
		boolean isStyledText = !JRCommonText.MARKUP_NONE.equals(context.getElement().getMarkup());
		maxFontSizeFinder = MaxFontSizeFinder.getInstance(isStyledText);
	}

	@Override
	public boolean start(JRStyledText styledText)
	{
		globalAttributes = styledText.getGlobalAttributes();
		allParagraphs = styledText.getAwtAttributedString(context.getFontUtil(),
				context.isIgnoreMissingFont(), context.getFontSizeScale()).getIterator();
		return true;
	}

	@Override
	public void startParagraph(int paragraphStart, int paragraphEnd, boolean truncateAtChar)
	{
		AttributedCharacterIterator paragraph = new AttributedString(allParagraphs, 
				paragraphStart, paragraphEnd).getIterator();
		startParagraph(paragraph, truncateAtChar);
	}

	@Override
	public void startEmptyParagraph(int paragraphStart)
	{
		Map<Attribute, Object> attributes = new AttributedString(allParagraphs, 
			paragraphStart, paragraphStart + 1).getIterator().getAttributes();
		AttributedCharacterIterator paragraph = new AttributedString(" ", attributes).getIterator();
		startParagraph(paragraph, false);
	}
	
	protected void startParagraph(AttributedCharacterIterator paragraph, boolean truncateAtChar)
	{
		this.paragraph = paragraph;
		BreakIterator breakIt = truncateAtChar ? BreakIterator.getCharacterInstance() 
				: BreakIterator.getLineInstance();
		lineMeasurer = new LineBreakMeasurer(paragraph, breakIt, context.getFontRenderContext());
	}

	@Override
	public int paragraphPosition()
	{
		return lineMeasurer.getPosition();
	}

	@Override
	public int paragraphEnd()
	{
		return paragraph.getEndIndex();
	}

	@Override
	public TextLine nextLine(float width, int endLimit, boolean requireWord)
	{
		TextLayout textLayout = lineMeasurer.nextLayout(width, endLimit, requireWord);
		return textLayout == null ? null : new TextLayoutLine(textLayout);
	}

	@Override
	public TextLine baseTextLine(int index)
	{
		AttributedString tmpText = new AttributedString(paragraph, index, index + 1);
		LineBreakMeasurer lbm = new LineBreakMeasurer(tmpText.getIterator(), context.getFontRenderContext());
		TextLayout tlyt = lbm.nextLayout(100);//FIXME what is this? why 100?
		return new TextLayoutLine(tlyt);
	}
	
	@Override
	public float maxFontsize(int start, int end)
	{
		return maxFontSizeFinder.findMaxFontSize(
				new AttributedString(paragraph, start, end).getIterator(),
				context.getElement().getFontSize());
	}
	
	@Override
	public String getLineText(int start, int end)
	{
		StringBuilder lineText = new StringBuilder();
		allParagraphs.setIndex(start);
		while (allParagraphs.getIndex() < end 
				&& allParagraphs.current() != '\n')
		{
			lineText.append(allParagraphs.current());
			allParagraphs.next();
		}
		return lineText.toString();
	}

	@Override
	public char charAt(int index)
	{
		return allParagraphs.setIndex(index);
	}

	@Override
	public TextLineWrapper lastLineWrapper(String lineText, int start, int textLength, boolean truncateAtChar)
	{
		AttributedString attributedText = new AttributedString(lineText);
		
		//set original attributes for the text part
		AttributedCharacterIterator textAttributes = new AttributedString(allParagraphs, 
				start, start + textLength).getIterator();
		setAttributes(attributedText, textAttributes, 0);
		
		//set global attributes for the suffix part
		setAttributes(attributedText, globalAttributes, textLength, lineText.length());

		AttributedCharacterIterator lineParagraph = attributedText.getIterator();
		
		ComplexTextLineWrapper lastLineWrapper = new ComplexTextLineWrapper(this);
		lastLineWrapper.startParagraph(lineParagraph, truncateAtChar);
		return lastLineWrapper;
	}
	
	protected void setAttributes(AttributedString string, AttributedCharacterIterator attributes, 
		int stringOffset)
	{
		for (char c = attributes.first(); c != CharacterIterator.DONE; c = attributes.next())
		{
			for (Iterator<Map.Entry<Attribute,Object>> it = attributes.getAttributes().entrySet().iterator(); it.hasNext();)
			{
				Map.Entry<Attribute,Object> attributeEntry = it.next();
				AttributedCharacterIterator.Attribute attribute = attributeEntry.getKey();
				if (attributes.getRunStart(attribute) == attributes.getIndex())
				{
					Object attributeValue = attributeEntry.getValue();
					string.addAttribute(
						attribute, 
						attributeValue, 
						attributes.getIndex() + stringOffset,
						attributes.getRunLimit(attribute) + stringOffset
						);
				}
			}
		}
	}
	
	protected void setAttributes(AttributedString string, Map<Attribute,Object> attributes, 
		int startIndex, int endIndex)
	{
		for (Iterator<Map.Entry<Attribute,Object>> it = attributes.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry<Attribute,Object> entry = it.next();
			Attribute attribute = entry.getKey();
			Object attributeValue = entry.getValue();
			string.addAttribute(attribute, attributeValue, startIndex, endIndex);
		}
	}
}
