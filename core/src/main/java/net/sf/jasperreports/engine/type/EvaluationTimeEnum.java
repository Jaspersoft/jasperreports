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
package net.sf.jasperreports.engine.type;

import net.sf.jasperreports.engine.JRVariable;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
public enum EvaluationTimeEnum implements NamedEnum
{
	/**
	 * A constant specifying that an expression should be evaluated at the exact moment in the filling process
	 * when it is encountered.
	 */
	NOW("Now"),

	/**
	 * A constant specifying that an expression should be evaluated at the end of the filling process.
	 */
	REPORT("Report"),

	/**
	 * A constant specifying that an expression should be evaluated after each page is filled.
	 */
	PAGE("Page"),

	/**
	 * A constant specifying that an expression should be evaluated after each column is filled.
	 */
	COLUMN("Column"),

	/**
	 * A constant specifying that an expression should be evaluated after each group break.
	 */
	GROUP("Group"),

	/**
	 * The element will be evaluated at band end.
	 */
	BAND("Band"),
	
	/**
	 * Evaluation time indicating that each variable participating in the expression
	 * should be evaluated at a time decided by the engine.
	 * <p/>
	 * Variables will be evaluated at a time corresponding to their reset type.
	 * Fields are evaluated "now", i.e. at the time the band the element lies on gets filled.
	 * <p/>
	 * This evaluation type should be used when report elements having expressions that combine 
	 * values evaluated at different times are required (e.g. percentage out of a total).
	 * <p/>
	 * NB: avoid using this evaluation type when other types suffice as it can lead
	 * to performance loss.
	 */
	AUTO("Auto"),
	
	/**
	 * Used for elements that are evaluated at the moment the master report ends.
	 * 
	 * @see JRVariable#MASTER_CURRENT_PAGE
	 * @see JRVariable#MASTER_TOTAL_PAGES
	 */
	MASTER("Master");

	/**
	 *
	 */
	private final transient String name;

	private EvaluationTimeEnum(String name)
	{
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 *
	 */
	public static EvaluationTimeEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static EvaluationTimeEnum getValueOrDefault(EvaluationTimeEnum value)
	{
		return value == null ? NOW : value;
	}
	
	@Override
	public EvaluationTimeEnum getDefault()
	{
		return NOW;
	}
}
