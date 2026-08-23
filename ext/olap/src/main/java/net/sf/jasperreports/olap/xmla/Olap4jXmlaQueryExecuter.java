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
package net.sf.jasperreports.olap.xmla;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.CellSetAxis;
import org.olap4j.CellSetAxisMetaData;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.Position;
import org.olap4j.layout.CellSetFormatter;
import org.olap4j.layout.RectangularCellSetFormatter;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRValueParameter;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.query.JRAbstractQueryExecuter;
import net.sf.jasperreports.olap.JRMdxQueryExecuterFactory;
import net.sf.jasperreports.olap.JROlapDataSource;



/**
 * @author swood
 */
public class Olap4jXmlaQueryExecuter extends JRAbstractQueryExecuter
{
	public static final String EXCEPTION_MESSAGE_KEY_CONNECTION_ERROR = "query.xmla.connection.error";
	public static final String EXCEPTION_MESSAGE_KEY_EXECUTE_QUERY_ERROR = "query.xmla.execute.query.error";
	
	public static final String OLAP4J_DRIVER = "olap4jDriver";
	public static final String OLAP4J_URL_PREFIX = "urlPrefix";
	public static final String XMLA_SERVER = "server";
	public static final String XMLA_CATALOG = "catalog";
	public static final String XMLA_DATA_SOURCE = "dataSource";
	public static final String XMLA_USER = "user";
	public static final String XMLA_PASSWORD = "password";
	public static final String OLAP4J_XMLA_DRIVER_CLASS = "org.olap4j.driver.xmla.XmlaOlap4jDriver";
	public static final String OLAP4J_XMLA_URL_PREFIX = "jdbc:xmla:";

	private static final Log log = LogFactory.getLog(Olap4jXmlaQueryExecuter.class);
	
	final DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
	
	private java.sql.Connection rConnection;
	private JRXmlaResult xmlaResult;


	/**
	 * 
	 */
	public Olap4jXmlaQueryExecuter(
		JasperReportsContext jasperReportsContext,
		JRDataset dataset, 
		Map<String, ? extends JRValueParameter> parametersMap
		)
	{
		super(jasperReportsContext, dataset, parametersMap);

		parseQuery();
	}

	@Override
	protected String getCanonicalQueryLanguage()
	{
		return JRMdxQueryExecuterFactory.CANONICAL_LANGUAGE;
	}

