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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.sf.jasperreports.engine.util.DeduplicableRegistry;


/**
 * Interface implemented by objects that can be deduplicated by detecting
 * previously created identical instances.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see DeduplicableRegistry
 */
public interface Deduplicable
{

	/**
	 * Returns a hash code to be used for the deduplicate map.
	 * 
	 * The returned hash code should be consitent wiwh {@link #isIdentical(Object)},
	 * i.e. if two objects are identical they should have the same hash code.
	 * 
	 * @return deduplication hash code
	 */
	@JsonIgnore
	int getHashCode();
	
	/**
	 * Determines if this instance is identical to another object.
	 * 
	 * @param object the object to compare with this instance
	 * @return <code>true</code> if the two objects are identical and can be deduplicated
	 */
	boolean isIdentical(Object object);
	
}
