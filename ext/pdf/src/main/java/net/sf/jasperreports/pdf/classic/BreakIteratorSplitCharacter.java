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
package net.sf.jasperreports.pdf.classic;

import java.text.BreakIterator;
import java.text.CharacterIterator;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.pdf.PdfReportConfiguration;

import com.lowagie.text.SplitCharacter;
import com.lowagie.text.pdf.PdfChunk;


/**
 * Implementation of {@link com.lowagie.text.SplitCharacter SplitCharacter} that
 * uses the same logic as AWT to break texts into lines.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * 
 * @see PdfReportConfiguration#isForceLineBreakPolicy()
 */
public class BreakIteratorSplitCharacter implements SplitCharacter
{

	private char[] chars;
	private int end;
	private boolean[] boundary;
	private int lastBoundary;
	private final BreakIterator breakIter;
	
	public BreakIteratorSplitCharacter()
	{
		this(BreakIterator.getLineInstance());
	}
	
	public BreakIteratorSplitCharacter(BreakIterator breakIter)
	{
		this.breakIter = breakIter;
	}

	@Override
	public boolean isSplitCharacter(int startIdx, int current, int endIdx, char[] cc, PdfChunk[] ck)
	{
		++current;
		if (current == endIdx)
		{
			return false;
		}

		if (!(chars == cc && this.end == endIdx))
		{
			chars = cc;
			this.end = endIdx;

			breakIter.setText(new ArrayCharIterator(cc, 0, endIdx));

			boundary = new boolean[endIdx + 1];

			lastBoundary = breakIter.first();
			if (lastBoundary != BreakIterator.DONE)
			{
				boundary[lastBoundary] = true;
			}
		}

		while (current > lastBoundary)
		{
			lastBoundary = breakIter.next();

			if (lastBoundary == BreakIterator.DONE)
			{
				lastBoundary = Integer.MAX_VALUE;
			}
			else
			{
				boundary[lastBoundary] = true;
			}
		}

		return boundary[current]
				|| currentChar(current - 1, cc, ck) <= ' ';
	}

	protected char currentChar(int current, char[] cc, PdfChunk[] ck)
	{
		char currentCh = cc[current];
		if (ck != null)
		{
			PdfChunk chunk = ck[Math.min(current, ck.length - 1)];
			currentCh = (char)chunk.getUnicodeEquivalent(currentCh);
		}
		return currentCh;
	}
	
	protected static class ArrayCharIterator implements CharacterIterator
	{
		public static final String EXCEPTION_MESSAGE_KEY_INVALID_INDEX = "util.array.char.iterator.invalid.index";

		private char[] chars;
		private int start;
		private int end;
		private int curr;

		public ArrayCharIterator(char[] chars, int start, int end)
		{
			this.chars = chars;
			this.start = start;
			this.end = end;
		}

		@Override
		public char first()
		{
			curr = start;
			return current();
		}

		@Override
		public char last()
		{
			if (end == start)
			{
				curr = end;
			}
			else
			{
				curr = end - 1;
			}
			return current();
		}

		@Override
		public char setIndex(int position)
		{
			if (position < start || position > end)
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_INVALID_INDEX,
						new Object[]{position, start, end});
			}
			curr = position;
			return current();
		}

		@Override
		public char current()
		{
			if (curr < start || curr >= end)
			{
				return DONE;
			}
			return chars[curr];
		}

		@Override
		public char next()
		{
			if (curr >= end - 1)
			{
				curr = end;
				return DONE;
			}
			curr++;
			return chars[curr];
		}

		@Override
		public char previous()
		{
			if (curr <= start)
			{
				return DONE;
			}
			curr--;
			return chars[curr];
		}

		@Override
		public int getBeginIndex()
		{
			return start;
		}

		@Override
		public int getEndIndex()
		{
			return end;
		}

		@Override
		public int getIndex()
		{
			return curr;
		}

		@Override
		public Object clone()
		{
			try
			{
				return super.clone();
			}
			catch (CloneNotSupportedException e)
			{
				throw new JRRuntimeException(e);
			}
		}
	}

}
