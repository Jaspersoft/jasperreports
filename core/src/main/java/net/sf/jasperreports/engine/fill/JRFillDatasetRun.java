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
package net.sf.jasperreports.engine.fill;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.data.cache.DataCacheHandler;
import net.sf.jasperreports.engine.CommonReturnValue;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDatasetParameter;
import net.sf.jasperreports.engine.JRDatasetRun;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRScriptletException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.VariableReturnValue;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;

/**
 * Class used to instantiate sub datasets.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillDatasetRun implements JRDatasetRun
{
	
	private static final Log log = LogFactory.getLog(JRFillDatasetRun.class);
	
	protected final BaseReportFiller filler;
	protected final JRFillExpressionEvaluator expressionEvaluator;

	protected final JRDatasetRun parentDatasetRun;
	protected final JRFillDataset dataset;

	protected JRExpression parametersMapExpression;

	protected JRDatasetParameter[] parameters;

	protected JRExpression connectionExpression;

	protected JRExpression dataSourceExpression;

	private FillReturnValues returnValues;
	private FillReturnValues.SourceContext returnValuesContext;
	
	/**
	 * Construct an instance for a dataset run.
	 * 
	 * @param filler the filler
	 * @param datasetRun the dataset run
	 * @param factory the fill object factory
	 */
	public JRFillDatasetRun(JRBaseFiller filler, JRDatasetRun datasetRun, JRFillObjectFactory factory)
	{
		this(filler, filler.getExpressionEvaluator(), datasetRun, factory);
	}

	protected JRFillDatasetRun(JRDatasetRun datasetRun, JRFillObjectFactory factory)
	{
		this(factory.getFiller(), factory.getExpressionEvaluator(), datasetRun, factory);
	}

	protected JRFillDatasetRun(JRBaseFiller filler, JRFillExpressionEvaluator expressionEvaluator,
			JRDatasetRun datasetRun, JRFillObjectFactory factory)
	{
		this(filler, expressionEvaluator, datasetRun, 
				filler.datasetMap.get(datasetRun.getDatasetName()));
		
		factory.put(datasetRun, this);
		
		initReturnValues(factory);
	}

	protected JRFillDatasetRun(BaseReportFiller filler, JRDatasetRun datasetRun, 
			JRFillDataset dataset)
	{
		this(filler, dataset.calculator, datasetRun, dataset);
	}

	protected JRFillDatasetRun(BaseReportFiller filler, JRFillExpressionEvaluator expressionEvaluator, 
			JRDatasetRun datasetRun, JRFillDataset dataset)
	{
		this.filler = filler;
		this.expressionEvaluator = expressionEvaluator;
		this.dataset = dataset;

		this.parentDatasetRun = datasetRun;
		parametersMapExpression = datasetRun.getParametersMapExpression();
		parameters = datasetRun.getParameters();
		connectionExpression = datasetRun.getConnectionExpression();
		dataSourceExpression = datasetRun.getDataSourceExpression();
	}

	public JRFillDatasetRun(JRFillDatasetRun datasetRun, JRFillCloneFactory factory)
	{
		this.filler = datasetRun.filler;
		this.expressionEvaluator = datasetRun.expressionEvaluator;
		this.dataset = datasetRun.dataset;
		
		this.parentDatasetRun = datasetRun.parentDatasetRun;
		this.parametersMapExpression = datasetRun.parametersMapExpression;
		this.parameters = datasetRun.parameters;
		this.connectionExpression = datasetRun.getConnectionExpression();
		this.dataSourceExpression = datasetRun.getDataSourceExpression();
		
		this.returnValues = new FillReturnValues(datasetRun.returnValues, factory);
		this.returnValuesContext = datasetRun.returnValuesContext;
	}

	protected void initReturnValues(JRFillObjectFactory factory)
	{
		if (log.isDebugEnabled())
		{
			log.debug("init return values");
		}
		
		returnValues = new FillReturnValues(parentDatasetRun.getReturnValues(), factory, filler);
		
		returnValuesContext = new AbstractVariableReturnValueSourceContext() 
		{
			@Override
			public Object getValue(CommonReturnValue returnValue) {
				return dataset.getVariableValue(((VariableReturnValue)returnValue).getFromVariable());
			}
			
			@Override
			public JRFillVariable getToVariable(String name) {
				return expressionEvaluator.getFillDataset().getVariable(name);
			}
			
			@Override
			public JRVariable getFromVariable(String name) {
				return dataset.getVariable(name);
			}
		};
	}

	public void setBand(JRFillBand band)
	{
		if (returnValues != null)
		{
			returnValues.setBand(band);
		}
	}
	
	/**
	 * Instantiates and iterates the sub dataset for a chart dataset evaluation.
	 * 
	 * @param elementDataset the chart dataset
	 * @param evaluation the evaluation type
	 * @throws JRException
	 */
	public void evaluate(JRFillElementDataset elementDataset, byte evaluation) throws JRException
	{
		if (returnValues != null)
		{
			try
			{
				//FIXME do this at compile time
				returnValues.checkReturnValues(returnValuesContext);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}
		
		saveReturnVariables();
		
		Map<String,Object> parameterValues = 
			JRFillSubreport.getParameterValues(
				filler,
				expressionEvaluator,
				parametersMapExpression, 
				parameters, 
				evaluation, 
				false, 
				dataset.getResourceBundle() != null,//hasResourceBundle
				false//hasFormatFactory
				);

		try
		{
			// set fill position for caching
			FillDatasetPosition datasetPosition = new FillDatasetPosition(expressionEvaluator.getFillDataset().fillPosition);
			datasetPosition.addAttribute("datasetRunUUID", getUUID());
			expressionEvaluator.getFillDataset().setCacheRecordIndex(datasetPosition, evaluation);		
			dataset.setFillPosition(datasetPosition);
			
			String cacheIncludedProp = JRPropertiesUtil.getOwnProperty(this, DataCacheHandler.PROPERTY_INCLUDED); 
			boolean cacheIncluded = JRPropertiesUtil.asBoolean(cacheIncludedProp, true);// default to true
			dataset.setCacheSkipped(!cacheIncluded);
			
			if (dataSourceExpression != null)
			{
				if (!(filler.fillContext.hasDataSnapshot() && cacheIncluded)) 
				{
					JRDataSource dataSource = (JRDataSource) expressionEvaluator.evaluate(dataSourceExpression, evaluation);
					dataset.setDatasourceParameterValue(parameterValues, dataSource);
				}
			}
			else if (connectionExpression != null)
			{
				Connection connection = (Connection) expressionEvaluator.evaluate(connectionExpression, evaluation);
				dataset.setConnectionParameterValue(parameterValues, connection);
			}

			copyConnectionParameter(parameterValues);
			
			dataset.filterElementDatasets(elementDataset);
			dataset.initCalculator();
			dataset.setParameterValues(parameterValues);
			dataset.evaluateFieldProperties();
			dataset.initDatasource();

			iterate();
		}
		finally
		{
			dataset.closeDatasource();
			dataset.disposeParameterContributors();
			dataset.restoreElementDatasets();
		}
		
		copyReturnValues();
	}

	protected void saveReturnVariables()
	{
		if (returnValues != null)
		{
			returnValues.saveReturnVariables();
		}
	}

	public void copyReturnValues()
	{
		if (returnValues != null)
		{
			returnValues.copyValues(returnValuesContext);
		}
	}

	protected void copyConnectionParameter(Map<String,Object> parameterValues)
	{
		JRQuery query = dataset.getQuery();
		if (query != null)
		{
			String language = query.getLanguage();
			if (connectionExpression == null && 
					(language.equals("sql") || language.equals("SQL")) &&
					!parameterValues.containsKey(JRParameter.REPORT_CONNECTION))
			{
				JRFillParameter connParam = expressionEvaluator.getFillDataset().getParametersMap().get(JRParameter.REPORT_CONNECTION);
				Connection connection = (Connection) connParam.getValue();
				parameterValues.put(JRParameter.REPORT_CONNECTION, connection);
			}
		}
	}

	protected void iterate() throws JRException
	{
		dataset.start();

		if (advanceDataset())
		{
			startData();

			detail();

			while (advanceDataset())
			{
				checkInterrupted();

				group();

				detail();
			}
		}
		else if (toStartWhenNoData())
		{
			startData();
		}
	}
	
	protected boolean toStartWhenNoData()
	{
		//needed for initializing element datasets
		return true;
	}

	protected boolean advanceDataset() throws JRException
	{
		return dataset.next();
	}

	
	protected void checkInterrupted()
	{
		if (filler != null)
		{
			filler.checkInterrupted();
		}
	}

	
	protected void group() throws JRException, JRScriptletException
	{
		dataset.calculator.estimateGroupRuptures();

		dataset.delegateScriptlet.callBeforeGroupInit();
		dataset.calculator.initializeVariables(ResetTypeEnum.GROUP, IncrementTypeEnum.GROUP);
		dataset.delegateScriptlet.callAfterGroupInit();
	}

	protected void startData() throws JRScriptletException, JRException
	{
		dataset.delegateScriptlet.callBeforeReportInit();
		dataset.calculator.initializeVariables(ResetTypeEnum.REPORT, IncrementTypeEnum.REPORT);
		dataset.delegateScriptlet.callAfterReportInit();
	}

	protected void detail() throws JRScriptletException, JRException
	{
		dataset.delegateScriptlet.callBeforeDetailEval();
		dataset.calculator.calculateVariables(true);
		dataset.delegateScriptlet.callAfterDetailEval();
	}

	@Override
	public String getDatasetName()
	{
		return dataset.getName();
	}

	@Override
	public JRExpression getParametersMapExpression()
	{
		return parametersMapExpression;
	}

	@Override
	public JRDatasetParameter[] getParameters()
	{
		return parameters;
	}

	@Override
	public JRExpression getConnectionExpression()
	{
		return connectionExpression;
	}

	@Override
	public JRExpression getDataSourceExpression()
	{
		return dataSourceExpression;
	}
	
	protected JRFillDataset getDataset()
	{
		return dataset;
	}

	@Override
	public UUID getUUID()
	{
		return parentDatasetRun.getUUID();
	}
	
	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean hasProperties()
	{
		return parentDatasetRun.hasProperties();
	}

	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return parentDatasetRun.getPropertiesMap();
	}
	
	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

	@Override
	public List<ReturnValue> getReturnValues()
	{
		return parentDatasetRun.getReturnValues();
	}
}
