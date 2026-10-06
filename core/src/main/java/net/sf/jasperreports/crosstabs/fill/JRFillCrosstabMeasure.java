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
package net.sf.jasperreports.crosstabs.fill;

import net.sf.jasperreports.crosstabs.JRCrosstabMeasure;
import net.sf.jasperreports.crosstabs.type.CrosstabPercentageEnum;
import net.sf.jasperreports.engine.JRExpression;
import net.sf.jasperreports.engine.JRVariable;
import net.sf.jasperreports.engine.fill.JRDefaultIncrementerFactory;
import net.sf.jasperreports.engine.fill.JRExtendedIncrementerFactory;
import net.sf.jasperreports.engine.fill.JRFillVariable;
import net.sf.jasperreports.engine.fill.JRIncrementerFactoryCache;
import net.sf.jasperreports.engine.type.CalculationEnum;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRFillCrosstabMeasure implements JRCrosstabMeasure
{
	protected JRCrosstabMeasure parentMeasure;
	protected JRFillVariable variable;
	protected JRExtendedIncrementerFactory incrementerFactory;
	protected JRPercentageCalculator percentageCalculator;

	public JRFillCrosstabMeasure(JRCrosstabMeasure measure, JRFillCrosstabObjectFactory factory)
	{
		factory.put(measure, this);
		
		parentMeasure = measure;
		
		variable = factory.getVariable(measure.getVariable());
		
		incrementerFactory = createIncrementerFactory();
		percentageCalculator = createPercentageCalculator();
	}

	@Override
	public String getName()
	{
		return parentMeasure.getName();
	}

	@Override
	public String getValueClassName()
	{
		return parentMeasure.getValueClassName();
	}

	@Override
	public Class<?> getValueClass()
	{
		return parentMeasure.getValueClass();
	}

	@Override
	public JRExpression getValueExpression()
	{
		return parentMeasure.getValueExpression();
	}

	@Override
	public CalculationEnum getCalculation()
	{
		return CalculationEnum.getValueOrDefault(parentMeasure.getCalculation());
	}

	@Override
	public String getIncrementerFactoryClassName()
	{
		return parentMeasure.getIncrementerFactoryClassName();
	}

	@Override
	public Class<?> getIncrementerFactoryClass()
	{
		return parentMeasure.getIncrementerFactoryClass();
	}

	@Override
	public CrosstabPercentageEnum getPercentageType()
	{
		return parentMeasure.getPercentageType();
	}

	@Override
	public JRVariable getVariable()
	{
		return variable;
	}

	public JRFillVariable getFillVariable()
	{
		return variable;
	}
	
	
	public JRExtendedIncrementerFactory getIncrementerFactory()
	{
		return incrementerFactory;
	}
	
	
	public JRPercentageCalculator getPercentageCalculator()
	{
		return percentageCalculator;
	}

	
	private JRExtendedIncrementerFactory createIncrementerFactory()
	{
		JRExtendedIncrementerFactory incrFactory;

		String incrementerFactoryClassName = getIncrementerFactoryClassName();
		if (incrementerFactoryClassName == null)
		{
			incrFactory = JRDefaultIncrementerFactory.getFactory(getValueClass());
		}
		else
		{
			incrFactory = (JRExtendedIncrementerFactory) JRIncrementerFactoryCache.getInstance(getIncrementerFactoryClass());
		}
		
		return incrFactory;
	}

	public JRPercentageCalculator createPercentageCalculator()
	{
		JRPercentageCalculator percentageCalc;
		
		if (getPercentageType() == CrosstabPercentageEnum.GRAND_TOTAL)
		{
			percentageCalc = JRPercentageCalculatorFactory.getPercentageCalculator(getPercentageCalculatorClass(), getValueClass());
		}
		else
		{
			percentageCalc = null;
		}
		
		return percentageCalc;
	}

	@Override
	public String getPercentageCalculatorClassName()
	{
		return parentMeasure.getPercentageCalculatorClassName();
	}

	@Override
	public Class<?> getPercentageCalculatorClass()
	{
		return parentMeasure.getPercentageCalculatorClass();
	}
	
	@Override
	public Object clone() 
	{
		throw new UnsupportedOperationException();
	}
}
