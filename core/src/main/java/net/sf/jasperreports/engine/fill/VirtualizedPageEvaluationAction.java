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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRVirtualizable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Delayed evaluation action that devirtualizes a set of elements in order to
 * evaluate one or several of them.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class VirtualizedPageEvaluationAction implements EvaluationBoundAction
{
	private static final Log log = LogFactory.getLog(VirtualizedPageEvaluationAction.class);
	
	private final JRVirtualizable<?> object;
	private final int sourceId;

	public VirtualizedPageEvaluationAction(JRVirtualizable<?> object, int sourceId)
	{
		this.object = object;
		this.sourceId = sourceId;
	}

	@Override
	public void execute(BoundActionExecutionContext executionContext)
			throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug(this + " Resolving delayed evaluations for virtualized page " + executionContext.getCurrentPageIndex()
					+ " on " + executionContext.getEvaluationTime());
		}
		
		// this forces devirtualization and queues the element evaluations via setElementEvaluationsToPage
		object.ensureVirtualData();
	}

	public int getSourceId()
	{
		return sourceId;
	}
}