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

import java.util.List;

import net.sf.jasperreports.engine.CommonReturnValue;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.fill.FillReturnValues.SourceContext;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.util.JRClassLoader;


/**
 * Implementation of {@link net.sf.jasperreports.engine.CommonReturnValue CommonReturnValue}
 * used by the filler.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public abstract class JRFillCommonReturnValue implements CommonReturnValue
{
	public static final String EXCEPTION_MESSAGE_KEY_INCREMENTER_CLASS_NOT_FOUND = "fill.subreport.return.value.incrementer.class.not.found";

	protected final String toVariable;
	protected final String incrementerFactoryClassName;
	protected final CalculationEnum calculation;
	private boolean derived;

	protected JRIncrementer incrementer;
	
	protected final BaseReportFiller filler;


	protected JRFillCommonReturnValue(
		CommonReturnValue returnValue, 
		JRFillObjectFactory factory, 
		BaseReportFiller filler
		)
	{
		factory.put(returnValue, this);

		toVariable = returnValue.getToVariable();
		incrementerFactoryClassName = returnValue.getIncrementerFactoryClassName();
		calculation = returnValue.getCalculation();
		
		this.filler = filler;
	}

	/**
	 * Cloning constructor.
	 * 
	 * @param fillReturnValue the object to clone
	 * @param factory the clone factory
	 */
	protected JRFillCommonReturnValue(JRFillCommonReturnValue fillReturnValue, JRFillCloneFactory factory)
	{
		this.toVariable = fillReturnValue.toVariable;
		this.incrementerFactoryClassName = fillReturnValue.incrementerFactoryClassName;
		this.calculation = fillReturnValue.calculation;
		this.derived = fillReturnValue.derived;
		this.incrementer = fillReturnValue.incrementer;
		this.filler = fillReturnValue.filler;
	}

	@Override
	public String getToVariable()
	{
		return toVariable;
	}
		
	@Override
	public String getIncrementerFactoryClassName()
	{
		return incrementerFactoryClassName;
	}
		
	@Override
	public CalculationEnum getCalculation()
	{
		return CalculationEnum.getValueOrDefault(calculation);
	}

		
	/**
	 * Gets the incrementer to be used for this copied value.
	 */
	public JRIncrementer getIncrementer(SourceContext sourceContext)
	{
		if (incrementer == null)
		{
			String incrementerFactoryClassName = getIncrementerFactoryClassName();
			
			JRIncrementerFactory incrementerFactory;
			if (incrementerFactoryClassName == null)
			{
				JRVariable toVariable = sourceContext.getToVariable(getToVariable());
				incrementerFactory = JRDefaultIncrementerFactory.getFactory(toVariable.getValueClass());
			}
			else
			{
				try
				{
					Class<?> incrementerFactoryClass = JRClassLoader.resolveClassForName(incrementerFactoryClassName);
					incrementerFactory = JRIncrementerFactoryCache.getInstance(incrementerFactoryClass); 
				}
				catch (ClassNotFoundException e)
				{
					throw 
						new JRRuntimeException(
							EXCEPTION_MESSAGE_KEY_INCREMENTER_CLASS_NOT_FOUND,
							new Object[]{incrementerFactoryClassName},
							e);
				}
			}
			
			incrementer = incrementerFactory.getIncrementer(getCalculation());
		}
		
		return incrementer;
	}

	public boolean isDerived()
	{
		return derived;
	}

	public void setDerived(boolean derived)
	{
		this.derived = derived;
	}
	
	protected JRFillCommonReturnValue addReturnValue(
			JRFillCommonReturnValue returnValue,
			List<JRFillCommonReturnValue> returnValueList,
			JRFillObjectFactory factory, BaseReportFiller filler)
	{
		CalculationEnum calculation = returnValue.getCalculation();
		switch (calculation)
		{
			case AVERAGE:
			case VARIANCE:
			{
				CommonReturnValue countVal = returnValue.createHelperReturnValue(returnValue, "_COUNT", CalculationEnum.COUNT);
				returnValue.addDerivedReturnValue(countVal, returnValueList, factory, filler);

				CommonReturnValue sumVal = returnValue.createHelperReturnValue(returnValue, "_SUM", CalculationEnum.SUM);
				returnValue.addDerivedReturnValue(sumVal, returnValueList, factory, filler);

				filler.addVariableCalculationReq(returnValue.getToVariable(), calculation);

				break;
			}
			case STANDARD_DEVIATION:
			{
				CommonReturnValue varianceVal = returnValue.createHelperReturnValue(returnValue, "_VARIANCE", CalculationEnum.VARIANCE);
				returnValue.addDerivedReturnValue(varianceVal, returnValueList, factory, filler);
				
				filler.addVariableCalculationReq(returnValue.getToVariable(), calculation);
				break;
			}
			case DISTINCT_COUNT:
			{
				CommonReturnValue countVal = returnValue.createDistinctCountHelperReturnValue(returnValue);
				returnValue.addDerivedReturnValue(countVal, returnValueList, factory, filler);
				
				filler.addVariableCalculationReq(returnValue.getToVariable(), calculation);
				break;
			}
			default:
		}

		returnValueList.add(returnValue);
		return returnValue;
	}

	protected abstract JRFillCommonReturnValue addDerivedReturnValue (
			CommonReturnValue parentReturnValue, 
			List<JRFillCommonReturnValue> returnValueList, 
			JRFillObjectFactory factory, BaseReportFiller filler
			);
	
	protected abstract CommonReturnValue createHelperReturnValue(CommonReturnValue returnValue, String nameSuffix, CalculationEnum calculation);

	protected abstract CommonReturnValue createDistinctCountHelperReturnValue(CommonReturnValue returnValue);

	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
