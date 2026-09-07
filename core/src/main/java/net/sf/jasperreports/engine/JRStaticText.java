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
package net.sf.jasperreports.engine;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;

import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * An abstract representation of a report static text. It provides functionality for static texts.
 * <p/>
 * Static texts are text elements with fixed content, which does not change during the
 * report-filling process. They are used mostly to introduce static text labels into the
 * generated documents.
 * <p/>
 * Besides the general element properties and the common text-specific properties, a static text 
 * definition has only the <code>&lt;text&gt;</code> tag, which introduces the fixed text content 
 * of the static text element. The text content can be collected using the {@link #getText()} method.
 * 
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 */
@JsonPropertyOrder({
	"kind",
	JRXmlConstants.ATTRIBUTE_uuid,
	JRXmlConstants.ATTRIBUTE_key,
	JRXmlConstants.ATTRIBUTE_x,
	JRXmlConstants.ATTRIBUTE_y,
	JRXmlConstants.ATTRIBUTE_width,
	JRXmlConstants.ATTRIBUTE_height,
	JRXmlConstants.ATTRIBUTE_forecolor,
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_mode,
	JRXmlConstants.ATTRIBUTE_positionType,
	JRXmlConstants.ATTRIBUTE_stretchType,
	"printRepeatedValues",
	"printInFirstWholeBand",
	"printWhenDetailOverflows",
	JRXmlConstants.ATTRIBUTE_printWhenGroupChanges,
	"removeLineWhenBlank",
	JRXmlConstants.ATTRIBUTE_markup,
	JRXmlConstants.ATTRIBUTE_fontName,
	JRXmlConstants.ATTRIBUTE_fontSize,
	JRXmlConstants.ATTRIBUTE_pdfFontName,
	"bold",
	"italic",
	"pdfEmbedded",
	JRXmlConstants.ATTRIBUTE_rotation,
	"strikeThrough",
	"underline",
	JRXmlConstants.ATTRIBUTE_pdfEncoding,
	JRXmlConstants.ATTRIBUTE_hTextAlign,
	JRXmlConstants.ATTRIBUTE_vTextAlign,
	JRXmlConstants.ATTRIBUTE_style,
	JRXmlConstants.ELEMENT_property,
	JRXmlConstants.ELEMENT_propertyExpression,
	JRXmlConstants.ELEMENT_styleExpression,
	JRXmlConstants.ELEMENT_printWhenExpression,
	JRXmlConstants.ELEMENT_box,
	JRXmlConstants.ELEMENT_paragraph,
	JRXmlConstants.ELEMENT_text
	})
@JsonTypeName("staticText")
@JsonDeserialize(as = JRDesignStaticText.class)
public interface JRStaticText extends JRTextElement
{


	/**
	 *
	 */
	@JacksonXmlCData
	public String getText();

	/**
	 *
	 */
	public void setText(String text);


}
