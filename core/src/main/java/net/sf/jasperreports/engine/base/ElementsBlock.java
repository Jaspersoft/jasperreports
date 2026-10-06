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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRVirtualizable;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRVirtualizationContext;
import net.sf.jasperreports.engine.fill.VirtualizationObjectInputStream;
import net.sf.jasperreports.engine.fill.VirtualizationObjectOutputStream;
import net.sf.jasperreports.engine.util.VirtualizableElementCounter;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ElementsBlock implements JRVirtualizable<VirtualElementsData>, ElementStore, Serializable
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	private static final Log log = LogFactory.getLog(ElementsBlock.class);

	private static final AtomicInteger uidCounter = new AtomicInteger();
	private static final Random uidRandom = new Random();
	
	private JRVirtualizationContext context;
	
	/**
	 * A unique identifier that is useful for serialization and deserialization
	 * to some persistence mechanism.
	 */
	private String uid;
	
	private List<JRPrintElement> elements;
	private transient volatile int size;
	private transient VirtualElementsData virtualData;
	private transient int deepElementCount;
	
	// only kept during the fill and does not get restored on deserialization
	private transient JRVirtualPrintPage page;
	
	public ElementsBlock(JRVirtualizationContext context, JRVirtualPrintPage page)
	{
		this.context = context;
		this.page = page;
		this.uid = makeUID();
		
		if (log.isDebugEnabled())
		{
			log.debug("generated uid " + uid + " for " + this);
		}
		
		this.elements = new ArrayList<>();
		this.size = 0;
		this.deepElementCount = 0;
	}
	
	public ElementsBlock(JRVirtualizationContext context, JRVirtualPrintPage page, String uid, int size)
	{
		this.context = context;
		this.page = page;
		this.uid = uid;
		this.size = size;
		//no need to set deepElementCount
	}

	@Override
	public void updatePage(JRVirtualPrintPage page)
	{
		this.page = page;
		
		JRVirtualizationContext newContext = page.getVirtualizationContext();
		context.updateParent(newContext);
	}

	@Override
	public void updateContext(JRVirtualizationContext context, JRVirtualPrintPage page)
	{
		JRVirtualizationContext oldContext = this.context;
		this.context = context;
		this.page = page;
		oldContext.getVirtualizer().contextChanged(this, oldContext);
	}

	private void lockContext()
	{
		//FIXME locking the whole context is too much, ideally we'd have a lock for this object only
		context.lock();
	}

	private void unlockContext()
	{
		context.unlock();
	}

	private void register()
	{
		if (context.getVirtualizer() != null)
		{
			context.getVirtualizer().registerObject(this);
		}
	}
	
	/**
	 * Make a new identifier for an object.
	 * 
	 * @return the new identifier
	 */
	private String makeUID()
	{
		//FIXME use UUID?
		return 
				System.identityHashCode(context) 
				+ "_" + System.identityHashCode(this)
				+ "_" + uidCounter.incrementAndGet()
				+ "_" + uidRandom.nextInt();
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public int deepSize()
	{
		return deepElementCount;
	}

	public boolean isEmpty()
	{
		return size == 0;
	}

	@Override
	public JRPrintElement get(int index)
	{
		lockContext();
		try
		{
			ensureDataAndTouch();
			return elements.get(index);
		}
		finally
		{
			unlockContext();
		}
	}

	protected boolean preAdd(JRPrintElement element, boolean force)
	{
		boolean empty = elements != null && elements.isEmpty();
		if (empty)
		{
			// if there are no elements, the object has not yet been registered with the virtualizer
			register();
		}
		else
		{
			ensureDataAndTouch();
		}
		
		// check whether we passed the element count threshold.
		// if the list is empty, allow at least one element to be added
		// no matter the element count.
		int elementCount = VirtualizableElementCounter.count(element);
		if (!force && !elements.isEmpty())
		{
			int pageSize = context.getPageElementSize();
			if (pageSize > 0 && deepElementCount + elementCount > pageSize)
			{
				if (log.isDebugEnabled())
				{
					log.debug("overflow of block with size " + deepElementCount 
							+ ", adding " + elementCount);
				}
				
				return false;
			}				
		}
		
		deepElementCount += elementCount;
		return true;
	}

	public boolean add(JRPrintElement element, boolean force)
	{
		lockContext();
		try
		{
			boolean adding = preAdd(element, force);
			if (adding)
			{
				if (element instanceof JRTemplatePrintFrame
						&& ((JRTemplatePrintFrame) element).getElements() instanceof VirtualizableElementList)
				{
					VirtualizableElementList frameList = ((VirtualizableElementList) ((JRTemplatePrintFrame) element).getElements());
					frameList.updatePage(page);
				}
				
				elements.add(element);
				++size;
			}
			return adding;
		}
		finally
		{
			unlockContext();
		}
	}
	
	@Override
	public boolean add(JRPrintElement element)
	{
		return add(element, false);
	}

	public boolean add(int index, JRPrintElement element, boolean force)
	{
		lockContext();
		try
		{
			boolean adding = preAdd(element, force);
			if (adding)
			{
				elements.add(index, element);
				++size;
			}
			return adding;
		}
		finally
		{
			unlockContext();
		}
	}
	
	@Override
	public boolean add(int index, JRPrintElement element)
	{
		return add(index, element, false);
	}

	@Override
	public JRPrintElement set(int index, JRPrintElement element)
	{
		lockContext();
		try
		{
			ensureDataAndTouch();
			
			JRPrintElement oldElement = elements.get(index);
			deepElementCount -= VirtualizableElementCounter.count(oldElement);
			deepElementCount += VirtualizableElementCounter.count(element);
			
			return elements.set(index, element);
		}
		finally
		{
			unlockContext();
		}
	}

	@Override
	public JRPrintElement remove(int index)
	{
		lockContext();
		try
		{
			ensureDataAndTouch();
			
			JRPrintElement element = elements.remove(index);
			--size;
			
			// decrement the deep count
			deepElementCount -= VirtualizableElementCounter.count(element);

			if (elements.isEmpty())
			{
				// if the list is empty now, deregister with the virtualizer.
				// this helps with subreports by immediately releasing the external storage.
				deregister();
			}

			return element;
		}
		finally
		{
			unlockContext();
		}
	}
	
	@Override
	public String getUID()
	{
		return uid;
	}

	private void ensureDataAndTouch()
	{
		if (elements == null)
		{
			ensureData();
		}
		else
		{
			if (context.getVirtualizer() != null)
			{
				context.getVirtualizer().touch(this);
			}
		}
	}
	
	@Override
	public void ensureVirtualData()
	{
		lockContext();
		try
		{
			if (elements == null)
			{
				ensureData();
			}
		}
		finally
		{
			unlockContext();
		}
	}

	private void ensureData()
	{
		if (context.getVirtualizer() != null)
		{
			context.getVirtualizer().requestData(this);
		}
	}

	@Override
	public void setVirtualData(VirtualElementsData virtualData)
	{
		lockContext();
		try
		{
			this.virtualData = virtualData;
			this.elements = virtualData.getElements();
			//FIXME recheck size?
		}
		finally
		{
			unlockContext();
		}
	}

	@Override
	public VirtualElementsData getVirtualData()
	{
		return virtualData;
	}

	@Override
	public void removeVirtualData()
	{
		lockContext();
		try
		{
			virtualData = null;
			elements = null;
		}
		finally
		{
			unlockContext();
		}
	}

	@Override
	public void beforeExternalization()
	{
		lockContext();
		try
		{
			virtualData = new VirtualElementsData(elements);
			context.beforeExternalization(this);
		}
		finally
		{
			unlockContext();
		}
	}

	@Override
	public void afterExternalization()
	{
		lockContext();
		try
		{
			context.afterExternalization(this);
		}
		finally
		{
			unlockContext();
		}
	}

	@Override
	public void afterInternalization()
	{
		lockContext();
		try
		{
			context.afterInternalization(this);
			virtualData = null;
		}
		finally
		{
			unlockContext();
		}
	}

	@Override
	public JRVirtualizationContext getContext()
	{
		return context;
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// we do not use the original ID as the source object might still be
		// alive in the JVM
		String oldUid = (String) in.readObject();
		uid = makeUID();
		if (log.isDebugEnabled())
		{
			log.debug("Original uid " + oldUid + "; new uid " + uid);
		}
		
		context = (JRVirtualizationContext) in.readObject();
		
		int length = in.readInt();
		//FIXME put a limit on the buffer
		byte[] buffer = new byte[length];
		in.readFully(buffer);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer, 0, buffer.length);
		VirtualizationObjectInputStream elementsStream = new VirtualizationObjectInputStream(inputStream, context);
		elements = (List<JRPrintElement>) elementsStream.readObject();
		size = elements.size();
		
		if (!elements.isEmpty())
		{
			register();
		}
	}

	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		lockContext();
		try
		{
			ensureDataAndTouch();
			beforeExternalization();
			
			try
			{
				// maybe we should no longer serialize the id, as a new one is
				// generated on deserialization
				out.writeObject(uid);
				out.writeObject(context);

				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				VirtualizationObjectOutputStream stream = new VirtualizationObjectOutputStream(bout, context);
				stream.writeObject(elements);
				stream.flush();

				byte[] bytes = bout.toByteArray();
				out.writeInt(bytes.length);
				out.write(bytes);
			}
			finally
			{
				afterExternalization();
			}
		}
		finally
		{
			unlockContext();
		}
	}

	// not implementing finalize because it can slow down GC to the point it can no longer handle the rate of newly created objects.
	// we're relying on the virtualizer to keep track of garbage collected objects via weak references.
/*	protected void finalize() throws Throwable //NOSONAR
	{
		dispose();
		super.finalize();
	}
*/
	@Override
	public void dispose()
	{
		lockContext();
		try
		{
			// if empty, the object is not registered
			if (elements == null || !elements.isEmpty())
			{
				deregister();
			}
			// removeVirtualData?  letting GC do his thing for now.
		}
		finally
		{
			unlockContext();
		}
	}

	private void deregister()
	{
		if (context.getVirtualizer() != null)
		{
			context.getVirtualizer().deregisterObject(this);
		}
	}
	
	@Override
	public JRVirtualPrintPage getPage()
	{
		return page;
	}

	@Override
	public void transferElements(Consumer<JRPrintElement> consumer)
	{
		lockContext();
		try
		{
			ensureDataAndTouch();
			
			elements.forEach(consumer);
			
			size = 0;
			deepElementCount = 0;
			// this helps with subreports by immediately releasing the external storage.
			deregister();
		}
		finally
		{
			unlockContext();
		}
	}
}