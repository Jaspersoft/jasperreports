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

import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.export.tabulator.TableCell;

/**
 * A generic print element HTML export handler.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface GenericElementHtmlHandler extends GenericElementHandler
{

	/**
	 * Returns an HTML fragment that is to be inserted in the export output.
	 * 
	 * @param element the generic print element
	 * @return the HTML fragment that represents the exported element
	 */
	String getHtmlFragment(JRHtmlExporterContext exporterContext, JRGenericPrintElement element);
	
	/**
	 * Exports a generic element.
	 * 
	 * <p>
	 * Access to the exporter output and environment is provided via the
	 * {@link JRHtmlExporterContext} argument.
	 * 
	 * @param exporterContext the exporter context
	 * @param element the generic element to export
	 */
	default void exportElement(JRHtmlExporterContext exporterContext, JRGenericPrintElement element, TableCell cell)
	{
	}
	
}
