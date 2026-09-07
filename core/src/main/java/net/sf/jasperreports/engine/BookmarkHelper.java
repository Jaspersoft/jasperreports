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


/*
 * Contributors:
 * John Bindel - jbindel@users.sourceforge.net 
 */

package net.sf.jasperreports.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.base.BasePrintBookmark;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class BookmarkHelper
{
	private BookmarkStack bookmarkStack;

	public BookmarkHelper(boolean isCollapseMissingBookmarkLevels)
	{
		bookmarkStack = new BookmarkStack(isCollapseMissingBookmarkLevels);
	}
	
	public void addBookmarks(JRPrintPage page, int pageIndex)
	{
		bookmarkStack.addBookmarks(page.getElements(), pageIndex, null);
	}
	
	public void updateBookmark(JRPrintElement element)
	{
		bookmarkStack.updateBookmark(element);
	}
	
	public List<PrintBookmark> getRootBookmarks()
	{
		return bookmarkStack.getRootBookmarks();
	}

	public void appendBookmarks(BookmarkHelper bookmarkHelper, int pageOffset)
	{
		bookmarkStack.appendBookmarks(bookmarkHelper.bookmarkStack, pageOffset);
	}
	
	public void addBookmark(BasePrintBookmark bookmark, int pageOffset)
	{
		bookmarkStack.addBookmark(bookmark, pageOffset);
	}
	
	public BookmarkIterator bookmarkIterator()
	{
		return bookmarkStack.bookmarkIterator();
	}
	
	public boolean hasBookmarks()
	{
		return bookmarkStack.hasBookmarks();
	}
}


/**
 * 
 */
class BookmarkStack
{
	private static final Log log = LogFactory.getLog(BookmarkStack.class); 
	
	LinkedList<BasePrintBookmark> stack;
	boolean isCollapseMissingBookmarkLevels;
	Map<PrintElementId, BasePrintBookmark> updateableBookmarks;

	public BookmarkStack(boolean isCollapseMissingBookmarkLevels)
	{
		stack = new LinkedList<>();
		this.isCollapseMissingBookmarkLevels = isCollapseMissingBookmarkLevels;
		
		//root bookmark is only used as container for root node
		BasePrintBookmark root = new BasePrintBookmark(null, 0, null, 0);
		push(root);
		
		updateableBookmarks = new HashMap<>();
	}

	public boolean hasBookmarks()
	{
		List<PrintBookmark> bookmarks = stack.getFirst().getBookmarks();
		return bookmarks != null && !bookmarks.isEmpty();
	}

	public void push(BasePrintBookmark bookmark)
	{
		stack.add(bookmark);
	}

	public BasePrintBookmark pop()
	{
		return stack.removeLast();
	}

	public BasePrintBookmark peek()
	{
		return stack.getLast();
	}

	protected BasePrintBookmark addBookmark(int level, String label, int pageIndex, String elementAddress)
	{
		BasePrintBookmark parent = this.peek();
		// searching for parent
		while (parent.getLevel() >= level)
		{
			this.pop();
			parent = this.peek();
		}

		if (!isCollapseMissingBookmarkLevels)
		{
			PrintBookmark parentPrintBookmark = parent;
			// creating empty bookmarks in order to preserve the bookmark level
			for (int i = parent.getLevel() + 1; i < level; ++i)
			{
				BasePrintBookmark emptyBookmark = createBookmark(parent, "", 
						parentPrintBookmark.getPageIndex(), parentPrintBookmark.getElementAddress(),
						parent.getLevel() + 1);
				this.push(emptyBookmark);
				parent = emptyBookmark;
			}
		}

		BasePrintBookmark bookmark = createBookmark(parent, label, pageIndex, elementAddress,
				isCollapseMissingBookmarkLevels ? level : parent.getLevel() + 1);
		this.push(bookmark);
		return bookmark;
	}
	
	protected BasePrintBookmark createBookmark(BasePrintBookmark parent, String label, 
			int pageIndex, String elementAddress, int level)
	{
		BasePrintBookmark printBookmark = new BasePrintBookmark(label, pageIndex, elementAddress, level);
		
		if (parent != null)
		{
			parent.addBookmark(printBookmark);
		}
		
		return printBookmark;
	}
	
	protected void addBookmarks(List<JRPrintElement> elements, int pageIndex, String elementAddress)
	{
		if (elements != null)
		{
			elementAddress = elementAddress == null ? "" : elementAddress;

			int i = 0;
			for (JRPrintElement element : elements)
			{
				if (element instanceof JRPrintFrame)
				{
					addBookmarks(((JRPrintFrame)element).getElements(), pageIndex, elementAddress + (elementAddress.length() == 0 ? "" : "_") + i + "_");
				}
				else if (element instanceof JRPrintAnchor)
				{
					JRPrintAnchor anchor = (JRPrintAnchor)element;
					int level = anchor.getBookmarkLevel();
					if (level != JRAnchor.NO_BOOKMARK)
					{
						String anchorName = anchor.getAnchorName();
						if (anchorName == null)
						{
							anchorName = element instanceof JRPrintText ? ((JRPrintText)element).getFullText() : null;
						}
						BasePrintBookmark bookmark = addBookmark(level, anchorName, pageIndex, elementAddress + i);
						
						// we're keeping a map with bookmarks for elements with late evaluation
						// not placing bookmarks for other elements in the map to save some space
						if (anchorName == null)// the element could have late evaluation; there's no exact test for it
						{
							PrintElementId elementId = PrintElementId.forElement(element);
							updateableBookmarks.put(elementId, bookmark);
						}
					}
				}
				i++;
			}
		}
	}

	public void updateBookmark(JRPrintElement element)
	{
		if (element instanceof JRPrintAnchor)
		{
			JRPrintAnchor anchor = (JRPrintAnchor) element;
			int level = anchor.getBookmarkLevel();
			if (level != JRAnchor.NO_BOOKMARK)
			{
				PrintElementId elementId = PrintElementId.forElement(element);
				BasePrintBookmark bookmark = updateableBookmarks.get(elementId);//FIXME remove from the map?
				if (bookmark == null)
				{
					if (log.isDebugEnabled())
					{
						// this can happen when the element has a delayed evaluation on the same page
						log.debug("Cound not find bookmark for " + elementId + " to update");
					}
				}
				else
				{
					String anchorName = anchor.getAnchorName();
					if (anchorName == null)
					{
						anchorName = element instanceof JRPrintText ? ((JRPrintText)element).getFullText() : null;
					}
					bookmark.setLabel(anchorName);
				}
			}
		}
	}
	
	protected List<PrintBookmark> getRootBookmarks()
	{
		return stack.getFirst().getBookmarks();
	}

	protected void addBookmark(BasePrintBookmark bookmark, int pageOffset)
	{
		addBookmark(bookmark.getLevel(), bookmark.getLabel(), 
				pageOffset + bookmark.getPageIndex(), bookmark.getElementAddress());
	}
	
	public void appendBookmarks(BookmarkStack bookmarkStack, int pageOffset)
	{
		// we're creating new objects though we could add the existing ones and update the page indices
		// depth first to preserve parent/children relations in the new objects
		for (BookmarkIterator iterator = bookmarkStack.bookmarkIterator(); iterator.hasBookmark(); iterator.next())
		{
			BasePrintBookmark bookmark = iterator.bookmark();
			addBookmark(bookmark, pageOffset);
		}
	}

	public BookmarkIterator bookmarkIterator()
	{
		return new BookmarkIterator(stack.getFirst());
	}
}