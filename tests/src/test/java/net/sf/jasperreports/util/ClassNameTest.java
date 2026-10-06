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
package net.sf.jasperreports.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.engine.util.JRClassLoader;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class ClassNameTest
{

	@Test(dataProvider = "realNames")
	public void standardClass(String className, String realName)
	{
		String classRealName = JRClassLoader.getClassRealName(className);
		assert classRealName != null;
		assert classRealName.equals(realName);
	}
	
	@DataProvider
	public Object[][] realNames()
	{
		return new Object[][] {
			{"java.lang.String", "java.lang.String"},
			{"java.lang.String[]", "[Ljava.lang.String;"},
			//FIXME multi dimensional arrays not currently working
			//{"java.lang.String[][]", "[[Ljava.lang.String;"},
			//{"java.lang.String[][][]", "[[[Ljava.lang.String;"},
			{"foo", "foo"},
			{"foo[]", "[Lfoo;"},
			{"boolean[]", "[Z"},
			{"byte[]", "[B"},
			{"char[]", "[C"},
			{"double[]", "[D"},
			{"float[]", "[F"},
			{"int[]", "[I"},
			{"long[]", "[J"},
			{"short[]", "[S"},
			//{"byte[][]", "[[B"},
			//{"int[][][]", "[[[I"},
		};
	}
	
}
