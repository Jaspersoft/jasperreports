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
package net.sf.jasperreports.compilers;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StandardReportClassWhitelistTest
{
	
	private static class Include
	{
		String className;
		boolean included;
		
		static Include yes(String className)
		{
			return of(className, true);
		}
		
		static Include no(String className)
		{
			return of(className, false);
		}
		
		static Include of(String className, boolean included)
		{
			Include test = new Include();
			test.className = className;
			test.included = included;
			return test;
		}
	}
	
	@Test(dataProvider = "whitelists")
	public void whitelist(String whitelist, String[] classes, Include[] includes)
	{
		StandardReportClassWhitelist classWhitelist = new StandardReportClassWhitelist();
		classWhitelist.addWhitelist(whitelist);
		if (classes != null)
		{
			for (String className : classes)
			{
				classWhitelist.addClass(className);
			}
		}
		
		for (Include include : includes)
		{
			boolean included = classWhitelist.includesClass(include.className);
			assert included == include.included;
		}
	}
	
	@DataProvider
	public Object[][] whitelists()
	{
		return new Object[][] {
			{"", null, new Include[] {Include.no("java.lang.String"), Include.no("java.lang.Integer")}},
			{"java.lang.String", null, new Include[] {Include.yes("java.lang.String"), Include.no("java.lang.Integer")}},
			{"java.lang.String ", null, new Include[] {Include.yes("java.lang.String"), Include.no("java.lang.Integer")}},
			{" java.lang.String  ", null, new Include[] {Include.yes("java.lang.String"), Include.no("java.lang.Integer")}},
			{"java.lang.String\n", null, new Include[] {Include.yes("java.lang.String"), Include.no("java.lang.Integer")}},
			{"\njava.lang.String\n", null, new Include[] {Include.yes("java.lang.String"), Include.no("java.lang.Integer")}},
			{"\njava.lang.String\n", null, new Include[] {Include.yes("java.lang.String"), Include.no("java.lang.Integer")}},
			{"java.lang.String,java.lang.Integer", null, new Include[] {Include.yes("java.lang.String"), 
					Include.yes("java.lang.Integer"), Include.no("java.lang.Long")}},
			{"java.lang.String\n,java.lang.Integer\n", null, new Include[] {Include.yes("java.lang.String"), 
					Include.yes("java.lang.Integer"), Include.no("java.lang.Long")}},
			{"java.lang.String , java.lang.Integer", null, new Include[] {Include.yes("java.lang.String"), 
					Include.yes("java.lang.Integer"), Include.no("java.lang.Long")}},
			{"java.lang.String,java.lang.Integer,java.lang.Long", null, new Include[] {Include.yes("java.lang.String"), 
					Include.yes("java.lang.Integer"), Include.yes("java.lang.Long"), Include.no("java.lang.Byte")}},
			{"*", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("Foo$Bar"),
					Include.no("java.lang.String"), Include.no("net.Foo")}}, 
			{"* ", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("Foo$Bar"),
					Include.no("java.lang.String"), Include.no("net.Foo")}}, 
			{" * ", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("Foo$Bar"),
					Include.no("java.lang.String"), Include.no("net.Foo")}}, 
			{"*\n", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("Foo$Bar"),
					Include.no("java.lang.String"), Include.no("net.Foo")}}, 
			{"**", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("Foo$Bar"),
					Include.yes("java.lang.String"), Include.yes("net.Foo")}}, 
			{" ** ", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("Foo$Bar"),
					Include.yes("java.lang.String"), Include.yes("net.Foo")}}, 
			{"\n** \n", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("Foo$Bar"),
					Include.yes("java.lang.String"), Include.yes("net.Foo")}}, 
			{"net.*", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.no("net.foo.Bar"), Include.no("net.met.foo.Bar"), Include.no("java.lang.String")}}, 
			{"net.foo.*", null, new Include[] {Include.yes("net.foo.Bar"), Include.yes("net.foo.FooBar"), 
					Include.no("net.Foo"), Include.no("net.foo.bar.Par"), Include.no("java.lang.String")}}, 
			{"net.Foo*", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.no("java.lang.String"), Include.no("net.foo.Bar"), Include.no("net.Moo")}}, 
			{"net.Foo*,java.lang.String", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.yes("java.lang.String"), Include.no("net.foo.Bar"), Include.no("net.Moo")}}, 
			{"java.lang.String,net.Foo*,net.Moo", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.yes("java.lang.String"), Include.no("net.foo.Bar"), Include.yes("net.Moo")}}, 
			{"net.Foo*,java.lang.String,org.Moo*", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.yes("java.lang.String"), Include.no("net.foo.Bar"), Include.no("net.Moo"),
					Include.yes("org.Moo"), Include.yes("org.MooBar")}}, 
			{"*.Foo", null, new Include[] {Include.yes("net.Foo"), Include.no("net.FooBar"), Include.no("net.Foo$Bar"),
					Include.no("java.lang.String"), Include.no("net.Foo.Bar"), Include.no("net.Moo"),
					Include.yes("org.Foo"), Include.no("net.met.Foo")}}, 
			{"net.foo*.Bar", null, new Include[] {Include.no("net.Bar"), Include.yes("net.foo.Bar"), Include.no("net.foomooBar"),
					Include.no("java.lang.String"), Include.no("net.Foo.Bar"), Include.no("net.Foo"),
					Include.yes("net.foomoo.Bar"), Include.no("net.met.Bar")}}, 
			{"net.**", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.yes("net.foo.Bar"), Include.yes("net.met.foo.Bar"), Include.no("java.lang.String")}}, 
			{"net.***", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.yes("net.foo.Bar"), Include.yes("net.met.foo.Bar"), Include.no("java.lang.String")}}, 
			{"net.****", null, new Include[] {Include.yes("net.Foo"), Include.yes("net.FooBar"), Include.yes("net.Foo$Bar"),
					Include.yes("net.foo.Bar"), Include.yes("net.met.foo.Bar"), Include.no("java.lang.String")}}, 
			{"net.foo.**", null, new Include[] {Include.yes("net.foo.Bar"), Include.yes("net.foo.FooBar"), 
					Include.no("net.Foo"), Include.yes("net.foo.bar.Par"), Include.no("java.lang.String")}}, 
			{"**Foo", null, new Include[] {Include.yes("Foo"), Include.yes("$Foo"), Include.yes("BarFoo"),
					Include.yes("net.Foo"), Include.yes("net.met.Foo"), Include.yes("net.met.BarFoo"),
					Include.no("FooBar"), Include.no("net.FooBar"), Include.no("net.Foo.Bar"), 
					Include.no("java.lang.String")}}, 
			{"**.Foo", null, new Include[] {Include.no("Foo"), Include.no("$Foo"), Include.no("BarFoo"),
					Include.yes("net.Foo"), Include.yes("net.met.Foo"), Include.no("net.met.BarFoo"),
					Include.no("FooBar"), Include.no("net.FooBar"), Include.no("net.Foo.Bar"), 
					Include.no("java.lang.String")}}, 
			{"net.**.*Foo", null, new Include[] {Include.no("Foo"), Include.yes("net.met.Foo"), Include.yes("net.bar.BarFoo"), 
					Include.yes("net.met.bar.BarFoo"),
					Include.no("net.Foo"), Include.no("BarFoo"), Include.no("net.BarFoo"), 
					Include.no("java.lang.String")}}, 
			{"java.lang.String", new String[] {"java.lang.Integer"}, new Include[] {Include.yes("java.lang.String"), 
					Include.yes("java.lang.Integer"), Include.no("java.lang.Long")}},
			{"java.lang.String", new String[] {"java.lang.Integer", "java.lang.Long"}, new Include[] {Include.yes("java.lang.String"), 
					Include.yes("java.lang.Integer"), Include.yes("java.lang.Long"), Include.no("java.lang.Byte")}},
			{"java.lang.String", new String[] {"java.lang.*"}, new Include[] {Include.yes("java.lang.String"), 
					Include.no("java.lang.Integer"), Include.no("java.lang.Long")}},
		};
	}

}
