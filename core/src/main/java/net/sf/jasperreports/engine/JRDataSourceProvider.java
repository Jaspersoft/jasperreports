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
 * Abstracts the means of creating and disposing a data source. 
 * This interface is meant to be the standard way to plug custom 
 * data sources into GUI designers. Typically the report developer will 
 * implement this interface to create and return a configured data source 
 * of the desired type and then configure the designer to use this implementation. 
 * <br>
 * The following example demonstrates a provider for a 
 * {@link net.sf.jasperreports.engine.data.JRBeanCollectionDataSource JRBeanCollectionDataSource}.
 * <code>
 * <pre>
 * public class MyBeansDataSource extends JRAbstractBeanDataSourceProvider {
 * 
 * 	public MyBeansDataSource() {
 * 		super(PersonBean.class);
 * 	}
 * 
 * 	public JRDataSource create(JasperReport report) throws JRException {
 * 		ArrayList list = new ArrayList();
 * 		list.add(new PersonBean("Teodor"));
 * 		list.add(new PersonBean("Peter"));
 * 		return new JRBeanCollectionDataSource(list);
 * 	}
 *	
 * 	public void dispose(JRDataSource dataSource) throws JRException {
 *		// nothing to dispose
 * 	}
 * }
 * </pre>
 * </code>
 * @author Peter Severin (peter_s@sourceforge.net, contact@jasperassistant.com)
 */
public interface JRDataSourceProvider 
{

	/**
	 * Returns true if the provider supports the {@link #getFields(JasperReport) getFields} 
	 * operation. By returning true in this method the data source provider indicates
	 * that it is able to introspect the data source and discover the available fields.
	 * 
	 * @return true if the getFields() operation is supported.
	 */
	public boolean supportsGetFieldsOperation();
	
	/**
	 * Returns the fields that are available from the data source.
	 * The provider can use the passed in report to extract some additional
	 * configuration information such as report properties. 
	 * 
	 * @param report the report that will be filled using the data source created by this provider.
	 * 	The passed in report can be null. That means that no compiled report is available yet.
	 * @return a non null fields array. If there are no fields then an empty array must be returned.
	 * 
	 * @throws UnsupportedOperationException is the method is not supported
	 * @throws JRException if an error occurs.
	 */
	public JRField[] getFields(JasperReport report) throws JRException, UnsupportedOperationException;
	
	/**
	 * Creates and returns a new instance of the provided data source.
	 * The provider can use the passed in report to extract some additional
	 * configuration information such as report properties. 
	 *  
	 * @param report the report that will be filled using the created data source.
	 * @throws JRException if the data source creation has failed
	 */
	public JRDataSource create(JasperReport report) throws JRException;

	/**
	 * Disposes the data source previously obtained using the 
	 * {@link #create(JasperReport) create} method.
	 * This method must close any resources associated with the 
	 * data source. For instance the database connection should be 
	 * closed in case of the
	 * {@link JRResultSetDataSource JRResultSetDataSource}. 
	 * <br>
	 * Note: The provider must take care of the resource - data source association. 
	 * For example in case of the {@link JRResultSetDataSource JRResultSetDataSource}
	 * a subclass of this data source can be created. This subclass will
	 * hold the database connection and the prepared statement that were
	 * used to obtain the ResultSet. On the time of the dispose these resources
	 * can be retrieved from the data source object and closed.
	 *
	 * @param dataSource the data source to dispose 
	 * @throws JRException if the data source could not be disposed
	 */
	public void dispose(JRDataSource dataSource) throws JRException;
	
}
