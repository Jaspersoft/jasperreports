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
package net.sf.jasperreports.groovy;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import net.sf.jasperreports.compilers.ReportClassFilter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.SimpleJasperReportsContext;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * Tests for the Groovy expression sandbox ({@link GroovyClassFilter}) applied when
 * {@link ReportClassFilter#PROPERTY_CLASS_FILTER_ENABLED} is set.
 *
 * <p>
 * These cover ways a report expression can reach a class that isn't on the report class
 * whitelist without the class ever appearing as the receiver or argument of the expression's
 * own calls: Groovy extension methods that dispatch to a different, hidden class
 * ({@code execute()}), methods that dispatch dynamically to other methods ({@code invokeMethod}),
 * synthetic properties/methods that expose the JVM's own {@code Class} objects ({@code getClass()},
 * {@code .class}), and {@code super} calls, which bypass the sandbox filtering entirely unless
 * explicitly re-implemented.
 * </p>
 *
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class GroovyClassFilterTest
{
	private JasperReportsContext jasperReportsContext;

	@BeforeClass
	public void init()
	{
		jasperReportsContext = new SimpleJasperReportsContext();
		jasperReportsContext.setProperty(ReportClassFilter.PROPERTY_CLASS_FILTER_ENABLED, "true");
	}

	protected JasperReport compile(JasperReportsContext jasperReportsContext, String expression) throws JRException
	{
		String jrxml = "<jasperReport name=\"classFilterTest\" language=\"groovy\">"
				+ "<title height=\"20\">"
				+ "<element kind=\"textField\" uuid=\"1e5d51b1-1111-2222-3333-000000000000\" "
				+ "x=\"0\" y=\"0\" width=\"400\" height=\"20\">"
				+ "<expression><![CDATA[" + expression + "]]></expression>"
				+ "</element></title></jasperReport>";

		JasperDesign design = JRXmlLoader.load(jasperReportsContext,
				new ByteArrayInputStream(jrxml.getBytes(StandardCharsets.UTF_8)));
		return JasperCompileManager.getInstance(jasperReportsContext).compile(design);
	}

	protected String fillAndGetText(JasperReportsContext jasperReportsContext, String expression) throws JRException
	{
		JasperReport report = compile(jasperReportsContext, expression);
		JasperPrint print = JasperFillManager.getInstance(jasperReportsContext).fill(
				report, new HashMap<>(), new JREmptyDataSource());
		JRPrintText printText = (JRPrintText) print.getPages().get(0).getElements().get(0);
		return printText.getFullText();
	}

	/**
	 * Fills a report with the given expression, asserting that it is rejected, and returns the
	 * root cause of the resulting exception.
	 */
	protected Throwable rejectedExpressionCause(JasperReportsContext jasperReportsContext, 
			String expression) throws JRException
	{
		try
		{
			String text = fillAndGetText(jasperReportsContext, expression);

			throw new AssertionError("expected the expression to be rejected, but it evaluated to \""
					+ text + "\": " + expression);
		}
		catch (JRException e)
		{
			Throwable cause = e;
			while (cause.getCause() != null)
			{
				cause = cause.getCause();
			}
			return cause;
		}
	}

	@Test(dataProvider = "allowedExpressions")
	public void allowedExpression(String expression, String expectedText) throws JRException
	{
		assert fillAndGetText(jasperReportsContext, expression).equals(expectedText);
	}

	@DataProvider
	public Object[][] allowedExpressions()
	{
		return new Object[][]{
			{"\"count=\" + [1,2,3].size()", "count=3"},
			{"\"upper=\" + \"abc\".toUpperCase()", "upper=ABC"},
			{"\"joined=\" + [\"a\",\"b\"].join(\"-\")", "joined=a-b"},
			{"\"max=\" + Math.max(1,2)", "max=2"},
			{"\"fmt=\" + String.format(\"%d-%s\", 5, \"x\")", "fmt=5-x"},
			{"\"bd=\" + (new BigDecimal(\"1.5\") + new BigDecimal(\"2.5\"))", "bd=4.0"},
			{"\"sorted=\" + [3,1,2].sort()", "sorted=[1, 2, 3]"},
			{"\"reversed=\" + \"abc\".reverse()", "reversed=cba"},
			{"\"v=\" + [10,20,30][1]", "v=20"},
			{"(\"abc\" instanceof String) ? \"yes\" : \"no\"", "yes"},
			{"super.toString() != null ? \"ok\" : \"no\"", "ok"},
			//extension methods on the default method whitelist
			{"\"sum=\" + (1 + 2)", "sum=3"},
			{"\"total=\" + [1,2,3].sum()", "total=6"},
			{"\"int=\" + (\"5\".toInteger() + 1)", "int=6"},
			{"\"cap=\" + \"abc\".capitalize()", "cap=Abc"},
			{"\"pad=\" + \"7\".padLeft(3, \"0\")", "pad=007"},
			{"\"rep=\" + (\"ab\" * 2)", "rep=abab"},
			{"\"list=\" + ([1,2] + [3])", "list=[1, 2, 3]"},
			{"\"cmp=\" + (\"a\" <=> \"b\")", "cmp=-1"},
		};
	}

	@Test(dataProvider = "blockedExpressions")
	public void blockedExpression(String expression, String expectedRejectedClassName) throws JRException
	{
		Throwable cause = rejectedExpressionCause(jasperReportsContext, expression);
		assert cause instanceof JRRuntimeException : "unexpected exception for \"" + expression + "\": " + cause;
		assert cause.getMessage() != null && cause.getMessage().contains(expectedRejectedClassName)
				: "unexpected exception message for \"" + expression + "\": " + cause.getMessage();
	}

	@DataProvider
	public Object[][] blockedExpressions()
	{
		return new Object[][]{
			//Groovy exposes process execution as an extension method on List/String/String[]/GString
			//(org.codehaus.groovy.runtime.ProcessGroovyMethods) rather than as a call on a receiver
			//class of its own, so the whitelisted List/String receiver never reveals what's really
			//being invoked; caught here via the declared return type (java.lang.Process) of the
			//resolved method, which is not on the Groovy method whitelist
			{"[\"echo\", \"pwned\"].execute()", "org.codehaus.groovy.runtime.ProcessGroovyMethods.execute"},
			{"\"echo pwned\".execute()", "org.codehaus.groovy.runtime.ProcessGroovyMethods.execute"},
			//invokeMethod is declared to return Object, so the return type check doesn't catch it,
			//but it dispatches to execute() outside the sandbox
			{"\"echo pwned\".invokeMethod(\"execute\", null)", "org.codehaus.groovy.runtime.DefaultGroovyMethods.invokeMethod"},
			{"\"runtime \" + (\"echo pwned\").invokeMethod(\"execute\", null)", "org.codehaus.groovy.runtime.DefaultGroovyMethods.invokeMethod"},
			{"this.invokeMethod(\"toString\", null)", "groovy.lang.GroovyObject.invokeMethod"},
			//extension methods with side effects, both instance and static
			{"println(\"pwned\")", "org.codehaus.groovy.runtime.DefaultGroovyMethods.println"},
			{"Math.sleep(1)", "org.codehaus.groovy.runtime.DefaultGroovyStaticMethods.sleep"},
			//extension property setter
			{"(\"abc\".metaClass = null) == null ? \"x\" : \"y\"", "org.codehaus.groovy.runtime.DefaultGroovyMethods.setMetaClass"},
			//getClass() and the equivalent ".class" property must never yield a Class object,
			//regardless of what class they would return
			{"\"abc\".getClass()", "java.lang.Class"},
			{"\"abc\".class", "java.lang.Class"},
			{"[1,2,3].class", "java.lang.Class"},
			//super.xxx() calls must be checked exactly like ordinary calls; GroovyValueFilter (the
			//class GroovyClassFilter extends) does not override onSuperCall at all, so without an
			//explicit override, this would otherwise bypass every check in GroovyClassFilter
			{"super.getClass()", "java.lang.Class"},
			//pre-existing behavior, kept here as a regression guard: java.lang.Class itself is never
			//whitelisted, so any reflective class lookup is rejected via the receiver check
			{"Class.forName(\"java.lang.String\")", "java.lang.Class"},
			//pre-existing behavior, kept here as a regression guard: an unwhitelisted class is never
			//visible, including through its own constructor
			{"new java.util.Random()", "java.util.Random"},
		};
	}

	@Test
	public void configuredMethodWhitelist() throws JRException
	{
		String expression = "\"abc\".inspect()";
		Throwable cause = rejectedExpressionCause(jasperReportsContext, expression);
		assert cause instanceof JRRuntimeException 
				&& cause.getMessage().contains("org.codehaus.groovy.runtime.DefaultGroovyMethods.inspect")
				: "unexpected exception for \"" + expression + "\": " + cause;

		SimpleJasperReportsContext whitelistContext = new SimpleJasperReportsContext(jasperReportsContext);
		whitelistContext.setProperty(GroovyMethodWhitelist.PROPERTY_PREFIX_METHOD_WHITELIST + "test", 
				"org.codehaus.groovy.runtime.DefaultGroovyMethods.inspect");
		String text = fillAndGetText(whitelistContext, expression);
		assert text.equals("'abc'") : text;
	}
}
