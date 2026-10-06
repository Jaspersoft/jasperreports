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
package net.sf.jasperreports.engine.part;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jasperreports.engine.BookmarkHelper;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRStyle;
import net.sf.jasperreports.engine.PrintPart;
import net.sf.jasperreports.engine.fill.BaseReportFiller;
import net.sf.jasperreports.engine.fill.DelayedFillActions;
import net.sf.jasperreports.engine.fill.JREvaluationTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillPartPrintOutput implements PartPrintOutput
{
	private static final Log log = LogFactory.getLog(FillPartPrintOutput.class);

	private TreeMap<Integer, PrintPart> parts;
	private List<JRPrintPage> pages;
	private DelayedFillActions delayedActions;
	private BookmarkHelper bookmarkHelper;
	private LinkedHashMap<String, JRStyle> styles;
	private LinkedHashSet<JROrigin> origins;

	public FillPartPrintOutput(BaseReportFiller filler)
	{
		parts = new TreeMap<>();
		pages = new ArrayList<>();
		
		delayedActions = new DelayedFillActions(filler);
		delayedActions.createDelayedEvaluationTime(JREvaluationTime.EVALUATION_TIME_MASTER);
		if (log.isDebugEnabled())
		{
			log.debug(this + " created delayed actions " + delayedActions.getId());
		}
		
		if (filler.getFillContext().isCollectingBookmarks())
		{
			bookmarkHelper = new BookmarkHelper(true);
		}
		
		styles = new LinkedHashMap<>();
		origins = new LinkedHashSet<>();
	}

	@Override
	public void startPart(PrintPart printPart, FillingPrintPart fillingPart)
	{
		int startIndex = pages.size();
		parts.put(startIndex, printPart);
		
		if (log.isDebugEnabled())
		{
			log.debug("added part " + printPart.getName() + " at index " + startIndex);
		}
	}

	@Override
	public void addPage(JRPrintPage page, DelayedFillActions delayedActionsSource)
	{
		int pageIndex = pages.size();
		if (log.isDebugEnabled())
		{
			log.debug("adding part page at index " + pageIndex);
		}
		
		pages.add(page);
		if (bookmarkHelper != null)
		{
			bookmarkHelper.addBookmarks(page, pageIndex);
		}
		
		delayedActions.moveMasterEvaluations(delayedActionsSource, page, pageIndex);
	}

	@Override
	public JRPrintPage getPage(int pageIndex)
	{
		return pages.get(pageIndex);
	}

	@Override
	public void pageUpdated(int partPageIndex)
	{
		//NOP
	}

	@Override
	public void append(FillPartPrintOutput output)
	{
		int pageOffset = pages.size();
		for (Map.Entry<Integer, PrintPart> partEntry : output.parts.entrySet())
		{
			parts.put(pageOffset + partEntry.getKey(), partEntry.getValue());
		}
		
		for (ListIterator<JRPrintPage> it = output.pages.listIterator(); it.hasNext();)
		{
			JRPrintPage page = it.next();
			pages.add(page);
			delayedActions.moveMasterEvaluations(output.delayedActions, page, pageOffset + it.previousIndex());
		}

		if (bookmarkHelper != null && output.bookmarkHelper != null)
		{
			// adding in bulk
			bookmarkHelper.appendBookmarks(output.bookmarkHelper, pageOffset);
		}
		
		addStyles(output.styles.values());
		addOrigins(output.origins);
	}

	public TreeMap<Integer, PrintPart> getParts()
	{
		return parts;
	}

	public List<JRPrintPage> getPages()
	{
		return pages;
	}

	public DelayedFillActions getDelayedActions()
	{
		return delayedActions;
	}

	@Override
	public BookmarkHelper getBookmarkHelper()
	{
		return bookmarkHelper;
	}

	@Override
	public void addStyles(Collection<JRStyle> stylesList)
	{
		for (JRStyle style : stylesList)
		{
			if (styles.containsKey(style.getName()))
			{
				log.debug("style " + style.getName() + " already present");
			}
			else
			{
				if (log.isDebugEnabled())
				{
					log.debug("added style " + style.getName());
				}
				
				styles.put(style.getName(), style);
			}
		}
	}

	public Collection<JRStyle> getStyles()
	{
		return styles.values();
	}

	@Override
	public void addOrigins(Collection<JROrigin> origins)
	{
		this.origins.addAll(origins);
	}

	public Collection<JROrigin> getOrigins()
	{
		return origins;
	}
}
