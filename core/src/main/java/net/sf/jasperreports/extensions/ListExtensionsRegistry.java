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
package net.sf.jasperreports.extensions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An extension registry that contains several lists of extensions.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ListExtensionsRegistry implements ExtensionsRegistry
{

	private final Map<Class<?>, List<Object>> extensions;
	
	public ListExtensionsRegistry()
	{
		extensions = new HashMap<>();
	}

	/**
	 * Adds an extension.
	 * 
	 * @param type the extension type
	 * @param extension the extension object
	 * @return this object
	 */
	public <T> ListExtensionsRegistry add(Class<T> type, T extension)
	{
		extensions.computeIfAbsent(type, k -> new ArrayList<>()).add(extension);
		return this;
	}
	
	@Override
	public <T> List<T> getExtensions(Class<T> extensionType)
	{
		@SuppressWarnings("unchecked")
		List<T> list = (List<T>) extensions.get(extensionType);
		return list;
	}

}
