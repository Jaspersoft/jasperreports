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
package net.sf.jasperreports.engine.util;

import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument.RunElement;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.base.JRBasePrintHyperlink;
import net.sf.jasperreports.engine.type.HyperlinkTypeEnum;
import net.sf.jasperreports.engine.util.JRStyledText.Run;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class HtmlEditorKitMarkupProcessor extends EditorKitMarkupProcessor
{
	private static final Log log = LogFactory.getLog(HtmlEditorKitMarkupProcessor.class);
	
	private Document document;
	private boolean bodyOccurred = false;
	private boolean isFirstContentTag = true;
	private boolean breaksFlow = false;
	private boolean suppressBreaksFlow = false;
	private int rootEndOffset;

	private Stack<StyledTextListInfo> htmlListStack;
	private boolean insideLi;
	private boolean liStart;
	private StyledTextListInfo justClosedList;

	/**
	 * 
	 */
	public static final class Factory implements MarkupProcessorFactory
	{ 
		@Override
		public MarkupProcessor createMarkupProcessor()
		{
			return new HtmlEditorKitMarkupProcessor();
		}
	}

	@Override
	public String convert(String srcText)
	{
		if (srcText.indexOf('<') >= 0 || srcText.indexOf('&') >= 0)
		{
			JRStyledText styledText = new JRStyledText();
			
			document = getDocument(srcText);

			bodyOccurred = false;
			isFirstContentTag = true;
			breaksFlow = false;
			suppressBreaksFlow = false;
			rootEndOffset = 0;

			htmlListStack = new Stack<>();
			insideLi = false;
			liStart = false;
			justClosedList = null;

			Element root = document.getDefaultRootElement();
			if (root != null)
			{
				rootEndOffset = root.getEndOffset();
				processElement(styledText, root);
			}

			styledText.setGlobalAttributes(new HashMap<>());
			
			return JRStyledTextParser.getInstance().write(styledText);
		}
		
		return srcText;
	}
	
	
	@Override
	protected EditorKit getEditorKit()
	{
		return new HTMLEditorKit();
	}
	
	
	private void processElement(JRStyledText styledText, Element parentElement)
	{
		for (int i = 0; i < parentElement.getElementCount(); i++)
		{
			Element element = parentElement.getElement(i);

			AttributeSet attrs = element.getAttributes();

			Object elementName = attrs.getAttribute(AbstractDocument.ElementNameAttribute);
			Object object = (elementName != null) ? null : attrs.getAttribute(StyleConstants.NameAttribute);
			HTML.Tag htmlTag = object instanceof HTML.Tag ? (HTML.Tag) object : null;

			if (htmlTag != null) 
			{
				suppressBreaksFlow |= (htmlTag == Tag.UL || htmlTag == Tag.OL || htmlTag == Tag.LI);
				// the breaksFlow is supposed to keep the aggregated flag for the nested tags
				// that precede the current element, so in theory the current element should not be part of the
				// update of the flag here;
				// but since the breaksFLow flag is used only in BR and CONTENT tags, which are both leaf elements,
				// then it is safe to simply exclude them from the aggregation based on the element.isLeaf() flag
				// the IMPLIED tag is not a breaksFlow one for the HTML parser, but we want it to be a breaksFlow element,
				// in our algorithm, so dealing with it here
				breaksFlow |= (!element.isLeaf() && htmlTag.breaksFlow()) || htmlTag == Tag.IMPLIED; 
				
				if (htmlTag == Tag.BODY)
				{
					bodyOccurred = true;
					processElement(styledText, element);
				}
				else if (htmlTag == Tag.BR)
				{
					if (
						bodyOccurred && !isFirstContentTag && breaksFlow && !suppressBreaksFlow 
						&& i == 0 // only for first BR element in parent
						)
					{
						styledText.append("\n");
						resizeRuns(styledText.getRuns(), styledText.length(), 1);
					}

					if (element.getEndOffset() != rootEndOffset - 1) // only if the BR element is not the very last element of the html snippet 
					{
						styledText.append("\n");
						
						int startIndex = styledText.length();
						resizeRuns(styledText.getRuns(), startIndex, 1);
	
						processElement(styledText, element);
						styledText.addRun(new JRStyledText.Run(new HashMap<>(), startIndex, styledText.length()));
	
						// a second newline is added in case the <br> tag was not empty, as it is usually used 
						if (startIndex < styledText.length()) {
							styledText.append("\n");
							resizeRuns(styledText.getRuns(), startIndex, 1);
						}
						
						breaksFlow = false;
						suppressBreaksFlow = true;
					}
				}
				else if (htmlTag == Tag.OL || htmlTag == Tag.UL)
				{
					Object type = attrs.getAttribute(HTML.Attribute.TYPE);
					Object start = attrs.getAttribute(HTML.Attribute.START);

					StyledTextListInfo htmlList = 
						new StyledTextListInfo(
							htmlTag == Tag.OL,
							htmlTag == Tag.OL && type != null ? String.valueOf(type) : null,
							htmlTag == Tag.OL && start != null ? Integer.valueOf(start.toString()) : null,
							insideLi
							);
					
					htmlList.setAtLiStart(liStart);

					htmlListStack.push(htmlList);
					
					insideLi = false;
					
					Map<Attribute,Object> styleAttrs = new HashMap<>();

					styleAttrs.put(JRTextAttribute.HTML_LIST, htmlListStack.toArray(new StyledTextListInfo[htmlListStack.size()]));
					styleAttrs.put(JRTextAttribute.HTML_LIST_ITEM, StyledTextListItemInfo.NO_LIST_ITEM_FILLER);
					
					int startIndex = styledText.length();

					processElement(styledText, element);

					styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
					
					justClosedList = htmlListStack.pop();
				}
				else if (htmlTag == Tag.LI)
				{
					Map<Attribute,Object> styleAttrs = new HashMap<>();

					StyledTextListInfo htmlList = null;
					
					boolean ulAdded = false;
					if (htmlListStack.size() == 0)
					{
						htmlList = new StyledTextListInfo(false, null, null, false);
						htmlListStack.push(htmlList);
						styleAttrs.put(JRTextAttribute.HTML_LIST, htmlListStack.toArray(new StyledTextListInfo[htmlListStack.size()]));
						styleAttrs.put(JRTextAttribute.HTML_LIST_ITEM, StyledTextListItemInfo.NO_LIST_ITEM_FILLER);
						ulAdded = true;
					}
					else
					{
						htmlList = htmlListStack.peek();
					}
					htmlList.setItemCount(htmlList.getItemCount() + 1);
					insideLi = true;
					liStart = true;
					justClosedList = null;
					
					styleAttrs.put(JRTextAttribute.HTML_LIST_ITEM, new StyledTextListItemInfo(htmlList.getItemCount() - 1));
					
					int startIndex = styledText.length();

					processElement(styledText, element);

					styledText.addRun(new JRStyledText.Run(styleAttrs, startIndex, styledText.length()));
					
					insideLi = false;
					liStart = false;
					if (justClosedList != null)
					{
						justClosedList.setAtLiEnd(true);
					}

					if (ulAdded)
					{
						htmlListStack.pop();
					}
				}
				else if (htmlTag == Tag.CONTENT)
				{
					String chunk = null;
					try
					{
						chunk = document.getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
					}
					catch (BadLocationException e)
					{
						if (log.isDebugEnabled())
						{
							log.debug("Error converting markup.", e);
						}
					}
					
					if (
						chunk != null
						&& !"\n".equals(chunk) 
						)
					{
						if (bodyOccurred && !isFirstContentTag && breaksFlow && !suppressBreaksFlow)
						{
							styledText.append("\n");
							resizeRuns(styledText.getRuns(), styledText.length(), 1);
						}

						isFirstContentTag = false;
						breaksFlow = false;
						suppressBreaksFlow = false;

						liStart = false;
						justClosedList = null;

						int startIndex = styledText.length();

						styledText.append(chunk);
						
						Map<Attribute,Object> styleAttributes = getAttributes(element.getAttributes());

						if (element instanceof RunElement)
						{
							RunElement runElement = (RunElement)element;
							AttributeSet attrSet = (AttributeSet)runElement.getAttribute(Tag.A);
							if (attrSet != null)
							{
								JRBasePrintHyperlink hyperlink = new JRBasePrintHyperlink();
								hyperlink.setHyperlinkType(HyperlinkTypeEnum.REFERENCE);
								hyperlink.setHyperlinkReference((String)attrSet.getAttribute(HTML.Attribute.HREF));
								hyperlink.setLinkTarget((String)attrSet.getAttribute(HTML.Attribute.TARGET));
								styleAttributes.put(JRTextAttribute.HYPERLINK, hyperlink);
							}
						}

						styledText.addRun(
							new JRStyledText.Run(styleAttributes, startIndex, styledText.length())
							);
					}
				}
				else if (htmlTag == Tag.P && element.getElementCount() == 1) // for clarity, deal with empty <p></p> tags separately, instead of adding more logic to the Tag.CONTENT processing above
				{
					Element pChildElement = element.getElement(0);
					AttributeSet pChildAttrs = pChildElement.getAttributes();
					Object pChildElementName = pChildAttrs.getAttribute(AbstractDocument.ElementNameAttribute);
					Object pChildObject = (pChildElementName != null) ? null : pChildAttrs.getAttribute(StyleConstants.NameAttribute);
					HTML.Tag pChildHtmlTag = object instanceof HTML.Tag ? (HTML.Tag) pChildObject : null;
					if (pChildHtmlTag == Tag.CONTENT)
					{
						String chunk = null;
						try
						{
							chunk = document.getText(pChildElement.getStartOffset(), pChildElement.getEndOffset() - pChildElement.getStartOffset());
						}
						catch (BadLocationException e)
						{
							if (log.isDebugEnabled())
							{
								log.debug("Error converting markup.", e);
							}
						}
						
						if ("\n".equals(chunk))
						{
							styledText.append("\n");
							resizeRuns(styledText.getRuns(), styledText.length(), 1);
						}
					}
				}
				else
				{
					if (bodyOccurred)
					{
						processElement(styledText, element);
					}
				}
				
				suppressBreaksFlow |= (htmlTag == Tag.UL || htmlTag == Tag.OL || htmlTag == Tag.LI);
			}
		}
	}
	
	/**
	 *
	 */
	private void resizeRuns(List<Run> runs, int startIndex, int count)
	{
		for (JRStyledText.Run run : runs)
		{
			if (run.startIndex <= startIndex && run.endIndex > startIndex - count)
			{
				run.endIndex += count;
			}
		}
	}
	
	/**
	 * 
	 * @param index the current index between 0 and 18277
	 * @param isUpperCase specifies whether the result should be made of upper case characters
	 * @return a character representation of the numeric index in an ordered bullet list, that contains up to 3 chars
	 */
	protected static String getOLBulletChars(int index, boolean isUpperCase)
	{
		// max 3-letter index is 18277
		if (index < 0 || index > 18277)	
		{
			throw 
				new JRRuntimeException(
					JRStringUtil.EXCEPTION_MESSAGE_KEY_NUMBER_OUTSIDE_BOUNDS,
					new Object[]{index});
		} 
		
		return JRStringUtil.getLetterNumeral(index, isUpperCase);
	}
}
