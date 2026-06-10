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
package net.sf.jasperreports.engine.scriptlets;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRAbstractScriptlet;
import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRException;

/**
 * Tests that the scriptlet class named in a report is verified against {@link JRAbstractScriptlet}
 * before it is instantiated, so that a class which is not a scriptlet is rejected without running
 * any of its code (neither static initializer nor constructor).
 *
 * <p>
 * This test is in the same package as {@link DefaultScriptletFactory} in order to reach its
 * protected {@link DefaultScriptletFactory#getScriptlet(String)} method.
 *
 */
public class DefaultScriptletFactoryTypeCheckTest
{

	/**
	 * Records code execution of the probe classes. Kept separate from the probes so that reading a
	 * flag does not, by itself, initialize the probe whose state is being observed.
	 */
	static class Flags
	{
		static volatile boolean notAScriptletStaticInitRan = false;
		static volatile boolean notAScriptletConstructorRan = false;
		static volatile boolean validScriptletConstructorRan = false;
	}

	/**
	 * Not a {@link JRAbstractScriptlet}; stands in for a malicious class named in a report.
	 */
	public static class NotAScriptlet
	{
		static
		{
			Flags.notAScriptletStaticInitRan = true;
		}

		public NotAScriptlet()
		{
			Flags.notAScriptletConstructorRan = true;
		}
	}

	public static class ValidScriptlet extends JRDefaultScriptlet
	{
		public ValidScriptlet()
		{
			Flags.validScriptletConstructorRan = true;
		}
	}

	@Test
	public void wrongTypeRejectedWithoutRunningItsCode() throws JRException
	{
		boolean rejected = false;
		try
		{
			// a class literal does not trigger initialization, so naming the probe this way is safe
			DefaultScriptletFactory.getInstance().getScriptlet(NotAScriptlet.class.getName());
		}
		catch (ClassCastException e)
		{
			rejected = true;
		}

		assert rejected : "a class that is not a scriptlet must be rejected";
		assert !Flags.notAScriptletStaticInitRan : "the static initializer must not run for a rejected class";
		assert !Flags.notAScriptletConstructorRan : "the constructor must not run for a rejected class";
	}

	@Test
	public void validTypeInstantiated() throws JRException
	{
		JRAbstractScriptlet scriptlet =
			DefaultScriptletFactory.getInstance().getScriptlet(ValidScriptlet.class.getName());

		assert scriptlet instanceof ValidScriptlet;
		assert Flags.validScriptletConstructorRan : "the constructor should run for a valid scriptlet";
	}

}
