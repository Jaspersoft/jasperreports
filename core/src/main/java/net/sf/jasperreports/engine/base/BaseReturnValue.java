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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectStreamClass;

import net.sf.jasperreports.engine.JRConstants;
import net.sf.jasperreports.engine.ReturnValue;
import net.sf.jasperreports.engine.type.CalculationEnum;

/**
 * Base implementation of {@link net.sf.jasperreports.engine.ReturnValue ReturnValue}.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class BaseReturnValue extends BaseCommonReturnValue implements ReturnValue
{
	/**
	 * 
	 */
	private static final long serialVersionUID = JRConstants.SERIAL_VERSION_UID;

	/**
	 * The name of the variable to be copied.
	 */
	protected String fromVariable;

	
	protected BaseReturnValue()
	{
	}

	
	protected BaseReturnValue(ReturnValue returnValue, JRBaseObjectFactory factory)
	{
		super(returnValue, factory);

		fromVariable = returnValue.getFromVariable();
	}

	/**
	 * Returns the name of the variable whose value should be copied.
	 * 
	 * @return the name of the variable whose value should be copied.
	 */
	@Override
	public String getFromVariable()
	{
		return this.fromVariable;
	}

	@Override
	public Object clone() 
	{
		return super.clone();
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		GetField fields = stream.readFields();
		fromVariable = (String) fields.get("fromVariable", null);
		
		// the fields of BaseCommonReturnValue were originally in this class.
		// if deserializing an old object, we need to manually copy the values into the parent class.
		ObjectStreamClass streamClass = fields.getObjectStreamClass();
		if (streamClass.getField("toVariable") != null)
		{
			this.toVariable = (String) fields.get("toVariable", null);
		}
		if (streamClass.getField("calculation") != null)
		{
			this.calculation = (CalculationEnum) fields.get("calculation", null);
		}
		if (streamClass.getField("incrementerFactoryClassName") != null)
		{
			this.incrementerFactoryClassName = (String) fields.get("incrementerFactoryClassName", null);
		}
	}
}
