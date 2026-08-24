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
package net.sf.jasperreports.components.list;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.component.FillPrepareResult;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillCloneable;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;

/**
 * Vertical fill list component implementation.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class VerticalFillList extends BaseFillList
{
	
	private static final Log log = LogFactory.getLog(VerticalFillList.class);
	
	private final FillListContents listContents;
	
	public VerticalFillList(ListComponent component, JRFillObjectFactory factory) throws JRException
	{
		super(component, factory);
		
		JRFillObjectFactory datasetFactory = new JRFillObjectFactory(factory, 
				createDatasetExpressionEvaluator());
		this.listContents = new FillListContents(component.getContents(), datasetFactory);
	}

	protected VerticalFillList(VerticalFillList list, JRFillCloneFactory factory)
	{
		super(list, factory);
		
		this.listContents = new FillListContents(list.listContents, factory);
	}

	@SuppressWarnings("deprecation")
	@Override
	public FillPrepareResult prepare(int availableHeight)
	{
		return prepare(availableHeight, true);
	}

	@Override
	public FillPrepareResult prepare(int availableHeight, boolean isOverflowAllowed)
	{
		createPrintFrame();
		try
		{
			boolean hadData = false;
			boolean overflow = false;
			
			if (filling)
			{
				// continuing after an overflow
				if (log.isDebugEnabled())
				{
					log.debug("Continuing list after overflow");
				}
				
				hadData = true;
				overflow = fillContents(availableHeight);
			}
			else
			{
				if (log.isDebugEnabled())
				{
					log.debug("Starting list rendering");
				}
				
				if (fillStarted)
				{
					// if already started and finished, rewind the data source
					if (log.isDebugEnabled())
					{
						log.debug("List reprinted, rewinding data source");
					}
					
					datasetRun.rewind();
				}
				
				datasetRun.start();
				fillStarted = true;
			}
			
			while (!overflow && datasetRun.next())
			{
				hadData = true;
				
				listContents.evaluateContents();
				overflow = fillContents(availableHeight);
			}

			if (overflow)
			{
				if (log.isDebugEnabled())
				{
					log.debug("List has overflowed");
				}
				
				// set the filling flag so that we know that we are continuing
				filling = true;
				return FillPrepareResult.printStretch(availableHeight, overflow);
			}
			else
			{
				// list has completed;
				
				if (log.isDebugEnabled())
				{
					log.debug("List has completed rendering");
				}
				
				filling = false;
				datasetRun.end();
				
				datasetRun.copyReturnValues();
				
				if (!hadData)
				{
					//if no data, set as no print
					return FillPrepareResult.NO_PRINT_NO_OVERFLOW;
				}

				return FillPrepareResult.printStretch(printFrame.getHeight(), false);
			}
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	protected boolean fillContents(int availableHeight) throws JRException
	{
		boolean overflow;
		int contentsAvailableHeight = availableHeight - printFrame.getHeight();
		if (contentsAvailableHeight < listContents.getHeight())
		{
			overflow = true;
		}
		else
		{
			listContents.prepare(contentsAvailableHeight);
			listContents.finalizeElementPositions();
			listContents.fillElements(
					new AppendingPrintElementContainer(printFrame));
			
			overflow = listContents.willOverflow();
		}
		return overflow;
	}

	@Override
	public JRFillCloneable createClone(JRFillCloneFactory factory)
	{
		return new VerticalFillList(this, factory);
	}

	@Override
	public void rewind()
	{
		if (fillStarted)
		{
			try
			{
				listContents.rewind();
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		
		super.rewind();
	}
}
