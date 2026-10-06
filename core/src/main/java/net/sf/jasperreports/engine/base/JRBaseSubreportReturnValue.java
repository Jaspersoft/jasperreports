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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JRSubreportReturnValue;
import net.sf.jasperreports.engine.type.CalculationEnum;
import net.sf.jasperreports.engine.xml.JRXmlConstants;


/**
 * Base implementation of {@link net.sf.jasperreports.engine.JRSubreportReturnValue JRSubreportReturnValue}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class JRBaseSubreportReturnValue implements JRSubreportReturnValue, Serializable // do not extend BaseCommonReturnValue to avoid deserialization field issues 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * The name of the subreport variable to be copied.
	 */
	protected String subreportVariable;

	/**
	 * The name of the master variable where the value should be copied.
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

	
	protected JRBaseSubreportReturnValue()
	{
	}

	
	protected JRBaseSubreportReturnValue(JRSubreportReturnValue returnValue, JRBaseObjectFactory factory)
	{
		factory.put(returnValue, this);

		subreportVariable = returnValue.getFromVariable();
		toVariable = returnValue.getToVariable();
		calculation = returnValue.getCalculation();
		incrementerFactoryClassName = returnValue.getIncrementerFactoryClassName();
	}

	/**
	 * Returns the name of the variable whose value should be copied.
	 * 
	 * @return the name of the variable whose value should be copied.
	 */
	@Override
	@JsonGetter(JRXmlConstants.ATTRIBUTE_subreportVariable)
	@JacksonXmlProperty(localName = JRXmlConstants.ATTRIBUTE_subreportVariable, isAttribute = true)
	public String getFromVariable()
	{
		return this.subreportVariable;
	}

	/**
	 * Returns the name of the master report variable where the value should be copied.
	 * 
	 * @return the name of the master report variable where the value should be copied.
	 */
	@Override
	public String getToVariable()
	{
		return this.toVariable;
	}

	/**
	 * Returns the calculation type.
	 * <p>
	 * When copying the value from the subreport, a formula can be applied such that sum,
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
	 * The factory will be used to increment the value of the master report variable
	 * with the value from the subreport.
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
