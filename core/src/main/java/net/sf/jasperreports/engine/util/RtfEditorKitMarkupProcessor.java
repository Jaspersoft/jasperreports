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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.AbstractDocument.LeafElement;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class RtfEditorKitMarkupProcessor extends EditorKitMarkupProcessor
{
	private static final Log log = LogFactory.getLog(RtfEditorKitMarkupProcessor.class);

	private static RtfEditorKitMarkupProcessor instance;  

	/**
	 * 
	 */
	public static final class Factory implements MarkupProcessorFactory
	{ 
		@Override
		public MarkupProcessor createMarkupProcessor()
		{
			return RtfEditorKitMarkupProcessor.getInstance();
		}
	}

	/**
	 * 
	 */
	public static RtfEditorKitMarkupProcessor getInstance()
	{
		if (instance == null)
		{
			instance = new RtfEditorKitMarkupProcessor();
		}
		return instance;
	}
	
	@Override
	public String convert(String srcText)
	{
		List<Element> elements = new ArrayList<>();

		Document document = getDocument(srcText);

		Element root = document.getDefaultRootElement();
		if (root != null)
		{
			addElements(elements, root);
		}

		String chunk = null;
		Element element = null;
		int startOffset = 0;
		int endOffset = 0;
		
		JRStyledText styledText = new JRStyledText();
		styledText.setGlobalAttributes(new HashMap<>());
		for (int i = 0; i < elements.size(); i++)
		{
			if (chunk != null)
			{
				styledText.append(chunk);
				styledText.addRun(new JRStyledText.Run(getAttributes(element.getAttributes()), startOffset, endOffset));
			}

			chunk = null;
			element = elements.get(i);
			startOffset = element.getStartOffset();
			endOffset = element.getEndOffset();

			try
			{
				chunk = document.getText(startOffset, endOffset - startOffset);
			}
			catch(BadLocationException e)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Error converting markup.", e);
				}
			}

		}

		if (chunk != null && !"\n".equals(chunk))
		{
			styledText.append(chunk);
			styledText.addRun(new JRStyledText.Run(getAttributes(element.getAttributes()), startOffset, endOffset));
		}

		return JRStyledTextParser.getInstance().write(styledText);
	}
	
	@Override
	protected EditorKit getEditorKit()
	{
		return new RTFEditorKit();
	}
	
	/**
	 * 
	 */
	protected void addElements(List<Element> elements, Element element) 
	{
		if (element instanceof LeafElement)
		{
			elements.add(element);
		}
		for (int i = 0; i < element.getElementCount(); i++)
		{
			Element child = element.getElement(i);
			addElements(elements, child);
		}
	}
	
}
