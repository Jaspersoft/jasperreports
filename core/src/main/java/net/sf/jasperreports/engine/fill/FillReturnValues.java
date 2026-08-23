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

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.CommonReturnValue;
import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.ReturnValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class FillReturnValues
{

	public static interface SourceContext
	{
		void check(CommonReturnValue returnValue) throws JRException;
		
		Object getValue(CommonReturnValue returnValue);
		
		JRFillVariable getToVariable(String name);
	}

	private static final Log log = LogFactory.getLog(FillReturnValues.class);
	
	private final BaseReportFiller filler;
	private JRFillCommonReturnValue[] returnValues;
	private JRFillBand band;
	
	public FillReturnValues(JRSubreportReturnValue[] values, JRFillObjectFactory factory, BaseReportFiller filler)
	{
		this.filler = filler;
		
		if (values != null && values.length > 0)
		{
			List<JRFillCommonReturnValue> returnValuesList = new ArrayList<>(values.length * 2);
			for (JRSubreportReturnValue returnValue : values)
			{
				JRFillVariableReturnValue fillReturnValue = factory.getSubreportReturnValue(returnValue);
				fillReturnValue.addReturnValue(fillReturnValue, returnValuesList, factory, filler);
			}
			
			returnValues = new JRFillVariableReturnValue[returnValuesList.size()];
			returnValuesList.toArray(returnValues);
		}
	}
	
	public FillReturnValues(List<ReturnValue> values, JRFillObjectFactory factory, BaseReportFiller filler)
	{
		this.filler = filler;
		
		if (values != null && !values.isEmpty())
		{
			List<JRFillCommonReturnValue> returnValuesList = new ArrayList<>(values.size() * 2);
			for (ReturnValue returnValue : values)
			{
				JRFillVariableReturnValue fillReturnValue = factory.getReturnValue(returnValue);
				fillReturnValue.addReturnValue(fillReturnValue, returnValuesList, factory, filler);
			}
			
			returnValues = new JRFillCommonReturnValue[returnValuesList.size()];
			returnValuesList.toArray(returnValues);
		}
	}

	public FillReturnValues(ExpressionReturnValue[] values, JRFillObjectFactory factory, BaseReportFiller filler)
	{
		this.filler = filler;
		
		if (values != null && values.length > 0)
		{
			List<JRFillCommonReturnValue> returnValuesList = new ArrayList<>(values.length * 2);
			for (ExpressionReturnValue returnValue : values)
			{
				JRFillExpressionReturnValue fillReturnValue = factory.getReturnValue(returnValue);
				fillReturnValue.addReturnValue(fillReturnValue, returnValuesList, factory, filler);
			}
			
			returnValues = new JRFillCommonReturnValue[returnValuesList.size()];
			returnValuesList.toArray(returnValues);
		}
	}

	protected FillReturnValues(FillReturnValues values, JRFillCloneFactory factory)
	{
		this.filler = values.filler;
		this.band = values.band;
		
		if (values.returnValues != null)
		{
			this.returnValues = new JRFillCommonReturnValue[values.returnValues.length];
			for (int i = 0; i < values.returnValues.length; i++)
			{
				this.returnValues[i] = new JRFillVariableReturnValue((JRFillVariableReturnValue)values.returnValues[i], factory);//FIXMERETURN the forced cast should be removed
			}
		}
	}

	public void setBand(JRFillBand band)
	{
		this.band = band;
		band.registerReturnValues(this);
	}

	public void saveReturnVariables()
	{
		if (log.isDebugEnabled())
		{
			log.debug("saving return variables on band " + band);
		}
		
		if (returnValues != null && band != null)
		{
			for (int i = 0; i < returnValues.length; i++)
			{
				String varName = returnValues[i].getToVariable();
				band.saveVariable(varName);
			}
		}
	}
	
	public boolean usesForReturnValue(String variableName)
	{
		boolean used = false;
		if (returnValues != null)
		{
			for (CommonReturnValue returnValue : returnValues)
			{
				if (returnValue.getToVariable().equals(variableName))
				{
					if (log.isDebugEnabled())
					{
//FIXMERETURN						log.debug("variable " + variableName 
//								+ " used for return value of " + returnValue.getFromVariable());
					}
					
					used = true;
					break;
				}
			}
		}
		return used;
	}

	/**
	 * Copies the values from the source to the variables of the master report.
	 */
	public void copyValues(SourceContext sourceContext)
	{
		if (returnValues != null && returnValues.length > 0)
		{
			for (int i = 0; i < returnValues.length; i++)
			{
				copyValue(returnValues[i], sourceContext);
			}
		}
	}

	protected void copyValue(JRFillCommonReturnValue returnValue, SourceContext sourceContext)
	{
		try
		{
			JRFillVariable variable = sourceContext.getToVariable(returnValue.getToVariable());
			Object value = sourceContext.getValue(returnValue);
			
			if (log.isTraceEnabled())
			{
//FIXMERETURN				log.trace("copying value " + value + " of " + returnValue.getFromVariable()
//						+ " to " + returnValue.getToVariable());
			}
			
			Object newValue = returnValue.getIncrementer(sourceContext).increment(variable, value, AbstractValueProvider.getCurrentValueProvider());
			variable.setOldValue(newValue);
			variable.setValue(newValue);
			variable.setIncrementedValue(newValue);
		}
		catch (JRException e)
		{
			throw new JRRuntimeException(e);
		}
	}
	
	/**
	 * Verifies the list of copied values against the subreport.
	 * 
	 * @throws JRException
	 */
	public void checkReturnValues(SourceContext sourceContext) throws JRException
	{
		if (returnValues != null && returnValues.length > 0)
		{
			for (JRFillCommonReturnValue returnValue : returnValues)
			{
				if (returnValue.isDerived())
				{
					// internally created, not checking
					continue;
				}
				
				sourceContext.check(returnValue);
			}
		}
	}
	
}
