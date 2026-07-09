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
package net.sf.jasperreports.util;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.design.JRDesignField;

/**
 * Tests that resolving the value class declared for a field does not run the class static
 * initializer. A field value class is only ever used as a type token, never instantiated by the
 * engine, so it must not execute any code merely by being named in a report.
 *
 */
public class FieldValueClassInitTest
{

	/**
	 * Records initialization of the probe. Kept separate from the probe so that reading the flag
	 * does not, by itself, initialize the probe whose state is being observed.
	 */
	static class Flags
	{
		static volatile boolean fieldValueProbeInitialized = false;
	}

	public static class FieldValueProbe
	{
		static
		{
			Flags.fieldValueProbeInitialized = true;
		}
	}

	@Test
	public void fieldValueClassNotInitialized()
	{
		JRDesignField field = new JRDesignField();
		// a class literal does not trigger initialization, so naming the probe this way is safe
		field.setValueClassName(FieldValueProbe.class.getName());

		Class<?> valueClass = field.getValueClass();

		assert valueClass == FieldValueProbe.class;
		assert !Flags.fieldValueProbeInitialized : "declaring a field value class must not initialize it";
	}

}
