/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2026 Actian Corporation, a division of HCL Software. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Actian,
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

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.ExpressionTypeEnum;
import net.sf.jasperreports.engine.util.ExpressionParser;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SimpleTextExpressionEvaluator
{

	private static final String EMPTY_RESULT = "";
	
	public static String evaluateExpression(JRExpression expression, ExpressionValues values)
	{
		if (expression.getType() != ExpressionTypeEnum.SIMPLE_TEXT)
		{
			throw new IllegalArgumentException("Simple text expression expected");
		}
		
		JRExpressionChunk[] chunks = expression.getChunks();
		return evaluate(chunks, values);
	}

	public static String evaluate(JRExpressionChunk[] chunks, ExpressionValues values)
	{
		if (chunks == null || chunks.length == 0)
		{
			return EMPTY_RESULT;
		}

		ResultCollector result = new ResultCollector(values);
		result.collectChunks(chunks);
		return result.getResult();
	}
	
	private static class ResultCollector implements ExpressionParser.ParseResult
	{
		private final ExpressionValues values;
		
		//keeping the first text separately to avoid creating a StringBuilder when there's a single chunk
		private String firstText;
		private StringBuilder resultBuilder;
		
		public ResultCollector(ExpressionValues values)
		{
			this.values = values;
		}
		
		public String getResult()
		{
			String result;
			if (resultBuilder != null)
			{
				result = resultBuilder.toString();
			}
			else if (firstText != null)
			{
				result = firstText;
			}
			else
			{
				result = EMPTY_RESULT;
			}
			return result;
		}

		private void appendText(String text)
		{
			assert text != null;
			
			if (resultBuilder == null)
			{
				if (firstText == null)
				{
					firstText = text;
				}
				else
				{
					resultBuilder = new StringBuilder(firstText.length() + text.length() + 16);
					resultBuilder.append(firstText);
					resultBuilder.append(text);
					firstText = null;
				}
			}
			else
			{
				resultBuilder.append(text);
			}
		}
		
		public void collectChunks(JRExpressionChunk[] chunks)
		{
			for (JRExpressionChunk chunk : chunks)
			{
				byte chunkType = chunk.getType();
				if (chunkType == JRExpressionChunk.TYPE_TEXT)
				{
					appendTextChunk(chunk.getText());
				}
				else
				{
					appendValueChunk(chunkType, chunk.getText());
				}
			}
		}
		
		private void appendTextChunk(String text)
		{
			if (text != null)
			{
				appendText(text);
			}
		}
		
		private void appendValueChunk(byte chunkType, String chunkText)
		{
			switch (chunkType)
			{
			case JRExpressionChunk.TYPE_RESOURCE:
				appendValueChunk(values.getMessage(chunkText));
				break;

			case JRExpressionChunk.TYPE_PARAMETER:
				appendValueChunk(values.getParameterValue(chunkText));
				break;

			case JRExpressionChunk.TYPE_FIELD:
				appendValueChunk(values.getFieldValue(chunkText));
				break;

			case JRExpressionChunk.TYPE_VARIABLE:
				appendValueChunk(values.getVariableValue(chunkText));
				break;

			default:
				throw new JRRuntimeException("Unexpected expression chunk type " + chunkType);
			}
		}
		
		private void appendValueChunk(Object value)
		{
			if (value == null)
			{
				appendText(String.valueOf(value));
			}
			else if (value instanceof String)
			{
				String text = (String) value;
				ExpressionParser expressionParser = ExpressionParser.instance();
				//fast test to determine whether the text could have further placeholders
				if (expressionParser.fastPlaceholderDetect(text))
				{
					//could have placehoders, parsing and recursing
					//TODO detect circular references?
					expressionParser.parseExpression(text, this);
				}
				else
				{
					//no placehoders
					appendText(text);
				}
			}
			else
			{
				//no formatting for now
				String text = String.valueOf(value);
				appendText(text == null ? String.valueOf(text) : text);
			}
		}

		@Override
		public void addTextChunk(String text)
		{
			appendTextChunk(text);
		}

		@Override
		public void addChunk(byte chunkType, String chunkText)
		{
			appendValueChunk(chunkType, chunkText);
		}
	}
	
}
