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
package net.sf.jasperreports.renderers.util;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;

/**
 * Checks that {@link SvgFontProcessor} still serializes the processed SVG document
 * after switching to the hardened transformer factory.
 */
public class SvgFontProcessorTest
{
	@Test
	public void replacesFontFamilies() throws Exception
	{
		SvgFontProcessor processor = 
			new SvgFontProcessor(DefaultJasperReportsContext.getInstance(), Locale.US)
			{
				@Override
				public String getFontFamily(String fontFamily, Locale locale)
				{
					return "Arial".equals(fontFamily) ? "DejaVu Sans" : fontFamily;
				}
			};

		String svg = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"10\" height=\"10\">"
				+ "<text x=\"0\" y=\"5\" font-family=\"Arial\">text</text></svg>";

		String output = new String(processor.process(svg.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

		assert output.contains("DejaVu Sans") : output;
		assert !output.contains("Arial") : output;
		assert output.contains(">text</text>") : output;
	}
}
