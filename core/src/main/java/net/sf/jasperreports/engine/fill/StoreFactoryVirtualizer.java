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

import java.io.IOException;

import org.apache.commons.collections4.map.ReferenceMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVirtualizable;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StoreFactoryVirtualizer extends JRAbstractLRUVirtualizer
{
	private static final Log log = LogFactory.getLog(StoreFactoryVirtualizer.class);
	public static final String EXCEPTION_MESSAGE_KEY_STORE_NOT_FOUND = "fill.virtualizer.store.not.found";
	
	private final VirtualizerStoreFactory storeFactory;
	private final ReferenceMap<JRVirtualizationContext, VirtualizerStore> contextStores;
	
	public StoreFactoryVirtualizer(int maxSize, VirtualizerStoreFactory storeFactory)
	{
		super(maxSize);

		this.storeFactory = storeFactory;
		
		this.contextStores = 
			new ReferenceMap<>(
				ReferenceMap.ReferenceStrength.WEAK, ReferenceMap.ReferenceStrength.HARD
				);
	}

	protected VirtualizerStore store(JRVirtualizable o, boolean create)
	{
		JRVirtualizationContext masterContext = o.getContext().getMasterContext();
		return store(masterContext, create);
	}

	protected VirtualizerStore store(JRVirtualizationContext context, boolean create)
	{
		VirtualizerStore store;
		synchronized (contextStores)
		{
			store = contextStores.get(context);
		}

		if (store != null || !create)
		{
			if (log.isTraceEnabled())
			{
				log.trace("found " + store + " for " + context);
			}
			
			return store;
		}
		
		//the context should be locked at this moment
		store = storeFactory.createStore(context);
		if (log.isDebugEnabled())
		{
			log.debug("created " + store + " for " + context);
		}
		
		// TODO lucianc 
		// do we need to keep a weak reference to the context, and dispose the store when the reference is cleared?
		// not doing that for now, assuming that store objects are disposed when garbage collected.
		synchronized (contextStores)
		{
			contextStores.put(context, store);
		}
		
		return store;
	}
	
	@Override
	protected void pageOut(JRVirtualizable o) throws IOException
	{
		VirtualizerStore store = store(o, true);
		boolean stored = store.store(o, serializer);
		if (!stored && !isReadOnly(o))
		{
			throw new IllegalStateException("Cannot virtualize data because the data for object UID \"" + o.getUID() + "\" already exists.");
		}
	}

	@Override
	protected void pageIn(JRVirtualizable o) throws IOException
	{
		VirtualizerStore store = store(o, false);
		if (store == null)
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_STORE_NOT_FOUND,
					new Object[]{o.getUID()});
		}
		
		store.retrieve(o, !isReadOnly(o), serializer);
	}

	@Override
	protected void dispose(JRVirtualizable o)
	{
		VirtualizerStore store = store(o, false);
		if (store == null)
		{
			if (log.isTraceEnabled())
			{
				log.trace("no store found for " + o.getUID() + " for disposal");
			}
			// not failing
			return;
		}

		store.remove(o.getUID());
	}
	
	@Override
	protected void dispose(String id)
	{
		// should not get here
		throw new UnsupportedOperationException();
	}

	public void dispose(JRVirtualizationContext context)
	{
		context.lock();
		try
		{
			// mark as disposed
			context.dispose();

			VirtualizerStore store = store(context, false);
			if (log.isDebugEnabled())
			{
				log.debug("found " + store + " for " + context + " for disposal");
			}
			
			if (store != null)
			{
				store.dispose();
			}
		}
		finally
		{
			context.unlock();
		}
	}
	
	@Override
	public void cleanup()
	{
		if (log.isDebugEnabled())
		{
			log.debug("disposing " + this);
		}

		synchronized (contextStores)
		{
			for (VirtualizerStore store : contextStores.values())
			{
				store.dispose();
			}

			contextStores.clear();
		}
	}
}
