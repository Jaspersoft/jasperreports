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

/*
 * Contributors:
 * Eugene D - eugenedruy@users.sourceforge.net 
 * Adrian Jackson - iapetus@users.sourceforge.net
 * David Taylor - exodussystems@users.sourceforge.net
 * Lars Kristensen - llk@users.sourceforge.net
 */
package net.sf.jasperreports.engine.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignExpressionChunk;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JRExpressionUtil
{
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_EXPRESSION_CHUNK_TYPE = "util.expression.unknown.expression.chunk.type";
	
	/**
	 *
	 */
	public static String getExpressionText(JRExpression exp)
	{
		return exp == null ? null : exp.getText();
	}


	private static final Pattern SIMPLE_EXPRESSION_PATTERN = 
		Pattern.compile("\\s*\"([^\"]*((?<=\\\\)\"[^\"]*)*)\"\\s*");
	
	private static final int SIMPLE_EXPRESSION_TEXT_GROUP = 1;
	
	private static final Pattern TEXT_QUOTE_PATTERN = Pattern.compile("\\\\\"");
	private static final String TEXT_QUOTE_REPLACEMENT = "\"";
	
	/**
	 *
	 */
	public static String getSimpleExpressionText(JRExpression expression)
	{
		String value = null;
		if (expression != null)
		{
			JRExpressionChunk[] chunks = expression.getChunks();
			if (
				chunks != null 
				&& chunks.length == 1 
				&& chunks[0].getType() == JRExpressionChunk.TYPE_TEXT
				)
			{
				String chunk = chunks[0].getText();
				Matcher matcher = SIMPLE_EXPRESSION_PATTERN.matcher(chunk);
				if (matcher.matches())
				{
					String text = matcher.group(SIMPLE_EXPRESSION_TEXT_GROUP);
					value = TEXT_QUOTE_PATTERN.matcher(text).replaceAll(
							TEXT_QUOTE_REPLACEMENT);
				}
			}
		}
		return value;
	}

	public static void visitChunks(JRExpression expression, ExpressionChunkVisitor visitor)
	{
		JRExpressionChunk[] chunks = expression.getChunks();
		if (chunks != null)
		{
			for (JRExpressionChunk chunk : chunks)
			{
				switch (chunk.getType())
				{
				case JRExpressionChunk.TYPE_TEXT:
					visitor.visitTextChunk(chunk);
					break;
				case JRExpressionChunk.TYPE_PARAMETER:
					visitor.visitParameterChunk(chunk);
					break;
				case JRExpressionChunk.TYPE_FIELD:
					visitor.visitFieldChunk(chunk);
					break;
				case JRExpressionChunk.TYPE_VARIABLE:
					visitor.visitVariableChunk(chunk);
					break;
				case JRExpressionChunk.TYPE_RESOURCE:
					visitor.visitResourceChunk(chunk);
					break;
				default:
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_UNKNOWN_EXPRESSION_CHUNK_TYPE,
							new Object[]{chunk.getType()});
				}
			}
		}
	}

	/**
	 *
	 */
	public static String convertSimpleTextExpression(JRExpression expression)
	{
		JRDesignExpression convertedExpression = null; 
		
		if (expression != null)
		{
			convertedExpression = new JRDesignExpression();
			JRExpressionChunk[] chunks = expression.getChunks();
			if (chunks != null) 
			{
				for (int i = 0; i < chunks.length; i++)
				{
					JRExpressionChunk chunk = chunks[i];
					JRDesignExpressionChunk convertedChunk = new JRDesignExpressionChunk();
					convertedChunk.setType(chunk.getType());
					
					if (chunk.getType() == JRExpressionChunk.TYPE_TEXT)
					{
						convertedChunk.setText(
							(i == 0 ? "" : " + ") 
							+ "\"" + JRStringUtil.escapeJavaStringLiteral(chunk.getText()) + "\""
							+ (i == chunks.length - 1 ? "" : " + ") 
							);
					}
					else
					{
						convertedChunk.setText(chunk.getText());
					}
					convertedExpression.addChunk(convertedChunk);
				}
			}
		}
		
		return convertedExpression == null ? null : convertedExpression.getText();
	}

	private JRExpressionUtil()
	{
	}
}
