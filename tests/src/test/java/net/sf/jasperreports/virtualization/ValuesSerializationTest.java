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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ValuesSerializationTest extends BaseSerializationTests
{
	@Test
	public void nullTest()
	{
		Object read = passThroughSerializationNoChecks(null);
		assert read == null;
	}
	
	@Test(dataProvider = "baseObjects")
	public void baseObject(Object value)
	{
		Object read = passThroughSerialization(value);
		assert read.equals(value);
	}

	@DataProvider
	public Object[][] baseObjects()
	{
		return new Object[][]{
				{false},
				{true},
				{(byte) 20},
				{(short) 670},
				{7},
				{System.currentTimeMillis()},
				{5.44f},
				{7.33d},
				{UUID.randomUUID()},
				{BigInteger.ZERO},
				{BigInteger.ONE},
				{BigInteger.TEN.pow(1000)},
				{BigDecimal.ZERO},
				{BigDecimal.ONE},
				{BigDecimal.valueOf(54321l, 2)},
				{BigDecimal.valueOf(5000).pow(10)},
				{new Date()},
				{new java.sql.Date(System.currentTimeMillis())},
				{new Time(System.currentTimeMillis())},
				{new Timestamp(System.currentTimeMillis())},
				{Arrays.asList(1, 2, 3)},
		};
	}
	
	@Test
	public void timestampNanos()
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		timestamp.setNanos(54321);
		Timestamp read = passThroughSerialization(timestamp);
		assert read.getNanos() == 54321;
		assert read.equals(timestamp);
	}
	
}
