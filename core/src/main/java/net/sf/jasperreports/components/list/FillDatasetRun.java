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
package net.sf.jasperreports.components.list;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.data.cache.DataCacheHandler;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.FillDatasetPosition;
import net.sf.jasperreports.engine.fill.JRFillCloneFactory;
import net.sf.jasperreports.engine.fill.JRFillDataset;
import net.sf.jasperreports.engine.fill.JRFillDatasetRun;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.fill.JRFillSubreport;
import net.sf.jasperreports.engine.util.JRReportUtils;

/**
 * Used to iterate on the list subdataset at fill time.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillDatasetRun extends JRFillDatasetRun
{

	private static final Log log = LogFactory.getLog(FillDatasetRun.class);
	
	private Map<String, Object> parameterValues;
	private FillDatasetPosition datasetPosition;
	private boolean cacheIncluded;
	private JRDataSource dataSource;
	private Connection connection;
	private boolean first;
	
	public FillDatasetRun(JRDatasetRun datasetRun,
			JRFillObjectFactory factory) throws JRException
	{
		super(factory.getReportFiller(), factory.getExpressionEvaluator(),
				datasetRun, 
				createFillDataset(datasetRun, factory));
		
		initReturnValues(factory);
		factory.registerDatasetRun(this);
	}

	public FillDatasetRun(FillDatasetRun datasetRun, JRFillCloneFactory factory)
	{
		super(datasetRun, factory);
	}

	private static JRFillDataset createFillDataset(JRDatasetRun datasetRun,
			JRFillObjectFactory factory) throws JRException
	{
		JasperReport jasperReport = factory.getReportFiller().getJasperReport();
		JRDataset reportDataset = JRReportUtils.findSubdataset(datasetRun, (JRReport)jasperReport);
		
		FillListDatasetFactory datasetFactory = new FillListDatasetFactory(factory);
		JRFillDataset fillDataset = datasetFactory.getDataset(reportDataset);
		fillDataset.createCalculator(jasperReport);
		fillDataset.inheritFromMain();
		return fillDataset;
	}
	
	public void evaluate(byte evaluation) throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug("Evaluating list dataset run parameters");
		}
		
		saveReturnVariables();
		
		parameterValues = JRFillSubreport.getParameterValues(
			filler, 
			expressionEvaluator,
			parametersMapExpression, 
			parameters, 
			evaluation, 
			false, 
			dataset.getResourceBundle() != null,//hasResourceBundle
			false//hasFormatFactory
			);
		
		JRFillDataset parentDataset = expressionEvaluator.getFillDataset();
		datasetPosition = new FillDatasetPosition(parentDataset.getDatasetPosition());
		datasetPosition.addAttribute("datasetRunUUID", getUUID());
		parentDataset.setCacheRecordIndex(datasetPosition, evaluation);
		
		String cacheIncludedProp = JRPropertiesUtil.getOwnProperty(this, DataCacheHandler.PROPERTY_INCLUDED); 
		cacheIncluded = JRPropertiesUtil.asBoolean(cacheIncludedProp, true);// default to true

		if (dataSourceExpression != null)
		{
			if (filler.getFillContext().hasDataSnapshot() && cacheIncluded) 
			{
				dataSource = null;
			}
			else
			{
				dataSource = (JRDataSource) expressionEvaluator.evaluate(
						dataSourceExpression, evaluation);
			}
		}
		else if (connectionExpression != null)
		{
			connection = (Connection) expressionEvaluator.evaluate(
					connectionExpression, evaluation);
		}
	}
	
	public void start() throws JRException
	{
		if (log.isDebugEnabled())
		{
			log.debug("Starting list dataset iteration");
		}
		
		if (dataSourceExpression != null)
		{
			dataset.setDatasourceParameterValue(parameterValues, dataSource);
		}
		else if (connectionExpression != null)
		{
			dataset.setConnectionParameterValue(parameterValues, connection);
		}

		// set fill position for caching
		dataset.setFillPosition(datasetPosition);
		dataset.setCacheSkipped(!cacheIncluded);

		copyConnectionParameter(parameterValues);
		dataset.initCalculator();
		dataset.setParameterValues(parameterValues);
		dataset.evaluateFieldProperties();
		dataset.initDatasource();
		
		dataset.start();
		first = true;
	}
	
	public boolean next() throws JRException
	{
		checkInterrupted();
		
		if (dataset.next())
		{
			if (first)
			{
				startData();
				first = false;
			}
			else
			{
				group();
			}
			
			detail();
			
			return true;
		}
		
		return false;
	}
	
	public void end()
	{
		if (log.isDebugEnabled())
		{
			log.debug("Closing the data source");
		}
		
		dataset.closeDatasource();
		dataset.disposeParameterContributors();
	}
	
	public void rewind() throws JRException
	{
		if (dataSource != null)
		{
			if (dataSource instanceof JRRewindableDataSource)
			{
				if (log.isDebugEnabled())
				{
					log.debug("Rewinding the list data source");
				}
				
				((JRRewindableDataSource) dataSource).moveFirst();
			}
			else
			{
				log.warn("Cannot rewind list data source as it is not a JRRewindableDataSource");
			}
		}
	}
	
	public Object evaluateDatasetExpression(JRExpression expression, byte evaluationType) 
		throws JRException
	{
		return dataset.evaluateExpression(expression, evaluationType);
	}

	@Override
	protected JRFillDataset getDataset()
	{
		// only added here for visibility
		return super.getDataset();
	}
	
	
}
