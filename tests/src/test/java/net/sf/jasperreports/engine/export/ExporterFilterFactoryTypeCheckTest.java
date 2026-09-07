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
package net.sf.jasperreports.engine.export;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRException;

/**
 * Tests that the exporter filter factory class, which can be named through a report property, is
 * verified against {@link ExporterFilterFactory} before it is instantiated, so that a class which
 * is not a filter factory is rejected without running any of its code.
 *
 */
public class ExporterFilterFactoryTypeCheckTest
{

	/**
	 * Records code execution of the probe classes. Kept separate from the probes so that reading a
	 * flag does not, by itself, initialize the probe whose state is being observed.
	 */
	static class Flags
	{
		static volatile boolean wrongTypeStaticInitRan = false;
		static volatile boolean wrongTypeConstructorRan = false;
		static volatile boolean validConstructorRan = false;
	}

	/**
	 * Not an {@link ExporterFilterFactory}; stands in for a malicious class named in a report.
	 */
	public static class NotAFilterFactory
	{
		static
		{
			Flags.wrongTypeStaticInitRan = true;
		}

		public NotAFilterFactory()
		{
			Flags.wrongTypeConstructorRan = true;
		}
	}

	public static class ValidFilterFactory implements ExporterFilterFactory
	{
		public ValidFilterFactory()
		{
			Flags.validConstructorRan = true;
		}

		@Override
		public ExporterFilter getFilter(JRExporterContext exporterContext) throws JRException
		{
			return null;
		}
	}

	@Test
	public void wrongTypeRejectedWithoutRunningItsCode() throws JRException
	{
		boolean rejected = false;
		try
		{
			// a class literal does not trigger initialization, so naming the probe this way is safe
			ExporterFilterFactoryUtil.getFilterFactory(NotAFilterFactory.class.getName());
		}
		catch (ClassCastException e)
		{
			rejected = true;
		}

		assert rejected : "a class that is not an ExporterFilterFactory must be rejected";
		assert !Flags.wrongTypeStaticInitRan : "the static initializer must not run for a rejected class";
		assert !Flags.wrongTypeConstructorRan : "the constructor must not run for a rejected class";
	}

	@Test
	public void validTypeInstantiated() throws JRException
	{
		ExporterFilterFactory factory =
			ExporterFilterFactoryUtil.getFilterFactory(ValidFilterFactory.class.getName());

		assert factory instanceof ValidFilterFactory;
		assert Flags.validConstructorRan : "the constructor should run for a valid ExporterFilterFactory";
	}

}
