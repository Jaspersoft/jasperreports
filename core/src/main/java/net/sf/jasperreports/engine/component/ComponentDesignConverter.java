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
package net.sf.jasperreports.engine.component;

import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.convert.ReportConverter;

/**
 * Converter of design report elements into a print elements used for report
 * design previewing.
 * 
 * <p>
 * Such converters are used when a report design that contains component elements
 * are previewed.  Each component implementation can include a converter that
 * provides a preview representation of a component instance.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see net.sf.jasperreports.view.JasperDesignViewer
 * @see JRPrintElement
 */
public interface ComponentDesignConverter
{

	/**
	 * Converts a component element into a print element that represents a preview
	 * of the component.
	 * 
	 * @param reportConverter the report converter instance
	 * @param element the component element
	 * @return a print element that represents a preview of the component
	 */
	JRPrintElement convert(ReportConverter reportConverter,
			JRComponentElement element);
	
}
