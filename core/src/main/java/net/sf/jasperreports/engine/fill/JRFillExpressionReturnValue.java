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

import java.util.List;

import net.sf.jasperreports.engine.CommonReturnValue;
import net.sf.jasperreports.engine.ExpressionReturnValue;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.design.DesignExpressionReturnValue;
import net.sf.jasperreports.engine.type.CalculationEnum;


/**
 * Implementation of {@link net.sf.jasperreports.engine.ExpressionReturnValue ExpressionReturnValue}
 * used by the filler.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public class JRFillExpressionReturnValue extends JRFillCommonReturnValue implements ExpressionReturnValue
{
	protected final JRExpression expression;

	public JRFillExpressionReturnValue(
		ExpressionReturnValue returnValue,
		JRFillObjectFactory factory, 
		BaseReportFiller filler
		)
	{
		super(returnValue, factory, filler);

		expression = returnValue.getExpression();
	}

	/**
	 * Cloning constructor.
	 * 
	 * @param fillReturnValue the object to clone
	 * @param factory the clone factory
	 */
	protected JRFillExpressionReturnValue(JRFillExpressionReturnValue fillReturnValue, JRFillCloneFactory factory)
	{
		super(fillReturnValue, factory);
		
		this.expression = fillReturnValue.expression;
	}

	@Override
	public JRExpression getExpression()
	{
		return expression;
	}

	@Override
	protected JRFillCommonReturnValue addDerivedReturnValue (
			CommonReturnValue parentReturnValue, 
			List<JRFillCommonReturnValue> returnValueList, 
			JRFillObjectFactory factory, BaseReportFiller filler
			)
	{
		JRFillExpressionReturnValue returnValue = factory.getReturnValue((ExpressionReturnValue)parentReturnValue);
		returnValue.setDerived(true);
		return addReturnValue(returnValue, returnValueList, factory, filler);
	}
	
	@Override
	protected CommonReturnValue createHelperReturnValue(CommonReturnValue returnValue, String nameSuffix, CalculationEnum calculation)
	{
		DesignExpressionReturnValue helper = new DesignExpressionReturnValue();
		helper.setToVariable(returnValue.getToVariable() + nameSuffix);
		helper.setExpression(((ExpressionReturnValue)returnValue).getExpression());
		helper.setCalculation(calculation);
		helper.setIncrementerFactoryClassName(helper.getIncrementerFactoryClassName());//FIXMERETURN shouldn't it be returnValue?
		
		return helper;
	}

	@Override
	protected CommonReturnValue createDistinctCountHelperReturnValue(CommonReturnValue returnValue)
	{
		DesignExpressionReturnValue helper = new DesignExpressionReturnValue();
		helper.setToVariable(returnValue.getToVariable() + "_DISTINCT_COUNT");
		helper.setExpression(((ExpressionReturnValue)returnValue).getExpression());
		helper.setCalculation(CalculationEnum.NOTHING);
		helper.setIncrementerFactoryClassName(JRDistinctCountIncrementerFactory.class.getName());
		
		return helper;
	}

}
