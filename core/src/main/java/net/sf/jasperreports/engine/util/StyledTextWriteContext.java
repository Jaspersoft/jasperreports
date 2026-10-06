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
package net.sf.jasperreports.engine.util;

import java.text.AttributedCharacterIterator.Attribute;
import java.util.Map;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class StyledTextWriteContext
{
	private StyledTextListInfo[] prevListStack;
	private StyledTextListItemInfo prevListItem;
	private StyledTextListInfo[] listStack;
	private StyledTextListItemInfo listItem;
	private boolean isFirstRun = true;
	private boolean runTextEndedWithNewLine = false;
	private boolean prevRunTextEndedWithNewLine = false;
	private boolean runTextStartsWithNewLine = false;

	private int prevDepth = 0;
	private int newDepth = 0;
	private int commonListDepth = 0;
	
	private boolean isProcessingCuts = false;

	public StyledTextWriteContext()
	{
	}
	
	public StyledTextWriteContext(boolean isProcessingCuts)
	{
		this.isProcessingCuts = isProcessingCuts;
	}
	
	public boolean isFirstRun() 
	{
		return isFirstRun;
	}

	public StyledTextListInfo getPrevList(int index) 
	{
		return prevListStack == null || prevListStack.length == 0 ? null : prevListStack[index];
	}

	public StyledTextListInfo getPrevList() 
	{
		return prevListStack == null || prevListStack.length == 0 ? null : prevListStack[prevListStack.length - 1];
	}

	public StyledTextListInfo getList(int index) 
	{
		return listStack == null || listStack.length == 0 ? null : listStack[index];
	}

	public StyledTextListInfo getList() 
	{
		return listStack == null || listStack.length == 0 ? null : listStack[listStack.length - 1];
	}

	public StyledTextListItemInfo getListItem() 
	{
		return listItem;
	}

	public boolean prevListItemEndedWithNewLine() 
	{
		return prevRunTextEndedWithNewLine;
	}

	public boolean listItemStartsWithNewLine() 
	{
		return runTextStartsWithNewLine;
	}

	public int getPrevDepth()
	{
		return prevDepth;
	}

	public int getDepth() 
	{
		return newDepth;
	}

	public int getCommonListDepth() 
	{
		return commonListDepth;
	}

	public boolean isListStart()
	{
		return (commonListDepth + 1 == newDepth);
	}

	public boolean isListEnd()
	{
		return (commonListDepth + 1 == prevDepth);
	}

	public boolean isListItemStart() 
	{
		return 
			(listItem != null // there is a new li
			&& listItem != StyledTextListItemInfo.NO_LIST_ITEM_FILLER // it is indeed a list item and not a filler
			&& listItem != prevListItem // it is different than the previous one
			&& (prevDepth <= newDepth // it is of a deeper level (this condition is probably redundant with respect to the following two)
				|| (commonListDepth == newDepth 
					&& (!prevListStack[commonListDepth].hasParentLi() // list was between li
						|| prevListStack[commonListDepth].atLiEnd())) // list was right at the end of li
				|| commonListDepth < newDepth)
			); // so opening it
	}

	public boolean isListItemEnd() 
	{
		return
			(prevListItem != null  // there was a li
			&& prevListItem != StyledTextListItemInfo.NO_LIST_ITEM_FILLER // it was indeed a list item and not a filler
			&& prevListItem != listItem  // was not the same as the new one
			&& (prevDepth >= newDepth // was of deeper level (this condition is probably redundant with respect to the following two)
				|| (commonListDepth == prevDepth 
					&& (!listStack[commonListDepth].hasParentLi() // new list is between li
						|| listStack[commonListDepth].atLiStart())) // new list is right at the start of li
				|| commonListDepth < prevDepth)
			); // so closing it
	}

	public boolean isListItemChange()
	{
		return listItem != prevListItem;
	}

	public void next(Map<Attribute,Object> attributes)
	{
		this.prevListStack = this.listStack;
		this.prevListItem = this.listItem;
		if (attributes == null)
		{
			this.listStack = null;
			this.listItem = null;
		}
		else
		{
			this.listStack = (StyledTextListInfo[])attributes.get(JRTextAttribute.HTML_LIST);
			this.listItem = (StyledTextListItemInfo)attributes.get(JRTextAttribute.HTML_LIST_ITEM);
		}
		
		prevDepth = prevListStack == null ? 0 : prevListStack.length;
		newDepth = listStack == null ? 0 : listStack.length;
		
		int minDepth = Math.min(prevDepth, newDepth);
		commonListDepth = 0;
		
		while (commonListDepth < minDepth)
		{
			if (listStack[commonListDepth] != prevListStack[commonListDepth])
			{
				break;
			}
			commonListDepth++;
		}
		
		if (isProcessingCuts)
		{
			if (isFirstRun)
			{
				if (this.listStack != null)
				{
					for (StyledTextListInfo list : listStack)
					{
						list.setCutStart(list.getStart() + list.getItemIndex());
					}
				}
			}

			if (isListItemStart() || getListItem() == StyledTextListItemInfo.NO_LIST_ITEM_FILLER)
			{
				StyledTextListInfo list = getList();
				if (list != null) // set proper item index also for non ordered lists, why not? && list.ordered())
				{
					if (getListItem() == StyledTextListItemInfo.NO_LIST_ITEM_FILLER)
					{
						list.setItemIndex(list.getItemIndex() + 1);
					}
					else
					{
						list.setItemIndex(getListItem().getItemIndex());
					}
				}
			}
		}
		
		this.isFirstRun = false;
	}

	public void next(
		Map<Attribute,Object> attributes,
		String runText
		) 
	{
		next(attributes);
		
		this.prevRunTextEndedWithNewLine = runTextEndedWithNewLine;
		this.runTextEndedWithNewLine = runText == null ? false : runText.endsWith("\n");
		
		this.runTextStartsWithNewLine = runText == null ? false : runText.startsWith("\n"); 
	}

	public void writeLists(StyledTextListWriter writer)
	{
		if (writer == null)
		{
			return;
		}
		
		if (isListItemEnd())
		{
			writer.endLi();
		}
		
		for (int i = getPrevDepth() - 1; i > getCommonListDepth(); i--)
		{
			StyledTextListInfo prevList = getPrevList(i);
			if (prevList.ordered())
			{
				writer.endOl();
			}
			else
			{
				writer.endUl();
			}
			if (prevList.hasParentLi())
			{
				writer.endLi();
			}
		}

		if (getPrevDepth() > getCommonListDepth())
		{
			StyledTextListInfo prevList = getPrevList(getCommonListDepth());
			if (prevList.ordered())
			{
				writer.endOl();
			}
			else
			{
				writer.endUl();
			}
			if (prevList.hasParentLi() && prevList.atLiEnd())
			{
				writer.endLi();
			}
		}

		if (getCommonListDepth() < getDepth())
		{
			StyledTextListInfo list = getList(getCommonListDepth());
			if (list.hasParentLi() && list.atLiStart())
			{
				writer.startLi(true);
			}
			if (list.ordered())
			{
				writer.startOl(list.getType(), list.getCutStart());
			}
			else
			{
				writer.startUl();
			}
		}

		for (int i = getCommonListDepth() + 1; i < getDepth(); i++)
		{
			StyledTextListInfo list = getList(i);
			if (list.hasParentLi())
			{
				writer.startLi(true);
			}
			if (list.ordered())
			{
				writer.startOl(list.getType(), list.getCutStart());
			}
			else
			{
				writer.startUl();
			}
		}

		if (isListItemStart())
		{
			writer.startLi(getListItem().noBullet());
		}
	}
}
