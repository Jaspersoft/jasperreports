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
package net.sf.jasperreports.engine.fill;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

import net.sf.jasperreports.engine.JRBiConsumer;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class DatasetExecution
{

	private JasperReport report;
	private Map<String, Object> parameterValues;
	private JRFillDataset fillDataset;

	public DatasetExecution(RepositoryContext repositoryContext, JasperReport report, Map<String,Object> parameters)
	{
		this.report = report;
		parameterValues = parameters == null ? new HashMap<>() : new HashMap<>(parameters);
		parameterValues.put(JRParameter.JASPER_REPORT, report);
		
		ObjectFactory factory = new ObjectFactory();
		JRDataset reportDataset = report.getMainDataset();
		fillDataset = factory.getDataset(reportDataset);
		
		@SuppressWarnings("deprecation")
		JasperReportsContext depContext = 
			net.sf.jasperreports.engine.util.LocalJasperReportsContext.getLocalContext(repositoryContext.getJasperReportsContext(), parameters);
		RepositoryContext fillRepositoryContext = depContext == repositoryContext.getJasperReportsContext() ? repositoryContext
				: SimpleRepositoryContext.of(depContext, repositoryContext.getResourceContext());
		fillDataset.setRepositoryContext(fillRepositoryContext);
	}

	public void evaluateParameters(BiConsumer<JRParameter, Object> parameterConsumer) throws JRException
	{
		boolean prepareData = fillDataset.getPropertiesUtil().getBooleanProperty(fillDataset, 
				JRParameterDefaultValuesEvaluator.PROPERTY_PARAMETER_DEFAULTS_EVALUATOR_PREPARES_DATA, false);
		try
		{
			runWithParameters(() -> 
			{
				JRParameter[] parameters = fillDataset.getParameters();
				for (JRParameter param : parameters)
				{
					Object value = fillDataset.getParameterValue(param.getName());
					parameterConsumer.accept(param, value);
				}
				return null;
			}, prepareData);
		}
		catch (JRException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	protected <R> R runWithParameters(Callable<R> action, boolean prepareData) throws Exception
	{
		fillDataset.createCalculator(report);
		fillDataset.initCalculator();

		JRResourcesFillUtil.ResourcesFillContext resourcesContext = 
			JRResourcesFillUtil.setResourcesFillContext(parameterValues);
		try
		{
			fillDataset.setParameterValues(parameterValues, prepareData);
			
			return action.call();
		}
		finally
		{
			fillDataset.disposeParameterContributors();
			JRResourcesFillUtil.revertResourcesFillContext(resourcesContext);
		}		
	}
	
	public void evaluateDataSource(JRBiConsumer<JRDataSource, Map<String, Object>> dataSourceConsumer) throws Exception
	{
		runWithParameters(() ->
		{
			try
			{
				fillDataset.evaluateFieldProperties();
				fillDataset.initDatasource();
				
				dataSourceConsumer.accept(fillDataset.dataSource, parameterValues);
				return null;
			}
			finally
			{
				fillDataset.closeDatasource();
			}
		}, true);
	}

	protected static class ObjectFactory extends JRFillObjectFactory
	{
		protected ObjectFactory()
		{
			super((JRBaseFiller) null, null);
		}

		@Override
		public JRFillGroup getGroup(JRGroup group)
		{
			JRDesignGroup dummyGroup = new JRDesignGroup();
			dummyGroup.setName("DUMMY_GROUP");
			return super.getGroup(dummyGroup);
		}
	}
	
}
