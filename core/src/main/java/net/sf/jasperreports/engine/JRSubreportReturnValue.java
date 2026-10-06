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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.engine.design.JRDesignSubreportReturnValue;

/**
 * A value copied from a subreport into a variable of the master report.
 * <p/>
 * The <code>subreportVariable</code> attribute (see {@link #getFromVariable()}) specifies the name 
 * of the subreport variable whose value is to be returned. At fill time, the name is checked to 
 * ensure it is an existing variable name of the report specified by the subreport expression.
 * <p/>
 * The <code>toVariable</code> attribute (see {@link #getToVariable()}) specifies the name of 
 * the parent report variable whose value is
 * to be copied/incremented with the value from the subreport. The name is checked at
 * compile time to ensure it is an existing variable name of the master report. At fill time,
 * the system checks that the types of the subreport and master variables are compatible. 
 * <p/>
 * A value returned from a subreport can simply be copied into the target master report
 * variable, or it can be subject to a certain type of calculation made on the variable. The
 * type of the operation performed with the returned value is specified by the calculation
 * attribute (see {@link #getCalculation()}), which works like the homonym attribute 
 * of the <code>&lt;variable&gt;</code> element. The default value is <code>Nothing</code>, 
 * which means that the value returned from the subreport will be simply copied into the 
 * master report variable.
 * <p/>
 * Just as for report variables, the engine lets users customize how they want the returned
 * subreport values handled. The <code>incrementerFactoryClass</code> attribute 
 * (see {@link #getIncrementerFactoryClassName()}) specifies the
 * factory class for creating the incrementer instance. The attribute is equivalent to the same
 * attribute of the <code>&lt;variable&gt;</code> element.
 * <p/>
 * A variable of the master report used when returning values from subreports should be
 * declared with <code>System</code> calculation because its value is not calculated by the main
 * calculation engine. The variable could declare a reset type, for example, when the sum of
 * a subreport total is to be calculated per one of the master's groups. The same value can
 * be returned more than once from a subreport, for example, if different calculations are
 * required.
 * <p/>
 * Note that the value from the subreport is not returned on a column or page break, but
 * only when the subreport filling is done. Also note that the calculation is a two-level
 * process - that is, if the subreport computes a total average and the master accumulates
 * values from the subreports using calculated averages, then the master result will be the
 * average of the subreport averages, not the average of the combined subreport records.
 * 
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
@JsonDeserialize(as = JRDesignSubreportReturnValue.class)
public interface JRSubreportReturnValue extends VariableReturnValue
{
}
