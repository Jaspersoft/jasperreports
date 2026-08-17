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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.crosstabs.JRCellContents;
import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.crosstabs.JRCrosstabBucket;
import net.sf.jasperreports.crosstabs.JRCrosstabCell;
import net.sf.jasperreports.crosstabs.JRCrosstabColumnGroup;
import net.sf.jasperreports.crosstabs.JRCrosstabDataset;
import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.JRCrosstabParameter;
import net.sf.jasperreports.crosstabs.JRCrosstabRowGroup;
import net.sf.jasperreports.crosstabs.design.JRDesignCrosstab;
import net.sf.jasperreports.engine.analytics.dataset.DataAxis;
import net.sf.jasperreports.engine.analytics.dataset.DataAxisLevel;
import net.sf.jasperreports.engine.analytics.dataset.DataLevelBucket;
import net.sf.jasperreports.engine.analytics.dataset.DataLevelBucketProperty;
import net.sf.jasperreports.engine.analytics.dataset.DataMeasure;
import net.sf.jasperreports.engine.analytics.dataset.MultiAxisData;
import net.sf.jasperreports.engine.analytics.dataset.MultiAxisDataset;
import net.sf.jasperreports.engine.component.Component;
import net.sf.jasperreports.engine.component.ComponentCompiler;
import net.sf.jasperreports.engine.component.ComponentManager;
import net.sf.jasperreports.engine.component.ComponentsEnvironment;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRValidationFault;
import net.sf.jasperreports.engine.design.JRVerifier;
import net.sf.jasperreports.engine.part.PartComponent;
import net.sf.jasperreports.engine.part.PartComponentManager;
import net.sf.jasperreports.engine.part.PartComponentsEnvironment;
import net.sf.jasperreports.engine.util.JRReportUtils;


