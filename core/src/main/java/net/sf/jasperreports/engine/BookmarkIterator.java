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
package net.sf.jasperreports.engine;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import net.sf.jasperreports.engine.base.BasePrintBookmark;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BookmarkIterator
{
	private Deque<BasePrintBookmark> stack = new LinkedList<>();
	
	public BookmarkIterator(BasePrintBookmark root)
	{
		// populating the queue with root bookmarks
		pushChildren(root);
	}
	
	private void pushChildren(BasePrintBookmark bookmark)
	{
		List<PrintBookmark> children = bookmark.getBookmarks();
		if (children != null)
		{
			// pushing in reverse order to preserve children iteration order
			for (ListIterator<PrintBookmark> it = children.listIterator(children.size()); it.hasPrevious();)
			{
				BasePrintBookmark child = (BasePrintBookmark) it.previous();
				stack.push(child);
			}
		}
	}

	public boolean hasBookmark()
	{
		return !stack.isEmpty();
	}
	
	public BasePrintBookmark bookmark()
	{
		return stack.peek();
	}

	public void next()
	{
		BasePrintBookmark bookmark = stack.pop();
		pushChildren(bookmark);
	}
	
}