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
package net.sf.jasperreports.engine.base;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;

/**
 * List implementation that can virtualize blocks of elements.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class VirtualizableElementList extends AbstractList<JRPrintElement> implements Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(VirtualizableElementList.class);

	protected final JRVirtualizationContext virtualizationContext;

	protected ElementStore store;
	
	public VirtualizableElementList(JRVirtualizationContext virtualizationContext, JRVirtualPrintPage page)
	{
		this.virtualizationContext = virtualizationContext;
		
		initStore(page);
	}
	
	public VirtualizableElementList(JRVirtualizationContext virtualizationContext, ElementStore store)
	{
		this.virtualizationContext = virtualizationContext;
		this.store = store;
	}

	private void initStore(JRVirtualPrintPage page)
	{
		// start with a simple block to reduce memory consumption for small pages
		this.store = new ElementsBlock(virtualizationContext, page);
		
		if (log.isDebugEnabled())
		{
			log.debug("created block " + this.store + " for " + this);
		}
	}
	
	public ElementStore getElementStore()
	{
		return store;
	}

	public synchronized void set(List<JRPrintElement> elements)
	{
		clear();
		addAll(elements);
	}

	private void cacheInContext(JRPrintElement element)
	{
		virtualizationContext.cacheTemplate(element);
	}

	@Override
	public synchronized JRPrintElement get(int index)
	{
		return store.get(index);
	}

	@Override
	public synchronized int size()
	{
		return store.size();
	}

	public synchronized int deepSize()
	{
		return store.deepSize();
	}

	@Override
	public synchronized JRPrintElement set(int index, JRPrintElement element)
	{
		cacheInContext(element);
		return store.set(index, element);
	}

	private void createBlockList()
	{
		ElementsBlockList blockList = new ElementsBlockList((ElementsBlock) store);
		blockList.addBlock();
		
		store = blockList;
	}

	@Override
	public synchronized boolean add(JRPrintElement element)
	{
		cacheInContext(element);

		if (!store.add(element))
		{
			// we had a single block and it overflowed.
			// create a block list.
			createBlockList();
			
			// add again, block list never overflows.
			store.add(element);
		}
		return true;
	}

	@Override
	public synchronized void add(int index, JRPrintElement element)
	{
		cacheInContext(element);

		if (!store.add(index, element))
		{
			// we had a single block and it overflowed.
			// create a block list.
			createBlockList();
			
			// add again, block list never overflows.
			store.add(index, element);
		}
	}

	@Override
	public synchronized JRPrintElement remove(int index)
	{
		return store.remove(index);
	}

	@Override
	public synchronized void clear()
	{
		// recreating the store
		JRVirtualPrintPage page = store.getPage();
		store.dispose();
		initStore(page);
	}

	public synchronized void dispose()
	{
		store.dispose();
	}

	public JRVirtualizationContext getVirtualizationContext()
	{
		return virtualizationContext;
	}

	public void updatePage(JRVirtualPrintPage page)
	{
		store.updatePage(page);
	}
	
	public ElementStore transferStore(JRVirtualizationContext context, JRVirtualPrintPage page)
	{
		ElementStore existingStore = store;
		initStore(store.getPage());
		existingStore.updateContext(context, page);
		return existingStore;
	}

	public void transferElements(Consumer<JRPrintElement> consumer)
	{
		store.transferElements(consumer);
		//recreate store
		initStore(store.getPage());
	}
	
	//FIXME implement faster bulk methods such as addAll
}
