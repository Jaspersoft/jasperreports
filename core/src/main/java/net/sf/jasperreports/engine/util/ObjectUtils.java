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
package net.sf.jasperreports.engine.util;

import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.Deduplicable;
import net.sf.jasperreports.engine.JRPropertiesMap;

/**
 * Object comparison and hashing utilities.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ObjectUtils
{

	/**
	 * Hash code creator for objects.
	 */
	public static class HashCode
	{
		private final int coefficient = 29;
		private int hash = 47;
		
		public HashCode()
		{
		}
		
		protected void addToHash(int value)
		{
			hash = coefficient * hash + value;
		}
		
		/**
		 * Adds an integer value to the hash.
		 * 
		 * @param value
		 */
		public void add(int value)
		{
			addToHash(value);
		}
		
		/**
		 * Adds a boolean value to the hash.
		 * 
		 * @param value
		 */
		public void add(boolean value)
		{
			addToHash(((Boolean)value).hashCode());
		}
		
		/**
		 * Adds an object to the hash, using <code>Object.hashCode()</value>.
		 * 
		 * @param value
		 */
		public void add(Object value)
		{
			addToHash(value == null ? 0 : value.hashCode());
		}
		
		/**
		 * Adds an object to the hash, checking whether the object implements {@link Deduplicable} 
		 * and using the deduplication hash code in that case.
		 * 
		 * @param value
		 */
		public void addIdentical(Object value)
		{
			if (value instanceof Deduplicable)
			{
				addToHash(((Deduplicable) value).getHashCode());
			}
			else
			{
				add(value);
			}
		}
		
		/**
		 * Adds an object to the hash using its identity (<code>System.identityHashCode</code>).
		 * 
		 * @param value
		 */
		public void addIdentity(Object value)
		{
			addToHash(System.identityHashCode(value));
		}
		
		/**
		 * Adds an array of objects to the hash, checking if the objects implement {@link Deduplicable}.
		 * 
		 * @param values
		 * @see #addIdentical(Object)
		 */
		public void addIdentical(Object[] values)
		{
			// treating null and empty as the same
			if (values == null || values.length == 0)
			{
				addToHash(0);
			}
			else
			{
				addToHash(values.length);
				for (Object object : values)
				{
					addIdentical(object);
				}
			}
		}
		
		/**
		 * Adds a list of objects to the hash, checking if the objects implement {@link Deduplicable}.
		 * 
		 * @param values
		 * @see #addIdentical(Object)
		 */
		public void addIdentical(List<?> values)
		{
			// treating null and empty as the same
			if (values == null || values.isEmpty())
			{
				addToHash(0);
			}
			else
			{
				addToHash(values.size());
				for (Object object : values)
				{
					addIdentical(object);
				}
			}
		}
		
		/**
		 * Adds a set of properties to the hash.
		 * 
		 * @param properties
		 */
		public void add(JRPropertiesMap properties)
		{
			if (properties == null || !properties.hasProperties())
			{
				addToHash(0);
			}
			else
			{
				String[] names = properties.getPropertyNames();
				for (String prop : names)
				{
					add(prop);
					add(properties.getProperty(prop));
				}
			}
		}

		public int getHashCode()
		{
			return hash;
		}
	}
	
	/**
	 * Returns a fresh hash code creator.
	 * 
	 * @return a fresh hash code creator
	 */
	public static HashCode hash()
	{
		return new HashCode();
	}
	
	public static int hashCode(Object o)
	{
		return o == null ? 0 : o.hashCode();
	}
	
	/**
	 * Determines whether two objects are the same as instances.
	 * 
	 * @param o1
	 * @param o2
	 * @return whether the two objects are the same as instances
	 */
	public static boolean equalsIdentity(Object o1, Object o2)
	{
		return o1 == o2;
	}
	
	/**
	 * Determines whether two objects are identical.
	 * 
	 * If the objects implement {@link Deduplicable}, the deduplication method
	 * is used to compare the objects.  Otherwise, <code>Object.equals</code> is used.
	 * 
	 * @param o1
	 * @param o2
	 * @return whether the two objects are identical
	 */
	public static boolean identical(Object o1, Object o2)
	{
		if (o1 instanceof Deduplicable && o2 instanceof Deduplicable)
		{
			return ((Deduplicable) o1).isIdentical(o2);
		}
		
		return equals(o1, o2);
	}
	
	/**
	 * Determines whether two arrays of objects are identical.
	 * 
	 * @param v1
	 * @param v2
	 * @return whether the two arrays are identical
	 * @see #identical(Object, Object)
	 */
	public static boolean identical(Object[] v1, Object[] v2)
	{
		// treating null and empty as the same
		if (v1 == null || v1.length == 0)
		{
			return v2 == null || v2.length == 0;
		}
		
		if (v2 == null 
				|| v1.length != v2.length)
		{
			return false;
		}
		
		for (int idx = 0; idx < v1.length; idx++)
		{
			if (!ObjectUtils.identical(v1[idx], v2[idx]))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determines whether two lists of objects are identical.
	 * 
	 * @param l1
	 * @param l2
	 * @return whether the two lists of objects are identical
	 * @see #identical(Object, Object)
	 */
	public static boolean identical(List<?> l1, List<?> l2)
	{
		// treating null and empty as the same
		if (l1 == null || l1.isEmpty())
		{
			return l2 == null || l2.isEmpty();
		}
		
		if (l2 == null || l1.size() != l2.size())
		{
			return false;
		}
		
		Iterator<?> i1 = l1.iterator();
		Iterator<?> i2 = l2.iterator();
		while (i1.hasNext())
		{
			Object o1 = i1.next();
			Object o2 = i2.next();
			if (!ObjectUtils.identical(o1, o2))
			{
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Determines whether two objects are equal, including <code>null</code> values.
	 * 
	 * @param o1
	 * @param o2
	 * @return whether the two objects are equal
	 */
	public static boolean equals(Object o1, Object o2)
	{
		return (o1 == null) ? (o2 == null) : (o2 != null && o1.equals(o2));
	}
	
	/**
	 * Determines whether two enum values are equal.
	 * 
	 * @param o1
	 * @param o2
	 * @return whether the two enum values are equal
	 */
	public static <T extends Enum<T>> boolean equals(Enum<T> o1, Enum<T> o2)
	{
		return o1 == o2;
	}

	/**
	 * Determines whether two boolean values are equal.
	 * 
	 * @param b1
	 * @param b2
	 * @return whether the two values are equal
	 */
	public static boolean equals(boolean b1, boolean b2)
	{
		return b1 == b2;
	}

	/**
	 * Determines whether two integer values are equal.
	 * 
	 * @param i1
	 * @param i2
	 * @return whether the two values are equal
	 */
	public static boolean equals(int i1, int i2)
	{
		return i1 == i2;
	}
	
	public static boolean equals(float f1, float f2)
	{
		return Float.floatToIntBits(f1) == Float.floatToIntBits(f2);
	}
	
	/**
	 * Determines whether two property sets are identical.
	 * 
	 * @param p1
	 * @param p2
	 * @return whether the two property sets are identical
	 */
	public static boolean equals(JRPropertiesMap p1, JRPropertiesMap p2)
	{
		// treating null and empty as the same
		if (p1 == null || !p1.hasProperties())
		{
			return p2 == null || !p2.hasProperties();
		}
		
		if (p2 == null || !p2.hasProperties())
		{
			return false;
		}
		
		String[] names1 = p1.getPropertyNames();
		String[] names2 = p2.getPropertyNames();
		
		if (names1.length != names2.length)
		{
			return false;
		}
		
		for (int i = 0; i < names1.length; i++)
		{
			String name1 = names1[i];
			String name2 = names2[i];
			if (!equals(name1, name2))
			{
				return false;
			}
			
			String value1 = p1.getProperty(name1);
			String value2 = p2.getProperty(name1);
			if (!equals(value1, value2))
			{
				return false;
			}
		}
		
		return true;
	}
	
}
