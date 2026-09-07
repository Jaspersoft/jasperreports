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
package net.sf.jasperreports.engine.util.text;

import java.lang.Character.UnicodeBlock;
import java.util.HashSet;
import java.util.Set;

/**
 * Legacy complex text layout implementation.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class LegacyTextLayoutAssessor implements TextLayoutAssessor
{

	protected static final int COMPEX_LAYOUT_START_CHAR = 0x0300;// got this from sun.font.FontUtilities
	protected static final int COMPEX_LAYOUT_END_CHAR = 0x206F;// got this from sun.font.FontUtilities
	
	protected static final Set<Character.UnicodeBlock> simpleLayoutBlocks;
	static
	{
		// white list of Unicode blocks that have simple text layout
		simpleLayoutBlocks = new HashSet<>();
		// got these from sun.font.FontUtilities, but the list is not exhaustive
		simpleLayoutBlocks.add(Character.UnicodeBlock.GREEK);
		simpleLayoutBlocks.add(Character.UnicodeBlock.CYRILLIC);
		simpleLayoutBlocks.add(Character.UnicodeBlock.CYRILLIC_SUPPLEMENTARY);
		simpleLayoutBlocks.add(Character.UnicodeBlock.ARMENIAN);
		simpleLayoutBlocks.add(Character.UnicodeBlock.SYRIAC);
		simpleLayoutBlocks.add(Character.UnicodeBlock.THAANA);
		simpleLayoutBlocks.add(Character.UnicodeBlock.MYANMAR);
		simpleLayoutBlocks.add(Character.UnicodeBlock.GEORGIAN);
		simpleLayoutBlocks.add(Character.UnicodeBlock.ETHIOPIC);
		simpleLayoutBlocks.add(Character.UnicodeBlock.TAGALOG);
		simpleLayoutBlocks.add(Character.UnicodeBlock.MONGOLIAN);
		simpleLayoutBlocks.add(Character.UnicodeBlock.LATIN_EXTENDED_ADDITIONAL);
		simpleLayoutBlocks.add(Character.UnicodeBlock.GREEK_EXTENDED);
	}

	@Override
	public boolean hasComplexLayout(char[] chars)
	{
		UnicodeBlock prevBlock = null;
		for (char ch : chars)
		{
			if (ch >= COMPEX_LAYOUT_START_CHAR && ch <= COMPEX_LAYOUT_END_CHAR)
			{
				//FIXME use icu4j or CharPredicateCache
				UnicodeBlock chBlock = Character.UnicodeBlock.of(ch);
				if (chBlock == null)
				{
					// being conservative
					return true;
				}
				
				// if the same block as the previous block, avoid going to the hash set
				// this could offer some speed improvement
				if (prevBlock != chBlock)
				{
					prevBlock = chBlock;
					
					if (!simpleLayoutBlocks.contains(chBlock))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

}
