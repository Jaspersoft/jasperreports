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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.sf.jasperreports.engine.JRRuntimeException;

/**
 * Working clones pooling utility used at fill time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRClonePool
{
	private static final String EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND = "fill.clone.pool.cannot.release.clone";
	
	private final JRFillCloneable original;
	private final LinkedList<JRFillCloneable> availableClones;
	private final boolean trackLockedClones;
	private final Set<JRFillCloneable> lockedClones;

	
	/**
	 * Creates a clone pool.
	 * 
	 * @param original the original element that will be cloned
	 * @param trackLockedClones whether to track clones retrieved from the pool
	 * 		<p>
	 * 		If set, the pool will keep a set of in-use clones and the caller will always
	 * 		have to release the clones back to the pool.
	 * @param useOriginal whether the original object can be used as a working clone
	 */
	public JRClonePool(JRFillCloneable original, boolean trackLockedClones, boolean useOriginal)
	{
		this.original = original;
		
		availableClones = new LinkedList<>();
		
		this.trackLockedClones = trackLockedClones;
		if (trackLockedClones)
		{
			lockedClones = new HashSet<>();
		}
		else
		{
			lockedClones = null;
		}
		
		if (useOriginal)
		{
			availableClones.add(original);
		}
	}
	
	
	/**
	 * Retrieves a clone from the pool.
	 * <p>
	 * The clone is reserved to the caller who will need to call
	 * {@link #releaseClone(Object) releaseClone(Object)} to release it back to the pool.
	 * 
	 * @return a clone of the original object
	 */
	public Object getClone()
	{
		JRFillCloneable clone;
		
		if (availableClones.isEmpty())
		{
			JRFillCloneFactory factory = new JRFillCloneFactory();
			clone = original.createClone(factory);
		}
		else
		{
			clone = availableClones.removeFirst();
		}
		
		if (trackLockedClones)
		{
			lockedClones.add(clone);
		}
		
		return clone;
	}
	
	
	/**
	 * Release the clone back to the pool.
	 * The clone will be available for other clients.
	 * 
	 * @param clone the clone to be released
	 */
	public void releaseClone(Object clone)
	{
		if (trackLockedClones)
		{
			if (!lockedClones.remove(clone))
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_PARAMETER_NOT_FOUND,
						(Object[])null);
			}
		}
		
		availableClones.addLast((JRFillCloneable)clone);
	}
}
