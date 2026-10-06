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
package net.sf.jasperreports.engine.part;

import net.sf.jasperreports.engine.JRException;

/**
 * A component handler used while filling the report.
 * 
 * <p>
 * The fill component implementation is responsible for managing a component
 * at fill time.  A typical implementation would evaluate a set of expressions
 * and create a print element to be included in the generated report.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public interface PartFillComponent
{

	/**
	 * Initializes the fill component with the fill context.
	 * 
	 * <p>
	 * This method is called before the fill component is used.
	 * 
	 * @param fillContext the fill context
	 */
	void initialize(PartFillContext fillContext);
	
	/**
	 * Evaluates the fill component.
	 * 
	 * <p>
	 * This method would evaluate the component expressions and store the
	 * results to be used in {@link #fill(PartPrintOutput)}.
	 * 
	 * @param evaluation the evaluation type
	 * @return the evaluated part component
	 * @throws JRException
	 */
	Object evaluate(byte evaluation) throws JRException;

	/**
	 * Fills the component by creating a print element which will be included
	 * in the generated report.
	 */
	void fill(EvaluatedPart evaluatedPart, PartPrintOutput output) throws JRException;

}
