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
package net.sf.jasperreports.engine.base;

import java.io.Serializable;

import net.sf.jasperreports.engine.CommonReturnValue;
import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.type.CalculationEnum;

/**
 * Base implementation of {@link net.sf.jasperreports.engine.CommonReturnValue CommonReturnValue}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseCommonReturnValue implements CommonReturnValue, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * The name of the variable where the value should be copied.
	 */
	protected String toVariable;
	
	/**
	 * The calculation type.
	 */
	protected CalculationEnum calculation;
	
	/**
	 * The incrementer factory class name.
	 */
	protected String incrementerFactoryClassName;

	
	protected BaseCommonReturnValue()
	{
	}

	
	protected BaseCommonReturnValue(CommonReturnValue returnValue, JRBaseObjectFactory factory)
	{
		factory.put(returnValue, this);

		toVariable = returnValue.getToVariable();
		calculation = returnValue.getCalculation();
		incrementerFactoryClassName = returnValue.getIncrementerFactoryClassName();
	}

	/**
	 * Returns the name of the report variable where the value should be copied.
	 * 
	 * @return the name of the report variable where the value should be copied.
	 */
	@Override
	public String getToVariable()
	{
		return this.toVariable;
	}

	/**
	 * Returns the calculation type.
	 * <p>
	 * When copying the returned value, a formula can be applied such that sum,
	 * maximum, average and so on can be computed.
	 * 
	 * @return the calculation type.
	 */
	@Override
	public CalculationEnum getCalculation()
	{
		return calculation;
	}

	/**
	 * Returns the incrementer factory class name.
	 * <p>
	 * The factory will be used to increment the value of the report variable
	 * with the returned value.
	 * 
	 * @return the incrementer factory class name.
	 */
	@Override
	public String getIncrementerFactoryClassName()
	{
		return incrementerFactoryClassName;
	}

	@Override
	public Object clone() 
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new JRRuntimeException(e);
		}
	}
}
