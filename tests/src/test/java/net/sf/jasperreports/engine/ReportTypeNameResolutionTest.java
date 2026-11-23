/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2025 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
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

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.analytics.dataset.DesignDataMeasure;
import net.sf.jasperreports.engine.design.JRDesignParameter;

/**
 * Tests that type names coming from a report definition are resolved without running any code
 * of the named classes.
 *
 */
public class ReportTypeNameResolutionTest
{

	/**
	 * Records code execution of the probe classes. Kept separate from the probes so that reading a
	 * flag does not, by itself, initialize the probe whose state is being observed.
	 */
	static class Flags
	{
		static volatile boolean nestedTypeStaticInitRan = false;
		static volatile boolean incrementerFactoryStaticInitRan = false;
	}

	/**
	 * Stands in for a malicious class named as a parameter nested type.
	 */
	public static class NestedTypeProbe
	{
		static
		{
			Flags.nestedTypeStaticInitRan = true;
		}
	}

	/**
	 * Stands in for a malicious class named as a measure incrementer factory.
	 */
	public static class IncrementerFactoryProbe
	{
		static
		{
			Flags.incrementerFactoryStaticInitRan = true;
		}
	}

	@Test
	public void parameterNestedTypeNotInitialized()
	{
		JRDesignParameter parameter = new JRDesignParameter();
		// a class literal does not trigger initialization, so naming the probe this way is safe
		parameter.setNestedTypeName(NestedTypeProbe.class.getName());

		Class<?> nestedType = parameter.getNestedType();

		assert nestedType == NestedTypeProbe.class;
		assert !Flags.nestedTypeStaticInitRan : "the static initializer must not run when resolving the nested type";
	}

	@Test
	public void measureIncrementerFactoryNotInitialized()
	{
		DesignDataMeasure measure = new DesignDataMeasure();
		measure.setIncrementerFactoryClassName(IncrementerFactoryProbe.class.getName());

		Class<?> incrementerFactoryClass = measure.getIncrementerFactoryClass();

		assert incrementerFactoryClass == IncrementerFactoryProbe.class;
		assert !Flags.incrementerFactoryStaticInitRan : "the static initializer must not run when resolving the incrementer factory class";
	}

}
