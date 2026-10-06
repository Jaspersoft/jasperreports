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


/**
 * This interface represents the abstract representation of a JasperReports data source. All data source types must
 * implement this interface.
 * <p>
 * Every time a report is filled, an instance of this interface is supplied or created behind the scenes by the reporting engine.
 * </p><p>
 * JasperReports provides default implementations of result set, bean collections and bean arrays data sources.
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JRDataSource
{
	/**
	 * Tries to position the cursor on the next element in the data source.
	 * @return true if there is a next record, false otherwise
	 * @throws JRException if any error occurs while trying to move to the next element
	 */
	public boolean next() throws JRException;

	/**
	 * Gets the field value for the current position.
	 * @return an object containing the field value. The object type must be the field object type.
	 */
	public Object getFieldValue(JRField jrField) throws JRException;


}
