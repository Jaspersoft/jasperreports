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

import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * Tests that {@link JRClassLoader#resolveClassForName(String)} loads a class without running its
 * static initializers, while {@link JRClassLoader#loadClassForName(String)} does initialize it.
 *
 */
public class ResolveClassForNameTest
{

	/**
	 * Records initialization of the probe classes. Kept separate from the probes so that reading a
	 * flag does not, by itself, initialize the probe whose state is being observed.
	 */
	static class Flags
	{
		static volatile boolean resolveProbeInitialized = false;
		static volatile boolean loadProbeInitialized = false;
	}

	public static class ResolveProbe
	{
		static
		{
			Flags.resolveProbeInitialized = true;
		}
	}

	public static class LoadProbe
	{
		static
		{
			Flags.loadProbeInitialized = true;
		}
	}

	@Test
	public void resolveDoesNotInitialize() throws ClassNotFoundException
	{
		// a class literal does not trigger initialization, so naming the probe this way is safe
		Class<?> clazz = JRClassLoader.resolveClassForName(ResolveProbe.class.getName());

		assert clazz == ResolveProbe.class;
		assert !Flags.resolveProbeInitialized : "resolveClassForName must not run the class static initializer";
	}

	@Test
	public void loadInitializes() throws ClassNotFoundException
	{
		Class<?> clazz = JRClassLoader.loadClassForName(LoadProbe.class.getName());

		assert clazz == LoadProbe.class;
		assert Flags.loadProbeInitialized : "loadClassForName should run the class static initializer";
	}

}
