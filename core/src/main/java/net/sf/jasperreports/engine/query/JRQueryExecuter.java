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
package net.sf.jasperreports.engine.query;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;


/**
 * Query executer interface.
 * <p/>
 * An implementation of this interface is created when the input data of a report/dataset
 * is specified by a query.
 * <p/>
 * The implementation will run the query and create a {@link net.sf.jasperreports.engine.JRDataSource JRDataSource}
 * from the result.
 * <p/>
 * The query executers would usually be initialized by a {@link net.sf.jasperreports.engine.query.QueryExecuterFactory QueryExecuterFactory}
 * with the query and the parameter values.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see net.sf.jasperreports.engine.query.QueryExecuterFactory
 */
public interface JRQueryExecuter
{
	/**
	 * Executes the query and creates a {@link JRDataSource JRDataSource} out of the result.
	 * 
	 * @return a {@link JRDataSource JRDataSource} wrapping the query execution result.
	 * @throws JRException
	 */
	public JRDataSource createDatasource() throws JRException;

	/**
	 * Closes resources kept open during the data source iteration.
	 * <p/>
	 * This method is called after the report is filled or the dataset is iterated.
	 * If a resource is not needed after the data source has been created, it should be
	 * released at the end of {@link #createDatasource() createDatasource}.
	 */
	public void close();

	/**
	 * Cancels the query if it's currently running.
	 * <p/>
	 * This method will be called from a different thread if the client decides to
	 * cancel the filling process.
	 * 
	 * @return <code>true</code> if and only if the query was running and it has been canceled
	 * @throws JRException
	 */
	public boolean cancelQuery() throws JRException;
}
