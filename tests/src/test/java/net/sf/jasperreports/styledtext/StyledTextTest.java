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
package net.sf.jasperreports.styledtext;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.testng.annotations.Test;

import net.sf.jasperreports.engine.util.JRStyledText;
import net.sf.jasperreports.engine.util.JRStyledText.Run;

/**
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class StyledTextTest {

	@Test
	public void appendText() {
		Map<Attribute, Object> globalAttributes = Collections.singletonMap(TextAttribute.SIZE, 12);
		JRStyledText styledText = new JRStyledText(Locale.US, "text", globalAttributes);
		styledText.append("foo");
		assert styledText.getText().equals("textfoo");
		
		List<Run> runs = styledText.getRuns();
		assert runs != null;
		assert runs.size() == 1;
		Run run = runs.get(0);
		assert run.attributes == globalAttributes;
		assert run.startIndex == 0;
		assert run.endIndex == 7;
	}

	@Test
	public void appendTextNoAttributes() {
		JRStyledText styledText = new JRStyledText(Locale.US, "text");
		styledText.append("foo");
		assert styledText.getText().equals("textfoo");
		
		List<Run> runs = styledText.getRuns();
		assert runs != null;
		assert runs.size() == 0;
	}

	@Test
	public void appendTextRuns() {
		Map<Attribute, Object> globalAttributes = Collections.singletonMap(TextAttribute.SIZE, 12);
		JRStyledText styledText = new JRStyledText(Locale.US, "text", globalAttributes);
		styledText.addRun(new Run(Collections.singletonMap(TextAttribute.SIZE, 13), 0, 2));
		styledText.addRun(new Run(Collections.singletonMap(TextAttribute.FAMILY, "serif"), 0, 4));
		
		styledText.append("foo");
		assert styledText.getText().equals("textfoo");
		
		List<Run> runs = styledText.getRuns();
		assert runs != null;
		assert runs.size() == 3;
		Run run = runs.get(0);
		assert run.attributes == globalAttributes;
		assert run.startIndex == 0;
		assert run.endIndex == 7;
		run = runs.get(1);
		assert run.startIndex == 0;
		assert run.endIndex == 2;
		run = runs.get(2);
		assert run.startIndex == 0;
		assert run.endIndex == 4;
	}
	

	@Test
	public void appendEmpty() {
		Map<Attribute, Object> globalAttributes = Collections.singletonMap(TextAttribute.SIZE, 12);
		JRStyledText styledText = new JRStyledText(Locale.US, "text", globalAttributes);
		styledText.append("");
		assert styledText.getText().equals("text");
		
		List<Run> runs = styledText.getRuns();
		assert runs != null;
		assert runs.size() == 1;
		Run run = runs.get(0);
		assert run.attributes == globalAttributes;
		assert run.startIndex == 0;
		assert run.endIndex == 4;
	}
}
