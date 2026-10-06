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

import net.sf.jasperreports.engine.fill.DatasetFillContext;

/**
 * A dataset row filter.
 * 
 * <p>
 * Such a filter can be used in addition to the dataset filter expression to
 * match dataset rows based on a programmatic criteria.
 * </p>
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see JRParameter#FILTER
 * @see JRDataset#getFilterExpression()
 */
public interface DatasetFilter
{

	/**
	 * Initializes the filter.
	 * 
	 * @param context dataset context information
	 */
	void init(DatasetFillContext context);
	
	/**
	 * Determines whether the current row matches the filter criteria.
	 * 
	 * Matching rows are included in the report, while non-matching rows are skipped.
	 * 
	 * @param evaluation the evaluation type.
	 * Currently only {@link EvaluationType#ESTIMATED} is used.
	 * @return <code>true<code> if the row is to be included in the report.
	 */
	boolean matches(EvaluationType evaluation);
	
}
