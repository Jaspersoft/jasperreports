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
package net.sf.jasperreports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRExpressionChunk;
import net.sf.jasperreports.engine.design.JRDesignExpression;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ExpressionParsingTest
{
	private static final Log log = LogFactory.getLog(ExpressionParsingTest.class);
	
	@Test(dataProvider = "expressions")
	public void baseObject(String expressionText, Object[] expectedChunks)
	{
		assert expectedChunks != null;
		assert expectedChunks.length % 2 == 0;
		
		JRDesignExpression expression = new JRDesignExpression(expressionText);
		
		assert expressionText.equals(expression.getText());
		
		JRExpressionChunk[] chunks = expression.getChunks();
		if (log.isDebugEnabled())
		{
			log.debug("expression \"" + expressionText + "\" has chunks " + chunks.length);
		}
		
		assert chunks != null;
		assert 2 * chunks.length == expectedChunks.length;
		for (int i = 0; i < chunks.length; i++)
		{
			if (log.isDebugEnabled())
			{
				log.debug("chunk " + i + ", type " + chunks[i].getType() + ", text \"" + chunks[i].getText() + "\"");
			}
			
			assert chunks[i].getType() == (Byte) expectedChunks[2 * i];
			assert chunks[i].getText().equals(expectedChunks[2 * i + 1]);
		}
	}

	@DataProvider
	public Object[][] expressions()
	{
		return new Object[][]{
				{"20", new Object[]{JRExpressionChunk.TYPE_TEXT, "20"}},
				{"\"20\"", new Object[]{JRExpressionChunk.TYPE_TEXT, "\"20\""}},
				{"$R{foo}", new Object[]{JRExpressionChunk.TYPE_RESOURCE, "foo"}},
				{"$P{foo}", new Object[]{JRExpressionChunk.TYPE_PARAMETER, "foo"}},
				{"$F{foo}", new Object[]{JRExpressionChunk.TYPE_FIELD, "foo"}},
				{"$V{foo}", new Object[]{JRExpressionChunk.TYPE_VARIABLE, "foo"}},
				{"$V{fo\no}", new Object[]{JRExpressionChunk.TYPE_VARIABLE, "fo\no"}},
				{"bar$V{foo}", new Object[]{JRExpressionChunk.TYPE_TEXT, "bar", JRExpressionChunk.TYPE_VARIABLE, "foo"}},
				{"$V{foo}bar", new Object[]{JRExpressionChunk.TYPE_VARIABLE, "foo", JRExpressionChunk.TYPE_TEXT, "bar"}},
				{"moo$V{foo}bar", new Object[]{JRExpressionChunk.TYPE_TEXT, "moo", JRExpressionChunk.TYPE_VARIABLE, "foo", JRExpressionChunk.TYPE_TEXT, "bar"}},
				{"$V{foo} + $F{bar}", new Object[]{JRExpressionChunk.TYPE_VARIABLE, "foo", JRExpressionChunk.TYPE_TEXT, " + ", JRExpressionChunk.TYPE_FIELD, "bar",}},
				{"$V{foo} \n+ $F{bar}", new Object[]{JRExpressionChunk.TYPE_VARIABLE, "foo", JRExpressionChunk.TYPE_TEXT, " \n+ ", JRExpressionChunk.TYPE_FIELD, "bar",}},
				{"$F{$foo}", new Object[]{JRExpressionChunk.TYPE_FIELD, "$foo"}},
				{"$F{foo", new Object[]{JRExpressionChunk.TYPE_TEXT, "$F{foo"}},
				{"$X{foo}", new Object[]{JRExpressionChunk.TYPE_TEXT, "$X{foo}"}},
				{"$F{foo}bar}", new Object[]{JRExpressionChunk.TYPE_FIELD, "foo", JRExpressionChunk.TYPE_TEXT, "bar}"}},
				{"$$P{foo}", new Object[]{JRExpressionChunk.TYPE_TEXT, "$P{foo}"}},
				{"$P{foo} + \"$$P{bar}\"", new Object[]{JRExpressionChunk.TYPE_PARAMETER, "foo", JRExpressionChunk.TYPE_TEXT, " + \"$P{bar}\""}},
				{"$P{foo} + \"$$P{bar}\" + $V{var}", new Object[]{JRExpressionChunk.TYPE_PARAMETER, "foo", JRExpressionChunk.TYPE_TEXT, " + \"$P{bar}\" + ", JRExpressionChunk.TYPE_VARIABLE, "var"}},
				{"$R{}", new Object[]{JRExpressionChunk.TYPE_RESOURCE, ""}},
		};
	}

}
