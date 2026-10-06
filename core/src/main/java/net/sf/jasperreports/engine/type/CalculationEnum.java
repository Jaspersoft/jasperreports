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


/**
 * @author Sanda Zaharia (shertage@users.sourceforge.net)
 */
public enum CalculationEnum implements NamedEnum
{
	/**
	 * The value is calculated by simply evaluating the variable expression.
	 */
	NOTHING("Nothing"),
	
	/**
	 * The value is calculated by counting the non-null values of the variable expression with every iteration in the data source.
	 * The count variable must be numeric, but the variable expression needs not, since its value is not important.
	 * On the other hand, the initial value expression must be numeric since it will be the count variable initial value.
	 */
	COUNT("Count"),
	
	/**
	 * The value is calculated by summing up the values returned by the variable's expression. Both the main expression and initial
	 * expression must have numeric type.
	 *
	 */
	SUM("Sum"),
	
	/**
	 * The value is obtained by calculating the average for the series of values obtained by evaluating the variable's
	 * expression for each record in the data source. Both the main expression and initial expression must have numeric type.
	 * <p>
	 * In order to calculate the average, the engine creates behind the scenes a helper report variable that calculates
	 * the sum of the values and uses it to calculate the average for those values. This helper sum variable gets its
	 * name from the corresponding average variable suffixed with "_SUM" sequence. This helper variable can be used
	 * in other report expressions just like any normal variable.
	 */
	AVERAGE("Average"),
	
	/**
	 * The value of the variable represents the lowest in the series of values obtained by evaluating the variable's
	 * expression for each data source record.
	 */
	LOWEST("Lowest"),
	
	/**
	 * The value of the variable represents the highest in the series of values obtained by evaluating the variable's
	 * expression for each data source record.
	 */
	HIGHEST("Highest"),
	
	/**
	 * The value is obtained by calculating the standard deviation for the series of values returned by evaluating the
	 * variable's expression.
	 * <p>
	 * Just like for the variables that calculate the average, the engine creates and uses helper report variables
	 * for first obtaining the sum and the count that correspond to your current series of values. The name for
	 * those helper variables that are created behind the scenes is obtained by suffixing the user variable with
	 * the "_SUM" or "_COUNT" suffix and they can be used in other report expressions like any other report variable.
	 * <p>
	 * For variables that calculate the standard deviation, there is always a helper variable present, that first
	 * calculates the variance for the series of values and it has the "_VARIANCE" suffix added to its name.
	 */
	STANDARD_DEVIATION("StandardDeviation"),
	
	/**
	 * The value is obtained by calculating the variance for the series of values returned by evaluating the
	 * variable's expression.
	 */
	VARIANCE("Variance"),
	
	/**
	 * The value is not calculated by JasperReports. The user must calculate the value of the variable, almost
	 * certainly using the scriptlets functionality. For this type of calculation, the only thing the engine does is
	 * to conserve the value users have calculated, from one iteration in the data source to the next.
	 */
	SYSTEM("System"),
	
	/**
	 * The variable keeps the first value and does not increment it on subsequent iterations.
	 */
	FIRST("First"),
	
	/**
	 * The value is calculated by counting the distinct non-null values of the variable expression with every iteration in the data source.
	 * The count variable must be numeric, but the variable expression needs not, since its value is not important.
	 * On the other hand, the initial value expression must be numeric since it will be the count variable initial value.
	 */
	DISTINCT_COUNT("DistinctCount");

	/**
	 *
	 */
	private final transient String name;

	private CalculationEnum(String name)
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
	public static CalculationEnum getByName(String name)
	{
		return EnumUtil.getEnumByName(values(), name);
	}
	
	/**
	 *
	 */
	public static CalculationEnum getValueOrDefault(CalculationEnum value)
	{
		return value == null ? NOTHING : value;
	}
	
	@Override
	public CalculationEnum getDefault()
	{
		return NOTHING;
	}
}