	@Override
	protected String getParameterReplacement(String parameterName)
	{
		return String.valueOf(getParameterValue(parameterName));
	}

	
	@Override
	public JRDataSource createDatasource() throws JRException
	{
		
		Properties connectProps = new Properties();
		connectProps.put(XMLA_SERVER, getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_URL));
		connectProps.put(XMLA_CATALOG, getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_CATALOG));
		connectProps.put(XMLA_DATA_SOURCE, getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_DATASOURCE));
		connectProps.put(XMLA_USER, getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_USER));
		connectProps.put(XMLA_PASSWORD, getParameterValue(JRXmlaQueryExecuterFactory.PARAMETER_XMLA_PASSWORD));
		connectProps.put(OLAP4J_DRIVER, OLAP4J_XMLA_DRIVER_CLASS);
		connectProps.put(OLAP4J_URL_PREFIX, OLAP4J_XMLA_URL_PREFIX);
		
		// load driver  and Connection
		rConnection = null;
		try
		{
			Class.forName(OLAP4J_XMLA_DRIVER_CLASS);
			rConnection = java.sql.DriverManager.getConnection(OLAP4J_XMLA_URL_PREFIX, connectProps);
		}
		catch (Throwable t) 
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_CONNECTION_ERROR,
					new Object[]{OLAP4J_XMLA_DRIVER_CLASS},
					t);
		}

		OlapConnection connection = (OlapConnection) rConnection;
		
		/*
		MdxParserFactory pFactory = connection.getParserFactory();
		MdxParser parser = pFactory.createMdxParser(connection);
		SelectNode parsedObject = parser.parseSelect(getQueryString());
		MdxValidator validator = pFactory.createMdxValidator(connection);
		try {
			validator.validateSelect(parsedObject);
		} catch (OlapException e) {
			throw new JRException("Invalid MDX query: " + getQueryString(), e);
		}
		*/
		if (log.isDebugEnabled())
		{
			log.debug("running MDX: " + getQueryString());
		}
		CellSet result = null;
		try
		{
			OlapStatement statement = connection.createStatement();

			result = statement.executeOlapQuery(getQueryString());
		}
		catch (OlapException e)
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_EXECUTE_QUERY_ERROR,
					new Object[]{getQueryString()},
					e);
		}
		
		parseResult(result);
		
		return new JROlapDataSource(dataset, xmlaResult);
	}

	@Override
	public boolean cancelQuery() throws JRException
	{
		return false;
	}

	@Override
	public void close()
	{
		if (rConnection != null)
		{
			try
			{
				rConnection.close();
			}
			catch (SQLException e)
			{
				throw new JRRuntimeException(e);
			}
			rConnection = null;
		}
	}

	/**
	 * Parses the result into this class's structure
	 * 
	 * @param result
	 *            The CellSet query result
	 */
	protected void parseResult(CellSet result) throws JRException
	{
		if (log.isDebugEnabled())
		{
			OutputStream bos = new ByteArrayOutputStream();
			CellSetFormatter formatter = new RectangularCellSetFormatter(true);
			formatter.format(result, new PrintWriter(bos, true));
			log.debug("Result:\n" + bos.toString());
		}

		xmlaResult = new JRXmlaResult();
		
		parseAxes(result);

		parseCellDataElement(result);
	}

	protected void parseAxes(CellSet result)
	{
		// Cycle over AxisInfo-Elements
		Iterator<CellSetAxis> itAxis = result.getAxes().iterator();
		log.debug("# axes: " + result.getAxes().size());
		while (itAxis.hasNext())
		{
			CellSetAxis itAxisElement = itAxis.next();
			Axis axis = itAxisElement.getAxisMetaData().getAxisOrdinal();
			if (axis.axisOrdinal() == Axis.FILTER.axisOrdinal())
			{
				if (log.isDebugEnabled())
				{
					log.debug("skipping filter axis: " + axis.name() + ", ordinal: " + axis.axisOrdinal());
				}
				continue;
			}

			JRXmlaResultAxis xmlaAxis = new JRXmlaResultAxis(axis.name());
			xmlaResult.addAxis(xmlaAxis);
			
			if (log.isDebugEnabled())
			{
				log.debug("adding axis: " + axis.name() + ", ordinal: " + axis.axisOrdinal());
			}
			handleHierInfo(xmlaAxis, itAxisElement.getAxisMetaData());
			
			ListIterator<Position> positionsIt = itAxisElement.iterator();
			while (positionsIt.hasNext())
			{
				Position p = positionsIt.next();
				if (log.isDebugEnabled())
				{
					log.debug("adding pos : " + p.getOrdinal() + ", with member size: " + p.getMembers().size());
				}
				handlePosition(xmlaAxis, itAxisElement, p);	
			}
			
		}
	}

	protected void parseCellDataElement(CellSet result) throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug("COLUMNS axis size: " + result.getAxes().get(Axis.COLUMNS.axisOrdinal())
				.getPositions().size());
			log.debug("ROWS axis size: " + result.getAxes().get(Axis.ROWS.axisOrdinal())
				.getPositions().size());
		}
		for (Position axis_1 : result.getAxes().get(Axis.ROWS.axisOrdinal()).getPositions())
		{
			for (Position axis_0 : result.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositions())
			{
				Cell currentCell = result.getCell(axis_0, axis_1);
				if (currentCell.isError())
				{
					handleCellErrors(currentCell);
				}
				else
				{
					int cellOrdinal = currentCell.getOrdinal();
					Object value = currentCell.getValue();
					if (value instanceof Number)
					{
						try
						{
							value = currentCell.getDoubleValue();
						}
						catch (OlapException e)
						{
							throw new JRException(e);
						}
					}
					
					String fmtValue = currentCell.getFormattedValue();
					JRXmlaCell cell = new JRXmlaCell(value, fmtValue);
					if (log.isDebugEnabled())
					{
						log.debug("Cell: " + cellOrdinal + ", at axis 0 pos: " + axis_0.getOrdinal() + ", axis 1 pos: " + axis_1.getOrdinal());
					}
					xmlaResult.setCell(cell, cellOrdinal);
				}
			}
		}
	}

	protected void handleCellErrors(Cell currentCell) throws JRException
	{
		log.error(currentCell.getValue());
		
		throw new JRException((OlapException) currentCell.getValue());
	}

	protected void handleHierInfo(JRXmlaResultAxis xmlaAxis, CellSetAxisMetaData axisMetaData)
	{
		for (Hierarchy h : axisMetaData.getHierarchies())
		{
			String dimName = h.getName(); // Get the Dimension Name

			JRXmlaHierarchy hier = new JRXmlaHierarchy(dimName);
			if (log.isDebugEnabled())
			{
				log.debug("Adding hierarchy: " + dimName);
			}
			xmlaAxis.addHierarchy(hier);
		}
	}

	protected void handlePosition(JRXmlaResultAxis axis, CellSetAxis itAxisElement, Position p)
	{
		JRXmlaMemberTuple tuple = new JRXmlaMemberTuple(p.getMembers().size());
		int memNum = 0;

		for (Member m : p.getMembers())
		{
			String hierName = m.getHierarchy().getName();
			String uName = m.getUniqueName();
			/* the OLAP4J API changed between 0.9.7 and 1.1.0 - deal with it */
			String caption = null;
			try
			{
				Method captionMethod = Member.class.getMethod("getCaption", new Class[] { Locale.class });
				try
				{
					caption = (String) captionMethod.invoke(m, new Object[]{null});
				}
				catch (Exception e)
				{
					throw new JRRuntimeException(e);
				}
			}
			catch (SecurityException e)
			{
				throw new JRRuntimeException(e);
			} 
			catch (NoSuchMethodException e)
			{
				try 
				{
					// This is an olap4j 1.1.0+ member with a getCaption() method
					Method captionMethod = Member.class.getMethod("getCaption");
					caption = (String) captionMethod.invoke(m);
				}
				catch (Exception e1)
				{
					throw new JRRuntimeException(e1);
				}
			}
			
			String lName = m.getLevel().getName();
			int lNum = m.getLevel().getDepth();
			if (log.isDebugEnabled())
			{
				log.debug("setting hierarchy: " + hierName + ", Level " + lName + ", level number: " + lNum);
			}
			JRXmlaMember member = new JRXmlaMember(caption, uName, hierName, lName, lNum);
			tuple.setMember(memNum++, member);
		}
		axis.addTuple(tuple);
	}
}
