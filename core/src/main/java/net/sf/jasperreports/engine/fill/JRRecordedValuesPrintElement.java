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

import java.util.Set;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

/**
 * Interface implemented by print elements that can record field/variable values.
 * <p/>
 * An element having {@link EvaluationTimeEnum#AUTO Auto evaluation time}
 * will generate an instance of this type as print element.
 * This instance will be responsible of storing field/variable values until the element can be evaluated.
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @see EvaluationTimeEnum#AUTO
 */
public interface JRRecordedValuesPrintElement extends JRPrintElement
{
	/**
	 * Initializes the recorded values set.
	 * 
	 * @param evaluationTimes future times when the values will be recorded
	 */
	void initRecordedValues(Set<JREvaluationTime> evaluationTimes);
	
	/**
	 * Returns the recorded values set.
	 * 
	 * @return the recorded values set
	 */
	JRRecordedValues getRecordedValues();
	
	
	/**
	 * Releases the recorded values set.
	 * <p/>
	 * This is called when all the recorded values are available and the element has been evaluated.
	 */
	void deleteRecordedValues();
}
