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
package net.sf.jasperreports.engine.export;

import net.sf.jasperreports.engine.JRGenericElementType;

/**
 * A bundle of {@link GenericElementHandler generic element handlers} that share
 * the same namespace.
 * 
 * <p>
 * Such a bundle can contain handlers for several generic element types, each
 * having a unique name.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRGenericElementType
 */
public interface GenericElementHandlerBundle
{

	/**
	 * Returns the namespace of this bundle.
	 * 
	 * @return the bundle namespace
	 */
	String getNamespace();
	
	/**
	 * Returns a generic element handler for a element type name and an
	 * exporter key.
	 * 
	 * @param elementName the element type name
	 * @param exporterKey the exporter key
	 * @return a generic element handler for the combination
	 */
	GenericElementHandler getHandler(String elementName, 
			String exporterKey);

}
