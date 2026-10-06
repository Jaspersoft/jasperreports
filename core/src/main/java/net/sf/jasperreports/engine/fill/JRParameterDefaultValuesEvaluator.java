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

import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.annotations.properties.Property;
import net.sf.jasperreports.annotations.properties.PropertyScope;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.properties.PropertyConstants;
import net.sf.jasperreports.repo.RepositoryContext;
import net.sf.jasperreports.repo.SimpleRepositoryContext;


/**
 * Utility class to be used to evaluate parameter default value expressions for a report
 * without actually filling it.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public final class JRParameterDefaultValuesEvaluator
{
	
	@Property(
			category = PropertyConstants.CATEGORY_OTHER,
			scopes = {PropertyScope.CONTEXT, PropertyScope.REPORT, PropertyScope.DATASET},
			sinceVersion = PropertyConstants.VERSION_7_0_6,
			valueType = Boolean.class,
			defaultValue = "false"
			)
	public static final String PROPERTY_PARAMETER_DEFAULTS_EVALUATOR_PREPARES_DATA = 
			JRPropertiesUtil.PROPERTY_PREFIX + "parameter.defaults.evaluator.prepares.data";

	/**
	 * Evaluates the default values for the parameters of a report.
	 * 
	 * @param report the report
	 * @param initialParameters initial parameter value map
	 * @return a map containing parameter values indexed by parameter names
	 * @throws JRException
	 */
	public static Map<String,Object> evaluateParameterDefaultValues(JasperReport report, Map<String,Object> initialParameters) throws JRException
	{
		return evaluateParameterDefaultValues(DefaultJasperReportsContext.getInstance(), report, initialParameters);
	}

	/**
	 * Evaluates the default values for the parameters of a report.
	 * 
	 * @param report the report
	 * @param initialParameters initial parameter value map
	 * @return a map containing parameter values indexed by parameter names
	 * @throws JRException
	 */
	public static Map<String,Object> evaluateParameterDefaultValues(JasperReportsContext jasperReportsContext, JasperReport report, Map<String,Object> initialParameters) throws JRException
	{
		return evaluateParameterDefaultValues(SimpleRepositoryContext.of(jasperReportsContext), report, initialParameters);
	}

	/**
	 * Evaluates the default values for the parameters of a report.
	 * 
	 * @param repositoryContext the repository context
	 * @param report the report
	 * @param initialParameters initial parameter value map
	 * @return a map containing parameter values indexed by parameter names
	 * @throws JRException
	 */
	//TODO use JasperReportSource instead of RepositoryContext?
	public static Map<String,Object> evaluateParameterDefaultValues(RepositoryContext repositoryContext, JasperReport report, Map<String,Object> initialParameters) throws JRException
	{
		Map<String,Object> parameterValues = new HashMap<>();
		DatasetExecution datasetExecution = new DatasetExecution(repositoryContext, report, initialParameters);
		datasetExecution.evaluateParameters((param, value) ->
		{
			if (!param.isSystemDefined())
			{
				parameterValues.put(param.getName(), value);
			}
		});
		return parameterValues;
	}
	

	private JRParameterDefaultValuesEvaluator()
	{
	}
}
