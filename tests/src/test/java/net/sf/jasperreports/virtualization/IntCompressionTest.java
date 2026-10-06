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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.virtualization.SerializationUtils;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class IntCompressionTest
{
	@Test(dataProvider = "integerValues")
	public void compressedInt(int value) throws IOException
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bout);
		SerializationUtils.writeIntCompressed(out, value);
		out.close();
		
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
		int read = SerializationUtils.readIntCompressed(in);
		assert read == value;
	}

	@DataProvider
	public Object[][] integerValues()
	{
		List<Integer> values = new ArrayList<>();
		for (int i = 0; i < 32; ++i)
		{
			values.add(1 << i);
			values.add(0x80000000 >> i);
			
			if (i > 1)
			{
				values.add((1 << i) | (1 << (i - 1)));
				values.add((1 << i) | (1 << (i - 2)));
			}
			
			if (i > 7)
			{
				values.add((1 << i) | 0xFF);
				values.add((1 << i) | 0x8F);
				values.add((1 << i) | 0xAA);
				values.add((1 << i) | 0x55);
			}
			
			if (i > 15)
			{
				values.add((1 << i) | 0xFF00);
				values.add((1 << i) | 0x8FAA);
				values.add((1 << i) | 0xAAFF);
				values.add((1 << i) | 0x5500);
			}
			
			if (i > 23)
			{
				values.add((1 << i) | 0xFFAA00);
				values.add((1 << i) | 0x8F55FF);
				values.add((1 << i) | 0xAAFF55);
				values.add((1 << i) | 0x55008F);
			}
		}
				
		Object[][] valArray = new Object[2 * values.size()][];
		int idx = 0;
		for (Integer value : values)
		{
			valArray[idx++] = new Object[]{value};
			valArray[idx++] = new Object[]{~value};
		}
		return valArray;
	}
	
}
