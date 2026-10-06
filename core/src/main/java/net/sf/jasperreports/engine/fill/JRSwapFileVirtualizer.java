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

import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.engine.util.StreamCompression;
import net.sf.jasperreports.engine.util.SwapFileVirtualizerStore;


/**
 * A virtualizer that uses a single swap file to serialize virtual data.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRSwapFileVirtualizer extends StoreVirtualizer
{
	
	/**
	 * Creates a virtualizer that uses a swap file.
	 * <p>
	 * The virtualizer will be considered the owner of the swap file.
	 * 
	 * @param maxSize the maximum size (in JRVirtualizable objects) of the paged in cache.
	 * @param swap the swap file to use for data virtualization
	 */
	public JRSwapFileVirtualizer(int maxSize, JRSwapFile swap)
	{
		this(maxSize, swap, true);
	}

	
	/**
	 * Creates a virtualizer that uses a swap file.
	 * 
	 * @param maxSize the maximum size (in JRVirtualizable objects) of the paged in cache.
	 * @param swap the swap file to use for data virtualization
	 * @param swapOwner whether the virtualizer is the owner (single user) of the swap file.
	 * If <code>true</code>, the virtualizer will dispose the swap file on
	 * {@link #cleanup() cleanup}.
	 */
	public JRSwapFileVirtualizer(int maxSize, JRSwapFile swap, boolean swapOwner)
	{
		super(maxSize, new SwapFileVirtualizerStore(swap, swapOwner));
	}
	
	/**
	 * Creates a virtualizer that uses a swap file.
	 * 
	 * @param maxSize the maximum size (in JRVirtualizable objects) of the paged in cache.
	 * @param swap the swap file to use for data virtualization
	 * @param swapOwner whether the virtualizer is the owner (single user) of the swap file.
	 * If <code>true</code>, the virtualizer will dispose the swap file on
	 * {@link #cleanup() cleanup}.
	 * @param compression stream compression to apply to serialized data
	 */
	public JRSwapFileVirtualizer(int maxSize, JRSwapFile swap, boolean swapOwner,
			StreamCompression compression)
	{
		super(maxSize, new SwapFileVirtualizerStore(swap, swapOwner, compression));
	}
}
