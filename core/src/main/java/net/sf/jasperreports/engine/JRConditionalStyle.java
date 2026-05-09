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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.sf.jasperreports.engine.design.JRDesignConditionalStyle;
import net.sf.jasperreports.engine.xml.JRXmlConstants;

/**
 * @author Ionut Nedelcu (ionutned@users.sourceforge.net)
 */
@JsonPropertyOrder({
	JRXmlConstants.ATTRIBUTE_style,
	JRXmlConstants.ATTRIBUTE_mode,
	JRXmlConstants.ATTRIBUTE_forecolor,
	JRXmlConstants.ATTRIBUTE_backcolor,
	JRXmlConstants.ATTRIBUTE_fill,
	JRXmlConstants.ATTRIBUTE_radius,
	JRXmlConstants.ATTRIBUTE_scaleImage,
	JRXmlConstants.ATTRIBUTE_hTextAlign,
	JRXmlConstants.ATTRIBUTE_vTextAlign,
	JRXmlConstants.ATTRIBUTE_hImageAlign,
	JRXmlConstants.ATTRIBUTE_vImageAlign,
	JRXmlConstants.ATTRIBUTE_rotation,
	JRXmlConstants.ATTRIBUTE_markup,
	JRXmlConstants.ATTRIBUTE_pattern,
	"blankWhenNull",
	JRXmlConstants.ATTRIBUTE_fontName,
	JRXmlConstants.ATTRIBUTE_fontSize,
	"bold",
	"italic",
	"underline",
	"strikeThrough",
	JRXmlConstants.ATTRIBUTE_pdfFontName,
	JRXmlConstants.ATTRIBUTE_pdfEncoding,
	"pdfEmbedded",
	"conditionExpression",
	JRXmlConstants.ELEMENT_pen,
	JRXmlConstants.ELEMENT_box,
	JRXmlConstants.ELEMENT_paragraph
	})
@JsonDeserialize(as = JRDesignConditionalStyle.class)
public interface JRConditionalStyle extends JRStyle
{
	@Override
	@JsonIgnore
	public boolean isDefault(); // override just for the sake of the json annotation

	public JRExpression getConditionExpression();
}
