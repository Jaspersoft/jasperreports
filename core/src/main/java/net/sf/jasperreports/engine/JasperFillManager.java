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
package net.sf.jasperreports.engine;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.fill.JRFiller;
import net.sf.jasperreports.engine.fill.JasperReportSource;
import net.sf.jasperreports.engine.fill.SimpleJasperReportSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryResourceContext;
import net.sf.jasperreports.repo.RepositoryUtil;
import net.sf.jasperreports.repo.ResourceInfo;
import net.sf.jasperreports.repo.SimpleRepositoryResourceContext;


/**
 * Facade class for filling compiled report designs with data from report data sources, 
 * in order to produce page-oriented documents, ready-to-print.
 * <p>
 * It exposes a variety of methods that receive a report template in the form of an object, file,
 * or input stream, and also produces a document in various output forms (object, file, or
 * output stream).
 * <p>
 * All methods receive a Map object that should contain the values for the report parameters.
 * These values are retrieved by the engine using the corresponding report parameter name as the key. 
 * <p>
 * There are two types of method signatures with regards to the data source
 * provided for filling the report:
 * <ul>
 * <li>Methods that receive an instance of the {@link net.sf.jasperreports.engine.JRDataSource} interface
 * and use it directly for retrieving report data;
 * <li>Methods that receive an instance of the <code>java.sql.Connection</code> interface and retrieve
 * the report data by executing the report internal SQL query through this JDBC connection and wrapping 
 * the returned <code>java.sql.ResultSet</code> object inside a {@link net.sf.jasperreports.engine.JRResultSetDataSource}
 * instance. 
 * </ul>
 * 
 * @see net.sf.jasperreports.engine.JasperReport
 * @see net.sf.jasperreports.engine.JRDataSource
 * @see net.sf.jasperreports.engine.fill.JRFiller
 * @see net.sf.jasperreports.engine.JasperPrint
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public final class JasperFillManager
{
	
	private static final Log log = LogFactory.getLog(JasperFillManager.class);
	
	/**
	 * Property that determines whether resource paths in subreports, style templates and data adapters 
	 * should be interpreted as relative to the master report location.
	 * <br/>
	 * Starting with version 6.6.0, relative paths in subreports, style templates and data adapters are
	 * resolved as relative to the resource that contains them.
	 * Prior to version 6.6.0, relative paths in subreports, style templates and data adapters were 
	 * resolved as relative to the master report resource.
	 * This property can be set to <code>true</code> to restore the pre 6.6.0 functionality.
	 * <br/>
	 * The default value of the property is <code>false</code>.
	 * <br/>
	 * 
	 * @deprecated The property should only be set when upgrading from a version older than 6.6.0 with a repository
	 * that relied on the fact that paths were relative to the master report.
	 * The property might be removed at some point in the future.
	 */
	@Property(
			category = PropertyConstants.CATEGORY_REPOSITORY,
			defaultValue = PropertyConstants.BOOLEAN_FALSE,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT},
			sinceVersion = PropertyConstants.VERSION_6_6_0,
			valueType = Boolean.class
			)
	@Deprecated
	public static final String PROPERTY_LEGACY_RELATIVE_PATH_ENABLED = JRPropertiesUtil.PROPERTY_PREFIX
			+ "legacy.relative.path.enabled";
	
	private final JasperReportsContext jasperReportsContext;


	/**
	 *
	 */
	private JasperFillManager(JasperReportsContext jasperReportsContext)
	{
		this.jasperReportsContext = jasperReportsContext;
	}
	
	
	/**
	 *
	 */
	private static JasperFillManager getDefaultInstance()
	{
		return new JasperFillManager(DefaultJasperReportsContext.getInstance());
	}
	
	
	/**
	 *
	 */
	public static JasperFillManager getInstance(JasperReportsContext jasperReportsContext)
	{
		return new JasperFillManager(jasperReportsContext);
	}
	
	
	/**
	 * Fills the compiled report design loaded from the specified file.
	 * The result of this operation is another file that will contain the serialized  
	 * {@link JasperPrint} object representing the generated document,
	 * having the same name as the report design as declared in the source file, 
	 * plus the <code>*.jrprint</code> extension, located in the same directory as the source file. 
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param params     report parameters map
	 * @param connection     JDBC connection object to use for executing the report internal SQL query
	 */
	public String fillToFile(
		String sourceFileName, 
		Map<String,Object> params,
		Connection connection
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperReport.getName() + ".jrprint");
		String destFileName = destFile.toString();

		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile, jasperReport), 
				params, connection);
		
		JRSaver.saveObject(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Fills the compiled report design loaded from the specified file.
	 * The result of this operation is another file that will contain the serialized  
	 * {@link JasperPrint} object representing the generated document,
	 * having the same name as the report design as declared in the source file, 
	 * plus the <code>*.jrprint</code> extension, located in the same directory as the source file. 
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param params     report parameters map
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public String fillToFile(
		String sourceFileName, 
		Map<String,Object> params
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperReport.getName() + ".jrprint");
		String destFileName = destFile.toString();

		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile, jasperReport), 
				params);

		JRSaver.saveObject(jasperPrint, destFileName);

		return destFileName;
	}

	
	/**
	 * Fills the compiled report design loaded from the file received as the first parameter
	 * and places the result in the file specified by the second parameter.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param destFileName   file name to place the generated report into
	 * @param params     report parameters map
	 * @param connection     JDBC connection object to use for executing the report internal SQL query
	 */
	public void fillToFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> params,
		Connection connection
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile), 
				params, connection);
		
		JRSaver.saveObject(jasperPrint, destFileName);
	}

	
	/**
	 * Fills the compiled report design loaded from the file received as the first parameter
	 * and places the result in the file specified by the second parameter.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param destFileName   file name to place the generated report into
	 * @param params     report parameters map
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public void fillToFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> params
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile), 
				params);

		JRSaver.saveObject(jasperPrint, destFileName);
	}

	
	/**
	 * Fills the compiled report design received as the first parameter
	 * and places the result in the file specified by the second parameter.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param destFileName file name to place the generated report into
	 * @param parameters   report parameters map
	 * @param connection   JDBC connection object to use for executing the report internal SQL query
	 */
	public void fillToFile(
		JasperReport jasperReport, 
		String destFileName, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		JasperPrint jasperPrint = fill(jasperReport, parameters, connection);

		JRSaver.saveObject(jasperPrint, destFileName);
	}

	
	/**
	 * Fills the compiled report design received as the first parameter
	 * and places the result in the file specified by the second parameter.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param destFileName file name to place the generated report into
	 * @param parameters   report parameters map
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public void fillToFile(
		JasperReport jasperReport, 
		String destFileName, 
		Map<String,Object> parameters
		) throws JRException
	{
		JasperPrint jasperPrint = fill(jasperReport, parameters);

		JRSaver.saveObject(jasperPrint, destFileName);
	}

	
	/**
	 * Fills the compiled report design loaded from the specified file and returns
	 * the generated report object.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param params     report parameters map
	 * @param connection     JDBC connection object to use for executing the report internal SQL query
	 * @return generated report object
	 */
	public JasperPrint fill(
		String sourceFileName, 
		Map<String,Object> params,
		Connection connection
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		return JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile), 
				params, connection);
	}

	
	/**
	 * Fills the compiled report design loaded from the specified file and returns
	 * the generated report object.
	 * 
	 * @param reportLocation the repository location of the compiled report
	 * @param params     report parameters map
	 * @param connection     JDBC connection object to use for executing the report internal SQL query
	 * @return generated report object
	 */
	public JasperPrint fillFromRepo(
		String reportLocation, 
		Map<String,Object> params,
		Connection connection
		) throws JRException
	{
		return JRFiller.fill(jasperReportsContext, 
				getReportSource(reportLocation), 
				params, connection);
	}

	
	/**
	 * Fills the compiled report design loaded from the specified file and returns
	 * the generated report object.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param params     report parameters map
	 * @return generated report object
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public JasperPrint fill(
		String sourceFileName, 
		Map<String,Object> params
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		return JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile), 
				params);
	}

	
	/**
	 * Fills the compiled report design loaded from the specified file and returns
	 * the generated report object.
	 * 
	 * @param reportLocation the repository location of the compiled report
	 * @param params     report parameters map
	 * @return generated report object
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public JasperPrint fillFromRepo(
		String reportLocation, 
		Map<String,Object> params
		) throws JRException
	{
		return JRFiller.fill(jasperReportsContext, 
				getReportSource(reportLocation), 
				params);
	}

	
	/**
	 * Fills the compiled report design loaded from the supplied input stream and writes
	 * the generated report object to the output stream specified by the second parameter.
	 * 
	 * @param inputStream  input stream to read the compiled report design object from
	 * @param outputStream output stream to write the generated report object to
	 * @param parameters   report parameters map
	 * @param connection   JDBC connection object to use for executing the report internal SQL query
	 */
	public void fillToStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, inputStream);

		fillToStream(jasperReport, outputStream, parameters, connection);
	}

	
	/**
	 * Fills the compiled report design loaded from the supplied input stream and writes
	 * the generated report object to the output stream specified by the second parameter.
	 * 
	 * @param inputStream  input stream to read the compiled report design object from
	 * @param outputStream output stream to write the generated report object to
	 * @param parameters   report parameters map
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public void fillToStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters
		) throws JRException
	{
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, inputStream);

		fillToStream(jasperReport, outputStream, parameters);
	}

	
	/**
	 * Fills the compiled report design supplied as the first parameter and writes
	 * the generated report object to the output stream specified by the second parameter.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param outputStream output stream to write the generated report object to
	 * @param parameters   report parameters map
	 * @param connection   JDBC connection object to use for executing the report internal SQL query
	 */
	public void fillToStream(
		JasperReport jasperReport, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		JasperPrint jasperPrint = fill(jasperReport, parameters, connection);

		JRSaver.saveObject(jasperPrint, outputStream);
	}

	
	/**
	 * Fills the compiled report design supplied as the first parameter and writes
	 * the generated report object to the output stream specified by the second parameter.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param outputStream output stream to write the generated report object to
	 * @param parameters   report parameters map
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public void fillToStream(
		JasperReport jasperReport, 
		OutputStream outputStream, 
		Map<String,Object> parameters
		) throws JRException
	{
		JasperPrint jasperPrint = fill(jasperReport, parameters);

		JRSaver.saveObject(jasperPrint, outputStream);
	}

	
	/**
	 * Fills the compiled report design loaded from the supplied input stream and returns
	 * the generated report object.
	 * 
	 * @param inputStream  input stream to read the compiled report design object from
	 * @param parameters   report parameters map
	 * @param connection   JDBC connection object to use for executing the report internal SQL query
	 * @return generated report object
	 */
	public JasperPrint fill(
		InputStream inputStream, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, inputStream);

		return fill(jasperReport, parameters, connection);
	}

	
	/**
	 * Fills the compiled report design loaded from the supplied input stream and returns
	 * the generated report object.
	 * 
	 * @param inputStream  input stream to read the compiled report design object from
	 * @param parameters   report parameters map
	 * @return generated report object
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public JasperPrint fill(
		InputStream inputStream, 
		Map<String,Object> parameters
		) throws JRException
	{
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, inputStream);

		return fill(jasperReport, parameters);
	}

	
	/**
	 * Fills the compiled report design supplied as the first parameter and returns
	 * the generated report object.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param parameters   report parameters map
	 * @param connection   JDBC connection object to use for executing the report internal SQL query
	 * @return generated report object
	 */
	public JasperPrint fill(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		Connection connection
		) throws JRException
	{
		return JRFiller.fill(jasperReportsContext, jasperReport, parameters, connection);
	}

	
	/**
	 * Fills the compiled report design supplied as the first parameter and returns
	 * the generated report object.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param parameters   report parameters map
	 * @return generated report object
	 * @see JRFiller#fill(JasperReportsContext, JasperReport, Map)
	 */
	public JasperPrint fill(
		JasperReport jasperReport, 
		Map<String,Object> parameters 
		) throws JRException
	{
		return JRFiller.fill(jasperReportsContext, jasperReport, parameters);
	}

	
	/**
	 * Fills the compiled report design loaded from the specified file.
	 * The result of this operation is another file that will contain the serialized  
	 * {@link JasperPrint} object representing the generated document,
	 * having the same name as the report design as declared in the source file, 
	 * plus the <code>*.jrprint</code> extension, located in the same directory as the source file. 
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param params     report parameters map
	 * @param dataSource     data source object
	 */
	public String fillToFile(
		String sourceFileName, 
		Map<String,Object> params,
		JRDataSource dataSource
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperReport.getName() + ".jrprint");
		String destFileName = destFile.toString();

		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile, jasperReport), 
				params, dataSource);

		JRSaver.saveObject(jasperPrint, destFileName);
		
		return destFileName;
	}

	
	/**
	 * Fills the compiled report design loaded from the file received as the first parameter
	 * and places the result in the file specified by the second parameter.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param destFileName   file name to place the generated report into
	 * @param params     report parameters map
	 * @param dataSource     data source object
	 */
	public void fillToFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> params,
		JRDataSource dataSource
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		JasperPrint jasperPrint = JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile), 
				params, dataSource);

		JRSaver.saveObject(jasperPrint, destFileName);
	}

	
	/**
	 * Fills the compiled report design received as the first parameter
	 * and places the result in the file specified by the second parameter.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param destFileName file name to place the generated report into
	 * @param parameters   report parameters map
	 * @param dataSource   data source object
	 */
	public void fillToFile(
		JasperReport jasperReport, 
		String destFileName, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		JasperPrint jasperPrint = fill(jasperReport, parameters, dataSource);

		JRSaver.saveObject(jasperPrint, destFileName);
	}

	
	/**
	 * Fills the compiled report design loaded from the specified file and returns
	 * the generated report object.
	 * 
	 * @param sourceFileName source file containing the compiled report design
	 * @param params     report parameters map
	 * @param dataSource     data source object
	 * @return generated report object
	 */
	public JasperPrint fill(
		String sourceFileName, 
		Map<String,Object> params,
		JRDataSource dataSource
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		return JRFiller.fill(jasperReportsContext, 
				getReportSource(sourceFile), 
				params, dataSource);
	}

	
	/**
	 * Fills the compiled report design loaded from the specified file and returns
	 * the generated report object.
	 * 
	 * @param reportLocation the repository location of the compiled report
	 * @param params     report parameters map
	 * @param dataSource     data source object
	 * @return generated report object
	 */
	public JasperPrint fillFromRepo(
		String reportLocation, 
		Map<String,Object> params,
		JRDataSource dataSource
		) throws JRException
	{
		return JRFiller.fill(jasperReportsContext, 
				getReportSource(reportLocation), 
				params, dataSource);
	}

	
	/**
	 * Fills the compiled report design loaded from the supplied input stream and writes
	 * the generated report object to the output stream specified by the second parameter.
	 * 
	 * @param inputStream  input stream to read the compiled report design object from
	 * @param outputStream output stream to write the generated report object to
	 * @param parameters   report parameters map
	 * @param dataSource   data source object
	 */
	public void fillToStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, inputStream);

		fillToStream(jasperReport, outputStream, parameters, dataSource);
	}

	
	/**
	 * Fills the compiled report design supplied as the first parameter and writes
	 * the generated report object to the output stream specified by the second parameter.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param outputStream output stream to write the generated report object to
	 * @param parameters   report parameters map
	 * @param dataSource   data source object
	 */
	public void fillToStream(
		JasperReport jasperReport, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		JasperPrint jasperPrint = fill(jasperReport, parameters, dataSource);

		JRSaver.saveObject(jasperPrint, outputStream);
	}

	
	/**
	 * Fills the compiled report design loaded from the supplied input stream and returns
	 * the generated report object.
	 * 
	 * @param inputStream  input stream to read the compiled report design object from
	 * @param parameters   report parameters map
	 * @param dataSource   data source object
	 * @return generated report object
	 */
	public JasperPrint fill(
		InputStream inputStream, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperReportsContext, inputStream);

		return fill(jasperReport, parameters, dataSource);
	}

	
	/**
	 * Fills the compiled report design supplied as the first parameter and returns
	 * the generated report object.
	 * 
	 * @param jasperReport compiled report design object to use for filling
	 * @param parameters   report parameters map
	 * @param dataSource   data source object
	 * @return generated report object
	 */
	public JasperPrint fill(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		JRDataSource dataSource
		) throws JRException
	{
		return JRFiller.fill(jasperReportsContext, jasperReport, parameters, dataSource);
	}
	
	
	/**
	 * @see #fillToFile(String, Map, Connection)
	 */
	public static String fillReportToFile(
		String sourceFileName, 
		Map<String,Object> params,
		Connection connection
		) throws JRException
	{
		return getDefaultInstance().fillToFile(sourceFileName, params, connection);
	}


	/**
	 * @see #fillToFile(String, Map)
	 */
	public static String fillReportToFile(
		String sourceFileName, 
		Map<String,Object> params
		) throws JRException
	{
		return getDefaultInstance().fillToFile(sourceFileName, params);
	}

	
	/**
	 * @see #fillToFile(String, String, Map, Connection)
	 */
	public static void fillReportToFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> params,
		Connection connection
		) throws JRException
	{
		getDefaultInstance().fillToFile(sourceFileName, destFileName, params, connection);
	}

	
	/**
	 * @see #fillToFile(String, String, Map)
	 */
	public static void fillReportToFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> params
		) throws JRException
	{
		getDefaultInstance().fillToFile(sourceFileName, destFileName, params);
	}

	
	/**
	 * @see #fillToFile(JasperReport, String, Map, Connection)
	 */
	public static void fillReportToFile(
		JasperReport jasperReport, 
		String destFileName, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		getDefaultInstance().fillToFile(jasperReport, destFileName, parameters, connection);
	}

	
	/**
	 * @see #fillToFile(JasperReport, String, Map)
	 */
	public static void fillReportToFile(
		JasperReport jasperReport, 
		String destFileName, 
		Map<String,Object> parameters
		) throws JRException
	{
		getDefaultInstance().fillToFile(jasperReport, destFileName, parameters);
	}

	
	/**
	 * @see #fill(String, Map, Connection)
	 */
	public static JasperPrint fillReport(
		String sourceFileName, 
		Map<String,Object> params,
		Connection connection
		) throws JRException
	{
		return getDefaultInstance().fill(sourceFileName, params, connection);
	}

	
	/**
	 * @see #fill(String, Map)
	 */
	public static JasperPrint fillReport(
		String sourceFileName, 
		Map<String,Object> params
		) throws JRException
	{
		return getDefaultInstance().fill(sourceFileName, params);
	}

	
	/**
	 * @see #fillToStream(InputStream, OutputStream, Map, Connection)
	 */
	public static void fillReportToStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		getDefaultInstance().fillToStream(inputStream, outputStream, parameters, connection);
	}

	
	/**
	 * @see #fillToStream(InputStream, OutputStream, Map)
	 */
	public static void fillReportToStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters
		) throws JRException
	{
		getDefaultInstance().fillToStream(inputStream, outputStream, parameters);
	}

	
	/**
	 * @see #fillToStream(JasperReport, OutputStream, Map, Connection)
	 */
	public static void fillReportToStream(
		JasperReport jasperReport, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		getDefaultInstance().fillToStream(jasperReport, outputStream, parameters, connection);
	}

	
	/**
	 * @see #fillToStream(JasperReport, OutputStream, Map)
	 */
	public static void fillReportToStream(
		JasperReport jasperReport, 
		OutputStream outputStream, 
		Map<String,Object> parameters
		) throws JRException
	{
		getDefaultInstance().fillToStream(jasperReport, outputStream, parameters);
	}

	
	/**
	 * @see #fill(InputStream, Map, Connection)
	 */
	public static JasperPrint fillReport(
		InputStream inputStream, 
		Map<String,Object> parameters,
		Connection connection
		) throws JRException
	{
		return getDefaultInstance().fill(inputStream, parameters, connection);
	}

	
	/**
	 * @see #fill(InputStream, Map)
	 */
	public static JasperPrint fillReport(
		InputStream inputStream, 
		Map<String,Object> parameters
		) throws JRException
	{
		return getDefaultInstance().fill(inputStream, parameters);
	}

	
	/**
	 * @see #fill(JasperReport, Map, Connection)
	 */
	public static JasperPrint fillReport(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		Connection connection
		) throws JRException
	{
		return getDefaultInstance().fill(jasperReport, parameters, connection);
	}

	
	/**
	 * @see #fill(JasperReport, Map)
	 */
	public static JasperPrint fillReport(
		JasperReport jasperReport, 
		Map<String,Object> parameters 
		) throws JRException
	{
		return getDefaultInstance().fill(jasperReport, parameters);
	}

	
	/**
	 * @see #fillToFile(String, Map, JRDataSource)
	 */
	public static String fillReportToFile(
		String sourceFileName, 
		Map<String,Object> params,
		JRDataSource dataSource
		) throws JRException
	{
		return getDefaultInstance().fillToFile(sourceFileName, params, dataSource);
	}

	
	/**
	 * @see #fillToFile(String, String, Map, JRDataSource)
	 */
	public static void fillReportToFile(
		String sourceFileName, 
		String destFileName, 
		Map<String,Object> params,
		JRDataSource dataSource
		) throws JRException
	{
		getDefaultInstance().fillToFile(sourceFileName, destFileName, params, dataSource);
	}

	
	/**
	 * @see #fillToFile(JasperReport, String, Map, JRDataSource)
	 */
	public static void fillReportToFile(
		JasperReport jasperReport, 
		String destFileName, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		getDefaultInstance().fillToFile(jasperReport, destFileName, parameters, dataSource);
	}

	
	/**
	 * @see #fill(String, Map, JRDataSource)
	 */
	public static JasperPrint fillReport(
		String sourceFileName, 
		Map<String,Object> params,
		JRDataSource dataSource
		) throws JRException
	{
		return getDefaultInstance().fill(sourceFileName, params, dataSource);
	}

	
	/**
	 * @see #fillToStream(InputStream, OutputStream, Map, JRDataSource)
	 */
	public static void fillReportToStream(
		InputStream inputStream, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		getDefaultInstance().fillToStream(inputStream, outputStream, parameters, dataSource);
	}

	
	/**
	 * @see #fillToStream(JasperReport, OutputStream, Map, JRDataSource)
	 */
	public static void fillReportToStream(
		JasperReport jasperReport, 
		OutputStream outputStream, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		getDefaultInstance().fillToStream(jasperReport, outputStream, parameters, dataSource);
	}

	
	/**
	 * @see #fill(InputStream, Map, JRDataSource)
	 */
	public static JasperPrint fillReport(
		InputStream inputStream, 
		Map<String,Object> parameters,
		JRDataSource dataSource
		) throws JRException
	{
		return getDefaultInstance().fill(inputStream, parameters, dataSource);
	}

	
	/**
	 * @see #fill(JasperReport, Map, JRDataSource)
	 */
	public static JasperPrint fillReport(
		JasperReport jasperReport, 
		Map<String,Object> parameters, 
		JRDataSource dataSource
		) throws JRException
	{
		return getDefaultInstance().fill(jasperReport, parameters, dataSource);
	}


	protected static JasperReportSource getReportSource(JasperReportsContext jasperReportsContext, 
			File reportFile) throws JRException
	{
		JasperFillManager manager = getInstance(jasperReportsContext);
		return manager.getReportSource(reportFile);
	}
	
	protected JasperReportSource getReportSource(File reportFile) throws JRException
	{
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperReportsContext, reportFile);
		return getReportSource(reportFile, jasperReport);
	}

	protected JasperReportSource getReportSource(File reportFile, JasperReport jasperReport)
	{
		//attempting resolve absolute paths as relative, that's what SimpleFileResolver(".") did
		RepositoryResourceContext fallbackContext = SimpleRepositoryResourceContext.of(".");
		SimpleRepositoryResourceContext reportContext = SimpleRepositoryResourceContext.of(
				reportFile.getParent(), fallbackContext);
		
		boolean legacyRelativePath = JRPropertiesUtil.getInstance(jasperReportsContext).getBooleanProperty(jasperReport, 
				PROPERTY_LEGACY_RELATIVE_PATH_ENABLED, false);
		if (legacyRelativePath)
		{
			//attempt to resolve paths as relative to the master report for backward compatibility
			reportContext.setSelfAsDerivedFallback(true);
		}
		
		return SimpleJasperReportSource.from(jasperReport, reportFile.getPath(), reportContext);
	}
	
	protected JasperReportSource getReportSource(String location) throws JRException
	{
		RepositoryUtil repository = RepositoryUtil.getInstance(jasperReportsContext);
		ResourceInfo resourceInfo = repository.getResourceInfo(location);
		JasperReportSource source;
		if (resourceInfo == null)
		{
			JasperReport report = repository.getReport(null, location);
			source = SimpleJasperReportSource.from(report, location, null);
		}
		else
		{
			String reportLocation = resourceInfo.getRepositoryResourceLocation();
			String contextLocation = resourceInfo.getRepositoryContextLocation();
			if (log.isDebugEnabled())
			{
				log.debug("report location " + location + " resolved to " + reportLocation
						+ ", context " + contextLocation);
			}
			
			JasperReport report = repository.getReport(null, reportLocation);
			source = SimpleJasperReportSource.from(report, reportLocation, 
					SimpleRepositoryResourceContext.of(contextLocation));
		}
		return source;
	}
}
