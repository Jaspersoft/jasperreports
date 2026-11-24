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

import java.io.ByteArrayOutputStream;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.base.JRBasePrintPage;
import net.sf.jasperreports.engine.base.JRBasePrintText;
import net.sf.jasperreports.engine.export.data.NumberTextValue;
import net.sf.jasperreports.engine.export.data.TextValue;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 * Tests that the value class of a print text, which can be named through a print file, is resolved
 * without running any of its code when detecting cell types during XLSX export.
 *
 */
public class XlsxValueClassTypeCheckTest
{

	/**
	 * Records code execution of the probe classes. Kept separate from the probes so that reading a
	 * flag does not, by itself, initialize the probe whose state is being observed.
	 */
	static class Flags
	{
		static volatile boolean notANumberStaticInitRan = false;
		static volatile boolean numberProbeStaticInitRan = false;
	}

	/**
	 * Not a {@link Number}; stands in for a malicious class named in a print file.
	 */
	public static class NotANumber
	{
		static
		{
			Flags.notANumberStaticInitRan = true;
		}
	}

	/**
	 * A {@link Number} subclass, so that number cell value detection is also exercised.
	 */
	public static class NumberProbe extends Number
	{
		private static final long serialVersionUID = 1L;

		static
		{
			Flags.numberProbeStaticInitRan = true;
		}

		@Override
		public int intValue()
		{
			return 0;
		}

		@Override
		public long longValue()
		{
			return 0;
		}

		@Override
		public float floatValue()
		{
			return 0;
		}

		@Override
		public double doubleValue()
		{
			return 0;
		}
	}

	/**
	 * Exposes the cell value detection of the exporter.
	 */
	static class TextValueExporter extends JRXlsxExporter
	{
		@Override
		public TextValue getTextValue(net.sf.jasperreports.engine.JRPrintText text, String textStr)
		{
			return super.getTextValue(text, textStr);
		}

		int resolvedClassCount()
		{
			return textValueClasses.size();
		}
	}

	@Test
	public void valueClassNotInitializedOnExport() throws JRException
	{
		JasperPrint jasperPrint = new JasperPrint();
		jasperPrint.setName("valueClassTypeCheck");
		jasperPrint.setPageWidth(200);
		jasperPrint.setPageHeight(100);

		JRBasePrintPage page = new JRBasePrintPage();
		// a class literal does not trigger initialization, so naming the probes this way is safe
		page.addElement(text(jasperPrint, 0, NotANumber.class.getName()));
		page.addElement(text(jasperPrint, 20, NumberProbe.class.getName()));
		jasperPrint.addPage(page);

		SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
		configuration.setDetectCellType(true);

		JRXlsxExporter exporter = new JRXlsxExporter();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setConfiguration(configuration);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		exporter.exportReport();

		assert out.size() > 0 : "the report should be exported";
		assert !Flags.notANumberStaticInitRan : "the static initializer must not run for a non number value class";
		assert !Flags.numberProbeStaticInitRan : "the static initializer must not run for a number value class";
	}

	@Test
	public void standardNumberDetected()
	{
		JasperPrint jasperPrint = new JasperPrint();
		JRBasePrintText text = text(jasperPrint, 0, Integer.class.getName());

		TextValueExporter exporter = new TextValueExporter();
		TextValue textValue = exporter.getTextValue(text, text.getFullText());

		assert textValue instanceof NumberTextValue : "an Integer value class should produce a number cell";
		assert Integer.valueOf(12).equals(((NumberTextValue) textValue).getValue());
		assert exporter.resolvedClassCount() == 0 : "a known value class should be matched by name, not resolved";
	}

	@Test
	public void valueClassTakenFromValue()
	{
		JasperPrint jasperPrint = new JasperPrint();
		// the name is not resolvable, so a number cell can only result from using the class of the value
		JRBasePrintText text = text(jasperPrint, 0, "net.sf.jasperreports.NoSuchValueClass");
		text.setValue(12);

		TextValueExporter exporter = new TextValueExporter();
		TextValue textValue = exporter.getTextValue(text, text.getFullText());

		assert textValue instanceof NumberTextValue : "the class of the value should be used when the value is present";
		assert Integer.valueOf(12).equals(((NumberTextValue) textValue).getValue());
		assert exporter.resolvedClassCount() == 0 : "the value class name should not be resolved when the value is present";
	}

	private JRBasePrintText text(JasperPrint jasperPrint, int y, String valueClassName)
	{
		JRBasePrintText text = new JRBasePrintText(jasperPrint.getDefaultStyleProvider());
		text.setX(0);
		text.setY(y);
		text.setWidth(100);
		text.setHeight(20);
		text.setText("12");
		text.setValueClassName(valueClassName);
		return text;
	}

}
