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
package net.sf.jasperreports.engine.fill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVirtualizable;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintElementVisitor;
import net.sf.jasperreports.engine.base.JRVirtualPrintPage;
import net.sf.jasperreports.engine.base.VirtualElementsData;
import net.sf.jasperreports.engine.base.VirtualizableElementList;
import net.sf.jasperreports.engine.base.VirtualizablePageElements;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.util.LinkedMap;
import net.sf.jasperreports.engine.util.UniformPrintElementVisitor;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DelayedFillActions implements VirtualizationListener<VirtualElementsData>
{
	private static final Log log = LogFactory.getLog(DelayedFillActions.class);
	
	protected static final String FILL_CACHE_KEY_ID = DelayedFillActions.class.getName() + "#id";
	public static final String EXCEPTION_MESSAGE_KEY_ELEMENT_NOT_FOUND = "fill.delayed.fill.actions.element.not.found";
	
	private final int id;
	private final BaseReportFiller reportFiller;
	private final JRFillContext fillContext;

	// we can use HashMap because the map is initialized in the beginning and doesn't change afterwards
	private final HashMap<JREvaluationTime, LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> actionsMap;
	
	private Map<Integer, JRFillElement> fillElements;
	private Set<Integer> masterFillElementIds;
	
	private Set<JRVirtualizationContext> listenedContexts;
	
	private Set<Integer> transferredIds;
	
	public DelayedFillActions(BaseReportFiller reportFiller)
	{
		this.id = assignId(reportFiller);
		this.reportFiller = reportFiller;
		this.fillContext = reportFiller.fillContext;
		this.actionsMap = new HashMap<>();
		this.fillElements = new HashMap<>();
		this.masterFillElementIds = new HashSet<>();
		this.listenedContexts = new HashSet<>();
	}
	
	private static int assignId(BaseReportFiller reportFiller)
	{
		AtomicInteger counter = reportFiller.fillContext.getFillCache(FILL_CACHE_KEY_ID, AtomicInteger::new);
		return counter.incrementAndGet();
	}

	public int getId()
	{
		return id;
	}

	public void createDelayedEvaluationTime(JREvaluationTime evaluationTime)
	{
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> evaluationActions = new LinkedHashMap<>();
		actionsMap.put(evaluationTime, evaluationActions);
	}

	protected void registerPage(JRPrintPage page)
	{
		if (page instanceof JRVirtualPrintPage)
		{
			JRVirtualizationContext virtualizationContext = ((JRVirtualPrintPage) page).getVirtualizationContext();
			if (!listenedContexts.contains(virtualizationContext))
			{
				//FIXMEBOOK part reports use a single context which will collect all listeners
				virtualizationContext.addListener(this);
				listenedContexts.add(virtualizationContext);
				
				if (log.isDebugEnabled())
				{
					log.debug(id + " registered virtualization listener on " + virtualizationContext);
				}
			}
		}
	}
	
	public void dispose()
	{
		for (JRVirtualizationContext virtualizationContext : listenedContexts)
		{
			virtualizationContext.removeListener(this);
			
			if (log.isDebugEnabled())
			{
				log.debug(id + " unregistered virtualization listener on " + virtualizationContext);
			}
		}
	}
	
	public void addDelayedAction(JRFillElement element, JRPrintElement printElement, JREvaluationTime evaluationTime, FillPageKey pageKey)
	{
		registerFillElement(element, evaluationTime);
		
		ElementEvaluationAction action = new ElementEvaluationAction(element, printElement);
		addDelayedAction(printElement, action, evaluationTime, pageKey);
	}

	protected void registerFillElement(JRFillElement element, JREvaluationTime evaluationTime)
	{
		int fillElementId = element.printElementOriginator.getSourceElementId();
		if (!fillElements.containsKey(fillElementId))
		{
			fillElements.put(fillElementId, element);
			
			if (evaluationTime.getType() == EvaluationTimeEnum.MASTER)
			{
				masterFillElementIds.add(fillElementId);
			}
		}
	}
	
	protected void registerTransferredId(int sourceId)
	{
		if (transferredIds == null)
		{
			transferredIds = new HashSet<>();
		}
		
		// duplicates are handled
		boolean added = transferredIds.add(sourceId);
		if (added && log.isDebugEnabled())
		{
			log.debug(id + " transferred id " + sourceId);
		}
	}
	
	public void addDelayedAction(Object actionKey, EvaluationBoundAction action, 
			JREvaluationTime evaluationTime, FillPageKey pageKey)
	{
		if (log.isDebugEnabled())
		{
			log.debug(id + " adding delayed action " + action + " at " + evaluationTime + ", key " + pageKey);
		}

		// get the pages map for the evaluation
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> pagesMap = actionsMap.get(evaluationTime);
		
		fillContext.lockVirtualizationContext();
		try
		{
			synchronized (pagesMap)
			{
				// get the actions map for the current page, creating if it does not yet exist
				LinkedMap<Object, EvaluationBoundAction> boundElementsMap = pageActionsMap(pagesMap, pageKey);
				
				// add the delayed element action to the map
				boundElementsMap.add(actionKey, action);
			}
		}
		finally
		{
			fillContext.unlockVirtualizationContext();
		}
	}

	protected LinkedMap<Object, EvaluationBoundAction> pageActionsMap(
			LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map, FillPageKey pageKey)
	{
		LinkedMap<Object, EvaluationBoundAction> pageMap = map.get(pageKey);
		if (pageMap == null)
		{
			pageMap = new LinkedMap<>();
			map.put(pageKey, pageMap);
			
			registerPage(pageKey.page);
		}
		return pageMap;
	}
	
	public void runActions(JREvaluationTime evaluationTime, byte evaluation) throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug(id + " running delayed actions on " + evaluationTime);
		}
		
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> pagesMap = actionsMap.get(evaluationTime);
		
		boolean hasEntry;
		do
		{
			reportFiller.checkInterrupted();
			
			// locking once per page so that we don't hold the lock for too long
			// (that would prevent async exporters from getting page data during a long resolve)
			fillContext.lockVirtualizationContext();
			try
			{
				synchronized (pagesMap)
				{
					// resolve a single page
					Iterator<Map.Entry<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> pagesIt = pagesMap.entrySet().iterator();
					hasEntry = pagesIt.hasNext();
					if (hasEntry)
					{
						Map.Entry<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> pageEntry = pagesIt.next();
						int pageIdx = pageEntry.getKey().index;
						
						if (log.isDebugEnabled())
						{
							log.debug(id + " running actions for page " + pageEntry.getKey().page + " at " + pageIdx);
						}
						
						StandardBoundActionExecutionContext context = new StandardBoundActionExecutionContext();
						context.setCurrentPageIndex(pageIdx);
						JasperPrint jasperPrint = fillContext.getMasterFiller().getJasperPrint();
						context.setTotalPages(jasperPrint.getPages().size());
						context.setEvaluationTime(evaluationTime);
						context.setExpressionEvaluationType(evaluation);
						
						LinkedMap<Object, EvaluationBoundAction> boundElementsMap = pageEntry.getValue();
						// execute the actions
						while (!boundElementsMap.isEmpty())
						{
							EvaluationBoundAction action = boundElementsMap.pop();
							action.execute(context);
						}
						
						// remove the entry from the pages map
						pagesIt.remove();
						
						// call the listener to signal that the page has been modified
						if (reportFiller.fillListener != null)
						{
							reportFiller.fillListener.pageUpdated(jasperPrint, pageIdx);
						}
					}
				}
			}
			finally
			{
				fillContext.unlockVirtualizationContext();
			}
		}
		while(hasEntry);
	}
	
	public boolean hasDelayedActions(JRPrintPage page)
	{
		FillPageKey pageKey = new FillPageKey(page);
		for (LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map : actionsMap.values())
		{
			fillContext.lockVirtualizationContext();
			try
			{
				synchronized (map)
				{
					LinkedMap<Object, EvaluationBoundAction> boundMap = map.get(pageKey);
					if (boundMap != null && !boundMap.isEmpty())
					{
						return true;
					}
				}
			}
			finally
			{
				fillContext.unlockVirtualizationContext();
			}
		}
		
		return false;
	}
	
	protected boolean hasMasterDelayedActions(JRPrintPage page)
	{
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> masterActions = actionsMap.get(JREvaluationTime.EVALUATION_TIME_MASTER);
		FillPageKey pageKey = new FillPageKey(page);
		
		fillContext.lockVirtualizationContext();
		try
		{
			synchronized (masterActions)//FIXME is this necessary?
			{
				LinkedMap<Object, EvaluationBoundAction> pageMasterActions = masterActions.get(pageKey);
				return pageMasterActions != null && !pageMasterActions.isEmpty();
			}
		}
		finally
		{
			fillContext.unlockVirtualizationContext();
		}
	}
	
	public void moveActions(FillPageKey fromKey, FillPageKey toKey)
	{
		if (log.isDebugEnabled())
		{
			log.debug(id + " moving actions from " + fromKey + " to " + toKey);
		}
		
		for (LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map : actionsMap.values())
		{
			fillContext.lockVirtualizationContext();
			try
			{
				synchronized (map)
				{
					LinkedMap<Object, EvaluationBoundAction> subreportMap = map.remove(fromKey);
					if (subreportMap != null && !subreportMap.isEmpty())
					{
						LinkedMap<Object, EvaluationBoundAction> masterMap = pageActionsMap(map, toKey);
						masterMap.addAll(subreportMap);
					}
				}
			}
			finally
			{
				fillContext.unlockVirtualizationContext();
			}
		}
	}

	@Override
	public void beforeExternalization(JRVirtualizable<VirtualElementsData> object)
	{
		JRVirtualizationContext virtualizationContext = object.getContext();
		virtualizationContext.lock();//already locked in ElementsBlock.beforeExternalization()
		try
		{
			writeElementEvaluations(object);
		}
		finally
		{
			virtualizationContext.unlock();
		}
	}
	
	protected void writeElementEvaluations(final JRVirtualizable<VirtualElementsData> object)
	{
		if (log.isDebugEnabled())
		{
			log.debug(id + " setting element evaluation for elements in " + object.getUID());
		}
		
		JRVirtualPrintPage page = ((VirtualizablePageElements) object).getPage();// ugly but needed for now
		FillPageKey pageKey = new FillPageKey(page);
		VirtualElementsData virtualData = object.getVirtualData();
		
		final Map<JREvaluationTime, Map<JRPrintElement, Integer>> evaluations = new LinkedHashMap<>();
		ElementEvaluationsCollector collector = (printElement, fillElement, evaluationTime) ->
		{
			// collection delayed evaluations for elements that are about to be externalized.
			// the evaluations store the ID of the fill elements in order to serialize the data.
			evaluations.computeIfAbsent(evaluationTime, k -> new LinkedHashMap<>())
				.put(printElement, fillElement.printElementOriginator.getSourceElementId());
		};
		
		doCollectElementEvaluations(page, virtualData.getElements(), collector, false);
		
		for (Map.Entry<JREvaluationTime, Map<JRPrintElement, Integer>> evalEntry : evaluations.entrySet())
		{
			JREvaluationTime evaluationTime = evalEntry.getKey();
			Map<JRPrintElement, Integer> elementEvaluations = evalEntry.getValue();
			
			// save the evaluations in the virtual data
			virtualData.setElementEvaluations(id, evaluationTime, elementEvaluations);
			
			// add an action for the page so that it gets devirtualized on resolveBoundElements
			VirtualizedPageEvaluationAction virtualizedAction = new VirtualizedPageEvaluationAction(object, id);
			LinkedMap<Object, EvaluationBoundAction> pageActions = actionsMap.get(evaluationTime).get(pageKey);
			pageActions.add(null, virtualizedAction);
			
			if (log.isDebugEnabled())
			{
				log.debug(id + " created action " + virtualizedAction);
			}
		}
	}

	@Override
	public void afterInternalization(JRVirtualizable<VirtualElementsData> object)
	{
		JRVirtualizationContext virtualizationContext = object.getContext();
		virtualizationContext.lock();
		try
		{
			readElementEvaluations(object);
		}
		finally
		{
			virtualizationContext.unlock();
		}
	}
	
	protected void readElementEvaluations(JRVirtualizable<VirtualElementsData> object)
	{
		JRVirtualPrintPage page = ((VirtualizablePageElements) object).getPage();// ugly but needed for now
		FillPageKey pageKey = new FillPageKey(page);
		
		for (Map.Entry<JREvaluationTime, LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> boundMapEntry : 
			actionsMap.entrySet())
		{
			JREvaluationTime evaluationTime = boundMapEntry.getKey();
			LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map = boundMapEntry.getValue();
			
			synchronized (map)
			{
				LinkedMap<Object, EvaluationBoundAction> actionsMap = map.get(pageKey);
				readElementEvaluations(object, id, evaluationTime, actionsMap);
				
				if (transferredIds != null)
				{
					//FIXMEBOOK does this have any effect on the order of the actions?
					for (Integer transferredId : transferredIds)
					{
						readElementEvaluations(object, transferredId, evaluationTime, actionsMap);
					}
				}
			}
		}
	}

	protected void readElementEvaluations(JRVirtualizable<VirtualElementsData> object, int sourceId, 
			JREvaluationTime evaluationTime, LinkedMap<Object, EvaluationBoundAction> actionsMap)
	{
		// get the delayed evaluations from the devirtualized data and add it back
		// to the filler delayed evaluation maps.
		VirtualElementsData elementsData = object.getVirtualData();
		Map<JRPrintElement, Integer> elementEvaluations = elementsData.getElementEvaluations(sourceId, evaluationTime);
		if (elementEvaluations != null)
		{
			for (Map.Entry<JRPrintElement, Integer> entry : elementEvaluations.entrySet())
			{
				JRPrintElement element = entry.getKey();
				int fillElementId = entry.getValue();
				JRFillElement fillElement = fillElements.get(fillElementId);
				
				if (log.isDebugEnabled())
				{
					log.debug(id + " got evaluation " + evaluationTime + ", source id " + sourceId + ", on " + element 
							+ ", from object " + object + ", using " + fillElement);
				}
				
				if (fillElement == null)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_ELEMENT_NOT_FOUND,  
							new Object[]{fillElementId} 
							);
				}
				
				// add first so that it will be executed immediately
				actionsMap.addFirst(element, new ElementEvaluationAction(fillElement, element));
			}
		}
	}

	public void moveMasterEvaluations(DelayedFillActions sourceActions, JRPrintPage page, int pageIndex)
	{
		FillPageKey sourcePageKey = new FillPageKey(page);
		FillPageKey destinationPageKey = new FillPageKey(page, pageIndex);
		moveMasterEvaluations(sourceActions, sourcePageKey, destinationPageKey);
	}
	
	public void moveMasterEvaluations(DelayedFillActions sourceActions, FillPageKey pageKey)
	{
		moveMasterEvaluations(sourceActions, pageKey, pageKey);
	}

	protected void moveMasterEvaluations(DelayedFillActions sourceActions, FillPageKey sourcePageKey, FillPageKey destinationPageKey)
	{
		if (log.isDebugEnabled())
		{
			log.debug(id + " moving master actions from " + sourceActions.id
					+ ", source " + sourcePageKey + ", destination " + destinationPageKey);
		}
		
		fillContext.lockVirtualizationContext();
		try
		{
			LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> actions = 
					sourceActions.actionsMap.get(JREvaluationTime.EVALUATION_TIME_MASTER);
			synchronized (actions)
			{
				LinkedMap<Object, EvaluationBoundAction> pageActions = actions.remove(sourcePageKey);//FIXMEBOOK deregister virt listener
				if (pageActions == null || pageActions.isEmpty())
				{
					return;
				}
				
				moveMasterActions(pageActions, destinationPageKey);
				
				// copy fill elements Ids for all master actions
				for (Integer elementId : sourceActions.masterFillElementIds)
				{
					if (!fillElements.containsKey(elementId))
					{
						fillElements.put(elementId, sourceActions.fillElements.get(elementId));
						masterFillElementIds.add(elementId);
					}
				}
			}
		}
		finally
		{
			fillContext.unlockVirtualizationContext();
		}
	}

	protected void moveMasterActions(LinkedMap<Object, EvaluationBoundAction> sourceActions, FillPageKey destinationPageKey)
	{
		LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> masterActions = 
				actionsMap.get(JREvaluationTime.EVALUATION_TIME_MASTER);
		synchronized (masterActions)
		{
			LinkedMap<Object, EvaluationBoundAction> masterPageActions = pageActionsMap(masterActions, destinationPageKey);
			
			while (!sourceActions.isEmpty())
			{
				Map.Entry<Object, EvaluationBoundAction> entry = sourceActions.popEntry();
				Object key = entry.getKey();
				EvaluationBoundAction action = entry.getValue();
				masterPageActions.add(key, action);
				actionMoved(action);
				
				if (log.isDebugEnabled())
				{
					log.debug(id + " moved action " + action);
				}
			}
		}
	}

	protected void actionMoved(EvaluationBoundAction action)
	{
		if (action instanceof VirtualizedPageEvaluationAction)//ugly
		{
			int sourceId = ((VirtualizedPageEvaluationAction) action).getSourceId();
			registerTransferredId(sourceId);
		}
	}

	public void collectElementEvaluations(JRPrintPage page, List<JRPrintElement> elements, 
			final ElementEvaluationsCollector collector)
	{
		fillContext.lockVirtualizationContext();
		try
		{
			doCollectElementEvaluations(page, elements, collector, true);
		}
		finally
		{
			fillContext.unlockVirtualizationContext();
		}
	}
	
	
	protected void doCollectElementEvaluations(JRPrintPage page, List<JRPrintElement> elements, 
			final ElementEvaluationsCollector collector, boolean clearEmpty)
	{
		FillPageKey pageKey = new FillPageKey(page);
		
		for (Map.Entry<JREvaluationTime, LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> boundMapEntry : 
			actionsMap.entrySet())
		{
			final JREvaluationTime evaluationTime = boundMapEntry.getKey();
			LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map = boundMapEntry.getValue();
			
			synchronized (map)
			{
				final LinkedMap<Object, EvaluationBoundAction> actionsMap = map.get(pageKey);
				
				if (actionsMap != null && !actionsMap.isEmpty())
				{
					// FIXME optimize for pages with a single virtual block
					// create a deep element visitor
					PrintElementVisitor<Void> visitor = new UniformPrintElementVisitor<Void>(true)
					{
						@Override
						protected void visitElement(JRPrintElement element, Void arg)
						{
							// remove the action from the map because we're saving it as part of the page.
							// ugly cast but acceptable for now.
							ElementEvaluationAction action = (ElementEvaluationAction) actionsMap.remove(element);
							if (action != null)
							{
								if (log.isDebugEnabled())
								{
									log.debug(id + " collecting evaluation " + evaluationTime + " of element " + element);
								}
								collector.collect(element, action.element, evaluationTime);
							}
						}

						@Override
						protected void visitFrameElements(List<JRPrintElement> elements, Void arg)
						{
							if (!(elements instanceof VirtualizableElementList))
							{
								super.visitFrameElements(elements, arg);
							}
						}
					};
					
					for (JRPrintElement element : elements)
					{
						element.accept(visitor, null);
					}
					
					if (clearEmpty && actionsMap.isEmpty())
					{
						map.remove(pageKey);
					}
				}
			}
		}
	}
	
	public void addElementEvaluations(JRPrintPage page, int pageIndex, ElementEvaluationsSource source)
	{
		FillPageKey pageKey = new FillPageKey(page, pageIndex);
		
		for (Map.Entry<JREvaluationTime, LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>>> boundMapEntry : 
			actionsMap.entrySet())
		{
			JREvaluationTime evaluationTime = boundMapEntry.getKey();
			LinkedHashMap<FillPageKey, LinkedMap<Object, EvaluationBoundAction>> map = boundMapEntry.getValue();
			
			synchronized (map)
			{
				Map<JRPrintElement, JRFillElement> elementEvaluations = source.getEvaluations(evaluationTime);
				if (elementEvaluations != null)
				{
					LinkedMap<Object, EvaluationBoundAction> actionsMap = pageActionsMap(map, pageKey);
					
					for (Map.Entry<JRPrintElement, JRFillElement> entry : elementEvaluations.entrySet())
					{
						JRPrintElement element = entry.getKey();
						JRFillElement fillElement = entry.getValue();
						
						if (log.isDebugEnabled())
						{
							log.debug(id + " got evaluation " + evaluationTime 
									+ ", source id " + fillElement.printElementOriginator.getSourceElementId() 
									+ ", on " + element + ", using " + fillElement);
						}
						
						actionsMap.add(element, new ElementEvaluationAction(fillElement, element));
					}
				}
				
			}
		}
	}
	
}