/**
 * An expression collector traverses a report and collects report expressions
 * out of it.
 * 
 * <p>
 * The expressions are then included into evaluator classes which are compiled
 * and used at report fill time to evaluate expressions.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRExpressionCollector
{
	
	private static final Log log = LogFactory.getLog(JRExpressionCollector.class);

	public static final String EXCEPTION_MESSAGE_KEY_EXPRESSION_NOT_FOUND = "engine.expression.collector.expression.not.found";
	public static final String EXCEPTION_MESSAGE_KEY_TWO_GENERATED_IDS = "engine.expression.collector.two.generated.ids";
	
	public static JRExpressionCollector collector(JasperReportsContext jasperReportsContext, JRReport report)
	{
		JRExpressionCollector collector = new JRExpressionCollector(jasperReportsContext, null, report);
		collector.collect();
		return collector;
	}

	public static List<JRExpression> collectExpressions(JasperReportsContext jasperReportsContext, JRReport report)
	{
		return collector(jasperReportsContext, report).getExpressions();
	}

	public static JRExpressionCollector collector(JasperReportsContext jasperReportsContext, JRReport report, JRCrosstab crosstab)
	{
		JRExpressionCollector collector = new JRExpressionCollector(jasperReportsContext, null, report);
		collector.collect(crosstab);
		return collector;
	}

	/**
	 * @deprecated To be removed.
	 */
	public static List<JRExpression> collectExpressions(JasperReportsContext jasperReportsContext, JRReport report, JRCrosstab crosstab)
	{
		return collector(jasperReportsContext, report, crosstab).getExpressions(crosstab);
	}

	private final JasperReportsContext jasperReportsContext;
	private final JRReport report;
	private final JRExpressionCollector parent;

	private Map<JRExpression,Integer> expressionIds;
	
	private LinkedList<Object> contextStack;
	private Map<JRExpression, Object> expressionContextMap;
	
	protected static interface ExpressionVerifier
	{
		public Map getParametersMap();
		
		public Map getFieldsMap();
		
		public Map getVariablesMap();
	}
	
	protected static class DatasetExpressionVerifier implements ExpressionVerifier
	{
		private final Map parametersMap;
		private final Map fieldsMap;
		private final Map variablesMap;
		
		public DatasetExpressionVerifier(JRDataset dataset)
		{
			if (dataset instanceof JRDesignDataset)
			{
				parametersMap = ((JRDesignDataset)dataset).getParametersMap();
				fieldsMap = ((JRDesignDataset)dataset).getFieldsMap();
				variablesMap = ((JRDesignDataset)dataset).getVariablesMap();
			}
			else
			{
				parametersMap = new HashMap<>();
				if (dataset.getParameters() != null)
				{
					for (JRParameter parameter : dataset.getParameters())
					{
						parametersMap.put(parameter.getName(), parameter);
					}
				}

				fieldsMap = new HashMap<>();
				if (dataset.getFields() != null)
				{
					for (JRField field : dataset.getFields())
					{
						fieldsMap.put(field.getName(), field);
					}
				}

				variablesMap = new HashMap<>();
				if (dataset.getVariables() != null)
				{
					for (JRVariable variable : dataset.getVariables())
					{
						variablesMap.put(variable.getName(), variable);
					}
				}
			}
		}
		
		@Override
		public Map getParametersMap() 
		{
			return parametersMap;
		}
		
		@Override
		public Map getFieldsMap() 
		{
			return fieldsMap;
		}
		
		@Override
		public Map getVariablesMap() 
		{
			return variablesMap;
		}
	}

	
	protected static class CrosstabExpressionVerifier implements ExpressionVerifier
	{
		private final Map parametersMap;
		private final Map fieldsMap;
		private final Map variablesMap;
		
		public CrosstabExpressionVerifier(JRCrosstab crosstab)
		{
			if (crosstab instanceof JRDesignCrosstab)
			{
				parametersMap = ((JRDesignCrosstab)crosstab).getParametersMap();
				fieldsMap = new HashMap<>();
				variablesMap = ((JRDesignCrosstab)crosstab).getVariablesMap();
			}
			else
			{
				parametersMap = new HashMap<>();
				if (crosstab.getParameters() != null)
				{
					for (JRCrosstabParameter parameter : crosstab.getParameters())
					{
						parametersMap.put(parameter.getName(), parameter);
					}
				}

				fieldsMap = new HashMap<>();

				variablesMap = new HashMap<>();
				if (crosstab.getVariables() != null)
				{
					for (JRVariable variable : crosstab.getVariables())
					{
						variablesMap.put(variable.getName(), variable);
					}
				}
			}
		}
		
		@Override
		public Map getParametersMap() 
		{
			return parametersMap;
		}
		
		@Override
		public Map getFieldsMap() 
		{
			return fieldsMap;
		}
		
		@Override
		public Map getVariablesMap() 
		{
			return variablesMap;
		}
	}

	private final ExpressionVerifier expressionVerifier;
	
	protected static class GeneratedIds
	{
		private final TreeMap<Integer, JRExpression> ids = new TreeMap<>();
		private int nextId;
		private List<JRExpression> expressions;
		
		public JRExpression get(Integer id)
		{
			return ids.get(id);
		}
		
		public JRExpression put(Integer id, JRExpression expression)
		{
			expressions = null;

			return ids.put(id, expression);
		}
		
		public void move(Integer id, Integer newId)
		{
			expressions = null;

			JRExpression expression = ids.remove(id);
			if (expression != null)
			{
				ids.put(newId, expression);
			}
		}

		public Integer nextId()
		{
			Integer id = nextId;
			while (ids.containsKey(id))
			{
				id = ++nextId;
			}
			return id;
		}

		public List<JRExpression> expressions()
		{
			if (expressions == null)
			{
				expressions = new ArrayList<>(ids.values());
			}
			return expressions;
		}

		public JRExpression expression(int id)
		{
			return ids.get(id);
		}
	}
	private GeneratedIds generatedIds = new GeneratedIds();

	private Map<JRCrosstab,Integer> crosstabIds;

	/**
	 * Collectors for sub datasets indexed by dataset name.
	 */
	private Map<String,JRExpressionCollector> datasetCollectors;

	/**
	 * Collectors for crosstabs.
	 */
	private Map<JRCrosstab,JRExpressionCollector> crosstabCollectors;

	private final Set<JRStyle> collectedStyles;


	protected JRExpressionCollector(JasperReportsContext jasperReportsContext, JRExpressionCollector parent, JRReport report)
	{
		this(jasperReportsContext, parent, report, new DatasetExpressionVerifier(report.getMainDataset()));
	}

	protected JRExpressionCollector(JasperReportsContext jasperReportsContext, JRExpressionCollector parent, JRReport report, ExpressionVerifier expressionVerifier)
	{
		this.jasperReportsContext = jasperReportsContext;
		this.parent = parent;
		this.report = report;
		this.expressionVerifier = expressionVerifier;

		if (parent == null)
		{
			expressionIds = new HashMap<>();
			datasetCollectors = new HashMap<>();
			crosstabCollectors = new HashMap<>();
			contextStack = new LinkedList<>();
			expressionContextMap = new HashMap<>();
			crosstabIds = new HashMap<>();
		}
		else
		{
			expressionIds = this.parent.expressionIds;
			contextStack = this.parent.contextStack;
			expressionContextMap = this.parent.expressionContextMap;
			crosstabIds = parent.crosstabIds;
		}

		collectedStyles = new HashSet<>();
	}

	/**
	 * Collects an expression.
	 * 
	 * @param expression the expression to collect
	 */
	public void addExpression(JRExpression expression)
	{
		if (expression != null)
		{
			Integer id = getExpressionId(expression);
			if (id == null)
			{
				id = generatedIds.nextId();
				setGeneratedId(expression, id);
				generatedIds.put(id, expression);
				
				if (log.isTraceEnabled())
				{
					log.trace(hashCode() + " generated id " + id 
							+ " for expression " + expression.hashCode() + " " + expression.getText());
				}
			}
			else
			{
				if (log.isTraceEnabled())
				{
					log.trace(hashCode() + " found id " + id 
							+ " for expression " + expression.hashCode() + " " + expression.getText());
				}
				
				if (canUseId(expression, id))
				{
					generatedIds.put(id, expression);
				}
				else
				{
					reassignId(expression, id);
				}
			}
			
			setExpressionContext(expression);
		}
	}

	private void setGeneratedId(JRExpression expression, Integer id)
	{
		Object existingId = expressionIds.put(expression, id);
		if (existingId != null && !existingId.equals(id))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_TWO_GENERATED_IDS,
					new Object[]{expression.getText()});
		}
	}

	private void updateGeneratedId(JRExpression expression, Integer currentId, Integer newId)
	{
		Object existingId = expressionIds.put(expression, newId);
		if (existingId == null || !existingId.equals(currentId))
		{
			throw 
				new JRRuntimeException(
					EXCEPTION_MESSAGE_KEY_TWO_GENERATED_IDS,
					new Object[]{expression.getText(), currentId});
		}
	}
	
	protected boolean canUseId(JRExpression expression, Integer id)
	{
		JRExpression existingExpression = generatedIds.get(id);
		return existingExpression == null || existingExpression.equals(expression);
	}

	protected void reassignId(JRExpression expression, Integer id)
	{
		List<JRExpressionCollector> collectors = collectorsForExpression(id, expression);
		Integer newId = generatedIds.nextId();
		while (!canUseId(collectors, expression, newId))
		{
			++newId;
		}
		
		if (log.isTraceEnabled())
		{
			log.trace(hashCode() + " updated id to " + newId
					+ " for expression " + expression.hashCode() + " " + expression.getText());
		}
		
		generatedIds.put(newId, expression);
		for (JRExpressionCollector collector : collectors)
		{
			collector.generatedIds.move(id, newId);
		}
		updateGeneratedId(expression, id, newId);
	}

	protected boolean canUseId(List<JRExpressionCollector> collectors, JRExpression expression, Integer id)
	{
		boolean canUse = canUseId(expression, id);
		if (canUse)
		{
			for (JRExpressionCollector collector : collectors)
			{
				if (!collector.canUseId(expression, id))
				{
					canUse = false;
				}
			}
		}
		return canUse;
	}

	protected JRExpressionCollector rootCollector()
	{
		return parent == null ? this : parent;
	}

	protected boolean hasExpression(Integer id, JRExpression expression)
	{
		JRExpression existingExpression = generatedIds.get(id);
		return existingExpression != null && existingExpression.equals(expression);
	}
	
	protected List<JRExpressionCollector> collectorsForExpression(Integer id, JRExpression expression)
	{
		JRExpressionCollector root = rootCollector();
		List<JRExpressionCollector> collectors = new ArrayList<>();
		
		if (root.hasExpression(id, expression))
		{
			collectors.add(root);
		}
		
		for (JRExpressionCollector collector : root.datasetCollectors.values())
		{
			if (collector.hasExpression(id, expression))
			{
				collectors.add(collector);
			}
		}
		
		for (JRExpressionCollector collector : root.crosstabCollectors.values())
		{
			if (collector.hasExpression(id, expression))
			{
				collectors.add(collector);
			}
		}
		
		return collectors;
	}

	protected void pushContextObject(Object context)
	{
		contextStack.addLast(context);
	}

	protected Object popContextObject()
	{
		return contextStack.removeLast();
	}

	protected void setExpressionContext(JRExpression expression)
	{
		if (!contextStack.isEmpty())
		{
			Object context = contextStack.getLast();
			expressionContextMap.put(expression, context);
		}
	}
	
	/**
	 * Returns the expression collector to which expressions in an element
	 * dataset belong.
	 * 
	 * <p>
	 * If the element dataset includes a subdataset run, a (sub) expression
	 * collector that corresponds to the subdataset will be returned.
	 * Otherwise, this/the main expression collector will be returned.
	 * 
	 * @param elementDataset an element dataset
	 * @return the expression collector to be used for the element dataset
	 */
	public JRExpressionCollector getCollector(JRElementDataset elementDataset)
	{
		JRExpressionCollector collector;

		JRDatasetRun datasetRun = elementDataset.getDatasetRun();
		if (datasetRun == null)
		{
			collector = this;
		}
		else
		{
			collector = getDatasetCollector(datasetRun.getDatasetName());
		}

		return collector;
	}


	/**
	 * Returns the expression collector for a report subdataset.
	 * 
	 * @param datasetName the subdataset name
	 * @return the expression collector for the subdataset
	 */
	public JRExpressionCollector getDatasetCollector(String datasetName)
	{
		JRExpressionCollector collector;
		if (parent == null)
		{
			collector = datasetCollectors.get(datasetName);
			if (collector == null)
			{
				collector = new JRExpressionCollector(jasperReportsContext, this, report, new DatasetExpressionVerifier(JRReportUtils.findSubdataset(datasetName, report)));
				datasetCollectors.put(datasetName, collector);
				
				if (log.isTraceEnabled())
				{
					log.trace(this.hashCode() + " created collector " + collector.hashCode() + " for dataset " + datasetName);
				}
			}
		}
		else
		{
			collector = parent.getDatasetCollector(datasetName);
		}
		return collector;
	}


	/**
	 * Returns the expression collector for a dataset.
	 *
	 * @param dataset the dataset
	 * @return the dataset expression collector
	 */
	public JRExpressionCollector getCollector(JRDataset dataset)
	{
		JRExpressionCollector collector;
		if (parent == null)
		{
			if (dataset.isMainDataset() || datasetCollectors == null)
			{
				collector = this;
			}
			else
			{
				collector = getDatasetCollector(dataset.getName());
			}
		}
		else
		{
			collector = parent.getCollector(dataset);
		}
		return collector;
	}


	/**
	 * Returns the expression collector for a crosstab.
	 *
	 * @param crosstab the crosstab
	 * @return the crosstab expression collector
	 */
	public JRExpressionCollector getCollector(JRCrosstab crosstab)
	{
		return
			parent == null
			? crosstabCollectors.computeIfAbsent(crosstab, k -> new JRExpressionCollector(jasperReportsContext, this, report, new CrosstabExpressionVerifier(crosstab)))
			: parent.getCollector(crosstab);
	}


	/**
	 * Returns the collected expressions.
	 *
	 * @return the collected expressions
	 */
	public List<JRExpression> getExpressions()
	{
		return new ArrayList<>(generatedIds.expressions());
	}


	/**
	 * Return all the expressions collected from the report.
	 * 
	 * @return all the expressions collected from the report
	 */
	public Collection<JRExpression> getReportExpressions()
	{
		return Collections.unmodifiableSet(expressionIds.keySet());
	}

	/**
	 * Returns the expressions collected for a dataset.
	 *
	 * @param dataset the dataset
	 * @return the expressions
	 */
	public List<JRExpression> getExpressions(JRDataset dataset)
	{
		return getCollector(dataset).getExpressions();
	}


	/**
	 * Returns the expressions collected for a crosstab.
	 *
	 * @param crosstab the crosstab
	 * @return the expressions
	 */
	public List<JRExpression> getExpressions(JRCrosstab crosstab)
	{
		return getCollector(crosstab).getExpressions();
	}


	public Integer getExpressionId(JRExpression expression)
	{
		return expressionIds.get(expression);
	}


	public JRExpression getExpression(int expressionId)
	{
		return generatedIds.expression(expressionId);
	}


	public Integer getCrosstabId(JRCrosstab crosstab)
	{
		return crosstabIds.get(crosstab);
	}

	public Object getExpressionContext(JRExpression expression)
	{
		return expressionContextMap.get(expression);
	}

	/**
	 *
	 */
	public Collection<JRExpression> collect()
	{
		collectTemplates();

		collect(report.getDefaultStyle());

		collect(report.getMainDataset());

		JRDataset[] datasets = report.getDatasets();
		if (datasets != null && datasets.length > 0)
		{
			for (int i = 0; i < datasets.length; i++)
			{
				JRExpressionCollector collector = getCollector(datasets[i]);
				collector.collect(datasets[i]);
			}
		}

		collect(report.getBackground());
		collect(report.getTitle());
		collect(report.getPageHeader());
		collect(report.getColumnHeader());
		collect(report.getDetailSection());
		collect(report.getColumnFooter());
		collect(report.getPageFooter());
		collect(report.getLastPageFooter());
		collect(report.getSummary());
		collect(report.getNoData());

		return getExpressions();
	}


	protected void collectTemplates()
	{
		JRReportTemplate[] templates = report.getTemplates();
		if (templates != null)
		{
			for (int i = 0; i < templates.length; i++)
			{
				JRReportTemplate template = templates[i];
				collect(template);
			}
		}
	}

	protected void collect(JRReportTemplate template)
	{
		addExpression(template.getSourceExpression());
	}

	/**
	 * Collects expressions used in a style definition.
	 * 
	 * @param style the style to collect expressions from
	 */
	public void collect(JRStyle style)
	{
		collect(style, false);
	}


	/**
	 * Collects expressions used in a style definition.
	 * 
	 * @param style the style to collect expressions from
	 */
	public void collect(JRStyle style, boolean skipFaulty)
	{
		if (style != null && collectedStyles.add(style))
		{
			JRConditionalStyle[] conditionalStyles = style.getConditionalStyles();

			if (conditionalStyles != null && conditionalStyles.length > 0)
			{
				for (int i = 0; i < conditionalStyles.length; i++)
				{
					JRExpression conditionExpression = conditionalStyles[i].getConditionExpression();
					Collection<JRValidationFault> brokenRules = new ArrayList<>();
					JRVerifier.verifyExpression(conditionExpression, expressionVerifier.getParametersMap(), expressionVerifier.getFieldsMap(), expressionVerifier.getVariablesMap(), brokenRules);
					if (brokenRules.size() == 0 || !skipFaulty)
					{
						addExpression(conditionExpression);
					}
				}
			}

			collect(style.getStyle(), skipFaulty);
		}
	}


	/**
	 *
	 */
	private void collect(JRParameter[] parameters)
	{
		if (parameters != null && parameters.length > 0)
		{
			for(int i = 0; i < parameters.length; i++)
			{
				addExpression(parameters[i].getDefaultValueExpression());
			}
		}
	}

	/**
	 *
	 */
	private void collect(JRField[] fields)
	{
		if (fields != null && fields.length > 0)
		{
			for (JRField field : fields)
			{
				collectPropertyExpressions(field.getPropertyExpressions());
			}
		}
	}

	/**
	 *
	 */
	private void collect(JRVariable[] variables)
	{
		if (variables != null && variables.length > 0)
		{
			for(int i = 0; i < variables.length; i++)
			{
				JRVariable variable = variables[i];
				addExpression(variable.getExpression());
				addExpression(variable.getInitialValueExpression());
			}
		}
	}

	/**
	 *
	 */
	private void collect(JRGroup[] groups)
	{
		if (groups != null && groups.length > 0)
		{
			for(int i = 0; i < groups.length; i++)
			{
				JRGroup group = groups[i];
				addExpression(group.getExpression());

				collect(group.getGroupHeaderSection());
				collect(group.getGroupFooterSection());
			}
		}
	}

	private void collect(JRScriptlet[] scriptlets)
	{
		if (scriptlets != null && scriptlets.length > 0)
		{
			for (int i = 0; i < scriptlets.length; i++)
			{
				JRScriptlet scriptlet = scriptlets[i];
				collectPropertyExpressions(scriptlet.getPropertyExpressions());
			}
		}
	}

	/**
	 *
	 */
	private void collect(JRStyle[] styles, boolean skipFaulty)
	{
		if (styles != null && styles.length > 0)
		{
			for (JRStyle style : styles)
			{
				collect(style, skipFaulty);
			}
		}
	}

	/**
	 *
	 */
	private void collect(JRSection section)
	{
		if (section != null)
		{
			JRBand[] bands = section.getBands();
			if (bands != null && bands.length > 0)
			{
				for(int i = 0; i < bands.length; i++)
				{
					collect(bands[i]);
				}
			}

			JRPart[] parts = section.getParts();
			if (parts != null && parts.length > 0)
			{
				for(int i = 0; i < parts.length; i++)
				{
					collect(parts[i]);
				}
			}
		}
	}

	/**
	 *
	 */
	private void collect(JRBand band)
	{
		if (band != null)
		{
			addExpression(band.getPrintWhenExpression());

			JRElement[] elements = band.getElements();
			if (elements != null && elements.length > 0)
			{
				for(int i = 0; i < elements.length; i++)
				{
					elements[i].collectExpressions(this);
				}
			}

			List<ExpressionReturnValue> returnValues = band.getReturnValues();
			if (returnValues != null && returnValues.size() > 0)
			{
				for (ExpressionReturnValue returnValue : returnValues)
				{
					addExpression(returnValue.getExpression());
				}
			}
		}
	}

	/**
	 *
	 */
	private void collect(JRPart part)
	{
		if (part != null)
		{
			addExpression(part.getPrintWhenExpression());
			addExpression(part.getPartNameExpression());
			collectPropertyExpressions(part.getPropertyExpressions());

			PartComponent component = part.getComponent();
			PartComponentManager manager = PartComponentsEnvironment.getInstance(jasperReportsContext).getManager(component);
			manager.getComponentCompiler(jasperReportsContext).collectExpressions(component, this);
		}
	}

	/**
	 *
	 */
	public void collectElement(JRElement element)
	{
		collect(element.getStyle());
		addExpression(element.getPrintWhenExpression());
		addExpression(element.getStyleExpression());
		collectPropertyExpressions(element.getPropertyExpressions());
	}

	public void collectPropertyExpressions(
			JRPropertyExpression[] propertyExpressions)
	{
		if (propertyExpressions != null && propertyExpressions.length > 0)
		{
			for (int i = 0; i < propertyExpressions.length; i++)
			{
				collectPropertyExpression(propertyExpressions[i]);
			}
		}
	}

	protected void collectPropertyExpression(
			JRPropertyExpression propertyExpression)
	{
		addExpression(propertyExpression.getValueExpression());
	}

	/**
	 *
	 */
	public void collectAnchor(JRAnchor anchor)
	{
		addExpression(anchor.getAnchorNameExpression());
		addExpression(anchor.getBookmarkLevelExpression());
	}


	public void collectHyperlink(JRHyperlink hyperlink)
	{
		if (hyperlink != null)
		{
			addExpression(hyperlink.getHyperlinkReferenceExpression());
			addExpression(hyperlink.getHyperlinkWhenExpression());
			addExpression(hyperlink.getHyperlinkAnchorExpression());
			addExpression(hyperlink.getHyperlinkPageExpression());
			addExpression(hyperlink.getHyperlinkTooltipExpression());

			JRHyperlinkParameter[] hyperlinkParameters = hyperlink.getHyperlinkParameters();
			if (hyperlinkParameters != null)
			{
				for (int i = 0; i < hyperlinkParameters.length; i++)
				{
					JRHyperlinkParameter parameter = hyperlinkParameters[i];
					collectHyperlinkParameter(parameter);
				}
			}
		}
	}

	public void collectHyperlinkParameter(JRHyperlinkParameter parameter)
	{
		if (parameter != null)
		{
			addExpression(parameter.getValueExpression());
		}
	}

	/**
	 *
	 */
	public void collect(JRBreak breakElement)
	{
		collectElement(breakElement);
	}

	/**
	 *
	 */
	public void collect(JRLine line)
	{
		collectElement(line);
	}

	/**
	 *
	 */
	public void collect(JRRectangle rectangle)
	{
		collectElement(rectangle);
	}

	/**
	 *
	 */
	public void collect(JREllipse ellipse)
	{
		collectElement(ellipse);
	}

	/**
	 *
	 */
	public void collect(JRImage image)
	{
		collectElement(image);
		addExpression(image.getExpression());
		collectAnchor(image);
		collectHyperlink(image);
	}

	/**
	 *
	 */
	public void collect(JRStaticText staticText)
	{
		collectElement(staticText);
	}

	/**
	 *
	 */
	public void collect(JRTextField textField)
	{
		collectElement(textField);
		addExpression(textField.getExpression());
		addExpression(textField.getPatternExpression());
		collectAnchor(textField);
		collectHyperlink(textField);
	}

	/**
	 *
	 */
	public void collect(JRSubreport subreport)
	{
		collectElement(subreport);
		addExpression(subreport.getParametersMapExpression());

		JRSubreportParameter[] parameters = subreport.getParameters();
		if (parameters != null && parameters.length > 0)
		{
			for(int j = 0; j < parameters.length; j++)
			{
				addExpression(parameters[j].getExpression());
			}
		}

		addExpression(subreport.getConnectionExpression());
		addExpression(subreport.getDataSourceExpression());
		addExpression(subreport.getExpression());
	}

	/**
	 * Collects expressions from a crosstab.
	 *
	 * @param crosstab the crosstab
	 */
	public void collect(JRCrosstab crosstab)
	{
		collectElement(crosstab);

		createCrosstabId(crosstab);

		JRCrosstabDataset dataset = crosstab.getDataset();
		collect(dataset);

		JRExpressionCollector datasetCollector = getCollector(dataset);
		JRExpressionCollector crosstabCollector = getCollector(crosstab);

		crosstabCollector.collect(report.getDefaultStyle());
		crosstabCollector.collect(report.getStyles(), true);

		addExpression(crosstab.getParametersMapExpression());

		JRCrosstabParameter[] parameters = crosstab.getParameters();
		if (parameters != null)
		{
			for (int i = 0; i < parameters.length; i++)
			{
				addExpression(parameters[i].getExpression());
			}
		}

		if (crosstab.getTitleCell() != null)
		{
			crosstabCollector.collect(crosstab.getTitleCell().getCellContents());
		}
		crosstabCollector.collect(crosstab.getHeaderCell());

		JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
		if (rowGroups != null)
		{
			for (int i = 0; i < rowGroups.length; i++)
			{
				JRCrosstabRowGroup rowGroup = rowGroups[i];
				JRCrosstabBucket bucket = rowGroup.getBucket();
				datasetCollector.addExpression(bucket.getExpression());
				
				crosstabCollector.pushContextObject(rowGroup);
				//order by expression is in the crosstab context
				crosstabCollector.addExpression(bucket.getOrderByExpression());
				addExpression(bucket.getComparatorExpression());
				crosstabCollector.collect(rowGroup.getHeader());
				crosstabCollector.collect(rowGroup.getTotalHeader());
				crosstabCollector.popContextObject();
			}
		}

		JRCrosstabColumnGroup[] colGroups = crosstab.getColumnGroups();
		if (colGroups != null)
		{
			for (int i = 0; i < colGroups.length; i++)
			{
				JRCrosstabColumnGroup columnGroup = colGroups[i];
				JRCrosstabBucket bucket = columnGroup.getBucket();
				datasetCollector.addExpression(bucket.getExpression());
				
				crosstabCollector.pushContextObject(columnGroup);
				//order by expression is in the crosstab context
				crosstabCollector.addExpression(bucket.getOrderByExpression());
				addExpression(bucket.getComparatorExpression());
				crosstabCollector.collect(columnGroup.getCrosstabHeader());
				crosstabCollector.collect(columnGroup.getHeader());
				crosstabCollector.collect(columnGroup.getTotalHeader());
				crosstabCollector.popContextObject();
			}
		}

		JRCrosstabMeasure[] measures = crosstab.getMeasures();
		if (measures != null)
		{
			for (int i = 0; i < measures.length; i++)
			{
				datasetCollector.addExpression(measures[i].getValueExpression());
			}
		}

		crosstabCollector.collect(crosstab.getWhenNoDataCell());

		collectCrosstabCells(crosstab, crosstabCollector);
	}


	private void createCrosstabId(JRCrosstab crosstab)
	{
		crosstabIds.put(crosstab, crosstabIds.size());
	}


	private void collectCrosstabCells(JRCrosstab crosstab, JRExpressionCollector crosstabCollector)
	{
		if (crosstab instanceof JRDesignCrosstab)
		{
			List<JRCrosstabCell> cellsList = ((JRDesignCrosstab) crosstab).getCellsList();

			if (cellsList != null)
			{
				for (Iterator<JRCrosstabCell> iter = cellsList.iterator(); iter.hasNext();)
				{
					JRCrosstabCell cell = iter.next();
					crosstabCollector.collect(cell.getContents());
				}
			}
		}
		else
		{
			JRCrosstabCell[][] cells = crosstab.getCells();
			if (cells != null)
			{
				for (int i = 0; i < cells.length; ++i)
				{
					for (int j = 0; j < cells[i].length; j++)
					{
						if (cells[i][j] != null)
						{
							crosstabCollector.collect(cells[i][j].getContents());
						}
					}
				}
			}
		}
	}


	/**
	 * Collects expressions from a dataset.
	 *
	 * @param dataset the dataset
	 * @return collected expressions
	 */
	public Collection<JRExpression> collect(JRDataset dataset)
	{
		JRExpressionCollector collector = getCollector(dataset);

		collector.collect(report.getStyles(), true);
		collector.collectPropertyExpressions(dataset.getPropertyExpressions());
		collector.collect(dataset.getParameters());
		collector.collect(dataset.getFields());
		collector.collect(dataset.getVariables());
		collector.collect(dataset.getGroups());

		collector.addExpression(dataset.getFilterExpression());
		
		collector.collect(dataset.getScriptlets());

		return getExpressions(dataset);
	}


	/**
	 * Collects expressions from an element dataset.
	 *
	 * @param dataset the element dataset
	 */
	public void collect(JRElementDataset dataset)
	{
		collect(dataset.getDatasetRun());

		JRExpression incrementWhenExpression = dataset.getIncrementWhenExpression();
		if (incrementWhenExpression != null)
		{
			JRExpressionCollector datasetCollector = getCollector(dataset);
			datasetCollector.addExpression(incrementWhenExpression);
		}
	}


	/**
	 * Collects expressions from a subdataset run object.
	 * 
	 * @param datasetRun the subdataset run
	 */
	public void collect(JRDatasetRun datasetRun)
	{
		if (datasetRun != null)
		{
			addExpression(datasetRun.getParametersMapExpression());
			addExpression(datasetRun.getConnectionExpression());
			addExpression(datasetRun.getDataSourceExpression());

			JRDatasetParameter[] parameters = datasetRun.getParameters();
			if (parameters != null && parameters.length > 0)
			{
				for (int i = 0; i < parameters.length; i++)
				{
					addExpression(parameters[i].getExpression());
				}
			}
		}
	}


	protected void collect(JRCellContents cell)
	{
		if (cell != null)
		{
			collect(cell.getStyle());
			JRElement[] elements = cell.getElements();
			if (elements != null && elements.length > 0)
			{
				for(int i = 0; i < elements.length; i++)
				{
					elements[i].collectExpressions(this);
				}
			}
		}
	}


	public void collect(JRFrame frame)
	{
		collectElement(frame);
		JRElement[] elements = frame.getElements();
		if (elements != null)
		{
			for (int i = 0; i < elements.length; i++)
			{
				elements[i].collectExpressions(this);
			}
		}
	}
	
	/**
	 * Collects expressions from a component element wrapper.
	 * 
	 * <p>
	 * Common element expressions are collected, and then the component
	 * compiler's
	 * {@link ComponentCompiler#collectExpressions(Component, JRExpressionCollector)}
	 * is called to collect component expressions.
	 * 
	 * @param componentElement the component element
	 */
	public void collect(JRComponentElement componentElement)
	{
		collectElement(componentElement);
		
		Component component = componentElement.getComponent();
		ComponentManager manager = ComponentsEnvironment.getInstance(jasperReportsContext).getManager(component);
		manager.getComponentCompiler(jasperReportsContext).collectExpressions(component, this);
	}
	
	/**
	 * Collects expressions from a generic element.
	 * 
	 * @param element the generic element
	 */
	public void collect(JRGenericElement element)
	{
		collectElement(element);
		
		JRGenericElementParameter[] parameters = element.getParameters();
		for (int i = 0; i < parameters.length; i++)
		{
			JRGenericElementParameter parameter = parameters[i];
			addExpression(parameter.getValueExpression());
		}
	}
	
	public void collect(MultiAxisData data)
	{
		if (data == null)
		{
			return;
		}
		
		MultiAxisDataset dataset = data.getDataset();
		collect(dataset);
		JRExpressionCollector datasetCollector = getCollector(dataset);
		
		List<DataAxis> axisList = data.getDataAxisList();
		for (DataAxis dataAxis : axisList)
		{
			for (DataAxisLevel level : dataAxis.getLevels())
			{
				collect(level, datasetCollector);
			}
		}
		
		for (DataMeasure measure : data.getMeasures())
		{
			addExpression(measure.getLabelExpression());
			datasetCollector.addExpression(measure.getValueExpression());
		}
	}

	protected void collect(DataAxisLevel level,
			JRExpressionCollector datasetCollector)
	{
		addExpression(level.getLabelExpression());
		
		DataLevelBucket bucket = level.getBucket();
		datasetCollector.addExpression(bucket.getExpression());
		datasetCollector.addExpression(bucket.getLabelExpression());
		addExpression(bucket.getComparatorExpression());
		
		List<DataLevelBucketProperty> bucketProperties = bucket.getBucketProperties();
		if (bucketProperties != null)
		{
			for (DataLevelBucketProperty bucketProperty : bucketProperties)
			{
				datasetCollector.addExpression(bucketProperty.getValueExpression());
			}
		}
	}

	public JasperReportsContext getJasperReportsContext()
	{
		return jasperReportsContext;
	}
}
