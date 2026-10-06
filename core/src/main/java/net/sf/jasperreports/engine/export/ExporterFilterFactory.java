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

import net.sf.jasperreports.engine.JRException;

/**
 * A factory of {@link ExporterFilter} instances.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface ExporterFilterFactory
{

	/**
	 * Returns an exporter filter to be used for a specific exporter context.
	 * 
	 * This method can return <code>null</code> if nothing is to be filtered.
	 * 
	 * @param exporterContext the exporter context
	 * @return an export filter for the exporter, or <code>null</code> if nothing
	 * is to be filtered
	 * @throws JRException
	 */
	ExporterFilter getFilter(JRExporterContext exporterContext) throws JRException;
	
}
