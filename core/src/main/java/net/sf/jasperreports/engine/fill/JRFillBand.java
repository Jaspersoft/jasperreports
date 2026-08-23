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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.CommonReturnValue;
import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JROrigin;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.BandTypeEnum;
import net.sf.jasperreports.engine.type.PrintOrderEnum;
import net.sf.jasperreports.engine.type.SplitTypeEnum;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillBand extends JRFillElementContainer implements JRBand, JROriginProvider
{

	private static final Log log = LogFactory.getLog(JRFillBand.class);

	/**
	 *
	 */
	private JRBand parent;

	private boolean isPrintWhenTrue = true;

	/**
	 *
	 */
	private boolean isNewPageColumn;
	private boolean isFirstWholeOnPageColumn;
	private Map<String,Boolean> isNewGroupMap = new HashMap<>();

	private Set<JREvaluationTime> nowEvaluationTimes;
	
	// used by subreports to save values of variables used as return receptacles
	// so that the values can be restored when the bands gets rewound
	private Map<String,Object> savedVariableValues = new HashMap<>();

	protected JROrigin origin;
	
	private SplitTypeEnum splitType;
	private Integer breakHeight;

	private FillReturnValues returnValues;
	private FillReturnValues.SourceContext returnValuesContext = new FillReturnValues.SourceContext() 
	{
		@Override
		public Object getValue(CommonReturnValue returnValue) 
		{
			ExpressionReturnValue expressionReturnValue = (ExpressionReturnValue)returnValue;
			Object value = null;
			try
			{
				value = filler.evaluateExpression(expressionReturnValue.getExpression(), JRExpression.EVALUATION_DEFAULT);
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
			return value;
		}
		
		@Override
		public void check(CommonReturnValue returnValue) throws JRException 
		{
			//FIXMERETURN check something
		}

		@Override
		public JRFillVariable getToVariable(String name)
		{
			return filler.getVariable(name);
		}
	};

	private Set<FillReturnValues> returnValuesSet;
	
	/**
	 *
	 */
	protected JRFillBand(
		JRBaseFiller filler,
		JRBand band,
		JRFillObjectFactory factory
		)
	{
		super(filler, band, factory);

		parent = band;
		
		// we need to do this before setBand()
		returnValuesSet = new LinkedHashSet<>();

		if (deepElements.length > 0)
		{
			for (int i = 0; i < deepElements.length; i++)
			{
				deepElements[i].setBand(this);
			}
		}

		List<ExpressionReturnValue> expRetValues = getReturnValues();
		returnValues = 
			new FillReturnValues(
				expRetValues == null ? null : (ExpressionReturnValue[]) expRetValues.toArray(new ExpressionReturnValue[expRetValues.size()]), //FIXMERETURN make special class for constructor differentiation
				factory, 
				filler
				);
		registerReturnValues(returnValues);
		
		initElements();

		initConditionalStyles();

		nowEvaluationTimes = new HashSet<>();
	}


	@Override
	public JROrigin getOrigin()
	{
		return origin;
	}

	
	/**
	 *
	 */
	protected void setOrigin(JROrigin origin)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Origin " + origin + " for band " + getId());
		}
		
		this.origin = origin;
		this.filler.getJasperPrint().addOrigin(origin);
	}

	
	/**
	 *
	 */
	protected void setNewPageColumn(boolean isNew)
	{
		this.isNewPageColumn = isNew;
	}


	/**
	 *
	 */
	protected boolean isNewPageColumn()
	{
		return isNewPageColumn;
	}


	/**
	 * Decides whether this band is the for whole band on the page/column.
	 *
	 * @return whether this band is the for whole band on the page/column
	 */
	protected boolean isFirstWholeOnPageColumn()
	{
		return isFirstWholeOnPageColumn;
	}


	/**
	 *
	 */
	protected void setNewGroup(JRGroup group, boolean isNew)
	{
		isNewGroupMap.put(group.getName(), isNew);
	}


	/**
	 *
	 */
	protected boolean isNewGroup(String group)
	{
		Boolean value = isNewGroupMap.get(group);

		if (value == null)
		{
			value = Boolean.FALSE;
		}

		return value;
	}


	@Override
	public int getHeight()
	{
		return (parent == null ? 0 : parent.getHeight());
	}

	/**
	 *
	 */
	public int getBreakHeight()
	{
		// needs to be lazy calculated because it depends on splitType, which is itself lazy loaded
		if (breakHeight == null)
		{
			breakHeight = getHeight();
			if (
				SplitTypeEnum.IMMEDIATE == getSplitType()
				&& elements != null && elements.length > 0
				)
			{
				for (JRElement element : elements)
				{
					int bottom = element.getY() + element.getHeight();
					breakHeight = bottom < breakHeight ? bottom : breakHeight;
				}
			}
		}

		return breakHeight;
	}

	@Override
	public SplitTypeEnum getSplitType()
	{
		// needs to be lazy loaded because in JRFillBand constructor above, the filler.getMainDataset() is not yet set, 
		// when the band is a group band
		if (splitType == null)
		{
			splitType = (parent == null ? null : parent.getSplitType());
			if (splitType == null)
			{
				splitType = 
					SplitTypeEnum.getByName(
						filler.getPropertiesUtil().getProperty(filler.getMainDataset(), JRBand.PROPERTY_SPLIT_TYPE)
						);
			}
		}
		
		return splitType;
	}

	@Override
	public void setSplitType(SplitTypeEnum splitType)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JRExpression getPrintWhenExpression()
	{
		return (parent == null ? null : parent.getPrintWhenExpression());
	}

	/**
	 *
	 */
	protected boolean isSplitPrevented()
	{
		return SplitTypeEnum.PREVENT == getSplitType();
	}

	/**
	 *
	 */
	protected boolean isPrintWhenExpressionNull()
	{
		return (getPrintWhenExpression() == null);
	}

	/**
	 *
	 */
	protected boolean isPrintWhenTrue()
	{
		return isPrintWhenTrue;
	}

	/**
	 *
	 */
	protected void setPrintWhenTrue(boolean isPrintWhenTrue)
	{
		this.isPrintWhenTrue = isPrintWhenTrue;
	}

	/**
	 *
	 */
	protected boolean isToPrint()
	{
		return
			this != filler.missingFillBand
			&& (isPrintWhenExpressionNull() 
			|| (!isPrintWhenExpressionNull() && isPrintWhenTrue()));
	}


	/**
	 *
	 */
	protected void evaluatePrintWhenExpression(
		byte evaluation
		) throws JRException
	{
		boolean isPrintTrue = false;

		JRExpression expression = getPrintWhenExpression();
		if (expression != null)
		{
			Boolean printWhenExpressionValue = (Boolean)filler.evaluateExpression(expression, evaluation);
			if (printWhenExpressionValue == null)
			{
				isPrintTrue = false;
			}
			else
			{
				isPrintTrue = printWhenExpressionValue;
			}
		}

		setPrintWhenTrue(isPrintTrue);
	}



	/**
	 *
	 */
	protected JRPrintBand refill(
		byte evaluation,
		int availableHeight
		) throws JRException
	{
		rewind();
		restoreSavedVariables();

		evaluatePrintWhenExpression(evaluation);
		
		JRPrintBand printBand = null;
		
		if (isToPrint())
		{
			evaluate(evaluation);
			
			printBand = fill(availableHeight);
		}
		else
		{
			printBand = new JRPrintBand();
		}
		
		return printBand;
	}


	/**
	 *
	 */
	protected JRPrintBand fill() throws JRException
	{
		return fill(getHeight(), false);
	}


	/**
	 *
	 */
	protected JRPrintBand fill(
		int availableHeight
		) throws JRException
	{
		return fill(availableHeight, true);
	}


	/**
	 *
	 */
	protected JRPrintBand fill(
		int availableHeight,
		boolean isOverflowAllowed
		) throws JRException
	{
		filler.checkInterrupted();

		filler.setBandOverFlowAllowed(isOverflowAllowed);

		initFill();

		if (isNewPageColumn && !isOverflow)
		{
			isFirstWholeOnPageColumn = true;
		}
		
		resetElements();

		prepareElements(availableHeight, isOverflowAllowed);

		isFirstWholeOnPageColumn = isNewPageColumn && isOverflow;
		isNewPageColumn = false;
		isNewGroupMap = new HashMap<>();

		JRPrintBand printBand = new JRPrintBand();
		fillElements(printBand);
		
		if (!willOverflow())
		{
			returnValues.copyValues(returnValuesContext);
		}

		return printBand;
	}


	protected boolean willOverflowWithElements()
	{
		return willOverflowWithElements;
	}


	@Override
	protected int getContainerHeight()
	{
		return getHeight();
	}


	@Override
	protected int getActualContainerHeight()
	{
		return getContainerHeight(); 
	}


	protected boolean isVariableUsedInReturns(String variableName)
	{
		boolean used = false;
		for (FillReturnValues returnValues : returnValuesSet)
		{
			if (returnValues.usesForReturnValue(variableName))
			{
				used = true;
				break;
			}
		}
		return used;
	}


	protected void addNowEvaluationTime(JREvaluationTime evaluationTime)
	{
		nowEvaluationTimes.add(evaluationTime);
	}


	protected void addNowEvaluationTimes(JREvaluationTime[] evaluationTimes)
	{
		for (int i = 0; i < evaluationTimes.length; i++)
		{
			nowEvaluationTimes.add(evaluationTimes[i]);
		}
	}


	protected boolean isNowEvaluationTime(JREvaluationTime evaluationTime)
	{
		return nowEvaluationTimes.contains(evaluationTime);
	}


	protected int getId()
	{
		//FIXME this is not necessarily unique
		return System.identityHashCode(this);
	}


	@Override
	protected void evaluate(byte evaluation) throws JRException
	{
		resetSavedVariables();
		evaluateConditionalStyles(evaluation);
		super.evaluate(evaluation);
	}
	
	protected void resetSavedVariables()
	{
		savedVariableValues.clear();
	}
	
	protected void saveVariable(String variableName)
	{
		if (!savedVariableValues.containsKey(variableName))
		{
			Object value = filler.getVariableValue(variableName);
			savedVariableValues.put(variableName, value);
		}
	}
	
	protected void restoreSavedVariables()
	{
		for (Map.Entry<String,Object> entry : savedVariableValues.entrySet())
		{
			String variableName = entry.getKey();
			Object value = entry.getValue();
			JRFillVariable variable = filler.getVariable(variableName);
			variable.setOldValue(value);
			variable.setValue(value);
			variable.setIncrementedValue(value);
		}
	}


	protected boolean isEmpty()
	{
		return this == filler.missingFillBand
			|| (getHeight() == 0
					&& (getElements() == null || getElements().length == 0)
					&& getPrintWhenExpression() == null);
	}

	protected boolean isColumnBand()
	{
		BandTypeEnum bandType = origin.getBandType();
		
		return
			bandType == BandTypeEnum.GROUP_HEADER
			|| bandType == BandTypeEnum.GROUP_FOOTER
			|| bandType == BandTypeEnum.DETAIL;
	}

	protected boolean isSplitTypePreventInhibited()
	{
		return isSplitTypePreventInhibited(true);
	}
	
	@Override
	public boolean isSplitTypePreventInhibited(boolean isTopLevelCall)
	{
		boolean isSplitTypePreventInhibited = false;
		
		if (
			((filler.printOrder == PrintOrderEnum.VERTICAL && filler.isFirstColumnBand)
			|| (filler.printOrder == PrintOrderEnum.HORIZONTAL && filler.isFirstPageBand))
			&& (isTopLevelCall || !filler.atLeastOneElementIsToPrint)
			)
		{
			if (isColumnBand() && filler.columnIndex < filler.columnCount - 1)
			{
				isSplitTypePreventInhibited = true;
			}
			else
			{
				if (filler.isSubreport())
				{
					isSplitTypePreventInhibited = filler.getBandReportParent().isSplitTypePreventInhibited(false);
				}
				else
				{
					isSplitTypePreventInhibited = true;
				}
			}
		}
		
		return isSplitTypePreventInhibited;
	}
	
	@Override
	public boolean hasProperties()
	{
		return parent.hasProperties();
	}

	// not doing anything with the properties at fill time
	@Override
	public JRPropertiesMap getPropertiesMap()
	{
		return parent.getPropertiesMap();
	}
	
	@Override
	public JRPropertiesHolder getParentProperties()
	{
		return null;
	}

	@Override
	public List<ExpressionReturnValue> getReturnValues()
	{
		return parent == null ? null : parent.getReturnValues();
	}

	public void registerReturnValues(FillReturnValues fillReturnValues)
	{
		returnValuesSet.add(fillReturnValues);
	}

}
