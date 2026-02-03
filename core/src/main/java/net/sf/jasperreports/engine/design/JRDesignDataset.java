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
package net.sf.jasperreports.engine.design;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import net.sf.jasperreports.engine.DatasetFilter;
import net.sf.jasperreports.engine.DatasetPropertyExpression;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRScriptlet;
import net.sf.jasperreports.engine.JRSortField;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.base.JRBaseDataset;
import net.sf.jasperreports.engine.query.QueryExecuterFactory;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.util.CloneStore;
import net.sf.jasperreports.engine.util.ContextClassLoaderObjectInputStream;
import net.sf.jasperreports.engine.util.FormatFactory;
import net.sf.jasperreports.engine.util.JRCloneUtils;
import net.sf.jasperreports.engine.util.JRQueryExecuterUtils;

/**
 * Implementation of {@link net.sf.jasperreports.engine.JRDataset JRDataset} to be used for report design.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRDesignDataset extends JRBaseDataset
{
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;
	
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_GROUP = "design.dataset.duplicate.group";
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_FIELD = "design.dataset.duplicate.field";
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_PARAMETER = "design.dataset.duplicate.parameter";
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_SCRIPTLET = "design.dataset.duplicate.scriptlet";
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_SORT_FIELD = "design.dataset.duplicate.sort.field";
	public static final String EXCEPTION_MESSAGE_KEY_DUPLICATE_VARIABLE = "design.dataset.duplicate.variable";
	public static final String EXCEPTION_MESSAGE_KEY_UNKNOWN_BUILTIN_PARAMETER_TYPE = "design.dataset.unknown.builtin.parameter.type";

	public static final String PROPERTY_FIELDS = "fields";

	public static final String PROPERTY_FILTER_EXPRESSION = "filterExpression";

	public static final String PROPERTY_GROUPS = "groups";
	
	public static final String PROPERTY_NAME = "name";
	
	public static final String PROPERTY_SCRIPTLETS = "scriptlets";
	
	public static final String PROPERTY_PARAMETERS = "parameters";
	
	public static final String PROPERTY_QUERY = "query";
	
	public static final String PROPERTY_RESOURCE_BUNDLE = "resourceBundle";
	
	public static final String PROPERTY_SCRIPTLET_CLASS = "scriptletClass";
	
	public static final String PROPERTY_SORT_FIELDS = "sortFields";
	
	public static final String PROPERTY_VARIABLES = "variables";
	
	public static final String PROPERTY_PROPERTY_EXPRESSIONS = "propertyExpressions";

	private transient JasperReportsContext jasperReportsContext;

	private boolean ownUUID;

	/**
	 * Scriptlets mapped by name.
	 */
	protected Map<String, JRScriptlet> scriptletsMap = new HashMap<>();
	protected List<JRScriptlet> scriptletsList = new ArrayList<>();

	/**
	 * Parameters mapped by name.
	 */
	protected Map<String, JRParameter> parametersMap = new HashMap<>();
	protected List<JRParameter> parametersList = new ArrayList<>();

	/**
	 * Fields mapped by name.
	 */
	protected Map<String, JRField> fieldsMap = new HashMap<>();
	protected List<JRField> fieldsList = new ArrayList<>();


	/**
	 * Sort fields mapped by name.
	 */
	protected Map<String, JRSortField> sortFieldsMap = new HashMap<>();
	protected List<JRSortField> sortFieldsList = new ArrayList<>();


	/**
	 * Variables mapped by name.
	 */
	protected Map<String, JRVariable> variablesMap = new HashMap<>();
	protected List<JRVariable> variablesList = new ArrayList<>();


	/**
	 * Groups mapped by name.
	 */
	protected Map<String, JRGroup> groupsMap = new HashMap<>();
	protected List<JRGroup> groupsList = new ArrayList<>();

	private List<DatasetPropertyExpression> propertyExpressions = new ArrayList<>();

	
	private class QueryLanguageChangeListener implements PropertyChangeListener, Serializable
	{
		private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			queryLanguageChanged((String) evt.getOldValue(), (String) evt.getNewValue());
		}
	}
	
	private PropertyChangeListener queryLanguageChangeListener = new QueryLanguageChangeListener();
	
	
	/**
	 * An array containing the built-in parameters that can be found and used in any report dataset.
	 */
	private static final Object[] BUILT_IN_PARAMETERS = new Object[] { 
		JRParameter.REPORT_CONTEXT, ReportContext.class, 
		JRParameter.REPORT_PARAMETERS_MAP, java.util.Map.class, 
		JRParameter.JASPER_REPORTS_CONTEXT, JasperReportsContext.class, 
		JRParameter.JASPER_REPORT, JasperReport.class, 
		JRParameter.REPORT_CONNECTION, Connection.class,
		JRParameter.REPORT_MAX_COUNT, Integer.class, 
		JRParameter.REPORT_DATA_SOURCE, JRDataSource.class, 
		JRParameter.REPORT_SCRIPTLET, JRAbstractScriptlet.class, 
		JRParameter.REPORT_LOCALE, Locale.class, 
		JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle.class,
		JRParameter.REPORT_TIME_ZONE, TimeZone.class, 
		JRParameter.REPORT_FORMAT_FACTORY, FormatFactory.class, 
		JRParameter.REPORT_CLASS_LOADER, ClassLoader.class,
		JRParameter.REPORT_TEMPLATES, Collection.class,
		JRParameter.SORT_FIELDS, List.class,
		JRParameter.FILTER, DatasetFilter.class
		};


	
	/**
	 * An array containing the built-in parameters that can be found and used in any report/main dataset.
	 */
	private static final Object[] BUILT_IN_PARAMETERS_MAIN = new Object[] { 
		JRParameter.REPORT_VIRTUALIZER, JRVirtualizer.class, 
		JRParameter.IS_IGNORE_PAGINATION, Boolean.class };

	
	@JsonCreator
	private JRDesignDataset()
	{
		this(false);
	}

	/**
	 * Create a dataset.
	 * 
	 * @param isMain whether this is the main dataset of the report or a sub dataset
	 * @see net.sf.jasperreports.engine.JRDataset#isMainDataset()
	 */
	public JRDesignDataset(boolean isMain)
	{
		this(DefaultJasperReportsContext.getInstance(), isMain);
	}

	/**
	 * Create a dataset.
	 * 
	 * @param isMain whether this is the main dataset of the report or a sub dataset
	 * @see net.sf.jasperreports.engine.JRDataset#isMainDataset()
	 */
	public JRDesignDataset(JasperReportsContext jasperReportsContext, boolean isMain)
	{
		super(isMain);
		
		this.jasperReportsContext = jasperReportsContext;
		
		addBuiltinParameters(BUILT_IN_PARAMETERS);
		
		if (isMain)
		{
			addBuiltinParameters(BUILT_IN_PARAMETERS_MAIN);
		}

		try 
		{
			addVariable(createPageNumberVariable());
			addVariable(createMasterCurrentPageVariable());
			addVariable(createMasterTotalPagesVariable());
			addVariable(createColumnNumberVariable());
			addVariable(createReportCountVariable());
			addVariable(createPageCountVariable());
			addVariable(createColumnCountVariable());
		}
		catch (JRException e)
		{
			//never reached
		}
	}

	private static JRDesignVariable createPageCountVariable()
	{
		JRDesignExpression expression;
		JRDesignVariable variable;
		variable = new JRDesignVariable();
		variable.setName(JRVariable.PAGE_COUNT);
		variable.setValueClass(Integer.class);
		variable.setResetType(ResetTypeEnum.PAGE);
		variable.setCalculation(CalculationEnum.COUNT);
		variable.setSystemDefined(true);
		expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(1)");
		variable.setExpression(expression);
		expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(0)");
		variable.setInitialValueExpression(expression);
		return variable;
	}

	private static JRDesignVariable createColumnNumberVariable()
	{
		JRDesignExpression expression;
		JRDesignVariable variable;
		variable = new JRDesignVariable();
		variable.setName(JRVariable.COLUMN_NUMBER);
		variable.setValueClass(Integer.class);
		//variable.setResetType(JRVariable.RESET_TYPE_COLUMN);
		variable.setResetType(ResetTypeEnum.PAGE);
		variable.setCalculation(CalculationEnum.SYSTEM);
		variable.setSystemDefined(true);
		expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		//expression.setText("$V{COLUMN_NUMBER} == null ? 1 : ($V{COLUMN_NUMBER} + 1)");
		expression.setText("new java.lang.Integer(1)");
		variable.setInitialValueExpression(expression);
		return variable;
	}

	private static JRDesignVariable createPageNumberVariable()
	{
		JRDesignVariable variable = new JRDesignVariable();
		variable.setName(JRVariable.PAGE_NUMBER);
		variable.setValueClass(Integer.class);
		//variable.setResetType(JRVariable.RESET_TYPE_PAGE);
		variable.setResetType(ResetTypeEnum.REPORT);
		variable.setCalculation(CalculationEnum.SYSTEM);
		variable.setSystemDefined(true);
		JRDesignExpression expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		//expression.setText("$V{PAGE_NUMBER} == null ? 1 : ($V{PAGE_NUMBER} + 1)");
		expression.setText("new java.lang.Integer(1)");
		variable.setInitialValueExpression(expression);
		return variable;
	}

	private static JRDesignVariable createMasterCurrentPageVariable()
	{
		JRDesignVariable variable = new JRDesignVariable();
		variable.setName(JRVariable.MASTER_CURRENT_PAGE);
		variable.setValueClass(Integer.class);
		variable.setResetType(ResetTypeEnum.REPORT);
		variable.setCalculation(CalculationEnum.SYSTEM);
		variable.setSystemDefined(true);
		return variable;
	}

	private static JRDesignVariable createMasterTotalPagesVariable()
	{
		JRDesignVariable variable = new JRDesignVariable();
		variable.setName(JRVariable.MASTER_TOTAL_PAGES);
		variable.setValueClass(Integer.class);
		variable.setResetType(ResetTypeEnum.REPORT);
		variable.setCalculation(CalculationEnum.SYSTEM);
		variable.setSystemDefined(true);
		return variable;
	}

	private static JRDesignVariable createColumnCountVariable()
	{
		JRDesignVariable variable;
		JRDesignExpression expression;
		variable = new JRDesignVariable();
		variable.setName(JRVariable.COLUMN_COUNT);
		variable.setValueClass(Integer.class);
		variable.setResetType(ResetTypeEnum.COLUMN);
		variable.setCalculation(CalculationEnum.COUNT);
		variable.setSystemDefined(true);
		expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(1)");
		variable.setExpression(expression);
		expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(0)");
		variable.setInitialValueExpression(expression);
		return variable;
	}

	private void addBuiltinParameters(Object[] parametersArray)
	{
		for (int i = 0; i < parametersArray.length; i++)
		{
			JRDesignParameter parameter = new JRDesignParameter();
			parameter.setName((String) parametersArray[i++]);
			
			Object parameterType = parametersArray[i];
			if (parameterType instanceof Class<?>)
			{
				parameter.setValueClass((Class<?>) parameterType);
			}
			else if (parameterType instanceof String)
			{
				parameter.setValueClassName((String) parameterType);
			}
			else
			{
				throw 
					new JRRuntimeException(
						EXCEPTION_MESSAGE_KEY_UNKNOWN_BUILTIN_PARAMETER_TYPE,
						new Object[]{parameterType, parameterType.getClass().getName()});
			}
			
			parameter.setSystemDefined(true);
			try
			{
				addParameter(parameter);
			}
			catch (JRException e)
			{
				// never reached
			}
		}
	}

	private static JRDesignVariable createReportCountVariable()
	{
		JRDesignVariable variable = new JRDesignVariable();
		variable.setName(JRVariable.REPORT_COUNT);
		variable.setValueClass(Integer.class);
		variable.setResetType(ResetTypeEnum.REPORT);
		variable.setCalculation(CalculationEnum.COUNT);
		variable.setSystemDefined(true);
		JRDesignExpression expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(1)");
		variable.setExpression(expression);
		expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(0)");
		variable.setInitialValueExpression(expression);
		return variable;
	}

	
	/**
	 * Sets the unique identifier for the report.
	 * 
	 * @param uuid the identifier
	 */
	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
		this.ownUUID = uuid != null;
	}
	
	/**
	 * Determines whether the report has an existing unique identifier.
	 * 
	 * Note that when no existing identifier is set, {@link #getUUID()} would generate and return
	 * an identifier.
	 * 
	 * @return whether the report has an externally set unique identifier
	 * @see #setUUID(UUID)
	 */
	public boolean hasUUID()
	{
		return ownUUID;
	}
	
	/**
	 * Sets the name of the dataset.
	 * @param name the name of the dataset
	 * @see net.sf.jasperreports.engine.JRDataset#getName()
	 */
	public void setName(String name)
	{
		Object old = this.name;
		this.name = name;
		getEventSupport().firePropertyChange(PROPERTY_NAME, old, this.name);
	}

	
	
	@Override
	public JRScriptlet[] getScriptlets()
	{
		JRScriptlet[] scriptletsArray = new JRScriptlet[scriptletsList.size()];

		scriptletsList.toArray(scriptletsArray);

		return scriptletsArray;
	}

	
	/**
	 * Returns the list of scriptlets, excluding the scriptletClass one.
	 * 
	 * @return list of {@link JRScriptlet JRScriptlet} objects
	 */
	@JsonIgnore
	public List<JRScriptlet> getScriptletsList()
	{
		return scriptletsList;
	}

	
	protected void setScriptlets(List<JRScriptlet> scriptlets) throws JRException
	{
		if (scriptlets != null)
		{
			for (JRScriptlet scriptlet : scriptlets)
			{
				addScriptlet(scriptlet);
			}
		}
	}

	
	/**
	 * Returns the map of scriptlets, excluding the scriptletClass one, indexed by name.
	 * 
	 * @return {@link JRScriptlet JRScriptlet} objects indexed by name
	 */
	@JsonIgnore
	public Map<String, JRScriptlet> getScriptletsMap()
	{
		return scriptletsMap;
	}

	
	/**
	 * Adds a scriptlet to the dataset.
	 * @param scriptlet the scriptlet to add
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getScriptlets()
	 */
	public void addScriptlet(JRScriptlet scriptlet) throws JRException
	{
		addScriptlet(scriptletsList.size(), scriptlet);
	}

	
	/**
	 * Inserts a scriptlet at the specified position into the dataset.
	 * @param index the scriptlet position
	 * @param scriptlet the scriptlet to insert
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getScriptlets()
	 */
	public void addScriptlet(int index, JRScriptlet scriptlet) throws JRException
	{
		if (scriptletsMap.containsKey(scriptlet.getName()))
		{
			throw 
			new JRException(
				EXCEPTION_MESSAGE_KEY_DUPLICATE_SCRIPTLET,
				new Object[]{scriptlet.getName()});
		}

		JRDesignParameter scriptletParameter = new JRDesignParameter();
		scriptletParameter.setName(scriptlet.getName() 
				+ JRScriptlet.SCRIPTLET_PARAMETER_NAME_SUFFIX);
		scriptletParameter.setValueClassName(scriptlet.getValueClassName());
		scriptletParameter.setSystemDefined(true);
		scriptletParameter.setForPrompting(false);

		addParameter(scriptletParameter);

		scriptletsList.add(index, scriptlet);
		scriptletsMap.put(scriptlet.getName(), scriptlet);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_SCRIPTLETS, scriptlet, index);
	}

	
	/**
	 * Removes a scriptlet from the dataset.
	 * 
	 * @param scriptletName the scriptlet name
	 * @return the removed scriptlet, or <code>null</code> if the scriptlet was not found
	 */
	public JRScriptlet removeScriptlet(String scriptletName)
	{
		return removeScriptlet(scriptletsMap.get(scriptletName));
	}

	
	/**
	 * Removes a scriptlet from the dataset.
	 * 
	 * @param scriptlet the scriptlet to be removed
	 * @return the scriptlet to be removed
	 */
	public JRScriptlet removeScriptlet(JRScriptlet scriptlet)
	{
		if (scriptlet != null)
		{
			removeParameter(scriptlet.getName() 
					+ JRScriptlet.SCRIPTLET_PARAMETER_NAME_SUFFIX);
			int idx = scriptletsList.indexOf(scriptlet);
			if (idx >= 0)
			{
				scriptletsList.remove(idx);
				scriptletsMap.remove(scriptlet.getName());
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_SCRIPTLETS, scriptlet, idx);
			}
		}

		return scriptlet;
	}

	
	@Override
	public JRParameter[] getParameters()
	{
		JRParameter[] parametersArray = new JRParameter[parametersList.size()];

		parametersList.toArray(parametersArray);

		return parametersArray;
	}

	
	/**
	 * Returns the list of parameters, including build-in ones.
	 * 
	 * @return list of {@link JRParameter JRParameter} objects
	 */
	@JsonIgnore
	public List<JRParameter> getParametersList()
	{
		return parametersList;
	}

	
	protected void setParameters(List<JRParameter> parameters) throws JRException
	{
		if (parameters != null)
		{
			for (JRParameter parameter : parameters)
			{
				addParameter(parameter);
			}
		}
	}

	
	/**
	 * Returns the map of parameters, including build-in ones, indexed by name.
	 * 
	 * @return {@link JRParameter JRParameter} objects indexed by name
	 */
	@JsonIgnore
	public Map<String, JRParameter> getParametersMap()
	{
		return parametersMap;
	}

	
	/**
	 * Adds a parameter to the dataset.
	 * @param parameter the parameter to add
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getParameters()
	 */
	public void addParameter(JRParameter parameter) throws JRException
	{
		addParameter(parametersList.size(), parameter);
	}

	
	/**
	 * Inserts a parameter at the specified position into the dataset.
	 * @param index the parameter position
	 * @param parameter the parameter to insert
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getParameters()
	 */
	public void addParameter(int index, JRParameter parameter) throws JRException
	{
		if (parametersMap.containsKey(parameter.getName()))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_PARAMETER,
					new Object[]{parameter.getName()});
		}

		parametersList.add(index, parameter);
		parametersMap.put(parameter.getName(), parameter);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PARAMETERS, parameter, index);
	}

	
	/**
	 * Removes a parameter from the dataset.
	 * 
	 * @param parameterName the parameter name
	 * @return the removed parameter, or <code>null</code> if the parameter was not found
	 */
	public JRParameter removeParameter(String parameterName)
	{
		return removeParameter(parametersMap.get(parameterName));
	}

	
	/**
	 * Removes a parameter from the dataset.
	 * 
	 * @param parameter the parameter to be removed
	 * @return the parameter to be removed
	 */
	public JRParameter removeParameter(JRParameter parameter)
	{
		if (parameter != null)
		{
			int idx = parametersList.indexOf(parameter);
			if (idx >= 0)
			{
				parametersList.remove(idx);
				parametersMap.remove(parameter.getName());
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PARAMETERS, parameter, idx);
			}
		}

		return parameter;
	}

	
	/**
	 * Sets the dataset query.
	 * 
	 * @param query the query
	 * @see net.sf.jasperreports.engine.JRDataset#getQuery()
	 */
	public void setQuery(JRDesignQuery query)
	{
		Object old = this.query;
		String oldLanguage = null;
		if (this.query != null)
		{
			((JRDesignQuery) this.query).removePropertyChangeListener(JRDesignQuery.PROPERTY_LANGUAGE, queryLanguageChangeListener);
			oldLanguage = this.query.getLanguage();
		}
		this.query = query;
		String newLanguage = null;
		if (query != null)
		{
			query.addPropertyChangeListener(JRDesignQuery.PROPERTY_LANGUAGE, queryLanguageChangeListener);
			newLanguage = query.getLanguage();
		}
		queryLanguageChanged(oldLanguage, newLanguage);
		getEventSupport().firePropertyChange(PROPERTY_QUERY, old, this.query);
	}

	
	/**
	 * Sets the scriptlet class name.
	 * <p>
	 * If no scriptlet class name is specified, a default scriptlet is used.
	 * 
	 * @param scriptletClass the class name of the scriptlet
	 * @see net.sf.jasperreports.engine.JRDataset#getScriptletClass()
	 */
	public void setScriptletClass(String scriptletClass)
	{
		Object old = this.scriptletClass;
		this.scriptletClass = scriptletClass;
		if (scriptletClass == null)
		{
			((JRDesignParameter) parametersMap.get(JRParameter.REPORT_SCRIPTLET)).setValueClass(JRAbstractScriptlet.class);
		}
		else
		{
			((JRDesignParameter) parametersMap.get(JRParameter.REPORT_SCRIPTLET)).setValueClassName(scriptletClass);
		}
		getEventSupport().firePropertyChange(PROPERTY_SCRIPTLET_CLASS, old, this.scriptletClass);
	}

	
	@Override
	public JRField[] getFields()
	{
		JRField[] fieldsArray = new JRField[fieldsList.size()];

		fieldsList.toArray(fieldsArray);

		return fieldsArray;
	}


	/**
	 * Returns the list of fields.
	 * 
	 * @return list of {@link JRField JRField} objects
	 */
	@JsonIgnore
	public List<JRField> getFieldsList()
	{
		return fieldsList;
	}

	
	protected void setFields(List<JRField> fields) throws JRException
	{
		if (fields != null)
		{
			for (JRField field : fields)
			{
				addField(field);
			}
		}
	}

	
	/**
	 * Returns the map of fields indexed by name.
	 * 
	 * @return {@link JRField JRField} objects indexed by name
	 */
	@JsonIgnore
	public Map<String, JRField> getFieldsMap()
	{
		return fieldsMap;
	}

	
	/**
	 * Adds a field to the dataset.
	 * @param field the field to add
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getFields()
	 */
	public void addField(JRField field) throws JRException
	{
		addField(fieldsList.size(), field);
	}

	
	/**
	 * Inserts a field at the specified position into the dataset.
	 * @param index the field position
	 * @param field the field to insert
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getFields()
	 */
	public void addField(int index, JRField field) throws JRException
	{
		if (fieldsMap.containsKey(field.getName()))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_FIELD,
					new Object[]{field.getName()});
		}

		fieldsList.add(index, field);
		fieldsMap.put(field.getName(), field);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_FIELDS, field, index);
	}

	
	/**
	 * Removes a field from the dataset.
	 * 
	 * @param fieldName the field name
	 * @return the removed field, or <code>null</code> if the field was not found
	 */
	public JRField removeField(String fieldName)
	{
		return removeField(fieldsMap.get(fieldName));
	}

	
	/**
	 * Removes a field from the dataset.
	 * 
	 * @param field the field to be removed
	 * @return the field to be removed
	 */
	public JRField removeField(JRField field)
	{
		if (field != null)
		{
			int idx = fieldsList.indexOf(field);
			if (idx >= 0)
			{
				fieldsList.remove(idx);
				fieldsMap.remove(field.getName());
				
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_FIELDS, field, idx);
			}
		}

		return field;
	}

	
	@Override
	public JRSortField[] getSortFields()
	{
		JRSortField[] sortFieldsArray = new JRSortField[sortFieldsList.size()];

		sortFieldsList.toArray(sortFieldsArray);

		return sortFieldsArray;
	}


	/**
	 * Returns the list of sort fields.
	 * 
	 * @return list of {@link JRSortField JRSortField} objects
	 */
	@JsonIgnore
	public List<JRSortField> getSortFieldsList()
	{
		return sortFieldsList;
	}

	
	protected void setSortFields(List<JRSortField> sortFields) throws JRException
	{
		if (sortFields != null)
		{
			for (JRSortField sortField : sortFields)
			{
				addSortField(sortField);
			}
		}
	}

	
	/**
	 * Returns the map of sort fields indexed by name and type.
	 * 
	 * @return {@link JRField JRField} objects indexed by name
	 */
	@JsonIgnore
	public Map<String, JRSortField> getSortFieldsMap()
	{
		return sortFieldsMap;
	}

	
	/**
	 * Adds a sort field to the dataset.
	 * @param sortField the sort field to add
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getSortFields()
	 */
	public void addSortField(JRSortField sortField) throws JRException
	{
		addSortField(sortFieldsList.size(), sortField);
	}

	
	/**
	 * Inserts a sort field at specified position into the dataset.
	 * @param index the sort field position
	 * @param sortField the sort field to insert
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getSortFields()
	 */
	public void addSortField(int index, JRSortField sortField) throws JRException
	{
		String sortFieldKey = getSortFieldKey(sortField);
		if (sortFieldsMap.containsKey(sortFieldKey))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_SORT_FIELD,
					new Object[]{sortField.getName()});
		}

		sortFieldsList.add(index, sortField);
		sortFieldsMap.put(sortFieldKey, sortField);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_SORT_FIELDS, sortField, index);
	}

	
	/**
	 * Removes a sort field from the dataset.
	 * 
	 * @param sortField the sort field to be removed
	 * @return the sort field to be removed
	 */
	public JRSortField removeSortField(JRSortField sortField)
	{
		if (sortField != null)
		{
			int idx = sortFieldsList.indexOf(sortField);
			if (idx >= 0)
			{
				sortFieldsList.remove(idx);
				sortFieldsMap.remove(getSortFieldKey(sortField));
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_SORT_FIELDS, sortField, idx);
			}
		}

		return sortField;
	}

	
	@Override
	public JRVariable[] getVariables()
	{
		JRVariable[] variablesArray = new JRVariable[variablesList.size()];

		variablesList.toArray(variablesArray);

		return variablesArray;
	}

	
	/**
	 * Returns the list of variables, including build-in ones.
	 * 
	 * @return list of {@link JRVariable JRVariable} objects
	 */
	@JsonIgnore
	public List<JRVariable> getVariablesList()
	{
		return variablesList;
	}

	
	protected void setVariables(List<JRDesignVariable> variables) throws JRException
	{
		if (variables != null)
		{
			for (JRDesignVariable variable : variables)
			{
				addVariable(variable);
			}
		}
	}

	
	/**
	 * Returns the map of variable, including build-in ones, indexed by name.
	 * 
	 * @return {@link JRVariable JRVariable} objects indexed by name
	 */
	@JsonIgnore
	public Map<String, JRVariable> getVariablesMap()
	{
		return variablesMap;
	}

	
	/**
	 * Adds a variable to the dataset.
	 * @param variable the variable to add
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getVariables()
	 */
	public void addVariable(JRDesignVariable variable) throws JRException
	{
		addVariable(variablesList.size(), variable, false);
	}
	
	
	/**
	 * Inserts a variable at specified position into the dataset.
	 * @param index the variable position
	 * @param variable the variable to insert
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getVariables()
	 */
	public void addVariable(int index, JRDesignVariable variable) throws JRException
	{
		addVariable(index, variable, variable.isSystemDefined());
	}
	
	
	/**
	 * Adds a variable to the dataset.
	 * 
	 * @param variable the variable to add
	 * @param system whether the variable should be added before user defined variables
	 * or at the end of the variables list
	 * @throws JRException
	 */
	protected void addVariable(JRDesignVariable variable, boolean system) throws JRException
	{
		addVariable(variablesList.size(), variable, system);
	}
	
	
	/**
	 * Inserts a variable at specified position into the dataset.
	 * 
	 * @param index the variable position
	 * @param variable the variable to insert
	 * @param system whether the variable should be inserted before user defined variables
	 * or at the end of the variables list
	 * @throws JRException
	 */
	protected void addVariable(int index, JRDesignVariable variable, boolean system) throws JRException
	{
		if (variablesMap.containsKey(variable.getName()))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_VARIABLE,
					new Object[]{variable.getName()});
		}

		if (system)
		{
			// add the variable before the first non-system variable
			ListIterator<JRVariable> it = variablesList.listIterator();
			while (it.hasNext())
			{
				JRVariable var = it.next();
				if (!var.isSystemDefined())
				{
					it.previous();
					break;
				}
			}
			it.add(variable);
			index = it.previousIndex();
		}
		else
		{
			variablesList.add(index, variable);
		}
		
		variablesMap.put(variable.getName(), variable);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_VARIABLES, variable, index);
	}

	
	/**
	 * Removes a variable from the dataset.
	 * 
	 * @param variableName the variable name
	 * @return the removed variable, or <code>null</code> if the variable was not found
	 */
	public JRVariable removeVariable(String variableName)
	{
		return removeVariable(variablesMap.get(variableName));
	}

	
	/**
	 * Removes a variable from the dataset.
	 * 
	 * @param variable the variable to be removed
	 * @return the variable to be removed
	 */
	public JRVariable removeVariable(JRVariable variable)
	{
		if (variable != null)
		{
			int idx = variablesList.indexOf(variable);
			if (idx >= 0)
			{
				variablesList.remove(idx);
				variablesMap.remove(variable.getName());
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_VARIABLES, variable, idx);
			}
		}

		return variable;
	}

	
	@Override
	public JRGroup[] getGroups()
	{
		JRGroup[] groupsArray = new JRGroup[groupsList.size()];

		groupsList.toArray(groupsArray);

		return groupsArray;
	}

	
	/**
	 * Returns the list of groups.
	 * 
	 * @return list of {@link JRGroup JRGroup} objects
	 */
	@JsonIgnore
	public List<JRGroup> getGroupsList()
	{
		return groupsList;
	}

	
	protected void setGroups(List<JRDesignGroup> groups) throws JRException
	{
		if (groups != null)
		{
			for (JRDesignGroup group : groups)
			{
				addGroup(group);
			}
		}
	}

	
	/**
	 * Returns the map of groups indexed by name.
	 * 
	 * @return {@link JRGroup JRGroup} objects indexed by name
	 */
	@JsonIgnore
	public Map<String, JRGroup> getGroupsMap()
	{
		return groupsMap;
	}
	/**
	 * Adds a group to the dataset.
	 * @param group the group to add
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getGroups()
	 */
	public void addGroup(JRDesignGroup group) throws JRException
	{
		addGroup(groupsList.size(), group);
	}

	
	/**
	 * Inserts a group at the specified position into the dataset.
	 * @param index the group position
	 * @param group the group to insert
	 * @throws JRException
	 * @see net.sf.jasperreports.engine.JRDataset#getGroups()
	 */
	public void addGroup(int index, JRDesignGroup group) throws JRException
	{
		if (groupsMap.containsKey(group.getName()))
		{
			throw 
				new JRException(
					EXCEPTION_MESSAGE_KEY_DUPLICATE_GROUP,
					new Object[]{group.getName()});
		}

		JRDesignVariable countVariable = new JRDesignVariable();
		countVariable.setName(group.getName() + "_COUNT");
		countVariable.setValueClass(Integer.class);
		countVariable.setResetType(ResetTypeEnum.GROUP);
		countVariable.setResetGroup(group.getName());
		countVariable.setCalculation(CalculationEnum.COUNT);
		countVariable.setSystemDefined(true);
		JRDesignExpression expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(1)");
		countVariable.setExpression(expression);
		expression = new JRDesignExpression();
		//expression.setValueClass(Integer.class);
		expression.setText("new java.lang.Integer(0)");
		countVariable.setInitialValueExpression(expression);

		addVariable(countVariable, true);

		group.setCountVariable(countVariable);

		groupsList.add(index, group);
		groupsMap.put(group.getName(), group);
		
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_GROUPS, group, index);
	}

	
	
	/**
	 * Removes a group from the dataset.
	 * 
	 * @param groupName the group name
	 * @return the removed group, or <code>null</code> if the group was not found
	 */
	public JRGroup removeGroup(String groupName)
	{
		return removeGroup(groupsMap.get(groupName));
	}

	
	/**
	 * Removes a group from the dataset.
	 * 
	 * @param group the group to be removed
	 * @return the group to be removed
	 */
	public JRGroup removeGroup(JRGroup group)
	{
		if (group != null)
		{
			removeVariable(group.getCountVariable());
			int idx = groupsList.indexOf(group);
			if (idx >= 0)
			{
				groupsList.remove(idx);
				groupsMap.remove(group.getName());
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_GROUPS, group, idx);
			}
		}

		return group;
	}
	
	
	/**
	 * Sets the base name of resource bundle to be used by the dataset.
	 * 
	 * @param resourceBundle the resource bundle base name
	 */
	public void setResourceBundle(String resourceBundle)
	{
		Object old = this.resourceBundle;
		this.resourceBundle = resourceBundle;
		getEventSupport().firePropertyChange(PROPERTY_RESOURCE_BUNDLE, old, this.resourceBundle);
	}
	
	
	protected void queryLanguageChanged(String oldLanguage, String newLanguage)
	{
		try
		{
			if (oldLanguage != null)
			{
				QueryExecuterFactory queryExecuterFactory = JRQueryExecuterUtils.getInstance(jasperReportsContext).getExecuterFactory(oldLanguage);//FIXMECONTEXT use some thread local
				Object[] builtinParameters = queryExecuterFactory.getBuiltinParameters();
				if (builtinParameters != null)
				{
					removeBuiltinParameters(builtinParameters);
				}
			}

			if (newLanguage != null)
			{
				QueryExecuterFactory queryExecuterFactory = JRQueryExecuterUtils.getInstance(jasperReportsContext).getExecuterFactory(newLanguage);
				Object[] builtinParameters = queryExecuterFactory.getBuiltinParameters();
				if (builtinParameters != null)
				{
					addBuiltinParameters(builtinParameters);
					sortSystemParamsFirst();
				}
			}
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	
	private void sortSystemParamsFirst()
	{
		Collections.sort(parametersList, new Comparator<JRParameter>()
				{
					@Override
					public int compare(JRParameter p1, JRParameter p2)
					{
//						JRParameter p1 = (JRParameter) o1;
//						JRParameter p2 = (JRParameter) o2;
						boolean s1 = p1.isSystemDefined();
						boolean s2 = p2.isSystemDefined();
						
						return s1 ? (s2 ? 0 : -1) : (s2 ? 1 : 0);
					}
				});
	}

	private void removeBuiltinParameters(Object[] builtinParameters)
	{
		for (int i = 0; i < builtinParameters.length; i += 2)
		{
			String parameterName = (String) builtinParameters[i];
			JRParameter parameter = parametersMap.get(parameterName);
			if (parameter != null && parameter.isSystemDefined())
			{
				removeParameter(parameter);
			}
		}
	}
	
	
	/**
	 * Adds/sets a property value.
	 * 
	 * @param propName the name of the property
	 * @param value the value of the property
	 */
	public void setProperty(String propName, String value)
	{
		//TODO event
		getPropertiesMap().setProperty(propName, value);
	}


	/**
	 * Add a dynamic/expression-based property.
	 * 
	 * @param propertyExpression the property to add
	 * @see #getPropertyExpressions()
	 */
	public void addPropertyExpression(DatasetPropertyExpression propertyExpression)
	{
		propertyExpressions.add(propertyExpression);
		getEventSupport().fireCollectionElementAddedEvent(PROPERTY_PROPERTY_EXPRESSIONS, 
				propertyExpression, propertyExpressions.size() - 1);
	}

	/**
	 * Remove a property expression.
	 * 
	 * @param propertyExpression the property expression to remove
	 * @see #addPropertyExpression(DatasetPropertyExpression)
	 */
	public void removePropertyExpression(DatasetPropertyExpression propertyExpression)
	{
		int idx = propertyExpressions.indexOf(propertyExpression);
		if (idx >= 0)
		{
			propertyExpressions.remove(idx);
			
			getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PROPERTY_EXPRESSIONS, 
					propertyExpression, idx);
		}
	}
	
	/**
	 * Remove a property expression.
	 * 
	 * @param name the name of the property to remove
	 * @return the removed property expression (if found)
	 */
	public DatasetPropertyExpression removePropertyExpression(String name)
	{
		DatasetPropertyExpression removed = null;
		for (ListIterator<DatasetPropertyExpression> it = propertyExpressions.listIterator(); it.hasNext();)
		{
			DatasetPropertyExpression prop = it.next();
			if (name.equals(prop.getName()))
			{
				removed = prop;
				int idx = it.previousIndex();
				
				it.remove();
				getEventSupport().fireCollectionElementRemovedEvent(PROPERTY_PROPERTY_EXPRESSIONS, 
						removed, idx);
				break;
			}
		}
		return removed;
	}
	
	/**
	 * Returns the list of property expressions.
	 * 
	 * @return the list of property expressions ({@link DatasetPropertyExpression} instances)
	 * @see #addPropertyExpression(DatasetPropertyExpression)
	 */
	@JsonIgnore
	public List<DatasetPropertyExpression> getPropertyExpressionsList()
	{
		return propertyExpressions;
	}
	
	@Override
	public DatasetPropertyExpression[] getPropertyExpressions()
	{
		DatasetPropertyExpression[] props;
		if (propertyExpressions.isEmpty())
		{
			props = null;
		}
		else
		{
			props = propertyExpressions.toArray(new DatasetPropertyExpression[propertyExpressions.size()]);
		}
		return props;
	}


	protected void setPropertyExpressions(List<DatasetPropertyExpression> properties)
	{
		if (properties != null)
		{
			for (DatasetPropertyExpression property : properties)
			{
				addPropertyExpression(property);
			}
		}
	}

	
	/**
	 * Sets the dataset filter expression.
	 * <p>
	 * The expression value class should be <code>java.lang.Boolean</code>.
	 * </p>
	 * 
	 * @param expression the boolean expression to use as filter expression
	 * @see JRDataset#getFilterExpression()
	 */
	public void setFilterExpression(JRExpression expression)
	{
		Object old = this.filterExpression;
		this.filterExpression = expression;
		getEventSupport().firePropertyChange(PROPERTY_FILTER_EXPRESSION, old, this.filterExpression);
	}


	/**
	 * 
	 */
	private String getSortFieldKey(JRSortField sortField)
	{
		return sortField.getName() + "|" + sortField.getType().getName();
	}

	
	/**
	 * 
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		
		if (sortFieldsMap == null)
		{
			sortFieldsMap = new HashMap<>();
		}
		if (sortFieldsList == null)
		{
			sortFieldsList = new ArrayList<>();
		}
		
		if (propertyExpressions == null)
		{
			propertyExpressions = new ArrayList<>();
		}

		@SuppressWarnings("resource")
		ContextClassLoaderObjectInputStream cclois = 
			in instanceof ContextClassLoaderObjectInputStream ? (ContextClassLoaderObjectInputStream)in : null;
		if (cclois == null)
		{
			jasperReportsContext = DefaultJasperReportsContext.getInstance();
		}
		else
		{
			jasperReportsContext = cclois.getJasperReportsContext();
		}
		
		//the listener is serialized, but not added to the query.
		//serializing the listener does not make much sense, it could have been easily recreated.
		addQueryLanguageListener();
	}

	@Override
	public Object clone() 
	{
		JRDesignDataset clone = (JRDesignDataset)super.clone();
		
		//recreate and register the query language listener
		clone.queryLanguageChangeListener = clone.new QueryLanguageChangeListener();
		clone.addQueryLanguageListener();
		
		if (parametersList != null)
		{
			clone.parametersList = new ArrayList<>(parametersList.size());
			clone.parametersMap = new HashMap<>(parametersList.size());
			for(int i = 0; i < parametersList.size(); i++)
			{
				JRParameter parameter = JRCloneUtils.nullSafeClone(parametersList.get(i));
				clone.parametersList.add(parameter);
				clone.parametersMap.put(parameter.getName(), parameter);
			}
		}
		
		if (fieldsList != null)
		{
			clone.fieldsList = new ArrayList<>(fieldsList.size());
			clone.fieldsMap = new HashMap<>(fieldsList.size());
			for(int i = 0; i < fieldsList.size(); i++)
			{
				JRField field = JRCloneUtils.nullSafeClone(fieldsList.get(i));
				clone.fieldsList.add(field);
				clone.fieldsMap.put(field.getName(), field);
			}
		}
		
		if (sortFieldsList != null)
		{
			clone.sortFieldsList = new ArrayList<>(sortFieldsList.size());
			clone.sortFieldsMap = new HashMap<>(sortFieldsList.size());
			for(int i = 0; i < sortFieldsList.size(); i++)
			{
				JRSortField sortField = JRCloneUtils.nullSafeClone(sortFieldsList.get(i));
				clone.sortFieldsList.add(sortField);
				clone.sortFieldsMap.put(getSortFieldKey(sortField), sortField);
			}
		}
		
		CloneStore cloneStore = new CloneStore();
		
		if (variablesList != null)
		{
			clone.variablesList = new ArrayList<>(variablesList.size());
			clone.variablesMap = new HashMap<>(variablesList.size());
			for(int i = 0; i < variablesList.size(); i++)
			{
				JRVariable variable = cloneStore.clone(variablesList.get(i));
				clone.variablesList.add(variable);
				clone.variablesMap.put(variable.getName(), variable);
			}
		}
		
		if (groupsList != null)
		{
			clone.groupsList = new ArrayList<>(groupsList.size());
			clone.groupsMap = new HashMap<>(groupsList.size());
			for(int i = 0; i < groupsList.size(); i++)
			{
				JRGroup group = cloneStore.clone(groupsList.get(i));
				clone.groupsList.add(group);
				clone.groupsMap.put(group.getName(), group);
			}
		}

		clone.propertyExpressions = JRCloneUtils.cloneList(propertyExpressions);
		
		return clone;
	}
	
	private void addQueryLanguageListener()
	{
		if (query instanceof JRDesignQuery)
		{
			((JRDesignQuery) query).addPropertyChangeListener(JRDesignQuery.PROPERTY_LANGUAGE, queryLanguageChangeListener);
		}
	}
	
}
