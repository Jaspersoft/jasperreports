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
package net.sf.jasperreports.virtualization;

import java.io.IOException;
import java.util.Date;

import org.testng.annotations.Test;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ReferencesTest extends BaseSerializationTests
{

	@Test
	public void testStringReference() throws IOException
	{
		SerializationJob job = serializationJob();
		String s = "x";
		job.out().writeJRObject(s);
		job.out().writeJRObject(s);
		
		String r1 = (String) job.in().readJRObject();
		String r2 = (String) job.in().readJRObject();
		assert r1 == r2;
		assert r1 != s;
		assert r1.equals(s);
	}

	@Test
	public void testStringNoReferenceLookup() throws IOException
	{
		SerializationJob job = serializationJob();
		String s = "x";
		job.out().writeJRObject(s);
		job.out().writeJRObject(s, false, true);
		
		String r1 = (String) job.in().readJRObject();
		String r2 = (String) job.in().readJRObject();
		assert r1 != r2;
		assert r1 != s;
		assert r1.equals(s);
		assert r2 != s;
		assert r2.equals(s);
	}

	@Test
	public void testStringNoReferenceStore() throws IOException
	{
		SerializationJob job = serializationJob();
		String s = "x";
		job.out().writeJRObject(s, true, false);
		job.out().writeJRObject(s);
		
		String r1 = (String) job.in().readJRObject();
		String r2 = (String) job.in().readJRObject();
		assert r1 != r2;
		assert r1 != s;
		assert r1.equals(s);
		assert r2 != s;
		assert r2.equals(s);
	}

	@Test
	public void testStringReferenceLookup() throws IOException
	{
		SerializationJob job = serializationJob();
		String s = "x";
		job.out().writeJRObject(s);
		job.out().writeJRObject(s, true, false);
		
		String r1 = (String) job.in().readJRObject();
		String r2 = (String) job.in().readJRObject();
		assert r1 == r2;
		assert r1 != s;
		assert r1.equals(s);
	}

	@Test
	public void testMixedReferences() throws IOException
	{
		SerializationJob job = serializationJob();
		String s = "x";
		Date d = new Date();
		job.out().writeJRObject(s);
		job.out().writeJRObject(d);
		job.out().writeJRObject(s);
		job.out().writeJRObject(d);
		
		String rs1 = (String) job.in().readJRObject();
		Date rd1 = (Date) job.in().readJRObject();
		String rs2 = (String) job.in().readJRObject();
		Date rd2 = (Date) job.in().readJRObject();
		
		assert rs1 == rs2;
		assert rs1 != s;
		assert rs1.equals(s);
		
		assert rd1 == rd2;
		assert rd1 != d;
		assert rd1.equals(d);
	}
}
