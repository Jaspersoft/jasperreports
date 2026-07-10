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
package net.sf.jasperreports.engine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVirtualizable;
import net.sf.jasperreports.engine.fill.VirtualizerStore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SwapFileVirtualizerStore implements VirtualizerStore
{
	private static final Log log = LogFactory.getLog(SwapFileVirtualizerStore.class);
	public static final String EXCEPTION_MESSAGE_KEY_DEVIRTUALIZING_ERROR = "util.swap.file.virtualizer.devirtualizing.error";
	public static final String EXCEPTION_MESSAGE_KEY_UNABLE_TO_READ_DATA = "util.swap.file.virtualizer.unable.to.read.data";
	public static final String EXCEPTION_MESSAGE_KEY_VIRTUALIZING_ERROR = "util.swap.file.virtualizer.virtualizing.error";
	
	private final JRSwapFile swap;
	private final boolean swapOwner;
	private final Map<String,JRSwapFile.SwapHandle> handles;
	private final StreamCompression compression;

	public SwapFileVirtualizerStore(JRSwapFile swap, boolean swapOwner)
	{
		this(swap, swapOwner, null);
	}

	public SwapFileVirtualizerStore(JRSwapFile swap, boolean swapOwner, StreamCompression compression)
	{
		this.swap = swap;
		this.swapOwner = swapOwner;
		this.handles = Collections.synchronizedMap(new HashMap<String,JRSwapFile.SwapHandle>());
		this.compression = compression;
	}
	
	@Override
	public String toString()
	{
		return "SwapFileVirtualizerStore " + swap.toString(); 
	}
	
	protected boolean isStored(JRVirtualizable<?> o)
	{
		return handles.containsKey(o.getUID());
	}
	
	@Override
	public boolean store(JRVirtualizable<?> o, VirtualizationSerializer serializer)
	{
		if (isStored(o))
		{
			if (log.isTraceEnabled())
			{
				log.trace("object " + o.getUID() + " already stored");
			}
			return false;
		}
		
		try
		{
			ByteArrayOutputStream bout = new ByteArrayOutputStream(3000);
			OutputStream out = compression == null ? bout : compression.compressedOutput(bout);
			serializer.writeData(o, out);
			out.close();
			
			byte[] data = bout.toByteArray();
			if (log.isTraceEnabled())
			{
				log.trace("writing " + data.length + " for object " + o.getUID() + " to " + swap);
			}
			
			JRSwapFile.SwapHandle handle = swap.write(data);
			handles.put(o.getUID(), handle);
			return true;
		}
		catch (IOException e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Error virtualizing object " + o.getUID() + " to " + swap, e);
			}
			throw
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_VIRTUALIZING_ERROR,
					(Object[])null,
					e);
		}
	}
	
	@Override
	public void retrieve(JRVirtualizable<?> o, boolean remove, VirtualizationSerializer serializer)
	{
		JRSwapFile.SwapHandle handle = handles.get(o.getUID());
		if (handle == null)
		{
			// should not happen
			//FIXME lucianc happened once, look into it
			if (log.isDebugEnabled())
			{
				log.debug("No swap handle found for " + o.getUID() + " in " + this);
			}
			throw
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_UNABLE_TO_READ_DATA,
					(Object[])null);
		}
		
		try
		{
			byte[] data = swap.read(handle, remove);
			if (log.isTraceEnabled())
			{
				log.trace("read " + data.length + " for object " + o.getUID() + " from " + swap);
			}
			
			ByteArrayInputStream rawInput = new ByteArrayInputStream(data);
			InputStream input = compression == null ? rawInput : compression.uncompressedInput(rawInput);
			serializer.readData(o, input);
			input.close();
		}
		catch (IOException e)
		{
			if (log.isDebugEnabled())
			{
				log.debug("Error reading object data " + o.getUID() + " from " + swap, e);
			}
			throw
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_DEVIRTUALIZING_ERROR,
					(Object[])null,
					e);
		}
		
		if (remove)
		{
			handles.remove(o.getUID());
		}
	}
	
	@Override
	public void remove(String objectId)
	{
		JRSwapFile.SwapHandle handle = handles.remove(objectId);
		if (handle == null)
		{
			if (log.isTraceEnabled())
			{
				log.trace("object " + objectId + " not found for removal");
			}
		}
		else
		{
			if (log.isTraceEnabled())
			{
				log.trace("removing object " + objectId + " from " + swap);
			}
			
			swap.free(handle);
		}
	}


	/**
	 * Disposes the swap file used if this virtualizer owns it.
	 * @see #SwapFileVirtualizerStore(JRSwapFile, boolean)
	 */
	@Override
	public void dispose()
	{
		handles.clear();
		if (swapOwner)
		{
			if (log.isDebugEnabled())
			{
				log.debug("disposing " + swap);
			}
			
			swap.dispose();
		}
	}
}
