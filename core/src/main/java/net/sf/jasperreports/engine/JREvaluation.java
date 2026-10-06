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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import net.sf.jasperreports.engine.type.EvaluationTimeEnum;


/**
 * Provides support for expressions evaluation.
 * <p/>
 * Normally, all report expressions are evaluated immediately, using the current values of
 * all the parameters, fields, and variables at that particular moment. It is like making a
 * photo of all data for every iteration in the data source during the report-filling process.
 * This means that at any particular time, you won't have access to values that are going to
 * be calculated later in the report-filling process. This makes perfect sense, since all the
 * variables are calculated step by step and reach their final value only when the iteration
 * arrives at the end of the data source range they cover.
 * <p/>
 * For example, a report variable that calculates the sum of a field for each page will not
 * contain the expected sum until the end of the page is reached. That's because the sum is
 * calculated step by step as the data source records are iterated through. At any particular
 * time, the sum will only be partial, since not all the records of the specified range will
 * have been processed.
 * <p/>
 * As a consequence, you cannot display a sum on the page header, since this value will be
 * known only when the end of the page is reached. At the beginning of the page, when
 * generating the page header, the sum variable would contain zero, or its initial value. To
 * address this problem, JasperReports provides a feature (the <code>evaluationTime</code> attribute)
 * that lets you decide the exact moment you want the text field expression to be evaluated,
 * avoiding the default behavior in which the expression is evaluated immediately when the
 * current report section is generated.
 * <p/>
 * The <code>evaluationTime</code> attribute can have one of the following values (see {@link #getEvaluationTime()}):
 * <ul>
 * <li><code>Now</code> - The expression is evaluated when the current band is filled.</li>
 * <li><code>Report</code> - The expression is evaluated when the end of the report is reached.</li>
 * <li><code>Page</code> - The expression is evaluated when the end of the current page is reached</li>
 * <li><code>Column</code> - The expression is evaluated when the end of the current column is reached</li>
 * <li><code>Group</code> - The expression is evaluated when the group specified by the <code>evaluationGroup</code> 
 * attribute (see {@link #getEvaluationGroup()}) changes</li>
 * <li><code>Auto</code> - Each variable participating in the expression is evaluated at a time corresponding 
 * to its reset type. Fields are evaluated <code>Now</code>. This evaluation type should be used for expressions 
 * that combine values evaluated at different times, like the percentage out of a total</li>
 * </ul>
 * The default value for this attribute is <code>Now</code>. In the example
 * presented previously, you could easily specify <code>evaluationTime="Page"</code> for the text
 * field placed in the page header section, so that it displays the value of the sum variable
 * only when reaching the end of the current page.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public interface JREvaluation
{

	/**
	 * Gets the evaluation time for this text field.
	 * @return one of the evaluation time constants in {@link JRExpression}
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JacksonXmlProperty(isAttribute = true)
	public EvaluationTimeEnum getEvaluationTime();

	/**
	 * Gets the evaluation group for this text field. Used only when evaluation time is group.
	 * @see EvaluationTimeEnum#GROUP
	 */
	@JacksonXmlProperty(isAttribute = true)
	public String getEvaluationGroup();

}
