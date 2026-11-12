/*
 * Copyright (C) 2026 Irisel Consulting SL. All rights reserved.
 *
 * This file is part of JasperReports.
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
package net.sf.jasperreports.deserialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.util.ContextClassLoaderObjectInputStream;
import net.sf.jasperreports.engine.util.DeserializationClassFilter;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 * Tests for CVE-2026-6009 deserialization class filter fix.
 * Verifies that the deserialization filter blocks non-whitelisted classes
 * and allows whitelisted classes through all deserialization entry points.
 */
public class DeserializationClassFilterTest
{
	@Test
	public void filterEnabledByDefault()
	{
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		assert filter.isFilteringEnabled() : "Deserialization class filter should be enabled by default";
	}

	@Test
	public void whitelistedClassAllowed()
	{
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		// java.util.HashMap is in the whitelist
		filter.checkClassVisibility("java.util.HashMap");
		// should not throw
	}

	@Test
	public void whitelistedPrimitiveAllowed()
	{
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		// Primitive type descriptors are in the hardcoded whitelist
		filter.checkClassVisibility("I");
		filter.checkClassVisibility("J");
		filter.checkClassVisibility("Z");
		filter.checkClassVisibility("D");
	}

	@Test
	public void whitelistedJasperReportsClassAllowed()
	{
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		// net.sf.jasperreports.engine.* is in the whitelist
		filter.checkClassVisibility("net.sf.jasperreports.engine.JasperReport");
	}

	@Test(expectedExceptions = JRRuntimeException.class)
	public void nonWhitelistedClassBlocked()
	{
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		// A class not in the whitelist should be blocked
		filter.checkClassVisibility("org.apache.commons.collections4.functors.InvokerTransformer");
	}

	@Test(expectedExceptions = JRRuntimeException.class)
	public void gadgetChainClassBlocked()
	{
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		// Common gadget chain class
		filter.checkClassVisibility("javax.management.BadAttributeValueExpException");
	}

	@Test
	public void whitelistedClassDeserializesViaContextStream() throws Exception
	{
		// Serialize a HashMap (whitelisted) and verify it deserializes through the filtered stream
		HashMap<String, String> original = new HashMap<>();
		original.put("key", "value");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(bout))
		{
			oos.writeObject(original);
		}

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		try (ContextClassLoaderObjectInputStream ois = new ContextClassLoaderObjectInputStream(context, bin))
		{
			Object result = ois.readObject();
			assert result instanceof HashMap : "Should deserialize HashMap successfully";
			@SuppressWarnings("unchecked")
			HashMap<String, String> map = (HashMap<String, String>) result;
			assert "value".equals(map.get("key")) : "Deserialized map should contain original data";
		}
	}

	@Test(expectedExceptions = JRRuntimeException.class)
	public void nonWhitelistedClassBlockedViaContextStream() throws Exception
	{
		// Serialize a non-whitelisted but Serializable object
		// java.io.File is Serializable but not in the deserialization whitelist
		java.io.File file = new java.io.File("/tmp/test");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(bout))
		{
			oos.writeObject(file);
		}

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		try (ContextClassLoaderObjectInputStream ois = new ContextClassLoaderObjectInputStream(context, bin))
		{
			ois.readObject(); // should throw JRRuntimeException
		}
	}

	@Test(expectedExceptions = JRRuntimeException.class)
	public void nonWhitelistedClassBlockedViaJRLoader() throws Exception
	{
		// Serialize a non-whitelisted but Serializable object
		java.io.File file = new java.io.File("/tmp/test");

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(bout))
		{
			oos.writeObject(file);
		}

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		JRLoader.loadObject(DefaultJasperReportsContext.getInstance(), bin); // should throw
	}

	@Test
	public void whitelistedStringDeserializes() throws Exception
	{
		// java.lang.String is in the hardcoded whitelist
		String original = "test-string";

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(bout))
		{
			oos.writeObject(original);
		}

		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		try (ContextClassLoaderObjectInputStream ois = new ContextClassLoaderObjectInputStream(context, bin))
		{
			Object result = ois.readObject();
			assert "test-string".equals(result) : "Should deserialize String successfully";
		}
	}

	@Test(expectedExceptions = JRRuntimeException.class)
	public void runtimeExecClassBlocked()
	{
		// Verify that Runtime (used in RCE attacks) is blocked
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		filter.checkClassVisibility("java.lang.Runtime");
	}

	@Test(expectedExceptions = JRRuntimeException.class)
	public void processBuilderClassBlocked()
	{
		// Verify that ProcessBuilder (used in RCE attacks) is blocked
		JasperReportsContext context = DefaultJasperReportsContext.getInstance();
		DeserializationClassFilter filter = new DeserializationClassFilter(context);
		filter.checkClassVisibility("java.lang.ProcessBuilder");
	}
}
