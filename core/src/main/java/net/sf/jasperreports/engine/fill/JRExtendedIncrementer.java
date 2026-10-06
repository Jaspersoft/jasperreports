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

import net.sf.jasperreports.engine.JRException;

/**
 * Extended incrementer interface.
 * <p>
 * The {@link net.sf.jasperreports.engine.fill.JRIncrementer JRIncrementer} has been
 * kept for backward compatibility.
 * <p>
 * The crosstab calculation engine requires extended incrementers.  An incrementer implementing
 * {@link net.sf.jasperreports.engine.fill.JRIncrementer JRIncrementer} can be used for report
 * variables only.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface JRExtendedIncrementer extends JRIncrementer
{
	/**
	 * Increments a calculable object with a value.
	 * 
	 * @param calculable the calculable
	 * @param expressionValue the value
	 * @param valueProvider value provider
	 * @return the incremented value
	 * @throws JRException
	 */
	public Object increment(
			JRCalculable calculable, 
			Object expressionValue, 
			AbstractValueProvider valueProvider
			) throws JRException;

	/**
	 * Returns the initial value for this calculation.
	 * <p>
	 * This method should return a neutral value for this calculation
	 * (e.g. 0 for sum, 1 for product, etc) or a default value if no neutral value exists.
	 * 
	 * @return the initial value for this calculation
	 */
	public Object initialValue();

	/**
	 * Combines two calculated values into one.
	 * 
	 * @param calculable the first calculated value
	 * @param calculableValue the second calculated value
	 * @param valueProvider the value provider used for the helper variables
	 * @return the combined value
	 * @throws JRException
	 */
	public Object combine(
		JRCalculable calculable, 
		JRCalculable calculableValue, 
		AbstractValueProvider valueProvider
		) throws JRException;
	
	/**
	 * Specifies whether <code>null</code> values are ignored by this incrementer.
	 * 
	 * If <code>null</code> values are ignored, the caller can chose to skip
	 * incrementing a calculation with a <code>null</code> value.
	 * 
	 * @return whether <code>null</code> values are ignored by this incrementer
	 */
	public boolean ignoresNullValues();
	
}
