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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ExpressionParser
{

	private static final ExpressionParser INSTANCE = new ExpressionParser();
	
	public static ExpressionParser instance()
	{
		return INSTANCE;
	}
	
	public static final Pattern PLACEHOLDER_PATTERN = 
			Pattern.compile("\\$([RPFV])\\{(.*?)\\}", Pattern.MULTILINE | Pattern.DOTALL);
	
	protected static final int PLACEHOLDER_TYPE_INDEX = 1;
	protected static final int PLACEHOLDER_TEXT_INDEX = 2;
	
	protected static final char PLACEHOLDER_START = '$';
	protected static final char PLACEHOLDER_OPEN = '{';
	protected static final char PLACEHOLDER_CLOSE = '}';
	
	protected static final char PLACEHOLDER_TYPE_RESOURCE_MESSAGE = 'R';
	protected static final char PLACEHOLDER_TYPE_PARAMETER = 'P';
	protected static final char PLACEHOLDER_TYPE_FIELD = 'F';
	protected static final char PLACEHOLDER_TYPE_VARIABLE = 'V';
	
	public static interface ParseResult
	{
		void addTextChunk(String text);

		void addChunk(byte chunkType, String chunkText);
	}
	
	public void parseExpression(String text, ParseResult result)
	{
		Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);

		int textChunkStart = 0;
		StringBuilder textChunk = new StringBuilder(text.length());
		while (matcher.find())
		{
			int matchStart = matcher.start();
			int matchEnd = matcher.end();
			if (matchStart > 0 && text.charAt(matchStart - 1) == '$')
			{
				// we have a $$ escape, append it to the text chunk with a single $
				textChunk.append(text, textChunkStart, matchStart - 1);
				textChunk.append(text, matchStart, matchEnd);
			}
			else
			{
				// we have a proper placeholder
				textChunk.append(text, textChunkStart, matchStart);
				if (textChunk.length() > 0)
				{
					result.addTextChunk(textChunk.toString());
					textChunk.delete(0, textChunk.length());
				}
				
				String chunkStringType = matcher.group(PLACEHOLDER_TYPE_INDEX);
				byte chunkType = chunkStringToType(chunkStringType);
				String chunkText = matcher.group(PLACEHOLDER_TEXT_INDEX);
				result.addChunk(chunkType, chunkText);
			}
			
			textChunkStart = matchEnd;
		}
		
		textChunk.append(text, textChunkStart, text.length());
		if (textChunk.length() > 0)
		{
			result.addTextChunk(textChunk.toString());
		}
	}
	
	protected byte chunkStringToType(String chunkStringType)
	{
		byte chunkType;
		char firstChar = chunkStringType.charAt(0);
		switch (firstChar)
		{
		case PLACEHOLDER_TYPE_PARAMETER:
			chunkType = JRExpressionChunk.TYPE_PARAMETER;
			break;

		case PLACEHOLDER_TYPE_FIELD:
			chunkType = JRExpressionChunk.TYPE_FIELD;
			break;

		case PLACEHOLDER_TYPE_VARIABLE:
			chunkType = JRExpressionChunk.TYPE_VARIABLE;
			break;

		case PLACEHOLDER_TYPE_RESOURCE_MESSAGE:
			chunkType = JRExpressionChunk.TYPE_RESOURCE;
			break;

		default:
			throw 
				new JRRuntimeException(
						JRExpressionUtil.EXCEPTION_MESSAGE_KEY_UNKNOWN_EXPRESSION_CHUNK_TYPE,
						new Object[]{chunkStringType});
		}
		return chunkType;
	}
	
	public boolean fastPlaceholderDetect(String text)
	{
		int textLength = text.length();
		int startIdx = text.indexOf(PLACEHOLDER_START);
		boolean found = false;
		while (startIdx >= 0)
		{
			if (startIdx + 3 >= textLength)
			{
				break;
			}
			
			char typeChart = text.charAt(startIdx + 1);
			if ((typeChart == PLACEHOLDER_TYPE_RESOURCE_MESSAGE || typeChart == PLACEHOLDER_TYPE_PARAMETER
					|| typeChart == PLACEHOLDER_TYPE_FIELD || typeChart == PLACEHOLDER_TYPE_VARIABLE)
				&& text.charAt(startIdx + 2) == PLACEHOLDER_OPEN)
			{
				found = true;
				break;
			}
			
			startIdx = text.indexOf(PLACEHOLDER_START, startIdx + 1);
		}
		return found;
	}
}
