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

/*
 * Contributors:
 * Peter Severin - peter_p_s@users.sourceforge.net 
 */
package net.sf.jasperreports.engine.fill;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.type.DatasetResetTypeEnum;
import net.sf.jasperreports.engine.type.IncrementTypeEnum;
import net.sf.jasperreports.engine.type.ResetTypeEnum;
import net.sf.jasperreports.engine.type.WhenResourceMissingTypeEnum;


/**
 * Evaluates JasperReports expressions.
 * <p>
 * The expressions calculator is the entity inside JasperReports that evaluates
 * expressions and increments variables or datasets at report-filling time. When a report
 * template is compiled, the report compiler produces and stores in the compiled report
 * template ({@link net.sf.jasperreports.engine.JasperReport} object) information that it 
 * will use at report-filling time to build an instance of the 
 * {@link net.sf.jasperreports.engine.fill.JRCalculator} class.
 * </p><p>
 * The Java-based report compilers generate a Java source file and compile it on the fly.
 * This generated class is a subclass of the {@link net.sf.jasperreports.engine.fill.JRCalculator}, 
 * and the bytecode produced by compiling it is stored inside the 
 * {@link net.sf.jasperreports.engine.JasperReport} object. At report-filling time, this
 * bytecode is loaded and the resulting class is instantiated to obtain the calculator object
 * needed for expression evaluation.
 * </p><p>
 * Only the report compiler creates the calculator instance because only the report compiler
 * can make sense of the information it stored in the compiled report template at report compilation
 * time.
 * </p>
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRCalculator implements JRFillExpressionEvaluator
{

	/**
	 *
	 */
	protected JRFillDataset dataset;
	protected Map<String, JRFillParameter> parsm;
	protected Map<String, JRFillField> fldsm;
	protected Map<String, JRFillVariable> varsm;
	protected Map<String, JRFillGroup> grpsm;
	protected JRFillVariable[] variables;
	protected JRFillGroup[] groups;
	protected JRFillElementDataset[] datasets;

	private JRFillVariable pageNumber;
	private JRFillVariable columnNumber;
	
	/**
	 * The expression evaluator
	 */
	private final DatasetExpressionEvaluator evaluator;


	/**
	 * Creates a calculator using an expression evaluator.
	 * 
	 * @param evaluator the expression evaluator
	 */
	protected JRCalculator(DatasetExpressionEvaluator evaluator)
	{
		this.evaluator = evaluator;
	}


	/**
	 * Creates a calculator using an expression evaluator.
	 * 
	 * @param evaluator the expression evaluator
	 */
	protected JRCalculator(JREvaluator evaluator)
	{
		this((DatasetExpressionEvaluator) evaluator);
	}


	/**
	 * Initializes the calculator.
	 * 
	 * @param dataset the dataset this calculator is used for
	 * @throws JRException
	 */
	protected void init(JRFillDataset dataset) throws JRException
	{
		this.dataset = dataset;
		parsm = dataset.parametersMap;
		fldsm = dataset.fieldsMap;
		varsm = dataset.variablesMap;
		variables = dataset.variables;
		grpsm = dataset.groupsMap;
		groups = dataset.groups;
		datasets = dataset.elementDatasets;

		pageNumber = varsm.get(JRVariable.PAGE_NUMBER);
		columnNumber = varsm.get(JRVariable.COLUMN_NUMBER);
		
		WhenResourceMissingTypeEnum whenResourceMissingType = dataset.getWhenResourceMissingType();
		boolean ignoreNPE = 
			JRPropertiesUtil.getInstance(getFillDataset().getJasperReportsContext())
				.getBooleanProperty(
					getFillDataset(), 
					JREvaluator.PROPERTY_IGNORE_NPE, 
					true
					);
		evaluator.init(parsm, fldsm,varsm, whenResourceMissingType, ignoreNPE);
	}


	/**
	 *
	 */
	public JRFillVariable getPageNumber()
	{
		return pageNumber;
	}
	

	/**
	 *
	 */
	public JRFillVariable getColumnNumber()
	{
		return columnNumber;
	}
	

	/**
	 *
	 */
	public void calculateVariables(boolean incrementDatasets) throws JRException
	{
		if (variables != null && variables.length > 0)
		{
			for (JRFillVariable variable : variables)
			{
				Object expressionValue = evaluate(variable.getExpression());
				Object newValue = variable.getIncrementer().increment(variable, expressionValue, AbstractValueProvider.getCurrentValueProvider());
				variable.setValue(newValue);
				variable.setInitialized(false);
				variable.setPreviousIncrementedValue(variable.getIncrementedValue());

				if (variable.getIncrementType() == IncrementTypeEnum.NONE)
				{
					variable.setIncrementedValue(variable.getValue());
				}
			}
		}

		if (incrementDatasets && datasets != null && datasets.length > 0)
		{
			for (JRFillElementDataset elementDataset : datasets)
			{
				elementDataset.evaluate(this);

				if (elementDataset.getIncrementType() == IncrementTypeEnum.NONE)
				{
					elementDataset.increment();
				}
			}
		}
	}


	protected void recalculateVariables() throws JRException
	{
		if (variables != null)
		{
			for (JRFillVariable variable : variables)
			{
				variable.setIncrementedValue(variable.getPreviousIncrementedValue());
			}
		}
		
		calculateVariables(false);
	}


	/**
	 *
	 */
	public void estimateVariables() throws JRException
	{
		if (variables != null && variables.length > 0)
		{
			for (JRFillVariable variable : variables)
			{
				Object expressionValue = evaluateEstimated(variable.getExpression());
				Object newValue = variable.getIncrementer().increment(variable, expressionValue,  AbstractValueProvider.getEstimatedValueProvider());
				variable.setEstimatedValue(newValue);
				//variable.setInitialized(false);
			}
		}
	}


	/**
	 * Determines group breaks based on estimated report values. 
	 * <p>
	 * {@link #estimateVariables() estimateVariables()} needs to be called prior to this method.
	 * </p>
	 * 
	 * @throws JRException
	 */
	public void estimateGroupRuptures() throws JRException
	{
		if (groups != null && groups.length > 0)
		{
			// we are making a first group break estimation pass just so that we give inner group level 
			// increment variables the chance to increment themselves, just in case they are participating 
			// into the group expression of outer groups 
			for (int i = groups.length - 1; i >= 0; i--)
			{
				JRFillGroup group = groups[i];
				
				Object oldValue = evaluateOld(group.getExpression());
				Object estimatedValue = evaluateEstimated(group.getExpression());

				if ( 
					(oldValue == null && estimatedValue != null) ||
					(oldValue != null && !oldValue.equals(estimatedValue))
					)
				{
					group.setHasChanged(true);
				}
				else
				{
					group.setHasChanged(false);
				}
			}

			// incrementing inner group level increment variables, just in case they are participating 
			// into the group expression of outer groups
			if (variables != null && variables.length > 0)
			{
				for (JRFillVariable variable : variables)
				{
					if (variable.getIncrementType() == IncrementTypeEnum.GROUP)
					{
						JRFillGroup group = grpsm.get(variable.getIncrementGroup());
						if (group.hasChanged())
						{
							variable.setIncrementedValue(variable.getValue());
						}
					}
				}
			}
			
			// estimate variables again so that group level increment variables that might have been 
			// incremented above, are taken into consideration
			estimateVariables();

			boolean groupHasChanged = false;
			for (JRFillGroup group : groups)
			{
				boolean isTopLevelChange = false;

				if (!groupHasChanged)
				{
					Object oldValue = evaluateOld(group.getExpression());
					Object estimatedValue = evaluateEstimated(group.getExpression());

					if ( 
						(oldValue == null && estimatedValue != null) ||
						(oldValue != null && !oldValue.equals(estimatedValue))
						)
					{
						groupHasChanged = true;
						isTopLevelChange = true;
					}
				}

				group.setHasChanged(groupHasChanged);
				group.setTopLevelChange(isTopLevelChange);
			}
		}
	}


	/**
	 *
	 */
	public void initializeVariables(ResetTypeEnum resetType, IncrementTypeEnum incrementType) throws JRException
	{
		if (variables != null && variables.length > 0)
		{
			for (JRFillVariable variable : variables)
			{
				incrementVariable(variable, incrementType);
				initializeVariable(variable, resetType);
			}
		}

		if (datasets != null && datasets.length > 0)
		{
			for (JRFillElementDataset dataset : datasets)
			{
				incrementDataset(dataset, incrementType);
				initializeDataset(dataset, resetType);
			}
		}
	}


	/**
	 *
	 */
	private void incrementVariable(JRFillVariable variable, IncrementTypeEnum incrementType)
	{
		if (variable.getIncrementType() != IncrementTypeEnum.NONE)
		{
			boolean toIncrement = false;
			boolean toSetPreviousValue = false;
			switch (incrementType)
			{
				case REPORT :
				{
					toIncrement = true;
					break;
				}
				case PAGE :
				{
					toIncrement = 
						(
						variable.getIncrementType() == IncrementTypeEnum.PAGE || 
						variable.getIncrementType() == IncrementTypeEnum.COLUMN
						);
					toSetPreviousValue = toIncrement;
					break;
				}
				case COLUMN :
				{
					toIncrement = (variable.getIncrementType() == IncrementTypeEnum.COLUMN);
					toSetPreviousValue = toIncrement;
					break;
				}
				case GROUP :
				{
					if (variable.getIncrementType() == IncrementTypeEnum.GROUP)
					{
						JRFillGroup group = grpsm.get(variable.getIncrementGroup());
						toIncrement = group.hasChanged();
					}
					break;
				}
				case NONE :
				default :
				{
				}
			}

			if (toIncrement)
			{
				variable.setIncrementedValue(variable.getValue());
				if (toSetPreviousValue)
				{
					variable.setPreviousIncrementedValue(variable.getValue());
				}
//				variable.setValue(
//					evaluate(variable.getInitialValueExpression())
//					);
//				variable.setInitialized(true);
			}
		}
		else
		{
			variable.setIncrementedValue(variable.getValue());
//			variable.setValue(
//				evaluate(variable.getExpression())
//				);
		}
	}


	/**
	 *
	 */
	private void incrementDataset(JRFillElementDataset elementDataset, IncrementTypeEnum incrementType)
	{
		if (elementDataset.getIncrementType() != IncrementTypeEnum.NONE)
		{
			boolean toIncrement = false;
			switch (incrementType)
			{
				case REPORT :
				{
					toIncrement = true;
					break;
				}
				case PAGE :
				{
					toIncrement = 
						(
						elementDataset.getIncrementType() == IncrementTypeEnum.PAGE || 
						elementDataset.getIncrementType() == IncrementTypeEnum.COLUMN
						);
					break;
				}
				case COLUMN :
				{
					toIncrement = (elementDataset.getIncrementType() == IncrementTypeEnum.COLUMN);
					break;
				}
				case GROUP :
				{
					if (elementDataset.getIncrementType() == IncrementTypeEnum.GROUP)
					{
						JRFillGroup group = grpsm.get(elementDataset.getIncrementGroup());
						toIncrement = group.hasChanged();
					}
					break;
				}
				case NONE :
				default :
				{
				}
			}

			if (toIncrement)
			{
				elementDataset.increment();
			}
		}
	}


	/**
	 *
	 */
	private void initializeVariable(JRFillVariable variable, ResetTypeEnum resetType) throws JRException
	{
		//if (jrVariable.getCalculation() != JRVariable.CALCULATION_NOTHING)
		if (variable.getResetType() != ResetTypeEnum.NONE)
		{
			boolean toInitialize = false;
			boolean toSetOldValue = false;
			switch (resetType)
			{
				case REPORT :
				{
					toInitialize = true;
					break;
				}
				case PAGE :
				{
					toInitialize = 
						(
						variable.getResetType() == ResetTypeEnum.PAGE || 
						variable.getResetType() == ResetTypeEnum.COLUMN
						);
					toSetOldValue = toInitialize;
					break;
				}
				case COLUMN :
				{
					toInitialize = (variable.getResetType() == ResetTypeEnum.COLUMN);
					toSetOldValue = toInitialize;
					break;
				}
				case GROUP :
				{
					if (variable.getResetType() == ResetTypeEnum.GROUP)
					{
						JRFillGroup group = grpsm.get(variable.getResetGroup());
						toInitialize = group.hasChanged();
					}
					break;
				}
				case NONE :
				default :
				{
				}
			}

			if (toInitialize)
			{
				variable.setValue(
					evaluate(variable.getInitialValueExpression())
					);
				variable.setInitialized(true);
				variable.setIncrementedValue(null);
				if (toSetOldValue)
				{
					variable.setOldValue(variable.getValue());
					variable.setPreviousIncrementedValue(null);
				}
			}
		}
		else
		{
			variable.setValue(
				evaluate(variable.getExpression())
				);
			variable.setIncrementedValue(variable.getValue());
		}
	}


	/**
	 *
	 */
	private void initializeDataset(JRFillElementDataset elementDataset, ResetTypeEnum resetType)
	{
		boolean toInitialize = false;
		switch (resetType)
		{
			case REPORT :
			{
				toInitialize = true;
				break;
			}
			case PAGE :
			{
				toInitialize = 
					(
					elementDataset.getDatasetResetType() == DatasetResetTypeEnum.PAGE || 
					elementDataset.getDatasetResetType() == DatasetResetTypeEnum.COLUMN
					);
				break;
			}
			case COLUMN :
			{
				toInitialize = (elementDataset.getDatasetResetType() == DatasetResetTypeEnum.COLUMN);
				break;
			}
			case GROUP :
			{
				if (elementDataset.getDatasetResetType() == DatasetResetTypeEnum.GROUP)
				{
					JRFillGroup group = grpsm.get(elementDataset.getResetGroup());
					toInitialize = group.hasChanged();
				}
				else if (elementDataset.getDatasetResetType() == DatasetResetTypeEnum.NONE)
				{
					// None reset type for datasets means resetting at each record
					toInitialize = true;
				}
				break;
			}
			case NONE :
			default :
			{
			}
		}

		if (toInitialize)
		{
			elementDataset.initialize();
		}
	}


	@Override
	public Object evaluate(
		JRExpression expression,
		byte evaluationType
		) throws JRException
	{
		Object value = null;
		
		switch (evaluationType)
		{
			case JRExpression.EVALUATION_OLD :
			{
				value = evaluateOld(expression);
				break;
			}
			case JRExpression.EVALUATION_ESTIMATED :
			{
				value = evaluateEstimated(expression);
				break;
			}
			case JRExpression.EVALUATION_DEFAULT :
			default :
			{
				value = evaluate(expression);
				break;
			}
		}

		return value;
	}
	

	/**
	 *
	 */
	public Object evaluateOld(JRExpression expression) throws JRExpressionEvalException
	{
		return evaluator.evaluateOld(expression);
	}


	/**
	 *
	 */
	public Object evaluateEstimated(JRExpression expression) throws JRExpressionEvalException
	{
		return evaluator.evaluateEstimated(expression);
	}


	/**
	 *
	 */
	public Object evaluate(JRExpression expression) throws JRExpressionEvalException
	{
		return evaluator.evaluate(expression);
	}


	@Override
	public JRFillDataset getFillDataset()
	{
		return dataset;
	}
}
